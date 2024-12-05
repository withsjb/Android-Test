package steps;

import io.cucumber.java.an.E;
//import org.junit.After;
//import org.junit.Before;
import io.cucumber.java.Before;
import io.cucumber.java.After;

import org.junit.AfterClass;
import utils.AndroidManager;
import utils.Jirafeatureissue;
import utils.XrayReportUploader;

import java.io.File;
import java.net.MalformedURLException;


public class Hooks {

    @Before
    public void startapp() throws MalformedURLException {
           System.out.println("before 테스트 정상 실행");
        AndroidManager.getDriver();

    }





    @After
    public void finisapp(){



            Jirafeatureissue.featureupload(new String[]{});

//           XrayReportUploader.XrayReport(new String[]{});
           System.out.println("after 테스트 정상 실행");
        }

    }



