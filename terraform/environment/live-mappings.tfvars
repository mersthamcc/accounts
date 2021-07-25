environment_name = "live"
hostname         = "api"


default_customer_id       = "d3601f9b174b11e691e20a5d7cf84c3e"
default_ledger_account_id = "9a99f762180811e691e20a5d7cf84c3e"
default_tax_rate_id       = "GB_STANDARD"
epos_validate_end_of_day  = true

ledger_mappings = [
  {
    M = {
      "epos_value" = {
        S = "Sat 1st XI"
      }
      "sage_value" = {
        S = "9ab66927180811e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "epos_value" = {
        S = "Sat 2nd XI"
      }
      "sage_value" = {
        S = "9ab66a34180811e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "epos_value" = {
        S = "Sat 3rd XI"
      }
      "sage_value" = {
        S = "9ab66ecc180811e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "epos_value" = {
        S = "Sat 4th XI"
      }
      "sage_value" = {
        S = "9ab66fdf180811e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "epos_value" = {
        S = "Sun 1st XI"
      }
      "sage_value" = {
        S = "9ab6779f180811e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "epos_value" = {
        S = "Sun 2nd XI"
      }
      "sage_value" = {
        S = "9ab6734f180811e691e20a5d7cf84c3e"
      }
    }
  },
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