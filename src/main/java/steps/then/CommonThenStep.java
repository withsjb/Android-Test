package steps.then;

import io.cucumber.java.en.Then;
import utils.AndroidManager;


public class CommonThenStep {

    private StringBuilder logBuffer = new StringBuilder();  // 로그를 수집할 StringBuilder 객체


    @Then("11번가 앱 종료")
    public void close11st(){
        String appId = "com.elevenst";
//        AndroidManager.getDriver().quit();11


    }



    @Then("일부로 실패")
    public void faildtest() {
        AndroidManager androidManager = new AndroidManager();

        String Filename = androidManager.getfilename();
        try {
            throw new RuntimeException("실패 유도");
        }catch(Throwable e){
  androidManager.captureScreenshotAndLog(e, Filename);
            throw e; // 에러 다시 던져줘야 테스트에서 실패처리
        }
        }
//    @Then("일부로 실패")
//    public void faildtest() {
//        try {
//            // 실패 유도
//            throw new RuntimeException("실패 유도");
//        } catch (RuntimeException e) {
//            System.out.println("스텝에서 오류 발생: " + e.getMessage());
//            logBuffer.append("스텝에서 오류 발생: ").append(e.getMessage()).append("\n");
//            throw e;  // 실패 시 시나리오가 실패로 처리되게 함
//        }
//    }

}
