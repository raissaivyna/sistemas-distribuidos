package evento;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Evento — mensagem publicada no broker.
 *
 * Tópicos disponíveis:
 *   VENCIMENTO   — produto próximo ou já vencido
 *   RECALL       — lote recolhido pelo fabricante
 *   REPOSICAO    — produto precisa ser reposto
 *   CADASTRO     — novo produto cadastrado
 *   REMOCAO      — produto removido do estoque
 */
public class Evento {

    public enum Topico {
        VENCIMENTO, RECALL, REPOSICAO, CADASTRO, REMOCAO
    }

    private final Topico topico;
    private final String conteudo;
    private final String timestamp;
    private final String publicador;

    public Evento(Topico topico, String conteudo, String publicador) {
        this.topico     = topico;
        this.conteudo   = conteudo;
        this.publicador = publicador;
        this.timestamp  = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public Topico getTopico()     { return topico; }
    public String getConteudo()   { return conteudo; }
    public String getTimestamp()  { return timestamp; }
    public String getPublicador() { return publicador; }

    @Override
    public String toString() {
        return "[" + timestamp + "] [" + topico + "] " +
               "(" + publicador + ") " + conteudo;
    }
}