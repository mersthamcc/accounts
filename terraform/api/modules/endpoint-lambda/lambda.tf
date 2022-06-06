resource "aws_lambda_function" "endpoint_lambda" {

  s3_bucket         = var.s3_bucket
  s3_key            = var.s3_key
  s3_object_version = var.s3_version

  function_name = "${var.environment_name}-${replace("${var.endpoint_name}-lambda", ".", "")}"
  role          = var.lambda_role_arn
  handler       = var.handler_function_name
  timeout       = var.timeout
  memory_size   = 3072

  environment {
    variables = merge(var.handler_environment_variables, {
      JAVA_TOOL_OPTIONS = "-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
    })
  }

  runtime = var.handler_runtime
}

resource "aws_cloudwatch_log_group" "lambda_log_group" {
  name              = "/aws/lambda/${aws_lambda_function.endpoint_lambda.function_name}"
  retention_in_days = 90

  depends_on = [
    aws_lambda_function.endpoint_lambda
  ]
}