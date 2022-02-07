resource "aws_s3_bucket" "lambda_source" {
  bucket_prefix = "mcc-accounts-deployment-"
  acl           = "private"

  versioning {
    enabled = true
  }

  lifecycle_rule {
    enabled = true
    noncurrent_version_expiration {
      days = 5
    }
  }
}

data "aws_s3_bucket_object" "lambda_source" {
  bucket = aws_s3_bucket.lambda_source.bucket
  key    = "${var.environment_name}-accounts.zip"
}