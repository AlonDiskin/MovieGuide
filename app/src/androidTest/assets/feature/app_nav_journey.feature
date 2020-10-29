Feature: App features navigation

  user journey that exercise his usage of
  app functionality to navigate through app features

  Scenario Outline: User navigate through app
    Given User launched app from device home
    Then Movie news should be shown in home screen
    When User navigates to "<feature>"
    Then "<feature>" ui should be shown
    Examples:
      | feature  |
      | reviews  |
      | settings |