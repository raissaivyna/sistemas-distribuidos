#ifndef VACINA_PERECIVEL_OUTPUTSTREAM_HPP
#define VACINA_PERECIVEL_OUTPUTSTREAM_HPP
#include "VacinaPerecivel.hpp"
#include <fstream>
#include <cstring>


class VacinaPerecivelOutputStream
{
private:
    VacinaPerecivel* vacinas;
    int quantidade;
    ostream& destino;
    
public:
    VacinaPerecivelOutputStream(VacinaPerecivel* vacinas, int quantidade, ostream& destino);
    void enviar();
    
    private:
    void writeInt(int valor);
    void writeString(const string& valor);
    void writeDouble(double valor);
};


#endif // VACINA_PERECIVEL_OUTPUTSTREAM_HPP