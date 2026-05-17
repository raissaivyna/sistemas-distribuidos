package servidor;

import java.net.*;

/**
 * ServidorRMI — ponto de entrada do servidor.
 *
 * Inicializa ClinicaImpl, cria o DatagramSocket UDP e inicia o Skeleton.
 *
 * Iniciar: java -cp out servidor.ServidorRMI
 * Porta  : 7896 (UDP)
 */
public class ServidorRMI {

    static final int PORTA = 7896;

    public static void main(String[] args) throws Exception {

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║   Servidor RMI — Clínica Veterinária         ║");
        System.out.println("║   UDP porta " + PORTA + "                          ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        ClinicaImpl impl = new ClinicaImpl();

        try (DatagramSocket socket = new DatagramSocket(PORTA)) {
            Skeleton skeleton = new Skeleton(impl, socket);
            skeleton.servirSempre(); // loop infinito
        }
    }
}