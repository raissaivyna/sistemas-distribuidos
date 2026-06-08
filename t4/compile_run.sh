#!/bin/bash
# compile_run.sh — Trabalho 4: Publish-Subscribe
#
# Uso:
#   bash compile_run.sh        → compila
#   bash compile_run.sh demo   → compila + executa demonstração

SRC="src/main/java"
OUT="out"

echo "Compilando..."
mkdir -p $OUT

javac -encoding UTF-8 -d $OUT \
  $SRC/evento/Evento.java \
  $SRC/broker/Broker.java \
  $SRC/publicador/PublicadorClinica.java \
  $SRC/assinante/AssinanteVeterinario.java \
  $SRC/DemoT4.java

if [ $? -ne 0 ]; then echo "Erro de compilacao."; exit 1; fi
echo "Compilado com sucesso!"
echo ""

case "$1" in
  demo) java -cp $OUT DemoT4 ;;
  *)    echo "Use: bash compile_run.sh demo" ;;
esac