@CDIC
Feature: Canadian Depositors Insurance Corporation

#  Functionality:- CDIC Trust Letter

#   ******Savings and DDA Accounts******
# 1. Trust Indicator-> Identifying an account is trust it should satisfy either of the below two points
#                      a) Account Name 1/Account Name 2 fields contain one of the following 13 strings of characters:
#                         TRUST, FIDUCIE, ITF, EFP, ITFMB, EFPBM, TR, FID, TRST, TRUSTEE, FIDUCIAIRE, TRUSTEES, FIDUCIAIRES
#                          Note:- Account Status:- Sole/Joint
#                      b) The Customer-to Account ‘in trust for’ relationship is present on the CIS extract: (English - French)
#                                     English                                             French
#                           -	Pers ITF PERS or NP ITF NP (TRU)                    Part EFP part ou PM EFP PM
#                           -	In Trust Primary Joint (TR1)                        Cpte joint princ. fiducie
#                           -	Trust Secondary Joint (TRT)                         Titul Sec-cpte JT Fid
#                           -	Non-Pers ITF Pers (TSC)                             P. mor. EFP part
#   ******IP Accounts******
#  Trust accounts in IP are determined by a ‘Y’ in the trust-account field. This field is set to ‘Y’ when the CI-IN-TRUST-FOR-IND = ‘1’
#  (indicating informal trust), the CI-SPECIAL-TRUST-INDICATOR = ‘1’ (indicating special trust), or the CI-PLAN-PRODUCT-CODE = NRSWD, NRSWT, RSCD, or NRSC

#  2. Trust Record Address-> 1. Personal Customer address is carried to different accounts created for that particular Personal customer and
#                                if required account mailing address can be changed then 2 PDF's generated (1-Customer Address, 1- Account Mailing Address)
#                              2. Non-Personal Customer address is carried to different accounts created for that particular Non-Personal Customer and
#                                 if required account mailing address can be changed but only 1 PDF gets generated(1- Customer Address)

#  3.Currency-> Testing Currency types for different accounts
#                a) Savings - (CAD, Euro, USD)
#                b) DDA - CAD
#                c) IP - (CAD, USD)

#  4. Letters-> Testing different PDF letters (Letter Types:- Letter 1E, Letter 2E, Letter 3E, Letter 2E PNTA, Letter 3E PNTA ,
#                                                             Letter 1F, Letter 2F, Letter 3F, Letter 2F PNTA, Letter 3F PNTA )
#  Note:- In Letter types E indicates 'English', F indicates 'French'
# Customer Accounts and Letter Types-> Letters are restricted based on Customer account types and Letter types
#                                       a) Letter 1E (Personal - Savings and IP Accounts)
#                                       b) Letter 2E (Non-Personal - Savings, DDA and IP Accounts)


#  1. Personal -> Savings/IP Accounts
#  2. Non-Personal - > Savings/IP Accounts/DDA
#  3. 1E, 2E,3E, 1F,2F,3F done
#  4. 3EPNTA,3EPNTA 2EPNTA, 2FPNTA pending
#  5. each customer having multiple savings account it will execute for every savings account and it will verify all savings account presnt in extract
#  6. each customer having 1 IP Account and having multiple GIC's it will only execute once and verifies all gic's and IP Accounts present in Extract
#  7. fields for verification address line-1, address line-2, city, postal code, gic
#  8. It captures account name, relation ship, status in excel for manual verification if necessary
#  9. It captures trust account or not for all savings and DDA account where as we don't have scope for IP Accounts wheter it is trust or not
#  we need to take support from funtional team
# 10. Framework level modification is pending
#
#Functional team suggested not to login and logout all the time for all test cases - done
# CLoSE ACCOUNTS -> It should not display in Extract file - done
#  iSSUER type - > capture GIC Name
#  delear-> capture  -BNS, SSI done
#  balance-> capture done
#primary customer having joint accounts we need to pick joint customer address and verify it with extract file done
#  3151255 12 - ist 85142 transit
#  adding zeros to fields like GIC, Account number, IP Account number
#
#  ******************************** Verifying Intralink Account Details in Extract File*********************************

  @Verify1ECustomerAccountsInExtract @AUTOMATED
  Scenario Outline: To Verify the Raw Data extract for Letter 1E
#    Jira Key :- CDIC-53
#    Description:- Verifying English Personal Customer Accounts Mailing Details with Extract file.
    Given the "<PersonalCustomer>" and "<ScenarioId>"
    Then Access the Intralink application and Locate the Customer
    And Retrieve the Address
    And Validate whether Accounts linked are Trust accounts
    Then Connect to the Raw Data extract for Letter
    And Validate the details mentioned for the Customer in the extract

    Examples:
      | PersonalCustomer | ScenarioId  |
      | LETTER1E         | LETTER1E_1  |
      | LETTER1E         | LETTER1E_2  |
