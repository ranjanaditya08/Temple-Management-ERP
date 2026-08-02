# 🔒 Locked Architecture Decisions

## 1. Architecture
- Architecture Style: **Modular Monolith**

---

## 2. Module Communication
- Synchronous communication through **public service interfaces**.
- Asynchronous communication through **domain events**.
- No direct repository or database access across modules.

---

## 3. Module Ownership
- Every module owns its own business logic and data.
- Only the owning module can create, update, or delete its entities.
- Other modules must interact through the owning module's public API.

Examples:
- Devotee Module → Owns `Devotee`
- Donation Module → Owns `Donation`
- Seva Module → Owns `SevaBooking`

---

## 4. Internal Module Structure

Module
├── api
├── application
├── domain
├── infrastructure
└── config

---

## 5. Layer Responsibilities

### API Layer
- REST Controllers
- Request/Response DTOs
- Input Validation
- API Documentation

### Application Layer
- Use-case orchestration
- Transaction management
- Inter-module communication
- Domain event publishing

### Domain Layer
- Entities
- Value Objects
- Business Rules
- Domain Services
- Repository Interfaces

### Infrastructure Layer
- Repository Implementations
- JPA/Hibernate
- Database access
- External integrations (Email, Payment, Storage, etc.)
- Configuration

---

## 6. Dependency Rule

Dependencies always point inward.

API
↓
Application
↓
Domain
↑
Infrastructure (implements interfaces)

- Domain must not depend on Spring, JPA, HTTP, Controllers, or Database frameworks.

---

## 7. Request Flow

Client
↓
API
↓
Application
↓
Domain
↓
Repository Interface
↓
Infrastructure
↓
Database

---

## 8. Entity Access Rule

❌ Never:
- Access another module's repositories.
- Read/write another module's tables directly.

✅ Always:
- Call the owning module's public service interface.

---

## 9. Business Logic Placement

Business rules belong only in:
- Domain Layer
- Application Layer

Business logic must not exist in:
- Controllers
- Repositories
- DTOs
- Mappers

---

## 10. Design Principles

- Feature-first package organization.
- Strong module boundaries.
- Low coupling, high cohesion.
- Single responsibility for each module.
- Framework-independent domain model.
- Future-ready for microservice extraction if required.

---

# 🔒 Locked Database Architecture Decisions

## 1. Database Strategy
- Single PostgreSQL database.
- Single database schema.
- Entire application runs on one database instance.

---

## 2. Module Data Ownership
- Every module owns its own tables.
- Only the owning module can create, update, or delete its data.
- Other modules must interact through the owning module's public service interface.

Examples:
- Devotee Module → `devotee`, `devotee_address`, `devotee_family`
- Donation Module → `donation`, `donation_item`, `donation_receipt`
- Seva Module → `seva`, `seva_booking`

---

## 3. Cross-Module Data Access
❌ Never:
- Access another module's repositories.
- Read/write another module's tables directly.
- Execute business operations on another module's entities.

✅ Always:
- Call the owning module's public service interface.
- Use domain events for asynchronous communication.

---

## 4. Cross-Module Relationships
- Cross-module relationships are maintained using entity IDs.
- Example:
    - `donation.devotee_id`
    - `seva_booking.devotee_id`
- Modules reference entities by ID but never own them.

---

## 5. Foreign Keys
- Database foreign keys are allowed between modules.
- Foreign keys are used to enforce referential integrity.
- Business interactions must still occur through module APIs, not direct table access.

---

## 6. Table Ownership
Each table has exactly one owning module.

Example:

Authentication
- user
- role
- permission

Devotee
- devotee
- devotee_address
- devotee_family

Donation
- donation
- donation_item
- donation_receipt

Seva
- seva
- seva_booking

---

## 7. Table Naming Convention
- Feature-oriented table names.
- Singular nouns.
- Prefix related tables with the module name where appropriate.

Examples:
- devotee
- devotee_family
- donation
- donation_receipt
- seva_booking

