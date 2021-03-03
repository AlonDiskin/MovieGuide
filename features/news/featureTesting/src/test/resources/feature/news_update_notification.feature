Feature: News update notification

  Notification service to alert user for unread published news articles.

  #Rule: Show notification in status bar upon unread available news

  @notify-user
  Scenario Outline: Show notification in user device
    Given Unread by user articles are published
    And News notification is disabled
    When User enable news update notification
    And App currently usage is "<active_usage>"
    Then App should "<notify_user>" user upon notification service run
    Examples:
      | active_usage      | notify_user |
      | in foreground     | not notify  |
      | not in foreground | notify      |

  #Rule: Open news screen when user tap notification

  @notification-tap
  Scenario: News screen opened when notification tapped
    Given News notification is shown
    When User tap on notification
    Then App should launch and open news screen