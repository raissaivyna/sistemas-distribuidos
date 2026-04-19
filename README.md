# 🐾 Sistema Distribuído — Clínica Veterinária

Projeto desenvolvido para a disciplina de Sistemas Distribuídos, com foco em comunicação cliente-servidor, serialização de dados e uso de múltiplos protocolos (TCP e UDP multicast).

Apresentação do projeto:
https://youtu.be/GZ2-29UqJ4U

---
## Desenvolvedoras
- Raissa Ívyna
- Francisca Ariane
---

## 📌 Objetivo

Implementar um sistema distribuído capaz de:

- Gerenciar produtos veterinários (vacinas, medicamentos, etc.)
- Realizar comunicação cliente-servidor via sockets (TCP)
- Enviar notificações em tempo real via multicast (UDP)
- Serializar e desserializar objetos (POJO)
- Manipular streams personalizados (InputStream/OutputStream)

---

## 🧱 Estrutura do Projeto

```

vet/
├── cliente/
│   ├── clienteClinicaCompleto.cpp   # cliente principal (TCP + UDP multicast)
│   ├── protocolo/
│   │   └── Protocolo.cpp           # empacotamento/desempacotamento
│   ├── src/
│   │   ├── Produto.cpp
│   │   ├── VacinaPerecivel.cpp
│   │   ├── ProdutoQuimioterapico.cpp
│   │   └── ...                     # classes POJO
│   ├── modelo/
│   │   ├── ProdutoServico.cpp      # regras de negócio
│   │   └── EstoqueServico.cpp
│   ├── stream/
│   │   ├── VacinaPerecivelOutputStream.cpp
│   │   └── VacinaPerecivelInputStream.cpp
│   └── multicast/
│       └── (receptor de alertas UDP)
│
├── src/                            # versão Java do servidor
│   ├── servidor/
│   ├── protocolo/
│   ├── pojo/
│   └── ...
│
├── vacinas.bin                     # arquivo binário de teste
└── compile_run.sh                 # script auxiliar

```

---

## 🧩 Tecnologias Utilizadas

- Java (Servidor)
- C++ (Cliente)
- Sockets TCP
- UDP Multicast
- Serialização manual (protocolo próprio)

---

## 🔄 Comunicação do Sistema

### 🔵 TCP (cliente-servidor)
Usado para:
- Listar produtos
- Buscar por espécie
- Cadastrar produtos
- Gerar relatórios

### 🟣 UDP Multicast
Usado para:
- Alertas (recall, promoção, vencimento)
- Notificações em tempo real para todos os clientes

---

## 📦 Protocolo de Comunicação

Formato da mensagem:

```

[1 byte]   operação
[4 bytes]  tamanho do payload
[N bytes]  payload (dados)

````

---

## 🧪 Funcionalidades Implementadas

- Cadastro de produtos
- Listagem e busca
- Verificação de produtos vencidos
- Envio de alertas multicast
- Janela de operação para pedidos
- Relatório de estoque

---

## 🧬 POJOs

Exemplos:
- VacinaPerecivel
- ProdutoQuimioterapico

---

## 🔁 Streams Customizados

### OutputStream
- Envia arrays de objetos POJO
- Testado com:
  - System.out
  - Arquivo
  - Socket TCP

### InputStream
- Lê dados serializados
- Reconstrói objetos

---

## ▶️ Como Executar

### 1. Compilar cliente (C++)
```bash
g++ cliente/clienteClinicaCompleto.cpp modelo/*.cpp stream/*.cpp src/*.cpp protocolo/*.cpp -Iinclude -o clienteClinicaCompleto
````

### 2. Executar cliente

```bash
./clienteClinicaCompleto
```

### 3. Executar servidor (Java)

```bash
javac -d out src/**/*.java
java -cp out servidor.ServidorClinicaCompleto
```

---

## 🌐 Execução em Rede

Para rodar em máquinas diferentes:

* Alterar o IP do servidor no cliente C++

```cpp
inet_pton(AF_INET, "10.10.239.205", &server.sin_addr);
```

* Garantir que ambas máquinas estejam na mesma rede

---

## ⚠️ Dificuldades Encontradas

* Configuração de rede (IP e conexão TCP)
* Problemas com multicast (dependência da rede)
* Integração entre Java e C++
* Implementação do protocolo binário
* Uso de threads para comunicação simultânea

---

## 💡 Conclusão

### ENTREGA 1:

* Comunicação via sockets
* Serialização de dados
* Concorrência
* Multicast


