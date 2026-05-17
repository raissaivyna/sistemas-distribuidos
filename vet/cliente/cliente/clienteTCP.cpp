#include <iostream>
#include <vector>
#include <cstring>
#include <sstream>
#include "Protocolo.hpp"
// g++ cliente/clienteTCP.cpp modelo/*.cpp stream/*.cpp -Iinclude -o cliente_tcp.exe -lws2_32

#ifdef _WIN32
    #include <winsock2.h>
    #pragma comment(lib, "ws2_32.lib")
#else
    #include <sys/socket.h>
    #include <arpa/inet.h>
    #include <unistd.h>
#endif

using namespace std;


int main() {
    #ifdef _WIN32
        WSADATA wsaData;
        WSAStartup(MAKEWORD(2, 2), &wsaData);
    #endif

    int sock = socket(AF_INET, SOCK_STREAM, 0);

    sockaddr_in serverAddr;
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(7896);
    serverAddr.sin_addr.s_addr = inet_addr("127.0.0.1");

    if (connect(sock, (sockaddr*)&serverAddr, sizeof(serverAddr)) < 0)
    {
        cout << "[CLIENTE] Erro ao conectar ao servidor" << endl;
        return 1;
    }
    cout << "[CLIENTE] Conectado ao servidor" << endl;

    Protocolo::enviarMensagem(sock, 1, "");

    int op;
    string resposta = Protocolo::receberResposta(sock, op);
    cout << "Resposta do servidor (op " << op << "): " << resposta << endl;

    // fecha
shutdown(sock, SHUT_WR);
#ifdef _WIN32
    closesocket(sock);
    WSACleanup();
#else
    close(sock);
#endif

    return 0;
}
    