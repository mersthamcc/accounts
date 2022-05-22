terraform {
  backend "remote" {
    organization = "mersthamcc"

    workspaces {
      name = "mersthamcc-dev-accounts-api"
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

    random = {
      source  = "hashicorp/random"
      version = "3.2.0"
    }

    time = {
      source  = "hashicorp/time"
      version = "0.7.2"
    }
  }
}

provider "aws" {
  region = "eu-west-2"

  default_tags {
    tags = {
      Environment = var.environment
      Application = "accounts-api"
    }
  }
}

provider "digitalocean" {
  token = var.digitalocean_token
}

provider "random" {}

provider "time" {}

data "terraform_remote_state" "accounts_core_state" {
  backend = "remote"
  config = {
    organization = "mersthamcc"

    workspaces = {
      name = "${var.aws_account_name}-accounts-core"
    }
  }
}