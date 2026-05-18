package servidor;

import entidade.*;
import serializacao.JSON;

import java.io.*;
import java.net.*;
import java.util.List;

public class ServidorRMI {

    static final int PORTA = 8080;

    static final ClinicaImpl  produtoService = new ClinicaImpl();
    static final EstoqueImpl  estoqueService = new EstoqueImpl(produtoService);

    // ── Wrapper status + valor ────────────────────────────────────────────────

    static class Resultado {
        String status, valor;
        Resultado(String status, String valor) { this.status = status; this.valor = valor; }
        static Resultado ok(String v)   { return new Resultado("OK",   v); }
        static Resultado erro(String v) { return new Resultado("ERRO", "\"" + v + "\""); }
    }

    public static void main(String[] args) throws Exception {
        try (ServerSocket ss = new ServerSocket(PORTA)) {
            while (true) {
                Socket cliente = ss.accept();
                new Thread(() -> {
                    try {
                        String requestStr = getRequest(cliente);
                        String replyStr   = expedicao(requestStr);
                        sendReply(cliente, replyStr);
                    } catch (Exception e) {
                        System.err.println("erro: " + e.getMessage());
                    }
                }).start();
            }
        }
    }

    static String getRequest(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
        String linha = in.readLine();
        return linha == null ? "{}" : linha;
    }

    static void sendReply(Socket socket, String replyJson) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println(replyJson);
        out.flush();
        socket.close();
    }

    // ── Expedidor ─────────────────────────────────────────────────────────────

    static String expedicao(String requestStr) {
        int    requestId  = getInt(requestStr, "requestId");
        String nomeObjeto = getNomeObjeto(requestStr);
        String metodoId   = JSON.get(requestStr, "metodoId");
        String parametros = extrairParametros(requestStr);

        Resultado res;
        try {
            if ("ProdutoService".equals(nomeObjeto)) {
                res = produtoSkeleton(metodoId, parametros);
            } else if ("EstoqueService".equals(nomeObjeto)) {
                res = estoqueSkeleton(metodoId, parametros);
            } else {
                res = Resultado.erro("Objeto remoto nao encontrado");
            }
        } catch (Exception e) {
            res = Resultado.erro(e.getMessage());
        }

        return "{"
            + "\"requestId\":"  + requestId    + ","
            + "\"status\":\""   + res.status   + "\","
            + "\"resultado\":"  + res.valor
            + "}";
    }

    // ── ProdutoSkeleton ──────────────────────────────────────────────────────

    static Resultado produtoSkeleton(String metodoId, String params) {
        switch (metodoId) {

            case "listarTodos":
            case "listarProdutos":
                return Resultado.ok(JSON.serializarLista(produtoService.listarProdutos()));

            case "buscarPorId": {
                int id = getInt(params, "id");
                for (Produto p : produtoService.listarProdutos())
                    if (p.getId() == id) return Resultado.ok(JSON.serializar(p));
                return Resultado.erro("Produto nao encontrado");
            }

            case "buscarPorEspecie":
                return Resultado.ok(JSON.serializarLista(
                    produtoService.buscarPorEspecie(JSON.get(params, "especie"))));

            case "cadastrarProduto": {
                Produto novo = JSON.desserializarProduto(params);
                int id = produtoService.cadastrarProduto(novo);
                return Resultado.ok("{\"id\":" + id + ",\"status\":\"cadastrado\"}");
            }

            case "remover": {
                int id = getInt(params, "id");
                boolean ok = produtoService.remover(id);
                return ok
                    ? Resultado.ok("{\"status\":\"removido\",\"id\":" + id + "}")
                    : Resultado.erro("Produto nao encontrado");
            }

            case "listarVencidos":
                return Resultado.ok(JSON.serializarLista(produtoService.listarVencidos()));

            case "calcularValorTotal": {
                double total = produtoService.listarProdutos()
                    .stream().mapToDouble(Produto::getPreco).sum();
                return Resultado.ok(String.valueOf(total));
            }

            case "gerarRelatorio":
                return Resultado.ok("\"" + produtoService.gerarRelatorio()
                    .replace("\n", "\\n").replace("\"", "\\\"") + "\"");

            default:
                return Resultado.erro("Metodo nao encontrado: " + metodoId);
        }
    }

    // ── EstoqueSkeleton ──────────────────────────────────────────────────────

    static Resultado estoqueSkeleton(String metodoId, String params) {
        switch (metodoId) {

            case "criarEstoque": {
                String local = JSON.get(params, "local");
                Estoque e = estoqueService.criarEstoque(local);
                return Resultado.ok(JSON.serializarEstoqueResumido(e));
            }

            case "listarEstoques":
                return Resultado.ok(
                    JSON.serializarListaEstoquesResumida(estoqueService.listarEstoques()));

            case "entradaProduto": {
                int estoqueId = getInt(params, "estoqueId");
                int produtoId = getInt(params, "produtoId");
                boolean ok = estoqueService.entradaProduto(estoqueId, produtoId);
                return ok
                    ? Resultado.ok("\"Produto adicionado ao estoque com sucesso\"")
                    : Resultado.erro("Estoque ou produto nao encontrado");
            }

            case "saidaProduto": {
                int estoqueId = getInt(params, "estoqueId");
                int produtoId = getInt(params, "produtoId");
                boolean ok = estoqueService.saidaProduto(estoqueId, produtoId);
                return ok
                    ? Resultado.ok("\"Produto removido do estoque com sucesso\"")
                    : Resultado.erro("Estoque ou produto nao encontrado");
            }

            case "alertarVencidos":
                return Resultado.ok(
                    estoqueService.alertarVencidosJson());

            default:
                return Resultado.erro("Metodo nao encontrado: " + metodoId);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    static String getNomeObjeto(String json) {
        String busca = "\"referenciaObj\":";
        int ini = json.indexOf(busca);
        if (ini < 0) return "";
        ini += busca.length();
        int fim = json.indexOf('}', ini);
        if (fim < 0) return "";
        return JSON.get(json.substring(ini, fim + 1), "NomeObjeto");
    }

    static String extrairParametros(String json) {
        String busca = "\"parametros\":";
        int ini = json.indexOf(busca);
        if (ini < 0) return "{}";
        ini += busca.length();
        if (ini >= json.length()) return "{}";
        if (json.charAt(ini) == '{') {
            int depth = 0, fim = ini;
            for (; fim < json.length(); fim++) {
                if (json.charAt(fim) == '{') depth++;
                if (json.charAt(fim) == '}') { depth--; if (depth == 0) break; }
            }
            return json.substring(ini, fim + 1);
        }
        return "{}";
    }

    static int getInt(String json, String chave) { return JSON.getInt(json, chave); }
}