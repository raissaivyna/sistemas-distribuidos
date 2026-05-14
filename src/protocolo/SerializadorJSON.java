package protocolo;

import pojo.*;
import java.util.List;

/**
 * SerializadorJSON — Representacao Externa de Dados (Fase 5)
 *
 * Converte objetos do dominio veterinario para JSON (serializacao)
 * e reconstroi objetos a partir de JSON (desserializacao).
 *
 * Usado em chamadas remotas: o cliente serializa o objeto, envia
 * como payload do Protocolo, o servidor desserializa e processa.
 *
 * Suporta: Produto, ProdutoVeterinario, ProdutoQuimioterapico,
 *          VacinaPerecivel, VacinaNaoPerecivel, Estoque
 */
public class SerializadorJSON {

    // ══════════════════════════════════════════════════════════════════════════
    // SERIALIZACAO — Objeto → JSON
    // ══════════════════════════════════════════════════════════════════════════

    public static String serializar(Produto p) {
        if (p instanceof VacinaPerecivel v)     return serializarVacinaPerecivel(v);
        if (p instanceof VacinaNaoPerecivel v)  return serializarVacinaNaoPerecivel(v);
        if (p instanceof ProdutoQuimioterapico q) return serializarQuimioterapico(q);
        if (p instanceof ProdutoBiologico b)    return serializarBiologico(b);
        if (p instanceof ProdutoVeterinario pv) return serializarVeterinario(pv);
        return serializarBase(p);
    }

    static String serializarBase(Produto p) {
        return "{"
            + campo("tipo",       p.getClass().getSimpleName()) + ","
            + campo("id",         p.getId())                    + ","
            + campo("nome",       p.getNome())                  + ","
            + campo("preco",      p.getPreco())                 + ","
            + campo("fabricante", p.getFabricante())
            + "}";
    }

    static String serializarVeterinario(ProdutoVeterinario p) {
        return "{"
            + campo("tipo",             p.getClass().getSimpleName()) + ","
            + campo("id",               p.getId())                    + ","
            + campo("nome",             p.getNome())                  + ","
            + campo("preco",            p.getPreco())                 + ","
            + campo("fabricante",       p.getFabricante())            + ","
            + campo("registroMapa",     p.getRegistroMapa())          + ","
            + campo("especieAlvo",      p.getEspecieAlvo())           + ","
            + campo("viaAdministracao", p.getViaAdministracao())
            + "}";
    }

    static String serializarQuimioterapico(ProdutoQuimioterapico p) {
        return "{"
            + campo("tipo",              "ProdutoQuimioterapico") + ","
            + campo("id",                p.getId())               + ","
            + campo("nome",              p.getNome())             + ","
            + campo("preco",             p.getPreco())            + ","
            + campo("fabricante",        p.getFabricante())       + ","
            + campo("registroMapa",      p.getRegistroMapa())     + ","
            + campo("especieAlvo",       p.getEspecieAlvo())      + ","
            + campo("viaAdministracao",  p.getViaAdministracao()) + ","
            + campo("principioAtivo",    p.getPrincipioAtivo())   + ","
            + campo("concentracao",      p.getConcentracao())     + ","
            + campo("classeTerapeutica", p.getClasseTerapeutica())+ ","
            + campoBool("retencaoCarencia", p.isRetencaoCarencia())
            + "}";
    }

    static String serializarBiologico(ProdutoBiologico p) {
        return "{"
            + campo("tipo",             "ProdutoBiologico")      + ","
            + campo("id",               p.getId())               + ","
            + campo("nome",             p.getNome())             + ","
            + campo("preco",            p.getPreco())            + ","
            + campo("fabricante",       p.getFabricante())       + ","
            + campo("registroMapa",     p.getRegistroMapa())     + ","
            + campo("especieAlvo",      p.getEspecieAlvo())      + ","
            + campo("viaAdministracao", p.getViaAdministracao()) + ","
            + campo("tipoAgente",       p.getTipoAgente())       + ","
            + campo("sorotipo",         p.getSorotipo())         + ","
            + campo("numDoses",         p.getNumDoses())
            + "}";
    }

