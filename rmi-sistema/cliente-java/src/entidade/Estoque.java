package entidade;

import java.util.ArrayList;
import java.util.List;

/**
 * Estoque — entidade (4 de 4)
 * Agregação ("tem-um") de Produto[].
 * Representa o estoque físico da clínica.
 */
public class Estoque {

    private int           id;
    private String        local;
    private List<Produto> produtos;   // ← agregação

    public Estoque() { this.produtos = new ArrayList<>(); }

    public Estoque(int id, String local) {
        this.id       = id;
        this.local    = local;
        this.produtos = new ArrayList<>();
    }

    public void    adicionarProduto(Produto p)           { produtos.add(p); }
    public boolean removerPorId(int idProduto)           { return produtos.removeIf(p -> p.getId() == idProduto); }
    public int     getTotalProdutos()                    { return produtos.size(); }

    public Produto buscarPorId(int idProduto) {
        return produtos.stream()
                       .filter(p -> p.getId() == idProduto)
                       .findFirst().orElse(null);
    }

    public int    getId()                  { return id; }
    public void   setId(int id)            { this.id = id; }
    public String getLocal()               { return local; }
    public void   setLocal(String local)   { this.local = local; }
    public List<Produto> getProdutos()     { return produtos; }

    @Override
    public String toString() {
        return "Estoque{id=" + id + ", local='" + local +
               "', total=" + getTotalProdutos() + "}";
    }
}