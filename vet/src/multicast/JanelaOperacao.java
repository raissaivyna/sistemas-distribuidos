package multicast;

import pojo.*;
import protocolo.SerializadorJSON;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * JanelaOperacao — janela de tempo para operacoes da clinica
 *
 * Adaptacao criativa do requisito de "tempo limite":
 * Em vez de votacao, a clinica abre uma JANELA DE PEDIDOS DE REPOSICAO.
 *
 * Durante a janela (ex: 2 minutos):
 *   - Clinicos podem registrar pedidos de reposicao de produtos
 *   - Admin pode enviar alertas via multicast a qualquer momento
 *   - Servidor aceita pedidos normalmente
 *
 * Ao encerrar:
 *   - Servidor fecha automaticamente os pedidos
 *   - Gera relatorio consolidado
 *   - Envia resultado via multicast UDP para todos
 *   - Nenhum novo pedido e aceito apos o prazo
 *
 * Uso:
 *   JanelaOperacao janela = new JanelaOperacao("Reposicao Semanal", 120);
 *   janela.abrir();
 *   janela.registrarPedido("dr.joao", vacina);
 *   // ... apos 120s, encerra automaticamente e envia relatorio
 */
public class JanelaOperacao {

    // ── Estado da janela ─────────────────────────────────────────────────────

    private final String       nomeOperacao;
    private final int          duracaoSegundos;
    private final AtomicBoolean aberta = new AtomicBoolean(false);

    private LocalDateTime  horaAbertura;
    private LocalDateTime  horaEncerramento;

    // Pedidos registrados: responsavel → lista de produtos
    private final Map<String, List<Produto>> pedidos = new ConcurrentHashMap<>();

    private ScheduledExecutorService agendador;
    private Runnable callbackEncerramento; // hook opcional

    // ── Construtor ───────────────────────────────────────────────────────────

    public JanelaOperacao(String nomeOperacao, int duracaoSegundos) {
        this.nomeOperacao    = nomeOperacao;
        this.duracaoSegundos = duracaoSegundos;
    }

    /** Define acao adicional a executar quando a janela fechar. */
    public void aoEncerrar(Runnable callback) {
        this.callbackEncerramento = callback;
    }

    // ── Ciclo de vida ────────────────────────────────────────────────────────

    /**
     * Abre a janela de operacao e agenda o encerramento automatico.
     */
    public void abrir() {
        if (aberta.get()) {
            System.out.println("[JANELA] Ja esta aberta.");
            return;
        }

        aberta.set(true);
        horaAbertura = LocalDateTime.now();
        horaEncerramento = horaAbertura.plusSeconds(duracaoSegundos);

        String fmt = DateTimeFormatter.ofPattern("HH:mm:ss").format(horaEncerramento);

        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║  JANELA ABERTA: " + nomeOperacao);
        System.out.printf ("║  Encerra automaticamente às %s\n", fmt);
        System.out.println("╚══════════════════════════════════════════════╝\n");

        // Notifica todos os clientes via multicast
        AlertaClinica.enviarAlerta(
            AlertaClinica.TipoAlerta.SISTEMA,
            "JANELA ABERTA: " + nomeOperacao +
            " | Prazo: " + duracaoSegundos + "s | Encerra: " + fmt
        );

        // Agenda encerramento automatico
        agendador = Executors.newSingleThreadScheduledExecutor();
        agendador.schedule(this::encerrar, duracaoSegundos, TimeUnit.SECONDS);

        // Alertas intermediarios: avisa quando faltar metade do tempo
        int metade = duracaoSegundos / 2;
        agendador.schedule(() ->
            AlertaClinica.enviarAlerta(
                AlertaClinica.TipoAlerta.SISTEMA,
                "AVISO: Janela '" + nomeOperacao + "' encerra em " +
                metade + " segundos!"
            ),
            metade, TimeUnit.SECONDS
        );
    }

