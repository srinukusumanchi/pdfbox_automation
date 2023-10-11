package com.quantum.cdic.pages;
/**
 * Created by Srinu Kusumanchi(s3810121) for CDIC January Release.
 */

import com.codoid.products.exception.FilloException;
import com.qmetry.qaf.automation.ui.WebDriverBaseTestPage;
import com.qmetry.qaf.automation.ui.annotations.FindBy;
import com.qmetry.qaf.automation.ui.api.PageLocator;
import com.qmetry.qaf.automation.ui.api.WebDriverTestPage;
import com.qmetry.qaf.automation.ui.webdriver.QAFWebElement;
import com.quantum.baseclass.BasePage;
import com.quantum.java.pages.ExtentReportHelper;
import com.quantum.utility.LoggingUtils;
import com.quantum.utility.ReadFromExcel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import static com.quantum.constants.CommonConstants.CDIC_TESTDATA_TEXT;
import static com.quantum.constants.CommonConstants.SCREENSHOT;
import static com.quantum.constants.CommonConstants.VALUE_TEXT;

public class CISCustomersInformation extends WebDriverBaseTestPage<WebDriverTestPage> {


    @FindBy(locator = "InformationTAB")
    private QAFWebElement informationTAB;

    @FindBy(locator = "standardCanada_US")
    private QAFWebElement standardCanadaUS;

    @FindBy(locator = "nonStandard_Foreign")
    private QAFWebElement nonStandardForiegn;

    @FindBy(locator = "InformationTABFrench")
    private QAFWebElement informationTABFrench;

    @FindBy(locator = "HomeAddressLineOne")
    private QAFWebElement homeAddressLineOne;

    @FindBy(locator = "HomeAddressLineTwo")
    private QAFWebElement homeAddressLineTwo;

    @FindBy(locator = "homeAddressCityStandard")
    private QAFWebElement homeAddressCityStandard;

    @FindBy(locator = "homeAddressPostalZipCodeStandard_NonStandard")
    private QAFWebElement homeAddressPostalZipCodeStandardNonStandard;

    @FindBy(locator = "homeAddressState")
    private QAFWebElement homeAddressState;

    @FindBy(locator = "homeAddressCityNonStandard")
    private QAFWebElement homeAddressCityNonStandard;


    @Override
    protected void openPage(PageLocator locator, Object... args) {
        //Abstract
    }

    /**
     * This function gets the customer City
     *
     * @return String - returns the customer City
     */
    private String getHomeAddressCityStandard() {
        return homeAddressCityStandard.getAttribute(VALUE_TEXT).trim();
    }

    /**
     * This function gets the customer Postal Code
     *
     * @return String - returns the customer Postal Code
     */
    private String getHomeAddressPostalZipCodeStandardNonStandard() {
        return homeAddressPostalZipCodeStandardNonStandard.getAttribute(VALUE_TEXT).trim();
    }

    /**
     * This function gets the customer State
     *
     * @return String - returns the customer State
     */
    private String getHomeAddressState() {
        return homeAddressState.getAttribute(VALUE_TEXT).trim();
    }

    /**
     * This function gets the customer Address is Standard or Non Standard
     *
     * @return String - returns the Address is Standard or Non Standard
     */
    private String getHomeAddressCityNonStandard() {
        return homeAddressCityNonStandard.getAttribute(VALUE_TEXT).trim();
    }

    /**
     * This function gets the customer Home Address Line 1
     *
     * @return String - returns the customer Home Address Line 1
     */
    private String getHomeAddressLineOne() {
        return homeAddressLineOne.getText().trim();
    }

    /**
     * This function gets the customer Home Address Line 2
     *
     * @return String - returns the customer Home Address Line 2
     */
    private String getHomeAddressLineTwo() {
        return homeAddressLineTwo.getText().trim();
    }

    /**
     * This function clicks on Information Link
     *
     * @parameters HashMap - login Details
     */
    public void clickOnInformationtLink(Map<String, String> details) {
        if ((details.get("Language").equals("English"))) {
            informationTAB.click();
        } else if ((details.get("Language").equals("French"))) {
            informationTABFrench.click();
        }
    }

    /**
     * This function clicks on Information Link
     *
     * @param readFromExcel - Excel Data Object,
     * @param testDataPath- Test Data Path,
     * @param sheetName-    Excel Sheet Name
     * @param scenarioID - Excel Unique Id(Scenario Id)
     */
    public void getHomeAddress(ReadFromExcel readFromExcel, String testDataPath, String sheetName, String scenarioID) throws IOException,FilloException {
        try {
            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testDataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioID, "AddressLine1_IntraLink", getHomeAddressLineOne());
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Customer Information Screen Address Line 1 " + getHomeAddressLineOne());
            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testDataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioID, "AddressLine2_IntraLink", getHomeAddressLineTwo());
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Customer Information Screen Address Line 2 " + getHomeAddressLineTwo());
            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testDataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioID, "PostalCode_IntraLink", getHomeAddressPostalZipCodeStandardNonStandard());
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Customer Information Screen Postal Code " + getHomeAddressPostalZipCodeStandardNonStandard());
            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testDataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioID, "State", getHomeAddressState());
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Customer Information Screen State " + getHomeAddressState());
            if (standardCanadaUS.isSelected()) {
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testDataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioID, "City_IntraLink", getHomeAddressCityStandard());
                LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Standard Canada/US is selected for Home Address in Customer Inforamtion Screen");
            } else if (nonStandardForiegn.isSelected()) {
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testDataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioID, "City_IntraLink", getHomeAddressCityNonStandard());
                LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Non Standard/Foreign is selected for Home Address in Customer Information Screen");
            }
        } catch (NoSuchElementException noSuchElement) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Customer Information Screen elements are not found " + noSuchElement.getMessage());
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Currently it is in " + driver.getTitle() + " page");
        }
    }


}
