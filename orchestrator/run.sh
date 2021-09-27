#!/bin/bash
apt install maven
mvn clean install
apt install containerd containernetworking-plugins
./scripts/grant-permission.sh & ./scripts/start-containerd.sh