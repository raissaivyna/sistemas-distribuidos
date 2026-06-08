package servico;

import entidade.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * RelatorioServico — Objeto Distribuído 3.
 * Gera relatórios consolidados da clínica.
 */
public class RelatorioServico {

    private final ProdutoServico produtoServico;
    private final EstoqueServico estoqueServico;

    public RelatorioServico(ProdutoServico ps, EstoqueServico es) {
        this.produtoServico = ps;
        this.estoqueServico = es;
    }

    /** Relatório geral consolidado */
    public Map<String, Object> gerarRelatorioGeral() {
        List<Produto> todos    = produtoServico.listarTodos();
        List<Estoque> estoques = estoqueServico.listarTodos();

        long vacinas  = todos.stream().filter(p -> p instanceof VacinaPerecivel).count();
        long vencidas = produtoServico.listarVencidas().size();
        double valor  = produtoServico.calcularValorTotal();

        Map<String, Object> rel = new LinkedHashMap<>();
        rel.put("geradoEm",           LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        rel.put("totalProdutos",      todos.size());
        rel.put("vacinasPerecíveis",  vacinas);
        rel.put("produtosVencidos",   vencidas);
        rel.put("totalEstoques",      estoques.size());
        rel.put("valorTotalEstoque",  String.format("R$ %.2f", valor));
        return rel;
    }

    /** Relatório de produtos por espécie */
    public Map<String, Object> relatorioPorEspecie(String especie) {
        List<Produto> lista = produtoServico.buscarPorEspecie(especie);
        double valor = lista.stream().mapToDouble(Produto::getPreco).sum();

        Map<String, Object> rel = new LinkedHashMap<>();
        rel.put("especie",   especie);
        rel.put("total",     lista.size());
        rel.put("valorTotal",String.format("R$ %.2f", valor));
        rel.put("produtos",  lista);
        return rel;
    }

    /** Resumo de saúde do estoque */
    public Map<String, Object> saudeEstoque() {
        List<Map<String, Object>> vencidos = estoqueServico.alertarVencidos();
        List<Estoque> estoques = estoqueServico.listarTodos();

        List<Map<String, Object>> resumoEstoques = new ArrayList<>();
        for (Estoque e : estoques) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id",            e.getId());
            item.put("local",         e.getLocal());
            item.put("totalProdutos", e.getTotalProdutos());
            resumoEstoques.add(item);
        }

        Map<String, Object> saude = new LinkedHashMap<>();
        saude.put("status",          vencidos.isEmpty() ? "SAUDAVEL" : "ATENCAO");
        saude.put("produtosVencidos", vencidos.size());
        saude.put("vencidos",         vencidos);
        saude.put("estoques",         resumoEstoques);
        return saude;
    }
}