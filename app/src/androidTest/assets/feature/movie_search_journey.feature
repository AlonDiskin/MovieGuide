Feature: User search for movie

  user journey that exercise his usage of
  app functionality to perform movies search

  Scenario Outline: User search for movie
    Given User launched app from device home
    And Open movies screen
    When User perform a search with the query "<query>"
    Then All movies with similar name to "<query>" should be listed
    Examples:
      | query      |
      | terminator |