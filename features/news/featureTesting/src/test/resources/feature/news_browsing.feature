Feature: News browsing

  Enable movie news content reading

  #Rule: list all current news items as headlines

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
      | error_cause       |
      | device networking |
      | remote server     |

  #Rule: enable article reading from headline selection

  @article-reading
  Scenario: Article selected for reading
    Given User opened headlines screen
    When User selects first shown headline
    Then Article screen should be opened
    And Headline article should be shown

  @article-reading-errors
  Scenario Outline: Article reading error handling
    Given Existing app error due to "<error_cause>"
    When User opened article screen
    Then User should be notifies with message describing error
    And  Should "<provide_retry>" retry option
    When Error "<error_cause>" is resolved
    And User "<select_retry>" retry
    Then Article screen should "<display>" article
    Examples:
      | error_cause       | provide_retry | select_retry | display     |
      | device networking | provide       | select       | display     |
      | remote server     | provide       | select       | display     |