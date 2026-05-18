#ifndef REQUEST_HPP
#define REQUEST_HPP

#pragma once
#include <string>
#include <nlohmann/json.hpp>

#include "../protocolo/RemoteObjectRef.hpp"

using namespace std;

using json = nlohmann::json;

class Request
{
    
public:
    string TipoMensagem;
    int requestId;

    // passagem por referencia: referenciaObj identifica o objeto remoto a ser invocado, metodoId identifica o método a ser chamado e parametros contém os parâmetros necessários para a invocação
    RemoteObjectRef referenciaObj; 
    string metodoId;

    json parametros;

    json toJson() const;
    static Request fromJson(const json& jsonString);
};



#endif