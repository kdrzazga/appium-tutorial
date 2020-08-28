package tests;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import pages.BasePage;
import utils.Config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Define methods to be performed at certain points of execution here
 * as well as methods and variables to be available in all tests
 */
public class BaseTest {

    protected AppiumDriver driver;

    @BeforeTest
    protected void setDriver() {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("deviceName", "deviceName");
        capabilities.setCapability("automationName", "uiautomator2");
        capabilities.setCapability("appPackage", Config.APP_PACKAGE);
        capabilities.setCapability("appActivity", Config.APP_ACTIVITY);
        capabilities.setCapability("skipUnlock", true);

        try {
            driver = new AndroidDriver(new URL(Config.SERVER_URL), capabilities);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
    }

    @AfterTest
    protected void quitDriver() {
        driver.quit();
    }

    /**
     * Prints specified success message to report
     * intended to be used at the end of the test
     * @param successMessage message to print
     */
    public void logTestSuccess(String successMessage) {
        BasePage.log("");
        BasePage.log("-----------------------------------------------");
        BasePage.log("Test successful: " + successMessage);
        BasePage.log("-----------------------------------------------");
    }

    public AppiumDriver getDriver () {
        return driver;
    }
}
