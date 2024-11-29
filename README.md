# ESR2024

## Links úteis:
- https://repositorium.sdum.uminho.pt/bitstream/1822/74179/1/Miguel%20Gil%20Pires%20da%20Silva.pdf
- https://github.com/JoaoGiesteira33/over-the-top

## Como executar o programa no CORE (tutorial para burros):
- Execuar o core
```bash
sudo core
```
- Noutra janela de terminal, executar o comando
```bash
xhost +
```  
- Abrir e iniciar uma topologia (por exemplo a underlay.imn)
- Abrir uma janela bash, ir para a pasta do projeto e compilar os fichieros da pasta oNode
```bash
cd /home/core/ESR2024/
javac oNode/*.java
```
- Executar o Bootstrapper (na underlay.imn usar o ficheiro overlay)
```bash
java oNode.oNode -b configfiles/[CONFIG_FILE]
```
- Executar o Servidor responsável pelo streaming (na underlay.imn usar o ficheiro pop)
```bash
export DISPLAY=:0.0
xhost +
java oNode.oNode -s configfiles/[POP_FILE] [BOOTSTRAPPER_IP]
```
- Executar os Nodos responśaveis por serem points of presence
```bash
java oNode.oNode -pop [BOOTSTRAPPER_IP]
```
- Executar os restantes Nodos (na underlay.imn os nodos verdes)
```bash
java oNode.oNode [BOOTSTRAPPER_IP]
```
- Executar o Cliente com o servidor e vídeo aos quais nos pretendemos ligar e ver 
```bash
export DISPLAY=:0.0
java oNode.oClient [SERVER_IP] [VIDEO_ID]
```
