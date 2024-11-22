package org.example;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options()
                .setUdid("emulator-5554")
                .setPlatformName("Android")
                .setAutomationName("uiautomator2")
                .setAppPackage("com.elevenst")
                .setAppActivity("com.elevenst.intro.Intro");


        AndroidDriver driver = new AndroidDriver(
                // The default URL in Appium 1 is http://127.0.0.1:4723/wd/hub
                new URL("http://127.0.0.1:4723"), options
        );
        

//        try {
//            WebElement el = driver.findElement(AppiumBy.xpath("//Button"));
//            el.click();
//            driver.getPageSource();
//        } finally {
//            driver.quit();
//        }
    }
}