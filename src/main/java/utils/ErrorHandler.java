package utils;

public class ErrorHandler {

    // 에러 메시지를 생성하는 메서드
    public static String getErrorReason(Throwable t, String ui, String anotherUi, int time) {
        String errorReason;

        switch (t.getClass().getSimpleName()) {
            case "NoSuchElementException":
                errorReason = String.format("UI 요소 %s를 찾지 못했습니다.", ui);
                break;
            case "ElementNotVisibleException":
                errorReason = String.format("UI 요소 %s가 화면에 보이지 않습니다.", ui);
                break;
            case "StaleElementReferenceException":
                errorReason = String.format("UI 요소 %s가 새로 로드되거나 화면에서 사라졌습니다.", ui);
                break;
            case "TimeoutException":
                errorReason = String.format("UI 요소 %s가 %d초 동안 화면에 나타나지 않았습니다.", ui, time);
                break;
            case "NoSuchWindowException":
                errorReason = "작업 중인 창을 찾을 수 없습니다.";
                break;
            case "WebDriverException":
                errorReason = "WebDriver가 제대로 연결되지 않았거나 실행되지 않았습니다. 경로를 확인하세요.";
                break;
            case "SessionNotCreatedException":
                errorReason = "WebDriver 세션을 시작할 수 없습니다. 드라이버 버전과 브라우저 버전을 확인하세요.";
                break;
            case "ElementClickInterceptedException":
                errorReason = String.format("클릭하려는 요소 %s가 다른 요소 %s에 의해 가려져 있습니다.", ui, anotherUi);
                break;
            case "HttpRequestException":
                errorReason = "네트워크 요청이 실패했습니다. 인터넷 및 와이파이를 확인하세요!";
                break;
            case "SocketTimeoutException":
                errorReason = "네트워크가 지정된 시간 동안 응답을 받지 못했습니다. 인터넷 및 와이파이를 확인하세요!";
                break;
            case "RuntimeException":
                errorReason = "테스트 진행 시간동안 요소가 나타나지 않았습니다.";
                break;
            default:
                errorReason = "알 수 없는 오류가 발생했습니다. 오류 메시지: " + t.getMessage();
                break;
        }

        return errorReason;
    }
}
