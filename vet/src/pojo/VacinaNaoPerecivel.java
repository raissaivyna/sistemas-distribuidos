package pojo;

/**
 * POJO 6 — VacinaNaoPerecivel
 * Subclasse de ProdutoBiologico.
 * Vacinas liofilizadas ou termotolerantes — armazenamento à temperatura ambiente.
 */
public class VacinaNaoPerecivel extends ProdutoBiologico {

    private String formaFarmaceutica; // Ex: "Liofilizado", "Suspensão termotolerante"
    private int    prazoValidadeMeses;// Validade em meses
    private double temperaturaMax;   // Temperatura máxima de armazenamento (°C)

    public VacinaNaoPerecivel() {}

    public VacinaNaoPerecivel(int id, String nome, double preco, String fabricante,
                              String registroMapa, String especieAlvo, String viaAdministracao,
                              String tipoAgente, String sorotipo, int numDoses,
                              String formaFarmaceutica, int prazoValidadeMeses,
                              double temperaturaMax) {
        super(id, nome, preco, fabricante, registroMapa, especieAlvo, viaAdministracao,
              tipoAgente, sorotipo, numDoses);
        this.formaFarmaceutica  = formaFarmaceutica;
        this.prazoValidadeMeses = prazoValidadeMeses;
        this.temperaturaMax     = temperaturaMax;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getFormaFarmaceutica()                             { return formaFarmaceutica; }
    public void   setFormaFarmaceutica(String formaFarmaceutica)     { this.formaFarmaceutica = formaFarmaceutica; }

    public int    getPrazoValidadeMeses()                            { return prazoValidadeMeses; }
    public void   setPrazoValidadeMeses(int p)                       { this.prazoValidadeMeses = p; }

    public double getTemperaturaMax()                                { return temperaturaMax; }
    public void   setTemperaturaMax(double t)                        { this.temperaturaMax = t; }

    @Override
    public String toString() {
        return "VacinaNaoPerecivel{" +
               "id=" + getId() +
               ", nome='" + getNome() + '\'' +
               ", forma='" + formaFarmaceutica + '\'' +
               ", validadeMeses=" + prazoValidadeMeses +
               ", tempMax=" + temperaturaMax + "°C}";
    }
}