@dqMaskingValidation
Feature: DQ Masking Validation
#  Table Validation

  @us0100masking
  Scenario: Verify table 0100-Depositor Data - Masking Content
    Given validate 0100-Depositor Data masking content
    And generate report

  @us0110masking
  Scenario: Verify table 0110-Personal Identification - Masking Content
    Then validate 0110-Personal Identification masking content
    And generate report

  @us120masking
  Scenario: Verify table 0120-Address Data - Masking Content
    Then validate 0120-Address Data masking content
    And generate report

  @us0121masking
  Scenario: Verify table 0121-External Account Data - Masking Content
    Then validate 0121-External Account Data masking content
    And generate report

  @us0130masking
  Scenario: Verify table 0130-Deposit Account Data - Masking Content
    Given validate 0130-Deposit Account Data masking content
    And generate report

  @us0140masking
  Scenario: Verify table 0140-Subsystem - Masking Content
    Then validate 0140-MI Deposit Hold Data masking content
    And generate report

  @us0152masking
  Scenario: Verify table 0152-Beneficiary Data - Not a Nominee Broker and not a Professional Trustee Account - Masking Content
    Then validate 0152-Beneficiary Data - Not a Nominee Broker and not a Professional Trustee Account masking content
    And generate report

  @us0153masking
  Scenario: Verify table 0153-Beneficiary Data - Nominee Broker - Masking Content
    Then validate 0153-Beneficiary Data - Nominee Broker masking content
    And generate report

  @us0400masking
  Scenario: Verify table 0400-Transaction Data - Masking Content
    Then validate 0400-Transaction Data masking content
    And generate report

  @us0500masking
  Scenario: Verify table 0500-Depositor/Deposit account reference table - Masking Content
    Then validate 0500-depositor/Deposit account reference table masking content
    And generate report

  @us0600masking
  Scenario: Verify table 0600-Ledger and Sub-Ledger Balances - Masking Content
    Then validate 0600-Ledger and Sub-Ledger Balances masking content
    And generate report

  @us0800masking
  Scenario: Verify table 0800-Hold Balance File - Masking Content
    Then validate 0800-Hold Balance File masking content
    And generate report

  @us0900masking
  Scenario: Verify table 0900-Account Accrued Interest Data - Masking Content
    Then validate 0900-Account Accrued Interest Data masking content
    And generate report