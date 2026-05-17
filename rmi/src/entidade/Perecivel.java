package entidade;

/**
 * Perecivel — interface para produtos com validade.
 */
public interface Perecivel {
    String  getDataValidade();
    boolean isVencido();
    String  getRequisitoArmazenamento();
}