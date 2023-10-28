# 使用基础镜像
FROM registry.cn-hangzhou.aliyuncs.com/hmetao_docker/hmetao-ubuntu
ENV LANG C.UTF-8

COPY target/*.jar hmetao.jar

EXPOSE 8972
ENTRYPOINT ["java","-jar","hmetao.jar"]