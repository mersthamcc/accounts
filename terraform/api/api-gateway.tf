resource "aws_api_gateway_domain_name" "api" {
  regional_certificate_arn = aws_acm_certificate_validation.api.certificate_arn
  domain_name              = "api.${data.terraform_remote_state.accounts_core_state.outputs.accounting_zone_name}"

  security_policy = "TLS_1_2"

  endpoint_configuration {
    types = ["REGIONAL"]
  }

  depends_on = [
    aws_acm_certificate_validation.api
  ]
}

resource "aws_api_gateway_rest_api" "api" {
  name = "${var.environment}-accounting-http-api"
}

resource "aws_api_gateway_deployment" "deployment" {
  rest_api_id = aws_api_gateway_rest_api.api.id

  triggers = {
    redeployment = sha1(jsonencode([
      module.sage_endpoint.integration_trigger_value,
      module.sage_callback_endpoint.integration_trigger_value,
      module.end_of_day_endpoint.integration_trigger_value,
      module.purge_endpoint.integration_trigger_value,
    ]))
  }

  lifecycle {
    create_before_destroy = true
  }
  depends_on = [
    module.sage_endpoint,
    module.sage_callback_endpoint,
    module.end_of_day_endpoint,
    module.purge_endpoint,
  ]
}

resource "aws_api_gateway_stage" "api_stage" {
  deployment_id = aws_api_gateway_deployment.deployment.id
  rest_api_id   = aws_api_gateway_rest_api.api.id
  stage_name    = var.environment

  depends_on = [
    module.sage_endpoint,
    module.sage_callback_endpoint,
    module.end_of_day_endpoint,
    module.purge_endpoint,
    aws_api_gateway_deployment.deployment,
  ]
}

resource "aws_api_gateway_base_path_mapping" "api" {
  api_id      = aws_api_gateway_rest_api.api.id
  stage_name  = aws_api_gateway_stage.api_stage.stage_name
  domain_name = aws_api_gateway_domain_name.api.domain_name
}
