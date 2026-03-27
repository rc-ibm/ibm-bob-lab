# IBM Bob Workshop - Core Banking Architecture & Code Understanding | Part 1
## Case Study: AltoroMutual Online Banking System (Java / Servlets / REST API)

### Audience
Software engineers, solution architects, and developers

### Goal of the Workshop
Demonstrate how **IBM Bob** can:
- Understand complex banking system codebases
- Analyze API architecture and data flows
- Generate comprehensive engineering artifacts (diagrams, documentation)
- Reason about transaction logic and data integrity
- Produce architecture documentation suitable for stakeholders

We use the **AltoroMutual Online Banking System**, a realistic banking demo application with a Java Servlet backend, REST API, and web-based interface.

### Bob IDE Mode
> **Required Mode:** `Ask`
>
> Ensure you are in **Ask Mode** before starting this lab. This mode provides optimal support for code analysis and documentation generation.

---

## Workshop Flow Overview

1. Understand the codebase structure
2. Generate architecture documentation with Mermaid diagrams
3. Document functional requirements
4. Analyze data flow and transaction pipeline
5. Identify system invariants and assumptions
6. Create comprehensive ARCHITECTURE.md document

Each step builds on the previous one and mirrors how banking software is documented in real enterprise projects.

---

## Step 1 - Understand the Code

### Why this step?
Before documenting, extending, or auditing financial software, engineers must **fully understand how it works**:
- Servlet-based web architecture
- REST API endpoints and authentication
- Transaction processing logic
- Role-based access control (user vs admin)
- Data integrity mechanisms
- Business rules and constraints

This step shows that IBM Bob can perform a **deep technical read** of banking software, not just summarize files.

### Prompt
```
Can you describe what this software is doing? In addition, can you show me the software architecture - a Mermaid diagram will be a good start.
```

#### Enhanced Prompt (use Bob's magic star to generate something similar):
```
Analyze the AltoroMutual Online Banking codebase and provide a comprehensive explanation of its functionality, including:

1. **Application Architecture**: How the Java Servlet-based backend handles web requests, session management, and the dual interface (web UI + REST API)
2. **Authentication & Authorization**: How LoginServlet and the authentication filters (AuthFilter, AdminFilter, ApiAuthFilter) work together to secure the application
3. **Role-Based Access Control**: How regular users and administrators differ in their permissions and capabilities
4. **Transaction Processing**: The transfer logic in TransferServlet and TransferAPI including balance verification, transaction recording, and the debit/credit pattern
5. **Data Model**: The relationship between User, Account, Transaction, and Feedback entities, and how DBUtil manages database operations
6. **REST API**: How the JAX-RS API (AltoroAPI) exposes banking operations and differs from the servlet-based web interface
7. **Database Initialization**: How StartupListener initializes the Derby database and the app.properties configuration system

Additionally, create detailed Mermaid diagrams showing:
- System architecture with all components (servlets, filters, API, database)
- Request flow for both web UI and REST API
- Authentication and authorization flow
- Transaction processing sequence
- Data model relationships (ER diagram)

Clearly state key invariants, business rules, and assumptions made by the code.
```

---

## Step 2 - Generate Architecture Document

### Why this step?
Enterprise systems require formal architecture documentation for:
- Regulatory compliance and audits
- Onboarding new team members
- System integration planning
- Change management processes

### Prompt
```
Based on your analysis, create a comprehensive ARCHITECTURE.md document that includes:

1. **Executive Summary**: One-paragraph overview of the AltoroMutual system
2. **System Architecture**: High-level Mermaid diagram showing all components (web layer, API layer, business logic, data layer)
3. **Component Descriptions**: Detailed explanation of each module:
   - Servlet layer (LoginServlet, AccountViewServlet, TransferServlet, AdminServlet, FeedbackServlet)
   - REST API layer (LoginAPI, AccountAPI, TransferAPI, AdminAPI, FeedbackAPI)
   - Security filters (AuthFilter, AdminFilter, ApiAuthFilter)
   - Data models (User, Account, Transaction, Feedback)
   - Utilities (DBUtil, ServletUtil, OperationsUtil)
4. **API Reference**: All REST endpoints with their purposes, parameters, and access controls
5. **Servlet Reference**: All servlet endpoints with their purposes and access controls
6. **Data Model**: Entity-relationship diagram using Mermaid showing User, Account, Transaction, and Feedback relationships
7. **Transaction Flow**: Sequence diagram for key operations (login, transfer, balance inquiry, admin operations)
8. **Security Model**: 
   - Session-based authentication for web UI
   - Token-based authentication for REST API
   - Authorization filters and access control
   - Admin privilege escalation
9. **Functional Requirements**: List of supported business operations for both users and administrators
10. **Database Architecture**: Derby database structure, initialization process, and the app.properties configuration system
11. **Non-Functional Requirements**: Performance, reliability, and scalability considerations
12. **Assumptions and Constraints**: Technical and business limitations

Save this as ARCHITECTURE.md in the current directory.
```

