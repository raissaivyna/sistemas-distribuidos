import io.javalin.Javalin;
import recurso.*;
import servico.*;

/**
 * ServidorApp — ponto de entrada da API REST da Clínica Veterinária.
 *
 * Rotas disponíveis:
 *
 * PRODUTOS:
 *   GET    /api/produtos
 *   GET    /api/produtos/{id}
 *   GET    /api/produtos/especie/{especie}
 *   GET    /api/produtos/vencidos
 *   GET    /api/produtos/valor-total
 *   POST   /api/produtos
 *   DELETE /api/produtos/{id}
 *
 * ESTOQUES:
 *   GET    /api/estoques
 *   GET    /api/estoques/{id}
 *   GET    /api/estoques/vencidos
 *   POST   /api/estoques
 *   POST   /api/estoques/{id}/entrada/{prodId}
 *   DELETE /api/estoques/{id}/saida/{prodId}
 *
 * RELATORIOS:
 *   GET    /api/relatorios/geral
 *   GET    /api/relatorios/saude-estoque
 *   GET    /api/relatorios/especie/{especie}
 *
 * Iniciar: java -jar target/servidor-api-1.0.0-jar-with-dependencies.jar
 * Porta  : 8080
 */
public class ServidorApp {

    public static void main(String[] args) {

        // Cria os 3 objetos distribuídos (serviços)
        ProdutoServico  produtoServico  = new ProdutoServico();
        EstoqueServico  estoqueServico  = new EstoqueServico(produtoServico);
        RelatorioServico relatorioServico = new RelatorioServico(produtoServico, estoqueServico);

        // Cria a aplicação Javalin
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors ->
                cors.addRule(it -> it.anyHost()) // permite qualquer cliente
            );
        });

        // Registra os recursos REST
        new ProdutoRecurso(produtoServico).registrar(app);
        new EstoqueRecurso(estoqueServico).registrar(app);
        new RelatorioRecurso(relatorioServico).registrar(app);

        // Tratamento global de erros
        app.exception(NumberFormatException.class, (e, ctx) ->
            ctx.status(400).json(java.util.Map.of("erro", "ID inválido: " + e.getMessage()))
        );
        app.exception(Exception.class, (e, ctx) ->
            ctx.status(500).json(java.util.Map.of("erro", e.getMessage()))
        );

        app.start(8080);

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║   API REST — Clínica Veterinária             ║");
        System.out.println("║   http://localhost:8080/api/produtos          ║");
        System.out.println("╚══════════════════════════════════════════════╝");
    }
}