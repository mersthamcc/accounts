output "integration_trigger_value" {
  value = jsonencode(aws_api_gateway_integration.endpoint_integration)
}
