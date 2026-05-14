#include <iostream>
#include "ProdutoBiologico.hpp"
#include "Perecivel.hpp"
#include <ctime>

#ifndef VACINA_PERECIVEL_HPP
#define VACINA_PERECIVEL_HPP

class VacinaPerecivel : public ProdutoBiologico, public Perecivel
{
private:
    string dataValidade;
    string requisitoArmazenamento;
    double temperaturaMinima;
    double temperaturaMaxima;

public:
    VacinaPerecivel();
    VacinaPerecivel(int id,const string& nome,double preco, const string& fabricante,
        const string& regM, const string& espA, const string& vAdm,
        const string& tipoAgente, const string& sorotipo, int numDoses, 
        string dataValidade, string requisitoArmazenamento, double temperaturaMinima, double temperaturaMaxima);
    const string& getDataValidade() const;
    bool ehVencido() const override;
    void setDataValidade(const string& dataValidade);
    string getRequisitoArmazenamento() const override;
    void setRequisitoArmazenamento(const string& requisitoArmazenamento);
    double getTemperaturaMaxima() const;
    void setTemperaturaMaxima(double temperaturaMaxima);
    double getTemperaturaMinima() const;
    void setTemperaturaMinima(double temperaturaMinima);
    string toString() const;
};



#endif