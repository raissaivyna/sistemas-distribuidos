#include "EstoqueServico.hpp"


EstoqueServico::EstoqueServico() {}

Estoque EstoqueServico::criarEstoque(const string& local)
{
    Estoque e(proximoId++, local);
    estoques.push_back(e);
    cout << "[ESTOQUE] Criado: " << e.toString() << endl;
    return e;
}

Estoque* EstoqueServico::buscarEstoquePorId(int id)
{
    // para cada estoque na lista de estoques, verifica se o id corresponde ao id buscado
    for (auto& estoque : estoques)
    {
        if (estoque.getId() == id)
        {
            return &estoque;
        }
    }
    return nullptr;
}

bool EstoqueServico::entradaProduto(int estoqueId, Produto* p)
{
    Estoque* estoque = buscarEstoquePorId(estoqueId);
    if (estoque)
    {
        estoque->adicionarProduto(p);
        cout << "[ENTRADA] " << p->getNome() << 
        " -> Estoque: " << estoque->getLocal() << endl;
        return true;
    }
    return false;
}


bool EstoqueServico::saidaProduto(int estoqueId, int ProdutoId)
{
    Estoque* estoque = buscarEstoquePorId(estoqueId);

    if (!estoque)
    {
        return false;
    }
    

    bool removido = estoque->removerProduto(ProdutoId);
    if (removido)
    {
        cout << "[SAIDA] Produto id=" << ProdutoId << " removido do Estoque " << estoque->getLocal() << endl;
    }
    return removido;
}

void EstoqueServico::alertarVencidos()
{
    cout << "\n========= ALERTA DE PRODUTOS VENCIDOS =========" << endl;
    bool encontrou = false;

    for (const auto& estoque : estoques)
    {
        for(auto* produto : estoque.getProdutos()){
            const Perecivel* p = dynamic_cast<const Perecivel*>(produto);
            if (p && p->ehVencido())
            {
                cout << " [VENCIDO] " << produto->getNome() << 
                " | validade: " << p->getDataValidade() 
                << " | Estoque: " << estoque.getLocal() << endl;
                encontrou = true;
            }
        }
    }

    if (!encontrou)
    {
        cout << "[ALERTA] Nenhum produto vencido encontrado." << endl;
    }
}

void EstoqueServico::relatorioGeral()
{
    cout << "\n========= RELATORIO GERAL DE ESTOQUES =========" << endl;
    for (const auto& estoque : estoques)
    {
        cout << "\nEstoque: " << estoque.getLocal()
        << " (id=" << estoque.getId() << ") - "
        << estoque.getTotalProdutos() << " produto(s)"
        << endl;

        for (const auto& produto : estoque.getProdutos())
        {
            cout << "  > " << produto->toString() << endl;
        }
    }
    cout << "===============================================" << endl;
}

const vector<Estoque>& EstoqueServico::listarEstoques() const
{
    return estoques;
}