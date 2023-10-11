package com.quantum.baseclass;

import com.qmetry.qaf.automation.ui.WebDriverTestBase;
import com.qmetry.qaf.automation.ui.webdriver.QAFExtendedWebDriver;
import com.quantum.java.pages.ExtentReportHelper;
import com.quantum.utility.LoggingUtils;
import com.quantum.utils.LogHelper;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.quantum.constants.CommonConstants.*;
import static org.openqa.selenium.remote.DriverCommand.SCREENSHOT;


public class BasePage {

    protected static Properties properties = new Properties();
    static QAFExtendedWebDriver driver = null;
    protected File file;

    protected enum BY_TYPE {

        BY_XPATH, BY_LINKTEXT, BY_ID, BY_CLASSNAME,
        BY_NAME, BY_CSSSELECTOR, BY_PARTIALLINKTEXT, BY_TAGNAME
    }

    public static ExtentReportHelper getExtentReportHelper() {
        return extentReportHelper;
    }

    public static void setExtentReportHelper(ExtentReportHelper extentReportHelper) {
        BasePage.extentReportHelper = extentReportHelper;
    }

    private static ExtentReportHelper extentReportHelper;

    // Page class constructor -> Invokes First when you call this class methods
    public BasePage() {

        try {
            file = new File(System.getProperty("user.dir") + "/resources/application.properties");
            FileInputStream fileInputStream = new FileInputStream(file);
            properties.load(fileInputStream);
            BasePage.setExtentReportHelper(new ExtentReportHelper());
        } catch (IOException e) {
            LogHelper.logger.error(e.getMessage());
        }

    }


    // calling WebDriver and returns driver object
    public RemoteWebDriver getDriver() {
        return driver;
    }


    // Calling Web page elements based on different Selenium properties
    protected By getLocator(String locator, BY_TYPE type) {

        switch (type) {
            case BY_XPATH:

                return By.xpath(locator);

            case BY_LINKTEXT:
                return By.linkText(locator);
            case BY_ID:

                return By.id(locator);

            case BY_CSSSELECTOR:
                return By.cssSelector(locator);
            case BY_CLASSNAME:

                return By.className(locator);

            case BY_NAME:
                return By.name(locator);

            case BY_PARTIALLINKTEXT:
                return By.partialLinkText(locator);

            case BY_TAGNAME:
                return By.tagName(locator);

        }
        throw new IllegalArgumentException("Invalid By Type, Please provide correct locator type");

    }

    /**
     * This function Opens the Browser Window
     */
    public static QAFExtendedWebDriver getBrowser() {
        System.setProperty("webdriver.ie.driver", properties.getProperty("system.webdriver.ie.driver"));
        driver = new WebDriverTestBase().getDriver();
        return driver;
    }//getBrowser

    public static String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());

    }

    public static void compress(String zipFileLocation, String srcDir) {

        try (
                FileOutputStream fos = new FileOutputStream(zipFileLocation);

                ZipOutputStream zos = new ZipOutputStream(fos)) {

            File dir = new File(srcDir);

            File[] files = dir.listFiles();
            // create byte buffer
            byte[] buffer = new byte[1024];

            for (int i = 0; i < files.length; i++) {

                LogHelper.logger.info("Adding file: " + files[i].getName());
                if (files[i].getName().contains(".png")) {
                    try (FileInputStream fis = new FileInputStream(files[i])) {

                        // begin writing a new ZIP entry, positions the stream to the start of the entry data
                        zos.putNextEntry(new ZipEntry(files[i].getName()));

                        int length;

                        while ((length = fis.read(buffer)) > 0) {
                            zos.write(buffer, 0, length);
                        }
                        zos.closeEntry();
                    }
                }
            }


        } catch (IOException ioe) {
            LogHelper.logger.error(("Error creating zip file" + ioe));
        }
    }

    /**
     * This function launches the Application URL from grid.properties
     */
    // Launch URL in opened application
    public void launchBaseURL() throws IOException {
        String url = properties.getProperty("env.baseurl");
        if (url.length() != 0) {
            try {
                driver.get(url);
                driver.navigate().refresh();
                LogHelper.logger.info("The Base URL:" + url + "is loaded successfully");
            } catch (UnreachableBrowserException e) {
                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Unable to load the Base URL:- " + e.fillInStackTrace());
                throw new UnreachableBrowserException(UNABLE_TO_LOAD_BASE_URL + url);
            }
        } else {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), UNABLE_TO_LOAD_BASE_URL + url + " Please provide a valid Base URL");
            throw new UnreachableBrowserException(UNABLE_TO_LOAD_BASE_URL
                    + url + " Please provide a valid Base URL.");
        }
    }

    /**
     * This function is to navigate the browser to a url
     *
     * @param url - url to which browser has to be navigated
     */
    public void navigateToURL(String url) throws IOException {
        try {
            driver.navigate().to(url);
        } catch (UnreachableBrowserException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Unable to launch the url: " + url);
            throw new UnreachableBrowserException("Unable to launch the url: " + url);
        }
    }


