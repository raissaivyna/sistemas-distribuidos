package assinante;

import broker.Broker;
import evento.Evento;
import evento.Evento.Topico;

import java.util.ArrayList;
import java.util.List;

/**
 * AssinanteVeterinario — recebe eventos do Broker.
 *
 * Não sabe de quem vêm os eventos (desacoplamento espacial).
 * Pode ficar offline e receber eventos retidos ao voltar (desacoplamento temporal).
 */
public class AssinanteVeterinario {

    private final Broker       broker;
    private final String       nome;
    private final List<Evento> eventosRecebidos = new ArrayList<>();

    public AssinanteVeterinario(String nome) {
        this.broker = Broker.getInstance();
        this.nome   = nome;
    }

    /**
     * Assina um tópico — passa o callback de tratamento.
     */
    public void assinar(Topico topico) {
        broker.assinar(topico, nome, evento -> {
            eventosRecebidos.add(evento);
            System.out.println("[" + nome + "] EVENTO RECEBIDO: " + evento);
        });
    }

    public void cancelarAssinatura(Topico topico) {
        broker.cancelarAssinatura(topico, nome);
    }

    /** Simula ficar offline — eventos serão retidos no broker */
    public void ficarOffline() {
        broker.marcarOffline(nome);
        System.out.println("[" + nome + "] ficou OFFLINE.");
    }

    /** Volta online — broker entrega eventos retidos */
    public void voltarOnline() {
        broker.marcarOnline(nome);
        System.out.println("[" + nome + "] voltou ONLINE.");
    }

    public String getNome()                     { return nome; }
    public List<Evento> getEventosRecebidos()   { return eventosRecebidos; }
    public int getTotalRecebidos()              { return eventosRecebidos.size(); }
}