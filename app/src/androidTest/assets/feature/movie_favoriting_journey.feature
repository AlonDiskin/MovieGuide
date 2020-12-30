Feature: User add movie to favorites list

  User journey that exercise his usage of
  app functionality to favorite an existing movie

  Scenario: User favorite a movie
    Given User launched app from device home
    And Open movies screen
    When User favorite first listed movie
    And Select to see all favorite movies
    Then Previously favorite movie should be shown