//    -------------------------------------------Waits---------------------------------------------------

    /**
     * @param time    - Maximum time to waits for a particular element
     * @param element - Element that need to be find in web page
     *                This function is to make the driver wait explicitly for few seconds until element finds
     */
    public WebElement waitForElementExplicit(long time, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, time);
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * @param time - Maximum time waits for a all elements in web page until it find
     *             This function is to make the driver wait Implicitly for few seconds.
     */


    /**
     * @param time         - Maximum time to waits for a particular element
     * @param pollingValue - For every interval of time it will search for paricular element
     *                     This function is to make the driver wait Implicitly for few seconds.
     */
    public WebDriverWait getwaitForElementPooling(long time, int pollingValue) {
        WebDriverWait wait = new WebDriverWait(driver, time);
        wait.pollingEvery(pollingValue, TimeUnit.SECONDS);
        wait.ignoring(NoSuchElementException.class);
        wait.ignoring(ElementNotVisibleException.class);
        wait.ignoring(StaleElementReferenceException.class);
        wait.ignoring(NoSuchFrameException.class);
        return wait;
    }

    /**
     * This function returns the Current Window URL
     *
     * @return String - returns the Current Window URL
     */
    public String getCurrentURL() {
        LogHelper.logger.info("The current Browser URL returned is" + driver.getCurrentUrl());
        return driver.getCurrentUrl();
    }

    /**
     * This function returns the Current Window Title
     *
     * @return String - returns the Current Window Title
     */
    public String getPageTitle() {
        try {
            delay(10000);
            LogHelper.logger.info("The Current Window title is " + driver.getTitle());

        } catch (IOException e) {
            LogHelper.logger.error(e.getMessage());
        }
        return driver.getTitle();
    }


//    -------------------------------------------Drop Down Helper---------------------------------------------------

    /**
     * This function checks whether the Current Window URL is same as the
     * Expected
     *
     * @param expectedURL - URL expected in the Current Window
     * @return boolean - returns true if the CurrentWindow URL matches the
     * expectedURL, else returns false
     */
    public boolean isURLAsExpected(String expectedURL) {
        LogHelper.logger.info("The Current URL:" + getCurrentURL() + "; Expected URL:"
                + expectedURL);
        return expectedURL.equals(getCurrentURL());
    }

    /**
     * This function is to make the driver wait explicitly for a condition to be
     * satisfied
     *
     * @param element - waits for an element
     *                visibility/presence/clickability has to be checked
     */
    private void addExplicitWait(WebElement element, String condition, int inttimeoutinseconds) throws IOException {

        WebDriverWait webDriverWait = new WebDriverWait(driver, inttimeoutinseconds, 250L);
        try {
            if (condition.equalsIgnoreCase(VISIBILITY_TEXT)) {
                webDriverWait.until(ExpectedConditions.visibilityOf(element));
                LogHelper.logger.info("Driver waits explicitly until the element with is visible");
            } else if (condition.equalsIgnoreCase("clickable")) {
                webDriverWait.until(ExpectedConditions.elementToBeClickable(element));
                LogHelper.logger.info("Driver waits explicitly until the element is clickable");
            } else if (condition.equalsIgnoreCase("presence")) {
                webDriverWait.until(ExpectedConditions.presenceOfElementLocated((By) element));
                LogHelper.logger.info("Driver waits explicitly until the element is presence");
            } else {
                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Condition String should be visibility or clickable or presence");
            }
        } catch (NoSuchElementException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), THE_ELEMENT_WITH_TEXT
                    + element
                    + NOT_FOUND_TEXT + e.fillInStackTrace());
            throw new NoSuchElementException(THE_ELEMENT_WITH_TEXT
                    + element
                    + NOT_FOUND_TEXT);
        } catch (UnsupportedCommandException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "The condition given to check the elemnt with"
                    + element
                    + " is invalid:-" + e.fillInStackTrace());
            throw new NoSuchElementException("The condition given to check the elemnt with"
                    + element
                    + " is invalid", e.fillInStackTrace());
        }
    }

    /**
     * This function is used to handle the page load - Page Sync
     */
    public void waitForPageLoad() {

        Wait<WebDriver> wait = new WebDriverWait(driver, 1000);
        wait.until(webdriver -> {
            LogHelper.logger.info("Current Window State       : "
                    + driver.executeScript("return document.readyState"));
            return String
                    .valueOf(driver.executeScript("return document.readyState"))
                    .equals("complete");
        });
    }

    /**
     * This function is to select a dropdown option using its index
     *
     * @param element - element to locate the dropdown which is to be selected
     * @param index   - index of the dropdown option to be selected
     */
    public void selectDropDownByIndex(WebElement element, int index) throws IOException {
        try {
            addExplicitWait(element, VISIBILITY_TEXT, 20000);
            Select dropDown = new Select(element);
            dropDown.selectByIndex(index);
            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "User is able to select the dropdown option with index: " + index + SUCCESSFULLY_TEXT);
            LogHelper.logger.info("The dropdown option with index: " + index + IS_SELECTED_TEXT);
        } catch (NoSuchElementException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), UNABLE_SELECT_DROPDOWN_TEXT + element + NOT_FOUND_TEXT + e.fillInStackTrace());
            throw new NoSuchElementException(THE_ELEMENT_WITH_TEXT
                    + element
                    + NOT_FOUND_TEXT);
        }
    }

    /**
     * This function is to select the dropdown options that have a value
     * matching the argument
     *
     * @param element - element to locate the dropdown which is to be selected
     * @param value   - value to match against the dropdown option to be selected
     */
    public void selectDropDownByValue(WebElement element, String value) throws IOException {
        try {
            addExplicitWait(element, VISIBILITY_TEXT, 20000);
            Select dropDown = new Select(element);
            dropDown.selectByValue(value);
            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "User is able to select the dropdown option with value: " + value
                    + SUCCESSFULLY_TEXT);
            LogHelper.logger.info("The dropdown option with value: " + value
                    + IS_SELECTED_TEXT);
        } catch (NoSuchElementException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), UNABLE_SELECT_DROPDOWN_TEXT
                    + element
                    + NOT_FOUND_TEXT + e.fillInStackTrace());
            throw new NoSuchElementException(THE_ELEMENT_WITH_TEXT
                    + element
                    + NOT_FOUND_TEXT);
        }
    }

