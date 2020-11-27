Feature: News content engagement

  User engagement rules for news content

  #Rule: Allow content sharing

  @headline-shared
  Scenario: Movie news headline shared
    Given User opened news headline screen
    Then All headlines should provide sharing option
    When User select to share the first headline
    Then App should open device share menu

  @article-shared
  Scenario: Movie news article shared
    Given User opened news article screen
    When User select to share the article
    Then App should open device share menu

  # Rule: Enable user to bookmark news items

  @headlines-bookmarked
  Scenario: User bookmarks headlines of interest
    Given User opened news headline screen
    And Bookmarked first 3 headlines
    Then App should bookmark headlines
    When User selects to un bookmark headlines
    Then Headlines should be removed from bookmarks

  @article-bookmarked
  Scenario: User bookmarks article
    Given User open article for reading
    And Bookmarks article
    Then App should bookmark article
    When User select to un bookmark article
    Then App should remove article from bookmarks