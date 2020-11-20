Feature: User article sharing

  user journey that exercise his usage of
  app functionality to share a movie news article

  Scenario: User share article
    Given User launched app from device home
    And Opened movie news screen
    When User selects to read first headline article
    And Read article content
    When User open article sharing menu
    Then Sharing menu should be displayed

