package utils;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.cucumber.java.Scenario;
import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import steps.Hooks;

/**
 비디오 관련 import
 */
import org.monte.media.Format;
import org.monte.media.math.Rational;
import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;


import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
// Singleton Design Pattern
public class AndroidManager {

    private ScreenRecorder screenRecorder;
    public static AndroidDriver driver;
    public static WebDriverWait wait;

    public static String error_close_id ="android:id/aerr_close";
    public static String wait_id ="android:id/aerr_wait";

    private StringBuilder logBuffer = new StringBuilder();

    public AndroidManager(){
        //    private
    }
    //
    public static AndroidDriver getDriver() throws MalformedURLException{
        // ,
        if(driver == null){

            try {
                UiAutomator2Options options = new UiAutomator2Options()
                        .setUdid("emulator-5554")
                        .setPlatformName("Android")
                        .setAutomationName("uiautomator2")
                        .setAppPackage("com.elevenst")
                        .setAppActivity("com.elevenst.intro.Intro");


                 driver = new AndroidDriver(
                        // The default URL in Appium 1 is http://127.0.0.1:4723/wd/hub
                        new URL("http://127.0.0.1:4723"), options
                );
                 System.out.println("드라이버 연결");
            }catch(MalformedURLException e){
                throw new RuntimeException(e);
            }
        }
        return driver;
    }
    public static WebDriverWait getWait() throws MalformedURLException{
        return getWait(10);    // 10  
    }
    // wait
    public static WebDriverWait getWait (int duration) throws MalformedURLException {
        if(wait ==null){
            if(driver == null){
                driver = getDriver();
            }
            wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
        }
        return wait;
    }


    public static WebElement getElementById(String id){

        try {
            if (driver == null) {
                driver = getDriver();
            }
            return getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
        }catch (MalformedURLException e){
            throw new RuntimeException("Driver initialization failed.", e);
        }
        }
    //  xpath
    public static WebElement getElementByXpath(String xPath) throws MalformedURLException{
        return getWait().until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xPath)));
    }
    public static List<WebElement> getAllElementById(String id) throws MalformedURLException{
        return  getWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(id)));
    }
    public static List<WebElement> getAllElementByXpath(String xPath) throws MalformedURLException{
        return  getWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xPath)));
    }


    public static HttpURLConnection connect (String urlString, String method, String username, String apiToken) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((username + ":" + apiToken).getBytes()));
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;

    }

    public void captureScreenshotAndLog(Throwable t,String Filename) {
        // 스크린샷 캡처

        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File screenshotReport = new File("src/main/save/screenshots/error_" + Filename + ".png");
//+ System.currentTimeMillis() + 는 추가 할수도 안할수도


        // 스크린샷 저장
        try {
            boolean isMoved = screenshot.renameTo(screenshotReport);
            if (isMoved) {
                System.out.println("스크린샷 저장 완료: " + Filename +  screenshotReport.getAbsolutePath());
            } else {
                System.out.println("스크린샷 저장 실패");
            }
        } catch (Exception e) {
            System.out.println("스크린샷 캡처 실패: " + e.getMessage());
        }

        // 오류 로그 기록
        StringBuilder logBuffer = new StringBuilder();
        logBuffer.append("예외 발생: ").append(t.getMessage()).append("\n");

        // 스택 트레이스를 기록
        for (StackTraceElement element : t.getStackTrace()) {
            logBuffer.append(element.toString()).append("\n");
        }

        // 콘솔에 출력
        System.out.println(logBuffer.toString());

        // 오류 로그를 파일에 기록
        File logFile = new File("src/main/save/logs/error_" + Filename + ".log");
        try (FileWriter writer = new FileWriter(logFile)) {
            writer.write(logBuffer.toString());
            System.out.println("로그 파일에 기록 완료: " + logFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("로그 파일 기록 중 오류 발생: " + e.getMessage());
        }
    }

    public static String getfilename(){
        Scenario scenario = Hooks.getScenario();
        String Filename = scenario.getName();
        System.out.println("file 이름****: " + Filename);

        return Filename;
    }


    //비디오 시작
    public void startRecording(String fileName) throws Exception {
//        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
//                .getDefaultScreenDevice()
//                .getDefaultConfiguration();
//        screenRecorder = new ScreenRecorder(gc, null, new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
//                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
//                        CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24,
//                        FrameRateKey, Rational.valueOf(15), QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
//                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)),
//                null, new File(fileName));
//        screenRecorder.start();

        String command = "adb shell screenrecord /sdcard/" + fileName + ".mp4"; // 파일 경로는 적절히 수정하세요
        Process process = Runtime.getRuntime().exec(command);
        System.out.println("녹화 시작: " + command);
    }

    //비디오 종료
    public void stopRecording(String filename) throws Exception {
        // 녹화 종료 명령어 실행
        String stopRecordingCommand = "adb shell pkill -l2 screenrecord"; // 녹화 종료
        Process stopRecordingProcess = Runtime.getRuntime().exec(stopRecordingCommand);
        stopRecordingProcess.waitFor();

        // 녹화가 끝난 후, 파일이 정상적으로 생성되었는지 확인
        String filePath = "/sdcard/" + filename + ".mp4";
        String checkFileCommand = "adb shell ls -l " + filePath; // 파일 존재 및 크기 확인
        Process checkFileProcess = Runtime.getRuntime().exec(checkFileCommand);
        checkFileProcess.waitFor();

        // 파일 크기가 0이 아니면 로컬로 가져오기
        String pullCommand = "adb pull " + filePath + " src/main/save/video/" + filename + ".mp4";

        // 파일 크기가 0인 경우 재시도 혹은 오류 처리
        String fileSizeCommand = "adb shell stat -c %s " + filePath;  // 파일 크기 확인
        Process fileSizeProcess = Runtime.getRuntime().exec(fileSizeCommand);
        fileSizeProcess.waitFor();

        // 프로세스 출력 값 (파일 크기) 확인
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileSizeProcess.getInputStream()))) {
            String fileSize = reader.readLine();
            if (fileSize != null && Integer.parseInt(fileSize) > 0) {
                // 파일이 정상적으로 생성되었으면 pull 명령어 실행
                System.out.println("파일이 정상적으로 생성되었습니다. 로컬로 복사합니다.");
                Process pullProcess = Runtime.getRuntime().exec(pullCommand);
                pullProcess.waitFor();
                System.out.println("녹화 종료, 비디오 저장 완료");
            } else {
                System.out.println("파일 크기가 0입니다. 녹화가 제대로 완료되지 않았을 수 있습니다.");
            }
        } catch (IOException e) {
            System.out.println("파일 크기 확인 중 오류 발생: " + e.getMessage());
        }
    }


}




