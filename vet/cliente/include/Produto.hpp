#ifndef PRODUTO_HPP
#define PRODUTO_HPP

#include <string>
#include <vector>
#include <iostream>
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