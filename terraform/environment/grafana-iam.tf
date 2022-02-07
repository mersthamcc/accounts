data "aws_iam_policy_document" "grafana_monitoring" {
  version = "2012-10-17"

  statement {
    sid    = "AllowReadingMetricsFromCloudWatch"
    effect = "Allow"
    actions = [
      "cloudwatch:DescribeAlarmsForMetric",
      "cloudwatch:DescribeAlarmHistory",
      "cloudwatch:DescribeAlarms",
      "cloudwatch:ListMetrics",
      "cloudwatch:GetMetricStatistics",
      "cloudwatch:GetMetricData",
    ]
    resources = [
      "*"
    ]
  }

  statement {
    sid    = "AllowReadingLogsFromCloudWatch"
    effect = "Allow"
    actions = [
      "logs:DescribeLogGroups",
      "logs:GetLogGroupFields",
      "logs:StartQuery",
      "logs:StopQuery",
      "logs:GetQueryResults",
      "logs:GetLogEvents"
    ]
    resources = [
      "*"
    ]
  }

  statement {
    sid    = "AllowReadingTagsInstancesRegionsFromEC2"
    effect = "Allow"
    actions = [
      "ec2:DescribeTags",
      "ec2:DescribeInstances",
      "ec2:DescribeRegions",
    ]
    resources = ["*"]
  }

  statement {
    sid    = "AllowReadingResourcesForTags"
    effect = "Allow"
    actions = [
      "tag:GetResources"
    ]
    resources = ["*"]
  }
}

resource "aws_iam_user" "grafana_monitoring" {
  name = "grafana-monitoring"
}

resource "aws_iam_access_key" "grafana_monitoring" {
  user = aws_iam_user.grafana_monitoring.id
}

data "aws_iam_policy_document" "grafana_monitoring_assume_role" {
  version = "2012-10-17"
  statement {
    actions = [
      "sts:AssumeRole"
    ]
    principals {
      identifiers = [
        aws_iam_user.grafana_monitoring.arn
      ]
      type = "AWS"
    }
    effect = "Allow"
  }
}

resource "aws_iam_role" "grafana_monitoring" {
  name               = "grafana-monitoring-role"
  assume_role_policy = data.aws_iam_policy_document.grafana_monitoring_assume_role.json
}

resource "aws_iam_policy" "grafana_monitoring" {
  name   = "grafana-monitoring"
  policy = data.aws_iam_policy_document.grafana_monitoring.json
}

resource "aws_iam_role_policy_attachment" "grafana_monitoring" {
  policy_arn = aws_iam_policy.grafana_monitoring.arn
  role       = aws_iam_role.grafana_monitoring.name
}
