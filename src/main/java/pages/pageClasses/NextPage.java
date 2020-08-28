package pages.pageClasses;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import pages.BasePage;

public class NextPage extends BasePage {   //TODO change class name

    //TODO implement entire class to reflect actual screen in your app

    public static final String PAGE_TITLE = "next page";

    @AndroidFindBy(xpath = "com.discord:id/auth_login")
    private MobileElement emailTextbox;

    @AndroidFindBy(xpath = "com.discord:id/auth_login")
    private MobileElement passwdTextbox;

    @AndroidFindBy(id = "com.discord:id/auth_login")
    private MobileElement loginButton;


    public NextPage(AppiumDriver driver) {
        super(driver);
    }

    public boolean isPageDisplayed() {
        return isElementDisplayed(loginButton, 30, PAGE_TITLE);
    }

}
