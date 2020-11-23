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
