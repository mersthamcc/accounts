data "terraform_remote_state" "default" {
  backend = "remote"
  config = {
    organization = "mersthamcc"

    workspaces = {
      name = "default"
    }
  }
}

resource "digitalocean_record" "nameservers" {
  for_each = toset(aws_route53_zone.accounting.name_servers)

  name   = var.domain
  domain = data.terraform_remote_state.default.outputs.main_domain_zone_id
  type   = "NS"
  value  = "${each.value}."
  ttl    = "86400"

  depends_on = [
    aws_route53_zone.accounting
  ]
}
