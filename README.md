Zde je profesionálně napsaný **README.md** soubor pro Git repozitář vašeho bakalářského projektu *Studentská komunitní platforma*:

---

````markdown
# 🧑‍🎓 The Student Community Platform

A responsive web-based platform created as part of a Bachelor's thesis at the University of Pardubice. The project aims to facilitate interaction, collaboration, and resource sharing among university students.

## 📚 Project Overview

This application serves as a centralized platform for students to:
- Create and manage study groups
- Share materials and announcements
- Discuss via forums
- Connect via internal messaging
- Integrate with IS/STAG for verified academic data

> 🎓 This project was developed by Andrii Streblychenko (FEI, University of Pardubice, 2025)

---

## ⚙️ Tech Stack

### Backend
- Java 21
- Spring Boot 3
- PostgreSQL
- JWT-based Authentication
- WebSocket (for chat and notifications)
- Maven

### Frontend
- Vue.js 3 + Composition API
- Vue Router
- Pinia (store)
- Tailwind CSS
- Axios

### Infrastructure
- Docker & Docker Compose
- RESTful API architecture
- CI-ready structure

---

## 🧩 Features

### ✅ Implemented
- JWT authentication (local + Microsoft login)
- Email verification flow
- Study group creation and role-based access control
- Public/private forums with reply & moderation options
- Personal and public user profiles
- Private chat between users (real-time)
- STAG integration (via token)
- User search and contact sharing

### 🚧 Planned (not yet implemented)
- Event system and calendar
- File storage in external/NoSQL system
- Marketplace ("bazar") for local student offers
- Group file attachments
- Group announcements/events (UI only for now)

---

## 🛡️ Security & Architecture

- RESTful backend following layered architecture (controllers, services, repositories, DTOs)
- Role-based authorization system
- Secure token management via `user_tokens` table
- MVVM-C frontend pattern with modular separation of view, logic, and routing
- Input validation both client- and server-side

---

## 🗃️ Database

- PostgreSQL schema with over 20 interconnected tables
- Support for views (e.g. `user_profile_view`, `pending_friend_requests`)
- Manual domain/role provisioning (for university-wide scope)
- JSONB usage for flexible profile metadata and post topics

---

## 🧪 How to Run Locally

### Prerequisites
- Docker
- Java 21
- Node.js + npm

### Backend

```bash
cd backend
./mvnw spring-boot:run
````

Or with Docker:

```bash
docker-compose up --build
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The app will be available at `http://localhost:5173`

---

## 📁 Directory Structure

```
├── backend
│   ├── src/main/java/cz/upce/platform
│   └── application.properties
├── frontend
│   ├── src
│   │   ├── views
│   │   ├── components
│   │   ├── stores
│   │   ├── models
│   │   └── router
└── docker-compose.yml
```

---

## ✍️ Author

**Andrii Streblychenko**
Faculty of Electrical Engineering and Informatics
University of Pardubice, 2025
📧 [streblychenko@student.upce.cz](mailto:streblychenko@student.upce.cz)

---

## 📝 License

This project is licensed under the MIT License – see the [LICENSE](LICENSE) file for details.

---

## 📎 Thesis

The project is documented in detail in the attached thesis:
📄 [`Bakalarka.pdf`](./docs/Bakalarka.pdf)

```

---

Pokud byste chtěl českou verzi tohoto README, nebo ji přidat jako `README.cs.md`, dejte vědět. Můžu také připravit `.gitignore`, `LICENSE`, nebo CI konfiguraci.
```
