# IBM Bob Workshop - Java Application Modernization | Part 2
## Case Study: AltoroJ Banking Application Upgrade (Java 8 → Java 21 + Jakarta EE 10)

### Audience
Software engineers, DevOps engineers, platform architects, and modernization specialists

### Goal of the Workshop
Demonstrate how **IBM Bob** can:
- Autonomously modernize Java applications using agentic workflows
- Analyze legacy codebases and create modernization plans
- Execute iterative modernization tasks with validation
- Update build configurations, dependencies, and runtime environments
- Perform large-scale namespace migrations (Java EE → Jakarta EE)
- Validate modernization through automated testing

We will use **IBM Bob's agentic modernization workflow** to comprehensively upgrade the AltoroJ Banking Application from Java 8 to Java 21 and migrate from Java EE 7 to Jakarta EE 10, leveraging Bob's autonomous capabilities to handle complex refactoring tasks including namespace migrations across the entire codebase.

---

## Understanding Bob's Agentic Modernization Workflow

### What is Agentic Modernization?

Traditional modernization approaches rely on:
- **Rule-based transformations**: Deterministic changes using recipes and patterns
- **Manual refactoring**: Developers making changes based on analysis reports

**Agentic modernization** introduces AI-driven autonomous agents that:
- Analyze codebases and understand context
- Create and manage task lists dynamically
- Execute complex refactoring iteratively
- Validate changes through testing and builds
- Self-correct when issues are detected
- Document all changes comprehensively

### How Bob's Workflow Works

1. **Analyze Phase**: Bob analyzes the application to understand its current state and checks for required tools
2. **Upgrade Phase**: Bob creates a comprehensive task list for modernization and performs agentic upgrade
3. **Validate Phase**: Bob runs tests, builds, and validates the application

### Key Advantages

- **Autonomous**: Bob works independently within defined boundaries
- **Iterative**: Changes are made incrementally with validation
- **Self-Healing**: Bob detects and fixes issues automatically
- **Comprehensive**: Bob handles both simple and complex transformations
- **Transparent**: Developers review and approve all changes
- **Documented**: Complete audit trail of all modifications

---

## Workshop Flow Overview

This lab follows Bob's agentic modernization workflow:

1. **Understanding**: Analyze the current application state
2. **Agentic Modernization**: Let Bob autonomously modernize the application
3. **Validation**: Review Bob's work and validate functionality

Each phase demonstrates Bob's autonomous capabilities while maintaining developer control and oversight.

To kickstart the workflow, you can either click on the start button for the `Java modernization` workflows in the Bob chat homepage, or send the command `/start_java_modernization` in the chat input box.

Choose `Java Upgrade` and Target Java version of `Java 21` when promopted.

---

## Phase 1: Analyze

In this phase, Bob analyzes the AltoroJ application to understand its current state and identify modernization requirements.

### What Bob Does:
- Examines build.gradle to identify current Java version (8) and Java EE dependencies
- Checks Gradle wrapper version compatibility
- Analyzes dependencies for deprecated configurations
- Identifies required changes for Java 21 compatibility
- Identifies Java EE to Jakarta EE migration requirements

### Key Findings:
- **Current Java Version**: 8 (very outdated)
- **Target Java Version**: 21 (LTS)
- **Current Platform**: Java EE 7 (javax.* packages)
- **Target Platform**: Jakarta EE 10 (jakarta.* packages)
- **Gradle Version**: 6.9.4 (needs upgrade for Java 21 support)
- **Deprecated Configurations**: `compile`, `providedCompile`, `archiveName`
- **Jersey Version**: 2.27 (needs upgrade to 3.x for Jakarta EE compatibility)
- **Namespace Migration**: 125+ imports need migration from javax.* to jakarta.*

---

## Phase 2: Upgrade - Part A (Java 21)

Bob autonomously performs the Java 21 modernization in an iterative manner, validating each change.

### Step 1: Update Java Compatibility

**What Bob Does:**
- Updates `sourceCompatibility` from "8" to "21"
- Updates `targetCompatibility` from "8" to "21"
- Validates the change by reading the file back

### Step 2: Upgrade Gradle Wrapper

**What Bob Does:**
- Updates `gradle/wrapper/gradle-wrapper.properties`
- Changes distribution URL from Gradle 6.9.4 to 8.5
- Validates the wrapper configuration

