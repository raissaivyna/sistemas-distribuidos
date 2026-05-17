#include "ProdutoBiologico.hpp"

ProdutoBiologico::ProdutoBiologico(){};
ProdutoBiologico::ProdutoBiologico(int id, const string& nome, double preco, const string& fabricante,
    const string& regM, const string& espA, const string& vAdm,    
    const string& tipoAgente, const string& sorotipo, int numDoses) 
    : ProdutoVeterinario(id, nome, preco, fabricante, regM, espA, vAdm),
    tipoAgente(tipoAgente), sorotipo(sorotipo), numDoses(numDoses) {}

const string& ProdutoBiologico::getTipoAgente() const{
    return tipoAgente;
}

void ProdutoBiologico::setTipoAgente(const string& tipoAgente){
    this->tipoAgente = tipoAgente;
}

const string& ProdutoBiologico::getSorotipo() const{
    return sorotipo;
}

void ProdutoBiologico::setSorotipo(const string& sorotipo){
    this->sorotipo = sorotipo;
}

int ProdutoBiologico::getNumDoses() const{
    return numDoses;
}

void ProdutoBiologico::setNumDoses(int numDoses){
    this->numDoses = numDoses;
}

string ProdutoBiologico::toString() const{
    return "ProdutoBiologico{id=" + to_string(this->getId()) + 
    ", nome='" + this->getNome() +
    "', tipoAgente='" + tipoAgente + 
    "', sorotipo='" + sorotipo + 
    "', numDoses=" + to_string(numDoses) + "}";
}
