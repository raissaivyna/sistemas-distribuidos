package servidor;

import entidade.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ClinicaImpl implements ClinicaRemota {

    private final List<Produto>         repositorio     = new CopyOnWriteArrayList<>();
    private final List<PedidoReposicao> pedidos         = new CopyOnWriteArrayList<>();
    private final AtomicInteger         proximoId       = new AtomicInteger(1);
    private final AtomicInteger         proximoPedidoId = new AtomicInteger(1);

    public ClinicaImpl() { popularDadosIniciais(); }

    @Override
    public List<Produto> listarProdutos() {
        return new ArrayList<>(repositorio);
    }

    @Override
    public List<Produto> buscarPorEspecie(String especie) {
        List<Produto> resultado = new ArrayList<>();
        for (Produto p : repositorio) {
            if (p instanceof ProdutoVeterinario) {
                ProdutoVeterinario pv = (ProdutoVeterinario) p;
                if (pv.getEspecieAlvo().equalsIgnoreCase(especie))
                    resultado.add(p);
            }
        }
        return resultado;
    }

    @Override
    public int cadastrarProduto(Produto produto) {
        produto.setId(proximoId.getAndIncrement());
        repositorio.add(produto);
        return produto.getId();
    }

    public boolean remover(int id) {
        return repositorio.removeIf(p -> p.getId() == id);
    }

    @Override
    public List<VacinaPerecivel> listarVencidos() {
        List<VacinaPerecivel> vencidas = new ArrayList<>();
        for (Produto p : repositorio) {
            if (p instanceof VacinaPerecivel) {
                VacinaPerecivel vp = (VacinaPerecivel) p;
                if (vp.isVencido()) vencidas.add(vp);
            }
        }
        return vencidas;
    }

    @Override
    public String gerarRelatorio() {
        long vacinas = 0, vencidas = 0;
        double valor = 0;
        for (Produto p : repositorio) {
            valor += p.getPreco();
            if (p instanceof VacinaPerecivel) {
                vacinas++;
                if (((VacinaPerecivel) p).isVencido()) vencidas++;
            }
        }
        long quimio = repositorio.size() - vacinas;
        return "=== Relatorio do Estoque ===\n" +
               "Total de produtos  : " + repositorio.size() + "\n" +
               "Vacinas pereciveis : " + vacinas + "\n" +
               "Outros produtos    : " + quimio  + "\n" +
               "Produtos vencidos  : " + vencidas + "\n" +
               "Pedidos registrados: " + pedidos.size() + "\n" +
               String.format("Valor total (R$)   : %.2f", valor);
    }

    @Override
    public String registrarPedido(PedidoReposicao pedido) {
        PedidoReposicao novo = new PedidoReposicao(
            proximoPedidoId.getAndIncrement(),
            pedido.getResponsavel(), pedido.getDataHora());
        for (Produto item : pedido.getItens()) novo.adicionarItem(item);
        pedidos.add(novo);
        return "Pedido #" + novo.getId() + " registrado por '" +
               novo.getResponsavel() + "' - " + novo.getItens().size() +
               " item(ns) - R$" + String.format("%.2f", novo.getValorTotal());
    }

    private void popularDadosIniciais() {
        cadastrarProduto(new VacinaPerecivel(0,"Vacina Anti-Rabica",32.50,"Zoetis",
            "BR-001","Canino","Subcutanea","31/12/2025","Refrigerado 2-8C",2.0,8.0));
        cadastrarProduto(new VacinaPerecivel(0,"Vacina Polivalente V10",28.00,"Merial",
            "BR-002","Canino","Subcutanea","15/08/2026","Refrigerado 2-8C",2.0,8.0));
        cadastrarProduto(new VacinaPerecivel(0,"Vacina Febre Aftosa",12.00,"Boehringer",
            "BR-003","Bovino","Intramuscular","01/03/2027","Refrigerado 4-8C",4.0,8.0));
        cadastrarProduto(new ProdutoVeterinario(0,"Amoxicilina 500mg",45.90,"MSD",
            "BR-004","Canino","Oral"));
    }
}