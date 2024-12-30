package utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.type.TypeFactory;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.cucumber.java.Scenario;
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
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Singleton Design Pattern
public class AndroidManager {

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
                        new URL("http://127.0.0.1:4723"), options
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

        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File screenshotReport = new File("src/main/save/screenshots/error_" + Filename + ".png");
//+ System.currentTimeMillis() + 는 추가 할수도 안할수도


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
        System.out.println(logBuffer.toString());

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
//    public static void writeCucumberJson(String filePath, List<Map<String, Object>> results) throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        FileWriter writer = new FileWriter(filePath);
//        mapper.writerWithDefaultPrettyPrinter().writeValue(writer, results);
//    }
//
//    public static String getDateFromTimestamp(String timestamp) {
//        try {
//            System.out.println("시간 자르기: " + timestamp);  // timestamp 출력 확인
//            return timestamp.substring(0, 10);  // yyyy-MM-dd 형식으로 잘라서 반환
//        } catch (Exception e) {
//            return "시간쪽 에러남";
//        }
//    }
//
//    public static List<Map<String, Object>> readCucumberJson(String filePath) throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        File file = new File(filePath);
//
//        // 파일이 존재하지 않거나 비어 있으면 빈 리스트 반환
//        if (!file.exists() || file.length() == 0) {
//            System.out.println("파일이 없거나 비어 있습니다. 빈 리스트를 반환합니다.");
//            return new ArrayList<>();
//        }
//
//        // 파일이 존재하면 JSON 데이터를 읽어 리스트로 변환
//        return mapper.readValue(file, TypeFactory.defaultInstance().constructCollectionType(List.class, Map.class));
//    }
//
//    public static void copyAndUpdateResults(String cucumberJsonPath, String weekJsonPath) throws IOException {
//        // cucumber.json에서 현재 결과 읽기
//        List<Map<String, Object>> currentResults = readCucumberJson(cucumberJsonPath);
//
//        // cucumberweek.json에서 기존 결과 읽기
//        List<Map<String, Object>> weekResults = readCucumberJson(weekJsonPath);
//
//        // 새로운 결과를 기존 결과에 추가하거나 갱신 (같은 날짜는 갱신)
//        weekResults = updateResultsWithCurrentDate(weekResults, currentResults);
//
//        // cucumberweek.json에 갱신된 결과 저장
//        writeCucumberJson(weekJsonPath, weekResults);
//    }
//
//
//    public static List<Map<String, Object>> updateResultsWithCurrentDate(List<Map<String, Object>> weekResults, List<Map<String, Object>> currentResults) {
//        Map<String, List<Map<String, Object>>> groupedResults = new HashMap<>();
//
//        // 기존 weekResults에서 날짜별로 그룹화
//        for (Map<String, Object> result : weekResults) {
//            String date = getDateFromTimestamp((String) result.get("start_timestamp"));
//            groupedResults.putIfAbsent(date, new ArrayList<>());
//            groupedResults.get(date).add(result);
//        }
//
//        // currentResults에서 날짜별로 그룹화
//        for (Map<String, Object> result : currentResults) {
//            String date = getDateFromTimestamp((String) result.get("start_timestamp"));
//
//            // 동일 날짜가 있으면 기존 결과를 덮어쓰고, 없으면 새로 추가
//            if (groupedResults.containsKey(date)) {
//                List<Map<String, Object>> existingResults = groupedResults.get(date);
//                // 가장 최근 결과로 덮어쓰는 부분 (필요 시 덮어쓸 추가 로직 구현 가능)
//                existingResults.clear();
//                existingResults.add(result);  // 새로운 데이터 추가
//            } else {
//                // 해당 날짜가 없으면 새로 추가
//                groupedResults.put(date, new ArrayList<>(List.of(result)));
//            }
//        }
//
//        // 결과 리스트로 다시 변환
//        List<Map<String, Object>> updatedResults = new ArrayList<>();
//        for (List<Map<String, Object>> dailyResults : groupedResults.values()) {
//            updatedResults.addAll(dailyResults);
//        }
//
//        return updatedResults;
//    }

//-----------------------------------******--------------

//    public static List<Map<String, Object>> readCucumberJson(String filePath) throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        File file = new File(filePath);
//
//        if (file.exists()) {
//            System.out.println("readcucumberjson 함수 234줄 : " + mapper.readValue(file, TypeFactory.defaultInstance().constructCollectionType(List.class, Map.class)));
//            return mapper.readValue(file, TypeFactory.defaultInstance().constructCollectionType(List.class, Map.class));
//        } else {
//            return new ArrayList<>();
//        }
//    }
//
//    // cucumberweek.json 파일 쓰기
//    public static void writeCucumberJson(String filePath, List<Map<String, Object>> results) throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        FileWriter writer = new FileWriter(filePath);
//        mapper.writerWithDefaultPrettyPrinter().writeValue(writer, results);
//    }
//
//    // ISO 8601 타임스탬프를 밀리초로 변환
//    public static long parseTimestamp(String timestamp) {
//        try {
//            return javax.xml.bind.DatatypeConverter.parseDateTime(timestamp).getTimeInMillis();
//        } catch (Exception e) {
//            return 0;
//        }
//    }
//
//    // ISO 8601 형식에서 날짜만 잘라서 반환 (yyyy-MM-dd)
//    public static String getDateFromTimestamp(String timestamp) {
//        try {
//            System.out.println("시간 자르기: " + timestamp);  // timestamp 출력 확인
//            return timestamp.substring(0, 10);  // yyyy-MM-dd 형식으로 잘라서 반환
//        } catch (Exception e) {
//            return "시간쪽 에러남";
//        }
//    }
//
//    // 결과를 날짜별로 갱신(덮어쓰기 및 추가)
//    public static List<Map<String, Object>> updateResultsWithCurrentDate(List<Map<String, Object>> weekResults, List<Map<String, Object>> currentResults) {
//        Map<String, List<Map<String, Object>>> groupedResults = new HashMap<>();
//
//        // weekResults에서 날짜별로 그룹화
//        for (Map<String, Object> result : weekResults) {
//            String date = getDateFromTimestamp((String) result.get("start_timestamp"));
//            groupedResults.putIfAbsent(date, new ArrayList<>());
//            groupedResults.get(date).add(result);
//        }
//
//        // currentResults에서 날짜별로 그룹화
//        for (Map<String, Object> result : currentResults) {
//            String date = getDateFromTimestamp((String) result.get("start_timestamp"));
//
//            // 동일 날짜가 있으면 기존 결과를 덮어쓰고, 없으면 새로 추가
//            if (groupedResults.containsKey(date)) {
//                // 기존 데이터를 덮어쓰지 않고, 새로운 결과 추가
//                List<Map<String, Object>> existingResults = groupedResults.get(date);
////                existingResults.clear();  // 기존 결과 삭제
//                existingResults.add(result);  // 새로운 결과 추가
//            } else {
//                // 해당 날짜가 없으면 새로 추가
//                groupedResults.put(date, new ArrayList<>(List.of(result)));
//            }
//        }
//
//        // 결과 리스트로 다시 변환
//        List<Map<String, Object>> updatedResults = new ArrayList<>();
//        for (List<Map<String, Object>> dailyResults : groupedResults.values()) {
//            updatedResults.addAll(dailyResults);  // 모든 날짜의 결과를 다시 리스트로 합침
//        }
//
//        return updatedResults;
//    }
}




