Feature: Available unread article notification

  user journey that exercise his usage of
  app functionality to be notified upon available unread
  movie news articles

  Scenario: User notified upon available articles
    Given User launched app from device home
    And Enabled news update notification service
    And New unread by user articles are published
    Then App should show a status bar notification