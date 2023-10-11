@dqFileCharacteristicsValidation
Feature: DQ File Characteristics validation


  @dqFileCharacteristics
  Scenario: Verify file characteristics for all dq files
    Then file length should be 29 excluding extension
    And file extension should be .txt
    And file count based on extract option
    And column headers and number of columns
    And generate report

  @dqFileNameElements
  Scenario: Verify file name elements for all dq files
    Given verify mi for all files
    And verify 23 position value of all files
    And generate report

  @dqMissingFiles
  Scenario Outline: Verify missing files report with respect to DQ Integrity file
    Given verify tables "<Table>" reference table "<ReferenceTable>"
    Examples:
      | Table | ReferenceTable                                         |
      | 0100  | 0202,0201                                              |
      | 0110  | 0100,0211                                              |
      | 0120  | 0100,0221                                              |
      | 0121  | 0100,0233                                              |
      | 0130  | 0800,0900,0231,0232,0233,0234,0235,0236,0237,0238,0239 |
      | 0140  | 0130,0241,0233                                         |
      | 0152  | 0130                                                   |
      | 0153  | 0130                                                   |
      | 0160  | 0130,0234                                              |
      | 0211  | 0212                                                   |
      | 0231  | 0240                                                   |
      | 0242  | 0233                                                   |
      | 0400  | 0401,0233                                              |
      | 0500  | 0100,0130,0501                                         |
      | 0800  | 0999,0235,0233                                         |
      | 0900  | 0999,0233                                              |