Feature: App visual theme selection preference

  # Rule: Provide app visual theme customization via app settings

    @theme-changed
    Scenario: User change app theme
      Given App theme has not been changed by user
      And User open settings screen
      Then App theme should be set to the default
      When User select different theme
      Then App theme should change as selected