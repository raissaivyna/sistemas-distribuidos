package servidor;

import entidade.*;
import serializacao.JSON;

import java.io.*;
import java.net.*;
import java.util.List;

public class ServidorRMI {

    static final int PORTA = 8080;

    public static void main(String[] args) throws Exception {
        ClinicaImpl impl = new ClinicaImpl();

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║   ServidorRMI Java (replica C++) — TCP 8080  ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        try (ServerSocket ss = new ServerSocket(PORTA)) {
            System.out.println("Aguardando conexoes na porta " + PORTA + "...");
            while (true) {
                Socket cliente = ss.accept();
                System.out.println("Conexao aceita: " + cliente.getInetAddress());
                new Thread(() -> tratarCliente(cliente, impl)).start();
            }
        }
    }

    static void tratarCliente(Socket socket, ClinicaImpl impl) {
        try {
            BufferedReader in  = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            PrintWriter    out = new PrintWriter(socket.getOutputStream(), true);

            // Lê UMA linha — cliente envia o request em uma linha só via println
            String requestStr = in.readLine();
            if (requestStr == null || requestStr.isBlank()) {
                socket.close();
                return;
            }

            System.out.println("Request recebido: " + requestStr);

            String referenciaObj = JSON.get(requestStr, "referenciaObj");
            String metodoId      = JSON.get(requestStr, "metodoId");
            String parametros    = extrairParametros(requestStr);
            int    requestId     = JSON.getInt(requestStr, "requestId");

            System.out.println("debug: referenciaObj=" + referenciaObj +
                               " metodoId=" + metodoId);

            String resultado;
            String status = "OK";

            if ("ProdutoService".equals(referenciaObj)) {
                resultado = despachar(impl, metodoId, parametros);
            } else {
                status    = "ERRO";
                resultado = "\"Objeto remoto nao encontrado\"";
            }

            String replyStr = "{"
                + "\"requestId\":"  + requestId + ","
                + "\"status\":\""   + status    + "\","
                + "\"resultado\":"  + resultado
                + "}";

            // Envia resposta e fecha conexão — igual ao C++
            out.println(replyStr);
            out.flush();
            socket.close();

            System.out.println("Reply enviado: " +
                replyStr.substring(0, Math.min(80, replyStr.length())));

        } catch (Exception e) {
            System.err.println("Erro ao tratar cliente: " + e.getMessage());
        }
    }

    static String despachar(ClinicaImpl impl, String metodoId, String params) {
        System.out.println("invocacao: " + metodoId);
        switch (metodoId) {

            case "listarTodos":
            case "listarProdutos":
                return JSON.serializarLista(impl.listarProdutos());

            case "buscarPorId": {
                int id = JSON.getInt(params, "id");
                for (Produto p : impl.listarProdutos())
                    if (p.getId() == id) return JSON.serializar(p);
                return "\"Produto nao encontrado\"";
            }

            case "buscarPorEspecie":
                return JSON.serializarLista(
                    impl.buscarPorEspecie(JSON.get(params, "especie")));

            case "cadastrarProduto": {
                Produto novo = JSON.desserializarProduto(params);
                int id = impl.cadastrarProduto(novo);
                return "{\"id\":" + id + ",\"status\":\"cadastrado\"}";
            }

            case "listarVencidos":
                return JSON.serializarLista(impl.listarVencidos());

            case "gerarRelatorio":
                return "\"" + impl.gerarRelatorio()
                    .replace("\n", "\\n")
                    .replace("\"", "\\\"") + "\"";

            case "registrarPedido": {
                PedidoReposicao pedido = desserializarPedido(params);
                return "\"" + impl.registrarPedido(pedido)
                    .replace("\"", "\\\"") + "\"";
            }

            default:
                return "\"Metodo desconhecido: " + metodoId + "\"";
        }
    }

    static String extrairParametros(String json) {
        String busca = "\"parametros\":";
        int ini = json.indexOf(busca);
        if (ini < 0) return "{}";
        ini += busca.length();
        char first = json.charAt(ini);
        if (first == '{') {
            int depth = 0, fim = ini;
            for (; fim < json.length(); fim++) {
                if (json.charAt(fim) == '{') depth++;
                if (json.charAt(fim) == '}') { depth--; if (depth == 0) break; }
            }
            return json.substring(ini, fim + 1);
        }
        return "{}";
    }

    static PedidoReposicao desserializarPedido(String json) {
        PedidoReposicao p = new PedidoReposicao(
            0, JSON.get(json, "responsavel"), JSON.get(json, "dataHora"));
        int iStart = json.indexOf("\"itens\":[");
        if (iStart >= 0) {
            int aStart = json.indexOf('[', iStart);
            int aEnd   = json.lastIndexOf(']');
            if (aStart >= 0 && aEnd > aStart)
                JSON.desserializarLista(json.substring(aStart, aEnd + 1))
                    .forEach(p::adicionarItem);
        }
        return p;
    }
}