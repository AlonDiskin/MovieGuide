Feature: Enable new reading

  Enable movie news content reading and engagement

  Rule: list all current news items as headlines

    @headlines-listed
    Scenario: Headlines are listed
      Given User opened news headlines screen
      When User scrolls to bottom of ui
      Then All headlines should be listed sorted descending by date
      When User refresh headlines
      Then Updated headlines should be shown

    @headlines-listing-errors
    Scenario Outline: Headlines listing fail
      Given Existing app error due to "<error_cause>"
      When User open news headlines screen
      Then User should be notifies with message describing error
      And  Should be provided with retry option
      Examples:
        | error_cause         |
        | device networking   |
        | remote server       |