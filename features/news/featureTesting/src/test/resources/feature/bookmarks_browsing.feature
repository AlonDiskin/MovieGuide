Feature: Bookmarked articles browsing

  #Rule:list all bookmarked articles

  @list-bookmarks
  Scenario: Bookmarks listed
    Given User has previously bookmarked news
    And User open bookmarks screen
    Then All bookmarks are listed by newest first
    When User select to sort bookmarks by oldest first
    Then Bookmarks should be sorted by oldest

  #Rule:enable bookmarked articles reading

  @read-bookmark
  Scenario: User read bookmarked article
    Given User has previously bookmarked article
    And User open bookmarks screen
    When User select to read the bookmarked article
    Then Bookmarked article should be shown
