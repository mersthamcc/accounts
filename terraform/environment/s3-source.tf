data "aws_s3_bucket_object" "lambda_source" {
  bucket = data.terraform_remote_state.accounts_state.outputs.deployment_bucket_name
  key    = "${var.environment_name}-accounts.zip"
}