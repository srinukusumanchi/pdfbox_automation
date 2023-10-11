package com.quantum.utility;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import com.qmetry.qaf.automation.core.ConfigurationManager;
import com.quantum.baseclass.BasePage;
import com.quantum.utils.LogHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.quantum.constants.CommonConstants.SCREENSHOT;
import static com.quantum.steps.CdicStepDefination.*;

/*
  Created by Srinu Kusumanchi(s3810121) for CDIC January Release.
 */
public class ReadTextFile {
    static ReadFromExcel readFromExcel = new ReadFromExcel();

    static Map<String, String> details;
    static Map<String, String> letterDetails;
    static String sheetName;
    static String testdataPath;
    static PDDocument pdfDocument = null;
    static List<Map<String, String>> extractAccounts = null;
    private static ArrayList<Boolean> flagList = null;
    private static PDDocument pdfDocumentGeneric = null;
    private static ArrayList<Boolean> ipFlagList = null;
    private List<String> headerList = null;
    private List<String> dataList = null;
    private Map<String, String> sample = null;
    private List<Map<String, String>> samples = null;
    private List<Map<String, String>> ipSamples = null;
    private Map<String, Boolean> exectionStatusFailedFields = null;
    private PDFTextStripper pdfTextStripper = null;
    private static PDDocument pdfDocumentLTR = null;
    private Recordset countryCodes = null;
    private String pdfFileGeneric = null;
    private String pdfFileLTR = null;

    private static List<Map<String, String>> dqSamples = null;

    public ReadTextFile() {
        details = getDetails();
        letterDetails = getLetterDetails();
        sheetName = getSheetName();
        testdataPath = getTestdataPath();
        extractAccounts = getExtractAccounts();
        pdfDocument = getPdfDocument();
    }


    public static List<Map<String, String>> getDqSamples() {
        return dqSamples;
    }

    public static void setDqSamples(List<Map<String, String>> dqSamples) {
        ReadTextFile.dqSamples = dqSamples;
    }


    /**
     * This function reads Text file
     *
     * @param path-         Extract path
     * @param accountNumber -Find account number in extract
     * @return Customer details
     * @throws IOException
     */
    public List<Map<String, String>> readTextFile(String path, String accountNumber) throws IOException {
        // Creating file object by placing notepad file location
        File file = new File(path);
        // Reading file using Buffer Reader object
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Extract file individual line
            String extractLine;
            //Reading file line by line until end of file
//                Intralink                                                            Raw Data Letter File
//        1.         N/A                                                                   LETDATE
//        2.        Address Line 1                                                      ADDRESS1
//        3.        Address Line 2/City/Address Line 3 + Province/State                 ADDRESS 2
//        4.        City/Address Line 3 + Province/State                                ADDRESS 3
//        5.        Postal/Zip Code                                                     POSTCD
//        6.        Account Number - from Products & Services Screen                    ACCTNO
//        7.        N/A                                                                 MAILDATE
//        8.        N/A                                                                 ASATDATE
//        9.                                                                            ISSUER
//        10.       From IP screen                                                      CERTNUM
//        11.                                                                           CSTCIF01
//        12.       Primary Customer Name from Intralink                               CSTNAM01
//                                                                                      CSTCIF02
//        13.       Secondary Customer Name from Intralink                             CSTNAM02
//                                                                                      CSTCIF03
//        14.       Secondary Customer Name from Intralink                             CSTNAM03
//                                                                                      CSTCIF04
//        15.       Secondary Customer Name from Intralink                             CSTNAM04
//                                                                                      CSTCIF05
//        16.       Secondary Customer Name from Intralink                             CSTNAM05
//                                                                                      CSTCIF06
//        17.       Secondary Customer Name from Intralink                             CSTNAM06
//                                                                                      CSTCIF07
//        18.       Secondary Customer Name from Intralink                             CSTNAM07
//                                                                                      CSTCIF08
//        19.       Secondary Customer Name from Intralink                             CSTNAM08
//                                                                                      CSTCIF09
//        20.       Secondary Customer Name from Intralink                             CSTNAM09
//                                                                                      CSTCIF10
//        21.       Secondary Customer Name from Intralink                             CSTNAM10
//                                                                                      LSTACT
//        22.       Account Number - from Products & Services Screen                   BASACT

            // Extract file Headers
            String headerValues = "LETDATE;ADDRESS1;ADDRESS 2;ADDRESS 3;POSTCD;ACCTNO;MAILDATE;ASATDATE;ISSUER;CERTNUM;CSTCIF01;CSTNAM01;CSTCIF02;CSTNAM02;CSTCIF03;CSTNAM03;CSTCIF04;CSTNAM04;CSTCIF05;CSTNAM05;CSTCIF06;CSTNAM06;CSTCIF07;CSTNAM07;CSTCIF08;CSTNAM08;CSTCIF09;CSTNAM09;CSTCIF10;CSTNAM10;LSTACT;BASACT";
            String headerValuesArray[] = headerValues.split("\\;");
            samples = new ArrayList<Map<String, String>>();
            headerList = new ArrayList<String>();
            for (String header : headerValuesArray) {
                headerList.add(header);
            }
            // Reading Extract file line by line
            while ((extractLine = br.readLine()) != null) {
                //Header values of extract

                //Object created to store all extract lines into dataList Object
                dataList = new ArrayList<String>();
                sample = new LinkedHashMap<String, String>();
                // Spliting based on ";"
                for (String s : extractLine.split("\\;")) {
                    //Storing all individual lines of text files in dataList object
                    dataList.add(s);
                }
                // Mapping Headers and Header value into Map object
                for (int i = 0; i < headerValuesArray.length; i++) {
                    sample.put(headerList.get(i), dataList.get(i));
                }

                //Based on account number passed those lines which contains same account numbers will get stored into samples object
                if (sample.get("BASACT").trim().equals(accountNumber)) {
                    samples.add(sample);
                }
            }
        }
        return samples;
    }

    /**
     * This function reads PDF Document
     *
     * @param pdfFilePath- PDF Path
     * @return PDF Document
     * @throws IOException
     */
    public PDDocument readPdfDocument(String pdfFilePath) throws IOException {
        // Creating PDF file document
        File file = new File(pdfFilePath);
        return PDDocument.load(file);
    }

    /**
     * This function verifies customer having single PDF
     *
     * @param pdfDocument- PDF Document
     * @return PDF Customer details
     * @throws IOException
     */
    private HashMap<Integer, List<String>> verifyCustomerHavingSinglePdf(PDDocument pdfDocument) throws IOException, FilloException {
        // Hash Map Object created to store Page Number and address details
        HashMap<Integer, List<String>> pages = new HashMap<Integer, List<String>>();
        String pdfData = null;
        String countryCode = null;
         /*Iterating all PDF pages for verifying customer address and Incrementing with 2 pages because each customer
        contains 2 pages*/
        for (int page = 0; page <= pdfDocument.getNumberOfPages() - 1; page = page + 2) {
            LoggingUtils.log("Page Number:-" + page);
            pdfTextStripper = new PDFTextStripper();
            pdfTextStripper.setStartPage(page + 1);
            pdfTextStripper.setEndPage(page + 1);
            pdfData = pdfTextStripper.getText(pdfDocument);
            countryCodes = readFromExcel.getSaveValue(System.getProperty("user.dir") + "/src/main/resources/data/", "CountryCodes.xlsx", "CountryCodes", details.get("State"));
            countryCode = countryCodes.getField("NameValue").trim();
            String excelAddress = details.get("CustomerName") + details.get("Joint1_CustomerName") + details.get("Joint2_CustomerName") + details.get("Joint3_CustomerName") +
                    details.get("AddressLine1_IntraLink") + details.get("AddressLine2_IntraLink") + details.get("City_IntraLink") + countryCode + details.get("PostalCode_IntraLink");

            // Retrieve Address details and stores in pdfAddressDetails list object
            List<String> pdfAddressDetails = getAddressDetails(page, pdfDocument);
            // Replacing spaces, newlines with empty string for PDF Address
            String pdfAddress = pdfAddressDetails.stream().map(x -> x.replaceAll("[\\n||\\r||\\s+||,]", "")).collect(Collectors.joining(""));
            // comparing excel address with pdf address by replacing spaces, newlines with empty string
            if (excelAddress.replaceAll("\\s+", "").replace(",", "").equals(pdfAddress)) {
                pages.put(page + 1, pdfAddressDetails);
            }
        }

        return pages;
    }


    /**
     * This function verifies customer having single PDF for PAFT Accounts
     *
     * @param pdfDocument- PDF Document
     * @return PDF Customer details for PAFT Accounts
     * @throws IOException
     */
    private HashMap<Integer, List<String>> verifyCustomerHavingSinglePdfPAFT(PDDocument pdfDocument) throws IOException, FilloException {
        // Hash Map Object created to store Page Number and address details
        HashMap<Integer, List<String>> pages = new HashMap<Integer, List<String>>();
        String countryCode = null;
        String pdfData = null;
        /*Iterating all PDF pages for verifying customer address and Incrementing with 2 pages because each customer
        contains 2 pages*/
        for (int page = 0; page < pdfDocument.getNumberOfPages(); page++) {
            LoggingUtils.log("Page Number:-" + page);
            pdfTextStripper = new PDFTextStripper();
            pdfTextStripper.setStartPage(page + 1);
            pdfTextStripper.setEndPage(page + 1);
            pdfData = pdfTextStripper.getText(pdfDocument);
            countryCodes = readFromExcel.getSaveValue(System.getProperty("user.dir") + "/src/main/resources/data/", "CountryCodes.xlsx", "CountryCodes", details.get("State"));
            countryCode = countryCodes.getField("NameValue").trim();

            String excelAddress = details.get("CustomerName") + details.get("AddressLine1_IntraLink") + details.get("AddressLine2_IntraLink") + details.get("City_IntraLink") + countryCode + details.get("PostalCode_IntraLink");

            // Retrieve Address details and stores in pdfAddressDetails list object
            List<String> pdfAddressDetails = getPAFTAddressDetails(page);
            // Replacing spaces, newlines with empty string for PDF Address
            String pdfAddress = pdfAddressDetails.stream().map(x -> x.replaceAll("[\\n||\\r||\\s+||,]", "")).collect(Collectors.joining(""));
            // comparing excel address with pdf address by replacing spaces, newlines with empty string
            if (excelAddress.replaceAll("\\s+", "").replace(",", "").equals(pdfAddress)) {
                pages.put(page + 1, pdfAddressDetails);
            }
        }

        return pages;
    }

    /**
     * This function gets IP Account details from PDF
     *
     * @param pdfData- Customer data
     * @return IP Account details
     */
    private List<String> getIPAccountDetails(String pdfData) {
        Pattern pattern = null;
        // Retrieving IP accounts details from PDF based on English and French
        if (details.get("File").trim().equals("1E") || details.get("File").trim().equals("2E") || details.get("File").trim().equals("3E")) {
//            pattern = Pattern.compile("[A-Z]{1,4}\\s+[IP#||GIC#0-9]{15,18}");
            pattern = Pattern.compile("IP#|GIC#");
        } else if (details.get("File").trim().equals("1F") || details.get("File").trim().equals("2F") || details.get("File").trim().equals("3F") || details.get("File").trim().equals("2FPNTA") || details.get("File").trim().equals("3FPNTA")) {
//            pattern = Pattern.compile("[A-Z]{1,4}\\s+[RP N°||CPG N°0-9]{15,18}");
            pattern = Pattern.compile("RP N°|CPG N°");
        }

        Pattern finalPattern = pattern;
        return Arrays.stream(pdfData.split("\n")).filter(x -> finalPattern.matcher(x).find() == true).collect(Collectors.toList());
    }

    private List<String> getIPAccountDetailsFromLTR(String pdfData) {
        Pattern pattern = null;
        // Retrieving IP accounts details from PDF based on English and French
        if (details.get("File").trim().equals("1E") || details.get("File").trim().equals("2E") || details.get("File").trim().equals("3E")) {
            pattern = Pattern.compile("IP#|GIC#");
        } else if (details.get("File").trim().equals("1F") || details.get("File").trim().equals("2F") || details.get("File").trim().equals("3F") || details.get("File").trim().equals("2FPNTA") || details.get("File").trim().equals("3FPNTA")) {
            pattern = Pattern.compile("RP N°|CPG N°");
        }

        Pattern finalPattern = pattern;
        return Arrays.stream(pdfData.split("\n")).filter(x -> finalPattern.matcher(x).find() == true).collect(Collectors.toList()).stream().map(x -> x.trim().replace("\r", "")).collect(Collectors.toList());
    }


    /**
     * This function gets Savings Account details from PDF
     *
     * @param pdfData- Customer data
     * @return Savings Account details
     */
    private List<String> getSavingsAccountDetails(String pdfData) {
        // Retrieving Savings accounts details from PDF
        Pattern pattern = Pattern.compile("^[A-Z]{1,4}\\s+[0-9]{1,5}\\s[0-9]{1,5}\\s[0-9]{1,2}$");

        return Arrays.stream(pdfData.split("\n")).filter(x -> pattern.matcher(x).find() == true).collect(Collectors.toList());
    }

    /**
     * This function gets Savings Account details from LTR PDF
     *
     * @param pdfData- Customer data
     * @return Savings Account details
     */
    private List<String> getSavingsAccountDetailsFromLTR(String pdfData) {
        // Retrieving Savings accounts details from PDF
        Pattern pattern = Pattern.compile("\\s+[0-9]{1,5}\\s[0-9]{1,5}\\s[0-9]{1,2}\\s+[A-Z]{1,4}");

        return Arrays.stream(pdfData.split("\n")).filter(x -> pattern.matcher(x).find() == true).collect(Collectors.toList());
    }


    /**
     * This function gets Customer address details  from PDF
     *
     * @param pageNumber- Identified Customer page
     * @return Customer address details
     * @throws IOException
     */
    private List<String> getAddressDetails(int pageNumber, PDDocument pdfDocument) throws IOException {
        /*Retrieving normal account Address details from PDF based co-ordinates
         **Note: Co-ordinates for address changes for normal accounts and PAFT accounts PDF's*/
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);
        Rectangle rect = new Rectangle(10, 100, 600, 130);
        stripper.addRegion("class1", rect);
        PDPage firstPage = pdfDocument.getPage(pageNumber);
        stripper.extractRegions(firstPage);

        return Arrays.asList(stripper.getTextForRegion("class1").split("\\n"));

    }

    /**
     * This function gets PAFT Customer address details  from PDF
     *
     * @param pageNumber- Identified Customer page
     * @return PAFT Customer address details
     * @throws IOException
     */
    private List<String> getPAFTAddressDetails(int pageNumber) throws IOException {
        /*Retrieving PAFT account Address details from PDF based co-ordinates
         **Note: Co-ordinates for address changes for normal accounts and PAFT accounts PDF's*/
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);
        Rectangle rect = new Rectangle(10, 130, 600, 130);
        stripper.addRegion("class1", rect);
        PDPage firstPage = pdfDocument.getPage(pageNumber);
        stripper.extractRegions(firstPage);

        return Arrays.asList(stripper.getTextForRegion("class1").split("\\n"));

    }

    /**
     * This function verifies all customer accounts are present in single PDF
     *
     * @param pdfAccounts- Customer accounts
     * @return accounts which are present in PDF
     * @throws IOException
     * @throws FilloException
     */
    private List<String> verifyingAllAccountsPresentInSinglePDF(List<String> pdfAccounts) throws FilloException {
        // Creating an object to verify all accounts present in single customer PDF
        List<String> accountsExists = new LinkedList<>();
        Recordset recordset = null;
        // Creating fillo object to retrieve accounts from excel
        Fillo fillo = new Fillo();
        Connection connection = fillo.getConnection(testdataPath + "\\" + "CDIC_InterimTestData.xlsx");
        String query = "SELECT * FROM LETTER" + details.get("File") + " WHERE CID=" + "'" + details.get("CID") + "'" + " AND  AddressLine1_IntraLink=" + "'" + details.get("AddressLine1_IntraLink") + "'"
                + " AND  AddressLine2_IntraLink=" + "'" + details.get("AddressLine2_IntraLink") + "'" + " AND  City_IntraLink=" + "'" + details.get("City_IntraLink") + "'" + " AND  PostalCode_IntraLink=" + "'" + details.get("PostalCode_IntraLink") + "'"
                + " AND  Joint1_CustomerName=" + "'" + details.get("Joint1_CustomerName") + "'" + " AND  Joint2_CustomerName=" + "'" + details.get("Joint2_CustomerName") + "'" + " AND  Joint3_CustomerName=" + "'" + details.get("Joint3_CustomerName") + "'"
                + " AND  Trust_Accounts=" + "'YES'" + " AND  File=" + "'" + details.get("File") + "'";
        try {
            recordset = connection.executeQuery(query);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        List<String> accountNumbers = new LinkedList<>();
        // Iterates and adds Savings/IP account numbers in accountNumber list object by padding with 0's
        if (recordset != null) {
            while (recordset.next()) {
                if (sheetName.equals("LETTER1E") || sheetName.equals("LETTER2E") || sheetName.equals("LETTER3E")) {
                    if (recordset.getField("AccountNumber").replaceAll("\\s+", "").length() < 11) {
                        String accountNumber = StringUtils.leftPad(recordset.getField("AccountNumber").replace("\\s+", ""), 15, "0").trim();
                        String delar = recordset.getField("Delar_Intralink").contains("SSI") ? "BNS" : recordset.getField("Delar_Intralink");
                        accountNumbers.add(delar + "  IP#" + accountNumber);
                        if (!recordset.getField("GIC_IntraLink").equals("")) {
                            String[] gics = recordset.getField("GIC_IntraLink").split("\\|");
                            String[] issuers = recordset.getField("Issuer_Intralink").split("\\|");
                            for (int i = 0; i < gics.length; i++) {
                                accountNumbers.add(issuers[i] + "  " + gics[i]);
                            }
                        }
                    } else if (recordset.getField("AccountNumber").length() > 11) {
                        String accountNumber = recordset.getField("Delar_Intralink") + "  " + recordset.getField("AccountNumber").trim().substring(0, 5) + " " + recordset.getField("AccountNumber").
                                trim().substring(5, 10) + " " + recordset.getField("AccountNumber").trim().substring(10, 12).trim();
                        accountNumbers.add(accountNumber);
                    }
                } else if (sheetName.equals("LETTER1F") || sheetName.equals("LETTER2F") || sheetName.equals("LETTER3F"))
                    if (recordset.getField("AccountNumber").replaceAll("\\s+", "").length() < 11) {
                        String accountNumber = StringUtils.leftPad(recordset.getField("AccountNumber").replace("\\s+", ""), 15, "0").trim();
                        accountNumbers.add(recordset.getField("Delar_Intralink") + "  RP N° " + accountNumber);
                        if (!recordset.getField("GIC_IntraLink").equals("")) {
                            String[] gics = recordset.getField("GIC_IntraLink").split("\\|");
                            String[] issuers = recordset.getField("Issuer_Intralink").split("\\|");
                            for (int i = 0; i < gics.length; i++) {
                                accountNumbers.add(issuers[i] + "  " + gics[i].replace("CPG#", "CPG N° "));
                            }
                        }
                    } else if (recordset.getField("AccountNumber").length() > 11) {
                        String accountNumber = recordset.getField("Delar_Intralink") + "  " + recordset.getField("AccountNumber").trim().substring(0, 5) + " " + recordset.getField("AccountNumber").
                                trim().substring(5, 10) + " " + recordset.getField("AccountNumber").trim().substring(10, 12).trim();
                        accountNumbers.add(accountNumber);
                    }

            }
        }

        // Stores all PDF account numbers into pdfAccountNumbers object
        List<String> pdfAccountNumbers = pdfAccounts.stream().map(x -> x.replace("\\s+", "").trim().replace("\r", "")).collect(Collectors.toList());
        // Verifies excel account numbers(Savings/IP) with PDF account numbers(Savings/IP) are present or not
        if (accountNumbers.stream().filter(x -> pdfAccountNumbers.contains(x) == false).collect(Collectors.toList()).size() >= 1) {
            accountsExists.add("Mismatched account" + accountNumbers.stream().filter(x -> pdfAccountNumbers.contains(x) == false).collect(Collectors.toList()));
        } else {
            accountsExists.add("All accounts are present in single PDF's");
        }
        return accountsExists;
    }


    private List<String> verifyingAllAccountsPresentInSinglePDFLTR(List<String> pdfAccounts) throws FilloException {
        // Creating an object to verify all accounts present in single customer PDF
        List<String> accountsExists = new LinkedList<>();
        Recordset recordset = null;
        // Creating fillo object to retrieve accounts from excel
        Fillo fillo = new Fillo();
        Connection connection = fillo.getConnection(testdataPath + "\\" + "CDIC_InterimTestData.xlsx");
        String query = "SELECT * FROM LETTER" + details.get("File") + " WHERE CID=" + "'" + details.get("CID") + "'" + " AND  AddressLine1_IntraLink=" + "'" + details.get("AddressLine1_IntraLink") + "'"
                + " AND  AddressLine2_IntraLink=" + "'" + details.get("AddressLine2_IntraLink") + "'" + " AND  City_IntraLink=" + "'" + details.get("City_IntraLink") + "'" + " AND  PostalCode_IntraLink=" + "'" + details.get("PostalCode_IntraLink") + "'"
                + " AND  Joint1_CustomerName=" + "'" + details.get("Joint1_CustomerName") + "'" + " AND  Joint2_CustomerName=" + "'" + details.get("Joint2_CustomerName") + "'" + " AND  Joint3_CustomerName=" + "'" + details.get("Joint3_CustomerName") + "'"
                + " AND  Trust_Accounts=" + "'YES'" + " AND  File=" + "'" + details.get("File") + "'";
        try {
            recordset = connection.executeQuery(query);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        List<String> accountNumbers = new LinkedList<>();
        // Iterates and adds Savings/IP account numbers in accountNumber list object by padding with 0's
        if (recordset != null) {
            while (recordset.next()) {
                if (sheetName.equals("LETTER1E") || sheetName.equals("LETTER2E") || sheetName.equals("LETTER3E")) {
                    if (recordset.getField("AccountNumber").replaceAll("\\s+", "").length() < 11) {
                        String accountNumber = StringUtils.leftPad(recordset.getField("AccountNumber").replace("\\s+", ""), 15, "0").trim();
                        String delar = recordset.getField("Delar_Intralink").contains("SSI") ? "BNS" : recordset.getField("Delar_Intralink");
                        accountNumbers.add("IP#" + accountNumber + " " + delar);
                        if (!recordset.getField("GIC_IntraLink").equals("")) {
                            String[] gics = recordset.getField("GIC_IntraLink").split("\\|");
                            String[] issuers = recordset.getField("Issuer_Intralink").split("\\|");
                            for (int i = 0; i < gics.length; i++) {
                                accountNumbers.add(gics[i] + " " + issuers[i]);
                            }
                        }
                    } else if (recordset.getField("AccountNumber").length() > 11) {
                        String accountNumber = recordset.getField("AccountNumber").trim().substring(0, 5) + " " + recordset.getField("AccountNumber").
                                trim().substring(5, 10) + " " + recordset.getField("AccountNumber").trim().substring(10, 12).trim() + " " + recordset.getField("Delar_Intralink");
                        accountNumbers.add(accountNumber);
                    }
                } else if (sheetName.equals("LETTER1F") || sheetName.equals("LETTER2F") || sheetName.equals("LETTER3F"))
                    if (recordset.getField("AccountNumber").replaceAll("\\s+", "").length() < 11) {
                        String accountNumber = StringUtils.leftPad(recordset.getField("AccountNumber").replace("\\s+", ""), 15, "0").trim();
                        String delar = recordset.getField("Delar_Intralink").contains("PSI") ? "BNE" : recordset.getField("Delar_Intralink");
                        accountNumbers.add("RP N° " + accountNumber + " " + delar);
                        if (!recordset.getField("GIC_IntraLink").equals("")) {
                            String[] gics = recordset.getField("GIC_IntraLink").split("\\|");
                            String[] issuers = recordset.getField("Issuer_Intralink").split("\\|");
                            for (int i = 0; i < gics.length; i++) {
                                accountNumbers.add(gics[i].replace("CPG#", "CPG N° ") + " " + issuers[i]);
                            }
                        }
                    } else if (recordset.getField("AccountNumber").length() > 11) {
                        String accountNumber = recordset.getField("AccountNumber").trim().substring(0, 5) + " " + recordset.getField("AccountNumber").
                                trim().substring(5, 10) + " " + recordset.getField("AccountNumber").trim().substring(10, 12).trim() + " " + recordset.getField("Delar_Intralink");
                        accountNumbers.add(accountNumber);
                    }

            }
        }

        // Stores all PDF account numbers into pdfAccountNumbers object
        List<String> pdfAccountNumbers = pdfAccounts.stream().map(x -> x.replace("\\s+", "").trim().replace("\r", "")).collect(Collectors.toList());
        // Verifies excel account numbers(Savings/IP) with PDF account numbers(Savings/IP) are present or not
        if (accountNumbers.stream().filter(x -> pdfAccountNumbers.contains(x) == false).collect(Collectors.toList()).size() >= 1) {
            accountsExists.add("Mismatched account" + accountNumbers.stream().filter(x -> pdfAccountNumbers.contains(x) == false).collect(Collectors.toList()));
        } else {
            accountsExists.add("All accounts are present in single PDF's");
        }
        return accountsExists;
    }

    /**
     * This function verifies all Intralink French customer accounts details with PDF
     *
     * @param pdfDocument- PDF Document
     * @return Execution status
     * @throws IOException
     * @throws FilloException
     */
    public List<String> verifyingFrenchPdfDetailsWithExtract(PDDocument pdfDocument) throws IOException, FilloException {
        String pdfData = null;
        String countryCode = null;
        // Page 1 Variables
        boolean primaryCustomerNameFlag = false;
        boolean letterDateFlag = false;
        boolean stampedDateFlag = false;
        boolean jointOneCustomerFlag = false;
        boolean jointTwoCustomerFlag = false;
        boolean jointThreeCustomerFlag = false;
        boolean addressLine1Flag = false;
        boolean addressLine2Flag = false;
        boolean addressLine3Flag = false;
        boolean addressLine4Flag = false;
        boolean accountNumberFlag = false;
        boolean delarFlag = false;
        boolean issuerFlag = false;

        //Page 2 Variables
        boolean dearSirMadamBody = false;
        boolean asatDateFlag = false;
        boolean primaryCustomerName_1Flag = false;
        boolean addressLine1Flag_1Flag = false;
        boolean addressLine2Flag_1Flag = false;
        boolean addressLine3Flag_1Flag = false;
        boolean addressLine4Flag_1Flag = false;
        int pdfCount = 0;
        // Creating object for Execution status
        List<String> executionStatus = new LinkedList<>();
        // Creating object for mismatch fields while comparing with excel value with pdf fields
        Map<String, Boolean> exectionStatusFailedFields = null;
        // pdfPages stores address details and it's PDF Page number
        HashMap<Integer, List<String>> pdfPages = verifyCustomerHavingSinglePdf(pdfDocument);
        Set<Integer> pdfNumbers = pdfPages.keySet();
        int pdfPage = 0;

        if (pdfPages.size() == 1) {
            pdfPage = (Integer) pdfPages.keySet().toArray()[0];
        }

        List<String> accountDetails = null;
        // Customer address should present only once in entire PDF Letter
        if (pdfPages.size() == 1) {
            LogHelper.logger.info("Customer Name:- " + details.get("CustomerName") + " and Scenarios ID:- " + details.get(getScenarioId()) + " " + "is located in PDF Page Number:-" + pdfPage);
            pdfTextStripper = new PDFTextStripper();
            pdfTextStripper.setStartPage(pdfPage);
            pdfTextStripper.setEndPage(pdfPage);
            pdfData = pdfTextStripper.getText(pdfDocument);
//             If account is French Savings then it goes to below condition
            if (!(details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI") || (details.get("ProductName").contains("- BNE") || details.get("ProductName").contains("- PSI")
                    || details.get("ProductName").contains("Momentum PLUS Savings")))) {

                //Verifying Page 1 details
//            Date on Page 3 (ASATDATE)
//            ->Primary Customer Name/Sole Customer Name (pages 1, 3)
//            ->Date on Page 1
//            ->Account Address line 1
//            ->Account Address line 2(Address Line 2/City/Address Line 3 + Province/State)
//            ->Account Address line 3(City/Address Line 3 + Province/State)
//            ->Account Address line 4(Postal/Zip Code)
//            ->Account Number (table)
//            ->Issuer (table)
                List<String> pdfAddressDetails = pdfPages.get(pdfPage);
                //pdfAddressDetails
                int addressPosition = 0;
                pdfPages.get(pdfPage);
                //Validation Primary Customer Name from Intralink to PDF
                primaryCustomerNameFlag = details.get("CustomerName").trim().equals(pdfAddressDetails.get(addressPosition).trim());
                if (primaryCustomerNameFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Primary Customer is as expected Actual:- " + pdfAddressDetails.get(addressPosition) + "Expected:- " + details.get("CustomerName"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer is not as expected Actual:- " + pdfAddressDetails.get(addressPosition) + "Expected:- " + details.get("CustomerName"));
                }

                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNamePDF", pdfAddressDetails.get(addressPosition));
                //Validation Letter Date from Intralink to PDF
                letterDateFlag = isContains(details.get("ASATDATE").replace("Le", ""), pdfData);
                if (letterDateFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Letter Date in Page 1 is as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE").replace("Le", ""));
                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Letter Date in Page 1 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE").replace("Le", ""));
                }
                //Validation Letter Date from Intralink to PDF
                stampedDateFlag = isContains(details.get("MAILDATE"), pdfData);
                if (stampedDateFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Stamped date On or before in Page 1 is as expected Actual:- " + pdfData + "Expected:- " + details.get("MAILDATE"));
                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Stamped date On or before in Page 1 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("MAILDATE"));
                }

                //Validation Joint One Customer from Intralink to PDF
                if (!details.get("Joint1_CustomerName").isEmpty()) {
                    String joint1CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointOneCustomerFlag = details.get("Joint1_CustomerName").trim().equals(joint1CustomerName.trim());
                    if (jointOneCustomerFlag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Joint One Customer is as expected Actual:- " + joint1CustomerName + "Expected:- " + details.get("Joint1_CustomerName"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint One Customer is not as expected Actual:- " + joint1CustomerName + "Expected:- " + details.get("Joint1_CustomerName"));
                    }
                } else {
                    jointOneCustomerFlag = true;

                }
                //Validation Joint Two Customer from Intralink to PDF
                if (!details.get("Joint2_CustomerName").isEmpty()) {
                    String joint2CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointTwoCustomerFlag = details.get("Joint2_CustomerName").trim().equals(joint2CustomerName.trim());
                    if (jointTwoCustomerFlag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Joint Two Customer is as expected Actual:- " + joint2CustomerName + "Expected:- " + details.get("Joint2_CustomerName"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint Two Customer is not as expected Actual:- " + joint2CustomerName + "Expected:- " + details.get("Joint2_CustomerName"));
                    }
                } else {
                    jointTwoCustomerFlag = true;
                }
                //Validation Joint Three Customer from Intralink to PDF
                if (!details.get("Joint3_CustomerName").isEmpty()) {
                    String joint3CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointThreeCustomerFlag = details.get("Joint3_CustomerName").trim().equals(joint3CustomerName.trim());
                    if (jointThreeCustomerFlag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Joint Three Customer is as expected Actual:- " + joint3CustomerName + "Expected:- " + details.get("Joint3_CustomerName"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint Three Customer is not as expected Actual:- " + joint3CustomerName + "Expected:- " + details.get("Joint3_CustomerName"));
                    }
                } else {
                    jointThreeCustomerFlag = true;
                }
                String pdfAddressLine1 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                addressLine1Flag = details.get("AddressLine1_IntraLink").trim().equals(pdfAddressLine1.trim());
                //Validation Address Line 1 Customer from Intralink to PDF
                if (addressLine1Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 1 is as expected Actual:- " + pdfAddressLine1 + "Expected:- " + details.get("AddressLine1_IntraLink"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 1 is not as expected Actual:- " + pdfAddressLine1 + "Expected:- " + details.get("AddressLine1_IntraLink"));
                }

                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_PDF", pdfAddressLine1);
                if (!details.get("AddressLine2_IntraLink").isEmpty()) {
                    String pdfAddressLine2 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    addressLine2Flag = details.get("AddressLine2_IntraLink").trim().equals(pdfAddressLine2.trim());
                    //Validation Address Line 2 Customer from Intralink to PDF
                    if (addressLine2Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 2 is as expected Actual:- " + pdfAddressLine2 + "Expected:- " + details.get("AddressLine2_IntraLink"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 2 is not as expected Actual:- " + pdfAddressLine2 + "Expected:- " + details.get("AddressLine2_IntraLink"));
                    }

                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_PDF", pdfAddressLine2);
                } else {
                    addressLine2Flag = true;
                }
                //Validation Address Line 3(City) Customer from Intralink to PDF
                countryCodes = readFromExcel.getSaveValue(System.getProperty("user.dir") + "/src/main/resources/data/", "CountryCodes.xlsx", "CountryCodes", details.get("State"));
                countryCode = countryCodes.getField("NameValue").trim();
                addressLine3Flag = countryCode.equals("") ? isContains(details.get("City_IntraLink").trim(), pdfData) : isContains(details.get("City_IntraLink").trim() + ", " + countryCode, pdfData);
                String pdfAddressLine3 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                if (addressLine3Flag) {

                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 3/City is as expected Actual:- " + pdfAddressLine3 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 3/City is as not expected Actual:- " + pdfAddressLine3 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_PDF", pdfAddressLine3);
                //readFromExcel.saveColumnValueToSpecificScenarioSheetName(TestdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", extractAccounts.get(position).get("ADDRESS1"));
                if (!details.get("PostalCode_IntraLink").isEmpty()) {

                    //String pdfAddressLine4 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
//                    addressLine4Flag = isContains(details.get("PostalCode_IntraLink"), pdfData);
                    addressLine4Flag = pdfData.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    //Validation Address Line 4(Postal Code) Customer from Intralink to PDF
                    if (addressLine4Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 4/Postal Code is as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 4/Postal Code is not as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    }

                } else {
                    addressLine4Flag = true;
                }
                // Retrieves savings account details from PDF
                accountDetails = getSavingsAccountDetails(pdfData);
                LogHelper.logger.info("Customer Name:- " + details.get("CustomerName") + " and Scenarios ID:- " + details.get("getScenarioId()") + " Account details are:- " + accountDetails.stream().collect(Collectors.joining(",")));

                int accountNumbersCount = accountDetails.stream().map(x -> x.replaceAll("\\D+", "")).collect(Collectors.toList()).
                        stream().filter(x -> details.get("AccountNumber").equals(x)).collect(Collectors.toList()).size();
                // It check whether intralink account present in PDF as well as only 1 account should be present instead of duplication
                if (accountNumbersCount == 1) {
                    if (details.get("Trust_Accounts").equals("YES")) {
                        accountNumberFlag = true;
                        if (accountNumberFlag) {
                            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Account Number is as expected Actual:- " + accountDetails.stream().map(x -> x.replaceAll("\\D+", "")).collect(Collectors.toList()).
                                    stream().filter(x -> details.get("AccountNumber").equals(x)).collect(Collectors.toList()) + "Expected:- " + details.get("AccountNumber"));

                        } else {
                            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Number is not as expected Actual:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                    "Expected:- " + details.get("AccountNumber"));
                        }
                        // Verifiying Dealar value by concatinating with account number
                        List<String> pdfDelar = accountDetails.stream().filter(x -> x.replace("\\r", "").contains(details.get("AccountNumber").
                                        trim().substring(0, 5) + " " + details.get("AccountNumber").
                                        trim().substring(5, 10) + " " + details.get("AccountNumber").trim().substring(10, 12))).
                                collect(Collectors.toList());
                        if (pdfDelar.size() != 0) {
                            String pdfDelarValue = Arrays.asList(pdfDelar.get(0).split("  ")).get(0);

                            delarFlag = pdfDelarValue.equals(details.get("Delar_Intralink"));
                            // Verifying Delar value
                            if (delarFlag) {

                                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Delar is as expected Actual:- " + pdfDelarValue + "Expected:- " + details.get("Delar_Intralink"));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_PDF", pdfDelarValue);

                            } else {
                                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Delar is not as expected Actual:- " + pdfDelarValue + " Expected:- " + details.get("Delar_Intralink"));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_PDF", pdfDelarValue);
                            }
                        }
                    } else if (details.get("Trust_Accounts").equals("NO")) {
                        executionStatus.add("Fail:-Account Numbers is Present in PDF even Trust is NO");
                    }
                }
                // If account number is more than 1 time in PDF then it comes to below condition
                else if (accountNumbersCount > 1) {
                    accountNumberFlag = false;
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Numbers and Delars are present multiple times in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                            "Expected Account Number:- " + details.get("AccountNumber") + " " + "Delar " + details.get("Delar_Intralink"));
                    delarFlag = false;
                    executionStatus.add("Account Numbers is repeated");

                }
                // If account number is not present in PDF then it comes to below condition
                else if (accountNumbersCount == 0) {
                    if (details.get("Trust_Accounts").equalsIgnoreCase("NO") ||
                            details.get("AccountStatus_Intralink").equalsIgnoreCase("Closed")) {
                        accountNumberFlag = true;
                        delarFlag = true;
                    } else {
                        accountNumberFlag = false;
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Numbers and Delars are not present in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                "Expected Account Number:- " + details.get("AccountNumber") + " " + "Delar " + details.get("Delar_Intralink"));
                        delarFlag = false;
                    }
                }


                //Verifying Page 2 details
