#ifndef PRODUTO_HPP
#define PRODUTO_HPP

#include <string>
using namespace std;

class Produto
{
protected:
    string nome;
    float preco;
public:
    Produto();
    Produto(string n, float p);  // n - nome, p - preco

    string getNome();
    float getPreco();
};


#endif