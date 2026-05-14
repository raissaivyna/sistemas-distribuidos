package cliente;

import protocolo.Protocolo;
import protocolo.Protocolo.*;
import protocolo.SerializadorJSON;
import multicast.ReceptorAlertas;
import pojo.*;

import java.io.*;
import java.net.*;

/**
 * ClienteClinicaCompleto — cliente final da Clinica Veterinaria
 *
 * Integra:
 *   TCP  → todas as operacoes do ServidorClinicaCompleto
 *   UDP  → escuta alertas multicast em background (thread daemon)
 *
 * Como funciona:
 *   - Ao iniciar, sobe ReceptorAlertas em background
 *   - Qualquer alerta enviado pelo servidor aparece automaticamente
 *   - As operacoes TCP seguem normalmente em paralelo
 *
 * Iniciar (ServidorClinicaCompleto deve estar rodando):
 *   java -cp out cliente.ClienteClinicaCompleto
 */
public class ClienteClinicaCompleto {

    static final String HOST  = "localhost";
    static final int    PORTA = 7896;

    public static void main(String[] args) throws Exception {

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║  Cliente Completo — Clinica Veterinaria      ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        // Inicia recepcao de alertas UDP multicast em background
        ReceptorAlertas receptor = new ReceptorAlertas();
        receptor.iniciar();

        Thread.sleep(300); // aguarda receptor entrar no grupo

        try (Socket socket    = new Socket(HOST, PORTA);
             InputStream  in  = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {

            System.out.println("Conectado em " + HOST + ":" + PORTA + "\n");

            // ── 1. Listar todos os produtos ──────────────────────────────────
            titulo("1 — Listar todos os produtos");
            chamar(out, in, Operacao.LISTAR_PRODUTOS, "");

            // ── 2. Buscar por especie ────────────────────────────────────────
            titulo("2 — Produtos para Canino");
            chamar(out, in, Operacao.BUSCAR_POR_ESPECIE, "Canino");

            // ── 3. Verificar vencidos (dispara alerta multicast no servidor) ─
            titulo("3 — Verificar vencidos (alerta multicast automatico)");
            chamar(out, in, Operacao.LISTAR_VENCIDOS, "");
            Thread.sleep(200); // aguarda alerta multicast chegar

            // ── 4. Admin envia alerta de recall ──────────────────────────────
            titulo("4 — Admin envia RECALL via multicast");
            chamar(out, in, Operacao.ENVIAR_ALERTA,
                "RECALL|Lote BT-2024 da Zoetis recolhido — contaminacao detectada");
            Thread.sleep(300);

            // ── 5. Admin envia promocao ──────────────────────────────────────
            titulo("5 — Admin envia PROMOCAO via multicast");
            chamar(out, in, Operacao.ENVIAR_ALERTA,
                "PROMOCAO|Merial com 20% de desconto para pedidos ate sexta-feira");
            Thread.sleep(300);

            // ── 6. Admin abre janela de pedidos de reposicao ─────────────────
            titulo("6 — Admin abre janela de reposicao (30 segundos)");
            chamar(out, in, Operacao.ABRIR_JANELA, "Reposicao Semanal|30");
            Thread.sleep(500);

            // ── 7. Clinicos registram pedidos durante a janela ───────────────
            titulo("7 — Dr. Joao registra pedido de reposicao");
            String jsonVacina = SerializadorJSON.serializar(
                new VacinaPerecivel(0, "Vacina Anti-Rabica", 32.50, "Zoetis",
                    "BR-001", "Canino", "Subcutanea",
                    "Virus Inativado", "PV-11", 10,
                    "15/08/2027", "Refrigerado 2-8C", 2.0, 8.0));
            chamar(out, in, Operacao.PEDIDO_REPOSICAO, "dr.joao|" + jsonVacina);

            titulo("8 — Dra. Maria registra pedido de reposicao");
            String jsonQuimio = SerializadorJSON.serializar(
                new ProdutoQuimioterapico(0, "Amoxicilina 500mg", 45.90, "MSD",
                    "BR-004", "Canino", "Oral",
                    "Amoxicilina", 14.0, "Antibiotico", false));
            chamar(out, in, Operacao.PEDIDO_REPOSICAO, "dra.maria|" + jsonQuimio);

            // ── 9. Cadastrar produto com JSON completo ───────────────────────
            titulo("9 — Cadastrar nova vacina (JSON completo)");
            String novaVacina = SerializadorJSON.serializar(
                new VacinaPerecivel(0, "Vacina Leishmaniose", 89.00, "MSD",
                    "BR-099", "Canino", "Subcutanea",
                    "Proteina Recombinante", "LBSap", 1,
                    "20/06/2027", "Refrigerado 2-8C", 2.0, 8.0));
            chamar(out, in, Operacao.CADASTRAR, novaVacina);

            // ── 10. Relatorio final ──────────────────────────────────────────
            titulo("10 — Relatorio final do estoque");
            chamar(out, in, Operacao.RELATORIO_ESTOQUE, "");

            System.out.println("\nAguardando 30s para janela encerrar e ver alerta multicast...");
            System.out.println("(Ctrl+C para sair antes)\n");
            Thread.sleep(32_000); // aguarda encerramento da janela + alerta UDP
        }
    }

    // ── Empacota, envia, desempacota e imprime ───────────────────────────────

    static void chamar(OutputStream out, InputStream in,
                       Operacao op, String payload) throws IOException {

        byte[] req = Protocolo.empacotar(op, payload);
        String resumo = payload.length() > 70
                        ? payload.substring(0, 70) + "..." : payload;

        System.out.println("  → [REQ]  op=" + op + "  |  " + resumo);

        out.write(req);
        out.flush();

        Mensagem resp = Protocolo.desempacotar(in);
        if (resp.operacao == Operacao.RESPOSTA_OK) {
            System.out.println("  ← [OK]   " + formatar(resp.payload));
        } else {
            System.out.println("  ← [ERR]  " + resp.payload);
        }
        System.out.println();
    }

    static void titulo(String t) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  " + t);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    static String formatar(String p) {
        if (!p.startsWith("[{")) return p;
        String[] itens = p.substring(1, p.length()-1).split("\\},\\{");
        StringBuilder sb = new StringBuilder("\n");
        for (String item : itens) {
            String full = item.startsWith("{") ? item+"}" : "{"+item+"}";
            sb.append("    ")
              .append(SerializadorJSON.get(full, "tipo")).append(" | ")
              .append(SerializadorJSON.get(full, "nome")).append(" | R$ ")
              .append(SerializadorJSON.get(full, "preco")).append(" | especie: ")
              .append(SerializadorJSON.get(full, "especieAlvo")).append("\n");
        }
        return sb.toString();
    }
}