//             ->Date on Page 3 (MAILDATE)
//             ->Primary Customer Name/Sole Customer Name (pages 1, 3)
//            ->Account Address line 1
//            ->Account Address line 2(Address Line 2/City/Address Line 3 + Province/State)
//            ->Account Address line 3(City/Address Line 3 + Province/State)
//            ->Account Address line 4(Postal/Zip Code)

                // Validating French Second page PDF
                pdfTextStripper.setStartPage(pdfPage + 1);
                pdfTextStripper.setEndPage(pdfPage + 1);
                pdfData = pdfTextStripper.getText(pdfDocument);
                List<String> addressDetailsPage2 = getAddressDetails(pdfPage, pdfDocument);
                int addressPosition_PageTwo = 0;
                dearSirMadamBody = pdfData.replaceAll("\r", "").contains(details.get("Page3Body"));
                //Validation Mail date from extract to PDF
                if (dearSirMadamBody) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Page 3 body is as expected Actual:- " + pdfData + "Expected:- " + details.get("Page3Body"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Page 3 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("Page3Body"));
                }
                //Validation ASAT date from extract to PDF
                asatDateFlag = isContains(details.get("ASATDATE"), pdfData);
                if (asatDateFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  ASAT Date is as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "ASAT Date is not as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE"));
                }

                primaryCustomerName_1Flag = details.get("CustomerName").trim().equals(addressDetailsPage2.get(addressPosition_PageTwo).trim());

                //Validation Primary Customer Name from Intralink to PDF
                if (primaryCustomerName_1Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Primary Customer in Page 2 is as expected Actual:- " + addressDetailsPage2.get(addressPosition_PageTwo) + "Expected:- " + details.get("CustomerName"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer in Page 2 is not as expected Actual:- " + addressDetailsPage2.get(addressPosition_PageTwo) + "Expected:- " + details.get("CustomerName"));
                }
                //Validation Address Line 1 from Intralink to PDF
                addressLine1Flag_1Flag = details.get("AddressLine1_IntraLink").trim().equals(addressDetailsPage2.get(addressPosition_PageTwo = addressPosition_PageTwo + 1).trim());
                //Validation Address Line 2 from Intralink to PDF
                if (!details.get("AddressLine2_IntraLink").isEmpty()) {
                    String pdfAddressLine2Page2 = addressDetailsPage2.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
                    addressLine2Flag_1Flag = details.get("AddressLine2_IntraLink").trim().equals(pdfAddressLine2Page2.trim());
                    if (addressLine2Flag_1Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Primary Customer in Page 2 is as expected Actual:- " + pdfAddressLine2Page2 + "Expected:- " + details.get("AddressLine2_IntraLink"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer in Page 2 is not as expected Actual:- " + pdfAddressLine2Page2 + "Expected:- " + details.get("AddressLine2_IntraLink"));
                    }

                } else {
                    addressLine2Flag_1Flag = true;
                }

                //Validation Address Line 3(City) from Intralink to PDF
                countryCodes = readFromExcel.getSaveValue(System.getProperty("user.dir") + "/src/main/resources/data/", "CountryCodes.xlsx", "CountryCodes", details.get("State"));
                countryCode = countryCodes.getField("NameValue").trim();
                addressLine3Flag_1Flag = countryCode.equals("") ? isContains(details.get("City_IntraLink").trim(), pdfData) : isContains(details.get("City_IntraLink").trim() + ", " + countryCode, pdfData);

                String pdfAddressLine3Page2 = pdfAddressDetails.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
                if (addressLine3Flag_1Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 3/City in Page 2 is as expected Actual:- " + pdfData + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 3/City in Page 2 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);
                }
                //Validation Address Line 4(Postal Code) from Intralink to PDF
                if (!details.get("PostalCode_IntraLink").isEmpty()) {
                    //String pdfAddressLine4Pag2 = pdfAddressDetails.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
//                    addressLine4Flag_1Flag = isContains(details.get("PostalCode_IntraLink"), pdfData);
                    addressLine4Flag_1Flag = pdfData.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    if (addressLine4Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 4/Postal Code is as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 4/Postal Code is not as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink"));
                    }

                } else {
                    addressLine4Flag_1Flag = true;
                }

                // Verifying Failed Details while comparing Intralink values with PDF
                exectionStatusFailedFields = new HashMap<>();


                exectionStatusFailedFields.put("Primary Customer Name", primaryCustomerNameFlag);
                exectionStatusFailedFields.put("Letter Date", letterDateFlag);
                exectionStatusFailedFields.put("Stamped Date", stampedDateFlag);
                exectionStatusFailedFields.put("Joint One Customer Name", jointOneCustomerFlag);
                exectionStatusFailedFields.put("Joint Two Customer Name", jointTwoCustomerFlag);
                exectionStatusFailedFields.put("Joint Three Customer Name", jointThreeCustomerFlag);
                exectionStatusFailedFields.put("AddressLine1", addressLine1Flag);
                exectionStatusFailedFields.put("AddressLine2", addressLine2Flag);
                exectionStatusFailedFields.put("City", addressLine3Flag);
                exectionStatusFailedFields.put("PostalCode", addressLine4Flag);
                exectionStatusFailedFields.put("AccountNumber", accountNumberFlag);
                exectionStatusFailedFields.put("Dealer", delarFlag);

                exectionStatusFailedFields.put("Primary Customer Name Page2", primaryCustomerName_1Flag);
                exectionStatusFailedFields.put("Address Line1 Page2", addressLine1Flag_1Flag);
                exectionStatusFailedFields.put("Address Line2 Page2", addressLine2Flag_1Flag);
                exectionStatusFailedFields.put("City Page2", addressLine3Flag_1Flag);
                exectionStatusFailedFields.put("Postal Code Page2", addressLine4Flag_1Flag);
                exectionStatusFailedFields.put("Page 3 Body", dearSirMadamBody);
                exectionStatusFailedFields.put("ASAT Date Page2", asatDateFlag);
                if (exectionStatusFailedFields.entrySet().
                        stream().filter(x -> x.getValue().equals(false)).collect(Collectors.toList()).size() > 0) {
                    executionStatus.add("Fail-> Mismatch Fields:-" + exectionStatusFailedFields.entrySet().
                            stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                            collect(Collectors.joining(",")));
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Intralink and PDF fields are mismatched at " + exectionStatusFailedFields.entrySet().
                            stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                            collect(Collectors.joining(",")));
                } else {
                    executionStatus.add("Pass");
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  All Intralink and PDF Fields are matched");
                }


                //If account is French IP Account then it goes to below condition
            } else if (details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI") || details.get("ProductName").contains("- BNE") || details.get("ProductName").contains("- PSI")
                    || details.get("ProductName").contains("Momentum PLUS Savings")) {


                //Verifying Page 1 details
//            Date on Page 3 (ASATDATE)
//            ->Primary Customer Name/Sole Customer Name (pages 1, 3)
//            ->Date on Page 1
//            ->Account Address line 1
//            ->Account Address line 2(Address Line 2/City/Address Line 3 + Province/State)
//            ->Account Address line 3(City/Address Line 3 + Province/State)
//            ->Account Address line 4(Postal/Zip Code)
//            ->Account Number (table)
//            ->Issuer (table)
//            -> Account Number (table) (Certificate Number)
                List<String> pdfAddressDetails = pdfPages.get(pdfPage);

                pdfTextStripper.setStartPage(pdfPage);
                pdfTextStripper.setEndPage(pdfPage);
                pdfData = pdfTextStripper.getText(pdfDocument);
                int addressPosition = 0;
                pdfPages.get(pdfPage);
                //Validation Primary Customer Name from Intralink to PDF
                primaryCustomerNameFlag = details.get("CustomerName").trim().equals(pdfAddressDetails.get(addressPosition).trim());
                if (primaryCustomerNameFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Primary Customer is as expected Actual:- " + pdfAddressDetails.get(addressPosition) + "Expected:- " + details.get("CustomerName"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer is not as expected Actual:- " + pdfAddressDetails.get(addressPosition) + "Expected:- " + details.get("CustomerName"));
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNamePDF", pdfAddressDetails.get(addressPosition));
                //Validation Letter Date from Intralink to PDF
                letterDateFlag = isContains(details.get("ASATDATE").replace("Le", ""), pdfData);
                if (letterDateFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Letter Date in Page 1 is as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE").replace("Le", ""));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Letter Date in Page 1 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE").replace("Le", ""));
                }
                //Validation Letter Date from Intralink to PDF
                stampedDateFlag = isContains(details.get("MAILDATE"), pdfData);
                if (stampedDateFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Stamped date On or before in Page 1 is as expected Actual:- " + pdfData + "Expected:- " + details.get("MAILDATE"));
                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Stamped date On or before in Page 1 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("MAILDATE"));
                }
                //Validation Joint One Customer from Intralink to PDF
                if (!details.get("Joint1_CustomerName").isEmpty()) {
                    String joint1CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointOneCustomerFlag = details.get("Joint1_CustomerName").trim().equals(joint1CustomerName.trim());
                    if (jointOneCustomerFlag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Joint One Customer is as expected Actual:- " + joint1CustomerName + "Expected:- " + details.get("Joint1_CustomerName"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint One Customer is not as expected Actual:- " + joint1CustomerName + "Expected:- " + details.get("Joint1_CustomerName"));
                    }
                } else {
                    jointOneCustomerFlag = true;

                }
                //Validation Joint Two Customer from Intralink to PDF
                if (!details.get("Joint2_CustomerName").isEmpty()) {
                    String joint2CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointTwoCustomerFlag = details.get("Joint2_CustomerName").trim().equals(joint2CustomerName.trim());
                    if (jointTwoCustomerFlag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Joint Two Customer is as expected Actual:- " + joint2CustomerName + "Expected:- " + details.get("Joint2_CustomerName"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint Two Customer is not as expected Actual:- " + joint2CustomerName + "Expected:- " + details.get("Joint2_CustomerName"));
                    }
                } else {
                    jointTwoCustomerFlag = true;
                }
                //Validation Joint Three Customer from Intralink to PDF
                if (!details.get("Joint3_CustomerName").isEmpty()) {
                    String joint3CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointThreeCustomerFlag = details.get("Joint3_CustomerName").equals(joint3CustomerName);
                    if (jointThreeCustomerFlag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Joint Three Customer is as expected Actual:- " + joint3CustomerName + "Expected:- " + details.get("Joint3_CustomerName"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint Three Customer is not as expected Actual:- " + joint3CustomerName + "Expected:- " + details.get("Joint3_CustomerName"));
                    }
                } else {
                    jointThreeCustomerFlag = true;
                }
                String pdfAddressLine1 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                addressLine1Flag = details.get("AddressLine1_IntraLink").trim().equals(pdfAddressLine1.trim());
                //Validation Address Line 1 Customer from Intralink to PDF
                if (addressLine1Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 1 is as expected Actual:- " + pdfAddressLine1 + "Expected:- " + details.get("AddressLine1_IntraLink"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 1 is not as expected Actual:- " + pdfAddressLine1 + "Expected:- " + details.get("AddressLine1_IntraLink"));
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_PDF", pdfAddressLine1);
                if (!details.get("AddressLine2_IntraLink").isEmpty()) {
                    String pdfAddressLine2 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    addressLine2Flag = details.get("AddressLine2_IntraLink").trim().equals(pdfAddressLine2.trim());
                    //Validation Address Line 2 Customer from Intralink to PDF
                    if (addressLine2Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 2 is as expected Actual:- " + pdfAddressLine2 + "Expected:- " + details.get("AddressLine2_IntraLink"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 2 is not as expected Actual:- " + pdfAddressLine2 + "Expected:- " + details.get("AddressLine2_IntraLink"));
                    }
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_PDF", pdfAddressLine2);
                } else {
                    addressLine2Flag = true;
                }
                //Validation Address Line 3(City) Customer from Intralink to PDF
                countryCodes = readFromExcel.getSaveValue(System.getProperty("user.dir") + "/src/main/resources/data/", "CountryCodes.xlsx", "CountryCodes", details.get("State"));
                countryCode = countryCodes.getField("NameValue").trim();
                addressLine3Flag = countryCode.equals("") ? isContains(details.get("City_IntraLink").trim(), pdfData) : isContains(details.get("City_IntraLink").trim() + ", " + countryCode, pdfData);
                String pdfAddressLine3 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                if (addressLine3Flag) {

                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 3/City is as expected Actual:- " + pdfAddressLine3 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 3/City is not as expected Actual:- " + pdfAddressLine3 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_PDF", pdfAddressLine3);
                //readFromExcel.saveColumnValueToSpecificScenarioSheetName(TestdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", extractAccounts.get(position).get("ADDRESS1"));
                if (!details.get("PostalCode_IntraLink").isEmpty()) {
//                    String pdfAddressLine4 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
//                    addressLine4Flag = isContains(details.get("PostalCode_IntraLink"), pdfData);
                    addressLine4Flag = pdfData.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    //Validation Address Line 4(Postal Code) Customer from Intralink to PDF
                    if (addressLine4Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 4/Postal Code is as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 4/Postal Code is not as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    }
                } else {
                    addressLine4Flag = true;
                }
                // End of Address validation for IP

                // Retrieves IP account details from PDF
                accountDetails = getIPAccountDetails(pdfData);
                LogHelper.logger.info("Customer Name:- " + details.get("CustomerName") + " and Scenarios ID:- " + details.get("getScenarioId()") + " Account details are:- " + accountDetails.stream().collect(Collectors.joining(",")));
                String accountNumber = "RP N° " + StringUtils.leftPad(letterDetails.get("AccountNumber").replace(" ", ""), 15, "0").replace(" ", "").trim();
                int accountNumbersCount = accountDetails.stream().map(x -> x.split("\\s\\s")[1].replaceAll("\\r", "")).collect(Collectors.toList()).
                        stream().filter(x -> accountNumber.equals(x)).collect(Collectors.toList()).size();
                // It check whether intralink account present in PDF as well as only 1 account should be present instead of duplication
                if (accountNumbersCount == 1) {
                    if (details.get("Trust_Accounts").equals("YES")) {
                        accountNumberFlag = true;
                        if (accountNumberFlag) {
                            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Account Number is as expected Actual:- " + accountDetails.stream().map(x -> x.replaceAll("\\D+", "")).collect(Collectors.toList()).
                                    stream().filter(x -> details.get("AccountNumber").equals(x)).collect(Collectors.toList()) + "Expected:- " + details.get("AccountNumber"));

                        } else {
                            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Number is not as expected Actual:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                    "Expected:- " + details.get("AccountNumber"));
                        }
                    }

                    // Verifiying Dealar value by concatinating with account number
                    String pdfDelar = accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(accountNumber)).collect(Collectors.joining(",")).trim();
                    String pdfDealarValue = null;
                    if (details.get("ProductName").contains(" - PSI")) {
                        delarFlag = pdfDelar.split(" ")[0].equals("BNE");

                    } else {
                        delarFlag = pdfDelar.split(" ")[0].equals(details.get("Delar_Intralink"));
                        pdfDealarValue = pdfDelar.split(" ")[0];
                    }


                    // Verifying Delar value
                    if (delarFlag) {
                        pdfDealarValue = "BNE";
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Delar is as expected Actual:- " + pdfDelar.split(" ")[0] + "Expected:- " + details.get("Delar_Intralink"));
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_PDF", pdfDealarValue);

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Delar is not as expected Actual:- " + pdfDelar.split(" ")[0] + "Expected:- " + details.get("Delar_Intralink"));
                        readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_PDF", pdfDealarValue);
                    }
                } else if (details.get("Trust_Accounts").equals("NO")) {
                    accountNumberFlag = true;
                    delarFlag = true;
                    executionStatus.add("Pass:- Account Numbers is not Present in PDF as Trust is NO");

                }
                // If account number is more than 1 time in PDF then it comes to below condition
                else if (accountNumbersCount > 1) {
                    accountNumberFlag = false;
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Numbers and Issuers are present multiple times in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                            "Expected Account Number:- " + details.get("AccountNumber") + " " + "Issuer " + details.get("Delar_Intralink"));
                    delarFlag = false;

                }
                // If account number is not present in PDF then it comes to below condition
                else if (accountNumbersCount == 0) {

                    if (details.get("Trust_Accounts").equalsIgnoreCase("NO")
                            || !details.get("AccountStatus_Intralink").equalsIgnoreCase("Closed")) {
                        accountNumberFlag = true;
                        delarFlag = true;
                    } else {
                        accountNumberFlag = false;
                        delarFlag = false;
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Numbers and Issuers are not present in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                "Expected Account Number:- " + details.get("AccountNumber") + " " + "Issuer " + details.get("Delar_Intralink"));


                    }


                }
                // Saves all GIC's from Excel to gics List object
                List<String> gics = new ArrayList<String>(Arrays.asList(details.get("GIC_IntraLink").split("\\|"))).stream().map(x -> x.replace("CPG#", "CPG N° ")).collect(Collectors.toList());

                LogHelper.logger.info("Intralin GIC's are :-" + gics.stream().collect(Collectors.joining(",")));

                // Saves all Issuers from Excel to issuers List object
                ArrayList<String> issuers = new ArrayList<String>(Arrays.asList(details.get("Issuer_Intralink").split("\\|")));
                LogHelper.logger.info("Intralin GIC's are :-" + issuers.stream().collect(Collectors.joining(",")));
                // Object created for GIC's
                Map<String, Boolean> gicsFlag = new HashMap<String, Boolean>();
                // Object created for Issuers
                List<String> issuersPdf = new LinkedList<>();
                // Verifying all IP Account having GIC then it will goes to below condition
                if (!details.get("GIC_IntraLink").equals("") && gics.size() >= 1) {

                    for (int gic = 0; gic < gics.size(); gic++) {

                        int finalGic = gic;
                        // Verifying all IP Accounts GIC's are present in PDF
                        List<String> certificateNumber = accountDetails.stream().map(x -> x.split("\\s\\s")[1].replaceAll("\\r", "")).collect(Collectors.toList()).
                                stream().filter(x -> gics.get(finalGic).trim().equals(x)).collect(Collectors.toList());
                        // It check whether intralink IP account GIC is present in PDF as well as only individual GIC should be present instead of duplication
                        if (certificateNumber.size() == 1) {
                            if (details.get("Trust_Accounts").equals("YES")) {
                                String gicValue = certificateNumber.get(0);
                                gicsFlag.put(gicValue, true);
                                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  GIC Number is as expected Actual:- " + gicValue + "Expected:- " + gics.get(finalGic));
                                // It check whether intralink IP account GIC Issuer is present in PDF as well as only individual GIC Issuer should be present instead of duplication
                                List<String> issuerValue = accountDetails.stream().filter(x -> x.replaceAll("\\r", "").equals(issuers.get(finalGic) + "  " + gics.get(finalGic)))
                                        .collect(Collectors.toList());
                                String issuerPdfValue = accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(gics.get(finalGic)))
                                        .collect(Collectors.toList()).size() == 1 ? accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(gics.get(finalGic)))
                                        .collect(Collectors.joining("")).split(" ")[0] : accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(gics.get(finalGic))).collect(Collectors.joining(","));

                                if (issuerValue.size() != 0) {
                                    issuersPdf.add(issuerPdfValue + " true");

                                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Issuer is as expected Actual:- " + accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(gics.get(finalGic)))
                                            .collect(Collectors.toList()) + "Expected:- " + issuers.get(finalGic) + "  " + gics.get(finalGic));
                                } else {
                                    issuersPdf.add(issuerPdfValue + " false");
                                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Issuer is not as expected Actual:- " + accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(gics.get(finalGic)))
                                            .collect(Collectors.toList()) + "Expected:- " + issuers.get(finalGic) + "  " + gics.get(finalGic));
                                }
                            } else if (details.get("Trust_Accounts").equals("NO")) {
                                gicsFlag.put(gics.get(finalGic), false);
                                issuersPdf.add("| false");
                                executionStatus.add("GIC's and Issuers are Present in PDF even Trust is NO");
                            }

                        }
                        // If account number is more than 1 time in PDF then it comes to below condition
                        else if (accountDetails.stream().map(x -> x.split("\\s+")[1]).collect(Collectors.toList()).
                                stream().filter(x -> gics.get(finalGic).equals(x)).collect(Collectors.toList()).size() > 1) {
                            gicsFlag.put(gics.get(finalGic), false);
                            issuersPdf.add("| false");
                            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "GIC's and Issuers are present multiple times  in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                    "Expected Account Number:- " + details.get("AccountNumber") + " " + "Issuer " + issuers.get(finalGic) + "  " + gics.get(finalGic));
                        }
                        // If account number is not present in PDF then it comes to below condition
                        else if (accountDetails.stream().map(x -> x.split("\\s+")[1]).collect(Collectors.toList()).
                                stream().filter(x -> gics.get(finalGic).equals(x)).collect(Collectors.toList()).size() == 0) {
                            gicsFlag.put(gics.get(finalGic), false);
                            issuersPdf.add("| false");
                            executionStatus.add(gics.get(gic) + " " + issuers.get(gic));
                            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "GIC's and Issuers are not present in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                    "Expected:- " + issuers.get(finalGic) + "  " + gics.get(finalGic));


                        }


                    }

                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "GIC_PDF", accountDetails.stream().filter(x -> x.contains("CPG N° ")).collect(Collectors.toList()).
                            stream().map(x -> x.split("\\s\\s")[1]).collect(Collectors.joining("|")));
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Issuer_PDF", accountDetails.stream().filter(x -> x.contains("CPG N° ")).collect(Collectors.joining("|")));

                    issuerFlag = issuersPdf.stream().filter(x -> x.contains("false")).collect(Collectors.toList()).size() < 1;

                }


                //Verifying Page 2 details
//             ->Date on Page 3 (MAILDATE)
//             ->Primary Customer Name/Sole Customer Name (pages 1, 3)
//            ->Account Address line 1
//            ->Account Address line 2(Address Line 2/City/Address Line 3 + Province/State)
//            ->Account Address line 3(City/Address Line 3 + Province/State)
//            ->Account Address line 4(Postal/Zip Code)

                // Validating French Second page PDF
                pdfTextStripper.setStartPage(pdfPage + 1);
                pdfTextStripper.setEndPage(pdfPage + 1);
                pdfData = pdfTextStripper.getText(pdfDocument);
                List<String> addressDetailsPage2 = getAddressDetails(pdfPage, pdfDocument);
                int addressPosition_PageTwo = 0;
                dearSirMadamBody = pdfData.replaceAll("\r", "").contains(details.get("Page3Body"));
                //Validation Mail date from extract to PDF
                if (dearSirMadamBody) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Page 3 body is as expected Actual:- " + pdfData + "Expected:- " + details.get("Page3Body"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Page 3 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("Page3Body"));
                }
                //Validation ASAT date from extract to PDF
                asatDateFlag = isContains(details.get("ASATDATE"), pdfData);
                if (asatDateFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  ASAT Date is as expected Actual:- " + pdfData + "Expected:- " + "April 30th, 2019");

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "ASAT Date is not as expected Actual:- " + pdfData + "Expected:- " + "April 30th, 2019");
                }
                primaryCustomerName_1Flag = details.get("CustomerName").trim().equals(addressDetailsPage2.get(addressPosition_PageTwo).trim());
                //Validation Primary Customer Name from Intralink to PDF
                if (primaryCustomerName_1Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Primary Customer in Page 2 is as expected Actual:- " + addressDetailsPage2.get(addressPosition_PageTwo) + "Expected:- " + details.get("CustomerNameExtract"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer in Page 2 is not as expected Actual:- " + addressDetailsPage2.get(addressPosition_PageTwo) + "Expected:- " + details.get("CustomerNameExtract"));
                }
                //Validation Address Line 1 from Intralink to PDF
                addressLine1Flag_1Flag = details.get("AddressLine1_IntraLink").trim().equals(addressDetailsPage2.get(addressPosition_PageTwo = addressPosition_PageTwo + 1).trim());
                //Validation Address Line 2 from Intralink to PDF
                if (!details.get("AddressLine2_IntraLink").isEmpty()) {
                    String pdfAddressLine2Page2 = addressDetailsPage2.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
                    addressLine2Flag_1Flag = details.get("AddressLine2_IntraLink").trim().equals(pdfAddressLine2Page2.trim());
                    if (addressLine2Flag_1Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Primary Customer in Page 2 is as expected Actual:- " + pdfAddressLine2Page2 + "Expected:- " + details.get("AddressLine2_IntraLink"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer in Page 2 is not as expected Actual:- " + pdfAddressLine2Page2 + "Expected:- " + details.get("AddressLine2_IntraLink"));
                    }

                } else {
                    addressLine2Flag_1Flag = true;
                }

                //Validation Address Line 3(City) from Intralink to PDF

                countryCodes = readFromExcel.getSaveValue(System.getProperty("user.dir") + "/src/main/resources/data/", "CountryCodes.xlsx", "CountryCodes", details.get("State"));
                countryCode = countryCodes.getField("NameValue").trim();
                addressLine3Flag_1Flag = countryCode.equals("") ? isContains(details.get("City_IntraLink").trim(), pdfData) : isContains(details.get("City_IntraLink").trim() + ", " + countryCode, pdfData);
                String pdfAddressLine3Page2 = pdfAddressDetails.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);

                if (addressLine3Flag_1Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 3/City in Page 2 is as expected Actual:- " + pdfAddressLine3Page2 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 3/City in Page 2 is not as expected Actual:- " + pdfAddressLine3Page2 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);
                }
                //Validation Address Line 4(Postal Code) from Intralink to PDF
                if (!details.get("PostalCode_IntraLink").isEmpty()) {
//                    String pdfAddressLine4Pag2 = pdfAddressDetails.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
                    addressLine4Flag_1Flag = pdfData.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));

                    if (addressLine4Flag_1Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 4/Postal Code is as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 4/Postal Code is not as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    }
                } else {
                    addressLine4Flag_1Flag = true;
                }
                // Verifying Failed Details while comparing Intralink values with PDF
                exectionStatusFailedFields = new HashMap<>();


                exectionStatusFailedFields.put("Primary Customer Name", primaryCustomerNameFlag);
                exectionStatusFailedFields.put("Letter Date", letterDateFlag);
                exectionStatusFailedFields.put("Stamped Date", stampedDateFlag);
                exectionStatusFailedFields.put("Joint One Customer Name", jointOneCustomerFlag);
                exectionStatusFailedFields.put("Joint Two Customer Name", jointTwoCustomerFlag);
                exectionStatusFailedFields.put("Joint Three Customer Name", jointThreeCustomerFlag);
                exectionStatusFailedFields.put("AddressLine1", addressLine1Flag);
                exectionStatusFailedFields.put("AddressLine2", addressLine2Flag);
                exectionStatusFailedFields.put("City", addressLine3Flag);
                exectionStatusFailedFields.put("PostalCode", addressLine4Flag);
                exectionStatusFailedFields.put("AccountNumber", accountNumberFlag);
                exectionStatusFailedFields.put("Dealer", delarFlag);
                exectionStatusFailedFields.put("Issuer", issuersPdf.size() <= 0 || issuerFlag);
                exectionStatusFailedFields.put("GICs", gicsFlag.size() <= 0 || gicsFlag.values().stream().filter(x -> x == false).collect(Collectors.toList()).size() < 1);

                exectionStatusFailedFields.put("Primary Customer Name Page2", primaryCustomerName_1Flag);
                exectionStatusFailedFields.put("Address Line1 Page2", addressLine1Flag_1Flag);
                exectionStatusFailedFields.put("Address Line2 Page2", addressLine2Flag_1Flag);
                exectionStatusFailedFields.put("City Page2", addressLine3Flag_1Flag);
                exectionStatusFailedFields.put("Postal Code Page2", addressLine4Flag_1Flag);
                exectionStatusFailedFields.put("Page 3 Body", dearSirMadamBody);
                exectionStatusFailedFields.put("ASAT Date Page2", asatDateFlag);
                if (exectionStatusFailedFields.entrySet().
                        stream().filter(x -> x.getValue().equals(false)).collect(Collectors.toList()).size() > 0) {
                    executionStatus.add("Fail-> Mismatch Fields:-" + exectionStatusFailedFields.entrySet().
                            stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                            collect(Collectors.joining(",")));
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Intralink and PDF fields are mismatched at " + exectionStatusFailedFields.entrySet().
                            stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                            collect(Collectors.joining(",")));

                } else {
                    executionStatus.add("Pass");
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  All Intralink and PDF Fields are matched");

                }


            }
// Verifying all acoounts(Savings and IP) associated to the customer is present in single pdf or not
            pdfTextStripper.setStartPage(pdfPage);
            pdfTextStripper.setEndPage(pdfPage);
            pdfData = pdfTextStripper.getText(pdfDocument);
            if (!(details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI") || (details.get("ProductName").contains("- BNE") || details.get("ProductName").contains("- PSI")))) {
                accountDetails.addAll(getIPAccountDetails(pdfData).stream().map(x -> x.trim()).collect(Collectors.toList()));
            } else if ((details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI") || (details.get("ProductName").contains("- BNE") || details.get("ProductName").contains("- PSI")))) {
                accountDetails.addAll(getSavingsAccountDetails(pdfData).stream().map(x -> x.trim()).collect(Collectors.toList()));
            }
            // Verifying all acoounts(Savings and IP) associated to the customer is present in single pdf or not
            List<String> accsInSinglePdf = verifyingAllAccountsPresentInSinglePDF(accountDetails);

            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AccountsStatus", accsInSinglePdf.stream().collect(Collectors.joining("|")));


        }
        // If Customer address is present more than once in entire PDF Letter then it enters to below condition
        else if (pdfPages.size() > 1) {

            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Multiple PDF's are generated in Pages-" + verifyCustomerHavingSinglePdf(pdfDocument).values().stream().map(x -> x + ", ").collect(Collectors.toList()) +
                    "for the customer Name:-" + details.get("CustomerName") +
                    "Joint1 Customer:- " + details.get("Joint1_CustomerName") + "<br />" +
                    "Joint2 Customer:- " + details.get("Joint2_CustomerName") + "<br />" +
                    "Joint3 Customer:- " + details.get("Joint3_CustomerName") + "<br />" +
                    "Address Line1:- " + details.get("AddressLine1_IntraLink") + "<br />" +
                    "Address Line2:- " + details.get("AddressLine2_IntraLink") + "<br />" +
                    "City:- " + details.get("AddressLine2_IntraLink") + "<br />" + "<br />" +
                    "Postal Code:-" + details.get("PostalCode_IntraLink"));
            executionStatus.add("Multiple Pdf's are generated in Pages:-" + pdfNumbers.stream().map(x -> x + " ").collect(Collectors.toList()));
        }
