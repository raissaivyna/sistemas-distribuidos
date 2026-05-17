package entidade;

import java.util.ArrayList;
import java.util.List;

public class PedidoReposicao {

    private int          id;
    private String       responsavel;
    private String       dataHora;
    private List<Produto> itens;

    public PedidoReposicao() { this.itens = new ArrayList<>(); }

    public PedidoReposicao(int id, String responsavel, String dataHora) {
        this.id          = id;
        this.responsavel = responsavel;
        this.dataHora    = dataHora;
        this.itens       = new ArrayList<>();
    }

    public void adicionarItem(Produto p) { itens.add(p); }

    public double getValorTotal() {
        double total = 0;
        for (Produto p : itens) total += p.getPreco();
        return total;
    }

    public int           getId()          { return id; }
    public void          setId(int id)    { this.id = id; }
    public String        getResponsavel() { return responsavel; }
    public String        getDataHora()    { return dataHora; }
    public List<Produto> getItens()       { return itens; }

    @Override
    public String toString() {
        return "PedidoReposicao{id=" + id + ", responsavel='" + responsavel +
               "', itens=" + itens.size() +
               ", valorTotal=R$" + String.format("%.2f", getValorTotal()) + "}";
    }
}