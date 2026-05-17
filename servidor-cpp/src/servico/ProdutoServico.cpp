#include "../include/services/ProdutoServico.hpp"

ProdutoServico::ProdutoServico() {
    Produto* p1 = new Produto(0, "Ração para Cães", 150.0, "PetFood Co.");
    Produto* p2 = new Produto(0, "Ração para Gatos", 120.0, "PetFood Co.");
    Produto* p3 = new ProdutoVeterinario(0, "Antipulgas para Cães", 80.0, "Veterinary Inc.", "REG123", "Cães", "Tópica");
    Produto* p4 = new VacinaPerecivel(0, "Vacina para Gatos", 200.0, "VaccineCorp", "REG456", "Gatos", "Intramuscular", "Vírus da Raiva", "Sorotipo A", 1, "2024-12-31", "Refrigerado", 2.0, 8.0);
    this->cadastrar(p1);
    this->cadastrar(p2);
    this->cadastrar(p3);
    this->cadastrar(p4);
}

Produto* ProdutoServico::cadastrar(Produto* p) {
    p->setId(proximoId++);
    repositorio.push_back(p);
    cout << "[SERVICO] Produto cadastrado: " << p->getNome() << endl;
    return p;
}

Produto* ProdutoServico::buscarPorId(int id) {
    for (auto* produto : repositorio) {
        if (produto->getId() == id) {
            return produto;
        }
    }
    return nullptr;
}

vector<Produto*> ProdutoServico::listarTodos() const {
    return this->repositorio;
}

bool ProdutoServico::remover(int id) {
    for (auto it = this->repositorio.begin(); it != this->repositorio.end(); ++it) {
        if ((*it)->getId() == id) {
            delete *it; // libera a memoria do produto removido
            this->repositorio.erase(it);
            cout << "[SERVIÇO] Produto id=" << id << " removido." << endl;
            return true;
        }
    }
    return false;
}

vector<VacinaPerecivel*> ProdutoServico::listarVacinasPereciveis() const {
    vector<VacinaPerecivel*> vacinas;
    for (auto* produto : repositorio) {
        if (auto* vacina = dynamic_cast<VacinaPerecivel*>(produto)) {
            if (vacina->ehVencido())
            {
                vacinas.push_back(vacina);
            }
            
        }
    }
    return vacinas;
}

json ProdutoServico::serializar(Produto* p) const {
    json j;
    j["id"] = p->getId();
    j["nome"] = p->getNome();
    j["preco"] = p->getPreco();
    j["fabricante"] = p->getFabricante();
    j["tipo"] = string(typeid(*p).name());
    return j;
}

int ProdutoServico::getTotalProdutos() const {
    return this->repositorio.size();
}

vector<ProdutoVeterinario*> ProdutoServico::buscarPorEspecie(const string& especie) const {
    vector<ProdutoVeterinario*> produtosEncontrados;
    for (auto* produto : repositorio) {
        if (auto* prodVet = dynamic_cast<ProdutoVeterinario*>(produto)) {
            if (prodVet->getEspecieAlvo() == especie) {
                produtosEncontrados.push_back(prodVet);
            }
        }
    }
    return produtosEncontrados;
}

double ProdutoServico::calcularValorTotal() const {
    double total = 0.0;
    for (auto* produto : repositorio) {
        total += produto->getPreco();
    }
    return total;
}

ProdutoServico::~ProdutoServico() {
    for (auto* produto : repositorio) {
        delete produto;
    }
}