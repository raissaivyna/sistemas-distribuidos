#include "Estoque.hpp"

Estoque::Estoque(){}

Estoque::Estoque(int i, string l)
{
    this->id = i;
    this->local = l;
}

void Estoque::adicionarProduto(Produto* p)
{
    produtos.push_back(p);
}

bool Estoque::removerProduto(int id)
{
    for (auto i = produtos.begin(); i != produtos.end(); ++i)
    {
        if ((*i)->getId() == id)
        {
            produtos.erase(i);
            return true;
        }
    }
    return false;
}

Produto* Estoque::buscarPorId(int id)
{
    for (auto* p : produtos)
    {
        if (p->getId() == id)
        {
            return p;
        }
    }
    return nullptr;
}

int Estoque::getTotalProdutos() const
{
    return produtos.size();
}

int Estoque::getId() const
{
    return id;
}

void Estoque::setId(int i)
{
    id = i;
}

string Estoque::getLocal() const
{
    return local;
}

void Estoque::setLocal(const string& l)
{
    local = l;
}

const vector<Produto*>& Estoque::getProdutos() const
{
    return produtos;
}

void Estoque::setProdutos(const vector<Produto*>& p)
{
    produtos = p;
}

string Estoque::toString() const
{
    string result = "Estoque{id=" + to_string(id) + 
    ", local='" + local + 
    "', totalProdutos=" + to_string(getTotalProdutos()) + 
    "}";
    return result;
}