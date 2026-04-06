#include "../include/ProdutoVeterinario.hpp"

ProdutoVeterinario::ProdutoVeterinario(){};

ProdutoVeterinario::ProdutoVeterinario(string n, float p, int t) 
    : Produto(n, p){
        tipo = t;
}

int ProdutoVeterinario::getTipo(){
    return tipo;
}