package servico;

import entidade.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * EstoqueServico — Objeto Distribuído 2.
 * Gerencia estoques físicos da clínica.
 * Compartilha o mesmo ProdutoServico para garantir consistência.
 */
public class EstoqueServico {

    private final ProdutoServico produtoServico;
    private final List<Estoque>  estoques  = new CopyOnWriteArrayList<>();
    private final AtomicInteger  proximoId = new AtomicInteger(1);

    public EstoqueServico(ProdutoServico produtoServico) {
        this.produtoServico = produtoServico;
    }

    public Estoque criar(String local) {
        Estoque e = new Estoque(proximoId.getAndIncrement(), local);
        estoques.add(e);
        return e;
    }

    public List<Estoque> listarTodos() {
        return new ArrayList<>(estoques);
    }

    public Optional<Estoque> buscarPorId(int id) {
        return estoques.stream().filter(e -> e.getId() == id).findFirst();
    }

    public boolean entradaProduto(int estoqueId, int produtoId) {
        Optional<Estoque> estoque = buscarPorId(estoqueId);
        Optional<Produto> produto = produtoServico.buscarPorId(produtoId);
        if (estoque.isEmpty() || produto.isEmpty()) return false;
        estoque.get().adicionarProduto(produto.get());
        return true;
    }

    public boolean saidaProduto(int estoqueId, int produtoId) {
        return buscarPorId(estoqueId)
            .map(e -> e.removerPorId(produtoId))
            .orElse(false);
    }

    public List<Map<String, Object>> alertarVencidos() {
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Estoque est : estoques) {
            for (Produto p : est.getProdutos()) {
                if (p instanceof VacinaPerecivel vp && vp.isVencida()) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("produto",   vp.getNome());
                    item.put("validade",  vp.getDataValidade());
                    item.put("estoque",   est.getLocal());
                    item.put("estoqueId", est.getId());
                    resultado.add(item);
                }
            }
        }
        return resultado;
    }
}