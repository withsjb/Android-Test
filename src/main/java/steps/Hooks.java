package steps;

import io.cucumber.java.AfterStep;
import io.cucumber.java.Scenario;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import utils.AndroidManager;
import utils.Jirafeatureissue;

import java.io.*;
import java.net.MalformedURLException;

public class Hooks {

    private WebDriver driver;
    private StringBuilder logBuffer = new StringBuilder(); // 로그 수집용 버퍼
    private PrintStream originalOut = System.out; // 원래 System.out 저장
    private PrintStream originalErr = System.err; // 원래 System.err 저장
    private ByteArrayOutputStream logStream = new ByteArrayOutputStream(); // 일반 로그 스트림
    private ByteArrayOutputStream errorStream = new ByteArrayOutputStream(); // 에러 로그 스트림

    private static Scenario scenario;

    public Hooks() {
    }

    public Hooks(WebDriver driver) {
        this.driver = driver;
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        Hooks.scenario = scenario;
    }

    public static Scenario getScenario() {
        return scenario;
    }

    @Before
    public void startapp() throws MalformedURLException {
        System.out.println("before 테스트 정상 실행");
        logBuffer.append("before 테스트 정상 실행\n");

        if (driver == null) {
            driver = AndroidManager.getDriver();
        }

        // 콘솔 출력 스트림 가로채기
        PrintStream logPrintStream = new PrintStream(logStream);
        PrintStream errorPrintStream = new PrintStream(errorStream);
        System.setOut(logPrintStream);
        System.setErr(errorPrintStream);
    }

    @After
    public void finisapp() {
        System.out.println("after 테스트 진입");
        logBuffer.append("after 테스트 진입\n");

        Jirafeatureissue.featureupload(new String[]{});
        System.out.println("after 테스트 정상 실행");
        logBuffer.append("after 테스트 정상 실행\n");

        // 원래 출력 스트림으로 복구
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

//    @AfterStep
//    public void captureScreenshotOnFailure(Scenario scenario) {
//        // 실패한 경우만 처리
//        if (scenario.isFailed()) {
//            System.out.println("테스트 실패: 스크린샷 및 로그 저장 시작");
//            logBuffer.append("테스트 실패: 스크린샷 및 로그 저장 시작\n");
//
//            try {
//                // 스크린샷 캡처 및 저장
//                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//                File screenshotReport = new File("src/main/save/screenshots/" + scenario.getName() + ".png");
//
//                boolean isMoved = screenshot.renameTo(screenshotReport);
//                if (isMoved) {
//                    System.out.println("스크린샷 저장 완료: " + screenshotReport.getAbsolutePath());
//                    logBuffer.append("스크린샷 저장 완료: ").append(screenshotReport.getAbsolutePath()).append("\n");
//                } else {
//                    System.out.println("스크린샷 저장 실패");
//                    logBuffer.append("스크린샷 저장 실패\n");
//                }
//
//                // 스텝 내에서 발생한 예외 정보 기록
////                System.out.println("스텝 내에서 발생한 예외를 기록 중...");
////                logBuffer.append("스텝 내에서 발생한 예외를 기록 중...\n");
//
//                // 예외를 강제로 던져서 로그 기록
//                try {
//                    // 예외를 직접 던져보기
////                    throw new RuntimeException("런 타임 에러 오류 잡기");
//                } catch (Throwable t) {
//                    // 예외를 잡고 로그에 기록
//                    System.out.println("예외 또는 오류 발생: " + t.getMessage());
//                    logBuffer.append("예외 또는 오류 발생: ").append(t.getMessage()).append("\n");
//
//                    // 스택 트레이스를 기록
//                    for (StackTraceElement element : t.getStackTrace()) {
//                        System.out.println(element.toString());
//                        logBuffer.append(element.toString()).append("\n");
//                    }
//                }
//
//                // 로그 파일 생성 및 저장
//                File logFile = new File("src/main/save/logs/" + scenario.getName() + ".log");
//                try (FileWriter writer = new FileWriter(logFile)) {
//                    writer.write(logBuffer.toString());
//                }
//                System.out.println("로그 저장 완료: " + logFile.getAbsolutePath());
//
//            } catch (IOException e) {
//                // 스크린샷이나 로그 저장 중 발생한 예외 처리
//                System.out.println("스크린샷 또는 로그 저장 중 오류 발생: " + e.getMessage());
//                logBuffer.append("스크린샷 또는 로그 저장 중 오류 발생: ").append(e.getMessage()).append("\n");
//            } catch (Throwable t) {
//                // 기타 모든 예외 처리
//                System.out.println("알 수 없는 오류 발생: " + t.getMessage());
//                logBuffer.append("알 수 없는 오류 발생: ").append(t.getMessage()).append("\n");
//            }
//        }
//    }





}