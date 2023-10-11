@dqDataValidation
Feature: DQ Data validation
#                        Total:- 33 Tables
# --------------------Data Table Types and Relationships----------------------------------------
#  Table 0100 – Depositor Data
#  Table 0110 – Personal Identification
#  Table 0120 – Address Data
#  Table 0121 – External Account Data
#  Table 0130 – Deposit Account Data
#  Table 0140 – MI Deposit Hold Data
#  Table 0152 – Beneficiary Data - Not a Nominee Broker and not a Professional Trustee
#  Table 0153 – Beneficiary Data - Nominee Broker
#  Table 0201 – Depositor Type
#  Table 0202 – Phone Type
#  Table 0211 - Personal Identification Type
#  Table 0212 – CDIC Personal ID Type
#  Table 0221 – Address Type
#  Table 0231 – Product Code
#  Table 0232 – Registered Plan Type
#  Table 0233 – Currency Code
#  Table 0234 – Insurance Determination Category Type
#  Table 0235 – CDIC Hold Status Code
#  Table 0236 – Account Status Code
#  Table 0237 – Trust Account Type
#  Table 0238 – Clearing Account Code
#  Table 0239 – Account Type
#  Table 0240 – CDIC Product Group Code
#  Table 0241 – MI Deposit Hold Code
#  Table 0242 – MI Published Foreign Currency Exchange Rate
#  Table 0400 – Transaction Data
#  Table 0401 – Transaction Code
#  Table 0500 – Depositor / Deposit Account Reference Table
#  Table 0501 – Relationship Type
#  Table 0600 – Ledger and Sub-Ledger Balances
#  Table 0800 – Hold Balance File
#  Table 0900 – Account Accrued Interest Data
#  Table 0999 – Subsystem

  Background: DQ Rules
    Given dq rules definitions
    And generate report

  @us100dq
  Scenario: Verify table 0100-Depositor Data
    Then validate 0100-Depositor Data
    And generate report

  @us0110dq
  Scenario: Verify table 0110-Personal Identification
    Then validate 0110-Personal Identification
    And generate report

  @us120dq
  Scenario: Verify table 0120-Address Data
    Then validate 0120-Address Data
    And generate report

  @us0121dq
  Scenario: Verify table 0121-External Account Data
    Then validate 0121-External Account Data
    And generate report

  @us0130dq
  Scenario: Verify table 0130-Deposit Account Data
    Then validate 0130-Deposit Account Data
    And generate report

  @us0140dq
  Scenario: Verify table 0140-Subsystem
    Then validate 0140-MI Deposit Hold Data
    And generate report

  @us0152dq
  Scenario: Verify table 0152-Beneficiary Data - Not a Nominee Broker and not a Professional Trustee Account
    Then validate 0152-Beneficiary Data - Not a Nominee Broker and not a Professional Trustee Account
    And generate report

  @us0153dq
  Scenario: Verify table 0153-Beneficiary Data - Nominee Broker
    Then validate 0153-Beneficiary Data - Nominee Broker
    And generate report

  @us0201dq
  Scenario: Verify table 0201-Depositor Type
    Then validate 0201-Depositor Type
    And generate report

  @us0202dq
  Scenario: Verify table 0202-phone type table
    Then validate 0202-phone type
    And generate report

  @us0211dq
  Scenario: Verify table 0211-Personal Identification Type
    Then validate 0211-Personal Identification Type
    And generate report

  @us0212dq
  Scenario: Verify table 0212-CDIC Personal ID Type
    Then validate 0212-CDIC Personal ID Type
    And generate report

  @us0221dq
  Scenario: Verify table 0221-Address Type
    Then validate 0221-Address Type
    And generate report

  @us0231dq
  Scenario: Verify table 0231-Product Code
    Then validate 0231-Product Code
    And generate report

  @us0232dq
  Scenario: Verify table 0232-Registered Plan Type
    Then validate 0232-Registered Plan Type
    And generate report

  @us0233dq
  Scenario: Verify table 0233-Currency Code
    Then validate 0233-Currency Code
    And generate report

  @us0234dq
  Scenario: Verify table 0234-Insurance Determination Category Type
    Then validate 0234-Insurance Determination Category Type
    And generate report

  @us0235dq
  Scenario: Verify table 0235-CDIC Hold Status Code
    Then validate 0235-CDIC Hold Status Code
    And generate report

  @us0236dq
  Scenario: Verify table 0236-Account Status Code
    Then validate 0236-Account Status Code
    And generate report

  @us0237dq
  Scenario: Verify table 0237-Trust Account Type
    Then validate 0237-Trust Account Type
    And generate report

  @us0238dq
  Scenario: Verify table 0238-Clearing Account Code
    Then validate 0238-Clearing Account Code
    And generate report

  @us0239dq
  Scenario: Verify table 0239-Account Type
    Then validate 0239-Account Type
    And generate report

  @us0240dq
  Scenario: Verify table 0240-CDIC Product Group Code
    Then validate 0240-CDIC Product Group Code
    And generate report

  @us0241dq
  Scenario: Verify table 0241-MI Deposit Hold Code
    Then validate 0241-MI Deposit Hold Code
    And generate report

  @us0242dq
  Scenario: Verify table 0242-MI Published Foreign Currency Exchange Rate
    Then validate 0242-MI Published Foreign Currency Exchange Rate
    And generate report

  @us0400dq
  Scenario: Verify table 0400-Transaction Data
    Then validate 0400-Transaction Data
    And generate report

  @us0401dq
  Scenario: Verify table 0401-Transaction Code
    Then validate 0401-Transaction Code
    And generate report

  @us0500dq
  Scenario: Verify table 0500-Depositor/Deposit account reference table
    Then validate 0500-depositor/Deposit account reference table
    And generate report

  @us0501dq
  Scenario: Verify table 0501-Relationship Type
    Then validate 0501-Relationship Type
    And generate report

  @us0600dq
  Scenario: Verify table 0600-Ledger and Sub-Ledger Balances
    Then validate 0600-Ledger and Sub-Ledger Balances
    And generate report


  @us0800dq
  Scenario: Verify table 0800-Hold Balance File
    Then validate 0800-Hold Balance File
    And generate report

  @us0900dq
  Scenario: Verify table 0900-Account Accrued Interest Data
    Then validate 0900-Account Accrued Interest Data
    And generate report

  @us0999dq
  Scenario: Verify table 0999-Subsystem
    Then validate 0999-Subsystem
    And generate report

