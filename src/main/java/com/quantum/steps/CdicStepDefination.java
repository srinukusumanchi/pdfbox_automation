package com.quantum.steps;

import com.codoid.products.exception.FilloException;
import com.qmetry.qaf.automation.core.ConfigurationManager;
import com.qmetry.qaf.automation.step.QAFTestStepProvider;
import com.qmetry.qaf.automation.ui.WebDriverTestBase;
import com.qmetry.qaf.automation.ui.webdriver.QAFExtendedWebDriver;
import com.quantum.baseclass.BasePage;
import com.quantum.cdic.pages.*;
import com.quantum.utility.LoggingUtils;
import com.quantum.utility.ReadFromExcel;
import com.quantum.utility.ReadTextFile;
import com.quantum.utils.LogHelper;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.openqa.selenium.By;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.quantum.constants.CommonConstants.CDIC_TESTDATA_TEXT;
import static com.quantum.constants.CommonConstants.SCREENSHOT;


@QAFTestStepProvider
public class CdicStepDefination extends BasePage {
    private static QAFExtendedWebDriver webDriver;


    static String scenarioId;

    static ReadFromExcel readFromExcel;
    static Map<String, String> details;
    static Map<String, String> signOnDetails;
    static Map<String, String> letterDetails;
    static Map<String, String> pdfDetails;


    static String sheetName;


    static String testdataPath;
    private static String extractFile;
    private String testdatasheetpath;
    static List<Map<String, String>> extractAccounts = null;
    static PDDocument pdfDocument = null;
    private static int loginCount = 1;
    private CISIntralinkLoginPage loginPage = new CISIntralinkLoginPage();
    private CISSelectBranch selectBranch = new CISSelectBranch();
    private CISIntralinkPortal intralinkPortal = new CISIntralinkPortal();
    private CISCustomerLocateSetup customerLocateSetup = new CISCustomerLocateSetup();
    private CISCustomersProductServices customersProductServices = new CISCustomersProductServices();
    private CISCustomersInformation cisCustomersInformation = new CISCustomersInformation();

    public CdicStepDefination() {
        scenarioId = getScenarioId();
        details = getDetails();
        signOnDetails = getSignOnDetails();
        letterDetails = getLetterDetails();
        pdfDetails = getPdfDetails();
        sheetName = getSheetName();
        testdatasheetpath = getTestdataPath();
        extractAccounts = getExtractAccounts();
        pdfDocument = getPdfDocument();
    }

    public static String getScenarioId() {
        return scenarioId;
    }

    public static void setScenarioId(String scenarioId) {
        CdicStepDefination.scenarioId = scenarioId;
    }

    public static Map<String, String> getDetails() {
        return details;
    }

    public static void setDetails(Map<String, String> details) {
        CdicStepDefination.details = details;
    }

    public static Map<String, String> getSignOnDetails() {
        return signOnDetails;
    }

    public static void setSignOnDetails(Map<String, String> signOnDetails) {
        CdicStepDefination.signOnDetails = signOnDetails;
    }

    public static Map<String, String> getLetterDetails() {
        return letterDetails;
    }

    public static void setLetterDetails(Map<String, String> letterDetails) {
        CdicStepDefination.letterDetails = letterDetails;
    }

    public static Map<String, String> getPdfDetails() {
        return pdfDetails;
    }

    public static void setPdfDetails(Map<String, String> pdfDetails) {
        CdicStepDefination.pdfDetails = pdfDetails;
    }

    public static String getSheetName() {
        return sheetName;
    }

    public static void setSheetName(String sheetName) {
        CdicStepDefination.sheetName = sheetName;
    }

    public static String getTestdataPath() {
        return testdataPath;
    }

    public static void setTestdataPath(String testdataPath) {
        CdicStepDefination.testdataPath = testdataPath;
    }

    public static List<Map<String, String>> getExtractAccounts() {
        return extractAccounts;
    }

