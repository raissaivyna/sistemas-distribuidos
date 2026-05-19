# RMI Sistema — Servidor C++ + Cliente Java

Projeto de sistemas distribuídos com comunicação RMI via protocolo
requisição-resposta (seção 5.2 do livro texto).

O **servidor** é implementado em **C++** e o **cliente** em **Java**.
Ambos se comunicam via **TCP na porta 8080** trocando mensagens **JSON**.

# [Relatório da Entrega 2](./Relatorio_Trabalho2_RMI.docx.pdf)

---

## Estrutura

```
rmi-sistema/
├── servidor-cpp/          ← servidor RMI em C++
│   ├── include/
│   │   ├── modelo/        ← headers das entidades
│   │   ├── rmi/           ← headers do protocolo RMI
│   │   └── utils/         ← utilitários (JSON, Socket)
│   └── src/
│       ├── main.cpp       ← ponto de entrada do servidor
│       ├── makefile
│       ├── modelo/        ← implementação das entidades
│       ├── protocolo/     ← Request e Reply
│       ├── rmi/           ← Expedidor e RMIServidor
│       ├── servico/       ← ProdutoServico e EstoqueServico
│       └── skeleton/      ← ProdutoSkeleton e EstoqueSkeleton
└── cliente-java/          ← cliente RMI em Java
    ├── compile_run.sh
    └── src/
        ├── entidade/      ← Produto, VacinaPerecivel, Estoque...
        ├── rmi/           ← RemoteObjectRef, RequestReplyProtocol
        ├── serializacao/  ← JSON.java
        ├── servidor/      ← ClinicaRemota, ClinicaImpl, EstoqueImpl, ServidorRMI
        └── cliente/       ← ClienteRMI
```

---

## Como executar

### Terminal 1 — Servidor C++

```bash
cd rmi-sistema/servidor-cpp/src
make
./rmi_servidor
```

O servidor ficará escutando na porta **8080**.

### Terminal 2 — Cliente Java

```bash
cd rmi-sistema/cliente-java
bash compile_run.sh cliente
```

---

## Formato das mensagens (JSON)

### Request (cliente → servidor)

```json
{
    "TipoMensagem": "Request",
    "requestId": 1,
    "referenciaObj": { "objetoId": 1, "NomeObjeto": "ProdutoService" },
    "metodoId": "buscarPorId",
    "parametros": { "id": 1 }
}
```

### Reply (servidor → cliente)

```json
{
    "requestId": 1,
    "status": "OK",
    "resultado": { ... }
}
```

---

## Serviços disponíveis

### ProdutoService

| Método | Parâmetros | Retorno |
|---|---|---|
| `buscarPorId` | `{"id": N}` | objeto Produto |
| `listarTodos` | `{}` | lista de Produtos |
| `buscarPorEspecie` | `{"especie": "Canino"}` | lista de Produtos |
| `calcularValorTotal` | `{}` | número |
| `remover` | `{"id": N}` | status |
| `listarVencidos` | `{}` | lista de VacinaPerecivel |

### EstoqueService

| Método | Parâmetros | Retorno |
|---|---|---|
| `criarEstoque` | `{"local": "nome"}` | objeto Estoque |
| `listarEstoques` | `{}` | lista de Estoques |
| `entradaProduto` | `{"estoqueId": N, "produtoId": N}` | status |
| `saidaProduto` | `{"estoqueId": N, "produtoId": N}` | status |
| `alertarVencidos` | `{}` | lista de vencidos |