### Step 3: Modernize Gradle Configuration

**What Bob Does:**
- Replaces deprecated `compile` with `implementation`
- Replaces deprecated `providedCompile` with `compileOnly`
- Replaces deprecated `archiveName` with `archiveFileName`
- Ensures all changes are syntactically correct

### Step 4: Fix Deprecated API Usage

**What Bob Does:**
- Identifies deprecated `Class.newInstance()` in DBUtil.java
- Replaces with `Class.getDeclaredConstructor().newInstance()`
- Ensures Java 21 compatibility for reflection API

---

## Phase 2: Upgrade - Part B (Jakarta EE 10 Migration)

After successfully upgrading to Java 21, Bob continues with the Jakarta EE 10 migration to modernize the application stack.

### Step 1: Update Dependencies

**What Bob Does:**
- Replaces `javax:javaee-api:7.0` with `jakarta.platform:jakarta.jakartaee-api:10.0.0`
- Updates Jersey from 2.27 to 3.1.3 (Jakarta-compatible versions):
  - `org.glassfish.jersey.core:jersey-server:3.1.3`
  - `org.glassfish.jersey.containers:jersey-container-servlet:3.1.3`
  - `org.glassfish.jersey.inject:jersey-hk2:3.1.3`

### Step 2: Update Web Deployment Descriptor

**What Bob Does:**
- Migrates `web.xml` from Java EE 2.5 to Jakarta EE 6.0
- Changes namespace from `http://java.sun.com/xml/ns/javaee` to `https://jakarta.ee/xml/ns/jakartaee`
- Updates schema location to `web-app_6_0.xsd`
- Updates init-param from `javax.ws.rs.Application` to `jakarta.ws.rs.Application`

### Step 3: Migrate Source Code Namespaces

**What Bob Does:**
- Performs automated namespace migration across 28 Java files
- **79 imports** migrated from `javax.servlet.*` to `jakarta.servlet.*`
- **42 imports** migrated from `javax.ws.rs.*` to `jakarta.ws.rs.*`
- **4 imports** migrated from `javax.annotation.*` to `jakarta.annotation.*`

**Files Modified:**
- All servlet classes (HttpServlet, HttpServletRequest, HttpServletResponse, etc.)
- All filter classes (Filter, FilterChain, FilterConfig)
- All JAX-RS API classes (@Path, @GET, @POST, Response, etc.)
- All security annotations (@PermitAll)

**Note:** Core Java packages (javax.sql, javax.xml, javax.crypto) remain unchanged as per Jakarta EE specification.

---

## Phase 3: Validate

Bob validates the modernization by building the application after each major phase.

### Build Validation - Java 21 Upgrade

**What Bob Does:**
- Executes the Gradle build command after Java 21 upgrade
- Monitors build output for errors
- Confirms successful compilation
- Verifies WAR file generation

### Build Results (Java 21):
✅ **BUILD SUCCESSFUL**
- All Java source files compiled with Java 21
- WAR file `altoromutual.war` generated successfully
- No compilation errors
- Deprecated API warning in DBUtil.java identified and fixed

### Build Validation - Jakarta EE 10 Migration

**What Bob Does:**
- Executes the Gradle build command after Jakarta EE migration
- Validates all namespace changes compile correctly
- Confirms no broken imports or references
- Verifies WAR file generation with Jakarta EE dependencies

### Build Results (Jakarta EE 10):
✅ **BUILD SUCCESSFUL**
- All 28 Java source files compiled with Jakarta EE 10
- 125+ namespace imports successfully migrated
- WAR file `altoromutual.war` generated successfully
- Zero compilation errors
- Application ready for Jakarta EE 10 compatible servers (Tomcat 10+, WildFly 27+)

### Summary of Validation:
- **Exit Code**: 0 (Success)
- **Compilation**: Clean, no errors across both phases
- **WAR Generation**: Successful
- **Namespace Migration**: 100% complete
- **Build Time**: ~5-7 seconds per build

---

## Key Modernization Changes

### 1. Gradle Configuration

