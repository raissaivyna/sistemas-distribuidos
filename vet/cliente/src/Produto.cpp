#include "../include/Produto.hpp"

Produto::Produto(){};

Produto::Produto(int i, const string& n, const double& p, const string& f)
    : id(i), nome(n), preco(p), fabricante(f){}


int Produto::getId() const{
    return this->id;
}

void Produto::setId(int i){
    this->id = i;
}

string Produto::getNome() const{
    return this->nome;
}

void Produto::setNome(const string& n){
    this->nome = n;
}

void Produto::setPreco(double p){
    this->preco = p;
}

double Produto::getPreco() const{
    return this->preco;
}

void Produto::setFabricante(const string& f){
    this->fabricante = f;
}

string Produto::getFabricante() const{
    return this->fabricante;
}

string Produto::toString() const{
    return "Produto{id=" + to_string(this->getId()) + 
    ", nome='" + this->getNome() + 
    "', preco=" + to_string(this->getPreco()) + 
    ", fabricante='" + this->getFabricante() + "'}";
}