package runner;

import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import utils.AndroidManager;
import utils.XrayReportUploader;

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

    private static final String CUCUMBER_JSON = "target/cucumber.json";
    private static final String CUCUMBER_WEEK_JSON = "target/cucumberweek.json";


    @AfterClass
    public static void uploadReport(){
        try{

            XrayReportUploader.XrayReport(new String[]{});

            while (!new java.io.File(CUCUMBER_JSON).exists()) {
                Thread.sleep(100); // 100ms 간격으로 체크
            }

            JsonNode newJson = readJsonFile(CUCUMBER_JSON);
            String newTimestamp = AndroidManager.getTimestamp(newJson);  // getTimestamp 메서드로 처리

            // cucumberweek.json 파일을 읽고 비교
            JsonNode existingJson = readJsonFile(CUCUMBER_WEEK_JSON);
            String existingTimestamp = AndroidManager.getTimestamp(existingJson);  // "2024-12-30"

            if (existingJson != null) {
                // 결과를 병합하는 로직
                List<Map<String, Object>> currentResults = AndroidManager.readCucumberJson(CUCUMBER_JSON);
                List<Map<String, Object>> weekResults = AndroidManager.readCucumberJson(CUCUMBER_WEEK_JSON);

                // 새로운 결과를 기존 cucumberweek.json에 추가
                weekResults.addAll(currentResults);

                // cucumberweek.json에 병합된 결과 저장
                AndroidManager.writeCucumberJson(CUCUMBER_WEEK_JSON, weekResults);
            } else {
                // cucumberweek.json이 존재하지 않으면 새로운 파일을 생성하고 결과를 기록
                List<Map<String, Object>> currentResults = AndroidManager.readCucumberJson(CUCUMBER_JSON);
                AndroidManager.writeCucumberJson(CUCUMBER_WEEK_JSON, currentResults);
            }
            // cucumber.json 읽기
//            List<Map<String, Object>> currentResults = AndroidManager.readCucumberJson(CUCUMBER_JSON);
//
//            // cucumberweek.json 파일 읽기
//            List<Map<String, Object>> weekResults = AndroidManager.readCucumberJson(CUCUMBER_WEEK_JSON);
//
//            // 새로운 결과를 주어진 파일에 추가(같은 날짜면 갱신)
//            weekResults = AndroidManager.updateResultsWithCurrentDate(weekResults, currentResults);
//
//            // 결과 출력하여 확인
//            System.out.println("Updated weekResults: " + weekResults);
//
//            // cucumberweek.json에 결과 저장
//            AndroidManager.writeCucumberJson(CUCUMBER_WEEK_JSON, weekResults);
//            AndroidManager.copyAndUpdateResults(CUCUMBER_JSON, CUCUMBER_WEEK_JSON);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}