Avoid generic names such as:
- master
- details
- transaction
- data

---

## 8. Soft Delete Strategy
Business entities use soft delete.

Standard fields:
- is_deleted
- deleted_at
- deleted_by

Physical deletion is reserved for exceptional administrative or maintenance scenarios.

---

## 9. Standard Audit Columns
All business tables will include:

- id
- created_at
- created_by
- updated_at
- updated_by
- is_deleted
- deleted_at
- deleted_by
- version

Purpose:
- Auditing
- Traceability
- Optimistic locking
- Concurrency control

---

## 10. Data Validation Strategy
Validation is enforced at multiple layers.

### API Layer
- Request validation
- Input format
- Mandatory fields

### Domain Layer
- Business rules
- Domain invariants
- Cross-field validation

### Database Layer
- Primary Keys
- Foreign Keys
- Unique Constraints
- NOT NULL Constraints
- CHECK Constraints

Each layer is responsible for protecting data integrity at its own boundary.

---

## 11. Database Principles
- One database for the entire application.
- One schema.
- Clear ownership of every table.
- No shared ownership.
- No direct cross-module data manipulation.
- Referential integrity enforced through foreign keys.
- Business rules enforced by the owning module.
- Database constraints complement application validation.

---

# 🔒 Locked Authentication & Authorization Architecture Decisions

## 1. Authentication Module
- Authentication is a dedicated standalone module.
- Responsible for:
    - Login
    - Logout
    - Refresh Token
    - Password Management
    - Session Management
    - JWT Generation
- No business module is responsible for authentication.

---

## 2. User Ownership
The Authentication module owns all identity-related entities.

Owned Entities:
- User
- Role
- Permission
- User Session
- Refresh Token

No other module may create or manage users, roles, or permissions.

---

## 3. Authentication Strategy
- JWT-based authentication.
- Stateless REST APIs.
- Every authenticated request must include a valid JWT Access Token.
- Refresh Tokens are used to obtain new Access Tokens without requiring users to log in again.

---

## 4. Authorization Strategy
Authorization consists of two independent checks.

### Permission Authorization
Determines whether the user is allowed to perform an operation.

Example:
- DONATION_CREATE
- DEVOTEE_EDIT

### Business Authorization
Determines whether the user is allowed to perform the operation on a specific resource.

Examples:
- User belongs to the correct temple.
- Donation is still editable.
- Record is not locked.
- User owns the resource.

Business authorization is implemented inside the Application and Domain layers.

---

## 5. Access Control Model
Role-Based Access Control (RBAC) with Permission-Based Authorization.

Structure:

User
↓
Role(s)
↓
Permission(s)

Roles contain permissions.
Permissions determine allowed operations.

---

## 6. Permission Naming Convention
Permissions follow the format:

MODULE_ACTION

Examples:

DEVOTEE_VIEW
DEVOTEE_CREATE
DEVOTEE_EDIT
DEVOTEE_DELETE

DONATION_VIEW
DONATION_CREATE
DONATION_EDIT
DONATION_DELETE

SEVA_VIEW
SEVA_BOOK
SEVA_CANCEL

REPORT_VIEW
REPORT_EXPORT

Avoid generic permissions such as:
- READ
- WRITE
- ADMIN

---

## 7. Current User Context
A common authenticated user context is available throughout the application.

Contains:

- userId
- username
- roles
- permissions

Application services use this context for authorization and auditing.

---

## 8. Authentication Flow

Client
↓
Login
↓
Authentication Module
↓
Credential Validation
↓
Generate JWT Access Token
↓
Generate Refresh Token
↓
Return Tokens
↓
Client includes Access Token in every request

---

## 9. Authorization Flow

Incoming Request
↓
JWT Authentication
↓
Permission Authorization
↓
Business Authorization
↓
Execute Use Case

Both authorization stages must succeed before the requested operation is executed.

---

## 10. Security Principles

