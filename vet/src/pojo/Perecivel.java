package pojo;

/**
 * Interface Perecivel
 * Representa produtos que podem estragar/vencer.
 * Implementada por: VacinaPerecivel
 */
public interface Perecivel {

    /** Retorna a data de validade do produto (formato dd/MM/yyyy) */
    String getDataValidade();

    /** Retorna true se o produto está vencido */
    boolean isVencido();

    /** Retorna os requisitos de armazenamento (ex: "Refrigerado 2-8°C") */
    String getRequisitoArmazenamento();
}