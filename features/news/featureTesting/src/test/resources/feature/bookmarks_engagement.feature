Feature: Bookmarked content engagement

  #Rule:share bookmarked article

  @share-bookmark
  Scenario: User share bookmark
    Given User has previously bookmarked article
    And User open bookmarks screen
    When Select to share bookmark
    Then Bookmark article link should be shared

  #Rule:delete bookmarked articles

  @delete-bookmarks
  Scenario: Bookmarks deleted
    Given User has previously bookmarked articles
    And User open bookmarks screen
    When User select to delete all bookmarks
    Then Bookmarks screen should be updated to show no items
    And All bookmarks should be deleted