FROM node:20-alpine AS frontend-build
WORKDIR /app
# Install Python and build dependencies for node-gyp
RUN apk add --no-cache python3 make g++ gcc libc-dev
# Create a symlink for python command
RUN ln -sf python3 /usr/bin/python
COPY package.json package-lock.json* ./
RUN npm install
COPY webpack.config.js .
COPY .env* ./
# Create directories if they don't exist
RUN mkdir -p src/main/resources/static/js src/main/resources/static/img/testimonials src/main/resources/static/dist
# Copy only existing directories
COPY src/main/resources/static/js ./src/main/resources/static/js
# Copy the img directory
COPY src/main/resources/static/img ./src/main/resources/static/img
RUN npm run build

FROM maven:3.9-eclipse-temurin-21 AS backend-build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
COPY --from=frontend-build /app/src/main/resources/static/dist ./src/main/resources/static/dist
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 