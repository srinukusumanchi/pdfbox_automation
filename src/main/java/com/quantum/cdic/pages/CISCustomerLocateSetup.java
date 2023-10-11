package com.quantum.cdic.pages;
/**
 * Created by Srinu Kusumanchi(s3810121) for CDIC January Release.
 */

import com.qmetry.qaf.automation.ui.WebDriverBaseTestPage;
import com.qmetry.qaf.automation.ui.annotations.FindBy;
import com.qmetry.qaf.automation.ui.api.PageLocator;
import com.qmetry.qaf.automation.ui.api.WebDriverTestPage;
import com.qmetry.qaf.automation.ui.webdriver.QAFWebElement;
import com.quantum.baseclass.BasePage;
import com.quantum.java.pages.ExtentReportHelper;
import com.quantum.utility.LoggingUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static com.quantum.constants.CommonConstants.SCREENSHOT;

public class CISCustomerLocateSetup extends WebDriverBaseTestPage<WebDriverTestPage> {

    @FindBy(locator = "CustomerLocateSetup")
    private QAFWebElement customerLocateSetup;

    @FindBy(locator = "CustomerLocatebyCID")
    private QAFWebElement customerLocatebyCID;

    @FindBy(locator = "PCUSTCIDFIND")
    private QAFWebElement pcustCIDFind;

    @Override
    protected void openPage(PageLocator pageLocator, Object... objects) {
    //Abstract
    }

    /**
     * This function locates customer with CID
     * @param loginDetails-  login Details
     * @param letterDetails- Customer Letter Details
     * @throws IOException
     */
    public void cisCustomerLocateByCID(Map<String, String> loginDetails, Map<String, String> letterDetails) throws IOException {
        try {
            customerLocateSetup.click();

            Select select = new Select(customerLocateSetup);
            if (loginDetails.get("Language").equals("English")) {
                select.selectByVisibleText("CID");
                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"**********Pass Statement*********  Customer Type CID value is selected from dropdown");
            } else if (loginDetails.get("Language").equals("français")) {
                select.selectByVisibleText("IDC");
                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"**********Pass Statement*********  Customer Type CID value is selected from dropdown");
            }
            customerLocatebyCID.sendKeys(letterDetails.get("CID").trim());
            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"**********Pass Statement*********  Customer CID search is displayed and entered :- " + letterDetails.get("CID").trim());
            pcustCIDFind.click();
            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"**********Pass Statement*********  Find button is clicked");
        } catch (NoSuchElementException noSuchElement) {

            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Locate type/ Customer CID Field/ Find button elements are not found" + noSuchElement.getMessage());
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Currently it is in " + driver.getTitle() + " page");
        }


    }

}

