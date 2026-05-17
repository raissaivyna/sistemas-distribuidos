package servidor;

import entidade.*;
import java.util.List;

/**
 * ClinicaRemota — interface do serviço remoto da clínica.
 *
 * Define os métodos disponíveis para invocação remota (mínimo 4).
 * Implementada pelo ClinicaImpl no servidor.
 * Conhecida pelo cliente através do Stub.
 *
 * Métodos remotos:
 *   1. listarProdutos()           — passagem por referência (retorna lista do servidor)
 *   2. buscarPorEspecie(especie)  — passagem por valor (String especie enviada serializada)
 *   3. cadastrarProduto(produto)  — passagem por valor (produto enviado como JSON)
 *   4. listarVencidos()           — passagem por referência (retorna lista do servidor)
 *   5. gerarRelatorio()           — passagem por referência (resultado calculado no servidor)
 *   6. registrarPedido(pedido)    — passagem por valor (pedido enviado como JSON)
 */
public interface ClinicaRemota {

    /** (1) Lista todos os produtos do estoque. */
    List<Produto> listarProdutos();

    /** (2) Busca produtos por espécie alvo (ex: "Canino", "Bovino"). */
    List<Produto> buscarPorEspecie(String especie);

    /** (3) Cadastra um novo produto no estoque. Retorna o id atribuído. */
    int cadastrarProduto(Produto produto);

    /** (4) Lista vacinas perecíveis vencidas. */
    List<VacinaPerecivel> listarVencidos();

    /** (5) Gera relatório resumido do estoque. */
    String gerarRelatorio();

    /** (6) Registra um pedido de reposição. */
    String registrarPedido(PedidoReposicao pedido);
}