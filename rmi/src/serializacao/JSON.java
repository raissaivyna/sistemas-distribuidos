package serializacao;

import entidade.*;
import java.util.ArrayList;
import java.util.List;

public class JSON {

    public static byte[] toBytes(String json) {
        try { return json.getBytes("UTF-8"); } catch (Exception e) { return new byte[0]; }
    }

    public static String fromBytes(byte[] bytes) {
        try { return new String(bytes, "UTF-8"); } catch (Exception e) { return ""; }
    }

    // ── Serialização ─────────────────────────────────────────────────────────

    public static String serializar(Produto p) {
        if (p instanceof VacinaPerecivel)    return serializarVacina((VacinaPerecivel) p);
        if (p instanceof ProdutoVeterinario) return serializarVeterinario((ProdutoVeterinario) p);
        return serializarBase(p);
    }

    static String serializarBase(Produto p) {
        return obj(str("tipo", p.getClass().getSimpleName()),
                   num("id", p.getId()), str("nome", p.getNome()),
                   num("preco", p.getPreco()), str("fabricante", p.getFabricante()));
    }

    static String serializarVeterinario(ProdutoVeterinario p) {
        return obj(str("tipo", p.getClass().getSimpleName()),
                   num("id", p.getId()), str("nome", p.getNome()),
                   num("preco", p.getPreco()), str("fabricante", p.getFabricante()),
                   str("registroMapa", p.getRegistroMapa()),
                   str("especieAlvo", p.getEspecieAlvo()),
                   str("viaAdministracao", p.getViaAdministracao()));
    }

    static String serializarVacina(VacinaPerecivel v) {
        return obj(str("tipo", "VacinaPerecivel"),
                   num("id", v.getId()), str("nome", v.getNome()),
                   num("preco", v.getPreco()), str("fabricante", v.getFabricante()),
                   str("registroMapa", v.getRegistroMapa()),
                   str("especieAlvo", v.getEspecieAlvo()),
                   str("viaAdministracao", v.getViaAdministracao()),
                   str("dataValidade", v.getDataValidade()),
                   str("armazenamento", v.getRequisitoArmazenamento()),
                   num("tempMin", v.getTemperaturaMinima()),
                   num("tempMax", v.getTemperaturaMaxima()),
                   bool("vencida", v.isVencido()));
    }

    public static String serializarLista(List<? extends Produto> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            sb.append(serializar(lista.get(i)));
            if (i < lista.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }

    public static String serializarPedido(PedidoReposicao p) {
        return obj(num("id", p.getId()),
                   str("responsavel", p.getResponsavel()),
                   str("dataHora", p.getDataHora()),
                   num("valorTotal", p.getValorTotal()),
                   raw("itens", serializarLista(p.getItens())));
    }

    // ── Desserialização ──────────────────────────────────────────────────────

    public static Produto desserializarProduto(String json) {
        String tipo = get(json, "tipo");
        if ("VacinaPerecivel".equals(tipo))    return desserializarVacina(json);
        if ("ProdutoVeterinario".equals(tipo)) return desserializarVeterinario(json);
        return desserializarBase(json);
    }

    static Produto desserializarBase(String json) {
        return new Produto(getInt(json,"id"), get(json,"nome"),
                           getDbl(json,"preco"), get(json,"fabricante"));
    }

    static ProdutoVeterinario desserializarVeterinario(String json) {
        return new ProdutoVeterinario(getInt(json,"id"), get(json,"nome"),
                   getDbl(json,"preco"), get(json,"fabricante"),
                   get(json,"registroMapa"), get(json,"especieAlvo"),
                   get(json,"viaAdministracao"));
    }

    static VacinaPerecivel desserializarVacina(String json) {
        return new VacinaPerecivel(getInt(json,"id"), get(json,"nome"),
                   getDbl(json,"preco"), get(json,"fabricante"),
                   get(json,"registroMapa"), get(json,"especieAlvo"),
                   get(json,"viaAdministracao"), get(json,"dataValidade"),
                   get(json,"armazenamento"),
                   getDbl(json,"tempMin"), getDbl(json,"tempMax"));
    }

    public static List<Produto> desserializarLista(String json) {
        List<Produto> lista = new ArrayList<>();
        if (json == null || json.isBlank() || json.equals("[]")) return lista;
        String interior = json.trim();
        if (interior.startsWith("[")) interior = interior.substring(1);
        if (interior.endsWith("]"))   interior = interior.substring(0, interior.length()-1);
        int depth = 0, start = 0;
        for (int i = 0; i < interior.length(); i++) {
            char c = interior.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) {
                    String item = interior.substring(start, i+1).trim();
                    if (!item.isEmpty()) lista.add(desserializarProduto(item));
                    start = i + 2;
                }
            }
        }
        return lista;
    }

    // ── Helpers de construção ────────────────────────────────────────────────

    static String obj(String... campos) { return "{" + String.join(",", campos) + "}"; }
    static String str(String k, String v)  { return "\""+k+"\":\""+esc(v)+"\""; }
    static String num(String k, int v)     { return "\""+k+"\":"+v; }
    static String num(String k, double v)  { return "\""+k+"\":"+v; }
    static String bool(String k, boolean v){ return "\""+k+"\":"+v; }
    static String raw(String k, String v)  { return "\""+k+"\":"+v; }
    static String esc(String s)            { return s == null ? "" : s.replace("\"","\\\""); }

    // ── Helpers de leitura ───────────────────────────────────────────────────

    public static String get(String json, String chave) {
        if (json == null) return "";
        String busca = "\"" + chave + "\":";
        int ini = json.indexOf(busca);
        if (ini < 0) return "";
        ini += busca.length();
        if (ini >= json.length()) return "";
        char first = json.charAt(ini);
        if (first == '"') {
            int fim = ini + 1;
            while (fim < json.length() &&
                   !(json.charAt(fim) == '"' && json.charAt(fim-1) != '\\')) fim++;
            return json.substring(ini+1, fim);
        }
        if (first == '{' || first == '[') return "";
        int fim = ini;
        while (fim < json.length() && json.charAt(fim) != ',' && json.charAt(fim) != '}') fim++;
        return json.substring(ini, fim).trim();
    }

    public static int     getInt (String j, String k) { try { return Integer.parseInt(get(j,k).trim()); } catch(Exception e){ return 0; } }
    public static double  getDbl (String j, String k) { try { return Double.parseDouble(get(j,k).trim()); } catch(Exception e){ return 0.0; } }
    public static boolean getBool(String j, String k) { return "true".equalsIgnoreCase(get(j,k).trim()); }
}