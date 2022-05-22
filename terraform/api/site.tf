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
      version = "2.19.0"
    }
  }
}

provider "aws" {
#  access_key = var.aws_access_key_id
#  secret_key = var.aws_secret_access_key
#  token      = var.aws_session_token
  region     = "eu-west-2"

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

data "terraform_remote_state" "accounts_state" {
  backend = "remote"
  config = {
    organization = "mersthamcc"

    workspaces = {
      name = "accounts"
    }
  }
}