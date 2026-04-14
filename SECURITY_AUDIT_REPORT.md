# AltoroJ Security Audit Report

**Report Date:** March 29, 2026  
**Application:** AltoroJ - Online Banking Demo Application  
**Version:** Current (as of audit date)  
**Auditor:** Security Review Team  
**Classification:** CONFIDENTIAL

---

## Executive Summary

### Application Overview

AltoroJ is a Java-based web application designed to demonstrate common web application vulnerabilities in a banking context. The application provides both a traditional web interface (JSP/Servlets) and a REST API for account management, fund transfers, and user authentication.

### Vulnerability Summary

| Severity | Count | Percentage |
|----------|-------|------------|
| **Critical** | 10 | 38.5% |
| **High** | 6 | 23.1% |
| **Medium** | 7 | 26.9% |
| **Low** | 3 | 11.5% |
| **Total** | 26 | 100% |

### Overall Risk Assessment

**RISK LEVEL: CRITICAL** ⚠️

The AltoroJ application contains **10 critical vulnerabilities** that pose immediate and severe risks to:
- Customer financial data confidentiality and integrity
- Authentication and authorization mechanisms
- System availability and operational continuity
- Regulatory compliance (PCI-DSS, SOC2, GDPR)

**Key Findings:**
- 7 SQL Injection vulnerabilities allowing complete database compromise
- 1 Command Injection vulnerability enabling remote code execution
- Multiple Cross-Site Scripting (XSS) vulnerabilities
- Weak authentication mechanisms with credential exposure
- Sensitive data logging in plaintext

### Immediate Action Required

1. **STOP PRODUCTION DEPLOYMENT** - This application must not be deployed in any production or customer-facing environment
2. **Implement SQL Injection fixes** within 24-48 hours (all database operations)
3. **Remove plaintext password logging** immediately
4. **Disable command execution features** (advancedStaticPageProcessing)
5. **Implement comprehensive input validation** across all user inputs

### Compliance Implications

| Standard | Status | Critical Gaps |
|----------|--------|---------------|
| **PCI-DSS v4.0** | ❌ NON-COMPLIANT | Req 6.2.4 (SQL Injection), 6.5.3 (Crypto), 10.2.2 (Logging) |
| **SOC2 Type II** | ❌ NON-COMPLIANT | CC6.1 (Logical Access), CC7.2 (System Monitoring) |
| **OWASP ASVS v4.0** | ❌ LEVEL 1 FAIL | V5 (Validation), V2 (Authentication), V8 (Data Protection) |
| **GDPR** | ⚠️ AT RISK | Art 32 (Security), Art 25 (Privacy by Design) |

---

## Vulnerability Inventory

### Critical Severity Vulnerabilities

#### VULN-001: SQL Injection in User Authentication

**Classification:**
- **CWE:** CWE-89 (SQL Injection)
- **OWASP Top 10 2021:** A03:2021 - Injection
- **Severity:** CRITICAL
- **CVSS v3.1 Score:** 9.8 (Critical)
  - Vector: CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H

**Location:**
- **File:** `src/com/ibm/security/appscan/altoromutual/util/DBUtil.java`
- **Class:** `DBUtil`
- **Method:** `isValidUser(String user, String password)`
- **Line:** 207

**Technical Description:**

The authentication method constructs SQL queries using string concatenation with unsanitized user input, creating a classic SQL injection vulnerability.

**Vulnerable Code:**
```java
ResultSet resultSet = statement.executeQuery(
    "SELECT COUNT(*) FROM PEOPLE WHERE USER_ID = '" + user + 
    "' AND PASSWORD='" + password + "'"
);
```

**Proof of Concept:**

An attacker can bypass authentication using SQL injection:

```
Username: admin'--
Password: anything
```

This produces the query:
```sql
SELECT COUNT(*) FROM PEOPLE WHERE USER_ID = 'admin'--' AND PASSWORD='anything'
```

The `--` comments out the password check, allowing authentication with any password.

**Attack Scenarios:**

1. **Authentication Bypass:** Gain admin access without valid credentials
2. **Data Exfiltration:** Use UNION-based injection to extract all user data
3. **Database Manipulation:** Insert, update, or delete records
4. **Privilege Escalation:** Modify user roles to gain admin privileges

**Business Impact:**

- Complete compromise of customer account data
- Unauthorized fund transfers
- Regulatory fines (PCI-DSS violations: $5,000-$100,000/month)
- Reputational damage and customer loss
- Legal liability for data breaches