//    ---------------------------------------------------Action Helper-----------------------------------------------------

    /**
     * This function is to select the dropdown options that displays text
     * matching the argument
     *
     * @param element     - element to locate the dropdown which is to be selected
     * @param visibleText - visible text to match against the dropdown option to
     *                    be selected
     */
    public void selectDropDownByVisibleText(WebElement element, String visibleText) throws IOException {
        try {
            addExplicitWait(element, VISIBILITY_TEXT, 20000);
            Select dropDown = new Select(element);
            dropDown.selectByVisibleText(visibleText);
            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "User is able to select the dropdown option with text: " + visibleText
                    + SUCCESSFULLY_TEXT);
            LogHelper.logger.info("The dropdown option with text: " + visibleText
                    + IS_SELECTED_TEXT);
        } catch (NoSuchElementException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), UNABLE_SELECT_DROPDOWN_TEXT
                    + element
                    + NOT_FOUND_TEXT + e.fillInStackTrace());
            throw new NoSuchElementException(THE_ELEMENT_WITH_TEXT
                    + element
                    + NOT_FOUND_TEXT);
        }
    }

    /**
     * This function is to get selected value from dropdown options
     *
     * @param element - Drop down element to perform actions
     */
    public String getSelectedValue(WebElement element) throws IOException {
        String selecteOption;
        try {
            addExplicitWait(element, VISIBILITY_TEXT, 20000);
            Select dropDown = new Select(element);
            selecteOption = dropDown.getFirstSelectedOption().getText();
            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Selected Option for the element is: " + selecteOption);
        } catch (NoSuchElementException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Unable find dropdown element; The element with"
                    + element
                    + NOT_FOUND_TEXT + e.fillInStackTrace());
            throw new NoSuchElementException(THE_ELEMENT_WITH_TEXT
                    + element
                    + NOT_FOUND_TEXT);
        }
        return selecteOption;

    }

    /**
     * This function is to get all values from dropdown element
     *
     * @param locator - Drop down element to get all drop down values
     */
    public List<String> getAllDropDownValues(WebElement locator) throws IOException {
        addExplicitWait(locator, VISIBILITY_TEXT, 20000);
        Select dropDownValues = new Select(locator);

        List<WebElement> elementList = dropDownValues.getOptions();
        ArrayList<String> elementValues = new ArrayList<>();

        for (WebElement element : elementList) {

            elementValues.add(element.getText());
        }

        return elementValues;
    }

    /**
     * This function is to move the mouse pointer to the specified location
     *
     * @param locator - By object to locate the element to which mouse pointer
     *                has to be moved
     */
    public void mouseOver(WebElement locator) throws IOException {
        try {
            addExplicitWait(locator, VISIBILITY_TEXT, 20000);
            new Actions(driver).moveToElement(locator)
                    .build().perform();
            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "User is able to perform Mouse hover successfully");
            LogHelper.logger.info("Mouse hover is performed on element with"
                    + locator);
        } catch (NoSuchElementException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Unable to perform MouseOver; The element with"
                    + NOT_FOUND_TEXT + e.fillInStackTrace());
            throw new NoSuchElementException(THE_ELEMENT_WITH_TEXT
                    + NOT_FOUND_TEXT);
        }
    }

    /**
     * This function is to click on an element by moving the mouse pointer to
     * the specified location or to read the tip of a mouse
     *
     * @param element - By object to locate the element to which mouse pointer
     *                has to be moved
     */
    public void moveMouseTipAndClick(WebElement element) throws IOException {
        try {
            addExplicitWait(element, VISIBILITY_TEXT, 20000);
            Locatable hoverItem = (Locatable) element;
            Mouse mouse = driver.getMouse();
            mouse.mouseMove(hoverItem.getCoordinates());
            mouse.click(hoverItem.getCoordinates());
            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "User is able to perform Mouse hover successfully");
        } catch (NoSuchElementException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Unable to perform click; The element with"
                    + element
                    + NOT_FOUND_TEXT + e.fillInStackTrace());
            throw new NoSuchElementException(THE_ELEMENT_WITH_TEXT
                    + element
                    + NOT_FOUND_TEXT);
        }
    }

    /**
     * This function is to perform double click on a webelement
     *
     * @param element - By object of the webelement on which double click has to
     *                be performed
     */
    public void doubleClick(WebElement element) throws IOException {
        try {
            addExplicitWait(element, VISIBILITY_TEXT, 20000);
            Actions builder = new Actions(driver);
            builder.doubleClick(element).perform();
            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "User is able to perform Double Click is performed on successfuly");
            LogHelper.logger.info(THE_ELEMENT_WITH_TEXT + element
                    + " is right clicked");
        } catch (NoSuchElementException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Unable to perform doubleClick; The element with"
                    + element
                    + NOT_FOUND_TEXT + e.fillInStackTrace());
            throw new NoSuchElementException(THE_ELEMENT_WITH_TEXT
                    + element
                    + NOT_FOUND_TEXT);
        }
    }