#      | LETTER1E         | LETTER1E_3  |
#      | LETTER1E         | LETTER1E_4  |
#      | LETTER1E         | LETTER1E_5  |
#      | LETTER1E         | LETTER1E_6  |
#      | LETTER1E         | LETTER1E_7  |
#      | LETTER1E         | LETTER1E_8  |
#      | LETTER1E         | LETTER1E_9  |
#      | LETTER1E         | LETTER1E_10 |
#      | LETTER1E         | LETTER1E_11 |
#      | LETTER1E         | LETTER1E_12 |
#      | LETTER1E         | LETTER1E_13 |
#      | LETTER1E         | LETTER1E_14 |
#      | LETTER1E         | LETTER1E_15 |
#      | LETTER1E         | LETTER1E_16 |
#      | LETTER1E         | LETTER1E_17 |
#      | LETTER1E         | LETTER1E_18 |
#      | LETTER1E         | LETTER1E_19 |
#      | LETTER1E         | LETTER1E_20 |
#      | LETTER1E         | LETTER1E_21 |
#      | LETTER1E         | LETTER1E_22 |
#      | LETTER1E         | LETTER1E_23 |
#      | LETTER1E         | LETTER1E_24 |
#      | LETTER1E         | LETTER1E_25 |
#      | LETTER1E         | LETTER1E_26 |
#      | LETTER1E         | LETTER1E_27 |
#      | LETTER1E         | LETTER1E_28 |
#      | LETTER1E         | LETTER1E_29 |
#      | LETTER1E         | LETTER1E_30 |
#      | LETTER1E         | LETTER1E_31 |
#      | LETTER1E         | LETTER1E_32 |
#      | LETTER1E         | LETTER1E_33 |
#      | LETTER1E         | LETTER1E_34 |
#      | LETTER1E         | LETTER1E_35 |
#      | LETTER1E         | LETTER1E_36 |
#      | LETTER1E         | LETTER1E_37 |
#      | LETTER1E         | LETTER1E_38 |
#      | LETTER1E         | LETTER1E_39 |
#      | LETTER1E         | LETTER1E_40 |
#      | LETTER1E         | LETTER1E_41 |
#      | LETTER1E         | LETTER1E_42 |
#      | LETTER1E         | LETTER1E_43 |
#      | LETTER1E         | LETTER1E_44 |
#      | LETTER1E         | LETTER1E_45 |
#      | LETTER1E         | LETTER1E_46 |
#      | LETTER1E         | LETTER1E_47 |
#      | LETTER1E         | LETTER1E_48 |
#      | LETTER1E         | LETTER1E_49 |
#      | LETTER1E         | LETTER1E_50 |
#      | LETTER1E         | LETTER1E_51 |
#      | LETTER1E         | LETTER1E_52 |
#      | LETTER1E         | LETTER1E_53 |
#      | LETTER1E         | LETTER1E_54 |
#      | LETTER1E         | LETTER1E_55 |
#      | LETTER1E         | LETTER1E_56 |
#      | LETTER1E         | LETTER1E_57 |
#      | LETTER1E         | LETTER1E_58 |
#      | LETTER1E         | LETTER1E_59 |
#      | LETTER1E         | LETTER1E_60 |
#      | LETTER1E         | LETTER1E_61 |
#      | LETTER1E         | LETTER1E_62 |
#      | LETTER1E         | LETTER1E_63 |
#      | LETTER1E         | LETTER1E_64 |
#      | LETTER1E         | LETTER1E_65 |
#      | LETTER1E         | LETTER1E_66 |
#      | LETTER1E         | LETTER1E_67 |
#      | LETTER1E         | LETTER1E_68 |
#      | LETTER1E         | LETTER1E_69 |
#      | LETTER1E         | LETTER1E_70 |
#      | LETTER1E         | LETTER1E_71 |
#      | LETTER1E         | LETTER1E_72 |
#      | LETTER1E         | LETTER1E_73 |
#      | LETTER1E         | LETTER1E_74 |
#      | LETTER1E         | LETTER1E_75 |
#      | LETTER1E         | LETTER1E_76 |
#      | LETTER1E         | LETTER1E_77 |
#      | LETTER1E         | LETTER1E_78 |
#      | LETTER1E         | LETTER1E_79 |
#      | LETTER1E         | LETTER1E_80 |
#      | LETTER1E         | LETTER1E_81 |
#      | LETTER1E         | LETTER1E_82 |
#      | LETTER1E         | LETTER1E_83 |
#      | LETTER1E         | LETTER1E_84 |
#      | LETTER1E         | LETTER1E_85 |
#      | LETTER1E         | LETTER1E_86 |
#      | LETTER1E         | LETTER1E_87 |
#      | LETTER1E         | LETTER1E_88 |
#      | LETTER1E         | LETTER1E_89 |
#      | LETTER1E         | LETTER1E_90 |
#      | LETTER1E         | LETTER1E_91 |
#      | LETTER1E         | LETTER1E_92 |
#      | LETTER1E         | LETTER1E_93 |
#      | LETTER1E         | LETTER1E_94 |
#      | LETTER1E         | LETTER1E_96 |
##      | LETTER1E         | LETTER1E_97 |
##      | LETTER1E         | LETTER1E_98 |
##      | LETTER1E         | LETTER1E_99 |
##      | LETTER1E         | LETTER1E_100 |
##      | LETTER1E         | LETTER1E_101 |
##      | LETTER1E         | LETTER1E_102 |
##      | LETTER1E         | LETTER1E_103 |
##      | LETTER1E         | LETTER1E_104 |
##      | LETTER1E         | LETTER1E_105 |
##      | LETTER1E         | LETTER1E_106 |
##      | LETTER1E         | LETTER1E_107 |
#      | LETTER1E         | LETTER1E_109 |
#      | LETTER1E         | LETTER1E_115 |
#      | LETTER1E         | LETTER1E_121 |
#      | LETTER1E         | LETTER1E_127 |
#      | LETTER1E         | LETTER1E_133 |
#      | LETTER1E         | LETTER1E_137 |
#      | LETTER1E         | LETTER1E_151 |
#      | LETTER1E         | LETTER1E_152 |
#      | LETTER1E         | LETTER1E_155 |
#      | LETTER1E         | LETTER1E_156 |
#      | LETTER1E         | LETTER1E_165 |
#      | LETTER1E         | LETTER1E_166 |
#      | LETTER1E         | LETTER1E_123 |
#      | LETTER1E         | LETTER1E_124 |
#      | LETTER1E         | LETTER1E_132 |
#      | LETTER1E         | LETTER1E_133 |
#      | LETTER1E         | LETTER1E_134 |
#      | LETTER1E         | LETTER1E_135 |
#      | LETTER1E         | LETTER1E_136 |
##      | LETTER1E         | LETTER1E_139 |
##      | LETTER1E         | LETTER1E_140 |
#      | LETTER1E         | LETTER1E_141 |
#      | LETTER1E         | LETTER1E_142 |
#      | LETTER1E         | LETTER1E_162 |
##      | LETTER1E         | LETTER1E_153 |
##      | LETTER1E         | LETTER1E_154 |
##      | LETTER1E         | LETTER1E_155 |
#      | LETTER1E         | LETTER1E_159 |
#      | LETTER1E         | LETTER1E_163 |
#      | LETTER1E         | LETTER1E_164 |
#| LETTER1E | LETTER1E_3   |
#| LETTER1E | LETTER1E_4   |
#| LETTER1E | LETTER1E_5   |
#| LETTER1E | LETTER1E_6   |
#| LETTER1E | LETTER1E_13  |
#| LETTER1E | LETTER1E_16  |
#| LETTER1E | LETTER1E_17  |
#| LETTER1E | LETTER1E_18  |
#| LETTER1E | LETTER1E_27  |
#| LETTER1E | LETTER1E_30  |
#| LETTER1E | LETTER1E_38  |
#| LETTER1E | LETTER1E_50  |
#| LETTER1E | LETTER1E_64  |
#| LETTER1E | LETTER1E_65  |
#| LETTER1E | LETTER1E_66  |
#| LETTER1E | LETTER1E_113 |
#| LETTER1E | LETTER1E_114 |
#| LETTER1E | LETTER1E_125 |
#| LETTER1E | LETTER1E_144 |
#| LETTER1E | LETTER1E_145 |

  @Verify2ECustomerAccountsInExtract @AUTOMATED
  Scenario Outline: To Verify the Raw Data extract for Letter 2E
   #    Jira Key :- CDIC-55
   #    Description:- Verifying English Non-Personal Customer Accounts Mailing Details with Extract file.
    Given the "<NonPersonalCustomer>" and "<ScenarioId>"
    Then Access the Intralink application and Locate the Customer
    And Retrieve the Address
    And Validate whether Accounts linked are Trust accounts
    Then Connect to the Raw Data extract for Letter
    And Validate the details mentioned for the Customer in the extract

    Examples:
      | NonPersonalCustomer | ScenarioId   |
#      | LETTER2E            | LETTER2E_1  |
#      | LETTER2E            | LETTER2E_1  |
#      | LETTER2E            | LETTER2E_2  |
#      | LETTER2E            | LETTER2E_3  |
#      | LETTER2E            | LETTER2E_4  |
#      | LETTER2E            | LETTER2E_5  |
#      | LETTER2E            | LETTER2E_6  |
#      | LETTER2E            | LETTER2E_7  |
      | LETTER2E            | LETTER2E_8   |
#      | LETTER2E            | LETTER2E_9  |
#      | LETTER2E            | LETTER2E_10 |
#      | LETTER2E            | LETTER2E_11 |
#      | LETTER2E            | LETTER2E_12 |
#      | LETTER2E            | LETTER2E_13 |
#      | LETTER2E            | LETTER2E_14 |
#      | LETTER2E            | LETTER2E_15 |
#      | LETTER2E            | LETTER2E_16 |
#      | LETTER2E            | LETTER2E_17 |
#      | LETTER2E            | LETTER2E_18 |
#      | LETTER2E            | LETTER2E_19 |
#      | LETTER2E            | LETTER2E_20 |
#      | LETTER2E            | LETTER2E_21 |
#      | LETTER2E            | LETTER2E_22 |
#      | LETTER2E            | LETTER2E_23 |
#      | LETTER2E            | LETTER2E_24 |
#      | LETTER2E            | LETTER2E_25 |
#      | LETTER2E            | LETTER2E_26 |
#      | LETTER2E            | LETTER2E_27 |
#      | LETTER2E            | LETTER2E_28 |
#      | LETTER2E            | LETTER2E_29 |
#      | LETTER2E            | LETTER2E_30 |
#      | LETTER2E            | LETTER2E_31 |
#      | LETTER2E            | LETTER2E_32 |
#      | LETTER2E            | LETTER2E_33 |
#      | LETTER2E            | LETTER2E_34 |
#      | LETTER2E            | LETTER2E_35 |
#      | LETTER2E            | LETTER2E_36 |
#      | LETTER2E            | LETTER2E_37 |
#      | LETTER2E            | LETTER2E_38 |
#      | LETTER2E            | LETTER2E_39 |
#      | LETTER2E            | LETTER2E_40 |
#      | LETTER2E            | LETTER2E_41 |
#      | LETTER2E            | LETTER2E_42 |
#      | LETTER2E            | LETTER2E_43 |
#      | LETTER2E            | LETTER2E_44 |
#      | LETTER2E            | LETTER2E_45 |
#      | LETTER2E            | LETTER2E_46 |
#      | LETTER2E            | LETTER2E_47 |
#      | LETTER2E            | LETTER2E_48 |
#      | LETTER2E            | LETTER2E_49 |
#      | LETTER2E            | LETTER2E_50 |
#      | LETTER2E            | LETTER2E_51 |
#      | LETTER2E            | LETTER2E_52 |
#      | LETTER2E            | LETTER2E_53 |
#      | LETTER2E            | LETTER2E_54 |
#      | LETTER2E            | LETTER2E_55 |
#      | LETTER2E            | LETTER2E_56 |
#      | LETTER2E            | LETTER2E_57 |
#      | LETTER2E            | LETTER2E_58 |
#      | LETTER2E            | LETTER2E_59 |
#      | LETTER2E            | LETTER2E_60 |
#      | LETTER2E            | LETTER2E_61 |
#      | LETTER2E            | LETTER2E_62 |
#      | LETTER2E            | LETTER2E_63 |
#      | LETTER2E            | LETTER2E_64 |
#      | LETTER2E            | LETTER2E_65 |
#      | LETTER2E            | LETTER2E_66 |
#      | LETTER2E            | LETTER2E_67 |
#      | LETTER2E            | LETTER2E_68 |
#      | LETTER2E            | LETTER2E_69 |
#      | LETTER2E            | LETTER2E_70 |
#      | LETTER2E            | LETTER2E_71 |
#      | LETTER2E            | LETTER2E_72 |
#      | LETTER2E            | LETTER2E_73 |
#      | LETTER2E            | LETTER2E_74 |
#      | LETTER2E            | LETTER2E_75 |
#      | LETTER2E            | LETTER2E_76 |
#      | LETTER2E            | LETTER2E_77 |
#      | LETTER2E            | LETTER2E_78 |
#      | LETTER2E            | LETTER2E_79 |
#      | LETTER2E            | LETTER2E_80 |
#      | LETTER2E            | LETTER2E_81 |
#      | LETTER2E            | LETTER2E_82 |
      | LETTER2E            | LETTER2E_83  |
