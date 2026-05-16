#ifndef EXPEDIDOR_HPP
#define EXPEDIDOR_HPP

#pragma once
#include "../protocolo/Request.hpp"
#include "../protocolo/Reply.hpp"


class Expedidor
{

public:
    Reply expedicao(const Request& request);
};


#endif