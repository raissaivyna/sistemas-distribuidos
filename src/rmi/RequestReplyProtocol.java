package rmi;

import java.io.*;
import java.net.Socket;

public class RequestReplyProtocol {
//2----
/* Referência ao objeto remoto é basicamente a pergunta : " onde está o objeto que quero chamar?"
* Antes de qualquer coisa, o cliente precisa saber onde está o servidor.
analogia ao e-mail. 
//3----
cat src/rmi/RemoteObjectRef.java

Identifica um único objeto no servidor pelo seu nome e endereço/porta onde está registrado.

*/

/*5--- Cliente faz uma chamada remota -> 
E oq acontece?
-Abre uma conexão TCP com o servidor (usando host e porta do RemoteObjectRef)
-Envia a mensagem JSON numa linha (out.println)
-Bloqueia esperando a resposta — o cliente fica parado aqui até o servidor responder
-Lê a resposta numa linha (in.readLine)
-Fecha o socket automaticamente (try-with-resources)
-Devolve os bytes da resposta para quem chamou

TCP foi escolhido como transporte porque é confiável: garante que a mensagem
chega completa e na ordem certa. UDP seria mais rápido mas poderia perder pacotes.
*/

/* e pq byte[]? */
    public byte[] doOperation(RemoteObjectRef ref,
                               String methodId,
                               byte[] arguments) throws IOException {

        String requestJson = new String(arguments, "UTF-8");

        try (Socket        socket = new Socket(ref.getHost(), ref.getPorta());
             PrintWriter   out    = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in    = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()))) {

            out.println(requestJson);

            String replyJson = in.readLine();
            if (replyJson == null) replyJson = "{}";

            return replyJson.getBytes("UTF-8");
        }
    }

    public static String getRequest(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
        String linha = in.readLine();
        return linha == null ? "{}" : linha;
    }

    public static void sendReply(Socket socket, String replyJson) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println(replyJson);
        out.flush();
        socket.close();
    }
}