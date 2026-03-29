# AltoroJ Functional Requirements Document

## Document Information
- **Version**: 1.0
- **Date**: 2026-03-29
- **Purpose**: Comprehensive functional requirements extracted from AltoroJ codebase
- **Scope**: User operations, administrative functions, business rules, and security requirements

---

## 1. User Operations

### 1.1 Authentication & Session Management

#### REQ-USER-001: User Login (Web UI)
**Description**: Users must authenticate via username and password to access banking features.

**Implementation**: `LoginServlet.doPost()`

**Acceptance Criteria**:
- User submits username and password via POST to `/doLogin`
- Username is converted to lowercase: `username.trim().toLowerCase()`
- Password is converted to lowercase: `password.trim().toLowerCase()`
- Credentials validated via `DBUtil.isValidUser(username, password)`
- On success:
  - User object stored in session with key `ServletUtil.SESSION_ATTR_USER`
  - Account list Base64-encoded and stored in `AltoroAccounts` cookie
  - User redirected to `/bank/main.jsp`
- On failure:
  - Error message stored in session attribute `loginError`
  - User redirected to `login.jsp`

**Business Rules**:
- Both username and password are case-insensitive (converted to lowercase)
- Empty or null credentials are rejected
- Failed login attempts are logged to Log4AltoroJ

**Security Note**: ⚠️ Password lowercase conversion reduces entropy (intentional vulnerability)

---

#### REQ-USER-002: User Login (REST API)
**Description**: API clients authenticate via JSON credentials to obtain authorization token.

**Implementation**: `LoginAPI.login()`

**Endpoint**: `POST /api/login`

**Request Body**:
```json
{
  "username": "string",
  "password": "string"
}
```

**Response (Success - 200 OK)**:
```json
{
  "success": "username is now logged in",
  "Authorization": "Base64-encoded-token"
}
```

**Response (Failure - 400 Bad Request)**:
```json
{
  "error": "username or password parameter missing"
}
```

**Acceptance Criteria**:
- Request body must be valid JSON
- Both `username` and `password` fields required
- Credentials converted to lowercase
- Token format: `Base64(Base64(username):Base64(password):randomString)`
- Token returned in `Authorization` field
- Invalid credentials return error message

**Business Rules**:
- Token contains encoded credentials (not secure - intentional)
- Random string appended for uniqueness
- No token expiration implemented

---

#### REQ-USER-003: User Logout (Web UI)
**Description**: Users can terminate their session.

**Implementation**: `LoginServlet.doGet()`

**Acceptance Criteria**:
- GET request to `/doLogin` triggers logout
- Session attribute `ServletUtil.SESSION_ATTR_USER` removed
- User redirected to `index.jsp`
- Exceptions silently caught

**Business Rules**:
- Logout always succeeds (no error conditions)
- Cookie remains in browser (not explicitly cleared)

---

#### REQ-USER-004: User Logout (REST API)
**Description**: API clients can terminate their session.

**Implementation**: `LogoutAPI.doLogOut()`

**Endpoint**: `GET /api/logout`

**Response (Success - 200 OK)**:
```json
{
  "LoggedOut": "True"
}
```

**Acceptance Criteria**:
- No authentication required (`@PermitAll`)
- Removes `SESSION_ATTR_USER` from session
- Returns success JSON
- Errors return 500 with error message

---

### 1.2 Account Management

#### REQ-USER-005: View Account List (Web UI)
**Description**: Authenticated users can view all their accounts.

**Implementation**: `bank/main.jsp` (displays accounts from session)

**Acceptance Criteria**:
- User must be authenticated (AuthFilter enforced)
- Accounts retrieved from User object in session
- Display account ID, name, and balance
- Support for multiple account types: Checking, Savings, Credit Card

**Business Rules**:
- Account list cached in session and cookie
- Cookie format: Base64-encoded `accountId~name~balance|...`
- All user accounts displayed (no filtering)

---

#### REQ-USER-006: View Account List (REST API)
**Description**: API clients can retrieve list of user's accounts.

**Implementation**: `AccountAPI.getAccounts()`

**Endpoint**: `GET /api/account`

**Authentication**: Required (Bearer token)

**Response (Success - 200 OK)**:
```json
{
  "Accounts": [
    {
      "Name": "Checking",
      "id": "800003"
    },
    {
      "Name": "Savings",
      "id": "800002"
    }
  ]
}
```

