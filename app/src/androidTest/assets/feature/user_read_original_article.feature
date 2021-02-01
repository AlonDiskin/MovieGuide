Feature: User read article on its origin web url

  user journey that exercise his usage of
  app functionality to read news article on its web url

  Scenario: User open article origin
    Given User launched app from device home
    And Open news screen
    When User selects to read first listed article
    And Select to read original published article
    Then App should open article web url