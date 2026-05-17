#include "../include/rmi/ProdutoSkeleton.hpp"

Reply ProdutoSkeleton::invocacao(const Request& request){
    Reply reply;

    reply.requestId = request.requestId;

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
    else if (request.metodoId == "buscarPorId")
    {
        cout << "invocacao buscarPorId com id: " << request.parametros["id"] << endl;
        int id = request.parametros["id"];
        Produto* produto = service.buscarPorId(id);
        if (produto)
        {
            reply.status = "OK";
            cout << "antes do json p/ debug" << endl;
            cout << "tipo do produto: " << typeid(*produto).name() << endl;
            reply.resultado = produto->toJson();
            cout << "depois do json p/ debug" << endl;
        }
        else
        {
            reply.status = "ERRO";
            reply.resultado = "Produto não encontrado";
        }
    }
    return reply;
}