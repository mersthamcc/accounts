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
  }

  required_version = "1.0.3"
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

data "terraform_remote_state" "accounts_state" {
  backend = "remote"
  config = {
    organization = "mersthamcc"

    workspaces = {
      name = "accounts"
    }
  }
}