**Acceptance Criteria**:
- Valid authorization token required
- User extracted from token
- All accounts for user returned
- Account ID and name included
- Returns 500 on error

**Business Rules**:
- No pagination (all accounts returned)
- No filtering options

---

#### REQ-USER-007: View Account Balance (REST API)
**Description**: API clients can view detailed account information including balance and recent transactions.

**Implementation**: `AccountAPI.getAccountBalance()`

**Endpoint**: `GET /api/account/{accountNo}`

**Authentication**: Required (Bearer token)

**Response (Success - 200 OK)**:
```json
{
  "accountId": "800003",
  "balance": "$15000.39",
  "last_10_transactions": [
    {
      "date": "2019-03-11",
      "transaction_type": "Deposit",
      "ammount": "$400.00"
    }
  ],
  "credits": [...],
  "debits": [...]
}
```

**Acceptance Criteria**:
- Account number provided in URL path
- Balance formatted with currency symbol
- Last 10 transactions included
- Sample credit/debit data included (hardcoded)
- Returns 500 on error

**Business Rules**:
- ⚠️ No account ownership validation (security vulnerability)
- Any authenticated user can view any account
- Balance formatted: `$0.00` for amounts < 1, `$.00` otherwise

---

#### REQ-USER-008: View Transaction History (REST API)
**Description**: API clients can query transactions for a specific account.

**Implementation**: `AccountAPI.showLastTenTransactions()` and `AccountAPI.getTransactions()`

**Endpoint 1**: `GET /api/account/{accountNo}/transactions`
- Returns last 10 transactions

**Endpoint 2**: `POST /api/account/{accountNo}/transactions`
- Returns transactions filtered by date range

**Request Body (POST)**:
```json
{
  "startDate": "2019-01-01",
  "endDate": "2019-12-31"
}
```

**Response (Success - 200 OK)**:
```json
{
  "transactions": [
    {
      "id": "2315",
      "date": "2019-03-11 16:00",
      "account": "800003",
      "type": "Deposit",
      "amount": "$400.00"
    }
  ]
}
```

**Acceptance Criteria**:
- GET returns last 10 transactions
- POST accepts date range in format `yyyy-mm-dd`
- Maximum 100 transactions returned
- Transactions sorted by date descending
- Amount formatted with currency symbol
- Returns 400 for invalid date format
- Returns 500 on database error

**Business Rules**:
- Date format must be `yyyy-mm-dd HH:mm:ss` in database
- Transactions limited to 100 entries
- No pagination support

---

### 1.3 Fund Transfer Operations

#### REQ-USER-009: Transfer Funds (Web UI)
**Description**: Authenticated users can transfer money between accounts.

**Implementation**: `TransferServlet.doPost()` → `OperationsUtil.doServletTransfer()` → `DBUtil.transferFunds()`

**Endpoint**: `POST /doTransfer`

**Parameters**:
- `fromAccount`: Source account (ID or name)
- `toAccount`: Destination account ID
- `transferAmount`: Amount to transfer

**Acceptance Criteria**:
- User must be authenticated
- Source account validated against user's cookie
- Destination account must exist
- Amount must be > 0
- On success:
  - Debit transaction created
  - Credit transaction created
  - Account balances updated
  - Success message displayed
- On failure:
  - Error message displayed
  - No partial transactions

**Business Rules**:
- Source account can be specified by ID or name
- ⚠️ No balance validation (can create negative balances)
- ⚠️ No daily transfer limits
- ⚠️ No transaction boundary (partial failures possible)
- Cash advance fee ($2.50) applied if source is credit card
- Transaction logged to Log4AltoroJ

**Transaction Records Created**:
1. Debit transaction (negative amount) for source account
2. Credit transaction (positive amount) for destination account
3. Fee transaction (if applicable)

**Balance Updates**:
1. Source account balance decreased
2. Destination account balance increased

---

#### REQ-USER-010: Transfer Funds (REST API)
**Description**: API clients can transfer funds between accounts.

**Implementation**: `TransferAPI.transfer()` → `OperationsUtil.doApiTransfer()` → `DBUtil.transferFunds()`

**Endpoint**: `POST /api/transfer`

**Authentication**: Required (Bearer token)

**Request Body**:
```json
{
  "fromAccount": "800003",
  "toAccount": "800002",
  "transferAmount": "100.00"
}
```

