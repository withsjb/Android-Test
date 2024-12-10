//package utils;
//
//import io.cucumber.java.Scenario;
//import org.monte.media.Format;
//import org.monte.media.FormatKeys;
//import org.monte.media.math.Rational;
//import org.monte.screenrecorder.ScreenRecorder;
//
//import java.awt.*;
//import java.io.File;
//
//import static org.monte.media.FormatKeys.*;
//import static org.monte.media.FormatKeys.FrameRateKey;
//import static org.monte.media.VideoFormatKeys.*;
//import static org.monte.media.VideoFormatKeys.QualityKey;
//
//public class javarecodingexample {
//
//    public void startRecording(String fileName) throws Exception {
//        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
//                .getDefaultScreenDevice()
//                .getDefaultConfiguration();
//        screenRecorder = new ScreenRecorder(gc, null, new Format(MediaTypeKey, FormatKeys.MediaType.FILE, MimeTypeKey, MIME_AVI),
//                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
//                        CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24,
//                        FrameRateKey, Rational.valueOf(15), QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
//                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)),
//                null, new File(fileName));
//        screenRecorder.start();
//    }
//
//    public void stopRecording() throws Exception {
//        if (screenRecorder != null) {
//            screenRecorder.stop();
//        }
//    }
//
//-------------------------hook.java---------------
//
//    private void deleteFilesInDirectory(File directory) {
//        File[] files = directory.listFiles();
//        if (files != null) {
//            for (File file : files) {
//                if (file.isDirectory()) {
//                    // 서브디렉토리라면 재귀적으로 삭제
//                    deleteFilesInDirectory(file);
//                }
//                if (file.exists() && file.isFile()) {
//                    if (file.delete()) {
//                        System.out.println("파일 삭제 완료: " + file.getAbsolutePath());
//                    } else {
//                        System.out.println("파일 삭제 실패: " + file.getAbsolutePath());
//                    }
//                }
//            }
//        }
//    }
//
//    public void startvideo(Scenario scenario)throws Exception{
//        String scenarioName = scenario.getName().replaceAll(" ", "_");
//        recordingFilePath = "src/main/save/video/" + scenarioName + ".avi";
//        androidManager.startRecording(recordingFilePath);
//        System.out.println("영상 저장 경로(시작)" + recordingFilePath);
//        System.out.println("녹화를 시작합니다.");
//
//    }
//
//    public void stopvideo(Scenario scenario)throws Exception{
//
//        androidManager.stopRecording();
//        File recordingDirectory = new File(recordingFilePath);
//        if (scenario.isFailed()) {
//            System.out.println("테스트 실패 녹화 저장: " + recordingFilePath);
//            System.out.println("영상 저장 경로(저장)" + recordingFilePath);
//            System.out.println("녹화를 종료합니다.");
//        } else {
//            // 테스트 성공 시 녹화본 삭제
//            if (recordingDirectory.exists() && recordingDirectory.isDirectory()) {
//                // 폴더 내 모든 파일을 삭제
//                deleteFilesInDirectory(recordingDirectory);
//
//                // 폴더 삭제
//                if (recordingDirectory.delete()) {
//                    System.out.println("폴더 삭제 완료: " + recordingDirectory.getAbsolutePath());
//                } else {
//                    System.out.println("폴더 삭제 실패: " + recordingDirectory.getAbsolutePath());
//                }
//            }
//        }
//    }
//}