#      | LETTER2E            | LETTER2E_84 |
      | LETTER2E            | LETTER2E_85  |
#      | LETTER2E            | LETTER2E_86 |
#      | LETTER2E            | LETTER2E_87 |
#      | LETTER2E            | LETTER2E_88 |
#      | LETTER2E            | LETTER2E_89 |
#      | LETTER2E            | LETTER2E_90 |
#      | LETTER2E            | LETTER2E_91 |
#      | LETTER2E            | LETTER2E_92 |
#      | LETTER2E            | LETTER2E_93 |
#      | LETTER2E            | LETTER2E_94 |
#      | LETTER2E            | LETTER2E_95 |
#      | LETTER2E            | LETTER2E_96 |
#      | LETTER2E            | LETTER2E_97 |
#      | LETTER2E            | LETTER2E_98 |
#      | LETTER2E            | LETTER2E_99 |
#      | LETTER2E            | LETTER2E_100 |
#      | LETTER2E            | LETTER2E_101 |
      | LETTER2E            | LETTER2E_102 |
      | LETTER2E            | LETTER2E_103 |
      | LETTER2E            | LETTER2E_104 |
      | LETTER2E            | LETTER2E_105 |
      | LETTER2E            | LETTER2E_106 |
      | LETTER2E            | LETTER2E_127 |
#      | LETTER2E            | LETTER2E_108 |
#      | LETTER2E            | LETTER2E_109 |
#      | LETTER2E            | LETTER2E_110 |
#      | LETTER2E            | LETTER2E_111 |
#      | LETTER2E            | LETTER2E_112 |
#      | LETTER2E            | LETTER2E_113 |
#      | LETTER2E            | LETTER2E_114 |
#      | LETTER2E            | LETTER2E_115 |
#      | LETTER2E            | LETTER2E_116 |
#      | LETTER2E            | LETTER2E_117 |
#      | LETTER2E            | LETTER2E_118 |
#      | LETTER2E            | LETTER2E_119 |
#      | LETTER2E            | LETTER2E_120 |
#      | LETTER2E            | LETTER2E_121 |
#      | LETTER2E            | LETTER2E_122 |
#      | LETTER2E            | LETTER2E_127 |
#      | LETTER2E            | LETTER2E_54 |
#      | LETTER2E            | LETTER2E_54 |
#      | LETTER2E            | LETTER2E_54 |
#      | LETTER2E            | LETTER2E_54 |
#      | LETTER2E            | LETTER2E_54 |
#      | LETTER2E            | LETTER2E_54 |
#      | LETTER2E            | LETTER2E_54 |
#      | LETTER2E            | LETTER2E_54 |
#      | LETTER2E            | LETTER2E_54 |
#      | LETTER2E            | LETTER2E_54 |
#      | LETTER2E            | LETTER2E_54 |
#      | LETTER2E            | LETTER2E_54 |
#      | LETTER2E            | LETTER2E_54 |
#      | LETTER2E            | LETTER2E_54 |
#      | LETTER2E            | LETTER2E_54 |
#      | LETTER2E            | LETTER2E_54 |
#      | LETTER2E            | LETTER2E_54 |
#      | LETTER2E            | LETTER2E_54 |
#      | LETTER2E            | LETTER2E_54 |
#      | LETTER2E            | LETTER2E_54 |
#

  @Verify3ECustomerAccountsInExtract @AUTOMATED
  Scenario Outline: To Verify the Raw Data extract for Letter 3E
 #    Jira Key :- CDIC-57
 #    Description:- Verifying BSC Transit English Non-Personal Customers Accounts Mailing Details with Extract file .
    Given the "<NonPersonalCustomer>" and "<ScenarioId>"
    Then Access the Intralink application and Locate the Customer
    And Retrieve the Address
    And Validate whether Accounts linked are Trust accounts
    Then Connect to the Raw Data extract for Letter
    And Validate the details mentioned for the Customer in the extract

    Examples:
      | NonPersonalCustomer | ScenarioId |
#      | LETTER3E            | LETTER3E_1  |
#      | LETTER3E            | LETTER3E_2  |
#      | LETTER3E            | LETTER3E_3  |
#      | LETTER3E            | LETTER3E_4  |
#      | LETTER3E            | LETTER3E_5  |
#      | LETTER3E            | LETTER3E_6  |
      | LETTER3E            | LETTER3E_7 |
#      | LETTER3E            | LETTER3E_8  |
#      | LETTER3E            | LETTER3E_9  |
#      | LETTER3E            | LETTER3E_10 |
#      | LETTER3E            | LETTER3E_11 |
#      | LETTER3E            | LETTER3E_12 |
#      | LETTER3E            | LETTER3E_13 |
#      | LETTER3E            | LETTER3E_14 |
#      | LETTER3E            | LETTER3E_15 |
#      | LETTER3E            | LETTER3E_16 |
#      | LETTER3E            | LETTER3E_17 |
#      | LETTER3E            | LETTER3E_18 |
#      | LETTER3E            | LETTER3E_19 |
#      | LETTER3E            | LETTER3E_20 |
#      | LETTER3E            | LETTER3E_21 |
#      | LETTER3E            | LETTER3E_22 |
#      | LETTER3E            | LETTER3E_23 |
#      | LETTER3E            | LETTER3E_24 |
#      | LETTER3E            | LETTER3E_25 |
#      | LETTER3E            | LETTER3E_26 |
#      | LETTER3E            | LETTER3E_27 |
#      | LETTER3E            | LETTER3E_28 |
#      | LETTER3E            | LETTER3E_29 |
#      | LETTER3E            | LETTER3E_30 |
#      | LETTER3E            | LETTER3E_31 |
#      | LETTER3E            | LETTER3E_32 |
#      | LETTER3E            | LETTER3E_33 |
#      | LETTER3E            | LETTER3E_34 |



  @Verify2EPNTACustomerAccountsWithExtract @AUTOMATED
  Scenario Outline: To Verify the Raw Data extract for Letter 2E PNTA
   #    Jira Key :- CDIC-65
   #    Description:- Verifying English Non-Personal Customers having PNTA Accounts Mailing Details with Extract file .
    Given the "<NonPersonalCustomer>" and "<ScenarioId>"
    Then Access the Intralink application and Locate the Customer
    And Retrieve the Address
    And Validate whether Accounts linked are Trust accounts
    Then Connect to the Raw Data extract for Letter
    And Validate the details mentioned for the Customer in the extract
  #    And signout from intralink application

    Examples:
      | NonPersonalCustomer | ScenarioId       |
      | LETTER2EPNTA        | LETTER2E_PNTA_1  |
      | LETTER2EPNTA        | LETTER2E_PNTA_2  |
      | LETTER2EPNTA        | LETTER2E_PNTA_3  |
      | LETTER2EPNTA        | LETTER2E_PNTA_4  |
      | LETTER2EPNTA        | LETTER2E_PNTA_5  |
      | LETTER2EPNTA        | LETTER2E_PNTA_6  |
      | LETTER2EPNTA        | LETTER2E_PNTA_7  |
      | LETTER2EPNTA        | LETTER2E_PNTA_8  |
      | LETTER2EPNTA        | LETTER2E_PNTA_9  |
      | LETTER2EPNTA        | LETTER2E_PNTA_10 |
      | LETTER2EPNTA        | LETTER2E_PNTA_11 |
      | LETTER2EPNTA        | LETTER2E_PNTA_12 |
      | LETTER2EPNTA        | LETTER2E_PNTA_13 |
      | LETTER2EPNTA        | LETTER2E_PNTA_14 |
      | LETTER2EPNTA        | LETTER2E_PNTA_15 |
      | LETTER2EPNTA        | LETTER2E_PNTA_16 |
      | LETTER2EPNTA        | LETTER2E_PNTA_17 |
      | LETTER2EPNTA        | LETTER2E_PNTA_18 |
      | LETTER2EPNTA        | LETTER2E_PNTA_19 |
      | LETTER2EPNTA        | LETTER2E_PNTA_20 |
      | LETTER2EPNTA        | LETTER2E_PNTA_21 |
      | LETTER2EPNTA        | LETTER2E_PNTA_22 |


  @Verify3EPNTACustomerAccountsWithExtract @AUTOMATED
  Scenario Outline: To Verify the Raw Data extract for Letter 3EPNTA
