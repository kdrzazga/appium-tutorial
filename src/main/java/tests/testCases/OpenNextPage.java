package tests.testCases;


import org.testng.annotations.Test;
import pages.pageClasses.FirstPage;
import pages.pageClasses.NextPage;
import tests.BaseTest;

import static org.assertj.core.api.Assertions.assertThat;

public class OpenNextPage extends BaseTest {   //TODO change class name

    final private static String TEST_SUCCESS = "user opened next page";   //TODO put your success message here

    //TODO implement method to perform actual test in your app
    @Test
    public void openNextPage() {

        FirstPage firstPage = new FirstPage(driver);

        firstPage.isPageDisplayed();
        //firstPage.doSomeAction();
        NextPage nextPage = firstPage.doSomeActionLeadingToOtherPage();

        assertThat(nextPage.isPageDisplayed()).isTrue();

        logTestSuccess(TEST_SUCCESS);
    }

}