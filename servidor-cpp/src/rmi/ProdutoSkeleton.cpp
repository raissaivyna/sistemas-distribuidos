#include "../include/rmi/ProdutoSkeleton.hpp"

Reply ProdutoSkeleton::invocacao(const Request& request){
    Reply reply;

    reply.requestId = request.requestId;

    // listarTodos: retorna todos os produtos cadastrados
    if (request.metodoId == "listarTodos")
    {
        auto lista = service.listarTodos();
        cout << "invocacao listarTodos, total: " << lista.size() << endl;

        json array = json::array();
        for (auto& produto : lista)
        {
            array.push_back(produto->toJson());
        }
        reply.status = "OK";
        reply.resultado = array;
    }
    
    // buscarPorId: retorna o produto com o id especificado ou erro se nao encontrado
    else if (request.metodoId == "buscarPorId")
    {
        cout << "invocacao buscarPorId com id: " << request.parametros["id"] << endl;
        int id = request.parametros["id"];
        Produto* produto = service.buscarPorId(id);
        if (produto)
        {
            reply.status = "OK";
            //cout << "antes do json p/ debug" << endl;
            cout << "tipo do produto: " << typeid(*produto).name() << endl;
            reply.resultado = produto->toJson();
            //cout << "depois do json p/ debug" << endl;
        }
        else
        {
            reply.status = "ERRO";
            reply.resultado = "Produto não encontrado";
        }
    }
    
    // buscaPorEspecie: retorna os produtos veterinarios para a especie especificada
    else if (request.metodoId == "buscarPorEspecie")
    {
        string especie = request.parametros["especie"];
        cout << "invocacao buscarPorEspecie com especie: " << especie << endl;
        auto lista = service.buscarPorEspecie(especie);
        json array = json::array();
        for (auto& produto : lista)
        {
            array.push_back(produto->toJson());
        }
        reply.status = "OK";
        reply.resultado = array;
    }
    
    // calcularValorTotal: retorna o valor total de todos os produtos cadastrados
    else if (request.metodoId == "calcularValorTotal")
    {
        cout << "invocacao calcularValorTotal" << endl;
        double total = service.calcularValorTotal();
        reply.status = "OK";
        reply.resultado = total;
    }

    // remover: remove o produto com o id especificado e retorna sucesso ou erro se nao encontrado
    else if (request.metodoId == "remover")
    {
        int id = request.parametros["id"];
        cout << "invocacao remover com id: " << id << endl;
        bool sucesso = service.remover(id);
        if (sucesso)
        {
            reply.status = "OK";
            reply.resultado = "Produto removido com sucesso";
        }
        else
        {
            reply.status = "ERRO";
            reply.resultado = "Produto não encontrado para remoção";
        }
    }

    // metodo nao conhecido
    else
    {
        reply.status = "ERRO";
        reply.resultado = "Metodo nao encontrado";
    }



    
    return reply;
}