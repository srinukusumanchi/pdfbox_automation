package com.quantum.cdic.pages;
/**
 * Created by Srinu Kusumanchi(s3810121) for CDIC January Release.
 */

import com.qmetry.qaf.automation.ui.WebDriverBaseTestPage;
import com.qmetry.qaf.automation.ui.WebDriverTestBase;
import com.qmetry.qaf.automation.ui.annotations.FindBy;
import com.qmetry.qaf.automation.ui.api.PageLocator;
import com.qmetry.qaf.automation.ui.api.WebDriverTestPage;
import com.qmetry.qaf.automation.ui.webdriver.QAFWebElement;
import com.quantum.baseclass.BasePage;
import com.quantum.java.pages.ExtentReportHelper;
import com.quantum.utility.LoggingUtils;
import com.quantum.utils.LogHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static com.quantum.constants.CommonConstants.*;

public class CISIntralinkPortal extends WebDriverBaseTestPage<WebDriverTestPage> {

    @FindBy(locator = LANGUAGE)
    private QAFWebElement language;

    @FindBy(locator = "InformationSupport")
    private QAFWebElement informationSupport;

    @FindBy(locator = "Customersalesservice")
    private QAFWebElement customersalesservice;

    @FindBy(locator = "CustomersalesserviceFrench")
    private QAFWebElement customersalesserviceFrench;

    @Override
    protected void openPage(PageLocator locator, Object... args) {
        //Abstract
    }

    /**
     * This function clicks on Customer Sales and Services Link
     *
     * @throws IOException
     */
    public void clickCustomerSalesServices() throws IOException,InterruptedException {
//        CISCustomersProductServices product=new CISCustomersProductServices();
//        Thread.sleep(6000);
//        product.clickOnIntralinkImage();
//        Thread.sleep(5000);
        try {

            driver.findElement(By.xpath(".//a[text()=\"Customer Sales & Service\" or text()=\"Vente et service à la clientèle\"]")).click();
        } catch (NoSuchElementException noSuchElement) {

            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Customer Sales and Services element is not found:-" + noSuchElement.getMessage());
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Currently it is in " + driver.getTitle() + " page");
        }


    }

    /**
     * This function click on Information Support Tab
     *
     * @param details - Customer Details
     * @throws InterruptedException
     */
    public void clickInformationSupport(Map<String, String> details) throws InterruptedException {

        Thread.sleep(6000);
        if (details.get(LANGUAGE).equals(ENGLISH)) {
            JavascriptExecutor js = driver;
            js.executeScript("arguments[0].click();", driver.findElement(By.xpath(".//span[text()='Information & Support']")));
            LogHelper.logger.info("");
        }
        if (details.get(LANGUAGE).equals(FRENCH)) {
            JavascriptExecutor js = driver;
            js.executeScript("arguments[0].click();", new WebDriverTestBase().getDriver().findElement(By.linkText("Information et soutien")));
            Thread.sleep(4000);

        }
    }

    /**
     * This function click on Swith Language link to migrate from English to French and Vice Versa
     *
     * @param details- Customer Details
     * @throws IOException
     */
    public void switchLanguage(Map<String, String> details) throws IOException {

        try {
            if (language.getText().equalsIgnoreCase(details.get(LANGUAGE))) {
                language.click();
                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),language.getText() + "is clicked");
            } else {
                LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Intralink is displayed in " + language + " Language");
            }
        } catch (NoSuchElementException noSuchElement) {

            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Language element is not found:-" + noSuchElement.getMessage());
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Currently it is in " + driver.getTitle() + " page");
        }

    }

}



