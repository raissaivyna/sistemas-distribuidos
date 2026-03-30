package pojo;

/**
 * POJO 1 — Superclasse Produto
 * Base de toda a hierarquia de produtos veterinários.
 */
public class Produto {

    private int    id;
    private String nome;
    private double preco;
    private String fabricante;

    public Produto() {}

    public Produto(int id, String nome, double preco, String fabricante) {
        this.id         = id;
        this.nome       = nome;
        this.preco      = preco;
        this.fabricante = fabricante;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public int    getId()          { return id; }
    public void   setId(int id)    { this.id = id; }

    public String getNome()                { return nome; }
    public void   setNome(String nome)     { this.nome = nome; }

    public double getPreco()               { return preco; }
    public void   setPreco(double preco)   { this.preco = preco; }

    public String getFabricante()                    { return fabricante; }
    public void   setFabricante(String fabricante)   { this.fabricante = fabricante; }

    @Override
    public String toString() {
        return "Produto{id=" + id +
               ", nome='" + nome + '\'' +
               ", preco=" + preco +
               ", fabricante='" + fabricante + '\'' + '}';
    }
}