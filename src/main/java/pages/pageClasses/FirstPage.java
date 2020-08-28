package pages.pageClasses;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import pages.BasePage;

public class FirstPage extends BasePage {   //TODO change class name

    public static final String PAGE_TITLE = "first page";   //TODO put your page name here

    //TODO implement your elements
/*
    @AndroidFindBy(id = "id of element")
    private MobileElement referenceElement;

    @AndroidFindBy(xpath = "xpath of element")
    private MobileElement someElement;
*/
    @AndroidFindBy(id = "com.discord:id/auth_landing_login")
    private MobileElement loginButton;


    public FirstPage(AppiumDriver driver) {
        super(driver);
    }

    //Using method from superclass to determine if reference element is displayed
    public boolean isPageDisplayed() {
        return isElementDisplayed(loginButton, 30, PAGE_TITLE);
    }

    //TODO implement actual actions

    public void doSomeAction() {
        //TODO code performing some action like:
        log("Explaining what is done");
        //someElement.sendKeys("xyz");
    }

    public NextPage doSomeActionLeadingToOtherPage() {
        //TODO code performing action resulting in going to other page like:
        log("Explaining what is done");
        loginButton.click();
        return new NextPage(driver);
    }

}
