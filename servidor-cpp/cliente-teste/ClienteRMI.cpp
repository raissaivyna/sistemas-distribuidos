#include <iostream>
#include <string>
#include <unistd.h>
#include <arpa/inet.h>

#include <nlohmann/json.hpp>

using json = nlohmann::json;
using namespace std;

int main(){
    /*
    criacao do socket tcp

    socket() cria um socket e retorna um descritor de arquivo para ele. 
    O socket é criado com o domínio AF_INET (IPv4), 
    o tipo SOCK_STREAM (TCP) 
    e o protocolo 0 (que escolhe automaticamente o protocolo TCP).
    */

    int clienteSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (clienteSocket < 0) {
        cerr << "Erro ao criar socket" << endl;
        return 1;
    }

    // configuracao do endereco do servidor
    sockaddr_in servidorEndereco;
    servidorEndereco.sin_family = AF_INET;
    servidorEndereco.sin_port = htons(8080); // porta do servidor
    inet_pton(AF_INET, "127.0.0.1", &servidorEndereco.sin_addr);

    // conexao ao servidor
    if (connect(clienteSocket, (sockaddr*)&servidorEndereco, sizeof(servidorEndereco)) < 0) {
        cerr << "Erro ao conectar ao servidor" << endl;
        return 1;
    }
    cout << "conectado ao servidor RMI" << endl;

    // montagem da requisicao rmi
    // bucarPorId(1)

    json requestJson = {
        {"TipoMensagem", "Request"},
        {"requestId", 1},
        {"referenciaObj", "ProdutoService"},
        {"metodoId", "buscarPorId"},
        {"parametros", {
            {"id", 1}
        }}
    };

    // serializacao da requisicao
    // dump() converte o objeto json em string
    string requestStr = requestJson.dump();

    // envio ao servidor
    send(clienteSocket, requestStr.c_str(), requestStr.size(), 0);
    cout << "request enviado: " << requestStr << endl;

    // recebimento da resposta
    char buffer[1024] = {0};
    int bytesRecebidos = recv(clienteSocket, buffer, 1024, 0);
    if (bytesRecebidos < 0) {
        cerr << "Erro ao receber resposta do servidor" << endl;
        return 1;
    }
    // transformar a resposta em string e exibe
    string respostaStr(buffer);
    // parse do json recebido
    json respostaJson = json::parse(respostaStr);
    cout << "Resposta recebida: " << respostaJson.dump(4) << endl;

    // fechamento do socket
    close(clienteSocket);
    return 0;
}