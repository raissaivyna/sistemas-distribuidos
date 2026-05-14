import pojo.VacinaPerecivel;
import stream.VacinaPerecivelOutputStream;

import java.io.*;
import java.net.*;

/**
 * TesteOutputStream — testa os 3 destinos do VacinaPerecivelOutputStream:
 *   i.  System.out  (saída padrão)
 *   ii. FileOutputStream  (arquivo em disco)
 *   iii. Socket TCP  (servidor remoto — inicie ServidorTCP antes)
 *
 * Execução: java -cp out TesteOutputStream
 */
public class TesteOutputStream {

    public static void main(String[] args) throws Exception {

        // ── Dados de teste ───────────────────────────────────────────────────
        VacinaPerecivel[] vacinas = {
            new VacinaPerecivel(
                1, "Vacina Anti-Rábica Canina", 32.50, "Zoetis",
                "BR-07321.0002", "Canino", "Subcutânea",
                "Vírus Inativado", "PV-11", 10,
                "31/12/2025", "Refrigerado 2-8°C", 2.0, 8.0
            ),
            new VacinaPerecivel(
                2, "Vacina Polivalente V10", 28.00, "Merial",
                "BR-07321.0005", "Canino", "Subcutânea",
                "Vírus Atenuado", "Múltiplo", 5,
                "15/08/2026", "Refrigerado 2-8°C", 2.0, 8.0
            ),
            new VacinaPerecivel(
                3, "Vacina Febre Aftosa", 12.00, "Boehringer",
                "BR-07321.0020", "Bovino", "Intramuscular",
                "Vírus Inativado", "O1 Campos", 50,
                "01/03/2027", "Refrigerado 4-8°C", 4.0, 8.0
            )
        };

        int quantidade = 3; // enviamos todos os 3

        // ════════════════════════════════════════════════════════════════════
        // TESTE i — Saída padrão (System.out)
        // ════════════════════════════════════════════════════════════════════
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║  TESTE i — System.out (bytes raw)        ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println("[obs: bytes binários aparecem como caracteres estranhos — normal!]\n");

        VacinaPerecivelOutputStream outStdout =
            new VacinaPerecivelOutputStream(vacinas, quantidade, System.out);
        outStdout.enviar();

        System.out.println("\n\n✅ TESTE i concluído.\n");

        // ════════════════════════════════════════════════════════════════════
        // TESTE ii — Arquivo (FileOutputStream)
        // ════════════════════════════════════════════════════════════════════
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║  TESTE ii — FileOutputStream             ║");
        System.out.println("╚══════════════════════════════════════════╝");

        String caminhoArquivo = "vacinas.bin";

        try (FileOutputStream fos = new FileOutputStream(caminhoArquivo)) {
            VacinaPerecivelOutputStream outArquivo =
                new VacinaPerecivelOutputStream(vacinas, quantidade, fos);
            outArquivo.enviar();
        }

        File arquivo = new File(caminhoArquivo);
        System.out.println("✅ Arquivo gerado: " + arquivo.getAbsolutePath());
        System.out.println("   Tamanho: " + arquivo.length() + " bytes\n");

        // ════════════════════════════════════════════════════════════════════
        // TESTE iii — Servidor TCP remoto
        // (inicie ServidorTCP em outro terminal antes de rodar este teste)
        // ════════════════════════════════════════════════════════════════════
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║  TESTE iii — Socket TCP (localhost:7896)  ║");
        System.out.println("╚══════════════════════════════════════════╝");

        try (Socket socket = new Socket("localhost", 7896)) {
            OutputStream socketOut = socket.getOutputStream();

            VacinaPerecivelOutputStream outTCP =
                new VacinaPerecivelOutputStream(vacinas, quantidade, socketOut);
            outTCP.enviar();

            System.out.println(" Dados enviados ao servidor TCP com sucesso!");
        } catch (ConnectException e) {
            System.out.println(" Servidor TCP não encontrado em localhost:7896.");
            System.out.println("   → Abra outro terminal e execute:");
            System.out.println("     java -cp out servidor.ServidorTCP");
            System.out.println("   Depois rode este teste novamente.\n");
        }
    }
}