    static String serializarVacinaPerecivel(VacinaPerecivel p) {
        return "{"
            + campo("tipo",                   "VacinaPerecivel")            + ","
            + campo("id",                     p.getId())                    + ","
            + campo("nome",                   p.getNome())                  + ","
            + campo("preco",                  p.getPreco())                 + ","
            + campo("fabricante",             p.getFabricante())            + ","
            + campo("registroMapa",           p.getRegistroMapa())          + ","
            + campo("especieAlvo",            p.getEspecieAlvo())           + ","
            + campo("viaAdministracao",       p.getViaAdministracao())      + ","
            + campo("tipoAgente",             p.getTipoAgente())            + ","
            + campo("sorotipo",               p.getSorotipo())              + ","
            + campo("numDoses",               p.getNumDoses())              + ","
            + campo("dataValidade",           p.getDataValidade())          + ","
            + campo("armazenamento",          p.getRequisitoArmazenamento())+ ","
            + campo("temperaturaMinima",      p.getTemperaturaMinima())     + ","
            + campo("temperaturaMaxima",      p.getTemperaturaMaxima())     + ","
            + campoBool("vencida",            p.isVencido())
            + "}";
    }

    static String serializarVacinaNaoPerecivel(VacinaNaoPerecivel p) {
        return "{"
            + campo("tipo",               "VacinaNaoPerecivel")      + ","
            + campo("id",                 p.getId())                  + ","
            + campo("nome",               p.getNome())                + ","
            + campo("preco",              p.getPreco())               + ","
            + campo("fabricante",         p.getFabricante())          + ","
            + campo("registroMapa",       p.getRegistroMapa())        + ","
            + campo("especieAlvo",        p.getEspecieAlvo())         + ","
            + campo("viaAdministracao",   p.getViaAdministracao())    + ","
            + campo("tipoAgente",         p.getTipoAgente())          + ","
            + campo("sorotipo",           p.getSorotipo())            + ","
            + campo("numDoses",           p.getNumDoses())            + ","
            + campo("formaFarmaceutica",  p.getFormaFarmaceutica())   + ","
            + campo("prazoValidadeMeses", p.getPrazoValidadeMeses())  + ","
            + campo("temperaturaMax",     p.getTemperaturaMax())
            + "}";
    }

