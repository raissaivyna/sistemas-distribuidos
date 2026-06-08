package recurso;

import io.javalin.Javalin;
import io.javalin.http.Context;
import servico.RelatorioServico;

/**
 * RelatorioRecurso — endpoints REST para relatórios.
 *
 * GET /api/relatorios/geral           → relatório geral consolidado
 * GET /api/relatorios/especie/{e}     → relatório por espécie
 * GET /api/relatorios/saude-estoque   → saúde do estoque
 */
public class RelatorioRecurso {

    private final RelatorioServico servico;

    public RelatorioRecurso(RelatorioServico servico) {
        this.servico = servico;
    }

    public void registrar(Javalin app) {
        app.get("/api/relatorios/geral",         this::geral);
        app.get("/api/relatorios/saude-estoque", this::saudeEstoque);
        app.get("/api/relatorios/especie/{e}",   this::porEspecie);
    }

    void geral(Context ctx) {
        ctx.json(servico.gerarRelatorioGeral());
    }

    void saudeEstoque(Context ctx) {
        ctx.json(servico.saudeEstoque());
    }

    void porEspecie(Context ctx) {
        ctx.json(servico.relatorioPorEspecie(ctx.pathParam("e")));
    }
}