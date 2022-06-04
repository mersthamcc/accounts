data "aws_iam_policy_document" "assume_role_policy" {
  version = "2012-10-17"
  statement {
    actions = [
      "sts:AssumeRole"
    ]
    principals {
      identifiers = [
        "lambda.amazonaws.com"
      ]
      type = "Service"
    }
    effect = "Allow"
  }
}

resource "aws_iam_role" "lambda_iam_role" {
  name_prefix = "common-lambda-role-"
  path        = "/${var.environment}/lambda/"

  assume_role_policy = data.aws_iam_policy_document.assume_role_policy.json
}

data "aws_iam_policy_document" "logging_policy" {
  version = "2012-10-17"
  statement {
    actions = [
      "logs:CreateLogStream",
      "logs:PutLogEvents",
    ]
    resources = [
      "arn:aws:logs:*:*:*"
    ]
    effect = "Allow"
  }
}

resource "aws_iam_policy" "endpoint_logging_policy" {
  name_prefix = "common-lambda-logging-"
  path        = "/${var.environment}/lambda/"
  description = "IAM policy for logging from a lambda"
  policy      = data.aws_iam_policy_document.logging_policy.json
}

resource "aws_iam_role_policy_attachment" "lambda_logs" {
  role       = aws_iam_role.lambda_iam_role.name
  policy_arn = aws_iam_policy.endpoint_logging_policy.arn
}

data "aws_iam_policy_document" "dynamo_policy_document" {
  statement {
    sid    = "AllowAccessToDynamoTables"
    effect = "Allow"
    actions = [
      "dynamodb:BatchGetItem",
      "dynamodb:DescribeStream",
      "dynamodb:DescribeTable",
      "dynamodb:Get*",
      "dynamodb:Query",
      "dynamodb:Scan",
      "dynamodb:BatchWriteItem",
      "dynamodb:UpdateItem",
      "dynamodb:PutItem",
    ]
    resources = [
      aws_dynamodb_table.config.arn,
      aws_dynamodb_table.token.arn,
    ]
  }
}

resource "aws_iam_policy" "lambda_dynamo_policy" {
  name_prefix = "common-lambda-dynamo-policy-"
  path        = "/${var.environment}/lambda/"
  description = "IAM policy for managing Dynamo connection for a lambda"
  policy      = data.aws_iam_policy_document.dynamo_policy_document.json
}

resource "aws_iam_role_policy_attachment" "lambda_dynamo" {
  role       = aws_iam_role.lambda_iam_role.name
  policy_arn = aws_iam_policy.lambda_dynamo_policy.arn
}

resource "aws_iam_role" "end_of_day_lambda_iam_role" {
  name_prefix = "end-of-day-lambda-role-"
  path        = "/${var.environment}/lambda/"

  assume_role_policy = data.aws_iam_policy_document.assume_role_policy.json
}

resource "aws_iam_role_policy_attachment" "end_of_day_lambda_dynamo" {
  role       = aws_iam_role.end_of_day_lambda_iam_role.name
  policy_arn = aws_iam_policy.lambda_dynamo_policy.arn
}

resource "aws_iam_role_policy_attachment" "end_of_day_lambda_logs" {
  role       = aws_iam_role.end_of_day_lambda_iam_role.name
  policy_arn = aws_iam_policy.endpoint_logging_policy.arn
}

data "aws_iam_policy_document" "audit_dynamo_policy_document" {
  statement {
    sid    = "AllowAccessToDynamoTables"
    effect = "Allow"
    actions = [
      "dynamodb:BatchWriteItem",
      "dynamodb:UpdateItem",
      "dynamodb:GetItem",
      "dynamodb:PutItem",
    ]
    resources = [
      aws_dynamodb_table.audit.arn,
      aws_dynamodb_table.error.arn,
    ]
  }
}

resource "aws_iam_policy" "lambda_audit_dynamo_policy" {
  name_prefix = "audit-lambda-dynamo-policy-"
  path        = "/${var.environment}/lambda/"
  description = "IAM policy for managing audit Dynamo table connection for a lambda"
  policy      = data.aws_iam_policy_document.audit_dynamo_policy_document.json
}

resource "aws_iam_role" "queue_processor_lambda_iam_role" {
  name_prefix = "queue-processor-lambda-role-"
  path        = "/${var.environment}/lambda/"

  assume_role_policy = data.aws_iam_policy_document.assume_role_policy.json
}

resource "aws_iam_role_policy_attachment" "queue_processor_lambda_dynamo" {
  role       = aws_iam_role.queue_processor_lambda_iam_role.name
  policy_arn = aws_iam_policy.lambda_dynamo_policy.arn
}

resource "aws_iam_role_policy_attachment" "queue_processor_lambda_logs" {
  role       = aws_iam_role.queue_processor_lambda_iam_role.name
  policy_arn = aws_iam_policy.endpoint_logging_policy.arn
}

resource "aws_iam_role_policy_attachment" "queue_processor_lambda_audit_dynamo" {
  role       = aws_iam_role.queue_processor_lambda_iam_role.name
  policy_arn = aws_iam_policy.lambda_audit_dynamo_policy.arn
}

resource "aws_iam_role" "match_fee_lambda_iam_role" {
  name_prefix        = "match-fee-lambda-role-"
  path               = "/${var.environment}/lambda/"
  assume_role_policy = data.aws_iam_policy_document.assume_role_policy.json
}

resource "aws_iam_role_policy_attachment" "match_fee_lambda_dynamo" {
  role       = aws_iam_role.match_fee_lambda_iam_role.name
  policy_arn = aws_iam_policy.lambda_dynamo_policy.arn
}

resource "aws_iam_role_policy_attachment" "match_fee_lambda_logs" {
  role       = aws_iam_role.match_fee_lambda_iam_role.name
  policy_arn = aws_iam_policy.endpoint_logging_policy.arn
}

resource "aws_iam_role" "end_of_day_endpoint_lambda_iam_role" {
  name_prefix = "end-of-day-lambda-role-"
  path        = "/${var.environment}/lambda/"

  assume_role_policy = data.aws_iam_policy_document.assume_role_policy.json
}

resource "aws_iam_role_policy_attachment" "end_of_day_endpoint_lambda_dynamo" {
  role       = aws_iam_role.end_of_day_endpoint_lambda_iam_role.name
  policy_arn = aws_iam_policy.lambda_dynamo_policy.arn
}

resource "aws_iam_role_policy_attachment" "end_of_day_endpoint_lambda_logs" {
  role       = aws_iam_role.end_of_day_endpoint_lambda_iam_role.name
  policy_arn = aws_iam_policy.endpoint_logging_policy.arn
}

data "aws_iam_policy_document" "allow_function_invoke" {
  statement {
    sid    = "AllowInvoke"
    effect = "Allow"

    actions = [
      "lambda:InvokeFunction"
    ]

    resources = [
      "${aws_lambda_function.process_end_of_day_lambda.arn}:$LATEST"
    ]
  }
}

resource "aws_iam_policy" "invoke_processor_policy" {
  name_prefix = "invoke-processor-"
  path        = "/${var.environment}/lambda/"
  description = "IAM policy to allow invocation of the end of day processor function"
  policy      = data.aws_iam_policy_document.allow_function_invoke.json
}

resource "aws_iam_role_policy_attachment" "end_of_day_endpoint_lambda_invoke" {
  role       = aws_iam_role.end_of_day_endpoint_lambda_iam_role.name
  policy_arn = aws_iam_policy.invoke_processor_policy.arn
}