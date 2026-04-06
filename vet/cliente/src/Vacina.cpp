#include "../include/Vacina.hpp"

Vacina::Vacina(){};

Vacina::Vacina(string n, float p, int t, bool per)
    : ProdutoVeterinario(n, p, t){
        perecivel = per;
    }

bool Vacina::ehPerecivel(){
    return perecivel;
}