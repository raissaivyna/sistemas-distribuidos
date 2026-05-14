#include <iostream>
#include "ProdutoVeterinario.hpp"
#ifndef PRODUTO_BIOLOGICO_HPP
#define PRODUTO_BIOLOGICO_HPP

class ProdutoBiologico : public ProdutoVeterinario
{
private:
    string tipoAgente;
    string sorotipo;
    int numDoses;
public:
    ProdutoBiologico();
    ProdutoBiologico(
        int id, const string& nome, double preco, const string& fabricante, 
        const string& regM, const string& espA, const string& vAdm,
        const string& tipoAgente, const string& sorotipo, int numDoses);
    const string& getTipoAgente() const;
    void setTipoAgente(const string& tipoAgente);
    const string& getSorotipo() const;
    void setSorotipo(const string& sorotipo);
    int getNumDoses() const;
    void setNumDoses(int numDoses);
    string toString() const;
};



#endif