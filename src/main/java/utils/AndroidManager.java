package utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.type.TypeFactory;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.cucumber.java.Scenario;
import org.jfree.chart.JFreeChart;
import org.json.JSONArray;
import org.json.JSONObject;
import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import steps.Hooks;


/**
 비디오 관련 import
 */
import org.monte.media.Format;
import org.monte.media.math.Rational;
import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;


import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.List;

// Singleton Design Pattern
public class AndroidManager {

    static String JIRA_URL;
    static String JIRA_API_TOKEN;
    static String USERNAME;

    private ScreenRecorder screenRecorder;
    public static AndroidDriver driver;
    public static WebDriverWait wait;

    public static String error_close_id ="android:id/aerr_close";
    public static String wait_id ="android:id/aerr_wait";

    private StringBuilder logBuffer = new StringBuilder();

    private static final ObjectMapper objectMapper = new ObjectMapper();
    public AndroidManager(){
        //    private
    }
    //
    public static AndroidDriver getDriver() throws MalformedURLException{
        // ,
        if(driver == null){

            try {
                UiAutomator2Options options = new UiAutomator2Options()
                        .setUdid("emulator-5554")
                        .setPlatformName("Android")
                        .setAutomationName("uiautomator2")
                        .setAppPackage("com.elevenst")
                        .setAppActivity("com.elevenst.intro.Intro");


                driver = new AndroidDriver(
                        // The default URL in Appium 1 is http://127.0.0.1:4723/wd/hub
                        new URL("http://127.0.0.1:4724"), options
                );
                System.out.println("드라이버 연결");
            }catch(MalformedURLException e){
                throw new RuntimeException(e);
            }
        }
        return driver;
    }
    public static WebDriverWait getWait() throws MalformedURLException{
        return getWait(10);    // 10  
    }
    // wait
    public static WebDriverWait getWait (int duration) throws MalformedURLException {
        if(wait ==null){
            if(driver == null){
                driver = getDriver();
            }
            wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
        }
        return wait;
    }

