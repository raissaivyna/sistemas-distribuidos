#include "../include/ProdutoVeterinario.hpp"

ProdutoVeterinario::ProdutoVeterinario(){};

ProdutoVeterinario::ProdutoVeterinario(int id, const string& nome, double preco, const string& fabricante,
     const string& regMap,const string& espA,const string& vAdm)
    : Produto(id, nome, preco, fabricante),
    registroMapa(regMap), especieAlvo(espA), viaAdministracao(vAdm) {}

string ProdutoVeterinario::getRegistroMapa() const{
    return this->registroMapa;
}

void ProdutoVeterinario::setRegistroMapa(const string& regM){
    this->registroMapa = regM;
}

string ProdutoVeterinario::getEspecieAlvo() const{
    return this->especieAlvo;
}

void ProdutoVeterinario::setEspecieAlvo(const string& espA){
    this->especieAlvo = espA;
}

string ProdutoVeterinario::getViaAdministracao() const{
    return this->viaAdministracao;
}

void ProdutoVeterinario::setViaAdministracao(const string& vAdm){
    this->viaAdministracao = vAdm;
}

string ProdutoVeterinario::toString() const{
    return "ProdutoVeterinario{id=" + to_string(this->getId()) + 
    ", nome='" + this->getNome() + 
    "', preco=" + to_string(this->getPreco()) + 
    ", fabricante='" + this->getFabricante() + 
    "', registroMapa='" + this->registroMapa + 
    "', especieAlvo='" + this->especieAlvo + 
    "', viaAdministracao='" + this->viaAdministracao + 
    "'}";
}