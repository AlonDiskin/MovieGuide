Feature: Reviewed movies searching

  Provide query based movies searching

  @movies-search
  Scenario Outline: User search for movies
    Given Reviewed movies containing "<query>" in title are "<existing>"
    When User open movies search screen
    And Perform search for reviewed movies with "<query>" query
    Then Search results should be shown for "<existing>" movies
    Examples:
      | query      | existing   |
      | terminator | true       |
      | cute cats  | false      |