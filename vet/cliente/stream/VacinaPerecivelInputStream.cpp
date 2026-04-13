#include "VacinaPerecivelInputStream.hpp"

VacinaPerecivelInputStream::VacinaPerecivelInputStream(istream& in) : origem(in)
{
}

VacinaPerecivel* VacinaPerecivelInputStream::ler(int& quantidade) {
    quantidade = readInt();
    VacinaPerecivel* vacinas = new VacinaPerecivel[quantidade];

    for (int i = 0; i < quantidade; i++)
    {
        //id
        int id = readInt();
        //nome
        string nome = readString();
        //preco
        double preco = readDouble();
        //data validade
        string dataValidade = readString();
        //requisito armazenamento
        string requisitoArmazenamento = readString();
        //temperatura minima
        double temperaturaMinima = readDouble();
        //temperatura maxima
        double temperaturaMaxima = readDouble();

        VacinaPerecivel v;
        v.setDataValidade(dataValidade);
        v.setRequisitoArmazenamento(requisitoArmazenamento);
        v.setTemperaturaMinima(temperaturaMinima);
        v.setTemperaturaMaxima(temperaturaMaxima);
        v.setId(id);
        v.setNome(nome);
        v.setPreco(preco);
        vacinas[i] = v; 
    }

    return vacinas;
}

int VacinaPerecivelInputStream::readInt() {
    unsigned char b1 = origem.get();
    unsigned char b2 = origem.get();
    unsigned char b3 = origem.get();
    unsigned char b4 = origem.get();
    return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;

}

double VacinaPerecivelInputStream::readDouble() {
    long long bits = 0;

    for (int i = 56; i >= 0; i -= 8) {
        unsigned char byte = origem.get();
        bits |= ((long long)byte << i);
    }
    double valor;
    memcpy(&valor, &bits, sizeof(double));
    return valor;
}

string VacinaPerecivelInputStream::readString() {
    unsigned char b1 = origem.get();
    unsigned char b2 = origem.get();

    int tamanho = (b1 << 8) | b2;

    char* buffer = new char[tamanho];
    origem.read(buffer, tamanho);

    string valor(buffer, tamanho);
    delete[] buffer;

    return valor;
}