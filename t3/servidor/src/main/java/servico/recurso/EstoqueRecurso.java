package recurso;

import entidade.Estoque;
import io.javalin.Javalin;
import io.javalin.http.Context;
import servico.EstoqueServico;

import java.util.Map;

/**
 * EstoqueRecurso — endpoints REST para estoques.
 *
 * GET    /api/estoques                         → listar todos
 * GET    /api/estoques/{id}                    → buscar por id
 * GET    /api/estoques/vencidos                → alertar vencidos
 * POST   /api/estoques                         → criar estoque
 * POST   /api/estoques/{id}/entrada/{prodId}   → entrada de produto
 * DELETE /api/estoques/{id}/saida/{prodId}     → saída de produto
 */
public class EstoqueRecurso {

    private final EstoqueServico servico;

    public EstoqueRecurso(EstoqueServico servico) {
        this.servico = servico;
    }

    public void registrar(Javalin app) {
        app.get("/api/estoques",                        this::listarTodos);
        app.get("/api/estoques/vencidos",               this::alertarVencidos);
        app.get("/api/estoques/{id}",                   this::buscarPorId);
        app.post("/api/estoques",                       this::criar);
        app.post("/api/estoques/{id}/entrada/{prodId}", this::entrada);
        app.delete("/api/estoques/{id}/saida/{prodId}", this::saida);
    }

    void listarTodos(Context ctx) {
        ctx.json(servico.listarTodos());
    }

    void buscarPorId(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        servico.buscarPorId(id)
            .ifPresentOrElse(
                ctx::json,
                () -> ctx.status(404).json(erro("Estoque id=" + id + " nao encontrado"))
            );
    }

    void alertarVencidos(Context ctx) {
        ctx.json(servico.alertarVencidos());
    }

    void criar(Context ctx) {
        Map<?, ?> body = ctx.bodyAsClass(Map.class);
        String local = (String) body.get("local");
        if (local == null || local.isBlank()) {
            ctx.status(400).json(erro("Campo 'local' e obrigatorio"));
            return;
        }
        Estoque e = servico.criar(local);
        ctx.status(201).json(e);
    }

    void entrada(Context ctx) {
        int estoqueId = Integer.parseInt(ctx.pathParam("id"));
        int produtoId = Integer.parseInt(ctx.pathParam("prodId"));
        boolean ok = servico.entradaProduto(estoqueId, produtoId);
        if (ok) ctx.json(Map.of("status", "produto adicionado ao estoque"));
        else    ctx.status(404).json(erro("Estoque ou produto nao encontrado"));
    }

    void saida(Context ctx) {
        int estoqueId = Integer.parseInt(ctx.pathParam("id"));
        int produtoId = Integer.parseInt(ctx.pathParam("prodId"));
        boolean ok = servico.saidaProduto(estoqueId, produtoId);
        if (ok) ctx.json(Map.of("status", "produto removido do estoque"));
        else    ctx.status(404).json(erro("Estoque ou produto nao encontrado"));
    }

    private Map<String, String> erro(String msg) {
        return Map.of("erro", msg);
    }
}