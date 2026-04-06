#include <iostream>
#include <sstream>

#include "../include/Vacina.hpp"
#include "../include/ProdutoOutputStream.hpp"
#include "ClienteTCP.hpp"

int main(){
    Vacina vacinas[2] = {
        Vacina("Raiva", 50.0, 2, true),
        Vacina("V10", 80.0, 2, false)
    };

    // serializa
    std::stringstream ss;
    ProdutoOutputStream out(vacinas, 2, ss);
    out.writeAll();

    std::string buffer = ss.str();

    // cliente TCP, conexao
    ClienteTCP cliente;

    if (!cliente.conectar("127.0.0.1", 12345))
    {
        std::cout << "erro ao conectar\n";
        return 1;
    }
    
    cliente.enviar(buffer);

    std::cout << "dados enviados com sucesso! \n";

    cliente.fechar();

    return 0;
}