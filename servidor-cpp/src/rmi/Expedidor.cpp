#include "../include/skeleton/ProdutoSkeleton.hpp"
#include "../include/rmi/Expedidor.hpp"

Reply Expedidor::expedicao(const Request& request){
    //cout << "debug: Expedidor::expedicao chamada com referenciaObj: " << request.referenciaObj << " e metodoId: " << request.metodoId << endl;

    const string& nome = request.referenciaObj.NomeObjeto;


    if (nome == "ProdutoService")
    {
        //cout << "debug: Referencia para ProdutoService reconhecida, encaminhando para ProdutoSkeleton" << endl;
        
        //cout << "debug: Criado ProdutoSkeleton, invocando metodo invocacao" << endl;

        return produtoSkeleton.invocacao(request);
    }else if (nome == "EstoqueService")
    {
        //cout << "debug: Referencia para EstoqueService reconhecida, encaminhando para EstoqueSkeleton" << endl;
        
        //cout << "debug: Criado EstoqueSkeleton, invocando metodo invocacao" << endl;

        return estoqueSkeleton.invocacao(request);
    }
    

    Reply reply;
    reply.requestId = request.requestId;
    reply.status = "Erro";
    reply.resultado = "Objeto remoto nao encontrado";
    return reply;
}