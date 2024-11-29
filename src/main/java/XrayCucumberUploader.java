import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;

import java.io.File;
import java.io.IOException;

public class XrayCucumberUploader {
    public static void main(String[] args) {
        // Xray API 엔드포인트
        String url = "https://xray.cloud.xpand-it.com/api/v3/import/execution/cucumber";

        // Xray API Token
        String xrayApiToken = "72e8fa1d30bd08f91a473065410e06f4a1676eb2547247e1ea99be86c42b8de0";

        // Cucumber JSON 결과 파일 경로
        File cucumberJson = new File("target/cucumber.json"); // 경로 확인 후 수정

        if (!cucumberJson.exists()) {
            System.err.println("Cucumber JSON 파일을 찾을 수 없습니다: " + cucumberJson.getAbsolutePath());
            return;
        }

        // HttpClient 생성
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // POST 요청 생성
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Authorization", "Bearer " + xrayApiToken);

            // Multipart 데이터 생성
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("results", new FileBody(cucumberJson, ContentType.APPLICATION_JSON));
            httpPost.setEntity(builder.build());

            // 요청 전송 및 응답 확인
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();
                System.out.println("응답 코드: " + statusCode);

                if (statusCode == 200) {
                    System.out.println("Cucumber 결과 전송 성공!");
                } else {
                    System.err.println("전송 실패. 응답 코드: " + statusCode);
                }
            }
        } catch (IOException e) {
            System.err.println("오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