**Response (Success - 200 OK)**:
```json
{
  "success": "100.0 was successfully transferred from Account 800003 into Account 800002 at 3/29/26 1:06 PM."
}
```

**Response (Failure - 500 Internal Server Error)**:
```json
{
  "error": "ERROR: Transaction failed. Please try again later."
}
```

**Acceptance Criteria**:
- Valid authorization token required
- All three parameters required
- Account IDs must be numeric
- Amount must be numeric
- User extracted from token
- Same business rules as web UI transfer
- Returns 400 for invalid JSON
- Returns 500 for execution errors

**Business Rules**:
- Same as REQ-USER-009
- ⚠️ No account ownership validation

---

### 1.4 Credit Card Operations

#### REQ-USER-011: Apply for Credit Card
**Description**: Authenticated users can apply for a new credit card.

**Implementation**: `CCApplyServlet.doPost()`

**Endpoint**: `POST /bank/apply`

**Parameters**:
- `passwd`: User's password for verification

**Acceptance Criteria**:
- User must be authenticated
- Password must match user's credentials
- Password converted to lowercase for validation
- On success: redirect to `applysuccess.jsp`
- On failure: display login error

**Business Rules**:
- Password re-verification required
- No actual credit card account created (simulation only)
- Password validation uses same lowercase conversion as login

---

### 1.5 Feedback & Communication

#### REQ-USER-012: Submit Feedback (Web UI)
**Description**: Users can submit feedback without authentication.

**Implementation**: `FeedbackServlet.doPost()`

**Endpoint**: `POST /feedback`

**Parameters**:
- `name`: Submitter's name
- `email_addr`: Email address
- `subject`: Feedback subject
- `comments`: Feedback message

**Acceptance Criteria**:
- No authentication required
- All fields optional except `comments`
- If `enableFeedbackRetention=true`:
  - Feedback stored in database
  - Feedback ID returned
- Redirect to `feedbacksuccess.jsp`
- Display confirmation with name and feedback ID

**Business Rules**:
- Feedback storage controlled by `enableFeedbackRetention` property
- SQL injection protection via `StringEscapeUtils.escapeSql()`
- Feedback ID auto-generated (starts at 1022)

---

#### REQ-USER-013: Submit Feedback (REST API)
**Description**: API clients can submit feedback without authentication.

**Implementation**: `FeedbackAPI.sendFeedback()`

**Endpoint**: `POST /api/feedback/submit`

**Authentication**: Not required (`@PermitAll`)

**Request Body**:
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "subject": "Great service",
  "message": "Thank you for the excellent banking experience."
}
```

**Response (Success - 200 OK)**:
```json
{
  "status": "Thank you!",
  "feedbackId": "1025"
}
```

**Acceptance Criteria**:
- No authentication required
- All four fields required
- Returns 400 if fields missing
- Returns feedback ID if storage enabled
- Returns 500 on error

---

#### REQ-USER-014: View Feedback (REST API)
**Description**: API clients can retrieve submitted feedback by ID.

**Implementation**: `FeedbackAPI.getFeedback()`

**Endpoint**: `GET /api/feedback/{feedbackId}`

**Authentication**: Required (Bearer token)

**Response (Success - 200 OK)**:
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "subject": "Great service",
  "message": "Thank you for the excellent banking experience."
}
```

**Acceptance Criteria**:
- Valid authorization token required
- Feedback ID must exist
- Returns all feedback fields
- ⚠️ No authorization check (any user can view any feedback)

---

#### REQ-USER-015: Subscribe to Newsletter
**Description**: Users can subscribe to mailing list.

**Implementation**: `SubscribeServlet.doPost()`

**Endpoint**: `POST /subscribe`

**Parameters**:
- `txtEmail`: Email address

**Acceptance Criteria**:
- Email must match regex: `^[\w\d\.%-]+@[\w\d\.%-]+\.\w{2,4}$`
- Invalid email redirects to `index.jsp`
- Valid email displays confirmation message
- No actual subscription occurs (simulation only)

**Business Rules**:
- Email validation via regex
- No database storage
- Confirmation message includes submitted email

---

### 1.6 Information Access

#### REQ-USER-016: View Account Dashboard
**Description**: Authenticated users can view their account dashboard.

**Implementation**: `bank/main.jsp`

**Acceptance Criteria**:
- User must be authenticated (AuthFilter)
- Display all user accounts
- Show account balances
- Provide navigation to other banking features