#    Jira Key :- CDIC-67
#    Description:- Verifying BSC Transit English Non-Personal Customers having PNTA Accounts Mailing Details with Extract file .
    Given the "<NonPersonalCustomer>" and "<ScenarioId>"
    Then Access the Intralink application and Locate the Customer
    And Retrieve the Address
    And Validate whether Accounts linked are Trust accounts
    Then Connect to the Raw Data extract for Letter
    And Validate the details mentioned for the Customer in the extract

    Examples:
      | NonPersonalCustomer | ScenarioId       |
#      | LETTER3EPNTA        | LETTER3E_PNTA_1  |
#      | LETTER3EPNTA        | LETTER3E_PNTA_2  |
#      | LETTER3EPNTA        | LETTER3E_PNTA_3  |
      | LETTER3EPNTA        | LETTER3E_PNTA_4  |
#      | LETTER3EPNTA        | LETTER3E_PNTA_5  |
#      | LETTER3EPNTA        | LETTER3E_PNTA_6  |
#      | LETTER3EPNTA        | LETTER3E_PNTA_7  |
#      | LETTER3EPNTA        | LETTER3E_PNTA_8  |
#      | LETTER3EPNTA        | LETTER3E_PNTA_9  |
      | LETTER3EPNTA        | LETTER3E_PNTA_10 |
#      | LETTER3EPNTA        | LETTER3E_PNTA_11 |
#      | LETTER3EPNTA        | LETTER3E_PNTA_12 |
#      | LETTER3EPNTA        | LETTER3E_PNTA_13 |
#      | LETTER3EPNTA        | LETTER3E_PNTA_14 |
#      | LETTER3EPNTA        | LETTER3E_PNTA_15 |
#      | LETTER3EPNTA        | LETTER3E_PNTA_16 |
      | LETTER3EPNTA        | LETTER3E_PNTA_17 |


  @Verify1FCustomerAccountsWithExtract @AUTOMATED
  Scenario Outline: To Verify the Raw Data extract for Letter 1F
 #    Jira Key :- CDIC-59
 #    Description:- Verifying French Personal Customers Accounts Mailing Details with Extract file .
    Given the "<PersonalCustomer>" and "<ScenarioId>"
    Then Access the Intralink application and Locate the Customer
    And Retrieve the Address
    And Validate whether Accounts linked are Trust accounts
    Then Connect to the Raw Data extract for Letter
    And Validate the details mentioned for the Customer in the extract

    Examples:
      | PersonalCustomer | ScenarioId  |
      | LETTER1F         | LETTER1F_1  |
      | LETTER1F         | LETTER1F_2  |
      | LETTER1F         | LETTER1F_3  |
      | LETTER1F         | LETTER1F_4  |
      | LETTER1F         | LETTER1F_5  |
      | LETTER1F         | LETTER1F_6  |
      | LETTER1F         | LETTER1F_7  |
      | LETTER1F         | LETTER1F_8  |
      | LETTER1F         | LETTER1F_9  |
      | LETTER1F         | LETTER1F_10 |
      | LETTER1F         | LETTER1F_11 |
      | LETTER1F         | LETTER1F_12 |
      | LETTER1F         | LETTER1F_13 |
      | LETTER1F         | LETTER1F_14 |
      | LETTER1F         | LETTER1F_15 |
      | LETTER1F         | LETTER1F_16 |
      | LETTER1F         | LETTER1F_17 |
      | LETTER1F         | LETTER1F_18 |
      | LETTER1F         | LETTER1F_19 |
      | LETTER1F         | LETTER1F_20 |
      | LETTER1F         | LETTER1F_21 |
      | LETTER1F         | LETTER1F_22 |
      | LETTER1F         | LETTER1F_23 |
      | LETTER1F         | LETTER1F_24 |
      | LETTER1F         | LETTER1F_25 |
      | LETTER1F         | LETTER1F_26 |
      | LETTER1F         | LETTER1F_27 |
      | LETTER1F         | LETTER1F_28 |
      | LETTER1F         | LETTER1F_29 |
      | LETTER1F         | LETTER1F_30 |
      | LETTER1F         | LETTER1F_31 |
      | LETTER1F         | LETTER1F_32 |
      | LETTER1F         | LETTER1F_33 |
      | LETTER1F         | LETTER1F_34 |

  @Verify2FCustomerAccountsWithExtract @AUTOMATED
  Scenario Outline: To Verify the Raw Data extract for Letter 2F
 #    Jira Key :- CDIC-61
 #    Description:- Verifying French Non-Personal Customers Accounts Mailing Details with Extract file .
    Given the "<NonPersonalCustomer>" and "<ScenarioId>"
    Then Access the Intralink application and Locate the Customer
    And Validate whether Accounts linked are Trust accounts
    And Retrieve the Address
    Then Connect to the Raw Data extract for Letter
    And Validate the details mentioned for the Customer in the extract

    Examples:
      | NonPersonalCustomer | ScenarioId  |
      | LETTER2F            | LETTER2F_1  |
      | LETTER2F            | LETTER2F_2  |
      | LETTER2F            | LETTER2F_3  |
      | LETTER2F            | LETTER2F_4  |
      | LETTER2F            | LETTER2F_5  |
      | LETTER2F            | LETTER2F_6  |
      | LETTER2F            | LETTER2F_7  |
      | LETTER2F            | LETTER2F_8  |
      | LETTER2F            | LETTER2F_9  |
      | LETTER2F            | LETTER2F_10 |
      | LETTER2F            | LETTER2F_11 |
      | LETTER2F            | LETTER2F_12 |
      | LETTER2F            | LETTER2F_13 |
      | LETTER2F            | LETTER2F_14 |
      | LETTER2F            | LETTER2F_15 |
      | LETTER2F            | LETTER2F_16 |
      | LETTER2F            | LETTER2F_17 |
      | LETTER2F            | LETTER2F_18 |
      | LETTER2F            | LETTER2F_19 |
      | LETTER2F            | LETTER2F_20 |
      | LETTER2F            | LETTER2F_21 |
      | LETTER2F            | LETTER2F_22 |
      | LETTER2F            | LETTER2F_23 |
      | LETTER2F            | LETTER2F_24 |
      | LETTER2F            | LETTER2F_25 |
      | LETTER2F            | LETTER2F_26 |
      | LETTER2F            | LETTER2F_27 |
      | LETTER2F            | LETTER2F_28 |
      | LETTER2F            | LETTER2F_29 |
      | LETTER2F            | LETTER2F_30 |


  @Verify3FCustomerAccountsWithExtract @AUTOMATED
  Scenario Outline: To Verify the Raw Data extract for Letter 3F
 #    Jira Key :- CDIC-63
 #    Description:- Verifying BSC Transit French Non-Personal Customers Accounts Mailing Details with Extract file .
    Given the "<NonPersonalCustomer>" and "<ScenarioId>"
    Then Access the Intralink application and Locate the Customer
    And Validate whether Accounts linked are Trust accounts
    And Retrieve the Address
    Then Connect to the Raw Data extract for Letter
    And Validate the details mentioned for the Customer in the extract
    And signout from intralink application

    Examples:
      | NonPersonalCustomer | ScenarioId  |
      | LETTER3F            | LETTER3F_1  |
      | LETTER3F            | LETTER3F_2  |
      | LETTER3F            | LETTER3F_3  |
      | LETTER3F            | LETTER3F_4  |
      | LETTER3F            | LETTER3F_5  |
      | LETTER3F            | LETTER3F_6  |
      | LETTER3F            | LETTER3F_7  |
      | LETTER3F            | LETTER3F_8  |
      | LETTER3F            | LETTER3F_9  |
      | LETTER3F            | LETTER3F_10 |
      | LETTER3F            | LETTER3F_11 |
      | LETTER3F            | LETTER3F_12 |
      | LETTER3F            | LETTER3F_13 |
      | LETTER3F            | LETTER3F_14 |


  @Verify2FPNTACustomerAccountsWithExtract @AUTOMATED
  Scenario Outline: To Verify the Raw Data extract for Letter 2FPNTA
 #    Jira Key :- CDIC-69
 #    Description:- Verifying French Non-Personal Customers having PNTA Accounts Mailing Details with Extract file .
    Given the "<NonPersonalCustomer>" and "<ScenarioId>"
    Then Access the Intralink application and Locate the Customer
    And Retrieve the Address
    And Validate whether Accounts linked are Trust accounts
    Then Connect to the Raw Data extract for Letter
    And Validate the details mentioned for the Customer in the extract

    Examples:
      | NonPersonalCustomer | ScenarioId      |
      | LETTER2FPNTA        | LETTER2F_PNTA_1 |
      | LETTER2FPNTA        | LETTER2F_PNTA_2 |
      | LETTER2FPNTA        | LETTER2F_PNTA_3 |
      | LETTER2FPNTA        | LETTER2F_PNTA_4 |
      | LETTER2FPNTA        | LETTER2F_PNTA_5 |
      | LETTER2FPNTA        | LETTER2F_PNTA_6 |
      | LETTER2FPNTA        | LETTER2F_PNTA_7 |
