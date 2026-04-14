FROM node:18-alpine AS frontend-builder
WORKDIR /app/frontend
COPY frontend/ ./
RUN npm install && npm run build

FROM maven:3.8-openjdk-11-slim AS backend-builder
WORKDIR /app/backend
COPY backend/ ./
RUN mvn clean package -DskipTests

FROM openjdk:11-jre-slim
WORKDIR /app
RUN apt-get update && apt-get install -y nginx && rm -rf /var/lib/apt/lists/*

COPY --from=backend-builder /app/backend/target/attendance-system-1.0.0.jar /app/app.jar
COPY --from=frontend-builder /app/frontend/dist /var/www/html
RUN mkdir -p /app/data && echo '[]' > /app/data/attendance.json

COPY nginx.conf /etc/nginx/sites-available/default
COPY start.sh /app/start.sh
RUN chmod +x /app/start.sh

EXPOSE 80
CMD ["/app/start.sh"]
