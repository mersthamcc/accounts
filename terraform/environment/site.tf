terraform {
  backend "remote" {
    organization = "mersthamcc"

    workspaces {
      name = "accounts-test"
    }
  }
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.0"
    }
    digitalocean = {
      source  = "digitalocean/digitalocean"
      version = "2.10.1"
    }
  }

  required_version = "1.1.5"
}

provider "aws" {
  access_key = var.aws_access_key_id
  secret_key = var.aws_secret_access_key
  token      = var.aws_session_token
  region     = var.aws_region

  default_tags {
    tags = {
      Environment = var.environment_name
      Application = "Accounts"
    }
  }
}

provider "digitalocean" {
  token = var.digitalocean_token
}