    public static void setExtractAccounts(List<Map<String, String>> extractAccounts) {
        CdicStepDefination.extractAccounts = extractAccounts;
    }

    public static PDDocument getPdfDocument() {
        return pdfDocument;
    }

    public static void setPdfDocument(PDDocument pdfDocument) {
        CdicStepDefination.pdfDocument = pdfDocument;
    }

 /*   @Override
    protected void openPage(PageLocator pageLocator, Object... objects) {
//Abstract
    }*/


    /**
     * This function reads excel data
     *
     * @throws Throwable
     * @Parameters String - Test Data sheet, String Scenario Id
     */
    @Given("^the \"([^\"]*)\" and \"([^\"]*)\"$")
    public void theAnd(String rawDataSheet, String scenarioIdTestData) throws IOException {
        String projectName = System.getProperty("user.dir");
        setTestdataPath(projectName + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "data");
        testdataPath = getTestdataPath();
        testdatasheetpath = testdataPath + File.separator + ConfigurationManager.getBundle().getString("environment");
        LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Execution Environment :- " + ConfigurationManager.getBundle().getString("environment"));
        testdataPath = testdatasheetpath;
        setSheetName(rawDataSheet);
        sheetName = getSheetName();
        setScenarioId(scenarioIdTestData);
        LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Executing scenarioId :- " + scenarioId);
        readFromExcel = new ReadFromExcel();
        letterDetails = readFromExcel.readFromExcel(testdataPath, CDIC_TESTDATA_TEXT, rawDataSheet, scenarioId);
        setLetterDetails(letterDetails);
        ReadFromExcel loginDetails = new ReadFromExcel();
        signOnDetails = loginDetails.readLoginDetailsFromExcel(testdataPath, CDIC_TESTDATA_TEXT, "LoginDetails", letterDetails.get("LoginDetail"));
        setSignOnDetails(signOnDetails);
        extractFile = getSignOnDetails().get(letterDetails.get("File").trim());
        LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Verifying letter type :- " + letterDetails.get("File"));
    }


    /**
     * This function logins with officer, selects branch, switch language, clicks on customer sales and services and
     * locates customer with CID
     *
     * @throws Throwable
     */
    @Then("^Access the Intralink application and Locate the Customer$")
    public void accessTheIntralinkApplicationAndLocateTheCustomer() throws IOException, InterruptedException {
        webDriver = new WebDriverTestBase().getDriver();

        if (loginCount == 1) {
            webDriver.get(signOnDetails.get("ApplicationUrl"));
            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  Intralink Application opened");

            webDriver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
            webDriver.manage().window().maximize();

            //Single Sign On with valid credentials
            webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
            loginPage.setScotiaID(signOnDetails);
            loginPage.setPassword(signOnDetails);
            loginPage.clickOnLogin();
            webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            //Select the desired branch
            selectBranch.cisSelectBranch(webDriver, signOnDetails);
            webDriver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);

            // switch Language if required
            intralinkPortal.switchLanguage(signOnDetails);

        }
        loginCount = loginCount + 1;
        intralinkPortal.clickCustomerSalesServices();
        webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

