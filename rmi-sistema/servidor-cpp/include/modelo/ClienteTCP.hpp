#ifndef CLIENTE_TCP_HPP
#define CLIENTE_TCP_HPP

#include <string>

#ifdef _WIN32
    #include <winsock2.h>
    #include <ws2tcpip.h>
    typedef SOCKET socket_t;
#else
    #include <sys/socket.h>
    #include <arpa/inet.h>
    #include <unistd.h>
    typedef int socket_t;
#endif

class ClienteTCP
{
private:
    socket_t sock;
public:
    ClienteTCP();
    bool conectar(const std::string& ip, int porta);
    bool enviar(const std::string& dados);
    void fechar();

};


#endif