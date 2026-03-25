# AGENTS.md

This file provides guidance to agents when working with code in this repository.

## Critical Non-Obvious Patterns

### Database Initialization
- Derby database auto-creates in `~/altoro/` (user home, NOT project directory)
- Database initializes on first login attempt via `DBUtil.isValidUser()` call in `StartupListener`
- Set `database.reinitializeOnStart=true` in `app.properties` to force DB reset on each restart
- External DB support via `database.alternateDataSource` property (requires JNDI datasource in Tomcat's context.xml)

### Build & Deploy
```bash
# Build WAR (outputs to build/libs/altoromutual.war)
gradle war

# WAR excludes compiled classes and libs (expects them in server's lib folder)
# See build.gradle lines 41-42 for exclusions
```

### Session Management Pattern
- User object stored in session with key `ServletUtil.SESSION_ATTR_USER`
- Account list encoded in Base64 and stored in cookie `AltoroAccounts`
- Admin access requires session attribute `admin=altoroadmin` (see `ServletUtil.SESSION_ATTR_ADMIN_*`)
- Passwords converted to lowercase during login (line 80 in `LoginServlet.java`)

### App Properties System
- Properties loaded from `WEB-INF/app.properties` at startup
- Access via `ServletUtil.getAppProperty(String)` or `ServletUtil.isAppPropertyTrue(String)`
- Properties enable dangerous features (command execution, XSS persistence, admin functions)
- Properties are commented out by default - must uncomment to enable

### REST API Configuration
- Swagger properties auto-update at startup in `ServletUtil.initializeRestAPI()`
- Modifies `swagger/properties.json` to set correct `basePath` based on context path
- Jersey JAX-RS configured via `AltoroAPI` class in web.xml

### No Test Framework
- Project has no unit tests or test framework configured
- Testing done manually or via security scanning tools (AppScan, etc.)


### Code Review
When reviewing code or executing security scans, check for compliance based on the OSCAL files inside the [`./im8`](./im8) directory. Always include the title of IM8 policy violated in the Bob Findings if posisble e.g. "LM-19: Log Sanitisation Violation - Password logging in plain text"