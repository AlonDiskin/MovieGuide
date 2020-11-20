Feature: Movies reviews content engagement

  Enable user content actions on movies reviews

    #Rule: Enable review content sharing

    @review-shared
    Scenario: Movie review is shared
    Given User read movie review
    When User Selects to share review data
    Then Review trailer should be shared via device sharing sheet
