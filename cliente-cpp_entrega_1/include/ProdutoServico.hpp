#ifndef PRODUTOSERVICO_HPP
#define PRODUTOSERVICO_HPP

#include "Produto.hpp"
#include "ProdutoVeterinario.hpp"
#include "VacinaPerecivel.hpp"
#include <typeinfo>

class ProdutoServico
{
private:
    vector <Produto*> repositorio;
    int proximoId = 1;
public:
    ProdutoServico();
    ~ProdutoServico();
    Produto* cadastrar(Produto* p);
    Produto* buscarPorId(int id);
    vector <Produto*> listarTodos() const;
    bool remover(int id);
    vector <VacinaPerecivel*> listarVacinasPereciveis() const;
    vector <ProdutoVeterinario*> buscarPorEspecie(const string& especie) const;
    double calcularValorTotal() const;
    string serializar(Produto* p) const;
    int getTotalProdutos() const;
};


#endif // PRODUTOSERVICO_HPP