package servidor;

import protocolo.Protocolo;
import protocolo.Protocolo.*;
import protocolo.SerializadorJSON;
import multicast.AlertaClinica;
import multicast.AlertaClinica.TipoAlerta;
import multicast.JanelaOperacao;
import pojo.*;
import modelo.ProdutoServico;

import java.io.*;
import java.net.*;
import java.util.List;

/**
 * ServidorClinicaCompleto — servidor final da Clinica Veterinaria
 *
 * Integra:
 *   TCP  → servico remoto de produtos (Fase 4)
 *   JSON → representacao externa de dados (Fase 5)
 *   UDP  → alertas multicast para todos os clientes
 *   JANELA → tempo limite para pedidos de reposicao
 *
 * Operacoes TCP (Protocolo):
 *   LISTAR_PRODUTOS    → lista todos
 *   BUSCAR_POR_ID      → busca por id
 *   BUSCAR_POR_ESPECIE → busca por especie
 *   CADASTRAR          → cadastra produto (payload JSON)
 *   REMOVER            → remove por id
 *   LISTAR_VENCIDOS    → alerta sobre vencidos
 *   RELATORIO_ESTOQUE  → resumo do estoque
 *   + novas operacoes:
 *   ABRIR_JANELA (8)   → admin abre janela de pedidos
 *   PEDIDO_REPOSICAO(9)→ clinico registra pedido durante janela
 *   ENVIAR_ALERTA (20) → admin envia alerta multicast
 *
 * Iniciar: java -cp out servidor.ServidorClinicaCompleto
 */
public class ServidorClinicaCompleto {

    static final int PORTA = 7896;

    static final ProdutoServico servico = new ProdutoServico();
    static JanelaOperacao janelaAtiva   = null;

    public static void main(String[] args) throws IOException {
        popularDados();
        verificarVencidosAoIniciar();

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║  Servidor Completo — Clinica Veterinaria     ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  TCP  → porta " + PORTA + " (servico de produtos)    ║");
        System.out.println("║  UDP  → multicast " + AlertaClinica.GRUPO_MULTICAST
                           + ":" + AlertaClinica.PORTA_UDP + "    ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        try (ServerSocket ss = new ServerSocket(PORTA)) {
            while (true) {
                Socket cliente = ss.accept();
                System.out.println("[TCP] Conexao: " +
                    cliente.getInetAddress() + ":" + cliente.getPort());
                new Thread(new HandlerCliente(cliente)).start();
            }
        }
    }

    // ── Handler TCP — uma thread por cliente ─────────────────────────────────

    static class HandlerCliente implements Runnable {
        private final Socket socket;
        HandlerCliente(Socket s) { this.socket = s; }

        @Override
        public void run() {
            String cid = socket.getInetAddress() + ":" + socket.getPort();
            try (InputStream  in  = socket.getInputStream();
                 OutputStream out = socket.getOutputStream()) {

                while (!socket.isClosed()) {
                    Mensagem req;
                    try {
                        req = Protocolo.desempacotar(in);
                    } catch (EOFException | SocketException e) {
                        System.out.println("[TCP] Desconectou: " + cid);
                        break;
                    }

                    System.out.println("[TCP] " + cid + " → " + req.operacao +
                                       " | " + req.payload.substring(0,
                                           Math.min(60, req.payload.length())));

                    byte[] resp = processar(req, cid);
                    out.write(resp);
                    out.flush();
                }
            } catch (IOException e) {
                System.err.println("[TCP] Erro " + cid + ": " + e.getMessage());
            }
        }

