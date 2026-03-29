# AltoroJ Data Pipeline Analysis

## Executive Summary

This document provides a comprehensive analysis of data flow through the AltoroJ banking application, covering request lifecycles, transaction states, session management, error handling, consistency guarantees, audit trails, and database initialization. It includes detailed flowcharts showing complete data pipelines for both web UI and REST API paths.

---

## 1. Request Lifecycle

### 1.1 Web UI Request Lifecycle

#### Complete Flow: HTTP Request → Database Commit

```mermaid
flowchart TD
    Start([HTTP Request]) --> TomcatReceive[Tomcat Receives Request]
    TomcatReceive --> FilterChain{Filter Chain}
    
    FilterChain --> AuthFilter{AuthFilter<br/>Check Session}
    AuthFilter -->|No Session| RedirectLogin[Redirect to login.jsp]
    AuthFilter -->|Valid Session| AdminFilter{AdminFilter<br/>Check Admin?}
    
    AdminFilter -->|Admin Required| CheckAdmin{Has admin<br/>attribute?}
    CheckAdmin -->|No| RedirectAdminLogin[Redirect to admin/login.jsp]
    CheckAdmin -->|Yes| ServletDispatch
    AdminFilter -->|Not Admin Page| ServletDispatch[Dispatch to Servlet]
    
    ServletDispatch --> ServletProcess[Servlet Processing]
    ServletProcess --> ValidateInput{Validate<br/>Input}
    
    ValidateInput -->|Invalid| SetError[Set Error Message]
    SetError --> ForwardJSP[Forward to JSP]
    
    ValidateInput -->|Valid| BusinessLogic[Business Logic Layer]
    BusinessLogic --> ServletUtil[ServletUtil Methods]
    ServletUtil --> OperationsUtil[OperationsUtil Methods]
    
    OperationsUtil --> DataAccess[Data Access Layer]
    DataAccess --> DBUtil[DBUtil Methods]
    DBUtil --> GetConnection{Get DB<br/>Connection}
    
    GetConnection -->|Connection Failed| DBError[SQLException]
    DBError --> ErrorResponse[Error Response]
    
    GetConnection -->|Connected| ExecuteSQL[Execute SQL]
    ExecuteSQL --> SQLExec{SQL<br/>Execution}
    
    SQLExec -->|SQL Error| SQLError[SQLException]
    SQLError --> ErrorResponse
    
    SQLExec -->|Success| AutoCommit[Auto-Commit<br/>Derby Default]
    AutoCommit --> UpdateSession[Update Session/Cookie]
    UpdateSession --> ForwardJSP
    
    ForwardJSP --> JSPRender[JSP Renders Response]
    JSPRender --> HTTPResponse[HTTP Response]
    HTTPResponse --> End([Response to Client])
    
    RedirectLogin --> End
    RedirectAdminLogin --> End
    ErrorResponse --> End
    
    style Start fill:#e1f5ff
    style End fill:#e1f5ff
    style AutoCommit fill:#90EE90
    style DBError fill:#ffcccc
    style SQLError fill:#ffcccc
    style ErrorResponse fill:#ffcccc
```

**Key Insight**: Each SQL statement auto-commits immediately in Derby. There is NO transaction boundary, creating consistency risks during multi-statement operations like fund transfers.

### 1.2 REST API Request Lifecycle

#### Complete Flow: API Request → Database Commit

