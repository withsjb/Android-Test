package runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/main/assets/features/", glue = "steps", plugin = {
        "pretty",                             // 실행 로그 보기 좋게 출력
        "json:target/cucumber.json"          // JSON 형식으로 결과 출력
})
public class Runner {


}