---

#### REQ-USER-017: Search News Articles
**Description**: Users can search news articles.

**Implementation**: `ServletUtil.searchArticles()`

**Acceptance Criteria**:
- Search query provided
- XML file parsed for matching articles
- Only public articles returned
- Results displayed in search results page

**Business Rules**:
- Articles stored in XML format
- `isPublic` flag controls visibility
- Title matching via `contains()` method

---

## 2. Administrative Operations

### 2.1 Admin Authentication

#### REQ-ADMIN-001: Admin Login
**Description**: Administrators must authenticate separately to access admin functions.

**Implementation**: `AdminLoginServlet.doPost()`

**Endpoint**: `POST /admin/doLogin`

**Parameters**:
- `uid`: Admin username
- `passw`: Admin password

**Acceptance Criteria**:
- Credentials validated via `DBUtil.isValidUser()`
- User role must be `Role.Admin`
- On success:
  - Session attribute `admin=altoroadmin` set
  - Redirect to `admin.jsp`
- On failure:
  - Error message displayed
  - Redirect to admin login

**Business Rules**:
- Requires both valid credentials AND admin role
- Admin session attribute required for admin operations
- Separate from regular user session

---

### 2.2 User Management

#### REQ-ADMIN-002: Add User (Web UI)
**Description**: Administrators can create new user accounts.

**Implementation**: `AdminServlet.doPost()` (endpoint: `/admin/addUser`)

**Parameters**:
- `firstname`: User's first name
- `lastname`: User's last name
- `username`: Unique username
- `password1`: Password
- `password2`: Password confirmation

**Acceptance Criteria**:
- Admin authentication required (AdminFilter)
- Username, password1, password2 are required
- Firstname and lastname optional (default to empty string)
- Passwords must match
- User created with role='user'
- Success/error message displayed

**Business Rules**:
- No password complexity requirements
- No username uniqueness validation (database constraint)
- Passwords stored in plaintext
- Default role is 'user' (not admin)

---

#### REQ-ADMIN-003: Add User (REST API)
**Description**: API clients can create new user accounts.

**Implementation**: `AdminAPI.addUser()`

**Endpoint**: `POST /api/admin/addUser`

**Authentication**: Required (Bearer token)

**Request Body**:
```json
{
  "firstname": "John",
  "lastname": "Doe",
  "username": "jdoe",
  "password1": "password123",
  "password2": "password123"
}
```

**Response (Success - 200 OK)**:
```json
{
  "success": "Requested operation has completed successfully."
}
```

**Response (Failure)**:
```json
{
  "error": "Entered passwords did not match."
}
```

**Acceptance Criteria**:
- Valid authorization token required
- Requires `enableAdminFunctions=true` in app.properties
- All five fields required
- Passwords must match
- Returns 400 for invalid JSON
- Returns 500 for database errors

**Business Rules**:
- Admin functions gated by configuration property
- Same validation as web UI

---

#### REQ-ADMIN-004: Change Password (Web UI)
**Description**: Administrators can change user passwords.

**Implementation**: `AdminServlet.doPost()` (endpoint: `/admin/changePassword`)

**Parameters**:
- `username`: Target username
- `password1`: New password
- `password2`: Password confirmation

**Acceptance Criteria**:
- Admin authentication required
- All three parameters required
- Passwords must match
- Password updated in database
- Success/error message displayed

**Business Rules**:
- No old password verification
- No password complexity requirements
- Admin can change any user's password

---

#### REQ-ADMIN-005: Change Password (REST API)
**Description**: API clients can change user passwords.

**Implementation**: `AdminAPI.changePassword()`

**Endpoint**: `POST /api/admin/changePassword`

**Authentication**: Required (Bearer token)

**Request Body**:
```json
{
  "username": "jdoe",
  "password1": "newpassword",
  "password2": "newpassword"
}
```

**Response (Success - 200 OK)**:
```json
{
  "success": "Requested operation has completed successfully."
}
```

**Acceptance Criteria**:
- Valid authorization token required
- Requires `enableAdminFunctions=true`
- All three fields required
- Passwords must match
- Returns 500 on error

**Business Rules**:
- ⚠️ No authorization check (any authenticated user can change passwords if property enabled)
- No old password verification

---

### 2.3 Account Management

#### REQ-ADMIN-006: Add Account
**Description**: Administrators can create new accounts for existing users.

