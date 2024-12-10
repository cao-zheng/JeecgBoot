FROM registry.cn-hangzhou.aliyuncs.com/jeecgdocker/alpine-java:8_server-jre_unlimited

MAINTAINER jeecgos@163.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

#RUN mkdir -p /jeecg-boot/config/jeecg/

WORKDIR /jeecg-boot

EXPOSE 9111

#ADD ./src/main/resources/jeecg ./config/jeecg
ADD ./target/jeecg-cloud-monitor-3.7.2.jar ./

CMD java -Djava.security.egd=file:/dev/./urandom -jar \
-Dspring.cloud.nacos.discovery.ip=172.16.65.246 \
-Dspring.cloud.nacos.discovery.port=9111 \
jeecg-cloud-monitor-3.7.2.jar