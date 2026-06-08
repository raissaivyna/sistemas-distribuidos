package entidade;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Produto — entidade base.
 * Jackson usa o campo "tipo" para deserializar subclasses corretamente.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ProdutoVeterinario.class, name = "ProdutoVeterinario"),
    @JsonSubTypes.Type(value = VacinaPerecivel.class,    name = "VacinaPerecivel")
})
public class Produto {

    private int    id;
    private String nome;
    private double preco;
    private String fabricante;

    public Produto() {}

    public Produto(int id, String nome, double preco, String fabricante) {
        this.id = id; this.nome = nome;
        this.preco = preco; this.fabricante = fabricante;
    }

    public int    getId()                  { return id; }
    public void   setId(int id)            { this.id = id; }
    public String getNome()                { return nome; }
    public void   setNome(String n)        { this.nome = n; }
    public double getPreco()               { return preco; }
    public void   setPreco(double p)       { this.preco = p; }
    public String getFabricante()          { return fabricante; }
    public void   setFabricante(String f)  { this.fabricante = f; }
}