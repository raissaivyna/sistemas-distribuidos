package cliente;

import entidade.*;
import rmi.RemoteObjectRef;
import rmi.RequestReplyProtocol;
import serializacao.JSON;

import java.io.IOException;
import java.util.List;
/*1--- int id = produtoService.buscarPorID(1); */
public class ClienteRMI {

    static final String HOST  = "localhost";
    static final int    PORTA = 8080;

    static RequestReplyProtocol protocolo = new RequestReplyProtocol();

    public static void main(String[] args) throws Exception {

        System.out.println("========== Cliente RMI Java ==========\n");

        System.out.println("[1] chamando buscarPorId(1)...");
        print(chamar("ProdutoService", 1, "buscarPorId", "{\"id\":1}", 1));

        System.out.println("[2] chamando listarTodos()...");
        print(chamar("ProdutoService", 1, "listarTodos", "{}", 2));

        System.out.println("[3] chamando buscarPorEspecie('Canino')...");
        print(chamar("ProdutoService", 1, "buscarPorEspecie", "{\"especie\":\"Canino\"}", 3));

        System.out.println("[4] chamando calcularValorTotal()...");
        print(chamar("ProdutoService", 1, "calcularValorTotal", "{}", 4));

        System.out.println("[5] chamando remover(2)...");
        print(chamar("ProdutoService", 1, "remover", "{\"id\":2}", 5));

        System.out.println("[6] chamando listarTodos() apos remover...");
        print(chamar("ProdutoService", 1, "listarTodos", "{}", 6));

        System.out.println("[7] chamando buscarPorId(999)... (nao existe)");
        print(chamar("ProdutoService", 1, "buscarPorId", "{\"id\":999}", 7));

        System.out.println("[8] chamando metodo inexistente 'fooBar'...");
        print(chamar("ProdutoService", 1, "fooBar", "{}", 8));

        System.out.println("[9] chamando criarEstoque('Deposito Central')...");
        print(chamar("EstoqueService", 2, "criarEstoque", "{\"local\":\"Deposito Central\"}", 9));

        System.out.println("[10] chamando listarEstoques()...");
        print(chamar("EstoqueService", 2, "listarEstoques", "{}", 10));

        System.out.println("[11] chamando entradaProduto(estoqueId:1, produtoId:1)...");
        print(chamar("EstoqueService", 2, "entradaProduto", "{\"estoqueId\":1,\"produtoId\":1}", 11));

        System.out.println("[12] chamando listarEstoques() apos entrada...");
        print(chamar("EstoqueService", 2, "listarEstoques", "{}", 12));

        System.out.println("[13] chamando alertarVencidos()...");
        print(chamar("EstoqueService", 2, "alertarVencidos", "{}", 13));

        System.out.println("[14] chamando saidaProduto(estoqueId:1, produtoId:1)...");
        print(chamar("EstoqueService", 2, "saidaProduto", "{\"estoqueId\":1,\"produtoId\":1}", 14));

        System.out.println("[15] chamando listarEstoques() apos saida...");
        print(chamar("EstoqueService", 2, "listarEstoques", "{}", 15));

        System.out.println("[16] chamando alertarVencidos() apos saida...");
        print(chamar("EstoqueService", 2, "alertarVencidos", "{}", 16));

        System.out.println("[17] chamando 'fooBar' no EstoqueService...");
        print(chamar("EstoqueService", 2, "fooBar", "{}", 17));

        System.out.println("[18] chamando 'fooBar' no ProdutoService...");
        print(chamar("ProdutoService", 1, "fooBar", "{}", 18));

        System.out.println("========== Fim dos testes ==========");
    }

    static void print(String json) {
        System.out.println("Resposta recebida: " + prettyPrint(json));
        System.out.println();
    }

    // ── Pretty-print JSON manual ─────────────────────────────────────────────

    static String prettyPrint(String json) {
        if (json == null || json.isBlank()) return json;
        StringBuilder sb     = new StringBuilder();
        int           indent = 0;
        boolean       inStr  = false;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            // controla se estamos dentro de uma string
            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                inStr = !inStr;
                sb.append(c);
                continue;
            }

            if (inStr) { sb.append(c); continue; }

            switch (c) {
                case '{': case '[':
                    sb.append(c).append('\n').append(spaces(++indent));
                    break;
                case '}': case ']':
                    sb.append('\n').append(spaces(--indent)).append(c);
                    break;
                case ',':
                    sb.append(c).append('\n').append(spaces(indent));
                    break;
                case ':':
                    sb.append(": ");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    static String spaces(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n * 4; i++) sb.append(' ');
        return sb.toString();
    }

    // ── Envio da requisição ──────────────────────────────────────────────────
/*--6---O método chamar() no ClienteRMI monta a mensagem no formato exato que o servidor espera */
    static String chamar(String nomeObjeto, int objetoId,
                          String metodoId, String parametros,
                          int requestId) throws IOException {

        String referenciaObj = "{\"objetoId\":" + objetoId +
                               ",\"NomeObjeto\":\"" + nomeObjeto + "\"}";

        String requestJson = "{"
            + "\"TipoMensagem\":\"Request\","
            + "\"requestId\":"    + requestId    + ","
            + "\"referenciaObj\":" + referenciaObj + ","
            + "\"metodoId\":\""   + metodoId     + "\","
            + "\"parametros\":"   + parametros
            + "}";
//4---
/*Cliente cria uma referencia assim :
já que o trabalho pede passagem por referência para a execução de objetos remotos.

--ultimo
ex:  chamar("ProdutoService", 1, "buscarPorId", "{\"id\":1}", 1)

{
    "TipoMensagem": "Request",
    "requestId": 1,
    "referenciaObj": { "objetoId": 1, "NomeObjeto": "ProdutoService" },
    "metodoId": "buscarPorId",
    "parametros": { "id": 1 }
}

 */
        RemoteObjectRef ref = new RemoteObjectRef(HOST, PORTA, nomeObjeto);
        byte[] resposta = protocolo.doOperation(ref, metodoId,
                                                 requestJson.getBytes("UTF-8"));
        return new String(resposta, "UTF-8");
    }
}