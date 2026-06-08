import assinante.AssinanteVeterinario;
import broker.Broker;
import evento.Evento.Topico;
import publicador.PublicadorClinica;

/**
 * DemoT4 — demonstração do Trabalho 4: Publish-Subscribe.
 *
 * Demonstra:
 *   ✅ Desacoplamento espacial  — publicador não conhece assinantes
 *   ✅ Desacoplamento temporal  — mensagens retidas quando assinante está offline
 *   ✅ Múltiplos assinantes     — mesmo evento entregue a vários
 *   ✅ Filtro por tópico        — cada assinante recebe só o que quer
 *
 * Executar: java -cp out DemoT4
 */
public class DemoT4 {

    public static void main(String[] args) throws InterruptedException {

        sep("TRABALHO 4 — Publish-Subscribe: Clínica Veterinária");

        Broker broker = Broker.getInstance();

        // ── Cria publicador e assinantes ─────────────────────────────────────
        PublicadorClinica servidor = new PublicadorClinica("ServidorClinica");

        AssinanteVeterinario medico    = new AssinanteVeterinario("Dr. Joao");
        AssinanteVeterinario tecnico   = new AssinanteVeterinario("Tecnico");
        AssinanteVeterinario estoquista = new AssinanteVeterinario("Estoquista");

        // ── Assinaturas ───────────────────────────────────────────────────────
        sep("1 — Registrando assinaturas");

        medico.assinar(Topico.VENCIMENTO);
        medico.assinar(Topico.RECALL);

        tecnico.assinar(Topico.REPOSICAO);
        tecnico.assinar(Topico.CADASTRO);
        tecnico.assinar(Topico.REMOCAO);

        estoquista.assinar(Topico.VENCIMENTO);
        estoquista.assinar(Topico.REPOSICAO);
        estoquista.assinar(Topico.CADASTRO);

        Thread.sleep(200);

        // ── Teste 1: Publicações normais ──────────────────────────────────────
        sep("2 — Publicando eventos (todos online)");

        servidor.publicarCadastro("Vacina Leishmaniose", 5);
        Thread.sleep(100);

        servidor.publicarVencimento("Vacina Anti-Rabica", "31/12/2025");
        Thread.sleep(100);

        servidor.publicarReposicao("Amoxicilina 500mg", 10);
        Thread.sleep(100);

        servidor.publicarRecall("Vacina Polivalente V10", "LOTE-2024-BR");
        Thread.sleep(200);

        // ── Teste 2: Desacoplamento temporal ──────────────────────────────────
        sep("3 — Desacoplamento Temporal: Tecnico fica OFFLINE");
        System.out.println("Publicador CONTINUA funcionando normalmente!\n");

        tecnico.ficarOffline();
        Thread.sleep(100);

        // Publica enquanto tecnico está offline
        servidor.publicarCadastro("Vacina Febre Aftosa Bovina", 6);
        Thread.sleep(100);
        servidor.publicarRemocao("Produto Descontinuado", 99);
        Thread.sleep(100);
        servidor.publicarReposicao("Ivermectina 1%", 5);
        Thread.sleep(100);

        System.out.println("\n[INFO] Tecnico tem " +
            broker.contarRetidos(Topico.CADASTRO, "Tecnico") + " evento(s) retido(s) em CADASTRO");
        System.out.println("[INFO] Tecnico tem " +
            broker.contarRetidos(Topico.REMOCAO, "Tecnico") + " evento(s) retido(s) em REMOCAO");
        System.out.println("[INFO] Tecnico tem " +
            broker.contarRetidos(Topico.REPOSICAO, "Tecnico") + " evento(s) retido(s) em REPOSICAO\n");

        // Tecnico volta online — recebe eventos retidos
        sep("4 — Tecnico volta ONLINE — recebe eventos retidos");
        tecnico.voltarOnline();
        Thread.sleep(500);

        // ── Teste 3: Desacoplamento espacial ──────────────────────────────────
        sep("5 — Desacoplamento Espacial");
        System.out.println("Publicador não sabe quem recebe — publica e esquece:");
        servidor.publicarRecall("Vacina Brucelose", "LOTE-B-2024");
        Thread.sleep(200);

        // ── Resumo ────────────────────────────────────────────────────────────
        sep("6 — Resumo final");
        System.out.printf("  %-20s recebeu %d evento(s)%n",
            medico.getNome(),     medico.getTotalRecebidos());
        System.out.printf("  %-20s recebeu %d evento(s)%n",
            tecnico.getNome(),    tecnico.getTotalRecebidos());
        System.out.printf("  %-20s recebeu %d evento(s)%n",
            estoquista.getNome(), estoquista.getTotalRecebidos());

        System.out.println("\n✅ Desacoplamento espacial:  publicador nunca conheceu os assinantes");
        System.out.println("✅ Desacoplamento temporal:  Tecnico recebeu eventos de quando estava offline");
        System.out.println("✅ Filtro por tópico:        cada assinante recebeu só seus tópicos");

        System.exit(0);
    }

    static void sep(String t) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  " + t);
        System.out.println("=".repeat(60));
    }
}