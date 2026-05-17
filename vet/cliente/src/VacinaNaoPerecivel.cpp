#include "VacinaNaoPerecivel.hpp"

VacinaNaoPerecivel::VacinaNaoPerecivel(){}

VacinaNaoPerecivel::VacinaNaoPerecivel(int id, const string& nome, double preco, const string& fabricante, 
    const string& regM, const string& espA, const string& vAdm, 
    const string& tipoAgente, const string& sorotipo, int numDoses, 
    const string& formaFarmaceutica, int prazoValidadeMeses, double temperaturaMax)
    : ProdutoBiologico(id, nome, preco, fabricante, regM, espA, vAdm, tipoAgente, sorotipo, numDoses) ,
    formaFarmaceutica(formaFarmaceutica), prazoValidadeMeses(prazoValidadeMeses), temperaturaMax(temperaturaMax) {};

const string& VacinaNaoPerecivel::getFormaFarmaceutica() const{
    return this->formaFarmaceutica;
}

void VacinaNaoPerecivel::setFormaFarmaceutica(const string& f) {
    this->formaFarmaceutica = f;
}

int VacinaNaoPerecivel::getprazoValidadeMeses() const{
    return this->prazoValidadeMeses;
}

void VacinaNaoPerecivel::setprazoValidadeMeses(int p) {
    this->prazoValidadeMeses = p;
}

double VacinaNaoPerecivel::getTemperaturaMax() const{
    return this->temperaturaMax;
}

void VacinaNaoPerecivel::setTemperaturaMax(double tempMax) {
    this->temperaturaMax = tempMax;
}

string VacinaNaoPerecivel::toString() const{
    return "VacinaNaoPerecivel{id=" + to_string(this->getId()) + 
    ", nome='" + this->getNome() + 
    "', forma='" + this->formaFarmaceutica + 
    "', validadeMeses=" + to_string(this->prazoValidadeMeses) + 
    ", tempMax=" + to_string(this->temperaturaMax) + 
    "°C}";
}