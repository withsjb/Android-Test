package steps.given;

import io.cucumber.java.en.Given;
import java.net.MalformedURLException;

public class CommonGivenStep {

    @Given("11번가 실행")
    public static void start11st() throws MalformedURLException {

        System.out.println("11번가 실행");

//        UiAutomator2Options options = new UiAutomator2Options()
//                .setUdid("emulator-5554")
//                .setPlatformName("Android")
//                .setAutomationName("uiautomator2")
//                .setAppPackage("com.elevenst")
//                .setAppActivity("com.elevenst.intro.Intro");
//
//
//        AndroidDriver driver = new AndroidDriver(
//                // The default URL in Appium 1 is http://127.0.0.1:4723/wd/hub
//                new URL("http://127.0.0.1:4723"), options
//        );
    }


    @Given("초기화면")
    public static void mainview() throws MalformedURLException {

        System.out.println("초기화면");


    }


}
