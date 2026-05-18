#include "../include/skeleton/EstoqueSkeleton.hpp"

Reply EstoqueSkeleton::invocacao(const Request &request)
{
    Reply reply;
    reply.requestId = request.requestId;

    // criarEstoque: cria um novo estoque
    // parametros: { "local" : string}
    if (request.metodoId == "criarEstoque")
    {
        string local = request.parametros["local"];
        cout << "invocacao criarEstoque com local: " << local << endl;
        Estoque estoque = estoqueServico.criarEstoque(local);
        reply.status = "OK";
        reply.resultado = estoque.toJson();
    }

    // listarEstoques: retorna a lista de estoques cadastrados
    else if (request.metodoId == "listarEstoques")
    {
        auto lista = estoqueServico.listarEstoques();
        cout << "invocacao listarEstoques, total: " << lista.size() << endl;

        json array = json::array();
        for (auto &estoque : lista)
        {
            array.push_back(estoque.toJson());
        }
        reply.status = "OK";
        reply.resultado = array;
    }

    // entradaProduto: registra a entrada de um produto em um estoque
    // parametros: { "estoqueId": int, "produtoId": int }
    else if (request.metodoId == "entradaProduto")
    {
        int estoqueId = request.parametros["estoqueId"];
        int produtoId = request.parametros["produtoId"];
        cout << "invocacao entradaProduto com estoqueId: " << estoqueId << " e produtoId: " << produtoId << endl;

        Produto *produto = produtoServico.buscarPorId(produtoId);
        if (produto)
        {
            bool sucesso = estoqueServico.entradaProduto(estoqueId, produto);
            if (sucesso)
            {
                reply.status = "OK";
                reply.resultado = "Produto adicionado ao estoque com sucesso";
            }
            else
            {
                reply.status = "ERRO";
                reply.resultado = "Falha ao adicionar produto ao estoque (estoqueId pode estar incorreto)";
            }
        }

        // saidaProduto: registra a saída de um produto de um estoque
        // parametros: { "estoqueId": int, "produtoId": int }
        else if (request.metodoId == "saidaProduto")
        {
            int estoqueId = request.parametros["estoqueId"];
            int produtoId = request.parametros["produtoId"];
            cout << "invocacao saidaProduto com estoqueId: " << estoqueId << " e produtoId: " << produtoId << endl;
            bool sucesso = estoqueServico.saidaProduto(estoqueId, produtoId);
            if (sucesso)
            {
                reply.status = "OK";
                reply.resultado = "Produto removido do estoque com sucesso";
            }
            else
            {
                reply.status = "ERRO";
                reply.resultado = "Falha ao remover produto do estoque (estoqueId ou produtoId podem estar incorretos)";
            }
        }
    }

    // saidaProduto: registra a saída de um produto de um estoque    // parametros: { "estoqueId": int, "produtoId": int }
    else if (request.metodoId == "saidaProduto")
    {
        int estoqueId = request.parametros["estoqueId"];
        int produtoId = request.parametros["produtoId"];
        cout << "invocacao saidaProduto com estoqueId: " << estoqueId << " e produtoId: " << produtoId << endl;
        bool sucesso = estoqueServico.saidaProduto(estoqueId, produtoId);
        if (sucesso)
        {
            reply.status = "OK";
            reply.resultado = "Produto removido do estoque com sucesso";
        }
        else
        {
            reply.status = "ERRO";
            reply.resultado = "Falha ao remover produto do estoque (estoqueId ou produtoId podem estar incorretos)";
        }
    }

    // alertarVencidos: verifica os produtos vencidos em todos os estoques e retorna uma lista de alertas
    else if (request.metodoId == "alertarVencidos")
    {
        cout << "invocacao alertarVencidos" << endl;
        json vencidos = json::array();

        for (const auto& estoque : estoqueServico.listarEstoques())
        {
            for(auto* produto : estoque.getProdutos())
            {
                const Perecivel* p = dynamic_cast<const Perecivel*>(produto);
                if (p && p->ehVencido())
                {
                    json item;
                    item["produto"] = produto->getNome();
                    item["validade"] = p->getDataValidade();
                    item["estoque"] = estoque.getLocal();
                    vencidos.push_back(item);
                }
            }
        }
        reply.status = "OK";
        reply.resultado = vencidos;
    }


    // metodo nao conhecido
    else
    {
        reply.status = "ERRO";
        reply.resultado = "Metodo nao encontrado: " + request.metodoId;
    }

    return reply;
}