#      | LETTER2FPNTA        | LETTER2FPNTA_8 |


  @Verify3FPNTACustomerAccountsWithExtract @AUTOMATED
  Scenario Outline: To Verify the Raw Data extract for Letter 3F PNTA
#    Jira Key :- CDIC-71
#    Description:- Verifying BSC Transit French Non-Personal Customers having PNTA Accounts Mailing Details with Extract file .
    Given the "<NonPersonalCustomer>" and "<ScenarioId>"
    Then Access the Intralink application and Locate the Customer
    And Validate whether Accounts linked are Trust accounts
    And Retrieve the Address
    Then Connect to the Raw Data extract for Letter
    And Validate the details mentioned for the Customer in the extract

    Examples:
      | NonPersonalCustomer | ScenarioId      |
      | Letter3FPNTA        | LETTER3F_PNTA_1 |
 #     | Letter3FPNTA        | LETTER3FPNTA_2 |

      #  ******************************** Verifying Intralink Account Details in PDF File*********************************

  @Verify1ECustomerAccountsWithPDF @AUTOMATED
  Scenario Outline: To Verify the PDF Letter generated for Letter 1E
 #    Jira Key :- CDIC-54
 #    Description:- Verifying English Personal Customer Accounts Mailing Details with PDF document.

    Given the "<PersonalCustomer>" and "<ScenarioId>"
    And Fetch the required Customer details from Raw Data extract and Intralink
    Then Connect to the PDF Letter
    Then Fetch the Customer details and validate the details from PDF with the details from Data sheet
    Examples:
      | PersonalCustomer | ScenarioId  |
      | LETTER1E         | LETTER1E_1  |
      | LETTER1E         | LETTER1E_2  |
