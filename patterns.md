# Architectural & Design Patterns

This document outlines the architectural patterns implemented in the **Tennis Club Management System**.
This project follows the principles of **Domain-Driven Design (DDD)** and **Enterprise Application Patterns** to ensure scalability, maintainability and data integrity.

## 1. Domain Model

**Application**: All core business logic (Users, Tournaments, Rankings, etc.)

**Why**: The system handles complex rules: automated tournament brackets, ranking-based registration and overlapping reservation checks. Using a Domain Model allows for rich object-oriented features like inheritance (`User` -> `Admin/Player`) and complex associations.

**Classes**: `User`, `Tournament`, `Match`, `Reservation`, `Court`, `Payment`

## 2. Data Mapper

**Application**: Mapping domain objects (e.g., `User`, `Tournament`) to database tables. 

**Why**: Data Mapper handles the transfer of data between objects and the database, while keeping them independent of one another. Complex domain model requires more scalable tools like this pattern.

**Classes**: `UserMapper`, `MatchMapper`, `CourtMapper`, `ReservationMapper`

## 3. Repository

**Application**: Encapsulating data access logic

**Why**: It provides a clean, collection-like interface for the Service Layer. Instead of writing raw SQL in business logic, we interact with Repositories that use Mappers to return fully reconstituted domain objects.

**Classes**: `UserRepository`, `ReservationRepository`, `CourtRepository`

## 4. Service Layer

**Application**: Central element of business logic, integrating operations on many Repositories simultaneously. Also Service Layer handles input data validation, orchestrating transactions using `Unit of Work` and mapping Entities to `DTO` objects, before sending them to frontend.

**Why**: Avoiding complicated logic in controllers, makes code more modular and easier to test. Allows to maintain data integrity.

**Classes**: `CourtService`, `ReservationService`, `UserService`

## 5. Unit of Work

**Application**: Manages atomic transactions and database synchronization.

**Why**: This is a crucial pattern for data integrity. The Unit of Work tracks all changes (new, dirty, deleted) during a single business transaction and ensures they are committed as a single unit (All-or-Nothing). 

**Classes**: `UnitOfWork`

## 6. Data Transfer Object (DTO)

**Application**: Communication between frontend (React/Electron) and backend (Java).

**Why**: To prevent leaking sensitive information (like password hashes) and to decouple the API contract from the database schema. DTOs allows us to send precisely the data the UI needs.

**Classes**: `CreateReservationRequest`, `LoginRequest`, `TournamentDetailsDTO`

## 7. Front Controller

**Application**: Implemented with help of `DispatcherServlet` in **Spring Boot*, which receives every HTTP request send from frontend and decides to which controller it should go to.

**Why**: One coherent mechanism managing traffic in the whole application.  

**Classes**: `CourtController`, `ReservationController`, `TournamentController`

## 8. Pessimistic Offline Lock

**Application**: Preventing concurrent modification during critical operations.
 
**Why**: Used during tournament bracket generation and score updates. It ensures that two administrators cannot modify the same tournament state simultaneously, maintaining system consistency.

**Classes**: `TournamentMapper`

## 9. Identity Field

**Application**: Storing primary key from the DB in application entities. Each table has id column, mapping directly to `Long id` field in Java.

**Why**: Maintain integrity between 

**Classes**: every table in the database 

## 10. Foreign Key Mapping

**Application**: Mapping relations between objects using foreign keys in the DB. 

**Why**: Easier search for related objects.

**Classes**: `reservations`, `payments` tables in database

## 11. Single Table Inheritance

**Application**: Mapping the `User` hierarchy to database.

**Why**: Since `Admin` and `Player` share most attributes (email, name, password) and differ mainly in permissions and ranking data, they are stored in a single `users` table with a `user_type` discriminator column (ENUM).

**Classes**: `users` table in database

## 12. Association Table Mapping

**Application**: Creation of additional table connecting two related tables - handling Many-to-Many relationships.

**Why**: Many-to-Many relationship without duplication of data. Elastic management of tournament participants.

**Classes**: `tournament_participants` table in database


