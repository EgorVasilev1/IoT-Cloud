#!bin/bash

#Installing Docker
sudo apt update && sudo apt upgrade -y
for pkg in docker.io docker-doc docker-compose docker-compose-v2 podman-docker containerd runc; do sudo apt-get remove $pkg; done
sudo apt-get install ca-certificates curl -y
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc -y
sudo chmod a+r /etc/apt/keyrings/docker.asc
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update -y

#Installing JDK17
sudo apt install software-properties-common -y
sudo add-apt-repository ppa:azul/java -y
sudo apt update -y
sudo apt install openjdk-17-jdk -y

#Installing Gradle 8.9
curl -s https://get.sdkman.io | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install gradle 8.9
sdk use gradle 8.9
echo 'export PATH="$PATH:$HOME/.sdkman/candidates/gradle/current/bin"' >> ~/.bashrc && source ~/.bashrc