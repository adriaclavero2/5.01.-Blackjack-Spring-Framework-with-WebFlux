# 🃏 Blackjack API WebFlux

> A reactive Blackjack game API built with Spring Boot 4 and WebFlux, featuring dual-database architecture for optimal performance.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![WebFlux](https://img.shields.io/badge/WebFlux-Reactive-blue.svg)](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

---

## 📖 Overview

This project is a **reactive Blackjack game API** developed with **Spring Boot 4** and **WebFlux**. It implements a dual-database architecture that separates relational data persistence (MySQL) from volatile real-time game state management (MongoDB), providing optimal performance and scalability.

### 🎯 Project Description

Implementation of a Java API with Spring Boot for a Blackjack game. The API is designed to connect and manage information simultaneously across two different databases:
- **MongoDB** for game state management
- **MySQL** for player profiles and statistics

The system features reactive architecture, global exception handling, comprehensive testing, and automatic documentation.

---

## ✨ Features

- **🎮 Player Management**: Profile creation and name updates
- **🎲 Blackjack Engine**: Complete game logic including:
  - Card dealing
  - Score calculation with dynamic Aces
  - Hit and Stand actions
  - Automatic win/loss detection
- **📊 Rankings & History**: Automatic win/loss tracking with success rate calculation (*win rate*)
- **⚠️ Global Exception Handling**: Clean JSON responses for both controlled errors (404, 400) and unhandled exceptions (500) using `@RestControllerAdvice`

---

## 🛠 Tech Stack

| Category | Technologies |
|----------|-------------|
| **Backend** | Java 21, Spring Boot 4 (WebFlux) |
| **Databases** | MySQL (Spring Data R2DBC - Reactive)<br>MongoDB (Spring Data Reactive MongoDB) |
| **Infrastructure** | Docker, Docker Compose |
| **Testing** | JUnit 5, Mockito, StepVerifier, WebTestClient |
| **API Tools** | Postman (Integration & Testing Flows) |

---

## 🚀 Installation & Setup

### Prerequisites

- Java 21+
- Docker & Docker Compose
- Maven 3.6+
- Ports 3307 (MySQL) and 27017 (MongoDB) available

### 1️⃣ Clone the Repository

```bash
git clone <your-repository-url>
cd blackjack-api
```

### 2️⃣ Start Database Infrastructure

The project includes a `docker-compose.yml` file to easily spin up the required databases without additional configuration.

```bash
# Start MongoDB and MySQL containers in detached mode
docker-compose up -d mysql-db mongo-db
```

> **Note**: Ensure ports `3307` (MySQL) and `27017` (MongoDB) are free on your local machine.

### 3️⃣ Run the Application

Once the Docker containers are running, start the application using Maven:

```bash
./mvnw spring-boot:run
```

The API will be available at: **http://localhost:8080**

### 4️⃣ Run Unit Tests

The project includes comprehensive unit tests for both Controller layer (using WebTestClient) and Service layer (using StepVerifier and Mockito).

```bash
./mvnw test
```

---

## 📸 Demo & Testing (Postman)

Business logic and database integration are demonstrated through Postman, simulating real HTTP client interaction flows.

### Complete Game Flow:

1. **Create a player**: `POST /player`
2. **Start a game**: `POST /game/new` (passing player ID)
3. **Play hand**: `POST /game/{id}/play` (Actions: "HIT" or "STAND")
4. **Check results**: `GET /ranking` (View updated statistics in MySQL after game completion in MongoDB)
5. **View history**: `GET /player/{playerId}/games` (See all games for a player)

---

## 🏗 Architecture & Design Decisions

### Reactive Architecture (WebFlux)

A **100% non-blocking approach** from controller to database (using R2DBC and Reactive MongoDB drivers) to maximize server performance and concurrency.

### Dual Database Strategy (Polyglot Persistence)

#### **MySQL**
- Used for highly structured entities with long-term logical relationships
- Stores: Users, Statistics, Rankings
- Provides ACID guarantees for critical player data

#### **MongoDB**
- Used to store the `Game` object state
- Perfect fit for card game structure:
  - Cards in hand
  - Remaining deck
  - Complex and dynamic document structure
- Enables extremely fast reads/writes during gameplay
- NoSQL document-oriented paradigm suits the volatile nature of game state

### Cascade Updates

Implemented a reactive flow where game completion in `GameService` asynchronously triggers statistics updates in `PlayerService`, ensuring data integrity across both databases.

```
Game Completion → MongoDB Update → Reactive Chain → MySQL Statistics Update
```

---

## 📡 API Endpoints

### Player Management
- `POST /player` - Create new player
- `PUT /player/{id}` - Update player name

### Game Operations
- `POST /game/new` - Start new game
- `POST /game/{id}/play` - Make a move (HIT/STAND)
- `GET /game/{id}` - Get game state

### Statistics & Rankings
- `GET /ranking` - View player rankings
- `GET /player/{playerId}/games` - Get player game history

---

## 🧪 Testing Strategy

- **Controller Layer**: Integration tests using `WebTestClient`
- **Service Layer**: Unit tests with `StepVerifier` for reactive streams
- **Mocking**: Mockito for dependency isolation
- **Coverage**: Comprehensive test coverage for business logic and edge cases

---

## 📂 Project Structure

```
blackjack-api/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/blackjack_api/
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       └── exception/
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

<div align="center">
  <strong>⭐ If you found this project useful, please consider giving it a star! ⭐</strong>
</div>
