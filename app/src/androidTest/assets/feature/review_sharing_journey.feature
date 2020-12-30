Feature: User share movie review

  user journey that exercise his usage of
  app functionality to share movie review

  Scenario: User share movie review
    Given User launched app from device home
    And Open movie reviews screen
    When User selects to read first listed review
    And User share review
    Then App should share review url