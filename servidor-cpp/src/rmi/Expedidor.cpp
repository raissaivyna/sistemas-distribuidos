#include "../include/rmi/ProdutoSkeleton.hpp"
#include "../include/rmi/Expedidor.hpp"

Reply Expedidor::expedicao(const Request& request){
    if (request.referenciaObj == "ProdutoService")
    {
        ProdutoSkeleton produtoSkeleton;

        return produtoSkeleton.invocacao(request);
    }
    
    Reply reply;
    reply.requestId = request.requestId;
    reply.status = "Erro";
    reply.resultado = "Objeto remoto nao encontrado";
    return reply;
}