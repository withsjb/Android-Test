package steps.then;

import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utils.AndroidManager;

import java.net.MalformedURLException;

public class Test3ThenStep {
    @Then("로그인 ui 뜨는거 확인")
    public void loginui() throws MalformedURLException{
        AndroidManager androidManager = new AndroidManager();
        String loginelementid= "00000000-0000-0660-0000-a1c7000048cb";
        String Filename = androidManager.getfilename();
        WebElement a1c = AndroidManager.getDriver().findElement(By.id(loginelementid));

        System.out.println("a1c 는 정상적으로 화면에 있음 " + a1c.getText());
        try{

        }catch (Throwable e){
            androidManager.captureScreenshotAndLog(e, Filename);
            throw e; // 에러 다시 던져줘야 테스트에서 실패처리
        }

    }
}
