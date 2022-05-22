output "end_of_day_username" {
  value     = random_password.end_of_day_username.result
  sensitive = true
}

output "end_of_day_password" {
  value     = random_password.end_of_day_password.result
  sensitive = true
}