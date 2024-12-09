package utils;

import java.io.*;
        import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Jirafeatureissue {



    static String FeaturePath;
    static String Xray_API_URL;
    static String XRAY_API_TOKEN;



    public static void loadProperties(String propertiesFilePath) throws IOException {
        Properties properties = new Properties();

        try (InputStream input = new FileInputStream(propertiesFilePath)) {
            properties.load(input);
            FeaturePath = properties.getProperty("feature.path");
            Xray_API_URL = properties.getProperty("xray.api.featureurl");
            XRAY_API_TOKEN = properties.getProperty("xray.api.token");
        }
    }

    public static File zipFeatureFile(String featureDirectoryPath) throws IOException {
        File featureDirectory = new File(featureDirectoryPath);
        if (!featureDirectory.isDirectory()) {
            throw new IllegalArgumentException("feature.path must be a directory containing .feature files.");
        }

        File zipFile = new File(featureDirectory, "xray_cucumber_features.zip");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            File[] featureFiles = featureDirectory.listFiles((dir, name) -> name.endsWith(".feature"));

            if (featureFiles == null || featureFiles.length == 0) {
                throw new FileNotFoundException("No .feature files found in the specified directory.");
            }

            for (File featureFile : featureFiles) {
                try (FileInputStream fis = new FileInputStream(featureFile)) {
                    zos.putNextEntry(new ZipEntry(featureFile.getName()));
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }
            }
        }

        return zipFile;
    }


    // Xray API를 통해 Feature 파일을 Jira 이슈에 import하는 메서드
    public static void importFeatureFileToXray(File zipFile) throws Exception {
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

                System.out.println("Feature files successfully imported to Jira Issue " );
            } else {
                System.out.println("Failed to import feature files.");
            }
        } finally {
            if (os != null) os.close();
            if (in != null) in.close();
        }
    }




    public static void featureupload(String[] args) {
        try {

            String resourece = "src/main/resources/application.properties";
            loadProperties(resourece);
            File zipFile = zipFeatureFile(FeaturePath);
            System.out.println("Feature file zipped successfully: " + zipFile.getAbsolutePath());
            importFeatureFileToXray(zipFile);
            System.out.println("jirafeature upload 테스트 정상 실행");
            Thread.sleep(1500);
            System.out.println("엑스레이 report 실행 시작");
//            XrayReportUploader.XrayReport(new String[]{});
            System.out.println("엑스레이 실행 종료");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        try {
            featureupload(args);
//            String resourece = "src/main/resources/application.properties";
//            loadProperties(resourece);
//                File zipFile = zipFeatureFile(FeaturePath);
//                System.out.println("Feature file zipped successfully: " + zipFile.getAbsolutePath());
//                importFeatureFileToXray(zipFile);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

