"""
cliente_python.py — Cliente Python da API REST da Clínica Veterinária.
Requer: pip install requests

Executar: python3 cliente_python.py
"""

import requests
import json

BASE = "http://localhost:8080/api"

def sep(titulo):
    print(f"\n{'='*50}")
    print(f"  {titulo}")
    print('='*50)

def imprimir(resp):
    print(f"  Status: {resp.status_code}")
    try:
        print(f"  Resposta: {json.dumps(resp.json(), indent=2, ensure_ascii=False)}")
    except Exception:
        print(f"  Resposta: {resp.text}")

def main():
    print("========== Cliente Python — Clínica Veterinária ==========\n")

    # ── Produtos ─────────────────────────────────────────────────────────────

    sep("1 — GET /api/produtos (listar todos)")
    imprimir(requests.get(f"{BASE}/produtos"))

    sep("2 — GET /api/produtos/1 (buscar por id)")
    imprimir(requests.get(f"{BASE}/produtos/1"))

    sep("3 — GET /api/produtos/especie/Canino")
    imprimir(requests.get(f"{BASE}/produtos/especie/Canino"))

    sep("4 — GET /api/produtos/vencidos")
    imprimir(requests.get(f"{BASE}/produtos/vencidos"))

    sep("5 — GET /api/produtos/valor-total")
    imprimir(requests.get(f"{BASE}/produtos/valor-total"))

    sep("6 — POST /api/produtos (cadastrar nova vacina)")
    nova_vacina = {
        "tipo": "VacinaPerecivel",
        "nome": "Vacina Leishmaniose",
        "preco": 89.00,
        "fabricante": "MSD",
        "registroMapa": "BR-099",
        "especieAlvo": "Canino",
        "viaAdministracao": "Subcutanea",
        "dataValidade": "20/06/2027",
        "armazenamento": "Refrigerado 2-8C",
        "temperaturaMinima": 2.0,
        "temperaturaMaxima": 8.0
    }
    resp = requests.post(f"{BASE}/produtos", json=nova_vacina)
    imprimir(resp)
    novo_id = resp.json().get("id") if resp.ok else None

    sep("7 — GET /api/produtos (listar após cadastro)")
    imprimir(requests.get(f"{BASE}/produtos"))

    if novo_id:
        sep(f"8 — DELETE /api/produtos/{novo_id} (remover)")
        imprimir(requests.delete(f"{BASE}/produtos/{novo_id}"))

    sep("9 — GET /api/produtos/999 (id inexistente)")
    imprimir(requests.get(f"{BASE}/produtos/999"))

    # ── Estoques ─────────────────────────────────────────────────────────────

    sep("10 — POST /api/estoques (criar estoque)")
    resp = requests.post(f"{BASE}/estoques", json={"local": "Deposito Central"})
    imprimir(resp)
    estoque_id = resp.json().get("id") if resp.ok else 1

    sep("11 — GET /api/estoques (listar)")
    imprimir(requests.get(f"{BASE}/estoques"))

    sep(f"12 — POST /api/estoques/{estoque_id}/entrada/1 (entrada produto)")
    imprimir(requests.post(f"{BASE}/estoques/{estoque_id}/entrada/1"))

    sep("13 — GET /api/estoques/vencidos (alertar vencidos)")
    imprimir(requests.get(f"{BASE}/estoques/vencidos"))

    sep(f"14 — DELETE /api/estoques/{estoque_id}/saida/1 (saída produto)")
    imprimir(requests.delete(f"{BASE}/estoques/{estoque_id}/saida/1"))

    # ── Relatórios ────────────────────────────────────────────────────────────

    sep("15 — GET /api/relatorios/geral")
    imprimir(requests.get(f"{BASE}/relatorios/geral"))

    sep("16 — GET /api/relatorios/saude-estoque")
    imprimir(requests.get(f"{BASE}/relatorios/saude-estoque"))

    sep("17 — GET /api/relatorios/especie/Bovino")
    imprimir(requests.get(f"{BASE}/relatorios/especie/Bovino"))

    print("\n========== Fim dos testes Python ==========")

if __name__ == "__main__":
    main()