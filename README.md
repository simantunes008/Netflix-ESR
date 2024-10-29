# ESR2024

## Links úteis:
- https://repositorium.sdum.uminho.pt/bitstream/1822/74179/1/Miguel%20Gil%20Pires%20da%20Silva.pdf

## Como executar o programa no CORE:
- Abrir uma janela bash;
- Ir para a pasta do projeto;
- Compilar os fichieros da pasta oNode;
```bash
cd /home/core/ESR2024/
javac oNode/*.java
```
- Executar um node como Bootstrapper;
```bash
java oNode.oNode -b configfiles/sopa
```
- Executar os restantes no modo Servidor/Cliente.
```bash
java oNode.oNode -sc <BOOTSTRAPPER_IP>
```

