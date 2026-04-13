#include <iostream>
#include <vector>
#include <cstring>
#include <sstream>
#include "VacinaPerecivelOutputStream.hpp"
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

    // criar objeto
    VacinaPerecivel vacinas[1] = {
        VacinaPerecivel(1, "Vacina A", 10.5, "Pfizer", "123", "humano", "oral",
        "virus", "A", 2, "10/10/2026", "geladeira", 2.0, 8.0)
    };

    // serializa
    ostringstream buffer(ios::binary);

    VacinaPerecivelOutputStream stream(vacinas, 1, buffer);
    stream.enviar();

    // pegar bytes
    string dados = buffer.str();

    // envia
    send(sock, dados.data(), dados.size(), 0);

    cout << "[CLIENTE] Dados enviados com sucesso!" << endl;

    // fecha

#ifdef _WIN32
    closesocket(sock);
    WSACleanup();
#else
    close(sock);
#endif

    return 0;
}
    