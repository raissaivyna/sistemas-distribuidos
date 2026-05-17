#!/bin/bash
# compile_run.sh — Clinica Veterinaria — Sistema Distribuido Completo
#
# Modos:
#   bash compile_run.sh                    → Main (POJOs)
#   bash compile_run.sh stream             → TesteOutputStream
#   bash compile_run.sh inputstream        → TesteInputStream
#   bash compile_run.sh servidorcompleto   → ServidorClinicaCompleto (porta 7896)
#   bash compile_run.sh clientecompleto    → ClienteClinicaCompleto  (TCP + multicast)
#   bash compile_run.sh receptor           → ReceptorAlertas standalone (UDP)
#   bash compile_run.sh serializacao       → TesteSerializacao (Fase 5)
#   bash compile_run.sh servidorclinica    → ServidorClinica simples

SRC="src"
OUT="out"

echo "Compilando..."
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
  "$SRC/stream/VacinaPerecivelOutputStream.java" \
  $SRC/stream/VacinaPerecivelInputStream.java \
  $SRC/protocolo/Protocolo.java \
  $SRC/protocolo/SerializadorJSON.java \
  $SRC/multicast/AlertaClinica.java \
  $SRC/multicast/ReceptorAlertas.java \
  $SRC/multicast/JanelaOperacao.java \
  $SRC/servidor/ServidorTCP.java \
  $SRC/servidor/ServidorTCPInputStream.java \
  $SRC/servidor/ServidorClinica.java \
  $SRC/servidor/ServidorClinicaCompleto.java \
  $SRC/cliente/ClienteClinica.java \
  $SRC/cliente/ClienteClinicaCompleto.java \
  $SRC/Main.java \
  $SRC/TesteOutputStream.java \
  $SRC/TesteInputStream.java \
  $SRC/TesteSerializacao.java

if [ $? -ne 0 ]; then echo "Erro de compilacao."; exit 1; fi
echo "Compilado com sucesso!"
echo ""

case "$1" in
  stream)           java -cp $OUT TesteOutputStream ;;
  inputstream)      java -cp $OUT TesteInputStream ;;
  servidorcompleto) echo "ServidorClinicaCompleto — porta 7896"
                    java -cp $OUT servidor.ServidorClinicaCompleto ;;
  clientecompleto)  echo "ClienteClinicaCompleto — TCP + UDP multicast"
                    java -cp $OUT cliente.ClienteClinicaCompleto ;;
  receptor)         echo "ReceptorAlertas standalone — UDP multicast"
                    java -cp $OUT multicast.ReceptorAlertas ;;
  serializacao)     java -cp $OUT TesteSerializacao ;;
  servidorclinica)  java -cp $OUT servidor.ServidorClinica ;;
  clienteclinica)   java -cp $OUT cliente.ClienteClinica ;;
  *)                java -cp $OUT Main ;;
esac