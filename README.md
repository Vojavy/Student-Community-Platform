# Student Community Platform

**Responsive web application for university students to interact, share resources, and collaborate.**

---

## Table of Contents

* [Overview](#overview)
* [Features](#features)
* [Architecture & Tech Stack](#architecture--tech-stack)
* [Prerequisites](#prerequisites)
* [Installation & Setup](#installation--setup)

  * [Backend (Spring Boot)](#backend-spring-boot)
  * [Frontend (Vue.js)](#frontend-vuejs)
  * [Database & Environment](#database--environment)
* [Running with Docker](#running-with-docker)
* [Usage](#usage)
* [Configuration](#configuration)

  * [STAG Integration](#stag-integration)
* [Contributing](#contributing)
* [License](#license)
* [Author](#author)

---

## Overview

This project implements a **responsive**, **secure** web–based database application designed to foster interaction, collaboration, and information sharing among university students.
Developed as a Bachelor’s thesis at the University of Pardubice, Faculty of Electrical Engineering and Informatics.

---

## Features

* **User Authentication & Authorization**

  * Email verification, JWT–based login
* **STAG Integration**

  * Pulls official study data (OSČÍSLO, faculty, program, year)
* **Study Groups Module**

  * Create/join private or public groups
  * Post news, share files, manage membership
* **Forum Module**

  * Multi–topic forums, threaded discussions
  * Moderator & pinned/informational flags
* **Real–time Chat & Notifications**

  * One–to–one WebSocket chat
  * In–app notifications
* **Marketplace & Calendar**

  * Buy/sell student items, events scheduling
* **User Profiles & Networking**

  * Public/private profiles, friends list, search
  * Skills, status, personal “About me”
* **Responsive UI**

  * Mobile–friendly layout in Vue.js

---

## Architecture & Tech Stack

| Layer           | Technology                             |
| --------------- | -------------------------------------- |
| **Backend**     | Java 17, Spring Boot, Spring Security  |
| **Frontend**    | Vue.js 3, Vuex, Vue Router, Axios      |
| **Database**    | PostgreSQL                             |
| **Realtime**    | WebSocket (STOMP over SockJS)          |
| **Container**   | Docker, Docker Compose                 |
| **Build Tools** | Maven (backend), npm & Vite (frontend) |

Architecture follows a **multilayer MVC** pattern:

* **Model**: Entities & DTOs
* **Repository**: Spring Data JPA
* **Service**: Business logic, STAG service
* **Controller**: REST & WebSocket endpoints
* **ViewModel/Store**: Vuex stores & reactive components

---

## Prerequisites

* **Java 17+**
* **Node.js 16+ & npm**
* **PostgreSQL 12+**
* **Docker & Docker Compose** (optional, recommended)

---

## Installation & Setup

### Backend (Spring Boot)

1. Clone the repo and navigate to the backend folder:

   ```bash
   git clone https://github.com/your-org/student-community-platform.git
   cd student-community-platform/backend
   ```
2. Configure application properties in `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/student_platform
   spring.datasource.username=…
   spring.datasource.password=…
   stag.api.url=https://stag.upce.cz/oauth2
   stag.client.id=…
   stag.client.secret=…
   ```
3. Build & run:

   ```bash
   mvn clean package
   java -jar target/student-community-platform-0.1.0.jar
   ```

### Frontend (Vue.js)

1. In a new terminal, go to the frontend folder:

   ```bash
   cd ../frontend
   ```
2. Install dependencies & configure API base URL in `.env`:

   ```
   VITE_API_BASE_URL=http://localhost:8080/api
   ```
3. Start development server:

   ```bash
   npm install
   npm run dev
   ```

### Database & Environment

1. Create PostgreSQL database & user:

   ```sql
   CREATE DATABASE student_platform;
   CREATE USER scp_user WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE student_platform TO scp_user;
   ```
2. Ensure the credentials match `application.properties`.

---

## Running with Docker

A simple way to launch the full stack:

```bash
cd docker
docker-compose up --build
```

This will start:

* `backend` service on `:8080`
* `frontend` service on `:5173`
* `postgres` service on `:5432`

---

## Usage

1. Register a new account or log in with your university email.
2. Complete your profile and connect to STAG for automatic data import.
3. Explore or create **groups**, **forums**, and **events**.
4. Chat privately with other students.
5. Share and browse study materials or marketplace posts.

---

## Configuration

### STAG Integration

To import official student data:

1. In your profile, click **Connect STAG**.
2. Authorize the application via the university’s OAuth2 endpoint.
3. On success, your STAG token is stored and student info is fetched automatically.

---

## Contributing

1. Fork the repository & create a feature branch:

   ```bash
   git checkout -b feature/YourFeature
   ```
2. Commit your changes & push:

   ```bash
   git commit -m "Add YourFeature"
   git push origin feature/YourFeature
   ```
3. Open a Pull Request for review.

Please follow the existing code style and include tests for new functionality.

---

## License

This project is licensed under the **MIT License**. See [LICENSE](LICENSE) for details.

---

## Author

**Andrii Streblychenko**
Bachelor Thesis, University of Pardubice, 2025
Email: [sir.strel@gmail.com](mailto:sir.strel@gmail.com)

---

*This README is part of the “Studentská komunitní platforma” project*
