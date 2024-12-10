package steps.when;

import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import utils.AndroidManager;
import org.openqa.selenium.interactions.Actions;

import java.net.MalformedURLException;
import java.util.List;

public class Test3WhenStep {
    AndroidManager androidManager = new AndroidManager();

    String Filename = androidManager.getfilename();


    @When("검색 버튼 클릭")
    public void searchbtn() throws MalformedURLException {
        try{
        String SearchId = "com.elevenst:id/gnb_text_search";


        androidManager.getElementById(SearchId).click();

        System.out.println("검색을 시작합니다.");


        }catch (Throwable e){
            androidManager.captureScreenshotAndLog(e, Filename);
            throw e; // 에러 다시 던져줘야 테스트에서 실패처리
        }
    }

    @When("검색 상세 버튼 클릭")
    public void searchtwobtn() throws MalformedURLException {
        try{
        String SearchtwoId = "com.elevenst:id/gnb_text_search";

        androidManager.getElementById(SearchtwoId).click();


        System.out.println("검색 상세 검색 시작합니다.");


        }catch (Throwable e){
            androidManager.captureScreenshotAndLog(e, Filename);
            throw e; // 에러 다시 던져줘야 테스트에서 실패처리
        }
    }


    @When("검색값 \"bag\" 입력")
    public void searchbag() throws MalformedURLException {
        try{
        String SearchtextId = "com.elevenst:id/search_input";
          String Serchicon  = "com.elevenst:id/icon_search";
        androidManager.getElementById(SearchtextId).click();

        androidManager.getElementById(SearchtextId).sendKeys("bag");

        androidManager.getElementById(Serchicon).click();

        System.out.println("검색을 시작합니다.");


        }catch (Throwable e){
            androidManager.captureScreenshotAndLog(e, Filename);
            throw e; // 에러 다시 던져줘야 테스트에서 실패처리
        }
    }


    @When("검색화면 첫번째 클릭")
    public void clickonebtn() throws MalformedURLException {
        Actions actions = new Actions(androidManager.getDriver());
        try{
//        String onebtn = "//*[@id=\"section_list\"]/div/ul/li[1]/div/div[2]";
            int x = 450;
            int y = 320;

            WebElement element = androidManager.getDriver().findElement(By.xpath("/html/body/div/div[2]/div[2]/div/div/div[2]/div[2]/section[2]/div/ul/li[1]"));


            actions.moveToElement(element).click().perform();
//            androidManager.getElementByXpath(onebtn).click();



        }catch (Throwable e){
            androidManager.captureScreenshotAndLog(e, Filename);
            throw e; // 에러 다시 던져줘야 테스트에서 실패처리
        }


    }

    @When("구매하기 클릭")
    public void clickbuybtn() throws MalformedURLException {
        try{
        String ckickbuy = "com.elevenst:id/purchaseTextView";

        androidManager.getElementById(ckickbuy).click();
        }catch (Throwable e){
            androidManager.captureScreenshotAndLog(e, Filename);
            throw e; // 에러 다시 던져줘야 테스트에서 실패처리
        }

    }

    @When("바로구매 버튼 클릭")
    public void nowbuybtn() throws MalformedURLException {
        try{
        String nowbuy = "com.elevenst:id/purchaseTextView";

        androidManager.getElementById(nowbuy).click();
        }catch (Throwable e){
            androidManager.captureScreenshotAndLog(e, Filename);
            throw e; // 에러 다시 던져줘야 테스트에서 실패처리
        }


    }

}
