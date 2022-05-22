terraform {
  backend "remote" {
    organization = "mersthamcc"

    workspaces {
      name = "mersthamcc-dev-accounts-core"
    }
  }

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.15.1"
    }

    digitalocean = {
      source  = "digitalocean/digitalocean"
      version = "2.19.0"
    }

    tfe = {
      source  = "hashicorp/tfe"
      version = "0.31.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

provider "digitalocean" {
  token = var.digitalocean_token
}

provider "tfe" {
  token = var.terraform_token
}

data "aws_billing_service_account" "current" {}