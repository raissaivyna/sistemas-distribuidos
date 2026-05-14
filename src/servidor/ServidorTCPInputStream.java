package servidor;

import stream.VacinaPerecivelInputStream;
import pojo.VacinaPerecivel;

import java.io.*;
import java.net.*;
import java.util.List;

/**
 * ServidorTCPInputStream — versão do servidor que usa VacinaPerecivelInputStream
 * para reconstruir objetos diretamente do socket TCP.
 *
 * Terminal 1:  java -cp out servidor.ServidorTCPInputStream
 * Terminal 2:  java -cp out TesteInputStream    (opção TCP)
 *          ou: java -cp out TesteOutputStream   (envia dados)
 */
public class ServidorTCPInputStream {

    private static final int PORTA = 7896;

    public static void main(String[] args) throws IOException {

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║  ServidorTCPInputStream — porta " + PORTA + "    ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println("Aguardando conexões...\n");

        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            while (true) {
                Socket cliente = serverSocket.accept();
                System.out.println("[SERVIDOR] Cliente conectado: " +
                                   cliente.getInetAddress() + "\n");

                new Thread(() -> tratarCliente(cliente)).start();
            }
        }
    }

    private static void tratarCliente(Socket socket) {
        try (VacinaPerecivelInputStream vis =
                 new VacinaPerecivelInputStream(socket.getInputStream())) {

            List<VacinaPerecivel> vacinas = vis.lerVacinas();

            System.out.println("\n=== Objetos reconstruídos via TCP ===");
            for (int i = 0; i < vacinas.size(); i++) {
                VacinaPerecivel v = vacinas.get(i);
                System.out.println("\nVacina [" + (i + 1) + "]");
                System.out.println("  Nome:          " + v.getNome());
                System.out.println("  Preço:         R$ " + v.getPreco());
                System.out.println("  Validade:      " + v.getDataValidade());
                System.out.println("  Armazenamento: " + v.getRequisitoArmazenamento());
                System.out.println("  Temp. mín:     " + v.getTemperaturaMinima() + "°C");
                System.out.println("  Temp. máx:     " + v.getTemperaturaMaxima() + "°C");
                System.out.println("  Vencida:       " + (v.isVencido() ? "SIM ⚠️" : "NÃO ✅"));
            }
            System.out.println("\n=====================================\n");

        } catch (IOException e) {
            System.out.println("[ERRO] " + e.getMessage());
        }
    }
}