variable "aws_account_name" {}

variable "aws_region" {
  default = "eu-west-2"
}

variable "digitalocean_token" {
  description = "Your Digital Ocean API token"
}

variable "environment" {
  description = "A logical name for this environment (e.g. test, live)"
  default     = "test"
}

variable "epos_api_key" {
  description = "EposNow API Key"
}
variable "epos_api_secret" {
  description = "EposNow API Secret"
}
variable "sage_api_client_id" {
  description = "SageOne API Client ID"
}
variable "sage_api_client_secret" {
  description = "SageOne API Client Secret"
}

variable "default_customer_id" {
  description = "Default customer to use if non-specified on transaction"
}

variable "default_ledger_account_id" {
  description = "Default ledger to use if no specific mapping found"
}

variable "default_tax_rate_id" {
  description = "Default tax rate to use if no specific mapping found"
}

variable "no_tax_rate_id" {
  description = "Tax rate to use if no VAT implication"
}

variable "epos_validate_end_of_day" {
  description = "Whether to validate the the EndOfDay webhook before processing"
  default     = true
}

variable "ledger_mappings" {
  description = "A HCL formatted DynamoDB JSON document mapping products to ledgers accounts"
  default     = []
}

variable "tax_rate_mapping" {
  description = "A HCL formatted DynamoDB JSON document mapping Epos tax rates to Sage tax rates"
  default     = []
}

variable "tender_mapping" {
  description = "A HCL formatted DynamoDB JSON document mapping Epos tenders to Sage bank accounts"
  default     = []
}

variable "play_cricket_team_mapping" {
  description = "A HCL formatted DynamoDB JSON document mapping PlayCricket team names to Sage contacts/ledgers"
  default     = []
}

variable "match_fee_transfer_enabled" {
  default = true
}
