##############################################################################
# Input Variables
##############################################################################

variable "ibmcloud_api_key" {
  type        = string
  description = "The IBM Cloud API key used for authentication and registry access"
  sensitive   = true
}

variable "region" {
  type        = string
  description = "IBM Cloud region where resources will be deployed"
  default     = "us-south"
  validation {
    condition     = can(regex("^(us-south|us-east|eu-gb|eu-de|jp-tok|au-syd|jp-osa|ca-tor|br-sao)$", var.region))
    error_message = "Region must be a valid IBM Cloud region."
  }
}

variable "prefix" {
  type        = string
  description = "Prefix to append to all resource names"
  default     = "altoromutual"
  validation {
    condition     = can(regex("^[a-z][-a-z0-9]*$", var.prefix))
    error_message = "Prefix must start with a lowercase letter and contain only lowercase letters, numbers, and hyphens."
  }
}

variable "resource_group_name" {
  type        = string
  description = "Name of existing resource group to use. If null, a new resource group will be created"
  default     = null
}

##############################################################################
# Container Registry Variables
##############################################################################

variable "image_reference" {
  type        = string
  description = "Full image reference including registry, namespace, and tag"
  default     = "us.icr.io/cr-txv-pd337he2/altoromutual:latest"
}

variable "registry_server" {
  type        = string
  description = "Container registry server URL"
  default     = "us.icr.io"
}

variable "registry_email" {
  type        = string
  description = "Email address for registry authentication"
  default     = "iamapikey@ibm.com"
}

##############################################################################
# Application Configuration
##############################################################################

variable "app_port" {
  type        = number
  description = "Port on which the application listens"
  default     = 8080
}

variable "app_env_variables" {
  type = list(object({
    type      = optional(string)
    name      = optional(string)
    value     = optional(string)
    prefix    = optional(string)
    key       = optional(string)
    reference = optional(string)
  }))
  description = "List of environment variables for the application"
  default     = []
}

##############################################################################
# Auto-scaling Configuration
##############################################################################

variable "scale_min_instances" {
  type        = number
  description = "Minimum number of instances (0 for scale-to-zero)"
  default     = 0
  validation {
    condition     = var.scale_min_instances >= 0 && var.scale_min_instances <= 250
    error_message = "Minimum instances must be between 0 and 250."
  }
}

variable "scale_max_instances" {
  type        = number
  description = "Maximum number of instances"
  default     = 2
  validation {
    condition     = var.scale_max_instances >= 1 && var.scale_max_instances <= 250
    error_message = "Maximum instances must be between 1 and 250."
  }
}

variable "scale_cpu_limit" {
  type        = string
  description = "CPU limit per instance (e.g., '1', '2', '4')"
  default     = "1"
  validation {
    condition     = can(regex("^(0\\.125|0\\.25|0\\.5|1|2|4|6|8)$", var.scale_cpu_limit))
    error_message = "CPU limit must be one of: 0.125, 0.25, 0.5, 1, 2, 4, 6, or 8."
  }
}

variable "scale_memory_limit" {
  type        = string
  description = "Memory limit per instance (e.g., '4G', '8G', '16G')"
  default     = "4G"
  validation {
    condition     = can(regex("^[0-9]+(M|G)$", var.scale_memory_limit))
    error_message = "Memory limit must be specified in M (megabytes) or G (gigabytes), e.g., '4G' or '512M'."
  }
}

variable "scale_ephemeral_storage_limit" {
  type        = string
  description = "Ephemeral storage limit per instance"
  default     = "400M"
  validation {
    condition     = can(regex("^[0-9]+(M|G)$", var.scale_ephemeral_storage_limit))
    error_message = "Ephemeral storage limit must be specified in M (megabytes) or G (gigabytes)."
  }
}

variable "scale_concurrency" {
  type        = number
  description = "Maximum number of concurrent requests per instance"
  default     = 100
  validation {
    condition     = var.scale_concurrency >= 0 && var.scale_concurrency <= 1000
    error_message = "Concurrency must be between 0 and 1000."
  }
}

variable "scale_request_timeout" {
  type        = number
  description = "Request timeout in seconds"
  default     = 300
  validation {
    condition     = var.scale_request_timeout >= 1 && var.scale_request_timeout <= 600
    error_message = "Request timeout must be between 1 and 600 seconds."
  }
}