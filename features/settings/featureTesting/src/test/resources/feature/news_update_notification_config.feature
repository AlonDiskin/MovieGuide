Feature: News update notification preference

  #Rule: Control news update service activation via app settings

  @service-activation
  Scenario: User config notification activation
    Given User has not changed news notification setting from default
    When User open settings screen
    Then News notification should be disabled
    When User enables news update notification
    Then App should enable news update notification service
    When User disable news notification update
    Then App should disable news update notification service