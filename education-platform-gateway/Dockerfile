FROM registry.cn-beijing.aliyuncs.com/gkxy_local/skywalking-java-agent:8.8.0

RUN mkdir -p /education-platform-gateway

VOLUME /education-platform-gateway/logs

WORKDIR /education-platform-gateway

ARG JAR_FILE=target/education-platform-gateway.jar

COPY ${JAR_FILE} app.jar

EXPOSE 60
#-Xms128m -Xmx256m
#ENV TZ=Asia/Shanghai JAVA_OPTS=" -Djava.security.egd=file:/dev/./urandom" JVM_OPTS=" -Xms256m -Xmx512m -Xss256k -XX:+UseConcMarkSweepGC -XX:MaxTenuringThreshold=6 -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:./logs/gc.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./logs/dumps/"
#
#CMD sleep 60; java ${JVM_OPTS} -jar app.jar $JAVA_OPTS

ENV TZ=Asia/Shanghai JAVA_OPTS=" -Djava.security.egd=file:/dev/./urandom" JVM_OPTS=" -Xms256m -Xmx512m -Xss256k -XX:+UseConcMarkSweepGC -XX:MaxTenuringThreshold=6 -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:./logs/gc.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./logs/dumps/"  SKYWALKING_OPTS=" -javaagent:/skywalking/skywalking-agent.jar -Dskywalking.collector.backend_service=skywalking-oap.middleware:11800 -Dskywalking.agent.service_name=expression-platform-gateway"

CMD sleep 60; java ${SKYWALKING_OPTS} ${JVM_OPTS} -jar app.jar $JAVA_OPTS
