package protocolo;

import java.io.*;

/**
 * Protocolo de comunicacao da Clinica Veterinaria
 *
 * Cada mensagem trafega no socket com este formato de bytes:
 *
 *   ┌──────────┬──────────────────┬───────────────────────┐
 *   │ 1 byte   │ 4 bytes          │ N bytes               │
 *   │ operacao │ tamanho payload  │ payload (UTF-8)       │
 *   └──────────┴──────────────────┴───────────────────────┘
 *
 * Operacoes disponiveis:
 *
 *   LISTAR_PRODUTOS   (1) — cliente pede lista completa
 *   BUSCAR_POR_ID     (2) — cliente envia id, recebe produto
 *   BUSCAR_POR_ESPECIE(3) — cliente envia especie, recebe lista
 *   CADASTRAR         (4) — cliente envia produto JSON, servidor confirma
 *   REMOVER           (5) — cliente envia id, servidor confirma
 *   LISTAR_VENCIDOS   (6) — cliente pede vacinas vencidas
 *   RELATORIO_ESTOQUE (7) — cliente pede relatorio geral
 *   RESPOSTA_OK      (10) — servidor confirma com dados
 *   RESPOSTA_ERRO    (11) — servidor informa erro
 */
public class Protocolo {

    public enum Operacao {
        LISTAR_PRODUTOS    (1),
        BUSCAR_POR_ID      (2),
        BUSCAR_POR_ESPECIE (3),
        CADASTRAR          (4),
        REMOVER            (5),
        LISTAR_VENCIDOS    (6),
        RELATORIO_ESTOQUE  (7),
        ABRIR_JANELA       (8),
        PEDIDO_REPOSICAO   (9),
        ENVIAR_ALERTA      (20),
        RESPOSTA_OK        (10),
        RESPOSTA_ERRO      (11);

        public final int codigo;
        Operacao(int c) { this.codigo = c; }

        public static Operacao deCodigo(int c) {
            for (Operacao o : values())
                if (o.codigo == c) return o;
            throw new IllegalArgumentException("Operacao desconhecida: " + c);
        }
    }

    // ── Mensagem recebida ────────────────────────────────────────────────────

    public static class Mensagem {
        public final Operacao operacao;
        public final String   payload;

        public Mensagem(Operacao operacao, String payload) {
            this.operacao = operacao;
            this.payload  = payload;
        }

        @Override
        public String toString() {
            String resumo = payload.length() > 80
                            ? payload.substring(0, 80) + "..." : payload;
            return "Mensagem{op=" + operacao + ", payload='" + resumo + "'}";
        }
    }

    // ── Empacotamento ────────────────────────────────────────────────────────

    /**
     * Empacota uma operacao + payload em bytes prontos para enviar no socket.
     *
     * @param op      operacao a executar
     * @param payload dados em texto (numero, JSON, string vazia)
     * @return array de bytes: [1 op][4 tamanho][N payload]
     */
    public static byte[] empacotar(Operacao op, String payload) throws IOException {
        byte[] payloadBytes = (payload == null ? "" : payload).getBytes("UTF-8");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream      dos  = new DataOutputStream(baos);

        dos.writeByte(op.codigo);             // 1 byte
        dos.writeInt(payloadBytes.length);    // 4 bytes
        dos.write(payloadBytes);              // N bytes
        dos.flush();

        return baos.toByteArray();
    }

    // ── Desempacotamento ─────────────────────────────────────────────────────

    /**
     * Le o proximo pacote do InputStream e monta uma Mensagem.
     * Bloqueia ate os bytes chegarem.
     */
    public static Mensagem desempacotar(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in);

        int  codigoOp = dis.readUnsignedByte();  // 1 byte
        int  tamanho  = dis.readInt();            // 4 bytes
        byte[] bytes  = dis.readNBytes(tamanho);  // N bytes

        Operacao op      = Operacao.deCodigo(codigoOp);
        String   payload = new String(bytes, "UTF-8");

        return new Mensagem(op, payload);
    }
}