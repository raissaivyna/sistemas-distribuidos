#include <iostream>
#include "../include/rmi/RMIServidor.hpp"

int main()
{
    RMIServidor servidor;
    servidor.iniciar(8080);
    return 0;
}