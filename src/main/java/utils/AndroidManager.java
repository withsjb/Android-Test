package utils;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.cucumber.java.Scenario;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import steps.Hooks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
// Singleton Design Pattern
public class AndroidManager {
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
}


//    public static WebElement error_close(String id) throws MalformedURLException{
//
//        try {
//            if (AndroidManager.getElementById(error_close_id) != null) {
//                AndroidManager.getElementById(error_close_id).click();
//                System.out.println("err 발생시 앱을 종료합니다.");
//                throw new RuntimeException("App closed due to error pop-up.");
//            }
//        }catch(Exception e) {
//            System.out.println("Error close button not found or could not be clicked.");
//        }
//
//        return (ExpectedConditions.visibilityOfElementLocated(By.id(id)));
//
//    }


