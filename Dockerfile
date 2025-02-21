FROM eclipse-temurin:21-jre

ENV ARTIFACT mayb-api.jar

COPY entrypoint.sh .
COPY build/libs/${ARTIFACT} .

EXPOSE 8080

RUN chmod +x ./entrypoint.sh
ENTRYPOINT ["./entrypoint.sh"]