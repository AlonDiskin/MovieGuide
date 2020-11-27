Feature: Bookmarked news content browsing

  #Rule: list all bookmarked news items by sorting criteria

  @list-bookmarks
  Scenario: User read bookmark
    Given User has previously bookmarked news
    And User open bookmarks screen
    Then All bookmarks are listed by newest first
    When User select to sort bookmarks by oldest first
    Then Bookmarks should be sorted by oldest
    When User select to read the first bookmark
    Then Bookmarked news article should be shown