// If Customer address is not present in entire PDF Letter then it enters to below condition
        else if (pdfPages.size() == 0) {
            executionStatus = verifyFrenchGenericLtrPdfsDetailsWithExtract();
            if (executionStatus.size() == 0) {
                if (details.get("Trust_Accounts").equalsIgnoreCase("NO")) {
                    executionStatus.add("Pass- PDF is not generated as trust is NO");
                } else if (details.get("Trust_Accounts").equalsIgnoreCase("YES")) {
                    // PDF will not generate when Account status is Closed
                    if (details.get("AccountStatus_Intralink").equalsIgnoreCase("Closed")) {
                        executionStatus.add("Pass- PDF is not generated as it is Closed account");
                    }
                    // Types of accouts which will not generate PDF's
                    else if (details.get("AccountStatus_Intralink").equals("Savings/Chequing")) {
                        executionStatus.add("Pass- PDF is not generated as it's account is " + details.get("AccountStatus_Intralink"));
                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "PDF is not generated for the customer Name:-" + details.get("CustomerName") +
                                "Joint1 Customer:- " + details.get("Joint1_CustomerName") + "<br />" +
                                "Joint2 Customer:- " + details.get("Joint2_CustomerName") + "<br />" +
                                "Joint3 Customer:- " + details.get("Joint3_CustomerName") + "<br />" +
                                "Address Line1:- " + details.get("AddressLine1_IntraLink") + "<br />" +
                                "Address Line2:- " + details.get("AddressLine2_IntraLink") + "<br />" +
                                "City:- " + details.get("AddressLine2_IntraLink") + "<br />" + "<br />" +
                                "Postal Code:-" + details.get("PostalCode_IntraLink"));
                        executionStatus.add("Pdf is not generated with customer details:-" + details.get("CustomerName") + "\n"
                                + details.get("Joint1_CustomerName") + "\n" + details.get("Joint2_CustomerName") + "\n" +
                                details.get("Joint3_CustomerName") + "\n" + details.get("AddressLine1_IntraLink") + "\n" +
                                details.get("AddressLine2_IntraLink") + "\n" + details.get("AddressLine2_IntraLink") + "\n" +
                                details.get("PostalCode_IntraLink"));
                    }

                }


            }

        }

        pdfDocument.close();

        return executionStatus;
    }

    /**
     * This function verifies all Intralink English customer accounts details with PDF
     *
     * @param pdfDocument- PDF Document
     * @return Execution status
     * @throws IOException
     * @throws FilloException
     */
    public List<String> verifyingEnglishPdfDetailsWithExtract(PDDocument pdfDocument) throws IOException, FilloException {
        String pdfData = null;
        String countryCode = null;

        // Page 1 Variables
        boolean primaryCustomerNameFlag = false;
        boolean letterDateFlag = false;
        boolean stampedDateFlag = false;
        boolean jointOneCustomerFlag = false;
        boolean jointTwoCustomerFlag = false;
        boolean jointThreeCustomerFlag = false;
        boolean addressLine1Flag = false;
        boolean addressLine2Flag = false;
        boolean addressLine3Flag = false;
        boolean addressLine4Flag = false;
        boolean accountNumberFlag = false;
        boolean delarFlag = false;
        boolean issuerFlag = false;

        //Page 2 Variables
        boolean dearSirMadamBody = false;
        boolean asatDateFlag = false;
        boolean primaryCustomerName_1Flag = false;
        boolean addressLine1Flag_1Flag = false;
        boolean addressLine2Flag_1Flag = false;
        boolean addressLine3Flag_1Flag = false;
        boolean addressLine4Flag_1Flag = false;
        int pdfCount = 0;
        // Creating object for Execution status
        List<String> executionStatus = new LinkedList<>();
        // Creating object for mismatch fields while comparing with excel value with pdf fields
        Map<String, Boolean> exectionStatusFailedFields = null;
        // pdfPages stores address details and it's PDF Page number
        HashMap<Integer, List<String>> pdfPages = verifyCustomerHavingSinglePdf(pdfDocument);
        Set<Integer> pdfNumbers = pdfPages.keySet();
        int pdfPage = 0;
        if (pdfPages.size() == 1) {
            pdfPage = (Integer) pdfPages.keySet().toArray()[0];
        }

        List<String> accountDetails = null;
        // Customer address should present only once in entire PDF Letter
        if (pdfPages.size() == 1) {
            LogHelper.logger.info("Customer Name:- " + details.get("CustomerName") + " and Scenarios ID:- " + details.get("getScenarioId()") + " " + "is located in PDF Page Number:-" + pdfPage);
            pdfTextStripper = new PDFTextStripper();
            pdfTextStripper.setStartPage(pdfPage);
            pdfTextStripper.setEndPage(pdfPage);
            pdfData = pdfTextStripper.getText(pdfDocument);
            //If account is English Savings then it goes to below condition
            if (!(details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI") || (details.get("ProductName").contains("- BNE") || details.get("ProductName").contains("- PSI") || details.get("ProductName").contains("Momentum PLUS Savings")))) {

                //Verifying Page 1 details
//            Date on Page 3 (ASATDATE)
//            ->Primary Customer Name/Sole Customer Name (pages 1, 3)
//            ->Date on Page 1
//            ->Account Address line 1
//            ->Account Address line 2(Address Line 2/City/Address Line 3 + Province/State)
//            ->Account Address line 3(City/Address Line 3 + Province/State)
//            ->Account Address line 4(Postal/Zip Code)
//            ->Account Number (table)
//            ->Issuer (table)
                List<String> pdfAddressDetails = pdfPages.get(pdfPage);
                //pdfAddressDetails
                int addressPosition = 0;
                pdfPages.get(pdfPage);
                //Validation Primary Customer Name from Intralink to PDF
                primaryCustomerNameFlag = details.get("CustomerName").trim().equals(pdfAddressDetails.get(addressPosition).trim());
                if (primaryCustomerNameFlag) {
                    LogHelper.logger.info("******Pass Statement*****  Primary Customer is as expected Actual:- " + pdfAddressDetails.get(addressPosition) + "Expected:- " + details.get("CustomerName"));

                } else {
                    LogHelper.logger.error("Primary Customer is not as expected Actual:- " + pdfAddressDetails.get(addressPosition) + "Expected:- " + details.get("CustomerName"));
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNamePDF", pdfAddressDetails.get(addressPosition));
                //Validation Letter Date from Intralink to PDF
                letterDateFlag = isContains(details.get("ASATDATE").replace("th", ""), pdfData);
                if (letterDateFlag) {
                    LogHelper.logger.info("******Pass Statement*****  Letter Date in Page 1 is as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE").replace("th", ""));

                } else {
                    LogHelper.logger.error("Letter Date in Page 1 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE").replace("th", ""));
                }
                //Validation Letter Date from Intralink to PDF
                stampedDateFlag = isContains(details.get("MAILDATE").replace(", 2020", "th"), pdfData);
                if (stampedDateFlag) {
                    LogHelper.logger.info("******Pass Statement*****  Stamped date On or before in Page 1 is as expected Actual:- " + pdfData + "Expected:- " + details.get("MAILDATE").replace(", 2020", "th"));

                } else {
                    LogHelper.logger.error("Stamped date On or before in Page 1 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("MAILDATE").replace(", 2020", "th"));
                }


                //Validation Joint One Customer from Intralink to PDF
                if (!details.get("Joint1_CustomerName").isEmpty()) {
                    String joint1CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointOneCustomerFlag = details.get("Joint1_CustomerName").trim().equals(joint1CustomerName.trim());
                    if (jointOneCustomerFlag) {
                        LogHelper.logger.info("******Pass Statement*****  Joint One Customer is as expected Actual:- " + joint1CustomerName + "Expected:- " + details.get("Joint1_CustomerName"));

                    } else {
                        LogHelper.logger.error("Joint One Customer is not as expected Actual:- " + joint1CustomerName + "Expected:- " + details.get("Joint1_CustomerName"));
                    }
                } else {
                    jointOneCustomerFlag = true;

                }
                //Validation Joint Two Customer from Intralink to PDF
                if (!details.get("Joint2_CustomerName").isEmpty()) {
                    String joint2CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointTwoCustomerFlag = details.get("Joint2_CustomerName").trim().equals(joint2CustomerName.trim());
                    if (jointTwoCustomerFlag) {
                        LogHelper.logger.info("******Pass Statement*****  Joint Two Customer is as expected Actual:- " + joint2CustomerName + "Expected:- " + details.get("Joint2_CustomerName"));

                    } else {
                        LogHelper.logger.error("Joint Two Customer is not as expected Actual:- " + joint2CustomerName + "Expected:- " + details.get("Joint2_CustomerName"));
                    }
                } else {
                    jointTwoCustomerFlag = true;
                }
                //Validation Joint Three Customer from Intralink to PDF
                if (!details.get("Joint3_CustomerName").isEmpty()) {
                    String joint3CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointThreeCustomerFlag = details.get("Joint3_CustomerName").trim().equals(joint3CustomerName.trim());
                    if (jointThreeCustomerFlag) {
                        LogHelper.logger.info("******Pass Statement*****  Joint Three Customer is as expected Actual:- " + joint3CustomerName + "Expected:- " + details.get("Joint3_CustomerName"));

                    } else {
                        LogHelper.logger.error("Joint Three Customer is not as expected Actual:- " + joint3CustomerName + "Expected:- " + details.get("Joint3_CustomerName"));
                    }
                } else {
                    jointThreeCustomerFlag = true;
                }
                String pdfAddressLine1 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                addressLine1Flag = details.get("AddressLine1_IntraLink").trim().equals(pdfAddressLine1.trim());
                //Validation Address Line 1 Customer from Intralink to PDF
                if (addressLine1Flag) {
                    LogHelper.logger.info("******Pass Statement*****  Address Line 1 is as expected Actual:- " + pdfAddressLine1 + "Expected:- " + details.get("AddressLine1_IntraLink"));

                } else {
                    LogHelper.logger.error("Address Line 1 is not as expected Actual:- " + pdfAddressLine1 + "Expected:- " + details.get("AddressLine1_IntraLink"));
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_PDF", pdfAddressLine1);
                if (!details.get("AddressLine2_IntraLink").isEmpty()) {
                    String pdfAddressLine2 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    addressLine2Flag = details.get("AddressLine2_IntraLink").trim().equals(pdfAddressLine2.trim());
                    //Validation Address Line 2 Customer from Intralink to PDF
                    if (addressLine2Flag) {
                        LogHelper.logger.info("******Pass Statement*****  Address Line 2 is as expected Actual:- " + pdfAddressLine2 + "Expected:- " + details.get("AddressLine2_IntraLink"));

                    } else {
                        LogHelper.logger.error("Address Line 2 is not as expected Actual:- " + pdfAddressLine2 + "Expected:- " + details.get("AddressLine2_IntraLink"));
                    }
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_PDF", pdfAddressLine2);
                } else {
                    addressLine2Flag = true;
                }
                //Validation Address Line 3(City) Customer from Intralink to PDF
                countryCodes = readFromExcel.getSaveValue(System.getProperty("user.dir") + "/src/main/resources/data/", "CountryCodes.xlsx", "CountryCodes", details.get("State"));
                countryCode = countryCodes.getField("NameValue").trim();
                addressLine3Flag = countryCode.equals("") ? isContains(details.get("City_IntraLink").trim(), pdfData) : isContains(details.get("City_IntraLink").trim() + ", " + countryCode, pdfData);
                String pdfAddressLine3 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                if (addressLine3Flag) {
                    LogHelper.logger.info("******Pass Statement*****  Address Line 3/City is as expected Actual:- " + pdfAddressLine3 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);

                } else {
                    LogHelper.logger.error("Address Line 3/City is not as expected Actual:- " + pdfAddressLine3 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_PDF", pdfAddressLine3);
                //readFromExcel.saveColumnValueToSpecificScenarioSheetName(TestdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", extractAccounts.get(position).get("ADDRESS1"));
                if (!details.get("PostalCode_IntraLink").isEmpty()) {

                    //String pdfAddressLine4 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
//                    addressLine4Flag = isContains(details.get("PostalCode_IntraLink"), pdfData);
                    addressLine4Flag = pdfData.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    //Validation Address Line 4(Postal Code) Customer from Intralink to PDF
                    if (addressLine4Flag) {
                        LogHelper.logger.info("******Pass Statement*****  Address Line 4/Postal Code is as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));

                    } else {
                        LogHelper.logger.error("Address Line 4/Postal Code is not as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    }

                } else {
                    addressLine4Flag = true;
                }
                // Retrieves savings account details from PDF
                accountDetails = getSavingsAccountDetails(pdfData);
                LogHelper.logger.info("Customer Name:- " + details.get("CustomerName") + " and Scenarios ID:- " + details.get("getScenarioId()") + " Account details are:- " + accountDetails.stream().collect(Collectors.joining(",")));
                int accountNumbersCount = accountDetails.stream().map(x -> x.replaceAll("\\D+", "")).collect(Collectors.toList()).
                        stream().filter(x -> details.get("AccountNumber").equals(x)).collect(Collectors.toList()).size();
                // It check whether intralink account present in PDF as well as only 1 account should be present instead of duplication
                if (accountNumbersCount == 1) {
                    if (details.get("Trust_Accounts").equals("YES")) {
                        accountNumberFlag = true;
                        if (accountNumberFlag) {
                            LogHelper.logger.info("******Pass Statement*****  Account Number is as expected Actual:- " + accountDetails.stream().map(x -> x.replaceAll("\\D+", "")).collect(Collectors.toList()).
                                    stream().filter(x -> details.get("AccountNumber").equals(x)).collect(Collectors.toList()) + "Expected:- " + details.get("AccountNumber"));

                        } else {
                            LogHelper.logger.error("Account Number is not as expected Actual:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                    "Expected:- " + details.get("AccountNumber"));
                        }
                        // Verifiying Dealar value by concatinating with account number
                        List<String> pdfDelar = accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(details.get("AccountNumber").
                                        trim().substring(0, 5) + " " + details.get("AccountNumber").
                                        trim().substring(5, 10) + " " + details.get("AccountNumber").trim().substring(10, 12))).
                                collect(Collectors.toList());
                        if (pdfDelar.size() != 0) {
                            String pdfDelarValue = Arrays.asList(pdfDelar.get(0).split("  ")).get(0);

                            delarFlag = pdfDelarValue.equals(details.get("Delar_Intralink"));
                            // Verifying Delar value
                            if (delarFlag) {

                                LogHelper.logger.info("******Pass Statement*****  Delar is as expected Actual:- " + pdfDelarValue + " Expected:- " + details.get("Delar_Intralink"));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_PDF", pdfDelarValue);

                            } else {
                                LogHelper.logger.error("Delar is not as expected Actual:- " + pdfDelarValue + " Expected:- " + details.get("Delar_Intralink"));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_PDF", pdfDelarValue);
                            }
                        }
                    } else if (details.get("Trust_Accounts").equals("NO")) {
                        executionStatus.add("Fail:-Account Numbers is Present in PDF even Trust is NO");
                    }

                }
                // If account number is more than 1 time in PDF then it comes to below condition
                else if (accountNumbersCount > 1) {
                    accountNumberFlag = false;
                    LogHelper.logger.error("Account Numbers and Delars are present multiple times in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                            "Expected Account Number:- " + details.get("AccountNumber") + " " + "Delar " + details.get("Delar_Intralink"));
                    delarFlag = false;
                    executionStatus.add("Account Numbers is repeated");

                }
                // If account number is not present in PDF then it comes to below condition
                else if (accountNumbersCount == 0) {
                    if (details.get("Trust_Accounts").equalsIgnoreCase("NO") || details.get("AccountStatus_Intralink").equalsIgnoreCase("Closed")) {
                        accountNumberFlag = true;
                        delarFlag = true;
                    } else {
                        accountNumberFlag = false;
                        LogHelper.logger.error("Account Numbers and Delars are not present in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                "Expected Account Number:- " + details.get("AccountNumber") + " " + "Delar " + details.get("Delar_Intralink"));
                        delarFlag = false;
                    }

                }


                //Verifying Page 2 details
//             ->Date on Page 3 (MAILDATE)
//             ->Primary Customer Name/Sole Customer Name (pages 1, 3)
//            ->Account Address line 1
//            ->Account Address line 2(Address Line 2/City/Address Line 3 + Province/State)
//            ->Account Address line 3(City/Address Line 3 + Province/State)
//            ->Account Address line 4(Postal/Zip Code)

                // Validating English Second page PDF
                pdfTextStripper.setStartPage(pdfPage + 1);
                pdfTextStripper.setEndPage(pdfPage + 1);
                pdfData = pdfTextStripper.getText(pdfDocument);
                List<String> addressDetailsPage2 = getAddressDetails(pdfPage, pdfDocument);
                int addressPosition_PageTwo = 0;
                dearSirMadamBody = pdfData.replaceAll("\r", "").contains(details.get("Page3Body"));
                //Validation Mail date from extract to PDF
                if (dearSirMadamBody) {
                    LogHelper.logger.info("******Pass Statement*****  Page 3 body is as expected Actual:- " + pdfData + "Expected:- " + details.get("Page3Body"));

                } else {
                    LogHelper.logger.error("Page 3 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("Page3Body"));
                }
                //Validation ASAT date from extract to PDF
                asatDateFlag = isContains(details.get("ASATDATE"), pdfData);
                if (asatDateFlag) {
                    LogHelper.logger.info("******Pass Statement*****  ASAT Date is as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE"));

                } else {
                    LogHelper.logger.error("ASAT Date is not as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE"));
                }

                primaryCustomerName_1Flag = details.get("CustomerName").trim().equals(addressDetailsPage2.get(addressPosition_PageTwo).trim());
                //Validation Primary Customer Name from Intralink to PDF
                if (primaryCustomerName_1Flag) {
                    LogHelper.logger.info("******Pass Statement*****  Primary Customer in Page 2 is as expected Actual:- " + addressDetailsPage2.get(addressPosition_PageTwo) + "Expected:- " + details.get("CustomerName"));

                } else {
                    LogHelper.logger.error("Primary Customer in Page 2 is not as expected Actual:- " + addressDetailsPage2.get(addressPosition_PageTwo) + "Expected:- " + details.get("CustomerName"));
                }
                //Validation Address Line 1 from Intralink to PDF
                addressLine1Flag_1Flag = details.get("AddressLine1_IntraLink").trim().equals(addressDetailsPage2.get(addressPosition_PageTwo = addressPosition_PageTwo + 1).trim());
                //Validation Address Line 2 from Intralink to PDF
                if (!details.get("AddressLine2_IntraLink").isEmpty()) {
                    String pdfAddressLine2Page2 = addressDetailsPage2.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
                    addressLine2Flag_1Flag = details.get("AddressLine2_IntraLink").trim().equals(pdfAddressLine2Page2.trim());
                    if (addressLine2Flag_1Flag) {
                        LogHelper.logger.info("******Pass Statement*****  Primary Customer in Page 2 is as expected Actual:- " + pdfAddressLine2Page2 + "Expected:- " + details.get("AddressLine2_IntraLink"));

                    } else {
                        LogHelper.logger.error("Primary Customer in Page 2 is not as expected Actual:- " + pdfAddressLine2Page2 + "Expected:- " + details.get("AddressLine2_IntraLink"));
                    }

                } else {
                    addressLine2Flag_1Flag = true;
                }

                //Validation Address Line 3(City) from Intralink to PDF

                countryCodes = readFromExcel.getSaveValue(System.getProperty("user.dir") + "/src/main/resources/data/", "CountryCodes.xlsx", "CountryCodes", details.get("State"));
                countryCode = countryCodes.getField("NameValue").trim();
                addressLine3Flag_1Flag = countryCode.equals("") ? isContains(details.get("City_IntraLink").trim(), pdfData) : isContains(details.get("City_IntraLink").trim() + ", " + countryCode, pdfData);

                String pdfAddressLine3Page2 = pdfAddressDetails.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
                if (addressLine3Flag_1Flag) {
                    LogHelper.logger.info("******Pass Statement*****  Address Line 3/City in Page 2 is as expected Actual:- " + pdfData + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);


                } else {
                    LogHelper.logger.error("Address Line 3/City in Page 2 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);
                }
                //Validation Address Line 4(Postal Code) from Intralink to PDF
                if (!details.get("PostalCode_IntraLink").isEmpty()) {
                    //String pdfAddressLine4Pag2 = pdfAddressDetails.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
//                    addressLine4Flag_1Flag = isContains(details.get("PostalCode_IntraLink"), pdfData);
                    addressLine4Flag_1Flag = pdfData.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    if (addressLine4Flag) {
                        LogHelper.logger.info("******Pass Statement*****  Address Line 4/Postal Code is as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink"));

                    } else {
                        LogHelper.logger.error("Address Line 4/Postal Code is not as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink"));
                    }

                } else {
                    addressLine4Flag_1Flag = true;
                }

                // Verifying Failed Details while comparing Intralink values with PDF
                exectionStatusFailedFields = new HashMap<>();


                exectionStatusFailedFields.put("Primary Customer Name", primaryCustomerNameFlag);
                exectionStatusFailedFields.put("Letter Date", letterDateFlag);
                exectionStatusFailedFields.put("Stamped Date", stampedDateFlag);

                exectionStatusFailedFields.put("Joint One Customer Name", jointOneCustomerFlag);
                exectionStatusFailedFields.put("Joint Two Customer Name", jointTwoCustomerFlag);
                exectionStatusFailedFields.put("Joint Three Customer Name", jointThreeCustomerFlag);
                exectionStatusFailedFields.put("AddressLine1", addressLine1Flag);
                exectionStatusFailedFields.put("AddressLine2", addressLine2Flag);
                exectionStatusFailedFields.put("City", addressLine3Flag);
                exectionStatusFailedFields.put("PostalCode", addressLine4Flag);
                exectionStatusFailedFields.put("AccountNumber", accountNumberFlag);
                exectionStatusFailedFields.put("Dealer", delarFlag);

                exectionStatusFailedFields.put("Primary Customer Name Page2", primaryCustomerName_1Flag);
                exectionStatusFailedFields.put("Address Line1 Page2", addressLine1Flag_1Flag);
                exectionStatusFailedFields.put("Address Line2 Page2", addressLine2Flag_1Flag);
                exectionStatusFailedFields.put("City Page2", addressLine3Flag_1Flag);
                exectionStatusFailedFields.put("Postal Code Page2", addressLine4Flag_1Flag);
                exectionStatusFailedFields.put("Page3 Body", dearSirMadamBody);
                exectionStatusFailedFields.put("ASAT Date Page2", asatDateFlag);
                if (exectionStatusFailedFields.entrySet().
                        stream().filter(x -> x.getValue().equals(false)).collect(Collectors.toList()).size() > 0) {
                    executionStatus.add("Fail-> Mismatch Fields:-" + exectionStatusFailedFields.entrySet().
                            stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                            collect(Collectors.joining(",")));
                    LogHelper.logger.error("Intralink and PDF fields are mismatched at " + exectionStatusFailedFields.entrySet().
                            stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                            collect(Collectors.joining(",")));
                } else {
                    executionStatus.add("Pass");
                    LogHelper.logger.info("******Pass Statement*****  All Intralink and PDF Fields are matched");

                }


            }
            //If account is French IP Account then it goes to below condition
            else if (details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI") || details.get("ProductName").contains("- BNE") || details.get("ProductName").contains("- PSI") || details.get("ProductName").contains("Momentum PLUS Savings")) {


                //Verifying Page 1 details
//            Date on Page 3 (ASATDATE)
//            ->Primary Customer Name/Sole Customer Name (pages 1, 3)
//            ->Date on Page 1
//            ->Account Address line 1
//            ->Account Address line 2(Address Line 2/City/Address Line 3 + Province/State)
//            ->Account Address line 3(City/Address Line 3 + Province/State)
//            ->Account Address line 4(Postal/Zip Code)
//            ->Account Number (table)
//            ->Issuer (table)
//            -> Account Number (table) (Certificate Number)
                List<String> pdfAddressDetails = pdfPages.get(pdfPage);

                pdfTextStripper.setStartPage(pdfPage);
                pdfTextStripper.setEndPage(pdfPage);
                pdfData = pdfTextStripper.getText(pdfDocument);
                int addressPosition = 0;
                pdfPages.get(pdfPage);
                //Validation Primary Customer Name from Intralink to PDF
                primaryCustomerNameFlag = details.get("CustomerName").trim().equals(pdfAddressDetails.get(addressPosition).trim());
                if (primaryCustomerNameFlag) {
                    LogHelper.logger.info("******Pass Statement*****  Primary Customer is as expected Actual:- " + pdfAddressDetails.get(addressPosition) + "Expected:- " + details.get("CustomerName"));

                } else {
                    LogHelper.logger.error("Primary Customer is not as expected Actual:- " + pdfAddressDetails.get(addressPosition) + "Expected:- " + details.get("CustomerName"));
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNamePDF", pdfAddressDetails.get(addressPosition));
                //Validation Letter Date from Intralink to PDF
                letterDateFlag = isContains(details.get("ASATDATE").replace("th", ""), pdfData);
                if (letterDateFlag) {
                    LogHelper.logger.info("******Pass Statement*****  Letter Date in Page 1 is as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE").replace("th", ""));

                } else {
                    LogHelper.logger.error("Letter Date in Page 1 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE").replace("th", ""));
                }
                //Validation Letter Date from Intralink to PDF
                stampedDateFlag = isContains(details.get("MAILDATE").replace(", 2020", "th"), pdfData);
                if (stampedDateFlag) {
                    LogHelper.logger.info("******Pass Statement*****  Stamped date On or before in Page 1 is as expected Actual:- " + pdfData + "Expected:- " + details.get("MAILDATE").replace(", 2020", "th"));

                } else {
                    LogHelper.logger.error("Stamped date On or before in Page 1 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("MAILDATE").replace(", 2020", "th"));
                }
                //Validation Joint One Customer from Intralink to PDF
                if (!details.get("Joint1_CustomerName").isEmpty()) {
                    String joint1CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointOneCustomerFlag = details.get("Joint1_CustomerName").trim().equals(joint1CustomerName.trim());
                    if (jointOneCustomerFlag) {
                        LogHelper.logger.info("******Pass Statement*****  Joint One Customer is as expected Actual:- " + joint1CustomerName + "Expected:- " + details.get("Joint1_CustomerName"));

                    } else {
                        LogHelper.logger.error("Joint One Customer is not as expected Actual:- " + joint1CustomerName + "Expected:- " + details.get("Joint1_CustomerName"));
                    }
                } else {
                    jointOneCustomerFlag = true;

                }
                //Validation Joint Two Customer from Intralink to PDF
                if (!details.get("Joint2_CustomerName").isEmpty()) {
                    String joint2CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointTwoCustomerFlag = details.get("Joint2_CustomerName").trim().equals(joint2CustomerName.trim());
                    if (jointTwoCustomerFlag) {
                        LogHelper.logger.info("******Pass Statement*****  Joint Two Customer is as expected Actual:- " + joint2CustomerName + "Expected:- " + details.get("Joint2_CustomerName"));

                    } else {
                        LogHelper.logger.error("Joint Two Customer is not as expected Actual:- " + joint2CustomerName + "Expected:- " + details.get("Joint2_CustomerName"));
                    }
                } else {
                    jointTwoCustomerFlag = true;
                }
                //Validation Joint Three Customer from Intralink to PDF
                if (!details.get("Joint3_CustomerName").isEmpty()) {
                    String joint3CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointThreeCustomerFlag = details.get("Joint3_CustomerName").equals(joint3CustomerName);
                    if (jointThreeCustomerFlag) {
                        LogHelper.logger.info("******Pass Statement*****  Joint Three Customer is as expected Actual:- " + joint3CustomerName + "Expected:- " + details.get("Joint3_CustomerName"));

                    } else {
                        LogHelper.logger.error("Joint Three Customer is not as expected Actual:- " + joint3CustomerName + "Expected:- " + details.get("Joint3_CustomerName"));
                    }
                } else {
                    jointThreeCustomerFlag = true;
                }
                String pdfAddressLine1 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                addressLine1Flag = details.get("AddressLine1_IntraLink").trim().equals(pdfAddressLine1.trim());
                //Validation Address Line 1 Customer from Intralink to PDF
                if (addressLine1Flag) {
                    LogHelper.logger.info("******Pass Statement*****  Address Line 1 is as expected Actual:- " + pdfAddressLine1 + "Expected:- " + details.get("AddressLine1_IntraLink"));

                } else {
                    LogHelper.logger.error("Address Line 1 is not as expected Actual:- " + pdfAddressLine1 + "Expected:- " + details.get("AddressLine1_IntraLink"));
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_PDF", pdfAddressLine1);
                if (!details.get("AddressLine2_IntraLink").isEmpty()) {
                    String pdfAddressLine2 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    addressLine2Flag = details.get("AddressLine2_IntraLink").trim().equals(pdfAddressLine2.trim());
                    //Validation Address Line 2 Customer from Intralink to PDF
                    if (addressLine2Flag) {
                        LogHelper.logger.info("******Pass Statement*****  Address Line 2 is as expected Actual:- " + pdfAddressLine2 + "Expected:- " + details.get("AddressLine2_IntraLink"));

                    } else {
                        LogHelper.logger.error("Address Line 2 is not as expected Actual:- " + pdfAddressLine2 + "Expected:- " + details.get("AddressLine2_IntraLink"));
                    }
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_PDF", pdfAddressLine2);
                } else {
                    addressLine2Flag = true;
                }
                //Validation Address Line 3(City) Customer from Intralink to PDF
                countryCodes = readFromExcel.getSaveValue(System.getProperty("user.dir") + "/src/main/resources/data/", "CountryCodes.xlsx", "CountryCodes", details.get("State"));
                countryCode = countryCodes.getField("NameValue").trim();
                addressLine3Flag = countryCode.equals("") ? isContains(details.get("City_IntraLink").trim(), pdfData) : isContains(details.get("City_IntraLink").trim() + ", " + countryCode, pdfData);

                String pdfAddressLine3 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                if (addressLine3Flag) {

                    LogHelper.logger.info("******Pass Statement*****  Address Line 3/City is as expected Actual:- " + pdfAddressLine3 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);

                } else {
                    LogHelper.logger.error("Address Line 3/City is not as expected Actual:- " + pdfAddressLine3 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_PDF", pdfAddressLine3);
                //readFromExcel.saveColumnValueToSpecificScenarioSheetName(TestdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", extractAccounts.get(position).get("ADDRESS1"));

                if (!details.get("PostalCode_IntraLink").isEmpty()) {
//                    String pdfAddressLine4 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
//                    addressLine4Flag = isContains(details.get("PostalCode_IntraLink"), pdfData);
                    addressLine4Flag = pdfData.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    //Validation Address Line 4(Postal Code) Customer from Intralink to PDF
                    if (addressLine4Flag) {
                        LogHelper.logger.info("******Pass Statement*****  Address Line 4/Postal Code is as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));

                    } else {
                        LogHelper.logger.error("Address Line 4/Postal Code is not as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    }

                } else {
                    addressLine4Flag = true;
                }
                // End of Address validation for IP

                // Retrieves IP account details from PDF
                accountDetails = getIPAccountDetails(pdfData);
                LogHelper.logger.info("Customer Name:- " + details.get("CustomerName") + " and Scenarios ID:- " + details.get("getScenarioId()") + " Account details are:- " + accountDetails.stream().collect(Collectors.joining(",")));
                String accountNumber = "IP#" + StringUtils.leftPad(letterDetails.get("AccountNumber").replace(" ", ""), 15, "0").replace(" ", "").trim();
                int accountNumbersCount = accountDetails.stream().map(x -> x.split("\\s+")[1]).collect(Collectors.toList()).
                        stream().filter(x -> accountNumber.equals(x)).collect(Collectors.toList()).size();
                // It check whether intralink account present in PDF as well as only 1 account should be present instead of duplication
                if (accountNumbersCount == 1) {
                    if (details.get("Trust_Accounts").equals("YES")) {
                        accountNumberFlag = true;
                        if (accountNumberFlag) {
                            LogHelper.logger.info("******Pass Statement*****  Account Number is as expected Actual:- " + accountDetails.stream().map(x -> x.replaceAll("\\D+", "")).collect(Collectors.toList()).
                                    stream().filter(x -> details.get("AccountNumber").equals(x)).collect(Collectors.toList()) + "Expected:- " + details.get("AccountNumber"));

                        } else {
                            LogHelper.logger.error("Account Number is not as expected Actual:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                    "Expected:- " + details.get("AccountNumber"));
                        }
                        // Verifiying Dealar value by concatinating with account number
                        String pdfDelar = accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(accountNumber)).collect(Collectors.joining(",")).trim();

                        if (details.get("ProductName").contains(" - SSI")) {
                            delarFlag = pdfDelar.split(" ")[0].equals("BNS");
                        } else {
                            delarFlag = pdfDelar.split(" ")[0].equals(details.get("Delar_Intralink"));
                        }

                        // Verifying Delar value
                        if (delarFlag) {
                            LogHelper.logger.info("******Pass Statement*****  Delar is as expected Actual:- " + pdfDelar.split(" ")[0] + "Expected:- " + details.get("Delar_Intralink"));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_PDF", pdfDelar.split(" ")[0]);

                        } else {
                            LogHelper.logger.error("Delar is not as expected Actual:- " + pdfDelar.split(" ")[0] + "Expected:- " + details.get("Delar_Intralink"));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_PDF", pdfDelar.split(" ")[0]);
                        }
                    } else if (details.get("Trust_Accounts").equals("NO")) {
                        accountNumberFlag = false;
                        delarFlag = false;
                        executionStatus.add("Account Numbers is Present in PDF even Trust is NO");
                    }
                }
                // If account number is more than 1 time in PDF then it comes to below condition
                else if (accountNumbersCount > 1) {
                    accountNumberFlag = false;
                    LogHelper.logger.error("Account Numbers and Issuers are present multiple times in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                            "Expected Account Number:- " + details.get("AccountNumber") + " " + "Issuer " + details.get("Delar_Intralink"));
                    delarFlag = false;

                }
                // If account number is not present in PDF then it comes to below condition
                else if (accountNumbersCount == 0) {
                    if (details.get("Trust_Accounts").equalsIgnoreCase("NO") ||
                            details.get("AccountStatus_Intralink").equalsIgnoreCase("Closed")) {
                        accountNumberFlag = true;
                        delarFlag = true;
                    } else {
                        accountNumberFlag = false;
                        LogHelper.logger.error("Account Numbers and Issuers are not present in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                "Expected Account Number:- " + details.get("AccountNumber") + " " + "Issuer " + details.get("Delar_Intralink"));
                        delarFlag = false;
                    }
                }
                // Saves all GIC's from Excel to gics List object
                ArrayList<String> gics = new ArrayList<String>(Arrays.asList(details.get("GIC_IntraLink").split("\\|")));
                LogHelper.logger.info("Intralin GIC's are :-" + gics.stream().collect(Collectors.joining(",")));
                ArrayList<String> issuers = new ArrayList<String>(Arrays.asList(details.get("Issuer_Intralink").split("\\|")));
                LogHelper.logger.info("Intralin GIC's are :-" + issuers.stream().collect(Collectors.joining(",")));
                // Object created for GIC's
                Map<String, Boolean> gicsFlag = new HashMap<String, Boolean>();
                List<String> issuersPdf = new LinkedList<>();

                // Map<String, Boolean> issuersFlag = null;
                // Verifying all IP Account having GIC then it will goes to below condition
                if (!details.get("GIC_IntraLink").equals("") && gics.size() >= 1) {

                    // Object created for Issuers

                    for (int gic = 0; gic < gics.size(); gic++) {

                        int finalGic = gic;
                        // Verifying all IP Accounts GIC's are present in PDF
                        List<String> certificateNumber = accountDetails.stream().map(x -> x.split("\\s+")[1]).collect(Collectors.toList()).
                                stream().filter(x -> gics.get(finalGic).trim().equals(x)).collect(Collectors.toList());
                        // It check whether intralink IP account GIC is present in PDF as well as only individual GIC should be present instead of duplication
                        if (certificateNumber.size() == 1) {
                            if (details.get("Trust_Accounts").equals("YES")) {
                                String gicValue = certificateNumber.get(0);

                                gicsFlag.put(gicValue, true);
                                LogHelper.logger.info("******Pass Statement*****  GIC Number is as expected Actual:- " + gicValue + "Expected:- " + gics.get(finalGic));
                                // It check whether intralink IP account GIC Issuer is present in PDF as well as only individual GIC Issuer should be present instead of duplication
                                List<String> issuerValue = accountDetails.stream().filter(x -> x.replaceAll("\\r", "").equals(issuers.get(finalGic) + "  " + gics.get(finalGic)))
                                        .collect(Collectors.toList());

                                String issuerPdfValue = accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(gics.get(finalGic)))
                                        .collect(Collectors.toList()).size() == 1 ? accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(gics.get(finalGic)))
                                        .collect(Collectors.joining("")).split(" ")[0] : accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(gics.get(finalGic)))
                                        .collect(Collectors.joining(","));


                                if (issuerValue.size() != 0) {
                                    issuersPdf.add(issuerPdfValue + " true");
                                    LogHelper.logger.info("******Pass Statement*****  Issuer is as expected Actual:- " + accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(gics.get(finalGic)))
                                            .collect(Collectors.toList()) + "Expected:- " + issuers.get(finalGic) + "  " + gics.get(finalGic));
                                } else {
                                    issuersPdf.add(issuerPdfValue + " false");
                                    LogHelper.logger.error("Issuer is not as expected Actual:- " + accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(gics.get(finalGic)))
                                            .collect(Collectors.toList()) + "Expected:- " + issuers.get(finalGic) + "  " + gics.get(finalGic));
                                }
                            } else if (details.get("Trust_Accounts").equals("NO")) {
                                gicsFlag.put(gics.get(finalGic), false);
                                issuersPdf.add("| false");
                                executionStatus.add("GIC's and Issuers are Present in PDF even Trust is NO");
                            }

                        }
                        // If account number is more than 1 time in PDF then it comes to below condition
                        else if (accountDetails.stream().map(x -> x.split("\\s+")[1]).collect(Collectors.toList()).
                                stream().filter(x -> gics.get(finalGic).equals(x)).collect(Collectors.toList()).size() > 1) {
                            gicsFlag.put(gics.get(finalGic), false);
                            issuersPdf.add("| false");
                            LogHelper.logger.error("GIC's and Issuers are present multiple times  in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                    "Expected Account Number:- " + details.get("AccountNumber") + " " + "Issuer " + issuers.get(finalGic) + "  " + gics.get(finalGic));
                        }
                        // If account number is not present in PDF then it comes to below condition
                        else if (accountDetails.stream().map(x -> x.split("\\s+")[1]).collect(Collectors.toList()).
                                stream().filter(x -> gics.get(finalGic).equals(x)).collect(Collectors.toList()).size() == 0) {
                            if (details.get("Trust_Accounts").equals("YES")) {
                                gicsFlag.put(gics.get(finalGic), false);
                                issuersPdf.add("| false");
                                executionStatus.add(gics.get(gic) + " " + issuers.get(gic));
                                LogHelper.logger.error("GIC's and Issuers are not present in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                        "Expected:- " + issuers.get(finalGic) + "  " + gics.get(finalGic));
                            } else if (details.get("Trust_Accounts").equals("NO")) {
                                gicsFlag.put(gics.get(finalGic), true);
                                issuersPdf.add("| true");
                                executionStatus.add("Pass:-GIC's and Issuers are not Present in PDF as Trust is NO");
                            }
                        }


                    }
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "GIC_PDF", accountDetails.stream().filter(x -> x.contains("GIC#")).collect(Collectors.toList()).
                            stream().map(x -> x.split("\\s+")[1]).collect(Collectors.joining("|")));
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Issuer_PDF", accountDetails.stream().map(x -> x.replaceAll("\\r", "")).filter(x -> x.contains("GIC#")).collect(Collectors.toList()).stream().map(x -> x.split("\\s+")[0]).collect(Collectors.joining("|")));
                    issuerFlag = issuersPdf.stream().filter(x -> x.contains("false")).collect(Collectors.toList()).size() < 1;

                }

                //Verifying Page 2 details
//             ->Date on Page 3 (MAILDATE)
//             ->Primary Customer Name/Sole Customer Name (pages 1, 3)
//            ->Account Address line 1
//            ->Account Address line 2(Address Line 2/City/Address Line 3 + Province/State)
//            ->Account Address line 3(City/Address Line 3 + Province/State)
//            ->Account Address line 4(Postal/Zip Code)

                // Validating English Second page PDF

                pdfTextStripper.setStartPage(pdfPage + 1);
                pdfTextStripper.setEndPage(pdfPage + 1);
                pdfData = pdfTextStripper.getText(pdfDocument);
                List<String> addressDetailsPage2 = getAddressDetails(pdfPage, pdfDocument);
                int addressPosition_PageTwo = 0;
                dearSirMadamBody = pdfData.replaceAll("\r", "").contains(details.get("Page3Body"));
                //Validation Mail date from extract to PDF
                if (dearSirMadamBody) {
                    LogHelper.logger.info("******Pass Statement*****  Page 3 body is as expected Actual:- " + pdfData + "Expected:- " + details.get("Page3Body"));

                } else {
                    LogHelper.logger.error("Page 3 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("Page3Body"));
                }
                //Validation ASAT date from extract to PDF
                asatDateFlag = isContains(details.get("ASATDATE"), pdfData);
                if (asatDateFlag) {
                    LogHelper.logger.info("******Pass Statement*****  ASAT Date is as expected Actual:- " + pdfData + "Expected:- " + "April 30th, 2019");

                } else {
                    LogHelper.logger.error("ASAT Date is not as expected Actual:- " + pdfData + "Expected:- " + "April 30th, 2019");
                }

                primaryCustomerName_1Flag = details.get("CustomerName").trim().equals(addressDetailsPage2.get(addressPosition_PageTwo).trim());
                //Validation Primary Customer Name from Intralink to PDF
                if (primaryCustomerName_1Flag) {
                    LogHelper.logger.info("******Pass Statement*****  Primary Customer in Page 2 is as expected Actual:- " + addressDetailsPage2.get(addressPosition_PageTwo) + "Expected:- " + details.get("CustomerNameExtract"));

                } else {
                    LogHelper.logger.error("Primary Customer in Page 2 is not as expected Actual:- " + addressDetailsPage2.get(addressPosition_PageTwo) + "Expected:- " + details.get("CustomerNameExtract"));
                }
                //Validation Address Line 1 from Intralink to PDF
                addressLine1Flag_1Flag = details.get("AddressLine1_IntraLink").trim().equals(addressDetailsPage2.get(addressPosition_PageTwo = addressPosition_PageTwo + 1).trim());
                //Validation Address Line 2 from Intralink to PDF
                if (!details.get("AddressLine2_IntraLink").isEmpty()) {
                    String pdfAddressLine2Page2 = addressDetailsPage2.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
                    addressLine2Flag_1Flag = details.get("AddressLine2_IntraLink").trim().equals(pdfAddressLine2Page2.trim());
                    if (addressLine2Flag_1Flag) {
                        LogHelper.logger.info("******Pass Statement*****  Primary Customer in Page 2 is as expected Actual:- " + pdfAddressLine2Page2 + "Expected:- " + details.get("AddressLine2_IntraLink"));

                    } else {
                        LogHelper.logger.error("Primary Customer in Page 2 is not as expected Actual:- " + pdfAddressLine2Page2 + "Expected:- " + details.get("AddressLine2_IntraLink"));
                    }

                } else {
                    addressLine2Flag_1Flag = true;
                }

                //Validation Address Line 3(City) from Intralink to PDF

                countryCodes = readFromExcel.getSaveValue(System.getProperty("user.dir") + "/src/main/resources/data/", "CountryCodes.xlsx", "CountryCodes", details.get("State"));
                countryCode = countryCodes.getField("NameValue").trim();
                addressLine3Flag_1Flag = countryCode.equals("") ? isContains(details.get("City_IntraLink").trim(), pdfData) : isContains(details.get("City_IntraLink").trim() + ", " + countryCode, pdfData);
                String pdfAddressLine3Page2 = pdfAddressDetails.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
                if (addressLine3Flag_1Flag) {
                    LogHelper.logger.info("******Pass Statement*****  Address Line 3/City in Page 2 is as expected Actual:- " + pdfAddressLine3Page2 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);

                } else {
                    LogHelper.logger.error("Address Line 3/City in Page 2 is not as expected Actual:- " + pdfAddressLine3Page2 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);
                }
                //Validation Address Line 4(Postal Code) from Intralink to PDF
                if (!details.get("PostalCode_IntraLink").isEmpty()) {
//                    String pdfAddressLine4Pag2 = pdfAddressDetails.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
                    addressLine4Flag_1Flag = pdfData.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));


                    if (addressLine4Flag_1Flag) {
                        LogHelper.logger.info("******Pass Statement*****  Address Line 4/Postal Code is as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));

                    } else {
                        LogHelper.logger.error("Address Line 4/Postal Code is not as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    }

                } else {
                    addressLine4Flag_1Flag = true;
                }
                // Verifying Failed Details while comparing Intralink values with PDF
                exectionStatusFailedFields = new HashMap<>();

                exectionStatusFailedFields.put("Primary Customer Name", primaryCustomerNameFlag);
                exectionStatusFailedFields.put("Letter Date", letterDateFlag);
                exectionStatusFailedFields.put("Stamped Date", stampedDateFlag);
                exectionStatusFailedFields.put("Joint One Customer Name", jointOneCustomerFlag);
                exectionStatusFailedFields.put("Joint Two Customer Name", jointTwoCustomerFlag);
                exectionStatusFailedFields.put("Joint Three Customer Name", jointThreeCustomerFlag);
                exectionStatusFailedFields.put("AddressLine1", addressLine1Flag);
                exectionStatusFailedFields.put("AddressLine2", addressLine2Flag);
                exectionStatusFailedFields.put("City", addressLine3Flag);
                exectionStatusFailedFields.put("PostalCode", addressLine4Flag);
                exectionStatusFailedFields.put("AccountNumber", accountNumberFlag);
                exectionStatusFailedFields.put("Dealer", delarFlag);
                exectionStatusFailedFields.put("Issuer", issuersPdf.size() <= 0 || issuerFlag);
                exectionStatusFailedFields.put("GICs", gicsFlag.size() <= 0 || gicsFlag.values().stream().filter(x -> x == false).collect(Collectors.toList()).size() < 1);
                exectionStatusFailedFields.put("Primary Customer Name Page2", primaryCustomerName_1Flag);
                exectionStatusFailedFields.put("Address Line1 Page2", addressLine1Flag_1Flag);
                exectionStatusFailedFields.put("Address Line2 Page2", addressLine2Flag_1Flag);
                exectionStatusFailedFields.put("City Page2", addressLine3Flag_1Flag);
                exectionStatusFailedFields.put("Postal Code Page2", addressLine4Flag_1Flag);
                exectionStatusFailedFields.put("Page 3 Body", dearSirMadamBody);
                exectionStatusFailedFields.put("ASAT Date Page2", asatDateFlag);
                if (exectionStatusFailedFields.entrySet().
                        stream().filter(x -> x.getValue().equals(false)).collect(Collectors.toList()).size() > 0) {
                    executionStatus.add("Fail-> Mismatch Fields:-" + exectionStatusFailedFields.entrySet().
                            stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                            collect(Collectors.joining(",")));
                    LogHelper.logger.error("Intralink and PDF fields are mismatched at " + exectionStatusFailedFields.entrySet().
                            stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                            collect(Collectors.joining(",")));

                } else {

                    executionStatus.add("Pass");
                    LogHelper.logger.info("******Pass Statement*****  All Intralink and PDF Fields are matched");

                }


            }
            // Verifying all acoounts(Savings and IP) associated to the customer is present in single pdf or not
            pdfTextStripper.setStartPage(pdfPage);
            pdfTextStripper.setEndPage(pdfPage);
            pdfData = pdfTextStripper.getText(pdfDocument);
            if (!(details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI") || (details.get("ProductName").contains("- BNE") || details.get("ProductName").contains("- PSI")))) {
                accountDetails.addAll(getIPAccountDetails(pdfData).stream().map(x -> x.trim()).collect(Collectors.toList()));
            } else if ((details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI") || (details.get("ProductName").contains("- BNE") || details.get("ProductName").contains("- PSI")))) {
                accountDetails.addAll(getSavingsAccountDetails(pdfData).stream().map(x -> x.trim()).collect(Collectors.toList()));
            }
            // Verifying all acoounts(Savings and IP) associated to the customer is present in single pdf or not
            List<String> accsInSinglePdf = verifyingAllAccountsPresentInSinglePDF(accountDetails);

            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AccountsStatus", accsInSinglePdf.stream().collect(Collectors.joining("|")));


        }
        // If Customer address is present more than once in entire PDF Letter then it enters to below condition
        else if (pdfPages.size() > 1) {

            LogHelper.logger.error("Multiple PDF's are generated in Pages-" + verifyCustomerHavingSinglePdf(pdfDocument).values().stream().map(x -> x + ", ").collect(Collectors.toList()) +
                    "for the customer Name:-" + details.get("CustomerName") +
                    "Joint1 Customer:- " + details.get("Joint1_CustomerName") + "<br />" +
                    "Joint2 Customer:- " + details.get("Joint2_CustomerName") + "<br />" +
                    "Joint3 Customer:- " + details.get("Joint3_CustomerName") + "<br />" +
                    "Address Line1:- " + details.get("AddressLine1_IntraLink") + "<br />" +
                    "Address Line2:- " + details.get("AddressLine2_IntraLink") + "<br />" +
                    "City:- " + details.get("AddressLine2_IntraLink") + "<br />" + "<br />" +
                    "Postal Code:-" + details.get("PostalCode_IntraLink"));
            executionStatus.add("Multiple Pdf's are generated in Pages:-" + pdfNumbers.stream().map(x -> x + " ").collect(Collectors.toList()));
        }
        // If Customer address is not present in entire PDF Letter then it enters to below condition
        else if (pdfPages.size() == 0) {
            executionStatus = verifyGenericLtrPdfsDetailsWithExtract();
            if (executionStatus.size() == 0) {
                if (details.get("Trust_Accounts").equalsIgnoreCase("NO")) {
                    // PDF will not generate when Trust Account is 'NO'
                    executionStatus.add("Pass- PDF is not generated as trust is NO");
                } else if (details.get("Trust_Accounts").equalsIgnoreCase("YES")) {
                    // PDF will not generate when Account status is Closed
                    if (details.get("AccountStatus_Intralink").equalsIgnoreCase("Closed")) {
                        executionStatus.add("Pass- PDF is not generated as it is Closed account");
                    }
                    // Types of accouts which will not generate PDF's
                    else if (details.get("AccountStatus_Intralink").equals("Savings/Chequing")) {
                        executionStatus.add("Pass- PDF is not generated as it's account is " + details.get("AccountStatus_Intralink"));
                    } else {
                        LogHelper.logger.error("PDF is not generated for the customer Name:-" + details.get("CustomerName") +
                                "Joint1 Customer:- " + details.get("Joint1_CustomerName") + "<br />" +
                                "Joint2 Customer:- " + details.get("Joint2_CustomerName") + "<br />" +
                                "Joint3 Customer:- " + details.get("Joint3_CustomerName") + "<br />" +
                                "Address Line1:- " + details.get("AddressLine1_IntraLink") + "<br />" +
                                "Address Line2:- " + details.get("AddressLine2_IntraLink") + "<br />" +
                                "City:- " + details.get("AddressLine2_IntraLink") + "<br />" + "<br />" +
                                "Postal Code:-" + details.get("PostalCode_IntraLink"));
                        executionStatus.add("Pdf is not generated with customer details:-" + details.get("CustomerName") + "\n"
                                + details.get("Joint1_CustomerName") + "\n" + details.get("Joint2_CustomerName") + "\n" +
                                details.get("Joint3_CustomerName") + "\n" + details.get("AddressLine1_IntraLink") + "\n" +
                                details.get("AddressLine2_IntraLink") + "\n" + details.get("AddressLine2_IntraLink") + "\n" +
                                details.get("PostalCode_IntraLink"));
                    }

                }
            }

        }

        pdfDocument.close();

        return executionStatus;
    }

    /**
     * This function verifies all Intralink English customer accounts details with PDF Generics and LTR when accounts are not present in Roll up PDF's
     *
     * @return Execution status
     * @throws IOException
     */
    public List<String> verifyGenericLtrPdfsDetailsWithExtract() throws IOException, FilloException {

        pdfFileGeneric = details.get("PDFGenericName");
        pdfDocumentGeneric = new ReadTextFile().readPdfDocument(System.getProperty("user.dir") + "/src/main/resources/data/" + ConfigurationManager.getBundle().getString("environment") + "/PDFs/" + pdfFileGeneric);

        String pdfData = null;
        String countryCode = null;
        String pdfPageNumberGeneric = null;
        boolean accountExistsLTR = false;
        // Page 1 Variables
        boolean primaryCustomerNameFlag = false;
        boolean letterDateFlag = false;
        boolean stampedDateFlag = false;
        boolean jointOneCustomerFlag = false;
        boolean jointTwoCustomerFlag = false;
        boolean jointThreeCustomerFlag = false;
        boolean addressLine1Flag = false;
        boolean addressLine2Flag = false;
        boolean addressLine3Flag = false;
        boolean addressLine4Flag = false;
        boolean accountNumberFlag = false;
        boolean delarFlag = false;
        boolean issuerFlag = false;

        //Page 2 Variables
        boolean dearSirMadamBody = false;
        boolean asatDateFlag = false;
        boolean primaryCustomerName1Flag = false;
        boolean addressLine1Flag1Flag = false;
        boolean addressLine2Flag1Flag = false;
        boolean addressLine3Flag1Flag = false;
        boolean addressLine4Flag1Flag = false;
        int pdfCount = 0;
        // Creating object for Execution status
        List<String> executionStatus = new LinkedList<>();
        // Creating object for mismatch fields while comparing with excel value with pdf fields
        Map<String, Boolean> exectionStatusFailedFields = null;
        // pdfPages stores address details and it's PDF Page number
        HashMap<Integer, List<String>> pdfPages = verifyCustomerHavingSinglePdf(pdfDocumentGeneric);
        Set<Integer> pdfNumbers = pdfPages.keySet();
        int pdfPage = 0;
        if (pdfPages.size() == 1) {
            pdfPage = (Integer) pdfPages.keySet().toArray()[0];
        }

        List<String> accountDetails = null;
        // Customer address should present only once in entire PDF Letter
        if (pdfPages.size() == 1) {
            accountExistsLTR = true;
            LogHelper.logger.info("Customer Name:- " + details.get("CustomerName") + " and Scenarios ID:- " + details.get("getScenarioId()") + " " + "is located in PDF Page Number:-" + pdfPage);
            pdfTextStripper = new PDFTextStripper();
            pdfTextStripper.setStartPage(pdfPage);
            pdfTextStripper.setEndPage(pdfPage);
            pdfData = pdfTextStripper.getText(pdfDocumentGeneric);
            Matcher matcher = Pattern.compile("[0-9]{1}-[0-9]{0,}").matcher(pdfData);
            if (matcher.find()) {
                pdfPageNumberGeneric = matcher.group();

            }
            //If account is English Savings then it goes to below condition
            if (!(details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI") || (details.get("ProductName").contains("- BNE") || details.get("ProductName").contains("- PSI") || details.get("ProductName").contains("Momentum PLUS Savings")))) {

                //Verifying Page 1 details
//            Date on Page 3 (ASATDATE)
//            ->Primary Customer Name/Sole Customer Name (pages 1, 3)
//            ->Date on Page 1
//            ->Account Address line 1
//            ->Account Address line 2(Address Line 2/City/Address Line 3 + Province/State)
//            ->Account Address line 3(City/Address Line 3 + Province/State)
//            ->Account Address line 4(Postal/Zip Code)
//            ->Account Number (table)
//            ->Issuer (table)
                List<String> pdfAddressDetails = pdfPages.get(pdfPage);
                //pdfAddressDetails
                int addressPosition = 0;
                pdfPages.get(pdfPage);
                //Validation Primary Customer Name from Intralink to PDF
                primaryCustomerNameFlag = details.get("CustomerName").trim().equals(pdfAddressDetails.get(addressPosition).trim());
                if (primaryCustomerNameFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Primary Customer is as expected Actual:- " + pdfAddressDetails.get(addressPosition) + "Expected:- " + details.get("CustomerName"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer is not as expected Actual:- " + pdfAddressDetails.get(addressPosition) + "Expected:- " + details.get("CustomerName"));
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNamePDF", pdfAddressDetails.get(addressPosition));
                //Validation Letter Date from Intralink to PDF
                letterDateFlag = isContains(details.get("ASATDATE").replace("th", ""), pdfData);
                if (letterDateFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Letter Date in Page 1 is as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE").replace("th", ""));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Letter Date in Page 1 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE").replace("th", ""));
                }
                //Validation Letter Date from Intralink to PDF
                stampedDateFlag = isContains(details.get("MAILDATE").replace(", 2020", "th"), pdfData);
                if (stampedDateFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Stamped date On or before in Page 1 is as expected Actual:- " + pdfData + "Expected:- " + details.get("MAILDATE").replace(", 2020", "th"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Stamped date On or before in Page 1 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("MAILDATE").replace(", 2020", "th"));
                }


                //Validation Joint One Customer from Intralink to PDF
                if (!details.get("Joint1_CustomerName").isEmpty()) {
                    String joint1CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointOneCustomerFlag = details.get("Joint1_CustomerName").trim().equals(joint1CustomerName.trim());
                    if (jointOneCustomerFlag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Joint One Customer is as expected Actual:- " + joint1CustomerName + "Expected:- " + details.get("Joint1_CustomerName"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint One Customer is not as expected Actual:- " + joint1CustomerName + "Expected:- " + details.get("Joint1_CustomerName"));
                    }
                } else {
                    jointOneCustomerFlag = true;

                }
                //Validation Joint Two Customer from Intralink to PDF
                if (!details.get("Joint2_CustomerName").isEmpty()) {
                    String joint2CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointTwoCustomerFlag = details.get("Joint2_CustomerName").trim().equals(joint2CustomerName.trim());
                    if (jointTwoCustomerFlag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Joint Two Customer is as expected Actual:- " + joint2CustomerName + "Expected:- " + details.get("Joint2_CustomerName"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint Two Customer is not as expected Actual:- " + joint2CustomerName + "Expected:- " + details.get("Joint2_CustomerName"));
                    }
                } else {
                    jointTwoCustomerFlag = true;
                }
                //Validation Joint Three Customer from Intralink to PDF
                if (!details.get("Joint3_CustomerName").isEmpty()) {
                    String joint3CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointThreeCustomerFlag = details.get("Joint3_CustomerName").trim().equals(joint3CustomerName.trim());
                    if (jointThreeCustomerFlag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Joint Three Customer is as expected Actual:- " + joint3CustomerName + "Expected:- " + details.get("Joint3_CustomerName"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint Three Customer is not as expected Actual:- " + joint3CustomerName + "Expected:- " + details.get("Joint3_CustomerName"));
                    }
                } else {
                    jointThreeCustomerFlag = true;
                }
                String pdfAddressLine1 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                addressLine1Flag = details.get("AddressLine1_IntraLink").trim().equals(pdfAddressLine1.trim());
                //Validation Address Line 1 Customer from Intralink to PDF
                if (addressLine1Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 1 is as expected Actual:- " + pdfAddressLine1 + "Expected:- " + details.get("AddressLine1_IntraLink"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 1 is not as expected Actual:- " + pdfAddressLine1 + "Expected:- " + details.get("AddressLine1_IntraLink"));
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_PDF", pdfAddressLine1);
                if (!details.get("AddressLine2_IntraLink").isEmpty()) {
                    String pdfAddressLine2 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    addressLine2Flag = details.get("AddressLine2_IntraLink").trim().equals(pdfAddressLine2.trim());
                    //Validation Address Line 2 Customer from Intralink to PDF
                    if (addressLine2Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 2 is as expected Actual:- " + pdfAddressLine2 + "Expected:- " + details.get("AddressLine2_IntraLink"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 2 is not as expected Actual:- " + pdfAddressLine2 + "Expected:- " + details.get("AddressLine2_IntraLink"));
                    }
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_PDF", pdfAddressLine2);
                } else {
                    addressLine2Flag = true;
                }
                //Validation Address Line 3(City) Customer from Intralink to PDF
                countryCodes = readFromExcel.getSaveValue(System.getProperty("user.dir") + "/src/main/resources/data/", "CountryCodes.xlsx", "CountryCodes", details.get("State"));
                countryCode = countryCodes.getField("NameValue").trim();
                addressLine3Flag = countryCode.equals("") ? isContains(details.get("City_IntraLink").trim(), pdfData) : isContains(details.get("City_IntraLink").trim() + ", " + countryCode, pdfData);
                String pdfAddressLine3 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                if (addressLine3Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 3/City is as expected Actual:- " + pdfAddressLine3 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 3/City is not as expected Actual:- " + pdfAddressLine3 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_PDF", pdfAddressLine3);
                //readFromExcel.saveColumnValueToSpecificScenarioSheetName(TestdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", extractAccounts.get(position).get("ADDRESS1"));
                if (!details.get("PostalCode_IntraLink").isEmpty()) {

                    //String pdfAddressLine4 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
//                    addressLine4Flag = isContains(details.get("PostalCode_IntraLink"), pdfData);
                    addressLine4Flag = pdfData.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    //Validation Address Line 4(Postal Code) Customer from Intralink to PDF
                    if (addressLine4Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 4/Postal Code is as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 4/Postal Code is not as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    }

                } else {
                    addressLine4Flag = true;
                }

                //Verifying Page 2 details
//             ->Date on Page 3 (MAILDATE)
//             ->Primary Customer Name/Sole Customer Name (pages 1, 3)
//            ->Account Address line 1
//            ->Account Address line 2(Address Line 2/City/Address Line 3 + Province/State)
//            ->Account Address line 3(City/Address Line 3 + Province/State)
//            ->Account Address line 4(Postal/Zip Code)

                // Validating English Second page PDF
                pdfTextStripper.setStartPage(pdfPage + 1);
                pdfTextStripper.setEndPage(pdfPage + 1);
                pdfData = pdfTextStripper.getText(pdfDocumentGeneric);


                List<String> addressDetailsPage2 = getAddressDetails(pdfPage, pdfDocumentGeneric);
                int addressPosition_PageTwo = 0;
                dearSirMadamBody = pdfData.replaceAll("\r", "").contains(details.get("Page3Body"));
                //Validation Mail date from extract to PDF
                if (dearSirMadamBody) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Page 3 body is as expected Actual:- " + pdfData + "Expected:- " + details.get("Page3Body"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Page 3 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("Page3Body"));
                }
                //Validation ASAT date from extract to PDF
                asatDateFlag = isContains(details.get("ASATDATE"), pdfData);
                if (asatDateFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  ASAT Date is as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "ASAT Date is not as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE"));
                }

                primaryCustomerName1Flag = details.get("CustomerName").trim().equals(addressDetailsPage2.get(addressPosition_PageTwo).trim());
                //Validation Primary Customer Name from Intralink to PDF
                if (primaryCustomerName1Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Primary Customer in Page 2 is as expected Actual:- " + addressDetailsPage2.get(addressPosition_PageTwo) + "Expected:- " + details.get("CustomerName"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer in Page 2 is not as expected Actual:- " + addressDetailsPage2.get(addressPosition_PageTwo) + "Expected:- " + details.get("CustomerName"));
                }
                //Validation Address Line 1 from Intralink to PDF
                addressLine1Flag1Flag = details.get("AddressLine1_IntraLink").trim().equals(addressDetailsPage2.get(addressPosition_PageTwo = addressPosition_PageTwo + 1).trim());
                //Validation Address Line 2 from Intralink to PDF
                if (!details.get("AddressLine2_IntraLink").isEmpty()) {
                    String pdfAddressLine2Page2 = addressDetailsPage2.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
                    addressLine2Flag1Flag = details.get("AddressLine2_IntraLink").trim().equals(pdfAddressLine2Page2.trim());
                    if (addressLine2Flag1Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Primary Customer in Page 2 is as expected Actual:- " + pdfAddressLine2Page2 + "Expected:- " + details.get("AddressLine2_IntraLink"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer in Page 2 is not as expected Actual:- " + pdfAddressLine2Page2 + "Expected:- " + details.get("AddressLine2_IntraLink"));
                    }

                } else {
                    addressLine2Flag1Flag = true;
                }

                //Validation Address Line 3(City) from Intralink to PDF

                countryCodes = readFromExcel.getSaveValue(System.getProperty("user.dir") + "/src/main/resources/data/", "CountryCodes.xlsx", "CountryCodes", details.get("State"));
                countryCode = countryCodes.getField("NameValue").trim();
                addressLine3Flag1Flag = countryCode.equals("") ? isContains(details.get("City_IntraLink").trim(), pdfData) : isContains(details.get("City_IntraLink").trim() + ", " + countryCode, pdfData);

                String pdfAddressLine3Page2 = pdfAddressDetails.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
                if (addressLine3Flag1Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 3/City in Page 2 is as expected Actual:- " + pdfData + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);


                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 3/City in Page 2 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);
                }
                //Validation Address Line 4(Postal Code) from Intralink to PDF
                if (!details.get("PostalCode_IntraLink").isEmpty()) {
                    //String pdfAddressLine4Pag2 = pdfAddressDetails.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
//                    addressLine4Flag_1Flag = isContains(details.get("PostalCode_IntraLink"), pdfData);
                    addressLine4Flag1Flag = pdfData.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    if (addressLine4Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 4/Postal Code is as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 4/Postal Code is not as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink"));
                    }

                } else {
                    addressLine4Flag1Flag = true;
                }

                // Retrieves savings account details from PDF
                accountDetails = getSavingsAccountDetailsFromLTR(verifyLtrPdfsDetailsWithExtract(pdfPageNumberGeneric));
                LogHelper.logger.info("Customer Name:- " + details.get("CustomerName") + " and Scenarios ID:- " + details.get("getScenarioId()") + " Account details are:- " + accountDetails.stream().collect(Collectors.joining(",")));
                int accountNumbersCount = accountDetails.stream().map(x -> x.replaceAll("\\D+", "")).collect(Collectors.toList()).
                        stream().filter(x -> details.get("AccountNumber").equals(x)).collect(Collectors.toList()).size();
                // It check whether intralink account present in PDF as well as only 1 account should be present instead of duplication
                if (accountNumbersCount == 1) {
                    if (details.get("Trust_Accounts").equals("YES")) {
                        accountNumberFlag = true;
                        if (accountNumberFlag) {
                            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Account Number is as expected Actual:- " + accountDetails.stream().map(x -> x.replaceAll("\\D+", "")).collect(Collectors.toList()).
                                    stream().filter(x -> details.get("AccountNumber").equals(x)).collect(Collectors.toList()) + "Expected:- " + details.get("AccountNumber"));

                        } else {
                            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Number is not as expected Actual:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                    "Expected:- " + details.get("AccountNumber"));
                        }
                        // Verifiying Dealar value by concatinating with account number
                        List<String> pdfDelar = accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(details.get("AccountNumber").
                                        trim().substring(0, 5) + " " + details.get("AccountNumber").
                                        trim().substring(5, 10) + " " + details.get("AccountNumber").trim().substring(10, 12))).
                                collect(Collectors.toList());
                        if (pdfDelar.size() != 0) {
                            Matcher matcher1 = Pattern.compile("[A-Z]{1,4}").matcher(pdfDelar.get(0));
                            matcher1.find();
                            String pdfDelarValue = matcher1.group();
                            delarFlag = pdfDelarValue.equals(details.get("Delar_Intralink"));
                            // Verifying Delar value
                            if (delarFlag) {

                                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Delar is as expected Actual:- " + pdfDelarValue + " Expected:- " + details.get("Delar_Intralink"));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_PDF", pdfDelarValue);

                            } else {
                                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Delar is not as expected Actual:- " + pdfDelarValue + " Expected:- " + details.get("Delar_Intralink"));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_PDF", pdfDelarValue);
                            }
                        }
                    } else if (details.get("Trust_Accounts").equals("NO")) {
                        accountNumberFlag = false;
                        delarFlag = false;
                        executionStatus.add("Account Number is Present in  LTR PDF even Trust is NO");
                    }

                }
                // If account number is more than 1 time in PDF then it comes to below condition
                else if (accountNumbersCount > 1) {
                    accountNumberFlag = false;
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Numbers and Delars are present multiple times in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                            "Expected Account Number:- " + details.get("AccountNumber") + " " + "Delar " + details.get("Delar_Intralink"));
                    delarFlag = false;
                    executionStatus.add("Account Numbers is repeated");

                }
                // If account number is not present in PDF then it comes to below condition
                else if (accountNumbersCount == 0) {
                    if (details.get("Trust_Accounts").equalsIgnoreCase("NO")) {
                        accountNumberFlag = true;
                        delarFlag = true;
                    } else {
                        accountNumberFlag = false;
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Numbers and Delars are not present in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                "Expected Account Number:- " + details.get("AccountNumber") + " " + "Delar " + details.get("Delar_Intralink"));
                        delarFlag = false;
                    }

                }
                // Verifying Failed Details while comparing Intralink values with PDF
                exectionStatusFailedFields = new HashMap<>();

                exectionStatusFailedFields.put("Primary Customer Name", primaryCustomerNameFlag);
                exectionStatusFailedFields.put("Letter Date", letterDateFlag);
                exectionStatusFailedFields.put("Stamped Date", stampedDateFlag);

                exectionStatusFailedFields.put("Joint One Customer Name", jointOneCustomerFlag);
                exectionStatusFailedFields.put("Joint Two Customer Name", jointTwoCustomerFlag);
                exectionStatusFailedFields.put("Joint Three Customer Name", jointThreeCustomerFlag);
                exectionStatusFailedFields.put("AddressLine1", addressLine1Flag);
                exectionStatusFailedFields.put("AddressLine2", addressLine2Flag);
                exectionStatusFailedFields.put("City", addressLine3Flag);
                exectionStatusFailedFields.put("PostalCode", addressLine4Flag);
                exectionStatusFailedFields.put("AccountNumber", accountNumberFlag);
                exectionStatusFailedFields.put("Dealer", delarFlag);

                exectionStatusFailedFields.put("Primary Customer Name Page2", primaryCustomerName1Flag);
                exectionStatusFailedFields.put("Address Line1 Page2", addressLine1Flag1Flag);
                exectionStatusFailedFields.put("Address Line2 Page2", addressLine2Flag1Flag);
                exectionStatusFailedFields.put("City Page2", addressLine3Flag1Flag);
                exectionStatusFailedFields.put("Postal Code Page2", addressLine4Flag1Flag);
                exectionStatusFailedFields.put("Page 3 Body", dearSirMadamBody);
                exectionStatusFailedFields.put("ASAT Date Page2", asatDateFlag);
                if (exectionStatusFailedFields.entrySet().
                        stream().filter(x -> x.getValue().equals(false)).collect(Collectors.toList()).size() > 0) {
                    executionStatus.add("Fail-> Customer Present in Generic and LTR Mismatch Fields:-" + exectionStatusFailedFields.entrySet().
                            stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                            collect(Collectors.joining(",")));
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Intralink and PDF fields are mismatched at " + exectionStatusFailedFields.entrySet().
                            stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                            collect(Collectors.joining(",")));
                } else {

                    executionStatus.add("Pass- Customer Details Present in Generic and LTR");
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  All Intralink and PDF Fields are matched");

                }
            } //If account is French IP Account then it goes to below condition
            else if (details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI") || details.get("ProductName").contains("- BNE") || details.get("ProductName").contains("- PSI") || details.get("ProductName").contains("Momentum PLUS Savings")) {


                //Verifying Page 1 details
//            Date on Page 3 (ASATDATE)
//            ->Primary Customer Name/Sole Customer Name (pages 1, 3)
//            ->Date on Page 1
//            ->Account Address line 1
//            ->Account Address line 2(Address Line 2/City/Address Line 3 + Province/State)
//            ->Account Address line 3(City/Address Line 3 + Province/State)
//            ->Account Address line 4(Postal/Zip Code)
//            ->Account Number (table)
//            ->Issuer (table)
//            -> Account Number (table) (Certificate Number)
                List<String> pdfAddressDetails = pdfPages.get(pdfPage);

                pdfTextStripper.setStartPage(pdfPage);
                pdfTextStripper.setEndPage(pdfPage);
                pdfData = pdfTextStripper.getText(pdfDocumentGeneric);
                int addressPosition = 0;
                pdfPages.get(pdfPage);
                //Validation Primary Customer Name from Intralink to PDF
                primaryCustomerNameFlag = details.get("CustomerName").trim().equals(pdfAddressDetails.get(addressPosition).trim());
                if (primaryCustomerNameFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Primary Customer is as expected Actual:- " + pdfAddressDetails.get(addressPosition) + "Expected:- " + details.get("CustomerName"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer is not as expected Actual:- " + pdfAddressDetails.get(addressPosition) + "Expected:- " + details.get("CustomerName"));
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNamePDF", pdfAddressDetails.get(addressPosition));
                //Validation Letter Date from Intralink to PDF
                letterDateFlag = isContains(details.get("ASATDATE").replace("th", ""), pdfData);
                if (letterDateFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Letter Date in Page 1 is as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE").replace("th", ""));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Letter Date in Page 1 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE").replace("th", ""));
                }
                //Validation Letter Date from Intralink to PDF
                stampedDateFlag = isContains(details.get("MAILDATE").replace(", 2020", "th"), pdfData);
                if (stampedDateFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Stamped date On or before in Page 1 is as expected Actual:- " + pdfData + "Expected:- " + details.get("MAILDATE").replace(", 2020", "th"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Stamped date On or before in Page 1 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("MAILDATE").replace(", 2020", "th"));
                }
                //Validation Joint One Customer from Intralink to PDF
                if (!details.get("Joint1_CustomerName").isEmpty()) {
                    String joint1CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointOneCustomerFlag = details.get("Joint1_CustomerName").trim().equals(joint1CustomerName.trim());
                    if (jointOneCustomerFlag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Joint One Customer is as expected Actual:- " + joint1CustomerName + "Expected:- " + details.get("Joint1_CustomerName"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint One Customer is not as expected Actual:- " + joint1CustomerName + "Expected:- " + details.get("Joint1_CustomerName"));
                    }
                } else {
                    jointOneCustomerFlag = true;

                }
                //Validation Joint Two Customer from Intralink to PDF
                if (!details.get("Joint2_CustomerName").isEmpty()) {
                    String joint2CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointTwoCustomerFlag = details.get("Joint2_CustomerName").trim().equals(joint2CustomerName.trim());
                    if (jointTwoCustomerFlag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Joint Two Customer is as expected Actual:- " + joint2CustomerName + "Expected:- " + details.get("Joint2_CustomerName"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint Two Customer is not as expected Actual:- " + joint2CustomerName + "Expected:- " + details.get("Joint2_CustomerName"));
                    }
                } else {
                    jointTwoCustomerFlag = true;
                }
                //Validation Joint Three Customer from Intralink to PDF
                if (!details.get("Joint3_CustomerName").isEmpty()) {
                    String joint3CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointThreeCustomerFlag = details.get("Joint3_CustomerName").equals(joint3CustomerName);
                    if (jointThreeCustomerFlag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Joint Three Customer is as expected Actual:- " + joint3CustomerName + "Expected:- " + details.get("Joint3_CustomerName"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint Three Customer is not as expected Actual:- " + joint3CustomerName + "Expected:- " + details.get("Joint3_CustomerName"));
                    }
                } else {
                    jointThreeCustomerFlag = true;
                }
                String pdfAddressLine1 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                addressLine1Flag = details.get("AddressLine1_IntraLink").trim().equals(pdfAddressLine1.trim());
                //Validation Address Line 1 Customer from Intralink to PDF
                if (addressLine1Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 1 is as expected Actual:- " + pdfAddressLine1 + "Expected:- " + details.get("AddressLine1_IntraLink"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 1 is not as expected Actual:- " + pdfAddressLine1 + "Expected:- " + details.get("AddressLine1_IntraLink"));
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_PDF", pdfAddressLine1);
                if (!details.get("AddressLine2_IntraLink").isEmpty()) {
                    String pdfAddressLine2 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    addressLine2Flag = details.get("AddressLine2_IntraLink").trim().equals(pdfAddressLine2.trim());
                    //Validation Address Line 2 Customer from Intralink to PDF
                    if (addressLine2Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 2 is as expected Actual:- " + pdfAddressLine2 + "Expected:- " + details.get("AddressLine2_IntraLink"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 2 is not as expected Actual:- " + pdfAddressLine2 + "Expected:- " + details.get("AddressLine2_IntraLink"));
                    }
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_PDF", pdfAddressLine2);
                } else {
                    addressLine2Flag = true;
                }
                //Validation Address Line 3(City) Customer from Intralink to PDF
                countryCodes = readFromExcel.getSaveValue(System.getProperty("user.dir") + "/src/main/resources/data/", "CountryCodes.xlsx", "CountryCodes", details.get("State"));
                countryCode = countryCodes.getField("NameValue").trim();
                addressLine3Flag = countryCode.equals("") ? isContains(details.get("City_IntraLink").trim(), pdfData) : isContains(details.get("City_IntraLink").trim() + ", " + countryCode, pdfData);

                String pdfAddressLine3 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                if (addressLine3Flag) {

                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 3/City is as expected Actual:- " + pdfAddressLine3 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 3/City is not as expected Actual:- " + pdfAddressLine3 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_PDF", pdfAddressLine3);

                if (!details.get("PostalCode_IntraLink").isEmpty()) {
                    addressLine4Flag = pdfData.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    //Validation Address Line 4(Postal Code) Customer from Intralink to PDF
                    if (addressLine4Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 4/Postal Code is as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 4/Postal Code is not as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    }

                } else {
                    addressLine4Flag = true;
                }
                // End of Address validation for IP

                // Retrieves IP account details from PDF
                accountDetails = getIPAccountDetailsFromLTR(verifyLtrPdfsDetailsWithExtract(pdfPageNumberGeneric));
                LogHelper.logger.info("Customer Name:- " + details.get("CustomerName") + " and Scenarios ID:- " + details.get("getScenarioId()") + " Account details are:- " + accountDetails.stream().collect(Collectors.joining(",")));
                String accountNumber = "IP#" + StringUtils.leftPad(letterDetails.get("AccountNumber").replace(" ", ""), 15, "0").replace(" ", "").trim();
                int accountNumbersCount = accountDetails.stream().map(x -> x.split("\\s+")[0]).collect(Collectors.toList()).
                        stream().filter(x -> accountNumber.equals(x)).collect(Collectors.toList()).size();
                // It check whether intralink account present in PDF as well as only 1 account should be present instead of duplication
                if (accountNumbersCount == 1) {
                    if (details.get("Trust_Accounts").equals("YES")) {
                        accountNumberFlag = true;
                        if (accountNumberFlag) {
                            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Account Number is as expected Actual:- " + accountDetails.stream().map(x -> x.replaceAll("\\D+", "")).collect(Collectors.toList()).
                                    stream().filter(x -> details.get("AccountNumber").equals(x)).collect(Collectors.toList()) + "Expected:- " + details.get("AccountNumber"));

                        } else {
                            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Number is not as expected Actual:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                    "Expected:- " + details.get("AccountNumber"));
                        }
                        // Verifiying Dealar value by concatinating with account number
                        String pdfDelar = accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(accountNumber)).collect(Collectors.joining(",")).trim();
                        if (details.get("ProductName").contains(" - SSI")) {
                            delarFlag = pdfDelar.split(" ")[1].equals("BNS");
                        } else {
                            delarFlag = pdfDelar.split(" ")[1].equals(details.get("Delar_Intralink"));
                        }
                        // Verifying Delar value
                        if (delarFlag) {
                            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Delar is as expected Actual:- " + pdfDelar.split(" ")[1] + "Expected:- " + details.get("Delar_Intralink"));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_PDF", pdfDelar.split(" ")[1]);

                        } else {
                            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Delar is not as expected Actual:- " + pdfDelar.split(" ")[1] + "Expected:- " + details.get("Delar_Intralink"));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_PDF", pdfDelar.split(" ")[1]);
                        }
                    } else if (details.get("Trust_Accounts").equals("NO")) {
                        accountNumberFlag = false;
                        delarFlag = false;
                        executionStatus.add("Account Number is Present in  LTR PDF even Trust is NO");
                    }

                }
                // If account number is more than 1 time in PDF then it comes to below condition
                else if (accountNumbersCount > 1) {
                    accountNumberFlag = false;
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Numbers and Issuers are present multiple times in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                            "Expected Account Number:- " + details.get("AccountNumber") + " " + "Issuer " + details.get("Delar_Intralink"));
                    delarFlag = false;

                }
                // If account number is not present in PDF then it comes to below condition
                else if (accountNumbersCount == 0) {
                    if (details.get("Trust_Accounts").equalsIgnoreCase("NO")) {
                        accountNumberFlag = true;
                        delarFlag = true;
                    } else {
                        accountNumberFlag = false;
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Numbers and Issuers are not present in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                "Expected Account Number:- " + details.get("AccountNumber") + " " + "Issuer " + details.get("Delar_Intralink"));
                        delarFlag = false;
                    }
                }
                // Saves all GIC's from Excel to gics List object
                ArrayList<String> gics = new ArrayList<String>(Arrays.asList(details.get("GIC_IntraLink").split("\\|")));
                LogHelper.logger.info("Intralin GIC's are :-" + gics.stream().collect(Collectors.joining(",")));
                ArrayList<String> issuers = new ArrayList<String>(Arrays.asList(details.get("Issuer_Intralink").split("\\|")));
                LogHelper.logger.info("Intralin GIC's are :-" + issuers.stream().collect(Collectors.joining(",")));
                // Object created for GIC's
                Map<String, Boolean> gicsFlag = new HashMap<String, Boolean>();
                List<String> issuersPdf = new LinkedList<>();

                // Verifying all IP Account having GIC then it will goes to below condition
                if (!details.get("GIC_IntraLink").equals("") && gics.size() >= 1) {

                    // Object created for Issuers

                    for (int gic = 0; gic < gics.size(); gic++) {

                        int finalGic = gic;
                        // Verifying all IP Accounts GIC's are present in PDF
                        List<String> certificateNumber = accountDetails.stream().map(x -> x.split("\\s+")[0]).collect(Collectors.toList()).
                                stream().filter(x -> gics.get(finalGic).trim().equals(x)).collect(Collectors.toList());
                        // It check whether intralink IP account GIC is present in PDF as well as only individual GIC should be present instead of duplication
                        if (certificateNumber.size() == 1) {
                            if (details.get("Trust_Accounts").equals("YES")) {
                                String gicValue = certificateNumber.get(0);

                                gicsFlag.put(gicValue, true);
                                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  GIC Number is as expected Actual:- " + gicValue + "Expected:- " + gics.get(finalGic));
                                // It check whether intralink IP account GIC Issuer is present in PDF as well as only individual GIC Issuer should be present instead of duplication
                                List<String> issuerValue = accountDetails.stream().filter(x -> x.trim().replaceAll("\\r", "").equals(gics.get(finalGic) + " " + issuers.get(finalGic)))
                                        .collect(Collectors.toList());

                                String issuerPdfValue = accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(gics.get(finalGic)))
                                        .collect(Collectors.toList()).size() == 1 ? accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(gics.get(finalGic)))
                                        .collect(Collectors.joining("")).split(" ")[0] : accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(gics.get(finalGic)))
                                        .collect(Collectors.joining(","));


                                if (issuerValue.size() != 0) {
                                    issuersPdf.add(issuerPdfValue + " true");
                                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Issuer is as expected Actual:- " + accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(gics.get(finalGic)))
                                            .collect(Collectors.toList()) + "Expected:- " + issuers.get(finalGic) + "  " + gics.get(finalGic));
                                } else {
                                    issuersPdf.add(issuerPdfValue + " false");
                                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Issuer is not as expected Actual:- " + accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(gics.get(finalGic)))
                                            .collect(Collectors.toList()) + "Expected:- " + issuers.get(finalGic) + "  " + gics.get(finalGic));
                                }

                            } else if (details.get("Trust_Accounts").equals("NO")) {
                                gicsFlag.put(gics.get(finalGic), false);
                                issuersPdf.add("| false");
                                executionStatus.add("GIC's and Issuers are Present in PDF even Trust is NO");
                            }

                        }
                        // If account number is more than 1 time in PDF then it comes to below condition
                        else if (accountDetails.stream().map(x -> x.split("\\s+")[1]).collect(Collectors.toList()).
                                stream().filter(x -> gics.get(finalGic).equals(x)).collect(Collectors.toList()).size() > 1) {
                            gicsFlag.put(gics.get(finalGic), false);
                            issuersPdf.add("| false");
                            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "GIC's and Issuers are present multiple times  in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                    "Expected Account Number:- " + details.get("AccountNumber") + " " + "Issuer " + issuers.get(finalGic) + "  " + gics.get(finalGic));
                        }
                        // If account number is not present in PDF then it comes to below condition
                        else if (accountDetails.stream().map(x -> x.split("\\s+")[1]).collect(Collectors.toList()).
                                stream().filter(x -> gics.get(finalGic).equals(x)).collect(Collectors.toList()).size() == 0) {
                            if (details.get("Trust_Accounts").equals("YES")) {
                                gicsFlag.put(gics.get(finalGic), false);
                                issuersPdf.add("| false");
                                executionStatus.add(gics.get(gic) + " " + issuers.get(gic));
                                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "GIC's and Issuers are not present in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                        "Expected:- " + issuers.get(finalGic) + "  " + gics.get(finalGic));
                            } else if (details.get("Trust_Accounts").equals("NO")) {
                                gicsFlag.put(gics.get(finalGic), true);
                                issuersPdf.add("| true");
                                executionStatus.add("Pass:-GIC's and Issuers are not Present in PDF as Trust is NO");

                            }


                        }


                    }
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "GIC_PDF", accountDetails.stream().filter(x -> x.contains("GIC#")).collect(Collectors.toList()).
                            stream().map(x -> x.split("\\s+")[1]).collect(Collectors.joining("|")));
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Issuer_PDF", accountDetails.stream().map(x -> x.trim()).filter(x -> x.contains("GIC#")).collect(Collectors.toList()).
                            stream().map(x -> x.split("\\s+")[1]).collect(Collectors.joining("|")));

                    issuerFlag = issuersPdf.stream().filter(x -> x.contains("false")).collect(Collectors.toList()).size() < 1;

                }

                //Verifying Page 2 details
