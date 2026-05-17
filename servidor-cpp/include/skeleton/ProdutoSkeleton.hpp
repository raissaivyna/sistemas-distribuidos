#ifndef PRODUTOSKELETON_HPP
#define PRODUTOSKELETON_HPP

#pragma once
#include "../protocolo/Request.hpp"
#include "../protocolo/Reply.hpp"
#include "../services/ProdutoServico.hpp"
#include <iostream>
using namespace std;

class ProdutoSkeleton
{
private:
    ProdutoServico service;
public:
    Reply invocacao(const Request& request);
    ProdutoServico& getService() { return service; } // metodo para acessar o ProdutoServico e garantir que o mesmo seja compartilhado com o EstoqueSkeleton, permitindo consistencia dos dados entre os dois skeletons
};




#endif