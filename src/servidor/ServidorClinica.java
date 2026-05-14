package servidor;

import protocolo.Protocolo;
import protocolo.Protocolo.*;
import pojo.*;
import modelo.ProdutoServico;

import java.io.*;
import java.net.*;
import java.util.List;

/**
 * ServidorClinica — servidor TCP multi-threaded da Clinica Veterinaria
 *
 * Responsabilidades:
 *   - Desempacota a requisicao do cliente
 *   - Processa a operacao no servico de produtos
 *   - Empacota a resposta e envia de volta
 *
 * Iniciar: java -cp out servidor.ServidorClinica
 * Porta  : 7896
 */
public class ServidorClinica {

    static final int PORTA = 7896;

    // Repositorio compartilhado entre threads (simula banco em memoria)
    static final ProdutoServico servico = new ProdutoServico();

    public static void main(String[] args) throws IOException {
        popularDados();

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║   Servidor Clinica Veterinaria — porta 7896  ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println("Aguardando clientes...\n");

        try (ServerSocket ss = new ServerSocket(PORTA)) {
            while (true) {
                Socket cliente = ss.accept();
                System.out.println("[SERVIDOR] Conexao: " +
                    cliente.getInetAddress() + ":" + cliente.getPort());
                new Thread(new HandlerCliente(cliente)).start();
            }
        }
    }

    // ── Handler — uma thread por cliente ────────────────────────────────────

    static class HandlerCliente implements Runnable {
        private final Socket socket;
        HandlerCliente(Socket s) { this.socket = s; }

        @Override
        public void run() {
            String id = socket.getInetAddress() + ":" + socket.getPort();
            try (InputStream  in  = socket.getInputStream();
                 OutputStream out = socket.getOutputStream()) {

                while (!socket.isClosed()) {

                    // 1. DESEMPACOTA requisicao do cliente
                    Mensagem req;
                    try {
                        req = Protocolo.desempacotar(in);
                    } catch (EOFException | SocketException e) {
                        System.out.println("[SERVIDOR] Cliente desconectou: " + id);
                        break;
                    }

                    System.out.println("[SERVIDOR] " + id + " → " + req);

                    // 2. Processa operacao
                    byte[] resposta = processar(req);

                    // 3. EMPACOTA e envia resposta
                    out.write(resposta);
                    out.flush();
                }

            } catch (IOException e) {
                System.err.println("[SERVIDOR] Erro " + id + ": " + e.getMessage());
            }
        }

        // ── Logica de cada operacao ──────────────────────────────────────────

        byte[] processar(Mensagem req) throws IOException {

            switch (req.operacao) {

                // ── Listar todos os produtos ─────────────────────────────────
                case LISTAR_PRODUTOS: {
                    List<Produto> lista = servico.listarTodos();
                    if (lista.isEmpty())
                        return ok("[]");

                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < lista.size(); i++) {
                        sb.append(produtoParaJson(lista.get(i)));
                        if (i < lista.size() - 1) sb.append(",");
                    }
                    sb.append("]");
                    return ok(sb.toString());
                }

                // ── Buscar por id ────────────────────────────────────────────
                case BUSCAR_POR_ID: {
                    int id = Integer.parseInt(req.payload.trim());
                    Produto p = servico.buscarPorId(id);
                    if (p == null)
                        return erro("Produto id=" + id + " nao encontrado.");
                    return ok(produtoParaJson(p));
                }

                // ── Buscar por especie ───────────────────────────────────────
                case BUSCAR_POR_ESPECIE: {
                    String especie = req.payload.trim();
                    List<ProdutoVeterinario> lista = servico.buscarPorEspecie(especie);
                    if (lista.isEmpty())
                        return ok("[]");

                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < lista.size(); i++) {
                        sb.append(produtoParaJson(lista.get(i)));
                        if (i < lista.size() - 1) sb.append(",");
                    }
                    sb.append("]");
                    return ok(sb.toString());
                }

                // ── Cadastrar produto ────────────────────────────────────────
                case CADASTRAR: {
                    String nome     = campo(req.payload, "nome");
                    String fab      = campo(req.payload, "fabricante");
                    String tipo     = campo(req.payload, "tipo");
                    double preco    = Double.parseDouble(campo(req.payload, "preco"));
                    String especie  = campo(req.payload, "especie");
                    String via      = campo(req.payload, "via");
                    String validade = campo(req.payload, "validade");
                    String armaz    = campo(req.payload, "armazenamento");

                    Produto novo;
                    if (tipo.equals("VacinaPerecivel")) {
                        double tMin = Double.parseDouble(campo(req.payload, "tempMin"));
                        double tMax = Double.parseDouble(campo(req.payload, "tempMax"));
                        novo = new VacinaPerecivel(0, nome, preco, fab,
                            "", especie, via, "", "", 0,
                            validade, armaz, tMin, tMax);
                    } else {
                        novo = new ProdutoQuimioterapico(0, nome, preco, fab,
                            "", especie, via,
                            campo(req.payload, "principioAtivo"), 0, "", false);
                    }

                    servico.cadastrar(novo);
                    return ok("Produto '" + nome + "' cadastrado com id=" + novo.getId());
                }

