#include "ProdutoQuimioterapico.hpp"

ProdutoQuimioterapico::ProdutoQuimioterapico(){};
ProdutoQuimioterapico::ProdutoQuimioterapico(int id, const string& nome, double preco, const string& fabricante, 
    const string& regM, const string& espA, const string& vAdm, 
    const string& principioAtivo, double concentracao, const string& classeTerapeutica, bool retencaoCarencia)
    : ProdutoVeterinario(id, nome, preco, fabricante, regM, espA, vAdm),
    principioAtivo(principioAtivo), concentracao(concentracao), classeTerapeutica(classeTerapeutica), retencaoCarencia(retencaoCarencia) {};

string ProdutoQuimioterapico::getPrincipioAtivo() const{
    return this->principioAtivo;
}

void ProdutoQuimioterapico::setPrincipioAtivo(const string& principioAtivo){
    this->principioAtivo = principioAtivo;
}

double ProdutoQuimioterapico::getConcentracao() const{
    return this->concentracao;
}

void ProdutoQuimioterapico::setConcentracao(double concentracao){
    this->concentracao = concentracao;
}

string ProdutoQuimioterapico::getClasseTerapeutica() const{
    return this->classeTerapeutica;
}

void ProdutoQuimioterapico::setClasseTerapeutica(const string& classeTerapeutica){
    this->classeTerapeutica = classeTerapeutica;
}

bool ProdutoQuimioterapico::ehRetencaoCarencia() const{
    return this->retencaoCarencia;
}

void ProdutoQuimioterapico::setRetencaoCarencia(bool retencaoCarencia){
    this->retencaoCarencia = retencaoCarencia;
}

string ProdutoQuimioterapico::toString() const{
    return "ProdutoQuimioterapico{id=" + to_string(this->getId()) + 
    ", nome='" + this->getNome() + 
    "', principioAtivo='" + this->principioAtivo + 
    "', concentracao=" + to_string(this->concentracao) + "%" + 
    ", classeTerapeutica='" + this->classeTerapeutica + 
    "', retencaoCarencia=" + (this->retencaoCarencia ? "true" : "false") + 
    "}";
}