# IBM Bob Workshop - Security Issue Discovery and Remediation | Part 3
## Case Study: AltoroJ Banking Application Security Audit (Java)

### Audience
Security engineers, DevSecOps professionals, and developers responsible for secure coding practices

### Goal of the Workshop
Demonstrate how **IBM Bob** can:
- Discover security vulnerabilities in Java banking applications
- Categorize issues by severity and type (OWASP, CWE)
- Generate comprehensive security audit reports
- Propose secure code fixes aligned with compliance standards
- Document remediation strategies for regulatory compliance (PCI-DSS, SOC2)

We will audit the **AltoroJ Banking Application**, which contains intentional security vulnerabilities for training purposes.

### Bob IDE Feature
> **Required Feature:** `/review` slash command
>
> You will use Bob's built-in **`/review` slash command** to perform comprehensive security analysis of JSP and Java files. This command analyzes code and identifies security vulnerabilities, bugs, performance issues, and style inconsistencies.

---

## Workshop Flow Overview

1. Use the `/review` slash command to analyze JSP and Java files for security vulnerabilities
2. Review security findings in the Bob Findings panel
3. Categorize and document each vulnerability with OWASP/CWE mappings
4. Generate a comprehensive security audit report
5. Apply secure code fixes following Java security best practices
6. Verify remediation effectiveness and compliance alignment

Each step builds on the previous one and mirrors how security audits are conducted in real enterprise banking environments.

---

## Step 1 - Review Code for Security Vulnerabilities

### Why this step?
Bob's `/review` slash command provides comprehensive code analysis that identifies security vulnerabilities, bugs, performance issues, and style inconsistencies. It can review:
- **Local uncommitted changes** in your working directory
- **Branch comparisons** against your current branch (HEAD)
- **Changes against GitHub issues** to validate if local changes address issue requirements

For this workshop, since we've already committed changes, we'll specifically target JSP and Java files for security review. The findings will automatically appear in the **Bob Findings panel**, making it easy to track and address each issue.

### Understanding the `/review` Command

The `/review` command can be used in multiple ways:

1. **Review local uncommitted changes:**
   ```
   /review
   ```
   Analyzes all uncommitted changes in your working directory.

2. **Compare branches:**
   ```
   /review <branch-name>
   ```
   Compares the specified branch against your current branch (HEAD).

3. **Validate against GitHub issue:**
   ```
   /review #<issue-number> --issue-coverage
   ```
   or
   ```
   /review <issue-url> --issue-coverage
   ```
   Validates if local changes align with and address the GitHub issue requirements.

