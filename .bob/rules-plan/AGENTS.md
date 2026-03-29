# Plan Mode Rules (Non-Obvious Only)

## Architectural Constraints

### Database Architecture
- Derby embedded DB creates in `~/altoro/` (user home, NOT project directory)
- Database initialization triggered by first `DBUtil.isValidUser()` call (side effect)
- External DB support requires JNDI datasource configuration in Tomcat's context.xml
- No migration system - database reinitializes completely when `database.reinitializeOnStart=true`

### Session Architecture
- User state stored in HttpSession with specific key `ServletUtil.SESSION_ATTR_USER`
- Account list duplicated in Base64-encoded cookie `AltoroAccounts` (not just session)
- Admin access requires session attribute `admin=altoroadmin` (hardcoded value)

### Build & Deployment Architecture
- WAR intentionally excludes compiled classes and libs (build.gradle:41-42)
- Dependencies must exist in Tomcat's lib folder (not self-contained WAR)
- Java 1.8 compatibility enforced

### REST API Architecture
- Swagger config modified programmatically at startup (not static)
- Jersey JAX-RS configured via `AltoroAPI` class (not standard servlet mapping)
- API base path auto-adjusts based on deployment context path

### Property System Architecture
- Properties loaded once at startup from `WEB-INF/app.properties`
- No hot-reload capability (requires Tomcat restart)

### Testing Architecture
- No unit test framework or test directory structure
- No CI/CD pipeline or automated testing infrastructure