#ifndef EXPEDIDOR_HPP
#define EXPEDIDOR_HPP

#pragma once
#include "../protocolo/Request.hpp"
#include "../protocolo/Reply.hpp"
#include "../rmi/ProdutoSkeleton.hpp"


class Expedidor
{
private:
    ProdutoSkeleton produtoSkeleton;

public:
    Reply expedicao(const Request& request);
};


#endif