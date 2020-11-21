Feature: User change app theme

  user journey that exercise his usage of
  app functionality to select app visual theme

  Scenario: User change app theme
    Given User launched app from device home
    And User open app settings screen
    When User select different app theme
    Then App visual theme should be changed as selected