# 第一阶段：编译代码 (使用 Maven 3.8 + JDK 17)
FROM maven:3.8.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 第二阶段：运行程序 (使用极简 JRE 17)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/scada-model-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口 (8080 是 Web 和 WebSocket 的统一端口)
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