---

## Step 3 - Data Pipeline Analysis

### Why this step?
Understanding how data flows through a system is critical for:
- Performance optimization
- Debugging transaction issues
- Planning system extensions
- Ensuring audit compliance

### Prompt
```
Create a detailed data pipeline analysis that covers:

1. **Request Lifecycle**: 
   - Web UI: From HTTP request through servlet filters to servlet processing to database commit
   - REST API: From HTTP request through API filters to JAX-RS resource to database commit
2. **Transaction States**: How transfers move through validation, execution, and confirmation in TransferServlet and TransferAPI
3. **Session Management**: How user sessions are created, maintained, and validated across requests
4. **Error Handling**: What happens when transactions fail at each stage (authentication, authorization, validation, execution)
5. **Consistency Guarantees**: How the system ensures data integrity during transfers
6. **Audit Trail**: What information is logged and stored in the Transaction table for compliance
7. **Database Initialization**: How StartupListener initializes the Derby database on first startup

Include Mermaid flowcharts showing:
- Complete data pipeline from client request to persisted transaction (web UI path)
- Complete data pipeline from API request to persisted transaction (REST API path)
- Session lifecycle and authentication flow
- Transaction processing with error handling branches
```

---

## Step 4 - Functional Requirements Extraction

### Why this step?
Documenting functional requirements helps stakeholders understand system capabilities without reading code.

### Prompt
```
Extract and document all functional requirements from the codebase:

1. **User Operations** (via web UI and API):
   - What can a regular user do?
   - What information can they access?
   - What are the limitations?
   - Which servlets and API endpoints support these operations?

2. **Administrative Operations**:
   - What administrative functions are available in AdminServlet and AdminAPI?
   - What privileged operations require admin access?
   - How is admin access granted and verified?

3. **Business Rules**:
   - Account balance validation rules
   - Transfer validation rules (minimum/maximum amounts, account verification)
   - Transaction recording requirements
   - Feedback submission and review process

4. **Security Requirements**:
   - Authentication requirements for web UI vs API
   - Session management rules
   - Password handling (note: analyze LoginServlet for password processing)
   - Admin privilege requirements

Format this as a structured requirements document with clear acceptance criteria for each requirement.
```

---

## Step 5 - System Invariants and Assumptions

### Why this step?
Identifying invariants helps prevent bugs and guides future development.

### Prompt
```
Identify and document all system invariants and assumptions:

1. **Data Invariants**: Rules that must always hold true in the database
   - Account balance integrity
   - Transaction record completeness
   - User-account relationships

2. **Business Invariants**: Financial rules that cannot be violated
   - Transfer amount validation
   - Account ownership verification
   - Transaction atomicity

3. **Security Invariants**: Access control rules that must be enforced
   - Authentication requirements
   - Session validation
   - Admin privilege checks
   - API token validation

4. **Technical Assumptions**: What the code assumes about its environment
   - Derby database location and initialization (check StartupListener and AGENTS.md)
   - Session storage and timeout
   - app.properties configuration
   - Servlet container capabilities

For each invariant, explain:
- What the invariant is
- Where in the code it's enforced (specific classes and methods)
- What could go wrong if violated
- Any security implications
```

---

## Expected Outcome

By the end of this lab, you should have:

1. A complete **ARCHITECTURE.md** document containing:
   - System overview with Mermaid architecture diagram
   - Component descriptions (servlets, API, filters, models, utilities)
   - Servlet and API reference with all endpoints
   - Data model with ER diagram
   - Transaction flow sequence diagrams
   - Security model documentation (web + API authentication)
   - Functional requirements for users and administrators
   - Data pipeline analysis (web UI and REST API paths)
   - System invariants and assumptions
   - Database initialization and configuration details

2. A deep understanding of how IBM Bob can:
   - Analyze complex Java-based financial codebases
   - Understand servlet-based web architectures
   - Document REST API implementations
   - Generate professional documentation
   - Create accurate technical diagrams
   - Extract business requirements from code
   - Identify critical system invariants

---

## Next Steps

After completing this lab:

**Proceed to Part 2**: [Java Modernization](./PART2_JAVA_MODERNIZATION.md)
- Modernize the codebase from Java version 1.7 to 21 through an agentic workflow