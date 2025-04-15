FROM jenkins/jenkins:lts

USER root

# Установка необходимых пакетов
RUN apt-get update && apt-get install -y \
    git \
    docker.io \
    sudo \
 && rm -rf /var/lib/apt/lists/*

# Разрешаем jenkins-пользователю использовать docker без sudo
RUN usermod -aG docker jenkins

# Устанавливаем docker-cli (если нужен конкретно cli)
# RUN apt-get update && apt-get install -y docker-cli

USER jenkins
