Feature: News content bookmarking

  user journey that exercise his usage of
  app functionality to perform bookmarking operations on news content

  Scenario: User perform news content bookmarking actions
    Given User launched app from device home
    And Opened article from a unbookmarked headline in news screen
    And Bookmarked article
    And Opened bookmarked news headlines screen
    Then Bookmarked article should be listed

