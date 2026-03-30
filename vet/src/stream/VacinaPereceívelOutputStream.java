package stream;

import pojo.VacinaPerecivel;
import java.io.IOException;
import java.io.OutputStream;

/**
 * VacinaPerecívelOutputStream — subclasse de OutputStream
 *
 * Serializa um array de VacinaPerecivel para qualquer OutputStream de destino.
 *
 * Formato de cada objeto no stream (bytes):
 * ┌─────────────────────────────────────────────────────┐
 * │  [4 bytes] id  (int)                                │
 * │  [N bytes] nome  (UTF: 2 bytes tamanho + conteúdo)  │
 * │  [8 bytes] preco (double)                           │
 * │  [N bytes] dataValidade (UTF)                       │
 * │  [N bytes] requisitoArmazenamento (UTF)             │
 * │  [8 bytes] temperaturaMinima (double)               │
 * │  [8 bytes] temperaturaMaxima (double)               │
 * └─────────────────────────────────────────────────────┘
 */
public class VacinaPereceívelOutputStream extends OutputStream {

    private final VacinaPerecivel[] vacinas;   // (i)  array de objetos
    private final int               quantidade; // (ii) número de objetos a enviar
    private final OutputStream      destino;   // (iv) destino: System.out, arquivo ou socket

    /**
     * @param vacinas    (i)  array de VacinaPerecivel a transmitir
     * @param quantidade (ii) quantos objetos do array serão enviados
     * @param destino    (iv) OutputStream de destino
     */
    public VacinaPereceívelOutputStream(VacinaPerecivel[] vacinas,
                                         int quantidade,
                                         OutputStream destino) {
        this.vacinas    = vacinas;
        this.quantidade = Math.min(quantidade, vacinas.length);
        this.destino    = destino;
    }

    // ── Método obrigatório de OutputStream ──────────────────────────────────

    /**
     * Escreve um único byte no destino.
     * Necessário para herdar OutputStream — a serialização real está em enviar().
     */
    @Override
    public void write(int b) throws IOException {
        destino.write(b);
    }

    // ── Serialização dos objetos ─────────────────────────────────────────────

    /**
     * Envia os objetos do array para o OutputStream de destino.
     * Chame este método após construir o stream.
     */
    public void enviar() throws IOException {

        // Envia o número total de objetos primeiro (para o leitor saber quantos esperar)
        writeInt(quantidade);

        for (int i = 0; i < quantidade; i++) {
            VacinaPerecivel v = vacinas[i];

            // (iii) pelo menos 3 atributos com seus bytes:
            // Atributo 1 — id (4 bytes, int)
            writeInt(v.getId());

            // Atributo 2 — nome (2 bytes de tamanho + N bytes UTF-8)
            writeUTF(v.getNome());

            // Atributo 3 — preco (8 bytes, double)
            writeDouble(v.getPreco());

            // Atributo 4 — dataValidade (UTF)
            writeUTF(v.getDataValidade());

            // Atributo 5 — requisitoArmazenamento (UTF)
            writeUTF(v.getRequisitoArmazenamento());

            // Atributo 6 — temperaturaMinima (8 bytes, double)
            writeDouble(v.getTemperaturaMinima());

            // Atributo 7 — temperaturaMaxima (8 bytes, double)
            writeDouble(v.getTemperaturaMaxima());
        }

        destino.flush();
    }

    // ── Helpers de escrita de tipos primitivos ───────────────────────────────

    /** Escreve int em 4 bytes (big-endian) */
    private void writeInt(int valor) throws IOException {
        destino.write((valor >>> 24) & 0xFF);
        destino.write((valor >>> 16) & 0xFF);
        destino.write((valor >>>  8) & 0xFF);
        destino.write( valor         & 0xFF);
    }

    /** Escreve double em 8 bytes via IEEE 754 long bits */
    private void writeDouble(double valor) throws IOException {
        long bits = Double.doubleToLongBits(valor);
        for (int i = 56; i >= 0; i -= 8) {
            destino.write((int)((bits >>> i) & 0xFF));
        }
    }

    /**
     * Escreve String em formato UTF simplificado:
     * 2 bytes com o tamanho em bytes + os bytes UTF-8 da string.
     */
    private void writeUTF(String s) throws IOException {
        if (s == null) s = "";
        byte[] bytes = s.getBytes("UTF-8");
        int len = bytes.length;
        // 2 bytes de tamanho
        destino.write((len >>> 8) & 0xFF);
        destino.write( len        & 0xFF);
        // conteúdo
        destino.write(bytes);
    }

    @Override
    public void flush() throws IOException {
        destino.flush();
    }

    @Override
    public void close() throws IOException {
        destino.close();
    }
}