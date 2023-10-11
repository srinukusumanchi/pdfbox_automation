@dqTableValidation
Feature: DQ Table Validation
#  Table Validation

  @0232RegisteredPlanType
  Scenario Outline: Verify 0232 registeredPlanType table validation
    Given column names are "Registered_Plan_Type_Code" and "Description"
    Then validate 0232 table "<REGISTERED_PLAN_TYPE_CODE>" and "<DESCRIPTION>"
    And generate report for "<REGISTERED_PLAN_TYPE_CODE>" as "1"
    Examples:
      | REGISTERED_PLAN_TYPE_CODE | DESCRIPTION    |
      | 1                         | Not Registered |

  @0234RegisteredPlanType
  Scenario Outline: Verify 0234 Insurance Determination Category Type
    Given column names are "Insurance_Determination_Category_Type_Code" and "Description"
    Then validate "0234" table "<Insurance_Determination_Category_Type_Code>" and "<Description>"
    And generate report for "<Insurance_Determination_Category_Type_Code>" as "10"
    Examples:
      | Insurance_Determination_Category_Type_Code | Description                        |
      | 1                                          | Ineligible                         |
      | 2                                          | Basic                              |
      | 3                                          | Joint                              |
      | 4                                          | Trust account                      |
      | 5                                          | Registered Retirement Savings Plan |
      | 6                                          | Registered Retirement Income Fund  |
      | 7                                          | Tax Free Savings Account           |
      | 9                                          | RESP                               |
      | 10                                         | RDSP                               |


  @0235CDICHoldStatusCode
  Scenario Outline: Verify 0235 CDIC Hold Status Code table validation
    Given column names are "CDIC_Hold_Status_Code" and "CDIC_Hold_Status"
    Then validate "0235" table "<CDIC_Hold_Status_Code>" and "<CDIC_Hold_Status>"
    And generate report for "<CDIC_Hold_Status_Code>" as "3"
    Examples:
      | CDIC_Hold_Status_Code | CDIC_Hold_Status  |
      | 1                     | No CDIC Hold      |
      | 2                     | CDIC Full Hold    |
      | 3                     | CDIC Partial Hold |

  @0237TrustAccountType
  Scenario Outline: Verify 0237 Trust Account Type table validation
    Given column names are "Trust_Account_Type_Code" and "Description"
    Then validate "0237" table "<Trust_Account_Type_Code>" and "<Description>"
    And generate report for "<Trust_Account_Type_Code>" as "4"
    Examples:
      | Trust_Account_Type_Code | Description                                                 |
      | 1                       | Accounts that are not trust accounts                        |
      | 2                       | Not a nominee broker and not a professional trustee account |
      | 3                       | Nominee Broker                                              |
      | 4                       | Professional Trustee Account                                |

  @0238ClearingAccountCode
  Scenario Outline: Verify 0238 Clearing Account Code table validation
    Given column names are "Clearing_Account_Code" and "Description"
    Then validate "0238" table "<Clearing_Account_Code>" and "<Description>"
    And generate report for "<Clearing_Account_Code>" as "1"
    Examples:
      | Clearing_Account_Code | Description            |
      | 1                     | Not a clearing account |

  @0239AccountType
  Scenario Outline: Verify 0239 Account Type table validation
    Given column names are "Account_Type_Code","MI_Account_Type" and "Description"
    Then validate "0239" table "<Account_Type_Code>","<MI_Account_Type>" and "<Description>"
    And generate report for "<Account_Type_Code>" as "26"
    Examples:
      | Account_Type_Code | MI_Account_Type | Description                                                   |
      | 1                 | SA_1            | Suspense account                                              |
      | 2                 | MT_2            | Mortgage Tax account                                          |
      | 3                 | CA_3            | Clearing account (acss clearing)                              |
      | 8                 | SA_2            | Escrow Suspense Account-Ineligible-Credit                     |
      | 9                 | SA_3            | Funds Transferred-Credit                                      |
      | 10                | SA_4            | Funds Transferred-Ineligible-Credit                           |
      | 11                | SA_5            | Other-Mixed                                                   |
      | 12                | SA_6            | Other-Ineligible-Mixed                                        |
      | 13                | SA_7            | Outstanding Cheques-Credit                                    |
      | 14                | SA_8            | Outstanding Cheques-Ineligible-Credit                         |
      | 15                | SA_9            | Remittance Accounts-Credit                                    |
      | 16                | SA_10           | Remittance Accounts-Ineligible-Credit                         |
      | 17                | SA_11           | Settlement Suspense Account-Mixed                             |
      | 18                | SA_12           | Settlement Suspense Account-Ineligible-Mixed                  |
      | 19                | SA_13           | SVC Float-Mixed                                               |
      | 20                | SA_14           | SVC Float-Ineligible-Mixed                                    |
      | 21                | SA_15           | Unclaimed Balances-Credit                                     |
      | 22                | SA_16           | Unclaimed Balances-Ineligible-Credit                          |
      | 23                | SA_17           | Unposted Transactions (system/account issue)-Mixed            |
      | 24                | SA_18           | Unposted Transactions (system/account issue)-Ineligible-Mixed |
      | 25                | SA_19           | FAS Deposits Suspense Account - Mixed                         |
      | 26                | SA_20           | FAS Deposits Suspense Account - Ineligible Mixed              |


  @0240CDICProductGroupCode
  Scenario Outline: Verify 0240 CDIC Product Group Code table validation
    Given column names are "CDIC_Product_Group_Code" and "CDIC_Product_Group"
    Then validate "0240" table "<CDIC_Product_Group_Code>" and "<CDIC_Product_Group>"
    And generate report for "<CDIC_Product_Group_Code>" as "4"
    Examples:
      | CDIC_Product_Group_Code | CDIC_Product_Group |
      | 1                       | Savings            |
      | 2                       | Chequing           |
      | 3                       | Term               |
      | 4                       | Other              |


  @0212CDICPersonalIDType
  Scenario Outline: Verify 0212 CDIC Personal ID Type table validation
    Given column names are "CDIC_Personal_ID_Type_Code" and "Description"
    Then validate 0212 table "<CDIC_Personal_ID_Type_Code>" and "<Description>"
    And generate report for "<CDIC_Personal_ID_Type_Code>" as "29"
    Examples:
      | CDIC_Personal_ID_Type_Code | Description                                                             |
      | 1                          | Birth Certificate from Canadian province or territory                   |
      | 2                          | Canadian Certificate of Registration of Birth Abroad                    |
      | 3                          | Canadian Immigration Identification Card                                |
      | 4                          | Certified Statement of Live Birth from a Canadian province or territory |
      | 5                          | Certificate of Canadian Citizenship                                     |
      | 6                          | Certificate of Indian Status                                            |
      | 7                          | Confirmation of Permanent Residence                                     |
      | 8                          | Credit Card                                                             |
      | 9                          | Current Employee ID                                                     |
      | 10                         | Current Professional Association License                                |
      | 11                         | Old Age Security card                                                   |
      | 12                         | Motor Vehicle Permit                                                    |
      | 13                         | Passport - Canadian                                                     |
      | 14                         | Passport - Foreign                                                      |
      | 15                         | Permanent Resident Card                                                 |
      | 16                         | Protected Person Status document                                        |
      | 17                         | Record of Landing                                                       |
      | 18                         | Registered Indian Record                                                |
      | 19                         | Student ID card                                                         |
      | 20                         | Temporary Resident Permit                                               |
      | 21                         | Union Card                                                              |
      | 22                         | Valid Driver's License                                                  |
      | 23                         | Work Permit                                                             |
      | 24                         | Social Insurance Number                                                 |
      | 25                         | Bank Card Number                                                        |
      | 26                         | CRA Business Number                                                     |
      | 27                         | CRA Trust Account Number                                                |
      | 28                         | Government issued identification from Canadian province or territory    |
      | 29                         | National Identification Number - Foreign                                |


  @0202PhoneType
  Scenario Outline: Verify 0202 Phone Type table validation
    Given column names are "Phone_Type_Code" and "Description"
    Then validate "0202" table "<Phone_Type_Code>" and "<Description>"
    And generate report for "<Phone_Type_Code>" as "5"
    Examples:
      | Phone_Type_Code | Description                |
      | 1               | Phone number not available |
      | 2               | Mobile                     |
      | 3               | Home or Personal           |
      | 4               | Business or Work           |
      | 5               | Fax                        |

  @0501PhoneType
  Scenario Outline: Verify 0501 Relationship Type table validation
    Given column names are "Relationship_Type_Code" and "Description"
    Then validate "0501" table "<Relationship_Type_Code>" and "<Description>"
    And generate report for "<Relationship_Type_Code>" as "901"
    Examples:
      | Relationship_Type_Code | Description          |
      | 901                    | NB/PT Contact Person |
