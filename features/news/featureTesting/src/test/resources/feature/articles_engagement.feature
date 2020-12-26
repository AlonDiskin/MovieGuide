Feature: News content engagement

  User engagement rules for news content

  #Rule:news articles sharing

  @listed-article-shared
  Scenario: News listed article shared
    Given User opened news headline screen
    Then All headlines should provide sharing option
    When User select to share the first headline
    Then App should open device share menu

  @article-shared
  Scenario: News article shared
    Given User opened news article screen
    When User select to share the article
    Then App should open device share menu

  # Rule:news articles bookmarking

  @bookmark-article
  Scenario: User bookmark article
    Given User open unbookmarked article for reading
    When User select to bookmark article
    Then Article should be shown as bookmarked
    And App should bookmark article
    When User select to un bookmark article
    Then Article should be shown as not bookmarked
    And App should remove article from bookmarks