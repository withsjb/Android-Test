package utils;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Properties;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class XrayReportUploader {


    static String Xray_API_URL;
    static String XRAY_API_TOKEN;

    static String USER_DIR;

    static String JIRA_URL;

    static String USERNAME;
    static String JIRA_TOKEN;

    static String oneonetestPlen;

    static String logpath;
    static String errorcaturepath;

    static String logandimgzip;

    private static ZonedDateTime koreantime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

    private static String formatdate = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(koreantime);

    public static void loadProperties(String propertiesFilePath) throws IOException {
        Properties properties = new Properties();

        try (InputStream input = new FileInputStream(propertiesFilePath)) {
            properties.load(input);
            USER_DIR = properties.getProperty("user.dir");
            Xray_API_URL = properties.getProperty("xray.api.cucumberurl");
            XRAY_API_TOKEN = properties.getProperty("xray.api.token");
            JIRA_URL = properties.getProperty("jira.base.url");
            JIRA_TOKEN = properties.getProperty("jira.api.token");
            USERNAME = properties.getProperty("username");
            oneonetestPlen = properties.getProperty("11st.plen");
            logpath = properties.getProperty("log.path");
            errorcaturepath = properties.getProperty("errorcature.path");
            logandimgzip = properties.getProperty("log.img.zip");
        }
    }


      //  jira 최근 이슈 jql 로 찾기
        public static String findrecentissue(String projectKey, String jiraApiToken, String username)throws Exception{

            String jql = "project = " + projectKey  + " AND issuetype = 'BUG' ORDER BY created DESC";
            String searchUrl = JIRA_URL + "/rest/api/3/search?jql=" + URLEncoder.encode(jql, StandardCharsets.UTF_8);
            System.out.println(searchUrl);

            HttpURLConnection connection = AndroidManager.connect(searchUrl, "GET", username, jiraApiToken );

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream is = connection.getInputStream()) {
                    String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray issues = jsonResponse.getJSONArray("issues");

                    if (issues.length() > 0) {
                        String issueKey = issues.getJSONObject(0).getString("key");
                        System.out.println("최근 생성된 Issue Key: " + issueKey);
                        return issueKey;
                    } else {
                        System.out.println("해당 프로젝트에 테스트 실행 이슈가 없습니다.");
                        return null;
                    }
                }
            } else {
                System.out.println("Jira 이슈 검색 실패. 응답 코드: " + responseCode);
                return null;
            }
        }


        //update isuue
        public static void update_summary(String issueKey, String summary,String jiraApiToken, String username )throws Exception {
            String url = JIRA_URL + "/rest/api/3/issue/" + issueKey;
            System.out.println(url);
            HttpURLConnection connection = AndroidManager.connect(url, "PUT", username, jiraApiToken);

            String jsonPayload = "{"
                    + "\"fields\": {"
                    + "\"summary\": \"" + summary + "\""
                    + "}"
                    + "}";

            // 요청 본문을 출력 스트림에 작성
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                System.out.println("이슈 이름 바꾸기 성공.");
            } else {
                System.out.println("이슈 이름 바꾸기 실패. Response code: " + responseCode);
                try (InputStream errorStream = connection.getErrorStream()) {
                    String errorResponse = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                    System.out.println("Error Response: " + errorResponse);
                }
            }

        }

        //테스트 결과 테스트 플랜하고 링크하기
    public static void LinkTeatPlen(String executKey,String testplenKey, String jiraApiToken, String username )throws Exception {
        String TestPlenurl = JIRA_URL + "/rest/api/3/issueLink";

        System.out.println(TestPlenurl);
        System.out.println(executKey);
        System.out.println(testplenKey);
      getissue(executKey, jiraApiToken, username);
        getissue(testplenKey, jiraApiToken, username);


        
        HttpURLConnection connection = AndroidManager.connect(TestPlenurl, "POST", username, jiraApiToken);

        String jsonPayload = "{"
                + "\"type\": {"
                + "\"name\": \"Test\""
                + "},"
                + "\"inwardIssue\": {"
                + "\"key\": \"" + executKey + "\""
                + "},"
                + "\"outwardIssue\": {"
                + "\"key\": \"" + testplenKey + "\""
                + "}"
                + "}";

        connection.setDoOutput(true);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
            System.out.println( testplenKey + "에 링크 성공");
        } else {
            System.out.println("실패. Response code: " + responseCode);
            try (InputStream errorStream = connection.getErrorStream()) {
                String errorResponse = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Error Response: " + errorResponse);
            }
        }
    }


    //getissue
    public static void getissue(String issueKey, String jiraApiToken, String username)throws Exception {

        String geturl = JIRA_URL + "/rest/api/3/issue/" + issueKey;

        HttpURLConnection connection = AndroidManager.connect(geturl, "GET", username, jiraApiToken);
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                System.out.println("getissue Response: " + response);
                System.out.println("___________");
            }
        } else {
            System.out.println("Failed: HTTP error code : " + responseCode);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    errorResponse.append(line);
                }
                System.out.println("Error Response: " + errorResponse);
            }
        }
    }



            // Cucumber JSON 결과를 Xray에 업로드하는 메서드
       public static void uploadTestReport(String cucumberJsonFilePath) throws Exception {
        // Cucumber JSON 파일을 읽기
        File cucumberJsonFile = new File(cucumberJsonFilePath);
           System.out.println("cucumber 위치: "+ cucumberJsonFilePath);
        byte[] cucumberJsonBytes = Files.readAllBytes(cucumberJsonFile.toPath());



        // HTTP 연결 설정
        URL url = new URL(Xray_API_URL);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + XRAY_API_TOKEN);

        // Bearer Token으로 인증
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);


        System.out.println(connection.getOutputStream());
        // JSON 데이터를 요청 본문에 추가
        try (OutputStream os = connection.getOutputStream()) {
            os.write(cucumberJsonBytes);
            System.out.println("Sending JSON data: " + new String(cucumberJsonBytes, StandardCharsets.UTF_8));


        }

        // 응답 확인
        int responseCode = connection.getResponseCode();
        System.out.println("Xray Response Code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Report uploaded successfully.");

        } else {
            System.out.println("Failed to upload report to Xray.");
        }

        //link 하기
    }



    public static void XrayReport(String[] args) {
        try {
            // Cucumber JSON 파일 경로와 Jira 이슈 키를 전달
            String resourece = "src/main/resources/application.properties";
            String projectKey = "SCRUM";

            loadProperties(resourece);
            System.out.println("Xray API URL: " + Xray_API_URL);
            System.out.println("Xray API Token: " + XRAY_API_TOKEN);

            String cucumberJsonFilePath = "target/cucumber.json";  // Cucumber JSON 파일 경로

            uploadTestReport(cucumberJsonFilePath);
           String issuekey =  findrecentissue(projectKey, JIRA_TOKEN, USERNAME);
           String new_summary = "11st test " + "[" + formatdate + "]";
           System.out.println(new_summary);
           update_summary(issuekey,new_summary,JIRA_TOKEN,USERNAME);
            captureandlog(issuekey,USERNAME,JIRA_TOKEN,logpath,errorcaturepath);
           LinkTeatPlen(issuekey, oneonetestPlen, JIRA_TOKEN, USERNAME );
           System.out.println(cucumberJsonFilePath);
            System.out.println("Xray report 테스트 정상 실행");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //log 및 캡쳐 사진 첨부

    public static void zipFiles(File[] files, String zipFileName) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFileName))) {
            byte[] buffer = new byte[1024];
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    zos.putNextEntry(new ZipEntry(file.getName()));
                    int length;
                    while ((length = fis.read(buffer)) >= 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }
            }
        }
    }

    // Jira에 파일 첨부 메서드
    public static void captureandlog(String issueKey, String username, String jiraApiToken, String logDirPath, String pngDirPath) throws Exception {
        String url = JIRA_URL + "/rest/api/3/issue/" + issueKey + "/attachments";
        System.out.println("첨부파일 url" + url);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((username + ":" + jiraApiToken).getBytes()));
        connection.setRequestProperty("X-Atlassian-Token", "no-check");  //xsrf 우화
        String boundary = UUID.randomUUID().toString();
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setDoOutput(true);

        // .log와 .png 파일 리스트 가져오기
        File[] logFiles = new File(logDirPath).listFiles((dir, name) -> name.endsWith(".log"));
        File[] pngFiles = new File(pngDirPath).listFiles((dir, name) -> name.endsWith(".png"));

        // 파일 리스트가 존재하면 압축 생성
        if (logFiles != null || pngFiles != null) {
            String zipFilePath = "logs_and_images.zip"; // 압축 파일 경로

            // .log와 .png 파일을 압축
            File tempZipFile = new File(zipFilePath);
            try {
                zipFiles(concatenateFiles(logFiles, pngFiles), zipFilePath); // 두 종류 파일을 하나로 합쳐서 압축
            } catch (IOException e) {
                System.out.println("파일 압축 중 오류 발생: " + e.getMessage());
                return;
            }

            // 파일을 첨부하는 부분
            try (OutputStream os = connection.getOutputStream()) {
                os.write(("--" + boundary + "\r\n").getBytes());
                os.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + tempZipFile.getName() + "\"\r\n").getBytes());
                os.write(("Content-Type: application/zip\r\n").getBytes());
                os.write(("Content-Transfer-Encoding: binary\r\n\r\n").getBytes());

                byte[] fileBytes = Files.readAllBytes(tempZipFile.toPath());
                os.write(fileBytes);
                os.write(("\r\n--" + boundary + "--\r\n").getBytes());
            }

            // 서버 응답 처리
            int responseCode = connection.getResponseCode();
            System.out.println("jira에 압축파일 전송 성공: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("로그 및 사진 전송 성공");
            } else {
                System.out.println("파일 첨부 실패.");
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    System.out.println("Error Response: " + errorResponse);
                }
            }
        } else {
            System.out.println("로그 파일이나 이미지 파일이 없습니다.");
        }
    }

    // .log와 .png 파일들을 하나의 배열로 합침
    public static File[] concatenateFiles(File[] logFiles, File[] pngFiles) {
        int logCount = logFiles == null ? 0 : logFiles.length;
        int pngCount = pngFiles == null ? 0 : pngFiles.length;
        File[] allFiles = new File[logCount + pngCount];

        if (logFiles != null) {
            System.arraycopy(logFiles, 0, allFiles, 0, logCount);
        }
        if (pngFiles != null) {
            System.arraycopy(pngFiles, 0, allFiles, logCount, pngCount);
        }

        return allFiles;
    }


    public static void main(String[] args) {
        try {

            XrayReport(args);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}