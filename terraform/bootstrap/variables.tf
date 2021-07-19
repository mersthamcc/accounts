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
