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

variable "aws_access_key" {
  description = "Your AWS Access token"
}

variable "aws_secret_key" {
  description = "Your AWS Access secret"
}

variable "aws_region" {
  description = "The AWS region in which to create resources"
  default     = "eu-west-2"
}

variable "domain" {
  description = "The default domain name used for DNS records"
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
