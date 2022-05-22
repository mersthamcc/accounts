data "aws_s3_bucket_object" "lambda_source" {
  bucket = "mcc-accounts-deployment-20220207000926474500000001" // data.terraform_remote_state.accounts_state.outputs.deployment_bucket_name
  key    = "${var.environment_name}-accounts.zip"
}