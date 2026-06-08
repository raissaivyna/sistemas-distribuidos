package recurso;

import entidade.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import servico.ProdutoServico;

import java.util.List;
import java.util.Optional;

/**
 * ProdutoRecurso — endpoints REST para produtos.
 *
 * GET    /api/produtos              → listar todos
 * GET    /api/produtos/{id}         → buscar por id
 * GET    /api/produtos/especie/{e}  → buscar por espécie
 * GET    /api/produtos/vencidos     → listar vencidos
 * GET    /api/produtos/valor-total  → valor total do estoque
 * POST   /api/produtos              → cadastrar
 * DELETE /api/produtos/{id}         → remover
 */
public class ProdutoRecurso {

    private final ProdutoServico servico;

    public ProdutoRecurso(ProdutoServico servico) {
        this.servico = servico;
    }

    public void registrar(Javalin app) {

        app.get("/api/produtos", this::listarTodos);
        app.get("/api/produtos/vencidos", this::listarVencidos);
        app.get("/api/produtos/valor-total", this::valorTotal);
        app.get("/api/produtos/especie/{especie}", this::buscarPorEspecie);
        app.get("/api/produtos/{id}", this::buscarPorId);
        app.post("/api/produtos", this::cadastrar);
        app.delete("/api/produtos/{id}", this::remover);
    }

    void listarTodos(Context ctx) {
        ctx.json(servico.listarTodos());
    }

    void buscarPorId(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Optional<Produto> p = servico.buscarPorId(id);
        if (p.isPresent()) ctx.json(p.get());
        else ctx.status(404).json(erro("Produto id=" + id + " nao encontrado"));
    }

    void buscarPorEspecie(Context ctx) {
        String especie = ctx.pathParam("especie");
        List<Produto> lista = servico.buscarPorEspecie(especie);
        ctx.json(lista);
    }

    void listarVencidos(Context ctx) {
        ctx.json(servico.listarVencidas());
    }

    void valorTotal(Context ctx) {
        ctx.json(java.util.Map.of(
            "valorTotal", servico.calcularValorTotal(),
            "formatado",  String.format("R$ %.2f", servico.calcularValorTotal())
        ));
    }

    void cadastrar(Context ctx) {
        Produto p = ctx.bodyAsClass(Produto.class);
        Produto criado = servico.cadastrar(p);
        ctx.status(201).json(criado);
    }

    void remover(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        boolean ok = servico.remover(id);
        if (ok) ctx.json(java.util.Map.of("status", "removido", "id", id));
        else    ctx.status(404).json(erro("Produto id=" + id + " nao encontrado"));
    }

    private java.util.Map<String, String> erro(String msg) {
        return java.util.Map.of("erro", msg);
    }
}