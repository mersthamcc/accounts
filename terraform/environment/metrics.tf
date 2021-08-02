resource "aws_cloudwatch_log_metric_filter" "transaction_processing_error_metric" {
  name           = "${var.environment_name}-transaction-processing-errors"
  pattern        = "?\"ERROR\" ?\"Exception\""
  log_group_name = aws_cloudwatch_log_group.sqs_transactions_log_group.name

  metric_transformation {
    name          = "ProcessingErrorCount"
    namespace     = "${var.environment_name}-processing-metrics"
    value         = "1"
    default_value = "0"
  }
}

resource "aws_cloudwatch_log_metric_filter" "transaction_processing_skip_metric" {
  name           = "${var.environment_name}-transaction-processing-skips"
  pattern        = "?\"Skipping transaction\""
  log_group_name = aws_cloudwatch_log_group.sqs_transactions_log_group.name

  metric_transformation {
    name          = "ProcessingSkipCount"
    namespace     = "${var.environment_name}-processing-metrics"
    value         = "1"
    default_value = "0"
  }
}

resource "aws_cloudwatch_log_metric_filter" "transaction_processing_success_metric" {
  name           = "${var.environment_name}-transaction-processing-success"
  pattern        = "?\"SageAccountingService - Created SI-\""
  log_group_name = aws_cloudwatch_log_group.sqs_transactions_log_group.name

  metric_transformation {
    name          = "ProcessingSuccessCount"
    namespace     = "${var.environment_name}-processing-metrics"
    value         = "1"
    default_value = "0"
  }
}