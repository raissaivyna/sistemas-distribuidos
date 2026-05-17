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

    int addlen = sizeof(sockaddr_in);
    SOCKET clientSocket = accept(serverSocket, (struct sockaddr*)&serverAddr, (socklen_t*)&addlen);
    if (clientSocket == INVALID_SOCKET) {
        cerr << "falha ao aceitar a conexao" << endl;
        closeSocket(serverSocket);
#ifdef _WIN32
        WSACleanup();
#endif
        return -1;
    }

    ostringstream bufferStream;

    VacinaPerecivel vacinas[1];
    vacinas[0].setId(1);
    vacinas[0].setNome("Vacina A");
    vacinas[0].setPreco(10.5);
    vacinas[0].setDataValidade("10/10/2026");
    vacinas[0].setRequisitoArmazenamento("geladeira");
    vacinas[0].setTemperaturaMinima(2.0);
    vacinas[0].setTemperaturaMaxima(8.0);

    VacinaPerecivelOutputStream output(vacinas, 1, bufferStream);
    output.enviar();

    string data = bufferStream.str();
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