package com.quantum.cdic.pages;
/**
 * Created by Srinu Kusumanchi(s3810121) for CDIC January Release.
 */

import com.qmetry.qaf.automation.ui.WebDriverBaseTestPage;
import com.qmetry.qaf.automation.ui.annotations.FindBy;
import com.qmetry.qaf.automation.ui.api.PageLocator;
import com.qmetry.qaf.automation.ui.api.WebDriverTestPage;
import com.qmetry.qaf.automation.ui.webdriver.QAFWebElement;

public class CISGicDetails extends WebDriverBaseTestPage<WebDriverTestPage> {

    @FindBy(locator = "certificateNumber")
    private QAFWebElement certificateNumber;

    @FindBy(locator = "issuer")
    private QAFWebElement issuer;


    @Override
    protected void openPage(PageLocator locator, Object... args) {
        //Abstract
    }
    /**
     * This function fetches GIC Certificate Number
     * @return Customer IP Account GIC Number
     */
    public String getCertificateNumber() {
        return certificateNumber.getText().trim();
    }

    /**
     * This function fetches GIC Certificate Issuer
     * @return Customer IP Account GIC Issuer
     */
    public String getIssuer() {
        return issuer.getText().trim();
    }


}