```mermaid
flowchart TD
    Start([HTTP API Request]) --> TomcatReceive[Tomcat Receives Request]
    TomcatReceive --> JerseyDispatch[Jersey JAX-RS Dispatcher]
    JerseyDispatch --> ApiAuthFilter{ApiAuthFilter<br/>Check Token}
    
    ApiAuthFilter -->|PermitAll| ResourceMethod[Resource Method]
    ApiAuthFilter -->|Requires Auth| ExtractToken{Extract<br/>Authorization<br/>Header}
    
    ExtractToken -->|Missing| Return401[Return 401<br/>Unauthorized]
    ExtractToken -->|Present| DecodeToken[Decode Base64 Token]
    
    DecodeToken --> ParseToken{Parse Token<br/>Format}
    ParseToken -->|Invalid Format| Return401
    ParseToken -->|Valid| ExtractCreds[Extract Username<br/>& Password]
    
    ExtractCreds --> ValidateDB{DBUtil.isValidUser<br/>SQL Query}
    ValidateDB -->|Invalid| Return401
    ValidateDB -->|Valid| ResourceMethod
    
    ResourceMethod --> ParseJSON{Parse JSON<br/>Request Body}
    ParseJSON -->|Invalid JSON| Return400[Return 400<br/>Bad Request]
    ParseJSON -->|Valid| ValidateParams{Validate<br/>Parameters}
    
    ValidateParams -->|Invalid| Return400
    ValidateParams -->|Valid| BusinessLogic[Business Logic]
    
    BusinessLogic --> OperationsUtil[OperationsUtil.doApiTransfer]
    OperationsUtil --> DBUtil[DBUtil.transferFunds]
    
    DBUtil --> GetConnection{Get DB<br/>Connection}
    GetConnection -->|Failed| Return500[Return 500<br/>Internal Error]
    
    GetConnection -->|Success| ValidateAccounts{Validate<br/>Accounts Exist}
    ValidateAccounts -->|Invalid| ReturnError[Return Error<br/>Message]
    
    ValidateAccounts -->|Valid| ExecuteTransfer[Execute Transfer<br/>SQL Statements]
    ExecuteTransfer --> InsertDebit[INSERT debit<br/>transaction]
    InsertDebit --> InsertCredit[INSERT credit<br/>transaction]
    InsertCredit --> CheckCreditCard{From Credit<br/>Card?}
    
    CheckCreditCard -->|Yes| InsertFee[INSERT cash<br/>advance fee]
    InsertFee --> UpdateBalances[UPDATE account<br/>balances]
    CheckCreditCard -->|No| UpdateBalances
    
    UpdateBalances --> AutoCommit[Auto-Commit]
    AutoCommit --> LogTransaction[Log to<br/>Log4AltoroJ]
    LogTransaction --> FormatJSON[Format JSON<br/>Response]
    
    FormatJSON --> Return200[Return 200 OK<br/>with JSON]
    Return200 --> End([Response to Client])
    
    Return401 --> End
    Return400 --> End
    Return500 --> End
    ReturnError --> End
    
    style Start fill:#e1f5ff
    style End fill:#e1f5ff
    style AutoCommit fill:#90EE90
    style Return401 fill:#ffcccc
    style Return400 fill:#ffcccc
    style Return500 fill:#ffcccc
    style ReturnError fill:#ffcccc
```

---

## 2. Transaction States

### 2.1 Transfer Transaction State Machine

```mermaid
stateDiagram-v2
    [*] --> Initiated: User submits transfer
    
    Initiated --> Validating: Extract parameters
    
    Validating --> ValidationFailed: Invalid input
    Validating --> Authenticated: Input valid
    
    ValidationFailed --> [*]: Return error
    
    Authenticated --> AuthFailed: Session/token invalid
    Authenticated --> Authorized: Auth successful
    
    AuthFailed --> [*]: Return 401/redirect
    
    Authorized --> AccountValidation: Check accounts
    
    AccountValidation --> AccountInvalid: Account not found
    AccountValidation --> AmountValidation: Accounts valid
    
    AccountInvalid --> [*]: Return error
    
    AmountValidation --> AmountInvalid: Amount <= 0
    AmountValidation --> Executing: Amount valid
    
    AmountInvalid --> [*]: Return error
    
    Executing --> TransactionInsert: Insert debit transaction
    TransactionInsert --> TransactionInsert2: Insert credit transaction
    TransactionInsert2 --> FeeCheck: Check if credit card
    
    FeeCheck --> FeeInsert: Is credit card
    FeeCheck --> BalanceUpdate: Not credit card
    
    FeeInsert --> BalanceUpdate: Insert fee transaction
    
    BalanceUpdate --> DebitUpdate: Update debit account
    DebitUpdate --> CreditUpdate: Update credit account
    
    CreditUpdate --> ExecutionFailed: SQL error
    CreditUpdate --> Committed: Auto-commit success
    
    ExecutionFailed --> [*]: Return 500/error
    
    Committed --> Logged: Log transaction
    Logged --> Completed: Return success
    
    Completed --> [*]
    
    note right of Executing
        No transaction boundary!
        Each SQL auto-commits
        Partial failure possible
    end note
    
    note right of Committed
        Derby auto-commit
        No rollback capability
    end note
```

