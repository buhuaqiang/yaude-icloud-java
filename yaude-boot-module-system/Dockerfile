FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER jeecgos@163.com

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime




ADD ./yaude-boot-module-system/target/yaude-boot-module-system-2.4.6.jar ./

ADD ./yaude-boot-module-system/jmx_prometheus_javaagent-0.17.0.jar /jmx_prometheus_javaagent-0.17.0.jar
ADD ./yaude-boot-module-system/prometheus-jmx-config.yaml /prometheus-jmx-config.yaml

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

ENV JMX_OPTS="-javaagent:/jmx_prometheus_javaagent-0.17.0.jar=9999:/prometheus-jmx-config.yaml"
ENTRYPOINT [ "sh", "-c", "java $JVM_OPTS  $JMX_OPTS -jar yaude-boot-module-system-2.4.6.jar" ]
EXPOSE 8091
