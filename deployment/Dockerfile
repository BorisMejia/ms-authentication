FROM eclipse-temurin:17-jdk-alpine
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
COPY /applications/app-service/build/libs/*.jar authentication.jar
USER appuser
VOLUME /tmp
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar authentication.jar"]