**Implementation**: `AdminServlet.doPost()` (endpoint: `/admin/addAccount`)

**Parameters**:
- `username`: Target username
- `accttypes`: Account type (Checking, Savings, Credit Card)

**Acceptance Criteria**:
- Admin authentication required
- Both parameters required
- User must exist
- Account created with balance = 0
- Success/error message displayed

**Business Rules**:
- Account ID auto-generated (starts at 800000)
- Initial balance always 0
- No limit on number of accounts per user

---

### 2.4 Feedback Management

#### REQ-ADMIN-007: View All Feedback
**Description**: Administrators can view all submitted feedback.

**Implementation**: `ServletUtil.getAllFeedback()` → `DBUtil.getFeedback(Feedback.FEEDBACK_ALL)`

**Acceptance Criteria**:
- Admin authentication required
- All feedback entries returned
- Display feedback ID, name, email, subject, message
- No pagination

**Business Rules**:
- Feedback constant `FEEDBACK_ALL = -1` retrieves all entries
- No filtering options

---

#### REQ-ADMIN-008: View Specific Feedback
**Description**: Administrators can view individual feedback entries.

**Implementation**: `ServletUtil.getFeedback(feedbackId)`

**Acceptance Criteria**:
- Admin authentication required
- Feedback ID must be > 0
- Returns single feedback entry
- Returns null if not found

---

## 3. Business Rules

### 3.1 Account Balance Rules

#### RULE-001: Balance Validation
**Current Implementation**: ⚠️ **NO VALIDATION**

**Expected Behavior** (not implemented):
- Transfers should check sufficient funds
- Negative balances should be prevented
- Overdraft limits should be enforced

**Actual Behavior**:
- Transfers proceed regardless of balance
- Negative balances allowed
- No overdraft protection

**Impact**: Users can transfer more than available balance

---

#### RULE-002: Balance Calculation
**Implementation**: `DBUtil.transferFunds()`

**Rules**:
- Debit amount = -transfer amount (negative)
- Credit amount = +transfer amount (positive)
- Credit card balances are reversed (amount owed, not owned)
- If source is credit card:
  - Debit amount = +transfer amount (increases debt)
  - Cash advance fee added ($2.50)

**Formula**:
```
Source Account (non-credit card):
  New Balance = Old Balance - Transfer Amount

Destination Account (non-credit card):
  New Balance = Old Balance + Transfer Amount

Source Account (credit card):
  New Balance = Old Balance + Transfer Amount + Cash Advance Fee
```

---

### 3.2 Transfer Validation Rules

#### RULE-003: Transfer Amount Validation
**Implementation**: `OperationsUtil.doServletTransfer()` and `OperationsUtil.doApiTransfer()`

**Rules**:
- Amount must be > 0
- Amount = 0 is silently ignored (no error)
- Amount < 0 returns error: "Transfer amount is invalid"
- No maximum amount limit

**Validation Code**:
```java
if (amount < 0) {
    message = "Transfer amount is invalid";
} else if (amount > 0) {
    // Proceed with transfer
}
// amount == 0: do nothing
```

---

#### RULE-004: Account Verification
**Implementation**: `DBUtil.transferFunds()`

**Rules**:
- Source account must exist
- Destination account must exist
- Account existence checked via `Account.getAccount(accountId)`
- Returns error if either account invalid

**Error Messages**:
- "Originating account is invalid"
- "Destination account is invalid"

**⚠️ Missing Validation**:
- No account ownership verification
- No account status check (active/closed)
- No account type restrictions

---

#### RULE-005: Transfer Between Account Types
**Implementation**: `DBUtil.transferFunds()`

**Rules**:
- Transfers allowed between any account types
- Special handling for credit card accounts:
  - Transfer FROM credit card = Cash Advance
  - Transfer TO credit card = Payment
  - Cash advance fee ($2.50) applied when source is credit card

**Transaction Types**:
- Regular account → Regular account: Withdrawal/Deposit
- Credit card → Regular account: Cash Advance/Deposit
- Regular account → Credit card: Withdrawal/Payment
- Credit card → Credit card: Cash Advance/Payment

---

### 3.3 Transaction Recording Requirements

#### RULE-006: Transaction Record Creation
**Implementation**: `DBUtil.transferFunds()`

**Requirements**:
- Every transfer creates 2-3 transaction records:
  1. Debit transaction (source account)
  2. Credit transaction (destination account)
  3. Fee transaction (if source is credit card)

