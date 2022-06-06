resource "aws_sqs_queue" "transactions" {
  name                       = "${var.environment}-transactions.fifo"
  max_message_size           = 262144
  message_retention_seconds  = 1209600
  visibility_timeout_seconds = 900

  fifo_queue                  = true
  content_based_deduplication = false

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.transactions_dlq.arn
    maxReceiveCount     = 3
  })
}

resource "aws_sqs_queue" "transactions_dlq" {
  name                      = "${var.environment}-transactions-dlq.fifo"
  max_message_size          = 262144
  message_retention_seconds = 1209600

  fifo_queue                  = true
  content_based_deduplication = false
}

resource "time_sleep" "wait_60_seconds" {
  depends_on      = [aws_sqs_queue.transactions]
  create_duration = "60s"
}

data "aws_iam_policy_document" "email_queue_policy_document" {
  statement {
    sid    = "SendSQS"
    effect = "Allow"

    principals {
      type = "AWS"
      identifiers = [
        aws_iam_role.end_of_day_lambda_iam_role.arn,
        aws_iam_role.match_fee_lambda_iam_role.arn,
      ]
    }

    actions = [
      "sqs:SendMessage",
      "sqs:ChangeMessageVisibility",
      "sqs:GetQueueAttributes",
    ]

    resources = [
      aws_sqs_queue.transactions.arn
    ]
  }

  statement {
    sid    = "ReceiveSQS"
    effect = "Allow"

    principals {
      type        = "AWS"
      identifiers = [aws_iam_role.queue_processor_lambda_iam_role.arn]
    }

    actions = [
      "sqs:ReceiveMessage",
      "sqs:DeleteMessage",
      "sqs:GetQueueAttributes",
    ]

    resources = [
      aws_sqs_queue.transactions.arn
    ]
  }

  depends_on = [
    time_sleep.wait_60_seconds,
    aws_iam_role.queue_processor_lambda_iam_role,
    aws_iam_role.end_of_day_lambda_iam_role,
    aws_iam_role.match_fee_lambda_iam_role,
  ]
}

resource "aws_sqs_queue_policy" "process_transactions_queue_policy" {
  depends_on = [
    time_sleep.wait_60_seconds,
    data.aws_iam_policy_document.email_queue_policy_document,
  ]

  queue_url = aws_sqs_queue.transactions.id
  policy    = data.aws_iam_policy_document.email_queue_policy_document.json
}

resource "aws_lambda_function" "process_transactions_sqs_lambda" {
  s3_bucket         = data.aws_s3_bucket_object.lambda_source.bucket
  s3_key            = data.aws_s3_bucket_object.lambda_source.key
  s3_object_version = data.aws_s3_bucket_object.lambda_source.version_id

  function_name = "${var.environment}-transaction-sqs-lambda"
  role          = aws_iam_role.queue_processor_lambda_iam_role.arn
  handler       = "cricket.merstham.website.accounts.lambda.ProcessTransactions::handleRequest"
  timeout       = 600
  memory_size   = 2048
  runtime       = "java11"

  environment {
    variables = {
      CONFIG_NAME       = var.environment
      JAVA_TOOL_OPTIONS = "-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
    }
  }

  depends_on = [
    aws_iam_role.queue_processor_lambda_iam_role,
  ]
}

resource "aws_lambda_event_source_mapping" "lambda_sqs_mapping" {
  event_source_arn = aws_sqs_queue.transactions.arn
  function_name    = aws_lambda_function.process_transactions_sqs_lambda.arn

  batch_size = 10
  enabled    = true

  depends_on = [
    aws_sqs_queue.transactions,
    aws_sqs_queue_policy.process_transactions_queue_policy,
    aws_lambda_function.process_transactions_sqs_lambda,
    aws_iam_role.queue_processor_lambda_iam_role,
  ]
}

resource "aws_cloudwatch_log_group" "sqs_transactions_log_group" {
  name              = "/aws/lambda/${aws_lambda_function.process_transactions_sqs_lambda.function_name}"
  retention_in_days = 90

  depends_on = [
    aws_lambda_function.process_transactions_sqs_lambda
  ]
}
