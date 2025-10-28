# ---------- Etapa 1: Build del frontend (Angular) ----------
FROM node:18 AS build-frontend
WORKDIR /app/frontend

COPY EasyWay-Frontend/EasyWay-main/ ./
RUN npm ci
RUN npm run build -- --configuration production
# NOTE: verifica en /app/frontend/dist/ cuál es el nombre de la carpeta generada (ej: dist/easyway)

# ---------- Etapa 2: Preparar backend ----------
FROM maven:3.9.4-eclipse-temurin-17 AS prepare-backend
WORKDIR /app/backend
COPY EasyWay-Backend/inventarioBackend/ ./

RUN mkdir -p src/main/resources/static

# Copia los contenidos del build del frontend dentro de static
# Si tu build genera dist/<app-name>, usa /app/frontend/dist/<app-name>/* en vez de dist/*
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

# Exponer el puerto por claridad (Render ignora EXPOSE y usará PORT)
EXPOSE 8080

# Usar sh -c para expandir la variable $PORT en la línea de comando
ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar --server.port=$PORT"]

