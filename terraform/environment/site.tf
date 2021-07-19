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

  required_version = "1.0.2"
}

provider "aws" {
  access_key = var.aws_access_key
  secret_key = var.aws_secret_key
  region     = var.aws_region
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