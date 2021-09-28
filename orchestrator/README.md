# Orchestrator

Der Prototyp kann auf Linux ausgeführt werden.  

Um die Anwendung zu starten, kann das ```run.sh``` Skript mit sudo ausgefürt werden. Das Skript führt nachstehende Schritte aus, die sonst manuell durchgeführt werden müssen.

1)  Maven installieren ```sudo apt install maven```
2)  Mit Maven JAR-Datei generieren ```mvn clean install```
3)  containerd installieren ```apt install containerd containernetworking-plugins```
4)  containerd starten und entsprechende Rechte des Unix Sockets anpassen
5)  Jar ausführen
