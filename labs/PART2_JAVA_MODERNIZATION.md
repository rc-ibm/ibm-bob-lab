# IBM Bob Workshop - Java Application Modernization | Part 2
## Case Study: AltoroJ Banking Application Upgrade (Java 1.7 → Java 21)

### Audience
Software engineers, DevOps engineers, platform architects, and modernization specialists

### Goal of the Workshop
Demonstrate how **IBM Bob** can:
- Autonomously modernize Java applications using agentic workflows
- Analyze legacy codebases and create modernization plans
- Execute iterative modernization tasks with validation
- Update build configurations, dependencies, and runtime environments
- Validate modernization through automated testing

We will use **IBM Bob's agentic modernization workflow** to upgrade the AltoroJ Banking Application from Java 1.7 to Java 21, leveraging Bob's autonomous capabilities to handle complex refactoring tasks.

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

---

## Phase 1: Analyze

In this phase, Bob analyzes the AltoroJ application to understand its current state and identify modernization requirements.

### What Bob Does:
- Examines build.gradle to identify current Java version (1.7)
- Checks Gradle wrapper version compatibility
- Analyzes dependencies for deprecated configurations
- Identifies required changes for Java 21 compatibility

### Key Findings:
- **Current Java Version**: 1.7 (very outdated)
- **Target Java Version**: 21 (LTS)
- **Gradle Version**: 6.9.4 (needs upgrade for Java 21 support)
- **Deprecated Configurations**: `compile`, `providedCompile`, `archiveName`

---

## Phase 2: Upgrade

Bob autonomously performs the modernization in an iterative manner, validating each change.

### Step 1: Update Java Compatibility

**What Bob Does:**
- Updates `sourceCompatibility` from "1.7" to "21"
- Updates `targetCompatibility` from "1.7" to "21"
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

---

## Phase 3: Validate

Bob validates the modernization by building the application.

### Build Validation

**What Bob Does:**
- Executes the Gradle build command
- Monitors build output for errors
- Confirms successful compilation
- Verifies WAR file generation

### Build Results:
✅ **BUILD SUCCESSFUL**
- All Java source files compiled with Java 21
- WAR file `altoromutual.war` generated successfully
- No compilation errors
- One deprecation warning in DBUtil.java (acceptable for legacy code)

### Summary of Validation:
- **Exit Code**: 0 (Success)
- **Compilation**: Clean, no errors
- **WAR Generation**: Successful
- **Build Time**: ~5 seconds

---

## Key Modernization Changes

### 1. Gradle Configuration

**Before (Java 1.7):**
```gradle
sourceCompatibility = "1.7"
targetCompatibility = "1.7"

dependencies {
    compile <>
    providedCompile 'javax:javaee-api:7.0'
}

war {
    archiveName = 'altoromutual.war'
}
```

**After (Java 21):**
```gradle
sourceCompatibility = "21"
targetCompatibility = "21"

dependencies {
    implementation <>
    compileOnly 'javax:javaee-api:7.0'
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

### 3. Build Configuration Modernization

| Deprecated | Modern Replacement |
|------------|-------------------|
| `compile` | `implementation` |
| `providedCompile` | `compileOnly` |
| `archiveName` | `archiveFileName` |

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
- Dependencies between changes (Gradle version must support Java 21)
- Impact of modifications (deprecated configurations need updates)
- Build system requirements
- Compatibility constraints

---

## Benefits of Agentic Modernization

### Traditional Approach
- Manual analysis of build files and dependencies (hours)
- Manual updates to configurations (hours)
- Manual testing and debugging (hours to days)
- Risk of missing deprecated configurations
- **Total: Days**

### Agentic Approach with Bob
- Automated analysis (seconds)
- Autonomous configuration updates with validation (minutes)
- Continuous automated build testing (included)
- Complete coverage of all required changes
- **Total: Minutes**

### Quality Improvements
- **Consistency**: All changes follow Gradle best practices
- **Completeness**: No missed deprecated configurations
- **Validation**: Every change is build-tested
- **Speed**: 10-100x faster than manual approach
- **Reproducibility**: Process can be repeated across projects

---


## Expected Outcomes

By the end of this lab, you should have:

1. **Modernized Application**
   - Java 21 compatibility (upgraded from Java 1.7)
   - Gradle 8.5 (upgraded from 6.9.4)
   - Modern Gradle dependency configurations
   - Successfully building WAR file

2. **Validated Functionality**
   - Application builds successfully with Java 21
   - No compilation errors
   - WAR file generated correctly
   - Build configuration modernized

3. **Understanding of Agentic Workflows**
   - How Bob analyzes legacy applications
   - How Bob plans and executes upgrade tasks
   - How Bob validates changes iteratively
   - How Bob handles build configurations
   - When to use agentic vs manual approaches

4. **Practical Experience**
   - Upgrading Java versions in legacy applications
   - Modernizing Gradle build configurations
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

This lab demonstrates IBM Bob's agentic modernization capabilities for Java version upgrades:

**Key Takeaways:**
- Agentic workflows enable autonomous modernization
- Bob handles build configuration updates iteratively
- Continuous validation ensures quality through builds
- Bob identifies and resolves compatibility issues
- Developer oversight is maintained throughout

**Modernization Achieved:**
- ✅ Java 1.7 → Java 21 LTS upgrade
- ✅ Gradle 6.9.4 → Gradle 8.5 upgrade
- ✅ Deprecated Gradle configurations modernized
- ✅ Successful build validation with Java 21
- ✅ WAR file generation confirmed

**Time & Efficiency:**
- **Traditional Approach**: Days of manual analysis, configuration updates, and testing
- **Bob's Agentic Approach**: Completed in minutes with automated validation
- **Token Usage**: ~8,500 tokens (~$0.31 API cost)

The agentic approach dramatically reduces modernization time while improving quality and consistency. Bob's autonomous capabilities, combined with developer oversight, provide the ideal balance of automation and control for enterprise Java modernization projects.