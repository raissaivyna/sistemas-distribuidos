#ifndef PRODUTO_VETERINARIO_HPP
#define PRODUTO_VETERINARIO_HPP

#include "Produto.hpp"

class ProdutoVeterinario : public Produto
{
protected:
    int tipo;

public:
    ProdutoVeterinario();

    // n = nome, p = preco, t = tipo
    ProdutoVeterinario(string n, float p, int t);

    int getTipo();
};



#endif