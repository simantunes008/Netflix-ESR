# ESR2024

## Links úteis:
- https://repositorium.sdum.uminho.pt/bitstream/1822/74179/1/Miguel%20Gil%20Pires%20da%20Silva.pdf
- https://github.com/JoaoGiesteira33/over-the-top

## Como executar o programa no CORE (tutorial para burros):
- Execuar o core
```bash
sudo core
```
- Abrir e iniciar uma topologia
- Abrir uma janela bash, ir para a pasta do projeto e compilar os fichieros da pasta oNode
```bash
cd /home/core/ESR2024/
javac oNode/*.java
```
- Executar o Bootstrapper
```bash
java oNode.oNode -b configfiles/<CONFIG_FILE>
```
- Executar os restantes no modo Servidor/Cliente
```bash
java oNode.oNode -sc <BOOTSTRAPPER_IP>
```

