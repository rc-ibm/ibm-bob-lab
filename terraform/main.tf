##############################################################################
# Terraform Configuration for AltoroMutual on IBM Code Engine
##############################################################################

terraform {
  required_version = ">= 1.9.0"
  required_providers {
    ibm = {
      source  = "IBM-Cloud/ibm"
      version = ">= 1.79.0, < 2.0.0"
    }
  }
}

##############################################################################
# Provider Configuration
##############################################################################

provider "ibm" {
  ibmcloud_api_key = var.ibmcloud_api_key
  region           = var.region
}

##############################################################################
# Resource Group
##############################################################################

module "resource_group" {
  source  = "terraform-ibm-modules/resource-group/ibm"
  version = "1.4.8"
  # Use existing resource group or create new one
  resource_group_name          = var.resource_group_name == null ? "${var.prefix}-resource-group" : null
  existing_resource_group_name = var.resource_group_name
}

##############################################################################
# Code Engine Project and Application
##############################################################################

module "code_engine" {
  source            = "terraform-ibm-modules/code-engine/ibm"
  version           = "4.8.3"
  resource_group_id = module.resource_group.resource_group_id
  project_name      = "${var.prefix}-project"

  # Create registry secret for private IBM Container Registry
  secrets = {
    "${var.prefix}-registry-secret" = {
      format = "registry"
      data = {
        "username" = "iamapikey"
        "password" = var.ibmcloud_api_key
        "server"   = var.registry_server
        "email"    = var.registry_email
      }
    }
  }

  # Deploy AltoroMutual application
  apps = {
    "${var.prefix}-app" = {
      image_reference = var.image_reference
      image_secret    = "${var.prefix}-registry-secret"
      image_port      = var.app_port

      # Auto-scaling configuration
      scale_min_instances           = var.scale_min_instances
      scale_max_instances           = var.scale_max_instances
      scale_cpu_limit               = var.scale_cpu_limit
      scale_memory_limit            = var.scale_memory_limit
      scale_ephemeral_storage_limit = var.scale_ephemeral_storage_limit
      scale_concurrency             = var.scale_concurrency
      scale_request_timeout         = var.scale_request_timeout

      # Public endpoint configuration
      managed_domain_mappings = "local_public"

      # Optional environment variables
      run_env_variables = var.app_env_variables
    }
  }
}