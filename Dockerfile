# 使用基础镜像
FROM ubuntu:latest

# 安装 GCC 和 OpenJDK 11
RUN apt-get update && \
    apt-get install -y gcc openjdk-11-jdk gcc-c++

# 设置环境变量
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
ENV PATH=$PATH:$JAVA_HOME/bin

COPY target/*.jar hmetao.jar

EXPOSE 8972
ENTRYPOINT ["java","-jar","hmetao.jar"]