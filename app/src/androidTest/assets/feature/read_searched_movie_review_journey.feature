Feature: User search for movie

  user journey that exercise his usage of
  app functionality to perform movies search and read a result review

  Scenario Outline: User search for movie
    Given User launched app from device home
    And Open movies screen
    And User perform a search with the query "<query>"
    And User read first resulted review detail
    Then Movie review detail should be shown
    Examples:
      | query      |
      | terminator |