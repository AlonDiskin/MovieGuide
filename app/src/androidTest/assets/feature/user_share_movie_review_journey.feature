Feature: User movie review sharing

  user journey that exercise his usage of
  app functionality to share a movie review

  Scenario: User share review
    Given User launched app from device home
    And Opened movie reviews screen
    When User selects to read first listed review
    And Read movie review
    When User select to share review
    Then Sharing menu should be displayed