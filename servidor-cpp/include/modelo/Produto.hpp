#ifndef PRODUTO_HPP
#define PRODUTO_HPP

#include <string>
#include <vector>
#include <iostream>
#include <nlohmann/json.hpp>
using json = nlohmann::json;
using namespace std;


class Produto
{
private:
    int id;
    string nome;
    double preco;
    string fabricante;

public:
    Produto();
    Produto(int i,const string& n, const double& p, const string& f);  // i - id, n - nome, p - preco, f - fabricante
    virtual ~Produto() {};
    virtual json toJson() const;

    int getId() const;
    void setId(int i);
    string getNome() const;
    void setNome(const string& n);
    double getPreco() const;
    void setPreco(double p);
    string getFabricante() const;
    void setFabricante(const string& f);
    string toString() const;
};


#endif