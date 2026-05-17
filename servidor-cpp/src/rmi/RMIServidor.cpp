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
#include "../include/utils/SocketUtils.hpp"
#include <iostream>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
using namespace std;

void RMIServidor::iniciar(int porta)
{
    // cria o socket uma vez, (bind e listen encapsulados)
    int servidorSocket = SocketUtils::criarSocketServidor(porta);
    cout << "aguardando conexoes na porta " << porta << "..." << endl;
    cout << "===============================================================================" << endl;

    Expedidor expedidor;
    while (true)
    {
        try
        {
            // getRequest: aceita conexao e recebe a requisicao
            int clienteSocket;
            json requestJson = SocketUtils::getRequest(servidorSocket, clienteSocket);
            cout << "request recebido: " << requestJson.dump() << endl;
            cout << "===============================================================================" << endl;
            

            // processa a requisicao e gera a resposta
            Request request = Request::fromJson(requestJson);
            Reply reply = expedidor.expedicao(request);

            // sendReply: envia a resposta de volta para o cliente e fecha a conexao
            SocketUtils::sendReply(clienteSocket, reply.toJson());
            cout << "reply enviado: " << reply.toJson().dump() << endl;
            cout << "===============================================================================" << endl;

        }
        catch(const std::exception& e)
        {
            std::cerr << 'erro ao processar requisicao: ' << e.what() << '\n';
            cout << "===============================================================================" << endl;
        }
        

        cout << "aguardando novas conexoes na porta " << porta << "..." << endl;
    }
}