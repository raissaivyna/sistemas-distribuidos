import pojo.*;
import protocolo.SerializadorJSON;
import protocolo.Protocolo;
import protocolo.Protocolo.*;

import java.io.*;
import java.net.*;

/**
 * TesteSerializacao — demonstra a Representacao Externa de Dados (Fase 5)
 *
 * Testa o ciclo completo:
 *   1. Objeto Java → JSON (serializacao)
 *   2. JSON viaja como payload no Protocolo via TCP
 *   3. JSON → Objeto Java (desserializacao)
 *   4. Objeto reconstruido funciona corretamente
 *
 * Executar (ServidorClinica deve estar rodando):
 *   java -cp out TesteSerializacao
 */
public class TesteSerializacao {

    public static void main(String[] args) throws Exception {

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║  Fase 5 — Representacao Externa de Dados (JSON) ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");

        // ── Teste 1: Serializar cada tipo de produto ─────────────────────────
        titulo("Teste 1 — Serializacao de cada tipo de produto");

        VacinaPerecivel vp = new VacinaPerecivel(
            1, "Vacina Anti-Rabica", 32.50, "Zoetis",
            "BR-001", "Canino", "Subcutanea",
            "Virus Inativado", "PV-11", 10,
            "31/12/2025", "Refrigerado 2-8C", 2.0, 8.0);

        VacinaNaoPerecivel vnp = new VacinaNaoPerecivel(
            2, "Vacina Brucelose", 18.00, "Fort Dodge",
            "BR-002", "Bovino", "Subcutanea",
            "Bacteria Inativada", "S19", 20,
            "Liofilizado", 24, 30.0);

        ProdutoQuimioterapico pq = new ProdutoQuimioterapico(
            3, "Amoxicilina 500mg", 45.90, "MSD",
            "BR-003", "Canino", "Oral",
            "Amoxicilina", 14.0, "Antibiotico", false);

        String jsonVP  = SerializadorJSON.serializar(vp);
        String jsonVNP = SerializadorJSON.serializar(vnp);
        String jsonPQ  = SerializadorJSON.serializar(pq);

        System.out.println("VacinaPerecivel JSON:\n  " + jsonVP);
        System.out.println("\nVacinaNaoPerecivel JSON:\n  " + jsonVNP);
        System.out.println("\nProdutoQuimioterapico JSON:\n  " + jsonPQ);

        // ── Teste 2: Desserializar e verificar campos ─────────────────────────
        titulo("Teste 2 — Desserializacao e verificacao de campos");

        VacinaPerecivel vpReconstruida = (VacinaPerecivel) SerializadorJSON.desserializar(jsonVP);
        System.out.println("Reconstruida: " + vpReconstruida);
        System.out.println("  isVencido()      : " + vpReconstruida.isVencido());
        System.out.println("  dataValidade     : " + vpReconstruida.getDataValidade());
        System.out.println("  armazenamento    : " + vpReconstruida.getRequisitoArmazenamento());
        System.out.println("  temperaturas     : " + vpReconstruida.getTemperaturaMinima()
                            + "C a " + vpReconstruida.getTemperaturaMaxima() + "C");

        ProdutoQuimioterapico pqReconstruido =
            (ProdutoQuimioterapico) SerializadorJSON.desserializar(jsonPQ);
        System.out.println("\nReconstruido: " + pqReconstruido);
        System.out.println("  principioAtivo   : " + pqReconstruido.getPrincipioAtivo());
        System.out.println("  classeTerapeutica: " + pqReconstruido.getClasseTerapeutica());

        // ── Teste 3: Serializar Estoque completo ─────────────────────────────
        titulo("Teste 3 — Serializacao de Estoque completo");

        Estoque estoque = new Estoque(1, "Clinica Central — Geladeira A");
        estoque.adicionarProduto(vp);
        estoque.adicionarProduto(vnp);
        estoque.adicionarProduto(pq);

        String jsonEstoque = SerializadorJSON.serializarEstoque(estoque);
        System.out.println("Estoque JSON:\n  " + jsonEstoque.substring(0, Math.min(200, jsonEstoque.length())) + "...");
        System.out.println("  [JSON completo tem " + jsonEstoque.length() + " caracteres]");

        // ── Teste 4: JSON como payload no Protocolo TCP ───────────────────────
        titulo("Teste 4 — JSON trafegando como payload no Protocolo TCP");

        // Simula envio: objeto → JSON → bytes do Protocolo
        byte[] bytesEnvio = Protocolo.empacotar(Operacao.CADASTRAR, jsonVP);
        System.out.println("Bytes empacotados para envio: " + bytesEnvio.length + " bytes");
        System.out.println("  [1 byte op][4 bytes tamanho][" + jsonVP.length() + " bytes JSON]");

        // Simula recepcao: bytes → Protocolo → JSON → objeto
        InputStream fakeStream = new ByteArrayInputStream(bytesEnvio);
        Mensagem msg = Protocolo.desempacotar(fakeStream);

        System.out.println("\nMensagem recebida:");
        System.out.println("  operacao : " + msg.operacao);
        System.out.println("  payload  : " + msg.payload.substring(0, 60) + "...");

        Produto reconstruidoDoWire = SerializadorJSON.desserializar(msg.payload);
        System.out.println("\nObjeto final reconstruido do wire:");
        System.out.println("  " + reconstruidoDoWire);
        System.out.println("  tipo real: " + reconstruidoDoWire.getClass().getSimpleName());

        // ── Teste 5: TCP real com ServidorClinica ────────────────────────────
        titulo("Teste 5 — Cadastro via TCP com JSON completo (ServidorClinica)");

        try (Socket socket   = new Socket("localhost", 7896);
             InputStream in  = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {

            // Serializa VacinaPerecivel para JSON e envia como CADASTRAR
            String jsonParaEnviar = SerializadorJSON.serializar(
                new VacinaPerecivel(0, "Vacina Leishmaniose", 89.00, "MSD",
                    "BR-099", "Canino", "Subcutanea",
                    "Proteina Recombinante", "LBSap", 1,
                    "20/06/2027", "Refrigerado 2-8C", 2.0, 8.0));

            byte[] req = Protocolo.empacotar(Operacao.CADASTRAR, jsonParaEnviar);
            out.write(req);
            out.flush();

            Mensagem resp = Protocolo.desempacotar(in);
            System.out.println("Resposta do servidor: [" + resp.operacao + "] " + resp.payload);

            // Pede relatorio para confirmar
            out.write(Protocolo.empacotar(Operacao.RELATORIO_ESTOQUE, ""));
            out.flush();
            Mensagem relatorio = Protocolo.desempacotar(in);
            System.out.println("\nRelatorio apos cadastro:\n" + relatorio.payload);

        } catch (ConnectException e) {
            System.out.println("Servidor nao encontrado. Para rodar o Teste 5:");
            System.out.println("  Terminal 1: bash compile_run.sh servidorclinica");
            System.out.println("  Terminal 2: bash compile_run.sh serializacao");
        }

        System.out.println("\nTodos os testes concluidos.");
    }

    static void titulo(String t) {
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("  " + t);
        System.out.println("══════════════════════════════════════════════════");
    }
}