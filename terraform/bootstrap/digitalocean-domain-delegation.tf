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
  count = length(aws_route53_zone.accounting.name_servers)

  name   = "accounting"
  domain = data.terraform_remote_state.default.outputs.main_domain_zone_id
  type   = "NS"
  value  = "${aws_route53_zone.accounting.name_servers[count.index]}."
  ttl    = "86400"

  depends_on = [
    aws_route53_zone.accounting
  ]
}
