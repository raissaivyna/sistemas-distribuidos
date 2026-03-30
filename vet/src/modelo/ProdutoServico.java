package modelo;

import pojo.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SERVIÇO 1 — ProdutoServico
 * Classe de modelo que implementa operações de negócio sobre produtos.
 * Simula o que seria uma camada de serviço (Service Layer).
 */
public class ProdutoServico {

    // "banco de dados" em memória para testes
    private List<Produto> repositorio = new ArrayList<>();
    private int proximoId = 1;

    // ── CRUD básico ──────────────────────────────────────────────────────────

    public Produto cadastrar(Produto p) {
        p.setId(proximoId++);
        repositorio.add(p);
        System.out.println("[SERVIÇO] Produto cadastrado: " + p);
        return p;
    }

    public Produto buscarPorId(int id) {
        return repositorio.stream()
                          .filter(p -> p.getId() == id)
                          .findFirst()
                          .orElse(null);
    }

    public List<Produto> listarTodos() {
        return new ArrayList<>(repositorio);
    }

    public boolean remover(int id) {
        boolean ok = repositorio.removeIf(p -> p.getId() == id);
        if (ok) System.out.println("[SERVIÇO] Produto id=" + id + " removido.");
        return ok;
    }

    // ── Regras de negócio ────────────────────────────────────────────────────

    /**
     * Lista apenas vacinas perecíveis que estão vencidas.
     */
    public List<VacinaPerecivel> listarVencidasPerecíveis() {
        return repositorio.stream()
                .filter(p -> p instanceof VacinaPerecivel)
                .map(p -> (VacinaPerecivel) p)
                .filter(VacinaPerecivel::isVencido)
                .collect(Collectors.toList());
    }

    /**
     * Lista todos os produtos por espécie alvo.
     * Só funciona para ProdutoVeterinario e subclasses.
     */
    public List<ProdutoVeterinario> buscarPorEspecie(String especie) {
        return repositorio.stream()
                .filter(p -> p instanceof ProdutoVeterinario)
                .map(p -> (ProdutoVeterinario) p)
                .filter(pv -> pv.getEspecieAlvo().equalsIgnoreCase(especie))
                .collect(Collectors.toList());
    }

    /**
     * Calcula o valor total do estoque (soma dos preços).
     */
    public double calcularValorTotal() {
        return repositorio.stream()
                          .mapToDouble(Produto::getPreco)
                          .sum();
    }

    /**
     * Serializa um produto para String no formato chave=valor.
     * Usado antes de enviar via Stream/TCP.
     */
    public String serializar(Produto p) {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(p.getId()).append(";");
        sb.append("nome=").append(p.getNome()).append(";");
        sb.append("preco=").append(p.getPreco()).append(";");
        sb.append("fabricante=").append(p.getFabricante()).append(";");
        sb.append("tipo=").append(p.getClass().getSimpleName());
        return sb.toString();
    }

    public int getTotalProdutos() { return repositorio.size(); }
}