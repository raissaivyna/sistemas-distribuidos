package servidor;

import java.io.*;
import java.net.*;

/**
 * ServidorTCP — recebe bytes enviados pelo VacinaPerecivelOutputStream via TCP.
 *
 * Execução: java -cp out servidor.ServidorTCP
 * (inicie ANTES do teste TCP no TesteOutputStream)
 */
public class ServidorTCP {

    private static final int PORTA = 7896;

    public static void main(String[] args) throws IOException {

        System.out.println("=== Servidor TCP aguardando na porta " + PORTA + " ===");

        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {

            // Servidor simples: atende um cliente por vez
            while (true) {
                Socket cliente = serverSocket.accept();
                System.out.println("\n[SERVIDOR] Cliente conectado: " +
                                   cliente.getInetAddress());

                // Trata cada conexão em thread separada
                new Thread(() -> tratarCliente(cliente)).start();
            }
        }
    }

    private static void tratarCliente(Socket socket) {
        try (InputStream in = socket.getInputStream()) {

            // Lê o número de vacinas
            int qtd = readInt(in);
            System.out.println("[SERVIDOR] Recebendo " + qtd + " vacina(s)...\n");

            for (int i = 0; i < qtd; i++) {
                int    id        = readInt(in);
                String nome      = readUTF(in);
                double preco     = readDouble(in);
                String validade  = readUTF(in);
                String armaz     = readUTF(in);
                double tempMin   = readDouble(in);
                double tempMax   = readDouble(in);

                System.out.println("--- Vacina " + (i+1) + " ---");
                System.out.println("  id:              " + id);
                System.out.println("  nome:            " + nome);
                System.out.println("  preco:           R$ " + preco);
                System.out.println("  validade:        " + validade);
                System.out.println("  armazenamento:   " + armaz);
                System.out.println("  temp. min:       " + tempMin + "°C");
                System.out.println("  temp. max:       " + tempMax + "°C");
            }

            System.out.println("\n[SERVIDOR] Todos os dados recebidos com sucesso.");

        } catch (IOException e) {
            System.out.println("[SERVIDOR] Erro: " + e.getMessage());
        }
    }

    // ── Helpers de leitura (espelho do OutputStream) ─────────────────────────

    private static int readInt(InputStream in) throws IOException {
        int b1 = in.read(), b2 = in.read(), b3 = in.read(), b4 = in.read();
        return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
    }

    private static double readDouble(InputStream in) throws IOException {
        long bits = 0;
        for (int i = 0; i < 8; i++) {
            bits = (bits << 8) | (in.read() & 0xFF);
        }
        return Double.longBitsToDouble(bits);
    }

    private static String readUTF(InputStream in) throws IOException {
        int len = ((in.read() & 0xFF) << 8) | (in.read() & 0xFF);
        byte[] bytes = in.readNBytes(len);
        return new String(bytes, "UTF-8");
    }
}