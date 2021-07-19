default_customer_id       = "d49c014e77c911e797950a57719b2edb"
default_ledger_account_id = "3a3c2fad180911e691e20a5d7cf84c3e"
default_tax_rate_id       = "GB_STANDARD"
epos_validate_end_of_day  = false

ledger_mappings = [
  {
    M = {
      "epos_value" = {
        S = "Sat 1st XI"
      }
      "sage_value" = {
        S = "50d44d46180911e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "epos_value" = {
        S = "Sat 2nd XI"
      }
      "sage_value" = {
        S = "50d44e4b180911e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "epos_value" = {
        S = "Sat 3rd XI"
      }
      "sage_value" = {
        S = "50d44f42180911e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "epos_value" = {
        S = "Sat 4th XI"
      }
      "sage_value" = {
        S = "50d4512d180911e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "epos_value" = {
        S = "Sun 1st XI"
      }
      "sage_value" = {
        S = "50d45230180911e691e20a5d7cf84c3e"
      }
    }
  },
  {
    M = {
      "epos_value" = {
        S = "Sun 2nd XI"
      }
      "sage_value" = {
        S = "50d4532a180911e691e20a5d7cf84c3e"
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
        S = "3399ec1a84c242cb8d8fe7c82a281351"
      }
    }
  },
  {
    M = {
      "epos_value" = {
        S = "88830"
      }
      "sage_value" = {
        S = "6a61344756614dc593945c2c2c45aabc"
      }
    }
  },
]