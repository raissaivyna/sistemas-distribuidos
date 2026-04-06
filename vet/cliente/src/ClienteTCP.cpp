#include "ClienteTCP.hpp"
#include <iostream>
#include <cstring>

#ifdef _WIN32
    #pragma comment(lib, "ws2_32.lib")
#endif

ClienteTCP::ClienteTCP(){
    #ifdef _WIN32
        WSADATA wsa;
        WSAStartup(MAKEWORD(2,2), &wsa);
    #endif
    sock = -1;
}

bool ClienteTCP::conectar(const std::string& ip, int porta){


    sock = socket(AF_INET, SOCK_STREAM, 0);
    if(sock < 0) return false;

    sockaddr_in server;
    server.sin_family = AF_INET;
    server.sin_port = htons(porta);

    #ifdef _WIN32
        server.sin_addr.s_addr = inet_addr(ip.c_str());
    #else
        if(inet_pton(AF_INET, ip.c_str(), &serv.sin_addr) <= 0)
            return false;
    #endif

    if(connect(sock, (sockaddr*)&server, sizeof(server)) < 0) 
        return false;
    
    return true;
}

bool ClienteTCP::enviar(const std::string& dados){
    if(sock < 0) return false;

    send(sock, dados.c_str(), dados.size(), 0);

    return true;
}



void ClienteTCP::fechar(){
    #ifdef _WIN32
        closesocket(sock);
        WSACleanup();
    #else
        close(sock);
    #endif
}