**Affected Components:**
- Web UI: Login page (`/login.jsp`)
- REST API: Login endpoint (`/api/login`)

**Remediation:**

Use PreparedStatement with parameterized queries:

```java
public static boolean isValidUser(String user, String password) throws SQLException {
    if (user == null || password == null || 
        user.trim().length() == 0 || password.trim().length() == 0)
        return false;
    
    Connection connection = getConnection();
    String query = "SELECT COUNT(*) FROM PEOPLE WHERE USER_ID = ? AND PASSWORD = ?";
    
    try (PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setString(1, user);
        statement.setString(2, password);
        
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) > 0;
        }
    }
    return false;
}
```

**Remediation Effort:** 2-4 hours  
**Priority:** P0 - Immediate  
**Compliance Requirements:** PCI-DSS 6.2.4, OWASP ASVS V5.3.4

---

#### VULN-002 through VULN-007: Additional SQL Injection Vulnerabilities

**Summary:** Six additional SQL injection vulnerabilities exist in:
- `getUserInfo()` - Line 230
- `getAccounts()` - Line 264
- `addAccount()` - Line 459
- `addUser()` - Line 482
- `changePassword()` - Line 494
- `storeFeedback()` - Line 507

All follow the same pattern of string concatenation in SQL queries. Each requires the same remediation approach using PreparedStatement.

**Combined Remediation Effort:** 8-12 hours  
**Priority:** P0 - Immediate

---

#### VULN-008: Sensitive Data Logging - Plaintext Passwords

**Classification:**
- **CWE:** CWE-532 (Insertion of Sensitive Information into Log File)
- **OWASP Top 10 2021:** A09:2021 - Security Logging and Monitoring Failures
- **Severity:** CRITICAL
- **CVSS v3.1 Score:** 8.2 (High)

**Location:**
- **File:** `src/com/ibm/security/appscan/altoromutual/servlet/LoginServlet.java`
- **Method:** `doPost()`
- **Line:** 71

**Vulnerable Code:**
```java
Log4AltoroJ.getInstance().logError(
    "Login failed >>> User: " + username + " >>> Password: " + password
);
```

**Business Impact:**

- PCI-DSS 3.4 violation (passwords must be unreadable)
- Credential exposure to internal threats
- Compliance audit failures

**Remediation:**

```java
Log4AltoroJ.getInstance().logError(
    "Login failed for user: " + username + " from IP: " + 
    request.getRemoteAddr() + " at " + new Date()
);
```

**Remediation Effort:** 30 minutes  
**Priority:** P0 - Immediate  
**Compliance Requirements:** PCI-DSS 3.4, 10.2.2

---

#### VULN-009: Command Injection Vulnerability

**Classification:**
- **CWE:** CWE-78 (OS Command Injection)
- **OWASP Top 10 2021:** A03:2021 - Injection
- **Severity:** CRITICAL
- **CVSS v3.1 Score:** 9.8 (Critical)

**Location:**
- **File:** `WebContent/index.jsp`
- **Lines:** 44-53

**Vulnerable Code:**
```java
if (ServletUtil.isAppPropertyTrue("advancedStaticPageProcessing")) {
    String command = "cat '" + path + "/" + content + "'";
    Process proc = Runtime.getRuntime().exec(new String[] {shell, shellarg, command});
}
```

**Proof of Concept:**
```
GET /index.jsp?content=test.htm';whoami;echo+' HTTP/1.1
```

**Business Impact:**

- Complete server compromise
- Estimated incident cost: $500K - $5M

**Remediation:**

Disable the feature or use File API with whitelist validation.

**Remediation Effort:** 4-6 hours  
**Priority:** P0 - Immediate

---

#### VULN-010: Weak Authentication Token Generation

**Classification:**
- **CWE:** CWE-330 (Use of Insufficiently Random Values)
- **OWASP Top 10 2021:** A02:2021 - Cryptographic Failures
- **Severity:** CRITICAL
- **CVSS v3.1 Score:** 8.1 (High)

**Location:**
- **File:** `src/com/ibm/security/appscan/altoromutual/util/OperationsUtil.java`
- **Method:** `makeRandomString()`
- **Lines:** 146-152

**Vulnerable Code:**
```java
byte[] array = new byte[7]; // Only 7 bytes!
new Random().nextBytes(array); // Non-cryptographic Random
```

**Remediation:**