    /**
     * Registra um pedido de reposicao de produto durante a janela.
     *
     * @param responsavel  login/nome de quem faz o pedido
     * @param produto      produto a repor
     * @return true se aceito, false se janela ja encerrou
     */
    public boolean registrarPedido(String responsavel, Produto produto) {
        if (!aberta.get()) {
            System.out.println("[JANELA] ⛔ Pedido recusado — janela '" +
                               nomeOperacao + "' encerrada.");
            return false;
        }

        pedidos.computeIfAbsent(responsavel, k -> new CopyOnWriteArrayList<>())
               .add(produto);

        System.out.println("[JANELA] ✅ Pedido registrado por " + responsavel +
                           " → " + produto.getNome());
        return true;
    }

    /**
     * Encerra a janela, gera relatorio e envia via multicast.
     * Chamado automaticamente pelo agendador, mas pode ser chamado manualmente.
     */
    public synchronized void encerrar() {
        if (!aberta.get()) return;
        aberta.set(false);

        String relatorio = gerarRelatorio();

        System.out.println(relatorio);

        // Envia relatorio via multicast para todos os clientes
        AlertaClinica.enviarAlerta(
            AlertaClinica.TipoAlerta.SISTEMA,
            "JANELA ENCERRADA: " + nomeOperacao + " | " + resumoParaMulticast()
        );

        if (callbackEncerramento != null)
            callbackEncerramento.run();

        if (agendador != null)
            agendador.shutdown();
    }

    // ── Relatorio ────────────────────────────────────────────────────────────

    private String gerarRelatorio() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder();

        sb.append("\n╔══════════════════════════════════════════════════════╗\n");
        sb.append("║  RELATORIO DE ENCERRAMENTO — ").append(nomeOperacao).append("\n");
        sb.append("╠══════════════════════════════════════════════════════╣\n");
        sb.append("║  Abertura     : ").append(fmt.format(horaAbertura)).append("\n");
        sb.append("║  Encerramento : ").append(fmt.format(LocalDateTime.now())).append("\n");
        sb.append("║  Duracao      : ").append(duracaoSegundos).append(" segundos\n");
        sb.append("╠══════════════════════════════════════════════════════╣\n");

        int totalPedidos = pedidos.values().stream().mapToInt(List::size).sum();
        sb.append("║  Total de pedidos: ").append(totalPedidos).append("\n");

        if (pedidos.isEmpty()) {
            sb.append("║  Nenhum pedido registrado.\n");
        } else {
            sb.append("╠══════════════════════════════════════════════════════╣\n");
            pedidos.forEach((resp, lista) -> {
                sb.append("║  Responsavel: ").append(resp)
                  .append(" (").append(lista.size()).append(" item(ns))\n");
                lista.forEach(p ->
                    sb.append("║    → [").append(p.getClass().getSimpleName()).append("] ")
                      .append(p.getNome()).append(" | R$ ").append(p.getPreco()).append("\n")
                );
            });

            double valorTotal = pedidos.values().stream()
                .flatMap(List::stream)
                .mapToDouble(Produto::getPreco)
                .sum();
            sb.append("╠══════════════════════════════════════════════════════╣\n");
            sb.append(String.format("║  Valor total dos pedidos: R$ %.2f\n", valorTotal));
        }

        sb.append("╚══════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }

    private String resumoParaMulticast() {
        int total = pedidos.values().stream().mapToInt(List::size).sum();
        double valor = pedidos.values().stream()
            .flatMap(List::stream).mapToDouble(Produto::getPreco).sum();
        return String.format("%d pedido(s) | R$ %.2f | %d responsavel(eis)",
                             total, valor, pedidos.size());
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public boolean isAberta()              { return aberta.get(); }
    public String  getNomeOperacao()       { return nomeOperacao; }
    public int     getDuracaoSegundos()    { return duracaoSegundos; }
    public Map<String, List<Produto>> getPedidos() { return Collections.unmodifiableMap(pedidos); }
}