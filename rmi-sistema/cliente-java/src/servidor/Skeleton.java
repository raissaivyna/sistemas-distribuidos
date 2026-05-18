package servidor;

import entidade.*;
import rmi.*;
import serializacao.JSON;

import java.io.IOException;
import java.net.*;
import java.util.List;

/**
 * Skeleton — dispatcher do servidor RMI.
 *
 * Responsabilidades:
 *   1. Recebe pacotes UDP via getRequest()
 *   2. Desserializa a Message
 *   3. Despacha para o método correto da ClinicaImpl
 *   4. Serializa o resultado
 *   5. Envia de volta via sendReply()
 *
 * Métodos despachados (por methodId):
 *   "listarProdutos"   → ClinicaImpl.listarProdutos()
 *   "buscarPorEspecie" → ClinicaImpl.buscarPorEspecie(especie)
 *   "cadastrarProduto" → ClinicaImpl.cadastrarProduto(produto)
 *   "listarVencidos"   → ClinicaImpl.listarVencidos()
 *   "gerarRelatorio"   → ClinicaImpl.gerarRelatorio()
 *   "registrarPedido"  → ClinicaImpl.registrarPedido(pedido)
 */
public class Skeleton {

    private final ClinicaImpl         impl;
    private final RequestReplyProtocol protocolo;

    public Skeleton(ClinicaImpl impl, DatagramSocket socket) {
        this.impl      = impl;
        this.protocolo = new RequestReplyProtocol(socket);
    }

    /**
     * Loop principal — processa requisições indefinidamente.
     * Cada requisição pode ser tratada em thread separada (multi-threaded).
     */
    public void servirSempre() throws IOException {
        System.out.println("[SKELETON] Aguardando requisições...\n");

        while (true) {
            // 1. Obtém próxima requisição
            DatagramPacket pkt = protocolo.getRequest();

            // 2. Processa em thread separada (servidor multi-threaded)
            final DatagramPacket requisicao = pkt;
            new Thread(() -> {
                try { processarRequisicao(requisicao); }
                catch (IOException e) {
                    System.err.println("[SKELETON] Erro ao processar: " + e.getMessage());
                }
            }).start();
        }
    }

    // ── Processamento de uma requisição ─────────────────────────────────────

    private void processarRequisicao(DatagramPacket pkt) throws IOException {

        // Extrai bytes do pacote
        byte[] data = new byte[pkt.getLength()];
        System.arraycopy(pkt.getData(), pkt.getOffset(), data, 0, pkt.getLength());

        // Desserializa Message
        Message req = Message.fromBytes(data);
        System.out.println("[SKELETON] Requisição: " + req);

        // Despacha pelo methodId
        byte[] resultadoBytes = despachar(req.getMethodId(),
                                           req.getArgumentsAsString());

        // Monta mensagem de resposta
        Message reply = new Message(
            Message.REPLY,
            req.getRequestId(),
            req.getObjectRef(),
            req.getMethodId(),
            resultadoBytes
        );

        // Envia resposta ao cliente
        protocolo.sendReply(reply.toBytes(),
                            pkt.getAddress(),
                            pkt.getPort());
    }

    // ── Dispatcher — methodId → método correto ───────────────────────────────

    private byte[] despachar(String methodId, String argsJson) throws IOException {
        try {
            String resultado = switch (methodId) {

                case "listarProdutos" -> {
                    // Passagem por REFERÊNCIA — lista calculada no servidor
                    List<Produto> lista = impl.listarProdutos();
                    yield JSON.serializarLista(lista);
                }

                case "buscarPorEspecie" -> {
                    // Passagem por VALOR — especie deserializada do JSON
                    String especie = JSON.get(argsJson, "especie");
                    List<Produto> lista = impl.buscarPorEspecie(especie);
                    yield JSON.serializarLista(lista);
                }

                case "cadastrarProduto" -> {
                    // Passagem por VALOR — produto desserializado do JSON
                    Produto produto = JSON.desserializarProduto(argsJson);
                    int novoId = impl.cadastrarProduto(produto);
                    yield "{\"id\":" + novoId + ",\"status\":\"cadastrado\"}";
                }

                case "listarVencidos" -> {
                    // Passagem por REFERÊNCIA — lista calculada no servidor
                    List<VacinaPerecivel> lista = impl.listarVencidos();
                    yield JSON.serializarLista(lista);
                }

                case "gerarRelatorio" -> {
                    // Passagem por REFERÊNCIA — string calculada no servidor
                    yield impl.gerarRelatorio();
                }

                case "registrarPedido" -> {
                    // Passagem por VALOR — pedido desserializado do JSON
                    PedidoReposicao pedido = desserializarPedido(argsJson);
                    yield impl.registrarPedido(pedido);
                }

                default -> "{\"erro\":\"Método desconhecido: " + methodId + "\"}";
            };

            return JSON.toBytes(resultado);

        } catch (Exception e) {
            String erro = "{\"erro\":\"" + e.getMessage() + "\"}";
            return JSON.toBytes(erro);
        }
    }

    // ── Helper: desserializa PedidoReposicao do JSON ─────────────────────────

    private PedidoReposicao desserializarPedido(String json) {
        PedidoReposicao pedido = new PedidoReposicao(
            0,
            JSON.get(json, "responsavel"),
            JSON.get(json, "dataHora")
        );

        // Extrai array de itens
        int itenStart = json.indexOf("\"itens\":[");
        if (itenStart >= 0) {
            int arrStart = json.indexOf('[', itenStart);
            int arrEnd   = json.lastIndexOf(']');
            if (arrStart >= 0 && arrEnd > arrStart) {
                String arr = json.substring(arrStart, arrEnd + 1);
                List<Produto> itens = JSON.desserializarLista(arr);
                itens.forEach(pedido::adicionarItem);
            }
        }
        return pedido;
    }
}