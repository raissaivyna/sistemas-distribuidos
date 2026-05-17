package pojo;

/**
 * POJO 4 — ProdutoBiologico
 * Subclasse de ProdutoVeterinario.
 * Representa produtos de origem biológica (vacinas, soros, antígenos).
 */
public class ProdutoBiologico extends ProdutoVeterinario {

    private String tipoAgente;     // Ex: "Vírus atenuado", "Bactéria inativada"
    private String sorotipo;       // Ex: "H1N1", "Sorotipo B"
    private int    numDoses;       // Número de doses no frasco

    public ProdutoBiologico() {}

    public ProdutoBiologico(int id, String nome, double preco, String fabricante,
                            String registroMapa, String especieAlvo, String viaAdministracao,
                            String tipoAgente, String sorotipo, int numDoses) {
        super(id, nome, preco, fabricante, registroMapa, especieAlvo, viaAdministracao);
        this.tipoAgente = tipoAgente;
        this.sorotipo   = sorotipo;
        this.numDoses   = numDoses;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getTipoAgente()                      { return tipoAgente; }
    public void   setTipoAgente(String tipoAgente)     { this.tipoAgente = tipoAgente; }

    public String getSorotipo()                        { return sorotipo; }
    public void   setSorotipo(String sorotipo)         { this.sorotipo = sorotipo; }

    public int    getNumDoses()                        { return numDoses; }
    public void   setNumDoses(int numDoses)            { this.numDoses = numDoses; }

    @Override
    public String toString() {
        return "ProdutoBiologico{" +
               "id=" + getId() +
               ", nome='" + getNome() + '\'' +
               ", tipoAgente='" + tipoAgente + '\'' +
               ", sorotipo='" + sorotipo + '\'' +
               ", numDoses=" + numDoses + '}';
    }
}