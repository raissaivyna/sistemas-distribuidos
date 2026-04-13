package multicast;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * AlertaClinica — sistema de notificacoes multicast UDP da Clinica Veterinaria
 *
 * O administrador envia alertas que chegam simultaneamente a TODOS os
 * clientes conectados na rede, sem precisar de conexao individual.
 *
 * Tipos de alerta:
 *   VENCIMENTO  — produto proxima da validade
 *   RECALL      — lote recolhido pelo fabricante
 *   PROMOCAO    — oferta especial de fornecedor
 *   REPOSICAO   — produto reabastecido no estoque
 *   URGENCIA    — alerta critico do administrador
 *
 * Formato da mensagem no pacote UDP (texto UTF-8):
 *   TIPO|TIMESTAMP|MENSAGEM
 *   ex: RECALL|2026-04-13 10:30:00|Lote BT-2024 da Zoetis recolhido urgente
 */
public class AlertaClinica {

    public static final String GRUPO_MULTICAST = "230.1.1.1";
    public static final int    PORTA_UDP       = 7899;

    // ── Tipos de alerta ──────────────────────────────────────────────────────

    public enum TipoAlerta {
        VENCIMENTO ("⚠️  VENCIMENTO"),
        RECALL     ("🚨 RECALL"),
        PROMOCAO   ("🏷️  PROMOCAO"),
        REPOSICAO  ("📦 REPOSICAO"),
        URGENCIA   ("🔴 URGENCIA"),
        SISTEMA    ("🖥️  SISTEMA");

        public final String rotulo;
        TipoAlerta(String r) { this.rotulo = r; }
    }

    // ── Envio de alerta (usado pelo servidor/admin) ──────────────────────────

    /**
     * Envia um alerta via UDP multicast para todos os clientes na rede.
     *
     * @param tipo     categoria do alerta
     * @param mensagem texto livre descrevendo o alerta
     */
    public static void enviarAlerta(TipoAlerta tipo, String mensagem) {
        try (MulticastSocket socket = new MulticastSocket()) {
            socket.setTimeToLive(4); // alcance da rede: 4 hops

            String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            String pacote = tipo.name() + "|" + timestamp + "|" + mensagem;
            byte[] dados  = pacote.getBytes("UTF-8");

            InetAddress    grupo = InetAddress.getByName(GRUPO_MULTICAST);
            DatagramPacket pkt   = new DatagramPacket(dados, dados.length,
                                                       grupo, PORTA_UDP);
            socket.send(pkt);

            System.out.println("[MULTICAST ENVIADO] " + tipo.rotulo +
                               " — " + mensagem);

        } catch (IOException e) {
            System.err.println("[MULTICAST ERRO] " + e.getMessage());
        }
    }

    // ── Recepcao de alertas (usado pelos clientes) ───────────────────────────

    /**
     * Estrutura de um alerta recebido.
     */
    public static class Alerta {
        public final TipoAlerta tipo;
        public final String     timestamp;
        public final String     mensagem;
        public final String     origem;

        public Alerta(TipoAlerta tipo, String timestamp,
                      String mensagem, String origem) {
            this.tipo      = tipo;
            this.timestamp = timestamp;
            this.mensagem  = mensagem;
            this.origem    = origem;
        }

        @Override
        public String toString() {
            return "\n  ╔═ " + tipo.rotulo + " ════════════════════════\n" +
                   "  ║ " + mensagem + "\n" +
                   "  ║ Hora   : " + timestamp + "\n" +
                   "  ║ Origem : " + origem + "\n" +
                   "  ╚═════════════════════════════════════";
        }
    }

    /**
     * Desempacota um pacote UDP recebido em um objeto Alerta.
     */
    public static Alerta desempacotar(DatagramPacket pkt) throws IOException {
        String texto  = new String(pkt.getData(), 0, pkt.getLength(), "UTF-8");
        String[] partes = texto.split("\\|", 3);

        TipoAlerta tipo = TipoAlerta.valueOf(partes[0]);
        String timestamp = partes.length > 1 ? partes[1] : "?";
        String mensagem  = partes.length > 2 ? partes[2] : texto;
        String origem    = pkt.getAddress().getHostAddress();

        return new Alerta(tipo, timestamp, mensagem, origem);
    }
}