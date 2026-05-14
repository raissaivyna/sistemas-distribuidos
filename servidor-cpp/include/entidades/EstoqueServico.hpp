#ifndef ESTOQUESERVICO_HPP
#define ESTOQUESERVICO_HPP
#include "Estoque.hpp"
#include "Perecivel.hpp"
#include "Produto.hpp"

class EstoqueServico
{
private:
    vector<Estoque> estoques;
    int proximoId = 1;
public:
    EstoqueServico();
    Estoque criarEstoque(const string& local);
    Estoque* buscarEstoquePorId(int id);
    bool entradaProduto(int estoqueId, Produto* p);
    bool saidaProduto(int estoqueId, int ProdutoId);
    void alertarVencidos();
    void relatorioGeral();
    const vector<Estoque>& listarEstoques() const;
};



#endif // ESTOQUESERVICO_HPP