**Before (Java 8 + Java EE 7):**
```gradle
sourceCompatibility = "8"
targetCompatibility = "8"

dependencies {
    compile <>
    providedCompile 'javax:javaee-api:7.0'
    compile 'org.glassfish.jersey.core:jersey-server:2.27'
    compile 'org.glassfish.jersey.containers:jersey-container-servlet:2.27'
    compile 'org.glassfish.jersey.inject:jersey-hk2:2.27'
}

war {
    archiveName = 'altoromutual.war'
}
```

**After (Java 21 + Jakarta EE 10):**
```gradle
sourceCompatibility = "21"
targetCompatibility = "21"

dependencies {
    implementation <>
    compileOnly 'jakarta.platform:jakarta.jakartaee-api:10.0.0'
    implementation 'org.glassfish.jersey.core:jersey-server:3.1.3'
    implementation 'org.glassfish.jersey.containers:jersey-container-servlet:3.1.3'
    implementation 'org.glassfish.jersey.inject:jersey-hk2:3.1.3'
}

war {
    archiveFileName = 'altoromutual.war'
}
```

### 2. Gradle Wrapper

**Before:**
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-6.9.4-bin.zip
```

**After:**
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
```

### 3. Web Deployment Descriptor (web.xml)

**Before (Java EE 2.5):**
```xml
<web-app xmlns="http://java.sun.com/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
         http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
         version="2.5">
    <init-param>
        <param-name>javax.ws.rs.Application</param-name>
        <param-value>com.ibm.security.appscan.altoromutual.api.AltoroAPI</param-value>
    </init-param>
</web-app>
```

**After (Jakarta EE 6.0):**
```xml
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee
         https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
         version="6.0">
    <init-param>
        <param-name>jakarta.ws.rs.Application</param-name>
        <param-value>com.ibm.security.appscan.altoromutual.api.AltoroAPI</param-value>
    </init-param>
</web-app>
```

### 4. Source Code Namespace Migration

**Before (javax.* packages):**
```java
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.annotation.security.PermitAll;
```

**After (jakarta.* packages):**
```java
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.annotation.security.PermitAll;
```

### 5. Build Configuration Modernization

| Deprecated | Modern Replacement |
|------------|-------------------|
| `compile` | `implementation` |
| `providedCompile` | `compileOnly` |
| `archiveName` | `archiveFileName` |
| `Class.newInstance()` | `Class.getDeclaredConstructor().newInstance()` |

### 6. Dependency Migration Summary

| Component | Before | After |
|-----------|--------|-------|
| Java EE API | javax:javaee-api:7.0 | jakarta.platform:jakarta.jakartaee-api:10.0.0 |
| Jersey Server | jersey-server:2.27 | jersey-server:3.1.3 |
| Jersey Container | jersey-container-servlet:2.27 | jersey-container-servlet:3.1.3 |
| Jersey Injection | jersey-hk2:2.27 | jersey-hk2:3.1.3 |

---

## Bob's Autonomous Capabilities Demonstrated

### 1. Dynamic Task Management

Bob doesn't just follow a fixed script. It:
- Creates initial task lists based on analysis
- Adds new tasks when issues are discovered
- Reprioritizes tasks based on dependencies
- Marks tasks complete after validation

### 2. Self-Correction

When Bob encounters issues:
- Detects build failures or errors
- Analyzes error messages
- Determines root cause
- Creates fix tasks
- Implements fixes
- Validates fixes work

### 3. Iterative Validation

Bob validates continuously:
- After each significant change
- Before moving to next task
- At completion of each phase
- Final validation before reporting success

### 4. Context Awareness

Bob understands:
- Dependencies between changes (Gradle version must support Java 21, Jersey 3.x requires Jakarta EE)
- Impact of modifications (deprecated configurations need updates, namespace changes affect all imports)
- Build system requirements
- Compatibility constraints (Jakarta EE 10 requires Java 11+, works with Java 21)
- Scope of refactoring (125+ imports across 28 files need coordinated migration)

### 5. Large-Scale Refactoring

Bob handles complex transformations:
- Automated namespace migration across entire codebase
- Coordinated updates to build files, deployment descriptors, and source code
- Maintains consistency across all affected files
- Validates each layer of changes independently

---

## Benefits of Agentic Modernization

### Traditional Approach
- Manual analysis of build files and dependencies (hours)
- Manual updates to configurations (hours)
- Manual namespace migration across 28 files (hours to days)
- Manual testing and debugging (hours to days)
- Risk of missing deprecated configurations
- Risk of inconsistent namespace migrations
- Risk of breaking imports or references
- **Total: Days to Weeks**

