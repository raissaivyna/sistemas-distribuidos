#!/bin/bash

# ─────────────────────────────────────────────────────────────
#  montar_projeto.sh
#
#  O que faz:
#    1. Clona o repositório C++ do GitHub (branch parte_2-RMI-servidor)
#    2. Copia o servidor C++ para rmi-sistema/servidor-cpp/
#    3. Copia o cliente Java local para rmi-sistema/cliente-java/
#    4. Gera README.md e compile_run.sh
#
#  Uso:
#    bash montar_projeto.sh
#
#  Pré-requisitos:
#    - git instalado
#    - Java (javac) instalado
#    - g++ e make instalados (para o servidor C++)
# ─────────────────────────────────────────────────────────────

GITHUB_URL="https://github.com/raissaivyna/sistemas-distribuidos"
BRANCH="parte_2-RMI-servidor"
CPP_SUBDIR="servidor-cpp"

JAVA_SRC="/home/raissaivyna/Documentos/sistemas-distribuidos-entrega2/rmi/src"

DEST="rmi-sistema"
CLONE_TMP="__clone_tmp__"

# ── Verificações iniciais ─────────────────────────────────────

echo "╔══════════════════════════════════════════════╗"
echo "║        Montando projeto RMI unificado        ║"
echo "╚══════════════════════════════════════════════╝"
echo ""

if ! command -v git &> /dev/null; then
    echo "ERRO: git não encontrado. Instale com: sudo apt install git"
    exit 1
fi

if [ ! -d "$JAVA_SRC" ]; then
    echo "ERRO: pasta Java não encontrada em '$JAVA_SRC'"
    echo "Verifique o caminho e tente novamente."
    exit 1
fi

if [ -d "$DEST" ]; then
    echo "AVISO: pasta '$DEST' já existe. Removendo para recriar..."
    rm -rf "$DEST"
fi

# ── Passo 1: clonar repositório C++ ──────────────────────────

echo "[1/4] Clonando servidor C++ do GitHub..."
echo "      branch : $BRANCH"
echo "      subdir : $CPP_SUBDIR"
echo ""

git clone --branch "$BRANCH" --single-branch "$GITHUB_URL" "$CLONE_TMP"

if [ $? -ne 0 ]; then
    echo "ERRO: falha ao clonar o repositório."
    echo "Verifique sua conexão e se a URL está correta."
    exit 1
fi

echo ""
echo "      Clone concluído."

# ── Passo 2: copiar servidor C++ ─────────────────────────────

echo ""
echo "[2/4] Copiando servidor C++..."

mkdir -p "$DEST/servidor-cpp"
cp -r "$CLONE_TMP/$CPP_SUBDIR/"* "$DEST/servidor-cpp/"

echo "      Copiado para $DEST/servidor-cpp/"

# remove clone temporário
rm -rf "$CLONE_TMP"

# ── Passo 3: copiar cliente Java ──────────────────────────────

echo ""
echo "[3/4] Copiando cliente Java de $JAVA_SRC ..."

mkdir -p "$DEST/cliente-java/src"

for PKG in entidade rmi serializacao servidor cliente; do
    if [ -d "$JAVA_SRC/$PKG" ]; then
        cp -r "$JAVA_SRC/$PKG" "$DEST/cliente-java/src/"
        echo "      src/$PKG/ copiado."
    else
        echo "      AVISO: src/$PKG/ não encontrado — pulando."
    fi
done

# ── Passo 4: gerar compile_run.sh ────────────────────────────

echo ""
echo "[4/4] Gerando arquivos de suporte..."

cat > "$DEST/cliente-java/compile_run.sh" << 'EOF'
#!/bin/bash
SRC="src"
OUT="out"
echo "Compilando..."
mkdir -p $OUT
javac -encoding UTF-8 -d $OUT \
  $SRC/entidade/Perecivel.java \
  $SRC/entidade/Produto.java \
  $SRC/entidade/ProdutoVeterinario.java \
  $SRC/entidade/VacinaPerecivel.java \
  $SRC/entidade/Estoque.java \
  $SRC/entidade/PedidoReposicao.java \
  $SRC/rmi/RemoteObjectRef.java \
  $SRC/rmi/Message.java \
  $SRC/rmi/RequestReplyProtocol.java \
  $SRC/serializacao/JSON.java \
  $SRC/servidor/ClinicaRemota.java \
  $SRC/servidor/ClinicaImpl.java \
  $SRC/servidor/EstoqueImpl.java \
  $SRC/servidor/ServidorRMI.java \
  $SRC/cliente/ClienteRMI.java
if [ $? -ne 0 ]; then echo "Erro de compilacao."; exit 1; fi
echo "Compilado com sucesso!"
echo ""
case "$1" in
  servidor) echo "Iniciando ServidorRMI Java — porta 8080..."
            java -cp $OUT servidor.ServidorRMI ;;
  cliente)  java -cp $OUT cliente.ClienteRMI ;;
  *)        echo "Uso: bash compile_run.sh servidor | cliente" ;;
esac
EOF
chmod +x "$DEST/cliente-java/compile_run.sh"

# ── Gerar README.md ───────────────────────────────────────────

cat > "$DEST/README.md" << 'EOF'
# RMI Sistema — Servidor C++ + Cliente Java

Projeto de sistemas distribuídos com comunicação RMI via protocolo
requisição-resposta (seção 5.2 do livro texto).

O **servidor** é implementado em **C++** e o **cliente** em **Java**.
Ambos se comunicam via **TCP na porta 8080** trocando mensagens **JSON**.

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
EOF

# ── Resultado final ───────────────────────────────────────────

echo ""
echo "╔══════════════════════════════════════════════╗"
echo "║              Projeto montado!                ║"
echo "╚══════════════════════════════════════════════╝"
echo ""
echo "Estrutura criada em '$DEST/':"
echo ""
find "$DEST" -not -path "*/out/*" -not -path "*/.git/*" | sort | \
    awk -F/ '{
        depth = NF - 1
        indent = ""
        for (i = 0; i < depth; i++) indent = indent "  "
        print indent "└── " $NF
    }'
echo ""
echo "Próximos passos:"
echo "  1. Servidor C++ → cd $DEST/servidor-cpp/src && make && ./rmi_servidor"
echo "  2. Cliente Java → cd $DEST/cliente-java    && bash compile_run.sh cliente"
