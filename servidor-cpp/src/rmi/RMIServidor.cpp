/*
fluxo:
socket
bind
listen
accept
recv
json::parse
Dispatcher
send*/

#include "../include/rmi/RMIServidor.hpp"
#include "../include/rmi/Expedidor.hpp"
#include <iostream>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
using namespace std;

void RMIServidor::iniciar(int porta)
{
    int server_fd, new_socket;
    struct sockaddr_in address;
    int opt = 1;
    int addrlen = sizeof(address);
    char buffer[1024] = {0};

    // Criar socket
    if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0)
    {
        perror("socket failed");
        exit(EXIT_FAILURE);
    }

    // conf opcoes do socket
    if (setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR | SO_REUSEPORT, &opt, sizeof(opt)))
    {
        perror("setsockopt");
        exit(EXIT_FAILURE);
    }

    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(porta);

    // bind
    if (bind(server_fd, (struct sockaddr *)&address, sizeof(address)) < 0)
    {
        perror("bind failed");
        exit(EXIT_FAILURE);
    }

    // listen
    if (listen(server_fd, 3) < 0)
    {
        perror("listen");
        exit(EXIT_FAILURE);
    }

    Expedidor expedidor;
    while (true)
    {
        cout << "Aguardando conexoes na porta " << porta << "..." << endl;

        // aceitacao de conexao
        if ((new_socket = accept(server_fd, (struct sockaddr *)&address, (socklen_t *)&addrlen)) < 0)
        {
            perror("accept");
            exit(EXIT_FAILURE);
        }

        cout << "conexao aceita!" << endl;

        // receber dados
        int valread = recv(new_socket, buffer, 1024, 0);
        if (valread < 0)
        {
            perror("recv");
            close(new_socket);
            continue;
        }

        string requestStr(buffer, valread);
        cout << "Request recebido: " << requestStr << endl;

        // processar a requisicao
        json requestJson = json::parse(requestStr);
        Request request = Request::fromJson(requestJson);
        Reply reply = expedidor.expedicao(request);

        // envia resposta
        string replyStr = reply.toJson().dump();
        send(new_socket, replyStr.c_str(), replyStr.size(), 0);
        cout << "Reply enviado: " << replyStr << endl;
        close(new_socket);
    }
}