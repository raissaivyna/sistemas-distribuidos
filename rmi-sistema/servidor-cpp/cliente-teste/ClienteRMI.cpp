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
            {"referenciaObj", {{"objetoId", 1}, {"NomeObjeto", "ProdutoService"}}},
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
            {"referenciaObj", {{"objetoId", 1}, {"NomeObjeto", "ProdutoService"}}},
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
            {"referenciaObj", {{"objetoId", 1}, {"NomeObjeto", "ProdutoService"}}},
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
            {"referenciaObj", {{"objetoId", 1}, {"NomeObjeto", "ProdutoService"}}},
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
            {"referenciaObj", {{"objetoId", 1}, {"NomeObjeto", "ProdutoService"}}},
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
            {"referenciaObj", {{"objetoId", 1}, {"NomeObjeto", "ProdutoService"}}},
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
            {"referenciaObj", {{"objetoId", 1}, {"NomeObjeto", "ProdutoService"}}},
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
            {"referenciaObj", {{"objetoId", 1}, {"NomeObjeto", "ProdutoService"}}},
            {"metodoId", "fooBar"},
            {"parametros", {}}
        };
        json respostaJson8 = SocketUtils::doOperation(enderecoServidor, portaServidor, requestJson8);
        cout << "Resposta recebida: " << respostaJson8.dump(4) << endl;

        // teste agora do EstoqueService para garantir que o mesmo ProdutoServico é compartilhado entre os skeletons e a consistencia dos dados entre os dois serviços
        // teste 9: criarEstoque
        cout << "\n[9] chamando criarEstoque('Deposito Central')..." << endl;
        json requestJson9 = {
            {"TipoMensagem", "Request"},
            {"requestId", 9},
            {"referenciaObj", {{"objetoId", 2}, {"NomeObjeto", "EstoqueService"}}},
            {"metodoId", "criarEstoque"},
            {"parametros", {
                {"local", "Deposito Central"}
            }}
        };
        json respostaJson9 = SocketUtils::doOperation(enderecoServidor, portaServidor, requestJson9);
        cout << "Resposta recebida: " << respostaJson9.dump(4) << endl;

        //  teste 10: listarEstoques
        cout << "\n[10] chamando listarEstoques()..." << endl;  
        json requestJson10 = {
            {"TipoMensagem", "Request"},
            {"requestId", 10},
            {"referenciaObj", {{"objetoId", 2}, {"NomeObjeto", "EstoqueService"}}},
            {"metodoId", "listarEstoques"},
            {"parametros", {}}
        };
        json respostaJson10 = SocketUtils::doOperation(enderecoServidor, portaServidor, requestJson10);
        cout << "Resposta recebida: " << respostaJson10.dump(4) << endl;

        // teste 11: entradaProduto (adicionar produto com id 1 ao estoque criado)
        cout << "\n[11] chamando entradaProduto(estoqueId: 1, produtoId: 1)..." << endl;
        json requestJson11 = {
            {"TipoMensagem", "Request"},
            {"requestId", 11},
            {"referenciaObj", {{"objetoId", 2}, {"NomeObjeto", "EstoqueService"}}},
            {"metodoId", "entradaProduto"},
            {"parametros", {
                {"estoqueId", 1},
                {"produtoId", 1}
            }}
        };
        json respostaJson11 = SocketUtils::doOperation(enderecoServidor, portaServidor, requestJson11);
        cout << "Resposta recebida: " << respostaJson11.dump(4) << endl;

        // teste 12: listarEstoques novamente para verificar se o produto foi adicionado ao estoque
        cout << "\n[12] chamando listarEstoques() novamente para verificar se o produto foi adicionado ao estoque..." << endl;  
        json requestJson12 = {
            {"TipoMensagem", "Request"},
            {"requestId", 12},
            {"referenciaObj", {{"objetoId", 2}, {"NomeObjeto", "EstoqueService"}}},
            {"metodoId", "listarEstoques"},
            {"parametros", {}}
        };
        json respostaJson12 = SocketUtils::doOperation(enderecoServidor, portaServidor, requestJson12);
        cout << "Resposta recebida: " << respostaJson12.dump(4) << endl;

        // teste 13: alertarVencidos (verificar se o produto adicionado ao estoque aparece como vencido ou nao, dependendo da data de validade do produto)
        cout << "\n[13] chamando alertarVencidos() para verificar se o produto adicionado ao estoque aparece como vencido ou nao..." << endl;  
        json requestJson13 = {
            {"TipoMensagem", "Request"},
            {"requestId", 13},
            {"referenciaObj", {{"objetoId", 2}, {"NomeObjeto", "EstoqueService"}}},
            {"metodoId", "alertarVencidos"},
            {"parametros", {}}
        };
        json respostaJson13 = SocketUtils::doOperation(enderecoServidor, portaServidor, requestJson13);
        cout << "Resposta recebida: " << respostaJson13.dump(4) << endl;

        // teste 14: saidaProduto (remover o produto do estoque)
        cout << "\n[14] chamando saidaProduto(estoqueId: 1, produtoId: 1) para remover o produto do estoque..." << endl;
        json requestJson14 = {
            {"TipoMensagem", "Request"},
            {"requestId", 14},
            {"referenciaObj", {{"objetoId", 2}, {"NomeObjeto", "EstoqueService"}}},
            {"metodoId", "saidaProduto"},
            {"parametros", {
                {"estoqueId", 1},
                {"produtoId", 1}
            }}
        };
        json respostaJson14 = SocketUtils::doOperation(enderecoServidor, portaServidor, requestJson14);
        cout << "Resposta recebida: " << respostaJson14.dump(4) << endl;

        // teste 15: listarEstoques novamente para verificar se o produto foi removido do estoque
        cout << "\n[15] chamando listarEstoques() novamente para verificar se o produto foi removido do estoque..." << endl;  
        json requestJson15 = {
            {"TipoMensagem", "Request"},
            {"requestId", 15},
            {"referenciaObj", {{"objetoId", 2}, {"NomeObjeto", "EstoqueService"}}},
            {"metodoId", "listarEstoques"},
            {"parametros", {}}
        };
        json respostaJson15 = SocketUtils::doOperation(enderecoServidor, portaServidor, requestJson15);
        cout << "Resposta recebida: " << respostaJson15.dump(4) << endl;

        // teste 16: alertarVencidos novamente para verificar se o produto removido do estoque nao aparece mais como vencido
        cout << "\n[16] chamando alertarVencidos() novamente para verificar se o produto removido do estoque nao aparece mais como vencido..." << endl;  
        json requestJson16 = {
            {"TipoMensagem", "Request"},
            {"requestId", 16},
            {"referenciaObj", {{"objetoId", 2}, {"NomeObjeto", "EstoqueService"}}},
            {"metodoId", "alertarVencidos"},
            {"parametros", {}}
        };
        json respostaJson16 = SocketUtils::doOperation(enderecoServidor, portaServidor, requestJson16);
        cout << "Resposta recebida: " << respostaJson16.dump(4) << endl;

        // teste 17: chamar metodo do EstoqueService que nao existe no ProdutoService para garantir que o Expedidor encaminha a requisicao para o skeleton correto e retorna erro de metodo nao encontrado quando o metodo nao existe no skeleton
        cout << "\n[17] chamando metodo 'fooBar' no EstoqueService para garantir que o Expedidor encaminha a requisicao para o skeleton correto e retorna erro de metodo nao encontrado quando o metodo nao existe no skeleton..." << endl;
        json requestJson17 = {
            {"TipoMensagem", "Request"},
            {"requestId", 17},
            {"referenciaObj", {{"objetoId", 2}, {"NomeObjeto", "EstoqueService"}}},
            {"metodoId", "fooBar"},
            {"parametros", {}}
        };
        json respostaJson17 = SocketUtils::doOperation(enderecoServidor, portaServidor, requestJson17);
        cout << "Resposta recebida: " << respostaJson17.dump(4) << endl;

        // teste 18: chamar metodo do ProdutoService que nao existe no EstoqueService para garantir que o Expedidor encaminha a requisicao para o skeleton correto e retorna erro de metodo nao encontrado quando o metodo nao existe no skeleton
        cout << "\n[18] chamando metodo 'fooBar' no ProdutoService para garantir que o Expedidor encaminha a requisicao para o skeleton correto e retorna erro de metodo nao encontrado quando o metodo nao existe no skeleton..." << endl;
        json requestJson18 = {
            {"TipoMensagem", "Request"},
            {"requestId", 18},
            {"referenciaObj", {{"objetoId", 1}, {"NomeObjeto", "ProdutoService"}}},
            {"metodoId", "fooBar"},
            {"parametros", {}}
        };
        json respostaJson18 = SocketUtils::doOperation(enderecoServidor, portaServidor, requestJson18);
        cout << "Resposta recebida: " << respostaJson18.dump(4) << endl;

    }
    catch(const std::exception& e)
    {
        cerr << "ERRO: " << e.what() << '\n';
        return 1;
    }
    

    return 0;
}