package cliente;

import entidade.*;
import rmi.RemoteObjectRef;
import rmi.RequestReplyProtocol;
import serializacao.JSON;

import java.io.IOException;
import java.util.List;

/**
 * ClienteRMI — replica do cliente C++ em Java.
 *
 * Usa o mesmo formato de mensagem do cliente C++:
 * {
 *   "TipoMensagem": "Request",
 *   "requestId": N,
 *   "referenciaObj": {"objetoId": 1, "NomeObjeto": "ProdutoService"},
 *   "metodoId": "...",
 *   "parametros": {...}
 * }
 */
public class ClienteRMI {

    static final String HOST  = "localhost";
    static final int    PORTA = 8080;

    static RequestReplyProtocol protocolo = new RequestReplyProtocol();

    public static void main(String[] args) throws Exception {

        System.out.println("========== Cliente RMI Java ==========\n");

        // ── Teste 1: buscarPorId ─────────────────────────────────────────────
        System.out.println("[1] chamando buscarPorId(1)...");
        String r1 = chamar("ProdutoService", 1, "buscarPorId", "{\"id\":1}", 1);
        System.out.println("Resposta: " + r1 + "\n");

        // ── Teste 2: listarTodos ─────────────────────────────────────────────
        System.out.println("[2] chamando listarTodos()...");
        String r2 = chamar("ProdutoService", 1, "listarTodos", "{}", 2);
        System.out.println("Resposta: " + r2 + "\n");

        // ── Teste 3: buscarPorEspecie ────────────────────────────────────────
        System.out.println("[3] chamando buscarPorEspecie('Canino')...");
        String r3 = chamar("ProdutoService", 1, "buscarPorEspecie", "{\"especie\":\"Canino\"}", 3);
        System.out.println("Resposta: " + r3 + "\n");

        // ── Teste 4: calcularValorTotal ──────────────────────────────────────
        System.out.println("[4] chamando calcularValorTotal()...");
        String r4 = chamar("ProdutoService", 1, "calcularValorTotal", "{}", 4);
        System.out.println("Resposta: " + r4 + "\n");

        // ── Teste 5: remover ─────────────────────────────────────────────────
        System.out.println("[5] chamando remover(2)...");
        String r5 = chamar("ProdutoService", 1, "remover", "{\"id\":2}", 5);
        System.out.println("Resposta: " + r5 + "\n");

        // ── Teste 6: listarTodos apos remover ────────────────────────────────
        System.out.println("[6] chamando listarTodos() apos remover...");
        String r6 = chamar("ProdutoService", 1, "listarTodos", "{}", 6);
        System.out.println("Resposta: " + r6 + "\n");

        // ── Teste 7: buscarPorId inexistente ─────────────────────────────────
        System.out.println("[7] chamando buscarPorId(999)... (nao existe)");
        String r7 = chamar("ProdutoService", 1, "buscarPorId", "{\"id\":999}", 7);
        System.out.println("Resposta: " + r7 + "\n");

        // ── Teste 8: metodo inexistente ──────────────────────────────────────
        System.out.println("[8] chamando metodo inexistente 'fooBar'...");
        String r8 = chamar("ProdutoService", 1, "fooBar", "{}", 8);
        System.out.println("Resposta: " + r8 + "\n");

        // ── Teste 9: criarEstoque ────────────────────────────────────────────
        System.out.println("[9] chamando criarEstoque('Deposito Central')...");
        String r9 = chamar("EstoqueService", 2, "criarEstoque", "{\"local\":\"Deposito Central\"}", 9);
        System.out.println("Resposta: " + r9 + "\n");

        // ── Teste 10: listarEstoques ─────────────────────────────────────────
        System.out.println("[10] chamando listarEstoques()...");
        String r10 = chamar("EstoqueService", 2, "listarEstoques", "{}", 10);
        System.out.println("Resposta: " + r10 + "\n");

        // ── Teste 11: entradaProduto ─────────────────────────────────────────
        System.out.println("[11] chamando entradaProduto(estoqueId:1, produtoId:1)...");
        String r11 = chamar("EstoqueService", 2, "entradaProduto",
                            "{\"estoqueId\":1,\"produtoId\":1}", 11);
        System.out.println("Resposta: " + r11 + "\n");

        // ── Teste 12: listarEstoques apos entrada ────────────────────────────
        System.out.println("[12] chamando listarEstoques() apos entrada...");
        String r12 = chamar("EstoqueService", 2, "listarEstoques", "{}", 12);
        System.out.println("Resposta: " + r12 + "\n");

        // ── Teste 13: alertarVencidos ────────────────────────────────────────
        System.out.println("[13] chamando alertarVencidos()...");
        String r13 = chamar("EstoqueService", 2, "alertarVencidos", "{}", 13);
        System.out.println("Resposta: " + r13 + "\n");

        // ── Teste 14: saidaProduto ───────────────────────────────────────────
        System.out.println("[14] chamando saidaProduto(estoqueId:1, produtoId:1)...");
        String r14 = chamar("EstoqueService", 2, "saidaProduto",
                            "{\"estoqueId\":1,\"produtoId\":1}", 14);
        System.out.println("Resposta: " + r14 + "\n");

        // ── Teste 15: listarEstoques apos saida ──────────────────────────────
        System.out.println("[15] chamando listarEstoques() apos saida...");
        String r15 = chamar("EstoqueService", 2, "listarEstoques", "{}", 15);
        System.out.println("Resposta: " + r15 + "\n");

        // ── Teste 16: alertarVencidos apos saida ─────────────────────────────
        System.out.println("[16] chamando alertarVencidos() apos saida...");
        String r16 = chamar("EstoqueService", 2, "alertarVencidos", "{}", 16);
        System.out.println("Resposta: " + r16 + "\n");

        // ── Teste 17: metodo inexistente no EstoqueService ───────────────────
        System.out.println("[17] chamando 'fooBar' no EstoqueService...");
        String r17 = chamar("EstoqueService", 2, "fooBar", "{}", 17);
        System.out.println("Resposta: " + r17 + "\n");

        // ── Teste 18: metodo inexistente no ProdutoService ───────────────────
        System.out.println("[18] chamando 'fooBar' no ProdutoService...");
        String r18 = chamar("ProdutoService", 1, "fooBar", "{}", 18);
        System.out.println("Resposta: " + r18 + "\n");

        System.out.println("========== Fim dos testes ==========");
    }

    /**
     * Monta o request no formato exato do cliente C++ e envia via doOperation.
     */
    static String chamar(String nomeObjeto, int objetoId,
                          String metodoId, String parametros,
                          int requestId) throws IOException {

        // Monta referenciaObj como objeto JSON — igual ao C++
        String referenciaObj = "{\"objetoId\":" + objetoId +
                               ",\"NomeObjeto\":\"" + nomeObjeto + "\"}";

        // Monta request completo
        String requestJson = "{"
            + "\"TipoMensagem\":\"Request\","
            + "\"requestId\":"    + requestId    + ","
            + "\"referenciaObj\":" + referenciaObj + ","
            + "\"metodoId\":\""   + metodoId     + "\","
            + "\"parametros\":"   + parametros
            + "}";

        RemoteObjectRef ref = new RemoteObjectRef(HOST, PORTA, nomeObjeto);
        byte[] resposta = protocolo.doOperation(ref, metodoId,
                                                 requestJson.getBytes("UTF-8"));
        return new String(resposta, "UTF-8");
    }
}