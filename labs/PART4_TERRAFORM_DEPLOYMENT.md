# IBM Bob Workshop - Cloud Deployment with Terraform | Part 4
## Case Study: Deploying AltoroMutual to IBM Code Engine

### Audience
DevOps engineers, cloud architects, platform engineers, and developers responsible for cloud deployments

### Goal of the Workshop
Demonstrate how **IBM Bob** can:
- Generate Infrastructure as Code (IaC) using Terraform
- Leverage MCP (Model Context Protocol) servers for cloud operations
- Deploy containerized applications to IBM Cloud Code Engine
- Configure auto-scaling and resource management
- Manage cloud infrastructure through conversational AI

We will deploy the **AltoroMutual Banking Application** to IBM Cloud Code Engine using Terraform, with the Docker image already pushed to IBM Cloud Container Registry.

### Bob IDE Feature
> **Required Feature:** Terraform IBM Modules MCP Server
>
> You will use Bob's **Terraform IBM Modules MCP** integration to generate and manage Terraform configurations for IBM Cloud deployments.

### Bob IDE Mode
> **Required Mode:** `Advanced`
>
> Ensure you are in **Advanced Mode** before starting this lab. This mode enables MCP usage.

---

## Workshop Flow Overview

1. Enable and configure the Terraform IBM Modules MCP server
2. Generate Terraform configuration for Code Engine deployment
3. Configure deployment variables and secrets
4. Deploy the application to IBM Cloud
5. Verify deployment and access the application
6. Manage and scale the deployment

Each step demonstrates how Bob can streamline cloud deployment workflows through AI-assisted infrastructure management.

---

## Understanding the Deployment Architecture

### What is IBM Code Engine?

IBM Code Engine is a fully managed, serverless platform that runs containerized workloads, including:
- Web applications
- Microservices
- Batch jobs
- Event-driven functions

**Key Benefits:**
- **Serverless**: No infrastructure management required
- **Auto-scaling**: Scales from zero to handle traffic spikes
- **Pay-per-use**: Only pay for resources consumed
- **Built-in CI/CD**: Integrated with IBM Cloud Container Registry
- **Enterprise-ready**: High availability and security

### Deployment Components

Our deployment creates:

```
┌─────────────────────────────────────────────────┐
│         IBM Cloud Code Engine Project           │
│                                                 │
│  ┌───────────────────────────────────────────┐  │
│  │   AltoroMutual Application                │  │
│  │                                           │  │
│  │  • Image: us.icr.io/cr-txv-pd337he2/      │  │
│  │           altoromutual:latest             │  │
│  │  • Port: 8080                             │  │
│  │  • Auto-scaling: 0-2 instances            │  │
│  │  • CPU: 1 vCPU per instance               │  │
│  │  • Memory: 4GB per instance               │  │
│  │  • Public URL endpoint                    │  │
│  └───────────────────────────────────────────┘  │
│                                                 │
│  ┌───────────────────────────────────────────┐  │
│  │   Registry Secret                         │  │
│  │   (Private registry authentication)       │  │
│  └───────────────────────────────────────────┘  │
│                                                 │
│  ┌───────────────────────────────────────────┐  │
│  │   Resource Group                          │  │
│  │   (Logical grouping of resources)         │  │
│  └───────────────────────────────────────────┘  │
└─────────────────────────────────────────────────┘
```

### Docker Image

The AltoroMutual application has been containerized and pushed to IBM Cloud Container Registry:
- **Registry**: `us.icr.io`
- **Namespace**: `cr-txv-pd337he2`
- **Image**: `altoromutual:latest`
- **Full Reference**: `us.icr.io/cr-txv-pd337he2/altoromutual:latest`

This is a **private registry**, requiring authentication via IBM Cloud API key.

---

## Step 1 - Enable Terraform IBM Modules MCP Server

### Why this step?

