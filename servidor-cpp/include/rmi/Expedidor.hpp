#ifndef EXPEDIDOR_HPP
#define EXPEDIDOR_HPP

#pragma once
#include "../protocolo/Request.hpp"
#include "../protocolo/Reply.hpp"
#include "../skeleton/ProdutoSkeleton.hpp"
#include "../skeleton/EstoqueSkeleton.hpp"


class Expedidor
{
private:
    ProdutoSkeleton produtoSkeleton;
    EstoqueSkeleton estoqueSkeleton{produtoSkeleton.getService()}; // passar a referencia do ProdutoServico do ProdutoSkeleton para o EstoqueSkeleton para garantir que ambos os skeletons compartilhem o mesmo ProdutoServico e, consequentemente, os mesmos dados de produtos

public:
    Reply expedicao(const Request& request);
};


#endif