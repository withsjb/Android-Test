package runner;

import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import utils.AndroidManager;
import utils.XrayReportUploader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static utils.AndroidManager.copyJsonFile;
import static utils.AndroidManager.readJsonFile;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/main/assets/features/", glue = "steps", plugin = {
        "pretty",                             // 실행 로그 보기 좋게 출력
        "json:target/cucumber.json"          // JSON 형식으로 결과 출력
})
public class Runner {
    static String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
    static String dateOnly = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    private static final String CUCUMBER_JSON = "target/cucumber.json";
    private static final String CUCUMBER_TIMESTAMP_JSON = "target/target_"+ dateOnly +"/cucumber_" + timestamp + ".json"; // 시간별 결과 저장 파일
    private static final String CUCUMBER_DAY_JSON = "target/target_"+ dateOnly +"/cucumber_" + dateOnly + ".json"; // 날짜만 포함된 결과 파일



    @AfterClass
    public static void uploadReport(){
        try {
            // Xray 보고서 업로드 (여기서는 예시로 비워두었습니다.)
            XrayReportUploader.XrayReport(new String[]{});

            // cucumber.json이 생성될 때까지 대기
            while (!new java.io.File(CUCUMBER_JSON).exists()) {
                Thread.sleep(100); // 100ms 간격으로 체크
            }

            // 날짜 기반 폴더가 존재하지 않으면 새로 생성
            File dayFolder = new File("target/target_" + dateOnly);
            if (!dayFolder.exists()) {
                dayFolder.mkdirs(); // 폴더가 없으면 생성
                System.out.println("폴더가 없어서 생성되었습니다: " + dayFolder.getAbsolutePath());
            }

            // cucumber.json 파일을 읽고
            JsonNode newJson = readJsonFile(CUCUMBER_JSON);

            File dayFile = new File(CUCUMBER_DAY_JSON);
            if (dayFile.exists()) {
                // 날짜가 같은 파일이 존재하면 기존 파일 삭제 후 새로운 파일로 교체
                System.out.println("같은 날짜의 파일이 존재하여 덮어씁니다.");
                dayFile.delete(); // 기존 파일 삭제
            }

            // 새로운 JSON 파일을 날짜에 맞춰 저장
            copyJsonFile(CUCUMBER_JSON, CUCUMBER_DAY_JSON);

            // timestamp에 맞게 새로운 파일로 복사
            copyJsonFile(CUCUMBER_JSON, CUCUMBER_TIMESTAMP_JSON);

            // 이 부분에서 추가적인 로직을 넣어 매번 새로운 파일에 데이터를 기록할 수 있음
            // 예를 들어, 결과를 누적하거나 특정 기준으로 변경 작업 등을 할 수 있습니다.

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}