- Authentication is centralized.
- Authorization is enforced at multiple layers.
- Business modules never implement authentication.
- Business modules never manage users or roles.
- Every API endpoint requires authentication unless explicitly marked public.
- Permission checks occur before business logic execution.
- Business rules are not embedded in the security framework.

---

## 11. Architecture Principles

- Dedicated Authentication module.
- JWT-based stateless authentication.
- RBAC with permission-based authorization.
- Roles are collections of permissions.
- Business authorization handled in Application/Domain layers.
- Standard CurrentUser abstraction available throughout the application.
- Clear separation between Authentication and Authorization responsibilities.

---

# 🔒 Locked Multi-Tenancy Architecture

## 1. Multi-Tenancy Strategy
- Architecture follows Shared Database + Shared Schema.
- Entire application uses one PostgreSQL database.
- All temples share the same schema.
- Every business record belongs to exactly one temple.

---

## 2. Tenant Definition
- A Temple is the tenant.
- Every business operation executes within the context of a single temple.

---

## 3. Tenant Identification
- Every business table contains:
    - temple_id (NOT NULL)

Examples:
- devotee.temple_id
- donation.temple_id
- seva.temple_id
- seva_booking.temple_id

Reference/master tables that are global (e.g., countries, states, payment modes) do not require temple_id.

---

## 4. Tenant Isolation
- Users can only access data belonging to their authorized temple(s).
- All business queries are automatically filtered by temple_id.
- Cross-tenant data access is prohibited unless explicitly granted by system-level permissions.

---

## 5. User–Temple Relationship

A User may belong to one or more temples.

Example:

Temple A
├── Admin
├── Accountant

Temple B
├── Manager

A Super Admin may have access to all temples.

---

## 6. Current Tenant Context

Each authenticated request contains:

- userId
- username
- roles
- permissions
- activeTempleId

The CurrentTenant context is available throughout the application.

---

## 7. Authentication
- Authentication remains centralized.
- JWT contains the user's authorized temple(s).
- Client sends the active temple when the user has access to multiple temples.
- Backend validates that the user is authorized for the selected temple.

---

## 8. Authorization
Access is granted only when BOTH conditions are satisfied:

1. User has the required permission.
2. User has access to the requested temple.

Example:

Permission:
DONATION_CREATE

AND

Temple Access:
Temple = 101

Only then may the operation proceed.

---

## 9. Module Ownership

Each module owns only its own data.

Examples:

Devotee Module
- devotee
- devotee_family

Donation Module
- donation
- donation_receipt

Seva Module
- seva
- seva_booking

Every owned table includes temple_id.

Modules never bypass tenant boundaries.

---

## 10. Database Constraints

Business tables include:

- id
- temple_id
- created_at
- created_by
- updated_at
- updated_by
- is_deleted
- deleted_at
- deleted_by
- version

Recommended composite indexes:

- (temple_id, id)
- (temple_id, status)
- (temple_id, created_at)

Additional indexes should include temple_id whenever queries are tenant-scoped.

---

## 11. Public APIs

Every module automatically executes within the CurrentTenant context.

Application services must never accept arbitrary temple_id values from clients for authorization decisions.
The backend derives or validates the active temple from the authenticated context.

---

## 12. Reports

All reports are tenant-aware by default.

Temple users:
- See only their own temple's data.

Super Admin:
- Can generate reports for:
    - Single temple
    - Multiple selected temples
    - All temples

---

## 13. Future Scalability

The architecture is designed so that, if required in the future, the system can evolve to:
- Separate schemas per tenant
- Separate databases per tenant
- Microservices

without changing business module boundaries.

---

## 14. Multi-Tenancy Principles

- Temple is the tenant.
- Shared PostgreSQL database.
- Shared schema.
- Tenant isolation through temple_id.
- Every business record belongs to one temple.
- Automatic tenant filtering.
- Centralized authentication.
- Permission + Temple authorization.
- No cross-tenant data leakage.
- Future-ready for higher isolation strategies.