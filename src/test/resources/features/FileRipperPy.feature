@FileRipper
Feature: File Ripper Python

  Scenario: File ripper looks up files and processes them
    Given fixed files stored on file system
    And a fixed file definition
    And file definition has input directory, file mask
    When the files are found and ripped
    Then data is returned for all files
    And files are still in input directory

  Scenario: File ripper looks up files, processes them, and moves them to completed
    Given fixed files stored on file system
    And a fixed file definition
    And file definition has input directory, file mask
    And file definition has completed directory
    When the files are found and ripped
    Then data is returned for all files
    And files are in completed directory

  Scenario: A delimited file and a fixed file definition
    Given a file whose fields are separated by a ","
    And a fixed file definition
    When the file is ripped
    Then file ripper throws exception
    And exception contains "Invalid line length in fixed width file."

  Scenario: A delimited file and a xml file definition
    Given a file whose fields are separated by a "|"
    And a xml file definition
    When the file is ripped
    Then file ripper throws exception
    And exception contains "Input file is not valid XML"

  Scenario: A fixed file and a xml file definition
    Given a file whose fields are of fixed width
    And a xml file definition
    When the file is ripped
    Then file ripper throws exception
    And exception contains "Input file is not valid XML"

  Scenario: A fixed file and a delimited file definition
    Given a file whose fields are of fixed width
    And a delimited file definition with "|"
    When the file is ripped
    Then file ripper throws exception
    And exception contains "Record '.*' has invalid number of fields"

  Scenario: A xml file and a delimited file definition
    Given a file in xml format
    And a delimited file definition with "\t"
    When the file is ripped
    Then file ripper throws exception
    And exception contains "Record '.*' has invalid number of fields"

  Scenario: A xml file and a fixed file definition
    Given a file in xml format
    And a fixed file definition
    When the file is ripped
    Then file ripper throws exception
    And exception contains "Invalid line length in fixed width file."