```java
public static String makeRandomString() {
    SecureRandom secureRandom = new SecureRandom();
    byte[] array = new byte[32]; // 256 bits
    secureRandom.nextBytes(array);
    return Base64.getEncoder().encodeToString(array);
}
```

**Remediation Effort:** 1 hour  
**Priority:** P0 - Immediate

---

### High Severity Vulnerabilities

#### VULN-011: Open Redirect Vulnerability

**Classification:**
- **CWE:** CWE-601 (URL Redirection to Untrusted Site)
- **Severity:** HIGH
- **CVSS v3.1 Score:** 7.4

**Location:** `WebContent/disclaimer.htm:13-20`

**Remediation:** Validate URLs against whitelist  
**Effort:** 2 hours  
**Priority:** P1

---

#### VULN-012: XSS in Search

**Classification:**
- **CWE:** CWE-79 (Cross-site Scripting)
- **Severity:** HIGH
- **CVSS v3.1 Score:** 7.1

**Location:** `WebContent/search.jsp:32`

**Remediation:** Use HTML encoding  
**Effort:** 30 minutes  
**Priority:** P1

---

#### VULN-013: Stored XSS in Feedback

**Classification:**
- **CWE:** CWE-79
- **Severity:** HIGH
- **CVSS v3.1 Score:** 7.1

**Location:** `WebContent/feedbacksuccess.jsp:62-74`

**Remediation:** Encode all output fields  
**Effort:** 1 hour  
**Priority:** P1

---

#### VULN-014: XSS in Survey

**Classification:**
- **CWE:** CWE-79
- **Severity:** HIGH
- **CVSS v3.1 Score:** 6.5

**Location:** `src/com/ibm/security/appscan/altoromutual/servlet/SurveyServlet.java:69`

**Remediation:** HTML encode email parameter  
**Effort:** 30 minutes  
**Priority:** P1

---

#### VULN-015: Insecure Token Encoding

**Classification:**
- **CWE:** CWE-522 (Insufficiently Protected Credentials)
- **Severity:** HIGH
- **CVSS v3.1 Score:** 7.5

**Location:** `src/com/ibm/security/appscan/altoromutual/api/LoginAPI.java:73`

**Remediation:** Use JWT tokens with proper signing  
**Effort:** 4-6 hours  
**Priority:** P1

---

#### VULN-016: Hardcoded Admin Credentials

**Classification:**
- **CWE:** CWE-798
- **Severity:** HIGH
- **CVSS v3.1 Score:** 7.5

**Location:** `WebContent/login.jsp:21`

**Remediation:** Remove comment  
**Effort:** 5 minutes  
**Priority:** P1

---

### Medium Severity Vulnerabilities

**VULN-017:** Password Case Sensitivity Weakening (LoginServlet.java:68)  
**VULN-018:** DOM-Based XSS (high_yield_investments.htm:96-100)  
**VULN-019:** Path Traversal (feedback.jsp:38)  
**VULN-020:** Missing Null Check (OperationsUtil.java:136)  
**VULN-021:** No Transfer Validation (TransferServlet.java:48)  
**VULN-022:** Username Case Sensitivity (LoginServlet.java:65)  
**VULN-023:** API Password Lowercase (LoginAPI.java:52)

**Combined Remediation Effort:** 8-12 hours  
**Priority:** P2 - Medium (90 days)

---

### Low Severity Vulnerabilities

**VULN-024:** Magic Number - DB Error (DBUtil.java:114)  
**VULN-025:** Magic Number - DateTime Error (DBUtil.java:394)  
**VULN-026:** Magic Number - Property Limit (ServletUtil.java:263)

**Combined Remediation Effort:** 1 hour  
**Priority:** P4 - Low (Ongoing)

---

## Risk Matrix

### Vulnerability Distribution by Severity and Exploitability

| Severity | Easy to Exploit | Moderate | Difficult | Total |
|----------|----------------|----------|-----------|-------|
| **Critical** | 8 | 2 | 0 | 10 |
| **High** | 4 | 2 | 0 | 6 |
| **Medium** | 3 | 4 | 0 | 7 |
| **Low** | 0 | 0 | 3 | 3 |
| **Total** | 15 | 8 | 3 | 26 |

### Risk Heat Map

