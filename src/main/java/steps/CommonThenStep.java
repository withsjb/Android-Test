package steps;

import io.cucumber.java.en.Then;
import utils.AndroidManager;


public class CommonThenStep {

    @Then("11번가 앱 종료")
    public void close11st(){
        String appId = "com.elevenst";
//        AndroidManager.getDriver().quit();


    }
}
