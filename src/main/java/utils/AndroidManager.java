package utils;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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

}
