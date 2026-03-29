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

#### Enhanced Prompt (use Bob's magic star - to the left of the Send button - to generate something similar):
```
Analyze the provided software and deliver a comprehensive explanation of its functionality, purpose, and operational behavior. Detail what problems it solves, how it processes data, what its key features are, and how different components interact during execution. Then create a detailed Mermaid diagram illustrating the software architecture, including all major components, modules, classes, services, data flows, dependencies, and their relationships. The diagram should clearly show the system's structure, layer separation if applicable, external integrations, data storage mechanisms, and communication patterns between components. Ensure the Mermaid syntax is correct and the diagram is sufficiently detailed to understand the complete architectural design at both high-level and component-level perspectives.
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
3. **Component Descriptions**: Detailed explanation of each module
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

Save this as DATA_PIPELINE_ANALYSIS.md in the current directory
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
   - System invariants and assumptions
   - Database initialization and configuration details

2. A detailed **DATA_PIPELINE_ANALYSIS.md** document containing:
   - Request lifecycle for web UI and REST API paths
   - Transaction state management and flow
   - Session management and authentication flow
   - Error handling at each pipeline stage
   - Data consistency guarantees
   - Audit trail and compliance logging
   - Database initialization process
   - Mermaid flowcharts for complete data pipelines

3. A comprehensive **FUNCTIONAL_REQUIREMENTS.md** document containing:
   - User operations (web UI and API capabilities)
   - Administrative operations and privileged functions
   - Business rules (validation, constraints, transaction requirements)
   - Security requirements (authentication, authorization, session management)
   - Clear acceptance criteria for each requirement

4. A deep understanding of how IBM Bob can:
   - Analyze complex Java-based financial codebases
   - Understand servlet-based web architectures
   - Document REST API implementations
   - Generate professional documentation
   - Create accurate technical diagrams
   - Extract business requirements from code
   - Identify critical system invariants

---

---

## Troubleshooting

### Mermaid Diagram Issues

If any Mermaid diagrams generated by Bob fail to render correctly or show syntax errors:

1. **Select the problematic Mermaid diagram text** in the editor (the code block between the ` ```mermaid ` markers)
2. **Add the selection to chat** using the "Chat" button, context menu ("Add To Context"), or keyboard shortcut (`ctrl/cmd+L`)
3. **Ask Bob to fix it** with a prompt like:
   ```
   This Mermaid diagram has a syntax error. Can you fix it?
   ```
   or
   ```
   The diagram is not rendering correctly. Please correct the syntax.
   ```

Bob will analyze the diagram syntax and correct any issues such as:
- Invalid node identifiers or special characters
- Incorrect arrow syntax
- Missing quotes around labels with spaces
- Improper nesting or indentation
- Unsupported Mermaid features

**Tip**: You can also select and add specific diagrams to chat to ask Bob to regenerate them with different styles or levels of detail.

## Next Steps

After completing this lab:

**Proceed to Part 2**: [Java Modernization](./PART2_JAVA_MODERNIZATION.md)
- Modernize the codebase from Java version 8 to 21 through an agentic workflow