        byte[] processar(Mensagem req, String cid) throws IOException {
            switch (req.operacao) {

                // ── Operacoes de produto (Fases 4 e 5) ──────────────────────

                case LISTAR_PRODUTOS: {
                    List<Produto> lista = servico.listarTodos();
                    if (lista.isEmpty()) return ok("[]");
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < lista.size(); i++) {
                        sb.append(SerializadorJSON.serializar(lista.get(i)));
                        if (i < lista.size() - 1) sb.append(",");
                    }
                    return ok(sb.append("]").toString());
                }

                case BUSCAR_POR_ID: {
                    int id = Integer.parseInt(req.payload.trim());
                    Produto p = servico.buscarPorId(id);
                    return p == null
                        ? erro("Produto id=" + id + " nao encontrado.")
                        : ok(SerializadorJSON.serializar(p));
                }

                case BUSCAR_POR_ESPECIE: {
                    List<ProdutoVeterinario> lista =
                        servico.buscarPorEspecie(req.payload.trim());
                    if (lista.isEmpty()) return ok("[]");
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < lista.size(); i++) {
                        sb.append(SerializadorJSON.serializar(lista.get(i)));
                        if (i < lista.size() - 1) sb.append(",");
                    }
                    return ok(sb.append("]").toString());
                }

                case CADASTRAR: {
                    // Desserializa JSON → objeto real
                    Produto novo = SerializadorJSON.desserializar(req.payload);
                    servico.cadastrar(novo);

                    // Envia alerta se for vacina perecivel proxima do vencimento
                    if (novo instanceof VacinaPerecivel vp && vp.isVencido()) {
                        AlertaClinica.enviarAlerta(TipoAlerta.VENCIMENTO,
                            "Produto cadastrado JA VENCIDO: " + vp.getNome() +
                            " | validade: " + vp.getDataValidade());
                    }
                    return ok("Cadastrado: " + novo.getNome() + " (id=" + novo.getId() + ")");
                }

                case REMOVER: {
                    int id = Integer.parseInt(req.payload.trim());
                    Produto p = servico.buscarPorId(id);
                    boolean ok = servico.remover(id);
                    if (ok && p != null) {
                        AlertaClinica.enviarAlerta(TipoAlerta.SISTEMA,
                            "Produto removido do estoque: " + p.getNome() +
                            " (id=" + id + ") por " + cid);
                    }
                    return ok ? ok("Removido id=" + id)
                              : erro("Nao encontrado id=" + id);
                }

                case LISTAR_VENCIDOS: {
                    List<VacinaPerecivel> v = servico.listarVencidasPerecíveis();
                    if (v.isEmpty()) return ok("Nenhuma vacina vencida.");
                    StringBuilder sb = new StringBuilder();
                    v.forEach(vp -> sb.append(SerializadorJSON.serializar(vp)).append("\n"));
                    // Dispara alerta automatico se houver vencidos
                    AlertaClinica.enviarAlerta(TipoAlerta.VENCIMENTO,
                        v.size() + " vacina(s) vencida(s) detectada(s) no estoque!");
                    return ok(sb.toString().trim());
                }

                case RELATORIO_ESTOQUE: {
                    List<Produto> todos   = servico.listarTodos();
                    long vacinas          = todos.stream().filter(p -> p instanceof VacinaPerecivel).count();
                    long quimio           = todos.stream().filter(p -> p instanceof ProdutoQuimioterapico).count();
                    double valor          = servico.calcularValorTotal();
                    String janelaStatus   = janelaAtiva != null && janelaAtiva.isAberta()
                        ? "ABERTA — " + janelaAtiva.getNomeOperacao()
                        : "fechada";

                    return ok("Total de produtos   : " + todos.size() + "\n" +
                              "Vacinas pereciveis  : " + vacinas + "\n" +
                              "Quimioterapicos     : " + quimio + "\n" +
                              "Valor total (R$)    : " + String.format("%.2f", valor) + "\n" +
                              "Janela de operacao  : " + janelaStatus);
                }

                // ── Operacao 8: Admin abre janela de pedidos ─────────────────
                // payload: "NomeOperacao|duracaoSegundos"
                case ABRIR_JANELA: {
                    String[] partes = req.payload.split("\\|", 2);
                    String nome     = partes[0].trim();
                    int    duracao  = partes.length > 1
                                     ? Integer.parseInt(partes[1].trim()) : 120;

                    if (janelaAtiva != null && janelaAtiva.isAberta()) {
                        return erro("Ja existe uma janela aberta: " +
                                    janelaAtiva.getNomeOperacao());
                    }

                    janelaAtiva = new JanelaOperacao(nome, duracao);
                    janelaAtiva.aoEncerrar(() ->
                        AlertaClinica.enviarAlerta(TipoAlerta.SISTEMA,
                            "JANELA ENCERRADA: " + nome +
                            " | Pedidos: " + janelaAtiva.getPedidos().values()
                                .stream().mapToInt(java.util.List::size).sum())
                    );
                    janelaAtiva.abrir();

                    return ok("Janela '" + nome + "' aberta por " + duracao + "s.");
                }

