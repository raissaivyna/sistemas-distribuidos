#include <fstream>
#include "VacinaPerecivelInputStream.hpp"

int main(){
    ifstream arquivo("saida.bin", ios::binary);
    if (!arquivo) {
        cerr << "Erro ao abrir o arquivo!" << endl;
        return 1;
    }

    VacinaPerecivelInputStream input(arquivo);

    int quantidade;
    VacinaPerecivel* vacinas = input.ler(quantidade);

    for (int i = 0; i < quantidade; i++)
    {
        cout << vacinas[i].toString() << endl;
    }
    
    delete[] vacinas;
    arquivo.close();
    return 0;
}