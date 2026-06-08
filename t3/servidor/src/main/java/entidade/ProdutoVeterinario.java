package entidade;

public class ProdutoVeterinario extends Produto {

    private String registroMapa;
    private String especieAlvo;
    private String viaAdministracao;

    public ProdutoVeterinario() {}

    public ProdutoVeterinario(int id, String nome, double preco, String fabricante,
                               String registroMapa, String especieAlvo, String via) {
        super(id, nome, preco, fabricante);
        this.registroMapa = registroMapa;
        this.especieAlvo  = especieAlvo;
        this.viaAdministracao = via;
    }

    public String getRegistroMapa()               { return registroMapa; }
    public void   setRegistroMapa(String r)        { this.registroMapa = r; }
    public String getEspecieAlvo()                { return especieAlvo; }
    public void   setEspecieAlvo(String e)         { this.especieAlvo = e; }
    public String getViaAdministracao()            { return viaAdministracao; }
    public void   setViaAdministracao(String v)    { this.viaAdministracao = v; }
}