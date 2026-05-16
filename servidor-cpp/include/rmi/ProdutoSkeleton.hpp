#ifndef PRODUTOSKELETON_HPP
#define PRODUTOSKELETON_HPP

#pragma once
#include "../protocolo/Request.hpp"
#include "../protocolo/Reply.hpp"
#include "../services/ProdutoServico.hpp"

class ProdutoSkeleton
{
private:
    ProdutoServico service;
public:
    Reply invocacao(const Request& request);
};




#endif