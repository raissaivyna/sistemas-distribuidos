import pojo.*;
import modelo.*;

/**
 * Classe principal para testar POJOs e serviços.
 * Execute: javac -d out src/**\/*.java && java -cp out Main
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║  Sistema de Controle de Prod. Veterinários║");
        System.out.println("╚══════════════════════════════════════════╝\n");

        // ── 1. Instanciar produtos ───────────────────────────────────────────
        ProdutoQuimioterapico antibiotico = new ProdutoQuimioterapico(
            0, "Amoxicilina 500mg", 45.90, "MSD Saúde Animal",
            "BR-07340.0001", "Canino", "Oral",
            "Amoxicilina", 14.0, "Antibiótico", false
        );

        VacinaPerecivel vacinaRaiva = new VacinaPerecivel(
            0, "Vacina Anti-Rábica Canina", 32.50, "Zoetis",
            "BR-07321.0002", "Canino", "Subcutânea",
            "Vírus Inativado", "PV-11", 10,
            "31/12/2025",          // <-- data no passado = vencida!
            "Refrigerado 2-8°C", 2.0, 8.0
        );

        VacinaPerecivel vacinaV10 = new VacinaPerecivel(
            0, "Vacina Polivalente V10", 28.00, "Merial",
            "BR-07321.0005", "Canino", "Subcutânea",
            "Vírus Atenuado", "Múltiplo", 5,
            "15/08/2026",          // validade futura = ok
            "Refrigerado 2-8°C", 2.0, 8.0
        );

        VacinaNaoPerecivel vacinaBrucela = new VacinaNaoPerecivel(
            0, "Vacina Brucelose Bovina", 18.00, "Fort Dodge",
            "BR-07321.0010", "Bovino", "Subcutânea",
            "Bactéria Inativada", "S19", 20,
            "Liofilizado", 24, 30.0
        );

        // ── 2. Usar ProdutoServico ───────────────────────────────────────────
        ProdutoServico produtoSvc = new ProdutoServico();
        produtoSvc.cadastrar(antibiotico);
        produtoSvc.cadastrar(vacinaRaiva);
        produtoSvc.cadastrar(vacinaV10);
        produtoSvc.cadastrar(vacinaBrucela);

        System.out.println("\nTotal cadastrado: " + produtoSvc.getTotalProdutos());
        System.out.println("Valor total do estoque: R$ " + produtoSvc.calcularValorTotal());

        System.out.println("\n-- Produtos para Caninos --");
        produtoSvc.buscarPorEspecie("Canino").forEach(System.out::println);

        System.out.println("\n-- Vacinas perecíveis VENCIDAS --");
        var vencidas = produtoSvc.listarVencidasPerecíveis();
        if (vencidas.isEmpty()) System.out.println("  Nenhuma.");
        else vencidas.forEach(v -> System.out.println("  VENCIDA: " + v));

        System.out.println("\n-- Serialização (para envio via Stream/TCP) --");
        System.out.println(produtoSvc.serializar(vacinaRaiva));

        // ── 3. Usar EstoqueServico ───────────────────────────────────────────
        EstoqueServico estoqueSvc = new EstoqueServico();
        Estoque geladeira = estoqueSvc.criarEstoque("Clínica - Geladeira A");
        Estoque prateleira = estoqueSvc.criarEstoque("Clínica - Prateleira B");

        estoqueSvc.entradaProduto(geladeira.getId(), vacinaRaiva);
        estoqueSvc.entradaProduto(geladeira.getId(), vacinaV10);
        estoqueSvc.entradaProduto(prateleira.getId(), antibiotico);
        estoqueSvc.entradaProduto(prateleira.getId(), vacinaBrucela);

        estoqueSvc.alertarVencidos();
        estoqueSvc.relatorioGeral();

        // ── 4. Teste de polimorfismo / interface Perecivel ───────────────────
        System.out.println("-- Teste de interface Perecivel --");
        Produto[] todos = { antibiotico, vacinaRaiva, vacinaV10, vacinaBrucela };
        for (Produto p : todos) {
            if (p instanceof Perecivel per) {
                System.out.println(p.getNome() + " → perecível, vencido=" + per.isVencido() +
                                   ", armazenamento: " + per.getRequisitoArmazenamento());
            } else {
                System.out.println(p.getNome() + " → NÃO perecível");
            }
        }
    }
}