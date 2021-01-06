Feature: Movies reviews content engagement

  Enable user content actions on movies reviews

  #Rule: Enable review content sharing

  @review-shared
  Scenario: Movie review is shared
    Given User read movie review
    When User Selects to share review data
    Then Review web url should be shared

  #Rule: Play selected trailer via existing device app

  @movie-trailer-played
  Scenario: User select movie trailer to view
    Given User read movie review that has movie trailers web links
    When User selects to view the first trailer
    Then App should send user to view trailer via other device app

  #Rule:allow to favorite movies

  @favorite-movie
  Scenario: User add movie to favorites
    Given User has no favorite movies
    And User open movie detail screen
    When User select to favorite movie
    Then Movie should be shown as favorite
    And Movie should be added to user favorite movies
    When User select to un favorite movie
    Then Movie should be shown as not favorite
    And Movie should be removed from user favorite movies

