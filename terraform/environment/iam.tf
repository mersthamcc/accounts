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
    sid    = ""
  }
}

resource "aws_iam_role" "lambda_iam_role" {
  name               = "${var.environment_name}-standard-lambda-role"
  assume_role_policy = data.aws_iam_policy_document.assume_role_policy.json
}

data "aws_iam_policy_document" "logging_policy" {
  version = "2012-10-17"
  statement {
    actions = [
      "logs:CreateLogGroup",
      "logs:CreateLogStream",
      "logs:PutLogEvents",
      "logs:CreateLogGroup"
    ]
    resources = [
      "arn:aws:logs:*:*:*"
    ]
    effect = "Allow"
  }
}

resource "aws_iam_policy" "endpoint_logging_policy" {
  name        = "${var.environment_name}-standard-lambda-logging"
  path        = "/"
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
      data.aws_dynamodb_table.config.arn,
      data.aws_dynamodb_table.token.arn,
    ]
  }
}

resource "aws_iam_policy" "lambda_dynamo_policy" {
  name        = "${var.environment_name}-standard-lambda-dynamo-policy"
  path        = "/"
  description = "IAM policy for managing Dynamo connection for a lambda"
  policy      = data.aws_iam_policy_document.dynamo_policy_document.json
}

resource "aws_iam_role_policy_attachment" "lambda_dynamo" {
  role       = aws_iam_role.lambda_iam_role.name
  policy_arn = aws_iam_policy.lambda_dynamo_policy.arn
}

resource "aws_iam_role" "end_of_day_lambda_iam_role" {
  name = "${var.environment_name}-end-of-day-lambda-role"

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
      "dynamodb:PutItem",
    ]
    resources = [
      aws_dynamodb_table.audit.arn,
    ]
  }
}

resource "aws_iam_policy" "lambda_audit_dynamo_policy" {
  name        = "${var.environment_name}-audit-lambda-dynamo-policy"
  path        = "/"
  description = "IAM policy for managing audit Dynamo table connection for a lambda"
  policy      = data.aws_iam_policy_document.audit_dynamo_policy_document.json
}

resource "aws_iam_role" "queue_processor_lambda_iam_role" {
  name = "${var.environment_name}-queue-processor-lambda-role"

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
