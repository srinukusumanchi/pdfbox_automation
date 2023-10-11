package com.quantum.cdic.pages;
/*
  Created by Srinu Kusumanchi(s3810121) for CDIC January Release.
 */

import com.qmetry.qaf.automation.ui.WebDriverBaseTestPage;
import com.qmetry.qaf.automation.ui.api.PageLocator;
import com.qmetry.qaf.automation.ui.api.WebDriverTestPage;
import com.qmetry.qaf.automation.ui.webdriver.QAFExtendedWebDriver;
import com.quantum.baseclass.BasePage;
import com.quantum.java.pages.ExtentReportHelper;
import com.quantum.utility.LoggingUtils;
import org.openqa.selenium.By;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.quantum.constants.CommonConstants.SCREENSHOT;
import static com.quantum.constants.CommonConstants.SEARCHBRANCH_TEXT;

public class CISSelectBranch extends WebDriverBaseTestPage<WebDriverTestPage> {

    @Override
    protected void openPage(PageLocator locator, Object... args) {
        //Abstract
    }

    /**
     * This function clicks on required transit
     *
     * @param details - customer details
     * @throws IOException
     */
    public void cisSelectBranch(QAFExtendedWebDriver driver, Map<String, String> details) throws IOException {
        driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
        try {
            if (driver.findElement(By.partialLinkText(details.get(SEARCHBRANCH_TEXT))).isDisplayed()) {

                driver.findElement(By.partialLinkText(details.get(SEARCHBRANCH_TEXT))).click();
                LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Navigated to " + driver.getTitle() + "page");
                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"**********Pass Statement*********  Clicked on " + details.get(SEARCHBRANCH_TEXT) + " transit");

            }

        } catch (NoSuchElementException noSuchElement) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),details.get(SEARCHBRANCH_TEXT) + " transit element is not found");
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Currently it is in " + driver.getTitle() + " page");
        }

    }
}




