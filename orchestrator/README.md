# Orchestrator

Der Prototyp kann auf Linux ausgeführt werden.  

Um die Anwendung zu starten, kann das ```run.sh``` Skript mit sudo ausgefürt werden. Das Skript führt nachstehende Schritte aus, die sonst manuell durchgeführt werden müssen.

1)  Maven installieren ```sudo apt install maven```
2)  Mit Maven JAR-Datei generieren ```mvn clean install```
3)  containerd installieren ```apt install containerd containernetworking-plugins```
4)  containerd starten und entsprechende Rechte des Unix Sockets anpassen
5)  Jar ausführen


## Exemplarische Aufrufe mit curl

Image herunterladen: 
```curl http://localhost:8080/image -X POST -d '{"id": "busybox"}' -H 'Content-Type: application/json'```

Anlegen eines Pods:  
```curl http://localhost:8080/pod -X POST -d '{"name":"test","namespace":"ns","count":2,"containers":[{"name":"testcontainer", "imageId":"busybox"}]}' -H 'Content-Type: application/json'```

Abfrage von Pods:  
```curl http://localhost:8080/pod```