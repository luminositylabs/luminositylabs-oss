#!/usr/bin/env bash

set -euf

MAVEN_BASE_URL=https://archive.apache.org/dist/maven/maven-3/
MAVEN_VERSION=3.8.5
MAVEN_SHA=88e30700f32a3f60e0d28d0f12a3525d29b7c20c72d130153df5b5d6d890c673

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

