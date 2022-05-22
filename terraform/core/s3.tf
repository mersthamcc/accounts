resource "aws_s3_bucket" "lambda_source" {
  bucket = "${var.aws_account_name}-deployment-versioned"
}

resource "aws_s3_bucket_acl" "example_bucket_acl" {
  bucket = aws_s3_bucket.lambda_source.id
  acl    = "private"
}

resource "aws_s3_bucket_versioning" "lambda_source_versioning" {
  bucket = aws_s3_bucket.lambda_source.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "lambda_source_lifecycle_configuration" {
  bucket = aws_s3_bucket.lambda_source.id
  rule {
    id     = "version-expiry"
    status = "Enabled"
    noncurrent_version_expiration {
      noncurrent_days = 5
    }
  }
}