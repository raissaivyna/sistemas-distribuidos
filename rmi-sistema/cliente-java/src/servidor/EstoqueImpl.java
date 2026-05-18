package servidor;

import entidade.*;
import serializacao.JSON;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class EstoqueImpl {

    private final ClinicaImpl   produtoService;
    private final List<Estoque> estoques  = new CopyOnWriteArrayList<>();
    private final AtomicInteger proximoId = new AtomicInteger(1);

    public EstoqueImpl(ClinicaImpl produtoService) {
        this.produtoService = produtoService;
    }

    public Estoque criarEstoque(String local) {
        Estoque e = new Estoque(proximoId.getAndIncrement(), local);
        estoques.add(e);
        return e;
    }

    public List<Estoque> listarEstoques() {
        return new ArrayList<>(estoques);
    }

    public boolean entradaProduto(int estoqueId, int produtoId) {
        Estoque estoque = buscarEstoque(estoqueId);
        if (estoque == null) return false;

        Produto produto = null;
        for (Produto p : produtoService.listarProdutos())
            if (p.getId() == produtoId) { produto = p; break; }
        if (produto == null) return false;

        estoque.adicionarProduto(produto);
        return true;
    }

    public boolean saidaProduto(int estoqueId, int produtoId) {
        Estoque estoque = buscarEstoque(estoqueId);
        if (estoque == null) return false;
        return estoque.removerPorId(produtoId);
    }

    /**
     * Retorna lista JSON de vencidos — formato igual ao C++:
     * [] quando vazio, ou array de objetos com nome/dataValidade/estoque.
     */
    public String alertarVencidosJson() {
        List<String> itens = new ArrayList<>();
        for (Estoque est : estoques) {
            for (Produto p : est.getProdutos()) {
                if (p instanceof VacinaPerecivel) {
                    VacinaPerecivel vp = (VacinaPerecivel) p;
                    if (vp.isVencido()) {
                        itens.add("{\"nome\":\"" + JSON.esc(vp.getNome()) + "\""
                            + ",\"dataValidade\":\"" + JSON.esc(vp.getDataValidade()) + "\""
                            + ",\"estoque\":\"" + JSON.esc(est.getLocal()) + "\"}");
                    }
                }
            }
        }
        if (itens.isEmpty()) return "[]";
        return "[" + String.join(",", itens) + "]";
    }

    /** Mantido para compatibilidade interna */
    public String alertarVencidos() {
        StringBuilder sb = new StringBuilder("=== Alerta de Vencidos ===\n");
        boolean achou = false;
        for (Estoque est : estoques) {
            for (Produto p : est.getProdutos()) {
                if (p instanceof VacinaPerecivel) {
                    VacinaPerecivel vp = (VacinaPerecivel) p;
                    if (vp.isVencido()) {
                        sb.append("[VENCIDO] ").append(vp.getNome())
                          .append(" | val: ").append(vp.getDataValidade())
                          .append(" | estoque: ").append(est.getLocal()).append("\n");
                        achou = true;
                    }
                }
            }
        }
        if (!achou) sb.append("Nenhum produto vencido.");
        return sb.toString().trim();
    }

    private Estoque buscarEstoque(int id) {
        for (Estoque e : estoques)
            if (e.getId() == id) return e;
        return null;
    }
}