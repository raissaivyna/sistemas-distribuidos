#include "../include/skeleton/ProdutoSkeleton.hpp"
#include "../include/rmi/Expedidor.hpp"

Reply Expedidor::expedicao(const Request& request){
    //cout << "debug: Expedidor::expedicao chamada com referenciaObj: " << request.referenciaObj << " e metodoId: " << request.metodoId << endl;
    if (request.referenciaObj == "ProdutoService")
    {
        //cout << "debug: Referencia para ProdutoService reconhecida, encaminhando para ProdutoSkeleton" << endl;
        
        //cout << "debug: Criado ProdutoSkeleton, invocando metodo invocacao" << endl;

        return produtoSkeleton.invocacao(request);
    }

    Reply reply;
    reply.requestId = request.requestId;
    reply.status = "Erro";
    reply.resultado = "Objeto remoto nao encontrado";
    return reply;
}