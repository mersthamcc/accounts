resource "aws_dynamodb_table" "audit" {
  name         = "${var.environment_name}-audit"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "barcode"

  attribute {
    name = "barcode"
    type = "S"
  }

  server_side_encryption {
    enabled = true
  }
}