```
                    LIKELIHOOD
                Low    Medium    High
              ┌──────┬─────────┬──────┐
         High │      │         │  10  │ CRITICAL
              ├──────┼─────────┼──────┤
IMPACT  Medium│      │    7    │   6  │ HIGH
              ├──────┼─────────┼──────┤
          Low │   3  │         │      │ MEDIUM/LOW
              └──────┴─────────┴──────┘
```

### Priority Ranking

| Priority | Count | Timeframe | Vulnerabilities |
|----------|-------|-----------|-----------------|
| **P0 - Immediate** | 10 | 24-48 hours | VULN-001 through VULN-010 |
| **P1 - High** | 6 | 30 days | VULN-011 through VULN-016 |
| **P2 - Medium** | 7 | 90 days | VULN-017 through VULN-023 |
| **P3 - Low** | 3 | Ongoing | VULN-024 through VULN-026 |

---

## Compliance Gap Analysis

### PCI-DSS v4.0 Requirements

| Requirement | Status | Affected Vulnerabilities | Remediation Priority |
|-------------|--------|-------------------------|---------------------|
| **6.2.4** - Prevent common coding vulnerabilities | ❌ FAIL | VULN-001 to VULN-007, VULN-009, VULN-012 to VULN-014 | P0 |
| **6.5.3** - Protect stored cardholder data | ❌ FAIL | VULN-008, VULN-010, VULN-015 | P0 |
| **8.2.1** - Strong authentication | ❌ FAIL | VULN-010, VULN-015, VULN-017, VULN-023 | P0 |
| **10.2.2** - Audit trail for authentication | ❌ FAIL | VULN-008 | P0 |
| **11.3.1** - External penetration testing | ⚠️ REQUIRED | All Critical/High | Annual |
| **11.3.2** - Internal penetration testing | ⚠️ REQUIRED | All vulnerabilities | Annual |

**Compliance Status:** **NON-COMPLIANT**

**Estimated Remediation Cost:** $150,000 - $250,000
- Development: 400-600 hours @ $150-200/hour
- Security testing: 200-300 hours @ $150-200/hour
- Audit preparation: 100-150 hours @ $150-200/hour

**Timeline to Compliance:** 6-9 months with dedicated resources

---

### SOC2 Trust Service Criteria

| Criteria | Status | Gap Description | Remediation |
|----------|--------|-----------------|-------------|
| **CC6.1** - Logical and Physical Access Controls | ❌ FAIL | Authentication bypass (VULN-001), weak tokens (VULN-010) | Fix SQL injection, implement strong auth |
| **CC6.6** - Logical Access - Removal | ⚠️ PARTIAL | No session timeout visible, token expiration issues | Implement session management |
| **CC6.7** - Logical Access - Restriction | ❌ FAIL | Authorization bypass possible via SQL injection | Fix all injection vulnerabilities |
| **CC7.2** - System Monitoring | ❌ FAIL | Sensitive data in logs (VULN-008) | Remove PII/credentials from logs |
| **CC7.3** - System Operations - Malicious Software | ⚠️ AT RISK | Command injection (VULN-009) allows malware | Fix command injection |

**SOC2 Readiness:** **NOT READY** - Estimated 9-12 months to readiness

---

### OWASP ASVS v4.0 Verification Requirements

| Level | Category | Status | Failed Requirements |
|-------|----------|--------|-------------------|
| **Level 1** | V2: Authentication | ❌ FAIL | V2.2.1, V2.3.1, V2.7.1 |
| **Level 1** | V5: Validation | ❌ FAIL | V5.1.1, V5.2.1, V5.3.4 |
| **Level 1** | V8: Data Protection | ❌ FAIL | V8.2.2, V8.3.4 |
| **Level 2** | V2: Authentication | ❌ FAIL | V2.2.3, V2.8.1 |
| **Level 2** | V9: Communications | ⚠️ UNKNOWN | Requires HTTPS verification |

**ASVS Level Achievement:** **LEVEL 0** (Does not meet Level 1 requirements)

---

### GDPR Compliance Impact

| Article | Requirement | Risk Level | Impact |
|---------|-------------|------------|--------|
| **Art 32** - Security of Processing | ❌ HIGH RISK | Critical vulnerabilities expose personal data | Potential fines up to €20M or 4% revenue |
| **Art 25** - Data Protection by Design | ❌ HIGH RISK | No security controls by default | Demonstrates lack of privacy by design |
| **Art 33** - Breach Notification | ⚠️ TRIGGERED | Any exploitation requires 72-hour notification | Legal and reputational consequences |
| **Art 5(1)(f)** - Integrity and Confidentiality | ❌ HIGH RISK | Cannot ensure data integrity/confidentiality | Fundamental GDPR principle violated |

