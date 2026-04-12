#ifndef PRODUTO_VETERINARIO_HPP
#define PRODUTO_VETERINARIO_HPP

#include "Produto.hpp"

class ProdutoVeterinario 
: public Produto
{
private:
    string registroMapa;
    string especieAlvo;
    string viaAdministracao;

public:
    ProdutoVeterinario();

    // n = nome, p = preco, t = tipo
    ProdutoVeterinario(int id, const string& nome, double preco, const string& fabricante,
        const string& regM, const string& espA, const string& vAdm);

    string getRegistroMapa() const;

    void setRegistroMapa(const string& regM);

    string getEspecieAlvo() const;
    void setEspecieAlvo(const string& espA);

    string getViaAdministracao() const;
    void setViaAdministracao(const string& vAdm);
    string toString()const;
};



#endif