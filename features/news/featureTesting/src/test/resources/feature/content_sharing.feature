Feature: News content sharing

  Provides user with news content sharing capabilities

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