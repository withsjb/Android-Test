@Epic-SCRUM-30
Feature: 11번가


  Scenario: 광고 팝업 종료
    Given 11번가 실행
    When 광고 팝업 클로즈
    When 수신 정보 동의안함
    When 알림 서비스 확인
    When 전용혜택안내x버튼 누르기



  Scenario: 일부로 실패
    Then 일부로 실패
