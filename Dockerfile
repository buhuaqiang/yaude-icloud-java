FROM maven:3.5.0-jdk-8-alpine AS builder

# 设置 Maven 的仓库目录
ENV MAVEN_REPO=/root/.m2/repository

# add pom.xml and source code
ADD ./pom.xml pom.xml
ADD ./yaude-boot-base yaude-boot-base
ADD ./yaude-boot-module-demo yaude-boot-module-demo
ADD ./yaude-boot-module-system yaude-boot-module-system
ADD ./yaude-icloud-license yaude-icloud-license
ADD ./yaude-icloud-openstack yaude-icloud-openstack

#RUN mkdir code
#COPY ./* code/
#WORKDIR code/

# package jar
RUN  mvn clean install -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -Dmaven.repo.local=./.m2 -P dev




FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER buhuaqiang@163.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir logs

RUN chmod 777 /logs

# copy jar from the first stage
# 在yaude-boot-module-system pom 已经把yaude-boot-module-system-2.4.6.jar 移到了根目录 target/
COPY --from=builder yaude-boot-module-system/target/yaude-boot-module-system-2.4.6.jar yaude-boot-module-system-2.4.6.jar
#COPY --from=builder target/yaude-boot-module-system-2.4.6.jar yaude-boot-module-system-2.4.6.jar

ENV JVM_OPTS="-XX:+UseContainerSupport \
               -XX:+UnlockExperimentalVMOptions \
               -XX:+UseCGroupMemoryLimitForHeap \
               -Xms2G \
               -Xmn1500m \
               -Xmx2G \
               -Xss256K \
               -XX:+UseParNewGC \
               -XX:+UseConcMarkSweepGC \
               -XX:CMSInitiatingOccupancyFraction=70 \
               -XX:+UseCMSInitiatingOccupancyOnly \
               -XX:+UseCMSCompactAtFullCollection \
               -XX:CMSFullGCsBeforeCompaction=0 \
               -XX:+CMSParallelInitialMarkEnabled \
               -XX:+CMSScavengeBeforeRemark \
               -XX:+DisableExplicitGC \
               -Dfile.encoding=utf-8 \
               -Duser.timezone=Asia/Shanghai \
               -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT [ "sh", "-c", "java $JVM_OPTS   -jar yaude-boot-module-system-2.4.6.jar" ]
EXPOSE 8080
