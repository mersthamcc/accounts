variable "aws_account_name" {}

variable "aws_region" {
  default = "eu-west-2"
}

variable "digitalocean_token" {
  description = "Your Digital Ocean API token"
}

variable "domain" {
  description = "The default domain name used for DNS records"
}

variable "do_region" {
  description = "The region to create the resources in"
  default     = "lon1"
}

variable "terraform_token" {
  default = "Terraform Cloud Team Token"
}
