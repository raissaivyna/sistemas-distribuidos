#ifndef ESTOQUE_HPP
#define ESTOQUE_HPP
#include "Produto.hpp"

class Estoque
{
private:
    int id;
    string local;
    vector<Produto*> produtos;
public:
    Estoque();
    Estoque(int i, string l);  // i - id, l - local
    void adicionarProduto(Produto* p);
    bool removerProduto(int id);
    Produto* buscarPorId(int id);
    int getTotalProdutos() const;
    int getId() const;
    void setId(int i);
    string getLocal() const;
    void setLocal(const string& l);
    const vector<Produto*>& getProdutos() const;
    void setProdutos(const vector<Produto*>& p);
    string toString() const;
};


#endif // ESTOQUE_HPP