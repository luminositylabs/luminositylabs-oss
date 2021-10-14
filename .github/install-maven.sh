#!/usr/bin/env bash

set -euf

MAVEN_BASE_URL=https://archive.apache.org/dist/maven/maven-3/
MAVEN_VERSION=3.8.3
MAVEN_SHA=0f1597d11085b8fe93d84652a18c6deea71ece9fabba45a02cf6600c7758fd5b

sudo apt-get update
sudo apt-get install -y curl
sudo mkdir -p /usr/share/maven /usr/share/maven/ref
sudo curl -fsSL -o /tmp/apache-maven.tar.gz ${MAVEN_BASE_URL}/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz
echo "${MAVEN_SHA} /tmp/apache-maven.tar.gz" | sha256sum -c -
sudo tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1
sudo rm -f /tmp/apache-maven.tar.gz
sudo ln -fs /usr/share/maven/bin/mvn /usr/bin/mvn
mvn -version

set +euf

