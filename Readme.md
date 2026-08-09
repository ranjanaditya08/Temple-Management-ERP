# Backend Project Structure

```angular2html
# Backend Project Structure

backend/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/tte/
│       │       ├── authentication/
│       │       │   ├── api/              # Controllers, DTOs, validation
│       │       │   ├── application/     # Use cases, orchestration, transactions
│       │       │   ├── domain/          # Entities, business rules, repository interfaces
│       │       │   ├── infrastructure/  # JPA, repositories, external integrations
│       │       │   └── config/          # Module configuration
│       │       │
│       │       ├── tenant/
│       │       ├── devotee/
│       │       ├── donation/
│       │       ├── seva/
│       │       └── shared/
│       │
│       └── resources/
│           └── application.properties
│
└── pom.xml
```