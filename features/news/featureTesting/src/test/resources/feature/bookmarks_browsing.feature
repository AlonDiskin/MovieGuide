Feature: Bookmarked news content browsing

  #Rule: list all bookmarked news items by sorting criteria

  @list-bookmarks
  Scenario Outline: User read bookmark
    Given User has previously bookmarked news
    And User open bookmarks screen
    Then All bookmarks are listed by 'news first' sorting
    When User select to sort bookmarks by "<sorting>"
    Then Bookmarks should be sorted as "<sorting>"
    When User select to read the first bookmark
    Then Bookmarked news article should be shown
    Examples:
      | sorting  |
      | oldest   |
      | newest   |