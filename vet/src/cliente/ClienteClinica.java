package cliente;

import protocolo.Protocolo;
import protocolo.Protocolo.*;

import java.io.*;
import java.net.*;

/**
 * ClienteClinica — cliente TCP da Clinica Veterinaria
 *
 * Demonstra empacotamento de request e desempacotamento de reply
 * para todas as 7 operacoes do servidor.
 *
 * Requer ServidorClinica rodando:
 *   java -cp out servidor.ServidorClinica
 *
 * Executar:
 *   java -cp out cliente.ClienteClinica
 */
public class ClienteClinica {

    static final String HOST  = "localhost";
    static final int    PORTA = 7896;

    public static void main(String[] args) throws Exception {

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║   Cliente Clinica Veterinaria                ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        try (Socket socket     = new Socket(HOST, PORTA);
             InputStream  in   = socket.getInputStream();
             OutputStream out  = socket.getOutputStream()) {

            System.out.println("Conectado em " + HOST + ":" + PORTA + "\n");

            // ── 1. Listar todos os produtos ──────────────────────────────────
            titulo("1 — Listar todos os produtos");
            chamar(out, in, Operacao.LISTAR_PRODUTOS, "");

            // ── 2. Buscar produto por id ─────────────────────────────────────
            titulo("2 — Buscar produto id=2");
            chamar(out, in, Operacao.BUSCAR_POR_ID, "2");

            // ── 3. Buscar por especie ────────────────────────────────────────
            titulo("3 — Buscar produtos para Canino");
            chamar(out, in, Operacao.BUSCAR_POR_ESPECIE, "Canino");

            // ── 4. Listar vacinas vencidas ───────────────────────────────────
            titulo("4 — Verificar vacinas vencidas");
            chamar(out, in, Operacao.LISTAR_VENCIDOS, "");

            // ── 5. Relatorio de estoque ──────────────────────────────────────
            titulo("5 — Relatorio geral do estoque");
            chamar(out, in, Operacao.RELATORIO_ESTOQUE, "");

            // ── 6. Cadastrar nova vacina ─────────────────────────────────────
            titulo("6 — Cadastrar nova vacina");
            String novaVacina =
                "{\"nome\":\"Vacina Raiva Felina\"" +
                ",\"preco\":29.90" +
                ",\"fabricante\":\"Pfizer Animal\"" +
                ",\"tipo\":\"VacinaPerecivel\"" +
                ",\"especie\":\"Felino\"" +
                ",\"via\":\"Subcutanea\"" +
                ",\"validade\":\"10/10/2026\"" +
                ",\"armazenamento\":\"Refrigerado 2-8C\"" +
                ",\"tempMin\":2.0" +
                ",\"tempMax\":8.0}";
            chamar(out, in, Operacao.CADASTRAR, novaVacina);

            // ── 7. Listar novamente para confirmar cadastro ──────────────────
            titulo("7 — Listar apos cadastro");
            chamar(out, in, Operacao.LISTAR_PRODUTOS, "");

            // ── 8. Remover produto id=1 ──────────────────────────────────────
            titulo("8 — Remover produto id=1");
            chamar(out, in, Operacao.REMOVER, "1");

            // ── 9. Buscar removido (deve retornar erro) ──────────────────────
            titulo("9 — Buscar produto removido id=1 (esperado: erro)");
            chamar(out, in, Operacao.BUSCAR_POR_ID, "1");
        }

        System.out.println("\nConexao encerrada.");
    }

    // ── Empacota, envia, desempacota e imprime ───────────────────────────────

    static void chamar(OutputStream out, InputStream in,
                       Operacao op, String payload) throws IOException {

        // EMPACOTA request
        byte[] request = Protocolo.empacotar(op, payload);

        String resumoPayload = payload.length() > 60
                               ? payload.substring(0, 60) + "..." : payload;
        System.out.println("  → [REQUEST]  op=" + op + "  payload=" + resumoPayload);

        out.write(request);
        out.flush();

        // DESEMPACOTA reply
        Mensagem reply = Protocolo.desempacotar(in);

        if (reply.operacao == Operacao.RESPOSTA_OK) {
            System.out.println("  ← [OK]       " + formatarResposta(reply.payload));
        } else {
            System.out.println("  ← [ERRO]     " + reply.payload);
        }
        System.out.println();
    }

    static void titulo(String t) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  " + t);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    /** Formata lista JSON para exibicao legivel */
    static String formatarResposta(String payload) {
        if (!payload.startsWith("[{")) return payload;
        String[] itens = payload.substring(1, payload.length()-1).split("\\},\\{");
        StringBuilder sb = new StringBuilder("\n");
        for (String item : itens) {
            String full = item.startsWith("{") ? item + "}" : "{" + item + "}";
            sb.append("    id=").append(extrair(full, "id"))
              .append("  nome=").append(extrair(full, "nome"))
              .append("  tipo=").append(extrair(full, "tipo"))
              .append("  especie=").append(extrair(full, "especie"))
              .append("  validade=").append(extrair(full, "validade"))
              .append("  vencida=").append(extrair(full, "vencida"))
              .append("\n");
        }
        return sb.toString();
    }

    static String extrair(String json, String chave) {
        String busca = "\"" + chave + "\":";
        int ini = json.indexOf(busca);
        if (ini < 0) return "?";
        ini += busca.length();
        if (json.charAt(ini) == '"') {
            int fim = json.indexOf('"', ini+1);
            return json.substring(ini+1, fim);
        }
        int fim = json.indexOf(',', ini);
        if (fim < 0) fim = json.indexOf('}', ini);
        return json.substring(ini, fim < 0 ? json.length() : fim).trim();
    }
}