**Transaction Fields**:
- `TRANSACTION_ID`: Auto-generated (starts at 2311)
- `ACCOUNTID`: Account ID
- `DATE`: Current timestamp
- `TYPE`: Transaction type (Withdrawal, Deposit, Payment, Cash Advance, Cash Advance Fee)
- `AMOUNT`: Transaction amount (negative for debits, positive for credits)

**⚠️ Missing Fields**:
- No user ID
- No related transaction ID (to link debit/credit pairs)
- No original/new balance
- No transfer description

---

#### RULE-007: Transaction Logging
**Implementation**: `Log4AltoroJ.logTransaction()`

**Requirements**:
- Successful transfers logged to application log
- Log format: `"<source_account> - <source_name>" → "<dest_account> - <dest_name>": <amount>`
- Example: `"800003 - Checking" → "800002 - Savings": 100.00`
- Failed transfers logged with error details

**What is NOT Logged**:
- User who initiated transfer
- Source IP address
- Session ID
- Timestamp (handled by log framework)

---

### 3.4 Feedback Submission Rules

#### RULE-008: Feedback Storage
**Implementation**: `OperationsUtil.sendFeedback()`

**Rules**:
- Storage controlled by `enableFeedbackRetention` property
- If enabled:
  - SQL injection protection applied via `StringEscapeUtils.escapeSql()`
  - Feedback stored in FEEDBACK table
  - Feedback ID returned
- If disabled:
  - Feedback not stored
  - Returns null

**Fields Stored**:
- `FEEDBACK_ID`: Auto-generated (starts at 1022)
- `NAME`: Submitter name
- `EMAIL`: Email address
- `SUBJECT`: Feedback subject
- `COMMENTS`: Feedback message

---

#### RULE-009: Feedback Retrieval
**Implementation**: `DBUtil.getFeedback()`

**Rules**:
- Retrieve all feedback: `getFeedback(Feedback.FEEDBACK_ALL)` where `FEEDBACK_ALL = -1`
- Retrieve specific feedback: `getFeedback(feedbackId)` where `feedbackId > 0`
- Returns ArrayList of Feedback objects
- No pagination or filtering

---

## 4. Security Requirements

### 4.1 Authentication Requirements

#### SEC-001: Web UI Authentication
**Implementation**: `LoginServlet` + `AuthFilter`

**Requirements**:
- Username and password required
- Credentials validated against PEOPLE table
- SQL query: `SELECT COUNT(*) FROM PEOPLE WHERE USER_ID='<user>' AND PASSWORD='<pass>'`
- ⚠️ SQL injection vulnerable (intentional)
- Session created on successful authentication
- User object stored in session
- Cookie created with Base64-encoded account list

**Password Processing**:
```java
username = username.trim().toLowerCase();
password = password.trim().toLowerCase();
```

**⚠️ Security Issues**:
- Passwords converted to lowercase (reduces entropy)
- SQL injection via string concatenation
- Passwords stored in plaintext
- No account lockout
- No CAPTCHA
- No rate limiting

---

#### SEC-002: REST API Authentication
**Implementation**: `LoginAPI` + `ApiAuthFilter`

**Requirements**:
- JSON credentials required
- Token generated on successful authentication
- Token format: `Base64(Base64(username):Base64(password):randomString)`
- Token included in `Authorization` header as `Bearer <token>`
- Token validated on each API request
- Credentials re-validated from database on each request

**Token Validation Process**:
1. Extract `Authorization` header
2. Remove `Bearer ` prefix
3. Base64 decode token
4. Split by `:` delimiter
5. Base64 decode username and password
6. Validate via `DBUtil.isValidUser()`

**⚠️ Security Issues**:
- Token contains credentials (not secure)
- No token expiration
- No token revocation
- Credentials validated on every request (performance impact)
- Same lowercase conversion as web UI

---

### 4.2 Session Management Rules

#### SEC-003: Session Creation
**Implementation**: `ServletUtil.establishSession()`

**Requirements**:
- Session created via `request.getSession(true)`
- User object stored with key `ServletUtil.SESSION_ATTR_USER`
- Account list Base64-encoded and stored in cookie
- Cookie name: `AltoroAccounts`
- Cookie format: `accountId~name~balance|accountId~name~balance|...`

