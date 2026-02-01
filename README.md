# Wallet Service

Учебный сервис кошелька на Java/Spring Boot.

Функциональность:

- `POST /api/v1/wallet` — пополнение / списание средств с кошелька.
- `GET /api/v1/wallets/{walletId}` — получение текущего баланса кошелька.
## Стек технологий

- Java 17
- Spring Boot 3 (Web, Data JPA, Validation)
- Liquibase — миграции БД
- H2 (in-memory) для локального запуска
- PostgreSQL (для Docker-окружения)
- Maven
- JUnit 5, Spring Boot Test, MockMvc — интеграционные тесты
## Архитектура

Пакеты:

- `org.example.walletservice.api` — REST-контроллер и DTO
- `org.example.walletservice.service` — бизнес-логика (`WalletService`)
- `org.example.walletservice.repository` — доступ к БД (`WalletRepository`)
- `org.example.walletservice.domain` — сущность `Wallet` и доменные исключения
## Модель данных

Таблица `wallet` (создаётся Liquibase):

- `id` (UUID, PK)
- `balance` (BIGINT, деньги в минимальных единицах — копейки)
- `version` (BIGINT)
- `created_at` (TIMESTAMPTZ)
- `updated_at` (TIMESTAMPTZ)
## Конкурентный доступ

- Операции `DEPOSIT` и `WITHDRAW` выполняются в транзакциях (`@Transactional`).
- Для изменения баланса используется пессимистическая блокировка записи:

  ```java
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select w from Wallet w where w.id = :id")
  Optional<Wallet> findByIdForUpdate(@Param("id") UUID id);

## REST API

### POST `/api/v1/wallet`

Изменение баланса кошелька (пополнение или списание).

Пример запроса:

`{
  "walletId": "11111111-1111-1111-1111-111111111111",
  "operationType": "DEPOSIT",
  "amount": 1000
}`

Пример ответа 200 OK

`{
  "walletId": "11111111-1111-1111-1111-111111111111",
  "balance": 11000
}`

### GET `/api/v1/wallets/{walletId}`

Получение текущего баланса кошелька.

Пример ответа 200 OK:

`{
  "walletId": "11111111-1111-1111-1111-111111111111",
  "balance": 11000
}`

## Формат ошибок

Все ошибки возвращаются в виде:

``
{
  "code": "ERROR_CODE",
  "message": "Описание ошибки"
}``

Примеры кодов:

WALLET_NOT_FOUND
— кошелёк не найден

INSUFFICIENT_FUNDS
— недостаточно средств

INVALID_JSON
— невалидное тело запроса

VALIDATION_ERROR
— ошибка валидации входных данных

TYPE_MISMATCH
— неверный тип параметра (например, некорректный UUID)

INTERNAL_ERROR
— внутренняя ошибка сервера

## Запуск локально (без Docker)

Требования:

- Java 17
- Maven

## Запуск через Docker

В корне проекта:

```bash
docker compose up -d

### Через Maven

В корне проекта:

`mvn clean package
java -jar target/wallet-service-0.0.1-SNAPSHOT.jar
Приложение поднимается на порту
8080`

### Через IntelliJ IDEA

- Открыть проект `wallet-service` в IntelliJ.
- Запустить класс `org.example.walletservice.WalletServiceApplication`.
- Приложение будет доступно по адресу: `http://localhost:8080`.

При старте создаётся тестовый кошелёк:

- `walletId`: `11111111-1111-1111-1111-111111111111`
- начальный баланс: `10000` (в копейках)

## Примеры запросов

Проверка баланса:

`bash
curl http://localhost:8080/api/v1/wallets/11111111-1111-1111-1111-111111111111`

Пополнение (DEPOSIT):

`curl -X POST "http://localhost:8080/api/v1/wallet" \
  -H "Content-Type: application/json" \
  -d "{\"walletId\":\"11111111-1111-1111-1111-111111111111\",\"operationType\":\"DEPOSIT\",\"amount\":1000}"`

## Тесты

Интеграционный тест контроллера:

- класс: `org.example.walletservice.api.WalletControllerTest`
- профиль: `test` (H2 + Liquibase)

Запуск тестов:

```bash
mvn test