//             ->Date on Page 3 (MAILDATE)
//             ->Primary Customer Name/Sole Customer Name (pages 1, 3)
//            ->Account Address line 1
//            ->Account Address line 2(Address Line 2/City/Address Line 3 + Province/State)
//            ->Account Address line 3(City/Address Line 3 + Province/State)
//            ->Account Address line 4(Postal/Zip Code)

                // Validating English Second page PDF

                pdfTextStripper.setStartPage(pdfPage + 1);
                pdfTextStripper.setEndPage(pdfPage + 1);
                pdfData = pdfTextStripper.getText(pdfDocumentGeneric);
                List<String> addressDetailsPage2 = getAddressDetails(pdfPage, pdfDocumentGeneric);
                int addressPosition_PageTwo = 0;
                dearSirMadamBody = pdfData.replaceAll("\r", "").contains(details.get("Page3Body"));
                //Validation Mail date from extract to PDF
                if (dearSirMadamBody) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Page 3 body is as expected Actual:- " + pdfData + "Expected:- " + details.get("Page3Body"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Page 3 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("Page3Body"));
                }
                //Validation ASAT date from extract to PDF
                asatDateFlag = isContains(details.get("ASATDATE"), pdfData);
                if (asatDateFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  ASAT Date is as expected Actual:- " + pdfData + "Expected:- " + "April 30th, 2019");

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "ASAT Date is not as expected Actual:- " + pdfData + "Expected:- " + "April 30th, 2019");
                }

                primaryCustomerName1Flag = details.get("CustomerName").trim().equals(addressDetailsPage2.get(addressPosition_PageTwo).trim());
                //Validation Primary Customer Name from Intralink to PDF
                if (primaryCustomerName1Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Primary Customer in Page 2 is as expected Actual:- " + addressDetailsPage2.get(addressPosition_PageTwo) + "Expected:- " + details.get("CustomerNameExtract"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer in Page 2 is not as expected Actual:- " + addressDetailsPage2.get(addressPosition_PageTwo) + "Expected:- " + details.get("CustomerNameExtract"));
                }
                //Validation Address Line 1 from Intralink to PDF
                addressLine1Flag1Flag = details.get("AddressLine1_IntraLink").trim().equals(addressDetailsPage2.get(addressPosition_PageTwo = addressPosition_PageTwo + 1).trim());
                //Validation Address Line 2 from Intralink to PDF
                if (!details.get("AddressLine2_IntraLink").isEmpty()) {
                    String pdfAddressLine2Page2 = addressDetailsPage2.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
                    addressLine2Flag1Flag = details.get("AddressLine2_IntraLink").trim().equals(pdfAddressLine2Page2.trim());
                    if (addressLine2Flag1Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Primary Customer in Page 2 is as expected Actual:- " + pdfAddressLine2Page2 + "Expected:- " + details.get("AddressLine2_IntraLink"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer in Page 2 is not as expected Actual:- " + pdfAddressLine2Page2 + "Expected:- " + details.get("AddressLine2_IntraLink"));
                    }

                } else {
                    addressLine2Flag1Flag = true;
                }

                //Validation Address Line 3(City) from Intralink to PDF

                countryCodes = readFromExcel.getSaveValue(System.getProperty("user.dir") + "/src/main/resources/data/", "CountryCodes.xlsx", "CountryCodes", details.get("State"));
                countryCode = countryCodes.getField("NameValue").trim();
                addressLine3Flag1Flag = countryCode.equals("") ? isContains(details.get("City_IntraLink").trim(), pdfData) : isContains(details.get("City_IntraLink").trim() + ", " + countryCode, pdfData);
                String pdfAddressLine3Page2 = pdfAddressDetails.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
                if (addressLine3Flag1Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 3/City in Page 2 is as expected Actual:- " + pdfAddressLine3Page2 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 3/City in Page 2 is not as expected Actual:- " + pdfAddressLine3Page2 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);
                }
                //Validation Address Line 4(Postal Code) from Intralink to PDF
                if (!details.get("PostalCode_IntraLink").isEmpty()) {
                    addressLine4Flag1Flag = pdfData.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));


                    if (addressLine4Flag1Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 4/Postal Code is as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 4/Postal Code is not as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    }

                } else {
                    addressLine4Flag1Flag = true;
                }
                // Verifying Failed Details while comparing Intralink values with PDF
                exectionStatusFailedFields = new HashMap<>();

                exectionStatusFailedFields.put("Primary Customer Name", primaryCustomerNameFlag);
                exectionStatusFailedFields.put("Letter Date", letterDateFlag);
                exectionStatusFailedFields.put("Stamped Date", stampedDateFlag);
                exectionStatusFailedFields.put("Joint One Customer Name", jointOneCustomerFlag);
                exectionStatusFailedFields.put("Joint Two Customer Name", jointTwoCustomerFlag);
                exectionStatusFailedFields.put("Joint Three Customer Name", jointThreeCustomerFlag);
                exectionStatusFailedFields.put("AddressLine1", addressLine1Flag);
                exectionStatusFailedFields.put("AddressLine2", addressLine2Flag);
                exectionStatusFailedFields.put("City", addressLine3Flag);
                exectionStatusFailedFields.put("PostalCode", addressLine4Flag);
                exectionStatusFailedFields.put("AccountNumber", accountNumberFlag);
                exectionStatusFailedFields.put("Dealer", delarFlag);
                exectionStatusFailedFields.put("Issuer", issuersPdf.size() <= 0 || issuerFlag);
                exectionStatusFailedFields.put("GICs", gicsFlag.size() <= 0 || gicsFlag.values().stream().filter(x -> x == false).collect(Collectors.toList()).size() < 1);
                exectionStatusFailedFields.put("Primary Customer Name Page2", primaryCustomerName1Flag);
                exectionStatusFailedFields.put("Address Line1 Page2", addressLine1Flag1Flag);
                exectionStatusFailedFields.put("Address Line2 Page2", addressLine2Flag1Flag);
                exectionStatusFailedFields.put("City Page2", addressLine3Flag1Flag);
                exectionStatusFailedFields.put("Postal Code Page2", addressLine4Flag1Flag);
                exectionStatusFailedFields.put("Page 3 Body", dearSirMadamBody);
                exectionStatusFailedFields.put("ASAT Date Page2", asatDateFlag);
                if (exectionStatusFailedFields.entrySet().
                        stream().filter(x -> x.getValue().equals(false)).collect(Collectors.toList()).size() > 0) {
                    executionStatus.add("Fail-> Mismatch Fields:-" + exectionStatusFailedFields.entrySet().
                            stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                            collect(Collectors.joining(",")));
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Intralink and PDF fields are mismatched at " + exectionStatusFailedFields.entrySet().
                            stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                            collect(Collectors.joining(",")));

                } else {
                    executionStatus.add("Pass- Customer Details Present in Generic and LTR");
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  All Intralink and PDF Fields are matched");
                }

            }
            // Verifying all acoounts(Savings and IP) associated to the customer is present in single pdf or not
            if (!(details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI") || (details.get("ProductName").contains("- BNE") || details.get("ProductName").contains("- PSI")))) {
                accountDetails.addAll(getIPAccountDetailsFromLTR(verifyLtrPdfsDetailsWithExtract(pdfPageNumberGeneric)).stream().map(x -> x.trim()).collect(Collectors.toList()));
            } else if ((details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI") || (details.get("ProductName").contains("- BNE") || details.get("ProductName").contains("- PSI")))) {
                accountDetails.addAll(getSavingsAccountDetailsFromLTR(verifyLtrPdfsDetailsWithExtract(pdfPageNumberGeneric)).stream().map(x -> x.trim()).collect(Collectors.toList()));
            }
            // Verifying all acoounts(Savings and IP) associated to the customer is present in single pdf or not
            List<String> accsInSinglePdf = verifyingAllAccountsPresentInSinglePDFLTR(accountDetails);

            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AccountsStatus", accsInSinglePdf.stream().collect(Collectors.joining("|")));


        }
        // If Customer address is present more than once in entire PDF Letter then it enters to below condition
        else if (pdfPages.size() > 1) {

            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Multiple PDF's are generated in Pages-" + verifyCustomerHavingSinglePdf(pdfDocument).values().stream().map(x -> x + ", ").collect(Collectors.toList()) +
                    "for the customer Name:-" + details.get("CustomerName") +
                    "Joint1 Customer:- " + details.get("Joint1_CustomerName") + "<br />" +
                    "Joint2 Customer:- " + details.get("Joint2_CustomerName") + "<br />" +
                    "Joint3 Customer:- " + details.get("Joint3_CustomerName") + "<br />" +
                    "Address Line1:- " + details.get("AddressLine1_IntraLink") + "<br />" +
                    "Address Line2:- " + details.get("AddressLine2_IntraLink") + "<br />" +
                    "City:- " + details.get("AddressLine2_IntraLink") + "<br />" + "<br />" +
                    "Postal Code:-" + details.get("PostalCode_IntraLink"));
            executionStatus.add("Multiple Pdf's are generated in Pages:-" + pdfNumbers.stream().map(x -> x + " ").collect(Collectors.toList()));
        }
        // If Customer address is not present in entire PDF Letter then it enters to below condition


        return executionStatus;
    }

    public List<String> verifyFrenchGenericLtrPdfsDetailsWithExtract() throws IOException, FilloException {

        pdfFileGeneric = details.get("PDFGenericName");
        pdfDocumentGeneric = new ReadTextFile().readPdfDocument(System.getProperty("user.dir") + "/src/main/resources/data/" + ConfigurationManager.getBundle().getString("environment") + "/PDFs/" + pdfFileGeneric);

        String pdfData = null;
        String countryCode = null;
        String pdfPageNumberGeneric = null;
        boolean accountExistsLTR = false;
        // Page 1 Variables
        boolean primaryCustomerNameFlag = false;
        boolean letterDateFlag = false;
        boolean stampedDateFlag = false;
        boolean jointOneCustomerFlag = false;
        boolean jointTwoCustomerFlag = false;
        boolean jointThreeCustomerFlag = false;
        boolean addressLine1Flag = false;
        boolean addressLine2Flag = false;
        boolean addressLine3Flag = false;
        boolean addressLine4Flag = false;
        boolean accountNumberFlag = false;
        boolean delarFlag = false;
        boolean issuerFlag = false;

        //Page 2 Variables
        boolean dearSirMadamBody = false;
        boolean asatDateFlag = false;
        boolean primaryCustomerName1Flag = false;
        boolean addressLine1Flag1Flag = false;
        boolean addressLine2Flag1Flag = false;
        boolean addressLine3Flag1Flag = false;
        boolean addressLine4Flag1Flag = false;
        int pdfCount = 0;
        // Creating object for Execution status
        List<String> executionStatus = new LinkedList<>();
        // Creating object for mismatch fields while comparing with excel value with pdf fields
        Map<String, Boolean> exectionStatusFailedFields = null;
        // pdfPages stores address details and it's PDF Page number
        HashMap<Integer, List<String>> pdfPages = verifyCustomerHavingSinglePdf(pdfDocumentGeneric);
        Set<Integer> pdfNumbers = pdfPages.keySet();
        int pdfPage = 0;
        if (pdfPages.size() == 1) {
            pdfPage = (Integer) pdfPages.keySet().toArray()[0];
        }

        List<String> accountDetails = null;
        // Customer address should present only once in entire PDF Letter
        if (pdfPages.size() == 1) {
            accountExistsLTR = true;
            LogHelper.logger.info("Customer Name:- " + details.get("CustomerName") + " and Scenarios ID:- " + details.get("getScenarioId()") + " " + "is located in PDF Page Number:-" + pdfPage);
            pdfTextStripper = new PDFTextStripper();
            pdfTextStripper.setStartPage(pdfPage);
            pdfTextStripper.setEndPage(pdfPage);
            pdfData = pdfTextStripper.getText(pdfDocumentGeneric);
            Matcher matcher = Pattern.compile("[0-9]{1}-[0-9]{0,}").matcher(pdfData);
            if (matcher.find()) {
                pdfPageNumberGeneric = matcher.group();

            }
            //If account is English Savings then it goes to below condition
            if (!(details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI") || (details.get("ProductName").contains("- BNE") || details.get("ProductName").contains("- PSI") || details.get("ProductName").contains("Momentum PLUS Savings")))) {

                //Verifying Page 1 details
//            Date on Page 3 (ASATDATE)
//            ->Primary Customer Name/Sole Customer Name (pages 1, 3)
//            ->Date on Page 1
//            ->Account Address line 1
//            ->Account Address line 2(Address Line 2/City/Address Line 3 + Province/State)
//            ->Account Address line 3(City/Address Line 3 + Province/State)
//            ->Account Address line 4(Postal/Zip Code)
//            ->Account Number (table)
//            ->Issuer (table)
                List<String> pdfAddressDetails = pdfPages.get(pdfPage);
                //pdfAddressDetails
                int addressPosition = 0;
                pdfPages.get(pdfPage);
                //Validation Primary Customer Name from Intralink to PDF
                primaryCustomerNameFlag = details.get("CustomerName").trim().equals(pdfAddressDetails.get(addressPosition).trim());
                if (primaryCustomerNameFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Primary Customer is as expected Actual:- " + pdfAddressDetails.get(addressPosition) + "Expected:- " + details.get("CustomerName"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer is not as expected Actual:- " + pdfAddressDetails.get(addressPosition) + "Expected:- " + details.get("CustomerName"));
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNamePDF", pdfAddressDetails.get(addressPosition));
                //Validation Letter Date from Intralink to PDF
                letterDateFlag = isContains(details.get("ASATDATE").replace("Le", ""), pdfData);
                if (letterDateFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Letter Date in Page 1 is as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE").replace("th", ""));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Letter Date in Page 1 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE").replace("th", ""));
                }
                //Validation Letter Date from Intralink to PDF
                stampedDateFlag = isContains(details.get("MAILDATE"), pdfData);
                if (stampedDateFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Stamped date On or before in Page 1 is as expected Actual:- " + pdfData + "Expected:- " + details.get("MAILDATE").replace(", 2020", "th"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Stamped date On or before in Page 1 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("MAILDATE").replace(", 2020", "th"));
                }


                //Validation Joint One Customer from Intralink to PDF
                if (!details.get("Joint1_CustomerName").isEmpty()) {
                    String joint1CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointOneCustomerFlag = details.get("Joint1_CustomerName").trim().equals(joint1CustomerName.trim());
                    if (jointOneCustomerFlag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Joint One Customer is as expected Actual:- " + joint1CustomerName + "Expected:- " + details.get("Joint1_CustomerName"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint One Customer is not as expected Actual:- " + joint1CustomerName + "Expected:- " + details.get("Joint1_CustomerName"));
                    }
                } else {
                    jointOneCustomerFlag = true;

                }
                //Validation Joint Two Customer from Intralink to PDF
                if (!details.get("Joint2_CustomerName").isEmpty()) {
                    String joint2CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointTwoCustomerFlag = details.get("Joint2_CustomerName").trim().equals(joint2CustomerName.trim());
                    if (jointTwoCustomerFlag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Joint Two Customer is as expected Actual:- " + joint2CustomerName + "Expected:- " + details.get("Joint2_CustomerName"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint Two Customer is not as expected Actual:- " + joint2CustomerName + "Expected:- " + details.get("Joint2_CustomerName"));
                    }
                } else {
                    jointTwoCustomerFlag = true;
                }
                //Validation Joint Three Customer from Intralink to PDF
                if (!details.get("Joint3_CustomerName").isEmpty()) {
                    String joint3CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointThreeCustomerFlag = details.get("Joint3_CustomerName").trim().equals(joint3CustomerName.trim());
                    if (jointThreeCustomerFlag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Joint Three Customer is as expected Actual:- " + joint3CustomerName + "Expected:- " + details.get("Joint3_CustomerName"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint Three Customer is not as expected Actual:- " + joint3CustomerName + "Expected:- " + details.get("Joint3_CustomerName"));
                    }
                } else {
                    jointThreeCustomerFlag = true;
                }
                String pdfAddressLine1 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                addressLine1Flag = details.get("AddressLine1_IntraLink").trim().equals(pdfAddressLine1.trim());
                //Validation Address Line 1 Customer from Intralink to PDF
                if (addressLine1Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 1 is as expected Actual:- " + pdfAddressLine1 + "Expected:- " + details.get("AddressLine1_IntraLink"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 1 is not as expected Actual:- " + pdfAddressLine1 + "Expected:- " + details.get("AddressLine1_IntraLink"));
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_PDF", pdfAddressLine1);
                if (!details.get("AddressLine2_IntraLink").isEmpty()) {
                    String pdfAddressLine2 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    addressLine2Flag = details.get("AddressLine2_IntraLink").trim().equals(pdfAddressLine2.trim());
                    //Validation Address Line 2 Customer from Intralink to PDF
                    if (addressLine2Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 2 is as expected Actual:- " + pdfAddressLine2 + "Expected:- " + details.get("AddressLine2_IntraLink"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 2 is not as expected Actual:- " + pdfAddressLine2 + "Expected:- " + details.get("AddressLine2_IntraLink"));
                    }
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_PDF", pdfAddressLine2);
                } else {
                    addressLine2Flag = true;
                }
                //Validation Address Line 3(City) Customer from Intralink to PDF
                countryCodes = readFromExcel.getSaveValue(System.getProperty("user.dir") + "/src/main/resources/data/", "CountryCodes.xlsx", "CountryCodes", details.get("State"));
                countryCode = countryCodes.getField("NameValue").trim();
                addressLine3Flag = countryCode.equals("") ? isContains(details.get("City_IntraLink").trim(), pdfData) : isContains(details.get("City_IntraLink").trim() + ", " + countryCode, pdfData);
                String pdfAddressLine3 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                if (addressLine3Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 3/City is as expected Actual:- " + pdfAddressLine3 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 3/City is not as expected Actual:- " + pdfAddressLine3 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_PDF", pdfAddressLine3);
                if (!details.get("PostalCode_IntraLink").isEmpty()) {

                    addressLine4Flag = pdfData.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    //Validation Address Line 4(Postal Code) Customer from Intralink to PDF
                    if (addressLine4Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 4/Postal Code is as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 4/Postal Code is not as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    }

                } else {
                    addressLine4Flag = true;
                }

                //Verifying Page 2 details
//             ->Date on Page 3 (MAILDATE)
//             ->Primary Customer Name/Sole Customer Name (pages 1, 3)
//            ->Account Address line 1
//            ->Account Address line 2(Address Line 2/City/Address Line 3 + Province/State)
//            ->Account Address line 3(City/Address Line 3 + Province/State)
//            ->Account Address line 4(Postal/Zip Code)

                // Validating English Second page PDF
                pdfTextStripper.setStartPage(pdfPage + 1);
                pdfTextStripper.setEndPage(pdfPage + 1);
                pdfData = pdfTextStripper.getText(pdfDocumentGeneric);


                List<String> addressDetailsPage2 = getAddressDetails(pdfPage, pdfDocumentGeneric);
                int addressPosition_PageTwo = 0;

                dearSirMadamBody = pdfData.replaceAll("\r", "").contains(details.get("Page3Body"));
                //Validation Mail date from extract to PDF
                if (dearSirMadamBody) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Page 3 body is as expected Actual:- " + pdfData + "Expected:- " + details.get("Page3Body"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Page 3 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("Page3Body"));
                }
                //Validation ASAT date from extract to PDF
                asatDateFlag = isContains(details.get("ASATDATE"), pdfData);
                if (asatDateFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  ASAT Date is as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "ASAT Date is not as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE"));
                }

                primaryCustomerName1Flag = details.get("CustomerName").trim().equals(addressDetailsPage2.get(addressPosition_PageTwo).trim());
                //Validation Primary Customer Name from Intralink to PDF
                if (primaryCustomerName1Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Primary Customer in Page 2 is as expected Actual:- " + addressDetailsPage2.get(addressPosition_PageTwo) + "Expected:- " + details.get("CustomerName"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer in Page 2 is not as expected Actual:- " + addressDetailsPage2.get(addressPosition_PageTwo) + "Expected:- " + details.get("CustomerName"));
                }
                //Validation Address Line 1 from Intralink to PDF
                addressLine1Flag1Flag = details.get("AddressLine1_IntraLink").trim().equals(addressDetailsPage2.get(addressPosition_PageTwo = addressPosition_PageTwo + 1).trim());
                //Validation Address Line 2 from Intralink to PDF
                if (!details.get("AddressLine2_IntraLink").isEmpty()) {
                    String pdfAddressLine2Page2 = addressDetailsPage2.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
                    addressLine2Flag1Flag = details.get("AddressLine2_IntraLink").trim().equals(pdfAddressLine2Page2.trim());
                    if (addressLine2Flag1Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Primary Customer in Page 2 is as expected Actual:- " + pdfAddressLine2Page2 + "Expected:- " + details.get("AddressLine2_IntraLink"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer in Page 2 is not as expected Actual:- " + pdfAddressLine2Page2 + "Expected:- " + details.get("AddressLine2_IntraLink"));
                    }

                } else {
                    addressLine2Flag1Flag = true;
                }

                //Validation Address Line 3(City) from Intralink to PDF

                countryCodes = readFromExcel.getSaveValue(System.getProperty("user.dir") + "/src/main/resources/data/", "CountryCodes.xlsx", "CountryCodes", details.get("State"));
                countryCode = countryCodes.getField("NameValue").trim();
                addressLine3Flag1Flag = countryCode.equals("") ? isContains(details.get("City_IntraLink").trim(), pdfData) : isContains(details.get("City_IntraLink").trim() + ", " + countryCode, pdfData);

                String pdfAddressLine3Page2 = pdfAddressDetails.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
                if (addressLine3Flag1Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 3/City in Page 2 is as expected Actual:- " + pdfData + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);


                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 3/City in Page 2 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);
                }
                //Validation Address Line 4(Postal Code) from Intralink to PDF
                if (!details.get("PostalCode_IntraLink").isEmpty()) {
                    //String pdfAddressLine4Pag2 = pdfAddressDetails.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
//                    addressLine4Flag_1Flag = isContains(details.get("PostalCode_IntraLink"), pdfData);
                    addressLine4Flag1Flag = pdfData.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    if (addressLine4Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 4/Postal Code is as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 4/Postal Code is not as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink"));
                    }

                } else {
                    addressLine4Flag1Flag = true;
                }

                // Retrieves savings account details from PDF
                accountDetails = getSavingsAccountDetailsFromLTR(verifyLtrPdfsDetailsWithExtract(pdfPageNumberGeneric));
                LogHelper.logger.info("Customer Name:- " + details.get("CustomerName") + " and Scenarios ID:- " + details.get("getScenarioId()") + " Account details are:- " + accountDetails.stream().collect(Collectors.joining(",")));
                int accountNumbersCount = accountDetails.stream().map(x -> x.replaceAll("\\D+", "")).collect(Collectors.toList()).
                        stream().filter(x -> details.get("AccountNumber").equals(x)).collect(Collectors.toList()).size();
                // It check whether intralink account present in PDF as well as only 1 account should be present instead of duplication
                if (accountNumbersCount == 1) {
                    if (details.get("Trust_Accounts").equals("YES")) {
                        accountNumberFlag = true;
                        if (accountNumberFlag) {
                            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Account Number is as expected Actual:- " + accountDetails.stream().map(x -> x.replaceAll("\\D+", "")).collect(Collectors.toList()).
                                    stream().filter(x -> details.get("AccountNumber").equals(x)).collect(Collectors.toList()) + "Expected:- " + details.get("AccountNumber"));

                        } else {
                            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Number is not as expected Actual:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                    "Expected:- " + details.get("AccountNumber"));
                        }
                        // Verifiying Dealar value by concatinating with account number
                        List<String> pdfDelar = accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(details.get("AccountNumber").
                                        trim().substring(0, 5) + " " + details.get("AccountNumber").
                                        trim().substring(5, 10) + " " + details.get("AccountNumber").trim().substring(10, 12))).
                                collect(Collectors.toList());
                        if (pdfDelar.size() != 0) {
                            Matcher matcher1 = Pattern.compile("[A-Z]{1,4}").matcher(pdfDelar.get(0));
                            matcher1.find();
                            String pdfDelarValue = matcher1.group();
                            delarFlag = pdfDelarValue.equals(details.get("Delar_Intralink"));
                            // Verifying Delar value
                            if (delarFlag) {

                                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Delar is as expected Actual:- " + pdfDelarValue + " Expected:- " + details.get("Delar_Intralink"));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_PDF", pdfDelarValue);

                            } else {
                                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Delar is not as expected Actual:- " + pdfDelarValue + " Expected:- " + details.get("Delar_Intralink"));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_PDF", pdfDelarValue);
                            }
                        }
                    } else if (details.get("Trust_Accounts").equals("NO")) {
                        accountNumberFlag = false;
                        delarFlag = false;
                        executionStatus.add("Account Number is Present in  LTR PDF even Trust is NO");
                    }

                }
                // If account number is more than 1 time in PDF then it comes to below condition
                else if (accountNumbersCount > 1) {
                    accountNumberFlag = false;
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Numbers and Delars are present multiple times in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                            "Expected Account Number:- " + details.get("AccountNumber") + " " + "Delar " + details.get("Delar_Intralink"));
                    delarFlag = false;
                    executionStatus.add("Account Numbers is repeated");

                }
                // If account number is not present in PDF then it comes to below condition
                else if (accountNumbersCount == 0) {
                    if (details.get("Trust_Accounts").equalsIgnoreCase("NO")) {
                        accountNumberFlag = true;
                        delarFlag = true;
                    } else {
                        accountNumberFlag = false;
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Numbers and Delars are not present in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                "Expected Account Number:- " + details.get("AccountNumber") + " " + "Delar " + details.get("Delar_Intralink"));
                        delarFlag = false;
                    }

                }
                // Verifying Failed Details while comparing Intralink values with PDF
                exectionStatusFailedFields = new HashMap<>();

                exectionStatusFailedFields.put("Primary Customer Name", primaryCustomerNameFlag);
                exectionStatusFailedFields.put("Letter Date", letterDateFlag);
                exectionStatusFailedFields.put("Stamped Date", stampedDateFlag);

                exectionStatusFailedFields.put("Joint One Customer Name", jointOneCustomerFlag);
                exectionStatusFailedFields.put("Joint Two Customer Name", jointTwoCustomerFlag);
                exectionStatusFailedFields.put("Joint Three Customer Name", jointThreeCustomerFlag);
                exectionStatusFailedFields.put("AddressLine1", addressLine1Flag);
                exectionStatusFailedFields.put("AddressLine2", addressLine2Flag);
                exectionStatusFailedFields.put("City", addressLine3Flag);
                exectionStatusFailedFields.put("PostalCode", addressLine4Flag);
                exectionStatusFailedFields.put("AccountNumber", accountNumberFlag);
                exectionStatusFailedFields.put("Dealer", delarFlag);

                exectionStatusFailedFields.put("Primary Customer Name Page2", primaryCustomerName1Flag);
                exectionStatusFailedFields.put("Address Line1 Page2", addressLine1Flag1Flag);
                exectionStatusFailedFields.put("Address Line2 Page2", addressLine2Flag1Flag);
                exectionStatusFailedFields.put("City Page2", addressLine3Flag1Flag);
                exectionStatusFailedFields.put("Postal Code Page2", addressLine4Flag1Flag);
                exectionStatusFailedFields.put("Page 3 Body", dearSirMadamBody);
                exectionStatusFailedFields.put("ASAT Date Page2", asatDateFlag);
                if (exectionStatusFailedFields.entrySet().
                        stream().filter(x -> x.getValue().equals(false)).collect(Collectors.toList()).size() > 0) {
                    executionStatus.add("Fail-> Customer Present in Generic and LTR Mismatch Fields:-" + exectionStatusFailedFields.entrySet().
                            stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                            collect(Collectors.joining(",")));
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Intralink and PDF fields are mismatched at " + exectionStatusFailedFields.entrySet().
                            stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                            collect(Collectors.joining(",")));
                } else {

                    executionStatus.add("Pass- Customer Details Present in Generic and LTR");
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  All Intralink and PDF Fields are matched");

                }
            } //If account is French IP Account then it goes to below condition
            else if (details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI") || details.get("ProductName").contains("- BNE") || details.get("ProductName").contains("- PSI") || details.get("ProductName").contains("Momentum PLUS Savings")) {


                //Verifying Page 1 details
//            Date on Page 3 (ASATDATE)
//            ->Primary Customer Name/Sole Customer Name (pages 1, 3)
//            ->Date on Page 1
//            ->Account Address line 1
//            ->Account Address line 2(Address Line 2/City/Address Line 3 + Province/State)
//            ->Account Address line 3(City/Address Line 3 + Province/State)
//            ->Account Address line 4(Postal/Zip Code)
//            ->Account Number (table)
//            ->Issuer (table)
//            -> Account Number (table) (Certificate Number)
                List<String> pdfAddressDetails = pdfPages.get(pdfPage);

                pdfTextStripper.setStartPage(pdfPage);
                pdfTextStripper.setEndPage(pdfPage);
                pdfData = pdfTextStripper.getText(pdfDocumentGeneric);
                int addressPosition = 0;
                pdfPages.get(pdfPage);
                //Validation Primary Customer Name from Intralink to PDF
                primaryCustomerNameFlag = details.get("CustomerName").trim().equals(pdfAddressDetails.get(addressPosition).trim());
                if (primaryCustomerNameFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Primary Customer is as expected Actual:- " + pdfAddressDetails.get(addressPosition) + "Expected:- " + details.get("CustomerName"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer is not as expected Actual:- " + pdfAddressDetails.get(addressPosition) + "Expected:- " + details.get("CustomerName"));
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNamePDF", pdfAddressDetails.get(addressPosition));
                //Validation Letter Date from Intralink to PDF
                letterDateFlag = isContains(details.get("ASATDATE").replace("Le", ""), pdfData);
                if (letterDateFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Letter Date in Page 1 is as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE").replace("th", ""));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Letter Date in Page 1 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE").replace("th", ""));
                }
                //Validation Letter Date from Intralink to PDF
                stampedDateFlag = isContains(details.get("MAILDATE"), pdfData);
                if (stampedDateFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Stamped date On or before in Page 1 is as expected Actual:- " + pdfData + "Expected:- " + details.get("MAILDATE").replace(", 2020", "th"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Stamped date On or before in Page 1 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("MAILDATE").replace(", 2020", "th"));
                }
                //Validation Joint One Customer from Intralink to PDF
                if (!details.get("Joint1_CustomerName").isEmpty()) {
                    String joint1CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointOneCustomerFlag = details.get("Joint1_CustomerName").trim().equals(joint1CustomerName.trim());
                    if (jointOneCustomerFlag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Joint One Customer is as expected Actual:- " + joint1CustomerName + "Expected:- " + details.get("Joint1_CustomerName"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint One Customer is not as expected Actual:- " + joint1CustomerName + "Expected:- " + details.get("Joint1_CustomerName"));
                    }
                } else {
                    jointOneCustomerFlag = true;

                }
                //Validation Joint Two Customer from Intralink to PDF
                if (!details.get("Joint2_CustomerName").isEmpty()) {
                    String joint2CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointTwoCustomerFlag = details.get("Joint2_CustomerName").trim().equals(joint2CustomerName.trim());
                    if (jointTwoCustomerFlag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Joint Two Customer is as expected Actual:- " + joint2CustomerName + "Expected:- " + details.get("Joint2_CustomerName"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint Two Customer is not as expected Actual:- " + joint2CustomerName + "Expected:- " + details.get("Joint2_CustomerName"));
                    }
                } else {
                    jointTwoCustomerFlag = true;
                }
                //Validation Joint Three Customer from Intralink to PDF
                if (!details.get("Joint3_CustomerName").isEmpty()) {
                    String joint3CustomerName = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    jointThreeCustomerFlag = details.get("Joint3_CustomerName").equals(joint3CustomerName);
                    if (jointThreeCustomerFlag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Joint Three Customer is as expected Actual:- " + joint3CustomerName + "Expected:- " + details.get("Joint3_CustomerName"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint Three Customer is not as expected Actual:- " + joint3CustomerName + "Expected:- " + details.get("Joint3_CustomerName"));
                    }
                } else {
                    jointThreeCustomerFlag = true;
                }
                String pdfAddressLine1 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                addressLine1Flag = details.get("AddressLine1_IntraLink").trim().equals(pdfAddressLine1.trim());
                //Validation Address Line 1 Customer from Intralink to PDF
                if (addressLine1Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 1 is as expected Actual:- " + pdfAddressLine1 + "Expected:- " + details.get("AddressLine1_IntraLink"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 1 is not as expected Actual:- " + pdfAddressLine1 + "Expected:- " + details.get("AddressLine1_IntraLink"));
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_PDF", pdfAddressLine1);
                if (!details.get("AddressLine2_IntraLink").isEmpty()) {
                    String pdfAddressLine2 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                    addressLine2Flag = details.get("AddressLine2_IntraLink").trim().equals(pdfAddressLine2.trim());
                    //Validation Address Line 2 Customer from Intralink to PDF
                    if (addressLine2Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 2 is as expected Actual:- " + pdfAddressLine2 + "Expected:- " + details.get("AddressLine2_IntraLink"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 2 is not as expected Actual:- " + pdfAddressLine2 + "Expected:- " + details.get("AddressLine2_IntraLink"));
                    }
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_PDF", pdfAddressLine2);
                } else {
                    addressLine2Flag = true;
                }
                //Validation Address Line 3(City) Customer from Intralink to PDF
                countryCodes = readFromExcel.getSaveValue(System.getProperty("user.dir") + "/src/main/resources/data/", "CountryCodes.xlsx", "CountryCodes", details.get("State"));
                countryCode = countryCodes.getField("NameValue").trim();
                addressLine3Flag = countryCode.equals("") ? isContains(details.get("City_IntraLink").trim(), pdfData) : isContains(details.get("City_IntraLink").trim() + ", " + countryCode, pdfData);

                String pdfAddressLine3 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                if (addressLine3Flag) {

                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 3/City is as expected Actual:- " + pdfAddressLine3 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 3/City is not as expected Actual:- " + pdfAddressLine3 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_PDF", pdfAddressLine3);

                if (!details.get("PostalCode_IntraLink").isEmpty()) {
                    addressLine4Flag = pdfData.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    //Validation Address Line 4(Postal Code) Customer from Intralink to PDF
                    if (addressLine4Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 4/Postal Code is as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 4/Postal Code is not as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    }

                } else {
                    addressLine4Flag = true;
                }
                // End of Address validation for IP

                // Retrieves IP account details from PDF
                accountDetails = getIPAccountDetailsFromLTR(verifyLtrPdfsDetailsWithExtract(pdfPageNumberGeneric));

                LogHelper.logger.info("Customer Name:- " + details.get("CustomerName") + " and Scenarios ID:- " + details.get("getScenarioId()") + " Account details are:- " + accountDetails.stream().collect(Collectors.joining(",")));
                String accountNumber = "RP N° " + StringUtils.leftPad(letterDetails.get("AccountNumber").replace(" ", ""), 15, "0").replace(" ", "").trim();
                int accountNumbersCount = accountDetails.stream().filter(x -> x.contains(accountNumber)).collect(Collectors.toList()).size();
                // It check whether intralink account present in PDF as well as only 1 account should be present instead of duplication
                if (accountNumbersCount == 1) {
                    if (details.get("Trust_Accounts").equals("YES")) {
                        accountNumberFlag = true;
                        if (accountNumberFlag) {
                            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Account Number is as expected Actual:- " + accountDetails.stream().map(x -> x.replaceAll("\\D+", "")).collect(Collectors.toList()).
                                    stream().filter(x -> details.get("AccountNumber").equals(x)).collect(Collectors.toList()) + "Expected:- " + details.get("AccountNumber"));

                        } else {
                            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Number is not as expected Actual:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                    "Expected:- " + details.get("AccountNumber"));
                        }
                        // Verifiying Dealar value by concatinating with account number
                        String pdfDelar = accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(accountNumber)).collect(Collectors.joining(",")).trim();
                        if (details.get("ProductName").contains(" - SSI")) {
                            delarFlag = pdfDelar.contains("BNE");
                        } else {
                            delarFlag = pdfDelar.contains(details.get("Delar_Intralink"));
                        }
                        // Verifying Delar value
                        if (delarFlag) {
                            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Delar is as expected Actual:- " + pdfDelar.split(" ")[1] + "Expected:- " + details.get("Delar_Intralink"));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_PDF", pdfDelar.split(" ")[3]);

                        } else {
                            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Delar is not as expected Actual:- " + pdfDelar.split(" ")[1] + "Expected:- " + details.get("Delar_Intralink"));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_PDF", pdfDelar.split(" ")[3]);
                        }
                    } else if (details.get("Trust_Accounts").equals("NO")) {
                        accountNumberFlag = false;
                        delarFlag = false;
                        executionStatus.add("Account Number is Present in  LTR PDF even Trust is NO");
                    }

                }
                // If account number is more than 1 time in PDF then it comes to below condition
                else if (accountNumbersCount > 1) {
                    accountNumberFlag = false;
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Numbers and Issuers are present multiple times in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                            "Expected Account Number:- " + details.get("AccountNumber") + " " + "Issuer " + details.get("Delar_Intralink"));
                    delarFlag = false;

                }
                // If account number is not present in PDF then it comes to below condition
                else if (accountNumbersCount == 0) {
                    if (details.get("Trust_Accounts").equalsIgnoreCase("NO")) {
                        accountNumberFlag = true;
                        delarFlag = true;
                    } else {
                        accountNumberFlag = false;
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Numbers and Issuers are not present in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                "Expected Account Number:- " + details.get("AccountNumber") + " " + "Issuer " + details.get("Delar_Intralink"));
                        delarFlag = false;
                    }
                }
                // Saves all GIC's from Excel to gics List object
                ArrayList<String> gics = new ArrayList<String>(Arrays.asList(details.get("GIC_IntraLink").split("\\|")));
                LogHelper.logger.info("Intralin GIC's are :-" + gics.stream().collect(Collectors.joining(",")));
                ArrayList<String> issuers = new ArrayList<String>(Arrays.asList(details.get("Issuer_Intralink").split("\\|")));
                LogHelper.logger.info("Intralin GIC's are :-" + issuers.stream().collect(Collectors.joining(",")));
                // Object created for GIC's
                Map<String, Boolean> gicsFlag = new HashMap<String, Boolean>();
                List<String> issuersPdf = new LinkedList<>();

                // Map<String, Boolean> issuersFlag = null;
                // Verifying all IP Account having GIC then it will goes to below condition
                if (!details.get("GIC_IntraLink").equals("") && gics.size() >= 1) {

                    // Object created for Issuers

                    for (int gic = 0; gic < gics.size(); gic++) {

                        int finalGic = gic;
                        // Verifying all IP Accounts GIC's are present in PDF
                        List<String> certificateNumber = accountDetails.stream().filter(x -> x.contains(gics.get(finalGic).trim().replace("CPG#", "CPG N° "))).collect(Collectors.toList());
                        // It check whether intralink IP account GIC is present in PDF as well as only individual GIC should be present instead of duplication
                        if (certificateNumber.size() == 1) {
                            if (details.get("Trust_Accounts").equals("YES")) {
                                String gicValue = certificateNumber.get(0);

                                gicsFlag.put(gicValue, true);
                                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  GIC Number is as expected Actual:- " + gicValue + "Expected:- " + gics.get(finalGic));
                                // It check whether intralink IP account GIC Issuer is present in PDF as well as only individual GIC Issuer should be present instead of duplication

                                if (accountDetails.stream().filter(x -> x.contains(gics.get(finalGic).replace("CPG#", "CPG N° "))).collect(Collectors.joining("")).equals(gics.get(finalGic).replace("CPG#", "CPG N° ") + " " + issuers.get(finalGic))) {
                                    issuersPdf.add(accountDetails.stream().filter(x -> x.contains(gics.get(finalGic).replace("CPG#", "CPG N° "))).collect(Collectors.joining("")).split(" ")[3] + " true");
                                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Issuer is as expected Actual:- " + accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(gics.get(finalGic)))
                                            .collect(Collectors.toList()) + "Expected:- " + issuers.get(finalGic) + "  " + gics.get(finalGic));
                                } else {
                                    issuersPdf.add(accountDetails.stream().filter(x -> x.contains(gics.get(finalGic).replace("CPG#", "CPG N° "))).collect(Collectors.joining("")).split(" ")[3] + " false");
                                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Issuer is not as expected Actual:- " + accountDetails.stream().filter(x -> x.replaceAll("\\r", "").contains(gics.get(finalGic)))
                                            .collect(Collectors.toList()) + "Expected:- " + issuers.get(finalGic) + "  " + gics.get(finalGic));
                                }

                            } else if (details.get("Trust_Accounts").equals("NO")) {
                                gicsFlag.put(gics.get(finalGic).replace("CPG#", "CPG N° "), false);
                                issuersPdf.add("| false");
                                executionStatus.add("GIC's and Issuers are Present in PDF even Trust is NO");
                            }

                        }
                        // If account number is more than 1 time in PDF then it comes to below condition
                        else if (accountDetails.stream().filter(x -> x.contains(gics.get(finalGic).trim().replace("CPG#", "CPG N° "))).collect(Collectors.toList()).size() > 1) {
                            gicsFlag.put(gics.get(finalGic).replace("CPG#", "CPG N° "), false);
                            issuersPdf.add("| false");
                            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "GIC's and Issuers are present multiple times  in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                    "Expected Account Number:- " + details.get("AccountNumber") + " " + "Issuer " + issuers.get(finalGic) + "  " + gics.get(finalGic));
                        }
                        // If account number is not present in PDF then it comes to below condition
                        else if (accountDetails.stream().filter(x -> x.contains(gics.get(finalGic).trim().replace("CPG#", "CPG N° "))).collect(Collectors.toList()).size() == 0) {
                            if (details.get("Trust_Accounts").equals("YES")) {
                                gicsFlag.put(gics.get(finalGic).replace("CPG#", "CPG N° "), false);
                                issuersPdf.add("| false");
                                executionStatus.add(gics.get(gic) + " " + issuers.get(gic));
                                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "GIC's and Issuers are not present in PDF Actual Account Details:- " + accountDetails.stream().collect(Collectors.joining(",")) +
                                        "Expected:- " + issuers.get(finalGic) + "  " + gics.get(finalGic));
                            } else if (details.get("Trust_Accounts").equals("NO")) {
                                gicsFlag.put(gics.get(finalGic).replace("CPG#", "CPG N° "), true);
                                issuersPdf.add("| true");
                                executionStatus.add("Pass:-GIC's and Issuers are not Present in PDF as Trust is NO");

                            }
                        }


                    }
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "GIC_PDF",
                            accountDetails.stream().filter(x -> x.contains("CPG N° ")).collect(Collectors.toList()).stream().map(x -> x.split(" ")[0] + " " + x.split(" ")[1] + " " + x.split(" ")[2]).collect(Collectors.joining("|")));

                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Issuer_PDF", accountDetails.stream().filter(x -> x.contains("CPG N° ")).collect(Collectors.toList()).stream().map(x -> x.split(" ")[3]).collect(Collectors.joining("|")));

                    issuerFlag = issuersPdf.stream().filter(x -> x.contains("false")).collect(Collectors.toList()).size() < 1;

                }

                //Verifying Page 2 details
