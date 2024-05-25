# Sử dụng hình ảnh chính thức của OpenJDK làm base image
FROM openjdk:17

# Đặt biến môi trường
ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JHIPSTER_SLEEP=0

# Tạo thư mục /app và sao chép file JAR của ứng dụng vào đó
RUN mkdir -p /app
COPY target/shopapp-backend-0.0.1-SNAPSHOT.jar /app/app.jar

# Chuyển đến thư mục /app
WORKDIR /app

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
