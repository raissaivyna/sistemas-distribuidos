#ifndef PRODUTO_OUTPUT_STREAM_HPP
#define PRODUTO_OUTPUT_STREAM_HPP

#include <ostream>
#include "Vacina.hpp"

class ProdutoOutputStream
{
private:
    std::ostream& destino;
    Vacina* lista;
    int qtd;
public:
    ProdutoOutputStream(Vacina* l, int quantidade, std::ostream& out);
    void writeAll();
};


#endif