package multicast;

import java.io.*;
import java.net.*;

/**
 * ReceptorAlertas — escuta alertas UDP multicast da clinica em background
 *
 * Roda em uma thread separada dentro de qualquer cliente.
 * Quando um alerta chega, imprime na tela imediatamente.
 *
 * Uso standalone:
 *   java -cp out multicast.ReceptorAlertas
 *
 * Uso embutido (dentro de outro programa):
 *   ReceptorAlertas receptor = new ReceptorAlertas();
 *   receptor.iniciar();  // inicia thread daemon
 *   // ... resto do programa roda normalmente
 *   receptor.parar();    // encerra quando quiser
 */
public class ReceptorAlertas implements Runnable {

    private volatile boolean rodando = true;
    private MulticastSocket  socket;

    // ── Iniciar como thread daemon ───────────────────────────────────────────

    public void iniciar() {
        Thread t = new Thread(this, "receptor-alertas-multicast");
        t.setDaemon(true); // encerra junto com a JVM
        t.start();
        System.out.println("[RECEPTOR] Escutando alertas multicast em " +
                           AlertaClinica.GRUPO_MULTICAST + ":" +
                           AlertaClinica.PORTA_UDP + "\n");
    }

    public void parar() {
        rodando = false;
        if (socket != null && !socket.isClosed())
            socket.close();
    }

    // ── Loop de recepcao ─────────────────────────────────────────────────────

    @Override
    public void run() {
        try {
            InetAddress grupo = InetAddress.getByName(AlertaClinica.GRUPO_MULTICAST);
            socket = new MulticastSocket(AlertaClinica.PORTA_UDP);
            socket.joinGroup(grupo);

            byte[] buffer = new byte[1024];

            while (rodando) {
                DatagramPacket pkt = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(pkt); // bloqueia ate chegar pacote
                    AlertaClinica.Alerta alerta = AlertaClinica.desempacotar(pkt);
                    System.out.println(alerta);
                } catch (SocketException e) {
                    if (rodando) // so imprime erro se nao foi parada intencional
                        System.err.println("[RECEPTOR] Socket encerrado: " + e.getMessage());
                    break;
                }
            }

            socket.leaveGroup(grupo);

        } catch (IOException e) {
            System.err.println("[RECEPTOR] Erro: " + e.getMessage());
        }
    }

    // ── Modo standalone ──────────────────────────────────────────────────────

    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║  Receptor de Alertas — Clinica Veterinaria   ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        ReceptorAlertas receptor = new ReceptorAlertas();
        receptor.iniciar();

        System.out.println("Aguardando alertas... (Ctrl+C para sair)\n");

        // Mantém a thread principal viva
        Thread.currentThread().join();
    }
}