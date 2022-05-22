resource "tfe_workspace" "accounting" {
  name         = "${var.aws_account_name}-accounting-api"
  organization = "mersthamcc"

  auto_apply            = false
  execution_mode        = "local"
  file_triggers_enabled = false
  global_remote_state   = false
}
