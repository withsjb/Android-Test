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

    private String recordingFilePath;

    private AndroidManager androidManager = new AndroidManager();
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


    private void deleteFilesInDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 서브디렉토리라면 재귀적으로 삭제
                    deleteFilesInDirectory(file);
                }
                if (file.exists() && file.isFile()) {
                    if (file.delete()) {
                        System.out.println("파일 삭제 완료: " + file.getAbsolutePath());
                    } else {
                        System.out.println("파일 삭제 실패: " + file.getAbsolutePath());
                    }
                }
            }
        }
    }

    public void startvideo(Scenario scenario)throws Exception{
        String scenarioName = scenario.getName().replaceAll(" ", "_");
//        recordingFilePath = "src/main/save/video/" + scenarioName + ".avi";
        androidManager.startRecording(scenarioName);

        System.out.println("hook 에 startvideo 녹화를 시작합니다. : " + scenarioName);

    }

    public void stopvideo(Scenario scenario, String filename)throws Exception{
        String recordingFilePath = "src/main/save/video/" + filename + ".mp4";
        System.out.println("삭제할 영상 경로: " + recordingFilePath);
        androidManager.stopRecording(filename);

        if (scenario.isFailed()) {
            System.out.println("테스트 실패 녹화 저장: " + recordingFilePath);
            System.out.println("영상 저장 경로(저장)" + recordingFilePath);
            System.out.println("녹화를 종료합니다.");
        } else {
            // 테스트 성공 시, 생성된 mp4 파일을 삭제
            File recordingFile = new File(recordingFilePath);
            if (recordingFile.exists()) {
                if (recordingFile.delete()) {
                    System.out.println("테스트 성공, 영상 파일 삭제됨: " + recordingFilePath);
                } else {
                    System.out.println("파일 삭제 실패: " + recordingFilePath);
                }
            }
        }
    }

    @Before
    public void startapp(Scenario scenario) throws MalformedURLException {
        try {
            System.out.println("before 테스트 정상 실행");
            logBuffer.append("before 테스트 정상 실행\n");

            if (driver == null) {
                driver = AndroidManager.getDriver();
            }
            startvideo(scenario);

            // 콘솔 출력 스트림 가로채기
            PrintStream logPrintStream = new PrintStream(logStream);
            PrintStream errorPrintStream = new PrintStream(errorStream);
            System.setOut(logPrintStream);
            System.setErr(errorPrintStream);
        }catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }



    @After
    public void finisapp(Scenario scenario) {
            String scenariofile = scenario.getName().replaceAll(" ", "_");

        try {
            System.out.println("after 테스트 진입");
            logBuffer.append("after 테스트 진입\n");

            Jirafeatureissue.featureupload(new String[]{});

            stopvideo(scenario, scenariofile);
            System.out.println("after 테스트 정상 실행");
            logBuffer.append("after 테스트 정상 실행\n");

            // 원래 출력 스트림으로 복구
            System.setOut(originalOut);
            System.setErr(originalErr);

//            Thread.sleep(20000); // 60000 밀리초 = 1분
            System.out.println("1분 대기 후 종료" + scenariofile);
        }catch (Exception e) {
            System.out.println("after 테스트 진입 실패: ");
            System.out.println(e.getMessage());
        }
    }






}