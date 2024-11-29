package steps;

import io.cucumber.java.en.Then;
import utils.AndroidManager;


public class CommonThenStep {

    @Then("11번가 앱 종료")
    public void close11st(){
        String appId = "com.elevenst";
//        AndroidManager.getDriver().quit();11


    }

    @Then("일부로 실패")
    public void faildtest(){
        throw new RuntimeException("실패 유도");
    }
}
