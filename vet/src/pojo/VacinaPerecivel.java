package pojo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * POJO 5 — VacinaPerecivel
 * Subclasse de ProdutoBiologico. Implementa a interface Perecivel.
 * Representa vacinas que requerem cadeia de frio e têm validade.
 */
public class VacinaPerecivel extends ProdutoBiologico implements Perecivel {

    private String dataValidade;           // formato dd/MM/yyyy
    private String requisitoArmazenamento; // Ex: "Refrigerado 2-8°C"
    private double temperaturaMinima;      // °C
    private double temperaturaMaxima;      // °C

    public VacinaPerecivel() {}

    public VacinaPerecivel(int id, String nome, double preco, String fabricante,
                           String registroMapa, String especieAlvo, String viaAdministracao,
                           String tipoAgente, String sorotipo, int numDoses,
                           String dataValidade, String requisitoArmazenamento,
                           double temperaturaMinima, double temperaturaMaxima) {
        super(id, nome, preco, fabricante, registroMapa, especieAlvo, viaAdministracao,
              tipoAgente, sorotipo, numDoses);
        this.dataValidade           = dataValidade;
        this.requisitoArmazenamento = requisitoArmazenamento;
        this.temperaturaMinima      = temperaturaMinima;
        this.temperaturaMaxima      = temperaturaMaxima;
    }

    // ── Implementação de Perecivel ───────────────────────────────────────────

    @Override
    public String getDataValidade() { return dataValidade; }

    @Override
    public boolean isVencido() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate validade    = LocalDate.parse(dataValidade, fmt);
        return LocalDate.now().isAfter(validade);
    }

    @Override
    public String getRequisitoArmazenamento() { return requisitoArmazenamento; }

    // ── Getters / Setters extras ─────────────────────────────────────────────

    public void   setDataValidade(String dataValidade)                   { this.dataValidade = dataValidade; }
    public void   setRequisitoArmazenamento(String r)                    { this.requisitoArmazenamento = r; }

    public double getTemperaturaMinima()                                  { return temperaturaMinima; }
    public void   setTemperaturaMinima(double t)                          { this.temperaturaMinima = t; }

    public double getTemperaturaMaxima()                                  { return temperaturaMaxima; }
    public void   setTemperaturaMaxima(double t)                          { this.temperaturaMaxima = t; }

    @Override
    public String toString() {
        return "VacinaPerecivel{" +
               "id=" + getId() +
               ", nome='" + getNome() + '\'' +
               ", validade='" + dataValidade + '\'' +
               ", vencida=" + isVencido() +
               ", armazenamento='" + requisitoArmazenamento + '\'' +
               ", temp=[" + temperaturaMinima + ";" + temperaturaMaxima + "]°C}";
    }
}