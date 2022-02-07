output "end_of_day_username" {
  value     = random_password.end_of_day_username.result
  sensitive = true
}

output "end_of_day_password" {
  value     = random_password.end_of_day_password.result
  sensitive = true
}

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

output "deployment_bucket_name" {
  value = aws_s3_bucket.lambda_source.bucket
}