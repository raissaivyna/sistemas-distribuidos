#include <iostream>
#include "ProdutoVeterinario.hpp"

#ifndef PRODUTO_QUIMIOTERAPICO_HPP
#define PRODUTO_QUIMIOTERAPICO_HPP

class ProdutoQuimioterapico : public ProdutoVeterinario
{
private:
    string principioAtivo;
    double concentracao;
    string classeTerapeutica;
    bool retencaoCarencia;
public:
    ProdutoQuimioterapico();
    ProdutoQuimioterapico(int id, const string& nome, double preco, const string& fabricante,
         const string& regM, const string& espA, const string& vAdm, 
         const string& principioAtivo, double concentracao, const string& classeTerapeutica, bool retencaoCarencia);
    string getPrincipioAtivo() const;
    void setPrincipioAtivo(const string& principioAtivo);
    double getConcentracao() const;
    void setConcentracao(double concentracao);
    string getClasseTerapeutica() const;
    void setClasseTerapeutica(const string& classeTerapeutica);
    bool ehRetencaoCarencia() const;
    void setRetencaoCarencia(bool retencaoCarencia);
    string toString() const;
};


#endif