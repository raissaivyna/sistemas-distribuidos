import pojo.VacinaPerecivel;
import stream.VacinaPerecívelInputStream;
import stream.VacinaPereceívelOutputStream;

import java.io.*;
import java.net.*;
import java.util.List;

/**
 * TesteInputStream — testa as 3 origens do VacinaPerecívelInputStream:
 *
 *   b) System.in       → java -cp out TesteInputStream stdin
 *   c) FileInputStream → java -cp out TesteInputStream arquivo
 *   d) Socket TCP      → java -cp out TesteInputStream tcp
 *      (requer ServidorTCPInputStream rodando em outro terminal)
 *
 * Sem argumento → roda todos os testes que não precisam de interação.
 */
public class TesteInputStream {

    // Vacinas usadas para gerar dados nos testes
    static VacinaPerecivel[] vacinas = {
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

    public static void main(String[] args) throws Exception {

        String modo = (args.length > 0) ? args[0] : "todos";

        switch (modo) {
            case "stdin"   -> testeStdin();
            case "arquivo" -> testeArquivo();
            case "tcp"     -> testeTCP();
            default        -> { testeArquivo(); testeTCP(); }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // TESTE b — System.in (entrada padrão)
    // Uso especial: gera bytes no stdout e redireciona para stdin no terminal
    //   java -cp out TesteOutputStream 2>/dev/null | java -cp out TesteInputStream stdin
    // ════════════════════════════════════════════════════════════════════════
    static void testeStdin() throws Exception {
        System.err.println("╔══════════════════════════════════════════╗");
        System.err.println("║  TESTE b — System.in                     ║");
        System.err.println("╚══════════════════════════════════════════╝");
        System.err.println("Lendo bytes do stdin...\n");

        try (VacinaPerecívelInputStream vis = new VacinaPerecívelInputStream(System.in)) {
            List<VacinaPerecivel> lidas = vis.lerVacinas();
            imprimirResultado(lidas);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // TESTE c — FileInputStream (lê o arquivo vacinas.bin gerado pelo TesteOutputStream)
    // ════════════════════════════════════════════════════════════════════════
    static void testeArquivo() throws Exception {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║  TESTE c — FileInputStream               ║");
        System.out.println("╚══════════════════════════════════════════╝");

        String caminho = "vacinas.bin";
        File arquivo   = new File(caminho);

        // Se o arquivo não existe, gera agora
        if (!arquivo.exists()) {
            System.out.println("Arquivo não encontrado. Gerando vacinas.bin primeiro...");
            try (FileOutputStream fos = new FileOutputStream(caminho)) {
                VacinaPereceívelOutputStream vos =
                    new VacinaPereceívelOutputStream(vacinas, vacinas.length, fos);
                vos.enviar();
            }
            System.out.println("Arquivo gerado: " + arquivo.getAbsolutePath());
        }

        System.out.println("Lendo: " + arquivo.getAbsolutePath() +
                           " (" + arquivo.length() + " bytes)\n");

        try (FileInputStream fis = new FileInputStream(arquivo);
             VacinaPerecívelInputStream vis = new VacinaPerecívelInputStream(fis)) {

            List<VacinaPerecivel> lidas = vis.lerVacinas();
            imprimirResultado(lidas);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // TESTE d — Socket TCP
    // Inicie o ServidorTCPInputStream em outro terminal antes de rodar este.
    // Aqui o cliente envia os dados E os lê de volta via loopback para demonstrar.
    // ════════════════════════════════════════════════════════════════════════
    static void testeTCP() throws Exception {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║  TESTE d — Socket TCP (localhost:7896)   ║");
        System.out.println("╚══════════════════════════════════════════╝");

        // Inicia mini-servidor local em thread separada para o teste ser autossuficiente
        ServerSocket[] ssHolder = new ServerSocket[1];
        Thread servidorThread = new Thread(() -> {
            try {
                ssHolder[0] = new ServerSocket(7896);
                System.out.println("[Mini-servidor] Aguardando na porta 7896...");
                Socket conn = ssHolder[0].accept();
                System.out.println("[Mini-servidor] Cliente conectado.\n");

                try (VacinaPerecívelInputStream vis =
                         new VacinaPerecívelInputStream(conn.getInputStream())) {
                    List<VacinaPerecivel> recebidas = vis.lerVacinas();
                    System.out.println("\n=== Objetos reconstruídos no servidor ===");
                    imprimirResultado(recebidas);
                }
                conn.close();
            } catch (IOException e) {
                if (!e.getMessage().contains("closed"))
                    System.out.println("[Mini-servidor] Erro: " + e.getMessage());
            }
        });
        servidorThread.setDaemon(true);
        servidorThread.start();

        Thread.sleep(400); // aguarda servidor subir

        // Cliente envia os dados
        try (Socket socket = new Socket("localhost", 7896)) {
            System.out.println("[Cliente TCP] Conectado. Enviando " + vacinas.length + " vacina(s)...");
            VacinaPereceívelOutputStream vos =
                new VacinaPereceívelOutputStream(vacinas, vacinas.length,
                                                  socket.getOutputStream());
            vos.enviar();
            System.out.println("[Cliente TCP] Dados enviados!\n");
        } catch (ConnectException e) {
            System.out.println("⚠️  Porta 7896 ocupada. Rode:");
            System.out.println("   Terminal 1: java -cp out servidor.ServidorTCPInputStream");
            System.out.println("   Terminal 2: java -cp out TesteInputStream tcp\n");
            if (ssHolder[0] != null) ssHolder[0].close();
            return;
        }

        servidorThread.join(3000); // aguarda servidor terminar de ler
        if (ssHolder[0] != null) ssHolder[0].close();
    }

    // ── Impressão formatada ──────────────────────────────────────────────────
    static void imprimirResultado(List<VacinaPerecivel> lista) {
        System.out.println("\n✅ " + lista.size() + " vacina(s) reconstruída(s):");
        for (int i = 0; i < lista.size(); i++) {
            VacinaPerecivel v = lista.get(i);
            System.out.println("\n  [" + (i+1) + "] " + v.getNome());
            System.out.println("      Preço:         R$ " + v.getPreco());
            System.out.println("      Validade:      " + v.getDataValidade());
            System.out.println("      Armazenamento: " + v.getRequisitoArmazenamento());
            System.out.println("      Temperatura:   " + v.getTemperaturaMinima()
                               + "°C a " + v.getTemperaturaMaxima() + "°C");
            System.out.println("      Vencida:       " + (v.isVencido() ? "SIM ⚠️" : "NÃO ✅"));
        }
        System.out.println();
    }
}