package modelo;

import pojo.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SERVIÇO 2 — EstoqueServico
 * Classe de modelo que gerencia estoques e movimentações de produtos.
 */
public class EstoqueServico {

    private List<Estoque> estoques = new ArrayList<>();
    private int proximoId = 1;

    // ── CRUD de estoques ─────────────────────────────────────────────────────

    public Estoque criarEstoque(String local) {
        Estoque e = new Estoque(proximoId++, local);
        estoques.add(e);
        System.out.println("[ESTOQUE] Criado: " + e);
        return e;
    }

    public Estoque buscarEstoquePorId(int id) {
        return estoques.stream()
                       .filter(e -> e.getId() == id)
                       .findFirst()
                       .orElse(null);
    }

    // ── Movimentações ────────────────────────────────────────────────────────

    /**
     * Adiciona um produto a um estoque específico.
     */
    public boolean entradaProduto(int idEstoque, Produto produto) {
        Estoque estoque = buscarEstoquePorId(idEstoque);
        if (estoque == null) {
            System.out.println("[ERRO] Estoque " + idEstoque + " não encontrado.");
            return false;
        }
        estoque.adicionarProduto(produto);
        System.out.println("[ENTRADA] " + produto.getNome() +
                           " → Estoque: " + estoque.getLocal());
        return true;
    }

    /**
     * Remove um produto de um estoque.
     */
    public boolean saidaProduto(int idEstoque, int idProduto) {
        Estoque estoque = buscarEstoquePorId(idEstoque);
        if (estoque == null) return false;
        boolean ok = estoque.removerProduto(idProduto);
        if (ok) System.out.println("[SAÍDA] Produto id=" + idProduto +
                                   " removido do estoque " + estoque.getLocal());
        return ok;
    }

    // ── Relatórios ───────────────────────────────────────────────────────────

    /**
     * Verifica se alguma vacina perecível está vencida em qualquer estoque.
     */
    public void alertarVencidos() {
        System.out.println("\n=== ALERTA DE PRODUTOS VENCIDOS ===");
        boolean achou = false;
        for (Estoque est : estoques) {
            for (Produto p : est.getProdutos()) {
                if (p instanceof Perecivel) {
                    Perecivel per = (Perecivel) p;
                    if (per.isVencido()) {
                        System.out.println("  [VENCIDO] " + p.getNome() +
                                           " | validade: " + per.getDataValidade() +
                                           " | estoque: " + est.getLocal());
                        achou = true;
                    }
                }
            }
        }
        if (!achou) System.out.println("  Nenhum produto vencido encontrado.");
        System.out.println("===================================\n");
    }

    /**
     * Imprime relatório completo de todos os estoques.
     */
    public void relatorioGeral() {
        System.out.println("\n========= RELATÓRIO GERAL DE ESTOQUES =========");
        for (Estoque est : estoques) {
            System.out.println("\nEstoque: " + est.getLocal() +
                               " (id=" + est.getId() + ") — " +
                               est.getTotalProdutos() + " produto(s)");
            for (Produto p : est.getProdutos()) {
                System.out.println("  > " + p);
            }
        }
        System.out.println("================================================\n");
    }

    public List<Estoque> listarEstoques() { return new ArrayList<>(estoques); }
}