//             ->Date on Page 3 (MAILDATE)
//             ->Primary Customer Name/Sole Customer Name (pages 1, 3)
//            ->Account Address line 1
//            ->Account Address line 2(Address Line 2/City/Address Line 3 + Province/State)
//            ->Account Address line 3(City/Address Line 3 + Province/State)
//            ->Account Address line 4(Postal/Zip Code)

                // Validating English Second page PDF

                pdfTextStripper.setStartPage(pdfPage + 1);
                pdfTextStripper.setEndPage(pdfPage + 1);
                pdfData = pdfTextStripper.getText(pdfDocumentGeneric);
                List<String> addressDetailsPage2 = getAddressDetails(pdfPage, pdfDocumentGeneric);
                int addressPosition_PageTwo = 0;
                dearSirMadamBody = pdfData.replaceAll("\r", "").contains(details.get("Page3Body"));
                //Validation Mail date from extract to PDF
                if (dearSirMadamBody) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Page 3 body is as expected Actual:- " + pdfData + "Expected:- " + details.get("Page3Body"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Page 3 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("Page3Body"));
                }
                //Validation ASAT date from extract to PDF
                asatDateFlag = isContains(details.get("ASATDATE"), pdfData);
                if (asatDateFlag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  ASAT Date is as expected Actual:- " + pdfData + "Expected:- " + "April 30th, 2019");

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "ASAT Date is not as expected Actual:- " + pdfData + "Expected:- " + "April 30th, 2019");
                }

                primaryCustomerName1Flag = details.get("CustomerName").trim().equals(addressDetailsPage2.get(addressPosition_PageTwo).trim());
                //Validation Primary Customer Name from Intralink to PDF
                if (primaryCustomerName1Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Primary Customer in Page 2 is as expected Actual:- " + addressDetailsPage2.get(addressPosition_PageTwo) + "Expected:- " + details.get("CustomerNameExtract"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer in Page 2 is not as expected Actual:- " + addressDetailsPage2.get(addressPosition_PageTwo) + "Expected:- " + details.get("CustomerNameExtract"));
                }
                //Validation Address Line 1 from Intralink to PDF
                addressLine1Flag1Flag = details.get("AddressLine1_IntraLink").trim().equals(addressDetailsPage2.get(addressPosition_PageTwo = addressPosition_PageTwo + 1).trim());
                //Validation Address Line 2 from Intralink to PDF
                if (!details.get("AddressLine2_IntraLink").isEmpty()) {
                    String pdfAddressLine2Page2 = addressDetailsPage2.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
                    addressLine2Flag1Flag = details.get("AddressLine2_IntraLink").trim().equals(pdfAddressLine2Page2.trim());
                    if (addressLine2Flag1Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Primary Customer in Page 2 is as expected Actual:- " + pdfAddressLine2Page2 + "Expected:- " + details.get("AddressLine2_IntraLink"));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer in Page 2 is not as expected Actual:- " + pdfAddressLine2Page2 + "Expected:- " + details.get("AddressLine2_IntraLink"));
                    }

                } else {
                    addressLine2Flag1Flag = true;
                }

                //Validation Address Line 3(City) from Intralink to PDF

                countryCodes = readFromExcel.getSaveValue(System.getProperty("user.dir") + "/src/main/resources/data/", "CountryCodes.xlsx", "CountryCodes", details.get("State"));
                countryCode = countryCodes.getField("NameValue").trim();
                addressLine3Flag1Flag = countryCode.equals("") ? isContains(details.get("City_IntraLink").trim(), pdfData) : isContains(details.get("City_IntraLink").trim() + ", " + countryCode, pdfData);
                String pdfAddressLine3Page2 = pdfAddressDetails.get(addressPosition_PageTwo = addressPosition_PageTwo + 1);
                if (addressLine3Flag1Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 3/City in Page 2 is as expected Actual:- " + pdfAddressLine3Page2 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 3/City in Page 2 is not as expected Actual:- " + pdfAddressLine3Page2 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);
                }
                //Validation Address Line 4(Postal Code) from Intralink to PDF
                if (!details.get("PostalCode_IntraLink").isEmpty()) {
                    addressLine4Flag1Flag = pdfData.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));


                    if (addressLine4Flag1Flag) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 4/Postal Code is as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));

                    } else {
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 4/Postal Code is not as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    }

                } else {
                    addressLine4Flag1Flag = true;
                }
                // Verifying Failed Details while comparing Intralink values with PDF
                exectionStatusFailedFields = new HashMap<>();

                exectionStatusFailedFields.put("Primary Customer Name", primaryCustomerNameFlag);
                exectionStatusFailedFields.put("Letter Date", letterDateFlag);
                exectionStatusFailedFields.put("Stamped Date", stampedDateFlag);
                exectionStatusFailedFields.put("Joint One Customer Name", jointOneCustomerFlag);
                exectionStatusFailedFields.put("Joint Two Customer Name", jointTwoCustomerFlag);
                exectionStatusFailedFields.put("Joint Three Customer Name", jointThreeCustomerFlag);
                exectionStatusFailedFields.put("AddressLine1", addressLine1Flag);
                exectionStatusFailedFields.put("AddressLine2", addressLine2Flag);
                exectionStatusFailedFields.put("City", addressLine3Flag);
                exectionStatusFailedFields.put("PostalCode", addressLine4Flag);
                exectionStatusFailedFields.put("AccountNumber", accountNumberFlag);
                exectionStatusFailedFields.put("Dealer", delarFlag);
                exectionStatusFailedFields.put("Issuer", issuersPdf.size() <= 0 || issuerFlag);
                exectionStatusFailedFields.put("GICs", gicsFlag.size() <= 0 || gicsFlag.values().stream().filter(x -> x == false).collect(Collectors.toList()).size() < 1);
                exectionStatusFailedFields.put("Primary Customer Name Page2", primaryCustomerName1Flag);
                exectionStatusFailedFields.put("Address Line1 Page2", addressLine1Flag1Flag);
                exectionStatusFailedFields.put("Address Line2 Page2", addressLine2Flag1Flag);
                exectionStatusFailedFields.put("City Page2", addressLine3Flag1Flag);
                exectionStatusFailedFields.put("Postal Code Page2", addressLine4Flag1Flag);
                exectionStatusFailedFields.put("Page 3 Body", dearSirMadamBody);
                exectionStatusFailedFields.put("ASAT Date Page2", asatDateFlag);
                if (exectionStatusFailedFields.entrySet().
                        stream().filter(x -> x.getValue().equals(false)).collect(Collectors.toList()).size() > 0) {
                    executionStatus.add("Fail-> Mismatch Fields:-" + exectionStatusFailedFields.entrySet().
                            stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                            collect(Collectors.joining(",")));
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Intralink and PDF fields are mismatched at " + exectionStatusFailedFields.entrySet().
                            stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                            collect(Collectors.joining(",")));

                } else {
                    executionStatus.add("Pass- Customer Details Present in Generic and LTR");
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  All Intralink and PDF Fields are matched");
                }

            }
            // Verifying all acoounts(Savings and IP) associated to the customer is present in single pdf or not
            if (!(details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI") || (details.get("ProductName").contains("- BNE") || details.get("ProductName").contains("- PSI")))) {
                accountDetails.addAll(getIPAccountDetailsFromLTR(verifyLtrPdfsDetailsWithExtract(pdfPageNumberGeneric)).stream().map(x -> x.trim()).collect(Collectors.toList()));
            } else if ((details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI") || (details.get("ProductName").contains("- BNE") || details.get("ProductName").contains("- PSI")))) {
                accountDetails.addAll(getSavingsAccountDetailsFromLTR(verifyLtrPdfsDetailsWithExtract(pdfPageNumberGeneric)).stream().map(x -> x.trim()).collect(Collectors.toList()));
            }
            // Verifying all acoounts(Savings and IP) associated to the customer is present in single pdf or not
            List<String> accsInSinglePdf = verifyingAllAccountsPresentInSinglePDFLTR(accountDetails);

            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AccountsStatus", accsInSinglePdf.stream().collect(Collectors.joining("|")));


        }
        // If Customer address is present more than once in entire PDF Letter then it enters to below condition
        else if (pdfPages.size() > 1) {

            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Multiple PDF's are generated in Pages-" + verifyCustomerHavingSinglePdf(pdfDocument).values().stream().map(x -> x + ", ").collect(Collectors.toList()) +
                    "for the customer Name:-" + details.get("CustomerName") +
                    "Joint1 Customer:- " + details.get("Joint1_CustomerName") + "<br />" +
                    "Joint2 Customer:- " + details.get("Joint2_CustomerName") + "<br />" +
                    "Joint3 Customer:- " + details.get("Joint3_CustomerName") + "<br />" +
                    "Address Line1:- " + details.get("AddressLine1_IntraLink") + "<br />" +
                    "Address Line2:- " + details.get("AddressLine2_IntraLink") + "<br />" +
                    "City:- " + details.get("AddressLine2_IntraLink") + "<br />" + "<br />" +
                    "Postal Code:-" + details.get("PostalCode_IntraLink"));
            executionStatus.add("Multiple Pdf's are generated in Pages:-" + pdfNumbers.stream().map(x -> x + " ").collect(Collectors.toList()));
        }
        // If Customer address is not present in entire PDF Letter then it enters to below condition


        return executionStatus;
    }

    /**
     * This function verifies all Intralink English customer accounts details with PDF Generics and LTR when accounts are not present in Roll up PDF's
     *
     * @return Execution status
     * @throws IOException
     */
    public String verifyLtrPdfsDetailsWithExtract(String genericPageNumber) throws IOException {
        pdfFileLTR = details.get("PDFLTRName");
        pdfDocumentLTR = new ReadTextFile().readPdfDocument(System.getProperty("user.dir") + "/src/main/resources/data/" + ConfigurationManager.getBundle().getString("environment") + "/PDFs/" + pdfFileLTR);
        String pdfData = null;

        /*Iterating all PDF pages for verifying customer accounts and Incrementing with 2 pages because each customer
        contains 2 pages*/
        for (int page = 0; page <= pdfDocumentLTR.getNumberOfPages() - 1; page = page + 2) {
            pdfTextStripper = new PDFTextStripper();
            pdfTextStripper.setStartPage(page + 1);
            pdfTextStripper.setEndPage(page + 1);
            pdfData = pdfTextStripper.getText(pdfDocumentLTR);

            if (pdfData.contains(genericPageNumber)) {
                break;
            }

        }
        return pdfData;
    }

    /**
     * This function verifies all Intralink English customer PAFT accounts details with PDF
     *
     * @param pdfDocument- PDF Document
     * @return Execution status
     * @throws IOException
     */
    public List<String> verifyingPAFTPdfDetailsWithExtract(PDDocument pdfDocument) throws
            IOException, FilloException {
        String pdfData = null;
        // Page 1 Variables
        boolean primaryCustomerNameFlag = false;
        boolean mailDateFlag = false;
        boolean letterDateFlag = false;
        boolean addressLine1Flag = false;
        boolean addressLine2Flag = false;
        boolean addressLine3Flag = false;
        boolean addressLine4Flag = false;

        int pdfCount = 0;
        // Creating object for Execution status
        List<String> executionStatus = new LinkedList<>();
        // Creating object for mismatch fields while comparing with excel value with pdf fields
        Map<String, Boolean> exectionStatusFailedFields = null;
        HashMap<Integer, List<String>> pdfPages = verifyCustomerHavingSinglePdfPAFT(pdfDocument);
        Set<Integer> pdfNumbers = pdfPages.keySet();
        int pdfPage = 0;
        if (pdfPages.size() == 1) {
            pdfPage = (Integer) pdfPages.keySet().toArray()[0];
        }

        List<String> accountDetails = null;
        // Customer address should present only once in entire PDF Letter
        if (pdfPages.size() == 1) {
            LogHelper.logger.info("Customer Name:- " + details.get("CustomerName") + " and Scenarios ID:- " + details.get("getScenarioId()") + " " + "is located in PDF Page Number:-" + pdfPage);
            pdfTextStripper = new PDFTextStripper();
            pdfTextStripper.setStartPage(pdfPage);
            pdfTextStripper.setEndPage(pdfPage);
            pdfData = pdfTextStripper.getText(pdfDocument);

            List<String> pdfAddressDetails = pdfPages.get(pdfPage);

            //pdfAddressDetails
            int addressPosition = 0;
            pdfPages.get(pdfPage);
            //Validation Primary Customer Name from Intralink to PDF
            primaryCustomerNameFlag = details.get("CustomerName").equals(pdfAddressDetails.get(addressPosition).trim());
            if (primaryCustomerNameFlag) {
                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Primary Customer is as expected Actual:- " + pdfAddressDetails.get(addressPosition) + "Expected:- " + details.get("CustomerName"));

            } else {
                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer is not as expected Actual:- " + pdfAddressDetails.get(addressPosition) + "Expected:- " + details.get("CustomerName"));
            }
            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNamePDF", pdfAddressDetails.get(addressPosition));
            //Validation Letter Date from Intralink to PDF
            letterDateFlag = isContains(details.get("ASATDATE").replace("th", ""), pdfData);
            if (letterDateFlag) {
                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Letter Date in Page 1 is as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE").replace("th", ""));

            } else {
                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Letter Date in Page 1 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("ASATDATE").replace("th", ""));
            }

            //Validation Mail Date from Intralink to PDF
            mailDateFlag = isContains(details.get("MAILDATE").trim(), pdfData);
            if (mailDateFlag) {
                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Mail Date in Page 1 is as expected Actual:- " + pdfData + "Expected:- " + details.get("MAILDATE"));

            } else {
                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Mail Date in Page 1 is not as expected Actual:- " + pdfData + "Expected:- " + details.get("MAILDATE"));
            }
            String pdfAddressLine1 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
            //Validation Address Line 1 Customer from Intralink to PDF
            addressLine1Flag = details.get("AddressLine1_IntraLink").trim().equals(pdfAddressLine1.trim());
            if (addressLine1Flag) {
                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 1 is as expected Actual:- " + pdfAddressLine1 + "Expected:- " + details.get("AddressLine1_IntraLink"));

            } else {
                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 1 is not as expected Actual:- " + pdfAddressLine1 + "Expected:- " + details.get("AddressLine1_IntraLink"));
            }
            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_PDF", pdfAddressLine1);
            //Validation Address Line 2 Customer from Intralink to PDF
            if (!details.get("AddressLine2_IntraLink").isEmpty()) {
                String pdfAddressLine2 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
                addressLine2Flag = details.get("AddressLine2_IntraLink").trim().equals(pdfAddressLine2.trim());

                if (addressLine2Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 2 is as expected Actual:- " + pdfAddressLine2 + "Expected:- " + details.get("AddressLine2_IntraLink"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 2 is not as expected Actual:- " + pdfAddressLine2 + "Expected:- " + details.get("AddressLine2_IntraLink"));
                }
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_PDF", pdfAddressLine2);
            } else {
                addressLine2Flag = true;
            }
            countryCodes = readFromExcel.getSaveValue(System.getProperty("user.dir") + "/src/main/resources/data/", "CountryCodes.xlsx", "CountryCodes", details.get("State"));
            String countryCode = countryCodes.getField("NameValue").trim();
            addressLine3Flag = countryCode.equals("") ? isContains(details.get("City_IntraLink").trim(), pdfData) : isContains(details.get("City_IntraLink").trim() + ", " + countryCode, pdfData);
            String pdfAddressLine3 = pdfAddressDetails.get(addressPosition = addressPosition + 1);
            //Validation Address Line 3(City) Customer from Intralink to PDF
            if (addressLine3Flag) {

                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 3/City is as expected Actual:- " + pdfAddressLine3 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);

            } else {
                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 3/City is not as expected Actual:- " + pdfAddressLine3 + "Expected:- " + details.get("City_IntraLink") + ", " + countryCode);
            }
            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_PDF", pdfAddressLine3);
            if (!details.get("PostalCode_IntraLink").isEmpty()) {

                addressLine4Flag = pdfData.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink"));
                //Validation Address Line 4(Postal Code) Customer from Intralink to PDF
                if (addressLine4Flag) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Address Line 4/Postal Code is as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink"));

                } else {
                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 4/Postal Code is not as expected Actual:- " + pdfData + "Expected:- " + details.get("PostalCode_IntraLink"));
                }

            } else {
                addressLine4Flag = true;
            }
            // Verifying Failed Details while comparing Intralink values with PDF
            exectionStatusFailedFields = new HashMap<>();


            exectionStatusFailedFields.put("Primary Customer Name", primaryCustomerNameFlag);
            exectionStatusFailedFields.put("Letter Date", letterDateFlag);
            exectionStatusFailedFields.put("AddressLine1", addressLine1Flag);
            exectionStatusFailedFields.put("AddressLine2", addressLine2Flag);
            exectionStatusFailedFields.put("City", addressLine3Flag);
            exectionStatusFailedFields.put("PostalCode", addressLine4Flag);
            if (exectionStatusFailedFields.entrySet().
                    stream().filter(x -> x.getValue().equals(false)).collect(Collectors.toList()).size() > 0) {
                executionStatus.add("Fail-> Mismatch Fields:-" + exectionStatusFailedFields.entrySet().
                        stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                        collect(Collectors.joining(",")));

                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Intralink and PDF fields are mismatched at " + exectionStatusFailedFields.entrySet().
                        stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                        collect(Collectors.joining(",")));

            } else {
                if (details.get("Trust_Accounts").equals("YES")) {
                    executionStatus.add("Pass");
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  All Intralink and PDF Fields are matched");
                } else if (details.get("Trust_Accounts").equals("NO")) {
                    executionStatus.add("Fail");
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "******Pass Statement*****  Trust No customers are present in PAFT");
                }


            }
        }
        // If account number is more than 1 time in PDF then it comes to below condition
        else if (pdfPages.size() > 1) {

            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Multiple PDF's are generated in Pages-" + pdfNumbers.stream().map(x -> x + " ").collect(Collectors.toList()) +
                    "for the customer Name:-" + details.get("CustomerName") +
                    "Address Line1:- " + details.get("AddressLine1_IntraLink") + "<br />" +
                    "Address Line2:- " + details.get("AddressLine2_IntraLink") + "<br />" +
                    "City:- " + details.get("AddressLine2_IntraLink") + "<br />" + "<br />" +
                    "Postal Code:-" + details.get("PostalCode_IntraLink"));
            executionStatus.add("Multiple Pdf's are generated in Pages:-" + pdfNumbers.stream().map(x -> x + " ").collect(Collectors.toList()));
        }
        // If account number is not present in PDF then it comes to below condition
        else {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "PDF is not generated for the customer Name:-" + details.get("CustomerName") +
                    "Address Line1:- " + details.get("AddressLine1_IntraLink") + "<br />" +
                    "Address Line2:- " + details.get("AddressLine2_IntraLink") + "<br />" +
                    "City:- " + details.get("AddressLine2_IntraLink") + "<br />" + "<br />" +
                    "Postal Code:-" + details.get("PostalCode_IntraLink"));
            executionStatus.add("Pdf is not generated with customer details:-" + details.get("CustomerName") + "\n"
                    + "\n" + details.get("AddressLine1_IntraLink") + "\n" +
                    details.get("AddressLine2_IntraLink") + "\n" + details.get("AddressLine2_IntraLink") + "\n" +
                    details.get("PostalCode_IntraLink"));


        }

        pdfDocument.close();

        return executionStatus;


    }

    /**
     * This function verifies substring value present in PDF Source or not
     *
     * @param source-  PDF Content
     * @param subItem- subString
     * @return Boolean- Sub string present in PDF Content or not
     */
    private Boolean isContains(String source, String subItem) {
        String searchValue = "\\b" + source + "\\b";
        Pattern pattern = Pattern.compile(searchValue);
        Matcher matcher = pattern.matcher(subItem);
        return matcher.find();
    }

    /**
     * This function verifies french customer IP Details with Extract
     *
     * @return List- Execution Status
     * @throws FilloException
     * @throws IOException
     */
    private List<String> ipDetailsVerificationFrench() throws FilloException, IOException {
        boolean addressLine1Flag = false;
        boolean addressLine2Flag = false;
        boolean cityFlag = false;
        boolean postalCodeFlag = false;
        boolean gicFlag = false;
        boolean accountFlag = false;
        boolean primaryCustomerNameFlag = false;
        boolean jointOneCustomerNameFlag = false;
        boolean jointTwoCustomerNameFlag = false;
        boolean jointThreeCustomerNameFlag = false;
        boolean issuerFlag = false;
        boolean delarFlag = false;
        // List object created to check which fields are failed
        flagList = new ArrayList<Boolean>();
        // List object created for Execution status
        List<String> executionStatus = new LinkedList<>();
        LogHelper.logger.info("Validating Intralink and Extract Fields");
        // Product contains BNE/PSE, Currency - USD then 2 IP records should present in Extract file (Exculuding GIC Records)
        // Product contains BNE/PSE, Currency - CAD then 1 IP records should present in Extract file (Exculuding GIC Records)
        if ((details.get("Currency").equals("USD") && details.get("ProductName").contains("BNE") && extractAccounts.stream().filter(x -> x.get("BASACT").trim().
                equals(StringUtils.leftPad(letterDetails.get("AccountNumber").
                                replace(" ", ""), 15, "0").
                        replace(" ", "").trim()) && x.get("ACCTNO").
                replace(" ", "").trim().
                equals("RP#" + StringUtils.leftPad(details.get("AccountNumber").
                        replace(" ", "").trim(), 15, "0"))
                && x.get("CERTNUM").trim().equals("")).collect(Collectors.toList()).size() == 2 || details.get("Currency").equals("CAD") && details.get("ProductName").contains("BNE")
                && extractAccounts.stream().filter(x -> x.get("BASACT").trim().
                equals(StringUtils.leftPad(letterDetails.get("AccountNumber").
                                replace(" ", ""), 15, "0").
                        replace(" ", "").trim()) && x.get("ACCTNO").
                replace(" ", "").trim().
                equals("RP#" + StringUtils.leftPad(details.get("AccountNumber").
                        replace(" ", "").trim(), 15, "0"))
                && x.get("CERTNUM").trim().equals("")).collect(Collectors.toList()).size() == 1) ||

                (details.get("Currency").equals("USD") && details.get("ProductName").contains("PSI") && extractAccounts.stream().filter(x -> x.get("BASACT").trim().
                        equals(StringUtils.leftPad(letterDetails.get("AccountNumber").
                                        replace(" ", ""), 15, "0").
                                replace(" ", "").trim()) && x.get("ACCTNO").
                        replace(" ", "").trim().
                        equals("RP#" + StringUtils.leftPad(details.get("AccountNumber").
                                replace(" ", "").trim(), 15, "0"))
                        && x.get("CERTNUM").trim().equals("")).collect(Collectors.toList()).size() == 0 || details.get("Currency").equals("CAD") && details.get("ProductName").contains("PSI")
                        && extractAccounts.stream().filter(x -> x.get("BASACT").trim().
                        equals(StringUtils.leftPad(letterDetails.get("AccountNumber").
                                        replace(" ", ""), 15, "0").
                                replace(" ", "").trim()) && x.get("ACCTNO").
                        replace(" ", "").trim().
                        equals("RP#" + StringUtils.leftPad(details.get("AccountNumber").
                                replace(" ", "").trim(), 15, "0"))
                        && x.get("CERTNUM").trim().equals("")).collect(Collectors.toList()).size() == 0)) {
            // List object are created for different fields
            List<String> primaryCustomerName = new LinkedList<>();
            List<String> addressLine1 = new LinkedList<>();
            List<String> addressLine2 = new LinkedList<>();
            List<String> city = new LinkedList<>();
            List<String> postal = new LinkedList<>();
            List<String> gic = new LinkedList<>();
            List<String> ip = new LinkedList<>();
            List<String> issuer = new LinkedList<>();
            String delar = "";
            // GIC's are split based on pipe symbol
            ArrayList<String> gics = new ArrayList<String>(Arrays.asList(details.get("GIC_IntraLink").split("\\|")));
            // Issuers are split based on pipe symbol
            ArrayList<String> issuers = new ArrayList<String>(Arrays.asList(details.get("Issuer_Intralink").split("\\|")));

            if (!details.get("GIC_IntraLink").equals("") && gics.size() >= 1) {
                //Adding empty string for verifying IP Account in Extract field
                gics.add(0, " ");
                issuers.add(0, " ");
            }
            // Verifying GIC's and IP Acconts with Extract
            for (int i = 0; i < gics.size(); i++) {
                // Iterating all accounts present in Extract file
                //Compares Intralink account details with Extract values like AddressLine1, City, AddressLine2, PostalCode,GIC's
                for (Map<String, String> extractAccount : extractAccounts) {
                    //Validation Address Line 1 from Intralink to Extract
                    addressLine1Flag = extractAccount.get("ADDRESS1").trim().equals(details.get("AddressLine1_IntraLink").trim());
                    LogHelper.logger.info("Address Line 1 Intralink is " + details.get("AddressLine1_IntraLink").trim() + "and Address Line 1 Extract is " + extractAccount.get("ADDRESS1").trim());
                    String countryCode = countryCodes.getField("NameValue").trim();
                    //Validation Address Line 2 from Intralink to Extract
                    addressLine2Flag = extractAccount.values().stream().map(String::trim).collect(Collectors.toList()).stream().anyMatch(x -> x.equals(details.get("AddressLine2_IntraLink").trim()));
                    LogHelper.logger.info("Address Line 2 Intralink is " + details.get("AddressLine2_IntraLink").trim() + "and Extract record data is  " + extractAccount.values());
                    //Validation City from Intralink to Extract
                    cityFlag = extractAccount.values().stream().map(String::trim).collect(Collectors.toList()).stream().filter(x -> x.replaceAll("\\s+", "").contains(details.get("City_IntraLink").replaceAll("\\s+", "").trim() + countryCode) == true).count() == 1;
                    LogHelper.logger.info("City Intralink is " + details.get("City_IntraLink").trim() + countryCode + "and Extract record data is  " + extractAccount.values());
                    /*Pattern pattern = Pattern.compile("\\d*[A-Z]{1,}\\d*");

                    Matcher matcher = pattern.matcher(details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));

                    //Validation Postal Code from Intralink to Extract
                    if (matcher.find()) {

                        if (extractAccount.values().stream().map(String::trim).collect(Collectors.toList()).stream().filter(x -> x.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replace(" ", "").trim()) == true).count() == 1) {


                            postalCodeFlag = true;

                        } else {

                            postalCodeFlag = false;

                        }


                    } else {

                        postalCodeFlag = extractAccount.get("POSTCD").replaceAll("\\s+", "").equals("");

                    }*/
                    postalCodeFlag = extractAccount.values().stream().map(String::trim).collect(Collectors.toList()).stream().filter(x -> x.replace("\\s+", "").contains(details.get("PostalCode_IntraLink").replace(" ", "").trim()) == true).count() == 1;
                    LogHelper.logger.info("Postal Code Intralink is " + details.get("PostalCode_IntraLink").trim() + countryCode + "and Extract record data is  " + extractAccount.values());
                    //Validation Account Number from Intralink to Extract
                    accountFlag = extractAccount.get("ACCTNO").trim().equals("RP#" + StringUtils.leftPad(details.get("AccountNumber").replace(" ", "").trim(), 15, "0"));
                    LogHelper.logger.info("Account Number Intralink is " + details.get("AccountNumber").trim() + "and Extract Account number is  " + extractAccount.get("ACCTNO").replaceAll(" ", "").trim());
                    //Validation GIC Number from Intralink to Extract
                    gicFlag = extractAccount.get("CERTNUM").trim().equals(gics.get(i).replace(" ", "").trim());
                    LogHelper.logger.info("GIC Intralink is " + details.get("GIC_IntraLink").trim() + "and Extract GIC is  " + extractAccount.get("CERTNUM").trim());
                    //Validation Primary Customer Name from Intralink to Extract
                    primaryCustomerNameFlag = extractAccount.get("CSTNAM01").trim().equals(details.get("CustomerName").trim().toUpperCase());
                    LogHelper.logger.info("Primary Customer Name Intralink is " + details.get("CustomerName").trim() + "and Extract Primary Customer Name is  " + extractAccount.get("CSTNAM01").trim());
                    //Validation Joint 1 Customer Name from Intralink to Extract
                    jointOneCustomerNameFlag = extractAccount.get("CSTNAM02").trim().equals(details.get("Joint1_CustomerName").trim().toUpperCase());
                    LogHelper.logger.info("Joint One Customer Name Intralink is " + details.get("Joint1_CustomerName").trim() + "and Extract Joint One is  " + extractAccount.get("CSTNAM02").trim());
                    //Validation Joint 2 Customer Name from Intralink to Extract
                    jointTwoCustomerNameFlag = extractAccount.get("CSTNAM03").trim().equals(details.get("Joint2_CustomerName").trim().toUpperCase());
                    LogHelper.logger.info("Joint Two Customer Name Intralink is " + details.get("Joint2_CustomerName").trim() + "and Extract Joint Two is  " + extractAccount.get("CSTNAM03").trim());
                    //Validation Joint 3 Customer Name from Intralink to Extract
                    jointThreeCustomerNameFlag = extractAccount.get("CSTNAM04").trim().equals(details.get("Joint3_CustomerName").trim().toUpperCase());
                    LogHelper.logger.info("Joint Three Customer Name Intralink is " + details.get("Joint3_CustomerName").trim() + "and Extract Joint Three is  " + extractAccount.get("CSTNAM04").trim());
                    // IP Record contains Delar
                    if (extractAccount.get("CERTNUM").trim().equals("")) {
                        delarFlag = extractAccount.get("ISSUER").trim().equals(details.get("Delar_Intralink").trim());
                        if (addressLine1Flag && addressLine2Flag && cityFlag && postalCodeFlag
                                && primaryCustomerNameFlag && jointOneCustomerNameFlag && jointTwoCustomerNameFlag && jointThreeCustomerNameFlag
                                && accountFlag && delarFlag && gicFlag) {
                            flagList.add(true);

                        } else {
                            flagList.add(false);
                        }
                    }
                    //GIC Record contains Issuer
                    else if (!extractAccount.get("CERTNUM").trim().equals("")) {
                        issuerFlag = extractAccount.get("ISSUER").trim().equals(issuers.get(i).trim());
                        if (addressLine1Flag && addressLine2Flag && cityFlag && postalCodeFlag
                                && primaryCustomerNameFlag && jointOneCustomerNameFlag && jointTwoCustomerNameFlag && jointThreeCustomerNameFlag
                                && accountFlag && issuerFlag && gicFlag) {
                            flagList.add(true);
                        } else {
                            flagList.add(false);
                        }
                    }


                    // Verifying is account fields are matched with Intralink fields with respect to multiple records in extract file


                }

                // Checks result based on 3 points.
//            1. Checks is account is found in Extract file
//            2. Checks why account is not present in Extract file
//            3. Checks is any record is duplicated instead of maintaining 1 unique record
                String result = flagList.stream().filter(x -> x.equals(true)).collect(Collectors.toList()).size() == 1 ? "Only 1 record is present in extract file for 1 account of Intralink" :
                        flagList.stream().filter(x -> x.equals(true)).collect(Collectors.toList()).size() > 1 ? "Intralink account is present more than 1 record in extract"
                                : "Intralink account is not present in extract";
                //When all fields are passed then it comes to below condition and only 1 record is present in Extract
                if (result.equals("Only 1 record is present in extract file for 1 account of Intralink")) {
                    // Out of multiple records of extract file checks which record is matching with Intralink values

                    int position = flagList.indexOf(true);
                    primaryCustomerName.add(extractAccounts.get(position).get("CSTNAM01").trim());
                    addressLine1.add(extractAccounts.get(position).get("ADDRESS1").trim());
                    addressLine2.add(extractAccounts.get(position).get("ADDRESS 2").trim());
                    city.add(extractAccounts.get(position).get("ADDRESS 3").trim());
                    postal.add(extractAccounts.get(position).get("POSTCD").trim());
                    ip.add(extractAccounts.get(position).get("ACCTNO").trim());
                    gic.add(extractAccounts.get(position).get("CERTNUM").trim());
                    if (!extractAccounts.get(position).get("CERTNUM").trim().equals("")) {
                        issuer.add(extractAccounts.get(position).get("ISSUER").trim());
                    } else {
                        delar = extractAccounts.get(position).get("ISSUER").trim();
                    }
                    // Negative Testing-> Account status is Not Close, Trust Account - YES
                    if (!details.get("AccountStatus_Intralink").equalsIgnoreCase("Close") && details.get("Trust_Accounts").equalsIgnoreCase("YES") && details.get("ProductName").contains(" PSI") &&
                            extractAccounts.get(position).get("ACCTNO").replace(" ", "").trim().
                                    equals("RP#" + StringUtils.leftPad(details.get("AccountNumber").
                                            replace(" ", "").trim(), 15, "0"))
                            && extractAccounts.get(position).get("CERTNUM").trim().equals("")) {
                        executionStatus.add("Fail (SSI Account containing IP is present in Extract)");
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "IP Account record containing product " + details.get("ProductName") + " is present in Extract");
                    }
                    // Account status is Not Close, Trust Account - YES - PASS
                    if (!details.get("AccountStatus_Intralink").equalsIgnoreCase("Close") && details.get("Trust_Accounts").equalsIgnoreCase("YES")) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  1 extract record field values are matched with Intralink Customer field values" +
                                "whose IP Account is " + extractAccounts.get(position).get("ACCTNO").trim() + " and GIC# is " + extractAccounts.get(position).get("CERTNUM").trim());
                        executionStatus.add("Pass");
                        // All fields are updated at once when GIC max number size reaches
                        if (i == gics.size() - 1) {
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNameExtract", primaryCustomerName.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", addressLine1.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_RawDataSheet", addressLine2.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_RawDataSheet", city.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "PostalCode_RawDataSheet", postal.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "GIC_RawDataSheet", gic.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Issuer_Extract", issuer.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_Extract", delar);
                        }
                    }
                    // Account status is  Close, Trust Account - YES - FAIL
                    else if (details.get("AccountStatus_Intralink").equalsIgnoreCase("Close") && details.get("Trust_Accounts").equalsIgnoreCase("YES")) {

                        executionStatus.add("Fail (Close account is present in Extract)");
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "1 extract record field values are matched with Intralink Customer field values because Account Status is " + details.get("AccountStatus_Intralink")
                                + " and Acccount trust value is " + details.get("Trust_Accounts") + " whose IP Account is " + extractAccounts.get(position).get("ACCTNO").trim() + " and GIC# is " + extractAccounts.get(position).get("CERTNUM").trim());
                        // All fields are updated at once when GIC max number size reaches
                        if (i == gics.size() - 1) {
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNameExtract", primaryCustomerName.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", addressLine1.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_RawDataSheet", addressLine2.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_RawDataSheet", city.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "PostalCode_RawDataSheet", postal.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "GIC_RawDataSheet", gic.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Issuer_Extract", issuer.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_Extract", delar);
                        }

                    }
                    //  Trust Account - NO
                    else if (details.get("Trust_Accounts").equalsIgnoreCase("NO")) {
                        executionStatus.add("Fail (Trust No accounts are present in Extract)");
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "1 extract record field values are matched with Intralink Customer field values because it is not trust account"
                                + " whose IP Account is " + extractAccounts.get(position).get("ACCTNO").trim() + " and GIC# is " + extractAccounts.get(position).get("CERTNUM").trim());

                        // All fields are updated at once when GIC max number size reaches
                        if (i == gics.size() - 1) {
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNameExtract", primaryCustomerName.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", addressLine1.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_RawDataSheet", addressLine2.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_RawDataSheet", city.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "PostalCode_RawDataSheet", postal.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "GIC_RawDataSheet", gic.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Issuer_Extract", issuer.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_Extract", delar);
                        }

                    }

                }
                // Intralink Account fields are not matching with Extract fields
                else if (result.equals("Intralink account is not present in extract")) {
                    // Account is Trust account
                    if (details.get("Trust_Accounts").equalsIgnoreCase("Yes")) {
                        // Account is closed, So account shouild not present in extract file
                        if (details.get("AccountStatus_Intralink").equalsIgnoreCase("Close")) {

                            primaryCustomerName.add("Not Found in extract due to close account");
                            addressLine1.add("Not Found in extract due to close account");
                            addressLine2.add("Not Found in extract due to close account");
                            city.add("Not Found in extract due to close account");
                            postal.add("Not Found in extract due to close account");
                            gic.add("Not Found in extract due to close account");
                            issuer.add("Not Found in extract due to close account");
                            if (extractAccounts.get(i).get("CERTNUM").trim().equals(""))
                                delar = "Not Found in extract due to close account";
                            executionStatus.add("Pass (Not present in Extract-Account RelationShip-> Close )");
                            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  No Records found in Extract because Account Relation ship is Close whose IP Account number is " +
                                    details.get("AccountNumber").replace(" ", "").trim());
                        }
                        // Product Name contains PSI then record should not present in Extract
                        else if (details.get("ProductName").contains("PSI")) {
                            executionStatus.add("Pass (Not present in Extract-Product Name contains-> SSI )");
                        } else {
                            // It will get fields which are not matched from Intralink(Excel data) with Extract file
                            String gicValue = gics.get(i);
                            List<Map<String, String>> unMatchedAccount = extractAccounts.stream().filter(x -> x.get("BASACT").trim().
                                    equals(StringUtils.leftPad(letterDetails.get("AccountNumber").
                                                    replace(" ", ""), 15, "0").
                                            replace(" ", "").trim()) && x.get("ACCTNO").
                                    replace(" ", "").trim().
                                    equals("RP#" + StringUtils.leftPad(details.get("AccountNumber").
                                            replace(" ", "").trim(), 15, "0"))
                                    && x.get("CERTNUM").trim().equals(gicValue.replaceAll("\\s+", ""))).collect(Collectors.toList());
                            primaryCustomerName.add(unMatchedAccount.get(0).get("CSTNAM01").trim());
                            addressLine1.add(unMatchedAccount.get(0).get("ADDRESS1").trim());
                            addressLine2.add(unMatchedAccount.get(0).get("ADDRESS 2").trim());
                            city.add(unMatchedAccount.get(0).get("ADDRESS 3").trim());
                            postal.add(unMatchedAccount.get(0).get("POSTCD").trim());

                            gic.add(unMatchedAccount.get(0).get("CERTNUM").trim());
                            if (!extractAccounts.get(i).get("CERTNUM").trim().equals(""))
                                issuer.add(unMatchedAccount.get(0).get("ISSUER").trim());
                            if (extractAccounts.get(i).get("CERTNUM").trim().equals(""))
                                delar = unMatchedAccount.get(0).get("ISSUER").trim();

                        }
                        // All fields are updated at once when GIC max number size reaches
                        if (i == gics.size() - 1) {
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNameExtract", primaryCustomerName.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", addressLine1.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_RawDataSheet", addressLine2.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_RawDataSheet", city.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "PostalCode_RawDataSheet", postal.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "GIC_RawDataSheet", gic.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Issuer_Extract", issuer.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_Extract", delar);

                            details = readFromExcel.readFromExcel(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId());
                            exectionStatusFailedFields = new HashMap<>();
                            // After updating values to excel sheet based on values updated it will check Intralink and Extract field values are correct or not
                            if (Arrays.asList(details.get("AddressLine1_RawDataSheet").split("\\|")).stream().filter(x -> x.trim().equals(details.get("AddressLine1_IntraLink").trim()) == false).count() >= 1) {
                                addressLine1Flag = false;
                                exectionStatusFailedFields.put("AddressLine1", addressLine1Flag);
                            } else {
                                addressLine1Flag = true;
                                exectionStatusFailedFields.put("AddressLine1", addressLine1Flag);
                            }
                            exectionStatusFailedFields.put("AddressLine2", addressLine2Flag);
                            exectionStatusFailedFields.put("City", cityFlag);
                            exectionStatusFailedFields.put("PostalCode", postalCodeFlag);

                            exectionStatusFailedFields.put("AccountNumber", accountFlag);
                            if (Arrays.asList(details.get("CustomerNameExtract").split("\\|")).stream().filter(x -> x.toUpperCase().equals(details.get("CustomerName").toUpperCase()) == false).count() >= 1) {
                                primaryCustomerNameFlag = false;
                                exectionStatusFailedFields.put("PrimaryCustomerName", primaryCustomerNameFlag);
                            } else {
                                primaryCustomerNameFlag = true;
                                exectionStatusFailedFields.put("PrimaryCustomerName", primaryCustomerNameFlag);
                            }

                            exectionStatusFailedFields.put("JointOneCustomerName", jointOneCustomerNameFlag);
                            exectionStatusFailedFields.put("JointTwoCustomerName", jointTwoCustomerNameFlag);
                            exectionStatusFailedFields.put("JointThreeCustomerName", jointThreeCustomerNameFlag);
                            if (!details.get("Dealer_Extract").equals(details.get("Delar_Intralink"))) {
                                delarFlag = false;
                                exectionStatusFailedFields.put("Dealer", delarFlag);
                            } else {
                                delarFlag = true;
                                exectionStatusFailedFields.put("Dealer", delarFlag);
                            }

                            List<String> gicsRawData = Arrays.asList(details.get("GIC_RawDataSheet").split("\\|")).stream().filter(x -> x.trim().length() > 0).collect(Collectors.toList());
                            List<String> gicsIntralink = Arrays.asList(details.get("GIC_IntraLink").split("\\|")).stream().collect(Collectors.toList());

                            if (gicsIntralink.stream().filter(x -> gicsRawData.contains(x.trim()) == false).collect(Collectors.toList()).size() >= 1) {
                                gicFlag = false;
                                exectionStatusFailedFields.put("Gic", gicFlag);
                            } else {
                                gicFlag = true;
                                exectionStatusFailedFields.put("Gic", gicFlag);
                            }
                            if (!details.get("Issuer_Extract").trim().equals(details.get("Issuer_Intralink").trim())) {
                                issuerFlag = false;
                                exectionStatusFailedFields.put("Issuer", issuerFlag);
                            } else {
                                issuerFlag = true;
                                exectionStatusFailedFields.put("Issuer", issuerFlag);
                            }


                            //exectionStatusFailedFields.add()
                            executionStatus.add("Fail-> Mismatch Fields:-" + exectionStatusFailedFields.entrySet().
                                    stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                                    collect(Collectors.joining(",")));

                            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Intralink and Extract fields are mismatched at " + exectionStatusFailedFields.entrySet().
                                    stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                                    collect(Collectors.joining(",")));
                            //   executionStatus.add("Failed (NOT found in Extract)");


                        }

                    }
                    // Account is not Trust account
                    else if (details.get("Trust_Accounts").equalsIgnoreCase("NO")) {
                        primaryCustomerName.add("Not Found in extract due to Trust (NO)");
                        addressLine1.add("Not Found in extract due to Trust (NO)");
                        addressLine2.add("Not Found in extract due to Trust (NO)");
                        city.add("Not Found in extract due to Trust (NO)");
                        postal.add("Not Found in extract due to Trust (NO)");
                        gic.add("Not Found in extract due to Trust (NO)");
                        if (extractAccounts.get(i).get("CERTNUM").trim().equals(""))
                            issuer.add("Not Found in extract due to Trust (NO)");
                        executionStatus.add("Pass (Not present in Extract-Trust-> No)");
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  No Records found in Extract because Account Number:- " + details.get("AccountNumber") + " is not trust");
                        // All fields are updated at once when GIC max number size reaches
                        if (i == gics.size() - 1) {
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNameExtract", primaryCustomerName.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", addressLine1.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_RawDataSheet", addressLine2.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_RawDataSheet", city.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "PostalCode_RawDataSheet", postal.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "GIC_RawDataSheet", gic.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Issuer_Extract", issuer.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_Extract", delar);


                        }
                    }
                    // Trust Account Column should not be Blank instead it should present either YES or NO
                    else {
                        executionStatus.add("Fail (Trust account column is blank)");
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Trust Account Column is blank for IP Account:- " + details.get("AccountNumber"));
                    }

                }


                // Checking whether account is eligible for CDIC or not if eligible StepFail not eligible StepPass
                else if (result.equals("Intralink account is present more than 1 record in extract")) {
                    // Account is repeated multiple times as it is not expected written with StepFail
                    LogHelper.logger.info("Multiple records are found in Extract for IP Account Number " + details.get("AccountNumber"));
                    // When Currency is USD then IP accounts should be present twice in Extract file
                    if (details.get("Currency").equals("USD")) {
                        // Checks 2 IP records present in Extract file
                        if (gics.get(i).trim().equals("") && extractAccounts.stream().filter(x -> x.get("BASACT").trim().
                                equals(StringUtils.leftPad(letterDetails.get("AccountNumber").
                                                replace(" ", ""), 15, "0").
                                        replace(" ", "").trim()) && x.get("ACCTNO").
                                replace(" ", "").trim().
                                equals("RP#" + StringUtils.leftPad(details.get("AccountNumber").
                                        replace(" ", "").trim(), 15, "0"))
                                && x.get("CERTNUM").trim().equals("")).collect(Collectors.toList()).size() == 2) {

                            ipSamples = new ArrayList<Map<String, String>>();
                            ipSamples.addAll(extractAccounts.stream().filter(x -> x.get("BASACT").trim().
                                    equals(StringUtils.leftPad(letterDetails.get("AccountNumber").
                                                    replace(" ", ""), 15, "0").
                                            replace(" ", "").trim()) && x.get("ACCTNO").
                                    replace(" ", "").trim().
                                    equals("RP#" + StringUtils.leftPad(details.get("AccountNumber").
                                            replace(" ", "").trim(), 15, "0"))
                                    && x.get("CERTNUM").trim().equals("")).collect(Collectors.toList()));
                            ipFlagList = new ArrayList<Boolean>();
                            // Iterates and verifies all extract fields with respect to Intralink field values
                            for (Map<String, String> ipAccount : ipSamples) {
                                //Validation Address Line 1 from Intralink to Extract
                                addressLine1Flag = ipAccount.get("ADDRESS1").trim().equals(details.get("AddressLine1_IntraLink").trim());
                                LogHelper.logger.info("Address Line 1 Intralink is " + details.get("AddressLine1_IntraLink").trim() + "and Address Line 1 Extract is " + ipAccount.get("ADDRESS1").trim());
                                String countryCode = countryCodes.getField("NameValue").trim();
                                //Validation Address Line 2 from Intralink to Extract
                                addressLine2Flag = ipAccount.values().stream().map(String::trim).collect(Collectors.toList()).stream().anyMatch(x -> x.equals(details.get("AddressLine2_IntraLink").trim()));
                                LogHelper.logger.info("Address Line 2 Intralink is " + details.get("AddressLine2_IntraLink").trim() + "and Extract record data is  " + ipAccount.values());
                                //Validation Address Line 3(City) from Intralink to Extract
                                cityFlag = ipAccount.values().stream().map(String::trim).collect(Collectors.toList()).stream().filter(x -> x.replaceAll("\\s+", "").contains(details.get("City_IntraLink").replaceAll("\\s+", "").trim() + countryCode) == true).count() == 1;
                                Pattern pattern = Pattern.compile("\\d*[A-Z]{1,}\\d*");

                                Matcher matcher = pattern.matcher(details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));

                                if (matcher.find()) {
                                    //Validation Address Line 4(Postal Code) from Intralink to Extract
                                    postalCodeFlag = ipAccount.values().stream().map(String::trim).collect(Collectors.toList()).stream().filter(x -> x.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replace(" ", "").trim()) == true).count() == 1;

                                } else {

                                    postalCodeFlag = ipAccount.get("POSTCD").replaceAll("\\s+", "").equals("");

                                }
                                LogHelper.logger.info("Postal Code Intralink is " + details.get("PostalCode_IntraLink").trim() + countryCode + "and Extract record data is  " + ipAccount.values());
                                //Validation Account Number from Intralink to Extract
                                accountFlag = ipAccount.get("ACCTNO").trim().equals("RP#" + StringUtils.leftPad(details.get("AccountNumber").replace(" ", "").trim(), 15, "0"));
                                LogHelper.logger.info("Account Number Intralink is " + details.get("AccountNumber").trim() + "and Extract Account number is  " + ipAccount.get("ACCTNO").replaceAll(" ", "").trim());
                                //Validation GIC Number from Intralink to Extract
                                gicFlag = ipAccount.get("CERTNUM").trim().equals(gics.get(i).replace(" ", "").trim());
                                LogHelper.logger.info("GIC Intralink is " + details.get("GIC_IntraLink").trim() + "and Extract GIC is  " + ipAccount.get("CERTNUM").trim());
                                //Validation Primary Customer Name from Intralink to Extract
                                primaryCustomerNameFlag = ipAccount.get("CSTNAM01").trim().equals(details.get("CustomerName").trim().toUpperCase());
                                LogHelper.logger.info("Primary Customer Name Intralink is " + details.get("CustomerName").trim() + "and Extract Primary Customer Name is  " + ipAccount.get("CSTNAM01").trim());
                                //Validation Joint 1 Customer Name from Intralink to Extract
                                jointOneCustomerNameFlag = ipAccount.get("CSTNAM02").trim().equals(details.get("Joint1_CustomerName").trim().toUpperCase());
                                LogHelper.logger.info("Joint One Customer Name Intralink is " + details.get("Joint1_CustomerName").trim() + "and Extract Joint One is  " + ipAccount.get("CSTNAM02").trim());
                                //Validation Joint 2 Customer Name from Intralink to Extract
                                jointTwoCustomerNameFlag = ipAccount.get("CSTNAM03").trim().equals(details.get("Joint2_CustomerName").trim().toUpperCase());
                                LogHelper.logger.info("Joint Two Customer Name Intralink is " + details.get("Joint2_CustomerName").trim() + "and Extract Joint Two is  " + ipAccount.get("CSTNAM03").trim());
                                //Validation Joint 3 Customer Name from Intralink to Extract
                                jointThreeCustomerNameFlag = ipAccount.get("CSTNAM04").trim().equals(details.get("Joint3_CustomerName").trim().toUpperCase());
                                LogHelper.logger.info("Joint Three Customer Name Intralink is " + details.get("Joint3_CustomerName").trim() + "and Extract Joint Three is  " + ipAccount.get("CSTNAM04").trim());
                                // IP Record contains Delar
                                if (ipAccount.get("CERTNUM").trim().equals(""))
                                    delarFlag = ipAccount.get("ISSUER").trim().equals(details.get("Delar_Intralink").trim());
                                LogHelper.logger.info("Delar Intralink is " + details.get("Delar_Intralink").trim() + "and Extract Delar is  " + ipAccount.get("ISSUER").trim());
                                if (addressLine1Flag && addressLine2Flag && cityFlag && postalCodeFlag
                                        && primaryCustomerNameFlag && jointOneCustomerNameFlag && jointTwoCustomerNameFlag && jointThreeCustomerNameFlag
                                        && accountFlag && delarFlag && gicFlag) {

                                    ipFlagList.add(true);

                                } else {
                                    ipFlagList.add(false);
                                }
                            }
                            // IP Records contains 2 when currency is USD
                            if (ipFlagList.stream().filter(x -> x.equals(true)).collect(Collectors.toList()).size() == 2) {
                                addressLine1.add(ipSamples.get(0).get("ADDRESS1").trim());
                                addressLine2.add(ipSamples.get(0).get("ADDRESS 2").trim());
                                city.add(ipSamples.get(0).get("ADDRESS 3").trim());
                                postal.add(ipSamples.get(0).get("POSTCD").trim());
                                ip.add(ipSamples.get(0).get("ACCTNO").trim());
                                gic.add(ipSamples.get(0).get("CERTNUM").trim());
                                if (!ipSamples.get(0).get("CERTNUM").trim().equals("")) {
                                    issuer.add(ipSamples.get(0).get("ISSUER").trim());
                                } else {
                                    delar = ipSamples.get(0).get("ISSUER").trim();
                                }
                                executionStatus.add("Pass-> (2 IP records verified for USD)");
                                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  It contains 2 IP Records because currency is USD and extract field values are matched with Intralink field values");
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNameExtract", primaryCustomerName.stream().collect(Collectors.joining("|")));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", addressLine1.stream().collect(Collectors.joining("|")));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_RawDataSheet", addressLine2.stream().collect(Collectors.joining("|")));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_RawDataSheet", city.stream().collect(Collectors.joining("|")));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "PostalCode_RawDataSheet", postal.stream().collect(Collectors.joining("|")));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "GIC_RawDataSheet", gic.stream().collect(Collectors.joining("|")));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Issuer_Extract", issuer.stream().collect(Collectors.joining("|")));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_Extract", delar);
                            }
