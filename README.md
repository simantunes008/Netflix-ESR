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
- Executar o Servidor responsável pelo streaming
```bash
java oNode.oNode -sc <BOOTSTRAPPER_IP> <POINTS_OF_PRESENCE_FILE>
```
- Executar os Nodos responśaveis por serem points of presence
```bash
java oNode.oNode -pop <BOOTSTRAPPER_IP>
```
- Executar os restantes Nodos
```bash
java oNode.oNode -n <BOOTSTRAPPER_IP>
```
- Executar o Cliente com o servidor ao qual nos pretendemos ligar
```bash
java oNode.oClient <SERVER_IP>
```
