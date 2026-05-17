#!/bin/bash
# compile_run.sh — Trabalho 2: RMI Clínica Veterinária
#
# Uso:
#   bash compile_run.sh           → compila
#   bash compile_run.sh servidor  → inicia ServidorRMI (porta 7896 UDP)
#   bash compile_run.sh cliente   → inicia ClienteRMI

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
  $SRC/servidor/Skeleton.java \
  $SRC/servidor/ServidorRMI.java \
  $SRC/cliente/Stub.java \
  $SRC/cliente/ClienteRMI.java

if [ $? -ne 0 ]; then echo "Erro de compilação."; exit 1; fi
echo "Compilado com sucesso!"
echo ""

case "$1" in
  servidor) echo "Iniciando ServidorRMI — UDP porta 7896..."
            java -cp $OUT servidor.ServidorRMI ;;
  cliente)  echo "Iniciando ClienteRMI..."
            java -cp $OUT cliente.ClienteRMI ;;
  *)        echo "Use: bash compile_run.sh servidor | cliente" ;;
esac