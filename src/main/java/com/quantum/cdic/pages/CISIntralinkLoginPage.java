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
import com.quantum.utils.LogHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.quantum.constants.CommonConstants.CURRENTLY_IT_IS_IN_TEXT;
import static com.quantum.constants.CommonConstants.PAGE_TEXT;
import static com.quantum.constants.CommonConstants.SCREENSHOT;

public class CISIntralinkLoginPage extends WebDriverBaseTestPage<WebDriverTestPage> {

    @FindBy(locator = "ScotiaID")
    private QAFWebElement scotiaID;

    @FindBy(locator = "Password")
    private QAFWebElement password;

    @FindBy(locator = "LogOn")
    private QAFWebElement logOn;


    @Override
    protected void openPage(PageLocator locator, Object... args) {
        //Abstract
    }

    /**
     * This function enters Officer Id
     *
     * @param details- customer details
     * @throws IOException
     */

    public void setScotiaID(Map<String, String> details) throws IOException {
        driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);

        if (driver.getTitle().contains("Select Branch") || driver.getTitle().contains("Sélectionner succursale")) {
            driver.findElement(By.xpath("//*[@id=\"Table1\"]/tbody/tr[1]/td[1]/a")).click();
            driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            LogHelper.logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
        if (driver.getTitle().contains("IntraLink Portal")) {
            driver.findElement(By.linkText("Sign-off")).click();
            driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        }
        if (driver.getTitle().contains("Portail IntraCom")) {
            driver.findElement(By.linkText("Sortie")).click();
            driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        }
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            LogHelper.logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        try {
            scotiaID.sendKeys(details.get("UserName"));
            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"**********Pass Statement*********  User Id entered:-" + details.get("UserName"));
        } catch (NoSuchElementException noSuchElement) {

            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"User Id element is not found:-" + noSuchElement.getMessage());
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),CURRENTLY_IT_IS_IN_TEXT+ driver.getTitle() + PAGE_TEXT);
        }

    }

    /**
     * This function enters Officer Id Password
     *
     * @param details- customer details
     * @throws IOException
     */
    public void setPassword(Map<String, String> details) throws IOException {
        try {
            password.sendKeys(details.get("Password"));
            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"**********Pass Statement*********  Password entered:-" + details.get("Password"));
        } catch (NoSuchElementException noSuchElement) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Password element is not found:-" + noSuchElement.getMessage());
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),CURRENTLY_IT_IS_IN_TEXT+ driver.getTitle() + PAGE_TEXT);
        }

    }

    /**
     * This function clicks on SignOn Button
     *
     * @throws IOException
     */
    public void clickOnLogin() throws IOException {
        try {
            logOn.click();
            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"**********Pass Statement*********  Log On button clicked");
        } catch (NoSuchElementException noSuchElement) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Log On button element is not found:-" + noSuchElement.getMessage());
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),CURRENTLY_IT_IS_IN_TEXT+ driver.getTitle() + PAGE_TEXT);
        }

    }


}