//---------------------------------------------------Window Helper----------------------------------------------------------

    /**
     * This function performs a click-and-hold at the location of the source
     * element; moves to the location of the target element, then releases the
     * mouse.
     *
     * @param initialElementLocator - By object of the initial location of the
     *                              source webelement
     * @param finalElementLocator   - By object of the final location where the
     *                              webelement has to be moved
     */
    public void dragAndDrop(WebElement initialElementLocator, WebElement finalElementLocator) throws IOException {
        try {
            addExplicitWait(initialElementLocator, VISIBILITY_TEXT, 20000);
            addExplicitWait(finalElementLocator, VISIBILITY_TEXT, 20000);
            Actions builder = new Actions(driver);
            Action dragAndDrop = builder
                    .clickAndHold(initialElementLocator)
                    .moveToElement(finalElementLocator)
                    .release(finalElementLocator)
                    .build();
            dragAndDrop.perform();
            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "User is able to perform Drag And Drop on  successfuly");
            LogHelper.logger.info("The element is draged from"
                    + initialElementLocator.toString().replace("By.", " ")
                    + " to"
                    + finalElementLocator.toString().replace("By.", " "));
        } catch (NoSuchElementException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Unable to perform dragAndDrop;The element is not draged from"
                    + initialElementLocator.toString().replace("By.", " ")
                    + " to"
                    + finalElementLocator.toString().replace("By.", " ") + ":--" + e.fillInStackTrace());
            throw new NoSuchElementException("Unable to perform dragAndDrop;The element is not draged from"
                    + initialElementLocator.toString().replace("By.", " ")
                    + " to"
                    + finalElementLocator.toString().replace("By.", " "));
        }
    }

    /**
     * This function is to move the Webelement to an offset from the top-left
     * corner of the Webelement
     *
     * @param locator     - By object to locate the Webelement which is to be moved
     * @param locatorName - Name of the locator to declared.
     * @param xOffset     - xOffset by which the Webelement will be moved from the
     *                    current position towards x-axis
     * @param yOffset     - yOffset by which the Webelement will be moved from the
     *                    current position towards y-axis
     */
    public void moveToElement(By locator, String locatorName, int xOffset, int yOffset) throws IOException {
        try {
            Actions builder = new Actions(driver);
            builder.moveToElement(driver.findElement(locator), xOffset, yOffset);
            LogHelper.logger.info("User is able to perform Move to element on " + locatorName + SUCCESSFULLY_TEXT);
            LogHelper.logger.info("Element with " + locator.toString().replace("By.", " ")
                    + " " + "is moved " + xOffset + " along x-axis and"
                    + yOffset + " along y-axis");
        } catch (MoveTargetOutOfBoundsException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Unable to move the element from current position:-" + e.fillInStackTrace());
            throw new MoveTargetOutOfBoundsException("Target provided x:" + xOffset
                    + "and y:" + yOffset + "is invalid");
        } catch (NoSuchElementException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Unable to move the element from current position:-" + e.fillInStackTrace());
            throw new NoSuchElementException(THE_ELEMENT_WITH_TEXT
                    + locator.toString().replace("By.", " ")
                    + NOT_FOUND_TEXT);
        }

    }

    /**
     * This function is to perform a right click on a particular webelement
     *
     * @param locator     - By object of the Webelement on which right click
     *                    operation has to be performed
     * @param locatorName - Name of the locator to declared.
     */
    public void rightClick(By locator, String locatorName) throws IOException {
        try {
            WebElement webElement = driver.findElement(locator);
            Actions action = new Actions(driver);
            action.contextClick(webElement).build().perform();
            LogHelper.logger.info("User is able to perform Right Click on " + locatorName + SUCCESSFULLY_TEXT);
            LogHelper.logger.info("The element with "
                    + locator.toString().replace("By.", " ") + " is right clicked");
        } catch (NoSuchElementException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), UNABLE_TO_SCROLL_TEXT + e.fillInStackTrace());
            throw new NoSuchElementException(THE_ELEMENT_WITH_TEXT
                    + locator.toString().replace("By.", " ")
                    + NOT_FOUND_TEXT);
        } catch (UnsupportedCommandException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), UNABLE_TO_SCROLL_TEXT + e.fillInStackTrace());
            throw new UnsupportedCommandException("Command used by the webdriver is unsupported");
        }
    }

    public Set<String> getWindowHandles() {

        return driver.getWindowHandles();

    }

    /**
     * This function is to load the previous URL in the browser history.
     */
    public void navigateBack() throws IOException {
        try {
            driver.navigate().back();
            LogHelper.logger.info("User is able to do backward page navigation successfully");
        } catch (WebDriverException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "The page cannot be navigated backward:--" + e.fillInStackTrace());
            throw new WebDriverException("The page cannot be navigated backward");
        }
    }

    /**
     * This function loads the URL which is forward in the browser's history.
     * Does nothing if we are on the latest page viewed.
     */
    public void navigateForward() throws IOException {
        try {
            driver.navigate().forward();
            LogHelper.logger.info("User is able to do forward page navigation successfully");
        } catch (UnreachableBrowserException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "The page cannot be navigated forward:--" + e.fillInStackTrace());
            throw new UnreachableBrowserException("The page cannot be navigated forward");
        }
    }

    /**
     * This function refresh the current page
     */
    public void refreshPage() throws IOException {
        try {
            driver.navigate().refresh();
            LogHelper.logger.info("User is able to do page refresh successfully");
        } catch (UnreachableBrowserException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "The page cannot be refreshed:--" + e.fillInStackTrace());
            throw new UnreachableBrowserException("The page cannot be refreshed");
        }
    }

    public void switchToChildWindow(int index) {
        LinkedList<String> windowId = new LinkedList<>(getWindowHandles());

        if (index < 0 || index > windowId.size()) {
            throw new IllegalArgumentException("Invalid Index:" + index);
        }
        driver.switchTo().window(windowId.get(index));
    }

    public void switchToParentWindow() {
        LinkedList<String> windowId = new LinkedList<>(getWindowHandles());

        driver.switchTo().window(windowId.get(0));

    }

    /**
     * This function is to get the current window handle
     *
     * @return windowHandle - returns the handle of current browser window
     */
    public String getWindowHandle() throws IOException {
        String windowHandle = driver.getWindowHandle();
        LogHelper.logger.info("The current window handle " + windowHandle
                + " is returned");
        try {
            windowHandle = driver.getWindowHandle();
            LogHelper.logger.info("The current window handle " + windowHandle + " is returned");
        } catch (WebDriverException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Unable to returm the window handle:-" + e.fillInStackTrace());
            throw new WebDriverException("Unable to returm the window handle");
        }
        return windowHandle;
    }

    /**
     * This function is to switch the driver from Current Window to newly opened
     * Window
     */
    public void switchToWindow() throws IOException {
        try {
            for (String windowHandle : driver.getWindowHandles()) {
                driver.switchTo().window(windowHandle);
            }
            LogHelper.logger.info("User is able to switch between windows successfuly");
            LogHelper.logger.info("The window is switched");
        } catch (NoSuchWindowException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Unable to switch the window:--" + e.fillInStackTrace());
            throw new NoSuchWindowException("Unable to switch the window");
        }
    }

    /**
     * This function is to switch into a frame using frame index
     *
     * @param index - index of the frame to which driver has to be switched into
     */
    public void switchToFrameByIndex(int index) throws IOException {
        try {
            driver.switchTo().parentFrame();
            driver.switchTo().frame(index);
            LogHelper.logger.info("User is able to switch into frame successfully");
            LogHelper.logger.info("The driver is switched into frame");
        } catch (NoSuchFrameException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), UNABLE_TO_SWITCH_TEXT + e.fillInStackTrace());
            throw new NoSuchFrameException(UNABLE_TO_SWITCH_TEXT);
        }
    }

    /**
     * This function is to switch into a frame using the frame name
     *
     * @param frameName - name of the frame to which driver has to be switched
     *                  into
     */
    public void switchToFrameByName(String frameName) throws IOException {
        if (!frameName.equalsIgnoreCase(null)) {
            try {
                driver.switchTo().frame(frameName);
                LogHelper.logger.info("User is able to switch into frame:" + frameName + SUCCESSFULLY_TEXT);
                LogHelper.logger.info("The driver is switched to frame: " + frameName);
            } catch (NoSuchFrameException e) {
                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), UNABLE_TO_SWITCH_TEXT + e.fillInStackTrace());
                throw new NoSuchFrameException(UNABLE_TO_SWITCH_TEXT);
            }
        }
        LogHelper.logger.info("Unable to switch into frame as framename is null");
    }


