resource "aws_s3_bucket" "lambda_source" {
  bucket = "mcc-accounts-deployment"
  acl    = "private"

  versioning {
    enabled = true
  }
}