**GDPR Risk Assessment:** **SEVERE** - Immediate remediation required before processing EU data

---

## Remediation Roadmap

### Phase 1: Critical Vulnerabilities (Immediate - Week 1-2)

**Objective:** Eliminate critical security risks that allow system compromise

| Task | Vulnerabilities | Effort | Owner | Dependencies |
|------|----------------|--------|-------|--------------|
| Fix all SQL Injection | VULN-001 to VULN-007 | 16 hours | Backend Dev | Database team review |
| Remove password logging | VULN-008 | 1 hour | Backend Dev | None |
| Disable command execution | VULN-009 | 2 hours | Backend Dev | Config management |
| Implement secure tokens | VULN-010 | 8 hours | Backend Dev | Crypto library |

**Total Effort:** 27 hours (3-4 days with 1 developer)  
**Success Criteria:** All P0 vulnerabilities resolved, penetration test shows no critical findings

---

### Phase 2: High Severity Vulnerabilities (Week 3-6)

**Objective:** Address high-risk vulnerabilities affecting data confidentiality and integrity

| Task | Vulnerabilities | Effort | Owner | Dependencies |
|------|----------------|--------|-------|--------------|
| Fix XSS vulnerabilities | VULN-012, VULN-013, VULN-014 | 8 hours | Frontend Dev | Output encoding library |
| Implement JWT authentication | VULN-015 | 16 hours | Backend Dev | Phase 1 complete |
| Fix open redirect | VULN-011 | 4 hours | Frontend Dev | URL validation |
| Remove hardcoded credentials | VULN-016 | 1 hour | DevOps | None |

**Total Effort:** 29 hours (4-5 days)  
**Success Criteria:** No high-severity findings in security scan

---

### Phase 3: Medium Severity Vulnerabilities (Week 7-12)

**Objective:** Strengthen security posture and address remaining risks

| Task | Vulnerabilities | Effort | Owner | Dependencies |
|------|----------------|--------|-------|--------------|
| Fix password handling | VULN-017, VULN-023 | 4 hours | Backend Dev | User communication |
| Implement input validation | VULN-018, VULN-019, VULN-021 | 12 hours | Backend Dev | Validation framework |
| Add null checks | VULN-020, VULN-022 | 4 hours | Backend Dev | None |

**Total Effort:** 20 hours (2-3 days)  
**Success Criteria:** All medium-severity issues resolved

---

### Phase 4: Code Quality and Hardening (Week 13+)

**Objective:** Improve code maintainability and implement defense-in-depth

| Task | Vulnerabilities | Effort | Owner | Dependencies |
|------|----------------|--------|-------|--------------|
| Refactor magic numbers | VULN-024, VULN-025, VULN-026 | 2 hours | Backend Dev | None |
| Implement WAF rules | N/A | 16 hours | DevOps | WAF solution |
| Security headers | N/A | 4 hours | DevOps | None |
| Rate limiting | N/A | 8 hours | Backend Dev | None |
| CSRF protection | N/A | 8 hours | Backend Dev | Framework support |

**Total Effort:** 38 hours (5 days)  
**Success Criteria:** Security hardening complete, ready for audit

---

### Remediation Dependencies

```
Phase 1 (Critical)
    ├── SQL Injection Fixes (VULN-001 to VULN-007)
    │   └── Required for: Phase 2 JWT implementation
    ├── Password Logging (VULN-008)
    │   └── Required for: PCI-DSS compliance
    ├── Command Injection (VULN-009)
    │   └── Required for: Production deployment
    └── Secure Tokens (VULN-010)
        └── Required for: Phase 2 JWT implementation

Phase 2 (High)
    ├── XSS Fixes (VULN-012, VULN-013, VULN-014)
    │   └── Required for: Phase 3 input validation
    └── JWT Implementation (VULN-015)
        └── Depends on: Phase 1 complete

Phase 3 (Medium)
    └── Input Validation (All)
        └── Depends on: Phase 2 XSS fixes

Phase 4 (Hardening)
    └── All tasks
        └── Depends on: Phases 1-3 complete
```

---

## Secure Coding Guidelines

### Java Security Best Practices

#### 1. Input Validation

**Always validate and sanitize user input:**