        //locate the customer by CID
        customerLocateSetup.cisCustomerLocateByCID(signOnDetails, letterDetails);
    }

    /**
     * This function retrives customer address based on account number(Savings/IP)
     *
     * @throws Throwable
     */
    @And("^Retrieve the Address$")
    public void getTheAddressDetailsFromAccountProfile() throws IOException, InterruptedException, FilloException {

        String scenarioId = CdicStepDefination.scenarioId;

        if ((sheetName.equals("LETTER1E") && letterDetails.get("AccountNumber").trim().length() > 11) || (sheetName.equals("LETTER1F") && letterDetails.get("AccountNumber").length() > 11)) {
            customersProductServices.clickProductByAccountNumber(letterDetails, testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId);
        } else if (
                (sheetName.equals("LETTER1E") && letterDetails.get("AccountNumber").length() < 11) || (sheetName.equals("LETTER1F") && letterDetails.get("AccountNumber").length() < 11) ||
                        (sheetName.equals("LETTER2E")) || (sheetName.equals("LETTER3E")) || (sheetName.equals("LETTER3F")) ||
                        (sheetName.equals("LETTER2F")) || (sheetName.equals("LETTER2EPNTA") || (sheetName.equals("LETTER3EPNTA")) || (sheetName.equals("LETTER2FPNTA")) || (sheetName.equals("LETTER3FPNTA")))) {
            String prdName = customersProductServices.getProductName(letterDetails);

            if (prdName != null) {
                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  Information page is displayed for the Product " + prdName + "(" + letterDetails.get("AccountNumber") + ")");
                //Get details from Information tab
                customersProductServices.clickOnInformationTAB(signOnDetails);

                cisCustomersInformation.getHomeAddress(readFromExcel, testdataPath, sheetName, scenarioId);
                customersProductServices.clickOnproductsAndServicesTAB();


                //Extracting Certificates No. for IP products only
                if (prdName.contains("- BNS") || prdName.contains("- SSI") || prdName.contains("- BNE") || prdName.contains("- PSI")) {
                    customersProductServices.clickProductByAccountNumber(letterDetails, testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId);
                } else
                    customersProductServices.clickProductByAccountNumber(letterDetails, testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId);


                webDriver.findElement(By.xpath(".//a[@id='ipb_IntralinkHREF']/img")).click();
                LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Clicked on Intralink Image to navigate to IntraLink Portal Page");

            } else if (prdName == null) {
                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Customer Account is not present in Customer Products and Services:- " + letterDetails.get("AccountNumber"));
            }

        }
        Thread.sleep(3000);
    }

    /**
     * This function validates whether customer accounts are Trust/Non Trust
     *
     * @throws Throwable
     */
    @And("^Validate whether Accounts linked are Trust accounts$")
    public void validateWhetherAccountsLinkedAreTrustAccounts() throws IOException, FilloException {
        details = readFromExcel.readFromExcel(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId);
        setDetails(details);
        if (sheetName.equals("LETTER1E") || sheetName.equals("LETTER2E") || sheetName.equals("LETTER3E") || sheetName.equals("LETTER2EPNTA") || sheetName.equals("LETTER3EPNTA")) {
            if (!(details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI") || details.get("ProductName").equals("Momentum PLUS Savings"))) {
                customersProductServices.verifyTrustAccountsOrNot(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId);
            } else if (details.get("ProductName").isEmpty()) {
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", "Product Name column is Blank");
                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Product name is null in Excel sheet");
            }
        } else if (sheetName.equals("LETTER1F") || sheetName.equals("LETTER2F") || sheetName.equals("LETTER3F") || sheetName.equals("LETTER2FPNTA") || sheetName.equals("LETTER3FPNTA")) {
            if (!(details.get("ProductName").contains("- BNE") || details.get("ProductName").contains("- PSI"))) {
                customersProductServices.verifyTrustAccountsOrNot(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId);
            } else if (details.get("ProductName").isEmpty()) {
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", "Product Name column is Blank");
                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Product name is null in Excel sheet");
            }

        }

    }

    /**
     * This function retrieves customer account extract data
     *
     * @throws Throwable
     */
    @Then("^Connect to the Raw Data extract for Letter$")
    public void connectToTheRawDataExtractForLetter() throws IOException {
        // It will pick extract file complete rows based on account number passed
        details = readFromExcel.readFromExcel(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId);
        setDetails(details);
        if (details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI") || details.get("ProductName").contains("- BNE") || details.get("ProductName").contains("- PSI") || details.get("ProductName").equals("Momentum PLUS Savings")) {
            String accountNumber = StringUtils.leftPad(letterDetails.get("AccountNumber").replace(" ", ""), 15, "0").replace(" ", "").trim();
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"IP Account Number :- " + accountNumber + " is verifying in extract");
            extractAccounts = new ReadTextFile().readTextFile(System.getProperty("user.dir") + "/src/main/resources/data/" + ConfigurationManager.getBundle().getString("environment") + "/Extracts/" + extractFile, accountNumber);
            setExtractAccounts(extractAccounts);
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),extractAccounts.size() + " records are found in extract with respect to IP Account Number :- " + accountNumber);
        } else {
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),"Savings/DDA Account Number :- " + letterDetails.get("AccountNumber").replace(" ", "") + " is verifying in extract");
            extractAccounts = new ReadTextFile().readTextFile(System.getProperty("user.dir") + "/src/main/resources/data/" + ConfigurationManager.getBundle().getString("environment") + "/Extracts/" + extractFile, letterDetails.get("AccountNumber").replace(" ", ""));
            setExtractAccounts(extractAccounts);
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),extractAccounts.size() + " records are found in extract with respect to Savings/DDA Account Number :- " + letterDetails.get("AccountNumber").replace(" ", ""));
        }
    }

    /**
     * This function validates customers data with respect to Intralink and Extract
     *
     * @throws Throwable
     */
    @And("^Validate the details mentioned for the Customer in the extract$")
    public void validateTheDetailsMentionedForTheCustomerInTheExtract() throws Throwable {
        details = getDetails();
        if (sheetName.equals("LETTER2EPNTA")) {
            //validating savings or DDA or IP non PAFT accounts in 2E
            if (details.get("File").equals("2E")) {
                List<String> executionStatus = new LinkedList<>();
                executionStatus.add(new ReadTextFile().verifyingIntralinkDetailsInExtract().stream().collect(Collectors.joining("|")));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", executionStatus.stream().collect(Collectors.joining("|")));
                LogHelper.logger.info(executionStatus);
                // Negative Testing
                extractFile = signOnDetails.get("2EPNTA");
                connectToTheRawDataExtractForLetter();
                if (extractAccounts.size() == 0) {
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus_PNTA", "Pass-(No Records present in 2EPNTA extract for 2E Accounts)");
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  No Records are present in 2EPNTA extract for 2E Accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                } else if (extractAccounts.size() >= 1) {
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus_PNTA", "Fail-(2E Records are present in 2EPNTA extract)");
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), extractAccounts.size() + " Records are present in 2EPNTA extract for 2E Accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                }
            } else if (details.get("File").equals("2EPNTA")) {
                //validating IP PAFT accounts in 2EPNTA
                List<String> executionStatus = new LinkedList<String>();
                executionStatus.add(new ReadTextFile().verifyingIntralinkDetailsInExtract().stream().collect(Collectors.joining("|")));
                LogHelper.logger.info(executionStatus);
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", executionStatus.stream().collect(Collectors.joining("|")));
                // Negative Testing
                extractFile = signOnDetails.get("2E");
                connectToTheRawDataExtractForLetter();
                if (extractAccounts.size() == 0) {
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus_PNTA", "Pass-(No Records present in 2E extract for 2EPNTA Accounts)");
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  No Records are present in 2E extract for 2EPNTA Accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                } else if (extractAccounts.size() >= 1) {
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus_PNTA", "Fail-(2EPNTA Records are present in 2E extract )");
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), extractAccounts.size() + " Records are present in 2E extract for 2EPNTA Accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                }
            }
        } else if (sheetName.equals("LETTER3EPNTA")) {
            //For savings and business and IP non PNTA accounts setup in BSC transit it is searched in 3E
            if (details.get("File").equals("3E") && (details.get("Transit").equals("47696") || details.get("Transit").equals("33993") || details.get("Transit").equals("63081"))) {
                List<String> executionStatus = new LinkedList<String>();
                executionStatus.add(new ReadTextFile().verifyingIntralinkDetailsInExtract().stream().collect(Collectors.joining("|")));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", executionStatus.stream().collect(Collectors.joining("|")));

            }
            //For IP-PNTA accounts setup in BSC transit it is searched in 3EPNTA
            else if (details.get("File").equals("3EPNTA") && (details.get("Transit").equals("47696") || details.get("Transit").equals("33993") || details.get("Transit").equals("63081"))) {
                List<String> executionStatus = new LinkedList<String>();
                LogHelper.logger.info(executionStatus);
                executionStatus.add(new ReadTextFile().verifyingIntralinkDetailsInExtract().stream().collect(Collectors.joining("|")));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", executionStatus.stream().collect(Collectors.joining("|")));

            }
            //For business account setup in non BSC transit it is searched in 2E
            else if (!(details.get("Transit").equals("47696") || details.get("Transit").equals("33993") || details.get("Transit").equals("63081")) && (details.get("ProductName").equals("Basic Business Account"))) {
                extractFile = signOnDetails.get("2E");
                connectToTheRawDataExtractForLetter();
                if (extractAccounts.size() == 0) {
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", "Fail-(No Records present in 2E extract also for non BSC transit accounts)");
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "No Records present in 2E extract also for non BSC transit accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                } else if (extractAccounts.size() >= 1) {
                    List<String> executionStatus = new LinkedList<String>();
                    executionStatus.add(new ReadTextFile().verifyingIntralinkDetailsInExtract().stream().collect(Collectors.joining("|")));
                    LogHelper.logger.info(executionStatus);
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", "Pass-(Record present in 2E extract for non BSC business Account )");
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), extractAccounts.size() + " Record present in 2E extract for non BSC business Account where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                }

            }
            //For IP setup in non BSC transit
            else if (!(details.get("Transit").equals("47696") || details.get("Transit").equals("33993")) && (details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI"))) {
                if (details.get("ProductName").equals("Pre-Need - BNS")) {
                    extractFile = signOnDetails.get("2EPNTA");
                    connectToTheRawDataExtractForLetter();
                    if (extractAccounts.size() == 0) {
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", "Fail-(No Records present in 2EPNTA extract also for non BSC transit,PNTA accounts)");
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "No Records present in 2EPNTA extract also for non BSC transit,PNTA accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                    } else if (extractAccounts.size() >= 1) {
                        List<String> executionStatus = new LinkedList<String>();
                        executionStatus.add(new ReadTextFile().verifyingIntralinkDetailsInExtract().stream().collect(Collectors.joining("|")));
                        LogHelper.logger.info(executionStatus);
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", "Pass-(Record present in 2EPNTA extract for non BSC transit,PNTA accounts)");
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), extractAccounts.size() + " Record present in 2EPNTA extract for non BSC transit,PNTA accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                    }
                } else {
                    extractFile = signOnDetails.get("2E");
                    connectToTheRawDataExtractForLetter();
                    if (extractAccounts.size() == 0) {
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", "Fail-(No Records present in 2E extract also for non BSC transit,non PNTA accounts)");
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "No Records present in 2E extract also for non BSC transit,non PNTA accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                    } else if (extractAccounts.size() >= 1) {
                        List<String> executionStatus = new LinkedList<String>();
                        executionStatus.add(new ReadTextFile().verifyingIntralinkDetailsInExtract().stream().collect(Collectors.joining("|")));
                        LogHelper.logger.info(executionStatus);
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", "Pass-(Record present in 2E extract for non BSC transit,non PNTA accounts)");
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), extractAccounts.size() + " Record present in 2E extract for non BSC transit,non PNTA accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                    }
                }

            }

            //For savings account setup in non BSC transit it is searched in 1E
            else {
                extractFile = signOnDetails.get("2E");
                connectToTheRawDataExtractForLetter();
                if (extractAccounts.size() == 0) {

                    if (details.get("Trust_Accounts").equalsIgnoreCase("NO")) {
                        List<String> executionStatus = new LinkedList<String>();
                        executionStatus.add(new ReadTextFile().verifyingIntralinkDetailsInExtract().stream().collect(Collectors.joining("|")));
                        LogHelper.logger.info(executionStatus);
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", executionStatus.stream().collect(Collectors.joining("|")));
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  No Records present in 2E extract for TRUST-NO where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                    } else if (details.get("Trust_Accounts").equalsIgnoreCase("YES")) {
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", "Fail-(No Records present in 2E extract also for non BSC transit savings accounts)");
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "No Records present in 2E extract also for non BSC transit savings accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                    }


                } else if (extractAccounts.size() >= 1) {
                    List<String> executionStatus = new LinkedList<String>();
                    executionStatus.add(new ReadTextFile().verifyingIntralinkDetailsInExtract().stream().collect(Collectors.joining("|")));
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", executionStatus.stream().collect(Collectors.joining("|")));
                    LogHelper.logger.info(executionStatus);
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), extractAccounts.size() + " Record present in 2E extract for non BSC savings Account where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                }

            }
        }

//        French PNTA
        if (sheetName.equals("LETTER2FPNTA")) {
            //validating savings or DDA or IP non PAFT accounts in 2F
            if (details.get("File").equals("2F")) {
                List<String> executionStatus = new LinkedList<String>();
                executionStatus.add(new ReadTextFile().verifyingIntralinkDetailsInExtract().stream().collect(Collectors.joining("|")));
                LogHelper.logger.info(executionStatus);
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", executionStatus.stream().collect(Collectors.joining("|")));

                // Negative Testing
                extractFile = signOnDetails.get("2FPNTA");
                connectToTheRawDataExtractForLetter();
                if (extractAccounts.size() == 0) {
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus_PNTA", "Pass-(No Records present in 2FPNTA extract for 2F Accounts)");
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  No Records are present in 2FPNTA extract for 2F Accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                } else if (extractAccounts.size() >= 1) {
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus_PNTA", "Fail-(2F Records are present in 2FPNTA extract)");
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), extractAccounts.size() + " Records are present in 2FPNTA extract for 2F Accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                }
            } else if (details.get("File").equals("2FPNTA")) {
                //validating IP PAFT accounts in 2EPNTA
                List<String> executionStatus = new LinkedList<String>();
                executionStatus.add(new ReadTextFile().verifyingIntralinkDetailsInExtract().stream().collect(Collectors.joining("|")));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", executionStatus.stream().collect(Collectors.joining("|")));
                // Negative Testing
                extractFile = signOnDetails.get("2F");
                connectToTheRawDataExtractForLetter();
                if (extractAccounts.size() == 0) {
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus_PNTA", "Pass-(No Records present in 2F extract for 2FPNTA Accounts)");
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  No Records are present in 2F extract for 2FPNTA Accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                } else if (extractAccounts.size() >= 1) {
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus_PNTA", "Fail-(2FPNTA Records are present in 2F extract )");
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), extractAccounts.size() + " Records are present in 2F extract for 2FPNTA Accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                }
            }
        }

        if (sheetName.equals("LETTER3FPNTA")) {
            //For savings and business and IP non PNTA accounts setup in BSC transit it is searched in 3E
            if (details.get("File").equals("3F") && (details.get("Transit").equals("47696") || details.get("Transit").equals("33993") || details.get("Transit").equals("63081"))) {
                if (details.get("Trust_Accounts").equalsIgnoreCase("NO")) {
                    List<String> executionStatus = new LinkedList<String>();
                    executionStatus.add(new ReadTextFile().verifyingIntralinkDetailsInExtract().stream().collect(Collectors.joining("|")));
                    LogHelper.logger.info(executionStatus);
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", executionStatus.stream().collect(Collectors.joining("|")));
                } else if (details.get("Trust_Accounts").equalsIgnoreCase("YES")) {
                    List<String> executionStatus = new LinkedList<String>();
                    executionStatus.add(new ReadTextFile().verifyingIntralinkDetailsInExtract().stream().collect(Collectors.joining("|")));
                    LogHelper.logger.info(executionStatus);
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", executionStatus.stream().collect(Collectors.joining("|")));
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", "Pass-(Records are present in 3F extract for savings and business accounts of BSC transit)");
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  Records are present in 3F extract for savings and business accounts of BSC transit where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                }
            }
            //For IP-PNTA accounts setup in BSC transit it is searched in 3EPNTA
            else if (details.get("File").equals("3FPNTA") && (details.get("Transit").equals("47696") || details.get("Transit").equals("33993") || details.get("Transit").equals("63081"))) {
                List<String> executionStatus = new LinkedList<String>();
                executionStatus.add(new ReadTextFile().verifyingIntralinkDetailsInExtract().stream().collect(Collectors.joining("|")));
                LogHelper.logger.info(executionStatus);
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", executionStatus.stream().collect(Collectors.joining("|")));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", "Pass-(Records present in 3FPNTA for PNTA accounts of BSC transits)");
                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  Records present in 3FPNTA for PNTA accounts of BSC transits where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());

            }
            //For business account setup in non BSC transit it is searched in 2E
            else if (!(details.get("Transit").equals("47696") || details.get("Transit").equals("33993")) && (details.get("ProductName").equals("Basic Business Account"))) {
                extractFile = signOnDetails.get("2F");
                connectToTheRawDataExtractForLetter();
                if (extractAccounts.size() == 0) {
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", "Fail-(No Records present in 2F extract also for non BSC transit business accounts)");
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "No Records present in 2F extract also for non BSC transit accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                } else if (extractAccounts.size() >= 1) {
                    List<String> executionStatus = new LinkedList<String>();
                    executionStatus.add(new ReadTextFile().verifyingIntralinkDetailsInExtract().stream().collect(Collectors.joining("|")));
                    LogHelper.logger.info(executionStatus);
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", "Pass-(Record present in 2F extract for non BSC business Account )");
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), extractAccounts.size() + " Record present in 2F extract for non BSC business Account where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                }

            }
            //For IP setup in non BSC transit
            else if (!(details.get("Transit").equals("47696") || details.get("Transit").equals("33993")) && (details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI"))) {
                if (details.get("ProductName").equals("Pre-Need - BNS")) {
                    extractFile = signOnDetails.get("2FPNTA");
                    connectToTheRawDataExtractForLetter();
                    if (extractAccounts.size() == 0) {
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", "Fail-(No Records present in 2FPNTA extract also for non BSC transit,PNTA accounts)");
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "No Records present in 2FPNTA extract also for non BSC transit,PNTA accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                    } else if (extractAccounts.size() >= 1) {
                        List<String> executionStatus = new LinkedList<String>();
                        executionStatus.add(new ReadTextFile().verifyingIntralinkDetailsInExtract().stream().collect(Collectors.joining("|")));
                        LogHelper.logger.info(executionStatus);
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", "Pass-(Record present in 2FPNTA extract for non BSC transit,PNTA accounts)");
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), extractAccounts.size() + " Record present in 2FPNTA extract for non BSC transit,PNTA accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                    }
                } else {
                    extractFile = signOnDetails.get("2F");
                    connectToTheRawDataExtractForLetter();
                    if (extractAccounts.size() == 0) {
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", "Fail-(No Records present in 2F extract also for non BSC transit,non PNTA accounts)");
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "No Records present in 2F extract also for non BSC transit,non PNTA accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                    } else if (extractAccounts.size() >= 1) {
                        List<String> executionStatus = new LinkedList<String>();
                        executionStatus.add(new ReadTextFile().verifyingIntralinkDetailsInExtract().stream().collect(Collectors.joining("|")));
                        LogHelper.logger.info(executionStatus);
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", "Pass-(Record present in 2F extract for non BSC transit,non PNTA accounts)");
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), extractAccounts.size() + " Record present in 2F extract for non BSC transit,non PNTA accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                    }
                }
            }
            //savings retail transit
            else {
                extractFile = signOnDetails.get("2F");
                connectToTheRawDataExtractForLetter();
                if (extractAccounts.size() == 0) {
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", "Fail-(No Records present in 2F extract also for non BSC transit,non PNTA accounts)");
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "No Records present in 2F extract also for non BSC transit,non PNTA accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                } else if (extractAccounts.size() >= 1) {
                    List<String> executionStatus = new LinkedList<>();
                    executionStatus.add(new ReadTextFile().verifyingIntralinkDetailsInExtract().stream().collect(Collectors.joining("|")));
                    LogHelper.logger.info(executionStatus);
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", "Pass-(Record present in 2F extract for non BSC transit,non PNTA accounts)");
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), extractAccounts.size() + " Record present in 2F extract for non BSC transit,non PNTA accounts where Account Number is :-" + details.get("AccountNumber").replace(" ", "").trim());
                }
            }

        } else if (!sheetName.contains("PNTA"))

        {
            List<String> executionStatus = new LinkedList<String>();
            executionStatus.add(new ReadTextFile().verifyingIntralinkDetailsInExtract().stream().collect(Collectors.joining("|")));
            LogHelper.logger.info(executionStatus);
            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus", executionStatus.stream().collect(Collectors.joining("|")));
        }

    }

    /**
     * This function sign out from Intralink
     *
     * @throws Throwable
     */
    @And("^signout from intralink application$")
    public void signoutFromIntralinkApplication() throws InterruptedException {
        customersProductServices.clickOnSignOff(signOnDetails);
    }

    /**
     * This function retrives customer account PDF data
     *
     * @throws Throwable
     */
    @Then("^Connect to the PDF Letter$")
    public void connectToThePDFLetter() throws IOException {
        details = letterDetails;
        String pdfFile = details.get("PDFRollUpName");
        pdfDocument = new ReadTextFile().readPdfDocument(System.getProperty("user.dir") + "/src/main/resources/data/" + ConfigurationManager.getBundle().getString("environment") + "/PDFs/" + pdfFile);
        setPdfDocument(pdfDocument);
    }


    @And("^Fetch the required Customer details from Raw Data extract and Intralink$")
    public void fetchTheRequiredCustomerDetailsFromRawDataExtractAndIntralink() {
        setPdfDetails(details);
        pdfDetails = getPdfDetails();
    }

    /**
     * This function validates customers data with respect to Intralink and PDF
     *
     * @throws Throwable
     */
    @Then("^Fetch the Customer details and validate the details from PDF with the details from Data sheet$")
    public void fetchTheCustomerDetailsAndValidateTheDetailsFromPDFWithTheDetailsFromDataSheet() throws IOException, FilloException {
        if (details.get("File").trim().equals("1F") || details.get("File").trim().equals("2F") || details.get("File").trim().equals("3F")) {
            List<String> executionStatus = new ReadTextFile().verifyingFrenchPdfDetailsWithExtract(pdfDocument);
            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus_PDF", executionStatus.stream().collect(Collectors.joining(",")));
            LogHelper.logger.info(executionStatus.stream().collect(Collectors.joining(",")));
        } else if (details.get("File").trim().equals("1E") || details.get("File").trim().equals("2E") || details.get("File").trim().equals("3E")) {

            List<String> executionStatus = new ReadTextFile().verifyingEnglishPdfDetailsWithExtract(pdfDocument);
            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus_PDF", executionStatus.stream().collect(Collectors.joining(",")));
            LogHelper.logger.info(executionStatus.stream().collect(Collectors.joining(",")));
        } else if (details.get("File").trim().equals("2EPNTA") || details.get("File").trim().equals("3EPNTA") ||
                details.get("File").trim().equals("2FPNTA") || details.get("File").trim().equals("3FPNTA")) {

            List<String> executionStatus = new ReadTextFile().verifyingPAFTPdfDetailsWithExtract(pdfDocument);
            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, CDIC_TESTDATA_TEXT, sheetName, scenarioId, "ExecutionStatus_PDF", executionStatus.stream().collect(Collectors.joining(",")));
            LogHelper.logger.info(executionStatus.stream().collect(Collectors.joining(",")));
        }
    }


}