//    ---------------------------------------------------Generic Helper-----------------------------------------------------

    /**
     * This function is to switch into a frame; frame is located as a webelemet
     *
     * @param locator - By object of the webelemet using which frame can be
     *                located
     */
    public void switchToFrameByWebElement(WebElement locator) throws IOException {
        try {
            driver.switchTo().frame(locator);
            LogHelper.logger.info("User is able to switch into frame successfully");
            LogHelper.logger.info("The driver is switched to frame with"
                    + locator.toString());
        } catch (NoSuchFrameException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), UNABLE_TO_SWITCH_TEXT + e.fillInStackTrace());
            throw new NoSuchFrameException(UNABLE_TO_SWITCH_TEXT);
        }
    }

    /**
     * This function closes the Current Browser Window
     */
    public void closeCurrentWindow() {
        driver.close();
        LogHelper.logger.info("Driver window is closed");
    }

    /**
     * This function clicks on the element which can be located by the By Object
     *
     * @param element     - Element that performs clicking action
     * @param locatorName - Name of the locator to declared. i.e., Name of the
     *                    locator_Button,Name of the locator_Link,etc
     */
    public void click(WebElement element, String locatorName) throws IOException {
        try {
            addExplicitWait(element, "clickable", 20000);//clickable,presence,visibility
            element.click();
            waitForPageLoad();
            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "User is able to click on " + locatorName
                    + SUCCESSFULLY_TEXT);
            LogHelper.logger.info("Click is performed on element with"
                    + element);
        } catch (NoSuchElementException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), THE_ELEMENT_WITH_TEXT
                    + element
                    + NOT_FOUND_TEXT + e.fillInStackTrace());
            throw new NoSuchElementException(THE_ELEMENT_WITH_TEXT
                    + element
                    + NOT_FOUND_TEXT);
        }
    }

    /**
     * This function is to pass a text into an input field within UI
     *
     * @param element     - Element that performs entering action
     * @param value       - Text value which is to be send to the input field
     * @param locatorName - Name of the locator to declared. i.e., Name of the
     *                    locator_Button,Name of the locator_Link,etc
     */
    public void type(WebElement element, String value, String locatorName) throws IOException {
        try {

            addExplicitWait(element, VISIBILITY_TEXT, 10000);//clickable,presence,visibility
            element.clear();
            element.sendKeys(value);
            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "User is able to enter " + value + " into " + locatorName + SUCCESSFULLY_TEXT);
            LogHelper.logger.info("String " + value + " is send to element with"
                    + element);
        } catch (NoSuchElementException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), THE_ELEMENT_WITH_TEXT
                    + element
                    + NOT_FOUND_TEXT + e.fillInStackTrace());
            throw new NoSuchElementException(THE_ELEMENT_WITH_TEXT
                    + element
                    + NOT_FOUND_TEXT);
        }
    }

    /**
     * This function is to get the visible text of an element within UI
     *
     * @param element - element to locate the element from which the text has
     *                to be taken
     * @return String - returns the innertext of the specified element
     */
    public String getText(WebElement element) throws IOException {
        String text = null;
        try {
            addExplicitWait(element, VISIBILITY_TEXT, 10000);//clickable,presence,visibility
            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "The value on the field with"
                    + element
                    + " is obtained");
            text = element.getText();
            LogHelper.logger.info("Text retreived from page is " + element + "");
        } catch (NoSuchElementException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Unable to get the text. The element with"
                    + element + NOT_FOUND_TEXT + e.fillInStackTrace());
            throw new NoSuchElementException("Unable to get the text. The element with"
                    + element
                    + NOT_FOUND_TEXT);
        }
        return text;
    }

    /**
     * This funtion is to maximize the Current Browser Window
     */
    public void maximizeWindow() throws IOException {
        try {
            driver.manage().window().maximize();
            LogHelper.logger.info("Window is maximized");
            LogHelper.logger.info("Window is maximized");
        } catch (UnreachableBrowserException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Unable to maximize the window:-" + e.fillInStackTrace());
            throw new NoSuchElementException("Unable to maximize the window");
        }
    }

    /**
     * This function is to add a time delay
     *
     * @param time - time duration in MilliSeconds
     */
    public void delay(int time) throws IOException {
        try {
            Thread.sleep(time);

            LogHelper.logger.info("Delay for " + time + " MilliSeconds is added");
        } catch (Exception e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Issue in adding extra delay:-" + e.fillInStackTrace());
        }
    }

    /**
     * This function is to check whether a webelement is visible or not
     *
     * @param element -  webelement which is to be checked either it is visible
     * @return boolean - returns true if the specified webelement is visible,
     * else it will return false
     */
    public boolean isElementVisible(WebElement element) {
        LogHelper.logger.info(" element is visible");
        LogHelper.logger.info("The element is visible");
        return element.isDisplayed();
    }

    /**
     * This function is to check whether a webelement is enabled or not
     *
     * @param element - webelement which is to be checked either it is enabled
     * @return boolean - returns true if the specified webelement is enabled,
     * else it will return false
     */
    public boolean isElementEnabled(WebElement element) {

        LogHelper.logger.info("element is enabled");
        LogHelper.logger.info("The element with is enabled");
        return element.isEnabled();
    }

    /**
     * This function is to check whether the Current Window Title is as expected
     *
     * @param expectedTitle - Title expected in the Current Window
     * @return boolean - returns true if the CurrentTitle matches the
     * expectedTitle, else it will return false
     */
    public boolean isTitleAsExpected(String expectedTitle) {
        LogHelper.logger.info("The current window title is " + getPageTitle()
                + " whereas the expected is " + expectedTitle);
        LogHelper.logger.info("The current window title is " + getPageTitle()
                + " whereas the expected is " + expectedTitle);
        return expectedTitle.equals(getPageTitle());
    }

    /**
     * This function is to get a cookie with a specific name
     *
     * @param cookieName - Name of the cookie which is to be returned
     * @return Cookie - Returns the cookie value for the name specified, or null
     * if no cookie found with the given name
     */
    public Cookie getCookie(String cookieName) {
        LogHelper.logger.info("User is able to obtain the cookie:" + cookieName + SUCCESSFULLY_TEXT);
        LogHelper.logger.info("The cookie:" + cookieName + " is obtained");
        return driver.manage().getCookieNamed(cookieName);
    }

    /**
     * This function is to delete a cookie from the browser's "cookie jar" The
     * domain of the cookie will be ignored.
     *
     * @param cookieName - name of the cookie which is to be deleted.
     */
    public void deleteCookieNamed(String cookieName) throws IOException {
        if (!cookieName.equalsIgnoreCase(null)) {
            try {
                LogHelper.logger.info("User is able to delete the cookie:" + cookieName + SUCCESSFULLY_TEXT);
                LogHelper.logger.info("The cookie:" + cookieName + " is deleted");
                driver.manage().deleteCookieNamed(cookieName);
            } catch (InvalidCookieDomainException e) {
                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Unable to delete the cookie:--" + e.fillInStackTrace());
                throw new InvalidCookieDomainException("The cookie with name "
                        + cookieName + " cannot be deleted");
            }
        } else {
            LogHelper.logger.info("Cookie Name is null; Unable to delete");
        }
    }
