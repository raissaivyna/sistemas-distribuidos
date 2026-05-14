#ifndef REQUEST_HPP
#define REQUEST_HPP

#pragma once
#include <string>
#include <nlohmann/json.hpp>
using namespace std;

using json = nlohmann::json;

class Request
{
    
public:
    string TipoMensagem;
    int requestId;

    string referenciaObj;
    string metodoId;

    json parametros;

    json toJson() const;
    static Request fromJson(const json& jsonString);
};



#endif