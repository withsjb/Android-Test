package utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;

public class XrayReportUploader {


    static String Xray_API_URL;
    static String XRAY_API_TOKEN;

    static String USER_DIR;


    public static void loadProperties(String propertiesFilePath) throws IOException {
        Properties properties = new Properties();

        try (InputStream input = new FileInputStream(propertiesFilePath)) {
            properties.load(input);
            USER_DIR = properties.getProperty("user.dir");
            Xray_API_URL = properties.getProperty("xray.api.cucumberurl");
            XRAY_API_TOKEN = properties.getProperty("xray.api.token");
        }
    }
    // Cucumber JSON 결과를 Xray에 업로드하는 메서드
       public static void uploadTestReport(String cucumberJsonFilePath) throws Exception {
        // Cucumber JSON 파일을 읽기
        File cucumberJsonFile = new File(cucumberJsonFilePath);
           System.out.println("패스 함수: "+ cucumberJsonFilePath);
        byte[] cucumberJsonBytes = Files.readAllBytes(cucumberJsonFile.toPath());
        System.out.println("uploadtestreport 함수: "+ cucumberJsonBytes);


        System.out.println("*번 실행");
        // HTTP 연결 설정
        URL url = new URL(Xray_API_URL);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + XRAY_API_TOKEN);

        // Bearer Token으로 인증
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        System.out.println("**번 실행");

        System.out.println(connection.getOutputStream());
        // JSON 데이터를 요청 본문에 추가
        try (OutputStream os = connection.getOutputStream()) {
            os.write(cucumberJsonBytes);
            System.out.println("Sending JSON data: " + new String(cucumberJsonBytes, StandardCharsets.UTF_8));

            System.out.println("***번 실행");
        }
        System.out.println("****번 실행");
        // 응답 확인
        int responseCode = connection.getResponseCode();
        System.out.println("Xray Response Code: " + responseCode);
        System.out.println("*****번 실행");
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Report uploaded successfully.");
            System.out.println("******번 실행");
        } else {
            System.out.println("Failed to upload report to Xray.");
        }
        System.out.println("*********번 실행");
    }

    public static void XrayReport(String[] args) {
        try {
            // Cucumber JSON 파일 경로와 Jira 이슈 키를 전달
            String resourece = "src/main/resources/application.properties";
            loadProperties(resourece);
            System.out.println("Xray API URL: " + Xray_API_URL);
            System.out.println("Xray API Token: " + XRAY_API_TOKEN);
            System.out.println("1번 실행");
            String cucumberJsonFilePath = USER_DIR + "target/cucumber.json";  // Cucumber JSON 파일 경로
            System.out.println("2번 실행");
            uploadTestReport(cucumberJsonFilePath);
            System.out.println("3번 실행");
            System.out.println(cucumberJsonFilePath);
            System.out.println("Xray report 테스트 정상 실행");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        try {
            XrayReport(args);
            // Cucumber JSON 파일 경로와 Jira 이슈 키를 전달
//            String resourece = "src/main/resources/application.properties";
//            loadProperties(resourece);
//            String cucumberJsonFilePath = "target/cucumber.json";  // Cucumber JSON 파일 경로
//            uploadTestReport(cucumberJsonFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}