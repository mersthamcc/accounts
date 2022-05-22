output "grafana_monitoring_access_key" {
  value = aws_iam_access_key.grafana_monitoring.id
}

output "grafana_monitoring_secret_key" {
  value     = aws_iam_access_key.grafana_monitoring.secret
  sensitive = true
}

output "grafana_monitoring_assume_role_arn" {
  value = aws_iam_role.grafana_monitoring.arn
}

output "accounting_zone_id" {
  value = aws_route53_zone.accounting.zone_id
}

output "accounting_zone_name" {
  value = aws_route53_zone.accounting.name
}

output "deployment_bucket_name" {
  value = aws_s3_bucket.lambda_source.bucket
}