```java
public class InputValidator {
    
    // Whitelist validation for alphanumeric
    public static boolean isValidUsername(String username) {
        if (username == null || username.length() < 3 || username.length() > 50) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9_-]+$");
    }
    
    // Email validation
    public static boolean isValidEmail(String email) {
        if (email == null || email.length() > 254) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
    
    // Numeric validation
    public static boolean isValidAmount(double amount) {
        return amount > 0 && amount <= 1000000 && !Double.isNaN(amount) && !Double.isInfinite(amount);
    }
}
```

#### 2. Output Encoding

**Always encode output to prevent XSS:**

```java
import org.apache.commons.text.StringEscapeUtils;

// In JSP
<%@ page import="org.apache.commons.text.StringEscapeUtils" %>
<%= StringEscapeUtils.escapeHtml4(userInput) %>

// In Java
String safeOutput = StringEscapeUtils.escapeHtml4(userInput);
response.getWriter().write(safeOutput);
```

#### 3. Parameterized Queries

**Always use PreparedStatement:**

```java
public User getUserByUsername(String username) throws SQLException {
    String query = "SELECT * FROM USERS WHERE username = ?";
    
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        
        stmt.setString(1, username);
        
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        }
    }
    return null;
}
```

#### 4. Secure Password Handling

**Use bcrypt or Argon2 for password hashing:**

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordService {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    
    public static String hashPassword(String plainPassword) {
        return encoder.encode(plainPassword);
    }
    
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }
}
```

#### 5. Secure Random Generation

**Use SecureRandom for cryptographic operations:**

```java
import java.security.SecureRandom;
import java.util.Base64;

public class TokenGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();
    
    public static String generateToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
```

---

### Servlet Security Patterns

#### 1. Authentication Filter

```java
@WebFilter("/*")
public class AuthenticationFilter implements Filter {
    
    private static final Set<String> PUBLIC_PATHS = Set.of(
        "/login.jsp", "/api/login", "/static/"
    );
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getRequestURI();
        
        // Allow public paths
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Check authentication
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }
}
```

#### 2. CSRF Protection

```java
public class CSRFTokenUtil {
    
    public static String generateToken(HttpSession session) {
        String token = TokenGenerator.generateToken();
        session.setAttribute("csrf_token", token);
        return token;
    }
    
    public static boolean validateToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        
        String sessionToken = (String) session.getAttribute("csrf_token");
        String requestToken = request.getParameter("csrf_token");
        
        return sessionToken != null && sessionToken.equals(requestToken);
    }
}
```

#### 3. Security Headers

```java
@WebFilter("/*")
public class SecurityHeadersFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Prevent clickjacking
        httpResponse.setHeader("X-Frame-Options", "DENY");
        
        // Enable XSS protection
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
        
        // Prevent MIME sniffing
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        
        // Content Security Policy
        httpResponse.setHeader("Content-Security-Policy", 
            "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'");
        
        // HSTS
        httpResponse.setHeader("Strict-Transport-Security", 
            "max-age=31536000; includeSubDomains");
        
        chain.doFilter(request, response);
    }
}
```

---

### Session Management Best Practices

```java
public class SessionManager {
    
    private static final int SESSION_TIMEOUT = 30 * 60; // 30 minutes
    private static final int MAX_SESSIONS_PER_USER = 3;
    
    public static void createSession(HttpServletRequest request, User user) {
        // Invalidate old session
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        
        // Create new session
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(SESSION_TIMEOUT);
        
        // Set secure attributes
        session.setAttribute("user", user);
        session.setAttribute("created_at", System.currentTimeMillis());
        session.setAttribute("ip_address", request.getRemoteAddr());
        
        // Regenerate session ID after login
        request.changeSessionId();
    }
    
    public static boolean validateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        
        // Check IP address hasn't changed
        String sessionIP = (String) session.getAttribute("ip_address");
        String currentIP = request.getRemoteAddr();
        if (!sessionIP.equals(currentIP)) {
            session.invalidate();
            return false;
        }
        
        return true;
    }
}
```

---

## Testing Recommendations

### 1. Static Application Security Testing (SAST)

**Recommended Tools:**
- **SonarQube** with Security plugin
- **Checkmarx**
- **Fortify Static Code Analyzer**
- **SpotBugs** with Find Security Bugs plugin

**Configuration:**

```xml
<!-- pom.xml for Maven -->
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.7.3.0</version>
    <configuration>
        <effort>Max</effort>
        <threshold>Low</threshold>
        <plugins>
            <plugin>
                <groupId>com.h3xstream.findsecbugs</groupId>
                <artifactId>findsecbugs-plugin</artifactId>
                <version>1.12.0</version>
            </plugin>
        </plugins>
    </configuration>
