package utils;

import io.appium.java_client.AppiumDriver;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import pages.BasePage;
import tests.BaseTest;

/**
 * Define desired behavior after testcase execution depending on the result
 */
public class TestResultListener extends TestListenerAdapter {

    @Override
    public void onTestStart(ITestResult tr) {

        BasePage.log("===============================================");
        BasePage.log("Starting test '" + tr.getName() + "'");
        BasePage.log("===============================================");
        BasePage.log("                                        ");
    }

    @Override
    public void onTestSuccess(ITestResult tr) {

        printScreenshotIntoReport(tr);

        BasePage.log("                                               ");
        BasePage.log("===============================================");
        BasePage.log("Test '" + tr.getName() + "' PASSED");
        BasePage.log("===============================================");
        BasePage.log("                                               ");
    }

    @Override
    public void onTestFailure(ITestResult tr) {

        printScreenshotIntoReport(tr);

        BasePage.log("                                               ");
        BasePage.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        BasePage.log("Oops, something went wrong...");
        BasePage.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        BasePage.log("                                               ");
        BasePage.log("===============================================");
        BasePage.log("Test '" + tr.getName() + "' FAILED");
        BasePage.log("===============================================");
        BasePage.log("                                               ");
    }

    @Override
    public void onTestSkipped(ITestResult tr) {

        printScreenshotIntoReport(tr);

        BasePage.log("                                               ");
        BasePage.log("===============================================");
        BasePage.log("Test '" + tr.getName() + "' SKIPPED");
        BasePage.log("===============================================");
        BasePage.log("                                               ");
    }

    public void printScreenshotIntoReport(ITestResult result) {
        AppiumDriver driver = getDriver(result);
        if (driver != null) {
            BasePage.printScreenShotIntoReport(driver);
        }
    }

    public AppiumDriver getDriver(ITestResult result) {
        Object currentClass = result.getInstance();
        AppiumDriver driver = ((BaseTest) currentClass).getDriver();
        return driver;
    }

}
