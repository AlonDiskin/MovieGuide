Feature: Movie reviews browsing

  Enable user movie reviews content browsing

  #Rule: List all reviewed movies by sorting criteria

  @reviewed-movies-listed
  Scenario Outline: Reviewed movies listed by sorting
    Given User opened movie reviews screen
    Then Reviewed movies should be listed and sorted by movie popularity in desc order
    When User select "<sorting>" sorting
    Then Reviewed movies should be listed and sorted by "<sorting>" in desc order
    Examples:
      | sorting      |
      | rating       |
      | release date |

  @reviews-listed-errors
  Scenario Outline: Reviews listing browsing errors handling
    Given Existing app error due to "<error_cause>"
    When User open reviews screen
    Then User should be notifies with message describing error
    And  Provided with retry option
    When Error "<error_cause>" is resolved
    And User select to retry
    Then Reviews should be shown in reviews screen
    Examples:
      | error_cause       |
      | device networking |
      | remote server     |

  #Rule: Provide movie review detail reading

  @review-reading
  Scenario: Movie review is read by user
    Given User opened movies screen
    When User selects first shown movie
    Then Review screen should be opened
    And Review detail should be shown

  @review-reading-errors
  Scenario Outline: Review reading error handling
    Given Existing app error due to "<error_cause>"
    When User open movie review screen
    Then User should be notifies with message describing error
    And  Provided with retry option
    When Error "<error_cause>" is resolved
    And User select to retry
    Then Movie Review detail should be shown
    Examples:
      | error_cause       |
      | device networking |
      | remote server     |
