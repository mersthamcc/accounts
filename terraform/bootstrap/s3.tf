resource "aws_s3_bucket" "lambda_source" {
  bucket = "mcc-accounts-deployment"
  acl    = "private"

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