#|LETTER1E|LETTER1E_3  |
#|LETTER1E|LETTER1E_4  |
#|LETTER1E|LETTER1E_5  |
#|LETTER1E|LETTER1E_6  |
#|LETTER1E|LETTER1E_7  |
#|LETTER1E|LETTER1E_8  |
#|LETTER1E|LETTER1E_9  |
#|LETTER1E|LETTER1E_10 |
#|LETTER1E|LETTER1E_11 |
#|LETTER1E|LETTER1E_12 |
#|LETTER1E|LETTER1E_13 |
#|LETTER1E|LETTER1E_14 |
#|LETTER1E|LETTER1E_15 |
#      | LETTER1E         | LETTER1E_16 |
#|LETTER1E|LETTER1E_17 |
#|LETTER1E|LETTER1E_18 |
#|LETTER1E|LETTER1E_19 |
#|LETTER1E|LETTER1E_20 |
#|LETTER1E|LETTER1E_21 |
#|LETTER1E|LETTER1E_22 |
#|LETTER1E|LETTER1E_23 |
#|LETTER1E|LETTER1E_24 |
#|LETTER1E|LETTER1E_25 |
#|LETTER1E|LETTER1E_26 |
#|LETTER1E|LETTER1E_27 |
#|LETTER1E|LETTER1E_28 |
#|LETTER1E|LETTER1E_29 |
#|LETTER1E|LETTER1E_30 |
#|LETTER1E|LETTER1E_31 |
#|LETTER1E|LETTER1E_32 |
#|LETTER1E|LETTER1E_33 |
#|LETTER1E|LETTER1E_34 |
#|LETTER1E|LETTER1E_35 |
#|LETTER1E|LETTER1E_36 |
#|LETTER1E|LETTER1E_37 |
#|LETTER1E|LETTER1E_38 |
#|LETTER1E|LETTER1E_39 |
#|LETTER1E|LETTER1E_40 |
#|LETTER1E|LETTER1E_41 |
#|LETTER1E|LETTER1E_42 |
#|LETTER1E|LETTER1E_43 |
#|LETTER1E|LETTER1E_44 |
#|LETTER1E|LETTER1E_45 |
#|LETTER1E|LETTER1E_46 |
#|LETTER1E|LETTER1E_47 |
#|LETTER1E|LETTER1E_48 |
#|LETTER1E|LETTER1E_49 |
#|LETTER1E|LETTER1E_50 |
#|LETTER1E|LETTER1E_51 |
#|LETTER1E|LETTER1E_52 |
#|LETTER1E|LETTER1E_53 |
#|LETTER1E|LETTER1E_54 |
#|LETTER1E|LETTER1E_55 |
#|LETTER1E|LETTER1E_56 |
#|LETTER1E|LETTER1E_57 |
#|LETTER1E|LETTER1E_58 |
#|LETTER1E|LETTER1E_59 |
#|LETTER1E|LETTER1E_60 |
#|LETTER1E|LETTER1E_61 |
#|LETTER1E|LETTER1E_62 |
#|LETTER1E|LETTER1E_63 |
#|LETTER1E|LETTER1E_64 |
#|LETTER1E|LETTER1E_65 |
#|LETTER1E|LETTER1E_66 |
#|LETTER1E|LETTER1E_66 |
#|LETTER1E|LETTER1E_67 |
#|LETTER1E|LETTER1E_68 |
#|LETTER1E|LETTER1E_69 |
#|LETTER1E|LETTER1E_70 |
#|LETTER1E|LETTER1E_71 |
#|LETTER1E|LETTER1E_72 |
#|LETTER1E|LETTER1E_73 |
#|LETTER1E|LETTER1E_74 |
#|LETTER1E|LETTER1E_75 |
#|LETTER1E|LETTER1E_76 |
#|LETTER1E|LETTER1E_77 |
#|LETTER1E|LETTER1E_78 |
#|LETTER1E|LETTER1E_79 |
#|LETTER1E|LETTER1E_80 |
#|LETTER1E|LETTER1E_82 |
#|LETTER1E|LETTER1E_83 |
#|LETTER1E|LETTER1E_84 |
#|LETTER1E|LETTER1E_85 |
#|LETTER1E|LETTER1E_86 |
#|LETTER1E|LETTER1E_87 |
#|LETTER1E|LETTER1E_88 |
#|LETTER1E|LETTER1E_89 |
#|LETTER1E|LETTER1E_90 |
#|LETTER1E|LETTER1E_91 |
#|LETTER1E|LETTER1E_92 |
#|LETTER1E|LETTER1E_93 |
#|LETTER1E|LETTER1E_94 |
#|LETTER1E|LETTER1E_95 |
#|LETTER1E|LETTER1E_96 |
#|LETTER1E|LETTER1E_97 |
#|LETTER1E|LETTER1E_98 |
#|LETTER1E|LETTER1E_99 |
#|LETTER1E|LETTER1E_100|
#|LETTER1E|LETTER1E_101|
#|LETTER1E|LETTER1E_102|
#|LETTER1E|LETTER1E_103|
#|LETTER1E|LETTER1E_104|
#|LETTER1E|LETTER1E_105|
#|LETTER1E|LETTER1E_106|
#|LETTER1E|LETTER1E_107|
#|LETTER1E|LETTER1E_108|
#|LETTER1E|LETTER1E_109|
#|LETTER1E|LETTER1E_110|
#|LETTER1E|LETTER1E_111|
#|LETTER1E|LETTER1E_112|
#|LETTER1E|LETTER1E_113|
#|LETTER1E|LETTER1E_114|
#|LETTER1E|LETTER1E_115|
#|LETTER1E|LETTER1E_116|
#|LETTER1E|LETTER1E_117|
#|LETTER1E|LETTER1E_118|
#|LETTER1E|LETTER1E_119|
#|LETTER1E|LETTER1E_120|
#|LETTER1E|LETTER1E_121|
#|LETTER1E|LETTER1E_122|
#|LETTER1E|LETTER1E_123|
#|LETTER1E|LETTER1E_124|
#|LETTER1E|LETTER1E_125|
#|LETTER1E|LETTER1E_126|
#|LETTER1E|LETTER1E_127|
#|LETTER1E|LETTER1E_128|
#|LETTER1E|LETTER1E_129|
#|LETTER1E|LETTER1E_130|
#|LETTER1E|LETTER1E_131|
#|LETTER1E|LETTER1E_132|
#|LETTER1E|LETTER1E_133|
#|LETTER1E|LETTER1E_134|
#|LETTER1E|LETTER1E_135|
#|LETTER1E|LETTER1E_136|
#|LETTER1E|LETTER1E_137|
#|LETTER1E|LETTER1E_138|
#|LETTER1E|LETTER1E_139|
#|LETTER1E|LETTER1E_140|
#|LETTER1E|LETTER1E_141|
#|LETTER1E|LETTER1E_142|
#|LETTER1E|LETTER1E_143|
#|LETTER1E|LETTER1E_144|
#|LETTER1E|LETTER1E_145|
#|LETTER1E|LETTER1E_146|
#|LETTER1E|LETTER1E_147|
#|LETTER1E|LETTER1E_148|
#|LETTER1E|LETTER1E_149|
#|LETTER1E|LETTER1E_150|
#|LETTER1E|LETTER1E_151|
#|LETTER1E|LETTER1E_152|
#|LETTER1E|LETTER1E_153|
#|LETTER1E|LETTER1E_154|
#|LETTER1E|LETTER1E_155|
#|LETTER1E|LETTER1E_156|
#|LETTER1E|LETTER1E_157|
#|LETTER1E|LETTER1E_158|
#|LETTER1E|LETTER1E_159|
#|LETTER1E|LETTER1E_160|
#|LETTER1E|LETTER1E_161|
#|LETTER1E|LETTER1E_162|
#|LETTER1E|LETTER1E_163|
#|LETTER1E|LETTER1E_164|
#|LETTER1E|LETTER1E_165|
#|LETTER1E|LETTER1E_166|

  @Verify2ECustomerAccountsWithPDF @AUTOMATED
  Scenario Outline: To Verify the PDF Letter generated for Letter 2E
  #    Jira Key :- CDIC-54
  #    Description:- Verifying English Personal Customer Accounts Mailing Details with PDF document.
    Given the "<NonPersonalCustomer>" and "<ScenarioId>"
    And Fetch the required Customer details from Raw Data extract and Intralink
    Then Connect to the PDF Letter
    Then Fetch the Customer details and validate the details from PDF with the details from Data sheet
    Examples:
      | NonPersonalCustomer | ScenarioId   |
      | LETTER2E            | LETTER2E_1   |
      | LETTER2E            | LETTER2E_2   |
      | LETTER2E            | LETTER2E_3   |
      | LETTER2E            | LETTER2E_4   |
      | LETTER2E            | LETTER2E_5   |
      | LETTER2E            | LETTER2E_6   |
      | LETTER2E            | LETTER2E_7   |
      | LETTER2E            | LETTER2E_8   |
      | LETTER2E            | LETTER2E_9   |
      | LETTER2E            | LETTER2E_10  |
      | LETTER2E            | LETTER2E_11  |
      | LETTER2E            | LETTER2E_12  |
      | LETTER2E            | LETTER2E_13  |
      | LETTER2E            | LETTER2E_14  |
      | LETTER2E            | LETTER2E_15  |
      | LETTER2E            | LETTER2E_16  |
      | LETTER2E            | LETTER2E_17  |
      | LETTER2E            | LETTER2E_18  |
      | LETTER2E            | LETTER2E_19  |
      | LETTER2E            | LETTER2E_20  |
      | LETTER2E            | LETTER2E_21  |
      | LETTER2E            | LETTER2E_22  |
      | LETTER2E            | LETTER2E_23  |
      | LETTER2E            | LETTER2E_24  |
      | LETTER2E            | LETTER2E_25  |
      | LETTER2E            | LETTER2E_26  |
      | LETTER2E            | LETTER2E_27  |
      | LETTER2E            | LETTER2E_28  |
      | LETTER2E            | LETTER2E_29  |
      | LETTER2E            | LETTER2E_30  |
      | LETTER2E            | LETTER2E_31  |
      | LETTER2E            | LETTER2E_32  |
      | LETTER2E            | LETTER2E_33  |
      | LETTER2E            | LETTER2E_34  |
      | LETTER2E            | LETTER2E_35  |
      | LETTER2E            | LETTER2E_36  |
      | LETTER2E            | LETTER2E_37  |
      | LETTER2E            | LETTER2E_38  |
      | LETTER2E            | LETTER2E_39  |
      | LETTER2E            | LETTER2E_40  |
      | LETTER2E            | LETTER2E_41  |
      | LETTER2E            | LETTER2E_42  |
      | LETTER2E            | LETTER2E_43  |
      | LETTER2E            | LETTER2E_44  |
      | LETTER2E            | LETTER2E_45  |
      | LETTER2E            | LETTER2E_46  |
      | LETTER2E            | LETTER2E_47  |
      | LETTER2E            | LETTER2E_48  |
      | LETTER2E            | LETTER2E_49  |
      | LETTER2E            | LETTER2E_50  |
      | LETTER2E            | LETTER2E_51  |
      | LETTER2E            | LETTER2E_52  |
      | LETTER2E            | LETTER2E_53  |
      | LETTER2E            | LETTER2E_54  |
      | LETTER2E            | LETTER2E_55  |
      | LETTER2E            | LETTER2E_56  |
      | LETTER2E            | LETTER2E_57  |
      | LETTER2E            | LETTER2E_58  |
      | LETTER2E            | LETTER2E_59  |
      | LETTER2E            | LETTER2E_60  |
      | LETTER2E            | LETTER2E_61  |
      | LETTER2E            | LETTER2E_62  |
      | LETTER2E            | LETTER2E_63  |
      | LETTER2E            | LETTER2E_64  |
      | LETTER2E            | LETTER2E_65  |
      | LETTER2E            | LETTER2E_66  |
      | LETTER2E            | LETTER2E_67  |
      | LETTER2E            | LETTER2E_68  |
      | LETTER2E            | LETTER2E_69  |
      | LETTER2E            | LETTER2E_70  |
      | LETTER2E            | LETTER2E_71  |
      | LETTER2E            | LETTER2E_72  |
      | LETTER2E            | LETTER2E_73  |
      | LETTER2E            | LETTER2E_74  |
      | LETTER2E            | LETTER2E_75  |
      | LETTER2E            | LETTER2E_76  |
      | LETTER2E            | LETTER2E_77  |
      | LETTER2E            | LETTER2E_78  |
      | LETTER2E            | LETTER2E_79  |
      | LETTER2E            | LETTER2E_80  |
      | LETTER2E            | LETTER2E_81  |
      | LETTER2E            | LETTER2E_82  |
      | LETTER2E            | LETTER2E_83  |
      | LETTER2E            | LETTER2E_84  |
      | LETTER2E            | LETTER2E_85  |
      | LETTER2E            | LETTER2E_86  |
      | LETTER2E            | LETTER2E_87  |
      | LETTER2E            | LETTER2E_88  |
      | LETTER2E            | LETTER2E_89  |
      | LETTER2E            | LETTER2E_90  |
      | LETTER2E            | LETTER2E_91  |
      | LETTER2E            | LETTER2E_92  |
      | LETTER2E            | LETTER2E_93  |
      | LETTER2E            | LETTER2E_94  |
      | LETTER2E            | LETTER2E_95  |
      | LETTER2E            | LETTER2E_96  |
      | LETTER2E            | LETTER2E_97  |
      | LETTER2E            | LETTER2E_98  |
      | LETTER2E            | LETTER2E_99  |
      | LETTER2E            | LETTER2E_100 |
      | LETTER2E            | LETTER2E_101 |
      | LETTER2E            | LETTER2E_102 |
      | LETTER2E            | LETTER2E_103 |
      | LETTER2E            | LETTER2E_104 |
      | LETTER2E            | LETTER2E_105 |
      | LETTER2E            | LETTER2E_106 |
      | LETTER2E            | LETTER2E_107 |
      | LETTER2E            | LETTER2E_108 |
      | LETTER2E            | LETTER2E_109 |
      | LETTER2E            | LETTER2E_110 |
      | LETTER2E            | LETTER2E_111 |
      | LETTER2E            | LETTER2E_112 |
      | LETTER2E            | LETTER2E_113 |
      | LETTER2E            | LETTER2E_114 |
      | LETTER2E            | LETTER2E_115 |
      | LETTER2E            | LETTER2E_116 |
      | LETTER2E            | LETTER2E_117 |
      | LETTER2E            | LETTER2E_118 |
      | LETTER2E            | LETTER2E_119 |
      | LETTER2E            | LETTER2E_120 |
      | LETTER2E            | LETTER2E_121 |
      | LETTER2E            | LETTER2E_122 |
      | LETTER2E            | LETTER2E_123 |
      | LETTER2E            | LETTER2E_124 |
      | LETTER2E            | LETTER2E_125 |
      | LETTER2E            | LETTER2E_126 |
      | LETTER2E            | LETTER2E_127 |
      | LETTER2E            | LETTER2E_128 |
      | LETTER2E            | LETTER2E_129 |
      | LETTER2E            | LETTER2E_130 |
      | LETTER2E            | LETTER2E_131 |
      | LETTER2E            | LETTER2E_132 |
      | LETTER2E            | LETTER2E_133 |
      | LETTER2E            | LETTER2E_134 |
      | LETTER2E            | LETTER2E_135 |
      | LETTER2E            | LETTER2E_136 |
      | LETTER2E            | LETTER2E_137 |
      | LETTER2E            | LETTER2E_138 |
      | LETTER2E            | LETTER2E_139 |

  @Verify3ECustomerAccountsWithPDF @AUTOMATED
  Scenario Outline: To Verify the PDF Letter generated for Letter 3E
 #    Jira Key :- CDIC-54
 #    Description:- Verifying English Personal Customer Accounts Mailing Details with PDF document.
    Given the "<NonPersonalCustomer>" and "<ScenarioId>"
    And Fetch the required Customer details from Raw Data extract and Intralink
    Then Connect to the PDF Letter
    Then Fetch the Customer details and validate the details from PDF with the details from Data sheet
    Examples:
      | NonPersonalCustomer | ScenarioId  |
      | LETTER3E            | LETTER3E_1  |
      | LETTER3E            | LETTER3E_2  |
      | LETTER3E            | LETTER3E_3  |
      | LETTER3E            | LETTER3E_4  |
      | LETTER3E            | LETTER3E_5  |
      | LETTER3E            | LETTER3E_6  |
      | LETTER3E            | LETTER3E_7  |
      | LETTER3E            | LETTER3E_8  |
      | LETTER3E            | LETTER3E_9  |
      | LETTER3E            | LETTER3E_10 |
      | LETTER3E            | LETTER3E_11 |
      | LETTER3E            | LETTER3E_12 |
      | LETTER3E            | LETTER3E_13 |
      | LETTER3E            | LETTER3E_14 |
      | LETTER3E            | LETTER3E_15 |
      | LETTER3E            | LETTER3E_16 |
      | LETTER3E            | LETTER3E_17 |
      | LETTER3E            | LETTER3E_18 |
      | LETTER3E            | LETTER3E_19 |
      | LETTER3E            | LETTER3E_20 |
      | LETTER3E            | LETTER3E_21 |
      | LETTER3E            | LETTER3E_22 |
      | LETTER3E            | LETTER3E_23 |
      | LETTER3E            | LETTER3E_24 |
      | LETTER3E            | LETTER3E_25 |
      | LETTER3E            | LETTER3E_26 |
      | LETTER3E            | LETTER3E_27 |
      | LETTER3E            | LETTER3E_28 |
      | LETTER3E            | LETTER3E_29 |
      | LETTER3E            | LETTER3E_30 |
      | LETTER3E            | LETTER3E_31 |
      | LETTER3E            | LETTER3E_32 |
      | LETTER3E            | LETTER3E_33 |
      | LETTER3E            | LETTER3E_34 |


  @Verify1FCustomerAccountsWithPDF @AUTOMATED
  Scenario Outline: To Verify the PDF Letter generated for Letter 1F
 #    Jira Key :- CDIC-54
 #    Description:- Verifying English Personal Customer Accounts Mailing Details with PDF document.
    Given the "<PersonalCustomer>" and "<ScenarioId>"
    And Fetch the required Customer details from Raw Data extract and Intralink
    Then Connect to the PDF Letter
    Then Fetch the Customer details and validate the details from PDF with the details from Data sheet
    Examples:
      | PersonalCustomer | ScenarioId |
      | LETTER1F         | LETTER1F_1 |
