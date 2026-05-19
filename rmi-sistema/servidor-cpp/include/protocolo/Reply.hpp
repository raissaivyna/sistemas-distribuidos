#ifndef REPLY_HPP
#define REPLY_HPP

#pragma once
#include <string>
#include <nlohmann/json.hpp>

using json = nlohmann::json;
using namespace std;

class Reply
{

public:
    int requestId;
    string status;
    json resultado;
    json toJson() const;
    static Reply fromJson(const json& j);
};



#endif