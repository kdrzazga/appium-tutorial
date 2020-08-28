package pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class BasePage {

    protected AppiumDriver driver;

    public BasePage(AppiumDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    /**
     * Useful method for checking if element is displayed without failing a test if it's not found (you can do that in assertion if you want)
     * you may use it for checking if the page is displayed using some unique element on it as reference
     * @param element element to check visibility of
     * @param timeoutSeconds timeout for confirming visibility of specified element
     * @param elementName element name to be used in report output
     * @return
     */
    public boolean isElementDisplayed(MobileElement element, int timeoutSeconds, String elementName) {
        try {
            log("Checking if " + elementName + " is displayed...");
            waitForElementVisibility(element, timeoutSeconds);
            return true;
        } catch (TimeoutException exception) {
            log("Looks like " + elementName + " is not displayed...");
        }
        return false;
    }

    /**
     * Waits specified amount of time for specified element to be visible
     * Throws exception if element doesn't become visible before timeout
     * @param element element expected to be visible
     * @param timeoutSeconds timeout for meeting expected conditions (visibility of element)
     */
    protected void waitForElementVisibility(MobileElement element, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeoutSeconds);
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Uses chained touch actions to perform swipe of specified duration specified number of times
     * @param times number of repetitions
     * @param x1 horizontal point of screen to start swipe at (in percent eg. 0.25, from left)
     * @param y1 vertical point of screen to start swipe at (in percent eg. 0.25, from top)
     * @param x2 horizontal point of screen to end swipe at (in percent eg. 0.75, from left)
     * @param y2 vertical point of screen to start swipe at (in percent eg. 0.5, from top)
     * @param ms duration of swipe (use small values to swipe a lot of content, larger for precise ones)
     */
    public void swipeByScreenPercentage(int times, double x1, double y1, double x2, double y2, int ms) {

        Dimension size = driver.manage().window().getSize();
        int xstart = (int) (size.width * x1);
        int ystart = (int) (size.height * y1);
        int xend = (int) (size.width * x2);
        int yend = (int) (size.height * y2);

        for (int i = 0; i < times; i++) {
            TouchAction touchAction = new TouchAction(driver);
            touchAction.press(PointOption.point(xstart, ystart)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(ms))).moveTo(PointOption.point(xend, yend)).release().perform();
            delay(200);
        }
    }

    /**
     * Sets global timeout for finding elements
     * @param seconds timeout in seconds
     */
    public void setImplicityWait(int seconds) {
        driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
    }

    /**
     * Waits for specified amount of milliseconds before continuing execution
     * Avoid using as means of waiting for elements, use WebDriverWait for that
     * @param delayMilliseconds milliseconds to wait
     */
    public void delay(int delayMilliseconds) {
        try {
            Thread.sleep(delayMilliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints screenshot into TestNG report
     * Calling will obscure output with base64 encoded image string
     * @param driver instance of AppiumDriver
     */
    public static void printScreenShotIntoReport(AppiumDriver driver) {
        String scrString = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        ScreenOrientation orientation = driver.getOrientation();
        if (orientation.toString().equalsIgnoreCase("LANDSCAPE")) {
            log("");
            log("<img style='width:500px;height:281px;' src=\"data:image/jpeg;base64," + scrString + "\">");
        } else {
            log("");
            log("<img style='width:281px;height:500px;' src=\"data:image/jpeg;base64," + scrString + "\">");
        }
    }

    /**
     * Prints param text into TestNG report
     * @param text text to print
     */
    public static void log(String text) {
        Reporter.log(text, true);
    }

}
