# TechMarket API

[![Java Version](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**TechMarket API** — это headless REST API для размещения объявлений о продаже техники (новой и бывшей в употреблении) и заключения сделок между пользователями.

## Основные возможности

-  **Аутентификация и авторизация** — JWT-токены, роли USER/ADMIN
-  **Управление объявлениями** — CRUD операции с фильтрацией и пагинацией
-  **Система сделок** — создание запросов на покупку, подтверждение, отмена
-  **Фильтрация** — по бренду, цене, состоянию товара
-  **Docker-контейнеризация** — легкий запуск через docker-compose
-  **Метрики и health checks** — Spring Boot Actuator
-  **Swagger UI** — интерактивная документация API

##  Стек технологий

| Категория | Технологии |
|-----------|------------|
| **Язык** | Java 17 |
| **Фреймворк** | Spring Boot 3.2.5, Spring Security, Spring Data JPA |
| **База данных** | PostgreSQL 16, H2 (для тестов) |
| **Аутентификация** | JWT (JJWT 0.11.5) |
| **Документация API** | Swagger/OpenAPI (springdoc-openapi 2.6.0) |
| **Сборка** | Maven |
| **Тестирование** | JUnit, Mockito, Spring Security Test |
| **Контейнеризация** | Docker, Docker Compose |
| **Мониторинг** | Spring Boot Actuator |
| **Качество кода** | Checkstyle, google-java-format |

##  Требования

- **Java 17** или выше
- **Maven 3.9+** (или использование ./mvnw)
- **Docker** и **Docker Compose** (опционально)
- **PostgreSQL 16** (при локальном запуске)

##  Запуск проекта
# Запуск всех сервисов (app + postgres)
docker-compose up -d

# Просмотр логов
docker-compose logs -f app

# Остановка
docker-compose down

# Полная очистка (включая данные)
docker-compose down -v