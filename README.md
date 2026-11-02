# food-court-service

Restaurants, dishes and orders — microservice of the **Plazoleta de comidas** challenge (PRAGMA).

## User stories

- HU2 — Create restaurant
- HU3 — Create dish
- HU4 — Update dish
- HU7 — Enable/disable dish
- HU9 — List restaurants
- HU10 — List dishes by restaurant
- HU11 — Place order
- HU12 — List orders by status
- HU13 — Assign order (IN_PREPARATION)
- HU15 — Deliver order (PIN)
- HU16 — Cancel order

## Stack

Java 21 · Spring Boot 3.5.x · Gradle 8.10.x · PostgreSQL · MapStruct · Lombok · springdoc-openapi · JUnit 5 + Mockito.

## Architecture

Hexagonal (ports & adapters): `domain` / `application` / `infrastructure`. Full conventions in `CLAUDE.md`.

## Run

```bash
./gradlew clean build
./gradlew bootRun   # http://localhost:8082 - Swagger UI at /swagger-ui.html
```
