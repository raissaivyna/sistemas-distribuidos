#include "VacinaPerecivelInputStream.hpp"

int main(){
    VacinaPerecivelInputStream input(cin);

    int quantidade;
    VacinaPerecivel* vacinas = input.ler(quantidade);

    for (int i = 0; i < quantidade; i++)
    {
        cout << vacinas[i].toString() << endl;
    }
    
    delete[] vacinas;
    return 0;
}