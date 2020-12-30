Feature: User bookmarks article

  User journey that exercise his usage of
  app functionality to bookmark news article

  Scenario: User bookmarks article
    Given User launched app from device home
    And Open news screen
    When User bookmarks first listed article
    And Open bookmarks screen
    Then Bookmarked article should be listed