**Critical Issue**: The lack of transaction boundaries means that if any step fails after the first INSERT, the database will be left in an inconsistent state with no ability to rollback.

---

## 3. Session Management

### 3.1 Session Lifecycle

```mermaid
flowchart TD
    Start([User Accesses Site]) --> CheckSession{Session<br/>Exists?}
    
    CheckSession -->|No| ShowLogin[Display login.jsp]
    CheckSession -->|Yes| ValidateSession{Session<br/>Valid?}
    
    ShowLogin --> UserLogin[User Enters<br/>Credentials]
    UserLogin --> SubmitLogin[POST to /doLogin]
    
    SubmitLogin --> LoginServlet[LoginServlet.doPost]
    LoginServlet --> LowercaseCreds[Convert username<br/>& password to<br/>lowercase]
    
    LowercaseCreds --> ValidateDB{DBUtil.isValidUser<br/>SQL Query}
    
    ValidateDB -->|Invalid| SetLoginError[Set loginError<br/>in session]
    SetLoginError --> RedirectLogin[Redirect to<br/>login.jsp]
    
    ValidateDB -->|Valid| CreateSession[Create/Get Session]
    CreateSession --> GetUserInfo[DBUtil.getUserInfo]
    GetUserInfo --> GetAccounts[DBUtil.getAccounts]
    
    GetAccounts --> CreateUserObj[Create User Object]
    CreateUserObj --> StoreInSession[session.setAttribute<br/>SESSION_ATTR_USER]
    
    StoreInSession --> EncodeAccounts[Base64 Encode<br/>Account List]
    EncodeAccounts --> CreateCookie[Create AltoroAccounts<br/>Cookie]
    
    CreateCookie --> SetCookie[response.addCookie]
    SetCookie --> RedirectMain[Redirect to<br/>bank/main.jsp]
    
    ValidateSession -->|Invalid| ShowLogin
    ValidateSession -->|Valid| CheckExpiry{Session<br/>Expired?}
    
    CheckExpiry -->|Yes| ShowLogin
    CheckExpiry -->|No| ServeContent[Serve Protected<br/>Content]
    
    ServeContent --> UserAction{User Action}
    UserAction -->|Logout| RemoveSession[session.removeAttribute<br/>SESSION_ATTR_USER]
    UserAction -->|Continue| UpdateAccess[Update lastAccessDate]
    
    RemoveSession --> RedirectIndex[Redirect to index.jsp]
    UpdateAccess --> ServeContent
    
    RedirectLogin --> End([End])
    RedirectMain --> End
    RedirectIndex --> End
    
    style Start fill:#e1f5ff
    style End fill:#e1f5ff
    style StoreInSession fill:#90EE90
    style CreateCookie fill:#90EE90
    style RemoveSession fill:#ffcccc
```

**Session Data Structure:**
- **User Object**: Contains username, firstName, lastName, role, lastAccessDate
- **Admin Attribute**: Set to "altoroadmin" for admin users
- **AltoroAccounts Cookie**: Base64-encoded list of accounts (format: `accountId~name~balance|...`)

---

## 4. Error Handling

### 4.1 Comprehensive Error Handling Flow