The **Terraform IBM Modules MCP Server** provides Bob with specialized capabilities for:
- Generating Terraform configurations for IBM Cloud
- Understanding IBM Cloud service requirements
- Providing best practices for infrastructure as code
- Validating Terraform syntax and structure

MCP (Model Context Protocol) servers extend Bob's capabilities by connecting to specialized tools and knowledge bases.

### Instructions

#### Option A: Enable via Bob Settings (Recommended)

1. **Open Bob Settings**
   - Click on the Bob icon in VS Code sidebar
   - Click the gear icon (⚙️) in the top-right corner
   - Or use Command Palette: "Bob: Open Settings"

2. **Navigate to MCP Servers**
   - In the settings panel, find the "MCP Servers" section
   - Look for available MCP servers

3. **Enable Terraform IBM Modules MCP**
   - Find "terraform-ibm-modules-mcp" in the list
   - Toggle the switch to enable it
   - The server will automatically connect

4. **Verify Connection**
   - You should see a green indicator showing the server is connected
   - Bob will now have access to Terraform IBM Modules tools

#### Option B: Manual Configuration

If the MCP server is not available in settings, you can configure it manually:

1. **Open MCP Configuration File**
   - Location: `./.bob/mcp_settings.json` (create if it doesn't exist)

2. **Add Server Configuration**
   ```json
   {
     "mcpServers": {
       "terraform-ibm-modules": {
         "command": "npx",
         "args": ["-y", "@terraform-ibm-modules/mcp-server"],
         "env": {}
       }
     }
   }
   ```

### What the MCP Server Provides

Once enabled, Bob gains access to:
- **Terraform Module Templates**: Pre-built configurations for IBM Cloud services
- **Best Practices**: IBM Cloud deployment patterns and recommendations
- **Validation Tools**: Syntax checking and configuration validation
- **Documentation**: Inline documentation for Terraform resources

---

## Step 2 - Generate Terraform Configuration

### Why this step?

Rather than manually writing Terraform configurations, Bob can generate production-ready infrastructure code based on your requirements. This ensures:
- Correct syntax and structure
- Best practices for IBM Cloud
- Proper resource dependencies
- Security configurations

### Prompt

```
Generate Terraform files to deploy this application on IBM Code Engine. The Docker image has already been pushed to a private registry on IBM Cloud: us.icr.io/cr-txv-pd337he2/altoromutual. 

Requirements:
1. Create a Code Engine project
2. Deploy the application with auto-scaling (0-2 instances)
3. Configure registry authentication for the private image
4. Set up public URL endpoint
5. Configure resource limits: 1 vCPU, 4GB memory per instance
6. Use port 8080 for the application
7. Include all necessary variables and outputs

Save the files in the terraform/ directory.
```

### What Bob Generates

Bob will create the following Terraform files in the `terraform/` directory:

1. **[`provider.tf`](../terraform/provider.tf)** - IBM Cloud provider configuration
2. **[`variables.tf`](../terraform/variables.tf)** - Input variables for customization
3. **[`main.tf`](../terraform/main.tf)** - Core infrastructure resources
4. **[`outputs.tf`](../terraform/outputs.tf)** - Output values after deployment
5. **[`terraform.tfvars.template`](../terraform/terraform.tfvars.template)** - Template for variable values
6. **[`README.md`](../terraform/README.md)** - Deployment documentation

### Key Configuration Elements

#### Provider Configuration ([`provider.tf`](../terraform/provider.tf))
```hcl
provider "ibm" {
  ibmcloud_api_key = var.ibmcloud_api_key
  region           = var.region
}
```

#### Resource Group Module ([`main.tf`](../terraform/main.tf))
```hcl
module "resource_group" {
  source              = "terraform-ibm-modules/resource-group/ibm"
  version             = "1.4.8"
  resource_group_name = "${var.prefix}-resource-group"
}
```

#### Code Engine Module ([`main.tf`](../terraform/main.tf))
```hcl
module "code_engine" {
  source            = "terraform-ibm-modules/code-engine/ibm"
  version           = "4.8.3"
  resource_group_id = module.resource_group.resource_group_id
  project_name      = "${var.prefix}-project"
  
  # Registry secret for private image access
  secrets = {
    "${var.prefix}-registry-secret" = {
      format = "registry"
      data = {
        "username" = "iamapikey"
        "password" = var.ibmcloud_api_key
        "server"   = "us.icr.io"
      }
    }
  }
  
  # Application configuration
  apps = {
    "${var.prefix}-altoromutual" = {
      image_reference         = var.image_reference
      image_secret           = "${var.prefix}-registry-secret"
      image_port             = var.app_port
      scale_min_instances    = var.scale_min_instances
      scale_max_instances    = var.scale_max_instances
      scale_cpu_limit        = var.scale_cpu_limit
      scale_memory_limit     = var.scale_memory_limit
      managed_domain_mappings = "local_public"
    }
  }
}
```

---

## Step 3 - Configure Deployment Variables

### Why this step?

Terraform uses variables to make configurations reusable and secure. You need to provide:
- IBM Cloud API key (for authentication)
- Region selection
- Resource naming preferences
- Scaling parameters

### Instructions

1. **Obtain IBM Cloud API Key** 
   
   This will be provided by your instructor.

2. **Create Configuration File**
   
   ```bash
   cd terraform
   cp terraform.tfvars.template terraform.tfvars
   ```

3. **Edit Configuration**
   
   Open [`terraform.tfvars`](../terraform/terraform.tfvars) and configure:
   
   - `ibmcloud_api_key`: provided by instructor
   - `resource_group`: `txv-itz-69c2a8a4be4b611aff7968` (uncommented)
   - `prefix`: ensure prefix is unique so that you do not have clashing projects with your labmates
   
   ```hcl
   # IBM Cloud API Key (required)
   ibmcloud_api_key = "YOUR_IBM_CLOUD_API_KEY"
   
   # Region where resources will be deployed
   region = "us-south"
   
   # Existing resource group name
   resource_group = "txv-itz-69c2a8a4be4b611aff7968"
   
   # Prefix for resource names (must be unique)
   prefix = "altoromutual-<YOUR_INITIALS>"
   
   # Container image reference
   image_reference = "us.icr.io/cr-txv-pd337he2/altoromutual:latest"
   
   # Registry email for authentication
   registry_email = "your-email@example.com"
   
   # Application port
   app_port = 8080
   
   # Scaling configuration
   scale_min_instances = 0
   scale_max_instances = 2
   scale_cpu_limit     = "1"
   scale_memory_limit  = "4G"
   ```

4. **Verify Configuration**
   
   Ensure:
   - API key is valid and has proper permissions
   - Image reference is correct
   - Resource group is correct
   - Region is correct

---

## Step 4 - Deploy to IBM Cloud

### Why this step?

Now that the configuration is ready, we'll use Terraform to provision the infrastructure and deploy the application to IBM Cloud Code Engine.

### Instructions

#### 1. Initialize Terraform

```bash
cd terraform
terraform init
```

**What this does:**
- Downloads required provider plugins (IBM Cloud provider)
- Downloads Terraform modules (resource-group, code-engine)
- Initializes the backend for state management
- Validates the configuration syntax

**Expected output:**
```
Initializing modules...
Downloading terraform-ibm-modules/resource-group/ibm 1.4.8...
Downloading terraform-ibm-modules/code-engine/ibm 4.8.3...

Initializing the backend...

Initializing provider plugins...
- Finding ibm-cloud/ibm versions matching ">= 1.79.0, < 2.0.0"...
- Installing ibm-cloud/ibm v1.89.0...

Terraform has been successfully initialized!
```

#### 2. Review Deployment Plan

```bash
terraform plan
```

**What this does:**
- Analyzes the current state (if any)
- Compares with desired configuration
- Shows what resources will be created/modified/destroyed
- Validates configuration without making changes

**Expected output:**
```
Terraform will perform the following actions:

  # module.code_engine.ibm_code_engine_app.app["altoromutual-altoromutual"] will be created
  + resource "ibm_code_engine_app" "app" {
      + name                = "altoromutual-altoromutual"
      + image_reference     = "us.icr.io/cr-txv-pd337he2/altoromutual:latest"
      + scale_min_instances = 0
      + scale_max_instances = 2
      ...
    }

  # module.code_engine.ibm_code_engine_project.project will be created
  + resource "ibm_code_engine_project" "project" {
      + name              = "altoromutual-project"
      + resource_group_id = (known after apply)
      ...
    }

Plan: 5 to add, 0 to change, 0 to destroy.
```

**Review the plan carefully:**
- Verify resource names match expectations
- Check scaling parameters
- Confirm image reference is correct
- Ensure region is correct

#### 3. Deploy the Application

```bash
terraform apply
```

**What this does:**
- Creates the resource group
- Creates the Code Engine project
- Creates the registry secret
- Deploys the application
- Configures auto-scaling
- Sets up the public URL

**You will be prompted:**
```
Do you want to perform these actions?
  Terraform will perform the actions described above.
  Only 'yes' will be accepted to approve.

  Enter a value: 
```

Type `yes` and press Enter.

**Deployment progress:**
```
module.resource_group.ibm_resource_group.resource_group: Creating...
module.resource_group.ibm_resource_group.resource_group: Creation complete after 2s

module.code_engine.ibm_code_engine_project.project: Creating...
module.code_engine.ibm_code_engine_project.project: Creation complete after 5s

module.code_engine.ibm_code_engine_secret.secret["altoromutual-registry-secret"]: Creating...
module.code_engine.ibm_code_engine_secret.secret["altoromutual-registry-secret"]: Creation complete after 2s

module.code_engine.ibm_code_engine_app.app["altoromutual-altoromutual"]: Creating...
module.code_engine.ibm_code_engine_app.app["altoromutual-altoromutual"]: Still creating... [10s elapsed]
module.code_engine.ibm_code_engine_app.app["altoromutual-altoromutual"]: Creation complete after 45s

Apply complete! Resources: 5 added, 0 changed, 0 destroyed.

Outputs:

app_url = "https://altoromutual-altoromutual.abcdef123456.us-south.codeengine.appdomain.cloud"
project_id = "12345678-1234-1234-1234-123456789abc"
resource_group_id = "87654321-4321-4321-4321-cba987654321"
```

**Deployment typically takes 2-3 minutes.**

#### 4. Retrieve Application URL

```bash
terraform output app_url
```

**Output:**
```
"https://altoromutual-altoromutual.abcdef123456.us-south.codeengine.appdomain.cloud"
```

---

## Step 5 - Verify Deployment

### Why this step?

After deployment, it's important to verify that:
- The application is running correctly
- The URL is accessible
- Auto-scaling is configured properly
- Resources are allocated as expected

### Instructions

#### 1. Access the Application

Open the application URL in your browser:

```bash
# Get the URL
terraform output -raw app_url

# Or open directly (macOS)
open $(terraform output -raw app_url)

# Or open directly (Linux)
xdg-open $(terraform output -raw app_url)
```

**Expected result:**
- AltoroMutual login page loads
- Application is responsive
- No error messages

#### 2. Test Application Functionality

**Login with test credentials:**
- Username: `jsmith`
- Password: `demo1234`

**Verify key features:**
- ✅ Login successful
- ✅ Account balances display
- ✅ Transaction history loads
- ✅ Transfer functionality works

#### 3. Check Deployment Status via IBM Cloud Console

1. **Navigate to Code Engine**
   - Go to [IBM Cloud Console](https://cloud.ibm.com)
   - Click **Navigation Menu** → **Code Engine**

2. **Select Your Project**
   - Find "altoromutual-project" (or your custom prefix)
   - Click to open

3. **View Application Details**
   - Click **Applications** tab
   - Select "altoromutual-altoromutual"
   - Review:
     - **Status**: Should show "Ready"
     - **URL**: Public endpoint
     - **Instances**: Current running instances
     - **CPU/Memory**: Resource usage

4. **Check Application Logs**
   - Click **Logs** tab
   - View application startup logs
   - Check for any errors or warnings

#### 4. Verify Auto-Scaling Configuration

```bash
# View all outputs
terraform output

# Check scaling configuration
terraform show | grep scale
```

**Expected configuration:**
```
scale_min_instances    = 1
scale_max_instances    = 10
scale_cpu_limit        = "1"
scale_memory_limit     = "4G"
scale_concurrency      = 100
```

#### 5. Test Auto-Scaling (Optional)

To test auto-scaling behavior, you can generate load:

```bash
# Install Apache Bench (if not already installed)
# macOS: brew install httpd
# Ubuntu: sudo apt-get install apache2-utils

# Generate load (100 requests, 10 concurrent)
ab -n 100 -c 10 $(terraform output -raw app_url)
```

**Monitor in IBM Cloud Console:**
- Watch the **Instances** count increase under load
- Observe CPU and memory metrics
- See instances scale down after load decreases

---

## Step 6 - Delete the Deployment

### Destroy the Deployment

When you no longer need the deployment:

```bash
terraform destroy
```

**You will be prompted:**
```
Do you really want to destroy all resources?
  Terraform will destroy all your managed infrastructure.
  There is no undo. Only 'yes' will be accepted to confirm.

  Enter a value:
```

Type `yes` to confirm.

**This will:**
- Delete the Code Engine application
- Delete the registry secret
- Delete the Code Engine project

**⚠️ Warning:** This action is irreversible. Ensure you have backups of any important data.

---

## Troubleshooting

### Terraform Apply Errors

**Symptoms:**
- `terraform apply` fails with cryptic error messages
- Resources fail to create
- Timeout errors during deployment
- Authentication or permission errors

**Solutions:**

1. **Enable debug logging to see detailed error information:**
   ```bash
   # Set debug logging level
   export TF_LOG=DEBUG
   
   # Run terraform apply with debug output
   terraform apply
   
   # Or combine in one command
   TF_LOG=DEBUG terraform apply
   ```
   
2. **Disable debug logging after troubleshooting:**
   ```bash
   unset TF_LOG
   ```

### Application Not Starting

**Symptoms:**
- Application status shows "Failed" or "Unknown"
- URL returns 502/503 errors
- Logs show container errors

**Solutions:**

1. **Check application logs:**
   ```bash
   ibmcloud login --apikey <API_KEY>
   ibmcloud target -g <RESOURCE_GROUP>
   ibmcloud ce project select --name altoromutual-<INITIALS>-project
   ibmcloud ce app logs --name altoromutual-<INITIALS>-altoromutual
   ```

2. **Check application status with debug info:**
   ```bash
   ibmcloud ce app get --name altoromutual-<INITIALS>-altoromutual
   ```

3. **Verify the image is accessible:**
   ```bash
   ibmcloud cr image-list --restrict cr-txv-pd337he2
   ```

### Terraform State Issues

**Symptoms:**
- "Resource already exists" errors
- State drift warnings

**Solutions:**

1. **Refresh state:**
   ```bash
   terraform refresh
   ```

---

## Expected Outcomes

By the end of this lab, you should have:

1. **Deployed Application**
   - AltoroMutual running on IBM Code Engine
   - Accessible via public URL
   - Auto-scaling configured (0-2 instances)
   - Private registry authentication working

2. **Infrastructure as Code**
   - Complete Terraform configuration
   - Version-controlled infrastructure
   - Reusable deployment templates
   - Documented deployment process

3. **Understanding of Bob's Capabilities**
   - How Bob leverages MCP servers for specialized tasks
   - How Bob generates production-ready Terraform code
   - How Bob assists with cloud deployment workflows
   - How to use Bob for infrastructure management

4. **Practical Cloud Skills**
   - Deploying containerized applications to IBM Cloud
   - Managing Code Engine resources
   - Configuring auto-scaling and resource limits
   - Troubleshooting cloud deployments
   - Using Terraform for infrastructure management

---

## Key Takeaways

### Bob's AI-Assisted Workflow

1. **MCP Integration**
   - Extends Bob's capabilities
   - Provides specialized knowledge
   - Enables tool integration

2. **Code Generation**
   - Production-ready configurations
   - Best practices built-in
   - Reduced manual effort

3. **Conversational Infrastructure**
   - Natural language to IaC
   - Interactive troubleshooting
   - Guided deployment process

---

## Additional Resources

### IBM Cloud Documentation
- [IBM Code Engine](https://cloud.ibm.com/docs/codeengine)
- [IBM Cloud Container Registry](https://cloud.ibm.com/docs/Registry)
- [IBM Cloud CLI](https://cloud.ibm.com/docs/cli)

### Terraform Resources
- [Terraform IBM Provider](https://registry.terraform.io/providers/IBM-Cloud/ibm/latest/docs)
- [Terraform IBM Modules - Code Engine](https://github.com/terraform-ibm-modules/terraform-ibm-code-engine)
- [Terraform Best Practices](https://www.terraform.io/docs/cloud/guides/recommended-practices/index.html)

### MCP Resources
- [Model Context Protocol](https://modelcontextprotocol.io/)
- [Terraform IBM Modules MCP Server](https://github.com/terraform-ibm-modules/mcp-server)

---

## Summary

This lab demonstrated IBM Bob's capabilities for cloud deployment automation:

**What We Accomplished:**
- ✅ Enabled Terraform IBM Modules MCP server
- ✅ Generated production-ready Terraform configuration
- ✅ Deployed AltoroMutual to IBM Code Engine
- ✅ Configured auto-scaling and resource management
- ✅ Verified deployment and functionality
- ✅ Learned deployment management and troubleshooting

**Key Technologies:**
- **IBM Code Engine**: Serverless container platform
- **Terraform**: Infrastructure as Code tool
- **IBM Cloud Container Registry**: Private Docker registry
- **MCP**: Model Context Protocol for AI tool integration
- **Bob**: AI-powered development assistant

**Deployment Metrics:**
- **Setup Time**: ~10 minutes (with Bob's assistance)
- **Deployment Time**: ~3 minutes (automated)
- **Resources Created**: 5 (resource group, project, secret, app, domain)
- **Cost**: Pay-per-use (scales to zero when idle)

**Business Value:**
- **Faster Deployments**: Minutes instead of hours
- **Reduced Errors**: AI-generated, validated configurations
- **Better Documentation**: Self-documenting infrastructure code
- **Improved Collaboration**: Version-controlled infrastructure
- **Cost Optimization**: Auto-scaling and serverless pricing

---

## Congratulations! 🎉

You've completed the IBM Bob Workshop series:

1. ✅ **Part 1**: Architecture Analysis and Documentation
2. ✅ **Part 2**: Java Modernization (1.7 → 21)
3. ✅ **Part 3**: Security Review and Remediation
4. ✅ **Part 4**: Cloud Deployment with Terraform

You now have hands-on experience with:
- AI-assisted code analysis and documentation
- Agentic modernization workflows
- Security vulnerability detection and remediation
- Infrastructure as Code with Terraform
- Cloud deployment to IBM Code Engine
- MCP server integration for specialized tasks

**Next Steps:**
- Explore other Bob modes and capabilities
- Try deploying your own applications
- Experiment with different MCP servers
- Share your experience with your team

Thank you for participating in the IBM Bob Workshop!