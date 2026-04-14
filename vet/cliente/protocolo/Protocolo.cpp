#include "Protocolo.hpp"
#include <sys/socket.h>

void writeInt(int sock, int value){
    unsigned char bytes[4];
    bytes[0] = (value >> 24) & 0xFF;
    bytes[1] = (value >> 16) & 0xFF;
    bytes[2] = (value >> 8) & 0xFF;
    bytes[3] = value & 0xFF;
    send(sock, bytes, 4, 0);
}

int readInt(int sock){
    unsigned char bytes[4];
    recv(sock, bytes, 4, MSG_WAITALL);
    return (bytes[0] << 24) | (bytes[1] << 16) | (bytes[2] << 8) | bytes[3];
}

void Protocolo::enviarMensagem(int sock, int operacao, const string& payload) {
    unsigned char op = operacao;
    send(sock, &op, 1, 0);      // 1 bytes
    writeInt(sock, payload.size());     // 4 bytes
    send(sock, payload.c_str(), payload.size(), 0);
}

string Protocolo::receberResposta(int sock, int& operacao) {
    unsigned char op;
    recv(sock, &op, 1, MSG_WAITALL); // 1 byte
    operacao = op;

    int tamanho = readInt(sock); // 4 bytes

    string buffer(tamanho, '\0');
    recv(sock, buffer.data(), tamanho, MSG_WAITALL);
        
    return buffer;
}