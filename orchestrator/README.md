# Orchestrator
Der Prototyp kann auf Linux ausgeführt werden. 
Um die prototypische Implementierung anzuwenden, muss zuvor containerd mit CNI-Plugins installiert werden.
```sudo apt install containerd containernetworking-plugins```

Mit Maven kann eine JAR-Datei generiert werden. ```mvn clean install```  
Vor dem Start muss containerd gestartet werden.  
Hierfür kann das Skript ``run.sh`` mit sudo ausgeführt werden.Es wird containerd gestartet und die Berechtigungen entsprechend angepasst.