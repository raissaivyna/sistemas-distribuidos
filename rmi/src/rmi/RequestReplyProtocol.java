package rmi;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RequestReplyProtocol — adaptado para servidor C++ via TCP + JSON.
 *
 * Mantém os 3 métodos do livro (seção 5.2):
 *   doOperation  — lado cliente
 *   getRequest   — lado servidor
 *   sendReply    — lado servidor
 *
 * Formato JSON enviado ao C++:
 *   {"requestId":1,"referenciaObj":"ProdutoService","metodoId":"listarTodos","parametros":{}}
 *
 * Formato JSON recebido do C++:
 *   {"requestId":1,"status":"OK","resultado":[...]}
 */
public class RequestReplyProtocol {

    private static final AtomicInteger reqCounter = new AtomicInteger(1);

    // ── doOperation — LADO CLIENTE ───────────────────────────────────────────

    public byte[] doOperation(RemoteObjectRef ref,
                               String methodId,
                               byte[] arguments) throws IOException {

        int    reqId    = reqCounter.getAndIncrement();
        String argsJson = (arguments != null && arguments.length > 0)
                          ? new String(arguments, "UTF-8") : "{}";

        // Monta JSON no formato que o C++ espera
        String requestJson = "{"
            + "\"requestId\":"       + reqId               + ","
            + "\"referenciaObj\":\"" + ref.getNomeObjeto() + "\","
            + "\"metodoId\":\""      + methodId            + "\","
            + "\"parametros\":"      + argsJson
            + "}";

        System.out.println("[CLIENT] doOperation → " +
                           ref.getNomeObjeto() + "." + methodId +
                           "() [reqId=" + reqId + "]");

        // TCP — mesmo protocolo do servidor C++
        try (Socket        socket = new Socket(ref.getHost(), ref.getPorta());
             PrintWriter   out    = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in    = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()))) {

            out.println(requestJson);

            // Lê até EOF (C++ fecha conexão após send)
            StringBuilder sb = new StringBuilder();
            String linha;
            while ((linha = in.readLine()) != null) sb.append(linha);

            String replyJson = sb.toString();
            System.out.println("[CLIENT] ← [reqId=" + reqId + "] " +
                replyJson.substring(0, Math.min(80, replyJson.length())));

            return extrairResultado(replyJson).getBytes("UTF-8");
        }
    }

    // ── getRequest / sendReply — mantidos por contrato do protocolo ──────────
    // Não usados quando o servidor é C++ — servidor Java usaria estes métodos

    public java.net.DatagramPacket getRequest() throws IOException {
        throw new UnsupportedOperationException("Servidor e C++ — use ServidorRMI.java para servidor Java.");
    }

    public void sendReply(byte[] reply, java.net.InetAddress host, int port) throws IOException {
        throw new UnsupportedOperationException("Servidor e C++ — use ServidorRMI.java para servidor Java.");
    }

    // ── Extrai "resultado" do JSON de reply ──────────────────────────────────

    private String extrairResultado(String reply) {
        if (reply == null || reply.isEmpty()) return "{}";
        String busca = "\"resultado\":";
        int ini = reply.indexOf(busca);
        if (ini < 0) return reply;
        ini += busca.length();
        if (ini >= reply.length()) return "{}";
        char first = reply.charAt(ini);
        if (first == '[' || first == '{') {
            char open = first, close = first == '[' ? ']' : '}';
            int depth = 0, fim = ini;
            for (; fim < reply.length(); fim++) {
                if (reply.charAt(fim) == open)  depth++;
                if (reply.charAt(fim) == close) { depth--; if (depth == 0) break; }
            }
            return reply.substring(ini, fim + 1);
        }
        if (first == '"') {
            int fim = reply.indexOf('"', ini + 1);
            return reply.substring(ini + 1, fim < 0 ? reply.length() : fim);
        }
        int fim = reply.indexOf('}', ini);
        return reply.substring(ini, fim < 0 ? reply.length() : fim).trim();
    }
}