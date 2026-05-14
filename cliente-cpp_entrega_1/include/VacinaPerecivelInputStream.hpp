#ifndef VACINA_PERECIVELINPUTSTREAM_HPP
#define VACINA_PERECIVELINPUTSTREAM_HPP

#include <cstring>
#include "VacinaPerecivel.hpp"
using namespace std;

class VacinaPerecivelInputStream
{
private:
    istream& origem;
public:
    VacinaPerecivelInputStream(istream& in);
    VacinaPerecivel* ler(int& quantidade);

    private:
    int readInt();
    double readDouble();
    string readString();
};




#endif