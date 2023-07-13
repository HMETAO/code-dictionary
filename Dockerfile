# 使用基础镜像
FROM ubuntu:23.10
#RUN sed -i "s@/archive.ubuntu.com/@/mirrors.tuna.tsinghua.edu.cn/@g" /etc/apt/sources.list \
#       && rm -Rf /var/lib/apt/lists/* \
#       && apt-get update --fix-missing -o Acquire::http::No-Cache=True
RUN  apt-get install -y openjdk-11-jdk build-essential

# 设置环境变量
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
ENV PATH=$PATH:$JAVA_HOME/bin

COPY target/*.jar hmetao.jar

EXPOSE 8972
ENTRYPOINT ["java","-jar","hmetao.jar"]