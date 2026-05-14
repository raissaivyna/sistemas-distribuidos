package pojo;

/**
 * POJO 3 — ProdutoQuimioterapico
 * Subclasse de ProdutoVeterinario.
 * Representa medicamentos de síntese química (antibióticos, antiparasitários, etc.).
 */
public class ProdutoQuimioterapico extends ProdutoVeterinario {

    private String principioAtivo;    // Ex: "Ivermectina", "Amoxicilina"
    private double concentracao;      // Ex: 1.0 (em %)
    private String classeTerapeutica; // Ex: "Antibiótico", "Antiparasitário"
    private boolean retencaoCarencia; // Exige carência para abate/leite?

    public ProdutoQuimioterapico() {}

    public ProdutoQuimioterapico(int id, String nome, double preco, String fabricante,
                                 String registroMapa, String especieAlvo, String viaAdministracao,
                                 String principioAtivo, double concentracao,
                                 String classeTerapeutica, boolean retencaoCarencia) {
        super(id, nome, preco, fabricante, registroMapa, especieAlvo, viaAdministracao);
        this.principioAtivo    = principioAtivo;
        this.concentracao      = concentracao;
        this.classeTerapeutica = classeTerapeutica;
        this.retencaoCarencia  = retencaoCarencia;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getPrincipioAtivo()                        { return principioAtivo; }
    public void   setPrincipioAtivo(String principioAtivo)   { this.principioAtivo = principioAtivo; }

    public double getConcentracao()                          { return concentracao; }
    public void   setConcentracao(double concentracao)       { this.concentracao = concentracao; }

    public String getClasseTerapeutica()                             { return classeTerapeutica; }
    public void   setClasseTerapeutica(String classeTerapeutica)     { this.classeTerapeutica = classeTerapeutica; }

    public boolean isRetencaoCarencia()                          { return retencaoCarencia; }
    public void    setRetencaoCarencia(boolean retencaoCarencia) { this.retencaoCarencia = retencaoCarencia; }

    @Override
    public String toString() {
        return "ProdutoQuimioterapico{" +
               "id=" + getId() +
               ", nome='" + getNome() + '\'' +
               ", principioAtivo='" + principioAtivo + '\'' +
               ", concentracao=" + concentracao + "%" +
               ", classeTerapeutica='" + classeTerapeutica + '\'' +
               ", retencaoCarencia=" + retencaoCarencia + '}';
    }
}