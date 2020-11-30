Feature: News articles browsing

  #Rule:list all current news articles

  @list-articles
  Scenario: Articles are listed
    Given User opened articles screen
    When User scrolls to bottom of ui
    Then All articles should be listed sorted descending by date
    When User refresh content
    Then Updated articles should be shown

  @articles-listing-errors
  Scenario Outline: Headlines listing fail
    Given Existing app error due to "<error_cause>"
    When User open news headlines screen
    Then User should be notifies with message describing error
    And  Should be provided with retry option
    Examples:
      | error_cause       |
      | device networking |
      | remote server     |

  #Rule:news article reading

  @article-reading
  Scenario: Article selected for reading
    Given User opened articles screen
    When User selects first shown article
    And Article should be shown

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