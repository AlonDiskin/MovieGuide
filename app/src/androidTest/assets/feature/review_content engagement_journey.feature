Feature: User movie review content engagement

  user journey that exercise his usage of
  app functionality to read and engage with movie review content

  Scenario: User engage movie review content
    Given User launched app from device home
    And Opened movie reviews screen
    When User selects to read first listed review
    And Read movie review
    When User select to share review
    Then Review web url should be shared
    When User select to view movie trailer
    Then App should send user to device app for trailer viewing