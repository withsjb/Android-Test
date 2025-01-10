package utils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import org.jfree.chart.ChartUtilities;

import java.awt.*;
import java.io.File;
import java.io.IOException;


public class cucumberchart {

    public static JFreeChart createPiechart(int countpassed, int countfailed){
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Passed", countpassed);
        dataset.setValue("Failed", countfailed);

        // 차트 생성
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Test Results",  // 차트 제목
                dataset,         // 데이터셋
                true,            // 범례 표시
                true,            // 툴팁 표시
                false);          // URL 처리

        // PiePlot 객체를 가져와서 각 항목의 색상 지정
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setSectionPaint("Passed", Color.GREEN);  // Passed 항목 색상 지정 (초록색)
        plot.setSectionPaint("Failed", Color.RED);    // Failed 항목 색상 지정 (빨간색)

        return pieChart;
    }


    public static void savePieChartAsImage(JFreeChart chart, String filePath) throws IOException {
        File file = new File(filePath);
        ChartUtilities.saveChartAsPNG(file, chart, 500, 500); // 이미지 크기 설정
    }
}
