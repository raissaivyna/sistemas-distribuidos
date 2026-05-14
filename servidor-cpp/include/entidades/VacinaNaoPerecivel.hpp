#ifndef VACINA_NAO_PERECIVEL_HPP
#define VACINA_NAO_PERECIVEL_HPP

#include "ProdutoBiologico.hpp"

class VacinaNaoPerecivel : public ProdutoBiologico
 {
    private:
        string formaFarmaceutica;
        int prazoValidadeMeses;
        double temperaturaMax;
    public:
        VacinaNaoPerecivel();
        VacinaNaoPerecivel(int id, const string& nome, double preco, const string& fabricante, 
            const string& regM, const string& espA, const string& vAdm, 
            const string& tipoAgente, const string& sorotipo, int numDoses, 
            const string& formaFarmaceutica, int prazoValidadeMeses, double temperaturaMax);
        const string& getFormaFarmaceutica() const;
        void setFormaFarmaceutica(const string& f);
        int getprazoValidadeMeses() const;
        void setprazoValidadeMeses(int p);
        double getTemperaturaMax() const;
        void setTemperaturaMax(double tempMax);
        string toString() const;
};

#endif // VACINA_NAO_PERECIVEL_HPP