module "match_fee_transfer_endpoint" {
  source = "./modules/endpoint-lambda"

  s3_bucket  = data.aws_s3_bucket_object.lambda_source.bucket
  s3_key     = data.aws_s3_bucket_object.lambda_source.key
  s3_version = data.aws_s3_bucket_object.lambda_source.version_id

  rest_api_id           = aws_api_gateway_rest_api.api.id
  root_resource_id      = aws_api_gateway_rest_api.api.root_resource_id
  execution_arn         = aws_api_gateway_rest_api.api.execution_arn
  endpoint_name         = "match-fee-transfer"
  endpoint_method       = "POST"
  environment_name      = var.environment
  handler_function_name = "cricket.merstham.website.accounts.lambda.MatchFeeTransfer::handleRequest"
  lambda_role_arn       = aws_iam_role.match_fee_lambda_iam_role.arn
  api_key_required      = true

  handler_environment_variables = {
    CONFIG_NAME = var.environment
  }
}

