#!/bin/bash
# run.sh — Trabalho 3: API REST Clínica Veterinária
#
# Uso:
#   bash run.sh servidor    → compila e inicia o servidor (porta 8080)
#   bash run.sh python      → executa cliente Python
#   bash run.sh cpp         → compila e executa cliente C++

case "$1" in

  servidor)
    echo "Compilando servidor..."
    cd servidor
    mvn package -q
    echo "Iniciando servidor na porta 8080..."
    java -jar target/servidor-api-1.0.0-jar-with-dependencies.jar
    ;;

  python)
    echo "Executando cliente Python..."
    cd cliente-python
    pip install requests -q
    python3 cliente_python.py
    ;;

  cpp)
    echo "Compilando cliente C++..."
    cd cliente-cpp
    g++ -o cliente_cpp cliente_cpp.cpp -lcurl
    echo "Executando cliente C++..."
    ./cliente_cpp
    ;;

  *)
    echo "Uso: bash run.sh servidor | python | cpp"
    ;;
esac