### Agentic Approach with Bob
- Automated analysis (seconds)
- Autonomous configuration updates with validation (minutes)
- Automated namespace migration across entire codebase (minutes)
- Continuous automated build testing (included)
- Complete coverage of all required changes
- **Total: Minutes**

### Quality Improvements
- **Consistency**: All changes follow best practices (Gradle, Jakarta EE)
- **Completeness**: No missed deprecated configurations or namespace migrations
- **Validation**: Every change is build-tested at each phase
- **Accuracy**: 100% correct namespace migrations (125+ imports)
- **Speed**: 10-100x faster than manual approach
- **Reproducibility**: Process can be repeated across projects
- **Scalability**: Handles large-scale refactoring efficiently

---


## Expected Outcomes

By the end of this lab, you should have:

1. **Modernized Application**
   - Java 21 compatibility (upgraded from Java 8)
   - Jakarta EE 10 (migrated from Java EE 7)
   - Gradle 8.5 (upgraded from 6.9.4)
   - Modern Gradle dependency configurations
   - Jersey 3.1.3 (upgraded from 2.27)
   - Successfully building WAR file

2. **Validated Functionality**
   - Application builds successfully with Java 21 and Jakarta EE 10
   - No compilation errors
   - WAR file generated correctly
   - Build configuration modernized
   - All namespace migrations completed (javax.* → jakarta.*)

3. **Understanding of Agentic Workflows**
   - How Bob analyzes legacy applications
   - How Bob plans and executes upgrade tasks
   - How Bob validates changes iteratively
   - How Bob handles build configurations and dependency migrations
   - How Bob performs large-scale namespace refactoring
   - When to use agentic vs manual approaches

4. **Practical Experience**
   - Upgrading Java versions in legacy applications
   - Migrating from Java EE to Jakarta EE
   - Modernizing Gradle build configurations
   - Performing namespace migrations across multiple files
   - Validating Java upgrades through builds
   - Understanding compatibility requirements

---

## Next Steps

After completing this lab:

**Proceed to Part 3**: Security Review and Remediation
- Apply security fixes to the modernized codebase
- Ensure compliance with security standards

---

## Summary

This lab demonstrates IBM Bob's agentic modernization capabilities for comprehensive Java application upgrades:

**Key Takeaways:**
- Agentic workflows enable autonomous modernization across multiple dimensions
- Bob handles build configuration updates, dependency migrations, and namespace refactoring iteratively
- Continuous validation ensures quality through builds at each step
- Bob identifies and resolves compatibility issues automatically
- Bob performs large-scale code transformations (125+ import statements across 28 files)
- Developer oversight is maintained throughout the process

**Modernization Achieved:**
- ✅ Java 8 → Java 21 LTS upgrade
- ✅ Java EE 7 → Jakarta EE 10 migration
- ✅ Gradle 6.9.4 → Gradle 8.5 upgrade
- ✅ Jersey 2.27 → Jersey 3.1.3 upgrade
- ✅ Deprecated Gradle configurations modernized
- ✅ Deprecated Java API usage fixed (reflection API)
- ✅ Web deployment descriptor migrated to Jakarta EE 6.0
- ✅ 125+ namespace imports migrated (javax.* → jakarta.*)
- ✅ 28 Java source files updated
- ✅ Successful build validation with Java 21 and Jakarta EE 10
- ✅ WAR file generation confirmed

**Time & Efficiency:**
- **Traditional Approach**: Days to weeks of manual analysis, configuration updates, namespace refactoring, and testing
- **Bob's Agentic Approach**: Completed in minutes with automated validation
- **Token Usage**: ~16,500 tokens (~$0.70 API cost for complete modernization)

**Migration Scope:**
- **Phase 1 (Java 21)**: ~8,500 tokens, $0.31
- **Phase 2 (Jakarta EE 10)**: ~8,000 tokens, $0.39

The agentic approach dramatically reduces modernization time while improving quality and consistency. Bob's autonomous capabilities, combined with developer oversight, provide the ideal balance of automation and control for enterprise Java modernization projects. The ability to handle both infrastructure upgrades (Java, Gradle) and application-level migrations (Jakarta EE namespaces) in a single workflow demonstrates the power of agentic modernization.