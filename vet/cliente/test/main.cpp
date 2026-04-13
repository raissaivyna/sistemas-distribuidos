// g++ src/*.cpp modelo/*.cpp stream/*.cpp test/main.cpp -Iinclude -o cliente.exe

#include "VacinaPerecivelOutputStream.hpp"
#include <fstream>

int main() {

    VacinaPerecivel vacinas[2] = {
        VacinaPerecivel(1, "Vacina A", 10.5, "Pfizer", "123", "humano", "oral",
        "virus", "A", 2, "10/10/2026", "geladeira", 2.0, 8.0),

        VacinaPerecivel(2, "Vacina B", 20.0, "Butantan", "456", "animal", "injeção",
        "bacteria", "B", 1, "05/05/2025", "freezer", -5.0, 2.0)
    };

    std::ofstream file("saida.bin", ios::binary);

    VacinaPerecivelOutputStream stream(vacinas, 1, std::cout);
    stream.enviar();

    VacinaPerecivelOutputStream stream2(vacinas, 2, file);
    stream2.enviar();
    

    file.close();

    return 0;
}