</plugin>
```

**CI/CD Integration:**

```yaml
# .github/workflows/security-scan.yml
name: Security Scan
on: [push, pull_request]

jobs:
  sast:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Run SpotBugs
        run: mvn spotbugs:check
      - name: SonarQube Scan
        run: mvn sonar:sonar -Dsonar.projectKey=altoromutual
```

---

### 2. Dynamic Application Security Testing (DAST)

**Recommended Tools:**
- **OWASP ZAP**
- **Burp Suite Professional**
- **Acunetix**
- **AppScan**

**Test Scenarios:**

| Test Category | Scenarios | Expected Result |
|---------------|-----------|-----------------|
| **SQL Injection** | Test all input fields with `' OR '1'='1`, `admin'--`, UNION attacks | No successful injection |
| **XSS** | Test with `<script>alert(1)</script>`, `<img src=x onerror=alert(1)>` | All output encoded |
| **Authentication** | Brute force, session fixation, token prediction | Rate limiting active, secure sessions |
| **Authorization** | Horizontal/vertical privilege escalation | Proper access controls |
| **CSRF** | Submit forms without CSRF token | Requests rejected |

**OWASP ZAP Automation:**

```bash
#!/bin/bash
# zap-scan.sh

ZAP_API_KEY="your-api-key"
TARGET_URL="http://localhost:8080/altoromutual"

# Start ZAP daemon
docker run -d --name zap -p 8090:8090 owasp/zap2docker-stable zap.sh -daemon \
    -host 0.0.0.0 -port 8090 -config api.key=$ZAP_API_KEY

# Wait for ZAP to start
sleep 30

# Spider the application
curl "http://localhost:8090/JSON/spider/action/scan/?url=$TARGET_URL&apikey=$ZAP_API_KEY"

# Active scan
curl "http://localhost:8090/JSON/ascan/action/scan/?url=$TARGET_URL&apikey=$ZAP_API_KEY"

# Generate report
curl "http://localhost:8090/OTHER/core/other/htmlreport/?apikey=$ZAP_API_KEY" > zap-report.html

# Stop ZAP
docker stop zap && docker rm zap
```

---

### 3. Manual Penetration Testing

**Focus Areas:**

1. **Authentication & Session Management**
   - Test password reset functionality
   - Session timeout verification
   - Concurrent session handling
   - Session fixation attacks

2. **Business Logic**
   - Negative amount transfers
   - Race conditions in fund transfers
   - Account enumeration
   - Transaction replay attacks

3. **API Security**
   - JWT token manipulation
   - API rate limiting
   - Mass assignment vulnerabilities
   - API versioning issues

4. **File Upload (if applicable)**
   - Malicious file upload
   - Path traversal via filename
   - File type validation bypass

**Penetration Testing Checklist:**

```markdown
## Pre-Test
- [ ] Obtain written authorization
- [ ] Define scope and rules of engagement
- [ ] Set up isolated test environment
- [ ] Backup application and database

## Reconnaissance
- [ ] Map application structure
- [ ] Identify entry points
- [ ] Enumerate users and roles
- [ ] Review client-side code

## Vulnerability Assessment
- [ ] Test all OWASP Top 10 categories
- [ ] Business logic testing
- [ ] API security testing
- [ ] Mobile app testing (if applicable)

## Exploitation
- [ ] Attempt to exploit identified vulnerabilities
- [ ] Document proof of concept
- [ ] Assess business impact
- [ ] Test defense mechanisms

## Post-Test
- [ ] Generate detailed report
- [ ] Present findings to stakeholders
- [ ] Provide remediation guidance
- [ ] Schedule retest after fixes
```

---

### 4. Security Regression Testing

