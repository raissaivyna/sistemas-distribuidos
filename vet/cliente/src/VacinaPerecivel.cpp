#include "../include/VacinaPerecivel.hpp"

VacinaPerecivel::VacinaPerecivel(){};

VacinaPerecivel::VacinaPerecivel(int id,const string& nome,double preco, const string& fabricante,
        const string& regM, const string& espA, const string& vAdm,
        const string& tipoAgente, const string& sorotipo, int numDoses, 
        string dataValidade, string requisitoArmazenamento, double temperaturaMinima, double temperaturaMaxima)
    : ProdutoBiologico(id, nome, preco, fabricante, regM, espA, vAdm, tipoAgente, sorotipo, numDoses),
    dataValidade(dataValidade), requisitoArmazenamento(requisitoArmazenamento), temperaturaMinima(temperaturaMinima), temperaturaMaxima(temperaturaMaxima) {}


const string& VacinaPerecivel::getDataValidade() const{
    return this->dataValidade;
}

bool VacinaPerecivel::ehVencido() const{
    int dia, mes, ano;
    //quebrando a string em data
    sscanf(this->dataValidade.c_str(), "%d/%d/%d", &dia, &mes, &ano);

    //struct da validade
    tm validade = {};
    validade.tm_mday = dia;
    validade.tm_mon = mes - 1; //mes de 0 a 11
    validade.tm_year = ano - 1900;

    time_t tempoValidade = mktime(&validade);  // data -> numero(timestamp)

    //data atual
    time_t agora = time(nullptr);

    return difftime(agora, tempoValidade) > 0;
} 

void VacinaPerecivel::setDataValidade(const string& dataValidade){
    this->dataValidade = dataValidade;
}

string VacinaPerecivel::getRequisitoArmazenamento() const{
    return this->requisitoArmazenamento;
}

void VacinaPerecivel::setRequisitoArmazenamento(const string& requisitoArmazenamento){
    this->requisitoArmazenamento = requisitoArmazenamento;
}

double VacinaPerecivel::getTemperaturaMaxima() const{
    return this->temperaturaMaxima;
}

void VacinaPerecivel::setTemperaturaMaxima(double temperaturaMaxima){
    this->temperaturaMaxima = temperaturaMaxima;
}

double VacinaPerecivel::getTemperaturaMinima() const{
    return this->temperaturaMinima;
}

void VacinaPerecivel::setTemperaturaMinima(double temperaturaMinima){
    this->temperaturaMinima = temperaturaMinima;
}

string VacinaPerecivel::toString() const{
    return "VacinaPerecivel{id=" + to_string(this->getId()) +
    ", nome='" + this->getNome() + 
    "', validade='" + this->dataValidade + 
    "', vencida=" + (this->ehVencido() ? "true" : "false") + 
    ", armazenamento='" + this->requisitoArmazenamento + 
    "', temp=[" + to_string(this->temperaturaMinima) + 
    ";" + to_string(this->temperaturaMaxima) + "]°C}";

}