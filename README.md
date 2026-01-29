# Formation Management System

Une application complète de gestion de formation avec backend Spring Boot et frontend React.

## 🚀 Technologies utilisées

### Backend
- **Spring Boot 3.5.9** (Java 21)
- **Spring Security** avec JWT
- **Spring Data JPA** avec Hibernate
- **MySQL 8.0**
- **Springdoc OpenAPI** pour la documentation API
- **Maven** pour la gestion des dépendances

### Frontend
- **React 18**
- **Redux Toolkit** pour la gestion d'état
- **Material-UI (MUI)** pour l'interface utilisateur
- **React Router** pour la navigation
- **Axios** pour les appels API

## 📋 Fonctionnalités

### Gestion des utilisateurs
- **Administrateurs** : Accès complet à toutes les fonctionnalités
- **Professeurs** : Gestion des séances et présences
- **Étudiants** : Consultation du calendrier et des séances

### Modules principaux
- 👥 **Gestion des étudiants** (CRUD complet)
- 👨‍🏫 **Gestion des professeurs** (CRUD complet)
- 📚 **Gestion des cours** (CRUD complet)
- 📅 **Gestion des séances** (planification et suivi)
- ✅ **Gestion des présences** (marquage par les professeurs)
- 📊 **Tableau de bord** avec statistiques

## 🛠️ Installation et démarrage

### Prérequis
- Java 21+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### Démarrage rapide (Recommandé)

**Option 1: Script automatique**
```bash
# Windows
start-all.bat

# Ou manuellement:
```

**Option 2: Démarrage manuel**

### Backend (Spring Boot)

1. **Cloner le projet**
```bash
git clone <repository-url>
cd formation-management
```

2. **Configuration de la base de données**
```properties
# src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/formation_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. **Démarrer l'application**
```bash
mvn spring-boot:run
# Ou utiliser: start-backend.bat
```

L'API sera accessible sur `http://localhost:8081`

### Frontend (React)

1. **Installer les dépendances**
```bash
cd formation-frontend
npm install
```

2. **Démarrer l'application**
```bash
npm start
# Ou utiliser: start-frontend.bat
```

L'application sera accessible sur `http://localhost:3000`

### ⚠️ Résolution des problèmes courants

**Erreur de compilation React:**
```bash
cd formation-frontend
rm -rf node_modules package-lock.json
npm install
npm start
```

**Erreur de base de données:**
- Vérifiez que MySQL est démarré
- Créez la base de données `formation_db`
- Vérifiez les credentials dans `application.properties`

## 📚 Documentation API

Une fois l'application démarrée, la documentation Swagger est disponible sur :
- **Swagger UI** : `http://localhost:8081/swagger-ui.html`
- **OpenAPI JSON** : `http://localhost:8081/v3/api-docs`

## 🔐 Authentification

L'application utilise JWT (JSON Web Tokens) pour l'authentification :
- Les tokens sont stockés dans des cookies HttpOnly pour la sécurité
- Durée de vie : 24 heures
- Rôles supportés : ADMIN, PROF, STUDENT

### Comptes de test
```
Admin:
- Email: admin@formation.com
- Mot de passe: admin123

Professeur:
- Email: prof@formation.com  
- Mot de passe: prof123

Étudiant:
- Email: student@formation.com
- Mot de passe: student123
```

## 🏗️ Architecture

### Backend
```
src/main/java/com/formation/
├── config/          # Configuration (Security, OpenAPI, etc.)
├── controller/      # Contrôleurs MVC (Thymeleaf)
├── controller/api/  # Contrôleurs REST API
├── dto/            # Data Transfer Objects
├── entity/         # Entités JPA
├── mapper/         # Mappers Entity ↔ DTO
├── repository/     # Repositories JPA
├── security/       # Configuration sécurité et JWT
└── service/        # Services métier
```

### Frontend
```
src/
├── components/     # Composants réutilisables
├── pages/         # Pages de l'application
├── services/      # Services API
├── store/         # Redux store et slices
└── utils/         # Utilitaires
```

## 🔄 API Endpoints

### Authentification
- `POST /api/auth/login` - Connexion
- `POST /api/auth/logout` - Déconnexion
- `GET /api/auth/me` - Informations utilisateur courant

### Étudiants (ADMIN uniquement)
- `GET /api/students` - Liste des étudiants
- `GET /api/students/{id}` - Détails d'un étudiant
- `POST /api/students` - Créer un étudiant
- `PUT /api/students/{id}` - Modifier un étudiant
- `DELETE /api/students/{id}` - Supprimer un étudiant
- `GET /api/students/count` - Nombre total d'étudiants

### Professeurs (ADMIN uniquement)
- `GET /api/profs` - Liste des professeurs
- `GET /api/profs/{id}` - Détails d'un professeur
- `POST /api/profs` - Créer un professeur
- `PUT /api/profs/{id}` - Modifier un professeur
- `DELETE /api/profs/{id}` - Supprimer un professeur

### Cours (ADMIN uniquement)
- `GET /api/courses` - Liste des cours
- `GET /api/courses/{id}` - Détails d'un cours
- `POST /api/courses` - Créer un cours
- `PUT /api/courses/{id}` - Modifier un cours
- `DELETE /api/courses/{id}` - Supprimer un cours

### Séances (ADMIN et PROF)
- `GET /api/sessions` - Liste des séances
- `GET /api/sessions/{id}` - Détails d'une séance
- `POST /api/sessions` - Créer une séance
- `PUT /api/sessions/{id}` - Modifier une séance
- `DELETE /api/sessions/{id}` - Supprimer une séance
- `GET /api/sessions/student/{studentId}` - Séances d'un étudiant
- `GET /api/sessions/prof/{profId}` - Séances d'un professeur

## 🔒 Sécurité

- **CORS** configuré pour le frontend React
- **JWT** avec cookies HttpOnly
- **Validation** des données côté backend et frontend
- **Autorisation** basée sur les rôles (@PreAuthorize)
- **Chiffrement** des mots de passe avec BCrypt

## 🚧 Développement futur

- [ ] Implémentation complète du calendrier interactif
- [ ] Gestion des niveaux et cours-niveaux
- [ ] Système de notifications
- [ ] Rapports et statistiques avancées
- [ ] Export des données (PDF, Excel)
- [ ] Interface mobile responsive
- [ ] Tests unitaires et d'intégration

## 📝 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 👥 Contributeurs

- Développeur principal : [Votre nom]
- Contact : [Votre email]

## 🐛 Signaler un bug

Pour signaler un bug ou demander une fonctionnalité, veuillez créer une issue sur GitHub.