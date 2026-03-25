# Tennis Club Management System

**A comprehensive desktop application built to streamline tennis club operations, manage reservations, and automate tournament brackets.**

The Tennis Club Management System is a full-stack application packaged as a ready-to-use Windows program. It integrates business logic
with an intuitive component based UI, providing tools for players to book courts and join tournaments, while offering administrators a complete dashboard to manage the club's infrastructure.

*(Disclaimer: For now this application only works on Windows OS)*

## Key Features

- **Tournament Engine** - Handles the full lifecycle of a tournament. From creation and player registration (validating minimum ranking requirements) to the **automatic generation of a single-elimination bracket** with assigned match times and courts.
- **Smart Reservations** - A court booking system with automatically generated timeslots to prevent overlaps. Features dynamic filtering and real-time availability.
- **User Management & Leaderboard** - Comprehensive profile system where players can track their tournament statistics, ranking points, and reservation history. Includes a dynamic, searchable global leaderboard.
- **Admin Dashboard** - Full CRUD (Create, Read, Update, Delete) capabilities for managing courts, tournaments, and users.
- **Mock Payment Integration** - Simulation of a payment flow for tournament entries and court reservations (status updates to `ACTIVE` upon completion).
- **Transactional Integrity** - Custom implementation of the **Unit of Work** pattern to ensure atomic operations (e.g., simultaneous court booking and payment processing)

## Tech Stack & Architecture

This project utilizes a layered architecture with specific design patterns (for a detailed overview, see [`patterns.md`](./patterns.md)). The application leverages a modern web stack wrapped into a desktop executable:

- **Backend:** Java 21, Spring Boot (Custom JDBC Persistence with Unit of Work pattern)
- **Frontend:** React
- **Desktop Wrapper:** Electron (bridges the React frontend and Spring Boot backend into a single `.exe` file) 
- **Database:** MySQL
- **Infrastructures:** Docker & Docker Compose (for containerized database management), Apache Maven

## Application Walkthrough & User Guide

### A) Player Experience

- **Registration & Profile:** Create an account to track your progress. The **Profile Dashboard** displays your reservations, tournament matches, and career statistics (wins/losses/ranking points).
- **Smart Court Booking:** Navigate to the `Courts` tab, use advanced filters (surface type, roof, price), and click `MAKE RESERVATION`. The system dynamically generates available 1-hour slots.
  - *Technical Note:* The status updates to `ACTIVE` only after the mock payment is validated.
- **Tournament Participation:** Join competitive events via the `Tournaments` tab.
  - **Ranking Lock:** Systems prevents registration if your ranking points are below the `Points required` threshold.
  - **Bracket Generation:** Once registration closes, the system automatically generates a **Single-Elimination Bracket**, assigning matches to previously made court reservations.
- **Global Leaderboard:** View the real-time ranking of all players. Click on any athlete to view their performance history.

### B) Administrator Capabilities

- **Club Management:** Full power to add, edit or close the courts for players.
- **Tournament Orchestration:** Admins define tournament rules, set ranking requirements, schedule timeslots for matches and enter final results.

## Quick Setup

To run application locally without building from source:

1. Clone or download the repository
   ```bash
   git clone https://github.com/szampen/tennis-club-management-system.git
   cd tennis-club-management-system
   ```
3. Start the MySQL database container. Navigate to `/docker` folder and run:
    ```bash
    docker-compose up -d
    ```
   (This automatically initializes the database using `init.sql` and creates an `ADMIN` user)
4. Download and run the `Tennis Club Manager.exe` file from the latest release.
5. The app will automatically connect to the local database. You can register a new account or log in.

## Local Development & Build Instructions

If you want to modify the code and build the executable yourself:

**Prerequisites:**

- JDK 21
- Apache Maven

**Important Build Requirement:**

To successfully bundle the Java backend with the frontend into single `.exe` file, you must download and extract JDK 21 into the frontend resources directory.
The exact path must be `src/main/frontend/resources/jre/bin/java.exe`

**Building the app:**

After making changes to the codebase, run the following command in the root directory:
```bash
mvn clean package
```

The final executable (`Tennis Club Manager.exe`) will be generated in the `src/main/frontend/dist-electron` folder.
