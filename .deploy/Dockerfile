FROM maven:3-openjdk-11 AS builder

WORKDIR /usr/src/build

COPY pom.xml .
COPY src src

RUN mvn
RUN cp target/gameboi-*-jar-with-dependencies.jar gameboi.jar

FROM openjdk:11

WORKDIR /usr/src/app

COPY --from=builder /usr/src/build/gameboi.jar .
COPY --from=builder /usr/src/build /usr/src/build
COPY .deploy/start.sh .deploy/start.sh

WORKDIR /var/gameboi

CMD ["/usr/src/app/.deploy/start.sh"]
