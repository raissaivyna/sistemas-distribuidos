#ifndef ESTOQUE_SKELETON_HPP
#define ESTOQUE_SKELETON_HPP

#pragma once
#include "../protocolo/Request.hpp"
#include "../protocolo/Reply.hpp"
#include "../services/EstoqueServico.hpp"
#include "../services/ProdutoServico.hpp"

#include <iostream>
using namespace std;

class EstoqueSkeleton
{
private:
    EstoqueServico estoqueServico;
    ProdutoServico produtoServico;   // referencia ao mesmo ProdutoServico usado pelo ProdutoSkeleton para garantir consistencia dos dados entre os dois skeletons
public:
    EstoqueSkeleton(ProdutoServico& produtoServico) : produtoServico(produtoServico) {}
    Reply invocacao(const Request& request);
};


#endif // ESTOQUE_SKELETON_HPP