aws_account_name = "mersthamcc-accounts"
environment      = "live"

default_customer_id       = "d3601f9b174b11e691e20a5d7cf84c3e"
default_ledger_account_id = "9a99f762180811e691e20a5d7cf84c3e"
default_tax_rate_id       = "GB_STANDARD"
no_tax_rate_id            = "GB_NO_TAX"
epos_validate_end_of_day  = true

ledger_mappings = [
  {
    M = {
      "epos_value" = {
        // Sat 1st XI
        S = "32939873"
      }
      "sage_value" = {
        S = "9ab66927180811e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "epos_value" = {
        // Sat 2nd XI
        S = "32939877"
      }
      "sage_value" = {
        S = "9ab66a34180811e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "epos_value" = {
        // Sat 3rd XI
        S = "33015677"
      }
      "sage_value" = {
        S = "9ab66ecc180811e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "epos_value" = {
        // Sat 4th XI
        S = "33015682"
      }
      "sage_value" = {
        S = "9ab66fdf180811e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "epos_value" = {
        // Sun 1st/Dev XI
        S = "33015693"
      }
      "sage_value" = {
        S = "9ab6779f180811e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "epos_value" = {
        // Sun 2nd XI
        S = "33015695"
      }
      "sage_value" = {
        S = "9ab6734f180811e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "epos_value" = {
        // 100 Club Membership (Annual)
        S = "42161302"
      }
      "sage_value" = {
        S = "ecb24610d47840f79bc2bf8ae56160fa"
      }
    }
  },
  {
    M = {
      "epos_value" = {
        // Feathers Training Top
        S = "42976258"
      }
      "sage_value" = {
        S = "9ab664ea180811e691e20a5d7cf84c3e"
      }
    }
  }
]

tax_rate_mapping = [
  {
    M = {
      "epos_value" = {
        S = "20VAT"
      }
      "sage_value" = {
        S = "GB_STANDARD"
      }
    }
  },
  {
    M = {
      "epos_value" = {
        S = "NoTax"
      }
      "sage_value" = {
        S = "GB_EXEMPT"
      }
    }
  },
]

tender_mapping = [
  {
    M = {
      "epos_value" = {
        S = "92276"
      }
      "sage_value" = {
        S = "3f7262bd363a4819a988112bc1ec9692"
      }
    }
  },
  {
    M = {
      "epos_value" = {
        S = "88830"
      }
      "sage_value" = {
        S = "5561463131944b35b364be9a74037da2"
      }
    }
  },
]

play_cricket_team_mapping = [
  {
    M = {
      "play_cricket_team_name" = {
        S = "1st XI"
      }
      "sage_customer_id" = {
        S = "d36e4998174b11e691e20a5d7cf84c3e"
      }
      "sage_ledger_id" = {
        S = "9ab66927180811e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "play_cricket_team_name" = {
        S = "2nd XI"
      }
      "sage_customer_id" = {
        S = "d36e5055174b11e691e20a5d7cf84c3e"
      }
      "sage_ledger_id" = {
        S = "9ab66a34180811e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "play_cricket_team_name" = {
        S = "3rd XI"
      }
      "sage_customer_id" = {
        S = "d36e55c3174b11e691e20a5d7cf84c3e"
      }
      "sage_ledger_id" = {
        S = "9ab66ecc180811e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "play_cricket_team_name" = {
        S = "4th XI"
      }
      "sage_customer_id" = {
        S = "d36e5ac0174b11e691e20a5d7cf84c3e"
      }
      "sage_ledger_id" = {
        S = "9ab66fdf180811e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "play_cricket_team_name" = {
        S = "5th XI"
      }
      "sage_customer_id" = {
        S = "a64d6615dfb941b5b028ea7cecc0ecfc"
      }
      "sage_ledger_id" = {
        S = "dfd94b9ba5d0438a88a5102b776ca193"
      }
    }
  },
  {
    M = {
      "play_cricket_team_name" = {
        S = "Development XI"
      }
      "sage_customer_id" = {
        S = "d36e69b8174b11e691e20a5d7cf84c3e"
      }
      "sage_ledger_id" = {
        S = "9ab6779f180811e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "play_cricket_team_name" = {
        S = "Friendly XI"
      }
      "sage_customer_id" = {
        S = "d36e64b5174b11e691e20a5d7cf84c3e"
      }
      "sage_ledger_id" = {
        S = "9ab6734f180811e691e20a5d7cf84c3e"
      }
    }
  },
]