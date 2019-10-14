@FileRipper
Feature: File Ripper Business Features

  Scenario Outline: A delimited file is processed by file ripper
    Given a file whose fields are separated by a '<delimiter>'
    When the file is ripped
    Then the file data is returned as json

    Examples:
      | delimiter |
      | \|        |
      | \t        |
      | ,         |
      | .         |

  Scenario: A fixed file is processed by file ripper
    Given a file whose fields are of fixed width
    When the file is ripped
    Then the file data is returned as json

  Scenario: An xml file is processed by file ripper
    Given a file in xml format
    When the file is ripped
    Then the file data is returned as json
