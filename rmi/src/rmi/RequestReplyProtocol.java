package rmi;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RequestReplyProtocol — implementa os 3 métodos da seção 5.2 do livro.
 *
 * Comunicação via UDP (DatagramSocket) — SEM sockets TCP.
 *
 * Métodos conforme o livro:
 *   doOperation  — lado cliente: envia requisição e aguarda resposta
 *   getRequest   — lado servidor: aguarda e retorna próxima requisição
 *   sendReply    — lado servidor: envia resposta ao cliente
 */
public class RequestReplyProtocol {

    private static final int    BUFFER_SIZE     = 65507; // max UDP payload
    private static final int    TIMEOUT_MS      = 5000;  // 5 segundos
    private static final int    MAX_RETRIES     = 3;

    private final DatagramSocket socket;
    private static final AtomicInteger reqCounter = new AtomicInteger(1);

    public RequestReplyProtocol(DatagramSocket socket) {
        this.socket = socket;
    }

    // ════════════════════════════════════════════════════════════════════════
    // doOperation — LADO CLIENTE
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Envia uma mensagem de requisição para o objeto remoto e retorna a resposta.
     *
     * @param ref       referência ao objeto remoto (host, porta, nome)
     * @param methodId  nome do método a invocar
     * @param arguments argumentos serializados em JSON (bytes UTF-8)
     * @return bytes da resposta (payload JSON da resposta)
     */
    public byte[] doOperation(RemoteObjectRef ref,
                               String methodId,
                               byte[] arguments) throws IOException {

        int reqId = reqCounter.getAndIncrement();

        // Monta mensagem de requisição
        Message request = new Message(
            Message.REQUEST, reqId,
            ref.getNomeObjeto(), methodId, arguments
        );

        byte[]        reqBytes = request.toBytes();
        InetAddress   host     = InetAddress.getByName(ref.getHost());
        DatagramPacket sendPkt = new DatagramPacket(reqBytes, reqBytes.length,
                                                    host, ref.getPorta());

        // Envia com retries em caso de timeout
        socket.setSoTimeout(TIMEOUT_MS);

        for (int tentativa = 1; tentativa <= MAX_RETRIES; tentativa++) {
            socket.send(sendPkt);
            System.out.println("[CLIENT] doOperation → " + ref.getNomeObjeto() +
                               "." + methodId + "() [reqId=" + reqId +
                               ", tentativa=" + tentativa + "]");

            try {
                // Aguarda resposta
                byte[]        buf     = new byte[BUFFER_SIZE];
                DatagramPacket recvPkt = new DatagramPacket(buf, buf.length);
                socket.receive(recvPkt);

                // Desserializa resposta
                byte[]  data  = trimBytes(recvPkt);
                Message reply = Message.fromBytes(data);

                // Verifica se é a resposta da requisição certa
                if (reply.getMessageType() == Message.REPLY &&
                    reply.getRequestId()   == reqId) {
                    System.out.println("[CLIENT] doOperation ← resposta recebida [reqId=" +
                                       reqId + "]");
                    return reply.getArguments();
                }

            } catch (SocketTimeoutException e) {
                System.out.println("[CLIENT] Timeout na tentativa " + tentativa +
                                   " — reenviando...");
            }
        }

        throw new IOException("doOperation: sem resposta após " + MAX_RETRIES + " tentativas.");
    }

    // ════════════════════════════════════════════════════════════════════════
    // getRequest — LADO SERVIDOR
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Obtém uma requisição de um cliente através da porta servidora.
     * Bloqueia até chegar um pacote.
     *
     * @return pacote UDP recebido (contém dados do cliente e bytes da mensagem)
     */
    public DatagramPacket getRequest() throws IOException {
        byte[]        buf = new byte[BUFFER_SIZE];
        DatagramPacket pkt = new DatagramPacket(buf, buf.length);
        socket.receive(pkt); // bloqueia aqui
        return pkt;
    }

    // ════════════════════════════════════════════════════════════════════════
    // sendReply — LADO SERVIDOR
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Envia a mensagem de resposta para o cliente.
     *
     * @param reply      bytes da resposta (payload JSON serializado)
     * @param clientHost endereço IP do cliente
     * @param clientPort porta do cliente
     */
    public void sendReply(byte[] reply,
                          InetAddress clientHost,
                          int clientPort) throws IOException {

        DatagramPacket pkt = new DatagramPacket(reply, reply.length,
                                                clientHost, clientPort);
        socket.send(pkt);
        System.out.println("[SERVER] sendReply → " +
                           clientHost.getHostAddress() + ":" + clientPort +
                           " (" + reply.length + " bytes)");
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    /** Extrai apenas os bytes recebidos (sem o padding do buffer). */
    private static byte[] trimBytes(DatagramPacket pkt) {
        byte[] trimmed = new byte[pkt.getLength()];
        System.arraycopy(pkt.getData(), pkt.getOffset(), trimmed, 0, pkt.getLength());
        return trimmed;
    }
}