#|LETTER1F|LETTER1F_2 |
#|LETTER1F|LETTER1F_3 |
#|LETTER1F|LETTER1F_4 |
#|LETTER1F|LETTER1F_5 |
#|LETTER1F|LETTER1F_6 |
#|LETTER1F|LETTER1F_7 |
#|LETTER1F|LETTER1F_8 |
#|LETTER1F|LETTER1F_9 |
#|LETTER1F|LETTER1F_10|
#|LETTER1F|LETTER1F_11|
#|LETTER1F|LETTER1F_12|
#|LETTER1F|LETTER1F_13|
#|LETTER1F|LETTER1F_14|
#|LETTER1F|LETTER1F_15|
#|LETTER1F|LETTER1F_16|
#|LETTER1F|LETTER1F_17|
#|LETTER1F|LETTER1F_18|
#|LETTER1F|LETTER1F_19|
#|LETTER1F|LETTER1F_20|
#|LETTER1F|LETTER1F_21|
#|LETTER1F|LETTER1F_22|
#|LETTER1F|LETTER1F_23|
#|LETTER1F|LETTER1F_24|
#|LETTER1F|LETTER1F_25|
#|LETTER1F|LETTER1F_26|
#|LETTER1F|LETTER1F_27|
#|LETTER1F|LETTER1F_28|
#|LETTER1F|LETTER1F_29|
#|LETTER1F|LETTER1F_30|
#|LETTER1F|LETTER1F_31|
#|LETTER1F|LETTER1F_32|
#|LETTER1F|LETTER1F_33|
#|LETTER1F|LETTER1F_34|


  @Verify2FCustomerAccountsWithPDF @AUTOMATED
  Scenario Outline: To Verify the PDF Letter generated for Letter 2F
#    Jira Key :- CDIC-62
#    Description:- Verifying French Non-Personal Customer Accounts Mailing Details with PDF document.

    Given the "<NonPersonalCustomer>" and "<ScenarioId>"
    And Fetch the required Customer details from Raw Data extract and Intralink
    Then Connect to the PDF Letter
    Then Fetch the Customer details and validate the details from PDF with the details from Data sheet
    Examples:
      | NonPersonalCustomer | ScenarioId  |
      | LETTER2F            | LETTER2F_1  |
      | LETTER2F            | LETTER2F_2  |
      | LETTER2F            | LETTER2F_3  |
      | LETTER2F            | LETTER2F_4  |
      | LETTER2F            | LETTER2F_5  |
      | LETTER2F            | LETTER2F_6  |
      | LETTER2F            | LETTER2F_7  |
      | LETTER2F            | LETTER2F_8  |
      | LETTER2F            | LETTER2F_9  |
      | LETTER2F            | LETTER2F_10 |
      | LETTER2F            | LETTER2F_11 |
      | LETTER2F            | LETTER2F_12 |
      | LETTER2F            | LETTER2F_13 |
      | LETTER2F            | LETTER2F_14 |
      | LETTER2F            | LETTER2F_15 |
      | LETTER2F            | LETTER2F_16 |
      | LETTER2F            | LETTER2F_17 |
      | LETTER2F            | LETTER2F_18 |
      | LETTER2F            | LETTER2F_19 |
      | LETTER2F            | LETTER2F_20 |
      | LETTER2F            | LETTER2F_21 |
      | LETTER2F            | LETTER2F_22 |
      | LETTER2F            | LETTER2F_23 |
      | LETTER2F            | LETTER2F_24 |
      | LETTER2F            | LETTER2F_25 |
      | LETTER2F            | LETTER2F_26 |
      | LETTER2F            | LETTER2F_27 |
      | LETTER2F            | LETTER2F_28 |
      | LETTER2F            | LETTER2F_29 |
      | LETTER2F            | LETTER2F_30 |


  @Verify3FCustomerAccountsWithPDF @AUTOMATED
  Scenario Outline: To Verify the PDF Letter generated for Letter 3F
