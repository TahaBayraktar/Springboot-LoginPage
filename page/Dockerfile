# 1. Java 21 ve Maven içeren bir temel görüntü kullanıyoruz
FROM maven:3.9.4-eclipse-temurin-21 AS build

# 2. Çalışma dizinini oluştur
WORKDIR /app

# 3. Proje dosyalarını Docker container içine kopyala
COPY . /app

# 4. Doğru çalışma dizinine geç (Eğer proje `page` içinde ise)
WORKDIR /app/page

# 5. Maven ile projeyi derle
RUN mvn clean package -DskipTests

# 6. Java 21 içeren bir runtime görüntüsü kullanıyoruz
FROM openjdk:21-jdk-slim

# 7. Çalışma dizinini oluştur
WORKDIR /app

# 8. Derlenen JAR dosyasını kopyala (Hedef `page/target/` olmalı)
COPY --from=build /app/page/target/*.jar app.jar

# 9. Uygulamayı başlat
CMD ["java", "-jar", "app.jar"]
