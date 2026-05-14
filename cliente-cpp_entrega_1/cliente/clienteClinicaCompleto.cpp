#include <iostream>
#include <thread>
#include <chrono>
#include <string>

#include "Protocolo.hpp"

#ifdef _WIN32
#include <winsock2.h>
#else
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>
#endif

using namespace std;

void escutarMulticast()
{
    int sock = socket(AF_INET, SOCK_DGRAM, 0);

    int reuse = 1;
    setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &reuse, sizeof(reuse));

    sockaddr_in addr{};
    addr.sin_family = AF_INET;
    addr.sin_port = htons(7899);
    addr.sin_addr.s_addr = INADDR_ANY;

    bind(sock, (sockaddr *)&addr, sizeof(addr));

    ip_mreq group{};
    group.imr_multiaddr.s_addr = inet_addr("230.1.1.1");
    group.imr_interface.s_addr = INADDR_ANY;

    setsockopt(sock, IPPROTO_IP, IP_ADD_MEMBERSHIP, &group, sizeof(group));

    cout << "[RECEPTOR] ouvindo multicast...\n";

    char buffer[1024];

    while (true)
    {
        int len = recv(sock, buffer, sizeof(buffer) - 1, 0);
        if (len > 0)
        {
            buffer[len] = '\0';
            cout << "\n[MULTICAST] " << buffer << "\n";
        }
    }
}

void chamar(int sock, int op, string payload)
{
    cout << "enviando...\n";
    Protocolo::enviarMensagem(sock, op, payload);

    cout << "esperando resposta...\n";
    int opResp;
    string resp = Protocolo::receberResposta(sock, opResp);

    if (opResp == 10)
    {
        cout << "[OK] " << resp << "\n\n";
    }
    else
    {
        cout << "[ERRO] " << resp << "\n\n";
    }
}

int main()
{

    thread t(escutarMulticast);
    t.detach();

    this_thread::sleep_for(chrono::milliseconds(300));

    int sock = socket(AF_INET, SOCK_STREAM, 0);

    sockaddr_in server{};
    server.sin_family = AF_INET;
    server.sin_port = htons(7896);

    cout << "tentando conectar...\n";

    if (inet_pton(AF_INET, "10.10.239.205", &server.sin_addr) <= 0)
    {
        cout << "erro no inet_pton\n";
        return 1;
    }

    if (connect(sock, (sockaddr *)&server, sizeof(server)) < 0)
    {
        perror("connect");
        return 1;
    }

    cout << "Conectado no servidor\n\n";

    // 1 - listar produtos
    cout << "=== LISTAR PRODUTOS ===\n";
    chamar(sock, 1, "");

    // 2 - buscar especie
    cout << "=== BUSCAR CANINO ===\n";
    chamar(sock, 3, "Canino");

    // 3 - vencidos (gera multicast)
    cout << "=== VENCIDOS ===\n";
    chamar(sock, 6, "");

    this_thread::sleep_for(chrono::milliseconds(300));

    // 4 - alerta recall
    cout << "=== RECALL ===\n";
    chamar(sock, 20, "RECALL|lote contaminado");

    // 5 - promocao
    cout << "=== PROMOCAO ===\n";
    chamar(sock, 20, "PROMOCAO|20% desconto hoje");

    // 6 - relatorio
    cout << "=== RELATORIO ===\n";
    chamar(sock, 7, "");

    cout << "\nfim do cliente\n";

#ifdef _WIN32
    closesocket(sock);
#else
    close(sock);
#endif

    return 0;
}