//--------------------------------------------JavaScriptHelper-----------------------------------------------------------

    /**
     * This function is to delete all the cookies for the current domain
     */
    public void deleteAllCookie() throws IOException {
        try {
            driver.manage().deleteAllCookies();
            LogHelper.logger.info("User is able to delete all cookies successfully");
            LogHelper.logger.info("All cookies are deleted");
        } catch (InvalidCookieDomainException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Unable to delete cookies:--" + e.fillInStackTrace());
            throw new InvalidCookieDomainException("Unable to delete cookies");
        }
    }

    /**
     * This function is used to assert the expected text in UI
     */
    public void assertText(String text) {
        try {
            delay(50);
            Assert.assertTrue(driver.getPageSource().contains(text));
            LogHelper.logger.info("" + text + " has been verified successfully in current page");

        } catch (NoSuchElementException e) {
            LogHelper.logger.info("" + text + " not displayed");
            java.util.logging.Logger.getLogger(BasePage.class.getName()).log(Level.SEVERE, "" + text + " not asserted", e);

        } catch (Exception ex) {
            LogHelper.logger.info("" + text + " not displayed");
            java.util.logging.Logger.getLogger(BasePage.class.getName()).log(Level.SEVERE, "" + text + " not asserted", ex);

        }
    }

    /**
     * This function is to click on a webelemet using JavascriptExecutor
     *
     * @param element - Name of the elemenet to click. *
     */
    public void clickUsingJavascriptExecutor(WebElement element) throws IOException {
        try {
            waitForElementExplicit(20, element);
            JavascriptExecutor javaScriptExecutor = driver;
            javaScriptExecutor.executeScript("arguments[0].click();", element);
            LogHelper.logger.info("User is able to click  successfully");
            LogHelper.logger.info("The element with is clicked");
            waitForPageLoad();
        } catch (NoSuchElementException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "The element with not found:--" + e.fillInStackTrace());
            throw new NoSuchElementException("The element with not found");
        }

    }

    /**
     * This function is to scroll the browser window to a webelement using
     * JavascriptExecutor
     *
     * @param element -  webelement to which the window has to
     *                be scroll to the element
     */
    public void scrollToElementUsingJavascriptExecutor(WebElement element) throws IOException {
        try {
            JavascriptExecutor js = driver;

            js.executeScript("arguments[0].scrollIntoView(true);", element);
            LogHelper.logger.info("User is able to scroll into  on browser successfully");
            LogHelper.logger.info("Browser window is scrolled to element with");
        } catch (NoSuchElementException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Unable to scroll: =" + e.fillInStackTrace());
            throw new NoSuchElementException("The element with not found");
        } catch (MoveTargetOutOfBoundsException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), UNABLE_TO_SCROLL_TEXT + e.fillInStackTrace());
            throw new MoveTargetOutOfBoundsException("Target element provided with is invalid");
        }

    }

    public void scrllVerticalToElementClick(WebElement element) {
        scrollIntoView(element);
        element.click();
    }

    public void scrollDownVertically() {
        javaScriptExecutorClick("window.scrollTo(0,document.body.scrollHeight)");
    }

    public void scrollUpVertically() {
        javaScriptExecutorClick("window.scrollTo(0,-document.body.scrollHeight)");
    }

    public void scrollDownUpByPixel(int value) //Negative value for Up and Positive value for down
    {
        javaScriptExecutorClick("window.scrollBy(0," + value + ")");
    }

    public void zoomInByPercentage(int value) {
        javaScriptExecutorClick("document.body.style.zoom='" + value + "%'");
    }

    public Object javaScriptExecutorClick(String script) {
        JavascriptExecutor exe = driver;

        return exe.executeScript(script);

    }
