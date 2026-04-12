#include <iostream>
#include "ProdutoServico.hpp"
#include "EstoqueServico.hpp"
#include "VacinaPerecivel.hpp"
#include "VacinaNaoPerecivel.hpp"
#include "ProdutoQuimioterapico.hpp"

using namespace std;

int main() {

    ProdutoServico produtoService;
    EstoqueServico estoqueService;

    // 🏪 criando estoque
    Estoque e1 = estoqueService.criarEstoque("Fortaleza");

    // 💉 criando produtos

    // vencido
    VacinaPerecivel* vacina1 = new VacinaPerecivel(
        0, "Vacina A", 50.0, "Pfizer",
        "123", "Bovino", "Injetavel",
        "Virus", "A1", 10,
        "01/01/2020", "Geladeira", 2, 8
    );

    // não vencido
    VacinaPerecivel* vacina2 = new VacinaPerecivel(
        0, "Vacina B", 70.0, "Butantan",
        "456", "Canino", "Oral",
        "Bacteria", "B2", 5,
        "01/01/2030", "Freezer", 1, 5
    );

    auto* quimio = new ProdutoQuimioterapico(
        0, "Antibiotico X", 30.0, "LabX",
        "789", "Felino", "Oral",
        "Amoxicilina", 10.0, "Antibiotico", true
    );

    // 📦 cadastrar produtos
    produtoService.cadastrar(vacina1);
    produtoService.cadastrar(vacina2);
    produtoService.cadastrar(quimio);

    // 📥 entrada no estoque
    estoqueService.entradaProduto(e1.getId(), vacina1);
    estoqueService.entradaProduto(e1.getId(), vacina2);
    estoqueService.entradaProduto(e1.getId(), quimio);

    // 📊 relatório
    estoqueService.relatorioGeral();

    // 🚨 alerta de vencidos
    estoqueService.alertarVencidos();

    // 💸 valor total
    cout << "\nValor total dos produtos: "
         << produtoService.calcularValorTotal() << endl;

    // 📤 remover produto
    estoqueService.saidaProduto(e1.getId(), vacina1->getId());

    // 📊 relatório depois da remoção
    estoqueService.relatorioGeral();

    return 0;
}