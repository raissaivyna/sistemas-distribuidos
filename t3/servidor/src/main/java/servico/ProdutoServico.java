package servico;

import entidade.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * ProdutoServico — Objeto Distribuído 1.
 * Gerencia o catálogo de produtos veterinários.
 */
public class ProdutoServico {

    private final List<Produto>  repositorio = new CopyOnWriteArrayList<>();
    private final AtomicInteger  proximoId   = new AtomicInteger(1);

    public ProdutoServico() { popularDados(); }

    public List<Produto> listarTodos() {
        return new ArrayList<>(repositorio);
    }

    public Optional<Produto> buscarPorId(int id) {
        return repositorio.stream().filter(p -> p.getId() == id).findFirst();
    }

    public List<Produto> buscarPorEspecie(String especie) {
        return repositorio.stream()
            .filter(p -> p instanceof ProdutoVeterinario)
            .map(p -> (ProdutoVeterinario) p)
            .filter(pv -> pv.getEspecieAlvo().equalsIgnoreCase(especie))
            .collect(Collectors.toList());
    }

    public Produto cadastrar(Produto p) {
        p.setId(proximoId.getAndIncrement());
        repositorio.add(p);
        return p;
    }

    public boolean remover(int id) {
        return repositorio.removeIf(p -> p.getId() == id);
    }

    public double calcularValorTotal() {
        return repositorio.stream().mapToDouble(Produto::getPreco).sum();
    }

    public List<VacinaPerecivel> listarVencidas() {
        return repositorio.stream()
            .filter(p -> p instanceof VacinaPerecivel)
            .map(p -> (VacinaPerecivel) p)
            .filter(VacinaPerecivel::isVencida)
            .collect(Collectors.toList());
    }

    private void popularDados() {
        cadastrar(new VacinaPerecivel(0,"Vacina Anti-Rabica",32.50,"Zoetis",
            "BR-001","Canino","Subcutanea","31/12/2025","Refrigerado 2-8C",2.0,8.0));
        cadastrar(new VacinaPerecivel(0,"Vacina Polivalente V10",28.00,"Merial",
            "BR-002","Canino","Subcutanea","15/08/2026","Refrigerado 2-8C",2.0,8.0));
        cadastrar(new VacinaPerecivel(0,"Vacina Febre Aftosa",12.00,"Boehringer",
            "BR-003","Bovino","Intramuscular","01/03/2027","Refrigerado 4-8C",4.0,8.0));
        cadastrar(new ProdutoVeterinario(0,"Amoxicilina 500mg",45.90,"MSD",
            "BR-004","Canino","Oral"));
    }
}