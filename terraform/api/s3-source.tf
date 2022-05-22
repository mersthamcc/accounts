data "aws_s3_bucket_object" "lambda_source" {
  bucket = "${var.aws_account_name}-deployment-versioned"
  key    = "accounts.zip"
}