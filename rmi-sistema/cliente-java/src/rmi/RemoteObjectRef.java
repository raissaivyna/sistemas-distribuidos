package rmi;

/**
 * RemoteObjectRef — referência a um objeto remoto.
 *
 * Identifica unicamente um objeto no servidor pelo seu nome
 * e o endereço/porta onde está registrado.
 *
 * Equivalente ao "Remote Object Reference" da seção 5.2 do livro.
 */
public class RemoteObjectRef {

    private final String host;          // endereço do servidor
    private final int    porta;         // porta do servidor
    private final String nomeObjeto;    // nome lógico do serviço remoto

    public RemoteObjectRef(String host, int porta, String nomeObjeto) {
        this.host        = host;
        this.porta       = porta;
        this.nomeObjeto  = nomeObjeto;
    }

    public String getHost()       { return host; }
    public int    getPorta()      { return porta; }
    public String getNomeObjeto() { return nomeObjeto; }

    @Override
    public String toString() {
        return "RemoteObjectRef{" + host + ":" + porta + "/" + nomeObjeto + "}";
    }
}