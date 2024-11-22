package steps;

import org.junit.Before;
import utils.AndroidManager;

import java.net.MalformedURLException;

public class Hooks {

    @Before
    public void startapp() throws MalformedURLException {
        AndroidManager.getDriver();
    }

}


