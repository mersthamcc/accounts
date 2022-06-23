resource "aws_dynamodb_table_item" "config" {
  table_name = aws_dynamodb_table.config.name
  hash_key   = aws_dynamodb_table.config.hash_key
  item = jsonencode({
    id = {
      S = var.environment
    }
    "api_configuration" = {
      M = {
        "epos_api_key" = {
          S = var.epos_api_key
        }
        "epos_api_secret" = {
          S = var.epos_api_secret
        }
        "epos_validate_end_of_day" = {
          BOOL = var.epos_validate_end_of_day
        }
        "sage_api_key" = {
          S = var.sage_api_client_id
        }
        "sage_api_secret" = {
          S = var.sage_api_client_secret
        }
        "queue_url" = {
          S = aws_sqs_queue.transactions.id
        }
      }
    }
    "mapping_configuration" = {
      M = {
        "default_customer_id" = {
          S = var.default_customer_id
        }
        "default_ledger_account_id" = {
          S = var.default_ledger_account_id
        }
        "default_tax_rate_id" = {
          S = var.default_tax_rate_id
        }
        "no_tax_rate_id" = {
          S = var.no_tax_rate_id
        }
        "ledger_mapping" = {
          L = var.ledger_mappings
        }
        "tax_rate_mapping" = {
          L = var.tax_rate_mapping
        }
        "tender_mapping" = {
          L = var.tender_mapping
        }
      }
    }
    play_cricket_team_mapping = {
      L = var.play_cricket_team_mapping
    }
  })
}
