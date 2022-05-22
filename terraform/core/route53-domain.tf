resource "aws_route53_zone" "accounting" {
  name = "${var.domain}.${data.terraform_remote_state.default.outputs.main_domain_zone_id}"
}

