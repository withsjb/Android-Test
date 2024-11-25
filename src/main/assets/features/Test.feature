Feature: 11번가


  Scenario: 광고 팝업 종료
    Given 11번가 실행
    When 광고 팝업 클로즈
    When 수신 정보 동의안함
    When 알림 서비스 확인
    When 전용혜택안내x버튼 누르기


  Scenario: 로그인
    Given 11번가 실행
    When 로그인 클릭광
    Then 로그인 화면 이동
    Then 임시 테스트용13456

#
#  Scenario: 명품개런티
#    Given 11번가 실행
#    When  명품개런티 버튼 클릭
#    Then 개런티 화면 이동