//-----------------------------------------------------------Alert Helper------------------------------------------------------

    public void scrollIntoView(WebElement element) {

        driver.executeScript("arguments[0].scrollIntoView();", element);

    }

    public void highlight(WebElement element) {

        driver.executeScript("arguments[0].setAttribute('style','background:yellow;border:2px solid red;');", element);
        try {
            Thread.sleep(1500);
        } catch (Exception e) {
            LogHelper.logger.error(e.getCause());
        }
        driver.executeScript("arguments[0].setAttribute('style','border:2px solid white;');", element);

    }

    /**
     * This function is to check whether there is any alert present.
     *
     * @return boolean - returns true if alert is present, else it will return
     * false
     */
    public boolean isAlertPresent() throws IOException {
        try {
            driver.switchTo().alert();
            LogHelper.logger.info("User is able to switch to alert box successfuly");
            LogHelper.logger.info("An alert box is present");
            return true;
        } catch (Exception e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "There is no alert present:--" + e.fillInStackTrace());
            return false;
        }
    }

    /**
     * This function is to handle the alert; Will Click on OK button First get a
     * handle to the open alert, prompt or confirmation and then accept the
     * alert.
     */
    public void acceptAlert() throws IOException {
        try {
            Alert alertBox = driver.switchTo().alert();
            alertBox.accept();
            LogHelper.logger.info("User is able to able to accept the alert box successfully");
            LogHelper.logger.info("The alert is accepted");
        } catch (NoAlertPresentException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), UNABLE_TO_ACCESS_ALERT_TEXT + e.fillInStackTrace());
            throw new NoAlertPresentException(ALERT_NOT_PRESENT_TEXT);
        }
    }

