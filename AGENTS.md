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


### Code Review
When reviewing code or executing security scans, always identify all security issues, categorize them by type (OWASP Top 10, CWE), and rank them by severity (Critical, High, Medium, Low).

For each vulnerability found, provide:
1. Location (file and line number)
2. Vulnerability type and category
3. Description of the security risk
4. Potential attack scenario
5. Severity rating with justification