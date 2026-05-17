package pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO 7 — Estoque
 * Classe de AGREGAÇÃO: contém uma lista de Produtos.
 * Representa o estoque físico de uma clínica/pet shop/fazenda.
 *
 * Relação: Estoque "tem" Produto[] (agregação, não herança)
 */
public class Estoque {

    private int          id;
    private String       local;        // Ex: "Clínica Central - Geladeira A"
    private List<Produto> produtos;

    public Estoque() {
        this.produtos = new ArrayList<>();
    }

    public Estoque(int id, String local) {
        this.id       = id;
        this.local    = local;
        this.produtos = new ArrayList<>();
    }

    // ── Operações de estoque ─────────────────────────────────────────────────

    public void adicionarProduto(Produto p) {
        produtos.add(p);
    }

    public boolean removerProduto(int idProduto) {
        return produtos.removeIf(p -> p.getId() == idProduto);
    }

    public Produto buscarPorId(int idProduto) {
        return produtos.stream()
                       .filter(p -> p.getId() == idProduto)
                       .findFirst()
                       .orElse(null);
    }

    public int getTotalProdutos() {
        return produtos.size();
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public int          getId()                   { return id; }
    public void         setId(int id)             { this.id = id; }

    public String       getLocal()                { return local; }
    public void         setLocal(String local)    { this.local = local; }

    public List<Produto> getProdutos()            { return produtos; }
    public void          setProdutos(List<Produto> produtos) { this.produtos = produtos; }

    @Override
    public String toString() {
        return "Estoque{id=" + id +
               ", local='" + local + '\'' +
               ", totalProdutos=" + getTotalProdutos() + '}';
    }
}