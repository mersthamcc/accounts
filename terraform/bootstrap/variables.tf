variable "terraform_token" {
  default = "Terraform Cloud Team Token"
}

variable "digitalocean_token" {
  description = "Your Digital Ocean API token"
}

variable "do_region" {
  description = "The region to create the resources in"
  default     = "lon1"
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
