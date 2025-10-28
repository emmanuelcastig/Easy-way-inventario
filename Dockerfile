# ---------- Etapa 1: Build del frontend (Angular) ----------
FROM node:18 AS build-frontend
WORKDIR /app/frontend

COPY EasyWay-Frontend/EasyWay-main/ ./
RUN npm ci
RUN npm run build -- --configuration production

# ---------- Etapa 2: Preparar backend ----------
FROM maven:3.9.4-eclipse-temurin-17 AS prepare-backend
WORKDIR /app/backend
COPY EasyWay-Backend/inventarioBackend/ ./

RUN mkdir -p src/main/resources/static
COPY --from=build-frontend /app/frontend/dist/* src/main/resources/static/

# ---------- Etapa 3: Compilar backend ----------
FROM maven:3.9.4-eclipse-temurin-17 AS build-backend
WORKDIR /app/backend
COPY --from=prepare-backend /app/backend/ ./
RUN mvn clean package -DskipTests

# ---------- Etapa 4: Imagen final ----------
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build-backend /app/backend/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