```mermaid
flowchart TD
    Start([Request Received]) --> Stage1{Authentication<br/>Stage}
    
    Stage1 -->|Auth Failed| AuthError[Authentication Error]
    Stage1 -->|Auth Success| Stage2{Authorization<br/>Stage}
    
    AuthError --> WebUI1{Web UI or<br/>API?}
    WebUI1 -->|Web UI| RedirectLogin[Redirect to login.jsp<br/>Set loginError in session]
    WebUI1 -->|API| Return401[Return 401 Unauthorized<br/>JSON error message]
    
    Stage2 -->|Authz Failed| AuthzError[Authorization Error]
    Stage2 -->|Authz Success| Stage3{Validation<br/>Stage}
    
    AuthzError --> WebUI2{Web UI or<br/>API?}
    WebUI2 -->|Web UI| RedirectAdminLogin[Redirect to admin/login.jsp]
    WebUI2 -->|API| Return403[Return 403 Forbidden<br/>JSON error message]
    
    Stage3 -->|Validation Failed| ValidationError[Validation Error]
    Stage3 -->|Validation Success| Stage4{Execution<br/>Stage}
    
    ValidationError --> WebUI3{Web UI or<br/>API?}
    WebUI3 -->|Web UI| ForwardError[Forward to JSP<br/>Display error message]
    WebUI3 -->|API| Return400[Return 400 Bad Request<br/>JSON error message]
    
    Stage4 -->|Execution Failed| ExecutionError[Execution Error]
    Stage4 -->|Execution Success| Success[Success Response]
    
    ExecutionError --> CheckError{Error Type}
    CheckError -->|SQLException| DBError[Database Error]
    CheckError -->|Business Logic| BusinessError[Business Logic Error]
    
    DBError --> WebUI4{Web UI or<br/>API?}
    WebUI4 -->|Web UI| Return500Web[Return 500<br/>Display error page]
    WebUI4 -->|API| Return500API[Return 500<br/>JSON error message]
    
    BusinessError --> WebUI5{Web UI or<br/>API?}
    WebUI5 -->|Web UI| ForwardErrorMsg[Forward to JSP<br/>Display error message]
    WebUI5 -->|API| ReturnErrorJSON[Return error JSON<br/>with message]
    
    Success --> End([Response Sent])
    RedirectLogin --> End
    Return401 --> End
    RedirectAdminLogin --> End
    Return403 --> End
    ForwardError --> End
    Return400 --> End
    Return500Web --> End
    Return500API --> End
    ForwardErrorMsg --> End
    ReturnErrorJSON --> End
    
    style Start fill:#e1f5ff
    style End fill:#e1f5ff
    style Success fill:#90EE90
    style AuthError fill:#ffcccc
    style AuthzError fill:#ffcccc
    style ValidationError fill:#ffcccc
    style ExecutionError fill:#ffcccc
    style DBError fill:#ff9999
    style BusinessError fill:#ffcccc
```

**Key Error Scenarios:**
1. **Authentication Failure**: Invalid credentials → 401 or redirect to login
2. **Authorization Failure**: Insufficient privileges → 403 or redirect to admin login
3. **Validation Failure**: Invalid parameters → 400 or error message
4. **Execution Failure**: Database/business logic error → 500 or error message

**Critical Gap**: No rollback mechanism for partial transaction failures.

---

## 5. Consistency Guarantees

### 5.1 Current State: NO ACID Guarantees

**The Problem:**
```java
// DBUtil.transferFunds() - NO TRANSACTION BOUNDARY
statement.execute("INSERT INTO TRANSACTIONS (debit)");  // Commits immediately
statement.execute("INSERT INTO TRANSACTIONS (credit)"); // Commits immediately  
statement.execute("UPDATE ACCOUNTS (debit)");           // Commits immediately
statement.execute("UPDATE ACCOUNTS (credit)");          // Commits immediately

// If any statement fails, previous commits persist
// Result: Inconsistent database state with NO ROLLBACK
```

