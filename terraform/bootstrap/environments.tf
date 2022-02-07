locals {
  terraform_version = "1.1.5"
  workspaces = {
    "accounts-test" = "remote"
    "accounts-live" = "remote"
  }
}

resource "tfe_workspace" "accounting" {
  for_each = local.workspaces

  name         = each.key
  organization = "mersthamcc"

  auto_apply            = false
  execution_mode        = each.value
  file_triggers_enabled = false
  global_remote_state   = false
  terraform_version     = local.terraform_version
}

resource "tfe_variable" "epos_api_key" {
  for_each = local.workspaces

  key          = "epos_api_key"
  value        = var.epos_api_key
  category     = "terraform"
  workspace_id = tfe_workspace.accounting[each.key].id
  sensitive    = true
}

resource "tfe_variable" "epos_api_secret" {
  for_each = local.workspaces

  key          = "epos_api_secret"
  value        = var.epos_api_secret
  category     = "terraform"
  workspace_id = tfe_workspace.accounting[each.key].id
  sensitive    = true
}

resource "tfe_variable" "sage_api_client_id" {
  for_each = local.workspaces

  key          = "sage_api_client_id"
  value        = var.sage_api_client_id
  category     = "terraform"
  workspace_id = tfe_workspace.accounting[each.key].id
  sensitive    = true
}

resource "tfe_variable" "sage_api_client_secret" {
  for_each = local.workspaces

  key          = "sage_api_client_secret"
  value        = var.sage_api_client_secret
  category     = "terraform"
  workspace_id = tfe_workspace.accounting[each.key].id
  sensitive    = true
}

resource "tfe_variable" "digitalocean_token" {
  for_each = local.workspaces

  key          = "digitalocean_token"
  value        = var.digitalocean_token
  category     = "terraform"
  workspace_id = tfe_workspace.accounting[each.key].id
  sensitive    = true
}

resource "tfe_variable" "do_region" {
  for_each = local.workspaces

  key          = "do_region"
  value        = var.do_region
  category     = "terraform"
  workspace_id = tfe_workspace.accounting[each.key].id
  sensitive    = true
}