For more details, see the [Bob documentation on /review](https://bob.ibm.com/docs/ide/features/slash-commands#review).

### Instructions

Since we've already committed our changes and want to review specific files for security issues, we'll use the `/review` command with a targeted approach:

1. **Open Bob Chat**
   - Click on the Bob icon in the VS Code sidebar
   - Ensure you're in a mode that supports code review (Code, Advanced, or Ask mode)

2. **Run the Security Review**
   
   Type the following command in Bob chat:
   ```
   /review @/src and @/WebContent for security vulnerabilities
   ```

   This will instruct Bob to:
   - Analyze all JSP files in the `WebContent/` directory
   - Analyze all Java servlet and utility files in the `src/` directory
   - Focus specifically on security vulnerabilities including:
     - SQL injection vulnerabilities
     - Cross-site scripting (XSS)
     - Authentication and authorization issues
     - Hardcoded credentials
     - Command injection
     - Insecure session management
     - Input validation issues

3. **Review Findings in Bob Findings Panel**
   
   After the review completes, Bob will automatically populate the **Bob Findings panel** with discovered issues:
   - Click on the "Bob Findings" icon in the VS Code sidebar (or use the command palette: "Bob: Show Findings")
   - Each finding will include:
     - **Severity level** (Critical, High, Medium, Low)
     - **Category** (Security, Performance, Maintainability, etc.)
     - **File location** with line numbers
     - **Description** of the issue
     - **Suggested remediation** (when applicable)
   
4. **Navigate to Issues**
   - Click on any finding in the panel to jump directly to the problematic code
   - Review the context and understand the security implication
   - Use the suggestions to plan your remediation strategy

### What Bob Analyzes

The `/review` command performs comprehensive analysis including:

- **Bug Detection:** Logic errors, null pointer issues, resource leaks
- **Security Checks:** OWASP Top 10 vulnerabilities, injection flaws, authentication issues
- **Performance Issues:** Inefficient algorithms, memory leaks, blocking operations
- **Style Consistency:** Code formatting, naming conventions, best practices
- **Compliance:** Checks against OSCAL policies defined in the `im8/` directory

### Expected Findings

The Bob Findings panel should reveal vulnerabilities including:

**LoginAPI.java:**
- Password converted to lowercase
- Passwork logging in plain text SQL injection in authentication (CWE-89)
- Weak authentication token generation

**DBUtil.java:**
- SQL injection
- Hardcoded database credentials

**disclaimer.htm:**
- Unvalidated redirect 

---

## Step 2 - Generate Security Audit Report

### Why this step?
A formal security audit report is essential for:
- Regulatory compliance (PCI-DSS, SOC2, ISO 27001, GDPR)
- Risk management and prioritization
- Developer guidance and training
- Executive communication
- Audit trail documentation for financial institutions

### Prompt
```
Generate a comprehensive SECURITY_AUDIT_REPORT.md document that includes:

1. **Executive Summary**
   - Overview of the AltoroJ application analyzed
   - Total vulnerabilities found by severity (Critical/High/Medium/Low)
   - Overall risk assessment for a banking application
   - Key recommendations for immediate action
   - Compliance implications (PCI-DSS, SOC2)

2. **Vulnerability Inventory**
   For each vulnerability:
   - Unique ID (e.g., VULN-001)
   - File, class, method, and line number
   - Vulnerability title
   - CWE ID and OWASP Top 10 2021 classification
   - Severity rating (Critical/High/Medium/Low)
   - CVSS v3.1 score estimate
   - Detailed technical description
   - Proof of concept / attack scenario with code examples
   - Business impact specific to banking operations
   - Affected components (web UI, REST API, or both)
   - Remediation recommendation with secure code example
   - Remediation effort estimate (hours/days)
   - Compliance requirements affected (PCI-DSS, SOC2, etc.)

3. **Risk Matrix**
   - Visual representation of vulnerabilities by severity and likelihood
   - Prioritization based on exploitability and business impact

4. **Compliance Gap Analysis**
   - PCI-DSS requirements affected by vulnerabilities
   - SOC2 trust service criteria impacted
   - OWASP ASVS verification requirements not met

5. **Remediation Roadmap**
   - Phase 1: Critical vulnerabilities (immediate action required)
   - Phase 2: High severity vulnerabilities (within 30 days)
   - Phase 3: Medium severity vulnerabilities (within 90 days)
   - Phase 4: Low severity and hardening (ongoing)
   - Dependencies between fixes

6. **Secure Coding Guidelines**
   - Java-specific security best practices
   - Servlet security patterns
   - JDBC security guidelines
   - Session management best practices
   - Input validation and output encoding standards

7. **Testing Recommendations**
   - SAST tool configuration
   - DAST testing scenarios
   - Manual penetration testing focus areas
   - Security regression testing requirements

Save this as SECURITY_AUDIT_REPORT.md in the current directory.
```

---

## Step 3 - Apply Security Fixes

### Why this step?
Demonstrating the ability to not only identify but also fix security issues is crucial for practical security engineering. Bob provides multiple ways to remediate vulnerabilities, from automated fixes to guided manual remediation.

### Option A: Use the "Fix with Bob" Button (Recommended for Quick Fixes)

The Bob Findings panel includes a **"Fix with Bob"** button for each identified vulnerability, allowing Bob to automatically generate and apply security fixes.

#### Instructions

1. **Open the Bob Findings Panel**
   - Click on the "Bob Findings" icon in the VS Code sidebar
   - Or use Command Palette: "Bob: Show Findings"

2. **Locate the SQL Injection Vulnerability**
   - Find the finding for [`DBUtil.java`](../src/com/ibm/security/appscan/altoromutual/util/DBUtil.java)
   - The finding should be categorized as:
     - **Category:** Security
     - **Type:** SQL Injection (CWE-89)
     - **Severity:** Critical or High

3. **Click "Fix with Bob"**
   - Click the **"Fix with Bob"** button on the SQL injection finding
   - Bob will automatically provide the task instructions for the fix

4. **Review the Applied Fix**
   - Bob will show you the changes made
   - Review the diff to understand the security improvement
   - Verify that PreparedStatement is used instead of string concatenation
   - Ensure proper parameterization is implemented

### Option B: Create a Manual Task for Bob (For Custom Requirements)

If you need more control over the remediation or want to fix multiple issues together, you can create a custom task for Bob.

#### Instructions

1. **Open Bob Chat**
   - Click on the Bob icon in the VS Code sidebar
   - Ensure you're in Code or Advanced mode

2. **Write a Detailed Task Description**
   
   When writing your task for Bob, include these key elements:
   - **Specific file path** using the `@` mention syntax (e.g., `@/src/com/ibm/security/appscan/altoromutual/util/DBUtil.java`)
   - **Vulnerability type** with CWE classification (e.g., SQL Injection, CWE-89)
   - **Reference to Workspace Problems** using the `@problems` tag to help Bob understand the diagnostic context
   - **Desired fix approach** (e.g., use PreparedStatement)
   - **Additional requirements** (e.g., add comments, preserve functionality)

   **Example Task:**
   ```
   Fix the SQL injection vulnerability in @/src/com/ibm/security/appscan/altoromutual/util/DBUtil.java identified in @problems.
   
   Replace all string concatenation in SQL queries with PreparedStatement to prevent SQL injection attacks. Specifically:
   
   1. Replace all string concatenation in SQL with PreparedStatement
   2. Use parameterized queries with ? placeholders
   3. Add detailed comments explaining the security improvement
   4. Ensure the fix doesn't break existing functionality
   5. Follow JDBC security best practices
   ```
   
   **Key Tips for Writing Effective Tasks:**
   - Use `@/path/to/file` to reference specific files - this helps Bob locate the exact file
   - Use `@problems` to reference the Workspace Problems panel - this gives Bob context about diagnostics
   - Mention the specific vulnerability type (e.g., "SQL Injection", "CWE-89") for clarity
   - Be explicit about the desired outcome (e.g., "use PreparedStatement instead of string concatenation")
   - Request documentation (e.g., "add comments explaining the security improvement")

3. **Submit the Task**
   - Press Enter to submit your task to Bob
   - Bob will analyze the file and vulnerability
   - Bob will generate and apply the security fix

4. **Review Bob's Response**
   - Bob will show you the changes made
   - Review the implementation details
   - Ask follow-up questions if needed

5. **Verify the Fix**
   - Check that all SQL queries use PreparedStatement
   - Ensure proper parameterization is implemented
   - Test the application functionality

---

## Expected Outcomes

By the end of this lab, you should have:

1. **SECURITY_AUDIT_REPORT.md** containing:
   - Complete vulnerability inventory with OWASP/CWE mappings
   - Risk assessment and prioritization
   - Compliance gap analysis (PCI-DSS, SOC2)
   - Remediation roadmap with effort estimates
   - Secure coding guidelines for Java/Servlets

2. **Fixed SQL Injection** vulnerabilities in `DBUtil.java`

3. Understanding of how IBM Bob can:
   - Identify security vulnerabilities in Java banking applications
   - Categorize issues using industry standards (OWASP, CWE)
   - Generate compliance-ready documentation
   - Apply secure coding fixes following best practices
   - Verify remediation effectiveness
   - Map fixes to compliance requirements

---

## Next Steps

After completing this lab:

Proceed to **Part 4** to learn how to deploy the secured application to IBM Cloud with Terraform through MCP.

---

## Additional Resources

- OWASP Top 10: https://owasp.org/Top10/
- OWASP ASVS: https://owasp.org/www-project-application-security-verification-standard/
- CWE Database: https://cwe.mitre.org/
- OWASP Java Security Cheat Sheet: https://cheatsheetseries.owasp.org/cheatsheets/Java_Security_Cheat_Sheet.html
- PCI-DSS Requirements: https://www.pcisecuritystandards.org/
- NIST Secure Software Development Framework: https://csrc.nist.gov/Projects/ssdf
- OWASP Java Encoder: https://owasp.org/www-project-java-encoder/