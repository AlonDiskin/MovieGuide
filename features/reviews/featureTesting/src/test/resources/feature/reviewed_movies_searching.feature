Feature: Reviewed movies searching

  #Rule: Provide query based movies searching

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

 #Rule: Enable search result movie review reading

  @read-searched-review
  Scenario Outline: User read search result review
    Given Reviewed existing movies searched by "<query>"
    When User open movies search screen
    And Perform search for reviewed movies with "<query>" query
    And Open first search resulted move read its review
    Then Movie review detail should be shown
    Examples:
      | query  |
      | hobbit |