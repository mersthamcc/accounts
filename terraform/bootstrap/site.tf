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
  }

  required_version = "1.0.2"
}

provider "aws" {
  access_key = var.aws_access_key
  secret_key = var.aws_secret_key
  region     = var.aws_region
}

provider "digitalocean" {
  token = var.digitalocean_token
}
