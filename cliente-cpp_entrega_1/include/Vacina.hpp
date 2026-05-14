#ifndef VACINA_HPP
#define VACINA_HPP

#include "ProdutoVeterinario.hpp"

class Vacina : public ProdutoVeterinario
{
private:
    bool perecivel;
public:
    Vacina();
    // n = nome, p = preco, t = tipo, per = perecivel ou nao
    Vacina(string n, float p, int t, bool per);

    bool ehPerecivel();
};



#endif