    public static void loadProperties(String propertiesFilePath) {
        Properties properties = new Properties();

        try (InputStream input = new FileInputStream(propertiesFilePath)) {
            properties.load(input);
            JIRA_URL = properties.getProperty("jira.url");
            JIRA_API_TOKEN = properties.getProperty("jira.api.token");
            USERNAME = properties.getProperty("username");
//            FeaturePath = properties.getProperty("feature.path");
//            Xray_API_URL = properties.getProperty("xray.api.url");
//            XRAY_API_TOKEN = properties.getProperty("xray.api.token");
        }catch (IOException e) {
            System.out.println("Properties 파일 로딩 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static WebElement getElementById(String id){

        try {
            if (driver == null) {
                driver = getDriver();
            }
            return getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
        }catch (MalformedURLException e){
            throw new RuntimeException("Driver initialization failed.", e);
        }
    }
    //  xpath
    public static WebElement getElementByXpath(String xPath) throws MalformedURLException{
        return getWait().until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xPath)));
    }
    public static List<WebElement> getAllElementById(String id) throws MalformedURLException{
        return  getWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(id)));
    }
    public static List<WebElement> getAllElementByXpath(String xPath) throws MalformedURLException{
        return  getWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xPath)));
    }


    public static HttpURLConnection connect (String urlString, String method, String username, String apiToken) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((username + ":" + apiToken).getBytes()));
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;

    }



    public void captureScreenshotAndLog(Throwable t,String Filename) {
        // 스크린샷 캡처
        String resourece = "src/main/resources/application.properties";
        loadProperties(resourece);
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File screenshotReport = new File("src/main/save/screenshots/error_" + Filename + ".png");
//+ System.currentTimeMillis() + 는 추가 할수도 안할수도

        String ui = "예제 UI"; // 실제 값으로 교체
        String anotherUi = "다른 UI"; // 실제 값으로 교체
        int time = 10; // 예제 시간, 실제 값으로 교체

        // 스크린샷 저장
        try {
            boolean isMoved = screenshot.renameTo(screenshotReport);
            if (isMoved) {
                System.out.println("스크린샷 저장 완료: " + Filename +  screenshotReport.getAbsolutePath());
            } else {
                System.out.println("스크린샷 저장 실패");
            }
        } catch (Exception e) {
            System.out.println("스크린샷 캡처 실패: " + e.getMessage());
        }

        // 오류 로그 기록
        StringBuilder logBuffer = new StringBuilder();
        logBuffer.append("예외 발생: ").append(t.getMessage()).append("\n");

        // 스택 트레이스를 기록
        for (StackTraceElement element : t.getStackTrace()) {
            logBuffer.append(element.toString()).append("\n");
        }

        // 콘솔에 출력


        String errreason = ErrorHandler.getErrorReason(t, ui, anotherUi, time);
        String logdetail = logBuffer.toString();
//        String logdetaila = logdetail.replace("\n", "\\n").replace("\"", "\\\"");

        Jiradefectissuecreate.defectissuecreate(logdetail,errreason ,JIRA_URL, USERNAME,JIRA_API_TOKEN);


        // 오류 로그를 파일에 기록
        File logFile = new File("src/main/save/logs/error_" + Filename + ".log");
        try (FileWriter writer = new FileWriter(logFile)) {


            writer.write(logBuffer.toString());
            System.out.println("로그 파일에 기록 완료: " + logFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("로그 파일 기록 중 오류 발생: " + e.getMessage());
        }
    }

    public static String getfilename(){
        Scenario scenario = Hooks.getScenario();
        String Filename = scenario.getName();
        System.out.println("file 이름****: " + Filename);

        return Filename;
    }


    //비디오 시작
    public void startRecording(String fileName) throws Exception {


        String command = "adb shell screenrecord /sdcard/" + fileName + ".mp4"; // 파일 경로는 적절히 수정하세요
        Process process = Runtime.getRuntime().exec(command);
        System.out.println("녹화 시작: " + command);
    }

    //비디오 종료
    public void stopRecording(String filename) throws Exception {
        // 녹화 종료 명령어 실행
        String stopRecordingCommand = "adb shell pkill -l2 screenrecord"; // 녹화 종료
        Process stopRecordingProcess = Runtime.getRuntime().exec(stopRecordingCommand);
        stopRecordingProcess.waitFor();

        // 녹화가 끝난 후, 파일이 정상적으로 생성되었는지 확인
        String filePath = "/sdcard/" + filename + ".mp4";
        String checkFileCommand = "adb shell ls -l " + filePath; // 파일 존재 및 크기 확인
        Process checkFileProcess = Runtime.getRuntime().exec(checkFileCommand);
        checkFileProcess.waitFor();

        // 파일 크기가 0이 아니면 로컬로 가져오기
        String pullCommand = "adb pull " + filePath + " src/main/save/video/" + filename + ".mp4";

        // 파일 크기가 0인 경우 재시도 혹은 오류 처리
        String fileSizeCommand = "adb shell stat -c %s " + filePath;  // 파일 크기 확인
        Process fileSizeProcess = Runtime.getRuntime().exec(fileSizeCommand);
        fileSizeProcess.waitFor();

        // 프로세스 출력 값 (파일 크기) 확인
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileSizeProcess.getInputStream()))) {
            String fileSize = reader.readLine();
            if (fileSize != null && Integer.parseInt(fileSize) > 0) {
                // 파일이 정상적으로 생성되었으면 pull 명령어 실행
                System.out.println("파일이 정상적으로 생성되었습니다. 로컬로 복사합니다.");
                Process pullProcess = Runtime.getRuntime().exec(pullCommand);
                pullProcess.waitFor();
                System.out.println("녹화 종료, 비디오 저장 완료");
            } else {
                System.out.println("파일 크기가 0입니다. 녹화가 제대로 완료되지 않았을 수 있습니다.");
            }
        } catch (IOException e) {
            System.out.println("파일 크기 확인 중 오류 발생: " + e.getMessage());
        }
    }




    //cucumber 주간 테스트 결과

    // JSON에서 start_timestamp 값을 추출하여 날짜만 반환
    public static String getTimestamp(JsonNode jsonNode) {
        // elements 배열을 가져옴
        JsonNode elementsNode = jsonNode.get(0).get("elements");

        // elements가 없거나 비어있는 경우 예외 처리
        if (elementsNode == null || elementsNode.isEmpty()) {
            throw new RuntimeException("elements 배열이 비어있거나 존재하지 않습니다.");
        }

        // 첫 번째 시나리오에서 start_timestamp를 추출
        JsonNode firstElement = elementsNode.get(0);
        JsonNode timestampNode = firstElement.get("start_timestamp");

        // start_timestamp가 없을 경우 예외 처리
        if (timestampNode == null || timestampNode.isNull()) {
            throw new RuntimeException("start_timestamp 필드가 존재하지 않습니다.");
        }

        // 날짜 부분만 추출하여 반환
        return timestampNode.asText().split("T")[0];
    }



    public static void appendJsonToFile(String sourceFilePath, String destinationFilePath) throws IOException {
        File sourceFile = new File(sourceFilePath);
        File destinationFile = new File(destinationFilePath);
        ObjectMapper objectMapper = new ObjectMapper();

        // 기존 파일이 없으면 새로 만듬
        if (!destinationFile.exists()) {
            destinationFile.createNewFile();
        }

        JsonNode sourceJson = readJsonFile(sourceFilePath);
        JsonNode existingJson = readJsonFile(destinationFilePath);

        // 새로운 JSON 데이터를 기존 데이터 뒤에 추가하는 로직
        // 이 부분은 기존 JSON 구조에 맞게 데이터를 합치는 방식으로 구현해야 합니다.
        // 예시로 기존 JSON의 배열에 새 JSON을 추가한다고 가정
        // 예: existingJson.get("tests").add(sourceJson);

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(destinationFile, existingJson); // 수정된 JSON을 덮어 씌운다.
    }

    public static JsonNode readJsonFile(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(Files.readAllBytes(Paths.get(filePath)));
    }

    public static void copyJsonFile(String sourceFilePath, String destinationFilePath) throws IOException {
        File sourceFile = new File(sourceFilePath);
        File destinationFile = new File(destinationFilePath);
        if (destinationFile.exists()) {
            System.out.println("복사 성공");
            destinationFile.delete(); // 기존 파일 삭제
        }
        Files.copy(sourceFile.toPath(), destinationFile.toPath());
    }

    public static List<Map<String, Object>> readCucumberJson(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return List.of(); // 파일이 없으면 빈 리스트 반환
        }

        JsonNode rootNode = objectMapper.readTree(file);
        // rootNode를 원하는 형태로 변환하는 코드 필요
        return objectMapper.convertValue(rootNode, List.class); // JsonNode를 List로 변환
    }

    // List<Map<String, Object>> 데이터를 cucumber.json 파일에 저장
    public static void writeCucumberJson(String filePath, List<Map<String, Object>> data) throws IOException {
        File file = new File(filePath);
        // 데이터를 JSON 형식으로 파일에 쓰기
        objectMapper.writeValue(file, data);
    }

    //json 파일 읽기
    public static String readJsonFromFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }



    //json에서 년월일 추출 및 표 제작
    //실패한 시나리오만 추출

    public static String generateFailureReport(JsonNode jsonResults) {
        StringBuilder table = new StringBuilder();

        // 표 제목 추가
        table.append("| 테스트 진행 년도 | 월 | 일 | 시간 | 테스트 시나리오 | 테스트 결과 |\n");
        table.append("| --- | --- | --- | --- | --- | --- |\n");

        // 현재 날짜로 년, 월, 일, 시간 추출
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String currentYear = currentDate.split("-")[0];
        String currentMonth = currentDate.split("-")[1];
        String currentDay = currentDate.split("-")[2];

        // 실패한 시나리오 추출
        Iterator<JsonNode> scenarios = jsonResults.elements();
        while (scenarios.hasNext()) {
            JsonNode scenario = scenarios.next();

            Iterator<JsonNode> elements = scenario.get("elements").elements();
            while (elements.hasNext()) {
                JsonNode element = elements.next();

                String scenarioName = element.get("name").asText();
                String status = element.get("steps").get(0).get("result").get("status").asText(); // 첫 번째 step의 status 사용

                if ("failed".equals(status)) {
                    String timestamp = element.get("start_timestamp").asText();
                    String time = timestamp.split("T")[1].split("\\.")[0];  // 시간 부분만 추출

                    // 표에 실패한 시나리오 추가
                    table.append(String.format("| %s | %s | %s | %s | \"%s\" | %s |\n",
                            currentYear, currentMonth, currentDay, time,
                            scenarioName.replace("\"", "\\\""),  // 따옴표 이스케이프
                            status));
                }
            }
        }

        return table.toString();
    }

    // 성공 리포트 생성
    public static String generateScenarioReport(JsonNode jsonResults, String issuekey) {
        int countPassed = 0;
        int countFailed = 0;

        // 결과를 담을 JSON 객체
        JSONObject result = new JSONObject();
        JSONObject docContent = new JSONObject();
        JSONArray content = new JSONArray();

        JSONArray tableContent = new JSONArray();
        JSONArray headerRow = new JSONArray();
        headerRow.put(createTableCell("테스트 기간", null));
        headerRow.put(createTableCell("시나리오" , null));
        headerRow.put(createTableCell("테스트 결과" , null));
        tableContent.put(createTableRow(headerRow));

        // 날짜 및 시나리오 정보 추출
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        for (JsonNode feature : jsonResults) {
            for (JsonNode scenario : feature.get("elements")) {
                String startTime = scenario.get("start_timestamp").asText();
                String scenarioName = scenario.get("name").asText();
                String testResult = "✅ Passed";

                for (JsonNode step : scenario.get("steps")) {
                    String stepStatus = step.get("result").get("status").asText();
                    if ("failed".equalsIgnoreCase(stepStatus)) {
                        testResult = "⛔ Failed";
                        //failed 1증가
                        countFailed++;
                        break;
                    }
                }
                    //passed 1증가
                if ("✅ Passed".equals(testResult)) {
                    countPassed++;
                }

                // 수정된 날짜 포맷 적용
                try {
                    Date date = dateFormat.parse(startTime); // T 구분자와 Z를 처리하는 포맷
                    String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

                    // 테이블 행 추가
                    JSONArray row = new JSONArray();
                    row.put(createTableCell(formattedDate, null));
                    row.put(createTableCell(scenarioName, null));
                    row.put(createTableCell(testResult, "Passed".equals(testResult) ? "#006644" : "#d32f2f")); // 성공/실패 색상 추가
                    tableContent.put(createTableRow(row));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Invalid date format: " + startTime);
                }
            }
        }

        // 테이블 생성
        JSONObject table = new JSONObject();
        table.put("type", "table");
        table.put("attrs", new JSONObject()
                .put("isNumberColumnEnabled", false)
                .put("layout", "center")
                .put("width", 900)
                .put("displayMode", "default"));
        table.put("content", tableContent);

        // 문서 내용
        content.put(new JSONObject().put("type", "paragraph").put("content", new JSONArray()
                .put(new JSONObject().put("type", "text").put("text", "This is a description above the table."))));

        content.put(table);

        content.put(new JSONObject().put("type", "paragraph").put("content", new JSONArray()
                .put(new JSONObject().put("type", "text").put("text", "This is a description after the table."))));

        // 최종 문서 작성
        docContent.put("type", "doc");
        docContent.put("version", 1);
        docContent.put("content", content);
        //차트 그리기
        try {
            JFreeChart pieChart = cucumberchart.createPiechart(countPassed, countFailed);
            String savePath = "src/main/save/chart/results" + issuekey +"_chart.png";
            cucumberchart.savePieChartAsImage(pieChart, savePath);
        }catch (Exception e){
            e.getMessage();
        }
        // 최종 결과 반환 (문서 형식으로)
        return docContent.toString();
    }

    // 테이블 셀 생성
    private static JSONObject createTableCell(String text, String color) {
        JSONObject cell = new JSONObject();
        cell.put("type", "tableCell");
        cell.put("content", new JSONArray()
                .put(new JSONObject().put("type", "paragraph").put("content", new JSONArray()
                        .put(new JSONObject().put("type", "text").put("text", text)
                                .put("marks", color != null ? new JSONArray()
                                        .put(new JSONObject().put("type", "textColor")
                                                .put("attrs", new JSONObject().put("color", color)))
                                        : null)))));
        return cell;
    }

    // 테이블 행 생성
    private static JSONObject createTableRow(JSONArray rowContent) {
        JSONObject row = new JSONObject();
        row.put("type", "tableRow");
        row.put("content", rowContent);
        return row;
    }

}