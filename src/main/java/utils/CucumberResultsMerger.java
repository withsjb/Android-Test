package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CucumberResultsMerger {
    public static void main(String[] args) {
        String resultsDir = "path/to/cucumber/results"; // Cucumber 결과 파일 경로
        String mergedOutputFile = "path/to/output/merged_results.json"; // 병합 결과 저장 경로

        try {
            // 1. 지난 일주일치 파일 필터링
            List<File> recentFiles = getRecentFiles(resultsDir);

            // 2. JSON 병합
            ObjectMapper mapper = new ObjectMapper();
            ArrayNode mergedResults = mapper.createArrayNode();

            for (File file : recentFiles) {
                JsonNode fileContent = mapper.readTree(file);
                if (fileContent.isArray()) {
                    fileContent.forEach(mergedResults::add);
                }
            }

            // 3. 결과 저장
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(mergedOutputFile), mergedResults);
            System.out.println("병합 완료: " + mergedOutputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<File> getRecentFiles(String directoryPath) throws IOException {
        File dir = new File(directoryPath);
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<File> recentFiles = new ArrayList<>();

        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".json")) {
                    String fileName = file.getName().replace(".json", "");
                    LocalDate fileDate = LocalDate.parse(fileName, formatter);

                    if (!fileDate.isBefore(today.minusDays(7))) {
                        recentFiles.add(file);
                    }
                }
            }
        }

        return recentFiles;
    }
}
