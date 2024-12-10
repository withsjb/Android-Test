package steps.when;

import io.cucumber.java.en.When;
import utils.AndroidManager;

import java.net.MalformedURLException;

public class CommonWhenStep {

    @When("광고 팝업 클로즈")
    public void closeganggo() throws MalformedURLException {
        String GanggoId = "com.elevenst:id/popup_close";

        AndroidManager.getElementById(GanggoId).click();

        System.out.println("광고를 종료하였습니다.");
    }


    @When("수신 정보 동의안함")
    public void noagree() throws MalformedURLException {
        String noagreeid = "com.elevenst:id/cancel";

        AndroidManager.getElementById(noagreeid).click();

        System.out.println("수신정보 거절버튼을 눌렀습니다.");
    }

    @When("알림 서비스 확인")
    public void bellservice() throws MalformedURLException {
        String bellid = "com.elevenst:id/ok";

        AndroidManager.getElementById(bellid).click();

        System.out.println("알림서비스 확인을 눌렀습니다.");
    }

    @When("전용혜택안내x버튼 누르기")
    public void service() throws MalformedURLException {
        String serviceid = "com.elevenst:id/close_btn";

        AndroidManager.getElementById(serviceid).click();

        System.out.println("전용혜택 안내 x를 눌렀습니다.");
    }

}
