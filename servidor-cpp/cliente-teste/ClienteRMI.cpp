#include <iostream>

#include "../include/utils/SocketUtils.hpp"
#include <nlohmann/json.hpp>

using json = nlohmann::json;
using namespace std;

int main(){
    /*
    cliente rmi de teste
    usa sockeUtils::doOperation
    doOperation encapsula: criacao do socket, conexao ao servidor, envio da requisicao e recebimento da resposta
    */

    const string enderecoServidor = "127.0.0.1";
    const int portaServidor = 8080;
    cout << "========== Cliente RMI Teste ==========" << endl;

    try
    {
        // teste 1: buscarPorId
        cout << "\n[1] chamando buscarPorId(1)..." << endl;

        json requestJson = {
            {"TipoMensagem", "Request"},
            {"requestId", 1},
            {"referenciaObj", "ProdutoService"},
            {"metodoId", "buscarPorId"},
            {"parametros", {
                {"id", 1}
            }}
        };

        // doOperation: envia a requisicao e recebe a resposta como json (byte[] doOperation(RemoteObjectRef o, int methodId, byte[] args))
        json respostaJson = SocketUtils::doOperation(enderecoServidor, portaServidor, requestJson);
        cout << "Resposta recebida: " << respostaJson.dump(4) << endl;

        // teste 2: listarTodos
        cout << "\n[2] chamando listarTodos()..." << endl;
        json requestJson2 = {
            {"TipoMensagem", "Request"},
            {"requestId", 2},
            {"referenciaObj", "ProdutoService"},
            {"metodoId", "listarTodos"},
            {"parametros", {}}
        };
        json respostaJson2 = SocketUtils::doOperation(enderecoServidor, portaServidor, requestJson2);
        cout << "Resposta recebida: " << respostaJson2.dump(4) << endl;

        // teste 3: buscarPorEspecie
        cout << "\n[3] chamando buscarPorEspecie('Cachorro')..." << endl;
        json requestJson3 = {
            {"TipoMensagem", "Request"},
            {"requestId", 3},
            {"referenciaObj", "ProdutoService"},
            {"metodoId", "buscarPorEspecie"},
            {"parametros", {
                {"especie", "Cachorro"}
            }}
        };
        json respostaJson3 = SocketUtils::doOperation(enderecoServidor, portaServidor, requestJson3);
        cout << "Resposta recebida: " << respostaJson3.dump(4) << endl;

        //teste 4: calcularValorTotal
        cout << "\n[4] chamando calcularValorTotal()..." << endl;
        json requestJson4 = {
            {"TipoMensagem", "Request"},
            {"requestId", 4},
            {"referenciaObj", "ProdutoService"},
            {"metodoId", "calcularValorTotal"},
            {"parametros", nullptr}
        };
        json respostaJson4 = SocketUtils::doOperation(enderecoServidor, portaServidor, requestJson4);
        cout << "Resposta recebida: " << respostaJson4.dump(4) << endl;

        // teste 5: remover
        cout << "\n[5] chamando remover(2)..." << endl;
        json requestJson5 = {
            {"TipoMensagem", "Request"},
            {"requestId", 5},
            {"referenciaObj", "ProdutoService"},
            {"metodoId", "remover"},
            {"parametros", {
                {"id", 2}
            }}
        };
        json respostaJson5 = SocketUtils::doOperation(enderecoServidor, portaServidor, requestJson5);
        cout << "Resposta recebida: " << respostaJson5.dump(4) << endl;

        // teste 6: listarTodos apos remover
        cout << "\n[6] chamando listarTodos() apos remover..." << endl;
        json requestJson6 = {
            {"TipoMensagem", "Request"},
            {"requestId", 6},
            {"referenciaObj", "ProdutoService"},
            {"metodoId", "listarTodos"},
            {"parametros", {}}
        };
        json respostaJson6 = SocketUtils::doOperation(enderecoServidor, portaServidor, requestJson6);
        cout << "Resposta recebida: " << respostaJson6.dump(4) << endl;

        // teste 7: buscarPorId inexistente
        cout << "\n[7] chamando buscarPorId(999)... (nao existe)" << endl;
        json requestJson7 = {
            {"TipoMensagem", "Request"},
            {"requestId", 7},
            {"referenciaObj", "ProdutoService"},
            {"metodoId", "buscarPorId"},
            {"parametros", {
                {"id", 999}
            }}
        };
        json respostaJson7 = SocketUtils::doOperation(enderecoServidor, portaServidor, requestJson7);
        cout << "Resposta recebida: " << respostaJson7.dump(4) << endl;

        // teste 8: chamar metodo inexistente
        cout << "\n[8] chamando metodo inexistente 'fooBar'..." << endl;
        json requestJson8 = {
            {"TipoMensagem", "Request"},
            {"requestId", 8},
            {"referenciaObj", "ProdutoService"},
            {"metodoId", "fooBar"},
            {"parametros", {}}
        };
        json respostaJson8 = SocketUtils::doOperation(enderecoServidor, portaServidor, requestJson8);
        cout << "Resposta recebida: " << respostaJson8.dump(4) << endl;
    }
    catch(const std::exception& e)
    {
        cerr << "ERRO: " << e.what() << '\n';
        return 1;
    }
    

    return 0;
}