package rmi;

import java.io.*;

/**
 * Message — empacotamento da mensagem requisição/resposta RMI.
 *
 * Segue o formato da seção 5.2 do livro (Coulouris):
 *
 *   ┌────────────┬──────────────┬──────────────┬──────────────┬──────────────┐
 *   │ messageType│ requestId    │ objectRef    │ methodId     │ arguments    │
 *   │  (1 byte)  │  (4 bytes)   │  (string)    │  (string)    │  (bytes[])   │
 *   └────────────┴──────────────┴──────────────┴──────────────┴──────────────┘
 *
 * messageType:
 *   0 = REQUEST  (cliente → servidor)
 *   1 = REPLY    (servidor → cliente)
 */
public class Message {

    public static final byte REQUEST = 0;
    public static final byte REPLY   = 1;

    private byte   messageType;   // REQUEST ou REPLY
    private int    requestId;     // identificador único da requisição
    private String objectRef;     // nome do objeto remoto (ex: "EstoqueServico")
    private String methodId;      // nome do método a invocar (ex: "listarProdutos")
    private byte[] arguments;     // argumentos serializados em JSON

    public Message() {}

    public Message(byte messageType, int requestId,
                   String objectRef, String methodId, byte[] arguments) {
        this.messageType = messageType;
        this.requestId   = requestId;
        this.objectRef   = objectRef;
        this.methodId    = methodId;
        this.arguments   = arguments == null ? new byte[0] : arguments;
    }

    // ── Serialização da mensagem inteira em bytes ────────────────────────────

    /**
     * Serializa esta mensagem em bytes para envio via DatagramSocket (UDP).
     *
     * Formato binário:
     *   [1] messageType
     *   [4] requestId
     *   [2+N] objectRef  (2 bytes tamanho + N bytes UTF-8)
     *   [2+N] methodId   (2 bytes tamanho + N bytes UTF-8)
     *   [4+N] arguments  (4 bytes tamanho + N bytes payload)
     */
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream      dos  = new DataOutputStream(baos);

        dos.writeByte(messageType);
        dos.writeInt(requestId);
        writeString(dos, objectRef == null ? "" : objectRef);
        writeString(dos, methodId  == null ? "" : methodId);

        byte[] args = arguments == null ? new byte[0] : arguments;
        dos.writeInt(args.length);
        dos.write(args);

        dos.flush();
        return baos.toByteArray();
    }

    /**
     * Desserializa uma Message a partir de bytes recebidos.
     */
    public static Message fromBytes(byte[] data) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

        Message msg = new Message();
        msg.messageType = dis.readByte();
        msg.requestId   = dis.readInt();
        msg.objectRef   = readString(dis);
        msg.methodId    = readString(dis);

        int argsLen  = dis.readInt();
        msg.arguments = dis.readNBytes(argsLen);

        return msg;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static void writeString(DataOutputStream dos, String s) throws IOException {
        byte[] bytes = s.getBytes("UTF-8");
        dos.writeShort(bytes.length);
        dos.write(bytes);
    }

    private static String readString(DataInputStream dis) throws IOException {
        int    len   = dis.readUnsignedShort();
        byte[] bytes = dis.readNBytes(len);
        return new String(bytes, "UTF-8");
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public byte   getMessageType()            { return messageType; }
    public void   setMessageType(byte t)      { this.messageType = t; }

    public int    getRequestId()              { return requestId; }
    public void   setRequestId(int id)        { this.requestId = id; }

    public String getObjectRef()              { return objectRef; }
    public void   setObjectRef(String o)      { this.objectRef = o; }

    public String getMethodId()               { return methodId; }
    public void   setMethodId(String m)       { this.methodId = m; }

    public byte[] getArguments()              { return arguments; }
    public void   setArguments(byte[] args)   { this.arguments = args; }

    /** Converte arguments para String UTF-8 (payload JSON). */
    public String getArgumentsAsString() {
        if (arguments == null || arguments.length == 0) return "";
        try { return new String(arguments, "UTF-8"); }
        catch (Exception e) { return ""; }
    }

    @Override
    public String toString() {
        String tipo = messageType == REQUEST ? "REQUEST" : "REPLY";
        return "Message{type=" + tipo + ", reqId=" + requestId +
               ", obj='" + objectRef + "', method='" + methodId +
               "', argsLen=" + (arguments == null ? 0 : arguments.length) + "}";
    }
}