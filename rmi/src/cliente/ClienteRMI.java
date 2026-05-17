package cliente;

import entidade.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClienteRMI {

    static final String HOST  = "localhost";
    static final int    PORTA = 8080;

    public static void main(String[] args) throws Exception {

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║   Cliente RMI — Clinica Veterinaria          ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        Stub clinica = new Stub(HOST, PORTA);

        titulo("1 — listarProdutos() [por referencia]");
        List<Produto> todos = clinica.listarProdutos();
        todos.forEach(p -> System.out.println("  " + p));

        titulo("2 — buscarPorEspecie('Canino') [por valor]");
        List<Produto> caninos = clinica.buscarPorEspecie("Canino");
        System.out.println("  Encontrados: " + caninos.size());
        caninos.forEach(p -> System.out.println("  " + p));

        titulo("3 — cadastrarProduto(VacinaPerecivel) [por valor]");
        VacinaPerecivel novaVacina = new VacinaPerecivel(
            0, "Vacina Leishmaniose", 89.00, "MSD",
            "BR-099", "Canino", "Subcutanea",
            "20/06/2027", "Refrigerado 2-8C", 2.0, 8.0);
        int novoId = clinica.cadastrarProduto(novaVacina);
        System.out.println("  Produto cadastrado com id=" + novoId);

        titulo("4 — listarVencidos() [por referencia]");
        List<VacinaPerecivel> vencidas = clinica.listarVencidos();
        if (vencidas.isEmpty()) {
            System.out.println("  Nenhuma vacina vencida.");
        } else {
            System.out.println("  " + vencidas.size() + " vencida(s):");
            vencidas.forEach(v ->
                System.out.println("  VENCIDA: " + v.getNome() +
                                   " | validade: " + v.getDataValidade()));
        }

        titulo("5 — Cadastrar ProdutoVeterinario e relistar");
        ProdutoVeterinario antiparasitario = new ProdutoVeterinario(
            0, "Ivermectina 1%", 23.50, "Ouro Fino",
            "BR-010", "Bovino", "Subcutanea");
        clinica.cadastrarProduto(antiparasitario);
        List<Produto> bovinos = clinica.buscarPorEspecie("Bovino");
        System.out.println("  Produtos para Bovino: " + bovinos.size());
        bovinos.forEach(p -> System.out.println("  " + p));

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
        System.out.println("  " + clinica.registrarPedido(pedido));

        titulo("7 — gerarRelatorio() [por referencia]");
        System.out.println(clinica.gerarRelatorio());

        System.out.println("\nCliente encerrado.");
    }

    static void titulo(String t) {
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  " + t);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}