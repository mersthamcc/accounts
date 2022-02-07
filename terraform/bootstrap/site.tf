terraform {
  backend "remote" {
    organization = "mersthamcc"

    workspaces {
      name = "accounts"
    }
  }
  required_providers {
    tfe = {
      source  = "hashicorp/tfe"
      version = "0.25.3"
    }
  }

  required_version = "1.1.5"
}

provider "tfe" {
  token = var.terraform_token
}