#include "../include/Produto.hpp"

Produto::Produto(){};

Produto::Produto(string n, float p){
    nome = n;
    preco = p;
}

string Produto::getNome(){
    return nome;
}

float Produto::getPreco(){
    return preco;
}