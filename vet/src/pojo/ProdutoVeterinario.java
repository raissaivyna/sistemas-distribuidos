package pojo;

/**
 * POJO 2 — ProdutoVeterinario
 * Subclasse de Produto. Adiciona informações específicas de uso veterinário.
 */
public class ProdutoVeterinario extends Produto {

    private String registroMapa;   // Registro no Ministério da Agricultura (MAPA)
    private String especieAlvo;    // Ex: "Bovino", "Canino", "Felino"
    private String viaAdministracao; // Ex: "Oral", "Intramuscular", "Subcutânea"

    public ProdutoVeterinario() {}

    public ProdutoVeterinario(int id, String nome, double preco, String fabricante,
                              String registroMapa, String especieAlvo, String viaAdministracao) {
        super(id, nome, preco, fabricante);
        this.registroMapa      = registroMapa;
        this.especieAlvo       = especieAlvo;
        this.viaAdministracao  = viaAdministracao;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getRegistroMapa()                      { return registroMapa; }
    public void   setRegistroMapa(String registroMapa)   { this.registroMapa = registroMapa; }

    public String getEspecieAlvo()                       { return especieAlvo; }
    public void   setEspecieAlvo(String especieAlvo)     { this.especieAlvo = especieAlvo; }

    public String getViaAdministracao()                          { return viaAdministracao; }
    public void   setViaAdministracao(String viaAdministracao)   { this.viaAdministracao = viaAdministracao; }

    @Override
    public String toString() {
        return "ProdutoVeterinario{" +
               "id=" + getId() +
               ", nome='" + getNome() + '\'' +
               ", preco=" + getPreco() +
               ", fabricante='" + getFabricante() + '\'' +
               ", registroMapa='" + registroMapa + '\'' +
               ", especieAlvo='" + especieAlvo + '\'' +
               ", viaAdministracao='" + viaAdministracao + '\'' + '}';
    }
}