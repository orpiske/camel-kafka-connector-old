FROM registry.access.redhat.com/ubi8/ubi-minimal as artemis-base
MAINTAINER Otavio Rodolfo Piske <angusyoung@gmail.com>
ARG ARTEMIS_VERSION
ENV ARTEMIS_VERSION ${ARTEMIS_VERSION:-2.7.0}
ARG ARTEMIS_JOURNAL
ENV ARTEMIS_JOURNAL ${ARTEMIS_JOURNAL:-aio}
ENV JMS_BROKER_ROOT /opt/camel-kafka-connector/artemis/
EXPOSE 61616
RUN microdnf install -y java-1.8.0-openjdk-headless libaio tar gzip && microdnf clean all
ENV JAVA_HOME /etc/alternatives/jre
RUN mkdir -p ${JMS_BROKER_ROOT}
WORKDIR ${JMS_BROKER_ROOT}
RUN curl https://archive.apache.org/dist/activemq/activemq-artemis/${ARTEMIS_VERSION}/apache-artemis-${ARTEMIS_VERSION}-bin.tar.gz -o apache-artemis.tar.gz && \
    mkdir -p apache-artemis && tar --strip-components=1 -xvf apache-artemis.tar.gz -C apache-artemis && \
    rm -f apache-artemis.tar.gz

FROM artemis-base as artemis
RUN ${JMS_BROKER_ROOT}/apache-artemis/bin/artemis create --${ARTEMIS_JOURNAL} --relax-jolokia --allow-anonymous --http-host 0.0.0.0 --user admin --password "admin" --role amq --data /artemis-storage ${JMS_BROKER_ROOT}/apache-artemis-instance
CMD [ "sh", "-c", "./apache-artemis-instance/bin/artemis run" ]