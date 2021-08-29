resource "aws_lambda_function" "match_fee_lambda" {

  s3_bucket         = data.aws_s3_bucket_object.lambda_source.bucket
  s3_key            = data.aws_s3_bucket_object.lambda_source.key
  s3_object_version = data.aws_s3_bucket_object.lambda_source.version_id

  function_name = "${var.environment_name}-match-fee-transfer-schedule"
  role          = aws_iam_role.match_fee_lambda_iam_role.arn
  handler       = "cricket.merstham.website.accounts.lambda.MatchFeeTransferScheduled::handleRequest"
  timeout       = 300
  memory_size   = 1024
  publish       = true

  environment {
    variables = {
      CONFIG_NAME = var.environment_name
    }
  }

  runtime = "java11"
}

resource "aws_cloudwatch_log_group" "lambda_log_group" {
  name              = "/aws/lambda/${aws_lambda_function.match_fee_lambda.function_name}"
  retention_in_days = 90
}

resource "aws_cloudwatch_event_rule" "match_fee_schedule" {
  name                = "${var.environment_name}-match-fee-transfer-schedule"
  schedule_expression = "cron(0 6 ? * FRI *)"
  is_enabled          = true
}

resource "aws_cloudwatch_event_target" "match_fee_schedule_target" {
  arn       = aws_lambda_function.match_fee_lambda.arn
  rule      = aws_cloudwatch_event_rule.match_fee_schedule.name
  target_id = aws_lambda_function.match_fee_lambda.version
}

resource "aws_lambda_permission" "allow_cloudwatch_to_call_match_fee_lambda" {
  statement_id  = "AllowExecutionFromCloudWatch"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.match_fee_lambda.function_name
  principal     = "events.amazonaws.com"
  source_arn    = aws_cloudwatch_event_rule.match_fee_schedule.arn
}