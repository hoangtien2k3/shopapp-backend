# Sử dụng hình ảnh Maven chính thức với OpenJDK 17 để build và chạy ứng dụng
FROM openjdk:17.0.2
# Thiết lập thư mục làm việc trong container
WORKDIR /app
# Sao chép file pom.xml và thư mục .mvn vào thư mục làm việc của container
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Download các dependencies trước
RUN ./mvnw dependency:go-offline
# Sao chép toàn bộ mã nguồn vào container
COPY src ./src
# Build ứng dụng Spring Boot
RUN ./mvnw clean package -DskipTests
# Run container
CMD ["./mvnw", "spring-boot:run"]

# Pull and start/run a DockerHub container
# -d: Chạy container ở chế độ nền (detach mode)
# -p 8080:8080: Chuyển tiếp cổng 8080 từ container sang cổng 8080 trên máy tính host
# -v "$(pwd):/app": Mount thư mục hiện tại của máy tính host vào /app trong container
# docker run -dp 8080:8080 --name shopapp-backend -v "$(pwd):/app" --network shopapp-network hoangtien2k3/shopapp-backend:v1.0.0