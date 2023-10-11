package com.quantum.cdic.pages;
/**
 * Created by Srinu Kusumanchi(s3810121) for CDIC January Release.
 */

import com.codoid.products.exception.FilloException;
import com.qmetry.qaf.automation.ui.WebDriverBaseTestPage;
import com.qmetry.qaf.automation.ui.WebDriverTestBase;
import com.qmetry.qaf.automation.ui.annotations.FindBy;
import com.qmetry.qaf.automation.ui.api.PageLocator;
import com.qmetry.qaf.automation.ui.api.WebDriverTestPage;
import com.qmetry.qaf.automation.ui.webdriver.QAFWebElement;
import com.quantum.baseclass.BasePage;
import com.quantum.utility.LoggingUtils;
import com.quantum.utility.ReadFromExcel;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.quantum.constants.CommonConstants.*;
import static com.quantum.steps.CdicStepDefination.*;


public class CISCustomersProductServices extends WebDriverBaseTestPage<WebDriverTestPage> {

    @FindBy(locator = "InformationTAB")
    private QAFWebElement informationTABEnglish;

    @FindBy(locator = "productsAndServicesTAB")
    private QAFWebElement productsAndServicesTAB;

    @FindBy(locator = "InformationTABFrench")
    private QAFWebElement informationTABFrench;

    @FindBy(locator = "IntralinkImage")
    private QAFWebElement intralinkImage;

    private static ReadFromExcel readFromExcel;
    static Map<String, String> signOnDetails;
    static Map<String, String> letterDetails;
    @Override
    protected void openPage(PageLocator locator, Object... args) {
        //Abstract
    }

    public CISCustomersProductServices() {
        signOnDetails = getSignOnDetails();
        letterDetails = getLetterDetails();
    }

