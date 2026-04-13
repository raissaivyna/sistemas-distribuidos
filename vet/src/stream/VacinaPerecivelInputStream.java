package stream;

import pojo.VacinaPerecivel;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * VacinaPerecivelInputStream — subclasse de InputStream
 *
 * Le bytes gerados pelo VacinaPerecivelOutputStream e reconstrói
 * os objetos VacinaPerecivel.
 *
 * Formato lido (espelho exato do OutputStream):
 *   [4 bytes]   quantidade de objetos  (int)
 *   Para cada objeto:
 *     [4 bytes]   id                   (int)
 *     [2+N bytes] nome                 (UTF)
 *     [8 bytes]   preco                (double)
 *     [2+N bytes] dataValidade         (UTF)
 *     [2+N bytes] armazenamento        (UTF)
 *     [8 bytes]   temperaturaMinima    (double)
 *     [8 bytes]   temperaturaMaxima    (double)
 *
 * Origens possiveis:
 *   b) System.in        (entrada padrao)
 *   c) FileInputStream  (arquivo .bin gerado pelo OutputStream)
 *   d) Socket TCP       (servidor remoto)
 */
public class VacinaPerecivelInputStream extends InputStream {

    private final InputStream origem;

    /**
     * @param origem InputStream de onde os bytes serao lidos.
     *               Aceita System.in, FileInputStream ou socket.getInputStream().
     */
    public VacinaPerecivelInputStream(InputStream origem) {
        this.origem = origem;
    }

    // ── Metodo obrigatorio de InputStream ────────────────────────────────────

    @Override
    public int read() throws IOException {
        return origem.read();
    }

    // ── Desserializacao ──────────────────────────────────────────────────────

    /**
     * Le todos os objetos VacinaPerecivel do stream.
     * @return lista com os objetos reconstruidos
     */
    public List<VacinaPerecivel> lerVacinas() throws IOException {
        List<VacinaPerecivel> lista = new ArrayList<>();

        int quantidade = readInt();
        System.out.println("[InputStream] Esperando " + quantidade + " vacina(s)...");

        for (int i = 0; i < quantidade; i++) {
            int    id       = readInt();
            String nome     = readUTF();
            double preco    = readDouble();
            String validade = readUTF();
            String armaz    = readUTF();
            double tempMin  = readDouble();
            double tempMax  = readDouble();

            VacinaPerecivel v = new VacinaPerecivel(
                id, nome, preco, "",
                "", "", "",
                "", "", 0,
                validade, armaz, tempMin, tempMax
            );

            lista.add(v);
            System.out.println("[InputStream] Reconstruida: " + v);
        }

        return lista;
    }

    // ── Helpers de leitura ───────────────────────────────────────────────────

    private int readInt() throws IOException {
        int b1 = origem.read(), b2 = origem.read(),
            b3 = origem.read(), b4 = origem.read();
        if ((b1 | b2 | b3 | b4) < 0)
            throw new IOException("Fim inesperado do stream (readInt).");
        return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
    }

    private double readDouble() throws IOException {
        long bits = 0;
        for (int i = 0; i < 8; i++) {
            int b = origem.read();
            if (b < 0) throw new IOException("Fim inesperado do stream (readDouble).");
            bits = (bits << 8) | (b & 0xFF);
        }
        return Double.longBitsToDouble(bits);
    }

    private String readUTF() throws IOException {
        int hi = origem.read(), lo = origem.read();
        if ((hi | lo) < 0) throw new IOException("Fim inesperado do stream (readUTF tamanho).");
        int    len   = (hi << 8) | (lo & 0xFF);
        byte[] bytes = origem.readNBytes(len);
        if (bytes.length < len)
            throw new IOException("Bytes insuficientes para reconstruir string UTF.");
        return new String(bytes, "UTF-8");
    }

    @Override
    public void close() throws IOException {
        origem.close();
    }
}