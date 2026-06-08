package publicador;

import broker.Broker;
import evento.Evento;
import evento.Evento.Topico;

/**
 * PublicadorClinica — publica eventos da clínica no Broker.
 *
 * Não sabe quem está assinando (desacoplamento espacial).
 * Pode publicar mesmo sem nenhum assinante ativo.
 */
public class PublicadorClinica {

    private final Broker broker;
    private final String nome;

    public PublicadorClinica(String nome) {
        this.broker = Broker.getInstance();
        this.nome   = nome;
    }

    public void publicarVencimento(String produto, String validade) {
        broker.publicar(new Evento(Topico.VENCIMENTO,
            "Produto VENCIDO: " + produto + " | Validade: " + validade, nome));
    }

    public void publicarRecall(String produto, String lote) {
        broker.publicar(new Evento(Topico.RECALL,
            "RECALL: " + produto + " | Lote: " + lote +
            " — Recolhimento obrigatório!", nome));
    }

    public void publicarReposicao(String produto, int quantidadeMinima) {
        broker.publicar(new Evento(Topico.REPOSICAO,
            "REPOSIÇÃO necessária: " + produto +
            " | Qtd mínima: " + quantidadeMinima, nome));
    }

    public void publicarCadastro(String produto, int id) {
        broker.publicar(new Evento(Topico.CADASTRO,
            "Novo produto cadastrado: " + produto + " (id=" + id + ")", nome));
    }

    public void publicarRemocao(String produto, int id) {
        broker.publicar(new Evento(Topico.REMOCAO,
            "Produto removido: " + produto + " (id=" + id + ")", nome));
    }
}