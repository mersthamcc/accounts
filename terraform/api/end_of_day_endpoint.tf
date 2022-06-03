resource "random_password" "end_of_day_username" {
  length  = 32
  special = false
  number  = false
}

resource "random_password" "end_of_day_password" {
  length  = 32
  special = false
  number  = false
}

module "end_of_day_endpoint" {
  source = "./modules/endpoint-lambda"

  s3_bucket  = data.aws_s3_bucket_object.lambda_source.bucket
  s3_key     = data.aws_s3_bucket_object.lambda_source.key
  s3_version = data.aws_s3_bucket_object.lambda_source.version_id

  rest_api_id           = aws_api_gateway_rest_api.api.id
  root_resource_id      = aws_api_gateway_rest_api.api.root_resource_id
  execution_arn         = aws_api_gateway_rest_api.api.execution_arn
  endpoint_name         = "end-of-day"
  endpoint_method       = "POST"
  environment_name      = var.environment
  handler_function_name = "cricket.merstham.website.accounts.lambda.EndOfDay::handleRequest"
  lambda_role_arn       = aws_iam_role.end_of_day_endpoint_lambda_iam_role.arn
  timeout               = 300
  handler_environment_variables = {
    CONFIG_NAME = var.environment
    END_OF_DAY_AUTH = base64encode(join(":", [
      random_password.end_of_day_username.result,
      random_password.end_of_day_password.result
      ]
    ))
    PROCESSING_LAMBDA_ARN = aws_lambda_function.process_end_of_day_lambda.arn
  }
}

