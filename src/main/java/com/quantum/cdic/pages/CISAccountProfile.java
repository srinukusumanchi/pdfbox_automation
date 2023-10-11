package com.quantum.cdic.pages;
/**
 * Created by Srinu Kusumanchi(s3810121) for CDIC January Release.
 */
import com.qmetry.qaf.automation.ui.WebDriverBaseTestPage;
import com.qmetry.qaf.automation.ui.annotations.FindBy;
import com.qmetry.qaf.automation.ui.api.PageLocator;
import com.qmetry.qaf.automation.ui.api.WebDriverTestPage;
import com.qmetry.qaf.automation.ui.webdriver.QAFWebElement;

public class CISAccountProfile extends WebDriverBaseTestPage<WebDriverTestPage> {

    @FindBy(locator = "PrdAccountTitleOne")
    private QAFWebElement prdAccountTitleOne;

    @FindBy(locator = "PrdAccountTitleTwo")
    private QAFWebElement prdAccountTitleTwo;

    @FindBy(locator = "PrdAddressLineOne")
    private QAFWebElement prdAddressLineOne;

    @FindBy(locator = "PrdAddressLineTwo")
    private QAFWebElement prdAddressLineTwo;

    @FindBy(locator = "PrdCityAddressLineThree")
    private QAFWebElement prdCityAddressLineThree;

    @FindBy(locator = "PrdProvince_State")
    private QAFWebElement prdProvinceState;

    @FindBy(locator = "PrdPostal_ZipCode")
    private QAFWebElement prdPostalZipCode;

    @FindBy(locator = "PrdCountry")
    private QAFWebElement prdCountry;

    @Override
    protected void openPage(PageLocator locator, Object... args) {
        //abstract
    }
    /**
     * This function gets the customer account title 1
     *
     * @return String - returns the customer account title 1
     */
    public String getPrdAccountTitleOne() {
        return prdAccountTitleOne.getText().trim();
    }

    /**
     * This function gets the customer account title 2
     *
     * @return String - returns the customer account title 2
     */
    public String getPrdAccountTitleTwo() {
        if (driver.getPageSource().contains("Account Title 2:") || driver.getPageSource().contains("Compte - Titre 2:") || driver.getPageSource().contains("Titre compte 2:")) {
            return prdAccountTitleTwo.getText().trim();
        } else {
            return "";
        }

    }

    /**
     * This function gets the customer address line 1
     *
     * @return String - returns the customer address line 1
     */

    public String getPrdAddressLineOne() {
        return prdAddressLineOne.getText().trim();
    }

    /**
     * This function gets the customer address line 2
     *
     * @return String - returns the customer address line 2
     */
    public String getPrdAddressLineTwo() {
        return prdAddressLineTwo.getText().trim();
    }
    /*
     * get the customer address line 3/City
     */
    public String getPrdCityAddressLineThree() {
        return prdCityAddressLineThree.getText().trim();
    }

    /**
     * This function gets the customer State
     *
     * @return String - returns the customer State
     */
    public String getPrdProvinceState() {
        return prdProvinceState.getText().trim();
    }

    /**
     * This function gets the customer Postal Code
     *
     * @return String - returns the customer Postal Code
     */
    public String getPrdPostalZipCode() {
        return prdPostalZipCode.getText().trim();
    }
    /**
     * This function gets the customer Country
     *
     * @return String - returns the customer Country
     */

    public String getPrdCountry() {
        return prdCountry.getText().trim();
    }

}