**Example Cookie Value** (before Base64):
```
800003~Checking~15000.39|800002~Savings~10000.42|4539082039396288~Credit Card~100.42|
```

**⚠️ Security Issues**:
- Base64 encoding (not encryption)
- Account balances exposed in cookie
- Cookie not marked HttpOnly or Secure
- No cookie expiration

---

#### SEC-004: Session Validation
**Implementation**: `AuthFilter.doFilter()`

**Requirements**:
- Check for User object in session
- Validate object is instance of User class
- Redirect to login if validation fails
- Applied to all `/bank/*` paths

**Validation Code**:
```java
Object user = request.getSession().getAttribute(ServletUtil.SESSION_ATTR_USER);
if (user == null || !(user instanceof User)) {
    response.sendRedirect(request.getContextPath() + "/login.jsp");
    return;
}
```

**⚠️ Security Issues**:
- No session timeout
- No session fixation protection
- No CSRF protection

---

### 4.3 Password Handling

#### SEC-005: Password Storage
**Implementation**: PEOPLE table

**Requirements**:
- Passwords stored in VARCHAR(20) column
- ⚠️ **Plaintext storage** (no hashing)
- Maximum password length: 20 characters
- No password complexity requirements

**Database Schema**:
```sql
CREATE TABLE PEOPLE (
    USER_ID VARCHAR(50) NOT NULL,
    PASSWORD VARCHAR(20) NOT NULL,
    ...
)
```

**⚠️ Security Issues**:
- No password hashing (bcrypt, PBKDF2, etc.)
- No salt
- Passwords visible in database
- Short maximum length (20 chars)

---

#### SEC-006: Password Validation
**Implementation**: `DBUtil.isValidUser()`

**Requirements**:
- Username and password converted to lowercase
- SQL query with string concatenation
- Case-insensitive comparison

**Validation Query**:
```sql
SELECT COUNT(*) FROM PEOPLE 
WHERE USER_ID = '<username>' AND PASSWORD='<password>'
```

**⚠️ Security Issues**:
- SQL injection vulnerable
- Case-insensitive (reduces security)
- No timing attack protection
- Failed attempts logged with credentials

---

### 4.4 Admin Privilege Requirements

#### SEC-007: Admin Access Control
**Implementation**: `AdminFilter.doFilter()`

**Requirements**:
- Check for session attribute `admin=altoroadmin`
- Applied to all `/admin/*` paths
- Redirect to admin login if not authorized

**Validation Code**:
```java
String admin = (String) session.getAttribute(ServletUtil.SESSION_ATTR_ADMIN_KEY);
if (admin == null || !ServletUtil.SESSION_ATTR_ADMIN_VALUE.equals(admin)) {
    response.sendRedirect(request.getContextPath() + "/admin/login.jsp");
    return;
}
```

**Admin Session Attributes**:
- Key: `ServletUtil.SESSION_ATTR_ADMIN_KEY` = "admin"
- Value: `ServletUtil.SESSION_ATTR_ADMIN_VALUE` = "altoroadmin"

---

#### SEC-008: Admin Function Gating
**Implementation**: `ServletUtil.getAppProperty("enableAdminFunctions")`

**Requirements**:
- Admin functions controlled by `enableAdminFunctions` property
- Property must be set to "true" (case-insensitive)
- Applies to:
  - Add user
  - Change password
  - Add account (web UI only)
- API admin functions check property before execution

**Property Check**:
```java
if (ServletUtil.getAppProperty("enableAdminFunctions").equalsIgnoreCase("true")) {
    // Execute admin function
}
```

**⚠️ Security Issues**:
- Property file can be modified
- No audit logging of admin actions
- No separation of admin privileges (all-or-nothing)

---

### 4.5 Authorization Rules

#### SEC-009: Account Access Authorization
**Current Implementation**: ⚠️ **NO AUTHORIZATION**

**Expected Behavior** (not implemented):
- Users should only access their own accounts
- Account ownership should be verified
- Unauthorized access should be denied

**Actual Behavior**:
- Web UI: Account access limited by cookie (weak protection)
- REST API: No account ownership validation
- Any authenticated user can access any account via API

**Impact**: Insecure Direct Object Reference vulnerability

---

#### SEC-010: API Endpoint Authorization
**Implementation**: `ApiAuthFilter` + `@PermitAll` annotation

