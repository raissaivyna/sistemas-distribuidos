package broker;

import evento.Evento;
import evento.Evento.Topico;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Broker — intermediário central do sistema Publish-Subscribe.
 *
 * Responsabilidades:
 *   - Receber eventos de publicadores
 *   - Rotear eventos para assinantes do tópico correto
 *   - Persistir eventos na fila enquanto assinante estiver offline
 *     (desacoplamento temporal)
 *   - Nunca revelar a identidade do publicador ao assinante
 *     (desacoplamento espacial)
 *
 * Implementação:
 *   - Uma fila por (tópico + assinante) para retenção offline
 *   - Entrega assíncrona via ExecutorService
 */
public class Broker {

    // Assinante: nome → callback de entrega
    private final Map<Topico, Map<String, Consumer<Evento>>> assinantes
        = new ConcurrentHashMap<>();

    // Fila de retenção: (tópico + nomeAssinante) → mensagens pendentes
    private final Map<String, Queue<Evento>> filaRetencao
        = new ConcurrentHashMap<>();

    // Assinantes offline temporariamente
    private final Set<String> offline = ConcurrentHashMap.newKeySet();

    private final ExecutorService executor
        = Executors.newCachedThreadPool();

    private static Broker instancia;

    private Broker() {
        for (Topico t : Topico.values())
            assinantes.put(t, new ConcurrentHashMap<>());
    }

    public static synchronized Broker getInstance() {
        if (instancia == null) instancia = new Broker();
        return instancia;
    }

    // ── Assinar ──────────────────────────────────────────────────────────────

    /**
     * Registra um assinante para um tópico.
     * Ao assinar, entrega imediatamente eventos retidos enquanto estava offline.
     */
    public void assinar(Topico topico, String nomeAssinante,
                        Consumer<Evento> callback) {
        assinantes.get(topico).put(nomeAssinante, callback);
        offline.remove(nomeAssinante);

        System.out.println("[BROKER] " + nomeAssinante +
                           " assinou tópico: " + topico);

        // Entrega eventos retidos (desacoplamento temporal)
        String chave = chave(topico, nomeAssinante);
        Queue<Evento> pendentes = filaRetencao.getOrDefault(chave, new LinkedList<>());
        if (!pendentes.isEmpty()) {
            System.out.println("[BROKER] Entregando " + pendentes.size() +
                               " evento(s) retido(s) para " + nomeAssinante);
            while (!pendentes.isEmpty()) {
                Evento e = pendentes.poll();
                executor.submit(() -> callback.accept(e));
            }
            filaRetencao.remove(chave);
        }
    }

    /**
     * Remove assinatura de um tópico.
     */
    public void cancelarAssinatura(Topico topico, String nomeAssinante) {
        assinantes.get(topico).remove(nomeAssinante);
        System.out.println("[BROKER] " + nomeAssinante +
                           " cancelou assinatura: " + topico);
    }

    /**
     * Marca assinante como offline — eventos serão retidos.
     */
    public void marcarOffline(String nomeAssinante) {
        offline.add(nomeAssinante);
        System.out.println("[BROKER] " + nomeAssinante + " marcado como OFFLINE.");
    }

    /**
     * Marca assinante como online — entrega eventos retidos.
     */
    public void marcarOnline(String nomeAssinante) {
        offline.remove(nomeAssinante);
        System.out.println("[BROKER] " + nomeAssinante + " marcado como ONLINE.");

        // Entrega todos os eventos retidos de todos os tópicos
        for (Topico topico : Topico.values()) {
            Consumer<Evento> callback = assinantes.get(topico).get(nomeAssinante);
            if (callback == null) continue;

            String chave = chave(topico, nomeAssinante);
            Queue<Evento> pendentes = filaRetencao.getOrDefault(chave, new LinkedList<>());
            if (!pendentes.isEmpty()) {
                System.out.println("[BROKER] Entregando " + pendentes.size() +
                                   " evento(s) retido(s) [" + topico +
                                   "] para " + nomeAssinante);
                while (!pendentes.isEmpty()) {
                    Evento e = pendentes.poll();
                    executor.submit(() -> callback.accept(e));
                }
                filaRetencao.remove(chave);
            }
        }
    }

    // ── Publicar ─────────────────────────────────────────────────────────────

    /**
     * Publica um evento no tópico.
     * Publicador NÃO sabe quem vai receber (desacoplamento espacial).
     * Se assinante estiver offline, evento é retido (desacoplamento temporal).
     */
    public void publicar(Evento evento) {
        System.out.println("[BROKER] Publicando: " + evento);

        Map<String, Consumer<Evento>> interessados = assinantes.get(evento.getTopico());
        if (interessados == null || interessados.isEmpty()) {
            System.out.println("[BROKER] Nenhum assinante para " + evento.getTopico());
            return;
        }

        for (Map.Entry<String, Consumer<Evento>> entry : interessados.entrySet()) {
            String nomeAssinante = entry.getKey();
            Consumer<Evento> callback = entry.getValue();

            if (offline.contains(nomeAssinante)) {
                // Retém na fila — desacoplamento temporal
                String chave = chave(evento.getTopico(), nomeAssinante);
                filaRetencao.computeIfAbsent(chave, k -> new LinkedList<>()).add(evento);
                System.out.println("[BROKER] " + nomeAssinante +
                                   " offline — evento retido na fila.");
            } else {
                // Entrega assíncrona
                executor.submit(() -> callback.accept(evento));
            }
        }
    }

    public int contarRetidos(Topico topico, String assinante) {
        return filaRetencao
            .getOrDefault(chave(topico, assinante), new LinkedList<>()).size();
    }

    private String chave(Topico topico, String assinante) {
        return topico + ":" + assinante;
    }
}