#include "VacinaPerecivelOutputStream.hpp"

VacinaPerecivelOutputStream::VacinaPerecivelOutputStream(VacinaPerecivel* vacinas, int quantidade, ostream& destino)
    : vacinas(vacinas), quantidade(quantidade), destino(destino)
{
}

void VacinaPerecivelOutputStream::enviar(){
    // envia quantidade
    writeInt(quantidade);

    for (int i = 0; i < quantidade; i++)
    {
        VacinaPerecivel& v = vacinas[i];

        //id
        writeInt(v.getId());
        //nome
        writeString(v.getNome());
        //preco
        writeDouble(v.getPreco());
        //data validade
        writeString(v.getDataValidade());
        //requisito armazenamento
        writeString(v.getRequisitoArmazenamento());
        //temperatura minima
        writeDouble(v.getTemperaturaMinima());
        //temperatura maxima
        writeDouble(v.getTemperaturaMaxima());
    }
    destino.flush();
}

void VacinaPerecivelOutputStream::writeInt(int valor) {
    destino.put((valor >> 24) & 0xFF);
    destino.put((valor >> 16) & 0xFF);
    destino.put((valor >> 8) & 0xFF);
    destino.put(valor & 0xFF);
}

void VacinaPerecivelOutputStream::writeDouble(double valor) {
    uint64_t bits;
    memcpy(&bits, &valor, sizeof(double));

    for (int i = 56; i >= 0; i -= 8) {
        destino.put((bits >> i) & 0xFF);
    }
    
}

void VacinaPerecivelOutputStream::writeString(const std::string& valor) {
    int tamanho = valor.size();
    
    unsigned char b1 = (tamanho >> 8) & 0xFF;
    unsigned char b2 = tamanho & 0xFF;

    destino.write((char*)&b1, 1);
    destino.write((char*)&b2, 1);

    destino.write(valor.c_str(), tamanho);
}