    /**
     * This function Clicks on Information Tab
     *
     * @param details - customer details
     */
    public void clickOnInformationTAB(Map<String, String> details) throws IOException {
        if (details.get(LANGUAGE).equals(ENGLISH)) {
            informationTABEnglish.click();
            LoggingUtils.log( (new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Clicked on Information Tab");
        } else if (details.get(LANGUAGE).equals(FRENCH)) {
            informationTABFrench.click();
            LoggingUtils.log( (new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Clicked on Information Tab");
        }

    }

    /**
     * This function Clicks on Products and Services Tab
     *
     * @throws InterruptedException
     */
    public void clickOnproductsAndServicesTAB() throws InterruptedException, IOException {

        driver.manage().timeouts().implicitlyWait(8, TimeUnit.SECONDS);
        productsAndServicesTAB.click();
        Thread.sleep(5000);

        if (new BasePage().isAlertPresent()) {
            new BasePage().acceptAlert();
        }
        LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Clicked on Products and Services Tab");

    }

    /**
     * This function fetches Customer Product Name based on Account Number
     *
     * @param details -  Customer Details
     * @return String - Customer Product Name
     * @throws IOException, InterruptedException
     */
    public String getProductName(Map<String, String> details) throws IOException, InterruptedException {
        readFromExcel = new ReadFromExcel();

        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), PASS_TEXT);
        List noOfProducts = driver.findElements(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr"));
        String accountNo = details.get(ACCOUNT_TEXT).replace(" ", "").trim();
        String prodName = null;

        boolean accntFoundFlag = false;
        for (int i = 2; i <= noOfProducts.size(); i++) {
            if (driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[2]")).getText().replace(" ", "").contains(accountNo)) {

                prodName = driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[1]")).getText();

                Thread.sleep(3000);

                accntFoundFlag = true;
                break;
            }

        }
        return prodName;
    }

    /**
     * This function fetches Customer Product Status based on Account Number
     *
     * @param details-      Customer Details
     * @param testdataPath- Excel Test Datapath
     * @param workSheet-    Excel Sheet Name
     * @param scenarioID-   Excel Unique value(Scenario Id)
     * @return String - Customer Product Status
     * @throws IOException, InterruptedException
     */
    public String getProductStatus(HashMap<String, String> details, String testdataPath, String workSheet, String sheetName, String scenarioID) throws IOException, InterruptedException, FilloException {
        readFromExcel = new ReadFromExcel();

        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), PASS_TEXT);
        List noOfProducts = driver.findElements(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr"));
        String accountNo = details.get(ACCOUNT_TEXT).replace(" ", "").trim();
        boolean accntFoundFlag = false;
        String prodStatus = null;
        for (int i = 2; i <= noOfProducts.size(); i++) {
            if (driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[2]")).getText().replace(" ", "").contains(accountNo)) {
                String prodLink = driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[1]/a")).getText();

                prodStatus = driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[5]")).getText();
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, workSheet, sheetName, scenarioID, "AccountStatus_Intralink", prodStatus.trim());

                Thread.sleep(3000);

                accntFoundFlag = true;
                break;
            }

        }
        return prodStatus;
    }

    /**
     * This function fetches Customer Product Relation Ship based on Account Number
     *
     * @param details-      Customer Details
     * @param testdataPath- Excel Test Datapath
     * @param workSheet-    Excel Sheet Name
     * @param sheetName-    Excel Sheet Name
     * @param scenarioID-   Excel Unique value(Scenario Id)
     * @return String - Customer Product Relation Ship
     * @throws IOException
     */
    public String getProductRelationShip(Map<String, String> details, String testdataPath, String workSheet, String sheetName, String scenarioID) throws IOException, InterruptedException, FilloException {
        readFromExcel = new ReadFromExcel();

        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), PASS_TEXT);
        List NoOfProducts = driver.findElements(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr"));
        String accountNo = details.get(ACCOUNT_TEXT).replace(" ", "").trim();
        boolean accntFoundFlag = false;
        String prodRelationShip = null;
        for (int i = 2; i <= NoOfProducts.size(); i++) {
            if (driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[2]")).getText().replace(" ", "").contains(accountNo)) {
                String prodLink = driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[1]/a")).getText();

                prodRelationShip = driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[8]")).getText();
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, workSheet, sheetName, scenarioID, PRODUCTRELATIONSHIP_TEXT, prodRelationShip.trim());

                Thread.sleep(3000);

                accntFoundFlag = true;
                break;
            }

        }
        return prodRelationShip;
    }

    /**
     * This function gets column Index Number based on it's Column Name
     *
     * @param tableHeaders- Table Headers
     * @param headerName-   Table Header Name
     * @return int - Column Index number
     */
    private int getColumnIndex(List<WebElement> tableHeaders, String headerName) {
        int count = 0;
        for (WebElement header : tableHeaders) {

            if (header.getText().equals(headerName)) {
                count = count + 1;
                break;
            } else {
                count = count + 1;
            }

        }

        return count;
    }

    /**
     * This function clicks on Product based on it's account number
     *
     * @param details-      Customer Details
     * @param testdataPath- Excel Test Datapath
     * @param workSheet-    Excel Sheet Name
     * @param sheetName-    Excel Sheet Name
     * @param scenarioID-   Excel Unique value(Scenario Id)
     * @throws IOException, InterruptedException
     */
    public void clickProductByAccountNumber(Map<String, String> details, String testdataPath, String workSheet, String sheetName, String scenarioID) throws IOException, InterruptedException, FilloException {

        CISAccountProfile cisAccountProfile = new CISAccountProfile();
        readFromExcel = new ReadFromExcel();
        List noOfProducts = null;
        List<WebElement> tableHeaders = null;
        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), PASS_TEXT);
        if (signOnDetails.get(LANGUAGE).equalsIgnoreCase(ENGLISH)) {
            noOfProducts = driver.findElements(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr"));
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), noOfProducts + " is displayed in " + driver.getTitle() + PAGE_TEXT);
            tableHeaders = driver.findElements(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[1]/td"));
        } else if (signOnDetails.get(LANGUAGE).equalsIgnoreCase(FRENCH)) {
            noOfProducts = driver.findElements(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr"));
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), noOfProducts + " is displayed in " + driver.getTitle() + PAGE_TEXT);
            tableHeaders = driver.findElements(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[1]/td"));
        }

        String accountNo = details.get(ACCOUNT_TEXT).replace(" ", "");
        String prodName = null;
        String prodStatus = null;
        String prodRelationShip = null;
        String balance = null;
        String transit = null;
        boolean accntFoundFlag = false;

        try {
            for (int i = 2; i <= noOfProducts.size(); i++) {
                if (driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[2]")).getText().replace(" ", "").contains(accountNo)) {
                    LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account number " + accountNo + " is found in " + driver.getTitle() + PAGE_TEXT);
                    if (signOnDetails.get(LANGUAGE).equalsIgnoreCase(ENGLISH)) {
                        prodName = driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[" + getColumnIndex(tableHeaders, "Products & Services") + "]")).getText();
                        LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Product Name is :-" + prodName);
                        prodStatus = driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[" + getColumnIndex(tableHeaders, "Status") + "]")).getText();
                        LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Product Status is :-" + prodStatus);
                        prodRelationShip = driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[" + getColumnIndex(tableHeaders, "Relationship") + "]")).getText();
                        LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Product RelationShip is :-" + prodRelationShip);
                        balance = driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[" + getColumnIndex(tableHeaders, "Balance") + "]")).getText();
                        LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Product Balance is :-" + balance);
                        transit = driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[" + getColumnIndex(tableHeaders, "BLT") + "]")).getText();
                        LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), CUSTOMERPRODUCT_TEXT + prodName + "(" + accountNo + ") is created in transit:-" + transit);
                        driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[1]/a")).click();
                        if (new BasePage().isAlertPresent()) {
                            new BasePage().acceptAlert();
                            Thread.sleep(5000);
                            if (new BasePage().isAlertPresent()) {
                                new BasePage().acceptAlert();
                            }
                        }
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  Clicked on Customer Product " + prodName + "(" + accountNo + ")");
                    } else if (signOnDetails.get(LANGUAGE).equalsIgnoreCase(FRENCH)) {
                        prodName = driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[" + getColumnIndex(tableHeaders, "Produits et services") + "]")).getText();
                        LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Product Name is :-" + prodName);
                        prodStatus = driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[" + getColumnIndex(tableHeaders, "Situation") + "]")).getText();
                        LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Product Status is :-" + prodStatus);
                        prodRelationShip = driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[" + getColumnIndex(tableHeaders, "Relation") + "]")).getText();
                        LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Product RelationShip is :-" + prodRelationShip);
                        balance = driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[" + getColumnIndex(tableHeaders, "Solde") + "]")).getText();
                        LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Product Balance is :-" + balance);
                        transit = driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[" + getColumnIndex(tableHeaders, "CES") + "]")).getText();
                        LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), CUSTOMERPRODUCT_TEXT + prodName + "(" + accountNo + ") is created in transit:-" + transit);
                        driver.findElement(By.xpath(".//*[text()='Products & Services']/ancestor::tbody/tr[" + i + "]/td[1]/a")).click();
                        Thread.sleep(5000);
                        if (new BasePage().isAlertPresent()) {
                            new BasePage().acceptAlert();
                        }
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  Clicked on Customer Product " + prodName + "(" + accountNo + ")");
                    }
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, workSheet, sheetName, scenarioID, "ProductName", prodName.trim());
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, workSheet, sheetName, scenarioID, "AccountStatus_Intralink", prodStatus.trim());
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, workSheet, sheetName, scenarioID, PRODUCTRELATIONSHIP_TEXT, prodRelationShip.trim());
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, workSheet, sheetName, scenarioID, "Balance", balance.trim());
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, workSheet, sheetName, scenarioID, "Transit", transit.trim());
                    if ((sheetName.equals(ONE_E_TEXT) || sheetName.equals(TWO_E_TEXT) || sheetName.equals(THREE_E_TEXT) || sheetName.equals(TWO_E_PNTA_TEXT) || sheetName.equals(THREE_E_PNTA_TEXT)) && letterDetails.get(ACCOUNT_TEXT).trim().length() > 11) {
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, workSheet, sheetName, scenarioID, DEALER_TEXT, "BNS");
                    } else if (sheetName.equals(ONE_F_TEXT) || sheetName.equals(TWO_F_TEXT) || sheetName.equals(THREE_F_TEXT) || sheetName.equals(TWO_F_PNTA_TEXT) || sheetName.equals(THREE_F_PNTA_TEXT) && letterDetails.get(ACCOUNT_TEXT).trim().length() > 11) {
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, workSheet, sheetName, scenarioID, DEALER_TEXT, "BNE");
                    }


                    Thread.sleep(3000);
                    accntFoundFlag = true;
                    break;
                }

            }
        } catch (NoSuchElementException noSuchElement) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Customer Products and Services Web Table elements are not found " + noSuchElement.getMessage());
            LoggingUtils.log( (new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),CURRENTLY_IT_IS_IN_TEXT + driver.getTitle() + PAGE_TEXT);
        } catch (NullPointerException nullPointerException) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Customer is not having products in Customer Products page" + nullPointerException.getMessage());
        }


        if (accntFoundFlag = false)
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account number " + accountNo + " is not found in " + driver.getTitle() + PAGE_TEXT);

        else if (accntFoundFlag = true) {

            driver.manage().timeouts().implicitlyWait(8, TimeUnit.SECONDS);

            try {
                if (driver.getPageSource().contains("Account Profile") || driver.getPageSource().contains("Profil du compte")) {
                    LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Navigated to Account Profile Screen");
                    LoggingUtils.log( (new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Account Profile screen is displayed for the Product " + prodName + " (" + accountNo + ")");

                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  Account Profile screen is displayed for the Product " + prodName + " (" + accountNo + ")");
                    if ((sheetName.equals(ONE_E_TEXT) && letterDetails.get(ACCOUNT_TEXT).trim().length() > 11) || (sheetName.equals(ONE_F_TEXT) && letterDetails.get(ACCOUNT_TEXT).length() > 11)) {
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, workSheet, sheetName, scenarioID, "AddressLine1_IntraLink", cisAccountProfile.getPrdAddressLineOne());
                        LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Profile screen Address Line 1:- " + cisAccountProfile.getPrdAddressLineOne());
                        if (driver.getPageSource().contains("Address Line 2:") || driver.getPageSource().contains("Adresse ligne 2:")) {
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, workSheet, sheetName, scenarioID, "AddressLine2_IntraLink", cisAccountProfile.getPrdAddressLineTwo());
                            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Profile screen Address Line 2:- " + cisAccountProfile.getPrdAddressLineTwo());
                        }
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, workSheet, sheetName, scenarioID, "City_IntraLink", cisAccountProfile.getPrdCityAddressLineThree());
                        LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Profile screen City:- " + cisAccountProfile.getPrdCityAddressLineThree());
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, workSheet, sheetName, scenarioID, "State", cisAccountProfile.getPrdProvinceState());
                        LoggingUtils.log( (new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Account Profile screen State:- " + cisAccountProfile.getPrdProvinceState());
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, workSheet, sheetName, scenarioID, "PostalCode_IntraLink", cisAccountProfile.getPrdPostalZipCode());
                        LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Profile screen Postal Code:- " + cisAccountProfile.getPrdPostalZipCode());
                    }

                    String accountTitle1 = new CISAccountProfile().getPrdAccountTitleOne();
                    LoggingUtils.log( (new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Account Profile screen Account Title 1:- " + accountTitle1);
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, workSheet, sheetName, scenarioID, "Account_TitleOne", accountTitle1);
                    String accountTitle2 = new CISAccountProfile().getPrdAccountTitleTwo();
                    LoggingUtils.log( (new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Account Profile screen Account Title 2:- " + accountTitle2);
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, workSheet, sheetName, scenarioID, ACCOUNTTITLETWO_TEXT, accountTitle2);
                }
            } catch (NoSuchElementException noSuchElement) {
                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Profile Screen elements are not found " + noSuchElement.getMessage());
                LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), CURRENTLY_IT_IS_IN_TEXT + driver.getTitle() + PAGE_TEXT);
            }
            try {
                if (driver.getPageSource().contains("Account Overview") || driver.getTitle().contains("Aperçu du compte")) {
                    LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Navigated to Account Overview Screen");

                    List<String> gicCertificateNumbers = new LinkedList<>();
                    List<String> issuer = new LinkedList<>();
                    if (details.get("ProductName").equals("Momentum PLUS Savings")) {
                        details = readFromExcel.readFromExcel(testdataPath, CDIC_TESTDATA_TEXT, sheetName, getScenarioId());
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioID, DEALER_TEXT, "BNS");
                        LoggingUtils.log( (new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Dealar value is :- " + "BNS" + "for Customer Product " + prodName + "(" + accountNo + ")  ");
                    } else {
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioID, DEALER_TEXT, prodName.split("- ")[(prodName.split("- ").length) - 1].trim());
                        LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Dealar value is :- " + prodName.split("- ")[1].trim() + "for Customer Product " + prodName + "(" + accountNo + ")  ");
                    }

                    if (driver.getPageSource().contains("Asset Class View") || driver.getPageSource().contains("Catégories d'actif")) {
                        driver.manage().timeouts().implicitlyWait(8, TimeUnit.SECONDS);

                        for (int i = 0; i < driver.findElements(By.xpath(".//span[contains(text(),\"$\")]")).size(); i++) {
                            int gicCount = driver.findElements(By.xpath(".//span[contains(text(),\"$\")]")).size();
                            WebDriverWait wait = new WebDriverWait(driver, 30);
                            wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(".//span[text()='Account Overview' or text()='Aperçu du compte']"))));
                            driver.findElements(By.xpath(".//span[contains(text(),\"$\")]")).get(i).click();
                            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), CUSTOMERPRODUCT_TEXT + prodName + "(" + accountNo + ") contains " + gicCount + " GIC's");
                            Thread.sleep(6000);
                            if (driver.getPageSource().contains("GIC Details") || driver.getPageSource().contains("Détails CPG")) {
                                if (sheetName.equalsIgnoreCase(ONE_F_TEXT) || sheetName.equalsIgnoreCase(TWO_F_TEXT) || sheetName.equalsIgnoreCase(THREE_F_TEXT) || sheetName.equalsIgnoreCase(TWO_F_PNTA_TEXT) || sheetName.equalsIgnoreCase(THREE_F_PNTA_TEXT)) {
                                    gicCertificateNumbers.add("CPG#" + StringUtils.leftPad(new CISGicDetails().getCertificateNumber(), 15, "0"));
                                    LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "GIC Certificate number-French " + new CISGicDetails().getCertificateNumber() + " is linked to Customer Product " + prodName + "(" + accountNo + ") ");
                                } else if (sheetName.equalsIgnoreCase(ONE_E_TEXT) || sheetName.equalsIgnoreCase(TWO_E_TEXT) || sheetName.equalsIgnoreCase(THREE_E_TEXT) || sheetName.equalsIgnoreCase(TWO_E_PNTA_TEXT) || sheetName.equalsIgnoreCase(THREE_E_PNTA_TEXT)) {
                                    gicCertificateNumbers.add("GIC#" + StringUtils.leftPad(new CISGicDetails().getCertificateNumber(), 15, "0"));
                                    LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "GIC Certificate number-English " + new CISGicDetails().getCertificateNumber() + " is linked to Customer Product " + prodName + "(" + accountNo + ") ");
                                }

                                if (sheetName.equalsIgnoreCase(ONE_F_TEXT) || sheetName.equalsIgnoreCase(TWO_F_TEXT) || sheetName.equalsIgnoreCase(THREE_F_TEXT) || sheetName.equalsIgnoreCase(TWO_F_PNTA_TEXT) || sheetName.equalsIgnoreCase(THREE_F_PNTA_TEXT)) {
                                    int issuerLength = new CISGicDetails().getIssuer().split(" ").length;
                                    String issuerNameFrench = new CISGicDetails().getIssuer().split(" ")[issuerLength - 1].equalsIgnoreCase("Scotia") ? "BNE" : new CISGicDetails().getIssuer().split(" ")[issuerLength - 1];
                                    issuer.add(issuerNameFrench);
                                } else if (sheetName.equalsIgnoreCase(ONE_E_TEXT) || sheetName.equalsIgnoreCase(TWO_E_TEXT) || sheetName.equalsIgnoreCase(THREE_E_TEXT) || sheetName.equalsIgnoreCase(TWO_E_PNTA_TEXT) || sheetName.equalsIgnoreCase(THREE_E_PNTA_TEXT)) {
                                    issuer.add(new CISGicDetails().getIssuer().split(" ")[0]);
                                }

                                LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Issuer for GIC Certificate number " + new CISGicDetails().getCertificateNumber() + " is " + new CISGicDetails().getIssuer().split(" ")[0]);
                                driver.navigate().back();
                                Thread.sleep(3000);
                                if (new BasePage().isAlertPresent()) {
                                    new BasePage().acceptAlert();
                                }

                            } else {
                                driver.navigate().back();
                                Thread.sleep(3000);
                                if (new BasePage().isAlertPresent()) {
                                    new BasePage().acceptAlert();
                                }
                            }

                        }
                        String gicNumbers = gicCertificateNumbers.stream().collect(Collectors.joining("|"));
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioID, "GIC_IntraLink", gicNumbers);
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioID, "Issuer_Intralink", issuer.stream().collect(Collectors.joining("|")));


                    }

                }
            } catch (NoSuchElementException noSuchElement) {
                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Overview Screen elements are not found " + noSuchElement.getMessage());
                LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), CURRENTLY_IT_IS_IN_TEXT + driver.getTitle() + PAGE_TEXT);
            } catch (UnhandledAlertException unhandledAlert) {
                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Unable to handle alert in " + driver.getTitle() + PAGE_TEXT);
            }


        }
    }

    /**
     * This function clicks on Intralink Image
     */
    public void clickOnIntralinkImage() {
        intralinkImage.click();
    }

    /**
     * This function clicks Sign Off link
     *
     * @param details- customer details
     * @throws InterruptedException
     */
    public void clickOnSignOff(Map<String, String> details) throws InterruptedException {
        Thread.sleep(6000);
        clickOnIntralinkImage();
        Thread.sleep(5000);
        if ((details.get(LANGUAGE).equals(ENGLISH)))
            new WebDriverTestBase().getDriver().findElement(By.linkText("Sign-off")).click();

        if ((details.get(LANGUAGE).equals(FRENCH)))
            new WebDriverTestBase().getDriver().findElement(By.linkText("Sortie")).click();

    }

    /**
     * This function Verifies whether customer account is Trust or not
     *
//     * @param details-      Customer Details
     * @param testdataPath- Excel Test Datapath
     * @param workSheet-    Excel Sheet Name
     * @param sheetName-    Excel Sheet Name
     * @param scenarioID-   Excel Unique value(Scenario Id)
     * @throws IOException
     */
    public void verifyTrustAccountsOrNot(String testdataPath, String workSheet, String sheetName, String scenarioID) throws IOException, FilloException {
        readFromExcel = new ReadFromExcel();
        Map<String, String> details = readFromExcel.readFromExcel(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioID);
        String accountTitle1 = details.get("Account_TitleOne");
        String accountTitle2 = details.get(ACCOUNTTITLETWO_TEXT);
        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, workSheet, sheetName, scenarioID, ACCOUNTTITLETWO_TEXT, accountTitle2);
        Boolean trustAccount = false;
        if (details.get(PRODUCTRELATIONSHIP_TEXT).equalsIgnoreCase("Sole") || details.get(PRODUCTRELATIONSHIP_TEXT).equalsIgnoreCase("Primary Joint") || details.get(PRODUCTRELATIONSHIP_TEXT).equalsIgnoreCase("Joint")
                || details.get(PRODUCTRELATIONSHIP_TEXT).equalsIgnoreCase("1 titul.") || details.get(PRODUCTRELATIONSHIP_TEXT).equalsIgnoreCase("Joint primaire") || details.get(PRODUCTRELATIONSHIP_TEXT).equalsIgnoreCase("Mixte") || details.get(PRODUCTRELATIONSHIP_TEXT).equalsIgnoreCase("Titul. pr. cpte joint ")) {
            // Trust Account Strings (TRUST, FIDUCIE, ITF, EFP, ITFMB, EFPBM, TR, FID, TRST, TRUSTEE, FIDUCIAIRE, TRUSTEES, FIDUCIAIRES)
            String trustStrings = "TRUST,FIDUCIE,ITF,EFP,ITFMB,EFPBM,IN TR,FID,TRST,TRUSTEE,FIDUCIAIRE,TRUSTEES,FIDUCIAIRES";
            String trustStringArray[] = trustStrings.split(",");

            if (Arrays.stream(trustStringArray).anyMatch(x -> accountTitle1.contains(x)) || Arrays.stream(trustStringArray).anyMatch(x -> accountTitle2.contains(x))) {
                trustAccount = true;
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, workSheet, sheetName, scenarioID, "Trust_Accounts", "YES");
                LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Customer Account is trust account where Account Title 1:- " + accountTitle1 + " contains string " + Arrays.stream(trustStringArray).filter(x -> accountTitle1.contains(x)).collect(Collectors.toList()) +
                        "and Account Title 2:-" + accountTitle2 + " contains string " + Arrays.stream(trustStringArray).filter(x -> accountTitle2.contains(x)).collect(Collectors.toList()));
            } else {
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, workSheet, sheetName, scenarioID, "Trust_Accounts", "NO");
                LoggingUtils.log( (new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Customer Account is not a trust account where Account Title 1:- " + accountTitle1 + " and Account Title 2:- " + accountTitle1 + " does not contains string among" + trustStrings);

            }

        }
        if (details.get(PRODUCTRELATIONSHIP_TEXT).equalsIgnoreCase("Pers ITF PERS or NP ITF NP")
                || details.get(PRODUCTRELATIONSHIP_TEXT).equalsIgnoreCase("In Trust Primary Joint")
                || details.get(PRODUCTRELATIONSHIP_TEXT).equalsIgnoreCase("Trust Secondary Joint")
                || details.get(PRODUCTRELATIONSHIP_TEXT).equalsIgnoreCase("Non-Pers ITF Pers")
                || details.get(PRODUCTRELATIONSHIP_TEXT).equalsIgnoreCase("Part EFP part ou PM EFP PM")
                || details.get(PRODUCTRELATIONSHIP_TEXT).equalsIgnoreCase("Cpte joint princ. fiducie")
                || details.get(PRODUCTRELATIONSHIP_TEXT).equalsIgnoreCase("Titul Sec-cpte JT Fid")
                || details.get(PRODUCTRELATIONSHIP_TEXT).equalsIgnoreCase("P. mor. EFP part")) {
            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, workSheet, sheetName, scenarioID, "Trust_Accounts", "YES");
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Customer Account is trust account where Product relation ship is " + details.get(PRODUCTRELATIONSHIP_TEXT));
        }

        intralinkImage.click();
        LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Clicked on Intralink Image to navigate to IntraLink Portal Page");
    }
}
