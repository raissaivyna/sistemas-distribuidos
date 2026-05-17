#ifndef SOCKET_UTILS_HPP
#define SOCKET_UTILS_HPP

#pragma once

#include <iostream>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <stdexcept>
#include <arpa/inet.h>
#include <nlohmann/json.hpp>

using json = nlohmann::json;
using namespace std;

/*
socketUtils.hpp: utilitario para operacoes de socket, como criação, envio e recebimento de dados. Encapsula toda a comunicacao TCP/IP, incluindo a aceitacao de conexoes, leitura e escrita de dados, e o tratamento de erros relacionados a sockets. Facilita a implementação do servidor RMI, permitindo que o RMIServidor se concentre na lógica de negocios, enquanto o SocketUtils lida com os detalhes da comunicacao de rede.
*/

class SocketUtils
{

public:
    // cliente: envia a requisicao e recebe a resposta como json (byte[] doOperation(RemoteObjectRef o, int methodId, byte[] args))
    static json doOperation(const string&, int porta, const json& requisicao) {
        int sock = 0;
        sockaddr_in serv_addr{};
        char buffer[1024] = {0};

        if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0)
        {
            throw runtime_error("doOperation: erro ao criar socket");
        }

        serv_addr.sin_family = AF_INET;
        serv_addr.sin_port = htons(porta);

        if (inet_pton(AF_INET, "127.0.0.1", &serv_addr.sin_addr)<=0)
        {
            throw runtime_error("doOperation: erro ao converter endereco");
        }
        if (connect(sock, (sockaddr*)&serv_addr, sizeof(serv_addr)) < 0)
        {
            throw runtime_error("doOperation: erro ao conectar ao servidor");
        }
        
        string dados = requisicao.dump();
        send(sock, dados.c_str(), dados.size(), 0);
        int bytesRecebidos = recv(sock, buffer, 1024, 0);
        close(sock);

        if (bytesRecebidos < 0)
        {
            throw runtime_error("doOperation: erro ao receber resposta do servidor");
        }
        return json::parse(string(buffer, bytesRecebidos));
    }

    // servidor: aguarda e retorna uma requisicao de um cliente (byte[] getRequest()), retorna o socket do cliente para o RMIServidor ler os dados e enviar a resposta
    static json getRequest(int server_fd, int& client_socket) {
        struct sockaddr_in clienteEndereco{};
        socklen_t addrlen = sizeof(clienteEndereco);
        char buffer[1024] = {0};

        if ((client_socket = accept(server_fd, (sockaddr*)&clienteEndereco, &addrlen)) < 0)
        {
            throw runtime_error("getRequest: erro ao aceitar conexao");
        }

        int bytesRecebidos = recv(client_socket, buffer, 1024, 0);
        if (bytesRecebidos < 0)
        {
            close(client_socket);
            throw runtime_error("getRequest: erro ao receber dados do cliente");
        }
        return json::parse(string(buffer, bytesRecebidos));
    }

    // servidor : envia a resposta de volta para o cliente (void sendReply(byte[] reply, InetAddress clienteHost, int clientePort)))

    static void sendReply(int client_socket, const json& resposta) {
        string dados = resposta.dump();
        send(client_socket, dados.c_str(), dados.size(), 0);
        close(client_socket);
    }

    // servidor: cria o socket do servidor e o configura para ouvir na porta especificada (int createServerSocket(int porta))
    static int criarSocketServidor(int porta) {
        int server_fd;
        sockaddr_in address{};
        int opt = 1;

        if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0)
        {
            throw runtime_error("criarSocketServidor: erro ao criar socket");
        }

        setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR | SO_REUSEPORT, &opt, sizeof(opt));

        address.sin_family = AF_INET;
        address.sin_addr.s_addr = INADDR_ANY;
        address.sin_port = htons(porta);
        if (bind(server_fd, (sockaddr*)&address, sizeof(address)) < 0)
        {
            throw runtime_error("criarSocketServidor: erro ao bindar socket");
        }if (listen(server_fd, 5) < 0)
        {
            throw runtime_error("criarSocketServidor: erro ao ouvir na porta");
        }
        return server_fd;
    }
        
};



#endif // SOCKET_UTILS_HPP