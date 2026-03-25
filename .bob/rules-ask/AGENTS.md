# Ask Mode Rules (Non-Obvious Only)

## Documentation Context

### Project Structure (Counterintuitive)
- `src/` contains servlet/API code, NOT static web content
- `WebContent/` is the web root (JSP, HTML, static files)
- `WebContent/WEB-INF/` contains config and libs (not accessible via HTTP)
- Database lives in `~/altoro/` (user home), NOT in project directory

### Hidden Architecture Decisions
- No test framework exists
- Passwords converted to lowercase
- Derby database auto-initializes on first login (not on startup)
- Swagger properties file modified programmatically at startup

### Misleading Naming
- `database.alternateDataSource` requires external JNDI setup in Tomcat
- `enableAdminFunctions` will trash database if used with security scanners

### REST API Documentation
- Swagger UI is the canonical API reference (not code comments)
- API endpoints registered via Jersey JAX-RS in `AltoroAPI` class
- Swagger config auto-updates based on deployment context path