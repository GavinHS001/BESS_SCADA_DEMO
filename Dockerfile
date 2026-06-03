# ===== 第一阶段：构建 =====
FROM maven:3.8.5-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

# ===== 第二阶段：运行 =====
FROM eclipse-temurin:17-jre

WORKDIR /app

# ⚠️ 关键修复点：jar 名称必须和 target 一致
COPY --from=build /app/target/scada-app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]