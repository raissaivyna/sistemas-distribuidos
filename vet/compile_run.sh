#!/bin/bash
# compile_run.sh — Compila e executa o projeto
# Uso:
#   bash compile_run.sh            → compila + roda Main (POJOs)
#   bash compile_run.sh stream     → compila + roda TesteOutputStream
#   bash compile_run.sh servidor   → compila + inicia ServidorTCP

SRC="src"
OUT="out"

echo "🔨 Compilando todo o projeto..."
mkdir -p $OUT

javac -encoding UTF-8 -d $OUT \
  $SRC/pojo/Perecivel.java \
  $SRC/pojo/Produto.java \
  $SRC/pojo/ProdutoVeterinario.java \
  $SRC/pojo/ProdutoQuimioterapico.java \
  $SRC/pojo/ProdutoBiologico.java \
  $SRC/pojo/VacinaPerecivel.java \
  $SRC/pojo/VacinaNaoPerecivel.java \
  $SRC/pojo/Estoque.java \
  $SRC/modelo/ProdutoServico.java \
  $SRC/modelo/EstoqueServico.java \
  $SRC/stream/VacinaPereceívelOutputStream.java \
  $SRC/stream/VacinaPerecívelInputStream.java \
  $SRC/servidor/ServidorTCP.java \
  $SRC/servidor/ServidorTCPInputStream.java \
  $SRC/Main.java \
  $SRC/TesteOutputStream.java \
  $SRC/TesteInputStream.java

if [ $? -ne 0 ]; then
  echo "❌ Erro de compilação."
  exit 1
fi

echo "✅ Compilado com sucesso!"
echo ""

case "$1" in
  stream)
    echo "▶️  Rodando TesteOutputStream (3 destinos)..."
    echo "----------------------------------------"
    java -cp $OUT TesteOutputStream
    ;;
  inputstream)
    echo "▶️  Rodando TesteInputStream (arquivo + TCP)..."
    echo "----------------------------------------"
    java -cp $OUT TesteInputStream
    ;;
  stdin)
    echo "▶️  Modo pipe — gera bytes e lê via stdin..."
    echo "----------------------------------------"
    java -cp $OUT TesteOutputStream 2>/dev/null | java -cp $OUT TesteInputStream stdin
    ;;
  servidor)
    echo "▶️  Iniciando ServidorTCP (raw) na porta 7896..."
    echo "----------------------------------------"
    java -cp $OUT servidor.ServidorTCP
    ;;
  servidor2)
    echo "▶️  Iniciando ServidorTCPInputStream na porta 7896..."
    echo "----------------------------------------"
    java -cp $OUT servidor.ServidorTCPInputStream
    ;;
  *)
    echo "▶️  Rodando Main (teste de POJOs e serviços)..."
    echo "----------------------------------------"
    java -cp $OUT Main
    ;;
esac