**Requirements**:
- All API endpoints require authentication by default
- Exceptions marked with `@PermitAll`:
  - `POST /api/login`
  - `POST /api/feedback/submit`
  - `GET /api/logout`
- Authorization header validated on each request
- Invalid/missing token returns 401 Unauthorized

**Exempt Endpoints**:
```java
@PermitAll
public Response login(...) { }

@PermitAll
public Response sendFeedback(...) { }

@PermitAll
public Response doLogOut(...) { }
```

---

## 5. Non-Functional Requirements

### 5.1 Data Integrity

#### NFR-001: Transaction Atomicity
**Current Implementation**: ⚠️ **NOT ATOMIC**

**Requirement**: Fund transfers should be atomic (all-or-nothing)

**Actual Behavior**:
- Each SQL statement auto-commits
- No transaction boundary
- Partial failures leave inconsistent state
- No rollback capability

**Impact**: Data integrity not guaranteed

---

#### NFR-002: Concurrent Access
**Current Implementation**: ⚠️ **NO CONCURRENCY CONTROL**

**Requirement**: Concurrent transfers should not cause race conditions

**Actual Behavior**:
- No locking mechanism
- No isolation level configuration
- Race conditions possible
- Incorrect balances possible with concurrent transfers

---

### 5.2 Performance

#### NFR-003: Database Connection
**Implementation**: Singleton pattern in `DBUtil`

**Requirements**:
- Single connection instance per application
- Connection reused across requests
- Auto-reconnect if connection closed

**Limitations**:
- Single connection limits throughput
- No connection pooling
- Bottleneck for concurrent users

---

### 5.3 Audit & Compliance

#### NFR-004: Audit Logging
**Current Implementation**: ⚠️ **INSUFFICIENT**

**Requirements** (not met):
- User attribution for all transactions
- IP address logging
- Session tracking
- Before/after balance logging
- Admin action logging

**Actual Behavior**:
- Transaction records lack user ID
- No IP address logging
- No comprehensive audit trail
- Does not meet compliance requirements (PCI-DSS, SOX, GDPR)

---

## 6. Configuration Requirements

### 6.1 Application Properties

#### CONF-001: Property File Location
**Location**: `WebContent/WEB-INF/app.properties`

**Loading**: `ServletUtil.initializeAppProperties()`

**Format**: `key=value` (one per line)

---

#### CONF-002: Available Properties

**enableAdminFunctions**
- Type: Boolean
- Default: false (commented out)
- Purpose: Enable admin operations (add user, change password, add account)
- Usage: `ServletUtil.isAppPropertyTrue("enableAdminFunctions")`

**enableFeedbackRetention**
- Type: Boolean
- Default: false (commented out)
- Purpose: Store feedback in database
- Usage: `ServletUtil.isAppPropertyTrue("enableFeedbackRetention")`

**advancedStaticPageProcessing**
- Type: Boolean
- Default: false (commented out)
- Purpose: Enable OS command execution for file lookup
- ⚠️ Security Risk: Command injection vulnerability

**specialLink**
- Type: String
- Default: none
- Purpose: Override certain navigation links
- Usage: `ServletUtil.getAppProperty("specialLink")`

**database.alternateDataSource**
- Type: String
- Default: none
- Purpose: JNDI datasource name for external database
- Usage: Connect to DB2, MySQL, etc. instead of Derby

**database.reinitializeOnStart**
- Type: Boolean
- Default: false (commented out)
- Purpose: Force database reinitialization on each startup
- Usage: Required for first-time external database setup

---

## 7. Summary of Intentional Vulnerabilities

This application is **deliberately vulnerable** for security training purposes. The following vulnerabilities are intentional:

1. **SQL Injection**: String concatenation in queries
2. **XSS**: Insufficient output encoding
3. **Weak Authentication**: Lowercase password conversion
4. **Insecure Direct Object Reference**: No account ownership validation
5. **Command Injection**: advancedStaticPageProcessing property
6. **Path Traversal**: File operations without validation
7. **Information Disclosure**: Verbose error messages
8. **Session Management**: Base64 encoding instead of encryption
9. **Missing Authorization**: Insufficient access controls
10. **Weak Cryptography**: Predictable token generation

**⚠️ DO NOT USE IN PRODUCTION**

---

## Document Revision History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2026-03-29 | Architecture Analysis | Initial comprehensive requirements document |

---

*This document describes requirements for a deliberately vulnerable application designed for security training. Do not deploy in production environments.*