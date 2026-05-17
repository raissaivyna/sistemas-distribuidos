#include "../include/protocolo/Reply.hpp"

json Reply::toJson() const
{
    json j;
    j["requestId"] = requestId;
    j["status"] = status;
    j["resultado"] = resultado;
    return j;
};

Reply Reply::fromJson(const json& j)
{
    Reply reply;
    reply.requestId = j["requestId"];
    reply.status = j["status"];
    reply.resultado = j["resultado"];
    return reply;
}

