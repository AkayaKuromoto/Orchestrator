#!/bin/bash
sleep 3
sudo chmod o+rw /run/containerd/containerd.sock
java -jar ./target/orchestrator-1.0.0.jar