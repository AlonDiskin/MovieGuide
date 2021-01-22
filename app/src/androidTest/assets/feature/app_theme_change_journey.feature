Feature: User change app theme

  User journey that exercise his usage of
  app functionality to set app visual theme

  Scenario: User change app theme
    Given User launched app from device home
    And User open app settings screen
    When User select different app theme
    And User rotate device
    Then App visual theme should be changed as selected