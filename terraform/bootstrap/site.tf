terraform {
  backend "remote" {
    organization = "mersthamcc"

    workspaces {
      name = "accounts"
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

    tfe = {
      source  = "hashicorp/tfe"
      version = "0.25.3"
    }
  }

  required_version = "1.0.3"
}

provider "aws" {
  access_key = var.aws_access_key_id
  secret_key = var.aws_secret_access_key
  token      = var.aws_session_token
  region     = var.aws_region
}

provider "digitalocean" {
  token = var.digitalocean_token
}

provider "tfe" {
  token = var.terraform_token
}