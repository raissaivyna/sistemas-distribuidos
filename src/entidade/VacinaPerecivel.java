package entidade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * VacinaPerecivel — entidade (3 de 4)
 * Extensão ("é-um") de ProdutoVeterinario.
 * Implementa interface Perecivel.
 */
public class VacinaPerecivel extends ProdutoVeterinario implements Perecivel {

    private String dataValidade;           // dd/MM/yyyy
    private String requisitoArmazenamento;
    private double temperaturaMinima;
    private double temperaturaMaxima;

    public VacinaPerecivel() {}

    public VacinaPerecivel(int id, String nome, double preco, String fabricante,
                            String registroMapa, String especieAlvo,
                            String viaAdministracao, String dataValidade,
                            String requisitoArmazenamento,
                            double temperaturaMinima, double temperaturaMaxima) {
        super(id, nome, preco, fabricante, registroMapa, especieAlvo, viaAdministracao);
        this.dataValidade           = dataValidade;
        this.requisitoArmazenamento = requisitoArmazenamento;
        this.temperaturaMinima      = temperaturaMinima;
        this.temperaturaMaxima      = temperaturaMaxima;
    }

    @Override public String  getDataValidade()          { return dataValidade; }
    public void              setDataValidade(String d)  { this.dataValidade = d; }

    @Override
    public boolean isVencido() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.now().isAfter(LocalDate.parse(dataValidade, fmt));
    }

    @Override public String  getRequisitoArmazenamento()      { return requisitoArmazenamento; }
    public void              setRequisitoArmazenamento(String r) { this.requisitoArmazenamento = r; }

    public double getTemperaturaMinima()               { return temperaturaMinima; }
    public void   setTemperaturaMinima(double t)       { this.temperaturaMinima = t; }

    public double getTemperaturaMaxima()               { return temperaturaMaxima; }
    public void   setTemperaturaMaxima(double t)       { this.temperaturaMaxima = t; }

    @Override
    public String toString() {
        return "VacinaPerecivel{id=" + getId() + ", nome='" + getNome() +
               "', validade='" + dataValidade +
               "', vencida=" + isVencido() +
               "', armazenamento='" + requisitoArmazenamento + "'}";
    }
}