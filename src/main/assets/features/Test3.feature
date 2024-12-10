Feature: 11번가

  @SCRUM-308
  Scenario: 비로그인시 구매 막힘 확인
    Given 초기화면
    When 검색 버튼 클릭
    When 검색 상세 버튼 클릭
    When 검색값 "bag" 입력
    When 검색화면 첫번째 클릭
    When 구매하기 클릭
    When 바로구매 버튼 클릭
    Then 로그인 ui 뜨는거 확인




