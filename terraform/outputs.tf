##############################################################################
# Outputs
##############################################################################

output "project_id" {
  description = "ID of the Code Engine project"
  value       = module.code_engine.project_id
}

output "project_name" {
  description = "Name of the Code Engine project"
  value       = "${var.prefix}-project"
}

output "app_name" {
  description = "Name of the deployed application"
  value       = "${var.prefix}-app"
}

output "app_url" {
  description = "Public URL of the deployed application"
  value       = try(module.code_engine.app["${var.prefix}-app"].endpoint, "URL not yet available")
}

output "app_status" {
  description = "Status of the deployed application"
  value       = try(module.code_engine.app["${var.prefix}-app"].status, "Status not yet available")
}

output "app_internal_url" {
  description = "Internal URL of the deployed application (within cluster)"
  value       = try(module.code_engine.app["${var.prefix}-app"].endpoint_internal, "Internal URL not yet available")
}

output "resource_group_id" {
  description = "ID of the resource group"
  value       = module.resource_group.resource_group_id
}

output "resource_group_name" {
  description = "Name of the resource group"
  value       = module.resource_group.resource_group_name
}

output "region" {
  description = "Region where resources are deployed"
  value       = var.region
}

output "registry_secret_name" {
  description = "Name of the registry secret for private image access"
  value       = "${var.prefix}-registry-secret"
}

output "scale_configuration" {
  description = "Auto-scaling configuration"
  value = {
    min_instances = var.scale_min_instances
    max_instances = var.scale_max_instances
    cpu_limit     = var.scale_cpu_limit
    memory_limit  = var.scale_memory_limit
  }
}