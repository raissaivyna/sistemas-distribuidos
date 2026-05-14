#ifndef REMOTE_OBJECT_REF_HPP
#define REMOTE_OBJECT_REF_HPP

#pragma once
#include <string>

using namespace std;

class RemoteObjectRef
{

public:
    int objetoId;
    string NomeObjeto;

    RemoteObjectRef(){}

    RemoteObjectRef(int id, const string& NomeObjeto){
        this->objetoId = id;
        this->NomeObjeto = NomeObjeto;
    };
};




#endif