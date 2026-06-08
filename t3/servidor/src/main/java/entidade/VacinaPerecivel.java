package entidade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VacinaPerecivel extends ProdutoVeterinario {

    private String dataValidade;
    private String armazenamento;
    private double temperaturaMinima;
    private double temperaturaMaxima;

    public VacinaPerecivel() {}

    public VacinaPerecivel(int id, String nome, double preco, String fabricante,
                            String registroMapa, String especie, String via,
                            String dataValidade, String armazenamento,
                            double tempMin, double tempMax) {
        super(id, nome, preco, fabricante, registroMapa, especie, via);
        this.dataValidade    = dataValidade;
        this.armazenamento   = armazenamento;
        this.temperaturaMinima = tempMin;
        this.temperaturaMaxima = tempMax;
    }

    public boolean isVencida() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.now().isAfter(LocalDate.parse(dataValidade, fmt));
    }

    public String getDataValidade()               { return dataValidade; }
    public void   setDataValidade(String d)        { this.dataValidade = d; }
    public String getArmazenamento()              { return armazenamento; }
    public void   setArmazenamento(String a)       { this.armazenamento = a; }
    public double getTemperaturaMinima()           { return temperaturaMinima; }
    public void   setTemperaturaMinima(double t)   { this.temperaturaMinima = t; }
    public double getTemperaturaMaxima()           { return temperaturaMaxima; }
    public void   setTemperaturaMaxima(double t)   { this.temperaturaMaxima = t; }
}