#    Jira Key :- CDIC-64
#    Description:- Verifying French Non-Personal Customer Accounts Mailing Details with PDF document.

    Given the "<NonPersonalCustomer>" and "<ScenarioId>"
    And Fetch the required Customer details from Raw Data extract and Intralink
    Then Connect to the PDF Letter
    Then Fetch the Customer details and validate the details from PDF with the details from Data sheet
    Examples:
      | NonPersonalCustomer | ScenarioId  |
      | LETTER3F            | LETTER3F_1  |
      | LETTER3F            | LETTER3F_2  |
      | LETTER3F            | LETTER3F_3  |
      | LETTER3F            | LETTER3F_4  |
      | LETTER3F            | LETTER3F_5  |
      | LETTER3F            | LETTER3F_6  |
      | LETTER3F            | LETTER3F_7  |
      | LETTER3F            | LETTER3F_8  |
      | LETTER3F            | LETTER3F_9  |
      | LETTER3F            | LETTER3F_10 |
      | LETTER3F            | LETTER3F_11 |
      | LETTER3F            | LETTER3F_12 |
      | LETTER3F            | LETTER3F_13 |
      | LETTER3F            | LETTER3F_14 |


  @Verify2FPNTACustomerAccountsWithPDF @AUTOMATED
  Scenario Outline: To Verify the PDF Letter generated for Letter 2FPNTA
#    Jira Key :- CDIC-70
#    Description:- Verifying French Non-Personal Customer PNTA Accounts Mailing Details with PDF document.

    Given the "<NonPersonalCustomer>" and "<ScenarioId>"
    And Fetch the required Customer details from Raw Data extract and Intralink
    Then Connect to the PDF Letter
    Then Fetch the Customer details and validate the details from PDF with the details from Data sheet
    Examples:
      | NonPersonalCustomer | ScenarioId      |
      | LETTER2FPNTA        | LETTER2F_PNTA_1 |
      | LETTER2FPNTA        | LETTER2F_PNTA_2 |
      | LETTER2FPNTA        | LETTER2F_PNTA_3 |
      | LETTER2FPNTA        | LETTER2F_PNTA_4 |
      | LETTER2FPNTA        | LETTER2F_PNTA_5 |
      | LETTER2FPNTA        | LETTER2F_PNTA_6 |
      | LETTER2FPNTA        | LETTER2F_PNTA_7 |


  @Verify3FPNTACustomerAccountsWithPDF @AUTOMATED
  Scenario Outline: To Verify the PDF Letter generated for Letter 3F PNTA
#    Jira Key :- CDIC-72
#    Description:- Verifying French Non-Personal Customer PNTA Accounts Mailing Details with PDF document.

    Given the "<NonPersonalCustomer>" and "<ScenarioId>"
    And Fetch the required Customer details from Raw Data extract and Intralink
    Then Connect to the PDF Letter
    Then Fetch the Customer details and validate the details from PDF with the details from Data sheet
    Examples:
      | NonPersonalCustomer | ScenarioId      |
      | Letter3FPNTA        | LETTER3F_PNTA_1 |


  @Verify2EPNTACustomerAccountsWithPDF @AUTOMATED
  Scenario Outline: To Verify the PDF Letter generated for Letter 2E PNTA
#    Jira Key :- CDIC-66
#    Description:- Verifying English Non-Personal Customer PNTA Accounts Mailing Details with PDF document.

    Given the "<NonPersonalCustomer>" and "<ScenarioId>"
    And Fetch the required Customer details from Raw Data extract and Intralink
    Then Connect to the PDF Letter
    Then Fetch the Customer details and validate the details from PDF with the details from Data sheet
    Examples:
      | NonPersonalCustomer | ScenarioId       |
      | LETTER2EPNTA        | LETTER2E_PNTA_1  |
      | LETTER2EPNTA        | LETTER2E_PNTA_2  |
      | LETTER2EPNTA        | LETTER2E_PNTA_3  |
      | LETTER2EPNTA        | LETTER2E_PNTA_4  |
      | LETTER2EPNTA        | LETTER2E_PNTA_5  |
      | LETTER2EPNTA        | LETTER2E_PNTA_6  |
      | LETTER2EPNTA        | LETTER2E_PNTA_7  |
      | LETTER2EPNTA        | LETTER2E_PNTA_8  |
      | LETTER2EPNTA        | LETTER2E_PNTA_9  |
      | LETTER2EPNTA        | LETTER2E_PNTA_10 |
      | LETTER2EPNTA        | LETTER2E_PNTA_11 |
      | LETTER2EPNTA        | LETTER2E_PNTA_12 |
      | LETTER2EPNTA        | LETTER2E_PNTA_13 |
      | LETTER2EPNTA        | LETTER2E_PNTA_14 |
      | LETTER2EPNTA        | LETTER2E_PNTA_15 |
      | LETTER2EPNTA        | LETTER2E_PNTA_16 |
      | LETTER2EPNTA        | LETTER2E_PNTA_17 |
      | LETTER2EPNTA        | LETTER2E_PNTA_18 |
      | LETTER2EPNTA        | LETTER2E_PNTA_19 |
      | LETTER2EPNTA        | LETTER2E_PNTA_20 |
      | LETTER2EPNTA        | LETTER2E_PNTA_21 |
      | LETTER2EPNTA        | LETTER2E_PNTA_22 |
      | LETTER2EPNTA        | LETTER2E_PNTA_23 |
      | LETTER2EPNTA        | LETTER2E_PNTA_24 |
      | LETTER2EPNTA        | LETTER2E_PNTA_25 |
      | LETTER2EPNTA        | LETTER2E_PNTA_26 |
      | LETTER2EPNTA        | LETTER2E_PNTA_27 |


  @Verify3EPNTACustomerAccountsWithPDF @AUTOMATED
  Scenario Outline: To Verify the PDF Letter generated for Letter 3EPNTA
#    Jira Key :- CDIC-68
#    Description:- Verifying English Non-Personal Customer PNTA Accounts Mailing Details with PDF document.

    Given the "<NonPersonalCustomer>" and "<ScenarioId>"
    And Fetch the required Customer details from Raw Data extract and Intralink
    Then Connect to the PDF Letter
    Then Fetch the Customer details and validate the details from PDF with the details from Data sheet
    Examples:
      | NonPersonalCustomer | ScenarioId       |
      | LETTER3EPNTA        | LETTER3E_PNTA_1  |
      | LETTER3EPNTA        | LETTER3E_PNTA_2  |
      | LETTER3EPNTA        | LETTER3E_PNTA_3  |
      | LETTER3EPNTA        | LETTER3E_PNTA_4  |
      | LETTER3EPNTA        | LETTER3E_PNTA_5  |
      | LETTER3EPNTA        | LETTER3E_PNTA_6  |
      | LETTER3EPNTA        | LETTER3E_PNTA_7  |
      | LETTER3EPNTA        | LETTER3E_PNTA_8  |
      | LETTER3EPNTA        | LETTER3E_PNTA_9  |
      | LETTER3EPNTA        | LETTER3E_PNTA_10 |
      | LETTER3EPNTA        | LETTER3E_PNTA_11 |
      | LETTER3EPNTA        | LETTER3E_PNTA_12 |
      | LETTER3EPNTA        | LETTER3E_PNTA_13 |
      | LETTER3EPNTA        | LETTER3E_PNTA_14 |
      | LETTER3EPNTA        | LETTER3E_PNTA_15 |
      | LETTER3EPNTA        | LETTER3E_PNTA_16 |
      | LETTER3EPNTA        | LETTER3E_PNTA_17 |