Feature: User share article

  User journey that exercise his usage of
  app functionality to share a news article

  Scenario: User share article
    Given User launched app from device home
    And Open news screen
    When User selects to read first listed article
    And User share article
    Then App should share article url

