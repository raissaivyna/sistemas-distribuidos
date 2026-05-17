package cliente;

import entidade.*;
import java.net.DatagramSocket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ClienteRMI — demonstra todas as invocações remotas da Clínica Veterinária.
 *
 * Usa o Stub como se fosse um objeto local.
 * O Stub cuida de serializar/enviar/receber/desserializar via UDP.
 *
 * Requer ServidorRMI rodando:
 *   java -cp out servidor.ServidorRMI
 *
 * Executar:
 *   java -cp out cliente.ClienteRMI
 */
public class ClienteRMI {

    static final String HOST  = "localhost";
    static final int    PORTA = 7896;

    public static void main(String[] args) throws Exception {

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║   Cliente RMI — Clínica Veterinária          ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        // DatagramSocket do cliente (porta 0 = SO escolhe automaticamente)
        try (DatagramSocket socket = new DatagramSocket()) {

            // Stub age como objeto local — internamente chama doOperation via UDP
            Stub clinica = new Stub(HOST, PORTA, socket);

            // ── 1. Listar todos os produtos (passagem por referência) ─────────
            titulo("1 — listarProdutos() [por referência]");
            List<Produto> todos = clinica.listarProdutos();
            todos.forEach(p -> System.out.println("  " + p));

            // ── 2. Buscar por espécie (passagem por valor — String) ───────────
            titulo("2 — buscarPorEspecie('Canino') [por valor]");
            List<Produto> caninos = clinica.buscarPorEspecie("Canino");
            System.out.println("  Encontrados: " + caninos.size());
            caninos.forEach(p -> System.out.println("  " + p));

            // ── 3. Cadastrar produto (passagem por valor — objeto JSON) ────────
            titulo("3 — cadastrarProduto(VacinaPerecivel) [por valor]");
            VacinaPerecivel novaVacina = new VacinaPerecivel(
                0, "Vacina Leishmaniose", 89.00, "MSD",
                "BR-099", "Canino", "Subcutanea",
                "20/06/2027", "Refrigerado 2-8C", 2.0, 8.0);
            int novoId = clinica.cadastrarProduto(novaVacina);
            System.out.println("  Produto cadastrado com id=" + novoId);

            // ── 4. Listar vencidos (passagem por referência) ──────────────────
            titulo("4 — listarVencidos() [por referência]");
            List<VacinaPerecivel> vencidas = clinica.listarVencidos();
            if (vencidas.isEmpty()) {
                System.out.println("  Nenhuma vacina vencida.");
            } else {
                System.out.println("  " + vencidas.size() + " vencida(s):");
                vencidas.forEach(v ->
                    System.out.println("  ⚠️  " + v.getNome() +
                                       " | validade: " + v.getDataValidade()));
            }

            // ── 5. Cadastrar mais um produto e listar novamente ────────────────
            titulo("5 — Cadastrar ProdutoVeterinario e relistar");
            ProdutoVeterinario antiparasitario = new ProdutoVeterinario(
                0, "Ivermectina 1%", 23.50, "Ouro Fino",
                "BR-010", "Bovino", "Subcutanea");
            clinica.cadastrarProduto(antiparasitario);

            List<Produto> bovinos = clinica.buscarPorEspecie("Bovino");
            System.out.println("  Produtos para Bovino: " + bovinos.size());
            bovinos.forEach(p -> System.out.println("  " + p));

            // ── 6. Registrar pedido de reposição (passagem por valor — objeto) ─
            titulo("6 — registrarPedido(PedidoReposicao) [por valor]");
            String agora = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            PedidoReposicao pedido = new PedidoReposicao(0, "dr.joao", agora);
            pedido.adicionarItem(new VacinaPerecivel(
                0, "Vacina Anti-Rabica", 32.50, "Zoetis",
                "BR-001", "Canino", "Subcutanea",
                "15/08/2027", "Refrigerado 2-8C", 2.0, 8.0));
            pedido.adicionarItem(new ProdutoVeterinario(
                0, "Amoxicilina 500mg", 45.90, "MSD",
                "BR-004", "Canino", "Oral"));

            String confirmacao = clinica.registrarPedido(pedido);
            System.out.println("  " + confirmacao);

            // ── 7. Relatório final (passagem por referência) ──────────────────
            titulo("7 — gerarRelatorio() [por referência]");
            System.out.println(clinica.gerarRelatorio());
        }

        System.out.println("\nCliente encerrado.");
    }

    static void titulo(String t) {
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  " + t);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}