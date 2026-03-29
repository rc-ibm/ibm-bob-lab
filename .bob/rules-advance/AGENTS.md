# Advance Mode Rules (Non-Obvious Only)

## Critical Patterns for Advanced Code Changes

### Session & Cookie Management
- User object MUST be stored with key `ServletUtil.SESSION_ATTR_USER` (not "user")
- Account list cookie MUST be Base64-encoded with name `AltoroAccounts`
- Admin session requires BOTH: session attribute `admin=altoroadmin` AND proper authentication

### Database Operations
- ALL password comparisons happen in lowercase (see `LoginServlet.java:80`)
- Database connection uses Derby embedded driver at `~/altoro/` (NOT project directory)
- `DBUtil.isValidUser()` triggers DB initialization on first call (side effect, not obvious)
- External DB requires JNDI datasource name in `database.alternateDataSource` property

### Property System
- Properties accessed via `ServletUtil.getAppProperty()` or `ServletUtil.isAppPropertyTrue()`
- Properties file location: `WEB-INF/app.properties` (loaded at startup only)

### REST API
- Swagger config auto-updates at startup via `ServletUtil.initializeRestAPI()`
- Jersey JAX-RS registration happens in `AltoroAPI` class (not web.xml servlet mapping)
- `swagger/properties.json` gets modified programmatically (don't edit manually)

### Build Artifacts
- WAR excludes compiled classes and libs (lines 41-42 in build.gradle)
- Server's lib folder must contain dependencies (not bundled in WAR)
- Source compatibility locked to Java 1.8 (line 5-6 in build.gradle)

### Browser Testing
- Application runs on Tomcat 7 (default port 8080)
- Database initializes on FIRST login attempt (use jsmith/demo1234)
- Swagger UI available at `/swagger/` path for REST API testing