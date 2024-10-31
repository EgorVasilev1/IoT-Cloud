#!bin/bash

sudo apt update && sudo apt upgrade -y
sudo apt install software-properties-common -y
sudo add-apt-repository ppa:azul/java -y
sudo apt update
sudo apt install openjdk-17-jdk -y

sudo apt install curl -y
curl -s https://get.sdkman.io | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install gradle 8.9
sdk use gradle 8.9
echo 'export PATH="$PATH:$HOME/.sdkman/candidates/gradle/current/bin"' >> ~/.bashrc && source ~/.bashrc