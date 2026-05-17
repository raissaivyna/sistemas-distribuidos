#include "../include/protocolo/Request.hpp"

json Request::toJson() const
{
    json j;
    j["TipoMensagem"] = TipoMensagem;
    j["requestId"] = requestId;
    j["referenciaObj"] = referenciaObj;
    j["metodoId"] = metodoId;
    j["parametros"] = parametros;
    return j;
};

Request Request::fromJson(const json& jsonString)
{
    Request req;

    req.TipoMensagem = jsonString["TipoMensagem"];
    req.requestId = jsonString["requestId"];
    req.referenciaObj = jsonString["referenciaObj"];
    req.metodoId = jsonString["metodoId"];
    req.parametros = jsonString["parametros"];
    
    return req;
}