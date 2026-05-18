#include "../include/protocolo/Request.hpp"

json Request::toJson() const
{
    json j;
    j["TipoMensagem"] = TipoMensagem;
    j["requestId"] = requestId;
    j["referenciaObj"] = {
        {"objetoId", referenciaObj.objetoId},
        {"NomeObjeto", referenciaObj.NomeObjeto}
    };
    j["metodoId"] = metodoId;
    j["parametros"] = parametros;
    return j;
};

Request Request::fromJson(const json& jsonString)
{
    Request req;

    req.TipoMensagem = jsonString["TipoMensagem"];
    req.requestId = jsonString["requestId"];

    // referenciaObj pode chegar como objeto { "objetoId": 1, "NomeObjeto": "ProdutoService" } ou como string "ProdutoService"
    if (jsonString["referenciaObj"].is_object())
    {
        req.referenciaObj.objetoId = jsonString["referenciaObj"]["objetoId"];
        req.referenciaObj.NomeObjeto = jsonString["referenciaObj"]["NomeObjeto"];
    }else{
        req.referenciaObj.objetoId = 0; // valor padrao ou algum identificador para indicar que é uma string
        req.referenciaObj.NomeObjeto = jsonString["referenciaObj"].get<string>();
    }
    req.metodoId = jsonString["metodoId"];
    req.parametros = jsonString["parametros"];
    
    return req;
}