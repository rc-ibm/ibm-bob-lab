# AltoroMutual on IBM Code Engine - Terraform Deployment

This Terraform configuration deploys the AltoroMutual application on IBM Cloud Code Engine with auto-scaling and private registry authentication.

## Architecture

The deployment creates:
- **Code Engine Project**: A serverless container platform project
- **Code Engine Application**: The AltoroMutual web application
- **Registry Secret**: Authentication for private IBM Container Registry
- **Resource Group**: Logical container for all resources (optional)
- **Auto-scaling**: 0-2 instances with scale-to-zero capability
- **Public Endpoint**: Automatically provisioned public URL

## Prerequisites

1. **IBM Cloud Account**: Active IBM Cloud account
2. **IBM Cloud API Key**: Create at https://cloud.ibm.com/iam/apikeys
3. **Terraform**: Version >= 1.9.0
4. **Docker Image**: Already pushed to `us.icr.io/cr-txv-pd337he2/altoromutual`

## Quick Start

### 1. Configure Variables

Copy the template and fill in your values:

```bash
cp terraform.tfvars.template terraform.tfvars
```

Edit `terraform.tfvars` and set your IBM Cloud API key:

```hcl
ibmcloud_api_key = "YOUR_IBM_CLOUD_API_KEY"
```

### 2. Initialize Terraform

```bash
terraform init
```

### 3. Review the Plan

```bash
terraform plan
```

### 4. Deploy

```bash
terraform apply
```

Type `yes` when prompted to confirm the deployment.

### 5. Access the Application

After deployment completes, get the application URL:

```bash
terraform output app_url
```

Visit the URL in your browser to access AltoroMutual.

## Configuration

### Resource Limits

The application is configured with:
- **CPU**: 1 vCPU per instance
- **Memory**: 4GB per instance
- **Storage**: 400MB ephemeral storage
- **Port**: 8080

### Auto-scaling

- **Minimum instances**: 0 (scale-to-zero enabled)
- **Maximum instances**: 2
- **Concurrency**: 100 requests per instance
- **Request timeout**: 300 seconds

### Registry Authentication

The configuration automatically creates a registry secret using your IBM Cloud API key to authenticate with the private IBM Container Registry.

## Customization

### Change Resource Limits

Edit `terraform.tfvars`:

```hcl
scale_cpu_limit    = "2"    # 2 vCPUs
scale_memory_limit = "8G"   # 8GB memory
```

### Adjust Auto-scaling

```hcl
scale_min_instances = 1     # Always keep 1 instance running
scale_max_instances = 5     # Scale up to 5 instances
```

### Add Environment Variables

```hcl
app_env_variables = [
  {
    type  = "literal"
    name  = "JAVA_OPTS"
    value = "-Xmx2g -Xms512m"
  },
  {
    type  = "literal"
    name  = "LOG_LEVEL"
    value = "INFO"
  }
]
```

### Use Different Region

```hcl
region = "eu-gb"  # London
```

Available regions: `us-south`, `us-east`, `eu-gb`, `eu-de`, `jp-tok`, `au-syd`, `jp-osa`, `ca-tor`, `br-sao`

## Outputs

After deployment, the following outputs are available:

```bash
# Get all outputs
terraform output

# Get specific output
terraform output app_url
terraform output project_id
```

Available outputs:
- `app_url`: Public URL of the application
- `project_id`: Code Engine project ID
- `project_name`: Code Engine project name
- `app_name`: Application name
- `app_status`: Application readiness status
- `resource_group_id`: Resource group ID
- `resource_group_name`: Resource group name
- `region`: Deployment region
- `registry_secret_name`: Name of the registry secret
- `scale_configuration`: Auto-scaling settings

## Managing the Deployment

### View Application Logs

```bash
ibmcloud ce app logs --name altoromutual-app --follow
```

### Check Application Status

```bash
ibmcloud ce app get --name altoromutual-app
```

### Update the Application

1. Modify `terraform.tfvars` with new settings
2. Run `terraform apply`

### Scale the Application

Update scaling parameters in `terraform.tfvars`:

```hcl
scale_min_instances = 2
scale_max_instances = 10
```

Then apply:

```bash
terraform apply
```

### Destroy Resources

To remove all resources:

```bash
terraform destroy
```

Type `yes` when prompted to confirm deletion.

## Troubleshooting

### Image Pull Errors

If the application fails to start with image pull errors:

1. Verify the API key has access to the registry:
   ```bash
   ibmcloud cr login
   ibmcloud cr images --restrict cr-txv-pd337he2
   ```

2. Check the registry secret:
   ```bash
   ibmcloud ce secret get --name altoromutual-registry-secret
   ```

### Application Not Starting

1. Check application logs:
   ```bash
   ibmcloud ce app logs --name altoromutual-app
   ```

2. Verify resource limits are sufficient:
   ```bash
   ibmcloud ce app get --name altoromutual-app
   ```

3. Ensure port 8080 is correct for your application

### Scale-to-Zero Issues

If the application doesn't scale to zero:

1. Check if there's active traffic
2. Verify `scale_min_instances = 0` in configuration
3. Wait for the scale-down delay period

## Cost Optimization

Code Engine charges based on:
- **vCPU-seconds**: CPU usage while running
- **GB-seconds**: Memory usage while running
- **Requests**: Number of HTTP requests

With scale-to-zero enabled (`scale_min_instances = 0`), you only pay when the application is actively serving requests.

## Security Considerations

1. **API Key**: Store your API key securely, never commit `terraform.tfvars` to version control
2. **Registry Access**: The registry secret uses your API key for authentication
3. **Public Endpoint**: The application is publicly accessible by default
4. **Network Isolation**: Consider using VPC integration for production deployments

## Additional Resources

- [IBM Code Engine Documentation](https://cloud.ibm.com/docs/codeengine)
- [Terraform IBM Provider](https://registry.terraform.io/providers/IBM-Cloud/ibm/latest/docs)
- [IBM Container Registry](https://cloud.ibm.com/docs/Registry)
- [Code Engine Pricing](https://cloud.ibm.com/docs/codeengine?topic=codeengine-pricing)

## Support

For issues with:
- **Terraform configuration**: Check the [terraform-ibm-modules/code-engine](https://github.com/terraform-ibm-modules/terraform-ibm-code-engine) repository
- **IBM Cloud services**: Contact IBM Cloud Support
- **AltoroMutual application**: Check the application repository