                // ── Operacao 9: Clinico registra pedido de reposicao ─────────
                // payload: "responsavel|{jsonProduto}"
                case PEDIDO_REPOSICAO: {
                    String[] partes   = req.payload.split("\\|", 2);
                    String responsavel = partes[0].trim();
                    String jsonProduto = partes.length > 1 ? partes[1] : "{}";

                    if (janelaAtiva == null || !janelaAtiva.isAberta())
                        return erro("Nenhuma janela de pedidos aberta no momento.");

                    Produto prod = SerializadorJSON.desserializar(jsonProduto);
                    boolean aceito = janelaAtiva.registrarPedido(responsavel, prod);

                    return aceito
                        ? ok("Pedido de '" + prod.getNome() + "' registrado com sucesso.")
                        : erro("Janela encerrada. Pedido nao aceito.");
                }

                // ── Operacao 20: Admin envia alerta multicast ────────────────
                // payload: "TIPO|mensagem"
                case ENVIAR_ALERTA: {
                    String[] partes = req.payload.split("\\|", 2);
                    TipoAlerta tipo;
                    String mensagem;
                    try {
                        tipo     = TipoAlerta.valueOf(partes[0].trim().toUpperCase());
                        mensagem = partes.length > 1 ? partes[1] : partes[0];
                    } catch (IllegalArgumentException e) {
                        tipo     = TipoAlerta.URGENCIA;
                        mensagem = req.payload;
                    }
                    AlertaClinica.enviarAlerta(tipo, mensagem);
                    return ok("Alerta " + tipo + " enviado via multicast.");
                }

                default:
                    return erro("Operacao desconhecida: " + req.operacao);
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    static byte[] ok(String p)   throws IOException {
        return Protocolo.empacotar(Operacao.RESPOSTA_OK,   p);
    }
    static byte[] erro(String p) throws IOException {
        return Protocolo.empacotar(Operacao.RESPOSTA_ERRO, p);
    }

    static void verificarVencidosAoIniciar() {
        List<VacinaPerecivel> vencidas = servico.listarVencidasPerecíveis();
        if (!vencidas.isEmpty()) {
            System.out.println("[ALERTA] " + vencidas.size() + " vacina(s) vencida(s)!");
            AlertaClinica.enviarAlerta(TipoAlerta.VENCIMENTO,
                vencidas.size() + " produto(s) vencido(s) no estoque ao iniciar o servidor!");
        }
    }

    static void popularDados() {
        servico.cadastrar(new VacinaPerecivel(0, "Vacina Anti-Rabica",    32.50, "Zoetis",
            "BR-001", "Canino", "Subcutanea", "Virus Inativado", "PV-11", 10,
            "31/12/2025", "Refrigerado 2-8C", 2.0, 8.0));
        servico.cadastrar(new VacinaPerecivel(0, "Vacina Polivalente V10", 28.00, "Merial",
            "BR-002", "Canino", "Subcutanea", "Virus Atenuado", "Multiplo", 5,
            "15/08/2026", "Refrigerado 2-8C", 2.0, 8.0));
        servico.cadastrar(new VacinaPerecivel(0, "Vacina Febre Aftosa",    12.00, "Boehringer",
            "BR-003", "Bovino", "Intramuscular", "Virus Inativado", "O1", 50,
            "01/03/2027", "Refrigerado 4-8C", 4.0, 8.0));
        servico.cadastrar(new ProdutoQuimioterapico(0, "Amoxicilina 500mg", 45.90, "MSD",
            "BR-004", "Canino", "Oral", "Amoxicilina", 14.0, "Antibiotico", false));
        System.out.println("[INIT] " + servico.getTotalProdutos() + " produtos carregados.\n");
    }
}