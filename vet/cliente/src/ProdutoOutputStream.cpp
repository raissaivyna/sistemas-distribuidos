#include "../include/ProdutoOutputStream.hpp"

ProdutoOutputStream::ProdutoOutputStream(Vacina* l, int quantidade, std::ostream& out)
    : lista(l), qtd(quantidade), destino(out){};

void ProdutoOutputStream::writeAll(){
    for (int i = 0; i < qtd; i++)
    {
        std::string nome = lista[i].getNome();
        int tamNome = nome.size();

        float preco = lista[i].getPreco();
        int tipo = lista[i].getTipo();
        bool perecivel = lista[i].ehPerecivel();

        destino.write(reinterpret_cast<const char*>(&tamNome), sizeof(tamNome));
        destino.write(nome.c_str(), tamNome);

        destino.write(reinterpret_cast<const char*>(&preco), sizeof(preco));
        destino.write(reinterpret_cast<const char*>(&tipo), sizeof(tipo));
        destino.write(reinterpret_cast<const char*>(&perecivel), sizeof(perecivel));
    }
    
}