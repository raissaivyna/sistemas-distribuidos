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
