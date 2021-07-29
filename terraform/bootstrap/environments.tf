locals {
  workspaces = [
    "accounts-test",
    "accounts-live",
  ]
}

resource "tfe_workspace" "accounting" {
  count        = length(local.workspaces)
  name         = local.workspaces[count.index]
  organization = "mersthamcc"

  auto_apply            = false
  execution_mode        = "remote"
  file_triggers_enabled = false
  global_remote_state   = false
  terraform_version     = "1.0.3"
}

resource "tfe_variable" "aws_access_key" {
  count = length(local.workspaces)

  key          = "aws_access_key"
  value        = var.aws_access_key
  category     = "terraform"
  workspace_id = tfe_workspace.accounting[count.index].id
  sensitive    = true
}

resource "tfe_variable" "aws_secret_key" {
  count = length(local.workspaces)

  key          = "aws_secret_key"
  value        = var.aws_secret_key
  category     = "terraform"
  workspace_id = tfe_workspace.accounting[count.index].id
  sensitive    = true
}

resource "tfe_variable" "epos_api_key" {
  count = length(local.workspaces)

  key          = "epos_api_key"
  value        = var.epos_api_key
  category     = "terraform"
  workspace_id = tfe_workspace.accounting[count.index].id
  sensitive    = true
}

resource "tfe_variable" "epos_api_secret" {
  count = length(local.workspaces)

  key          = "epos_api_secret"
  value        = var.epos_api_secret
  category     = "terraform"
  workspace_id = tfe_workspace.accounting[count.index].id
  sensitive    = true
}

resource "tfe_variable" "sage_api_client_id" {
  count = length(local.workspaces)

  key          = "sage_api_client_id"
  value        = var.sage_api_client_id
  category     = "terraform"
  workspace_id = tfe_workspace.accounting[count.index].id
  sensitive    = true
}

resource "tfe_variable" "sage_api_client_secret" {
  count = length(local.workspaces)

  key          = "sage_api_client_secret"
  value        = var.sage_api_client_secret
  category     = "terraform"
  workspace_id = tfe_workspace.accounting[count.index].id
  sensitive    = true
}
