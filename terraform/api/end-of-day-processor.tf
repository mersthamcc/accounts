resource "aws_lambda_function" "process_end_of_day_lambda" {
  s3_bucket         = data.aws_s3_bucket_object.lambda_source.bucket
  s3_key            = data.aws_s3_bucket_object.lambda_source.key
  s3_object_version = data.aws_s3_bucket_object.lambda_source.version_id

  function_name = "${var.environment}-end-of-day-processor"
  role          = aws_iam_role.end_of_day_lambda_iam_role.arn
  handler       = "cricket.merstham.website.accounts.lambda.ProcessEndOfDay::handleRequest"
  timeout       = 300
  memory_size   = 3072

  environment {
    variables = {
      CONFIG_NAME       = var.environment
      JAVA_TOOL_OPTIONS = "-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
    }
  }

  runtime = "java11"
}

resource "aws_cloudwatch_log_group" "process_end_of_day_lambda_log_group" {
  name              = "/aws/lambda/${aws_lambda_function.process_end_of_day_lambda.function_name}"
  retention_in_days = 90

  depends_on = [
    aws_lambda_function.process_end_of_day_lambda
  ]
}