//--------------------------------------------------Table Helper----------------------------------------------------

    /**
     * This function is to handle the alert; Will Click on Cancel button First
     * get a handle to the open alert, prompt or confirmation and then dismiss
     * the alert.
     */
    public void dismissAlert() throws IOException {
        try {
            Alert alertBox = driver.switchTo().alert();
            alertBox.dismiss();
            LogHelper.logger.info("User is able to able to dismiss the alert box successfully ");
            LogHelper.logger.info("The alert is dismissed");
        } catch (NoAlertPresentException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), UNABLE_TO_ACCESS_ALERT_TEXT + e.fillInStackTrace());
            throw new NoAlertPresentException(ALERT_NOT_PRESENT_TEXT);
        }
    }

    /**
     * This function is to get the text which is present on the Alert.
     *
     * @return String - returns the text message which is present on the Alert.
     */
    public String getAlertMessage() throws IOException {
        String alertMessage = null;
        try {
            Alert alertBox = driver.switchTo().alert();
            LogHelper.logger.info("User is able to retrieve the text " + alertBox.getText() + " within popup successfully");
            LogHelper.logger.info("The text " + alertBox.getText() + " within popup is returned");
            alertMessage = alertBox.getText();
        } catch (NoAlertPresentException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), UNABLE_TO_ACCESS_ALERT_TEXT + e.fillInStackTrace());
            throw new NoAlertPresentException(ALERT_NOT_PRESENT_TEXT);
        }
        return alertMessage;
    }

    /**
     * This function for select box in the form of table
     *
     * @param locator
     * @param locatorName
     * @return
     */

    public List<WebElement> webElements(By locator, String locatorName) throws IOException {
        List<WebElement> allElements;
        try {
            allElements = driver.findElements(locator);
        } catch (NoSuchElementException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "selectFromTable: Unable to find the element:-" + e.fillInStackTrace());
            throw new NoSuchElementException(THE_ELEMENT_WITH_TEXT
                    + locatorName + NOT_FOUND_TEXT);
        }

        return allElements;
    }

    public void selectFromTable(WebElement table, WebElement td, String text, String locatorName) throws IOException {
        try {
            delay(500);
            addExplicitWait(table, VISIBILITY_TEXT, 10000);//clickable,presence,visibility
            WebElement tableWebElement = table;
            tableWebElement.click();
            WebElement tableTd = td;
            List<WebElement> allOptions = tableTd.findElements(By.tagName("td"));
            for (WebElement we : allOptions) {
                if (we.getText().equals(text)) {
                    we.click();
                    LogHelper.logger.info("User is able to select the dropdown option with text: " + text
                            + SUCCESSFULLY_TEXT);
                    return;
                }
            }
        } catch (NoSuchElementException e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "selectFromTable: Unable to find the element:-" + e.fillInStackTrace());
            throw new NoSuchElementException(THE_ELEMENT_WITH_TEXT
                    + locatorName + NOT_FOUND_TEXT);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(BasePage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public static String getCurrentDate() {
        return getCurrentDateTime().substring(0, 12);
    }

    //------------------------------------------------------------Date Helper------------------------------------------------
    public void datePicker(String date, String browser) {
        LogHelper.logger.info(date);


        Calendar now = Calendar.getInstance();
        int currentMonth = now.get(Calendar.MONTH) + 1;

        String[] dmy = date.split("/");
        int month = Integer.parseInt(dmy[1]);

        String specificdate = dmy[0];
        LogHelper.logger.info(month);
        LogHelper.logger.info(currentMonth);

        if (month != currentMonth) {

            if ("Firefox".equals(browser)) {
                selectDate(By.xpath("//*[@id='lh_calendarMatrix_5']/table"), By.className(CALENDER_BODY_CONTAINER_TEXT), specificdate);
            } else if ("Chrome".equals(browser)) {
                selectDate(By.xpath("//*[@id='lh_calendarMatrix_1']/table"), By.className(CALENDER_BODY_CONTAINER_TEXT), specificdate);
            } else if ("InternetExplorer".equals(browser)) {
                selectDate(By.xpath("//*[@id='lh_calendarMatrix_1']/table"), By.className(CALENDER_BODY_CONTAINER_TEXT), specificdate);
            }
        } else {
            if ("Firefox".equals(browser)) {
                selectDate(By.xpath("//*[@id='lh_calendarMatrix_4']/table"), By.className(CALENDER_BODY_CONTAINER_TEXT), specificdate);
            } else if ("Chrome".equals(browser)) {
                selectDate(By.xpath("//*[@id='lh_calendarMatrix_0']/table"), By.className(CALENDER_BODY_CONTAINER_TEXT), specificdate);
            } else if ("InternetExplorer".equals(browser)) {
                selectDate(By.xpath("//*[@id='lh_calendarMatrix_0']/table"), By.className(CALENDER_BODY_CONTAINER_TEXT), specificdate);
            }
        }

    }

    public void selectDate(By div, By by, String date) {
        try {
            // times out after 5 seconds
            WebDriverWait wait = new WebDriverWait(driver, 10);
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(div));
            WebElement dateWidget = element.findElement(by);
            List<WebElement> columns = dateWidget.findElements(By.tagName("td"));

            for (WebElement cell : columns) {
                LogHelper.logger.info(cell.getText());
                if (cell.getText().equals(date)) {
                    cell.click();
                    break;
                }
            }
        } catch (NoSuchElementException e) {
            LogHelper.logger.error(e.getMessage());
        }
    }

    public void selectFromList(By list, String text) {
        try {
            List<WebElement> allElements = driver.findElements(list);
            for (WebElement element : allElements) {

                if (element.getText().equals(text)) {
                    element.click();

                }
            }

        } catch (NoSuchElementException e) {
            LogHelper.logger.error(e.getMessage());

        }


    }

    //-------------------------------------------------Files Helper---------------------------------------------------------
    private void deleteFolderFiles(File folder) throws IOException {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolderFiles(f);
                } else {
                    Files.delete(Paths.get(f.getPath()));
                }
            }
        }
    }

    public Boolean fileExistsVerify(String folderName) {
        file = new File(folderName);
        boolean folderExists = false;
        if (file.exists()) {
            folderExists = true;
        }
        return folderExists;
    }

    public boolean folderExistsVerify(String folderName) {
        File folder = new File(folderName);
        boolean folderExists = false;
        if (folder.exists() && folder.isDirectory()) {
            folderExists = true;
        }
        return folderExists;
    }


    public void folderCreate(String folderName) {
        File folder = new File(folderName);
        boolean folderExist = folderExistsVerify(folder.getAbsolutePath());
        if (!folderExist) {
            folder.mkdir();
        }

    }

    public void moveDirectory(String srcDirecotory, String destDirectory) throws IOException {
        try {
            File srcDir = new File(srcDirecotory);
            File destDir = new File(destDirectory);
            if (folderExistsVerify(destDirectory)) {
                FileUtils.deleteDirectory(destDir);
            }
            FileUtils.moveDirectory(srcDir, destDir);
        } catch (Exception e) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Error in moving file directory:- " + e.fillInStackTrace());
        }
    }

}
