variable "endpoint_name" {
  type = string
}

variable "endpoint_method" {
  type = string
}

variable "handler_function_name" {
  type = string
}

variable "handler_environment_variables" {
  type = map(string)
}

variable "handler_runtime" {
  type    = string
  default = "java11"
}

variable "lambda_role_arn" {
  type = string
}

variable "rest_api_id" {
  type = string
}

variable "execution_arn" {}
variable "root_resource_id" {}

variable "s3_bucket" {}
variable "s3_key" {}
variable "s3_version" {}

variable "environment_name" {
  description = "Name of the environment"
}

variable "timeout" {
  default = 30
}

variable "api_key_required" {
  default = false
}
