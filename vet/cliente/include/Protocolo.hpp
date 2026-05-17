#ifndef PROTOCOLO_HPP
#define PROTOCOLO_HPP
#include <string>
using namespace std;

namespace Protocolo {
    enum Operacao {
        LISTAR_PRODUTOS = 1,
        BUSCAR_POR_ID = 2,
        BUSCAR_POR_ESPECIE = 3,
        CADASTRAR = 4,
        REMOVER = 5,
        LISTAR_VENCIDOS = 6,
        RELATORIO_ESTOQUE = 7,
        RESPOSTA_OK = 10,
        RESPOSTA_ERRO = 11
    };

    void enviarMensagem(int sock, int operacao, const string& payload);

    string receberResposta(int sock, int& operacao);
}


#endif