package cliente;

import entidade.*;
import rmi.*;
import serializacao.JSON;
import servidor.ClinicaRemota;

import java.io.IOException;
import java.net.DatagramSocket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Stub — proxy do lado cliente.
 *
 * Implementa ClinicaRemota, mas em vez de executar localmente,
 * cada método serializa os argumentos, chama doOperation() e
 * desserializa a resposta.
 *
 * O cliente usa o Stub como se fosse o objeto local — não sabe
 * que está se comunicando via rede UDP.
 *
 * Passagem por REFERÊNCIA → resultado vem do servidor via rede
 * Passagem por VALOR      → argumentos serializados em JSON antes de enviar
 */
public class Stub implements ClinicaRemota {

    private final RemoteObjectRef      ref;
    private final RequestReplyProtocol protocolo;

    public Stub(String host, int porta, DatagramSocket socket) {
        this.ref       = new RemoteObjectRef(host, porta, "ClinicaServico");
        this.protocolo = new RequestReplyProtocol(socket);
    }

    // ── Método 1: listarProdutos ─────────────────────────────────────────────
    // Passagem por REFERÊNCIA — resultado vem do servidor

    @Override
    public List<Produto> listarProdutos() {
        try {
            byte[] resposta = protocolo.doOperation(ref, "listarProdutos",
                                                     JSON.toBytes("{}"));
            return JSON.desserializarLista(JSON.fromBytes(resposta));
        } catch (IOException e) {
            System.err.println("[STUB] Erro listarProdutos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ── Método 2: buscarPorEspecie ───────────────────────────────────────────
    // Passagem por VALOR — String especie serializada em JSON

    @Override
    public List<Produto> buscarPorEspecie(String especie) {
        try {
            // Serializa argumento por VALOR
            String argsJson = "{\"especie\":\"" + especie + "\"}";
            byte[] resposta = protocolo.doOperation(ref, "buscarPorEspecie",
                                                     JSON.toBytes(argsJson));
            return JSON.desserializarLista(JSON.fromBytes(resposta));
        } catch (IOException e) {
            System.err.println("[STUB] Erro buscarPorEspecie: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ── Método 3: cadastrarProduto ───────────────────────────────────────────
    // Passagem por VALOR — Produto serializado em JSON

    @Override
    public int cadastrarProduto(Produto produto) {
        try {
            // Serializa objeto por VALOR (representação externa de dados)
            String jsonProduto = JSON.serializar(produto);
            byte[] resposta    = protocolo.doOperation(ref, "cadastrarProduto",
                                                        JSON.toBytes(jsonProduto));
            String respostaStr = JSON.fromBytes(resposta);
            return JSON.getInt(respostaStr, "id");
        } catch (IOException e) {
            System.err.println("[STUB] Erro cadastrarProduto: " + e.getMessage());
            return -1;
        }
    }

    // ── Método 4: listarVencidos ─────────────────────────────────────────────
    // Passagem por REFERÊNCIA — resultado calculado no servidor

    @Override
    public List<VacinaPerecivel> listarVencidos() {
        try {
            byte[] resposta = protocolo.doOperation(ref, "listarVencidos",
                                                     JSON.toBytes("{}"));
            List<Produto> lista = JSON.desserializarLista(JSON.fromBytes(resposta));
            List<VacinaPerecivel> vencidas = new ArrayList<>();
            for (Produto p : lista)
                if (p instanceof VacinaPerecivel vp) vencidas.add(vp);
            return vencidas;
        } catch (IOException e) {
            System.err.println("[STUB] Erro listarVencidos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ── Método 5: gerarRelatorio ─────────────────────────────────────────────
    // Passagem por REFERÊNCIA — string calculada no servidor

    @Override
    public String gerarRelatorio() {
        try {
            byte[] resposta = protocolo.doOperation(ref, "gerarRelatorio",
                                                     JSON.toBytes("{}"));
            return JSON.fromBytes(resposta);
        } catch (IOException e) {
            System.err.println("[STUB] Erro gerarRelatorio: " + e.getMessage());
            return "Erro ao obter relatório.";
        }
    }

    // ── Método 6: registrarPedido ────────────────────────────────────────────
    // Passagem por VALOR — PedidoReposicao serializado em JSON

    @Override
    public String registrarPedido(PedidoReposicao pedido) {
        try {
            // Serializa objeto por VALOR
            String jsonPedido = serializarPedido(pedido);
            byte[] resposta   = protocolo.doOperation(ref, "registrarPedido",
                                                       JSON.toBytes(jsonPedido));
            return JSON.fromBytes(resposta);
        } catch (IOException e) {
            System.err.println("[STUB] Erro registrarPedido: " + e.getMessage());
            return "Erro ao registrar pedido.";
        }
    }

    // ── Helper — serializa PedidoReposicao para JSON ─────────────────────────

    private String serializarPedido(PedidoReposicao p) {
        return "{\"responsavel\":\"" + p.getResponsavel() + "\"" +
               ",\"dataHora\":\"" + p.getDataHora() + "\"" +
               ",\"itens\":" + JSON.serializarLista(p.getItens()) + "}";
    }
}