**Consistency Issues:**
1. ❌ No transaction boundary
2. ❌ No balance validation
3. ❌ No concurrent access control
4. ❌ No idempotency
5. ❌ No rollback capability

**What SHOULD Be Implemented:**
```java
connection.setAutoCommit(false);
try {
    // All operations
    statement.execute("INSERT...");
    statement.execute("UPDATE...");
    connection.commit(); // All or nothing
} catch (SQLException e) {
    connection.rollback(); // Undo all changes
}
```

---

## 6. Audit Trail

### 6.1 What is Logged

**TRANSACTIONS Table:**
- ✅ Transaction ID (auto-generated)
- ✅ Account ID
- ✅ Timestamp
- ✅ Transaction type
- ✅ Amount

**Log4AltoroJ:**
- ✅ Successful transfers
- ✅ Login failures
- ✅ Database errors
- ✅ Initialization errors

### 6.2 What is NOT Logged

**Critical Gaps:**
- ❌ User who initiated transaction
- ❌ Source IP address
- ❌ Session ID
- ❌ Related transaction ID (for transfers)
- ❌ Before/after balances
- ❌ Authorization method (web/API)
- ❌ Admin operations
- ❌ Password changes
- ❌ Account modifications

**Compliance Impact**: Does NOT meet PCI-DSS, SOX, GDPR, GLBA, or ISO 27001 requirements.

---

## 7. Database Initialization

### 7.1 Initialization Trigger

```mermaid
flowchart TD
    Start([Tomcat Startup]) --> StartupListener[StartupListener.contextInitialized]
    StartupListener --> InitProps[Load app.properties]
    InitProps --> InitLog[Initialize log file]
    InitLog --> TriggerDB[DBUtil.isValidUser<br/>bogus, user]
    
    TriggerDB --> GetConnection[DBUtil.getConnection]
    GetConnection --> CheckInstance{Instance<br/>Exists?}
    
    CheckInstance -->|No| CreateInstance[new DBUtil]
    CreateInstance --> SetDerbyHome[derby.system.home<br/>= ~/altoro/]
    SetDerbyHome --> LoadDriver[Load Derby Driver]
    
    LoadDriver --> TryConnect{Connect to<br/>jdbc:derby:altoro}
    
    TryConnect -->|Success| CheckReinit{Reinitialize<br/>on Start?}
    TryConnect -->|Error 40000| CreateDB[Connect with<br/>;create=true]
    
    CreateDB --> InitDB[DBUtil.initDB]
    CheckReinit -->|Yes| InitDB
    CheckReinit -->|No| ReturnConn[Return Connection]
    
    InitDB --> DropTables[DROP existing tables]
    DropTables --> CreateTables[CREATE tables]
    CreateTables --> SeedData[INSERT seed data]
    SeedData --> Complete[Initialization Complete]
    
    ReturnConn --> Complete
    Complete --> End([Ready for Requests])
    
    style Start fill:#e1f5ff
    style End fill:#90EE90
    style InitDB fill:#ffffcc
```

**Database Location**: `~/altoro/` (user home directory, NOT project directory)

**Initialization Modes:**
1. **First Startup**: Database doesn't exist → auto-creates with seed data
2. **Subsequent Startups**: Database exists → uses existing data
3. **Force Reinit**: Set `database.reinitializeOnStart=true` → drops and recreates

---

## Summary

This analysis reveals several critical architectural issues in AltoroJ:

1. **No Transaction Management**: Each SQL statement auto-commits, creating consistency risks
2. **Insufficient Error Handling**: No rollback capability for partial failures
3. **Weak Session Security**: Base64 encoding instead of encryption
4. **Inadequate Audit Trail**: Missing user attribution, IP tracking, and balance history
5. **No Consistency Guarantees**: Race conditions and negative balances possible

These issues are **intentional** as AltoroJ is designed as a vulnerable application for security training purposes.

---

*Document Version: 1.0 | Last Updated: 2026-03-29*