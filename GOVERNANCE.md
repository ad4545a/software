# Governance & Coding Standards

## 1. Technology Stack (Frozen)
- **UI**: JavaFX 21 + CSS
- **Architecture**: MVC (Strict Separation)
- **Database**: H2 Embedded (AES Encrypted)
- **ORM**: JPA (Hibernate)
- **Security**: BCrypt (Cost 12) + AES
- **Reporting**: JasperReports
- **Packaging**: jpackage (.exe)

**FORBIDDEN**: Spring Boot, Web UI, Cloud DB.

## 2. Project Structure
```
jewellery-erp/
 ├─ app/           # Main entry point (MainApp.java)
 ├─ config/        # Configuration classes
 ├─ ui/            # Views (FXML)
 ├─ controller/    # JavaFX Controllers
 ├─ service/       # Business Logic
 ├─ repository/    # Data Access (DAO/Repository)
 ├─ model/         # Entities
 ├─ security/      # Auth & Encryption
 ├─ util/          # Utilities
 ├─ reports/       # Jasper definitions
 ├─ db/            # Database migration/setup
 └─ resources/     # Assets, FXML, Properties
```

## 3. Architecture Rules
1.  **UI Layer**: Never talks to DB directly. Only calls Controllers/Services.
2.  **Controllers**: Handle UI events, delegate to Services. NO SQL here.
3.  **Services**: Contain ALL business logic. Transaction management.
4.  **Repositories**: Handle DB interactions (Hibernate/JPA).

## 4. Naming Conventions
-   **Classes**: `PascalCase` (e.g., `UserSession`)
-   **Methods**: `camelCase` (e.g., `calculateTax`)
-   **Database Tables**: `SNAKE_CASE` (e.g., `LOGIN_AUDIT`)
-   **Database Columns**: `snake_case` (e.g., `password_hash`)

## 5. Data Standards
-   **Weight**: `DECIMAL(10,3)` (3 decimal places mandatory)
-   **Currency**: `DECIMAL(12,2)`
-   **Dates**: `LocalDate`, `LocalDateTime` (No `java.util.Date`)

## 6. Exception Handling
-   No silent failures.
-   Display User-safe message.
-   Log technical details with Error Code.

## 7. Security (Phase 1+)
-   **Passwords**: BCrypt (Cost 12).
-   **Session**: 30 min timeout.
-   **Role Checks**: Mandatory on backend and frontend.
