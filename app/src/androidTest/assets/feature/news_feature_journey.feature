Feature: News features user usage

  this feature files contains user journey scenarios that exercise user usage of
  app news feature functionality

  Scenario: User share article
    Given User launched app from device home
    And Opened movie news screen
    And Read first shown headline
    When User selects to read first headline article
    And Open article sharing menu
    Then Sharing menu should be displayed

