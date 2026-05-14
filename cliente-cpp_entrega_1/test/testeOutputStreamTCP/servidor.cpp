#ifdef _WIN32
    #include <winsock2.h>
    #include <ws2tcpip.h>
    #pragma comment(lib, "ws2_32.lib")
#else
    #include <sys/socket.h>
    #include <netinet/in.h>
    #include <unistd.h>
#endif

#include <iostream>
#include <sstream>
#include "VacinaPerecivelOutputStream.hpp"

void closeSocket(int sock) {
#ifdef _WIN32
    closesocket(sock);
#else
    close(sock);
#endif
}

int main() {
#ifdef _WIN32
    WSADATA wsaData;
    if(WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) {
        cerr << "falha ao inicializar o Winsock" << endl;
        return -1;
    }
#endif

    SOCKET serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSocket == INVALID_SOCKET) {
        cerr << "falha ao criar o socket" << endl;
        
#ifdef _WIN32
        WSACleanup();
#endif
        return -1;        
    }

    sockaddr_in serverAddr;
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_addr.s_addr = INADDR_ANY;
    serverAddr.sin_port = htons(8080);

    if (bind(serverSocket, (struct sockaddr*)&serverAddr, sizeof(serverAddr)) == SOCKET_ERROR) {
        cerr << "falha ao bindar o socket" << endl;
        closeSocket(serverSocket);
#ifdef _WIN32
        WSACleanup();
#endif
        return -1;
    }

    if (listen(serverSocket, 1) == SOCKET_ERROR) {
        cerr << "falha ao escutar o socket" << endl;
        closeSocket(serverSocket);
#ifdef _WIN32
        WSACleanup();
#endif
        return -1;
    }
    cout << "servidor aguardando conexao na porta 8080..." << endl;

    SOCKET clientSocket = accept(serverSocket, nullptr, nullptr);
    cout << "cliente conectado!" << endl;
    if (clientSocket == INVALID_SOCKET) {
        cerr << "falha ao aceitar a conexao" << endl;
        closeSocket(serverSocket);
#ifdef _WIN32
        WSACleanup();
#endif
        return -1;
    }

        VacinaPerecivel vacinas[2] = {
        VacinaPerecivel(1, "Vacina A", 10.5, "Pfizer", "123", "humano", "oral",
        "virus", "A", 2, "10/10/2026", "geladeira", 2.0, 8.0),

        VacinaPerecivel(2, "Vacina B", 20.0, "Butantan", "456", "animal", "injeção",
        "bacteria", "B", 1, "05/05/2025", "freezer", -5.0, 2.0)
    };

    ostringstream bufferStream;

    VacinaPerecivelOutputStream output(vacinas, 2, bufferStream);
    output.enviar();

    string data = bufferStream.str();

    cout << "tamanho dos dados a serem enviados: " << data.size() << " bytes" << endl;

    int total = 0;
    int length = data.size();
    while (total < length)
    {
        cout << "bytes enviados: " << data.size() << endl;
        int sent = send(clientSocket, data.c_str() + total, length - total, 0);
        if (sent == SOCKET_ERROR)
        {
            cerr << "erro ao enviar dados" << endl;
            break;
        }
        total += sent;
    }
    

#ifdef _WIN32
    shutdown(clientSocket, SD_SEND);
#else
    shutdown(clientSocket, SHUT_WR);
#endif

    closeSocket(clientSocket);
    closeSocket(serverSocket);

#ifdef _WIN32
    WSACleanup();
#endif
    return 0;
}