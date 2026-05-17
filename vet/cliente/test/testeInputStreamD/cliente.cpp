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
#include "VacinaPerecivelInputStream.hpp"

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

    SOCKET clientSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (clientSocket == INVALID_SOCKET) {
        cerr << "falha ao criar o socket" << endl;

#ifdef _WIN32
        WSACleanup();
#endif
        return -1;

}
    sockaddr_in serverAddr;
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(8080);
    serverAddr.sin_addr.s_addr = inet_addr("127.0.0.1");


    if (connect(clientSocket, (struct sockaddr*)&serverAddr, sizeof(serverAddr)) == SOCKET_ERROR) {
        cerr << "falha ao conectar ao servidor" << endl;
        closeSocket(clientSocket);
#ifdef _WIN32
        WSACleanup();
#endif
        return -1;
    }

    cout << "conectado ao servidor" << endl;

    string buffer;
    char temp[1024];

    int bytes = recv(clientSocket, temp, sizeof(temp), 0);
    buffer.append(temp, bytes);
    
    
    istringstream is(buffer);

    VacinaPerecivelInputStream input(is);

    cout << "bytes recebidos: " << buffer.size() << endl;

    int quantidade;
    VacinaPerecivel* vacinas = input.ler(quantidade);
    for (int i = 0; i < quantidade; i++)
    {
        cout << vacinas[i].toString() << endl;
    }
    
    cout << "recebido " << quantidade << " vacinas do servidor" << endl;
    delete[] vacinas;

    closeSocket(clientSocket);
#ifdef _WIN32
    WSACleanup();
#endif
    return 0;
}