                // ── Remover produto ──────────────────────────────────────────
                case REMOVER: {
                    int id = Integer.parseInt(req.payload.trim());
                    boolean ok = servico.remover(id);
                    return ok ? ok("Produto id=" + id + " removido com sucesso.")
                              : erro("Produto id=" + id + " nao encontrado.");
                }

                // ── Listar vacinas vencidas ──────────────────────────────────
                case LISTAR_VENCIDOS: {
                    List<VacinaPerecivel> vencidas = servico.listarVencidasPerecíveis();
                    if (vencidas.isEmpty())
                        return ok("Nenhuma vacina vencida encontrada.");

                    StringBuilder sb = new StringBuilder();
                    for (VacinaPerecivel v : vencidas) {
                        sb.append("[VENCIDA] ").append(v.getNome())
                          .append(" | validade: ").append(v.getDataValidade())
                          .append("\n");
                    }
                    return ok(sb.toString().trim());
                }

                // ── Relatorio de estoque ─────────────────────────────────────
                case RELATORIO_ESTOQUE: {
                    List<Produto> todos = servico.listarTodos();
                    long vacinas    = todos.stream().filter(p -> p instanceof VacinaPerecivel).count();
                    long quimio     = todos.stream().filter(p -> p instanceof ProdutoQuimioterapico).count();
                    double valorTotal = servico.calcularValorTotal();

                    String rel = "Total de produtos: " + todos.size() + "\n" +
                                 "Vacinas pereciveis: " + vacinas + "\n" +
                                 "Quimioterapicos: " + quimio + "\n" +
                                 "Valor total do estoque: R$ " + String.format("%.2f", valorTotal);
                    return ok(rel);
                }

                default:
                    return erro("Operacao nao reconhecida.");
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    static byte[] ok(String payload) throws IOException {
        return Protocolo.empacotar(Operacao.RESPOSTA_OK, payload);
    }

    static byte[] erro(String msg) throws IOException {
        return Protocolo.empacotar(Operacao.RESPOSTA_ERRO, msg);
    }

    /** Serializa Produto para JSON simples */
    static String produtoParaJson(Produto p) {
        String tipo     = p.getClass().getSimpleName();
        String validade = (p instanceof VacinaPerecivel vp) ? vp.getDataValidade() : "N/A";
        String vencida  = (p instanceof VacinaPerecivel vp) ? String.valueOf(vp.isVencido()) : "N/A";
        String especie  = (p instanceof ProdutoVeterinario pv) ? pv.getEspecieAlvo() : "";

        return "{\"id\":" + p.getId() +
               ",\"nome\":\"" + p.getNome() + "\"" +
               ",\"preco\":" + p.getPreco() +
               ",\"fabricante\":\"" + p.getFabricante() + "\"" +
               ",\"tipo\":\"" + tipo + "\"" +
               ",\"especie\":\"" + especie + "\"" +
               ",\"validade\":\"" + validade + "\"" +
               ",\"vencida\":" + vencida + "}";
    }

    /** Extrai valor de campo JSON simples por chave */
    static String campo(String json, String chave) {
        String busca = "\"" + chave + "\":";
        int ini = json.indexOf(busca);
        if (ini < 0) return "";
        ini += busca.length();
        if (ini >= json.length()) return "";
        if (json.charAt(ini) == '"') {
            int fim = json.indexOf('"', ini + 1);
            return fim < 0 ? "" : json.substring(ini + 1, fim);
        }
        int fim = json.indexOf(',', ini);
        if (fim < 0) fim = json.indexOf('}', ini);
        return fim < 0 ? "" : json.substring(ini, fim).trim();
    }

    /** Dados iniciais */
    static void popularDados() {
        servico.cadastrar(new VacinaPerecivel(
            0, "Vacina Anti-Rabica", 32.50, "Zoetis",
            "BR-001", "Canino", "Subcutanea",
            "Virus Inativado", "PV-11", 10,
            "31/12/2025", "Refrigerado 2-8C", 2.0, 8.0));

        servico.cadastrar(new VacinaPerecivel(
            0, "Vacina Polivalente V10", 28.00, "Merial",
            "BR-002", "Canino", "Subcutanea",
            "Virus Atenuado", "Multiplo", 5,
            "15/08/2026", "Refrigerado 2-8C", 2.0, 8.0));

        servico.cadastrar(new VacinaPerecivel(
            0, "Vacina Febre Aftosa", 12.00, "Boehringer",
            "BR-003", "Bovino", "Intramuscular",
            "Virus Inativado", "O1", 50,
            "01/03/2027", "Refrigerado 4-8C", 4.0, 8.0));

        servico.cadastrar(new ProdutoQuimioterapico(
            0, "Amoxicilina 500mg", 45.90, "MSD",
            "BR-004", "Canino", "Oral",
            "Amoxicilina", 14.0, "Antibiotico", false));

        System.out.println("[SERVIDOR] " + servico.getTotalProdutos() +
                           " produtos carregados.\n");
    }
}