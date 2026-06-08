package entidade;

import java.util.ArrayList;
import java.util.List;

public class Estoque {

    private int           id;
    private String        local;
    private List<Produto> produtos = new ArrayList<>();

    public Estoque() {}

    public Estoque(int id, String local) {
        this.id = id; this.local = local;
    }

    public void    adicionarProduto(Produto p)       { produtos.add(p); }
    public boolean removerPorId(int idProduto)        { return produtos.removeIf(p -> p.getId() == idProduto); }
    public int     getTotalProdutos()                 { return produtos.size(); }

    public int           getId()           { return id; }
    public void          setId(int id)     { this.id = id; }
    public String        getLocal()        { return local; }
    public void          setLocal(String l){ this.local = l; }
    public List<Produto> getProdutos()     { return produtos; }
    public void          setProdutos(List<Produto> p) { this.produtos = p; }
}