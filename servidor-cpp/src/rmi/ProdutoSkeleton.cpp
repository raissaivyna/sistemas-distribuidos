#include "../include/rmi/ProdutoSkeleton.hpp"

Reply ProdutoSkeleton::invocacao(const Request& request){
    Reply reply;

    reply.requestId = request.requestId;

    if (request.metodoId == "listarTodos")
    {
        auto lista = service.listarTodos();

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
        int id = request.parametros["id"];
        Produto* produto = service.buscarPorId(id);
        if (produto)
        {
            reply.status = "OK";
            reply.resultado = produto->toJson();
        }
        else
        {
            reply.status = "ERRO";
            reply.resultado = "Produto não encontrado";
        }
    }
    return reply;
}