//                            Currency is USD and 2 IP Records are not present in Extract then it is FAIL
                            else {
                                executionStatus.add("Fail-> (2 IP records are not same for USD)");
                                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "It contains 2 IP Records because currency is USD and extract field values are not matched with Intralink field values");
                            }


                        }
//                        Currency is USD and 2 IP Records are not present/GIC's are duplicated in Extract then it is FAIL
                        else {
                            executionStatus.add("Fail-> (Duplicate records found in Extract for GIC's)");
                            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Duplicate GIC Records are found in Extract for IP Account" + details.get("AccountNumber"));
                        }

                    } else {
                        executionStatus.add("Fail (Duplicate records found in Extract)");
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Duplicate Records are found in Extract for IP Account" + details.get("AccountNumber"));
                    }
                    ipFlagList.clear();
                }
                flagList.clear();

            }
        }
        /*Currency USD- 2 IP Records should present(Excluding GIC's), 1 GIC Record present for individual GIC Account
        and Currency CAD- 1 IP Record should present in Extract,  1 GIC Record present for individual GIC Account */
        else {
            executionStatus.add("Fail:- IP records deviation with respect to currency/IP Record is missing");
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "IP records deviation with respect to currency(USD having 1 record/CAD having 2 records)/IP Record is missing for BNS Delar");
        }
        return executionStatus;
    }

    /**
     * This function verifies english customer IP Details with Extract
     *
     * @return List- Execution Status
     * @throws FilloException
     * @throws IOException
     */
    private List<String> ipDetailsVerificationEnglish() throws FilloException, IOException {
        boolean addressLine1Flag = false;
        boolean addressLine2Flag = false;
        boolean cityFlag = false;
        boolean postalCodeFlag = false;
        boolean gicFlag = false;
        boolean accountFlag = false;
        boolean primaryCustomerNameFlag = false;
        boolean jointOneCustomerNameFlag = false;
        boolean jointTwoCustomerNameFlag = false;
        boolean jointThreeCustomerNameFlag = false;
        boolean issuerFlag = false;
        boolean delarFlag = false;
        // List object created to check which fields are failed
        flagList = new ArrayList<Boolean>();
        // List object created for Execution status
        List<String> executionStatus = new LinkedList<>();
        LogHelper.logger.info("Validating Intralink and Extract Fields");
        // Product contains BNE/PSE, Currency - USD then 2 IP records should present in Extract file (Exculuding GIC Records)
        // Product contains BNE/PSE, Currency - CAD then 1 IP records should present in Extract file (Exculuding GIC Records)
        if ((details.get("Currency").equals("USD") && details.get("ProductName").contains("BNS") && extractAccounts.stream().filter(x -> x.get("BASACT").trim().
                equals(StringUtils.leftPad(letterDetails.get("AccountNumber").
                                replace(" ", ""), 15, "0").
                        replace(" ", "").trim()) && x.get("ACCTNO").
                replace(" ", "").trim().
                equals("IP#" + StringUtils.leftPad(details.get("AccountNumber").
                        replace(" ", "").trim(), 15, "0"))
                && x.get("CERTNUM").trim().equals("")).collect(Collectors.toList()).size() == 2 || details.get("Currency").equals("CAD") && details.get("ProductName").contains("BNS") || details.get("ProductName").equals("Momentum PLUS Savings")
                && extractAccounts.stream().filter(x -> x.get("BASACT").trim().
                equals(StringUtils.leftPad(letterDetails.get("AccountNumber").
                                replace(" ", ""), 15, "0").
                        replace(" ", "").trim()) && x.get("ACCTNO").
                replace(" ", "").trim().
                equals("IP#" + StringUtils.leftPad(details.get("AccountNumber").
                        replace(" ", "").trim(), 15, "0"))
                && x.get("CERTNUM").trim().equals("")).collect(Collectors.toList()).size() == 1) ||

                (details.get("Currency").equals("USD") && details.get("ProductName").contains("SSI") && extractAccounts.stream().filter(x -> x.get("BASACT").trim().
                        equals(StringUtils.leftPad(letterDetails.get("AccountNumber").
                                        replace(" ", ""), 15, "0").
                                replace(" ", "").trim()) && x.get("ACCTNO").
                        replace(" ", "").trim().
                        equals("IP#" + StringUtils.leftPad(details.get("AccountNumber").
                                replace(" ", "").trim(), 15, "0"))
                        && x.get("CERTNUM").trim().equals("")).collect(Collectors.toList()).size() == 0 || details.get("Currency").equals("CAD") && details.get("ProductName").contains("SSI")
                        && extractAccounts.stream().filter(x -> x.get("BASACT").trim().
                        equals(StringUtils.leftPad(letterDetails.get("AccountNumber").
                                        replace(" ", ""), 15, "0").
                                replace(" ", "").trim()) && x.get("ACCTNO").
                        replaceAll(" ", "").trim().
                        equals("IP#" + StringUtils.leftPad(details.get("AccountNumber").
                                replace(" ", "").trim(), 15, "0"))
                        && x.get("CERTNUM").trim().equals("")).collect(Collectors.toList()).size() == 0)) {
            // List object are created for different fields
            List<String> primaryCustomerName = new LinkedList<>();
            List<String> addressLine1 = new LinkedList<>();
            List<String> addressLine2 = new LinkedList<>();
            List<String> city = new LinkedList<>();
            List<String> postal = new LinkedList<>();
            List<String> gic = new LinkedList<>();
            List<String> ip = new LinkedList<>();
            List<String> issuer = new LinkedList<>();
            String delar = "";
            // GIC's are split based on pipe symbol
            ArrayList<String> gics = new ArrayList<String>(Arrays.asList(details.get("GIC_IntraLink").split("\\|")));
            // Issuers are split based on pipe symbol
            ArrayList<String> issuers = new ArrayList<String>(Arrays.asList(details.get("Issuer_Intralink").split("\\|")));
            if (!details.get("GIC_IntraLink").equals("") && gics.size() >= 1) {
                //Adding empty string for verifying IP Account in Extract field
                gics.add(0, " ");
                issuers.add(0, " ");
            }
            // Verifying GIC's and IP Acconts with Extract
            for (int i = 0; i < gics.size(); i++) {
                // Iterating all accounts present in Extract file
                //Compares Intralink account details with Extract values like AddressLine1, City, AddressLine2, PostalCode,GIC's
                for (Map<String, String> extractAccount : extractAccounts) {
                    //Validation Address Line 1 from Intralink to Extract
                    addressLine1Flag = extractAccount.get("ADDRESS1").trim().equals(details.get("AddressLine1_IntraLink").trim());
                    LogHelper.logger.info("Address Line 1 Intralink is " + details.get("AddressLine1_IntraLink").trim() + "and Address Line 1 Extract is " + extractAccount.get("ADDRESS1").trim());
                    String countryCode = countryCodes.getField("NameValue").trim();
                    //Validation Address Line 2 from Intralink to Extract
                    addressLine2Flag = extractAccount.values().stream().map(String::trim).collect(Collectors.toList()).stream().anyMatch(x -> x.equals(details.get("AddressLine2_IntraLink").trim()));
                    LogHelper.logger.info("Address Line 2 Intralink is " + details.get("AddressLine2_IntraLink").trim() + "and Extract record data is  " + extractAccount.values());
                    //Validation City from Intralink to Extract
                    cityFlag = extractAccount.values().stream().map(String::trim).collect(Collectors.toList()).stream().filter(x -> x.replaceAll("\\s+", "").contains(details.get("City_IntraLink").replaceAll("\\s+", "").trim() + countryCode) == true).count() == 1;
                    LogHelper.logger.info("City Intralink is " + details.get("City_IntraLink").trim() + countryCode + "and Extract record data is  " + extractAccount.values());

                    Pattern pattern = Pattern.compile("\\d*[A-Z]{1,}\\d*");
                    Matcher matcher = pattern.matcher(details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                    //Validation Postal Code from Intralink to Extract
                    if (matcher.find()) {
                        postalCodeFlag = extractAccount.values().stream().map(String::trim).collect(Collectors.toList()).stream().filter(x -> x.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replace(" ", "").trim()) == true).count() == 1;

                    } else {
                        postalCodeFlag = extractAccount.get("POSTCD").replaceAll("\\s+", "").equals("");
                    }
                    LogHelper.logger.info("Postal Code Intralink is " + details.get("PostalCode_IntraLink").trim() + countryCode + "and Extract record data is  " + extractAccount.values());
                    //Validation Account Number from Intralink to Extract
                    accountFlag = extractAccount.get("ACCTNO").trim().equals("IP#" + StringUtils.leftPad(details.get("AccountNumber").replace(" ", "").trim(), 15, "0"));
                    LogHelper.logger.info("Account Number Intralink is " + details.get("AccountNumber").trim() + "and Extract Account number is  " + extractAccount.get("ACCTNO").replaceAll(" ", "").trim());
                    //Validation GIC Number from Intralink to Extract
                    gicFlag = extractAccount.get("CERTNUM").trim().equals(gics.get(i).replace(" ", "").trim());
                    LogHelper.logger.info("GIC Intralink is " + details.get("GIC_IntraLink").trim() + "and Extract GIC is  " + extractAccount.get("CERTNUM").trim());
                    //Validation Primary Customer Name from Intralink to Extract
                    primaryCustomerNameFlag = extractAccount.get("CSTNAM01").trim().equals(details.get("CustomerName").trim().toUpperCase());
                    LogHelper.logger.info("Primary Customer Name Intralink is " + details.get("CustomerName").trim() + "and Extract Primary Customer Name is  " + extractAccount.get("CSTNAM01").trim());
                    //Validation Joint 1 Customer Name from Intralink to Extract
                    jointOneCustomerNameFlag = extractAccount.get("CSTNAM02").trim().equals(details.get("Joint1_CustomerName").trim().toUpperCase());
                    LogHelper.logger.info("Joint One Customer Name Intralink is " + details.get("Joint1_CustomerName").trim() + "and Extract Joint One is  " + extractAccount.get("CSTNAM02").trim());
                    //Validation Joint 2 Customer Name from Intralink to Extract
                    jointTwoCustomerNameFlag = extractAccount.get("CSTNAM03").trim().equals(details.get("Joint2_CustomerName").trim().toUpperCase());
                    LogHelper.logger.info("Joint Two Customer Name Intralink is " + details.get("Joint2_CustomerName").trim() + "and Extract Joint Two is  " + extractAccount.get("CSTNAM03").trim());
                    //Validation Joint 3 Customer Name from Intralink to Extract
                    jointThreeCustomerNameFlag = extractAccount.get("CSTNAM04").trim().equals(details.get("Joint3_CustomerName").trim().toUpperCase());
                    LogHelper.logger.info("Joint Three Customer Name Intralink is " + details.get("Joint3_CustomerName").trim() + "and Extract Joint Three is  " + extractAccount.get("CSTNAM04").trim());
                    // IP Record contains Delar
                    if (extractAccount.get("CERTNUM").trim().equals("")) {
                        delarFlag = extractAccount.get("ISSUER").trim().equals(details.get("Delar_Intralink").trim());
                        if (addressLine1Flag && addressLine2Flag && cityFlag && postalCodeFlag
                                && primaryCustomerNameFlag && jointOneCustomerNameFlag && jointTwoCustomerNameFlag && jointThreeCustomerNameFlag
                                && accountFlag && delarFlag && gicFlag) {
                            flagList.add(true);

                        } else {
                            flagList.add(false);
                        }
                    }
                    //GIC Record contains Issuer
                    else if (!extractAccount.get("CERTNUM").trim().equals("")) {
                        issuerFlag = extractAccount.get("ISSUER").trim().equals(issuers.get(i).trim());
                        if (addressLine1Flag && addressLine2Flag && cityFlag && postalCodeFlag
                                && primaryCustomerNameFlag && jointOneCustomerNameFlag && jointTwoCustomerNameFlag && jointThreeCustomerNameFlag
                                && accountFlag && issuerFlag && gicFlag) {
                            flagList.add(true);
                        } else {
                            flagList.add(false);
                        }
                    }


                    // Verifying is account fields are matched with Intralink fields with respect to multiple records in extract file


                }

                // Checks result based on 3 points.
//            1. Checks is account is found in Extract file
//            2. Checks why account is not present in Extract file
//            3. Checks is any record is duplicated instead of maintaining 1 unique record
                String result = flagList.stream().filter(x -> x.equals(true)).collect(Collectors.toList()).size() == 1 ? "Only 1 record is present in extract file for 1 account of Intralink" :
                        flagList.stream().filter(x -> x.equals(true)).collect(Collectors.toList()).size() > 1 ? "Intralink account is present more than 1 record in extract"
                                : "Intralink account is not present in extract";
                //When all fields are passed then it comes to below condition and only 1 record is present in Extract
                if (result.equals("Only 1 record is present in extract file for 1 account of Intralink")) {
                    // Out of multiple records of extract file checks which record is matching with Intralink values

                    int position = flagList.indexOf(true);
                    primaryCustomerName.add(extractAccounts.get(position).get("CSTNAM01").trim());
                    addressLine1.add(extractAccounts.get(position).get("ADDRESS1").trim());
                    addressLine2.add(extractAccounts.get(position).get("ADDRESS 2").trim());
                    city.add(extractAccounts.get(position).get("ADDRESS 3").trim());
                    postal.add(extractAccounts.get(position).get("POSTCD").trim());
                    ip.add(extractAccounts.get(position).get("ACCTNO").trim());
                    gic.add(extractAccounts.get(position).get("CERTNUM").trim());
                    if (!extractAccounts.get(position).get("CERTNUM").trim().equals("")) {
                        issuer.add(extractAccounts.get(position).get("ISSUER").trim());
                    } else {
                        delar = extractAccounts.get(position).get("ISSUER").trim();
                    }
                    // Negative Testing-> Account status is Not Close, Trust Account - YES
                    if (!details.get("AccountStatus_Intralink").equalsIgnoreCase("Close") && details.get("Trust_Accounts").equalsIgnoreCase("YES") && details.get("ProductName").contains(" SSI") &&
                            extractAccounts.get(position).get("ACCTNO").replaceAll(" ", "").trim().
                                    equals("IP#" + StringUtils.leftPad(details.get("AccountNumber").
                                            replace(" ", "").trim(), 15, "0"))
                            && extractAccounts.get(position).get("CERTNUM").trim().equals("")) {
                        executionStatus.add("Fail (SSI Account containing IP is present in Extract)");
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "IP Account record containing product " + details.get("ProductName") + " is present in Extract");
                    }
                    // Account status is Not Close, Trust Account - YES - PASS
                    if (!details.get("AccountStatus_Intralink").equalsIgnoreCase("Close") && details.get("Trust_Accounts").equalsIgnoreCase("YES")) {
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  1 extract record field values are matched with Intralink Customer field values" +
                                "whose IP Account is " + extractAccounts.get(position).get("ACCTNO").trim() + " and GIC# is " + extractAccounts.get(position).get("CERTNUM").trim());
                        executionStatus.add("Pass");
                        // All fields are updated at once when GIC max number size reaches
                        if (i == gics.size() - 1) {
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNameExtract", primaryCustomerName.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", addressLine1.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_RawDataSheet", addressLine2.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_RawDataSheet", city.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "PostalCode_RawDataSheet", postal.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "GIC_RawDataSheet", gic.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Issuer_Extract", issuer.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_Extract", delar);
                        }
                    }
                    // Account status is  Close, Trust Account - YES - FAIL
                    else if (details.get("AccountStatus_Intralink").equalsIgnoreCase("Close") && details.get("Trust_Accounts").equalsIgnoreCase("YES")) {

                        executionStatus.add("Fail (Close account is present in Extract)");
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "1 extract record field values are matched with Intralink Customer field values because Account Status is " + details.get("AccountStatus_Intralink")
                                + " and Acccount trust value is " + details.get("Trust_Accounts") + " whose IP Account is " + extractAccounts.get(position).get("ACCTNO").trim() + " and GIC# is " + extractAccounts.get(position).get("CERTNUM").trim());
                        // All fields are updated at once when GIC max number size reaches
                        if (i == gics.size() - 1) {
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNameExtract", primaryCustomerName.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", addressLine1.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_RawDataSheet", addressLine2.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_RawDataSheet", city.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "PostalCode_RawDataSheet", postal.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "GIC_RawDataSheet", gic.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Issuer_Extract", issuer.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_Extract", delar);
                        }

                    }
                    //  Trust Account - NO
                    else if (details.get("Trust_Accounts").equalsIgnoreCase("NO")) {
                        executionStatus.add("Fail (Trust No accounts are present in Extract)");
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "1 extract record field values are matched with Intralink Customer field values because it is not trust account"
                                + " whose IP Account is " + extractAccounts.get(position).get("ACCTNO").trim() + " and GIC# is " + extractAccounts.get(position).get("CERTNUM").trim());
                        executionStatus.add("Fail (Trust No accounts are present in Extract)");

                        // All fields are updated at once when GIC max number size reaches
                        if (i == gics.size() - 1) {
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNameExtract", primaryCustomerName.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", addressLine1.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_RawDataSheet", addressLine2.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_RawDataSheet", city.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "PostalCode_RawDataSheet", postal.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "GIC_RawDataSheet", gic.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Issuer_Extract", issuer.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_Extract", delar);
                        }

                    }

                }
                // Intralink Account fields are not matching with Extract fields
                else if (result.equals("Intralink account is not present in extract")) {
                    // Account is Trust account
                    if (details.get("Trust_Accounts").equalsIgnoreCase("Yes")) {
                        // Account is closed, So account shouild not present in extract file
                        if (details.get("AccountStatus_Intralink").equalsIgnoreCase("Close")) {

                            primaryCustomerName.add("Not Found in extract due to close account");
                            addressLine1.add("Not Found in extract due to close account");
                            addressLine2.add("Not Found in extract due to close account");
                            city.add("Not Found in extract due to close account");
                            postal.add("Not Found in extract due to close account");
                            gic.add("Not Found in extract due to close account");
                            issuer.add("Not Found in extract due to close account");
                            if (extractAccounts.get(i).get("CERTNUM").trim().equals(""))
                                delar = "Not Found in extract due to close account";
                            executionStatus.add("Pass (Not present in Extract-Account RelationShip-> Close )");
                            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  No Records found in Extract because Account Relation ship is Close whose IP Account number is " +
                                    details.get("AccountNumber").replace(" ", "").trim());
                        }
                        // Product Name contains SSI then record should not present in Extract
                        else if (details.get("ProductName").contains("SSI")) {
                            executionStatus.add("Pass (Not present in Extract-Product Name contains-> SSI )");
                        } else {
                            // It will get fields which are not matched from Intralink(Excel data) with Extract file
                            String gicValue = gics.get(i);
                            List<Map<String, String>> unMatchedAccount = extractAccounts.stream().filter(x -> x.get("BASACT").trim().
                                    equals(StringUtils.leftPad(letterDetails.get("AccountNumber").
                                                    replace(" ", ""), 15, "0").
                                            replace(" ", "").trim()) && x.get("ACCTNO").
                                    replaceAll(" ", "").trim().
                                    equals("IP#" + StringUtils.leftPad(details.get("AccountNumber").
                                            replace(" ", "").trim(), 15, "0"))
                                    && x.get("CERTNUM").trim().equals(gicValue.replaceAll("\\s+", ""))).collect(Collectors.toList());
                            primaryCustomerName.add(unMatchedAccount.get(0).get("CSTNAM01").trim());
                            addressLine1.add(unMatchedAccount.get(0).get("ADDRESS1").trim());
                            addressLine2.add(unMatchedAccount.get(0).get("ADDRESS 2").trim());
                            city.add(unMatchedAccount.get(0).get("ADDRESS 3").trim());
                            postal.add(unMatchedAccount.get(0).get("POSTCD").trim());
                            gic.add(unMatchedAccount.get(0).get("CERTNUM").trim());
                            if (!extractAccounts.get(i).get("CERTNUM").trim().equals(""))
                                issuer.add(unMatchedAccount.get(0).get("ISSUER").trim());
                            if (extractAccounts.get(i).get("CERTNUM").trim().equals(""))
                                delar = unMatchedAccount.get(0).get("ISSUER").trim();

                        }
                        // All fields are updated at once when GIC max number size reaches
                        if (i == gics.size() - 1) {
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNameExtract", primaryCustomerName.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", addressLine1.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_RawDataSheet", addressLine2.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_RawDataSheet", city.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "PostalCode_RawDataSheet", postal.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "GIC_RawDataSheet", gic.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Issuer_Extract", issuer.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_Extract", delar);

                            details = readFromExcel.readFromExcel(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId());
                            exectionStatusFailedFields = new HashMap<>();
                            // After updating values to excel sheet based on values updated it will check Intralink and Extract field values are correct or not
                            if (Arrays.asList(details.get("AddressLine1_RawDataSheet").split("\\|")).stream().filter(x -> x.trim().equals(details.get("AddressLine1_IntraLink").trim()) == false).count() >= 1) {
                                addressLine1Flag = false;
                                exectionStatusFailedFields.put("AddressLine1", addressLine1Flag);
                            } else {
                                addressLine1Flag = true;
                                exectionStatusFailedFields.put("AddressLine1", addressLine1Flag);
                            }
                            exectionStatusFailedFields.put("AddressLine2", addressLine2Flag);
                            exectionStatusFailedFields.put("City", cityFlag);
                            exectionStatusFailedFields.put("PostalCode", postalCodeFlag);
                            exectionStatusFailedFields.put("AccountNumber", accountFlag);
                            if (Arrays.asList(details.get("CustomerNameExtract").split("\\|")).stream().filter(x -> x.toUpperCase().equals(details.get("CustomerName").toUpperCase()) == false).count() >= 1) {
                                primaryCustomerNameFlag = false;
                                exectionStatusFailedFields.put("PrimaryCustomerName", primaryCustomerNameFlag);
                            } else {
                                primaryCustomerNameFlag = true;
                                exectionStatusFailedFields.put("PrimaryCustomerName", primaryCustomerNameFlag);
                            }

                            exectionStatusFailedFields.put("JointOneCustomerName", jointOneCustomerNameFlag);
                            exectionStatusFailedFields.put("JointTwoCustomerName", jointTwoCustomerNameFlag);
                            exectionStatusFailedFields.put("JointThreeCustomerName", jointThreeCustomerNameFlag);
                            if (!details.get("Dealer_Extract").equals(details.get("Delar_Intralink"))) {
                                delarFlag = false;
                                exectionStatusFailedFields.put("Dealer", delarFlag);
                            } else {
                                delarFlag = true;
                                exectionStatusFailedFields.put("Dealer", delarFlag);
                            }

                            List<String> gicsRawData = Arrays.asList(details.get("GIC_RawDataSheet").split("\\|")).stream().filter(x -> x.trim().length() > 0).collect(Collectors.toList());
                            List<String> gicsIntralink = Arrays.asList(details.get("GIC_IntraLink").split("\\|")).stream().collect(Collectors.toList());

                            if (gicsIntralink.stream().filter(x -> gicsRawData.contains(x.trim()) == false).collect(Collectors.toList()).size() >= 1) {
                                gicFlag = false;
                                exectionStatusFailedFields.put("Gic", gicFlag);
                            } else {
                                gicFlag = true;
                                exectionStatusFailedFields.put("Gic", gicFlag);
                            }
                            if (!details.get("Issuer_Extract").trim().equals(details.get("Issuer_Intralink").trim())) {
                                issuerFlag = false;
                                exectionStatusFailedFields.put("Issuer", issuerFlag);
                            } else {
                                issuerFlag = true;
                                exectionStatusFailedFields.put("Issuer", issuerFlag);
                            }


                            //exectionStatusFailedFields.add()
                            executionStatus.add("Fail-> Mismatch Fields:-" + exectionStatusFailedFields.entrySet().
                                    stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                                    collect(Collectors.joining(",")));

                            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Intralink and Extract fields are mismatched at " + exectionStatusFailedFields.entrySet().
                                    stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                                    collect(Collectors.joining(",")));
                            //   executionStatus.add("Failed (NOT found in Extract)");

                        }

                    }
                    // Account is not Trust account
                    else if (details.get("Trust_Accounts").equalsIgnoreCase("NO")) {
                        primaryCustomerName.add("Not Found in extract due to Trust (NO)");
                        addressLine1.add("Not Found in extract due to Trust (NO)");
                        addressLine2.add("Not Found in extract due to Trust (NO)");
                        city.add("Not Found in extract due to Trust (NO)");
                        postal.add("Not Found in extract due to Trust (NO)");
                        gic.add("Not Found in extract due to Trust (NO)");
                        if (extractAccounts.get(i).get("CERTNUM").trim().equals(""))
                            issuer.add("Not Found in extract due to Trust (NO)");
                        executionStatus.add("Pass (Not present in Extract-Trust-> No)");
                        LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  No Records found in Extract because Account Number:- " + details.get("AccountNumber") + " is not trust");
                        // All fields are updated at once when GIC max number size reaches
                        if (i == gics.size() - 1) {
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNameExtract", primaryCustomerName.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", addressLine1.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_RawDataSheet", addressLine2.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_RawDataSheet", city.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "PostalCode_RawDataSheet", postal.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "GIC_RawDataSheet", gic.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Issuer_Extract", issuer.stream().collect(Collectors.joining("|")));
                            readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_Extract", delar);
                        }
                    }
                    // Trust Account Column should not be Blank instead it should present either YES or NO
                    else {
                        executionStatus.add("Fail (Trust account column is blank)");
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Trust Account Column is blank for IP Account:- " + details.get("AccountNumber"));
                    }

                }


                // Checking whether account is eligible for CDIC or not if eligible StepFail not eligible StepPass
                else if (result.equals("Intralink account is present more than 1 record in extract")) {
                    // Account is repeated multiple times as it is not expected written with StepFail
                    LogHelper.logger.info("Multiple records are found in Extract for IP Account Number " + details.get("AccountNumber"));
                    // When Currency is USD then IP accounts should be present twice in Extract file
                    if (details.get("Currency").equals("USD")) {
                        // Checks 2 IP records present in Extract file
                        if (gics.get(i).trim().equals("") && extractAccounts.stream().filter(x -> x.get("BASACT").trim().
                                equals(StringUtils.leftPad(letterDetails.get("AccountNumber").
                                                replace(" ", ""), 15, "0").
                                        replace(" ", "").trim()) && x.get("ACCTNO").
                                replaceAll(" ", "").trim().
                                equals("IP#" + StringUtils.leftPad(details.get("AccountNumber").
                                        replace(" ", "").trim(), 15, "0"))
                                && x.get("CERTNUM").trim().equals("")).collect(Collectors.toList()).size() == 2) {

                            ipSamples = new ArrayList<Map<String, String>>();
                            ipSamples.addAll(extractAccounts.stream().filter(x -> x.get("BASACT").trim().
                                    equals(StringUtils.leftPad(letterDetails.get("AccountNumber").
                                                    replace(" ", ""), 15, "0").
                                            replace(" ", "").trim()) && x.get("ACCTNO").
                                    replaceAll(" ", "").trim().
                                    equals("IP#" + StringUtils.leftPad(details.get("AccountNumber").
                                            replace(" ", "").trim(), 15, "0"))
                                    && x.get("CERTNUM").trim().equals("")).collect(Collectors.toList()));
                            ipFlagList = new ArrayList<Boolean>();
                            // Iterates and verifies all extract fields with respect to Intralink field values
                            for (Map<String, String> ipAccount : ipSamples) {
                                //Validation Address Line 1 from Intralink to Extract
                                addressLine1Flag = ipAccount.get("ADDRESS1").trim().equals(details.get("AddressLine1_IntraLink").trim());
                                LogHelper.logger.info("Address Line 1 Intralink is " + details.get("AddressLine1_IntraLink").trim() + "and Address Line 1 Extract is " + ipAccount.get("ADDRESS1").trim());
                                String countryCode = countryCodes.getField("NameValue").trim();
                                //Validation Address Line 2 from Intralink to Extract
                                addressLine2Flag = ipAccount.values().stream().map(String::trim).collect(Collectors.toList()).stream().anyMatch(x -> x.equals(details.get("AddressLine2_IntraLink").trim()));
                                LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 2 Intralink is " + details.get("AddressLine2_IntraLink").trim() + "and Extract record data is  " + ipAccount.values());
                                //Validation Address Line 3(City) from Intralink to Extract
                                cityFlag = ipAccount.values().stream().map(String::trim).collect(Collectors.toList()).stream().filter(x -> x.replaceAll("\\s+", "").contains(details.get("City_IntraLink").replaceAll("\\s+", "").trim() + countryCode) == true).count() == 1;
                                Pattern pattern = Pattern.compile("\\d*[A-Z]{1,}\\d*");
                                Matcher matcher = pattern.matcher(details.get("PostalCode_IntraLink").replaceAll("\\s+", ""));
                                if (matcher.find()) {
                                    //Validation Address Line 4(Postal Code) from Intralink to Extract
                                    postalCodeFlag = ipAccount.values().stream().map(String::trim).collect(Collectors.toList()).stream().filter(x -> x.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replace(" ", "").trim()) == true).count() == 1;

                                } else {
                                    postalCodeFlag = ipAccount.get("POSTCD").replaceAll("\\s+", "").equals("");
                                }
                                LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Postal Code Intralink is " + details.get("PostalCode_IntraLink").trim() + countryCode + "and Extract record data is  " + ipAccount.values());
                                //Validation Account Number from Intralink to Extract
                                accountFlag = ipAccount.get("ACCTNO").trim().equals("IP#" + StringUtils.leftPad(details.get("AccountNumber").replace(" ", "").trim(), 15, "0"));
                                LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Number Intralink is " + details.get("AccountNumber").trim() + "and Extract Account number is  " + ipAccount.get("ACCTNO").replaceAll(" ", "").trim());
                                //Validation GIC Number from Intralink to Extract
                                gicFlag = ipAccount.get("CERTNUM").trim().equals(gics.get(i).replace(" ", "").trim());
                                LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "GIC Intralink is " + details.get("GIC_IntraLink").trim() + "and Extract GIC is  " + ipAccount.get("CERTNUM").trim());
                                //Validation Primary Customer Name from Intralink to Extract
                                primaryCustomerNameFlag = ipAccount.get("CSTNAM01").trim().equals(details.get("CustomerName").trim().toUpperCase());
                                LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer Name Intralink is " + details.get("CustomerName").trim() + "and Extract Primary Customer Name is  " + ipAccount.get("CSTNAM01").trim());
                                //Validation Joint 1 Customer Name from Intralink to Extract
                                jointOneCustomerNameFlag = ipAccount.get("CSTNAM02").trim().equals(details.get("Joint1_CustomerName").trim().toUpperCase());
                                LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint One Customer Name Intralink is " + details.get("Joint1_CustomerName").trim() + "and Extract Joint One is  " + ipAccount.get("CSTNAM02").trim());
                                //Validation Joint 2 Customer Name from Intralink to Extract
                                jointTwoCustomerNameFlag = ipAccount.get("CSTNAM03").trim().equals(details.get("Joint2_CustomerName").trim().toUpperCase());
                                LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint Two Customer Name Intralink is " + details.get("Joint2_CustomerName").trim() + "and Extract Joint Two is  " + ipAccount.get("CSTNAM03").trim());
                                //Validation Joint 3 Customer Name from Intralink to Extract
                                jointThreeCustomerNameFlag = ipAccount.get("CSTNAM04").trim().equals(details.get("Joint3_CustomerName").trim().toUpperCase());
                                LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint Three Customer Name Intralink is " + details.get("Joint3_CustomerName").trim() + "and Extract Joint Three is  " + ipAccount.get("CSTNAM04").trim());
                                // IP Record contains Delar
                                if (ipAccount.get("CERTNUM").trim().equals(""))
                                    delarFlag = ipAccount.get("ISSUER").trim().equals(details.get("Delar_Intralink").trim());


                                LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Delar Intralink is " + details.get("Delar_Intralink").trim() + "and Extract Delar is  " + ipAccount.get("ISSUER").trim());
                                if (addressLine1Flag && addressLine2Flag && cityFlag && postalCodeFlag
                                        && primaryCustomerNameFlag && jointOneCustomerNameFlag && jointTwoCustomerNameFlag && jointThreeCustomerNameFlag
                                        && accountFlag && delarFlag && gicFlag) {

                                    ipFlagList.add(true);

                                } else {
                                    ipFlagList.add(false);
                                }
                            }
                            // IP Records contains 2 when currency is USD
                            if (ipFlagList.stream().filter(x -> x.equals(true)).collect(Collectors.toList()).size() == 2) {
                                addressLine1.add(ipSamples.get(0).get("ADDRESS1").trim());
                                addressLine2.add(ipSamples.get(0).get("ADDRESS 2").trim());
                                city.add(ipSamples.get(0).get("ADDRESS 3").trim());
                                postal.add(ipSamples.get(0).get("POSTCD").trim());
                                ip.add(ipSamples.get(0).get("ACCTNO").trim());
                                gic.add(ipSamples.get(0).get("CERTNUM").trim());
                                if (!ipSamples.get(0).get("CERTNUM").trim().equals("")) {
                                    issuer.add(ipSamples.get(0).get("ISSUER").trim());
                                } else {
                                    delar = ipSamples.get(0).get("ISSUER").trim();
                                }
                                executionStatus.add("Pass-> (2 IP records verified for USD)");
                                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  It contains 2 IP Records because currency is USD and extract field values are matched with Intralink field values");
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNameExtract", primaryCustomerName.stream().collect(Collectors.joining("|")));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", addressLine1.stream().collect(Collectors.joining("|")));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_RawDataSheet", addressLine2.stream().collect(Collectors.joining("|")));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_RawDataSheet", city.stream().collect(Collectors.joining("|")));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "PostalCode_RawDataSheet", postal.stream().collect(Collectors.joining("|")));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "GIC_RawDataSheet", gic.stream().collect(Collectors.joining("|")));
                                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_Extract", delar);
                            }
                            //Currency is USD and 2 IP Records are not present in Extract then it is FAIL
                            else {
                                executionStatus.add("Fail-> (2 IP records are not same for USD)");
                                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "It contains 2 IP Records because currency is USD and extract field values are not matched with Intralink field values");
                            }


                        }
                        // Currency is USD and 2 IP Records are not present/GIC's are duplicated in Extract then it is FAIL
                        else {
                            executionStatus.add("Fail-> (Duplicate records found in Extract for GIC's)");
                            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Duplicate GIC Records are found in Extract for IP Account" + details.get("AccountNumber"));
                        }

                    } else {
                        executionStatus.add("Fail (Duplicate records found in Extract)");
                        LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Duplicate Records are found in Extract for IP Account" + details.get("AccountNumber"));
                    }
                    ipFlagList.clear();
                }
                flagList.clear();

            }
        }
        /*Currency USD- 2 IP Records should present(Excluding GIC's), 1 GIC Record present for individual GIC Account
        and Currency CAD- 1 IP Record should present in Extract,  1 GIC Record present for individual GIC Account */
        else {
            executionStatus.add("Fail:- IP records deviation with respect to currency/IP Record is missing");
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "IP records deviation with respect to currency(USD having 1 record/CAD having 2 records)/IP Record is missing for BNS Delar");
        }
        return executionStatus;

    }

    /**
     * This function verifies english customer Savings Details with Extract
     *
     * @return List- Execution Status
     * @throws FilloException
     * @throws IOException
     */
    private List<String> savingsDetailsVerification() throws FilloException, IOException {

        boolean addressLine1Flag = false;
        boolean addressLine2Flag = false;
        boolean cityFlag = false;
        boolean postalCodeFlag = false;
        boolean gicFlag = false;
        boolean accountFlag = false;
        boolean primaryCustomerNameFlag = false;
        boolean jointOneCustomerNameFlag = false;
        boolean jointTwoCustomerNameFlag = false;
        boolean jointThreeCustomerNameFlag = false;
        boolean issuerFlag = false;
        boolean delarFlag = false;
        // List object created to check which fields are failed
        flagList = new ArrayList<Boolean>();
        // List object created for Execution status
        List<String> executionStatus = new LinkedList<>();

        // Iterating all accounts present in Extract file
        //Compares Intralink account details with Extract values like AddressLine1, City, AddressLine2, PostalCode
        LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Validating Intralink and Extract Fields");
        // Verifying Savings Accounts with Extract
        //Compares Intralink account details with Extract values like AddressLine1, City, AddressLine2, PostalCode
        for (Map<String, String> extractAccount : extractAccounts) {
            //Validation Address Line 1 from Intralink to Extract
            addressLine1Flag = extractAccount.get("ADDRESS1").trim().equals(details.get("AddressLine1_IntraLink").trim());
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 1 Intralink is " + details.get("AddressLine1_IntraLink").trim() + "and Address Line 1 Extract is " + extractAccount.get("ADDRESS1").trim());
            String countryCode = countryCodes.getField("NameValue").trim();
            //Validation Address Line 2 from Intralink to Extract
            addressLine2Flag = extractAccount.values().stream().map(String::trim).collect(Collectors.toList()).stream().anyMatch(x -> x.equals(details.get("AddressLine2_IntraLink").trim()));
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Address Line 2 Intralink is " + details.get("AddressLine2_IntraLink").trim() + "and Extract record data is  " + extractAccount.values());
            //Validation City from Intralink to Extract
            cityFlag = extractAccount.values().stream().map(String::trim).collect(Collectors.toList()).stream().filter(x -> x.replaceAll("\\s+", "").contains(details.get("City_IntraLink").replaceAll("\\s+", "").trim() + countryCode) == true).count() == 1;

            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "City Intralink is " + details.get("City_IntraLink").trim() + countryCode + "and Extract record data is  " + extractAccount.values());

            postalCodeFlag = extractAccount.values().stream().map(String::trim).collect(Collectors.toList()).stream().filter(x -> x.replaceAll("\\s+", "").contains(details.get("PostalCode_IntraLink").replace(" ", "").trim()) == true).count() == 1;
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Postal Code Intralink is " + details.get("PostalCode_IntraLink").trim() + countryCode + "and Extract record data is  " + extractAccount.values());
            //Validation Account Number from Intralink to Extract
            accountFlag = extractAccount.get("ACCTNO").replaceAll(" ", "").trim().equals(details.get("AccountNumber").replaceAll(" ", "").trim());
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Number Intralink is " + details.get("AccountNumber").trim() + "and Extract Account number is  " + extractAccount.get("ACCTNO").replaceAll(" ", "").trim());
            //Validation Delar from Intralink to Extract
            delarFlag = extractAccount.get("ISSUER").trim().equals(details.get("Delar_Intralink").trim());
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Delar Intralink is " + details.get("Delar_Intralink").trim() + "and Extract Delar is  " + extractAccount.get("ISSUER").trim());
            //Validation Primary Customer Name from Intralink to Extract
            primaryCustomerNameFlag = extractAccount.get("CSTNAM01").trim().equals(details.get("CustomerName").trim().toUpperCase());
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Primary Customer Name Intralink is " + details.get("CustomerName").trim() + "and Extract Primary Customer Name is  " + extractAccount.get("CSTNAM01").trim());
            //Validation Joint 1 Customer Name from Intralink to Extract
            jointOneCustomerNameFlag = extractAccount.get("CSTNAM02").trim().equals(details.get("Joint1_CustomerName").trim().toUpperCase());
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint One Customer Name Intralink is " + details.get("Joint1_CustomerName").trim() + "and Extract Joint One is  " + extractAccount.get("CSTNAM02").trim());
            //Validation Joint 2 Customer Name from Intralink to Extract
            jointTwoCustomerNameFlag = extractAccount.get("CSTNAM03").trim().equals(details.get("Joint2_CustomerName").trim().toUpperCase());
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint Two Customer Name Intralink is " + details.get("Joint2_CustomerName").trim() + "and Extract Joint Two is  " + extractAccount.get("CSTNAM03").trim());
            //Validation Joint 3 Customer Name from Intralink to Extract
            jointThreeCustomerNameFlag = extractAccount.get("CSTNAM04").trim().equals(details.get("Joint3_CustomerName").trim().toUpperCase());
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Joint Three Customer Name Intralink is " + details.get("Joint3_CustomerName").trim() + "and Extract Joint Three is  " + extractAccount.get("CSTNAM04").trim());
            gicFlag = extractAccount.get("CERTNUM").trim().equals(details.get("GIC_IntraLink").trim());
            LoggingUtils.log((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "GIC Intralink is " + details.get("GIC_IntraLink").trim() + "and Extract GIC is  " + extractAccount.get("CERTNUM").trim());

            // Verifying is account fields are matched with Intralink fields with respect to multiple records in extract file
            if (addressLine1Flag && addressLine2Flag && cityFlag && postalCodeFlag && accountFlag
                    && primaryCustomerNameFlag && jointOneCustomerNameFlag && jointTwoCustomerNameFlag && jointThreeCustomerNameFlag
                    && delarFlag && gicFlag) {
                flagList.add(true);
            } else {
                flagList.add(false);
            }

        }
        // Checks result based on 3 points.
        //            1. Checks is account is found in Extract file
        //            2. Checks why account is not present in Extract file
        //            3. Checks is any record is duplicated instead of maintaining 1 unique record
        String result = flagList.stream().filter(x -> x.equals(true)).collect(Collectors.toList()).size() == 1 ? "Only 1 record is present in extract file for 1 account of Intralink" :
                flagList.stream().filter(x -> x.equals(true)).collect(Collectors.toList()).size() > 1 ? "Intralink account is present more than 1 record in extract"
                        : "Intralink account is not present in extract";
        //When all fields are passed then it comes to below condition and only 1 record is present in Extract
        if (result.equals("Only 1 record is present in extract file for 1 account of Intralink")) {
            // Out of multiple records of extract file checks which record is matching with Intralink values

            int position = flagList.indexOf(true);
            // If account is present in extract make sure account is not closed account
            if (!details.get("AccountStatus_Intralink").equalsIgnoreCase("Close") && details.get("Trust_Accounts").equalsIgnoreCase("YES")) {
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNameExtract", extractAccounts.get(position).get("CSTNAM01"));

                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", extractAccounts.get(position).get("ADDRESS1"));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_RawDataSheet", extractAccounts.get(position).get("ADDRESS 2"));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_RawDataSheet", extractAccounts.get(position).get("ADDRESS 3"));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "PostalCode_RawDataSheet", extractAccounts.get(position).get("POSTCD"));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_Extract", extractAccounts.get(position).get("ISSUER"));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "GIC_RawDataSheet", extractAccounts.get(position).get("CERTNUM"));
                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  1 extract record field values are matched with Intralink Customer field values");
                executionStatus.add("Passed");
            } else if (details.get("AccountStatus_Intralink").equalsIgnoreCase("Close") && details.get("Trust_Accounts").equalsIgnoreCase("YES")) {
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNameExtract", extractAccounts.get(position).get("CSTNAM01"));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", extractAccounts.get(position).get("ADDRESS1"));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_RawDataSheet", extractAccounts.get(position).get("ADDRESS 2"));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_RawDataSheet", extractAccounts.get(position).get("ADDRESS 3"));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "PostalCode_RawDataSheet", extractAccounts.get(position).get("POSTCD"));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_Extract", extractAccounts.get(position).get("ISSUER"));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "GIC_RawDataSheet", extractAccounts.get(position).get("CERTNUM"));
                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "1 extract record field values are matched with Intralink Customer field values because Account Status is " + details.get("AccountStatus_Intralink")
                        + " and Acccount trust value is " + details.get("Trust_Accounts"));
                executionStatus.add("Fail (Close account is present in Extract)");
            } else if (details.get("Trust_Accounts").equalsIgnoreCase("NO")) {
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNameExtract", extractAccounts.get(position).get("CSTNAM01"));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", extractAccounts.get(position).get("ADDRESS1"));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_RawDataSheet", extractAccounts.get(position).get("ADDRESS 2"));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_RawDataSheet", extractAccounts.get(position).get("ADDRESS 3"));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "PostalCode_RawDataSheet", extractAccounts.get(position).get("POSTCD"));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_Extract", extractAccounts.get(position).get("ISSUER"));
                readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "GIC_RawDataSheet", extractAccounts.get(position).get("CERTNUM"));
                LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "1 extract record field values are matched with Intralink Customer field values because it is not trust account");
                executionStatus.add("Fail (Trust No accounts are present in Extract)");
            }


        }
        // Account is not present in Extract
        else if (result.equals("Intralink account is not present in extract")) {
            // Account is Trust account
            if (details.get("Trust_Accounts").equalsIgnoreCase("Yes")) {
                // Account is closed, So account shouild not present in extract file
                if (details.get("AccountStatus_Intralink").equalsIgnoreCase("Close")) {
                    LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  No Records found in Extract because Account Relation ship is Close");
                    executionStatus.add("Pass (Not present in Extract-Account RelationShip-> Close )");
                } else {
                    // Out of multiple records of extract file checks which field is not matching with Intralink values
                    int position = flagList.indexOf(false);
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "CustomerNameExtract", extractAccounts.get(position).get("CSTNAM01"));
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine1_RawDataSheet", extractAccounts.get(position).get("ADDRESS1"));
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "AddressLine2_RawDataSheet", extractAccounts.get(position).get("ADDRESS 2"));
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "City_RawDataSheet", extractAccounts.get(position).get("ADDRESS 3"));
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "PostalCode_RawDataSheet", extractAccounts.get(position).get("POSTCD"));
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "Dealer_Extract", extractAccounts.get(position).get("ISSUER"));
                    readFromExcel.saveColumnValueToSpecificScenarioSheetName(testdataPath, "CDIC_InterimTestData.xlsx", sheetName, getScenarioId(), "GIC_RawDataSheet", extractAccounts.get(position).get("CERTNUM"));
                    exectionStatusFailedFields = new HashMap<>();
                    exectionStatusFailedFields.put("AddressLine1", addressLine1Flag);
                    exectionStatusFailedFields.put("AddressLine2", addressLine2Flag);
                    exectionStatusFailedFields.put("City", cityFlag);
                    exectionStatusFailedFields.put("PostalCode", postalCodeFlag);
                    exectionStatusFailedFields.put("AccountNumber", accountFlag);
                    exectionStatusFailedFields.put("PrimaryCustomerName", primaryCustomerNameFlag);
                    exectionStatusFailedFields.put("JointOneCustomerName", jointOneCustomerNameFlag);
                    exectionStatusFailedFields.put("JointTwoCustomerName", jointTwoCustomerNameFlag);
                    exectionStatusFailedFields.put("JointThreeCustomerName", jointThreeCustomerNameFlag);
                    exectionStatusFailedFields.put("Dealer", delarFlag);
                    exectionStatusFailedFields.put("Gic", gicFlag);
                    //exectionStatusFailedFields.add()
                    executionStatus.add("Fail-> Mismatch Fields:-" + exectionStatusFailedFields.entrySet().
                            stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                            collect(Collectors.joining(",")));

                    LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Intralink and Extract fields are mismatched at " + exectionStatusFailedFields.entrySet().
                            stream().filter(x -> x.getValue().equals(false)).map(x -> x.getKey()).
                            collect(Collectors.joining(",")));
                }

            }
            // Account is not Trust account
            else if (details.get("Trust_Accounts").equalsIgnoreCase("NO")) {
                executionStatus.add("Pass (Not present in Extract-Trust-> No )");
                LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  No Records found in Extract because Account Number# " + details.get("AccountNumber") + " is not trust");
            }
        } else if (result.equals("Intralink account is present more than 1 record in extract")) {
            executionStatus.add("Fail (Duplicate records found in Extract)");
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Duplicate records are found in extract for account number:- " + extractAccounts.get(0).get("ACCTNO"));

        }
        return executionStatus;
    }

    /**
     * This function routes to different functions based on the account (Savings, IP English, IP French)
     *
     * @return List- Execution Status
     * @throws FilloException
     * @throws IOException
     */
    public List<String> verifyingIntralinkDetailsInExtract() throws FilloException, IOException {

        List<String> executionStatus = new LinkedList<>();
        // Verifying Savings and DDA accounts in Extract file
        countryCodes = readFromExcel.getSaveValue(System.getProperty("user.dir") + "/src/main/resources/data/", "CountryCodes.xlsx", "CountryCodes", details.get("State"));
        if (extractAccounts.size() != 0) {
            if (!(details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI") || (details.get("ProductName").contains("- BNE") || details.get("ProductName").contains("- PSI")) || details.get("ProductName").equals("Momentum PLUS Savings"))) {
                executionStatus = savingsDetailsVerification();
            }
            // Verifying IP Accounts and GIC's in Extract file
            //Compares Intralink account details with Extract values like AddressLine1, City, AddressLine2, PostalCode, GIC Account Number

//             Product Name contains BNS, SSI, Momentum Savings OR BNE, PSI then it enter below condition
            if (details.get("ProductName").contains("- BNS") || details.get("ProductName").contains("- SSI") || details.get("ProductName").contains("- BNE") || details.get("ProductName").contains("- PSI") || details.get("ProductName").equals("Momentum PLUS Savings")) {
//                When Sheets are French then it navigates to method - ipDetailsVerificationFrench
                if (sheetName.equalsIgnoreCase("LETTER1F") || sheetName.equalsIgnoreCase("LETTER2F") || sheetName.equalsIgnoreCase("LETTER3F") || sheetName.equalsIgnoreCase("LETTER2FPNTA") || sheetName.equalsIgnoreCase("LETTER3FPNTA")) {
                    executionStatus = ipDetailsVerificationFrench();
                }
//                When Sheets are English then it navigates to method - ipDetailsVerificationEnglish
                else if (sheetName.equalsIgnoreCase("LETTER1E") || sheetName.equalsIgnoreCase("LETTER2E") || sheetName.equalsIgnoreCase("LETTER3E") || sheetName.equalsIgnoreCase("LETTER2EPNTA") || sheetName.equalsIgnoreCase("LETTER3EPNTA")) {
                    executionStatus = ipDetailsVerificationEnglish();
                }
            }
        } else if (details.get("AccountStatus_Intralink").equalsIgnoreCase("Close")) {
            executionStatus.add("Pass-> Account Number is not found in extract file as it is close account ");
            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  No Records found in Extract because account is Closed");

        } else if (details.get("Trust_Accounts").equalsIgnoreCase("NO")) {
            executionStatus.add("Pass-> Account Number is not found in extract for Trust-NO ");
            LoggingUtils.logSuccess((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "**********Pass Statement*********  No Records found in Extract because it not trust account");
        } else {
            executionStatus.add("Fail-> Account Number is not found in extract file");
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "Account Number:- " + details.get("AccountNumber").trim() + " is not found in extract");
        }
        return executionStatus;
    }


//    DQ Rules

    /**
     * This function reads Text file and store all lines into List<Map<String,String></>></>
     */
    public List<Map<String, String>> readDQMaskingTextFiles(String path) throws IOException {
        // Creating file object by placing notepad file location
        File file = new File(path);
        int headerCounter = 0;
        // Reading file using Buffer Reader object
        try (BufferedReader br = new BufferedReader(new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8")))) {
            // Extract file individual line
            String extractLine;

            dqSamples = new ArrayList<Map<String, String>>();
            headerList = new ArrayList<String>();

            // Reading Extract file line by line
            while ((extractLine = br.readLine()) != null) {
                //Header values
                if (headerCounter == 0) {
                    for (String header : extractLine.split("\\|")) {
                        headerList.add(header.replace("﻿","").trim());
                    }
                    headerCounter = headerCounter + 1;
                } else {
                    //Object created to store all extract lines into dataList Object
                    dataList = new ArrayList<String>();
                    sample = new LinkedHashMap<String, String>();
                    // Spliting based on ";"
                    for (String s : extractLine.split("\\|")) {
                        //Storing all individual lines of text files in dataList object
                        dataList.add(s);
                    }
                    int headerDataDifference = headerList.size() - dataList.size();
                    if (headerDataDifference <= 3 && (headerList.size() - dataList.size()) != 0) {
                        for (int i = 0; i <= headerDataDifference; i++) {
                            dataList.add("");
                        }
                    }
                    // Mapping Headers and Header value into Map object
                    for (int i = 0; i < headerList.size(); i++) {
                        sample.put(headerList.get(i), dataList.get(i));
                    }

                    dqSamples.add(sample);
                }
            }
        }

        setDqSamples(dqSamples);
        return dqSamples;
    }


    public List<String> getHeaderList(String filePath) throws IOException {
        // Creating file object by placing notepad file location
        File file = new File(filePath);
        int headerCounter = 0;
        // Reading file using Buffer Reader object
        try (BufferedReader br = new BufferedReader(new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8")))) {
            // Extract file individual line
            String extractLine;

            headerList = new ArrayList<String>();

            // Reading Extract file line by line
            while ((extractLine = br.readLine()) != null) {
                //Header values of extract
                if (headerCounter == 0) {
                    for (String header : extractLine.split("\\|")) {
                        headerList.add(header.trim());
                    }
                    headerCounter = headerCounter + 1;
                } else {
                    break;
                }

            }
        }
        return headerList;
    }
}
