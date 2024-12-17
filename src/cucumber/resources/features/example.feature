Feature: Test Healthcheck

  Scenario: Should return 200 OK
    Given the app is running
    When a GET request is sent to "/healthcheck"
    Then a 200 response is returned

  Scenario: Should return 401 Unauthorized
    Given the app is running
    And a 200 is returned from the external app
    When a GET request is sent to "/fetch/external"
    Then a 200 response is returned
    And a "GET" request was made to the external server