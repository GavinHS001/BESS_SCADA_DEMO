# ===== build stage =====
FROM maven:3.8.5-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

# 🔥 关键：直接找唯一 jar（避免名字问题）
RUN ls -lh /app/target

# ===== run stage =====
FROM eclipse-temurin:17-jre

WORKDIR /app

# ✅ 不写死 jar 名字（解决你现在所有问题）
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]