# 🎓 Microservices Student Enrollment System

Architecture microservices Spring Boot – basée sur le cours.

---

## 📐 Architecture

```
Client (Browser/Mobile)
        │
        ▼
  [API Gateway :8080]        ← Spring Cloud Gateway
        │
   ┌────┴─────────┐
   │              │
[Eureka :8761]  Routes vers:
                 ├── [student-service  :8081]  → MySQL: student_db
                 ├── [course-service   :8082]  → MySQL: course_db
                 └── [enrollment-service :8083] → MySQL: enrollment_db
```

---

## 🚀 Démarrage (ordre obligatoire)

### Prérequis
- Java 17+
- Maven 3.8+
- MySQL 8 (root/root ou modifier les `application.yml`)

### 1. Eureka Server
```bash
cd eureka-server
mvn spring-boot:run
# http://localhost:8761
```

### 2. Student Service
```bash
cd student-service
mvn spring-boot:run
```

### 3. Course Service
```bash
cd course-service
mvn spring-boot:run
```

### 4. Enrollment Service
```bash
cd enrollment-service
mvn spring-boot:run
```

### 5. API Gateway
```bash
cd api-gateway
mvn spring-boot:run
```

---

## 📡 Endpoints (via Gateway → port 8080)

### Students
| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/students` | Liste tous les étudiants |
| GET | `/api/students/{id}` | Étudiant par ID |
| GET | `/api/students/cnie/{cnie}` | Étudiant par CNIE |
| POST | `/api/students` | Créer un étudiant |
| PUT | `/api/students/{id}` | Modifier un étudiant |
| DELETE | `/api/students/{id}` | Supprimer un étudiant |

### Courses
| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/courses` | Liste tous les cours |
| GET | `/api/courses/{id}` | Cours par ID |
| POST | `/api/courses` | Créer un cours |
| PUT | `/api/courses/{id}` | Modifier un cours |
| DELETE | `/api/courses/{id}` | Supprimer un cours |

### Enrollments
| Méthode | URL | Description |
|---------|-----|-------------|
| POST | `/api/enrollments` | Inscrire un étudiant |
| GET | `/api/enrollments/student/{cnie}` | Dashboard étudiant |
| DELETE | `/api/enrollments/{id}?cnie=CD2387` | Annuler (< 24h) |

---

## 📋 Exemples de requêtes

### Créer un étudiant
```json
POST /api/students
{
  "cnie": "CD2387",
  "firstName": "Ahmed",
  "lastName": "Benali",
  "email": "ahmed@example.com"
}
```

### Créer un cours
```json
POST /api/courses
{
  "title": "Spring Framework",
  "description": "Backend avec Spring Boot",
  "credits": 4
}
```

### Inscrire un étudiant
```json
POST /api/enrollments
{
  "cnie": "CD2387",
  "courseId": 1
}
```

### Dashboard (réponse attendue)
```json
GET /api/enrollments/student/CD2387
[
  {
    "enrollmentId": 1,
    "studentCnie": "CD2387",
    "courseName": "Spring Framework",
    "date": "2026-05-09T10:30:00",
    "deletable": true
  }
]
```

---

## 🔧 Règles métier
- **Max 3 étudiants** par cours
- **Annulation uniquement dans les 24h** après inscription
- Le champ `deletable` indique si la suppression est possible
- Pas de double inscription au même cours

---

## ⚙️ Configuration MySQL

Les bases de données sont créées automatiquement (`createDatabaseIfNotExist=true`).  
Pour modifier les credentials, éditez les fichiers `src/main/resources/application.yml` de chaque service.

Par défaut :
- URL : `localhost:3306`
- Username : `root`
- Password : `root`
