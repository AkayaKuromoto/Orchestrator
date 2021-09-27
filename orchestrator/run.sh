#!/bin/bash
apt install containerd containernetworking-plugins
./scripts/grant-permission.sh & ./scripts/start-containerd.sh