**Automated Security Test Suite:**

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityRegressionTests {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    public void testSQLInjectionPrevention() {
        // Test SQL injection in login
        String sqlPayload = "admin'--";
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/login",
            new LoginRequest(sqlPayload, "password"),
            String.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).doesNotContain("SQL");
    }
    
    @Test
    public void testXSSPrevention() {
        String xssPayload = "<script>alert(1)</script>";
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/search?query=" + xssPayload,
            String.class
        );
        
        assertThat(response.getBody()).doesNotContain("<script>");
        assertThat(response.getBody()).contains("&lt;script&gt;");
    }
    
    @Test
    public void testCSRFProtection() {
        // Attempt transfer without CSRF token
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/transfer",
            new TransferRequest(123456, 789012, 100.00),
            String.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
    
    @Test
    public void testSecurityHeaders() {
        ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);
        
        assertThat(response.getHeaders().get("X-Frame-Options")).contains("DENY");
        assertThat(response.getHeaders().get("X-Content-Type-Options")).contains("nosniff");
        assertThat(response.getHeaders().get("X-XSS-Protection")).contains("1; mode=block");
    }
}
```

---

## Appendix A: Vulnerability Mapping

### OWASP Top 10 2021 Mapping

| OWASP Category | Vulnerabilities | Count |
|----------------|----------------|-------|
| **A01:2021 - Broken Access Control** | VULN-011, VULN-019 | 2 |
| **A02:2021 - Cryptographic Failures** | VULN-010, VULN-015 | 2 |
| **A03:2021 - Injection** | VULN-001 to VULN-007, VULN-009, VULN-012 to VULN-014, VULN-018 | 14 |
| **A04:2021 - Insecure Design** | VULN-020, VULN-021 | 2 |
| **A07:2021 - Identification and Authentication Failures** | VULN-016, VULN-017, VULN-022, VULN-023 | 4 |
| **A09:2021 - Security Logging and Monitoring Failures** | VULN-008 | 1 |

### CWE Top 25 Mapping

| CWE ID | Description | Vulnerabilities | Severity |
|--------|-------------|----------------|----------|
| **CWE-89** | SQL Injection | VULN-001 to VULN-007 | Critical |
| **CWE-79** | Cross-site Scripting | VULN-012, VULN-013, VULN-014, VULN-018 | High |
| **CWE-78** | OS Command Injection | VULN-009 | Critical |
| **CWE-330** | Insufficient Randomness | VULN-010 | Critical |
| **CWE-532** | Information Exposure Through Log Files | VULN-008 | Critical |
| **CWE-522** | Insufficiently Protected Credentials | VULN-015 | High |
| **CWE-601** | URL Redirection to Untrusted Site | VULN-011 | High |

---

## Appendix B: Glossary

**CVSS** - Common Vulnerability Scoring System: Industry standard for assessing vulnerability severity

**CSRF** - Cross-Site Request Forgery: Attack forcing users to execute unwanted actions

**DAST** - Dynamic Application Security Testing: Testing running applications for vulnerabilities

**JWT** - JSON Web Token: Compact, URL-safe means of representing claims

**OWASP** - Open Web Application Security Project: Nonprofit focused on software security

**PCI-DSS** - Payment Card Industry Data Security Standard: Security standard for card data

**PreparedStatement** - Java API for parameterized SQL queries preventing injection

**SAST** - Static Application Security Testing: Analyzing source code for vulnerabilities

**SOC2** - Service Organization Control 2: Audit for service providers' security controls

**SQL Injection** - Attack inserting malicious SQL code into application queries

**XSS** - Cross-Site Scripting: Injecting malicious scripts into web pages

---

## Appendix C: References

### Standards and Frameworks

1. **OWASP Top 10 2021**  
   https://owasp.org/Top10/

2. **PCI-DSS v4.0**  
   https://www.pcisecuritystandards.org/

3. **OWASP ASVS v4.0**  
   https://owasp.org/www-project-application-security-verification-standard/

4. **CWE Top 25**  
   https://cwe.mitre.org/top25/

5. **NIST Cybersecurity Framework**  
   https://www.nist.gov/cyberframework

### Tools and Resources

1. **OWASP ZAP**  
   https://www.zaproxy.org/

2. **Burp Suite**  
   https://portswigger.net/burp

3. **SonarQube**  
   https://www.sonarqube.org/

4. **OWASP Dependency-Check**  
   https://owasp.org/www-project-dependency-check/

5. **Java Security Documentation**  
   https://docs.oracle.com/en/java/javase/11/security/

---

## Document Control

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2026-03-29 | Security Review Team | Initial comprehensive audit report |

**Distribution List:**
- Chief Information Security Officer (CISO)
- Chief Technology Officer (CTO)
- Development Team Lead
- DevOps Manager
- Compliance Officer
- Legal Department

**Classification:** CONFIDENTIAL - Internal Use Only

**Next Review Date:** 2026-06-29 (90 days)

---

**END OF REPORT**