    /** Serializa Estoque com todos os seus produtos */
    public static String serializarEstoque(Estoque e) {
        StringBuilder sb = new StringBuilder();
        sb.append("{")
          .append(campo("id",    e.getId()))    .append(",")
          .append(campo("local", e.getLocal())).append(",")
          .append("\"produtos\":[");

        List<Produto> lista = e.getProdutos();
        for (int i = 0; i < lista.size(); i++) {
            sb.append(serializar(lista.get(i)));
            if (i < lista.size() - 1) sb.append(",");
        }
        sb.append("]}");
        return sb.toString();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // DESSERIALIZACAO — JSON → Objeto
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Reconstroi um Produto a partir de JSON.
     * O campo "tipo" determina a subclasse concreta a instanciar.
     */
    public static Produto desserializar(String json) {
        String tipo = get(json, "tipo");

        return switch (tipo) {
            case "VacinaPerecivel"      -> desserializarVacinaPerecivel(json);
            case "VacinaNaoPerecivel"   -> desserializarVacinaNaoPerecivel(json);
            case "ProdutoQuimioterapico"-> desserializarQuimioterapico(json);
            case "ProdutoBiologico"     -> desserializarBiologico(json);
            case "ProdutoVeterinario"   -> desserializarVeterinario(json);
            default                    -> desserializarBase(json);
        };
    }

    static Produto desserializarBase(String json) {
        return new Produto(
            getInt(json, "id"),
            get(json, "nome"),
            getDbl(json, "preco"),
            get(json, "fabricante")
        );
    }

    static ProdutoVeterinario desserializarVeterinario(String json) {
        return new ProdutoVeterinario(
            getInt(json, "id"),
            get(json, "nome"),
            getDbl(json, "preco"),
            get(json, "fabricante"),
            get(json, "registroMapa"),
            get(json, "especieAlvo"),
            get(json, "viaAdministracao")
        );
    }

    static ProdutoQuimioterapico desserializarQuimioterapico(String json) {
        return new ProdutoQuimioterapico(
            getInt(json, "id"),
            get(json, "nome"),
            getDbl(json, "preco"),
            get(json, "fabricante"),
            get(json, "registroMapa"),
            get(json, "especieAlvo"),
            get(json, "viaAdministracao"),
            get(json, "principioAtivo"),
            getDbl(json, "concentracao"),
            get(json, "classeTerapeutica"),
            getBool(json, "retencaoCarencia")
        );
    }

    static ProdutoBiologico desserializarBiologico(String json) {
        return new ProdutoBiologico(
            getInt(json, "id"),
            get(json, "nome"),
            getDbl(json, "preco"),
            get(json, "fabricante"),
            get(json, "registroMapa"),
            get(json, "especieAlvo"),
            get(json, "viaAdministracao"),
            get(json, "tipoAgente"),
            get(json, "sorotipo"),
            getInt(json, "numDoses")
        );
    }

    static VacinaPerecivel desserializarVacinaPerecivel(String json) {
        return new VacinaPerecivel(
            getInt(json, "id"),
            get(json, "nome"),
            getDbl(json, "preco"),
            get(json, "fabricante"),
            get(json, "registroMapa"),
            get(json, "especieAlvo"),
            get(json, "viaAdministracao"),
            get(json, "tipoAgente"),
            get(json, "sorotipo"),
            getInt(json, "numDoses"),
            get(json, "dataValidade"),
            get(json, "armazenamento"),
            getDbl(json, "temperaturaMinima"),
            getDbl(json, "temperaturaMaxima")
        );
    }

    static VacinaNaoPerecivel desserializarVacinaNaoPerecivel(String json) {
        return new VacinaNaoPerecivel(
            getInt(json, "id"),
            get(json, "nome"),
            getDbl(json, "preco"),
            get(json, "fabricante"),
            get(json, "registroMapa"),
            get(json, "especieAlvo"),
            get(json, "viaAdministracao"),
            get(json, "tipoAgente"),
            get(json, "sorotipo"),
            getInt(json, "numDoses"),
            get(json, "formaFarmaceutica"),
            getInt(json, "prazoValidadeMeses"),
            getDbl(json, "temperaturaMax")
        );
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Helpers de construcao JSON
    // ══════════════════════════════════════════════════════════════════════════

    static String campo(String chave, String valor) {
        return "\"" + chave + "\":\"" + (valor == null ? "" : valor) + "\"";
    }
    static String campo(String chave, int valor) {
        return "\"" + chave + "\":" + valor;
    }
    static String campo(String chave, double valor) {
        return "\"" + chave + "\":" + valor;
    }
    static String campoBool(String chave, boolean valor) {
        return "\"" + chave + "\":" + valor;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Helpers de leitura JSON
    // ══════════════════════════════════════════════════════════════════════════

    public static String get(String json, String chave) {
        String busca = "\"" + chave + "\":";
        int ini = json.indexOf(busca);
        if (ini < 0) return "";
        ini += busca.length();
        if (ini >= json.length()) return "";
        if (json.charAt(ini) == '"') {
            int fim = json.indexOf('"', ini + 1);
            return fim < 0 ? "" : json.substring(ini + 1, fim);
        }
        int fim1 = json.indexOf(',', ini);
        int fim2 = json.indexOf('}', ini);
        int fim  = (fim1 < 0) ? fim2 : (fim2 < 0) ? fim1 : Math.min(fim1, fim2);
        return fim < 0 ? json.substring(ini).trim() : json.substring(ini, fim).trim();
    }

    public static int    getInt (String json, String k) { try { return Integer.parseInt(get(json,k).trim()); } catch(Exception e){ return 0; } }
    public static double getDbl (String json, String k) { try { return Double.parseDouble(get(json,k).trim()); } catch(Exception e){ return 0.0; } }
    public static boolean getBool(String json, String k) { return "true".equalsIgnoreCase(get(json,k).trim()); }
}