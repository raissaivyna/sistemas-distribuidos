#include "ClienteAPI.hpp"

#include <iostream>
#include <nlohmann/json.hpp>

using  namespace std;
using json = nlohmann::json;

int main(){
    ClienteAPI api;

    string BASE_URL = "http://localhost:8080/api";

    cout << "\n===LISTAR PRODUTOS===" << endl;
    cout << api.get(BASE_URL + "/produtos") << endl;

    cout << "\n === BUSCAR PRODUTO 1 ===" << endl;
    cout << api.get(BASE_URL + "/produtos/1") << endl;

    cout << "\n === BUSCAR ESPECIE ===" << endl;
    cout << api.get(BASE_URL + "/produtos/especie/Canino") << endl;

    json vacina;
    vacina["tipo"] = "VacinaPerecivel";
    vacina["nome"] = "Vacina Leishmaniose";
    vacina["preco"] = 150.0;
    vacina["fabricante"] = "MSD";
    vacina["registroMapa"] = "BR-099";
    vacina["especieAlvo"] = "Canino";
    vacina["viaAdministracao"] = "Subcutanea";
    vacina["dataValidade"] = "2025-12-31";
    vacina["armazenamento"] = "Refrigerado 2-8C";
    vacina["temperaturaMinima"] = 2.0;
    vacina["temperaturaMaxima"] = 8.0;
    cout << "\n === CADASTRAR VACINA ===" << endl;
    cout << api.post(BASE_URL + "/produtos", vacina.dump()) << endl;

    cout << "\n=== VALOR TOTAL ===" << endl;
    cout << api.get(BASE_URL + "/produtos/valor-total") << endl;

    cout << "\n === RELATORIO GERAL ===" << endl;
    cout << api.get(BASE_URL + "/relatorios/geral") << endl;
}