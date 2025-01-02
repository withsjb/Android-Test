package utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Jiradefectissuecreate {
    static String JIRA_URL;
    static String JIRA_API_TOKEN;
    static String USERNAME;
    static String FeaturePath;
    static String Xray_API_URL;
    static String XRAY_API_TOKEN;


    public static void loadProperties(String propertiesFilePath) throws IOException {
        Properties properties = new Properties();

        try (InputStream input = new FileInputStream(propertiesFilePath)) {
            properties.load(input);
            JIRA_URL = properties.getProperty("jira.url");
            JIRA_API_TOKEN = properties.getProperty("jira.api.token");
            USERNAME = properties.getProperty("username");
            FeaturePath = properties.getProperty("feature.path");
            Xray_API_URL = properties.getProperty("xray.api.url");
            XRAY_API_TOKEN = properties.getProperty("xray.api.token");
        }
    }

    public static String createIssue(String jiraUrl, String username, String apiToken, String summary, String logdetail) throws Exception {
        String descriptionJson = AndroidManager.readJsonFromFile("src/main/resources/jiraissue.json");

        // 로그 내용을 description에 맞게 대체 (먼저 값을 넣고, 그 후에 특수 문자 치환)
        String faillogdetail = descriptionJson.replace("{fail log}", logdetail);

        // 특수 문자 처리 (줄바꿈, 큰따옴표 등)
        String description = faillogdetail.replace("\n", "\\n").replace("\"", "\\\"");

        // Jira Issue 생성 요청 JSON
        String jsonInputString = "{"
                + "\"fields\": {"
                + "\"project\": { \"key\": \"TW\" },"
                + "\"summary\": \"" + summary + "\","
                + "\"description\": \"" + description.replace("{fail log}", description) + "\","
                + "\"issuetype\": { \"name\": \"Defect\" }"
                + "}"
                + "}";

        // HTTP 연결 설정
        URL url = new URL(jiraUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((username + ":" + apiToken).getBytes()));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // 요청 본문 전송
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // 응답 확인
        int responseCode = connection.getResponseCode();
        System.out.println("Jira Issue Creation Response Code: " + responseCode);

        // 응답 본문 처리
        if (responseCode == HttpURLConnection.HTTP_CREATED) {
            // 이슈 생성이 성공적으로 이루어졌다면, 응답 본문을 통해 이슈 키를 추출
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                // 예시로 JSON 응답에서 "key" 값을 추출
                String issueKey = extractIssueKeyFromResponse(response.toString());
                return issueKey;
            }
        } else {
            System.out.println("Failed to create issue.");
            return null;
        }
    }

//    public static File zipFeatureFile(String featureFilePath) throws IOException {
//        File featureFile = new File(featureFilePath);
//        File zipFile = new File(featureFile.getParent(), "xray_cucumber_features.zip");
//
//        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
//             FileInputStream fis = new FileInputStream(featureFile)) {
//
//            zos.putNextEntry(new ZipEntry(featureFile.getName()));
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = fis.read(buffer)) > 0) {
//                zos.write(buffer, 0, length);
//            }
//            zos.closeEntry();
//        }
//
//        return zipFile;
//    }


    // Xray API를 통해 Feature 파일을 Jira 이슈에 import하는 메서드
    public static void importFeatureFileToXray(String issueKey, File zipFile) throws Exception {
        HttpURLConnection connection = null;
        OutputStream os = null;
        BufferedReader in = null;

        try {
            URL url = new URL(Xray_API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + XRAY_API_TOKEN);
            String boundary = UUID.randomUUID().toString();
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            connection.setDoOutput(true);

            // Multipart form-data로 Feature 파일 전송


            os = connection.getOutputStream();
            String CRLF = "\r\n";
            String twoHyphens = "--";

            // 파일의 시작 부분 (Boundary)
            os.write((twoHyphens + boundary + CRLF).getBytes());
            os.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + zipFile.getName() + "\"" + CRLF).getBytes());
            os.write(("Content-Type: application/zip" + CRLF).getBytes());
            os.write(CRLF.getBytes());

            // 파일 데이터 전송
            byte[] fileBytes = Files.readAllBytes(zipFile.toPath());
            os.write(fileBytes);
            os.write(CRLF.getBytes());

            // 끝 부분 (Boundary)
            os.write((twoHyphens + boundary + twoHyphens + CRLF).getBytes());

            // 서버 응답 확인
            int responseCode = connection.getResponseCode();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            System.out.println("Xray Import Response Code: " + responseCode);
            System.out.println("Xray Import Response: " + response.toString());

            // 응답에서 Jira 이슈 키 확인
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Feature files successfully imported to Jira Issue " + issueKey);
            } else {
                System.out.println("Failed to import feature files.");
            }
        } finally {
            if (os != null) os.close();
            if (in != null) in.close();
        }
    }

    // 응답에서 이슈 키를 추출하는 메서드
    public static String extractIssueKeyFromResponse(String response) {
        // 응답 JSON에서 "key" 값을 추출하는 로직
        // 예: {"id":"10001", "key":"SCRUM-123", "self":"https://..."}

        // 예시로 간단하게 key 추출 (실제 응답은 더 복잡할 수 있음)
        String key = null;
        int keyStart = response.indexOf("\"key\":\"") + 7;
        int keyEnd = response.indexOf("\"", keyStart);
        if (keyStart != -1 && keyEnd != -1) {
            key = response.substring(keyStart, keyEnd);
        }
        return key;

    }


    public static void defectissuecreate(String logdetail, String jira_url, String username, String jira_api){
        try {
            String summary = "defect 에러";

            String issueKey = createIssue(jira_url, username, jira_api, summary, logdetail);

            if (issueKey != null) {
                System.out.println("defect 이슈가 생성되었습니다! *****: " + issueKey);
//                File zipFile = zipFeatureFile(FeaturePath);
//                System.out.println("Feature file zipped successfully: " + zipFile.getAbsolutePath());
//                importFeatureFileToXray(issueKey, zipFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            String resourece = "src/main/resources/application.properties";
            loadProperties(resourece);
            String new_description = "예외 발생: 실패 유도\n";
            String redescription = new_description.replace("\n", "\\n").replace("\"", "\\\"");

            defectissuecreate(redescription, JIRA_URL, USERNAME, JIRA_API_TOKEN);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}