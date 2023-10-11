package com.quantum.steps;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qmetry.qaf.automation.core.ConfigurationManager;
import com.qmetry.qaf.automation.step.QAFTestStepProvider;
import com.quantum.utility.DQRules;
import com.quantum.utility.DataBaseData;
import com.quantum.utility.ExtentHelper;
import com.quantum.utility.ReadTextFile;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@QAFTestStepProvider
public class DQStepDefinition extends ExtentHelper {


    public static String getTableId() {
        return tableId;
    }

    public static void setTableId(String tableId) {
        DQStepDefinition.tableId = tableId;
    }

    private static String tableId = null;
    private static String tableValue1 = null;
    private static String tableValue2 = null;
    private static String tableValue3 = null;

    public static String getTableValue1() {
        return tableValue1;
    }

    public static void setTableValue1(String tableValue1) {
        DQStepDefinition.tableValue1 = tableValue1;
    }

    public static String getTableValue2() {
        return tableValue2;
    }

    public static void setTableValue2(String tableValue2) {
        DQStepDefinition.tableValue2 = tableValue2;
    }

    public static String getTableValue3() {
        return tableValue3;
    }

    public static void setTableValue3(String tableValue3) {
        DQStepDefinition.tableValue3 = tableValue3;
    }

    @Given("^dq rules definitions$")
    public void dqRulesDefinitions() {
        test.log("INFO", "Rule-1 --> Integer – each character in the string is an integer between 0 and 9\n" +
                "Definition: Series of 1 or more digits (0-9) – may be prefixed with “+” or “–”\n" +
                "Rule: IF NOT EMPTY THEN must be Integer");

        test.log("INFO", "Rule-2 --> Variable Character – any alphanumeric string\n" +
                "Definition: Series of alphanumeric characters (a-z, A-Z, 0-9, BLANK, `~!@#$%^&*()_-+={}[]:;\"'?/\\<>,.)\n" +
                "Rule: IF NOT EMPTY THEN must be Variable Character");

        test.log("INFO", "Rule-3 --> Character (n) – an alphanumeric character string of length n");

        test.log("INFO", "Rule-4 --> DATE – YYYYMMDD\n" +
                "Definition: Series of 8 digits (0 - 9) in format (YYYYMMDD) AND must be a valid date\n" +
                "Rule: IF NOT EMPTY THEN must be DATE");

        test.log("INFO", "Rule-5 --> DATE:TIME – YYYYMMDD:HHMMSS\n" +
                "Definition: <Date>:<Time> where\n" +
                "1 - <Date> is series of 8 digits (0 - 9) in format (YYYYMMDD) AND must be a valid date\n" +
                "2 - The colon character (:) is present\n" +
                "3 - <Time> is series of 6 digits (0 - 9) in format (HHMMSS); valid time otherwise “000000”\n" +
                "Rule: IF NOT EMPTY THEN must be DATE:TIME");

        test.log("INFO", "Rule-6 --> Decimal (X,Y) –with X integers plus Y integers after the decimal point\n" +
                "Definition: DECIMAL(X,Y) where\n" +
                "1 - The X portion of the decimal number is a series of digits (0 - 9) with a length up to a maximum of X number of numerical digits\n" +
                "2 - The decimal character is present (. OR ,)\n" +
                "3 - The Y portion of the decimal number is a series of digits (0 - 9) with a length up to a maximum of Y number of numerical digits\n" +
                "Rule: IF NOT EMPTY THEN must be DECIMAL(X,Y)");

        test.log("INFO", "Rule-9 --> PK\n" +
                "Definition: Primary Key check\n" +
                "Rule: IF PK must be NO DUPLICATES");

        test.log("INFO", "Rule-10 --> FK (-> to table FK)\n" +
                "Definition: Foreign Key check\n" +
                "Rule: IF FK, must be PK in related table");

        test.log("INFO", "Rule-13 --> Value must be one of Y or N");

        test.log("INFO", "Rule-15 --> Must be either GL or SL");

        test.log("INFO", "Rule-16 --> Must be either DR or CR");

        test.log("INFO", "Rule-18 --> Must not be blank");

        test.log("INFO", "Rule-21 --> Value must be one of Y, N, or EMPTY");

        test.log("INFO", "Rule-22 --> Value must be one of E, F or O");

        test.log("INFO", "Rule-24 --> Value must be one of A, P or EMPTY");

        test.log("INFO", "Rule-25 --> CRA Currency code");

        test.log("INFO", "Rule-26 --> ISO Currency code");

        test.log("INFO", "Rule-27 --> Country names as per Canada Post Addressing Guidelines / International Destination Listing");
    }

    @Then("^validate (\\d+)-depositor/Deposit account reference table$")
    public void validateDepositorDepositAccountReferenceTable(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId + "_dq").exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        List<String> results = new LinkedList<>();
        String mi = null;
        String headers = null;
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> depositorIdsTable500 = null;
            List<String> subSystemFilesDepositorIds0100 = null;
            List<String> accountUniqueIdsTable500 = null;
            List<String> subSystemFilesAccountUniqueIds0130 = null;
            List<String> relationShiptTypeCodeTable500 = null;
            List<String> subSystemFilesRelationShipTypeCode0501 = null;
            for (Map<String, String> dqRecord : dqRecords) {
                //        Depositor_Unique_ID
                //Rule:- 2 - It can have every character including french, It is free text field
                //Rule:- 10 - Values present the file should be present in other file, Which primary key
                //                for other file where as foreign key for current file verification
                //Rule:- 18 - It should not be blank
// -----------------------------           Depositor_Unique_ID-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Depositor_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") + " is as per Rule-2");

                } else {
                    System.err.println("--- FAIL ---> Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + " is not as per Rule-2");
                    test.fail("--- FAIL ---> Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + " is not as per Rule-2");
                }

                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (depositorIdsTable500 == null) {
                    depositorIdsTable500 = new DQRules().getListOfString(mi, "0100", "Depositor_Unique_ID", fileName.substring(26, 29), "DQ");
                    subSystemFilesDepositorIds0100 = new DQRules().getDQFileNames("0100").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).collect(Collectors.toList());
                    if (subSystemFilesDepositorIds0100.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0100:- " + mi + "*0100" + fileName.substring(22, 29) +
                                " is not found for Rule-304-Table-100");

                        System.err.println("--- FAIL ---> Sub_System_File-0100:- " + mi + "*0100" + fileName.substring(22, 29) +
                                " is not found for Rule-304-Table-100");
                    }
                }

                boolean depositorIdFlag100 = depositorIdsTable500.contains(dqRecord.get("Depositor_Unique_ID"));
                if (depositorIdFlag100) {
                    test.pass("**********Pass Statement********* Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") +
                            " is as per Rule-10");

                } else {
                    System.err.println("--- FAIL ---> Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") +
                            "  is not as per Rule-10 and not found in file " + subSystemFilesDepositorIds0100);
                    test.fail("--- FAIL ---> Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") +
                            "  is not as per Rule-10 and not found in file " + subSystemFilesDepositorIds0100);
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Depositor_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + " is as per Rule-18");

                } else {
                    System.err.println("--- FAIL ---> Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + " is not as per Rule-18");
                    test.fail("--- FAIL ---> Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + " is not as per Rule-18");
                }
// ----------------------------- Account_Unique_ID-----------------------------

                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Account_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));

                } else {
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-2 " +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-2 " +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification

                //   Rule-10
                if (accountUniqueIdsTable500 == null) {
                    accountUniqueIdsTable500 = new DQRules().getListOfString(mi, "0130", "Account_Unique_ID", fileName.substring(26, 29), "DQ");
                    subSystemFilesAccountUniqueIds0130 = new DQRules().getDQFileNames("0130").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFilesAccountUniqueIds0130.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0130:- " + mi + "*0130" + fileName.substring(22, 29) +
                                " is not found for Rule-304-Table-130");

                        System.err.println("--- FAIL ---> Sub_System_File-0130:- " + mi + "*0130" + fileName.substring(22, 29) +
                                " is not found for Rule-304-Table-130");
                    }
                }

                boolean accountUniqueIdFlag800 = accountUniqueIdsTable500.contains(dqRecord.get("Account_Unique_ID"));
                if (accountUniqueIdFlag800) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is as per Rule-10" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") +
                            " is not as per Rule-10 and not found in file " + subSystemFilesAccountUniqueIds0130 + " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") +
                            " is not as per Rule-10 and not found in file " + subSystemFilesAccountUniqueIds0130 +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Account_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is as per Rule-18" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));

                } else {
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-18" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-18" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }


// ----------------------------- Relationship_Type_Code-----------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Relationship_Type_Code"))) {
                    test.pass("**********Pass Statement********* Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code") +
                            " is as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));


                } else {
                    System.err.println("--- FAIL ---> Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code") +
                            " is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    test.fail("--- FAIL ---> Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code") +
                            " is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (relationShiptTypeCodeTable500 == null) {
                    relationShiptTypeCodeTable500 = new DQRules().getListOfString(mi, "0501", "Relationship_Type_Code", fileName.substring(26, 29), "DQ");
                    subSystemFilesRelationShipTypeCode0501 = new DQRules().getDQFileNames("0501").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFilesRelationShipTypeCode0501.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0501:- " + mi + "*0501" + fileName.substring(22, 29) +
                                " is not found for Rule-304-Table-501");

                        System.err.println("--- FAIL ---> Sub_System_File-0501:- " + mi + "*0501" + fileName.substring(22, 29) +
                                " is not found for Rule-501");
                    }
                }
                if (subSystemFilesRelationShipTypeCode0501.size() != 0) {
                    boolean relationShipTypeFlag = relationShiptTypeCodeTable500.contains(dqRecord.get("Relationship_Type_Code"));
                    if (relationShipTypeFlag) {
                        test.pass("**********Pass Statement********* Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code") +
                                " is as per Rule-10 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));

                    } else {
                        System.err.println("--- FAIL ---> Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFilesRelationShipTypeCode0501 +
                                " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                        test.fail("--- FAIL ---> Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFilesRelationShipTypeCode0501 +
                                " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    }
                } else {
                    test.fail("--- FAIL ---> Sub_System_File-0501:- " + mi + "*0501" + fileName.substring(22, 29) +
                            " is not found for Rule-304-Table-501");

                    System.err.println("--- FAIL ---> Sub_System_File-0501:- " + mi + "*0501" + fileName.substring(22, 29) +
                            " is not found for Rule-501");
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Relationship_Type_Code"))) {
                    test.pass("**********Pass Statement********* Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code") + " is as per Rule-18"
                            + " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    System.err.println("--- FAIL ---> Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code") +
                            " is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    test.fail("--- FAIL ---> Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code") +
                            " is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

// ----------------------------- Primary_Account_Holder_Flag-----------------------------
                //            Rule-3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("Primary_Account_Holder_Flag"), 1)) {
                    test.pass("**********Pass Statement********* Primary_Account_Holder_Flag:- " + dqRecord.get("Primary_Account_Holder_Flag") + " is as per Rule-1 " +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    System.err.println("--- FAIL ---> Primary_Account_Holder_Flag:- " + dqRecord.get("Primary_Account_Holder_Flag") +
                            " is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    test.fail("--- FAIL ---> Primary_Account_Holder_Flag:- " + dqRecord.get("Primary_Account_Holder_Flag") +
                            " is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }


                //Rule:- 13 - Only those characters only should display
//            Specific those characters in pipe delimited 'Y', 'N'

                if (rules.acceptsOnlySpecificCharactersRule13(dqRecord.get("Primary_Account_Holder_Flag"), "Y|N")) {
                    test.pass("**********Pass Statement********* Primary_Account_Holder_Flag:- " + dqRecord.get("Primary_Account_Holder_Flag") + " is as per Rule-13" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    System.err.println("--- FAIL ---> Primary_Account_Holder_Flag:- " + dqRecord.get("Primary_Account_Holder_Flag") +
                            " is not as per Rule-13 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    test.fail("--- FAIL ---> Primary_Account_Holder_Flag:- " + dqRecord.get("Primary_Account_Holder_Flag") +
                            " is not as per Rule-13 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Primary_Account_Holder_Flag"))) {
                    test.pass("**********Pass Statement********* Primary_Account_Holder_Flag:- " + dqRecord.get("Primary_Account_Holder_Flag") + " is as per Rule-18" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    System.err.println("--- FAIL ---> Primary_Account_Holder_Flag:- " + dqRecord.get("Primary_Account_Holder_Flag") +
                            " is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    test.fail("--- FAIL ---> Primary_Account_Holder_Flag:- " + dqRecord.get("Primary_Account_Holder_Flag") +
                            " is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                // ----------------------------- Payee_Flag-----------------------------
//            Rule-3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("Payee_Flag"), 1)) {
                    test.pass("**********Pass Statement********* Payee_Flag:- " + dqRecord.get("Payee_Flag") + " is as per Rule-3" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    System.err.println("--- FAIL ---> Payee_Flag:- " + dqRecord.get("Payee_Flag") +
                            " is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    test.fail("--- FAIL ---> Payee_Flag:- " + dqRecord.get("Payee_Flag") +
                            " is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }


                //Rule:- 13 - Only those characters only should display
//            Specific those characters in pipe delimited 'Y', 'N'

                if (rules.acceptsOnlySpecificCharactersRule13(dqRecord.get("Payee_Flag"), "Y|N")) {
                    test.pass("**********Pass Statement********* Payee_Flag:- " + dqRecord.get("Payee_Flag") + " is as per Rule-13" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    System.err.println("--- FAIL ---> Payee_Flag:- " + dqRecord.get("Payee_Flag") +
                            " is not as per Rule-13 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    test.fail("--- FAIL ---> Payee_Flag:- " + dqRecord.get("Payee_Flag") +
                            " is not as per Rule-13 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Payee_Flag"))) {
                    test.pass("**********Pass Statement********* Payee_Flag:- " + dqRecord.get("Payee_Flag") + " is as per Rule-18" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    System.err.println("--- FAIL ---> Payee_Flag:- " + dqRecord.get("Payee_Flag") +
                            " is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    test.fail("--- FAIL ---> Payee_Flag:- " + dqRecord.get("Payee_Flag") +
                            " is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    @Then("^validate (\\d+)-phone type$")
    public void validatePhoneType(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        String headers = null;
        List<String> results = new LinkedList<>();

        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> phoneTypeCodeTableRule90202 = null;
            List<String> phoneTypeCodeDuplicates = null;
            //Rule:- 1 - It should be integer type 0-9 and it can have prefix + or -
            // and size is greater than 1
//         //Rule:- 9 - It is a primary key and there should not be any duplicates and It is a mandatory field
//         Rule:-18  - Must not be blank
            for (Map<String, String> dqRecord : dqRecords) {
//            Rule-1
//------------------------------Phone_Type_Code---------------------------------------
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Phone_Type_Code"))) {
                    test.pass("**********Pass Statement********* Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code") + " is as per Rule-1");
                } else {
                    test.fail("Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code") +
                            " is not as per Rule-1");
                    System.err.println("Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code") +
                            " is not as per Rule-1");
                }


                //            Rule-9
                if (phoneTypeCodeTableRule90202 == null) {
                    phoneTypeCodeTableRule90202 = new DQRules().getListOfString(mi, "0202", "Phone_Type_Code", fileName.substring(26, 29), "DQ");
                    phoneTypeCodeDuplicates =
                            phoneTypeCodeTableRule90202.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(phoneTypeCodeDuplicates.contains(dqRecord.get("Phone_Type_Code")))) {
                    test.pass("**********Pass Statement********* Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code") + " is not as per Rule-9");
                }


                //   Rule-18

                if (rules.noBlankRule18(dqRecord.get("Phone_Type_Code"))) {
                    test.pass("**********Pass Statement********* Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code") + " is as per Rule-18");
                } else {
                    test.fail("Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code") +
                            " is not as per Rule-18");
                    System.err.println("Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code") +
                            " is not as per Rule-18");
                }


//------------------------------Description---------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-2" +
                            " for Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code"));
                } else {
                    test.fail("Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code"));
                    System.err.println("Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-18" +
                            " for Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code"));
                }
                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));

    }

    @Then("^validate (\\d+)-Deposit Account Data$")
    public void validateDepositAccountData(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        List<String> fileValues = new DQRules().getDQFileNames(tableId);
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> depositorUniqueIdTableRule90100 = null;
            List<String> depositorUniqueIdDuplicates = null;
            List<String> accountUniqueIdTable0800 = null;
            List<String> subSystemFiles0800 = null;
            List<String> accountUniqueIdTable0900 = null;
            List<String> subSystemFiles0900 = null;
            List<String> productCodeTable0231 = null;
            List<String> subSystemFiles0231 = null;
            List<String> registeredPlanTypeCodeTable0232 = null;
            List<String> subSystemFiles0232 = null;
            List<String> currencyCodeTable0233 = null;
            List<String> subSystemFiles0233 = null;
            List<String> insuranceDeterminationCategoryTypeCodeTable0234 = null;
            List<String> subSystemFiles0234 = null;
            List<String> accountStatusCodeTable0236 = null;
            List<String> subSystemFiles0236 = null;
            List<String> trustAccountTypeCodeTable0237 = null;
            List<String> subSystemFiles0237 = null;
            List<String> cdicHoldStatusCodeTable0235 = null;
            List<String> subSystemFiles0235 = null;
            List<String> clearingAccountCodeTable0238 = null;
            List<String> subSystemFiles0238 = null;
            List<String> accountTypeCodeTable0239 = null;
            List<String> subSystemFiles0239 = null;
            for (Map<String, String> dqRecord : dqRecords) {
//-------------------------------------------Account_Unique_ID------------------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Account_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is as per Rule-2");
                } else {
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-2");
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-2");
                }


                //            Rule-9
                // Rule:- 9 New code
                if (depositorUniqueIdTableRule90100 == null) {
                    depositorUniqueIdTableRule90100 = new DQRules().getListOfString(mi, "0130", "Account_Unique_ID", fileName.substring(26, 29), "DQ");
                    depositorUniqueIdDuplicates =
                            depositorUniqueIdTableRule90100.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(depositorUniqueIdDuplicates.contains(dqRecord.get("Account_Unique_ID")))
                        || dqRecord.get("Account_Unique_ID").isEmpty()) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-9");
                }


                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (accountUniqueIdTable0800 == null) {
                    accountUniqueIdTable0800 = new DQRules().getListOfString(mi, "0800", "Account_Unique_ID", fileName.substring(26, 29), "DQ");
                    subSystemFiles0800 = new DQRules().getDQFileNames("0800").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0800.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0800:- " + mi + "*0800" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0800.size() != 0) {
                    boolean accountUniqueIdFlag800 = accountUniqueIdTable0800.contains(dqRecord.get("Account_Unique_ID"));
                    if (accountUniqueIdFlag800) {
                        test.pass("**********Pass Statement********* Account_Unique_ID-0800:- " + dqRecord.get("Account_Unique_ID") + " is as per Rule-10");
                    } else {
                        test.fail("--- FAIL ---> Account_Unique_ID-0800:- " + dqRecord.get("Account_Unique_ID") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0800);

                        System.err.println("--- FAIL ---> Account_Unique_ID-0800:- " + dqRecord.get("Account_Unique_ID") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0800);
                    }

                }


                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (accountUniqueIdTable0900 == null) {
                    accountUniqueIdTable0900 = new DQRules().getListOfString(mi, "0900", "Account_Unique_ID", fileName.substring(26, 29), "DQ");
                    subSystemFiles0900 = new DQRules().getDQFileNames("0900").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0900.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0900:- " + mi + "*0900" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0900.size() != 0) {
                    boolean accountUniqueIdFlag900 = accountUniqueIdTable0900.contains(dqRecord.get("Account_Unique_ID"));
                    //                Verifying value in table Id - 0900
                    if (accountUniqueIdFlag900) {
                        test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is as per Rule-10");
                    } else {
                        test.fail("--- FAIL ---> Account_Unique_ID-0900:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-10 and not found in file " + subSystemFiles0900);
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-10 and not found in file " + subSystemFiles0900);
                    }

                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Account_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is as per Rule-18");

                } else {
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-18");
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-18");
                }

//-------------------------------------------Account_Number------------------------------------------------
//Rule:- 2 - It can have every character including french, It is free text field
//Rule:- 18 - It should not be blank
//            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Account_Number"))) {
                    test.pass("**********Pass Statement********* Account_Number:- " + dqRecord.get("Account_Number") + " is as per Rule-2" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Account_Number:- " + dqRecord.get("Account_Number") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Account_Number:- " + dqRecord.get("Account_Number") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Account_Number"))) {
                    test.pass("**********Pass Statement********* Account_Number:- " + dqRecord.get("Account_Number") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Account_Number:- " + dqRecord.get("Account_Number") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Account_Number:- " + dqRecord.get("Account_Number") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }
//-------------------------------------------Account_Branch------------------------------------------------
//            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Account_Branch"))) {
                    test.pass("**********Pass Statement********* Account_Branch:- " + dqRecord.get("Account_Branch") + " is as per Rule-2" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Account_Branch:- " + dqRecord.get("Account_Branch") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Account_Branch:- " + dqRecord.get("Account_Branch") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

//-------------------------------------------Product_Code------------------------------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Product_Code"))) {
                    test.pass("**********Pass Statement********* Product_Code:- " + dqRecord.get("Product_Code") + " is as per Rule-1" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Product_Code:- " + dqRecord.get("Product_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Product_Code:- " + dqRecord.get("Product_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (productCodeTable0231 == null) {
                    productCodeTable0231 = new DQRules().getListOfString(mi, "0231", "Product_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0231 = new DQRules().getDQFileNames("0231").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0231.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0231:- " + mi + "*0231" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0231.size() != 0) {
                    boolean productCodeFlag231 = productCodeTable0231.contains(dqRecord.get("Product_Code"));
                    //                Verifying value in table Id - 0231
                    if (productCodeFlag231) {
                        test.pass("**********Pass Statement********* Product_Code:- " + dqRecord.get("Product_Code") + " is as per Rule-10" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Product_Code:- " + dqRecord.get("Product_Code") +
                                " is not found in table:- 0231, So it is not as per Rule-10 and not found in file " + subSystemFiles0231 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Product_Code:- " + dqRecord.get("Product_Code") +
                                " is not found in table:- 0231, So it is not as per Rule-10 and not found in file " + subSystemFiles0231 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    }

                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Product_Code"))) {
                    test.pass("**********Pass Statement********* Product_Code:- " + dqRecord.get("Product_Code") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Product_Code:- " + dqRecord.get("Product_Code") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Product_Code:- " + dqRecord.get("Product_Code") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

//-------------------------------------------Registered_Plan_Type_Code------------------------------------------------
                //            Rule-1

                if (rules.onlyNumericCharactersRule1(dqRecord.get("Registered_Plan_Type_Code"))) {
                    test.pass("**********Pass Statement********* Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code") + " is as per Rule-1" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (registeredPlanTypeCodeTable0232 == null) {
                    registeredPlanTypeCodeTable0232 = new DQRules().getListOfString(mi, "0232", "Registered_Plan_Type_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0232 = new DQRules().getDQFileNames("0232").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0232.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0232:- " + mi + "*0232" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0232.size() != 0) {
                    boolean registeredPlanTypeCodeFlag232 = registeredPlanTypeCodeTable0232.contains(dqRecord.get("Registered_Plan_Type_Code"));
                    //                Verifying value in table Id - 0232
                    if (registeredPlanTypeCodeFlag232) {
                        test.pass("**********Pass Statement********* Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code") + " is as per Rule-10" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0232 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0232 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    }

                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Registered_Plan_Type_Code"))) {
                    test.pass("**********Pass Statement********* Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------Registered_Plan_Number------------------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Registered_Plan_Number")) ||
                        dqRecord.get("Registered_Plan_Number").isEmpty()) {
                    test.pass("**********Pass Statement********* Registered_Plan_Number:- " + dqRecord.get("Registered_Plan_Number") + " is as per Rule-2" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Registered_Plan_Number:- " + dqRecord.get("Registered_Plan_Number") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Registered_Plan_Number:- " + dqRecord.get("Registered_Plan_Number") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

//-------------------------------------------Currency_Code------------------------------------------------
                //            Rule-1

                if (rules.onlyNumericCharactersRule1(dqRecord.get("Currency_Code"))) {
                    test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") + " is as per Rule-1" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //   Rule-10
                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (currencyCodeTable0233 == null) {
                    currencyCodeTable0233 = new DQRules().getListOfString(mi, "0233", "Currency_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0233 = new DQRules().getDQFileNames("0233").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0233.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0233:- " + mi + "*0233" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }

                if (subSystemFiles0233.size() != 0) {
                    boolean currencyCodeFlag233 = currencyCodeTable0233.contains(dqRecord.get("Currency_Code"));
                    //                Verifying value in table Id - 0233
                    if (currencyCodeFlag233) {
                        test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") + " is as per Rule-10" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0233 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0233 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    }

                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Currency_Code"))) {
                    test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

//-------------------------------------------Insurance_Determination_Category_Type_Code------------------------------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Insurance_Determination_Category_Type_Code"))) {
                    test.pass("**********Pass Statement********* Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code") + " is as per Rule-1" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //   Rule-10
                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (insuranceDeterminationCategoryTypeCodeTable0234 == null) {
                    insuranceDeterminationCategoryTypeCodeTable0234 = new DQRules().getListOfString(mi, "0234", "Insurance_Determination_Category_Type_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0234 = new DQRules().getDQFileNames("0234").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0234.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0234:- " + mi + "*0234" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0234.size() != 0) {
                    boolean insuranceDeterminationCategoryTypeCodeFlag234 = insuranceDeterminationCategoryTypeCodeTable0234.contains(dqRecord.get("Insurance_Determination_Category_Type_Code"));
                    //                Verifying value in table Id - 0234
                    if (insuranceDeterminationCategoryTypeCodeFlag234) {
                        test.pass("**********Pass Statement********* Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code") + " is as per Rule-10" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0234 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0234 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    }

                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Insurance_Determination_Category_Type_Code"))) {
                    test.pass("**********Pass Statement********* Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

//-------------------------------------------Account_Balance------------------------------------------------
                //            Rule-6
                if (rules.amountValidationRule6(dqRecord.get("Account_Balance"), "30", "2")) {
                    test.pass("**********Pass Statement********* Account_Balance:- " + dqRecord.get("Account_Balance") + " is as per Rule-6" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Account_Balance:- " + dqRecord.get("Account_Balance") +
                            " is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Account_Balance:- " + dqRecord.get("Account_Balance") +
                            " is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Account_Balance"))) {
                    test.pass("**********Pass Statement********* Account_Balance:- " + dqRecord.get("Account_Balance") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Account_Balance:- " + dqRecord.get("Account_Balance") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Account_Balance:- " + dqRecord.get("Account_Balance") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


//-------------------------------------------Accessible_Balance------------------------------------------------
                //            Rule-6
                if (rules.amountValidationRule6(dqRecord.get("Accessible_Balance"), "30", "2")) {
                    test.pass("**********Pass Statement********* Accessible_Balance:- " + dqRecord.get("Accessible_Balance") + " is as per Rule-6" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Accessible_Balance:- " + dqRecord.get("Accessible_Balance") +
                            " is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Accessible_Balance:- " + dqRecord.get("Accessible_Balance") +
                            " is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Accessible_Balance"))) {
                    test.pass("**********Pass Statement********* Accessible_Balance:- " + dqRecord.get("Accessible_Balance") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Accessible_Balance:- " + dqRecord.get("Accessible_Balance") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Accessible_Balance:- " + dqRecord.get("Accessible_Balance") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

//-------------------------------------------Maturity_Date------------------------------------------------
                //            Rule-4
                if (rules.birthDateValidationRule4(dqRecord.get("Maturity_Date"))) {
                    test.pass("**********Pass Statement********* Maturity_Date:- " + dqRecord.get("Maturity_Date") + " is as per Rule-4" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Maturity_Date:- " + dqRecord.get("Maturity_Date") +
                            " is not as per Rule-4 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Maturity_Date:- " + dqRecord.get("Maturity_Date") +
                            " is not as per Rule-4 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

//-------------------------------------------Account_Status_Code------------------------------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Account_Status_Code"))) {
                    test.pass("**********Pass Statement********* Account_Status_Code:- " + dqRecord.get("Account_Status_Code") + " is as per Rule-1" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Account_Status_Code:- " + dqRecord.get("Account_Status_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Account_Status_Code:- " + dqRecord.get("Account_Status_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //   Rule-10
                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (accountStatusCodeTable0236 == null) {
                    accountStatusCodeTable0236 = new DQRules().getListOfString(mi, "0236", "Account_Status_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0236 = new DQRules().getDQFileNames("0236").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0236.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0236:- " + mi + "*0236" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0236.size() != 0) {
                    boolean accountStatusCoseFlag236 = accountStatusCodeTable0236.contains(dqRecord.get("Account_Status_Code"));
                    //                Verifying value in table Id - 0234
                    if (accountStatusCoseFlag236) {
                        test.pass("**********Pass Statement********* Account_Status_Code:- " + dqRecord.get("Account_Status_Code") + " is as per Rule-10" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Account_Status_Code:- " + dqRecord.get("Account_Status_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0236 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Account_Status_Code:- " + dqRecord.get("Account_Status_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0236 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    }

                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Account_Status_Code"))) {
                    test.pass("**********Pass Statement********* Account_Status_Code:- " + dqRecord.get("Account_Status_Code") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Account_Status_Code:- " + dqRecord.get("Account_Status_Code") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Account_Status_Code:- " + dqRecord.get("Account_Status_Code") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------Trust_Account_Type_Code------------------------------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Trust_Account_Type_Code"))) {
                    test.pass("**********Pass Statement********* Trust_Account_Type_Code:- " + dqRecord.get("Trust_Account_Type_Code") + " is as per Rule-1" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Trust_Account_Type_Code:- " + dqRecord.get("Trust_Account_Type_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Trust_Account_Type_Code:- " + dqRecord.get("Trust_Account_Type_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //   Rule-10
                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (trustAccountTypeCodeTable0237 == null) {
                    trustAccountTypeCodeTable0237 = new DQRules().getListOfString(mi, "0237", "Trust_Account_Type_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0237 = new DQRules().getDQFileNames("0237").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0237.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0237:- " + mi + "*0237" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0237.size() != 0) {
                    boolean trustAccountTypeCodeFlag237 = trustAccountTypeCodeTable0237.contains(dqRecord.get("Trust_Account_Type_Code"));
                    //                Verifying value in table Id - 0234
                    if (trustAccountTypeCodeFlag237) {
                        test.pass("**********Pass Statement********* Trust_Account_Type_Code:- " + dqRecord.get("Trust_Account_Type_Code") + " is as per Rule-10" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Trust_Account_Type_Code:- " + dqRecord.get("Trust_Account_Type_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0237 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Trust_Account_Type_Code:- " + dqRecord.get("Trust_Account_Type_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0237 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    }
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Trust_Account_Type_Code"))) {
                    test.pass("**********Pass Statement********* Trust_Account_Type_Code:- " + dqRecord.get("Trust_Account_Type_Code") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Trust_Account_Type_Code:- " + dqRecord.get("Trust_Account_Type_Code") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Trust_Account_Type_Code:- " + dqRecord.get("Trust_Account_Type_Code") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------CDIC_Hold_Status_Code------------------------------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("CDIC_Hold_Status_Code"))) {
                    test.pass("**********Pass Statement********* CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") + " is as per Rule-1" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //   Rule-10
                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (cdicHoldStatusCodeTable0235 == null) {
                    cdicHoldStatusCodeTable0235 = new DQRules().getListOfString(mi, "0235", "CDIC_Hold_Status_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0235 = new DQRules().getDQFileNames("0235").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0235.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0235:- " + mi + "*0235" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }

                if (subSystemFiles0235.size() != 0) {
                    boolean cdicHoldStatusCodeTableFlag235 = cdicHoldStatusCodeTable0235.contains(dqRecord.get("CDIC_Hold_Status_Code"));
                    //                Verifying value in table Id - 0235
                    if (cdicHoldStatusCodeTableFlag235) {
                        test.pass("**********Pass Statement********* CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") + " is as per Rule-10" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0235 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0235 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    }
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("CDIC_Hold_Status_Code"))) {
                    test.pass("**********Pass Statement********* CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------Joint_Account_Flag------------------------------------------------
                //            Rule-3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("Joint_Account_Flag"), 1)) {
                    test.pass("**********Pass Statement********* Joint_Account_Flag:- " + dqRecord.get("Joint_Account_Flag") + " is as per Rule-3" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Joint_Account_Flag:- " + dqRecord.get("Joint_Account_Flag") +
                            " is not as per Rule-3 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Joint_Account_Flag:- " + dqRecord.get("Joint_Account_Flag") +
                            " is not as per Rule-3 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //Rule:- 13 - Only those characters only should display
//            Specific those characters in pipe delimited 'Y', 'N'
                if (rules.acceptsOnlySpecificCharactersRule13(dqRecord.get("Joint_Account_Flag"), "Y|N")) {
                    test.pass("**********Pass Statement********* Joint_Account_Flag:- " + dqRecord.get("Joint_Account_Flag") + " is as per Rule-13" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Joint_Account_Flag:- " + dqRecord.get("Joint_Account_Flag") +
                            " is not as per Rule-13 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Joint_Account_Flag:- " + dqRecord.get("Joint_Account_Flag") +
                            " is not as per Rule-13 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Joint_Account_Flag"))) {
                    test.pass("**********Pass Statement********* Joint_Account_Flag:- " + dqRecord.get("Joint_Account_Flag") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Joint_Account_Flag:- " + dqRecord.get("Joint_Account_Flag") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Joint_Account_Flag:- " + dqRecord.get("Joint_Account_Flag") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------Clearing_Account_Code------------------------------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Clearing_Account_Code"))) {
                    test.pass("**********Pass Statement********* Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code") + " is as per Rule-1" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //   Rule-10
                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (clearingAccountCodeTable0238 == null) {
                    clearingAccountCodeTable0238 = new DQRules().getListOfString(mi, "0238", "Clearing_Account_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0238 = new DQRules().getDQFileNames("0238").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0238.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0238:- " + mi + "*0238" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }

                if (subSystemFiles0238.size() != 0) {
                    boolean cdicHoldStatusCodeTableFlag238 = clearingAccountCodeTable0238.contains(dqRecord.get("Clearing_Account_Code"));
                    //                Verifying value in table Id - 0238
                    if (cdicHoldStatusCodeTableFlag238) {
                        test.pass("**********Pass Statement********* Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code") + " is as per Rule-10" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0238 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0238 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    }

                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Clearing_Account_Code"))) {
                    test.pass("**********Pass Statement********* Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------Account_Type_Code------------------------------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Account_Type_Code"))) {
                    test.pass("**********Pass Statement********* Account_Type_Code:- " + dqRecord.get("Account_Type_Code") + " is as per Rule-1" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Account_Type_Code:- " + dqRecord.get("Account_Type_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Account_Type_Code:- " + dqRecord.get("Account_Type_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //   Rule-10
                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (accountTypeCodeTable0239 == null) {
                    accountTypeCodeTable0239 = new DQRules().getListOfString(mi, "0239", "Account_Type_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0239 = new DQRules().getDQFileNames("0239").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0239.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0239:- " + mi + "*0239" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }

                if (subSystemFiles0239.size() != 0) {
                    boolean accountTypeCodeFlag239 = accountTypeCodeTable0239.contains(dqRecord.get("Account_Type_Code"));
                    //                Verifying value in table Id - 0238
                    if (accountTypeCodeFlag239) {
                        test.pass("**********Pass Statement********* Account_Type_Code:- " + dqRecord.get("Account_Type_Code") + " is as per Rule-10" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Account_Type_Code:- " + dqRecord.get("Account_Type_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0239 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Account_Type_Code:- " + dqRecord.get("Account_Type_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0239 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    }
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Account_Type_Code"))) {
                    test.pass("**********Pass Statement********* Account_Type_Code:- " + dqRecord.get("Account_Type_Code") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Account_Type_Code:- " + dqRecord.get("Account_Type_Code") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Account_Type_Code:- " + dqRecord.get("Account_Type_Code") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //-------------------------------------------MI_Issued_Registered_Account_Flag------------------------------------------------
                //            Rule-3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("MI_Issued_Registered_Account_Flag"), 1)) {
                    test.pass("**********Pass Statement********* MI_Issued_Registered_Account_Flag:- " + dqRecord.get("MI_Issued_Registered_Account_Flag") + " is as per Rule-3" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> MI_Issued_Registered_Account_Flag:- " + dqRecord.get("MI_Issued_Registered_Account_Flag") +
                            " is not as per Rule-3 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> MI_Issued_Registered_Account_Flag:- " + dqRecord.get("MI_Issued_Registered_Account_Flag") +
                            " is not as per Rule-3 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //Rule:- 21
                if (rules.acceptsOnlySpecificCharactersRule21(dqRecord.get("MI_Issued_Registered_Account_Flag"), "Y|N|^$")
                        || dqRecord.get("MI_Issued_Registered_Account_Flag").isEmpty()) {
                    test.pass("**********Pass Statement********* MI_Issued_Registered_Account_Flag:- " + dqRecord.get("MI_Issued_Registered_Account_Flag") + " is as per Rule-21" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> MI_Issued_Registered_Account_Flag:- " + dqRecord.get("MI_Issued_Registered_Account_Flag") +
                            " is not as per Rule-21 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> MI_Issued_Registered_Account_Flag:- " + dqRecord.get("MI_Issued_Registered_Account_Flag") +
                            " is not as per Rule-21 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

//-------------------------------------------MI_Related_Deposit_Flag------------------------------------------------
                //            Rule-3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("MI_Related_Deposit_Flag"), 1)) {
                    test.pass("**********Pass Statement********* MI_Related_Deposit_Flag:- " + dqRecord.get("MI_Related_Deposit_Flag") + " is as per Rule-3" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> MI_Related_Deposit_Flag:- " + dqRecord.get("MI_Related_Deposit_Flag") +
                            " is not as per Rule-3 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> MI_Related_Deposit_Flag:- " + dqRecord.get("MI_Related_Deposit_Flag") +
                            " is not as per Rule-3 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //Rule:- 13 - Only those characters only should display
//            Specific those characters in pipe delimited 'Y', 'N'
                if (rules.acceptsOnlySpecificCharactersRule13(dqRecord.get("MI_Related_Deposit_Flag"), "Y|N")) {
                    test.pass("**********Pass Statement********* Joint_Account_Flag:- " + dqRecord.get("Joint_Account_Flag") + " is as per Rule-13" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> MI_Related_Deposit_Flag:- " + dqRecord.get("MI_Related_Deposit_Flag") +
                            " is not as per Rule-13 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> MI_Related_Deposit_Flag:- " + dqRecord.get("MI_Related_Deposit_Flag") +
                            " is not as per Rule-13 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("MI_Related_Deposit_Flag"))) {
                    test.pass("**********Pass Statement********* MI_Related_Deposit_Flag:- " + dqRecord.get("MI_Related_Deposit_Flag") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> MI_Related_Deposit_Flag:- " + dqRecord.get("MI_Related_Deposit_Flag") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> MI_Related_Deposit_Flag:- " + dqRecord.get("MI_Related_Deposit_Flag") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }
                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }


    @Then("^validate (\\d+)-MI Published Foreign Currency Exchange Rate$")
    public void validateMIPublishedForeignCurrencyExchangeRate(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> isoCurrencyCodeTableRule90242 = null;
            List<String> isoCurrencyCodeDuplicates = null;
            List<String> isoCurrencyCodeTableRule100242 = null;
            List<String> subSystemFiles0233 = null;
            for (Map<String, String> dqRecord : dqRecords) {
//-------------------------------------------ISO_Currency_Code------------------------------------------------

                //Rule:- 3 - It should be character length of character is 3
                //            Rule-3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("ISO_Currency_Code"), 3)) {
                    test.pass("**********Pass Statement********* ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") + " is as per Rule-3");
                } else {
                    test.fail("--- FAIL ---> ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") +
                            " is not as per Rule-3");
                    System.err.println("--- FAIL ---> ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") +
                            " is not as per Rule-3");
                }

                //            Rule-9
                // Rule:- 9 New code
                if (isoCurrencyCodeTableRule90242 == null) {
                    isoCurrencyCodeTableRule90242 = new DQRules().getListOfString(mi, "0242", "ISO_Currency_Code", fileName.substring(26, 29), "DQ");
                    isoCurrencyCodeDuplicates =
                            isoCurrencyCodeTableRule90242.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(isoCurrencyCodeDuplicates.contains(dqRecord.get("ISO_Currency_Code")))
                        || dqRecord.get("ISO_Currency_Code").isEmpty()) {
                    test.pass("**********Pass Statement********* ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") + " is not as per Rule-9");
                }

                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (isoCurrencyCodeTableRule100242 == null) {
                    isoCurrencyCodeTableRule100242 = new DQRules().getListOfString(mi, "0233", "ISO_Currency_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0233 = new DQRules().getDQFileNames("0233").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0233.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0233:- " + mi + "*0233" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0233.size() != 0) {
                    boolean isoCurrencyCodeFlag233 = isoCurrencyCodeTableRule100242.contains(dqRecord.get("ISO_Currency_Code"));
                    if (isoCurrencyCodeFlag233) {
                        test.pass("**********Pass Statement********* ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") + " is as per Rule-10");
                    } else {
                        test.fail("--- FAIL ---> ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") +
                                " is not found in table:- 0233, So it is not as per Rule-10 and not found in file " + subSystemFiles0233);
                        System.err.println("--- FAIL ---> ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") +
                                " is not found in table:- 0233, So it is not as per Rule-10 and not found in file " + subSystemFiles0233);
                    }
                }

                //Rule-18
                if (rules.noBlankRule18(dqRecord.get("ISO_Currency_Code"))) {
                    test.pass("**********Pass Statement********* ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") + " is as per Rule-18");
                } else {
                    test.fail("--- FAIL ---> ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") +
                            " is not as per Rule-18");
                    System.err.println("--- FAIL ---> ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") +
                            " is not as per Rule-18");
                }

                //Rule-26
                if (rules.isoCountryRule26(dqRecord.get("ISO_Currency_Code"))) {
                    test.pass("**********Pass Statement********* ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") + " is as per Rule-26");
                } else {
                    test.fail("--- FAIL ---> ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") +
                            " is not as per Rule-26");
                    System.err.println("--- FAIL ---> ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") +
                            " is not as per Rule-26");
                }

//-------------------------------------------Foreign_Currency_CAD_FX------------------------------------------------
                //            Rule-6
                if (rules.amountValidationRule6(dqRecord.get("Foreign_Currency_CAD_FX"), "30", "6")) {
                    test.pass("**********Pass Statement********* Foreign_Currency_CAD_FX:- " + dqRecord.get("Foreign_Currency_CAD_FX") + " is as per Rule-6" +
                            " for ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code"));
                } else {
                    test.fail("--- FAIL ---> Foreign_Currency_CAD_FX:- " + dqRecord.get("Foreign_Currency_CAD_FX") +
                            " is not as per Rule-6 for ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code"));
                    System.err.println("--- FAIL ---> Foreign_Currency_CAD_FX:- " + dqRecord.get("Foreign_Currency_CAD_FX") +
                            " is not as per Rule-6 for ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code"));
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Foreign_Currency_CAD_FX"))) {
                    test.pass("**********Pass Statement********* Foreign_Currency_CAD_FX:- " + dqRecord.get("Foreign_Currency_CAD_FX") + " is as per Rule-18" +
                            " for ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code"));
                } else {
                    test.fail("--- FAIL ---> Foreign_Currency_CAD_FX:- " + dqRecord.get("Foreign_Currency_CAD_FX") +
                            " is not as per Rule-18 for ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code"));
                    System.err.println("--- FAIL ---> Foreign_Currency_CAD_FX:- " + dqRecord.get("Foreign_Currency_CAD_FX") +
                            " is not as per Rule-18 for ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code"));
                }
                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    @Then("^validate (\\d+)-Insurance Determination Category Type$")
    public void validateInsuranceDeterminationCategoryType(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> insuranceDeterminationCategoryTypeCodeTableRule90100 = null;
            List<String> insuranceDeterminationCategoryTypeCodeDuplicates = null;
            for (Map<String, String> dqRecord : dqRecords) {
//-------------------------------------------Insurance_Determination_Category_Type_Code------------------------------------------------
//                Rule:- 1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Insurance_Determination_Category_Type_Code"))) {
                    test.pass("**********Pass Statement********* Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code") + " is as per Rule-1");
                } else {
                    test.fail("--- FAIL ---> Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code") +
                            " is not as per Rule-1");
                    System.err.println("--- FAIL ---> Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code") +
                            " is not as per Rule-1");
                }

                //            Rule-9
                // Rule:- 9 New code
                if (insuranceDeterminationCategoryTypeCodeTableRule90100 == null) {
                    insuranceDeterminationCategoryTypeCodeTableRule90100 = new DQRules().getListOfString(mi, "0234", "Insurance_Determination_Category_Type_Code", fileName.substring(26, 29), "DQ");
                    insuranceDeterminationCategoryTypeCodeDuplicates =
                            insuranceDeterminationCategoryTypeCodeTableRule90100.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(insuranceDeterminationCategoryTypeCodeDuplicates.contains(dqRecord.get("Insurance_Determination_Category_Type_Code")))
                        || dqRecord.get("Insurance_Determination_Category_Type_Code").isEmpty()) {
                    test.pass("**********Pass Statement********* Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code") + " is not as per Rule-9");
                }

                //Rule:- 18 - It should not be blank
                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Insurance_Determination_Category_Type_Code"))) {
                    test.pass("**********Pass Statement********* Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code") + " is as per Rule-18");
                } else {
                    test.fail("--- FAIL ---> Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code") + " is not as per Rule-18");
                    System.err.println("--- FAIL ---> Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code") + " is not as per Rule-18");
                }


                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-2" +
                            " for Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-18" +
                            " for Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Insurance_Determination_Category_Type_Code:- " + dqRecord.get("Insurance_Determination_Category_Type_Code"));
                }

                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));

    }

    @Then("^validate (\\d+)-Beneficiary Data - Not a Nominee Broker and not a Professional Trustee Account$")
    public void validateBeneficiaryDataNotANomineeBrokerAndNotAProfessionalTrusteeAccount(String tableId) throws
            IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        List<String> fileValues = new DQRules().getDQFileNames(tableId);
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> accountUniqueIdTable0130 = null;
            List<String> accountUniqueIdSubSystemFiles0130 = null;
            List<String> accountNumberTable0130 = null;
            List<String> accountNumberSubSystemFiles0130 = null;
            for (Map<String, String> dqRecord : dqRecords) {
// ----------------------------- Account_Unique_ID-----------------------------

                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Account_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is as per Rule-2");

                } else {
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-2");
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-2");
                }


                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (accountUniqueIdTable0130 == null) {
                    accountUniqueIdTable0130 = new DQRules().getListOfString(mi, "0130", "Account_Unique_ID", fileName.substring(26, 29), "DQ");
                    accountUniqueIdSubSystemFiles0130 = new DQRules().getDQFileNames("0130").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (accountUniqueIdSubSystemFiles0130.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0130:- " + mi + "*0130" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (accountUniqueIdSubSystemFiles0130.size() != 0) {
                    boolean accountUniqueIdFlag0130 = accountUniqueIdTable0130.contains(dqRecord.get("Account_Unique_ID"));
                    if (accountUniqueIdFlag0130) {
                        test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") +
                                " is as per Rule-10");

                    } else {
                        test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") +
                                " is not as per Rule-10 and not found in file " + accountUniqueIdSubSystemFiles0130);
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") +
                                " is not as per Rule-10 and not found in file " + accountUniqueIdSubSystemFiles0130);
                    }
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Account_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is as per Rule-18");

                } else {
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-18");
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-18");
                }

                //-------------------------------------------Account_Number------------------------------------------------
                //Rule:- 2 - It can have every character including french, It is free text field
                // Rule:- 10 - Values present the file should be present in other file, Which primary key
                //                for other file where as foreign key for current file verification
                //Rule:- 18 - It should not be blank
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Account_Number"))) {
                    test.pass("**********Pass Statement********* Account_Number:- " + dqRecord.get("Account_Number") + " is as per Rule-2" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Account_Number:- " + dqRecord.get("Account_Number") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Account_Number:- " + dqRecord.get("Account_Number") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (accountNumberTable0130 == null) {
                    accountNumberTable0130 = new DQRules().getListOfString(mi, "0130", "Account_Number", fileName.substring(26, 29), "DQ");
                    accountNumberSubSystemFiles0130 = new DQRules().getDQFileNames("0130").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (accountNumberSubSystemFiles0130.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0130:- " + mi + "*0130" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (accountNumberSubSystemFiles0130.size() != 0) {
                    boolean accountNumberFlag0130 = accountNumberTable0130.contains(dqRecord.get("Account_Number"));
                    if (accountNumberFlag0130) {
                        test.pass("**********Pass Statement********* Account_Number:- " + dqRecord.get("Account_Number") + " is as per Rule-10" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Account_Number:- " + dqRecord.get("Account_Number") +
                                " is not as per Rule-10 and not found in file " + accountNumberSubSystemFiles0130 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Account_Number:- " + dqRecord.get("Account_Number") +
                                " is not as per Rule-10 and not found in file " + accountNumberSubSystemFiles0130 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    }

                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Account_Number"))) {
                    test.pass("**********Pass Statement********* Account_Number:- " + dqRecord.get("Account_Number") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Account_Number:- " + dqRecord.get("Account_Number") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Account_Number:- " + dqRecord.get("Account_Number") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------Name------------------------------------------------
                // Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Name"))) {
                    test.pass("**********Pass Statement********* Name:- " + dqRecord.get("Name") + " is as per Rule-2" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Name:- " + dqRecord.get("Name") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Name:- " + dqRecord.get("Name") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //March Release -  Rule-18 - As per user story CDICPHSTWO-200 Rule:-18 is not required to check
                //August Release - Rule-18 - As per user story CDICPHSTWO-939 Rule:-18 is required to check
                // Rule-18
                if (rules.noBlankRule18(dqRecord.get("Name"))) {
                    test.pass("**********Pass Statement********* Name:- " + dqRecord.get("Name") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Name:- " + dqRecord.get("Name") + "is not as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Name:- " + dqRecord.get("Name") + "is not as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------First Name------------------------------------------------
//            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("First_Name"))) {
                    test.pass("**********Pass Statement********* First_Name:- " + dqRecord.get("First_Name") + " is as per Rule-2" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> First_Name:- " + dqRecord.get("First_Name") +
                            "is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> First_Name:- " + dqRecord.get("First_Name") +
                            "is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------Middle_Name------------------------------------------------
//            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Middle_Name"))) {
                    test.pass("**********Pass Statement********* Middle_Name:- " + dqRecord.get("Middle_Name") + " is as per Rule-2" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Middle_Name:- " + dqRecord.get("Middle_Name") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Middle_Name:- " + dqRecord.get("Middle_Name") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------Last_Name------------------------------------------------
//            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Last_Name"))) {
                    test.pass("**********Pass Statement********* Last_Name:- " + dqRecord.get("Last_Name") + " is as per Rule-2" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Last_Name:- " + dqRecord.get("Last_Name") +
                            "is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Last_Name:- " + dqRecord.get("Last_Name") +
                            "is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }
                //-------------------------------------------Address_1------------------------------------------------
//            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Address_1"))) {
                    test.pass("**********Pass Statement********* Address_1:- " + dqRecord.get("Address_1") + " is as per Rule-2" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Address_1:- " + dqRecord.get("Address_1") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Address_1:- " + dqRecord.get("Address_1") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                // Rule-18
                //March Release -  Rule-18 - As per user story CDICPHSTWO-200 Rule:-18 is not required to check
                //August Release - Rule-18 - As per user story CDICPHSTWO-939 Rule:-18 is required to check
                if (rules.noBlankRule18(dqRecord.get("Address_1"))) {
                    test.pass("**********Pass Statement********* Address_1:- " + dqRecord.get("Address_1") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Address_1:- " + dqRecord.get("Address_1") + "is not as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Address_1:- " + dqRecord.get("Address_1") + "is not as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------Address_2------------------------------------------------
//            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Address_2"))) {
                    test.pass("**********Pass Statement********* Address_2:- " + dqRecord.get("Address_2") + " is as per Rule-2" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Address_2:- " + dqRecord.get("Address_2") +
                            "is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Address_2:- " + dqRecord.get("Address_2") +
                            "is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //-------------------------------------------City------------------------------------------------
//            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("City"))) {
                    test.pass("**********Pass Statement********* City:- " + dqRecord.get("City") + " is as per Rule-2" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> City:- " + dqRecord.get("City") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> City:- " + dqRecord.get("City") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //Rule:- 18 - It should not be blank
                // March Release - Rule-18 - As per user story CDICPHSTWO-200 Rule:-18 is not required to check
                //August Release - Rule-18 - As per user story CDICPHSTWO-939 Rule:-18 is required to check
                if (dqRecord.get("Country").equalsIgnoreCase("CANADA") || dqRecord.get("Country").equalsIgnoreCase("USA")) {
                    if (rules.noBlankRule18(dqRecord.get("City"))) {
                        test.pass("**********Pass Statement********* City:- " + dqRecord.get("City") + " is as per Rule-18" +
                                " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> City:- " + dqRecord.get("City") +
                                " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> City:- " + dqRecord.get("City") +
                                " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    }
                }


                //-------------------------------------------Province------------------------------------------------
//            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Province"))) {
                    test.pass("**********Pass Statement********* Province:- " + dqRecord.get("Province") + " is as per Rule-2" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Province:- " + dqRecord.get("Province") +
                            "is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Province:- " + dqRecord.get("Province") +
                            "is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                // Rule-18 -
                // March Release - Rule-18 - As per user story CDICPHSTWO-200 Rule:-18 is not required to check
                //August Release - Rule-18 - As per user story CDICPHSTWO-939 Rule:-18 is required to check
                if (dqRecord.get("Country").equalsIgnoreCase("CANADA") || dqRecord.get("Country").equalsIgnoreCase("USA")) {
                    if (rules.noBlankRule18(dqRecord.get("Province"))) {
                        test.pass("**********Pass Statement********* Province:- " + dqRecord.get("Province") + " is as per Rule-18" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Province:- " + dqRecord.get("Province") + "is not as per Rule-18" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Province:- " + dqRecord.get("Province") + "is not as per Rule-18" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    }
                }

              /*  if (dqRecord.get("Country").equalsIgnoreCase("CANADA") || dqRecord.get("Country").equalsIgnoreCase("USA")) {
                    if(!(dqRecord.get("Province").equals(""))){
                        if (rules.provinceRule28(dqRecord.get("Province"))) {
                            test.pass("**********Pass Statement********* Province:- " + dqRecord.get("Province") + " is as per Rule-28" +
                                    " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                        } else {
                            test.fail("--- FAIL ---> Province:- " + dqRecord.get("Province") + " is not as per Rule-28" +
                                    " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                            System.err.println("--- FAIL ---> Province:- " + dqRecord.get("Province") + " is not as per Rule-28" +
                                    " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                        }
                    }
                }*/

                //-------------------------------------------Postal_Code------------------------------------------------
//            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Postal_Code"))) {
                    test.pass("**********Pass Statement********* Postal_Code:- " + dqRecord.get("Postal_Code") + " is as per Rule-2" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Postal_Code:- " + dqRecord.get("Postal_Code") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Postal_Code:- " + dqRecord.get("Postal_Code") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                // Rule-18
                // March Release - Rule-18 - As per user story CDICPHSTWO-200 Rule:-18 is not required to check
                //August Release - Rule-18 - As per user story CDICPHSTWO-939 Rule:-18 is required to check
                if (dqRecord.get("Country").equalsIgnoreCase("CANADA") || dqRecord.get("Country").equalsIgnoreCase("USA")) {
                    if (rules.noBlankRule18(dqRecord.get("Postal_Code"))) {
                        test.pass("**********Pass Statement********* Postal_Code:- " + dqRecord.get("Postal_Code") + " is as per Rule-18" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Postal_Code:- " + dqRecord.get("Postal_Code") + "is not as per Rule-18" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Postal_Code:- " + dqRecord.get("Postal_Code") + "is not as per Rule-18" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    }
                }


                //-------------------------------------------Country------------------------------------------------
//            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Country"))) {
                    test.pass("**********Pass Statement********* Country:- " + dqRecord.get("Country") + " is as per Rule-2" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Country:- " + dqRecord.get("Country") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Country:- " + dqRecord.get("Country") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                // Rule-18
                // March Release - Rule-18 - As per user story CDICPHSTWO-200 Rule:-18 is not required to check
                //August Release - Rule-18 - As per user story CDICPHSTWO-939 Rule:-18 is required to check
                if (rules.noBlankRule18(dqRecord.get("Country"))) {
                    test.pass("**********Pass Statement********* Country:- " + dqRecord.get("Country") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Country:- " + dqRecord.get("Country") + "is not as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Country:- " + dqRecord.get("Country") + "is not as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                // Rule-27 - Filed 'Country' must be as per Canada Post addressing guidelines/ International destination listing in table 120
                if (rules.countryNamesRule27(dqRecord.get("Country")) || dqRecord.get("Country").isEmpty()) {
                    test.pass("**********Pass Statement********* Country:- " + dqRecord.get("Country") + " is as per Rule-27" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Country:- " + dqRecord.get("Country") +
                            "is not as per Rule-27 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Country:- " + dqRecord.get("Country") +
                            "is not as per Rule-27 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------SIA_Individual_Flag------------------------------------------------
//              Rule:- 3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("SIA_Individual_Flag"), 1)) {
                    test.pass("**********Pass Statement********* SIA_Individual_Flag:- " + dqRecord.get("SIA_Individual_Flag") + " is as per Rule-3" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> SIA_Individual_Flag:- " + dqRecord.get("SIA_Individual_Flag") +
                            "is not as per Rule-3 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> SIA_Individual_Flag:- " + dqRecord.get("SIA_Individual_Flag") +
                            "is not as per Rule-3 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

//                Rule:- 21
                if (rules.acceptsOnlySpecificCharactersRule21(dqRecord.get("SIA_Individual_Flag"), "Y|N|^$")) {
                    test.pass("**********Pass Statement********* SIA_Individual_Flag:- " + dqRecord.get("SIA_Individual_Flag") + " is as per Rule-21" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> SIA_Individual_Flag:- " + dqRecord.get("SIA_Individual_Flag") +
                            " is not as per Rule-21 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> SIA_Individual_Flag:- " + dqRecord.get("SIA_Individual_Flag") +
                            " is not as per Rule-21 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

//-------------------------------------------Interest_In_Deposit_Flag------------------------------------------------
//               Rule-3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("Interest_In_Deposit_Flag"), 1)) {
                    test.pass("**********Pass Statement********* Interest_In_Deposit_Flag:- " + dqRecord.get("Interest_In_Deposit_Flag") + " is as per Rule-3" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Interest_In_Deposit_Flag:- " + dqRecord.get("Interest_In_Deposit_Flag") +
                            " is not as per Rule-3 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Interest_In_Deposit_Flag:- " + dqRecord.get("Interest_In_Deposit_Flag") +
                            " is not as per Rule-3 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

//                Rule:- 24
                if (rules.acceptsOnlySpecificCharactersRule24(dqRecord.get("Interest_In_Deposit_Flag"), "A|P|^$")) {
                    test.pass("**********Pass Statement********* Interest_In_Deposit_Flag:- " + dqRecord.get("Interest_In_Deposit_Flag") + " is as per Rule-24" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Interest_In_Deposit_Flag:- " + dqRecord.get("Interest_In_Deposit_Flag") +
                            " is not as per Rule-24 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Interest_In_Deposit_Flag:- " + dqRecord.get("Interest_In_Deposit_Flag") +
                            " is not as per Rule-24 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------Interest_In_Deposit------------------------------------------------
                //            Rule-6
                if (rules.amountValidationRule6(dqRecord.get("Interest_In_Deposit"), "30", "6")) {
                    test.pass("**********Pass Statement********* Interest_In_Deposit:- " + dqRecord.get("Interest_In_Deposit") + " is as per Rule-6" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Interest_In_Deposit:- " + dqRecord.get("Interest_In_Deposit") +
                            " is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Interest_In_Deposit:- " + dqRecord.get("Interest_In_Deposit") +
                            " is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }
                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    @Then("^validate (\\d+)-Personal Identification$")
    public void validatePersonalIdentification(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;

        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            List<String> depositorUniqueIdTable0100 = null;
            List<String> subSystemFiles0100 = null;
            List<String> personalIdTypeCodeTable0211 = null;
            List<String> subSystemFiles0211 = null;
            DQRules rules = new DQRules();
            for (Map<String, String> dqRecord : dqRecords) {
// ----------------------------- Depositor_Unique_ID-----------------------------

                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Depositor_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") + " is as per Rule-2");

                } else {
                    test.fail(
                            "--- FAIL ---> Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") + "is not as per Rule-2");
                    System.err.println("--- FAIL ---> Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") + "is not as per Rule-2");
                }

                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (depositorUniqueIdTable0100 == null) {
                    depositorUniqueIdTable0100 = new DQRules().getListOfString(mi, "0100", "Depositor_Unique_ID", fileName.substring(26, 29), "DQ");
                    subSystemFiles0100 = new DQRules().getDQFileNames("0100").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).collect(Collectors.toList());
                    if (subSystemFiles0100.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0100:- " + mi + "*0100" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0100.size() != 0) {
                    boolean depositorUniqueIdFlag = depositorUniqueIdTable0100.contains(dqRecord.get("Depositor_Unique_ID"));
                    if (depositorUniqueIdFlag || dqRecord.get("Depositor_Unique_ID").isEmpty()) {
                        test.pass("**********Pass Statement********* Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") +
                                " is as per Rule-10");

                    } else {
                        test.fail(
                                "--- FAIL ---> Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") +
                                        " is not as per Rule-10 and not found in file " + subSystemFiles0100);
                        System.err.println("--- FAIL ---> Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0100);
                    }
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Depositor_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + "is as per Rule-18");

                } else {
                    test.fail(
                            "--- FAIL ---> Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") + "is not as per Rule-18");
                    System.err.println("--- FAIL ---> Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") + "is not as per Rule-18");
                }


                // ----------------------------- Personal_ID_Count-----------------------------
//                Rule:- 1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Personal_ID_Count"))
                        || dqRecord.get("Personal_ID_Count").isEmpty()) {
                    test.pass("**********Pass Statement********* Personal_ID_Count:- " + dqRecord.get("Personal_ID_Count") + " is as per Rule-1" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Personal_ID_Count:- " + dqRecord.get("Personal_ID_Count") +
                            "is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Personal_ID_Count:- " + dqRecord.get("Personal_ID_Count") +
                            "is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }
                // ----------------------------- Identification_Number-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Identification_Number"))) {
                    test.pass("**********Pass Statement********* Identification_Number:- " + dqRecord.get("Identification_Number") + " is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Identification_Number:- " + dqRecord.get("Identification_Number") + "is not as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Identification_Number:- " + dqRecord.get("Identification_Number") + "is not as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Identification_Number"))) {
                    test.pass("**********Pass Statement********* Identification_Number:- " + dqRecord.get("Identification_Number") + " is as per Rule-18" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Identification_Number:- " + dqRecord.get("Identification_Number") + "is not as per Rule-18");
                    System.err.println("--- FAIL ---> Identification_Number:- " + dqRecord.get("Identification_Number") + "is not as per Rule-18");
                }

                // ----------------------------- Personal_ID_Type_Code-----------------------------
//                Rule:- 1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Personal_ID_Type_Code"))) {
                    test.pass("**********Pass Statement********* Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code") + " is as per Rule-1" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code") +
                            "is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code") +
                            "is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (personalIdTypeCodeTable0211 == null) {
                    personalIdTypeCodeTable0211 = new DQRules().getListOfString(mi, "0211", "Personal_ID_Type_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0211 = new DQRules().getDQFileNames("0211").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).collect(Collectors.toList());
                    if (subSystemFiles0211.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0211:- " + mi + "*0211" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0211.size() != 0) {
                    boolean personalIdTypeCodeFlag = personalIdTypeCodeTable0211.contains(dqRecord.get("Personal_ID_Type_Code"));
                    if (personalIdTypeCodeFlag || dqRecord.get("Personal_ID_Type_Code").isEmpty()) {
                        test.pass("**********Pass Statement********* Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code") + " is as per Rule-10" +
                                " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0211 + " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                        System.err.println("--- FAIL ---> Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0211 + " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    }
                }


                // Rule-18
                if (rules.noBlankRule18(dqRecord.get("Personal_ID_Type_Code"))) {
                    test.pass("**********Pass Statement********* Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code") + " is as per Rule-18" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code") + "is not as per Rule-18" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code") + "is not as per Rule-18" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    @Then("^validate (\\d+)-Trust Account Type$")
    public void validateTrustAccountType(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        List<String> fileValues = new DQRules().getDQFileNames(tableId);
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> trustAccountTypeCodeTableRule90237 = null;
            List<String> trustAccountTypeCodeDuplicates = null;
            for (Map<String, String> dqRecord : dqRecords) {
                //-------------------------------------------Trust_Account_Type_Code------------------------------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Trust_Account_Type_Code"))) {
                    test.pass("**********Pass Statement********* Trust_Account_Type_Code:- " + dqRecord.get("Trust_Account_Type_Code") + " is as per Rule-1");
                } else {
                    test.fail("--- FAIL ---> Trust_Account_Type_Code:- " + dqRecord.get("Trust_Account_Type_Code") +
                            "is not as per Rule-1");
                }


                //            Rule-9
                // Rule:- 9 New code
                if (trustAccountTypeCodeTableRule90237 == null) {
                    trustAccountTypeCodeTableRule90237 = new DQRules().getListOfString(mi, "0237", "Trust_Account_Type_Code", fileName.substring(26, 29), "DQ");
                    trustAccountTypeCodeDuplicates =
                            trustAccountTypeCodeTableRule90237.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(trustAccountTypeCodeDuplicates.contains(dqRecord.get("Trust_Account_Type_Code")))
                        || dqRecord.get("Trust_Account_Type_Code").isEmpty()) {
                    test.pass("**********Pass Statement********* Trust_Account_Type_Code:- " + dqRecord.get("Trust_Account_Type_Code") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Trust_Account_Type_Code:- " + dqRecord.get("Trust_Account_Type_Code") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> Trust_Account_Type_Code:- " + dqRecord.get("Trust_Account_Type_Code") + " is not as per Rule-9");
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Trust_Account_Type_Code"))) {
                    test.pass("**********Pass Statement********* Trust_Account_Type_Code:- " + dqRecord.get("Trust_Account_Type_Code") + " is as per Rule-18");
                } else {
                    test.fail(
                            "--- FAIL ---> Trust_Account_Type_Code:- " + dqRecord.get("Trust_Account_Type_Code") +
                                    " is not as per Rule-18");
                }

                //-------------------------------------------Description------------------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-2" +
                            " for Trust_Account_Type_Code:- " + dqRecord.get("Trust_Account_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-18" +
                            " for Trust_Account_Type_Code:- " + dqRecord.get("Trust_Account_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code"));
                }
                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));

    }

    @Then("^validate (\\d+)-Currency Code$")
    public void validateCurrencyCode(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> currencyCodeTableRule90233 = null;
            List<String> currencyCodeDuplicates = null;
            for (Map<String, String> dqRecord : dqRecords) {
//-------------------------------------------Currency_Code------------------------------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Currency_Code"))) {
                    test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") + " is as per Rule-1");
                } else {
                    test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            "is not as per Rule-1");
                    System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            "is not as per Rule-1");
                }

                //            Rule-9
                if (currencyCodeTableRule90233 == null) {
                    currencyCodeTableRule90233 = new DQRules().getListOfString(mi, "0233", "Currency_Code", fileName.substring(26, 29), "DQ");
                    currencyCodeDuplicates =
                            currencyCodeTableRule90233.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(currencyCodeDuplicates.contains(dqRecord.get("Currency_Code")))) {
                    test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") + "is not as per Rule-9");
                    System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") + "is not as per Rule-9");
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Currency_Code"))) {
                    test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") + " is as per Rule-18");
                } else {
                    test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            "is not as per Rule-18");
                    System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            "is not as per Rule-18");
                }

                //-------------------------------------------MI_Currency_Code------------------------------------------------
                if (rules.acceptsAllChactersRule2(dqRecord.get("MI_Currency_Code"))) {
                    test.pass("**********Pass Statement********* MI_Currency_Code:- " + dqRecord.get("MI_Currency_Code") + " is as per Rule-2" +
                            " for Currency_Code:- " + dqRecord.get("Currency_Code"));
                } else {
                    test.fail("--- FAIL ---> MI_Currency_Code:- " + dqRecord.get("MI_Currency_Code") +
                            " is not as per Rule-2 for Currency_Code:- " + dqRecord.get("Currency_Code"));
                    System.err.println("--- FAIL ---> MI_Currency_Code:- " + dqRecord.get("MI_Currency_Code") +
                            " is not as per Rule-2 for Currency_Code:- " + dqRecord.get("Currency_Code"));
                }

//-------------------------------------------ISO_Currency_Code------------------------------------------------

                //Rule:- 3 - It should be character length of character is 3
                //            Rule-3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("ISO_Currency_Code"), 3)) {
                    test.pass("**********Pass Statement********* ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") + " is as per Rule-3" +
                            " for Currency_Code:- " + dqRecord.get("Currency_Code"));
                } else {
                    test.fail("--- FAIL ---> ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") +
                            " is not as per Rule-3 for Currency_Code:- " + dqRecord.get("Currency_Code"));
                    System.err.println("--- FAIL ---> ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") +
                            " is not as per Rule-3 for Currency_Code:- " + dqRecord.get("Currency_Code"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("ISO_Currency_Code"))) {
                    test.pass("**********Pass Statement********* ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") + " is as per Rule-18" +
                            " for Currency_Code:- " + dqRecord.get("Currency_Code"));
                } else {
                    test.fail("--- FAIL ---> ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") +
                            " is not as per Rule-18 for Currency_Code:- " + dqRecord.get("Currency_Code"));
                    System.err.println("--- FAIL ---> ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") +
                            " is not as per Rule-18 for Currency_Code:- " + dqRecord.get("Currency_Code"));
                }

                //Rule-26
                if (rules.isoCountryRule26(dqRecord.get("ISO_Currency_Code"))) {
                    test.pass("**********Pass Statement********* ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") + " is as per Rule-26" +
                            " for Currency_Code:- " + dqRecord.get("Currency_Code"));
                } else {
                    test.fail("--- FAIL ---> ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") +
                            " is not as per Rule-26 for Currency_Code:- " + dqRecord.get("Currency_Code"));
                    System.err.println("--- FAIL ---> ISO_Currency_Code:- " + dqRecord.get("ISO_Currency_Code") +
                            " is not as per Rule-26 for Currency_Code:- " + dqRecord.get("Currency_Code"));
                }

                //-------------------------------------------Description------------------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-2" +
                            " for Currency_Code:- " + dqRecord.get("Currency_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Currency_Code:- " + dqRecord.get("Currency_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Currency_Code:- " + dqRecord.get("Currency_Code"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-18" +
                            " for Currency_Code:- " + dqRecord.get("Currency_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Currency_Code:- " + dqRecord.get("Currency_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Currency_Code:- " + dqRecord.get("Currency_Code"));
                }


                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));

    }

    @Then("^validate (\\d+)-Address Type$")
    public void validateAddressType(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        String headers = null;
        List<String> results = new LinkedList<>();

        List<String> addressTypeCodeTableRule90221 = null;
        List<String> addressTypeCodeDuplicates = null;
        List<String> fileValues = new DQRules().getDQFileNames(tableId);
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            for (Map<String, String> dqRecord : dqRecords) {

//-------------------------------------------Address_Type_Code------------------------------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Address_Type_Code"))) {
                    test.pass("**********Pass Statement********* Address_Type_Code:- " + dqRecord.get("Address_Type_Code") + "is as per Rule-1");
                } else {
                    test.fail("--- FAIL ---> Address_Type_Code:- " + dqRecord.get("Address_Type_Code") +
                            "is not as per Rule-1");
                    System.err.println("--- FAIL ---> Address_Type_Code:- " + dqRecord.get("Address_Type_Code") +
                            "is not as per Rule-1");
                }


                //            Rule-9
                if (addressTypeCodeTableRule90221 == null) {
                    addressTypeCodeTableRule90221 = new DQRules().getListOfString(mi, "0221", "Address_Type_Code", fileName.substring(26, 29), "DQ");
                    addressTypeCodeDuplicates =
                            addressTypeCodeTableRule90221.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(addressTypeCodeDuplicates.contains(dqRecord.get("Address_Type_Code")))) {
                    test.pass("**********Pass Statement********* Address_Type_Code:- " + dqRecord.get("Address_Type_Code") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Address_Type_Code:- " + dqRecord.get("Address_Type_Code") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> Address_Type_Code:- " + dqRecord.get("Address_Type_Code") + " is not as per Rule-9");
                }


                //Rule:- 18 - It should not be blank
                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Address_Type_Code"))) {
                    test.pass("**********Pass Statement********* Address_Type_Code:- " + dqRecord.get("Address_Type_Code") + "is as per Rule-18");
                } else {
                    test.fail("--- FAIL ---> Address_Type_Code:- " + dqRecord.get("Address_Type_Code") +
                            " is not as per Rule-18");
                    System.err.println("--- FAIL ---> Address_Type_Code:- " + dqRecord.get("Address_Type_Code") +
                            " is not as per Rule-18");
                }

                //-------------------------------------------MI_Address_Type------------------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("MI_Address_Type"))) {
                    test.pass("**********Pass Statement********* MI_Address_Type:- " + dqRecord.get("MI_Address_Type") + " is as per Rule-2" +
                            " for Address_Type_Code:- " + dqRecord.get("Address_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> MI_Address_Type:- " + dqRecord.get("MI_Address_Type") +
                            " is not as per Rule-2 for Address_Type_Code:- " + dqRecord.get("Address_Type_Code"));
                    System.err.println("--- FAIL ---> MI_Address_Type:- " + dqRecord.get("MI_Address_Type") +
                            " is not as per Rule-2 for Address_Type_Code:- " + dqRecord.get("Address_Type_Code"));
                }

                //-------------------------------------------Description------------------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-2" +
                            " for Address_Type_Code:- " + dqRecord.get("Address_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Address_Type_Code:- " + dqRecord.get("Address_Type_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Address_Type_Code:- " + dqRecord.get("Address_Type_Code"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-18" +
                            " for Address_Type_Code:- " + dqRecord.get("Address_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Address_Type_Code:- " + dqRecord.get("Address_Type_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Address_Type_Code:- " + dqRecord.get("Address_Type_Code"));
                }

                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));

    }


    @Then("^validate (\\d+)-Beneficiary Data - Nominee Broker$")
    public void validateBeneficiaryDataNomineeBroker(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> accountUniqueIdTable0130 = null;
            List<String> subSystemFilesAccountUniqueId0130 = null;
            List<String> accountNumberTable0130 = null;
            List<String> subSystemFilesAccountNumber0130 = null;

            for (Map<String, String> dqRecord : dqRecords) {
                //-------------------------------------------Account_Unique_ID------------------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Account_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is as per Rule-2");

                } else {
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-2");
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-2");
                }


                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (accountUniqueIdTable0130 == null) {
                    accountUniqueIdTable0130 = new DQRules().getListOfString(mi, "0130", "Account_Unique_ID", fileName.substring(26, 29), "DQ");
                    subSystemFilesAccountUniqueId0130 = new DQRules().getDQFileNames("0130").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFilesAccountUniqueId0130.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0130:- " + mi + "*0130" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFilesAccountUniqueId0130.size() != 0) {
                    boolean accountUniqueIdFlag130 = accountUniqueIdTable0130.contains(dqRecord.get("Account_Unique_ID"));
                    if (accountUniqueIdFlag130) {
                        test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") +
                                " is as per Rule-10");

                    } else {
                        test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") +
                                " is not as per Rule-10 and not found in file " + subSystemFilesAccountUniqueId0130);
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") +
                                " is not as per Rule-10 and not found in file " + subSystemFilesAccountUniqueId0130);
                    }
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Account_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is as per Rule-18");

                } else {
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") +
                            " is not as per Rule-18");
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") +
                            " is not as per Rule-18");
                }


//-------------------------------------------Account_Number------------------------------------------------
//Rule:- 2 - It can have every character including french, It is free text field
//Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
//Rule:- 18 - It should not be blank
//            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Account_Number"))) {
                    test.pass("**********Pass Statement********* Account_Number:- " + dqRecord.get("Account_Number") + " is as per Rule-2" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Account_Number:- " + dqRecord.get("Account_Number") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Account_Number:- " + dqRecord.get("Account_Number") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (accountNumberTable0130 == null) {
                    accountNumberTable0130 = new DQRules().getListOfString(mi, "0130", "Account_Number", fileName.substring(26, 29), "DQ");
                    subSystemFilesAccountNumber0130 = new DQRules().getDQFileNames("0130").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFilesAccountNumber0130.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0130:- " + mi + "*0130" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }

                if (subSystemFilesAccountNumber0130.size() != 0) {
                    boolean accountNumberFlag130 = accountNumberTable0130.contains(dqRecord.get("Account_Unique_ID"));
                    if (accountNumberFlag130) {
                        test.pass("**********Pass Statement********* Account_Number:- " + dqRecord.get("Account_Number") + " is as per Rule-10" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Account_Number:- " + dqRecord.get("Account_Number") +
                                " is not as per Rule-10 and not found in file " + subSystemFilesAccountNumber0130 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Account_Number:- " + dqRecord.get("Account_Number") +
                                " is not as per Rule-10 and not found in file " + subSystemFilesAccountNumber0130 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    }
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Account_Number"))) {
                    test.pass("**********Pass Statement********* Account_Number:- " + dqRecord.get("Account_Number") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Account_Number:- " + dqRecord.get("Account_Number") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Account_Number:- " + dqRecord.get("Account_Number") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


//-------------------------------------------Beneficiary_ID------------------------------------------------
                //Rule:- 2 - It can have every character including french, It is free text field
//Rule:- 18 - It should not be blank
//            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Beneficiary_ID"))) {
                    test.pass("**********Pass Statement********* Beneficiary_ID:- " + dqRecord.get("Beneficiary_ID") + " is as per Rule-2" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Beneficiary_ID:- " + dqRecord.get("Beneficiary_ID") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Beneficiary_ID:- " + dqRecord.get("Beneficiary_ID") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Beneficiary_ID"))) {
                    test.pass("**********Pass Statement********* Beneficiary_ID:- " + dqRecord.get("Beneficiary_ID") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Beneficiary_ID:- " + dqRecord.get("Beneficiary_ID") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Beneficiary_ID:- " + dqRecord.get("Beneficiary_ID") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------SIA_Individual_Flag------------------------------------------------
//              Rule:- 3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("SIA_Individual_Flag"), 1)
                        || dqRecord.get("SIA_Individual_Flag").isEmpty()) {
                    test.pass("**********Pass Statement********* SIA_Individual_Flag:- " + dqRecord.get("SIA_Individual_Flag") + " is as per Rule-3" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> SIA_Individual_Flag:- " + dqRecord.get("SIA_Individual_Flag") +
                            "is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> SIA_Individual_Flag:- " + dqRecord.get("SIA_Individual_Flag") +
                            "is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

//                Rule:- 21
                if (rules.acceptsOnlySpecificCharactersRule21(dqRecord.get("SIA_Individual_Flag"), "Y|N|^$")) {
                    test.pass("**********Pass Statement********* SIA_Individual_Flag:- " + dqRecord.get("SIA_Individual_Flag") + " is as per Rule-21" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> SIA_Individual_Flag:- " + dqRecord.get("SIA_Individual_Flag") +
                            " is not as per Rule-21 for Depositor_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> SIA_Individual_Flag:- " + dqRecord.get("SIA_Individual_Flag") +
                            " is not as per Rule-21 for Depositor_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------Interest_In_Deposit_Flag------------------------------------------------
//               Rule-3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("Interest_In_Deposit_Flag"), 1)) {
                    test.pass("**********Pass Statement********* Interest_In_Deposit_Flag:- " + dqRecord.get("Interest_In_Deposit_Flag") + " is as per Rule-3" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Interest_In_Deposit_Flag:- " + dqRecord.get("Interest_In_Deposit_Flag") +
                            " is not as per Rule-3 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Interest_In_Deposit_Flag:- " + dqRecord.get("Interest_In_Deposit_Flag") +
                            " is not as per Rule-3 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

//                Rule:- 24
                if (rules.acceptsOnlySpecificCharactersRule24(dqRecord.get("Interest_In_Deposit_Flag"), "A|P|^$")) {
                    test.pass("**********Pass Statement********* Interest_In_Deposit_Flag:- " + dqRecord.get("Interest_In_Deposit_Flag") + " is as per Rule-24" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Interest_In_Deposit_Flag:- " + dqRecord.get("Interest_In_Deposit_Flag") +
                            "is not as per Rule-24 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Interest_In_Deposit_Flag:- " + dqRecord.get("Interest_In_Deposit_Flag") +
                            "is not as per Rule-24 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------Interest_In_Deposit------------------------------------------------
                //            Rule-6
                if (rules.amountValidationRule6(dqRecord.get("Interest_In_Deposit"), "30", "6")) {
                    test.pass("**********Pass Statement********* Interest_In_Deposit:- " + dqRecord.get("Interest_In_Deposit") + " is as per Rule-6" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Interest_In_Deposit:- " + dqRecord.get("Interest_In_Deposit") +
                            " is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Interest_In_Deposit:- " + dqRecord.get("Interest_In_Deposit") +
                            " is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------IB_LEI------------------------------------------------
//              Rule:-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("IB_LEI"))) {
                    test.pass("**********Pass Statement********* IB_LEI:- " + dqRecord.get("IB_LEI") + " is as per Rule-2" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> IB_LEI:- " + dqRecord.get("IB_LEI") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> IB_LEI:- " + dqRecord.get("IB_LEI") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }

            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));

    }

    @Then("^validate (\\d+)-Personal Identification Type$")
    public void validatePersonalIdentificationType(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> cdicPersonalIdTypeCodeTable0212 = null;
            List<String> subSystemFilesPersonalIdType0212 = null;
            List<String> personalIdTypeCodeTableRule90211 = null;
            List<String> personalIdTypeCodeDuplicates = null;
            for (Map<String, String> dqRecord : dqRecords) {
                // ----------------------------- Personal_ID_Type_Code-----------------------------
//                Rule:- 1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Personal_ID_Type_Code"))) {
                    test.pass("**********Pass Statement********* Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code") + "is as per Rule-1");
                } else {
                    test.fail("--- FAIL ---> Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code") +
                            "is not as per Rule-1");
                    System.err.println("--- FAIL ---> Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code") +
                            "is not as per Rule-1");
                }

                //            Rule-9
                if (personalIdTypeCodeTableRule90211 == null) {
                    personalIdTypeCodeTableRule90211 = new DQRules().getListOfString(mi, "0211", "Personal_ID_Type_Code", fileName.substring(26, 29), "DQ");
                    personalIdTypeCodeDuplicates =
                            personalIdTypeCodeTableRule90211.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(personalIdTypeCodeDuplicates.contains(dqRecord.get("Personal_ID_Type_Code")))) {
                    test.pass("**********Pass Statement********* Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code") + " is not as per Rule-9");
                }

                // Rule-18
                if (rules.noBlankRule18(dqRecord.get("Personal_ID_Type_Code"))) {
                    test.pass("**********Pass Statement********* Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code") + "is as per Rule-18");
                } else {
                    test.fail("--- FAIL ---> Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code") + "is not as per Rule-18");
                    System.err.println("--- FAIL ---> Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code") + "is not as per Rule-18");
                }
                // ----------------------------- MI_Personal_ID_Type-----------------------------
                //              Rule:-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("MI_Personal_ID_Type"))) {
                    test.pass("**********Pass Statement********* MI_Personal_ID_Type:- " + dqRecord.get("MI_Personal_ID_Type") + "is as per Rule-2" +
                            " for Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> MI_Personal_ID_Type:- " + dqRecord.get("MI_Personal_ID_Type") +
                            "is not as per Rule-2 for Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code"));
                    System.err.println("--- FAIL ---> MI_Personal_ID_Type:- " + dqRecord.get("MI_Personal_ID_Type") +
                            "is not as per Rule-2 for Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code"));
                }

                // ----------------------------- Description-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + "is as per Rule-2" +
                            " for Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + "is as per Rule-18" +
                            " for Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Phone_Type_Code:- " + dqRecord.get("Phone_Type_Code"));
                }

// ----------------------------- CDIC_Personal_ID_Type_Code-----------------------------
//                Rule:- 1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("CDIC_Personal_ID_Type_Code"))) {
                    test.pass("**********Pass Statement********* CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code") + "is as per Rule-1" +
                            " for Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code") +
                            "is not as per Rule-1 for Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code"));
                    System.err.println("--- FAIL ---> CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code") +
                            "is not as per Rule-1 for Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code"));
                }


                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (cdicPersonalIdTypeCodeTable0212 == null) {
                    cdicPersonalIdTypeCodeTable0212 = new DQRules().getListOfString(mi, "0212", "CDIC_Personal_ID_Type_Code", fileName.substring(26, 29), "DQ");
                    subSystemFilesPersonalIdType0212 = new DQRules().getDQFileNames("0212").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).collect(Collectors.toList());
                    if (subSystemFilesPersonalIdType0212.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0212:- " + mi + "*0212" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFilesPersonalIdType0212.size() != 0) {
                    boolean cdicPersonalIdTypeFlag212 = cdicPersonalIdTypeCodeTable0212.contains(dqRecord.get("CDIC_Personal_ID_Type_Code"));
                    if (cdicPersonalIdTypeFlag212) {
                        test.pass("**********Pass Statement********* CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code") + "is as per Rule-10" +
                                " for Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code"));
                    } else {
                        test.fail("--- FAIL ---> CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFilesPersonalIdType0212 + " for Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code"));
                        System.err.println("--- FAIL ---> CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFilesPersonalIdType0212 + " for Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code"));
                    }
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("CDIC_Personal_ID_Type_Code"))) {
                    test.pass("**********Pass Statement********* CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code") + "is as per Rule-18" +
                            " for Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code") +
                            "is not as per Rule-18 for Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code"));
                    System.err.println("--- FAIL ---> CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code") +
                            "is not as per Rule-18 for Personal_ID_Type_Code:- " + dqRecord.get("Personal_ID_Type_Code"));
                }
                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));

    }


    @Then("^validate (\\d+)-External Account Data$")
    public void validateExternalAccountData(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;

        List<String> fileValues = new DQRules().getDQFileNames(tableId);
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            List<String> depositorUniqueIdTable0100 = null;
            List<String> subSystemFiles0100 = null;
            List<String> currencyCodeTable0233 = null;
            List<String> subSystemFiles0233 = null;
            DQRules rules = new DQRules();
            for (Map<String, String> dqRecord : dqRecords) {
                // ----------------------------- Depositor_Unique_ID-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Depositor_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") + "is as per Rule-2");

                } else {
                    test.fail("--- FAIL ---> Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + "is not as per Rule-2");
                    System.err.println("--- FAIL ---> Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + "is not as per Rule-2");
                }

                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (depositorUniqueIdTable0100 == null) {
                    depositorUniqueIdTable0100 = new DQRules().getListOfString(mi, "0100", "Depositor_Unique_ID", fileName.substring(26, 29), "DQ");
                    subSystemFiles0100 = new DQRules().getDQFileNames("0100").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).collect(Collectors.toList());
                    if (subSystemFiles0100.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0100:- " + mi + "*0100" + fileName.substring(22, 29) +
                                " is not found for Rule-304");
                    }
                }
                if (subSystemFiles0100.size() != 0) {
                    boolean depositorUniqueIdFlag = depositorUniqueIdTable0100.contains(dqRecord.get("Depositor_Unique_ID"));
                    if (depositorUniqueIdFlag || dqRecord.get("Depositor_Unique_ID").isEmpty()) {
                        test.pass("**********Pass Statement********* Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") +
                                " is as per Rule-10");

                    } else {
                        test.fail("--- FAIL ---> Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") +
                                " is not as per Rule-10 and not found in file " + fileValues.stream().filter(x -> x.substring(0, 4).equals(fileName.substring(26, 29))).collect(Collectors.toList()));
                        System.err.println("--- FAIL ---> Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") +
                                " is not as per Rule-10 and not found in file " + fileValues.stream().filter(x -> x.substring(0, 4).equals(fileName.substring(26, 29))).collect(Collectors.toList()));
                    }

                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Depositor_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + "is as per Rule-18");

                } else {
                    test.fail("--- FAIL ---> Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + "is not as per Rule-18");
                    System.err.println("--- FAIL ---> Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + "is not as per Rule-18");
                }


                // ----------------------------- Payee_Name-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Payee_Name"))) {
                    test.pass("**********Pass Statement********* Payee_Name:- " + dqRecord.get("Payee_Name") + " is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Payee_Name:- " + dqRecord.get("Depositor_Unique_ID") + "is not as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Payee_Name:- " + dqRecord.get("Depositor_Unique_ID") + "is not as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Payee_Name"))) {
                    test.pass("**********Pass Statement********* Payee_Name:- " + dqRecord.get("Payee_Name") + " is as per Rule-18" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Payee_Name- " + dqRecord.get("Payee_Name") + "is not as per Rule-18 " +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Payee_Name- " + dqRecord.get("Payee_Name") + "is not as per Rule-18 " +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                // ----------------------------- Institution_ Number-----------------------------
//            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Institution_Number"))) {
                    test.pass("**********Pass Statement********* Institution_Number:- " + dqRecord.get("Institution_Number") + " is as per Rule-1" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Institution_Number:- " + dqRecord.get("Institution_Number") +
                            "is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Institution_Number:- " + dqRecord.get("Institution_Number") +
                            "is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Institution_Number"))) {
                    test.pass("**********Pass Statement********* Institution_Number:- " + dqRecord.get("Institution_Number") + " is as per Rule-18" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Institution_Number- " + dqRecord.get("Institution_Number") + "is not as per Rule-18 " +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Institution_Number- " + dqRecord.get("Institution_Number") + "is not as per Rule-18 " +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                // ----------------------------- Transit_Number-----------------------------
//            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Transit_Number"))) {
                    test.pass("**********Pass Statement********* Transit_Number:- " + dqRecord.get("Transit_Number") + " is as per Rule-1" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Transit_Number:- " + dqRecord.get("Transit_Number") +
                            "is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Transit_Number:- " + dqRecord.get("Transit_Number") +
                            "is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Transit_Number"))) {
                    test.pass("**********Pass Statement********* Transit_Number:- " + dqRecord.get("Transit_Number") + " is as per Rule-18" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Transit_Number- " + dqRecord.get("Transit_Number") + " is not as per Rule-18 " +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Transit_Number- " + dqRecord.get("Transit_Number") + " is not as per Rule-18 " +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                // ----------------------------- Account_Number-----------------------------
//            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Account_Number"))) {
                    test.pass("**********Pass Statement********* Account_Number:- " + dqRecord.get("Account_Number") + " is as per Rule-1" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Account_Number:- " + dqRecord.get("Account_Number") +
                            " is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Account_Number:- " + dqRecord.get("Account_Number") +
                            " is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Account_Number"))) {
                    test.pass("**********Pass Statement********* Account_Number:- " + dqRecord.get("Account_Number") + " is as per Rule-18" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Account_Number- " + dqRecord.get("Account_Number") + "is not as per Rule-18 " +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Account_Number- " + dqRecord.get("Account_Number") + "is not as per Rule-18 " +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //-------------------------------------------Currency_Code------------------------------------------------
                //            Rule-1

                if (rules.onlyNumericCharactersRule1(dqRecord.get("Currency_Code"))) {
                    test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") + " is as per Rule-1" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            "is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            "is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }


                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (currencyCodeTable0233 == null) {
                    currencyCodeTable0233 = new DQRules().getListOfString(mi, "0233", "Currency_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0233 = new DQRules().getDQFileNames("0233").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> !(x.substring(26, 29).equals(fileName.substring(26, 29)))).collect(Collectors.toList());
                    if (subSystemFiles0233.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0233:- " + mi + "*0233" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0233.size() != 0) {
                    boolean currencyCodeFlag = currencyCodeTable0233.contains(dqRecord.get("Currency_Code"));
                    if (currencyCodeFlag) {
                        test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") + " is as per Rule-10" +
                                " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0233 + " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                        System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0233 + " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    }
                } else {
                    test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            " is not as per Rule-10 and not found in file " + subSystemFiles0233 + " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            " is not as per Rule-10 and not found in file " + subSystemFiles0233 + " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Currency_Code"))) {
                    test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") + " is as per Rule-18" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            " is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            " is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

//-------------------------------------------Joint_Account_Flag------------------------------------------------
                //   Rule-3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("Joint_Account_Flag"), 1)) {
                    test.pass("**********Pass Statement********* Joint_Account_Flag:- " + dqRecord.get("Joint_Account_Flag") + " is as per Rule-3" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Joint_Account_Flag:- " + dqRecord.get("Joint_Account_Flag") +
                            "is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Joint_Account_Flag:- " + dqRecord.get("Joint_Account_Flag") +
                            "is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }
//Rule:- 21
                if (rules.acceptsOnlySpecificCharactersRule21(dqRecord.get("Joint_Account_Flag"), "Y|N|^$")
                        || dqRecord.get("Joint_Account_Flag").isEmpty()) {
                    test.pass("**********Pass Statement********* Joint_Account_Flag:- " + dqRecord.get("Joint_Account_Flag") + " is as per Rule-21" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Joint_Account_Flag:- " + dqRecord.get("Joint_Account_Flag") +
                            "is not as per Rule-21 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Joint_Account_Flag:- " + dqRecord.get("Joint_Account_Flag") +
                            "is not as per Rule-21 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //-------------------------------------------Start_Date------------------------------------------------
//                Rule:- 4
                if (rules.birthDateValidationRule4(dqRecord.get("Start_Date"))) {
                    test.pass("**********Pass Statement********* Start_Date:- " + dqRecord.get("Start_Date") + " is as per Rule-4" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Start_Date:- " + dqRecord.get("Start_Date") +
                            "is not as per Rule-4 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Start_Date:- " + dqRecord.get("Start_Date") +
                            "is not as per Rule-4 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //-------------------------------------------Last_Funds_ Transfer------------------------------------------------
//                Rule:- 4
                if (rules.birthDateValidationRule4(dqRecord.get("Last_Funds_Transfer"))) {
                    test.pass("**********Pass Statement********* Last_Funds_Transfer:- " + dqRecord.get("Last_Funds_Transfer") + " is as per Rule-4" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Last_Funds_Transfer:- " + dqRecord.get("Last_Funds_Transfer") +
                            " is not as per Rule-4 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Last_Funds_Transfer:- " + dqRecord.get("Last_Funds_Transfer") +
                            " is not as per Rule-4 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //-------------------------------------------Last_Outbound_Funds_Transfer------------------------------------------------
//                Rule:- 4
                if (rules.birthDateValidationRule4(dqRecord.get("Last_Outbound_Funds_Transfer"))) {
                    test.pass("**********Pass Statement********* Last_Outbound_Funds_Transfer:- " + dqRecord.get("Last_Outbound_Funds_Transfer") + " is as per Rule-4" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Last_Outbound_Funds_Transfer:- " + dqRecord.get("Last_Outbound_Funds_Transfer") +
                            " is not as per Rule-4 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Last_Outbound_Funds_Transfer:- " + dqRecord.get("Last_Outbound_Funds_Transfer") +
                            " is not as per Rule-4 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //-------------------------------------------Next_Outbound_Funds_Transfer------------------------------------------------
//                Rule:- 4
                if (rules.birthDateValidationRule4(dqRecord.get("Next_Outbound_Funds_Transfer"))) {
                    test.pass("**********Pass Statement********* Next_Outbound_Funds_Transfer:- " + dqRecord.get("Next_Outbound_Funds_Transfer") + " is as per Rule-4" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Next_Outbound_Funds_Transfer:- " + dqRecord.get("Next_Outbound_Funds_Transfer") +
                            " is not as per Rule-4 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Next_Outbound_Funds_Transfer:- " + dqRecord.get("Next_Outbound_Funds_Transfer") +
                            " is not as per Rule-4 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }
                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessages.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    @Then("^validate (\\d+)-CDIC Personal ID Type$")
    public void validateCDICPersonalIDType(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        String headers = null;
        List<String> results = new LinkedList<>();
        List<String> cdicPersonalIdTypeCodeTable0212 = null;
        List<String> cdicPersonalIdTypeDuplicates = null;
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();

            for (Map<String, String> dqRecord : dqRecords) {
                // ----------------------------- CDIC_Personal_ID_Type_Code-----------------------------
//                Rule:- 1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("CDIC_Personal_ID_Type_Code"))) {
                    test.pass("**********Pass Statement********* CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code") + "is as per Rule-1");
                } else {
                    test.fail("--- FAIL ---> CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code") +
                            "is not as per Rule-1");
                    System.err.println("--- FAIL ---> CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code") +
                            "is not as per Rule-1");
                }


                //            Rule-9
                if (cdicPersonalIdTypeCodeTable0212 == null) {
                    cdicPersonalIdTypeCodeTable0212 = new DQRules().getListOfString(mi, "0202", "Phone_Type_Code", fileName.substring(26, 29), "DQ");
                    cdicPersonalIdTypeDuplicates =
                            cdicPersonalIdTypeCodeTable0212.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(cdicPersonalIdTypeDuplicates.contains(dqRecord.get("CDIC_Personal_ID_Type_Code")))) {
                    test.pass("**********Pass Statement********* CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code") + " is not as per Rule-9");
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("CDIC_Personal_ID_Type_Code"))) {
                    test.pass("**********Pass Statement********* CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code") + "is as per Rule-18");
                } else {
                    test.fail("--- FAIL ---> CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code") +
                            "is not as per Rule-18");
                    System.err.println("--- FAIL ---> CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code") +
                            "is not as per Rule-18");
                }

                // ----------------------------- Description-----------------------------
//            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + "is as per Rule-2" +
                            " for CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + "is as per Rule-18" +
                            " for CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code"));
                    System.err.println(("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for CDIC_Personal_ID_Type_Code:- " + dqRecord.get("CDIC_Personal_ID_Type_Code")));
                }
                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));

    }

    @Then("^validate (\\d+)-Depositor Type$")
    public void validateDepositorType(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> depositorTypeCodeTableRule90201 = null;
            List<String> depositorTypeCodeDuplicates = null;
            for (Map<String, String> dqRecord : dqRecords) {
//            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Depositor_Type_Code"))) {
                    test.pass("**********Pass Statement********* Depositor_Type_Code:- " + dqRecord.get("Depositor_Type_Code") + " is as per Rule-1");
                } else {
                    test.fail("--- FAIL ---> Depositor_Type_Code:- " + dqRecord.get("Depositor_Type_Code") +
                            "is not as per Rule-1");
                    System.err.println("--- FAIL ---> Depositor_Type_Code:- " + dqRecord.get("Depositor_Type_Code") +
                            "is not as per Rule-1");
                }

                //            Rule-9
                if (depositorTypeCodeTableRule90201 == null) {
                    depositorTypeCodeTableRule90201 = new DQRules().getListOfString(mi, "0201", "Depositor_Type_Code", fileName.substring(26, 29), "DQ");
                    depositorTypeCodeDuplicates =
                            depositorTypeCodeTableRule90201.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(depositorTypeCodeDuplicates.contains(dqRecord.get("Depositor_Type_Code")))) {
                    test.pass("**********Pass Statement********* Depositor_Type_Code:- " + dqRecord.get("Depositor_Type_Code") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Depositor_Type_Code:- " + dqRecord.get("Depositor_Type_Code") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> Depositor_Type_Code:- " + dqRecord.get("Depositor_Type_Code") + " is not as per Rule-9");
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Depositor_Type_Code"))) {
                    test.pass("**********Pass Statement********* Depositor_Type_Code:- " + dqRecord.get("Depositor_Type_Code") + " is as per Rule-18");
                } else {
                    test.fail("--- FAIL ---> Depositor_Type_Code:- " + dqRecord.get("Depositor_Type_Code") +
                            " is not as per Rule-18");
                    System.err.println("--- FAIL ---> Depositor_Type_Code:- " + dqRecord.get("Depositor_Type_Code") +
                            " is not as per Rule-18");
                }

                // ----------------------------- MI_Depositor_Type-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("MI_Depositor_Type"))) {
                    test.pass("**********Pass Statement********* MI_Depositor_Type:- " + dqRecord.get("MI_Depositor_Type") + " is as per Rule-2" +
                            " for Depositor_Type_Code:- " + dqRecord.get("Depositor_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> MI_Depositor_Type:- " + dqRecord.get("MI_Depositor_Type") + "is not as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> MI_Depositor_Type:- " + dqRecord.get("MI_Depositor_Type") + "is not as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                // ----------------------------- Description-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-2" +
                            " for Depositor_Type_Code:- " + dqRecord.get("Depositor_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-18" +
                            " for Depositor_Type_Code:- " + dqRecord.get("Depositor_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));

    }

    @Then("^validate (\\d+)-Product Code$")
    public void validateProductCode(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }

        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        String headers = null;
        List<String> results = new LinkedList<>();

        List<String> fileValues = new DQRules().getDQFileNames(tableId);
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> productCodeTableRule90231 = null;
            List<String> productCodeDuplicates = null;
            List<String> cdicProductGroupCodeTableRule90240 = null;
            List<String> subSystemFilesCDICProductGroupCode0240 = null;
            for (Map<String, String> dqRecord : dqRecords) {

                //-------------------------------------------Product_Code------------------------------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Product_Code"))) {
                    test.pass("**********Pass Statement********* Product_Code:- " + dqRecord.get("Product_Code") + " is as per Rule-1");
                } else {
                    test.fail("--- FAIL ---> Product_Code:- " + dqRecord.get("Product_Code") +
                            "is not as per Rule-1");
                    System.err.println("--- FAIL ---> Product_Code:- " + dqRecord.get("Product_Code") +
                            "is not as per Rule-1");
                }


                //            Rule-9
                if (productCodeTableRule90231 == null) {
                    productCodeTableRule90231 = new DQRules().getListOfString(mi, "0231", "Product_Code", fileName.substring(26, 29), "DQ");
                    productCodeDuplicates =
                            productCodeTableRule90231.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(productCodeDuplicates.contains(dqRecord.get("Product_Code")))) {
                    test.pass("**********Pass Statement********* Product_Code:- " + dqRecord.get("Product_Code") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Product_Code:- " + dqRecord.get("Product_Code") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> Product_Code:- " + dqRecord.get("Product_Code") + " is not as per Rule-9");
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Product_Code"))) {
                    test.pass("**********Pass Statement********* Product_Code:- " + dqRecord.get("Product_Code") + " is as per Rule-18");
                } else {
                    test.fail("--- FAIL ---> Product_Code:- " + dqRecord.get("Product_Code") +
                            "is not as per Rule-18");
                }

                // ----------------------------- MI_Product_Code-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("MI_Product_Code"))) {
                    test.pass("**********Pass Statement********* MI_Product_Code:- " + dqRecord.get("MI_Product_Code") + " is as per Rule-2" +
                            " for Product_Code:- " + dqRecord.get("Product_Code"));
                } else {
                    test.fail("--- FAIL ---> MI_Product_Code:- " + dqRecord.get("MI_Product_Code") +
                            " is not as per Rule-2 for Product_Code:- " + dqRecord.get("Product_Code"));
                }

                //------------------------------Description---------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-2" +
                            " for Product_Code:- " + dqRecord.get("Product_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Product_Code:- " + dqRecord.get("Product_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Product_Code:- " + dqRecord.get("Product_Code"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-18" +
                            " for Product_Code:- " + dqRecord.get("Product_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Product_Code:- " + dqRecord.get("Product_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Product_Code:- " + dqRecord.get("Product_Code"));
                }


                // ----------------------------- CDIC_Product_Group_Code-----------------------------
//                Rule:- 1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("CDIC_Product_Group_Code"))) {
                    test.pass("**********Pass Statement********* CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code") + " is as per Rule-1" +
                            " for Product_Code:- " + dqRecord.get("Product_Code"));
                } else {
                    test.fail("--- FAIL ---> CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code") +
                            "is not as per Rule-1 for Product_Code:- " + dqRecord.get("Product_Code"));
                    System.err.println("--- FAIL ---> CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code") +
                            "is not as per Rule-1 for Product_Code:- " + dqRecord.get("Product_Code"));
                }

                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (cdicProductGroupCodeTableRule90240 == null) {
                    cdicProductGroupCodeTableRule90240 = new DQRules().getListOfString(mi, "0240", "CDIC_Product_Group_Code", fileName.substring(26, 29), "DQ");
                    subSystemFilesCDICProductGroupCode0240 = new DQRules().getDQFileNames("0240").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFilesCDICProductGroupCode0240.size() == 0) {
                        test.fail("--- FAIL ---> CDIC_Product_Group_Code-0240:- " + mi + "*0240" + fileName.substring(22, 29) +
                                " is not found for Rule-304");
                        System.err.println("--- FAIL ---> CDIC_Product_Group_Code-0240:- " + mi + "*0240" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (cdicProductGroupCodeTableRule90240.size() != 0) {
                    boolean cdicProductGroupFlag240 = cdicProductGroupCodeTableRule90240.contains(dqRecord.get("CDIC_Product_Group_Code"));
                    if (cdicProductGroupFlag240) {
                        test.pass("**********Pass Statement********* CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code") + " is as per Rule-10" +
                                " for Product_Code:- " + dqRecord.get("Product_Code"));
                    } else {
                        test.fail("--- FAIL ---> CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFilesCDICProductGroupCode0240 + " for Product_Code:- " + dqRecord.get("Product_Code"));
                        System.err.println("--- FAIL ---> CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFilesCDICProductGroupCode0240 + " for Product_Code:- " + dqRecord.get("Product_Code"));
                    }
                }
                // Rule-18
                if (rules.noBlankRule18(dqRecord.get("CDIC_Product_Group_Code"))) {
                    test.pass("**********Pass Statement********* CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code") + " is as per Rule-18" +
                            " for Product_Code:- " + dqRecord.get("Product_Code"));
                } else {
                    test.fail(
                            "--- FAIL ---> CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code") + "is not as per Rule-18" +
                                    " for Product_Code:- " + dqRecord.get("Product_Code"));
                }

                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));

    }


    @Then("^validate (\\d+)-Registered Plan Type$")
    public void validateRegisteredPlanType(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> registeredPlanTypeTableRule90232 = null;
            List<String> registeredPlanTypeDuplicates = null;
            for (Map<String, String> dqRecord : dqRecords) {
                // ----------------------------- Registered_Plan_Type_Code-----------------------------
//                Rule:- 1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Registered_Plan_Type_Code"))) {
                    test.pass("**********Pass Statement********* Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code") + " is as per Rule-1");
                } else {
                    test.fail("--- FAIL ---> Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code") +
                            "is not as per Rule-1");
                    System.err.println("--- FAIL ---> Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code") +
                            "is not as per Rule-1");
                }


                //            Rule-9
                if (registeredPlanTypeTableRule90232 == null) {
                    registeredPlanTypeTableRule90232 = new DQRules().getListOfString(mi, "0232", "Registered_Plan_Type_Code", fileName.substring(26, 29), "DQ");
                    registeredPlanTypeDuplicates =
                            registeredPlanTypeTableRule90232.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(registeredPlanTypeDuplicates.contains(dqRecord.get("Registered_Plan_Type_Code")))) {
                    test.pass("**********Pass Statement********* Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code") + "is not as per Rule-9");
                    System.err.println("--- FAIL ---> Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code") + "is not as per Rule-9");
                }

                // Rule-18
                if (rules.noBlankRule18(dqRecord.get("Registered_Plan_Type_Code"))) {
                    test.pass("**********Pass Statement********* Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code") + " is as per Rule-18");
                } else {
                    test.fail("--- FAIL ---> Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code") + "is not as per Rule-18");
                    System.err.println("--- FAIL ---> Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code") + "is not as per Rule-18");
                }

                //------------------------------MI_Registered_Plan_Type---------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("MI_Registered_Plan_Type"))) {
                    test.pass("**********Pass Statement********* MI_Registered_Plan_Type:- " + dqRecord.get("MI_Registered_Plan_Type") + " is as per Rule-2" +
                            " for Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> MI_Registered_Plan_Type:- " + dqRecord.get("MI_Registered_Plan_Type") +
                            " is not as per Rule-2 for Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code"));
                    System.err.println("--- FAIL ---> MI_Registered_Plan_Type:- " + dqRecord.get("MI_Registered_Plan_Type") +
                            " is not as per Rule-2 for Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code"));
                }

                //------------------------------Description---------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-2" +
                            " for Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-18" +
                            " for Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Registered_Plan_Type_Code:- " + dqRecord.get("Registered_Plan_Type_Code"));
                }
                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));

    }

    @Then("^validate (\\d+)-CDIC Hold Status Code$")
    public void validateCDICHoldStatusCode(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> cdicHoldStatusCodeTableRule90235 = null;
            List<String> cdicHoldStatusCodeDuplicates = null;
            for (Map<String, String> dqRecord : dqRecords) {
                //------------------------------CDIC_Hold_Status_Code---------------------------------------
//                Rule:- 1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("CDIC_Hold_Status_Code"))) {
                    test.pass("**********Pass Statement********* CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") + " is as per Rule-1");
                } else {
                    test.fail("--- FAIL ---> CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") +
                            "is not as per Rule-1");
                    System.err.println("--- FAIL ---> CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") +
                            "is not as per Rule-1");
                }

                //            Rule-9
                // Rule:- 9 New code
                if (cdicHoldStatusCodeTableRule90235 == null) {
                    cdicHoldStatusCodeTableRule90235 = new DQRules().getListOfString(mi, "0235", "CDIC_Hold_Status_Code", fileName.substring(26, 29), "DQ");
                    cdicHoldStatusCodeDuplicates =
                            cdicHoldStatusCodeTableRule90235.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(cdicHoldStatusCodeDuplicates.contains(dqRecord.get("CDIC_Hold_Status_Code")))
                        || dqRecord.get("CDIC_Hold_Status_Code").isEmpty()) {
                    test.pass("**********Pass Statement********* CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") + " is not as per Rule-9");
                }

                // Rule-18
                if (rules.noBlankRule18(dqRecord.get("CDIC_Hold_Status_Code"))) {
                    test.pass("**********Pass Statement********* CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") + " is as per Rule-18");
                } else {
                    test.fail("--- FAIL ---> CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") + "is not as per Rule-18");
                    System.err.println("--- FAIL ---> CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") + "is not as per Rule-18");
                }
//------------------------------CDIC_Hold_Status---------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("CDIC_Hold_Status"))) {
                    test.pass("**********Pass Statement********* CDIC_Hold_Status:- " + dqRecord.get("CDIC_Hold_Status") + " is as per Rule-2" +
                            " for CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code"));
                } else {
                    test.fail("--- FAIL ---> CDIC_Hold_Status:- " + dqRecord.get("CDIC_Hold_Status") +
                            " is not as per Rule-2 for CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code"));
                    System.err.println("--- FAIL ---> CDIC_Hold_Status:- " + dqRecord.get("CDIC_Hold_Status") +
                            " is not as per Rule-2 for CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("CDIC_Hold_Status"))) {
                    test.pass("**********Pass Statement********* CDIC_Hold_Status:- " + dqRecord.get("CDIC_Hold_Status") + " is as per Rule-18" +
                            " for CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code"));
                } else {
                    test.fail("--- FAIL ---> CDIC_Hold_Status:- " + dqRecord.get("CDIC_Hold_Status") +
                            " is not as per Rule-18 for CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code"));
                    System.err.println("--- FAIL ---> CDIC_Hold_Status:- " + dqRecord.get("CDIC_Hold_Status") +
                            " is not as per Rule-18 for CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code"));
                }

                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));

    }


    @Then("^validate (\\d+)-Account Status Code$")
    public void validateAccountStatusCode(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        List<String> fileValues = new DQRules().getDQFileNames(tableId);
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> accountStatusCodeTableRule90236 = null;
            List<String> accountStatusCodeDuplicates = null;
            for (Map<String, String> dqRecord : dqRecords) {
//-------------------------------------------Account_Status_Code------------------------------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Account_Status_Code"))) {
                    test.pass("**********Pass Statement********* Account_Status_Code:- " + dqRecord.get("Account_Status_Code") + " is as per Rule-1");
                } else {
                    test.fail("--- FAIL ---> Account_Status_Code:- " + dqRecord.get("Account_Status_Code") +
                            " is not as per Rule-1");
                    System.err.println("--- FAIL ---> Account_Status_Code:- " + dqRecord.get("Account_Status_Code") +
                            " is not as per Rule-1");
                }

                //   Rule-9
                if (rules.noDuplicatesPrimaryKeyRule9(dqRecords, "Account_Status_Code", dqRecord.get("Account_Status_Code"))) {
                    test.pass("**********Pass Statement********* Account_Status_Code:- " + dqRecord.get("Account_Status_Code") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Account_Status_Code:- " + dqRecord.get("Account_Status_Code") + "is not as per Rule-9");
                    System.err.println("--- FAIL ---> Account_Status_Code:- " + dqRecord.get("Account_Status_Code") + "is not as per Rule-9");
                }

                //            Rule-9
                // Rule:- 9 New code
                if (accountStatusCodeTableRule90236 == null) {
                    accountStatusCodeTableRule90236 = new DQRules().getListOfString(mi, "0236", "Account_Status_Code", fileName.substring(26, 29), "DQ");
                    accountStatusCodeDuplicates =
                            accountStatusCodeTableRule90236.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(accountStatusCodeDuplicates.contains(dqRecord.get("Account_Status_Code")))
                        || dqRecord.get("Account_Status_Code").isEmpty()) {
                    test.pass("**********Pass Statement********* Account_Status_Code:- " + dqRecord.get("Account_Status_Code") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Account_Status_Code:- " + dqRecord.get("Account_Status_Code") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> Account_Status_Code:- " + dqRecord.get("Account_Status_Code") + " is not as per Rule-9");
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Account_Status_Code"))) {
                    test.pass("**********Pass Statement********* Account_Status_Code:- " + dqRecord.get("Account_Status_Code") + " is as per Rule-18");
                } else {
                    test.fail("--- FAIL ---> Account_Status_Code:- " + dqRecord.get("Account_Status_Code") +
                            "is not as per Rule-18");
                    System.err.println("--- FAIL ---> Account_Status_Code:- " + dqRecord.get("Account_Status_Code") +
                            "is not as per Rule-18");
                }

                //------------------------------MI_Account_Status_Code---------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("MI_Account_Status_Code"))) {
                    test.pass("**********Pass Statement********* MI_Account_Status_Code:- " + dqRecord.get("MI_Account_Status_Code") + " is as per Rule-2" +
                            " for Account_Status_Code:- " + dqRecord.get("Account_Status_Code"));
                } else {
                    test.fail("--- FAIL ---> MI_Account_Status_Code:- " + dqRecord.get("MI_Account_Status_Code") +
                            " is not as per Rule-2 for Account_Status_Code:- " + dqRecord.get("Account_Status_Code"));
                    System.err.println("--- FAIL ---> MI_Account_Status_Code:- " + dqRecord.get("MI_Account_Status_Code") +
                            " is not as per Rule-2 for Account_Status_Code:- " + dqRecord.get("Account_Status_Code"));
                }

                //------------------------------Description---------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-2" +
                            " for Account_Status_Code:- " + dqRecord.get("Account_Status_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Account_Status_Code:- " + dqRecord.get("Account_Status_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Account_Status_Code:- " + dqRecord.get("Account_Status_Code"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-18" +
                            " for Account_Status_Code:- " + dqRecord.get("Account_Status_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Account_Status_Code:- " + dqRecord.get("Account_Status_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Account_Status_Code:- " + dqRecord.get("Account_Status_Code"));
                }
                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));

    }

    @Then("^validate (\\d+)-Clearing Account Code$")
    public void validateClearingAccountCode(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> clearingAccountCodeTableRule90237 = null;
            List<String> clearingAccountCodeDuplicates = null;
            for (Map<String, String> dqRecord : dqRecords) {
                //-------------------------------------------Clearing_Account_Code------------------------------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Clearing_Account_Code"))) {
                    test.pass("**********Pass Statement********* Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code") + " is as per Rule-1");
                } else {
                    test.fail("--- FAIL ---> Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code") +
                            "is not as per Rule-1");
                    System.err.println("--- FAIL ---> Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code") +
                            "is not as per Rule-1");
                }

                //            Rule-9
                // Rule:- 9 New code
                if (clearingAccountCodeTableRule90237 == null) {
                    clearingAccountCodeTableRule90237 = new DQRules().getListOfString(mi, "0238", "Clearing_Account_Code", fileName.substring(26, 29), "DQ");
                    clearingAccountCodeDuplicates =
                            clearingAccountCodeTableRule90237.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(clearingAccountCodeDuplicates.contains(dqRecord.get("Clearing_Account_Code")))
                        || dqRecord.get("Clearing_Account_Code").isEmpty()) {
                    test.pass("**********Pass Statement********* Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code") + " is not as per Rule-9");
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Clearing_Account_Code"))) {
                    test.pass("**********Pass Statement********* Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code") + " is as per Rule-18");
                } else {
                    test.fail("--- FAIL ---> Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code") +
                            "is not as per Rule-18");
                    System.err.println("--- FAIL ---> Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code") +
                            "is not as per Rule-18");
                }

                //-------------------------------------------MI_Clearing_Account------------------------------------------------
                //   Rule-3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("MI_Clearing_Account"), 1)) {
                    test.pass("**********Pass Statement********* MI_Clearing_Account:- " + dqRecord.get("MI_Clearing_Account") + " is as per Rule-3" +
                            " for Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code"));
                } else {
                    test.fail("--- FAIL ---> MI_Clearing_Account:- " + dqRecord.get("MI_Clearing_Account") +
                            "is not as per Rule-3 for Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code"));
                    System.err.println("--- FAIL ---> MI_Clearing_Account:- " + dqRecord.get("MI_Clearing_Account") +
                            "is not as per Rule-3 for Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code"));
                }

                //------------------------------Description---------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-2" +
                            " for Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-18" +
                            " for Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Clearing_Account_Code:- " + dqRecord.get("Clearing_Account_Code"));
                }
                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    @Then("^validate (\\d+)-Account Type$")
    public void validateAccountType(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        List<String> fileValues = new DQRules().getDQFileNames(tableId);
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> accountTypeCodeTableRule90239 = null;
            List<String> accountTypeCodeDuplicates = null;
            for (Map<String, String> dqRecord : dqRecords) {
                //-------------------------------------------Account_Type_Code------------------------------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Account_Type_Code"))) {
                    test.pass("**********Pass Statement********* Account_Type_Code:- " + dqRecord.get("Account_Type_Code") + " is as per Rule-1");
                } else {
                    test.fail("--- FAIL ---> Account_Type_Code:- " + dqRecord.get("Account_Type_Code") +
                            "is not as per Rule-1");
                    System.err.println("--- FAIL ---> Account_Type_Code:- " + dqRecord.get("Account_Type_Code") +
                            "is not as per Rule-1");
                }

                //            Rule-9
                // Rule:- 9 New code
                if (accountTypeCodeTableRule90239 == null) {
                    accountTypeCodeTableRule90239 = new DQRules().getListOfString(mi, "0239", "Account_Type_Code", fileName.substring(26, 29), "DQ");
                    accountTypeCodeDuplicates =
                            accountTypeCodeTableRule90239.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(accountTypeCodeDuplicates.contains(dqRecord.get("Account_Type_Code")))
                        || dqRecord.get("Account_Type_Code").isEmpty()) {
                    test.pass("**********Pass Statement********* Account_Type_Code:- " + dqRecord.get("Account_Type_Code") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Account_Type_Code:- " + dqRecord.get("Account_Type_Code") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> Account_Type_Code:- " + dqRecord.get("Account_Type_Code") + " is not as per Rule-9");
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Account_Type_Code"))) {
                    test.pass("**********Pass Statement********* Account_Type_Code:- " + dqRecord.get("Account_Type_Code") + " is as per Rule-18");
                } else {
                    test.fail("--- FAIL ---> Account_Type_Code:- " + dqRecord.get("Account_Type_Code") +
                            "is not as per Rule-18");
                    System.err.println("--- FAIL ---> Account_Type_Code:- " + dqRecord.get("Account_Type_Code") +
                            "is not as per Rule-18");
                }
                //-------------------------------------------MI_Account_Type------------------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("MI_Account_Type"))) {
                    test.pass("**********Pass Statement********* MI_Account_Type:- " + dqRecord.get("MI_Account_Type") + " is as per Rule-2" +
                            " for Account_Type_Code:- " + dqRecord.get("Account_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> MI_Account_Type:- " + dqRecord.get("MI_Account_Type") +
                            " is not as per Rule-2 for Account_Type_Code:- " + dqRecord.get("Account_Type_Code"));
                    System.err.println("--- FAIL ---> MI_Account_Type:- " + dqRecord.get("MI_Account_Type") +
                            " is not as per Rule-2 for Account_Type_Code:- " + dqRecord.get("Account_Type_Code"));
                }

                //-------------------------------------------Description------------------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("MI_Account_Type") + " is as per Rule-2" +
                            " for Account_Type_Code:- " + dqRecord.get("Account_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Account_Type_Code:- " + dqRecord.get("Account_Type_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Account_Type_Code:- " + dqRecord.get("Account_Type_Code"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-18" +
                            " for Account_Type_Code:- " + dqRecord.get("Account_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Account_Type_Code:- " + dqRecord.get("Account_Type_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Account_Type_Code:- " + dqRecord.get("Account_Type_Code"));
                }
                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    @Then("^validate (\\d+)-Transaction Data$")
    public void validateTransactionData(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        List<String> fileValues = new DQRules().getDQFileNames(tableId);
        String headers = null;
        List<String> results = new LinkedList<>();

        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> transactionCodeTable0401 = null;
            List<String> subSystemFiles0401 = null;
            List<String> currencyCodeTable0233 = null;
            List<String> subSystemFiles0233 = null;
            for (Map<String, String> dqRecord : dqRecords) {

                // ----------------------------- Account_Unique_ID-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Account_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is as per Rule-2");

                } else {
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is not as per Rule-2");
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is not as per Rule-2");
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Account_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is as per Rule-18");

                } else {
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is not as per Rule-18");
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is not as per Rule-18");
                }

                // ----------------------------- Transaction_Number-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Transaction_Number"))) {
                    test.pass("**********Pass Statement********* Transaction_Number:- " + dqRecord.get("Transaction_Number") + " is as per Rule-2" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Transaction_Number:- " + dqRecord.get("Transaction_Number") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Transaction_Number:- " + dqRecord.get("Transaction_Number") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                // ----------------------------- Transaction_Item_Number-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Transaction_Item_Number"))) {
                    test.pass("**********Pass Statement********* Transaction_Item_Number:- " + dqRecord.get("Transaction_Item_Number") + " is as per Rule-2" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Transaction_Item_Number:- " + dqRecord.get("Transaction_Item_Number") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Transaction_Item_Number:- " + dqRecord.get("Transaction_Item_Number") +
                            " is not as per Rule-2 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                // ----------------------------- Created_Date-----------------------------
                //Rule:- 5 - Date format 'yyyyMMdd:HHmmss'
                if (rules.addressChangeValidationRule5(dqRecord.get("Created_Date"))) {
                    test.pass("**********Pass Statement********* Created_Date:- " + dqRecord.get("Created_Date") + " is as per Rule-5" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Created_Date:- " + dqRecord.get("Created_Date") +
                            "is not as per Rule-5 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Created_Date:- " + dqRecord.get("Created_Date") +
                            "is not as per Rule-5 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Created_Date"))) {
                    test.pass("**********Pass Statement********* Created_Date:- " + dqRecord.get("Created_Date") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Created_Date:- " + dqRecord.get("Created_Date") + "is not as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Created_Date:- " + dqRecord.get("Created_Date") + "is not as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                // ----------------------------- Posted_Date-----------------------------
                //Rule:- 5 - Date format 'yyyyMMdd:HHmmss'
                if (rules.addressChangeValidationRule5(dqRecord.get("Posted_Date"))) {
                    test.pass("**********Pass Statement********* Posted_Date:- " + dqRecord.get("Posted_Date") + " is as per Rule-5" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Posted_Date:- " + dqRecord.get("Posted_Date") +
                            "is not as per Rule-5 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Posted_Date:- " + dqRecord.get("Posted_Date") +
                            "is not as per Rule-5 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                // ----------------------------- Transaction_Value-----------------------------
                //            Rule-6
                if (rules.amountValidationRule6(dqRecord.get("Transaction_Value"), "30", "2")) {
                    test.pass("**********Pass Statement********* Transaction_Value:- " + dqRecord.get("Transaction_Value") + " is as per Rule-6" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Transaction_Value:- " + dqRecord.get("Transaction_Value") +
                            "is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Transaction_Value:- " + dqRecord.get("Transaction_Value") +
                            "is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Transaction_Value"))) {
                    test.pass("**********Pass Statement********* Transaction_Value:- " + dqRecord.get("Transaction_Value") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Transaction_Value:- " + dqRecord.get("Transaction_Value") + "is not as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Transaction_Value:- " + dqRecord.get("Transaction_Value") + "is not as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                // ----------------------------- Foreign_Value-----------------------------
                //            Rule-6
                if (rules.amountValidationRule6(dqRecord.get("Foreign_Value"), "30", "2")) {
                    test.pass("**********Pass Statement********* Foreign_Value:- " + dqRecord.get("Foreign_Value") + " is as per Rule-6" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Foreign_Value:- " + dqRecord.get("Foreign_Value") +
                            "is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Foreign_Value:- " + dqRecord.get("Foreign_Value") +
                            "is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                // ----------------------------- Transaction_Code-----------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Transaction_Code"))) {
                    test.pass("**********Pass Statement********* Transaction_Code:- " + dqRecord.get("Transaction_Code") + " is as per Rule-1" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Transaction_Code:- " + dqRecord.get("Transaction_Code") +
                            "is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Transaction_Code:- " + dqRecord.get("Transaction_Code") +
                            "is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (transactionCodeTable0401 == null) {
                    transactionCodeTable0401 = new DQRules().getListOfString(mi, "0401", "Transaction_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0401 = new DQRules().getDQFileNames("0401").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0401.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0401:- " + mi + "*0401" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0401.size() != 0) {
                    boolean transactionCodeFlag401 = transactionCodeTable0401.contains(dqRecord.get("Transaction_Code"));
                    if (transactionCodeFlag401) {
                        test.pass("**********Pass Statement********* Transaction_Code:- " + dqRecord.get("Transaction_Code") + " is as per Rule-10" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Transaction_Code:- " + dqRecord.get("Transaction_Code") +
                                " is not as per Rule-10 and not found in file " + fileValues.stream().filter(x -> x.substring(0, 4).equals(fileName.substring(26, 29))).collect(Collectors.toList()) + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Transaction_Code:- " + dqRecord.get("Transaction_Code") +
                                " is not as per Rule-10 and not found in file " + fileValues.stream().filter(x -> x.substring(0, 4).equals(fileName.substring(26, 29))).collect(Collectors.toList()) + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    }
                }
                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Transaction_Code"))) {
                    test.pass("**********Pass Statement********* Transaction_Code:- " + dqRecord.get("Transaction_Code") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Transaction_Code:- " + dqRecord.get("Transaction_Code") + "is not as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Transaction_Code:- " + dqRecord.get("Transaction_Code") + "is not as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                // ----------------------------- Currency_Code-----------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Currency_Code"))) {
                    test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") + " is as per Rule-1" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //Rule:- 10 - Values present the file should be present in other file, Which primary key
                //                for other file where as foreign key for current file verification
                if (currencyCodeTable0233 == null) {
                    currencyCodeTable0233 = new DQRules().getListOfString(mi, "0233", "Currency_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0233 = new DQRules().getDQFileNames("0233").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0233.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0233:- " + mi + "*0233" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0233.size() != 0) {
                    boolean currencyCodeFlag233 = currencyCodeTable0233.contains(dqRecord.get("Currency_Code"));
                    if (currencyCodeFlag233) {
                        test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") + " is as per Rule-10" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0233 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0233 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    }
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Currency_Code"))) {
                    test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") + "is not as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") + "is not as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                // ----------------------------- Debit_Credit_Flag-----------------------------
//              Rule:- 3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("Debit_Credit_Flag"), 2)) {
                    test.pass("**********Pass Statement********* Debit_Credit_Flag:- " + dqRecord.get("Debit_Credit_Flag") + " is as per Rule-3" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Debit_Credit_Flag:- " + dqRecord.get("Debit_Credit_Flag") +
                            " is not as per Rule-3 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Debit_Credit_Flag:- " + dqRecord.get("Debit_Credit_Flag") +
                            " is not as per Rule-3 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                // Rule-16
                if (rules.acceptsOnlySpecificCharactersRule16(dqRecord.get("Debit_Credit_Flag"), "DR|CR")) {
                    test.pass("**********Pass Statement********* Debit_Credit_Flag:- " + dqRecord.get("Debit_Credit_Flag") + " is as per Rule-16" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Debit_Credit_Flag:- " + dqRecord.get("Debit_Credit_Flag") + "is not as per Rule-16" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Debit_Credit_Flag:- " + dqRecord.get("Debit_Credit_Flag") + "is not as per Rule-16" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Debit_Credit_Flag"))) {
                    test.pass("**********Pass Statement********* Debit_Credit_Flag:- " + dqRecord.get("Debit_Credit_Flag") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Debit_Credit_Flag:- " + dqRecord.get("Debit_Credit_Flag") + "is not as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Debit_Credit_Flag:- " + dqRecord.get("Debit_Credit_Flag") + "is not as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        if (results.get(0) != null) {
            HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
        } else {
            System.err.println("There is no data in table - " + tableId);
        }

    }

    @Then("^validate (\\d+)-Relationship Type$")
    public void validateRelationshipType(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        List<String> fileValues = new DQRules().getDQFileNames(tableId);
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> relationShipTypeCodeTableRule90501 = null;
            List<String> relationShipTypeCodeDuplicates = null;
            for (Map<String, String> dqRecord : dqRecords) {
                // ----------------------------- Relationship_Type_Code-----------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Relationship_Type_Code"))) {
                    test.pass("**********Pass Statement********* Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code") +
                            "is as per Rule-1");
                } else {
                    test.fail("--- FAIL ---> Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code") +
                            "is not as per Rule-1");
                    System.err.println("--- FAIL ---> Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code") +
                            "is not as per Rule-1");
                }


                //            Rule-9
                // Rule:- 9 New code
                if (relationShipTypeCodeTableRule90501 == null) {
                    relationShipTypeCodeTableRule90501 = new DQRules().getListOfString(mi, "0501", "Relationship_Type_Code", fileName.substring(26, 29), "DQ");
                    relationShipTypeCodeDuplicates =
                            relationShipTypeCodeTableRule90501.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(relationShipTypeCodeDuplicates.contains(dqRecord.get("Relationship_Type_Code")))
                        || dqRecord.get("Relationship_Type_Code").isEmpty()) {
                    test.pass("**********Pass Statement********* Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code") + " is not as per Rule-9");
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Relationship_Type_Code"))) {
                    test.pass("**********Pass Statement********* Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code") + "is as per Rule-18");

                } else {
                    test.fail("--- FAIL ---> Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code") +
                            "is not as per Rule-18");
                    System.err.println("--- FAIL ---> Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code") +
                            "is not as per Rule-18");
                }

                // ----------------------------- MI_Relationship_Type-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("MI_Relationship_Type"))) {
                    test.pass("**********Pass Statement********* MI_Relationship_Type:- " + dqRecord.get("MI_Relationship_Type") + " is as per Rule-2" +
                            " for Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> MI_Relationship_Type:- " + dqRecord.get("MI_Relationship_Type") +
                            " is not as per Rule-2 for Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code"));
                    System.err.println("--- FAIL ---> MI_Relationship_Type:- " + dqRecord.get("MI_Relationship_Type") +
                            " is not as per Rule-2 for Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code"));
                }

                //-------------------------------------------Description------------------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-2" +
                            " for Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-18" +
                            " for Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Relationship_Type_Code:- " + dqRecord.get("Relationship_Type_Code"));
                }

                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    @Then("^validate (\\d+)-CDIC Product Group Code$")
    public void validateCDICProductGroupCode(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        List<String> fileValues = new DQRules().getDQFileNames(tableId);
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> cdicProductGroupCodeTableRule90240 = null;
            List<String> cdicProductGroupCodeDuplicates = null;
            for (Map<String, String> dqRecord : dqRecords) {
                // ----------------------------- CDIC_Product_Group_Code-----------------------------
//                Rule:- 1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("CDIC_Product_Group_Code"))) {
                    test.pass("**********Pass Statement********* CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code") + " is as per Rule-1");
                } else {
                    test.fail("--- FAIL ---> CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code") +
                            "is not as per Rule-1");
                    System.err.println("--- FAIL ---> CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code") +
                            "is not as per Rule-1");
                }

//            Rule-9
                // Rule:- 9 New code
                if (cdicProductGroupCodeTableRule90240 == null) {
                    cdicProductGroupCodeTableRule90240 = new DQRules().getListOfString(mi, "0240", "CDIC_Product_Group", fileName.substring(26, 29), "DQ");
                    cdicProductGroupCodeDuplicates =
                            cdicProductGroupCodeTableRule90240.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(cdicProductGroupCodeDuplicates.contains(dqRecord.get("CDIC_Product_Group")))
                        || dqRecord.get("CDIC_Product_Group").isEmpty()) {
                    test.pass("**********Pass Statement********* CDIC_Product_Group:- " + dqRecord.get("CDIC_Product_Group") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> CDIC_Product_Group:- " + dqRecord.get("CDIC_Product_Group") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> CDIC_Product_Group:- " + dqRecord.get("CDIC_Product_Group") + " is not as per Rule-9");
                }


                // Rule-18
                if (rules.noBlankRule18(dqRecord.get("CDIC_Product_Group_Code"))) {
                    test.pass("**********Pass Statement********* CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code") + " is as per Rule-18");
                } else {
                    test.fail("--- FAIL ---> CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code") + "is not as per Rule-18");
                    System.err.println("--- FAIL ---> CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code") + "is not as per Rule-18");
                }

                // -----------------------------CDIC_Product_Group-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("CDIC_Product_Group"))) {
                    test.pass("**********Pass Statement********* CDIC_Product_Group:- " + dqRecord.get("CDIC_Product_Group") + " is as per Rule-2" +
                            " for CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code"));
                } else {
                    test.fail("--- FAIL ---> CDIC_Product_Group:- " + dqRecord.get("CDIC_Product_Group") +
                            " is not as per Rule-2 for CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code"));
                    System.err.println("--- FAIL ---> CDIC_Product_Group:- " + dqRecord.get("CDIC_Product_Group") +
                            " is not as per Rule-2 for CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("CDIC_Product_Group"))) {
                    test.pass("**********Pass Statement********* CDIC_Product_Group:- " + dqRecord.get("CDIC_Product_Group") + " is as per Rule-18" +
                            " for CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code"));
                } else {
                    test.fail("--- FAIL ---> CDIC_Product_Group:- " + dqRecord.get("CDIC_Product_Group") +
                            " is not as per Rule-18 for CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code"));
                    System.err.println("--- FAIL ---> CDIC_Product_Group:- " + dqRecord.get("CDIC_Product_Group") +
                            " is not as per Rule-18 for CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code"));
                }

                // -----------------------------Description-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-2" +
                            " for CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-18" +
                            " for CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for CDIC_Product_Group_Code:- " + dqRecord.get("CDIC_Product_Group_Code"));
                }
                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    @Then("^validate (\\d+)-Transaction Code$")
    public void validateTransactionCode(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> transactionCodeTableRule90401 = null;
            List<String> transactionCodeDuplicates = null;
            for (Map<String, String> dqRecord : dqRecords) {

                // ----------------------------- Transaction_Code-----------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Transaction_Code"))) {
                    test.pass("**********Pass Statement********* Transaction_Code:- " + dqRecord.get("Transaction_Code") + "is as per Rule-1");
                } else {
                    test.fail("--- FAIL ---> Transaction_Code:- " + dqRecord.get("Transaction_Code") +
                            "is not as per Rule-1");
                    System.err.println("--- FAIL ---> Transaction_Code:- " + dqRecord.get("Transaction_Code") +
                            "is not as per Rule-1");
                }

                //   Rule-9
                if (rules.noDuplicatesPrimaryKeyRule9(dqRecords, "Transaction_Code", dqRecord.get("Transaction_Code"))) {
                    test.pass("**********Pass Statement********* Transaction_Code:- " + dqRecord.get("Transaction_Code") + "is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Transaction_Code:- " + dqRecord.get("Transaction_Code") + "is not as per Rule-9");
                    System.err.println("--- FAIL ---> Transaction_Code:- " + dqRecord.get("Transaction_Code") + "is not as per Rule-9");
                }

                //            Rule-9
                // Rule:- 9 New code
                if (transactionCodeTableRule90401 == null) {
                    transactionCodeTableRule90401 = new DQRules().getListOfString(mi, "0401", "Transaction_Code", fileName.substring(26, 29), "DQ");
                    transactionCodeDuplicates =
                            transactionCodeTableRule90401.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(transactionCodeDuplicates.contains(dqRecord.get("Transaction_Code")))
                        || dqRecord.get("Transaction_Code").isEmpty()) {
                    test.pass("**********Pass Statement********* Transaction_Code:- " + dqRecord.get("Transaction_Code") + "is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Transaction_Code:- " + dqRecord.get("Transaction_Code") + "is not as per Rule-9");
                    System.err.println("--- FAIL ---> Transaction_Code:- " + dqRecord.get("Transaction_Code") + "is not as per Rule-9");
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Transaction_Code"))) {
                    test.pass("**********Pass Statement********* Transaction_Code:- " + dqRecord.get("Transaction_Code") + "is as per Rule-18");
                } else {
                    test.fail("--- FAIL ---> Transaction_Code:- " + dqRecord.get("Transaction_Code") + "is not as per Rule-18");
                    System.err.println("--- FAIL ---> Transaction_Code:- " + dqRecord.get("Transaction_Code") + "is not as per Rule-18");
                }

                // -----------------------------MI_Transaction_Code-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("MI_Transaction_Code"))) {
                    test.pass("**********Pass Statement********* MI_Transaction_Code:- " + dqRecord.get("MI_Transaction_Code") + " is as per Rule-2" +
                            " for Transaction_Code:- " + dqRecord.get("Transaction_Code"));
                } else {
                    test.fail("--- FAIL ---> MI_Transaction_Code:- " + dqRecord.get("MI_Transaction_Code") +
                            " is not as per Rule-2 for Transaction_Code:- " + dqRecord.get("Transaction_Code"));
                    System.err.println("--- FAIL ---> MI_Transaction_Code:- " + dqRecord.get("MI_Transaction_Code") +
                            " is not as per Rule-2 for Transaction_Code:- " + dqRecord.get("Transaction_Code"));
                }

                // -----------------------------Description-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-2" +
                            " for Transaction_Code:- " + dqRecord.get("Transaction_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Transaction_Code:- " + dqRecord.get("Transaction_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Transaction_Code:- " + dqRecord.get("Transaction_Code"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-18" +
                            " for Transaction_Code:- " + dqRecord.get("Transaction_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Transaction_Code:- " + dqRecord.get("Transaction_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Transaction_Code:- " + dqRecord.get("Transaction_Code"));
                }

                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        if (results.get(0) != null) {
            HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
        } else {
            System.err.println("There is no data in table - " + tableId);
        }

    }

    @Then("^validate (\\d+)-Hold Balance File$")
    public void validateHoldBalanceFile(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        List<String> fileValues = new DQRules().getDQFileNames(tableId);
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> accountUniqueIdTableRule90800 = null;
            List<String> accountUniqueIdDuplicates = null;
            List<String> subSystemFieldIdTable0800 = null;
            List<String> subSystemFiles0999 = null;
            List<String> cdicHoldStatusCodeTable0800 = null;
            List<String> subSystemFiles0235 = null;
            List<String> currencyCodeTable0800 = null;
            List<String> subSystemFiles0233 = null;
            for (Map<String, String> dqRecord : dqRecords) {

                // ----------------------------- Account_Unique_ID-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Account_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is as per Rule-2");

                } else {
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is not as per Rule-2");
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is not as per Rule-2");
                }

                //            Rule-9
                // Rule:- 9 New code
                if (accountUniqueIdTableRule90800 == null) {
                    accountUniqueIdTableRule90800 = new DQRules().getListOfString(mi, "0800", "Account_Unique_ID", fileName.substring(26, 29), "DQ");
                    accountUniqueIdDuplicates =
                            accountUniqueIdTableRule90800.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(accountUniqueIdDuplicates.contains(dqRecord.get("Account_Unique_ID")))
                        || dqRecord.get("Account_Unique_ID").isEmpty()) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-9");
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Account_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is as per Rule-18");

                } else {
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is not as per Rule-18");
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is not as per Rule-18");
                }


                // ----------------------------- Subsystem_ID-----------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Subsystem_ID"))) {
                    test.pass("**********Pass Statement********* Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                            "is as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                            "is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                            "is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (subSystemFieldIdTable0800 == null) {
                    subSystemFieldIdTable0800 = new DQRules().getListOfString(mi, "0999", "Subsystem_ID", fileName.substring(26, 29), "DQ");
                    subSystemFiles0999 = new DQRules().getDQFileNames("0999").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0999.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0999:- " + mi + "*0999" + fileName.substring(22, 29) +
                                " is not found for Rule-304");
                        System.err.println("--- FAIL ---> Sub_System_File-0999:- " + mi + "*0999" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0999.size() != 0) {
                    boolean subSystemFlag999 = subSystemFieldIdTable0800.contains(dqRecord.get("Subsystem_ID"));
                    //                Verifying value in table Id - 0999
                    if (subSystemFlag999) {
                        test.pass("**********Pass Statement********* Subsystem_ID:- " + dqRecord.get("Subsystem_ID") + " is as per Rule-10");
                    } else {
                        test.fail("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") + " is not as per Rule-10 and not found in file " + subSystemFiles0999);
                        System.err.println("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") + " is not as per Rule-10 and not found in file " + subSystemFiles0999);
                    }
                } else {
                    test.fail("--- FAIL ---> Sub_System_File-0999:- " + mi + "*0999" + fileName.substring(22, 29) +
                            " is not found for Rule-304");
                    System.err.println("--- FAIL ---> Sub_System_File-0999:- " + mi + "*0999" + fileName.substring(22, 29) +
                            " is not found for Rule-304");

                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Subsystem_ID"))) {
                    test.pass("**********Pass Statement********* Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                            "is as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------CDIC_Hold_Status_Code------------------------------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("CDIC_Hold_Status_Code"))) {
                    test.pass("**********Pass Statement********* CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") +
                            "is as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") +
                            "is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") +
                            "is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (cdicHoldStatusCodeTable0800 == null) {
                    cdicHoldStatusCodeTable0800 = new DQRules().getListOfString(mi, "0235", "CDIC_Hold_Status_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0235 = new DQRules().getDQFileNames("0235").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0235.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0235:- " + mi + "*0235" + fileName.substring(22, 29) +
                                " is not found for Rule-304");
                        System.err.println("--- FAIL ---> Sub_System_File-0235:- " + mi + "*0235" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0235.size() != 0) {
                    boolean cdicHoldStatusCodeFlag0800 = cdicHoldStatusCodeTable0800.contains(dqRecord.get("CDIC_Hold_Status_Code"));
                    //                Verifying value in table Id - 0235
                    if (cdicHoldStatusCodeFlag0800) {
                        test.pass("**********Pass Statement********* CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") + " is as per Rule-10");
                    } else {
                        test.fail("--- FAIL ---> CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") + " is not as per Rule-10 and not found in file " + subSystemFiles0235);
                        System.err.println("--- FAIL ---> CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") + " is not as per Rule-10 and not found in file " + subSystemFiles0235);
                    }
                } else {
                    test.fail("--- FAIL ---> Sub_System_File-0235:- " + mi + "*0235" + fileName.substring(22, 29) +
                            " is not found for Rule-304");
                    System.err.println("--- FAIL ---> Sub_System_File-0235:- " + mi + "*0235" + fileName.substring(22, 29) +
                            " is not found for Rule-304");
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("CDIC_Hold_Status_Code"))) {
                    test.pass("**********Pass Statement********* CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") +
                            "is as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") +
                            "is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> CDIC_Hold_Status_Code:- " + dqRecord.get("CDIC_Hold_Status_Code") +
                            "is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------Account_Balance------------------------------------------------
                //            Rule-6
                if (rules.amountValidationRule6(dqRecord.get("Account_Balance"), "30", "2")) {
                    test.pass("**********Pass Statement********* Account_Balance:- " + dqRecord.get("Account_Balance") +
                            " is as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Account_Balance:- " + dqRecord.get("Account_Balance") +
                            "is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Account_Balance:- " + dqRecord.get("Account_Balance") +
                            "is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Account_Balance"))) {
                    test.pass("**********Pass Statement********* Account_Balance:- " + dqRecord.get("Account_Balance") +
                            " is as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Account_Balance:- " + dqRecord.get("Account_Balance") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Account_Balance:- " + dqRecord.get("Account_Balance") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------Accessible_Balance------------------------------------------------
                //            Rule-6
                if (rules.amountValidationRule6(dqRecord.get("Accessible_Balance"), "30", "2")) {
                    test.pass("**********Pass Statement********* Accessible_Balance:- " + dqRecord.get("Accessible_Balance") +
                            " is as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Accessible_Balance:- " + dqRecord.get("Accessible_Balance") +
                            "is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Accessible_Balance:- " + dqRecord.get("Accessible_Balance") +
                            "is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Accessible_Balance"))) {
                    test.pass("**********Pass Statement********* Accessible_Balance:- " + dqRecord.get("Accessible_Balance") +
                            " is as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Accessible_Balance:- " + dqRecord.get("Accessible_Balance") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Accessible_Balance:- " + dqRecord.get("Accessible_Balance") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------CDIC_Hold_Amount------------------------------------------------
                //            Rule-6
                if (rules.amountValidationRule6(dqRecord.get("CDIC_Hold_Amount"), "30", "2")) {
                    test.pass("**********Pass Statement********* CDIC_Hold_Amount:- " + dqRecord.get("CDIC_Hold_Amount") +
                            " is as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> CDIC_Hold_Amount:- " + dqRecord.get("CDIC_Hold_Amount") +
                            " is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> CDIC_Hold_Amount:- " + dqRecord.get("CDIC_Hold_Amount") +
                            " is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("CDIC_Hold_Amount"))) {
                    test.pass("**********Pass Statement********* CDIC_Hold_Amount:- " + dqRecord.get("CDIC_Hold_Amount") +
                            " is as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> CDIC_Hold_Amount:- " + dqRecord.get("CDIC_Hold_Amount") +
                            "is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> CDIC_Hold_Amount:- " + dqRecord.get("CDIC_Hold_Amount") +
                            "is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------Currency_Code------------------------------------------------
                //            Rule-1

                if (rules.onlyNumericCharactersRule1(dqRecord.get("Currency_Code"))) {
                    test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") +
                            " is as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            "is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            "is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (currencyCodeTable0800 == null) {
                    currencyCodeTable0800 = new DQRules().getListOfString(mi, "0233", "Currency_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0233 = new DQRules().getDQFileNames("0233").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0233.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0233:- " + mi + "*0233" + fileName.substring(22, 29) +
                                " is not found for Rule-304");
                        System.err.println("--- FAIL ---> Sub_System_File-0233:- " + mi + "*0233" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0233.size() != 0) {
                    boolean currencyCodeFlag800 = currencyCodeTable0800.contains(dqRecord.get("Currency_Code"));
                    //                Verifying value in table Id - 0900
                    if (currencyCodeFlag800) {
                        test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") + " is as per Rule-10");
                    } else {
                        test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") + " is not as per Rule-10 and not found in file " + subSystemFiles0233);
                        System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") + " is not as per Rule-10 and not found in file " + subSystemFiles0233);
                    }

                } else {
                    test.fail("--- FAIL ---> Sub_System_File-0233:- " + mi + "*0233" + fileName.substring(22, 29) +
                            " is not found for Rule-304");
                    System.err.println("--- FAIL ---> Sub_System_File-0233:- " + mi + "*0233" + fileName.substring(22, 29) +
                            " is not found for Rule-304");
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Currency_Code"))) {
                    test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") +
                            " is as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            "is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            "is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }
                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    @Then("^validate (\\d+)-Account Accrued Interest Data$")
    public void validateAccountAccruedInterestData(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        List<String> fileValues = new DQRules().getDQFileNames(tableId);
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> accountUniqueIdTableRule90900 = null;
            List<String> accountUniqueIdDuplicates = null;
            List<String> subSystemIdTable0900 = null;
            List<String> subSystemFiles0999 = null;
            List<String> currencyCodeTable0900 = null;
            List<String> subSystemFiles0233 = null;
            for (Map<String, String> dqRecord : dqRecords) {

                // ----------------------------- Account_Unique_ID-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Account_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is as per Rule-2");

                } else {
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is not as per Rule-2");
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is not as per Rule-2");
                }

                //            Rule-9
                // Rule:- 9 New code
                if (accountUniqueIdTableRule90900 == null) {
                    accountUniqueIdTableRule90900 = new DQRules().getListOfString(mi, "0900", "Account_Unique_ID", fileName.substring(26, 29), "DQ");
                    accountUniqueIdDuplicates =
                            accountUniqueIdTableRule90900.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(accountUniqueIdDuplicates.contains(dqRecord.get("Account_Unique_ID")))
                        || dqRecord.get("Account_Unique_ID").isEmpty()) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-9");
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Account_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is as per Rule-18");

                } else {
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is not as per Rule-18");
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is not as per Rule-18");
                }

                // ----------------------------- Subsystem_ID-----------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Subsystem_ID"))) {
                    test.pass("**********Pass Statement********* Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                            " is as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                            "is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                            "is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (subSystemIdTable0900 == null) {
                    subSystemIdTable0900 = new DQRules().getListOfString(mi, "0999", "Subsystem_ID", fileName.substring(26, 29), "DQ");
                    subSystemFiles0999 = new DQRules().getDQFileNames("0999").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0999.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0999:- " + mi + "*0999" + fileName.substring(22, 29) +
                                " is not found for Rule-304");
                        System.err.println("--- FAIL ---> Sub_System_File-0999:- " + mi + "*0999" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0999.size() != 0) {
                    boolean subSystemIdFlag900 = subSystemIdTable0900.contains(dqRecord.get("Subsystem_ID"));
                    if (subSystemIdFlag900) {
                        test.pass("**********Pass Statement********* Subsystem_ID:- " + dqRecord.get("Subsystem_ID") + " is as per Rule-10");
                    } else {
                        test.fail("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0999);

                        System.err.println("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0999);
                    }
                } else {
                    test.fail("--- FAIL ---> Sub_System_File-0999:- " + mi + "*0999" + fileName.substring(22, 29) +
                            " is not found for Rule-304");
                    System.err.println("--- FAIL ---> Sub_System_File-0999:- " + mi + "*0999" + fileName.substring(22, 29) +
                            " is not found for Rule-304");
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Subsystem_ID"))) {
                    test.pass("**********Pass Statement********* Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                            " is as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                // ----------------------------- Last_Interest_Payment_Date-----------------------------
                if (rules.birthDateValidationRule4(dqRecord.get("Last_Interest_Payment_Date"))) {
                    test.pass("**********Pass Statement********* Last_Interest_Payment_Date:- " + dqRecord.get("Last_Interest_Payment_Date") +
                            " is as per Rule-4 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Last_Interest_Payment_Date:- " + dqRecord.get("Last_Interest_Payment_Date") +
                            "is not as per Rule-4 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Last_Interest_Payment_Date:- " + dqRecord.get("Last_Interest_Payment_Date") +
                            "is not as per Rule-4 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------Interest_Accrued_Amount------------------------------------------------
                //            Rule-6
                if (rules.amountValidationRule6(dqRecord.get("Interest_Accrued_Amount"), "30", "4")) {
                    test.pass("**********Pass Statement********* Interest_Accrued_Amount:- " + dqRecord.get("Interest_Accrued_Amount") +
                            " is as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Interest_Accrued_Amount:- " + dqRecord.get("Interest_Accrued_Amount") +
                            "is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Interest_Accrued_Amount:- " + dqRecord.get("Interest_Accrued_Amount") +
                            "is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Interest_Accrued_Amount"))) {
                    test.pass("**********Pass Statement********* Interest_Accrued_Amount:- " + dqRecord.get("Interest_Accrued_Amount") +
                            " is as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Interest_Accrued_Amount:- " + dqRecord.get("Interest_Accrued_Amount") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Interest_Accrued_Amount:- " + dqRecord.get("Interest_Accrued_Amount") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------Currency_Code------------------------------------------------
                //            Rule-1

                if (rules.onlyNumericCharactersRule1(dqRecord.get("Currency_Code"))) {
                    test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") +
                            " is as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            "is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            "is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (currencyCodeTable0900 == null) {
                    currencyCodeTable0900 = new DQRules().getListOfString(mi, "0233", "Currency_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0233 = new DQRules().getDQFileNames("0233").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0233.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0233:- " + mi + "*0233" + fileName.substring(22, 29) +
                                " is not found for Rule-304");
                        System.err.println("--- FAIL ---> Sub_System_File-0233:- " + mi + "*0233" + fileName.substring(22, 29) +
                                " is not found for Rule-304");
                    }
                }
                if (subSystemFiles0999.size() != 0) {
                    boolean currencyCodeFlag800 = currencyCodeTable0900.contains(dqRecord.get("Currency_Code"));
                    //                Verifying value in table Id - 0900
                    if (currencyCodeFlag800) {
                        test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") + " is as per Rule-10");
                    } else {
                        test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") + " is not as per Rule-10 and not found in file " + subSystemFiles0233);
                        System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") + " is not as per Rule-10 and not found in file " + subSystemFiles0233);
                    }
                } else {
                    test.fail("--- FAIL ---> Sub_System_File-0233:- " + mi + "*0233" + fileName.substring(22, 29) +
                            " is not found for Rule-304");
                    System.err.println("--- FAIL ---> Sub_System_File-0233:- " + mi + "*0233" + fileName.substring(22, 29) +
                            " is not found for Rule-304");
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Currency_Code"))) {
                    test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") +
                            " is as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            "is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            "is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    @Then("^validate (\\d+)-Subsystem$")
    public void validateSubsystem(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> subSystemIdRule90999 = null;
            List<String> subSystemIdDuplicates = null;
            for (Map<String, String> dqRecord : dqRecords) {
                // ----------------------------- Subsystem_ID-----------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Subsystem_ID"))) {
                    test.pass("**********Pass Statement********* Subsystem_ID:- " + dqRecord.get("Subsystem_ID") + "is as per Rule-1");
                } else {
                    test.fail("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                            "is not as per Rule-1");
                    System.err.println("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                            "is not as per Rule-1");
                }

                //            Rule-9
                // Rule:- 9 New code
                if (subSystemIdRule90999 == null) {
                    subSystemIdRule90999 = new DQRules().getListOfString(mi, "0999", "Subsystem_ID", fileName.substring(26, 29), "DQ");
                    subSystemIdDuplicates =
                            subSystemIdRule90999.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(subSystemIdDuplicates.contains(dqRecord.get("Subsystem_ID")))
                        || dqRecord.get("Subsystem_ID").isEmpty()) {
                    test.pass("**********Pass Statement********* Subsystem_ID:- " + dqRecord.get("Subsystem_ID") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") + " is not as per Rule-9");
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Subsystem_ID"))) {
                    test.pass("**********Pass Statement********* Subsystem_ID:- " + dqRecord.get("Subsystem_ID") + "is as per Rule-18");
                } else {
                    test.fail("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                            " is not as per Rule-18");
                    System.err.println("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                            " is not as per Rule-18");
                }

                // ----------------------------- MI_Subsystem_Code-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("MI_Subsystem_Code"))) {
                    test.pass("**********Pass Statement********* MI_Subsystem_Code:- " + dqRecord.get("MI_Subsystem_Code") +
                            " is as per Rule-2 for Subsystem_ID:- " + dqRecord.get("Subsystem_ID"));
                } else {
                    test.fail("--- FAIL ---> MI_Subsystem_Code:- " + dqRecord.get("MI_Subsystem_Code") + "is not as per Rule-2" +
                            " for Subsystem_ID:- " + dqRecord.get("Subsystem_ID"));
                    System.err.println("--- FAIL ---> MI_Subsystem_Code:- " + dqRecord.get("MI_Subsystem_Code") + "is not as per Rule-2" +
                            " for Subsystem_ID:- " + dqRecord.get("Subsystem_ID"));
                }

                // -----------------------------Description-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") +
                            " is as per Rule-2 for Subsystem_ID:- " + dqRecord.get("Subsystem_ID"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Subsystem_ID:- " + dqRecord.get("Subsystem_ID"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for Subsystem_ID:- " + dqRecord.get("Subsystem_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") +
                            " is as per Rule-18 for Subsystem_ID:- " + dqRecord.get("Subsystem_ID"));
                } else {
                    test.fail(
                            "--- FAIL ---> Description:- " + dqRecord.get("Description") +
                                    " is not as per Rule-18 for Subsystem_ID:- " + dqRecord.get("Subsystem_ID"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for Subsystem_ID:- " + dqRecord.get("Subsystem_ID"));
                }

                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }


    @Then("^validate (\\d+)-MI Deposit Hold Data$")
    public void validateMIDepositHoldData(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        List<String> fileValues = new DQRules().getDQFileNames(tableId);
        String headers = null;
        List<String> results = new LinkedList<>();

        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> accountUniqueIdTable0130 = null;
            List<String> subSystemFiles0130 = null;
            List<String> miDepositHoldCodeTable0241 = null;
            List<String> subSystemFiles0241 = null;
            List<String> currencyCodeTable0233 = null;
            List<String> subSystemFiles0233 = null;
            for (Map<String, String> dqRecord : dqRecords) {

                //-------------------------------------------Account_Unique_ID------------------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Account_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is as per Rule-2");

                } else {
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is not as per Rule-2");
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is not as per Rule-2");
                }


                //Rule:- 10 - Values present the file should be present in other file, Which primary key
                //                for other file where as foreign key for current file verification
                //   Rule-10
                if (accountUniqueIdTable0130 == null) {
                    accountUniqueIdTable0130 = new DQRules().getListOfString(mi, "0130", "Account_Unique_ID", fileName.substring(26, 29), "DQ");
                    subSystemFiles0130 = new DQRules().getDQFileNames("0130").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0130.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0130:- " + mi + "*0130" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0130.size() != 0) {
                    boolean accountUniqueIdFlag130 = accountUniqueIdTable0130.contains(dqRecord.get("Account_Unique_ID"));
                    if (accountUniqueIdFlag130) {
                        test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") +
                                " is as per Rule-10");

                    } else {
                        test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0130);
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0130);
                    }

                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Account_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is as per Rule-18");

                } else {
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-18");
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + " is not as per Rule-18");
                }

                // ----------------------------- MI_Deposit_Hold_Code-----------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("MI_Deposit_Hold_Code"))) {
                    test.pass("**********Pass Statement********* MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code") + " is as per Rule-1" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code") +
                            "is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code") +
                            "is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //Rule:- 10 - Values present the file should be present in other file, Which primary key
                //                for other file where as foreign key for current file verification
                //   Rule-10
                if (miDepositHoldCodeTable0241 == null) {
                    miDepositHoldCodeTable0241 = new DQRules().getListOfString(mi, "0241", "MI_Deposit_Hold_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0241 = new DQRules().getDQFileNames("0241").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0241.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0241:- " + mi + "*0241" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0241.size() != 0) {
                    boolean miDepositHoldCodeFlag241 = miDepositHoldCodeTable0241.contains(dqRecord.get("MI_Deposit_Hold_Code"));
                    if (miDepositHoldCodeFlag241) {
                        test.pass("**********Pass Statement********* MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code") + " is as per Rule-10" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0241 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0241 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    }
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("MI_Deposit_Hold_Code"))) {
                    test.pass("**********Pass Statement********* MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code") +
                            " is not as per Rule-18  for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code") +
                            " is not as per Rule-18  for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------MI_Deposit_Hold_Scheduled_Release_Date------------------------------------------------
//                Rule:- 4
                if (rules.birthDateValidationRule4(dqRecord.get("MI_Deposit_Hold_Scheduled_Release_Date"))) {
                    test.pass("**********Pass Statement********* MI_Deposit_Hold_Scheduled_Release_Date:- " + dqRecord.get("MI_Deposit_Hold_Scheduled_Release_Date") + " is as per Rule-4" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> MI_Deposit_Hold_Scheduled_Release_Date:- " + dqRecord.get("MI_Deposit_Hold_Scheduled_Release_Date") +
                            "is not as per Rule-4 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> MI_Deposit_Hold_Scheduled_Release_Date:- " + dqRecord.get("MI_Deposit_Hold_Scheduled_Release_Date") +
                            "is not as per Rule-4 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //-------------------------------------------Currency_Code------------------------------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Currency_Code"))) {
                    test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") + " is as per Rule-1" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            " is not as per Rule-1 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }


                //Rule:- 10 - Values present the file should be present in other file, Which primary key
                //                for other file where as foreign key for current file verification
                //   Rule-10
                if (currencyCodeTable0233 == null) {
                    currencyCodeTable0233 = new DQRules().getListOfString(mi, "0233", "Currency_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0233 = new DQRules().getDQFileNames("0233").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                    if (subSystemFiles0233.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0233:- " + mi + "*0233" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0233.size() != 0) {
                    boolean currencyCodeFlag233 = currencyCodeTable0233.contains(dqRecord.get("Currency_Code"));
                    if (currencyCodeFlag233) {
                        test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") + " is as per Rule-10" +
                                " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0233 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0233 + " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    }
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Currency_Code"))) {
                    test.pass("**********Pass Statement********* Currency_Code:- " + dqRecord.get("Currency_Code") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> Currency_Code:- " + dqRecord.get("Currency_Code") +
                            " is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //-------------------------------------------MI_Deposit_Hold_Amount------------------------------------------------
                //            Rule-6
                if (rules.amountValidationRule6(dqRecord.get("MI_Deposit_Hold_Amount"), "30", "2")) {
                    test.pass("**********Pass Statement********* MI_Deposit_Hold_Amount:- " + dqRecord.get("MI_Deposit_Hold_Amount") + " is as per Rule-6" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> MI_Deposit_Hold_Amount:- " + dqRecord.get("MI_Deposit_Hold_Amount") +
                            "is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> MI_Deposit_Hold_Amount:- " + dqRecord.get("MI_Deposit_Hold_Amount") +
                            "is not as per Rule-6 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("MI_Deposit_Hold_Amount"))) {
                    test.pass("**********Pass Statement********* MI_Deposit_Hold_Amount:- " + dqRecord.get("MI_Deposit_Hold_Amount") + " is as per Rule-18" +
                            " for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> MI_Deposit_Hold_Amount:- " + dqRecord.get("MI_Deposit_Hold_Amount") +
                            "is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                    System.err.println("--- FAIL ---> MI_Deposit_Hold_Amount:- " + dqRecord.get("MI_Deposit_Hold_Amount") +
                            "is not as per Rule-18 for Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID"));
                }
                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }


    @Then("^validate (\\d+)-MI Deposit Hold Code$")
    public void validateMIDepositHoldCode(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        List<String> fileValues = new DQRules().getDQFileNames(tableId);
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> miDepositHoldCodeTableRule90241 = null;
            List<String> miDepositHoldCodeDuplicates = null;
            for (Map<String, String> dqRecord : dqRecords) {
// ----------------------------- MI_Deposit_Hold_Code-----------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("MI_Deposit_Hold_Code"))) {
                    test.pass("**********Pass Statement********* MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code") + " is as per Rule-1");
                } else {
                    test.fail("--- FAIL ---> MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code") +
                            "is not as per Rule-1");
                    System.err.println("--- FAIL ---> MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code") +
                            "is not as per Rule-1");
                }


                //            Rule-9
                // Rule:- 9 New code
                if (miDepositHoldCodeTableRule90241 == null) {
                    miDepositHoldCodeTableRule90241 = new DQRules().getListOfString(mi, "0241", "MI_Deposit_Hold_Code", fileName.substring(26, 29), "DQ");
                    miDepositHoldCodeDuplicates =
                            miDepositHoldCodeTableRule90241.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(miDepositHoldCodeDuplicates.contains(dqRecord.get("MI_Deposit_Hold_Code")))
                        || dqRecord.get("MI_Deposit_Hold_Code").isEmpty()) {
                    test.pass("**********Pass Statement********* MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code") + " is not as per Rule-9");
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("MI_Deposit_Hold_Code"))) {
                    test.pass("**********Pass Statement********* MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code") + " is as per Rule-18");
                } else {
                    test.fail("--- FAIL ---> MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code") +
                            " is not as per Rule-18");
                    System.err.println("--- FAIL ---> MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code") +
                            " is not as per Rule-18");
                }

                //-------------------------------------------MI_Deposit_Hold_Type------------------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("MI_Deposit_Hold_Type"))) {
                    test.pass("**********Pass Statement********* MI_Deposit_Hold_Type:- " + dqRecord.get("MI_Deposit_Hold_Type") + " is as per Rule-2" +
                            " for MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code"));
                } else {
                    test.fail("--- FAIL ---> MI_Deposit_Hold_Type:- " + dqRecord.get("MI_Deposit_Hold_Type") + "is not as per Rule-2" +
                            " for MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code"));
                    System.err.println("--- FAIL ---> MI_Deposit_Hold_Type:- " + dqRecord.get("MI_Deposit_Hold_Type") + "is not as per Rule-2" +
                            " for MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code"));
                }

                //------------------------------Description---------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-2" +
                            " for MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-2 for MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Description"))) {
                    test.pass("**********Pass Statement********* Description:- " + dqRecord.get("Description") + " is as per Rule-18" +
                            " for MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code"));
                } else {
                    test.fail("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code"));
                    System.err.println("--- FAIL ---> Description:- " + dqRecord.get("Description") +
                            " is not as per Rule-18 for MI_Deposit_Hold_Code:- " + dqRecord.get("MI_Deposit_Hold_Code"));
                }
                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    @Then("^validate (\\d+)-Address Data$")
    public void validateAddressData(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;

        List<String> fileValues = new DQRules().getDQFileNames(tableId);
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> depositorUniqueIdTable0100 = null;
            List<String> subSystemFiles0100 = null;
            List<String> addressTypeCodeTable0221 = null;
            List<String> subSystemFiles0221 = null;
            for (Map<String, String> dqRecord : dqRecords) {
//        Depositor_Unique_ID
//Rule:- 2 - It can have every character including french, It is free text field
//Rule:- 9 - It is a primary key and there should not be any duplicates and It is a mandatory field
//Rule:- 18 - It should not be blank
                //------------------------------Depositor_Unique_ID---------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Depositor_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") + " is as per Rule-2");

                } else {
                    test.fail(
                            "--- FAIL ---> Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + " is not as per Rule-2");
                    System.err.println("--- FAIL ---> Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + " is not as per Rule-2");
                }

                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (depositorUniqueIdTable0100 == null) {
                    depositorUniqueIdTable0100 = new DQRules().getListOfString(mi, "0100", "Depositor_Unique_ID", fileName.substring(26, 29), "DQ");
                    subSystemFiles0100 = new DQRules().getDQFileNames("0100").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).collect(Collectors.toList());
                    if (subSystemFiles0100.size() == 0) {
                        test.fail("--- FAIL ---> Sub_System_File-0100:- " + mi + "*0100" + fileName.substring(22, 29) +
                                " is not found for Rule-304");

                    }
                }
                if (subSystemFiles0100.size() != 0) {
                    boolean depositorUniqueIdFlag = depositorUniqueIdTable0100.contains(dqRecord.get("Depositor_Unique_ID"));
                    if (depositorUniqueIdFlag) {
                        test.pass("**********Pass Statement********* Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") +
                                " is as per Rule-10");

                    } else {
                        test.fail(
                                "--- FAIL ---> Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") +
                                        " is not as per Rule-10 and not found in file " + subSystemFiles0100);
                        System.err.println("--- FAIL ---> Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0100);
                    }
                } else {
                    test.fail("--- FAIL ---> Sub_System_File-0100:- " + mi + "*0100" + fileName.substring(22, 29) +
                            " is not found for Rule-304");
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Depositor_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + "is as per Rule-18");

                } else {
                    test.fail(
                            "--- FAIL ---> Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") +
                                    " is not as per Rule-18");
                    System.err.println("--- FAIL ---> Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") +
                            " is not as per Rule-18");
                }

                //------------------------------Address_Count---------------------------------------
                //            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Address_Count"))
                        || dqRecord.get("Address_Count").isEmpty()) {
                    test.pass("**********Pass Statement********* Address_Count:- " + dqRecord.get("Address_Count") + " is as per Rule-1" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail(
                            "--- FAIL ---> Address_Count:- " + dqRecord.get("Address_Count") +
                                    "is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Address_Count:- " + dqRecord.get("Address_Count") +
                            "is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Address_Type_Code---------------------------------------

                if (rules.onlyNumericCharactersRule1(dqRecord.get("Address_Type_Code"))) {
                    test.pass("**********Pass Statement********* Address_Type_Code:- " + dqRecord.get("Address_Type_Code") + " is as per Rule-1" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail(
                            "--- FAIL ---> Address_Type_Code:- " + dqRecord.get("Address_Type_Code") +
                                    "is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Address_Type_Code:- " + dqRecord.get("Address_Type_Code") +
                            "is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (addressTypeCodeTable0221 == null) {
                    addressTypeCodeTable0221 = new DQRules().getListOfString(mi, "0221", "Address_Type_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0221 = new DQRules().getDQFileNames("0221").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).collect(Collectors.toList());

                }
                if (subSystemFiles0221.size() != 0) {
                    boolean addressTypeCodeFlag = addressTypeCodeTable0221.contains(dqRecord.get("Address_Type_Code"));
                    if (addressTypeCodeFlag || dqRecord.get("Address_Type_Code").isEmpty()) {
                        test.pass("**********Pass Statement********* Address_Type_Code:- " + dqRecord.get("Address_Type_Code") + " is as per Rule-10" +
                                " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    } else {
                        test.fail(
                                "--- FAIL ---> Address_Type_Code:- " + dqRecord.get("Address_Type_Code") +
                                        " is not as per Rule-10 and not found in file " + subSystemFiles0221 + " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                        System.err.println("--- FAIL ---> Address_Type_Code:- " + dqRecord.get("Address_Type_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0221 + " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    }
                } else {
                    test.fail("--- FAIL ---> Sub_System_File-0221:- " + mi + "*0221" + fileName.substring(22, 29) +
                            " is not found for Rule-304");
                }


                //Rule:- 18 - It should not be blank
                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Address_Type_Code"))) {
                    test.pass("**********Pass Statement********* Address_Type_Code:- " + dqRecord.get("Address_Type_Code") + " is as per Rule-18" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail(
                            "--- FAIL ---> Address_Type_Code:- " + dqRecord.get("Address_Type_Code") +
                                    " is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Address_Type_Code:- " + dqRecord.get("Address_Type_Code") +
                            " is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Primary_Address_Flag---------------------------------------
                //            Rule-3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("Primary_Address_Flag"), 1)) {
                    test.pass("**********Pass Statement********* Primary_Address_Flag:- " + dqRecord.get("Primary_Address_Flag") + " is as per Rule-3" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail(
                            "--- FAIL ---> Primary_Address_Flag:- " + dqRecord.get("Primary_Address_Flag") +
                                    "is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Primary_Address_Flag:- " + dqRecord.get("Primary_Address_Flag") +
                            "is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //Rule:- 13 - Only those characters only should display
//            Specific those characters in pipe delimited 'Y', 'N'
                if (rules.acceptsOnlySpecificCharactersRule13(dqRecord.get("Primary_Address_Flag"), "Y|N|y|n")) {
                    test.pass("**********Pass Statement********* Primary_Address_Flag:- " + dqRecord.get("Primary_Address_Flag") + " is as per Rule-13" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail(
                            "--- FAIL ---> Primary_Address_Flag:- " + dqRecord.get("Primary_Address_Flag") +
                                    "is not as per Rule-13 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));

                    System.err.println("--- FAIL ---> Primary_Address_Flag:- " + dqRecord.get("Primary_Address_Flag") +
                            "is not as per Rule-13 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Primary_Address_Flag"))) {
                    test.pass("**********Pass Statement********* Primary_Address_Flag:- " + dqRecord.get("Primary_Address_Flag") + " is as per Rule-18" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail(
                            "--- FAIL ---> Primary_Address_Flag:- " + dqRecord.get("Primary_Address_Flag") +
                                    " is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Primary_Address_Flag:- " + dqRecord.get("Primary_Address_Flag") +
                            " is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Address_Change---------------------------------------
                //Rule:- 5 - Date format 'yyyyMMdd:HHmmss'
                if (rules.addressChangeValidationRule5(dqRecord.get("Address_Change"))) {
                    test.pass("**********Pass Statement********* Address_Change:- " + dqRecord.get("Address_Change") + " is as per Rule-5" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail(
                            "--- FAIL ---> Address_Change:- " + dqRecord.get("Address_Change") +
                                    "is not as per Rule-5 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Address_Change:- " + dqRecord.get("Address_Change") +
                            "is not as per Rule-5 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Undeliverable_Flag---------------------------------------
                //            Rule-3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("Undeliverable_Flag"), 1)) {
                    test.pass("**********Pass Statement********* Undeliverable_Flag:- " + dqRecord.get("Undeliverable_Flag") + " is as per Rule-3" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail(
                            "--- FAIL ---> Undeliverable_Flag:- " + dqRecord.get("Undeliverable_Flag") +
                                    "is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Undeliverable_Flag:- " + dqRecord.get("Undeliverable_Flag") +
                            "is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //Rule:- 21 - Only those characters only should display
//            Specific those characters in pipe delimited 'Y', 'N' or EMPTY
                if (rules.acceptsOnlySpecificCharactersRule21(dqRecord.get("Undeliverable_Flag"), "Y|N|^$")
                        || dqRecord.get("Undeliverable_Flag").isEmpty()) {
                    test.pass("**********Pass Statement********* Undeliverable_Flag:- " + dqRecord.get("Undeliverable_Flag") + " is as per Rule-21" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Undeliverable_Flag:- " + dqRecord.get("Undeliverable_Flag") +
                            "is not as per Rule-21 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Undeliverable_Flag:- " + dqRecord.get("Undeliverable_Flag") +
                            "is not as per Rule-21 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Address_1---------------------------------------
                //Rule:- 2 - It can have every character including french, It is free text field
                //Rule:- 18 - It should not be blank
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Address_1"))) {
                    test.pass("**********Pass Statement********* Address_1:- " + dqRecord.get("Address_1") + " is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail(
                            "--- FAIL ---> Address_1:- " + dqRecord.get("Address_1") +
                                    "is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Address_1:- " + dqRecord.get("Address_1") +
                            "is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }


                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Address_1"))) {
                    test.pass("**********Pass Statement********* Address_1:- " + dqRecord.get("Address_1") + " is as per Rule-18" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Address_1:- " + dqRecord.get("Address_1") +
                            "is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Address_1:- " + dqRecord.get("Address_1") +
                            "is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Address_2---------------------------------------
//            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Address_2"))) {
                    test.pass("**********Pass Statement********* Address_2:- " + dqRecord.get("Address_2") + " is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail(
                            "--- FAIL ---> Address_2:- " + dqRecord.get("Address_2") +
                                    "is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Address_2:- " + dqRecord.get("Address_2") +
                            "is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }


                //------------------------------City---------------------------------------
                //Rule:- 2 - It can have every character including french, It is free text field
                if (rules.acceptsAllChactersRule2(dqRecord.get("City"))) {
                    test.pass("**********Pass Statement********* City:- " + dqRecord.get("City") + " is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail(
                            "--- FAIL ---> City:- " + dqRecord.get("City") +
                                    "is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> City:- " + dqRecord.get("City") +
                            "is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //Rule:- 18 - It should not be blank
                if (dqRecord.get("Country").equalsIgnoreCase("CANADA") || dqRecord.get("Country").equalsIgnoreCase("USA")) {
                    if (rules.noBlankRule18(dqRecord.get("City"))) {
                        test.pass("**********Pass Statement********* City:- " + dqRecord.get("City") + " is as per Rule-18" +
                                " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> City:- " + dqRecord.get("City") +
                                " is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                        System.err.println("--- FAIL ---> City:- " + dqRecord.get("City") +
                                " is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    }
                }


                //------------------------------Province---------------------------------------
                //Rule:- 2 - It can have every character including french, It is free text field
                if (rules.acceptsAllChactersRule2(dqRecord.get("Province"))) {
                    test.pass("**********Pass Statement********* Province:- " + dqRecord.get("Province") + " is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Province:- " + dqRecord.get("Province") +
                            "is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Province:- " + dqRecord.get("Province") +
                            "is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //Rule:- 18 - It should not be blank
                if (dqRecord.get("Country").equalsIgnoreCase("CANADA") || dqRecord.get("Country").equalsIgnoreCase("USA")) {
                    if (rules.noBlankRule18(dqRecord.get("Province"))) {
                        test.pass("**********Pass Statement********* Province:- " + dqRecord.get("Province") + " is as per Rule-18" +
                                " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    } else {
                        test.fail(
                                "--- FAIL ---> Province:- " + dqRecord.get("Province") +
                                        " is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                        System.err.println("--- FAIL ---> Province:- " + dqRecord.get("Province") +
                                " is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    }

                }


                //------------------------------Postal_Code---------------------------------------
                //Rule:- 2 - It can have every character including french, It is free text field
                if (rules.acceptsAllChactersRule2(dqRecord.get("Postal_Code"))) {
                    test.pass("**********Pass Statement********* Postal_Code:- " + dqRecord.get("Postal_Code") + " is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail(
                            "--- FAIL ---> Postal_Code:- " + dqRecord.get("Postal_Code") +
                                    " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Postal_Code:- " + dqRecord.get("Postal_Code") +
                            " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //Rule:- 18 - It should not be blank
                if (dqRecord.get("Country").equalsIgnoreCase("CANADA") || dqRecord.get("Country").equalsIgnoreCase("USA")) {
                    if (rules.noBlankRule18(dqRecord.get("Postal_Code"))) {
                        test.pass("**********Pass Statement********* Postal_Code:- " + dqRecord.get("Postal_Code") + " is as per Rule-18" +
                                " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    } else {
                        test.fail(
                                "--- FAIL ---> Postal_Code:- " + dqRecord.get("Postal_Code") +
                                        "is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                        System.err.println("--- FAIL ---> Postal_Code:- " + dqRecord.get("Postal_Code") +
                                "is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    }

                }


                //------------------------------Country---------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Country"))) {
                    test.pass("**********Pass Statement********* Country:- " + dqRecord.get("Country") + " is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail(
                            "--- FAIL ---> Country:- " + dqRecord.get("Country") +
                                    " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Country:- " + dqRecord.get("Country") +
                            " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Country"))) {
                    test.pass("**********Pass Statement********* Country:- " + dqRecord.get("Country") + " is as per Rule-18" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail(
                            "--- FAIL ---> Country:- " + dqRecord.get("Country") +
                                    "is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Country:- " + dqRecord.get("Country") +
                            "is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }


                // Rule-27 - Filed 'Country' must be as per Canada Post addressing guidelines/ International destination listing in table 120
                if (rules.countryNamesRule27(dqRecord.get("Country"))) {
                    test.pass("**********Pass Statement********* Country:- " + dqRecord.get("Country") + " is as per Rule-27" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail(
                            "--- FAIL ---> Country:- " + dqRecord.get("Country") +
                                    "is not as per Rule-27 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Country:- " + dqRecord.get("Country") +
                            "is not as per Rule-27 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    @Then("^validate (\\d+)-Depositor Data$")
    public void validateDepositorData(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;

        List<String> fileValues = new DQRules().getDQFileNames(tableId);
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            List<String> depositorTypeCode = null;
            List<String> phoneType = null;
            List<String> subSystemFiles0202 = null;
            List<String> depositorUniqueIdTable0100 = null;
            List<String> depositorUniqueIdDuplicates = null;
            List<String> depositorTypeCodeTable0100 = null;
            List<String> subSystemFiles0201 = null;
            DQRules rules = new DQRules();
            for (Map<String, String> dqRecord : dqRecords) {
//
                //------------------------------Depositor_Unique_ID---------------------------------------
                //Rule:- 2 - It can have every character including french, It is free text field
                //Rule:- 9 - It is a primary key and there should not be any duplicates and It is a mandatory field
                //Rule:- 18 - It should not be blank
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Depositor_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") + "is as per Rule-2");
                } else {
                    test.fail(
                            "--- FAIL ---> Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + "is not as per Rule-2");
                    System.err.println("--- FAIL ---> Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + "is not as per Rule-2");
                }

                //            Rule-9

                if (depositorUniqueIdTable0100 == null) {
                    depositorUniqueIdTable0100 = new DQRules().getListOfString(mi, "0100", "Depositor_Unique_ID", fileName.substring(26, 29), "DQ");
                    depositorUniqueIdDuplicates =
                            depositorUniqueIdTable0100.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(depositorUniqueIdDuplicates.contains(dqRecord.get("Depositor_Unique_ID")))) {
                    test.pass("**********Pass Statement********* Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID") + "is as per Rule-9");
                } else {
                    test.fail(
                            "--- FAIL ---> Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + "is not as per Rule-9");
                    System.err.println("--- FAIL ---> Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + "is not as per Rule-9");
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Depositor_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + "is as per Rule-18");

                } else {
                    test.fail("--- FAIL ---> Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + "is not as per Rule-18");
                    System.err.println("--- FAIL ---> Depositor_Unique_Id:- " + dqRecord.get("Depositor_Unique_ID") + "is not as per Rule-18");
                }

                //------------------------------Depositor_ID_Link---------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Depositor_ID_Link"))) {
                    test.pass("**********Pass Statement********* Depositor_ID_Link:- " + dqRecord.get("Depositor_ID_Link") + "is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Depositor_ID_Link:- " + dqRecord.get("Depositor_ID_Link") +
                            "is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Depositor_ID_Link:- " + dqRecord.get("Depositor_ID_Link") +
                            "is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Depositor_ID_Link"))) {
                    test.pass("**********Pass Statement********* Depositor_ID_Link:- " + dqRecord.get("Depositor_ID_Link") + "is as per Rule-18" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Depositor_ID_Link:- " + dqRecord.get("Depositor_ID_Link") +
                            "is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Depositor_ID_Link:- " + dqRecord.get("Depositor_ID_Link") +
                            "is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Subsystem_ID---------------------------------------
                //Rule:- 1 - It should be integer type 0-9 and it can have prefix + or -
                // and size is greater than 1 and it can be empty string
                // Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Subsystem_ID"))) {
                    test.pass("**********Pass Statement********* Subsystem_ID:- " + dqRecord.get("Subsystem_ID") + "is as per Rule-1" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                            "is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                            "is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification

                //   Rule-10
//                Rule is removed confirmed by shankar - developer
                /*if (rules.verifyForeignKeyInPrimaryKeyFileRule10(mi, "0999", "Subsystem_ID", dqRecord.get("Subsystem_ID"),"DQ")) {

                } else {
                    test.fail("--- FAIL ---> Subsystem_ID:- " + dqRecord.get("Subsystem_ID") +
                            "is not as per Rule-10 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }*/

                //------------------------------Depositor_Branch---------------------------------------
                // Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Depositor_Branch"))) {
                    test.pass("**********Pass Statement********* Depositor_Branch:- " + dqRecord.get("Depositor_Branch") + "is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Depositor_Branch:- " + dqRecord.get("Depositor_Branch") +
                            "is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Depositor_Branch:- " + dqRecord.get("Depositor_Branch") +
                            "is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Depositor_ID---------------------------------------
                //Rule:- 2 - It can have every character including french, It is free text field
//            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Depositor_ID"))) {
                    test.pass("**********Pass Statement********* Depositor_ID:- " + dqRecord.get("Depositor_ID") + "is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Depositor_ID:- " + dqRecord.get("Depositor_ID") +
                            "is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Depositor_ID:- " + dqRecord.get("Depositor_ID") +
                            "is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Name_Prefix---------------------------------------
                //Rule:- 2 - It can have every character including french, It is free text field
                // Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Name_Prefix")) ||
                        dqRecord.get("Name_Prefix").isEmpty()) {
                    test.pass("**********Pass Statement********* Name_Prefix:- " + dqRecord.get("Name_Prefix") + "is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Name_Prefix:- " + dqRecord.get("Name_Prefix") +
                            "is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Name_Prefix:- " + dqRecord.get("Name_Prefix") +
                            "is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Name---------------------------------------
                //Rule:- 2 - It can have every character including french, It is free text field
//            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Name"))) {
                    test.pass("**********Pass Statement********* Name:- " + dqRecord.get("Name") + "is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Name:- " + dqRecord.get("Name") +
                            " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Name:- " + dqRecord.get("Name") +
                            " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //   Rule-18
                if (rules.noBlankRule18(dqRecord.get("Name"))) {
                    test.pass("**********Pass Statement********* Name:- " + dqRecord.get("Name") + "is as per Rule-18" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Name:- " + dqRecord.get("Name") +
                            "is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Name:- " + dqRecord.get("Name") +
                            "is not as per Rule-18 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------First_Name---------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("First_Name"))) {
                    test.pass("**********Pass Statement********* First_Name:- " + dqRecord.get("First_Name") + "is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> First_Name:- " + dqRecord.get("First_Name") +
                            " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Middle_Name---------------------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Middle_Name")) ||
                        dqRecord.get("Middle_Name").isEmpty()) {
                    test.pass("**********Pass Statement********* Middle_Name:- " + dqRecord.get("Middle_Name") + "is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Middle_Name:- " + dqRecord.get("Middle_Name") +
                            " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Middle_Name:- " + dqRecord.get("Middle_Name") +
                            " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Last_Name---------------------------------------
                //Rule:- 2 - It can have every character including french, It is free text field
                // Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Last_Name")) ||
                        dqRecord.get("Last_Name").isEmpty()) {
                    test.pass("**********Pass Statement********* Last_Name:- " + dqRecord.get("Last_Name") + "is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Last_Name:- " + dqRecord.get("Last_Name") +
                            " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Last_Name:- " + dqRecord.get("Last_Name") +
                            " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Name_Suffix---------------------------------------
                if (rules.acceptsAllChactersRule2(dqRecord.get("Name_Suffix")) ||
                        dqRecord.get("Name_Suffix").isEmpty()) {
                    test.pass("**********Pass Statement********* Name_Suffix:- " + dqRecord.get("Name_Suffix") + "is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Name_Suffix:- " + dqRecord.get("Name_Suffix") +
                            " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Name_Suffix:- " + dqRecord.get("Name_Suffix") +
                            " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Birth_Date---------------------------------------
                //Rule:- 4 - Date format 'yyyyMMdd'
//            Rule-4
                if (rules.birthDateValidationRule4(dqRecord.get("Birth_Date"))) {
                    test.pass("**********Pass Statement********* Birth_Date:- " + dqRecord.get("Birth_Date") + "is as per Rule-4" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Birth_Date:- " + dqRecord.get("Birth_Date") +
                            "is not as per Rule-4 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Birth_Date:- " + dqRecord.get("Birth_Date") +
                            "is not as per Rule-4 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Phone_1---------------------------------------
                //Rule:- 2 - It can have every character including french, It is free text field
//            Rule-2
//             Need to confirm blank is allowed or not because in excel it is striked off
                if (rules.acceptsAllChactersRule2(dqRecord.get("Phone_1"))) {
                    test.pass("**********Pass Statement********* Phone_1:- " + dqRecord.get("Phone_1") + " is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Phone_1:- " + dqRecord.get("Phone_1") +
                            " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Phone_1:- " + dqRecord.get("Phone_1") +
                            " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Phone_2---------------------------------------
                //Rule:- 2 - It can have every character including french, It is free text field
//            Rule-2
//             Need to confirm blank is allowed or not because in excel it is striked off
                if (rules.acceptsAllChactersRule2(dqRecord.get("Phone_2"))) {
                    test.pass("**********Pass Statement********* Phone_2:- " + dqRecord.get("Phone_2") + " is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Phone_2:- " + dqRecord.get("Phone_2") +
                            " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Phone_2:- " + dqRecord.get("Phone_2") +
                            " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Email---------------------------------------
                //Rule:- 2 - It can have every character including french, It is free text field
//            Rule-2
//             Need to confirm blank is allowed or not because in excel it is striked off
                if (rules.acceptsAllChactersRule2(dqRecord.get("Email"))
                        || dqRecord.get("Email").isEmpty()) {
                    test.pass("**********Pass Statement********* Email:- " + dqRecord.get("Email") + "is as per Rule-2" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Email:- " + dqRecord.get("Email") +
                            " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Email:- " + dqRecord.get("Email") +
                            " is not as per Rule-2 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Depositor_Type_Code---------------------------------------
                //Rule:- 1 - It should be integer type 0-9 and it can have prefix + or -
                // and size is greater than 1 and it can be empty string
//            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Depositor_Type_Code"))) {
                    test.pass("**********Pass Statement********* Depositor_Type_Code:- " + dqRecord.get("Depositor_Type_Code") + "is as per Rule-1" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Depositor_Type_Code:- " + dqRecord.get("Depositor_Type_Code") +
                            " is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Depositor_Type_Code:- " + dqRecord.get("Depositor_Type_Code") +
                            " is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (depositorTypeCodeTable0100 == null) {
                    depositorTypeCodeTable0100 = new DQRules().getListOfString(mi, "0201", "Depositor_Type_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0201 = new DQRules().getDQFileNames("0201").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).collect(Collectors.toList());
                }
                if (subSystemFiles0201.size() != 0) {
                    boolean depositoryTypeCodeFlag = depositorTypeCodeTable0100.contains(dqRecord.get("Depositor_Type_Code"));
                    if (depositoryTypeCodeFlag) {
                        test.pass("**********Pass Statement********* Depositor_Type_Code:- " + dqRecord.get("Depositor_Type_Code") +
                                " is as per Rule-10");

                    } else {
                        test.fail(
                                "--- FAIL ---> Depositor_Type_Code:- " + dqRecord.get("Depositor_Type_Code") +
                                        " is not as per Rule-10 and not found in file " + subSystemFiles0201);
                        System.err.println("--- FAIL ---> Depositor_Type_Code:- " + dqRecord.get("Depositor_Type_Code") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0201);
                    }
                } else {
                    test.fail("--- FAIL ---> Sub_System_File-0201:- " + mi + "*0201" + fileName.substring(22, 29) +
                            " is not found for Rule-304");
                }

                //------------------------------Depositor_Agent_Flag---------------------------------------
                //Rule:- 3 - It should be character length of character is 1
                // Rule-3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("Depositor_Agent_Flag"), 1)) {
                    test.pass("**********Pass Statement********* Depositor_Agent_Flag:- " + dqRecord.get("Depositor_Agent_Flag") + "is as per Rule-3" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Depositor_Agent_Flag:- " + dqRecord.get("Depositor_Agent_Flag") +
                            " is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Depositor_Agent_Flag:- " + dqRecord.get("Depositor_Agent_Flag") +
                            " is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Language_Flag---------------------------------------
                //Rule:- 3 - It should be specific characters 'E', 'F' or 'O' and length of character is 1
                // Rule-3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("Language_Flag"), 1)) {
                    test.pass("**********Pass Statement********* Language_Flag:- " + dqRecord.get("Language_Flag") + "is as per Rule-3" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Language_Flag:- " + dqRecord.get("Language_Flag") +
                            "is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Language_Flag:- " + dqRecord.get("Language_Flag") +
                            "is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //Rule:- 22 - Only those characters only should display
                //   Specific those characters in pipe delimited
                if (rules.acceptsOnlySpecificCharactersRule22(dqRecord.get("Language_Flag"), "E|F|O")) {
                    test.pass("**********Pass Statement********* Language_Flag:- " + dqRecord.get("Language_Flag") + "is as per Rule-22" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Language_Flag:- " + dqRecord.get("Language_Flag") +
                            "is not as per Rule-22 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Language_Flag:- " + dqRecord.get("Language_Flag") +
                            "is not as per Rule-22 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Employee_Flag---------------------------------------
                //Rule:- 3 - It should be character length of character is 1
                //Rule-3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("Employee_Flag"), 1)) {
                    test.pass("**********Pass Statement********* Employee_Flag:- " + dqRecord.get("Employee_Flag") + "is as per Rule-3" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Employee_Flag:- " + dqRecord.get("Employee_Flag") +
                            "is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Employee_Flag:- " + dqRecord.get("Employee_Flag") +
                            "is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Phone_1_Type---------------------------------------
                //Rule:- 1 - It should be integer type 0-9 and it can have prefix + or -
                // and size is greater than 1 and it can be empty string
//            Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Phone_1_Type"))) {
                    test.pass("**********Pass Statement********* Phone_1_Type:- " + dqRecord.get("Phone_1_Type") + "is as per Rule-1" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Phone_1_Type:- " + dqRecord.get("Phone_1_Type") +
                            "is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Phone_1_Type:- " + dqRecord.get("Phone_1_Type") +
                            "is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }


                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (phoneType == null) {
                    phoneType = new DQRules().getListOfString(mi, "0202", "Phone_Type_Code", fileName.substring(26, 29), "DQ");
                    subSystemFiles0202 = new DQRules().getDQFileNames("0202").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).collect(Collectors.toList());
                }
                if (subSystemFiles0202.size() != 0) {
                    boolean phoneOneTypeFlag = phoneType.contains(dqRecord.get("Phone_1_Type"));
                    if (phoneOneTypeFlag) {
                        test.pass("**********Pass Statement********* Phone_1_Type:- " + dqRecord.get("Phone_1_Type") +
                                " is as per Rule-10");

                    } else {
                        test.fail(
                                "--- FAIL ---> Phone_1_Type:- " + dqRecord.get("Phone_1_Type") +
                                        " is not as per Rule-10 and not found in file " + subSystemFiles0202);
                        System.err.println("--- FAIL ---> Phone_1_Type:- " + dqRecord.get("Phone_1_Type") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0202);
                    }
                } else {
                    test.fail("--- FAIL ---> Sub_System_File-0202:- " + mi + "*0202" + fileName.substring(22, 29) +
                            " is not found for Rule-304");
                }


                //------------------------------Phone_2_Type---------------------------------------
                //Rule:- 1 - It should be integer type 0-9 and it can have prefix + or -
                // and size is greater than 1 and it can be empty string
                //  Rule-1
                if (rules.onlyNumericCharactersRule1(dqRecord.get("Phone_2_Type"))) {
                    test.pass("**********Pass Statement********* Phone_2_Type:- " + dqRecord.get("Phone_2_Type") + "is as per Rule-1" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Phone_2_Type:- " + dqRecord.get("Phone_2_Type") +
                            "is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Phone_2_Type:- " + dqRecord.get("Phone_2_Type") +
                            "is not as per Rule-1 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                //   Rule-10
                if (subSystemFiles0202.size() != 0) {
                    boolean phoneTwoTypeFlag = phoneType.contains(dqRecord.get("Phone_2_Type"));
                    if (phoneTwoTypeFlag) {
                        test.pass("**********Pass Statement********* Phone_2_Type:- " + dqRecord.get("Phone_2_Type") +
                                " is as per Rule-10");

                    } else {
                        test.fail(
                                "--- FAIL ---> Phone_2_Type:- " + dqRecord.get("Phone_2_Type") +
                                        " is not as per Rule-10 and not found in file " + subSystemFiles0202);
                        System.err.println("--- FAIL ---> Phone_2_Type:- " + dqRecord.get("Phone_2_Type") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0202);
                    }
                } else {
                    test.fail("--- FAIL ---> Sub_System_File-0202:- " + mi + "*0202" + fileName.substring(22, 29) +
                            " is not found for Rule-304");
                }

                //------------------------------MI_Responsible_Party_Flag---------------------------------------
                //Rule:- 3 - It should be character length of character is 1
                // Rule-3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("MI_Responsible_Party_Flag"), 1)) {
                    test.pass("**********Pass Statement********* MI_Responsible_Party_Flag:- " + dqRecord.get("MI_Responsible_Party_Flag") + "is as per Rule-3" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> MI_Responsible_Party_Flag:- " + dqRecord.get("MI_Responsible_Party_Flag") +
                            "is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> MI_Responsible_Party_Flag:- " + dqRecord.get("MI_Responsible_Party_Flag") +
                            "is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //Rule:- 13 - Only those characters only should display
                // Specific those characters in pipe delimited 'Y', 'N'
                if (rules.acceptsOnlySpecificCharactersRule13(dqRecord.get("MI_Responsible_Party_Flag"), "Y|N")) {
                    test.pass("**********Pass Statement********* MI_Responsible_Party_Flag:- " + dqRecord.get("MI_Responsible_Party_Flag") + "is as per Rule-13" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> MI_Responsible_Party_Flag:- " + dqRecord.get("MI_Responsible_Party_Flag") +
                            "is not as per Rule-13 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> MI_Responsible_Party_Flag:- " + dqRecord.get("MI_Responsible_Party_Flag") +
                            "is not as per Rule-13 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

                //------------------------------Non_Resident_Country_Code---------------------------------------
                //Rule:- 3 - It should be character length of character is 1
//            Rule-3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("Non_Resident_Country_Code"), 3)) {
                    test.pass("**********Pass Statement********* Non_Resident_Country_Code:- " + dqRecord.get("Non_Resident_Country_Code") + "is as per Rule-3" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Non_Resident_Country_Code:- " + dqRecord.get("Non_Resident_Country_Code") +
                            "is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Non_Resident_Country_Code:- " + dqRecord.get("Non_Resident_Country_Code") +
                            "is not as per Rule-3 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }

//            Rule-25 - IF not EMPTY, must be CRA Country code
                if (dqRecord.get("Non_Resident_Country_Code").isEmpty()) {
                    test.pass("**********Pass Statement********* Non_Resident_Country_Code is  EMPTY i.e \"\" which is as expected ");
                } else if (rules.craCountryCodesRule25(dqRecord.get("Non_Resident_Country_Code"))) {
                    test.pass("**********Pass Statement********* Non_Resident_Country_Code:- " + dqRecord.get("Non_Resident_Country_Code") + "is as per Rule-25" +
                            " for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                } else {
                    test.fail("--- FAIL ---> Non_Resident_Country_Code:- " + dqRecord.get("Non_Resident_Country_Code") +
                            "is not as per Rule-25 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                    System.err.println("--- FAIL ---> Non_Resident_Country_Code:- " + dqRecord.get("Non_Resident_Country_Code") +
                            "is not as per Rule-25 for Depositor_Unique_ID:- " + dqRecord.get("Depositor_Unique_ID"));
                }
                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }

        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }


    @Then("^validate (\\d+)-Ledger and Sub-Ledger Balances$")
    public void validateLedgerAndSubLedgerBalances(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_dq").mkdir();
        }
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;
        List<String> fileValues = new DQRules().getDQFileNames(tableId);
        String headers = null;
        List<String> results = new LinkedList<>();
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> ledgerAccountTableRule90600 = null;
            List<String> ledgerAccountDuplicates = null;
            for (Map<String, String> dqRecord : dqRecords) {
// ----------------------------- Ledger_Account-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Ledger_Account"))) {
                    test.pass("**********Pass Statement********* Ledger_Account:- " + dqRecord.get("Ledger_Account") +
                            "is as per Rule-2");
                } else {
                    test.fail("--- FAIL ---> Ledger_Account:- " + dqRecord.get("Ledger_Account") + "is not as per Rule-2");
                    System.err.println("--- FAIL ---> Ledger_Account:- " + dqRecord.get("Ledger_Account") + "is not as per Rule-2");
                }

                //            Rule-9
                // Rule:- 9 New code
                if (ledgerAccountTableRule90600 == null) {
                    ledgerAccountTableRule90600 = new DQRules().getListOfString(mi, "0600", "Ledger_Account", fileName.substring(26, 29), "DQ");
                    ledgerAccountDuplicates =
                            ledgerAccountTableRule90600.stream().collect(Collectors.groupingBy(Function.identity()))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue().size() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
                }

                if (!(ledgerAccountDuplicates.contains(dqRecord.get("Ledger_Account")))
                        || dqRecord.get("Ledger_Account").isEmpty()) {
                    test.pass("**********Pass Statement********* Ledger_Account:- " + dqRecord.get("Ledger_Account") + " is as per Rule-9");
                } else {
                    test.fail("--- FAIL ---> Ledger_Account:- " + dqRecord.get("Ledger_Account") + " is not as per Rule-9");
                    System.err.println("--- FAIL ---> Ledger_Account:- " + dqRecord.get("Ledger_Account") + " is not as per Rule-9");
                }

                // ----------------------------- Ledger_Description-----------------------------

                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Ledger_Description"))) {
                    test.pass("**********Pass Statement********* Ledger_Description:- " + dqRecord.get("Ledger_Description") + "is as per Rule-2" +
                            " for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                } else {
                    test.fail("--- FAIL ---> Ledger_Description:- " + dqRecord.get("Ledger_Description") + "is not as per Rule-2" +
                            " for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                    System.err.println("--- FAIL ---> Ledger_Description:- " + dqRecord.get("Ledger_Description") + "is not as per Rule-2" +
                            " for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                }

                // ----------------------------- Ledger_Flag-----------------------------
                //Rule:- 3 - It should be character length of character is 3
                //            Rule-3
                if (rules.acceptsAllChactersWithSpecificLengthRule3(dqRecord.get("Ledger_Flag"), 2)) {
                    test.pass("**********Pass Statement********* Ledger_Flag:- " + dqRecord.get("Ledger_Flag") + "is as per Rule-3" +
                            " for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                } else {
                    test.fail("--- FAIL ---> Ledger_Flag:- " + dqRecord.get("Ledger_Flag") +
                            " is not as per Rule-3 for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                    System.err.println("--- FAIL ---> Ledger_Flag:- " + dqRecord.get("Ledger_Flag") +
                            " is not as per Rule-3 for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                }
                //Rule:- 15
                if (rules.acceptsOnlySpecificCharactersRule15(dqRecord.get("Ledger_Flag"), "GL|SL")) {
                    test.pass("**********Pass Statement********* Ledger_Flag:- " + dqRecord.get("Ledger_Flag") + "is as per Rule-15" +
                            " for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                } else {
                    test.fail(
                            "--- FAIL ---> Ledger_Flag:- " + dqRecord.get("Ledger_Flag") +
                                    "is not as per Rule-15 for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                    System.err.println("--- FAIL ---> Ledger_Flag:- " + dqRecord.get("Ledger_Flag") +
                            "is not as per Rule-15 for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                }

                // ----------------------------- GL_Account-----------------------------

                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("GL_Account"))) {
                    test.pass("**********Pass Statement********* GL_Account:- " + dqRecord.get("GL_Account") + "is as per Rule-2" +
                            " for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                } else {
                    test.fail("--- FAIL ---> GL_Account:- " + dqRecord.get("GL_Account") + "is not as per Rule-2" +
                            " for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                    System.err.println("--- FAIL ---> GL_Account:- " + dqRecord.get("GL_Account") + "is not as per Rule-2" +
                            " for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                }

                // ----------------------------- Debit-----------------------------
                //            Rule-6
                if (rules.amountValidationRule6(dqRecord.get("Debit"), "30", "2")) {
                    test.pass("**********Pass Statement********* Debit:- " + dqRecord.get("Debit") + "is as per Rule-6" +
                            " for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                } else {
                    test.fail("--- FAIL ---> Debit:- " + dqRecord.get("Debit") +
                            "is not as per Rule-6 for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                    System.err.println("--- FAIL ---> Debit:- " + dqRecord.get("Debit") +
                            "is not as per Rule-6 for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                }

                // ----------------------------- Credit-----------------------------
                //            Rule-6
                if (rules.amountValidationRule6(dqRecord.get("Credit"), "30", "2")) {
                    test.pass("**********Pass Statement********* Credit:- " + dqRecord.get("Credit") + "is as per Rule-6" +
                            " for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                } else {
                    test.fail("--- FAIL ---> Credit:- " + dqRecord.get("Credit") +
                            "is not as per Rule-6 for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                    System.err.println("--- FAIL ---> Credit:- " + dqRecord.get("Credit") +
                            "is not as per Rule-6 for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                }

                // ----------------------------- Account_Unique_ID-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Account_Unique_ID"))) {
                    test.pass("**********Pass Statement********* Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is as per Rule-2"
                            + " for Ledger_Account:- " + dqRecord.get("Ledger_Account"));

                } else {
                    test.fail("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is not as per Rule-2" +
                            " for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                    System.err.println("--- FAIL ---> Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID") + "is not as per Rule-2" +
                            " for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                }

                // ----------------------------- Account_Number-----------------------------
                //            Rule-2
                if (rules.acceptsAllChactersRule2(dqRecord.get("Account_Number"))) {
                    test.pass("**********Pass Statement********* Account_Number:- " + dqRecord.get("Account_Number") + "is as per Rule-2" +
                            " for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                } else {
                    test.fail("--- FAIL ---> Account_Number:- " + dqRecord.get("Account_Number") + "is not as per Rule-2" +
                            " for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                    System.err.println("--- FAIL ---> Account_Number:- " + dqRecord.get("Account_Number") + "is not as per Rule-2" +
                            " for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                }

                // ----------------------------- Account_Balance-----------------------------
                //            Rule-6
                if (rules.amountValidationRule6(dqRecord.get("Account_Balance"), "30", "2")) {
                    test.pass("**********Pass Statement********* Account_Balance:- " + dqRecord.get("Account_Balance") + "is as per Rule-6" +
                            " for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                } else {
                    test.fail("--- FAIL ---> Account_Balance:- " + dqRecord.get("Account_Balance") +
                            "is not as per Rule-6 for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                    System.err.println("--- FAIL ---> Account_Balance:- " + dqRecord.get("Account_Balance") +
                            "is not as per Rule-6 for Ledger_Account:- " + dqRecord.get("Ledger_Account"));
                }
                if (failedMessages.size() != 0) {
                    String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                    if (!failedMessage.isEmpty()) {
                        dqRecord.put("Result", failedMessage);
                        dqRecord.put("FileName", fileName);
                        failedMessages = null;
                        failedMessages = new LinkedList<>();
                    }
                } else {
                    dqRecord.put("Result", "**PASS**");
                    dqRecord.put("FileName", fileName);
                }


            }
            if (headers == null && dqRecords.size() > 0) {
                headers = dqRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : dqRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_dq.txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_dq/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    @Then("^validate (\\d+) table \"([^\"]*)\" and \"([^\"]*)\"$")
    public void validateTableAnd(String tableIdValue, String tableValue1, String tableValue2) throws Throwable {
        String tableId = String.valueOf(tableIdValue);
        String columnName1 = getTableValue1();
        String columnName2 = getTableValue2();

        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        boolean identificationFlag = false;
        for (String fileName : fileNames) {
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            int tableValue1Count = dqRecords.stream().filter(x -> x.get(columnName1).equals(tableValue1)).collect(Collectors.toList()).size();
            int tableValue2Count = dqRecords.stream().filter(x -> x.get(columnName2).replace("–", "-").equals(tableValue2)).collect(Collectors.toList()).size();
            if (tableValue1Count == 1) {
                test.pass("**********Pass Statement********* " + columnName1 + " :- " + tableValue1 + " has only 1 record which" +
                        "is as expected");
            } else {
                test.fail("--- FAIL --->  " + columnName1 + ":- " + tableValue1 +
                        " has " + tableValue1Count + " records instead of 1 record");
                System.err.println("--- FAIL --->  " + columnName1 + ":- " + tableValue1 +
                        " has " + tableValue1Count + " records instead of 1 record");
            }
            if (tableValue2Count == 1) {
                test.pass("**********Pass Statement********* " + columnName2 + " :- " + tableValue2 + " has only 1 record which" +
                        "is as expected");
            } else {
                test.fail(
                        "--- FAIL --->  " + columnName2 + ":- " + tableValue2 +
                                " has " + tableValue2Count + " records instead of 1 record");
                System.err.println("--- FAIL --->  " + columnName2 + ":- " + tableValue2 +
                        " has " + tableValue2Count + " records instead of 1 record");
            }
            for (Map<String, String> dqRecord : dqRecords) {
                if (dqRecord.get(columnName1).equals(tableValue1)) {
                    if (dqRecord.get(columnName2).replace("–", "-").equals(tableValue2)) {
                        test.pass("**********Pass Statement********* " + columnName1 + " :- " + tableValue1 + " and "
                                + columnName2 + " :- " + tableValue2 + " values are present in the file:- " + fileName + " which is as expected");
                        identificationFlag = true;
                        break;
                    } else {
                        test.fail("--- FAIL ---> " + columnName2 + ":- " + tableValue2 +
                                " is not as expected for " + columnName1 + ":- " + tableValue1);
                        System.err.println("--- FAIL ---> " + columnName2 + ":- " + tableValue2 +
                                " is not as expected for " + columnName1 + ":- " + tableValue1);
                    }
                }
            }
            if (identificationFlag == false) {
                test.fail("--- FAIL ---> " + columnName1 + ":- " + tableValue1 +
                        " and " + columnName2 + ":- " + tableValue2 + " is not found in the file " + fileName);
                System.err.println("--- FAIL ---> " + columnName1 + ":- " + tableValue1 +
                        " and " + columnName2 + ":- " + tableValue2 + " is not found in the file " + fileName);
            }
        }
    }

    @Then("^validate \"([^\"]*)\" table \"([^\"]*)\" and \"([^\"]*)\"$")
    public void validateTableAnda(String tableId, String tableValue1, String tableValue2) throws Throwable {
        String columnName1 = getTableValue1();
        String columnName2 = getTableValue2();
        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        boolean identificationFlag = false;
        for (String fileName : fileNames) {
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            int tableValue1Count = dqRecords.stream().filter(x -> x.get(columnName1).equals(tableValue1)).collect(Collectors.toList()).size();
            int tableValue2Count = dqRecords.stream().filter(x -> x.get(columnName2).equals(tableValue2)).collect(Collectors.toList()).size();
            if (tableValue1Count == 1) {
                test.pass("**********Pass Statement********* " + columnName1 + " :- " + tableValue1 + "has only 1 record which" +
                        "is as expected");
            } else {
                test.fail("--- FAIL --->  " + columnName1 + ":- " + tableValue1 +
                        " has " + tableValue1Count + " records instead of 1 record");
                System.err.println("--- FAIL --->  " + columnName1 + ":- " + tableValue1 +
                        " has " + tableValue1Count + " records instead of 1 record");
            }
            if (tableValue2Count == 1) {
                test.pass("**********Pass Statement********* " + columnName2 + " :- " + tableValue2 + "has only 1 record which" +
                        "is as expected");
            } else {
                test.fail(
                        "--- FAIL --->  " + columnName2 + ":- " + tableValue2 +
                                " has " + tableValue2Count + " records instead of 1 record");
                System.err.println("--- FAIL --->  " + columnName2 + ":- " + tableValue2 +
                        " has " + tableValue2Count + " records instead of 1 record");
            }
            for (Map<String, String> dqRecord : dqRecords) {
                if (dqRecord.get(columnName1).equals(tableValue1)) {
                    if (dqRecord.get(columnName2).equals(tableValue2)) {
                        test.pass("**********Pass Statement********* " + columnName1 + " :- " + tableValue1 + " and "
                                + columnName2 + " :- " + tableValue2 + " values are present in the file:- " + fileName + " which is as expected");
                        identificationFlag = true;
                        break;
                    } else {
                        test.fail("--- FAIL ---> " + columnName2 + ":- " + tableValue2 +
                                " is not as expected for " + columnName1 + ":- " + tableValue1);
                        System.err.println("--- FAIL ---> " + columnName2 + ":- " + tableValue2 +
                                " is not as expected for " + columnName1 + ":- " + tableValue1);
                    }
                }
            }
            if (identificationFlag == false) {
                test.fail("--- FAIL ---> " + columnName1 + ":- " + tableValue1 +
                        " and " + columnName2 + ":- " + tableValue2 + " is not found in the file " + fileName);
                System.err.println("--- FAIL ---> " + columnName1 + ":- " + tableValue1 +
                        " and " + columnName2 + ":- " + tableValue2 + " is not found in the file " + fileName);
            }
        }

    }

    @Given("^column names are \"([^\"]*)\" and \"([^\"]*)\"$")
    public void columnNamesAreAnd(String tableValue1, String tableValue2) throws Throwable {
        setTableValue1(tableValue1);
        setTableValue2(tableValue2);
    }

    @Given("^column names are \"([^\"]*)\",\"([^\"]*)\" and \"([^\"]*)\"$")
    public void columnNamesAreAnd(String tableValue1, String tableValue2, String tableValue3) throws Throwable {
        setTableValue1(tableValue1);
        setTableValue2(tableValue2);
        setTableValue3(tableValue3);
    }

    @Then("^validate \"([^\"]*)\" table \"([^\"]*)\",\"([^\"]*)\" and \"([^\"]*)\"$")
    public void validateTableAnd(String tableId, String tableValue1, String tableValue2, String tableValue3) throws
            Throwable {
        String columnName1 = getTableValue1();
        String columnName2 = getTableValue2();
        String columnName3 = getTableValue3();

        List<String> fileNames = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        boolean identificationFlag = false;
        for (String fileName : fileNames) {
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            int tableValue1Count = dqRecords.stream().filter(x -> x.get(columnName1).equals(tableValue1)).collect(Collectors.toList()).size();
            int tableValue2Count = dqRecords.stream().filter(x -> x.get(columnName2).equals(tableValue2)).collect(Collectors.toList()).size();
            int tableValue3Count = dqRecords.stream().filter(x -> x.get(columnName3).equals(tableValue3)).collect(Collectors.toList()).size();

            if (tableValue1Count == 1) {
                test.pass("**********Pass Statement********* " + columnName1 + " :- " + tableValue1 + "has only 1 record which" +
                        "is as expected");
            } else {
                test.fail(
                        "--- FAIL --->  " + columnName1 + ":- " + tableValue1 +
                                " has " + tableValue1Count + " records instead of 1 record");
                System.err.println("--- FAIL --->  " + columnName1 + ":- " + tableValue1 +
                        " has " + tableValue1Count + " records instead of 1 record");
            }
            if (tableValue2Count == 1) {
                test.pass("**********Pass Statement********* " + columnName2 + " :- " + tableValue2 + "has only 1 record which" +
                        "is as expected");
            } else {
                test.fail("--- FAIL --->  " + columnName2 + ":- " + tableValue2 +
                        " has " + tableValue2Count + " records instead of 1 record");
                System.err.println("--- FAIL --->  " + columnName2 + ":- " + tableValue2 +
                        " has " + tableValue2Count + " records instead of 1 record");
            }
            if (tableValue3Count == 1) {
                test.pass("**********Pass Statement********* " + columnName3 + " :- " + tableValue3 + "has only 1 record which" +
                        "is as expected");
            } else {
                test.fail("--- FAIL --->  " + columnName3 + ":- " + tableValue3 +
                        " has " + tableValue3Count + " records instead of 1 record");
                System.err.println("--- FAIL --->  " + columnName3 + ":- " + tableValue3 +
                        " has " + tableValue3Count + " records instead of 1 record");
            }
            for (Map<String, String> dqRecord : dqRecords) {
                if (dqRecord.get(columnName1).equals(tableValue1)) {
                    if (dqRecord.get(columnName2).equals(tableValue2)) {
                        if (dqRecord.get(columnName3).equals(tableValue3)) {
                            test.pass("**********Pass Statement********* " + columnName1 + " :- " + tableValue1 + " and "
                                    + columnName2 + " :- " + tableValue2 + " and "
                                    + columnName3 + " :- " + tableValue3 + " values are present in the file:- " + fileName + " which is as expected");
                            identificationFlag = true;
                            break;
                        } else {
                            test.fail("--- FAIL ---> " + columnName1 + ":- " + tableValue1 +
                                    " is not as expected for " + columnName2 + ":- " + tableValue2 +
                                    " and " + columnName3 + ":- " + tableValue3);
                            System.err.println("--- FAIL ---> " + columnName1 + ":- " + tableValue1 +
                                    " is not as expected for " + columnName2 + ":- " + tableValue2 +
                                    " and " + columnName3 + ":- " + tableValue3);
                        }
                    }
                }
            }
            if (identificationFlag == false) {
                test.fail("--- FAIL ---> " + columnName1 + ":- " + tableValue1 +
                        " and " + columnName2 + ":- " + tableValue2 + " and " + columnName3 + ":- " + tableValue3 +
                        "is not found in the file " + fileName);
                System.err.println("--- FAIL ---> " + columnName1 + ":- " + tableValue1 +
                        " and " + columnName2 + ":- " + tableValue2 + " and " + columnName3 + ":- " + tableValue3 +
                        "is not found in the file " + fileName);
            }
        }

    }


    @Then("^file length should be (\\d+) excluding extension$")
    public void fileLengthShouldBeExcludingExtension(int length) {
        File filesFolder = new File(System.getProperty("user.dir") + "\\resources\\dqFiles");
        File[] listOfFiles = filesFolder.listFiles();
        for (File file : listOfFiles) {
            if (file.getName().replace(".txt", "").length() == length) {
                test.pass("**********Pass Statement********* File:- " + file.getName().replace(".txt", "") +
                        " Length is as expected and it's length is " + length);
            } else {
                test.fail(
                        "--- FAIL ---> File:- " + file.getName().replace(".txt", "") +
                                " length is " + file.getName().replace(".txt", "").length() + " instead of " + length);
                System.err.println("--- FAIL ---> File:- " + file.getName().replace(".txt", "") +
                        " length is " + file.getName().replace(".txt", "").length() + " instead of " + length);
            }
        }
    }

    @And("^file extension should be \\.txt$")
    public void fileExtensionShouldBeTxt() {
        File filesFolder = new File(System.getProperty("user.dir") + "\\resources\\dqFiles");
        File[] listOfFiles = filesFolder.listFiles();
        for (File file : listOfFiles) {
            if (file.getName().contains(".txt")) {
                test.pass("**********Pass Statement********* File Name:- " + file +
                        " contains .txt extension");
            } else {
                test.fail(
                        "--- FAIL ---> File Name:- " + file + "" +
                                " does not contains .txt extension");
                System.err.println("--- FAIL ---> File Name:- " + file + "" +
                        " does not contains .txt extension");
            }
        }
    }

    @And("^file count based on extract option$")
    public void fileCountBasedOnExtractOption() {
        long fileCountExpected = Long.parseLong(ConfigurationManager.getBundle().getPropertyValue("dqrules.file.count"));
        File filesFolder = new File(System.getProperty("user.dir") + "\\resources\\dqFiles");
        long fileCountActual = Arrays.stream(filesFolder.listFiles()).count();
        if (fileCountActual == fileCountExpected) {
            test.pass("**********Pass Statement********* Files count " + fileCountActual +
                    " is as expected");
        } else {
            test.fail(
                    "--- FAIL ---> Files count is not as expected, Actual file count is:- " + fileCountActual +
                            " and Expected file count is:- " + fileCountExpected);
            System.err.println("--- FAIL ---> Files count is not as expected, Actual file count is:- " + fileCountActual +
                    " and Expected file count is:- " + fileCountExpected);
        }
    }

    @And("^column headers and number of columns$")
    public void columnHeadersAndNumberOfColumns() throws IOException {
        Properties dqRulesHeaders = new Properties();
        File dqfile = new File(System.getProperty("user.dir") + "/resources/dqRulesHeaders.properties");
        FileInputStream fileInputStream = new FileInputStream(dqfile);
        dqRulesHeaders.load(fileInputStream);

        File filesFolder = new File(System.getProperty("user.dir") + "\\resources\\dqFiles");
        File[] listOfFiles = filesFolder.listFiles();
        for (File file : listOfFiles) {
            String fileTableId = file.getName().substring(18, 22);
            List<String> headersExpected = Arrays.asList(dqRulesHeaders.get(fileTableId).toString().split(","));
            List<String> headersActual = new ReadTextFile().getHeaderList(file.getPath());
            if (headersActual.size() == headersExpected.size()) {
                test.pass("**********Pass Statement********* File:- " + file.getName() + " Headers size is deviated Actual Headers Size:- " + headersActual.size() +
                        " and Expected Headers Size:- " + headersExpected.size());
                if (headersActual.stream().anyMatch(element -> headersExpected.contains(element))) {
                    test.pass("**********Pass Statement******** File:- " + file.getName() + " Headers are as expected Headers:- " + headersActual);
                } else {
                    test.fail(
                            "--- FAIL ---> File:- " + file + " Headers are deviated Actual Headers:- " + headersActual +
                                    " and Expected Headers:- " + headersExpected);
                    System.err.println("--- FAIL ---> File:- " + file.getName() + "and TableId:- " + fileTableId + " Headers are deviated Actual Headers:- " + headersActual +
                            " and Expected Headers:- " + headersExpected);
                }
            } else {
                test.fail(
                        "--- FAIL ---> File:- " + file + " Headers size is deviated Actual Headers Size:- " + headersActual.size() +
                                " and Expected Headers Size:- " + headersExpected.size());
                System.err.println("--- FAIL ---> File:- " + file.getName() + "and TableId:- " + fileTableId + " Headers size is deviated Actual Headers Size:- " + headersActual.size() +
                        " and Expected Headers Size:- " + headersExpected.size());

                test.fail(
                        "--- FAIL ---> File:- " + file + " Headers are deviated Actual Headers:- " + headersActual +
                                " and Expected Headers:- " + headersExpected);
                System.err.println("--- FAIL ---> File:- " + file.getName() + "and TableId:- " + fileTableId + " Headers are deviated Actual Headers:- " + headersActual +
                        " and Expected Headers:- " + headersExpected);
            }

        }

    }

    @Given("^verify mi for all files$")
    public void verifyMiForAllFiles() {
        File filesFolder = new File(System.getProperty("user.dir") + "\\resources\\dqFiles");
        File[] listOfFiles = filesFolder.listFiles();
        String[] mi = {"ADSB", "B2BB", "B2TC", "CHIN", "BMOA", "BMOM", "BNSA", "BMOT", "BRID", "CPAC", "CANT", "CIBC", "CTIR", "CWBA", "CWTC", "CMTC", "CIMI", "CITC", "CIDL", "CITB", "CCAP", "COMT", "CMPU", "CFSA", "CONT", "CSAB", "CTCB", "DESJ", "DIRC", "DBOC", "EFFT", "EQTB", "EQTT", "FNAT", "GEBC", "HABI", "EFTC", "HMBK", "HOMB", "HOME", "HSBC", "HSBM", "HSTC", "ICIC", "IATI", "ICBC", "INVT", "KHBC", "LAUB", "LAUT", "LBCT", "LSMC", "LPTR", "MANU", "MANT", "MCAN", "MOTB", "MTCC", "NATC", "NATT", "NABC", "NBTC", "PHTC", "PETC", "PBOC", "PCBA", "RBCD", "STRE", "RBCA", "RBCM", "ROYT", "RTCC", "STIC", "SCMC", "SHIN", "SLFT", "TNGT", "TDMC", "TDPM", "TDBA", "UBSB", "VATC", "CITZ", "VBNK", "WOBC"};

        for (File file : listOfFiles) {
            if (Arrays.asList(mi).contains(file.getName().substring(0, 4))) {
//                System.out.println("**********Pass Statement******** File:- " + file.getName() + " MI exists in Member Institution ID codes");
            } else {
                System.err.println("--- FAIL ---> File:- " + file.getName() + " MI does not exists in Member Institution ID codes");
            }
        }
    }

    @And("^verify (\\d+) position value of all files$")
    public void verifyPositionValueOfAllFiles(int position) {
        File filesFolder = new File(System.getProperty("user.dir") + "\\resources\\dqFiles");
        File[] listOfFiles = filesFolder.listFiles();
        String[] subSystemIds = {"001", "002", "003", "004", "005", "006", "007", "008", "009", "010", "011", "012", "013", "014",
                "015", "016", "017", "018", "019",};
        for (File file : listOfFiles) {
            String miId = file.getName().substring(18, 22);
            if (file.getName().substring(22, 23).equals("3")) {
//                System.out.println("**********Pass Statement******** File:- " + file.getName() + " 23rd position is '3'");
                if (miId.equals("0100") || miId.equals("0110") || miId.equals("0120")
                        || miId.equals("0121") || miId.equals("0201") || miId.equals("0202")
                        || miId.equals("0211") || miId.equals("0212") || miId.equals("0221")) {
                    if (file.getName().substring(26, 29).equals("000")) {
//                        System.out.println("**********Pass Statement******** File:- " + file.getName() + "Position:- 27 to 29 is '000' for MI Id:- " + miId + "is as expected");
                    } else {
                        System.err.println("--- FAIL ---> File:- " + file.getName() + "Position:- 27 to 29 is '" + file.getName().substring(26, 29) + "' instead of '000'");
                    }
                } else if (miId.equals("0130") || miId.equals("0140") || miId.equals("0152")
                        || miId.equals("0153") || miId.equals("0160") || miId.equals("0231")
                        || miId.equals("0232") || miId.equals("0233") || miId.equals("0234")
                        || miId.equals("0235") || miId.equals("0236") || miId.equals("0237")
                        || miId.equals("0238") || miId.equals("0239") || miId.equals("0240")
                        || miId.equals("0241") || miId.equals("0242") || miId.equals("0400")
                        || miId.equals("0401") || miId.equals("0500") || miId.equals("0501")
                        || miId.equals("0600") || miId.equals("0800") || miId.equals("0900")
                        || miId.equals("0999")) {
                    if (Arrays.asList(subSystemIds).contains(file.getName().substring(26, 29))) {
                       /* System.out.println("**********Pass Statement******** File:- " + file.getName() +
                                "Position:- 27 to 29 is with in 0999 Subsystem_ID for MI Id:- " + miId + "is as expected");*/
                    } else {
                        System.err.println("--- FAIL ---> File:- " + file.getName() + "Position:- 27 to 29 is not with in 0999 Subsystem_ID for MI Id:- " + miId +
                                "Actual value:- " + file.getName().substring(26, 29) + " List of susbsytem Id's Expected value:- " +
                                subSystemIds);
                    }
                } else {
                    System.err.println("--- FAIL ---> File:- " + file.getName() + " MI id is not listed as part of requirement " +
                            " and it MI Id:- " + miId);
                }
            } else {
                System.err.println("--- FAIL ---> File:- " + file.getName() + " 23rd position value is " + file.getName().substring(22, 23) + " instead of '3'");
            }
        }
    }

    @And("^generate report$")
    public void generateReport() {
//        extent.flush();
    }

    @And("^generate report for \"([^\"]*)\" as \"([^\"]*)\"$")
    public void generateReportForAs(String key, String examplesEnded) throws Throwable {
       /* if (key.equals(examplesEnded)) {
            extent.flush();
        }*/

    }

    private HashMap<String, String> getMappingForDQandMasking(String tableId) throws IOException {
        List<String> fileNamesMasking = new DQRules().getMasking0100BeforeNameLogicFiles(tableId);
        List<String> fileNamesDq = new DQRules().getDQFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        DQRules rules = new DQRules();
        HashMap<String, String> dqMaskingDepositorIdMasking0100 = new HashMap<>();
        if (fileNamesDq.size() == fileNamesMasking.size()) {
            for (String maskingFileName : fileNamesMasking) {
                System.out.println(maskingFileName);
                String dqFileName = new DQRules().getDQFileNames(tableId).stream().filter(x -> x.substring(0, 4).equals(maskingFileName.substring(0, 4)))
                        .filter(x -> x.substring(18, 22).equals(maskingFileName.substring(18, 22)))
                        .filter(x -> x.substring(26, 29).equals(maskingFileName.substring(26, 29))).collect(Collectors.toList()).get(0);

                readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/masking0100BeforeNameLogicFiles/" + maskingFileName);
                List<Map<String, String>> maskingRecords = ReadTextFile.getDqSamples();

                readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + dqFileName);
                List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();

                for (int index = 0; index < maskingRecords.size(); index++) {
                    String maskingDepositorUniqueId = maskingRecords.get(index).get("Depositor_Unique_ID");
                    String dqDepositorUniqueId = dqRecords.get(index).get("Depositor_Unique_ID");
                    dqMaskingDepositorIdMasking0100.put(maskingDepositorUniqueId, dqDepositorUniqueId);
                }
            }
        } else {
            System.err.println("Old logic Masking files and DQ Files count is not same");
        }
        return dqMaskingDepositorIdMasking0100;
    }


    @Given("^validate (\\d+)-Depositor Data masking content$")
    public void validateDepositorDataMaskingContent(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_masking").mkdir();
        }
        List<String> fileNames = new DQRules().getMaskingFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        List<String> results = new LinkedList<>();
        String mi = null;
        String headers = null;
        HashMap<String, String> dqMaskingMappingDepsitorUniqueId0100 = getMappingForDQandMasking(tableId);

        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----Masking File Name--> " + fileName + " --------");
            System.out.println("-----Masking File Name--> " + fileName + " --------");
            String finalMi = mi;
            String dqFileName = new DQRules().getDQFileNames(tableId).stream().filter(x -> x.substring(0, 4).equals(finalMi))
                    .filter(x -> x.substring(18, 22).equals(fileName.substring(18, 22)))
                    .filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList()).get(0);
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + dqFileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/maskingFiles/" + fileName);
            List<Map<String, String>> maskingRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> depositorUniqueIdTable0100 = null;
            List<String> depositorUniqueIdDuplicates = null;
            List<String> depositorUniqueIdTableMasking0500 = new DQRules().getListOfString(mi, "0500", "Depositor_Unique_ID", "", "MASKING");
            List<String> accountUniqueIdTable0500 = new DQRules().getListOfString(mi, "0500", "Account_Unique_ID", "", "MASKING");
            List<String> accountUniqueIdTable0130 = new DQRules().getListOfString(mi, "0130", "Account_Unique_ID", "", "MASKING");
            List<String> trustAccountTypeCodeTable0130 = new DQRules().getListOfString(mi, "0130", "Trust_Account_Type_Code", "", "MASKING");
            if (maskingRecords.size() == dqRecords.size()) {
                test.pass("**********Pass Statement********* Masking file and DQ file:- " + fileName +
                        " records size is equal and it's size is " + maskingRecords.size());
                List<String> dqRecordsUniqueIds = dqRecords.stream().map(x -> x.get("Depositor_Unique_ID")).collect(Collectors.toList());

                for (int index = 0; index < maskingRecords.size(); index++) {
                    //  Below are the masking field
                    // Depositor_Unique_ID
                    String depositorUniqueIdMasking0100 = maskingRecords.get(index).get("Depositor_Unique_ID");
                    String depositorUniqueIdMappingDQ = dqMaskingMappingDepsitorUniqueId0100.get(depositorUniqueIdMasking0100);
                    int indexDQ = dqRecordsUniqueIds.indexOf(depositorUniqueIdMappingDQ);
                    if ((!(maskingRecords.get(index).get("Depositor_Unique_ID").equals(
                            dqRecords.get(indexDQ).get("Depositor_Unique_ID")))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Depositor_Unique_ID")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Depositor_Unique_ID is masked, Masking Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " and DQ  Depositor_Unique_ID:- " + dqRecords.get(indexDQ).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + " is not encrypted as per Rule-Masking");
                        System.err.println("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + " is not encrypted as per Rule-Masking");
                    }

// Rule:- 9 New code
                    if (depositorUniqueIdTable0100 == null) {
                        depositorUniqueIdTable0100 = new DQRules().getListOfString(mi, "0100", "Depositor_Unique_ID", fileName.substring(26, 29), "MASKING");
                        depositorUniqueIdDuplicates =
                                depositorUniqueIdTable0100.stream().collect(Collectors.groupingBy(Function.identity()))
                                        .entrySet()
                                        .stream()
                                        .filter(e -> e.getValue().size() > 1)
                                        .map(Map.Entry::getKey)
                                        .collect(Collectors.toList());
                    }

                    if (!(depositorUniqueIdDuplicates.contains(maskingRecords.get(index).get("Depositor_Unique_ID")))) {
                        test.pass("**********Pass Statement********* Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + "is as per Rule-9");
                    } else {
                        test.fail("--- FAIL ---> Depositor_Unique_Id:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + "is not as per Rule-9");
                        System.err.println("--- FAIL ---> Depositor_Unique_Id:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + "is not as per Rule-9");
                    }

                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Depositor_Unique_ID"))) {
                        test.pass("**********Pass Statement********* Depositor_Unique_Id:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + "is as per Rule-18");

                    } else {
                        test.fail("--- FAIL ---> Depositor_Unique_Id:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + "is not as per Rule-18");
                        System.err.println("--- FAIL ---> Depositor_Unique_Id:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + "is not as per Rule-18");
                    }


                    // Depositor_ID_Link
                    if ((!(maskingRecords.get(index).get("Depositor_ID_Link").equals(
                            dqRecords.get(indexDQ).get("Depositor_ID_Link")))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Depositor_ID_Link")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Depositor_ID_Link is masked, Masking Depositor_ID_Link:- " + maskingRecords.get(index).get("Depositor_ID_Link") +
                                " and DQ Depositor_ID_Link:- " + dqRecords.get(indexDQ).get("Depositor_ID_Link") + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Depositor_ID_Link:- " + maskingRecords.get(index).get("Depositor_ID_Link") + " is not encrypted as per Rule-Masking " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));

                        System.err.println("--- FAIL ---> Depositor_ID_Link:- " + maskingRecords.get(index).get("Depositor_ID_Link") + " is not encrypted as per Rule-Masking " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    }

                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Depositor_ID_Link"))) {
                        test.pass("**********Pass Statement********* Depositor_ID_Link:- " + maskingRecords.get(index).get("Depositor_ID_Link") + "is as per Rule-18" +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Depositor_ID_Link:- " + maskingRecords.get(index).get("Depositor_ID_Link") +
                                "is not as per Rule-18 for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                        System.err.println("--- FAIL ---> Depositor_ID_Link:- " + maskingRecords.get(index).get("Depositor_ID_Link") +
                                "is not as per Rule-18 for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    }

                    // Subsystem_ID
                    if (maskingRecords.get(index).get("Subsystem_ID").equals(
                            dqRecords.get(indexDQ).get("Subsystem_ID"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Subsystem_ID:- " + maskingRecords.get(index).get("Subsystem_ID") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + "Subsystem_ID:- " + maskingRecords.get(index).get("Subsystem_ID") +
                                " is not equal with DQ Subsystem_ID:- " + dqRecords.get(indexDQ).get("Subsystem_ID") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + "Subsystem_ID:- " + maskingRecords.get(index).get("Subsystem_ID") +
                                " is not equal with DQ Subsystem_ID:- " + dqRecords.get(indexDQ).get("Subsystem_ID") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                    }

                    // Depositor_Branch
                    if (maskingRecords.get(index).get("Depositor_Branch").equals(
                            dqRecords.get(indexDQ).get("Depositor_Branch"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Depositor_Branch:- " + maskingRecords.get(index).get("Depositor_Branch") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + "Depositor_Branch:- " + maskingRecords.get(index).get("Depositor_Branch") +
                                " is not equal with DQ Depositor_Branch:- " + dqRecords.get(indexDQ).get("Depositor_Branch") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + "Depositor_Branch:- " + maskingRecords.get(index).get("Depositor_Branch") +
                                " is not equal with DQ Depositor_Branch:- " + dqRecords.get(indexDQ).get("Depositor_Branch") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                    }

                    // Depositor_ID
                    if (maskingRecords.get(index).get("Depositor_ID").isEmpty() && dqRecords.get(indexDQ).get("Depositor_ID").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Depositor_ID is Empty/Blank for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else if ((!(maskingRecords.get(index).get("Depositor_ID").equals(
                            dqRecords.get(indexDQ).get("Depositor_ID")))) &&
                            rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Depositor_ID"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Depositor_ID is masked, Masking Depositor_ID:- " + maskingRecords.get(index).get("Depositor_ID") +
                                " and DQ Depositor_ID:- " + dqRecords.get(indexDQ).get("Depositor_ID") + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Depositor_ID:- " + maskingRecords.get(index).get("Depositor_ID") +
                                " is not masked as per Rule-Masking for Depositor_Unique_ID:- " +
                                maskingRecords.get(index).get("Depositor_Unique_ID"));
                        System.err.println("--- FAIL ---> Depositor_ID:- " + maskingRecords.get(index).get("Depositor_ID") +
                                " is not masked as per Rule-Masking for Depositor_Unique_ID:- " +
                                maskingRecords.get(index).get("Depositor_Unique_ID"));
                    }
                    // Name_Prefix
                    if (maskingRecords.get(index).get("Name_Prefix").isEmpty() && dqRecords.get(indexDQ).get("Name_Prefix").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Name_Prefix is Empty/Blank for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else if ((!(maskingRecords.get(index).get("Name_Prefix").equals(
                            dqRecords.get(indexDQ).get("Name_Prefix")))) &&
                            rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Name_Prefix"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Name_Prefix is masked, Masking Name_Prefix:- " + maskingRecords.get(index).get("Name_Prefix") +
                                " and DQ Name_Prefix:- " + dqRecords.get(indexDQ).get("Name_Prefix") + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Name_Prefix:- " + maskingRecords.get(index).get("Name_Prefix") +
                                " is not masked as per Rule-Masking for Depositor_Unique_ID:- " +
                                maskingRecords.get(index).get("Depositor_Unique_ID"));
                        System.err.println("--- FAIL ---> Name_Prefix:- " + maskingRecords.get(index).get("Name_Prefix") +
                                " is not masked as per Rule-Masking for Depositor_Unique_ID:- " +
                                maskingRecords.get(index).get("Depositor_Unique_ID"));
                    }

                    // Name
//                    if (rules.checkMaskedOrNotMasked(dqRecords.get(index).get("Name"))) {
// Here table 130 having Trust_Account_Type_Code -3 then table-100 Depositor_Unique_ID should not be masked
                    // Step:-1 - Grab depositor unique_Id and get Account_Unique_ID from table-0500
//                        Step:- 2 - Now verify Account_Unique_ID in table 0130
//                        Step:-3 - If table-0130 returns 3 then it should not be masked else it should be masked
                    String depositorUniqueId = maskingRecords.get(index).get("Depositor_Unique_ID");

                    int indexOfDepositorUniqueId = depositorUniqueIdTableMasking0500.indexOf(depositorUniqueId);
                    String accountUniqueId = null;
                    if (indexOfDepositorUniqueId != -1) {
                        accountUniqueId = accountUniqueIdTable0500.get(indexOfDepositorUniqueId);
                    }
                    String trustAccountTypeCode = null;
                    if (accountUniqueId != null) {
                        int indexOfAccountUniqueId = accountUniqueIdTable0130.indexOf(accountUniqueId);
                        if (indexOfAccountUniqueId != -1) {
                            trustAccountTypeCode = trustAccountTypeCodeTable0130.get(indexOfAccountUniqueId);
                        } else {
                            trustAccountTypeCode = "0";
                        }

                    } else {
                        trustAccountTypeCode = "0";
                    }

                    if (trustAccountTypeCode.equals("3")) {
                        if (maskingRecords.get(index).get("Name").equals(
                                dqRecords.get(indexDQ).get("Name")) &&
                                !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Name")))) {
                            test.pass("**********Pass Statement********* DQ file and Masking file Name:-" + maskingRecords.get(index).get("Name") + " is not Masked because Trust_Account_Type_Code is '3'" +
                                    " for Account_Unique_ID:- " + accountUniqueId + " and 0100 table Depositor_Unique_ID:- " + depositorUniqueId);
                        } else if (maskingRecords.get(index).get("Name").isEmpty()) {
                            if (!(maskingRecords.get(index).get("Name").equals(
                                    dqRecords.get(indexDQ).get("Name")))) {
                                test.fail("--- FAIL ---> Name:- " + maskingRecords.get(index).get("Name") +
                                        " and Un-Masking Name:- " + dqRecords.get(indexDQ).get("Name") + " is not Blank/Empty for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                        " which is not as per Rule-Masking");
                                System.err.println("--- FAIL ---> Name:- " + maskingRecords.get(index).get("Name") +
                                        " and Un-Masking Name:- " + dqRecords.get(indexDQ).get("Name") + " is not Blank/Empty for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                        " which is not as per Rule-Masking");
                            }
                        } else {
                            test.fail("--- FAIL ---> Name :- " + maskingRecords.get(index).get("Name") + " is masked when Trust_Account_Type_Code is '3' " +
                                    " for Account_Unique_ID:- " + accountUniqueId + " and 0100 table Depositor_Unique_ID:- " + depositorUniqueId +
                                    " is not as per Rule-Masking");
                            System.err.println("--- FAIL ---> Name :- " + maskingRecords.get(index).get("Name") + " is masked when Trust_Account_Type_Code is '3' " +
                                    " for Account_Unique_ID:- " + accountUniqueId + " and 0100 table Depositor_Unique_ID:- " + depositorUniqueId +
                                    " is not as per Rule-Masking");
                        }
                    } else if (maskingRecords.get(index).get("Name").isEmpty()) {
                        if (!(maskingRecords.get(index).get("Name").equals(
                                dqRecords.get(indexDQ).get("Name")))) {
                            test.fail("--- FAIL --->  Name:- " + maskingRecords.get(index).get("Name") +
                                    " and Un Masking Name:- " + dqRecords.get(indexDQ).get("Name") + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                    " is not as per Rule-Masking");
                            System.err.println("--- FAIL --->  Name:- " + maskingRecords.get(index).get("Name") +
                                    " and Un Masking Name:- " + dqRecords.get(indexDQ).get("Name") + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                    " is not as per Rule-Masking");
                        }
                    } else if ((!(maskingRecords.get(index).get("Name").equals(
                            dqRecords.get(indexDQ).get("Name")))) &&
                            rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Name").replaceAll("\\s", "X"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Name is masked, Masking Name:- " + maskingRecords.get(index).get("Name") +
                                " and DQ Name:- " + dqRecords.get(indexDQ).get("Name") + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else if ((maskingRecords.get(index).get("Name").equals(
                            dqRecords.get(indexDQ).get("Name"))) &&
                            rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Name").replaceAll("\\s", "X"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Name is masked, Masking Name:- " + maskingRecords.get(index).get("Name") +
                                " and DQ Depositor_Unique_ID:- " + dqRecords.get(indexDQ).get("Depositor_Unique_ID"));

                    } else if ((!(maskingRecords.get(index).get("Name").equals(
                            dqRecords.get(indexDQ).get("Name")))) &&
                            !rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Name").replaceAll("\\s", "X"))) {
                        test.fail("--- FAIL ---> Name:- " + dqRecords.get(indexDQ).get("Name") +
                                " and Masking file Name:- " + maskingRecords.get(index).get("Name") + "for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Name:- " + dqRecords.get(indexDQ).get("Name") +
                                " and Masking file Name:- " + maskingRecords.get(index).get("Name") + "for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                    } else {
                        test.fail("--- FAIL ---> Name:- " + dqRecords.get(indexDQ).get("Name") +
                                " and Masking file Name:- " + maskingRecords.get(index).get("Name") + "for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Name:- " + dqRecords.get(indexDQ).get("Name") +
                                " and Masking file Name:- " + maskingRecords.get(index).get("Name") + "for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                    }

//   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Name"))) {
                        test.pass("**********Pass Statement********* Name:- " + maskingRecords.get(index).get("Name") + "is as per Rule-18" +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Name:- " + maskingRecords.get(index).get("Name") +
                                "is not as per Rule-18 for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                        System.err.println("--- FAIL ---> Name:- " + maskingRecords.get(index).get("Name") +
                                "is not as per Rule-18 for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    }
                    //First_Name
                    if (rules.checkMaskedOrNotMasked(dqRecords.get(indexDQ).get("First_Name"))) {
                        if (maskingRecords.get(index).get("First_Name").equals(
                                dqRecords.get(indexDQ).get("First_Name").replaceAll("\\s", "X"))) {
                            test.pass("**********Pass Statement********* DQ file and Masking file First_Name is masked, Masking First_Name:- " + maskingRecords.get(index).get("First_Name") +
                                    " for DQ Depositor_Unique_ID:- " + dqRecords.get(indexDQ).get("Depositor_Unique_ID"));
                        } else {
                            test.fail("--- FAIL ---> First_Name:- " + dqRecords.get(indexDQ).get("First_Name") +
                                    " and Masking file First_Name:- " + maskingRecords.get(index).get("First_Name") +
                                    " are different for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                    " is not as per Rule-Masking");
                            System.err.println("--- FAIL ---> First_Name:- " + dqRecords.get(indexDQ).get("First_Name") +
                                    " and Masking file First_Name:- " + maskingRecords.get(index).get("First_Name") +
                                    " are different for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                    " is not as per Rule-Masking");
                        }
                    } else if (maskingRecords.get(index).get("First_Name").isEmpty() && dqRecords.get(indexDQ).get("First_Name").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file First_Name is Empty/Blank for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else if ((!(maskingRecords.get(index).get("First_Name").equals(
                            dqRecords.get(indexDQ).get("First_Name")))) &&
                            rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("First_Name"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file First_Name is masked, Masking First_Name:- " + maskingRecords.get(index).get("First_Name") +
                                " and DQ First_Name:- " + dqRecords.get(indexDQ).get("First_Name") + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> First_Name:- " + maskingRecords.get(index).get("First_Name")
                                + " are not masked  for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> First_Name:- " + maskingRecords.get(index).get("First_Name")
                                + " are not masked  for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                    }

                    //Middle_Name
                    if (rules.checkMaskedOrNotMasked(dqRecords.get(indexDQ).get("Middle_Name"))) {
                        if (maskingRecords.get(index).get("Middle_Name").equals(
                                dqRecords.get(indexDQ).get("Middle_Name").replaceAll("\\s", "X"))) {
                            test.pass("**********Pass Statement********* DQ file and Masking file Middle_Name is masked, Masking Middle_Name:- " + maskingRecords.get(index).get("Middle_Name") +
                                    " for DQ Depositor_Unique_ID:- " + dqRecords.get(indexDQ).get("Depositor_Unique_ID"));
                        } else {
                            test.fail("--- FAIL ---> Middle_Name:- " + dqRecords.get(indexDQ).get("Middle_Name") +
                                    " and Masking file Middle_Name:- " + maskingRecords.get(index).get("Middle_Name") +
                                    " are different for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                    " is not as per Rule-Masking");
                            System.err.println("--- FAIL ---> Middle_Name:- " + dqRecords.get(indexDQ).get("Middle_Name") +
                                    " and Masking file Middle_Name:- " + maskingRecords.get(index).get("Middle_Name") +
                                    " are different for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                    " is not as per Rule-Masking");
                        }
                    } else if (maskingRecords.get(index).get("Middle_Name").isEmpty() && dqRecords.get(indexDQ).get("Middle_Name").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Middle_Name is Empty/Blank for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else if ((!(maskingRecords.get(index).get("Middle_Name").equals(
                            dqRecords.get(indexDQ).get("Middle_Name")))) &&
                            rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Middle_Name"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Middle_Name is masked, Masking Middle_Name:- " + maskingRecords.get(index).get("Middle_Name") +
                                " and DQ Middle_Name:- " + dqRecords.get(indexDQ).get("Middle_Name") + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> DQ file and Masking file Middle_Name are same Middle_Name:- " + maskingRecords.get(index).get("Middle_Name")
                                + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> DQ file and Masking file Middle_Name are same Middle_Name:- " + maskingRecords.get(index).get("Middle_Name")
                                + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                    }

                    //Last_Name
                    if (rules.checkMaskedOrNotMasked(dqRecords.get(indexDQ).get("Last_Name"))) {
                        if (maskingRecords.get(index).get("Last_Name").equals(
                                dqRecords.get(indexDQ).get("Last_Name").replaceAll("\\s", "X"))) {
                            test.pass("**********Pass Statement********* DQ file and Masking file Last_Name is masked, Masking Last_Name:- " + maskingRecords.get(index).get("Last_Name") +
                                    " for DQ Depositor_Unique_ID:- " + dqRecords.get(indexDQ).get("Depositor_Unique_ID"));
                        } else {
                            test.fail("--- FAIL ---> Last_Name:- " + dqRecords.get(indexDQ).get("Last_Name") +
                                    " and Masking file Last_Name:- " + maskingRecords.get(index).get("Last_Name") +
                                    " are different for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                    " is not as per Rule-Masking");
                            System.err.println("--- FAIL ---> Last_Name:- " + dqRecords.get(indexDQ).get("Last_Name") +
                                    " and Masking file Last_Name:- " + maskingRecords.get(index).get("Last_Name") +
                                    " are different for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                    " is not as per Rule-Masking");
                        }
                    } else if (maskingRecords.get(index).get("Last_Name").isEmpty() && dqRecords.get(indexDQ).get("Last_Name").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Last_Name is Empty/Blank for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else if ((!(maskingRecords.get(index).get("Last_Name").equals(
                            dqRecords.get(indexDQ).get("Last_Name")))) &&
                            rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Last_Name"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Last_Name is masked, Masking Last_Name:- " + maskingRecords.get(index).get("Last_Name") +
                                " and DQ Last_Name:- " + dqRecords.get(indexDQ).get("Last_Name") + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Last_Name:- " + maskingRecords.get(index).get("Last_Name") + " is same as DQ Last_Name"
                                + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + " is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Last_Name:- " + maskingRecords.get(index).get("Last_Name") + " is same as DQ Last_Name"
                                + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + " is not as per Rule-Masking");
                    }
                    //Name_Suffix
                    if (maskingRecords.get(index).get("Name_Suffix").isEmpty() && dqRecords.get(indexDQ).get("Name_Suffix").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Name_Suffix is Empty/Blank for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else if ((!(maskingRecords.get(index).get("Name_Suffix").equals(
                            dqRecords.get(indexDQ).get("Name_Suffix")))) &&
                            rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Name_Suffix"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Name_Suffix is masked, Masking Name_Suffix:- " + maskingRecords.get(index).get("Name_Suffix") +
                                " and DQ Name_Suffix:- " + dqRecords.get(indexDQ).get("Name_Suffix") + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Name_Suffix:- " + maskingRecords.get(index).get("Name_Suffix") + " is same as DQ Last_Name"
                                + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + " is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Name_Suffix:- " + maskingRecords.get(index).get("Name_Suffix") + " is same as DQ Last_Name"
                                + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + " is not as per Rule-Masking");
                    }
//                    Birth_Date
                    if (maskingRecords.get(index).get("Birth_Date").equals("19000101")) {
                        test.pass("**********Pass Statement********* Masking file Birth_Date is 19000101"
                                + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Birth_Date:- " + maskingRecords.get(index).get("Depositor_Unique_ID")
                                + "is not '19000101' for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Birth_Date:- " + maskingRecords.get(index).get("Depositor_Unique_ID")
                                + "is not '19000101' for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                    }

                    // Phone_1
                    if (rules.checkMaskedOrNotMasked(dqRecords.get(indexDQ).get("Phone_1"))) {
                        if (maskingRecords.get(index).get("Phone_1").equals(
                                dqRecords.get(indexDQ).get("Phone_1").replaceAll("\\s", "X"))) {
                            test.pass("**********Pass Statement********* DQ file and Masking file Phone_1 is masked, Masking Phone_1:- " + maskingRecords.get(index).get("Phone_1") +
                                    " for DQ Depositor_Unique_ID:- " + dqRecords.get(indexDQ).get("Depositor_Unique_ID"));
                        } else {
                            test.fail("--- FAIL ---> Phone_1:- " + dqRecords.get(indexDQ).get("Phone_1") +
                                    " and Masking file Phone_1:- " + maskingRecords.get(index).get("Phone_1") +
                                    " are different for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                    " is not as per Rule-Masking");
                            System.err.println("--- FAIL ---> Phone_1:- " + dqRecords.get(indexDQ).get("Phone_1") +
                                    " and Masking file Phone_1:- " + maskingRecords.get(index).get("Phone_1") +
                                    " are different for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                    " is not as per Rule-Masking");
                        }
                    } else if (maskingRecords.get(index).get("Phone_1").isEmpty() && dqRecords.get(indexDQ).get("Phone_1").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Phone_1 is Empty/Blank for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else if ((!(maskingRecords.get(index).get("Phone_1").equals(
                            dqRecords.get(indexDQ).get("Phone_1")))) &&
                            rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Phone_1"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Phone_1 is masked, Masking Phone_1:- " + maskingRecords.get(index).get("Phone_1") +
                                " and DQ Phone_1:- " + dqRecords.get(indexDQ).get("Phone_1") + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Phone_1:- " + maskingRecords.get(index).get("Phone_1")
                                + " are same for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Phone_1:- " + maskingRecords.get(index).get("Phone_1")
                                + " are same for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                    }
                    // Phone_2
                    if (maskingRecords.get(index).get("Phone_2").isEmpty() && dqRecords.get(indexDQ).get("Phone_2").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Phone_2 is Empty/Blank for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else if ((!(maskingRecords.get(index).get("Phone_2").equals(
                            dqRecords.get(indexDQ).get("Phone_2")))) &&
                            rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Phone_2"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Phone_2 is masked, Masking Phone_2:- " + maskingRecords.get(index).get("Phone_2") +
                                " and DQ Phone_2:- " + dqRecords.get(indexDQ).get("Phone_2") + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Phone_2:- " + maskingRecords.get(index).get("Phone_2")
                                + " are same for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Phone_2:- " + maskingRecords.get(index).get("Phone_2")
                                + " are same for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                    }
                    // Email
                    if (maskingRecords.get(index).get("Email").isEmpty() && dqRecords.get(indexDQ).get("Email").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Email is Empty/Blank for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else if ((!(maskingRecords.get(index).get("Email").equals(
                            dqRecords.get(indexDQ).get("Email"))) &&
                            rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Email")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Email is masked, Masking Email:- " + maskingRecords.get(index).get("Email") +
                                " and DQ Email:- " + dqRecords.get(indexDQ).get("Email") + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Email:- " + maskingRecords.get(index).get("Email")
                                + " DQ file and Masking file Email are same for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Email:- " + maskingRecords.get(index).get("Email")
                                + " DQ file and Masking file Email are same for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                    }

                    // Depositor_Type_Code
                    if (maskingRecords.get(index).get("Depositor_Type_Code").equals(
                            dqRecords.get(indexDQ).get("Depositor_Type_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Depositor_Type_Code:- " + maskingRecords.get(index).get("Depositor_Type_Code") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Masking Depositor_Type_Code:- " + maskingRecords.get(index).get("Depositor_Type_Code") +
                                " is not equal with DQ Depositor_Type_Code:- " + dqRecords.get(indexDQ).get("Depositor_Type_Code") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Masking Depositor_Type_Code:- " + maskingRecords.get(index).get("Depositor_Type_Code") +
                                " is not equal with DQ Depositor_Type_Code:- " + dqRecords.get(indexDQ).get("Depositor_Type_Code") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                    }

                    // Depositor_Agent_Flag
                    if (maskingRecords.get(index).get("Depositor_Agent_Flag").equals(
                            dqRecords.get(indexDQ).get("Depositor_Agent_Flag"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Depositor_Agent_Flag:- " + maskingRecords.get(index).get("Depositor_Agent_Flag") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Masking Depositor_Agent_Flag:- " + maskingRecords.get(index).get("Depositor_Agent_Flag") +
                                " is not equal with DQ Depositor_Agent_Flag:- " + dqRecords.get(indexDQ).get("Depositor_Agent_Flag") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Masking Depositor_Agent_Flag:- " + maskingRecords.get(index).get("Depositor_Agent_Flag") +
                                " is not equal with DQ Depositor_Agent_Flag:- " + dqRecords.get(indexDQ).get("Depositor_Agent_Flag") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                    }

                    // Language_Flag
                    if (maskingRecords.get(index).get("Language_Flag").equals(
                            dqRecords.get(indexDQ).get("Language_Flag"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Language_Flag:- " + maskingRecords.get(index).get("Language_Flag") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Masking Language_Flag:- " + maskingRecords.get(index).get("Language_Flag") +
                                " is not equal with DQ Language_Flag:- " + dqRecords.get(indexDQ).get("Language_Flag") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Masking Language_Flag:- " + maskingRecords.get(index).get("Language_Flag") +
                                " is not equal with DQ Language_Flag:- " + dqRecords.get(indexDQ).get("Language_Flag") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                    }

                    // Employee_Flag
                    if (maskingRecords.get(index).get("Employee_Flag").equals(
                            dqRecords.get(indexDQ).get("Employee_Flag"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Employee_Flag:- " + maskingRecords.get(index).get("Employee_Flag") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Masking Employee_Flag:- " + maskingRecords.get(index).get("Employee_Flag") +
                                " is not equal with DQ Employee_Flag:- " + dqRecords.get(indexDQ).get("Employee_Flag") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Masking Employee_Flag:- " + maskingRecords.get(index).get("Employee_Flag") +
                                " is not equal with DQ Employee_Flag:- " + dqRecords.get(indexDQ).get("Employee_Flag") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                    }

                    // Phone_1_Type
                    if (maskingRecords.get(index).get("Phone_1_Type").equals(
                            dqRecords.get(indexDQ).get("Phone_1_Type"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Phone_1_Type:- " + maskingRecords.get(index).get("Phone_1_Type") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Masking Phone_1_Type:- " + maskingRecords.get(index).get("Phone_1_Type") +
                                " is not equal with DQ Phone_1_Type:- " + dqRecords.get(indexDQ).get("Phone_1_Type") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Masking Phone_1_Type:- " + maskingRecords.get(index).get("Phone_1_Type") +
                                " is not equal with DQ Phone_1_Type:- " + dqRecords.get(indexDQ).get("Phone_1_Type") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                    }

                    // Phone_2_Type
                    if (maskingRecords.get(index).get("Phone_2_Type").equals(
                            dqRecords.get(indexDQ).get("Phone_2_Type"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Phone_2_Type:- " + maskingRecords.get(index).get("Phone_2_Type") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Masking Phone_2_Type:- " + maskingRecords.get(index).get("Phone_2_Type") +
                                " is not equal with DQ Phone_2_Type:- " + dqRecords.get(indexDQ).get("Phone_2_Type") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Masking Phone_2_Type:- " + maskingRecords.get(index).get("Phone_2_Type") +
                                " is not equal with DQ Phone_2_Type:- " + dqRecords.get(indexDQ).get("Phone_2_Type") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                    }

                    // MI_Responsible_Party_Flag
                    if (maskingRecords.get(index).get("MI_Responsible_Party_Flag").equals(
                            dqRecords.get(indexDQ).get("MI_Responsible_Party_Flag"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule MI_Responsible_Party_Flag:- " + maskingRecords.get(index).get("MI_Responsible_Party_Flag") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Masking MI_Responsible_Party_Flag:- " + maskingRecords.get(index).get("MI_Responsible_Party_Flag") +
                                " is not equal with DQ MI_Responsible_Party_Flag:- " + dqRecords.get(indexDQ).get("MI_Responsible_Party_Flag") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Masking MI_Responsible_Party_Flag:- " + maskingRecords.get(index).get("MI_Responsible_Party_Flag") +
                                " is not equal with DQ MI_Responsible_Party_Flag:- " + dqRecords.get(indexDQ).get("MI_Responsible_Party_Flag") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                    }

                    // Non_Resident_Country_Code
                    if (maskingRecords.get(index).get("Non_Resident_Country_Code").equals(
                            dqRecords.get(indexDQ).get("Non_Resident_Country_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Non_Resident_Country_Code:- " + maskingRecords.get(index).get("Non_Resident_Country_Code") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Masking Non_Resident_Country_Code:- " + maskingRecords.get(index).get("Non_Resident_Country_Code") +
                                " is not equal with DQ Non_Resident_Country_Code:- " + dqRecords.get(indexDQ).get("Non_Resident_Country_Code") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Masking Non_Resident_Country_Code:- " + maskingRecords.get(index).get("Non_Resident_Country_Code") +
                                " is not equal with DQ Non_Resident_Country_Code:- " + dqRecords.get(indexDQ).get("Non_Resident_Country_Code") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-Masking");
                    }

                    if (failedMessages.size() != 0) {
                        String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                        if (!failedMessage.isEmpty()) {
                            maskingRecords.get(index).put("Result", failedMessage);
                            maskingRecords.get(index).put("FileName", fileName);
                            failedMessages = null;
                            failedMessages = new LinkedList<>();
                        }
                    } else {
                        maskingRecords.get(index).put("Result", "**PASS**");
                        maskingRecords.get(index).put("FileName", fileName);
                    }


                }

            } else {
                test.fail("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
                System.err.println("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
            }

            if (headers == null && maskingRecords.size() > 0) {
                headers = maskingRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : maskingRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + ".txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    @Then("^validate (\\d+)-Personal Identification masking content$")
    public void validatePersonalIdentificationMaskingContent(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_masking").mkdir();
        }
        List<String> fileNames = new DQRules().getMaskingFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        List<String> results = new LinkedList<>();
        String mi = null;

        String headers = null;
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            test.log("INFO", "-----Masking File Name--> " + fileName + " --------");
            System.out.println("-----Masking File Name--> " + fileName + " --------");

            String finalMi = mi;
            String dqFileName = new DQRules().getDQFileNames(tableId).stream().filter(x -> x.substring(0, 4).equals(finalMi))
                    .filter(x -> x.substring(18, 22).equals(fileName.substring(18, 22)))
                    .filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList()).get(0);
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + dqFileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/maskingFiles/" + fileName);
            List<Map<String, String>> maskingRecords = ReadTextFile.getDqSamples();
            List<String> depositorUniqueIdTable0100 = null;
            DQRules rules = new DQRules();
            List<String> subSystemFiles0100 = null;
            if (maskingRecords.size() == dqRecords.size()) {
                test.pass("**********Pass Statement********* Masking file and DQ file:- " + fileName +
                        " records size is equal and it's size is " + maskingRecords.size());
                for (int index = 0; index < maskingRecords.size(); index++) {
                    //  Below are the masking field
//                    Depositor_Unique_ID
                    // Depositor_Unique_ID
                    if ((!(maskingRecords.get(index).get("Depositor_Unique_ID").equals(
                            dqRecords.get(index).get("Depositor_Unique_ID")))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Depositor_Unique_ID")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Depositor_Unique_ID is masked, Masking Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " and DQ  Depositor_Unique_ID:- " + dqRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is same for both DQ and Masking");
                        System.err.println("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is same for both DQ and Masking which is not as per Rule-Masking");
                    }
                    //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                    //   Rule-10
                    if (depositorUniqueIdTable0100 == null) {
                        depositorUniqueIdTable0100 = new DQRules().getListOfString(mi, "0100", "Depositor_Unique_ID", fileName.substring(26, 29), "MASKING");
                        subSystemFiles0100 = new DQRules().getMaskingFileNames("0100").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).collect(Collectors.toList());
                        if (subSystemFiles0100.size() == 0) {
                            test.fail("--- FAIL ---> Sub_System_File-0100:- " + mi + "*0100" + fileName.substring(22, 29) +
                                    " is not found for Rule-304");

                        }
                    }
                    boolean depositorUniqueIdFlag = depositorUniqueIdTable0100.contains(maskingRecords.get(index).get("Depositor_Unique_ID"));
                    if (depositorUniqueIdFlag) {
                        test.pass("**********Pass Statement********* Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is as per Rule-10");

                    } else {
                        test.fail("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0100);
                        System.err.println("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0100);
                    }

                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Depositor_Unique_ID"))) {
                        test.pass("**********Pass Statement********* Depositor_Unique_Id:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + "is as per Rule-18");

                    } else {
                        test.fail(
                                "--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + "is not as per Rule-18");
                        System.err.println("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + "is not as per Rule-18");
                    }

                    // Personal_ID_Count
                    if (maskingRecords.get(index).get("Personal_ID_Count").equals(
                            dqRecords.get(index).get("Personal_ID_Count"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Personal_ID_Count:- " + maskingRecords.get(index).get("Personal_ID_Count") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Personal_ID_Count:- " + maskingRecords.get(index).get("Personal_ID_Count") +
                                " is not equal with DQ Personal_ID_Count:- " + dqRecords.get(index).get("Personal_ID_Count") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID")
                                + " is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Personal_ID_Count:- " + maskingRecords.get(index).get("Personal_ID_Count") +
                                " is not equal with DQ Personal_ID_Count:- " + dqRecords.get(index).get("Personal_ID_Count") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID")
                                + " is not as per Rule-Masking");
                    }

//                    Identification_Number
                    if (rules.checkMaskedOrNotMasked(dqRecords.get(index).get("Identification_Number"))) {
                        if (maskingRecords.get(index).get("Identification_Number").equals(
                                dqRecords.get(index).get("Identification_Number").replaceAll("\\s", "X"))) {
                            test.pass("**********Pass Statement********* DQ file and Masking file Identification_Number is masked, Masking Identification_Number:- " + maskingRecords.get(index).get("Identification_Number") +
                                    " and DQ Depositor_Unique_ID:- " + dqRecords.get(index).get("Depositor_Unique_ID"));
                        } else {
                            test.fail("--- FAIL ---> Identification_Number:- " + dqRecords.get(index).get("Identification_Number") +
                                    " and Masking file Identification_Number:- " + maskingRecords.get(index).get("Identification_Number") + " are different for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                    "which is not as per Rule-Masking");
                            System.err.println("--- FAIL ---> Identification_Number:- " + dqRecords.get(index).get("Identification_Number") +
                                    " and Masking file Identification_Number:- " + maskingRecords.get(index).get("Identification_Number") + " are different for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                    "which is not as per Rule-Masking");
                        }
                    } else if (maskingRecords.get(index).get("Identification_Number").isEmpty() && dqRecords.get(index).get("Identification_Number").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Identification_Number is Empty/Blank for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else if ((!(maskingRecords.get(index).get("Identification_Number").equals(
                            dqRecords.get(index).get("Identification_Number")))) &&
                            rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Identification_Number"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Identification_Number is masked, Masking Identification_Number:- " + maskingRecords.get(index).get("Identification_Number") +
                                " and DQ Identification_Number:- " + dqRecords.get(index).get("Identification_Number") + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Identification_Number:- " + maskingRecords.get(index).get("Identification_Number") +
                                " are same as Non-Masking file for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                "which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Identification_Number:- " + maskingRecords.get(index).get("Identification_Number") +
                                " are same as Non-Masking file for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                "which is not as per Rule-Masking");
                    }

                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Identification_Number"))) {
                        test.pass("**********Pass Statement********* Identification_Number:- " + maskingRecords.get(index).get("Identification_Number") + " is as per Rule-18" +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Identification_Number:- " + maskingRecords.get(index).get("Identification_Number") + "is not as per Rule-18");
                        System.err.println("--- FAIL ---> Identification_Number:- " + maskingRecords.get(index).get("Identification_Number") + "is not as per Rule-18");
                    }

                    // Personal_ID_Type_Code
                    if (maskingRecords.get(index).get("Personal_ID_Type_Code").equals(
                            dqRecords.get(index).get("Personal_ID_Type_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Personal_ID_Type_Code:- " + maskingRecords.get(index).get("Personal_ID_Type_Code") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Masking Personal_ID_Type_Code:- " + maskingRecords.get(index).get("Personal_ID_Type_Code") +
                                " is not equal with Non-Masking file Personal_ID_Type_Code:- " + dqRecords.get(index).get("Personal_ID_Type_Code") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Masking Personal_ID_Type_Code:- " + maskingRecords.get(index).get("Personal_ID_Type_Code") +
                                " is not equal with Non-Masking file Personal_ID_Type_Code:- " + dqRecords.get(index).get("Personal_ID_Type_Code") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                    }

                    if (failedMessages.size() != 0) {
                        String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                        if (!failedMessage.isEmpty()) {
                            maskingRecords.get(index).put("Result", failedMessage);
                            maskingRecords.get(index).put("FileName", fileName);
                            failedMessages = null;
                            failedMessages = new LinkedList<>();
                        }
                    } else {
                        maskingRecords.get(index).put("Result", "**PASS**");
                        maskingRecords.get(index).put("FileName", fileName);
                    }


                }

            } else {
                test.fail("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
                System.err.println("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
            }

            if (headers == null && maskingRecords.size() > 0) {
                headers = maskingRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : maskingRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + ".txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }


    @Then("^validate (\\d+)-Address Data masking content$")
    public void validateAddressDataMaskingContent(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_masking").mkdir();
        }
        List<String> fileNames = new DQRules().getMaskingFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        List<String> results = new LinkedList<>();
        String mi = null;

        List<String> fileValues = new DQRules().getMaskingFileNames(tableId);
        String headers = null;
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            test.log("INFO", "-----Masking File Name--> " + fileName + " --------");
            System.out.println("-----Masking File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/maskingFiles/" + fileName);
            List<Map<String, String>> maskingRecords = ReadTextFile.getDqSamples();
            List<String> depositorUniqueIdTable0100 = null;
            List<String> subSystemFiles0100 = null;
            DQRules rules = new DQRules();
            if (maskingRecords.size() == dqRecords.size()) {
                test.pass("**********Pass Statement********* Masking file and DQ file:- " + fileName +
                        " records size is equal and it's size is " + maskingRecords.size());
                for (int index = 0; index < maskingRecords.size(); index++) {
                    // Depositor_Unique_ID
                    if (!(maskingRecords.get(index).get("Depositor_Unique_ID").equals(
                            dqRecords.get(index).get("Depositor_Unique_ID"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Depositor_Unique_ID")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Depositor_Unique_ID is masked, Masking Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " and DQ  Depositor_Unique_ID:- " + dqRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + " is same as Non-Masking file which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + " is same as Non-Masking file which is not as per Rule-Masking");
                    }

                    //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                    //   Rule-10
                    if (depositorUniqueIdTable0100 == null) {
                        depositorUniqueIdTable0100 = new DQRules().getListOfString(mi, "0100", "Depositor_Unique_ID", fileName.substring(26, 29), "MASKING");
                        subSystemFiles0100 = new DQRules().getMaskingFileNames("0100").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).collect(Collectors.toList());
                        if (subSystemFiles0100.size() == 0) {
                            test.fail("--- FAIL ---> Sub_System_File-0100:- " + mi + "*0100" + fileName.substring(22, 29) +
                                    " is not found for Rule-304");

                        }
                    }
                    boolean depositorUniqueIdFlag = depositorUniqueIdTable0100.contains(maskingRecords.get(index).get("Depositor_Unique_ID"));
                    if (depositorUniqueIdFlag) {
                        test.pass("**********Pass Statement********* Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is as per Rule-10");

                    } else {
                        test.fail("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0100);
                        System.err.println("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-10 and not found in file " + subSystemFiles0100);
                    }
                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Depositor_Unique_ID"))) {
                        test.pass("**********Pass Statement********* Depositor_Unique_Id:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + "is as per Rule-18");

                    } else {
                        test.fail("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-18");
                        System.err.println("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-18");
                    }

                    // Address_Count
                    if (maskingRecords.get(index).get("Address_Count").equals(
                            dqRecords.get(index).get("Address_Count"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Address_Count:- " + maskingRecords.get(index).get("Address_Count") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Address_Count:- " + maskingRecords.get(index).get("Address_Count") +
                                " is not equal with DQ Address_Count:- " + dqRecords.get(index).get("Address_Count") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID"));
                        System.err.println("--- FAIL ---> Index- " + index + " Address_Count:- " + maskingRecords.get(index).get("Address_Count") +
                                " is not equal with DQ Address_Count:- " + dqRecords.get(index).get("Address_Count") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                    }

                    // Address_Type_Code
                    if (maskingRecords.get(index).get("Address_Type_Code").equals(
                            dqRecords.get(index).get("Address_Type_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Address_Type_Code:- " + maskingRecords.get(index).get("Address_Type_Code") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Address_Type_Code:- " + maskingRecords.get(index).get("Address_Type_Code") +
                                " is not equal with DQ Address_Type_Code:- " + dqRecords.get(index).get("Address_Type_Code") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Address_Type_Code:- " + maskingRecords.get(index).get("Address_Type_Code") +
                                " is not equal with DQ Address_Type_Code:- " + dqRecords.get(index).get("Address_Type_Code") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                    }

                    // Primary_Address_Flag
                    if (maskingRecords.get(index).get("Primary_Address_Flag").equals(
                            dqRecords.get(index).get("Primary_Address_Flag"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Primary_Address_Flag:- " + maskingRecords.get(index).get("Primary_Address_Flag") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Primary_Address_Flag:- " + maskingRecords.get(index).get("Primary_Address_Flag") +
                                " is not equal with DQ Primary_Address_Flag:- " + dqRecords.get(index).get("Primary_Address_Flag") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Primary_Address_Flag:- " + maskingRecords.get(index).get("Primary_Address_Flag") +
                                " is not equal with DQ Primary_Address_Flag:- " + dqRecords.get(index).get("Primary_Address_Flag") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                    }

                    // Address_Change
                    if (maskingRecords.get(index).get("Address_Change").equals(
                            dqRecords.get(index).get("Address_Change"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Address_Change:- " + maskingRecords.get(index).get("Address_Change") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Address_Change:- " + maskingRecords.get(index).get("Address_Change") +
                                " is not equal with DQ Address_Change:- " + dqRecords.get(index).get("Address_Change") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Address_Change:- " + maskingRecords.get(index).get("Address_Change") +
                                " is not equal with DQ Address_Change:- " + dqRecords.get(index).get("Address_Change") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                    }

                    // Undeliverable_Flag
                    if (maskingRecords.get(index).get("Undeliverable_Flag").equals(
                            dqRecords.get(index).get("Undeliverable_Flag"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Undeliverable_Flag:- " + maskingRecords.get(index).get("Undeliverable_Flag") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Undeliverable_Flag:- " + maskingRecords.get(index).get("Undeliverable_Flag") +
                                " is not equal with DQ Undeliverable_Flag:- " + dqRecords.get(index).get("Undeliverable_Flag") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Undeliverable_Flag:- " + maskingRecords.get(index).get("Undeliverable_Flag") +
                                " is not equal with DQ Undeliverable_Flag:- " + dqRecords.get(index).get("Undeliverable_Flag") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                    }

                    // Address_1
                    if (rules.checkMaskedOrNotMasked(dqRecords.get(index).get("Address_1"))) {
                        if (maskingRecords.get(index).get("Address_1").equals(
                                dqRecords.get(index).get("Address_1").replaceAll("\\s", "X"))) {
                            test.pass("**********Pass Statement********* DQ file and Masking file Address_1 is masked, Masking Address_1:- " + maskingRecords.get(index).get("Address_1") +
                                    " and DQ Depositor_Unique_ID:- " + dqRecords.get(index).get("Depositor_Unique_ID"));
                        } else {
                            test.fail("--- FAIL ---> Address_1:- " + dqRecords.get(index).get("Address_1") +
                                    " and Masking file Address_1:- " + maskingRecords.get(index).get("Address_1") +
                                    " are different for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                    " which is not as per Rule-Masking");
                            System.err.println("--- FAIL ---> Address_1:- " + dqRecords.get(index).get("Address_1") +
                                    " and Masking file Address_1:- " + maskingRecords.get(index).get("Address_1") +
                                    " are different for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                    " which is not as per Rule-Masking");
                        }
                    } else if (!(maskingRecords.get(index).get("Address_1").equals(
                            dqRecords.get(index).get("Address_1"))) &&
                            rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Address_1"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Address_1 is masked, Masking Address_1:- " + maskingRecords.get(index).get("Address_1") +
                                " and DQ  Address_1:- " + dqRecords.get(index).get("Address_1") +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else if (maskingRecords.get(index).get("Address_1").isEmpty() && dqRecords.get(index).get("Address_1").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Address_1 is Blank," +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Address_1:- " + maskingRecords.get(index).get("Address_1")
                                + " is same as Non-Masking file  for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Address_1:- " + maskingRecords.get(index).get("Address_1")
                                + " is same as Non-Masking file  for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Address_1"))) {
                        test.pass("**********Pass Statement********* Address_1:- " + maskingRecords.get(index).get("Address_1") + " is as per Rule-18" +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Address_1:- " + maskingRecords.get(index).get("Address_1") +
                                "is not as per Rule-18 for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                        System.err.println("--- FAIL ---> Address_1:- " + maskingRecords.get(index).get("Address_1") +
                                "is not as per Rule-18 for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    }

                    //  Address_2
                    if (rules.checkMaskedOrNotMasked(dqRecords.get(index).get("Address_2"))) {
                        if (maskingRecords.get(index).get("Address_2").equals(
                                dqRecords.get(index).get("Address_2").replaceAll("\\s", "X"))) {
                            test.pass("**********Pass Statement********* DQ file and Masking file Address_2 is masked, Masking Address_2:- " + maskingRecords.get(index).get("Address_2") +
                                    " and DQ Depositor_Unique_ID:- " + dqRecords.get(index).get("Depositor_Unique_ID"));
                        } else {
                            test.fail("--- FAIL ---> Address_2:- " + dqRecords.get(index).get("Address_2") +
                                    " and Masking file Address_2:- " + maskingRecords.get(index).get("Address_2") + " are different for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID")
                                    + " which is not as per Rule-Masking");
                            System.err.println("--- FAIL ---> Address_2:- " + dqRecords.get(index).get("Address_2") +
                                    " and Masking file Address_2:- " + maskingRecords.get(index).get("Address_2") + " are different for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID")
                                    + " which is not as per Rule-Masking");
                        }
                    } else if (!(maskingRecords.get(index).get("Address_2").equals(
                            dqRecords.get(index).get("Address_2"))) &&
                            rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Address_2"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Address_2 is masked, Masking Address_2:- " + maskingRecords.get(index).get("Address_2") +
                                " and DQ  Address_2:- " + dqRecords.get(index).get("Address_2"));
                    } else if (maskingRecords.get(index).get("Address_2").isEmpty() && dqRecords.get(index).get("Address_2").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Address_2 is Blank for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Address_2:- " + maskingRecords.get(index).get("Address_2") +
                                " is same as Non-Masking file for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Address_2:- " + maskingRecords.get(index).get("Address_2") +
                                " is same as Non-Masking file for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // City
                    if (maskingRecords.get(index).get("City").equals(
                            dqRecords.get(index).get("City"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule City:- " + maskingRecords.get(index).get("City") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " City:- " + maskingRecords.get(index).get("City") +
                                " is not equal with Non-Masking City:- " + dqRecords.get(index).get("City") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " City:- " + maskingRecords.get(index).get("City") +
                                " is not equal with Non-Masking City:- " + dqRecords.get(index).get("City") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Province
                    if (maskingRecords.get(index).get("Province").equals(
                            dqRecords.get(index).get("Province"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Province:- " + maskingRecords.get(index).get("Province") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Province:- " + maskingRecords.get(index).get("Province") +
                                " is not equal with DQ Province:- " + dqRecords.get(index).get("Province") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID")
                                + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Province:- " + maskingRecords.get(index).get("Province") +
                                " is not equal with DQ Province:- " + dqRecords.get(index).get("Province") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID")
                                + " which is not as per Rule-Masking");
                    }

                    //  Postal_Code
                    int lengthOfString = maskingRecords.get(index).get("Postal_Code").length();
                 /*   if ((!(maskingRecords.get(index).get("Postal_Code").equals(
                            dqRecords.get(index).get("Postal_Code")))) &&
                            rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Postal_Code").substring((lengthOfString - 3), lengthOfString))
                            && !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Postal_Code").substring(0, (lengthOfString - 3))))) {*/

                    if (maskingRecords.get(index).get("Postal_Code").isEmpty() && dqRecords.get(index).get("Postal_Code").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Postal_Code is Blank for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else if ((!(maskingRecords.get(index).get("Postal_Code").equals(
                            dqRecords.get(index).get("Postal_Code"))))) {
                        if (lengthOfString <= 3) {
                            if (!(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Postal_Code").substring(0, 3)))) {
                                test.pass("**********Pass Statement********* DQ file and Masking file Postal_Code is masked, Masking Postal_Code:- " + maskingRecords.get(index).get("Postal_Code") +
                                        " and DQ  Postal_Code:- " + dqRecords.get(index).get("Postal_Code"));
                            } else if (rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Postal_Code").substring(0, 3))) {
                                test.fail("--- FAIL ---> Postal_Code:- " + maskingRecords.get(index).get("Postal_Code") + " is same as Non-Masking " +
                                        "for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID")
                                        + " which is not as per Rule-Masking");
                                System.err.println("--- FAIL ---> Postal_Code:- " + maskingRecords.get(index).get("Postal_Code") + " is same as Non-Masking " +
                                        "for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID")
                                        + " which is not as per Rule-Masking");
                            } else {
                                test.fail("--- FAIL ---> Postal_Code:- " + maskingRecords.get(index).get("Postal_Code") + " is same as Non-Masking " +
                                        "for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID")
                                        + " which is not as per Rule-Masking");
                                System.err.println("--- FAIL ---> Postal_Code:- " + maskingRecords.get(index).get("Postal_Code") + " is same as Non-Masking " +
                                        "for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID")
                                        + " which is not as per Rule-Masking");
                            }
                        } else if (lengthOfString > 3) {
                            if (rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Postal_Code").replace(maskingRecords.get(index).get("Postal_Code").substring(0, 3), ""))
                                    && !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Postal_Code").substring(0, 3)))) {
                                test.pass("**********Pass Statement********* DQ file and Masking file Postal_Code is masked, Masking Postal_Code:- " + maskingRecords.get(index).get("Postal_Code") +
                                        " and DQ  Postal_Code:- " + dqRecords.get(index).get("Postal_Code"));
                            } else {
                                test.fail("--- FAIL ---> Postal_Code:- " + maskingRecords.get(index).get("Postal_Code") + " is same as Non-Masking " +
                                        "for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID")
                                        + " which is not as per Rule-Masking");
                                System.err.println("--- FAIL ---> Postal_Code:- " + maskingRecords.get(index).get("Postal_Code") + " is same as Non-Masking " +
                                        "for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID")
                                        + " which is not as per Rule-Masking");
                            }

                        }
                    }

                    // Country
                    if (maskingRecords.get(index).get("Country").equals(
                            dqRecords.get(index).get("Country"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Country:- " + maskingRecords.get(index).get("Country") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Country:- " + maskingRecords.get(index).get("Country") +
                                " is not equal with Non-Masking Country:- " + dqRecords.get(index).get("Country") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Country:- " + maskingRecords.get(index).get("Country") +
                                " is not equal with Non-Masking Country:- " + dqRecords.get(index).get("Country") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    //Rule:- 18 - It should not be blank
                    if (maskingRecords.get(index).get("Country").equalsIgnoreCase("CANADA") || maskingRecords.get(index).get("Country").equalsIgnoreCase("USA")) {
                        if (rules.noBlankRule18(maskingRecords.get(index).get("Postal_Code"))) {
                            test.pass("**********Pass Statement********* Postal_Code:- " + maskingRecords.get(index).get("Postal_Code") + " is as per Rule-18" +
                                    " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                        } else {
                            test.fail(
                                    "--- FAIL ---> Postal_Code:- " + maskingRecords.get(index).get("Postal_Code") +
                                            "is not as per Rule-18 for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                            System.err.println("--- FAIL ---> Postal_Code:- " + maskingRecords.get(index).get("Postal_Code") +
                                    "is not as per Rule-18 for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                        }

                    }
                    if (failedMessages.size() != 0) {
                        String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                        if (!failedMessage.isEmpty()) {
                            maskingRecords.get(index).put("Result", failedMessage);
                            maskingRecords.get(index).put("FileName", fileName);
                            failedMessages = null;
                            failedMessages = new LinkedList<>();
                        }
                    } else {
                        maskingRecords.get(index).put("Result", "**PASS**");
                        maskingRecords.get(index).put("FileName", fileName);
                    }


                }

            } else {
                test.fail("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
                System.err.println("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
            }

            if (headers == null && maskingRecords.size() > 0) {
                headers = maskingRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : maskingRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + ".txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    @Then("^validate (\\d+)-External Account Data masking content$")
    public void validateExternalAccountDataMaskingContent(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_masking").mkdir();
        }
        List<String> fileNames = new DQRules().getMaskingFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        List<String> results = new LinkedList<>();
        String mi = null;

        List<String> fileValues = new DQRules().getMaskingFileNames(tableId);
        String headers = null;
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            test.log("INFO", "-----Masking File Name--> " + fileName + " --------");
            System.out.println("-----Masking File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/maskingFiles/" + fileName);
            List<Map<String, String>> maskingRecords = ReadTextFile.getDqSamples();
            List<String> depositorUniqueIdTable0100 = null;
            List<String> subSystemFiles0100 = null;
            DQRules rules = new DQRules();
            if (maskingRecords.size() == dqRecords.size()) {
                test.pass("**********Pass Statement********* Masking file and DQ file:- " + fileName +
                        " records size is equal and it's size is " + maskingRecords.size());
                for (int index = 0; index < maskingRecords.size(); index++) {
                    // Depositor_Unique_ID
                    if (!(maskingRecords.get(index).get("Depositor_Unique_ID").equals(
                            dqRecords.get(index).get("Depositor_Unique_ID"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Depositor_Unique_ID")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Depositor_Unique_ID is masked, Masking Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " and DQ  Depositor_Unique_ID:- " + dqRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + " is same as Non-Masking file which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + " is same as Non-Masking file which is not as per Rule-Masking");
                    }

                    //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                    //   Rule-10
                    if (depositorUniqueIdTable0100 == null) {
                        depositorUniqueIdTable0100 = new DQRules().getListOfString(mi, "0100", "Depositor_Unique_ID", fileName.substring(26, 29), "MASKING");
                        subSystemFiles0100 = new DQRules().getDQFileNames("0100").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).collect(Collectors.toList());
                        if (subSystemFiles0100.size() == 0) {
                            test.fail("--- FAIL ---> Sub_System_File-0100:- " + mi + "*0100" + fileName.substring(22, 29) +
                                    " is not found for Rule-304");
                        }
                    }
                    if (subSystemFiles0100.size() != 0) {
                        boolean depositorUniqueIdFlag = depositorUniqueIdTable0100.contains(maskingRecords.get(index).get("Depositor_Unique_ID"));
                        if (depositorUniqueIdFlag) {
                            test.pass("**********Pass Statement********* Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                    " is as per Rule-10");

                        } else {
                            test.fail("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                    " is not as per Rule-10 and not found in file " + subSystemFiles0100);
                            System.err.println("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                    " is not as per Rule-10 and not found in file " + subSystemFiles0100);
                        }

                    }


                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Depositor_Unique_ID"))) {
                        test.pass("**********Pass Statement********* Depositor_Unique_Id:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + "is as per Rule-18");

                    } else {
                        test.fail("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-18");
                        System.err.println("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-18");
                    }

                    //------------------------------Payee_Name---------------------------------------
                    if (!(maskingRecords.get(index).get("Payee_Name").equals(
                            dqRecords.get(index).get("Payee_Name"))) &&
                            rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Payee_Name"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Payee_Name is masked, Masking Payee_Name:- " + maskingRecords.get(index).get("Payee_Name") +
                                " and DQ Payee_Name:- " + dqRecords.get(index).get("Payee_Name"));
                    } else {
                        test.fail("--- FAIL ---> Payee_Name:- " + maskingRecords.get(index).get("Payee_Name") + " and Non-Masking are same " +
                                "for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Payee_Name:- " + maskingRecords.get(index).get("Payee_Name") + " and Non-Masking are same " +
                                "for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Payee_Name"))) {
                        test.pass("**********Pass Statement********* Payee_Name:- " + maskingRecords.get(index).get("Payee_Name") + "is as per Rule-18");

                    } else {
                        test.fail("--- FAIL ---> Payee_Name:- " + maskingRecords.get(index).get("Payee_Name") +
                                " is not as per Rule-18 ");
                        System.err.println("--- FAIL ---> Payee_Name:- " + maskingRecords.get(index).get("Payee_Name") +
                                " is not as per Rule-18");
                    }
                    // Institution_Number
                    if (maskingRecords.get(index).get("Institution_Number").equals(
                            dqRecords.get(index).get("Institution_Number"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Institution_Number:- " + maskingRecords.get(index).get("Institution_Number") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Institution_Number:- " + maskingRecords.get(index).get("Institution_Number") +
                                " is not equal with DQ Institution_Number:- " + dqRecords.get(index).get("Institution_Number") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Institution_Number:- " + maskingRecords.get(index).get("Institution_Number") +
                                " is not equal with DQ Institution_Number:- " + dqRecords.get(index).get("Institution_Number") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                    }

                    //------------------------------Transit_Number---------------------------------------
                    if (!(maskingRecords.get(index).get("Transit_Number").equals(
                            dqRecords.get(index).get("Transit_Number"))) &&
                            rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Transit_Number"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Transit_Number is masked, Masking Transit_Number:- " + maskingRecords.get(index).get("Transit_Number") +
                                " and DQ Transit_Number:- " + dqRecords.get(index).get("Transit_Number"));
                    } else {
                        test.fail("--- FAIL ---> Transit_Number:- " + maskingRecords.get(index).get("Transit_Number") +
                                " is same as Non-Masking for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Transit_Number:- " + maskingRecords.get(index).get("Transit_Number") +
                                " is same as Non-Masking for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Transit_Number"))) {
                        test.pass("**********Pass Statement********* Transit_Number:- " + maskingRecords.get(index).get("Transit_Number") + "is as per Rule-18");

                    } else {
                        test.fail("--- FAIL ---> Transit_Number:- " + maskingRecords.get(index).get("Transit_Number") +
                                " is not as per Rule-18");
                        System.err.println("--- FAIL ---> Transit_Number:- " + maskingRecords.get(index).get("Transit_Number") +
                                " is not as per Rule-18");
                    }

                    //------------------------------Account_Number---------------------------------------
                    if (!(maskingRecords.get(index).get("Account_Number").equals(
                            dqRecords.get(index).get("Account_Number"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Account_Number")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Account_Number is masked, Masking Account_Number:- " + maskingRecords.get(index).get("Account_Number") +
                                " and DQ Account_Number:- " + dqRecords.get(index).get("Account_Number"));
                    } else {
                        test.fail("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") + " is same as Non-Masking for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") + " is same as Non-Masking for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Account_Number"))) {
                        test.pass("**********Pass Statement********* Account_Number:- " + maskingRecords.get(index).get("Account_Number") + "is as per Rule-18");

                    } else {
                        test.fail("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") +
                                " is not as per Rule-18");
                        System.err.println("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") +
                                " is not as per Rule-18");
                    }

                    // Currency_Code
                    if (maskingRecords.get(index).get("Currency_Code").equals(
                            dqRecords.get(index).get("Currency_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Currency_Code:- " + maskingRecords.get(index).get("Currency_Code") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Currency_Code:- " + maskingRecords.get(index).get("Currency_Code") +
                                " is not equal with DQ Currency_Code:- " + dqRecords.get(index).get("Currency_Code") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Masking Currency_Code:- " + maskingRecords.get(index).get("Currency_Code") +
                                " is not equal with DQ Currency_Code:- " + dqRecords.get(index).get("Currency_Code") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                    }

                    // Joint_Account_Flag
                    if (maskingRecords.get(index).get("Joint_Account_Flag").equals(
                            dqRecords.get(index).get("Joint_Account_Flag"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Joint_Account_Flag:- " + maskingRecords.get(index).get("Joint_Account_Flag") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Joint_Account_Flag:- " + maskingRecords.get(index).get("Joint_Account_Flag") +
                                " is not equal with DQ Joint_Account_Flag:- " + dqRecords.get(index).get("Joint_Account_Flag") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Masking Joint_Account_Flag:- " + maskingRecords.get(index).get("Joint_Account_Flag") +
                                " is not equal with DQ Joint_Account_Flag:- " + dqRecords.get(index).get("Joint_Account_Flag") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                    }

                    // Start_Date
                    if (maskingRecords.get(index).get("Start_Date").equals(
                            dqRecords.get(index).get("Start_Date"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Start_Date:- " + maskingRecords.get(index).get("Start_Date") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Start_Date:- " + maskingRecords.get(index).get("Start_Date") +
                                " is not equal with DQ Start_Date:- " + dqRecords.get(index).get("Start_Date") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Start_Date:- " + maskingRecords.get(index).get("Start_Date") +
                                " is not equal with DQ Start_Date:- " + dqRecords.get(index).get("Start_Date") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                    }

                    // Last_Funds_Transfer
                    if (maskingRecords.get(index).get("Last_Funds_Transfer").equals(
                            dqRecords.get(index).get("Last_Funds_Transfer"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Last_Funds_Transfer:- " + maskingRecords.get(index).get("Last_Funds_Transfer") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Last_Funds_Transfer:- " + maskingRecords.get(index).get("Last_Funds_Transfer") +
                                " is not equal with DQ Last_Funds_Transfer:- " + dqRecords.get(index).get("Last_Funds_Transfer") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Last_Funds_Transfer:- " + maskingRecords.get(index).get("Last_Funds_Transfer") +
                                " is not equal with DQ Last_Funds_Transfer:- " + dqRecords.get(index).get("Last_Funds_Transfer") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                    }

                    // Last_Outbound_Funds_Transfer
                    if (maskingRecords.get(index).get("Last_Outbound_Funds_Transfer").equals(
                            dqRecords.get(index).get("Last_Outbound_Funds_Transfer"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Last_Outbound_Funds_Transfer:- " + maskingRecords.get(index).get("Last_Outbound_Funds_Transfer") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Last_Outbound_Funds_Transfer:- " + maskingRecords.get(index).get("Last_Outbound_Funds_Transfer") +
                                " is not equal with DQ Last_Outbound_Funds_Transfer:- " + dqRecords.get(index).get("Last_Outbound_Funds_Transfer") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Last_Outbound_Funds_Transfer:- " + maskingRecords.get(index).get("Last_Outbound_Funds_Transfer") +
                                " is not equal with DQ Last_Outbound_Funds_Transfer:- " + dqRecords.get(index).get("Last_Outbound_Funds_Transfer") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                    }

                    // Next_Outbound_Funds_Transfer
                    if (maskingRecords.get(index).get("Next_Outbound_Funds_Transfer").equals(
                            dqRecords.get(index).get("Next_Outbound_Funds_Transfer"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Next_Outbound_Funds_Transfer:- " + maskingRecords.get(index).get("Next_Outbound_Funds_Transfer") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Next_Outbound_Funds_Transfer:- " + maskingRecords.get(index).get("Next_Outbound_Funds_Transfer") +
                                " is not equal with DQ Next_Outbound_Funds_Transfer:- " + dqRecords.get(index).get("Next_Outbound_Funds_Transfer") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Next_Outbound_Funds_Transfer:- " + maskingRecords.get(index).get("Next_Outbound_Funds_Transfer") +
                                " is not equal with DQ Next_Outbound_Funds_Transfer:- " + dqRecords.get(index).get("Next_Outbound_Funds_Transfer") + " for Depositor_Unique_ID:- "
                                + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                    }

                    if (failedMessages.size() != 0) {
                        String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                        if (!failedMessage.isEmpty()) {
                            maskingRecords.get(index).put("Result", failedMessage);
                            maskingRecords.get(index).put("FileName", fileName);
                            failedMessages = null;
                            failedMessages = new LinkedList<>();
                        }
                    } else {
                        maskingRecords.get(index).put("Result", "**PASS**");
                        maskingRecords.get(index).put("FileName", fileName);
                    }


                }

            } else {
                test.fail("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
                System.err.println("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
            }

            if (headers == null && maskingRecords.size() > 0) {
                headers = maskingRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : maskingRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + ".txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }


    @Given("^validate (\\d+)-Deposit Account Data masking content$")
    public void validateDepositAccountDataMaskingContent(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_masking").mkdir();
        }
        List<String> fileNames = new DQRules().getMaskingFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        List<String> results = new LinkedList<>();
        String mi = null;

        List<String> fileValues = new DQRules().getMaskingFileNames(tableId);
        String headers = null;
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            test.log("INFO", "-----Masking File Name--> " + fileName + " --------");
            System.out.println("-----Masking File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/maskingFiles/" + fileName);
            List<Map<String, String>> maskingRecords = ReadTextFile.getDqSamples();
            List<String> accountUniqueIdTable0800 = null;
            List<String> subSystemFiles0800 = null;
            List<String> accountUniqueIdTable0900 = null;
            List<String> subSystemFiles0900 = null;
            DQRules rules = new DQRules();
            if (maskingRecords.size() == dqRecords.size()) {
                test.pass("**********Pass Statement********* Masking file and DQ file:- " + fileName +
                        " records size is equal and it's size is " + maskingRecords.size());
                for (int index = 0; index < maskingRecords.size(); index++) {
                    //  Below are the masking field
                    // Account_Unique_ID
                    // Account_Number
                    // Registered_Plan_Number
                    if (!(maskingRecords.get(index).get("Account_Unique_ID").equals(
                            dqRecords.get(index).get("Account_Unique_ID"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Account_Unique_ID")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Account_Unique_ID is masked, Masking Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " and DQ  Account_Unique_ID:- " + dqRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID")
                                + " Non-Masking file is same which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID")
                                + " Non-Masking file is same which is not as per Rule-Masking");
                    }

                    //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                    //   Rule-10
                    if (accountUniqueIdTable0800 == null) {
                        accountUniqueIdTable0800 = new DQRules().getListOfString(mi, "0800", "Account_Unique_ID", fileName.substring(26, 29), "MASKING");
                        subSystemFiles0800 = new DQRules().getDQFileNames("0800").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                        if (subSystemFiles0800.size() == 0) {
                            test.fail("--- FAIL ---> Sub_System_File-0800:- " + mi + "*0800" + fileName.substring(22, 29) +
                                    " is not found for Rule-304");

                        }
                    }
                    if (subSystemFiles0800.size() != 0) {
                        boolean accountUniqueIdFlag800 = accountUniqueIdTable0800.contains(maskingRecords.get(index).get("Account_Unique_ID"));
                        if (accountUniqueIdFlag800) {
                            test.pass("**********Pass Statement********* Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + " is as per Rule-10");
                        } else {
                            test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                    " is not as per Rule-10 and not found in file " + subSystemFiles0800);

                            System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                    " is not as per Rule-10 and not found in file " + subSystemFiles0800);
                        }

                    }


                    //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                    //   Rule-10
                    if (accountUniqueIdTable0900 == null) {
                        accountUniqueIdTable0900 = new DQRules().getListOfString(mi, "0900", "Account_Unique_ID", fileName.substring(26, 29), "MASKING");
                        subSystemFiles0900 = new DQRules().getDQFileNames("0900").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                        if (subSystemFiles0900.size() == 0) {
                            test.fail("--- FAIL ---> Sub_System_File-0900:- " + mi + "*0900" + fileName.substring(22, 29) +
                                    " is not found for Rule-304");

                        }
                    }
                    if (subSystemFiles0900.size() != 0) {
                        boolean accountUniqueIdFlag900 = accountUniqueIdTable0900.contains(maskingRecords.get(index).get("Account_Unique_ID"));
                        //                Verifying value in table Id - 0900
                        if (accountUniqueIdFlag900) {
                            test.pass("**********Pass Statement********* Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + " is as per Rule-10");
                        } else {
                            test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + " is not as per Rule-10 and not found in file " + subSystemFiles0900);
                            System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + " is not as per Rule-10 and not found in file " + subSystemFiles0900);
                        }

                    }


                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Account_Unique_ID"))) {
                        test.pass("**********Pass Statement********* Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + "is as per Rule-18");

                    } else {
                        test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " is not as per Rule-18");
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " is not as per Rule-18");
                    }
                    //Account_Number
                    if (!(maskingRecords.get(index).get("Account_Number").equals(
                            dqRecords.get(index).get("Account_Number"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Account_Number")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Account_Number is masked, Masking Account_Number:- " + maskingRecords.get(index).get("Account_Number") +
                                " and DQ Account_Number:- " + dqRecords.get(index).get("Account_Number"));
                    } else {
                        test.fail("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") + "Non-Masking file Account_Number is same for " +
                                "Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID")
                                + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") + "Non-Masking file Account_Number is same for " +
                                "Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID")
                                + " which is not as per Rule-Masking");
                    }

                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Account_Number"))) {
                        test.pass("**********Pass Statement********* Account_Number:- " + maskingRecords.get(index).get("Account_Number") + "is as per Rule-18");

                    } else {
                        test.fail("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") +
                                " is not as per Rule-18");
                        System.err.println("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") +
                                " is not as per Rule-18");
                    }

                    // Account_Branch
                    if (maskingRecords.get(index).get("Account_Branch").equals(
                            dqRecords.get(index).get("Account_Branch"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Account_Branch:- " + maskingRecords.get(index).get("Account_Branch") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Account_Branch:- " + maskingRecords.get(index).get("Account_Branch") +
                                " is not equal with Non-Masking Account_Branch:- " + dqRecords.get(index).get("Account_Branch") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Account_Branch:- " + maskingRecords.get(index).get("Account_Branch") +
                                " is not equal with Non-Masking Account_Branch:- " + dqRecords.get(index).get("Account_Branch") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Product_Code
                    if (maskingRecords.get(index).get("Product_Code").equals(
                            dqRecords.get(index).get("Product_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Product_Code:- " + maskingRecords.get(index).get("Product_Code") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Product_Code:- " + maskingRecords.get(index).get("Product_Code") +
                                " is not equal with Non-Masking Product_Code:- " + dqRecords.get(index).get("Product_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Product_Code:- " + maskingRecords.get(index).get("Product_Code") +
                                " is not equal with Non-Masking Product_Code:- " + dqRecords.get(index).get("Product_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Registered_Plan_Type_Code
                    if (maskingRecords.get(index).get("Registered_Plan_Type_Code").equals(
                            dqRecords.get(index).get("Registered_Plan_Type_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Registered_Plan_Type_Code:- " + maskingRecords.get(index).get("Registered_Plan_Type_Code") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Registered_Plan_Type_Code:- " + maskingRecords.get(index).get("Registered_Plan_Type_Code") +
                                " is not equal with Non-Masking Registered_Plan_Type_Code:- " + dqRecords.get(index).get("Registered_Plan_Type_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Registered_Plan_Type_Code:- " + maskingRecords.get(index).get("Registered_Plan_Type_Code") +
                                " is not equal with Non-Masking Registered_Plan_Type_Code:- " + dqRecords.get(index).get("Registered_Plan_Type_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    //Registered_Plan_Number
                    if (!(maskingRecords.get(index).get("Registered_Plan_Number").equals(
                            dqRecords.get(index).get("Registered_Plan_Number"))) &&
                            rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Registered_Plan_Number"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Registered_Plan_Number is masked, Masking Registered_Plan_Number:- " + maskingRecords.get(index).get("Registered_Plan_Number") +
                                " and DQ Registered_Plan_Number:- " + dqRecords.get(index).get("Registered_Plan_Number"));
                    } else if (maskingRecords.get(index).get("Registered_Plan_Number").isEmpty() && dqRecords.get(index).get("Registered_Plan_Number").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Registered_Plan_Number is Blank");
                    } else {
                        test.fail("--- FAIL ---> Registered_Plan_Number:- " + maskingRecords.get(index).get("Registered_Plan_Number") + " is same as Non-Masking which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Registered_Plan_Number:- " + maskingRecords.get(index).get("Registered_Plan_Number") + " is same as Non-Masking which is not as per Rule-Masking");
                    }

                    // Currency_Code
                    if (maskingRecords.get(index).get("Currency_Code").equals(
                            dqRecords.get(index).get("Currency_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Currency_Code:- " + maskingRecords.get(index).get("Currency_Code") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Currency_Code:- " + maskingRecords.get(index).get("Currency_Code") +
                                " is not equal with Non-Masking Currency_Code:- " + dqRecords.get(index).get("Currency_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Currency_Code:- " + maskingRecords.get(index).get("Currency_Code") +
                                " is not equal with Non-Masking Currency_Code:- " + dqRecords.get(index).get("Currency_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Insurance_Determination_Category_Type_Code
                    if (maskingRecords.get(index).get("Insurance_Determination_Category_Type_Code").equals(
                            dqRecords.get(index).get("Insurance_Determination_Category_Type_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Insurance_Determination_Category_Type_Code:- " + maskingRecords.get(index).get("Insurance_Determination_Category_Type_Code") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Insurance_Determination_Category_Type_Code:- " + maskingRecords.get(index).get("Insurance_Determination_Category_Type_Code") +
                                " is not equal with Non-Masking Insurance_Determination_Category_Type_Code:- " + dqRecords.get(index).get("Insurance_Determination_Category_Type_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Insurance_Determination_Category_Type_Code:- " + maskingRecords.get(index).get("Insurance_Determination_Category_Type_Code") +
                                " is not equal with Non-Masking Insurance_Determination_Category_Type_Code:- " + dqRecords.get(index).get("Insurance_Determination_Category_Type_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Account_Balance
                    if (maskingRecords.get(index).get("Account_Balance").equals(
                            dqRecords.get(index).get("Account_Balance"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Account_Balance:- " + maskingRecords.get(index).get("Account_Balance") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Account_Balance:- " + maskingRecords.get(index).get("Account_Balance") +
                                " is not equal with Non-Masking Account_Balance:- " + dqRecords.get(index).get("Account_Balance") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Account_Balance:- " + maskingRecords.get(index).get("Account_Balance") +
                                " is not equal with Non-Masking Account_Balance:- " + dqRecords.get(index).get("Account_Balance") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Accessible_Balance
                    if (maskingRecords.get(index).get("Accessible_Balance").equals(
                            dqRecords.get(index).get("Accessible_Balance"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Accessible_Balance:- " + maskingRecords.get(index).get("Accessible_Balance") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Accessible_Balance:- " + maskingRecords.get(index).get("Accessible_Balance") +
                                " is not equal with Non-Masking Accessible_Balance:- " + dqRecords.get(index).get("Accessible_Balance") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Accessible_Balance:- " + maskingRecords.get(index).get("Accessible_Balance") +
                                " is not equal with Non-Masking Accessible_Balance:- " + dqRecords.get(index).get("Accessible_Balance") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Maturity_Date
                    if (maskingRecords.get(index).get("Maturity_Date").equals(
                            dqRecords.get(index).get("Maturity_Date"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Maturity_Date:- " + maskingRecords.get(index).get("Maturity_Date") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Maturity_Date:- " + maskingRecords.get(index).get("Maturity_Date") +
                                " is not equal with Non-Masking Maturity_Date:- " + dqRecords.get(index).get("Maturity_Date") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Maturity_Date:- " + maskingRecords.get(index).get("Maturity_Date") +
                                " is not equal with Non-Masking Maturity_Date:- " + dqRecords.get(index).get("Maturity_Date") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Account_Status_Code
                    if (maskingRecords.get(index).get("Account_Status_Code").equals(
                            dqRecords.get(index).get("Account_Status_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Account_Status_Code:- " + maskingRecords.get(index).get("Account_Status_Code") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Account_Status_Code:- " + maskingRecords.get(index).get("Account_Status_Code") +
                                " is not equal with Non-Masking Account_Status_Code:- " + dqRecords.get(index).get("Account_Status_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Account_Status_Code:- " + maskingRecords.get(index).get("Account_Status_Code") +
                                " is not equal with Non-Masking Account_Status_Code:- " + dqRecords.get(index).get("Account_Status_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Trust_Account_Type_Code
                    if (maskingRecords.get(index).get("Trust_Account_Type_Code").equals(
                            dqRecords.get(index).get("Trust_Account_Type_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Trust_Account_Type_Code:- " + maskingRecords.get(index).get("Trust_Account_Type_Code") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Trust_Account_Type_Code:- " + maskingRecords.get(index).get("Trust_Account_Type_Code") +
                                " is not equal with Non-Masking Trust_Account_Type_Code:- " + dqRecords.get(index).get("Trust_Account_Type_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Trust_Account_Type_Code:- " + maskingRecords.get(index).get("Trust_Account_Type_Code") +
                                " is not equal with Non-Masking Trust_Account_Type_Code:- " + dqRecords.get(index).get("Trust_Account_Type_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // CDIC_Hold_Status_Code
                    if (maskingRecords.get(index).get("CDIC_Hold_Status_Code").equals(
                            dqRecords.get(index).get("CDIC_Hold_Status_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule CDIC_Hold_Status_Code:- " + maskingRecords.get(index).get("CDIC_Hold_Status_Code") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " CDIC_Hold_Status_Code:- " + maskingRecords.get(index).get("CDIC_Hold_Status_Code") +
                                " is not equal with Non-Masking CDIC_Hold_Status_Code:- " + dqRecords.get(index).get("CDIC_Hold_Status_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " CDIC_Hold_Status_Code:- " + maskingRecords.get(index).get("CDIC_Hold_Status_Code") +
                                " is not equal with Non-Masking CDIC_Hold_Status_Code:- " + dqRecords.get(index).get("CDIC_Hold_Status_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Joint_Account_Flag
                    if (maskingRecords.get(index).get("Joint_Account_Flag").equals(
                            dqRecords.get(index).get("Joint_Account_Flag"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Joint_Account_Flag:- " + maskingRecords.get(index).get("Joint_Account_Flag") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Joint_Account_Flag:- " + maskingRecords.get(index).get("Joint_Account_Flag") +
                                " is not equal with Non-Masking Joint_Account_Flag:- " + dqRecords.get(index).get("Joint_Account_Flag") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Joint_Account_Flag:- " + maskingRecords.get(index).get("Joint_Account_Flag") +
                                " is not equal with Non-Masking Joint_Account_Flag:- " + dqRecords.get(index).get("Joint_Account_Flag") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Clearing_Account_Code
                    if (maskingRecords.get(index).get("Clearing_Account_Code").equals(
                            dqRecords.get(index).get("Clearing_Account_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Clearing_Account_Code:- " + maskingRecords.get(index).get("Clearing_Account_Code") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Clearing_Account_Code:- " + maskingRecords.get(index).get("Clearing_Account_Code") +
                                " is not equal with Non-Masking Clearing_Account_Code:- " + dqRecords.get(index).get("Clearing_Account_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Clearing_Account_Code:- " + maskingRecords.get(index).get("Clearing_Account_Code") +
                                " is not equal with Non-Masking Clearing_Account_Code:- " + dqRecords.get(index).get("Clearing_Account_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Account_Type_Code
                    if (maskingRecords.get(index).get("Account_Type_Code").equals(
                            dqRecords.get(index).get("Account_Type_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Account_Type_Code:- " + maskingRecords.get(index).get("Account_Type_Code") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Account_Type_Code:- " + maskingRecords.get(index).get("Account_Type_Code") +
                                " is not equal with Non-Masking Account_Type_Code:- " + dqRecords.get(index).get("Account_Type_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Account_Type_Code:- " + maskingRecords.get(index).get("Account_Type_Code") +
                                " is not equal with Non-Masking Account_Type_Code:- " + dqRecords.get(index).get("Account_Type_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // MI_Issued_Registered_Account_Flag
                    if (maskingRecords.get(index).get("MI_Issued_Registered_Account_Flag").equals(
                            dqRecords.get(index).get("MI_Issued_Registered_Account_Flag"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule MI_Issued_Registered_Account_Flag:- " + maskingRecords.get(index).get("MI_Issued_Registered_Account_Flag") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " MI_Issued_Registered_Account_Flag:- " + maskingRecords.get(index).get("MI_Issued_Registered_Account_Flag") +
                                " is not equal with Non-Masking MI_Issued_Registered_Account_Flag:- " + dqRecords.get(index).get("MI_Issued_Registered_Account_Flag") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " MI_Issued_Registered_Account_Flag:- " + maskingRecords.get(index).get("MI_Issued_Registered_Account_Flag") +
                                " is not equal with Non-Masking MI_Issued_Registered_Account_Flag:- " + dqRecords.get(index).get("MI_Issued_Registered_Account_Flag") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // MI_Related_Deposit_Flag
                    if (maskingRecords.get(index).get("MI_Related_Deposit_Flag").equals(
                            dqRecords.get(index).get("MI_Related_Deposit_Flag"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule MI_Related_Deposit_Flag:- " + maskingRecords.get(index).get("MI_Related_Deposit_Flag") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " MI_Related_Deposit_Flag:- " + maskingRecords.get(index).get("MI_Related_Deposit_Flag") +
                                " is not equal with Non-Masking MI_Related_Deposit_Flag:- " + dqRecords.get(index).get("MI_Related_Deposit_Flag") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " MI_Related_Deposit_Flag:- " + maskingRecords.get(index).get("MI_Related_Deposit_Flag") +
                                " is not equal with Non-Masking MI_Related_Deposit_Flag:- " + dqRecords.get(index).get("MI_Related_Deposit_Flag") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    if (failedMessages.size() != 0) {
                        String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                        if (!failedMessage.isEmpty()) {
                            maskingRecords.get(index).put("Result", failedMessage);
                            maskingRecords.get(index).put("FileName", fileName);
                            failedMessages = null;
                            failedMessages = new LinkedList<>();
                        }
                    } else {
                        maskingRecords.get(index).put("Result", "**PASS**");
                        maskingRecords.get(index).put("FileName", fileName);
                    }


                }

            } else {
                test.fail("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
                System.err.println("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
            }

            if (headers == null && maskingRecords.size() > 0) {
                headers = maskingRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : maskingRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + ".txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    @Then("^validate (\\d+)-MI Deposit Hold Data masking content$")
    public void validateMIDepositHoldDataMaskingContent(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_masking").mkdir();
        }
        List<String> fileNames = new DQRules().getMaskingFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        List<String> results = new LinkedList<>();
        String mi = null;

        List<String> fileValues = new DQRules().getMaskingFileNames(tableId);
        String headers = null;
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            test.log("INFO", "-----Masking File Name--> " + fileName + " --------");
            System.out.println("-----Masking File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/maskingFiles/" + fileName);
            List<Map<String, String>> maskingRecords = ReadTextFile.getDqSamples();
            List<String> accountUniqueIdTable0130 = null;
            List<String> subSystemFiles0130 = null;
            DQRules rules = new DQRules();
            if (maskingRecords.size() == dqRecords.size()) {
                test.pass("**********Pass Statement********* Masking file and DQ file:- " + fileName +
                        " records size is equal and it's size is " + maskingRecords.size());
                for (int index = 0; index < maskingRecords.size(); index++) {
                    if (!(maskingRecords.get(index).get("Account_Unique_ID").equals(
                            dqRecords.get(index).get("Account_Unique_ID"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Account_Unique_ID")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Account_Unique_ID is masked, Masking Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " and DQ  Account_Unique_ID:- " + dqRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " is same as Non-Masking file which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " is same as Non-Masking file which is not as per Rule-Masking");
                    }

                    //Rule:- 10 - Values present the file should be present in other file, Which primary key
                    //                for other file where as foreign key for current file verification
                    //   Rule-10
                    if (accountUniqueIdTable0130 == null) {
                        accountUniqueIdTable0130 = new DQRules().getListOfString(mi, "0130", "Account_Unique_ID", fileName.substring(26, 29), "MASKING");
                        subSystemFiles0130 = new DQRules().getDQFileNames("0130").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                        if (subSystemFiles0130.size() == 0) {
                            test.fail("--- FAIL ---> Sub_System_File-0130:- " + mi + "*0130" + fileName.substring(22, 29) +
                                    " is not found for Rule-304");

                        }
                    }
                    if (subSystemFiles0130.size() != 0) {
                        boolean accountUniqueIdFlag130 = accountUniqueIdTable0130.contains(maskingRecords.get(index).get("Account_Unique_ID"));
                        if (accountUniqueIdFlag130) {
                            test.pass("**********Pass Statement********* Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                    " is as per Rule-10");

                        } else {
                            test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                    " is not as per Rule-10 and not found in file " + subSystemFiles0130);
                            System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                    " is not as per Rule-10 and not found in file " + subSystemFiles0130);
                        }

                    }

                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Account_Unique_ID"))) {
                        test.pass("**********Pass Statement********* Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + "is as per Rule-18");

                    } else {
                        test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " is not as per Rule-18");
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " is not as per Rule-18");
                    }

                    // MI_Deposit_Hold_Code
                    if (maskingRecords.get(index).get("MI_Deposit_Hold_Code").equals(
                            dqRecords.get(index).get("MI_Deposit_Hold_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule MI_Deposit_Hold_Code:- " + maskingRecords.get(index).get("MI_Deposit_Hold_Code") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " MI_Deposit_Hold_Code:- " + maskingRecords.get(index).get("MI_Deposit_Hold_Code") +
                                " is not equal with Non-Masking MI_Deposit_Hold_Code:- " + dqRecords.get(index).get("MI_Deposit_Hold_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " MI_Deposit_Hold_Code:- " + maskingRecords.get(index).get("MI_Deposit_Hold_Code") +
                                " is not equal with Non-Masking MI_Deposit_Hold_Code:- " + dqRecords.get(index).get("MI_Deposit_Hold_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // MI_Deposit_Hold_Scheduled_Release_Date
                    if (maskingRecords.get(index).get("MI_Deposit_Hold_Scheduled_Release_Date").equals(
                            dqRecords.get(index).get("MI_Deposit_Hold_Scheduled_Release_Date"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule MI_Deposit_Hold_Scheduled_Release_Date:- " + maskingRecords.get(index).get("MI_Deposit_Hold_Scheduled_Release_Date") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " MI_Deposit_Hold_Scheduled_Release_Date:- " + maskingRecords.get(index).get("MI_Deposit_Hold_Scheduled_Release_Date") +
                                " is not equal with Non-Masking MI_Deposit_Hold_Scheduled_Release_Date:- " + dqRecords.get(index).get("MI_Deposit_Hold_Scheduled_Release_Date") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " MI_Deposit_Hold_Scheduled_Release_Date:- " + maskingRecords.get(index).get("MI_Deposit_Hold_Scheduled_Release_Date") +
                                " is not equal with Non-Masking MI_Deposit_Hold_Scheduled_Release_Date:- " + dqRecords.get(index).get("MI_Deposit_Hold_Scheduled_Release_Date") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Currency_Code
                    if (maskingRecords.get(index).get("Currency_Code").equals(
                            dqRecords.get(index).get("Currency_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Currency_Code:- " + maskingRecords.get(index).get("Currency_Code") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Currency_Code:- " + maskingRecords.get(index).get("Currency_Code") +
                                " is not equal with Non-Masking Currency_Code:- " + dqRecords.get(index).get("Currency_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Currency_Code:- " + maskingRecords.get(index).get("Currency_Code") +
                                " is not equal with Non-Masking Currency_Code:- " + dqRecords.get(index).get("Currency_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // MI_Deposit_Hold_Amount
                    if (maskingRecords.get(index).get("MI_Deposit_Hold_Amount").equals(
                            dqRecords.get(index).get("MI_Deposit_Hold_Amount"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule MI_Deposit_Hold_Amount:- " + maskingRecords.get(index).get("MI_Deposit_Hold_Amount") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " MI_Deposit_Hold_Amount:- " + maskingRecords.get(index).get("MI_Deposit_Hold_Amount") +
                                " is not equal with Non-Masking MI_Deposit_Hold_Amount:- " + dqRecords.get(index).get("MI_Deposit_Hold_Amount") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " MI_Deposit_Hold_Amount:- " + maskingRecords.get(index).get("MI_Deposit_Hold_Amount") +
                                " is not equal with Non-Masking MI_Deposit_Hold_Amount:- " + dqRecords.get(index).get("MI_Deposit_Hold_Amount") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    if (failedMessages.size() != 0) {
                        String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                        if (!failedMessage.isEmpty()) {
                            maskingRecords.get(index).put("Result", failedMessage);
                            maskingRecords.get(index).put("FileName", fileName);
                            failedMessages = null;
                            failedMessages = new LinkedList<>();
                        }
                    } else {
                        maskingRecords.get(index).put("Result", "**PASS**");
                        maskingRecords.get(index).put("FileName", fileName);
                    }


                }

            } else {
                test.fail("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
                System.err.println("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
            }

            if (headers == null && maskingRecords.size() > 0) {
                headers = maskingRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : maskingRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + ".txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    private List<Map<String, String>> getIndexOfStringFromFile(List<Map<String, String>> dqRecords, String columnName, String completeString) {
        List<Map<String, String>> indexesOfStrings = new LinkedList<>();
        for (int row = 0; row < dqRecords.size(); row++) {
            Map<String, String> record = dqRecords.get(row);
//            for (int column = 0; column < record.size(); column++) {
            if (record.get(columnName).equals(completeString)) {
                Map<String, String> matchedIndexString = new LinkedHashMap<>();
                int rowCount = row;
                matchedIndexString.put("RowCount", String.valueOf(rowCount));
                matchedIndexString.put("ColumnName", columnName);
                indexesOfStrings.add(matchedIndexString);
            }
//        }
        }
        return indexesOfStrings;
    }

    private void verifyEncryptedIdentically(List<Map<String, String>> masking,
                                            List<Map<String, String>> indexOfString,
                                            String column, String expectedValue) {

        for (Map<String, String> matchedRecord : indexOfString) {
            int rowCount = Integer.parseInt(matchedRecord.get("RowCount"));
            String columnName = matchedRecord.get("ColumnName");
            if (!masking.get(rowCount).get(columnName).equals(expectedValue)) {
                test.fail("--- FAIL ---> " + column + ":- " + masking.get(rowCount).get(columnName) +
                        " is not encrypted properly for Masking file row number: " + rowCount +
                        " Column Name: " + columnName +
                        " Actual Value:- " + masking.get(rowCount).get(columnName) +
                        " Expected Value :- " + expectedValue);

                System.err.println("--- FAIL ---> " + column + ":- " + masking.get(rowCount).get(columnName) +
                        " is not encrypted properly for Masking file row number: " + rowCount +
                        " Column Name: " + columnName +
                        " Actual Value:- " + masking.get(rowCount).get(columnName) +
                        " Expected Value :- " + expectedValue);
            }
        }
    }

    @Then("^validate (\\d+)-Beneficiary Data - Not a Nominee Broker and not a Professional Trustee Account masking content$")
    public void validateBeneficiaryDataNotANomineeBrokerAndNotAProfessionalTrusteeAccountMaskingContent(String
                                                                                                                tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_masking").mkdir();
        }
        List<String> fileNames = new DQRules().getMaskingFileNames(tableId);
        List<String> results = new LinkedList<>();
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;

        List<String> fileValues = new DQRules().getMaskingFileNames(tableId);
        String headers = null;
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            test.log("INFO", "-----Masking File Name--> " + fileName + " --------");
            System.out.println("-----Masking File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/maskingFiles/" + fileName);
            List<Map<String, String>> maskingRecords = ReadTextFile.getDqSamples();
            List<String> accountUniqueIdTable0130 = null;
            List<String> accountUniqueIdSubSystemFiles0130 = null;
            List<String> accountNumberTable0130 = null;
            List<String> accountNumberSubSystemFiles0130 = null;
            DQRules rules = new DQRules();
            if (maskingRecords.size() == dqRecords.size()) {
                test.pass("**********Pass Statement********* Masking file and DQ file:- " + fileName +
                        " records size is equal and it's size is " + maskingRecords.size());
                for (int index = 0; index < maskingRecords.size(); index++) {
//                    Account_Unique_ID
                    if (!(maskingRecords.get(index).get("Account_Unique_ID").equals(
                            dqRecords.get(index).get("Account_Unique_ID"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Account_Unique_ID")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Account_Unique_ID is masked, Masking Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " and DQ Account_Unique_ID:- " + dqRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + " is same as Non-Masking" +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + " is same as Non-Masking" +
                                " which is not as per Rule-Masking");
                    }

                    //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                    //   Rule-10
                    if (accountUniqueIdTable0130 == null) {
                        accountUniqueIdTable0130 = new DQRules().getListOfString(mi, "0130", "Account_Unique_ID", fileName.substring(26, 29), "MASKING");
                        accountUniqueIdSubSystemFiles0130 = new DQRules().getDQFileNames("0130").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                        if (accountUniqueIdSubSystemFiles0130.size() == 0) {
                            test.fail("--- FAIL ---> Sub_System_File-0130:- " + mi + "*0130" + fileName.substring(22, 29) +
                                    " is not found for Rule-304");

                        }
                    }
                    if (accountUniqueIdSubSystemFiles0130.size() != 0) {
                        boolean accountUniqueIdFlag0130 = accountUniqueIdTable0130.contains(maskingRecords.get(index).get("Account_Unique_ID"));
                        if (accountUniqueIdFlag0130) {
                            test.pass("**********Pass Statement********* Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                    " is as per Rule-10");

                        } else {
                            test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                    " is not as per Rule-10 and not found in file " + accountUniqueIdSubSystemFiles0130);
                            System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                    " is not as per Rule-10 and not found in file " + accountUniqueIdSubSystemFiles0130);
                        }
                    }


                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Account_Unique_ID"))) {
                        test.pass("**********Pass Statement********* Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + "is as per Rule-18");

                    } else {
                        test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " is not as per Rule-18");
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " is not as per Rule-18");
                    }

/*
//                     Check value is encrypted identically in the entire file
                    if(dqRecords.get(index).get("Account_Unique_ID").isEmpty()&&maskingRecords.get(index).get("Account_Unique_ID").isEmpty()){
                        List<Map<String, String>> accountUniqueIdIndexes = getIndexOfStringFromFile(dqRecords, "Account_Unique_ID", dqRecords.get(index).get("Account_Unique_ID"));
                        verifyEncryptedIdentically(maskingRecords, accountUniqueIdIndexes,
                                "Account_Unique_ID", maskingRecords.get(index).get("Account_Unique_ID"));
                    }

*/

                    //Account_Number
                    if (!(maskingRecords.get(index).get("Account_Number").equals(
                            dqRecords.get(index).get("Account_Number"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Account_Number")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Account_Number is masked, Masking Account_Number:- " + maskingRecords.get(index).get("Account_Number") +
                                " and DQ Account_Number:- " + dqRecords.get(index).get("Account_Number" +
                                "for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID")));
                    } else if (maskingRecords.get(index).get("Account_Number").isEmpty() && dqRecords.get(index).get("Account_Number").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Account_Number is Blank," +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") + " is same as Non-Masking " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") + " is same as Non-Masking " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                    //   Rule-10
                    if (accountNumberTable0130 == null) {
                        accountNumberTable0130 = new DQRules().getListOfString(mi, "0130", "Account_Number", fileName.substring(26, 29), "MASKING");
                        accountNumberSubSystemFiles0130 = new DQRules().getDQFileNames("0130").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                        if (accountNumberSubSystemFiles0130.size() == 0) {
                            test.fail("--- FAIL ---> Sub_System_File-0130:- " + mi + "*0130" + fileName.substring(22, 29) +
                                    " is not found for Rule-304");

                        }
                    }
                    if (accountNumberSubSystemFiles0130.size() != 0) {
                        boolean accountNumberFlag0130 = accountNumberTable0130.contains(maskingRecords.get(index).get("Account_Number"));
                        if (accountNumberFlag0130) {
                            test.pass("**********Pass Statement********* Account_Number:- " + maskingRecords.get(index).get("Account_Number") + " is as per Rule-10" +
                                    " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                        } else {
                            test.fail("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") +
                                    " is not as per Rule-10 and not found in file " + accountNumberSubSystemFiles0130 + " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                            System.err.println("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") +
                                    " is not as per Rule-10 and not found in file " + accountNumberSubSystemFiles0130 + " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                        }

                    }

                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Account_Number"))) {
                        test.pass("**********Pass Statement********* Account_Number:- " + maskingRecords.get(index).get("Account_Number") + "is as per Rule-18");

                    } else {
                        test.fail("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") +
                                " is not as per Rule-18");
                        System.err.println("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") +
                                " is not as per Rule-18 for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    }

             /*       //                     Check value is encrypted identically in the entire file
                    if(dqRecords.get(index).get("Account_Number").isEmpty()&&maskingRecords.get(index).get("Account_Number").isEmpty()){
                        List<Map<String, String>> accountNumberIndexes = getIndexOfStringFromFile(dqRecords,"Account_Number", dqRecords.get(index).get("Account_Number"));
                        verifyEncryptedIdentically(maskingRecords, accountNumberIndexes, "Account_Number", maskingRecords.get(index).get("Account_Number"));
                    }
*/


                    // Name
//                    As per User Story CDICPHSTWO-938 7W Production Data: Table 0152 - Name, First_Name, Middle_Name, Last_Name, Address_1, Address_2, Postal_Code fields value across the table to be encrypted
                 /*   if ((!(maskingRecords.get(index).get("Name").equals(
                            dqRecords.get(index).get("Name")))) &&
                            rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Name"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Name is masked, Masking Name:- " + maskingRecords.get(index).get("Name") +
                                " and DQ Name:- " + dqRecords.get(index).get("Name") + " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else if (maskingRecords.get(index).get("Name").isEmpty() && dqRecords.get(index).get("Name").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Name is Blank," +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Name:- " + maskingRecords.get(index).get("Name") + " is same as Non-Masking " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Name:- " + maskingRecords.get(index).get("Name") + " is same as Non-Masking " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }
*/

                    // Name
                    if (maskingRecords.get(index).get("Name").isEmpty() && dqRecords.get(index).get("Name").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Name is Blank/EMPTY for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else if (!(maskingRecords.get(index).get("Name").equals(
                            dqRecords.get(index).get("Name"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Name")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Name is encrypted masking encrypted Name:- " + maskingRecords.get(index).get("Name") +
                                " and DQ Name:- " + dqRecords.get(index).get("Name") + " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Name:- " + maskingRecords.get(index).get("Name") + " is not encrypted masking encrypted Name - " + maskingRecords.get(index).get("Name") +
                                " and DQ Name - " + dqRecords.get(index).get("Name") + " for Account_Unique_ID - " + maskingRecords.get(index).get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Name:- " + maskingRecords.get(index).get("Name") + " is not encrypted masking encrypted Name - " + maskingRecords.get(index).get("Name") +
                                " and DQ Name - " + dqRecords.get(index).get("Name") + " for Account_Unique_ID - " + maskingRecords.get(index).get("Account_Unique_ID"));
                    }

                 /*   //                     Check value is encrypted identically in the entire file
                    if(dqRecords.get(index).get("Name").isEmpty()&&maskingRecords.get(index).get("Name").isEmpty()){
                        List<Map<String, String>> nameIndexes = getIndexOfStringFromFile(dqRecords,"Name", dqRecords.get(index).get("Name"));
                        verifyEncryptedIdentically(maskingRecords, nameIndexes, "Name", maskingRecords.get(index).get("Name"));
                    }
*/

                    // First_Name
                    //    As per User Story CDICPHSTWO-938 7W Production Data: Table 0152 - Name, First_Name, Middle_Name, Last_Name, Address_1, Address_2, Postal_Code fields value across the table to be encrypted
                   /* if (maskingRecords.get(index).get("First_Name").isEmpty() && dqRecords.get(index).get("First_Name").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file First_Name is Blank/EMPTY for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else if (rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("First_Name"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file First_Name is masked, Masking First_Name:- " + maskingRecords.get(index).get("First_Name") +
                                " and DQ First_Name:- " + dqRecords.get(index).get("First_Name") + " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> First_Name:- " + maskingRecords.get(index).get("First_Name") + " is same as Non-Masking " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> First_Name:- " + maskingRecords.get(index).get("First_Name") + " is same as Non-Masking " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }*/
                    // First Name
                    if (maskingRecords.get(index).get("First_Name").isEmpty() && dqRecords.get(index).get("First_Name").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file First_Name is Blank/EMPTY for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else if (!(maskingRecords.get(index).get("First_Name").equals(
                            dqRecords.get(index).get("First_Name"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("First_Name")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file First Name is encrypted masking encrypted Name:- " + maskingRecords.get(index).get("First_Name") +
                                " and DQ First_Name:- " + dqRecords.get(index).get("First_Name") + " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> First_Name:- " + maskingRecords.get(index).get("First_Name") + " is not encrypted masking encrypted First_Name - " + maskingRecords.get(index).get("First_Name") +
                                " and DQ First_Name - " + dqRecords.get(index).get("First_Name") + " for Account_Unique_ID - " + maskingRecords.get(index).get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> First_Name:- " + maskingRecords.get(index).get("First_Name") + " is not encrypted masking encrypted First_Name - " + maskingRecords.get(index).get("First_Name") +
                                " and DQ First_Name - " + dqRecords.get(index).get("First_Name") + " for Account_Unique_ID - " + maskingRecords.get(index).get("Account_Unique_ID"));
                    }

                /*    //                     Check value is encrypted identically in the entire file
                    if(dqRecords.get(index).get("First_Name").isEmpty()&&maskingRecords.get(index).get("First_Name").isEmpty()){
                        List<Map<String, String>> firstNameIndexes = getIndexOfStringFromFile(dqRecords,"First_Name", dqRecords.get(index).get("First_Name"));
                        verifyEncryptedIdentically(maskingRecords, firstNameIndexes, "First_Name", maskingRecords.get(index).get("First_Name"));
                    }
*/

                    // Middle_Name
                    //    As per User Story CDICPHSTWO-938 7W Production Data: Table 0152 - Name, First_Name, Middle_Name, Last_Name, Address_1, Address_2, Postal_Code fields value across the table to be encrypted
                    /*if (maskingRecords.get(index).get("Middle_Name").isEmpty() && dqRecords.get(index).get("Middle_Name").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Middle_Name is Blank/EMPTY for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else if (rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Middle_Name"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Middle_Name is masked, Masking Middle_Name:- " + maskingRecords.get(index).get("Middle_Name") +
                                " and DQ Middle_Name:- " + dqRecords.get(index).get("Middle_Name") + " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Middle_Name:- " + maskingRecords.get(index).get("Middle_Name") + " is same as Non-Masking " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Middle_Name:- " + maskingRecords.get(index).get("Middle_Name") + " is same as Non-Masking " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }*/
                    if (maskingRecords.get(index).get("Middle_Name").isEmpty() && dqRecords.get(index).get("Middle_Name").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Middle_Name is Blank/EMPTY for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else if (!(maskingRecords.get(index).get("Middle_Name").equals(
                            dqRecords.get(index).get("Middle_Name"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Middle_Name")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Middle Name is encrypted masking encrypted Name:- " + maskingRecords.get(index).get("Middle_Name") +
                                " and DQ Middle_Name:- " + dqRecords.get(index).get("Middle_Name") + " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Middle_Name:- " + maskingRecords.get(index).get("Middle_Name") + " is not encrypted masking encrypted Middle_Name - " + maskingRecords.get(index).get("Middle_Name") +
                                " and DQ Middle_Name - " + dqRecords.get(index).get("Middle_Name") + " for Account_Unique_ID - " + maskingRecords.get(index).get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Middle_Name:- " + maskingRecords.get(index).get("Middle_Name") + " is not encrypted masking encrypted Middle_Name - " + maskingRecords.get(index).get("Middle_Name") +
                                " and DQ Middle_Name - " + dqRecords.get(index).get("Middle_Name") + " for Account_Unique_ID - " + maskingRecords.get(index).get("Account_Unique_ID"));
                    }

                  /*  //                     Check value is encrypted identically in the entire file
                    if(dqRecords.get(index).get("Middle_Name").isEmpty()&&maskingRecords.get(index).get("Middle_Name").isEmpty()){
                        List<Map<String, String>> middleNameIndexes = getIndexOfStringFromFile(dqRecords,"Middle_Name", dqRecords.get(index).get("Middle_Name"));
                        verifyEncryptedIdentically(maskingRecords, middleNameIndexes, "Middle_Name", maskingRecords.get(index).get("Middle_Name"));
                    }*/


                    // Last_Name
                    //    As per User Story CDICPHSTWO-938 7W Production Data: Table 0152 - Name, First_Name, Middle_Name, Last_Name, Address_1, Address_2, Postal_Code fields value across the table to be encrypted
                    /*if (maskingRecords.get(index).get("Last_Name").isEmpty() && dqRecords.get(index).get("Last_Name").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Last_Name is Blank/EMPTY for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else if (rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Last_Name"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Last_Name is masked, Masking Last_Name:- " + maskingRecords.get(index).get("Last_Name") +
                                " and DQ Last_Name:- " + dqRecords.get(index).get("Last_Name") + " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Last_Name:- " + maskingRecords.get(index).get("Last_Name") + " is same as Non-Masking " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Last_Name:- " + maskingRecords.get(index).get("Last_Name") + " is same as Non-Masking " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }*/
                    if (maskingRecords.get(index).get("Last_Name").isEmpty() && dqRecords.get(index).get("Last_Name").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Last_Name is Blank/EMPTY for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else if (!(maskingRecords.get(index).get("Last_Name").equals(
                            dqRecords.get(index).get("Last_Name"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Last_Name")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Last_Name is encrypted masking encrypted Name:- " + maskingRecords.get(index).get("Last_Name") +
                                " and DQ Last_Name:- " + dqRecords.get(index).get("Last_Name") + " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Last_Name:- " + maskingRecords.get(index).get("Last_Name") + " is not encrypted masking encrypted Last_Name:- " + maskingRecords.get(index).get("Last_Name") +
                                " and DQ Last_Name:- " + dqRecords.get(index).get("Last_Name") + " for Account_Unique_ID - " + maskingRecords.get(index).get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Last_Name:- " + maskingRecords.get(index).get("Last_Name") + " is not encrypted masking encrypted Last_Name:- " + maskingRecords.get(index).get("Last_Name") +
                                " and DQ Last_Name:- " + dqRecords.get(index).get("Last_Name") + " for Account_Unique_ID - " + maskingRecords.get(index).get("Account_Unique_ID"));
                    }

                 /*   //                     Check value is encrypted identically in the entire file
                    if(dqRecords.get(index).get("Last_Name").isEmpty()&&maskingRecords.get(index).get("Last_Name").isEmpty()){
                        List<Map<String, String>> lastNameIndexes = getIndexOfStringFromFile(dqRecords,"Last_Name", dqRecords.get(index).get("Last_Name"));
                        verifyEncryptedIdentically(maskingRecords, lastNameIndexes, "Last_Name", maskingRecords.get(index).get("Last_Name"));
                    }*/


                    // Address_1
                    //    As per User Story CDICPHSTWO-938 7W Production Data: Table 0152 - Name, First_Name, Middle_Name, Last_Name, Address_1, Address_2, Postal_Code fields value across the table to be encrypted
                    /*if ((!(maskingRecords.get(index).get("Address_1").equals(
                            dqRecords.get(index).get("Address_1")))) && rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Address_1"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Address_1 is masked, Masking Address_1:- " + maskingRecords.get(index).get("Address_1") +
                                " and DQ Address_1:- " + dqRecords.get(index).get("Address_1") + " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else if (maskingRecords.get(index).get("Address_1").isEmpty() && dqRecords.get(index).get("Address_1").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Address_1 is Blank," +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Address_1:- " + maskingRecords.get(index).get("Address_1") + " is same as Non-Masking " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Address_1:- " + maskingRecords.get(index).get("Address_1") + " is same as Non-Masking " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }
*/

                    if (maskingRecords.get(index).get("Address_1").isEmpty() && dqRecords.get(index).get("Address_1").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Address_1 is Blank/EMPTY for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else if (!(maskingRecords.get(index).get("Address_1").equals(
                            dqRecords.get(index).get("Address_1"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Address_1")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Address_1 is encrypted masking encrypted Name:- " + maskingRecords.get(index).get("Address_1") +
                                " and DQ Address_1:- " + dqRecords.get(index).get("Address_1") + " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Address_1:- " + maskingRecords.get(index).get("Address_1") + " is not encrypted masking encrypted Address_1 - " + maskingRecords.get(index).get("Address_1") +
                                " and DQ Address_1 - " + dqRecords.get(index).get("Address_1") + " for Account_Unique_ID - " + maskingRecords.get(index).get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Address_1:- " + maskingRecords.get(index).get("Address_1") + " is not encrypted masking encrypted Address_1 - " + maskingRecords.get(index).get("Address_1") +
                                " and DQ Address_1 - " + dqRecords.get(index).get("Address_1") + " for Account_Unique_ID - " + maskingRecords.get(index).get("Account_Unique_ID"));
                    }

                   /* //                     Check value is encrypted identically in the entire file
                    if(dqRecords.get(index).get("Address_1").isEmpty()&&maskingRecords.get(index).get("Address_1").isEmpty()){
                        List<Map<String, String>> addressOneIndexes = getIndexOfStringFromFile(dqRecords,"Address_1", dqRecords.get(index).get("Address_1"));
                        verifyEncryptedIdentically(maskingRecords, addressOneIndexes, "Address_1", maskingRecords.get(index).get("Address_1"));

                    }*/

                    // Address_2
                    //    As per User Story CDICPHSTWO-938 7W Production Data: Table 0152 - Name, First_Name, Middle_Name, Last_Name, Address_1, Address_2, Postal_Code fields value across the table to be encrypted
                    /*if (maskingRecords.get(index).get("Address_2").isEmpty() && dqRecords.get(index).get("Address_2").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Address_2 is Blank/EMPTY for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else if (rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Address_2"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Address_2 is masked, Masking Address_2:- " + maskingRecords.get(index).get("Address_2") +
                                " and DQ Address_2:- " + dqRecords.get(index).get("Address_2") + " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Address_2:- " + maskingRecords.get(index).get("Address_2") + " is same as Non-Masking " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Address_2:- " + maskingRecords.get(index).get("Address_2") + " is same as Non-Masking " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }*/
                    if (maskingRecords.get(index).get("Address_2").isEmpty() && dqRecords.get(index).get("Address_2").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Address_2 is Blank/EMPTY for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else if (!(maskingRecords.get(index).get("Address_2").equals(
                            dqRecords.get(index).get("Address_2"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Address_2")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Address_2 is encrypted masking encrypted Name:- " + maskingRecords.get(index).get("Address_2") +
                                " and DQ Address_2:- " + dqRecords.get(index).get("Address_2") + " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Address_2:- " + maskingRecords.get(index).get("Address_2") + " is not encrypted masking encrypted Address_2 - " + maskingRecords.get(index).get("Address_2") +
                                " and DQ Address_2 - " + dqRecords.get(index).get("Address_2") + " for Account_Unique_ID - " + maskingRecords.get(index).get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Address_2:- " + maskingRecords.get(index).get("Address_2") + " is not encrypted masking encrypted Address_2 - " + maskingRecords.get(index).get("Address_2") +
                                " and DQ Address_2:- " + dqRecords.get(index).get("Address_2") + " for Account_Unique_ID - " + maskingRecords.get(index).get("Account_Unique_ID"));
                    }

                 /*   //                     Check value is encrypted identically in the entire file
                    if(dqRecords.get(index).get("Address_2").isEmpty()&&maskingRecords.get(index).get("Address_2").isEmpty()){
                        List<Map<String, String>> addressTwoIndexes = getIndexOfStringFromFile(dqRecords,"Address_2", dqRecords.get(index).get("Address_2"));
                        verifyEncryptedIdentically(maskingRecords, addressTwoIndexes, "Address_2", maskingRecords.get(index).get("Address_2"));
                    }*/


                    // City
                    if (maskingRecords.get(index).get("City").equals(
                            dqRecords.get(index).get("City"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule City:- " + maskingRecords.get(index).get("City") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " City:- " + maskingRecords.get(index).get("City") +
                                " is not equal with Non-Masking City - " + dqRecords.get(index).get("City") +
                                " for Account_Unique_ID - " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " City:- " + maskingRecords.get(index).get("City") +
                                " is not equal with Non-Masking City - " + dqRecords.get(index).get("City") +
                                " for Account_Unique_ID - " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Province
                    if (maskingRecords.get(index).get("Province").equals(
                            dqRecords.get(index).get("Province"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Province:- " + maskingRecords.get(index).get("Province") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Province:- " + maskingRecords.get(index).get("Province") +
                                " is not equal with Non-Masking Province - " + dqRecords.get(index).get("Province") +
                                " for Account_Unique_ID - " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Province:- " + maskingRecords.get(index).get("Province") +
                                " is not equal with Non-Masking Province - " + dqRecords.get(index).get("Province") +
                                " for Account_Unique_ID - " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Postal_Code
                   /* if ((!(maskingRecords.get(index).get("Postal_Code").equals(
                            dqRecords.get(index).get("Postal_Code")))) && rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Postal_Code"))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Postal_Code is masked, Masking Postal_Code:- " + maskingRecords.get(index).get("Postal_Code") +
                                " and DQ Postal_Code:- " + dqRecords.get(index).get("Postal_Code") + " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else if (maskingRecords.get(index).get("Postal_Code").isEmpty() && dqRecords.get(index).get("Postal_Code").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Postal_Code is Blank," +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Province:- " + maskingRecords.get(index).get("Province") + " is same as Non-Masking " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Province:- " + maskingRecords.get(index).get("Province") + " is same as Non-Masking " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }*/

                    if (maskingRecords.get(index).get("Postal_Code").isEmpty() && dqRecords.get(index).get("Postal_Code").isEmpty()) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Postal_Code is Blank/EMPTY for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else if (!(maskingRecords.get(index).get("Postal_Code").equals(
                            dqRecords.get(index).get("Postal_Code"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Postal_Code")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Postal_Code is encrypted masking encrypted Name:- " + maskingRecords.get(index).get("Postal_Code") +
                                " and DQ Postal_Code:- " + dqRecords.get(index).get("Postal_Code") + " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Postal_Code:- " + maskingRecords.get(index).get("Postal_Code") + " is not encrypted masking encrypted Postal_Code - " + maskingRecords.get(index).get("Postal_Code") +
                                " and DQ Postal_Code - " + dqRecords.get(index).get("Postal_Code"));
                        System.err.println("--- FAIL ---> Postal_Code:- " + maskingRecords.get(index).get("Postal_Code") + " is not encrypted masking encrypted Postal_Code - " + maskingRecords.get(index).get("Postal_Code") +
                                " and DQ Postal_Code - " + dqRecords.get(index).get("Postal_Code") + " for Account_Unique_ID - " + maskingRecords.get(index).get("Account_Unique_ID"));
                    }

                    /*//                     Check value is encrypted identically in the entire file
                    if(dqRecords.get(index).get("Postal_Code").isEmpty()&&maskingRecords.get(index).get("Postal_Code").isEmpty()){
                        List<Map<String, String>> postalCodeIndexes = getIndexOfStringFromFile(dqRecords,"Postal_Code", dqRecords.get(index).get("Postal_Code"));
                        verifyEncryptedIdentically(maskingRecords, postalCodeIndexes, "Postal_Code", maskingRecords.get(index).get("Postal_Code"));
                    }*/


                   /* //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Postal_Code"))) {
                        test.pass("**********Pass Statement********* Postal_Code:- " + maskingRecords.get(index).get("Postal_Code") + "is as per Rule-18" +
                                "for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Postal_Code:- " + maskingRecords.get(index).get("Postal_Code") +
                                "is not as per Rule-18 for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                        System.err.println("--- FAIL ---> Postal_Code:- " + maskingRecords.get(index).get("Postal_Code") +
                                "is not as per Rule-18 for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    }*/

                    // Country
                    if (maskingRecords.get(index).get("Country").equals(
                            dqRecords.get(index).get("Country"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Country:- " + maskingRecords.get(index).get("Country") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Country:- " + maskingRecords.get(index).get("Country") +
                                " is not equal with DQ Country - " + dqRecords.get(index).get("Country") + " for Account_Unique_ID:- "
                                + maskingRecords.get(index).get("Account_Unique_ID") + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Country:- " + maskingRecords.get(index).get("Country") +
                                " is not equal with DQ Country - " + dqRecords.get(index).get("Country") + " for Account_Unique_ID - "
                                + maskingRecords.get(index).get("Account_Unique_ID") + " which is not as per Rule-Masking");
                    }

                    // SIA_Individual_Flag
                    if (maskingRecords.get(index).get("SIA_Individual_Flag").equals(
                            dqRecords.get(index).get("SIA_Individual_Flag"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule SIA_Individual_Flag:- " + maskingRecords.get(index).get("SIA_Individual_Flag") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " SIA_Individual_Flag:- " + maskingRecords.get(index).get("SIA_Individual_Flag") +
                                " is not equal with DQ SIA_Individual_Flag - " + dqRecords.get(index).get("SIA_Individual_Flag") + " for Account_Unique_ID - "
                                + maskingRecords.get(index).get("Account_Unique_ID") + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " SIA_Individual_Flag:- " + maskingRecords.get(index).get("SIA_Individual_Flag") +
                                " is not equal with DQ SIA_Individual_Flag - " + dqRecords.get(index).get("SIA_Individual_Flag") + " for Account_Unique_ID - "
                                + maskingRecords.get(index).get("Account_Unique_ID") + " which is not as per Rule-Masking");
                    }

                    // Interest_In_Deposit_Flag
                    if (maskingRecords.get(index).get("Interest_In_Deposit_Flag").equals(
                            dqRecords.get(index).get("Interest_In_Deposit_Flag"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Interest_In_Deposit_Flag:- " + maskingRecords.get(index).get("Interest_In_Deposit_Flag") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Interest_In_Deposit_Flag:- " + maskingRecords.get(index).get("Interest_In_Deposit_Flag") +
                                " is not equal with DQ Interest_In_Deposit_Flag - " + dqRecords.get(index).get("Interest_In_Deposit_Flag") + " for Account_Unique_ID - "
                                + maskingRecords.get(index).get("Account_Unique_ID") + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Interest_In_Deposit_Flag:- " + maskingRecords.get(index).get("Interest_In_Deposit_Flag") +
                                " is not equal with DQ Interest_In_Deposit_Flag - " + dqRecords.get(index).get("Interest_In_Deposit_Flag") + " for Account_Unique_ID - "
                                + maskingRecords.get(index).get("Account_Unique_ID") + " which is not as per Rule-Masking");
                    }

                    // Interest_In_Deposit
                    if (maskingRecords.get(index).get("Interest_In_Deposit").equals(
                            dqRecords.get(index).get("Interest_In_Deposit"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Interest_In_Deposit:- " + maskingRecords.get(index).get("Interest_In_Deposit") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Interest_In_Deposit:- " + maskingRecords.get(index).get("Interest_In_Deposit") +
                                " is not equal with DQ Interest_In_Deposit - " + dqRecords.get(index).get("Interest_In_Deposit") + " for Account_Unique_ID - "
                                + maskingRecords.get(index).get("Account_Unique_ID") + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Interest_In_Deposit:- " + maskingRecords.get(index).get("Interest_In_Deposit") +
                                " is not equal with DQ Interest_In_Deposit - " + dqRecords.get(index).get("Interest_In_Deposit") + " for Account_Unique_ID - "
                                + maskingRecords.get(index).get("Account_Unique_ID") + " which is not as per Rule-Masking");
                    }

                    if (failedMessages.size() != 0) {
                        String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                        if (!failedMessage.isEmpty()) {
                            maskingRecords.get(index).put("Result", failedMessage);
                            maskingRecords.get(index).put("FileName", fileName);
                            failedMessages = null;
                            failedMessages = new LinkedList<>();
                        }
                    } else {
                        maskingRecords.get(index).put("Result", "**PASS**");
                        maskingRecords.get(index).put("FileName", fileName);
                    }


                }

            } else {
                test.fail("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
                System.err.println("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
            }

            if (headers == null && maskingRecords.size() > 0) {
                headers = maskingRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : maskingRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + ".txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    @Then("^validate (\\d+)-Beneficiary Data - Nominee Broker masking content$")
    public void validateBeneficiaryDataNomineeBrokerMaskingContent(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_masking").mkdir();
        }
        List<String> fileNames = new DQRules().getMaskingFileNames(tableId);
        List<String> results = new LinkedList<>();
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;

        List<String> fileValues = new DQRules().getMaskingFileNames(tableId);
        String headers = null;
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            test.log("INFO", "-----Masking File Name--> " + fileName + " --------");
            System.out.println("-----Masking File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/maskingFiles/" + fileName);
            List<Map<String, String>> maskingRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            List<String> accountUniqueIdTable0130 = null;
            List<String> subSystemFilesAccountUniqueId0130 = null;
            List<String> accountNumberTable0130 = null;
            List<String> subSystemFilesAccountNumber0130 = null;
            if (maskingRecords.size() == dqRecords.size()) {
                test.pass("**********Pass Statement********* Masking file and DQ file:- " + fileName +
                        " records size is equal and it's size is " + maskingRecords.size());
                for (int index = 0; index < maskingRecords.size(); index++) {
//                    Account_Unique_ID
                    if (!(maskingRecords.get(index).get("Account_Unique_ID").equals(
                            dqRecords.get(index).get("Account_Unique_ID"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Account_Unique_ID")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Account_Unique_ID is masked, Masking Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " and DQ Account_Unique_ID:- " + dqRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + " is same as Non-Masking which is not as per Rule-Masking");
                        test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + " is same as Non-Masking which is not as per Rule-Masking");
                    }

                    //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                    //   Rule-10
                    if (accountUniqueIdTable0130 == null) {
                        accountUniqueIdTable0130 = new DQRules().getListOfString(mi, "0130", "Account_Unique_ID", fileName.substring(26, 29), "MASKING");
                        subSystemFilesAccountUniqueId0130 = new DQRules().getDQFileNames("0130").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                        if (subSystemFilesAccountUniqueId0130.size() == 0) {
                            test.fail("--- FAIL ---> Sub_System_File-0130:- " + mi + "*0130" + fileName.substring(22, 29) +
                                    " is not found for Rule-304");

                        }
                    }
                    if (subSystemFilesAccountUniqueId0130.size() != 0) {
                        boolean accountUniqueIdFlag130 = accountUniqueIdTable0130.contains(maskingRecords.get(index).get("Account_Unique_ID"));
                        if (accountUniqueIdFlag130) {
                            test.pass("**********Pass Statement********* Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                    " is as per Rule-10");

                        } else {
                            test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                    " is not as per Rule-10 and not found in file " + subSystemFilesAccountUniqueId0130);
                            System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                    " is not as per Rule-10 and not found in file " + subSystemFilesAccountUniqueId0130);
                        }
                    }


                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Account_Unique_ID"))) {
                        test.pass("**********Pass Statement********* Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + "is as per Rule-18");

                    } else {
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " is not as per Rule-18");
                        test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " is not as per Rule-18");
                    }

                    //Account_Number
                    if (!(maskingRecords.get(index).get("Account_Number").equals(
                            dqRecords.get(index).get("Account_Number"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Account_Number")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Account_Number is masked, Masking Account_Number:- " + maskingRecords.get(index).get("Account_Number") +
                                " and DQ Account_Number:- " + dqRecords.get(index).get("Account_Number" +
                                "for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID")));
                    } else {
                        System.err.println("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") + " is same as Non-Masking " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        test.fail("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") + " is same as Non-Masking " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                    //   Rule-10
                    if (accountNumberTable0130 == null) {
                        accountNumberTable0130 = new DQRules().getListOfString(mi, "0130", "Account_Number", fileName.substring(26, 29), "MASKING");
                        subSystemFilesAccountNumber0130 = new DQRules().getDQFileNames("0130").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                        if (subSystemFilesAccountNumber0130.size() == 0) {
                            test.fail("--- FAIL ---> Sub_System_File-0130:- " + mi + "*0130" + fileName.substring(22, 29) +
                                    " is not found for Rule-304");

                        }
                    }

                    if (subSystemFilesAccountNumber0130.size() != 0) {
                        boolean accountNumberFlag130 = accountNumberTable0130.contains(maskingRecords.get(index).get("Account_Unique_ID"));
                        if (accountNumberFlag130) {
                            test.pass("**********Pass Statement********* Account_Number:- " + maskingRecords.get(index).get("Account_Number") + " is as per Rule-10" +
                                    " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                        } else {
                            test.fail("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") +
                                    " is not as per Rule-10 and not found in file " + subSystemFilesAccountNumber0130 + " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                            System.err.println("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") +
                                    " is not as per Rule-10 and not found in file " + subSystemFilesAccountNumber0130 + " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                        }
                    }

                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Account_Number"))) {
                        test.pass("**********Pass Statement********* Account_Number:- " + maskingRecords.get(index).get("Account_Number") + "is as per Rule-18");

                    } else {
                        System.err.println("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") +
                                " is not as per Rule-18");
                        test.fail("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") +
                                " is not as per Rule-18");
                    }

                    // Beneficiary_ID
                    if (maskingRecords.get(index).get("Beneficiary_ID").equals(
                            dqRecords.get(index).get("Beneficiary_ID"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Beneficiary_ID:- " + maskingRecords.get(index).get("Beneficiary_ID") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Beneficiary_ID:- " + maskingRecords.get(index).get("Beneficiary_ID") +
                                " is not equal with Non-Masking Beneficiary_ID:- " + dqRecords.get(index).get("Beneficiary_ID") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Beneficiary_ID:- " + maskingRecords.get(index).get("Beneficiary_ID") +
                                " is not equal with Non-Masking Beneficiary_ID:- " + dqRecords.get(index).get("Beneficiary_ID") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // SIA_Individual_Flag
                    if (maskingRecords.get(index).get("SIA_Individual_Flag").equals(
                            dqRecords.get(index).get("SIA_Individual_Flag"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule SIA_Individual_Flag:- " + maskingRecords.get(index).get("SIA_Individual_Flag") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " SIA_Individual_Flag:- " + maskingRecords.get(index).get("SIA_Individual_Flag") +
                                " is not equal with Non-Masking SIA_Individual_Flag:- " + dqRecords.get(index).get("SIA_Individual_Flag") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " SIA_Individual_Flag:- " + maskingRecords.get(index).get("SIA_Individual_Flag") +
                                " is not equal with Non-Masking SIA_Individual_Flag:- " + dqRecords.get(index).get("SIA_Individual_Flag") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Interest_In_Deposit_Flag
                    if (maskingRecords.get(index).get("Interest_In_Deposit_Flag").equals(
                            dqRecords.get(index).get("Interest_In_Deposit_Flag"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Interest_In_Deposit_Flag:- " + maskingRecords.get(index).get("Interest_In_Deposit_Flag") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Interest_In_Deposit_Flag:- " + maskingRecords.get(index).get("Interest_In_Deposit_Flag") +
                                " is not equal with Non-Masking Interest_In_Deposit_Flag:- " + dqRecords.get(index).get("Interest_In_Deposit_Flag") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Interest_In_Deposit_Flag:- " + maskingRecords.get(index).get("Interest_In_Deposit_Flag") +
                                " is not equal with Non-Masking Interest_In_Deposit_Flag:- " + dqRecords.get(index).get("Interest_In_Deposit_Flag") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Interest_In_Deposit
                    if (maskingRecords.get(index).get("Interest_In_Deposit").equals(
                            dqRecords.get(index).get("Interest_In_Deposit"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Interest_In_Deposit:- " + maskingRecords.get(index).get("Interest_In_Deposit") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Interest_In_Deposit:- " + maskingRecords.get(index).get("Interest_In_Deposit") +
                                " is not equal with Non-Masking Interest_In_Deposit:- " + dqRecords.get(index).get("Interest_In_Deposit") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Interest_In_Deposit:- " + maskingRecords.get(index).get("Interest_In_Deposit") +
                                " is not equal with Non-Masking Interest_In_Deposit:- " + dqRecords.get(index).get("Interest_In_Deposit") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // IB_LEI
                    if (maskingRecords.get(index).get("IB_LEI").equals(
                            dqRecords.get(index).get("IB_LEI"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule IB_LEI:- " + maskingRecords.get(index).get("IB_LEI") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " IB_LEI:- " + maskingRecords.get(index).get("IB_LEI") +
                                " is not equal with Non-Masking IB_LEI:- " + dqRecords.get(index).get("IB_LEI") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " IB_LEI:- " + maskingRecords.get(index).get("IB_LEI") +
                                " is not equal with Non-Masking IB_LEI:- " + dqRecords.get(index).get("IB_LEI") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    if (failedMessages.size() != 0) {
                        String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                        if (!failedMessage.isEmpty()) {
                            maskingRecords.get(index).put("Result", failedMessage);
                            maskingRecords.get(index).put("FileName", fileName);
                            failedMessages = null;
                            failedMessages = new LinkedList<>();
                        }
                    } else {
                        maskingRecords.get(index).put("Result", "**PASS**");
                        maskingRecords.get(index).put("FileName", fileName);
                    }


                }

            } else {
                test.fail("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
                System.err.println("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
            }

            if (headers == null && maskingRecords.size() > 0) {
                headers = maskingRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : maskingRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + ".txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }


    @Then("^validate (\\d+)-Transaction Data masking content$")
    public void validateTransactionDataMaskingContent(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_masking").mkdir();
        }
        List<String> fileNames = new DQRules().getMaskingFileNames(tableId);
        List<String> results = new LinkedList<>();
        ReadTextFile readTextFile = new ReadTextFile();
        String mi = null;

        List<String> fileValues = new DQRules().getMaskingFileNames(tableId);
        String headers = null;
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            test.log("INFO", "-----Masking File Name--> " + fileName + " --------");
            System.out.println("-----Masking File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/maskingFiles/" + fileName);
            List<Map<String, String>> maskingRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            if (maskingRecords.size() == dqRecords.size()) {
                test.pass("**********Pass Statement********* Masking file and DQ file:- " + fileName +
                        " records size is equal and it's size is " + maskingRecords.size());
                for (int index = 0; index < maskingRecords.size(); index++) {
                    if (!(maskingRecords.get(index).get("Account_Unique_ID").equals(
                            dqRecords.get(index).get("Account_Unique_ID"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Account_Unique_ID")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Account_Unique_ID is masked, Masking Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " and DQ Account_Unique_ID:- " + dqRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + " is same which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + " is same which is not as per Rule-Masking");
                    }

                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Account_Unique_ID"))) {
                        test.pass("**********Pass Statement********* Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + "is as per Rule-18");

                    } else {
                        test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " is not as per Rule-18");
                    }

                    // Transaction_Number
                    if (maskingRecords.get(index).get("Transaction_Number").equals(
                            dqRecords.get(index).get("Transaction_Number"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Transaction_Number:- " + maskingRecords.get(index).get("Transaction_Number") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Transaction_Number:- " + maskingRecords.get(index).get("Transaction_Number") +
                                " is not equal with Non-Masking Transaction_Number:- " + dqRecords.get(index).get("Transaction_Number") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Transaction_Number:- " + maskingRecords.get(index).get("Transaction_Number") +
                                " is not equal with Non-Masking Transaction_Number:- " + dqRecords.get(index).get("Transaction_Number") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Transaction_Item_Number
                    if (maskingRecords.get(index).get("Transaction_Item_Number").equals(
                            dqRecords.get(index).get("Transaction_Item_Number"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Transaction_Item_Number:- " + maskingRecords.get(index).get("Transaction_Item_Number") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Transaction_Item_Number:- " + maskingRecords.get(index).get("Transaction_Item_Number") +
                                " is not equal with Non-Masking Transaction_Item_Number:- " + dqRecords.get(index).get("Transaction_Item_Number") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Transaction_Item_Number:- " + maskingRecords.get(index).get("Transaction_Item_Number") +
                                " is not equal with Non-Masking Transaction_Item_Number:- " + dqRecords.get(index).get("Transaction_Item_Number") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Created_Date
                    if (maskingRecords.get(index).get("Created_Date").equals(
                            dqRecords.get(index).get("Created_Date"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Created_Date:- " + maskingRecords.get(index).get("Created_Date") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Created_Date:- " + maskingRecords.get(index).get("Created_Date") +
                                " is not equal with Non-Masking Created_Date:- " + dqRecords.get(index).get("Created_Date") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Created_Date:- " + maskingRecords.get(index).get("Created_Date") +
                                " is not equal with Non-Masking Created_Date:- " + dqRecords.get(index).get("Created_Date") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Posted_Date
                    if (maskingRecords.get(index).get("Posted_Date").equals(
                            dqRecords.get(index).get("Posted_Date"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Posted_Date:- " + maskingRecords.get(index).get("Posted_Date") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Posted_Date:- " + maskingRecords.get(index).get("Posted_Date") +
                                " is not equal with Non-Masking Posted_Date:- " + dqRecords.get(index).get("Posted_Date") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Posted_Date:- " + maskingRecords.get(index).get("Posted_Date") +
                                " is not equal with Non-Masking Posted_Date:- " + dqRecords.get(index).get("Posted_Date") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Transaction_Value
                    if (maskingRecords.get(index).get("Transaction_Value").equals(
                            dqRecords.get(index).get("Transaction_Value"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Transaction_Value:- " + maskingRecords.get(index).get("Transaction_Value") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Transaction_Value:- " + maskingRecords.get(index).get("Transaction_Value") +
                                " is not equal with Non-Masking Transaction_Value:- " + dqRecords.get(index).get("Transaction_Value") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Transaction_Value:- " + maskingRecords.get(index).get("Transaction_Value") +
                                " is not equal with Non-Masking Transaction_Value:- " + dqRecords.get(index).get("Transaction_Value") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Foreign_Value
                    if (maskingRecords.get(index).get("Foreign_Value").equals(
                            dqRecords.get(index).get("Foreign_Value"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Foreign_Value:- " + maskingRecords.get(index).get("Foreign_Value") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Foreign_Value:- " + maskingRecords.get(index).get("Foreign_Value") +
                                " is not equal with Non-Masking Foreign_Value:- " + dqRecords.get(index).get("Foreign_Value") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Foreign_Value:- " + maskingRecords.get(index).get("Foreign_Value") +
                                " is not equal with Non-Masking Foreign_Value:- " + dqRecords.get(index).get("Foreign_Value") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Transaction_Code
                    if (maskingRecords.get(index).get("Transaction_Code").equals(
                            dqRecords.get(index).get("Transaction_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Transaction_Code:- " + maskingRecords.get(index).get("Transaction_Code") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Transaction_Code:- " + maskingRecords.get(index).get("Transaction_Code") +
                                " is not equal with Non-Masking Transaction_Code:- " + dqRecords.get(index).get("Transaction_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Transaction_Code:- " + maskingRecords.get(index).get("Transaction_Code") +
                                " is not equal with Non-Masking Transaction_Code:- " + dqRecords.get(index).get("Transaction_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Currency_Code
                    if (maskingRecords.get(index).get("Currency_Code").equals(
                            dqRecords.get(index).get("Currency_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Currency_Code:- " + maskingRecords.get(index).get("Currency_Code") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Currency_Code:- " + maskingRecords.get(index).get("Currency_Code") +
                                " is not equal with Non-Masking Currency_Code:- " + dqRecords.get(index).get("Currency_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Currency_Code:- " + maskingRecords.get(index).get("Currency_Code") +
                                " is not equal with Non-Masking Currency_Code:- " + dqRecords.get(index).get("Currency_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Debit_Credit_Flag
                    if (maskingRecords.get(index).get("Debit_Credit_Flag").equals(
                            dqRecords.get(index).get("Debit_Credit_Flag"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Debit_Credit_Flag:- " + maskingRecords.get(index).get("Debit_Credit_Flag") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Debit_Credit_Flag:- " + maskingRecords.get(index).get("Debit_Credit_Flag") +
                                " is not equal with Non-Masking Debit_Credit_Flag:- " + dqRecords.get(index).get("Debit_Credit_Flag") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Debit_Credit_Flag:- " + maskingRecords.get(index).get("Debit_Credit_Flag") +
                                " is not equal with Non-Masking Debit_Credit_Flag:- " + dqRecords.get(index).get("Debit_Credit_Flag") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }


                    if (failedMessages.size() != 0) {
                        String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                        if (!failedMessage.isEmpty()) {
                            maskingRecords.get(index).put("Result", failedMessage);
                            maskingRecords.get(index).put("FileName", fileName);
                            failedMessages = null;
                            failedMessages = new LinkedList<>();
                        }
                    } else {
                        maskingRecords.get(index).put("Result", "**PASS**");
                        maskingRecords.get(index).put("FileName", fileName);
                    }


                }

            } else {
                test.fail("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
                System.err.println("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
            }

            if (headers == null && maskingRecords.size() != 0) {
                headers = maskingRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : maskingRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }

        }

        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + "_dq.txt"), results);
        if (results.get(0) != null) {
            HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
        } else {
            System.err.println("There is no data in table - " + tableId);
        }

    }

    @Then("^validate (\\d+)-depositor/Deposit account reference table masking content$")
    public void validateDepositorDepositAccountReferenceTableMaskingContent(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_masking").mkdir();
        }
        List<String> fileNames = new DQRules().getMaskingFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        List<String> results = new LinkedList<>();
        String mi = null;

        List<String> fileValues = new DQRules().getMaskingFileNames(tableId);
        String headers = null;
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            List<String> depositorIdsTable500 = null;
            List<String> subSystemFilesDepositorIds0100 = null;
            List<String> accountUniqueIdsTable500 = null;
            List<String> subSystemFilesAccountUniqueIds0130 = null;
            test.log("INFO", "-----Masking File Name--> " + fileName + " --------");
            System.out.println("-----Masking File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/maskingFiles/" + fileName);
            List<Map<String, String>> maskingRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            if (maskingRecords.size() == dqRecords.size()) {
                test.pass("**********Pass Statement********* Masking file and DQ file:- " + fileName +
                        " records size is equal and it's size is " + maskingRecords.size());
                for (int index = 0; index < maskingRecords.size(); index++) {
                    // Depositor_Unique_ID
                    if (!(maskingRecords.get(index).get("Depositor_Unique_ID").equals(
                            dqRecords.get(index).get("Depositor_Unique_ID"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Depositor_Unique_ID")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Depositor_Unique_ID is masked, Masking Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " and DQ  Depositor_Unique_ID:- " + dqRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        System.err.println("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + " is same as Masking which is not as per Rule-Masking");
                        test.fail("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + " is same as Masking which is not as per Rule-Masking");
                    }


                    //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification
                    //   Rule-10
                    if (depositorIdsTable500 == null) {
                        depositorIdsTable500 = new DQRules().getListOfString(mi, "0100", "Depositor_Unique_ID", fileName.substring(26, 29), "MASKING");
                        subSystemFilesDepositorIds0100 = new DQRules().getDQFileNames("0100").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).collect(Collectors.toList());
                        if (subSystemFilesDepositorIds0100.size() == 0) {
                            test.fail("--- FAIL ---> Sub_System_File-0100:- " + mi + "*0100" + fileName.substring(22, 29) +
                                    " is not found for Rule-304-Table-100");

                            System.err.println("--- FAIL ---> Sub_System_File-0100:- " + mi + "*0100" + fileName.substring(22, 29) +
                                    " is not found for Rule-304-Table-100");
                        }
                    }

                    boolean depositorIdFlag100 = depositorIdsTable500.contains(maskingRecords.get(index).get("Depositor_Unique_ID"));
                    if (depositorIdFlag100) {
                        test.pass("**********Pass Statement********* Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is as per Rule-10");

                    } else {
                        System.err.println("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                "  is not as per Rule-10 and not found in file " + subSystemFilesDepositorIds0100);
                        test.fail("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                "  is not as per Rule-10 and not found in file " + subSystemFilesDepositorIds0100);
                    }

                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Depositor_Unique_ID"))) {
                        test.pass("**********Pass Statement********* Depositor_Unique_Id:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + "is as per Rule-18");

                    } else {
                        test.fail(
                                "--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                        " is not as per Rule-18");
                        System.err.println("--- FAIL ---> Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " is not as per Rule-18");
                    }
                    // Account_Unique_ID
                    if (!(maskingRecords.get(index).get("Account_Unique_ID").equals(
                            dqRecords.get(index).get("Account_Unique_ID"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Account_Unique_ID")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Account_Unique_ID is masked, Masking Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " and DQ Account_Unique_ID:- " + dqRecords.get(index).get("Account_Unique_ID") +
                                "for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + " is same as Masking " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + " is same as Masking " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") + " which is not as per Rule-Masking");
                    }


                    //Rule:- 10 - Values present the file should be present in other file, Which primary key
//                for other file where as foreign key for current file verification

                    //   Rule-10
                    if (accountUniqueIdsTable500 == null) {
                        accountUniqueIdsTable500 = new DQRules().getListOfString(mi, "0130", "Account_Unique_ID", fileName.substring(26, 29), "MASKING");
                        subSystemFilesAccountUniqueIds0130 = new DQRules().getDQFileNames("0130").stream().filter(x -> x.substring(0, 4).equals(fileName.substring(0, 4))).filter(x -> x.substring(26, 29).equals(fileName.substring(26, 29))).collect(Collectors.toList());
                        if (subSystemFilesAccountUniqueIds0130.size() == 0) {
                            test.fail("--- FAIL ---> Sub_System_File-0130:- " + mi + "*0130" + fileName.substring(22, 29) +
                                    " is not found for Rule-304-Table-130");

                            System.err.println("--- FAIL ---> Sub_System_File-0130:- " + mi + "*0130" + fileName.substring(22, 29) +
                                    " is not found for Rule-304-Table-130");
                        }
                    }

                    boolean accountUniqueIdFlag800 = accountUniqueIdsTable500.contains(maskingRecords.get(index).get("Account_Unique_ID"));
                    if (accountUniqueIdFlag800) {
                        test.pass("**********Pass Statement********* Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + " is as per Rule-10" +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " is not as per Rule-10 and not found in file " + subSystemFilesAccountUniqueIds0130 + " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                        test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " is not as per Rule-10 and not found in file " + subSystemFilesAccountUniqueIds0130 +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    }


                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Account_Unique_ID"))) {
                        test.pass("**********Pass Statement********* Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + "is as per Rule-18");

                    } else {
                        test.fail(
                                "--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                        " is not as per Rule-18");
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " is not as per Rule-18");
                    }

                    // Relationship_Type_Code
                    if (maskingRecords.get(index).get("Relationship_Type_Code").equals(
                            dqRecords.get(index).get("Relationship_Type_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Relationship_Type_Code:- " + maskingRecords.get(index).get("Relationship_Type_Code") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Relationship_Type_Code:- " + maskingRecords.get(index).get("Relationship_Type_Code") +
                                " is not equal with Non-Masking Relationship_Type_Code:- " + dqRecords.get(index).get("Relationship_Type_Code") +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Relationship_Type_Code:- " + maskingRecords.get(index).get("Relationship_Type_Code") +
                                " is not equal with Non-Masking Relationship_Type_Code:- " + dqRecords.get(index).get("Relationship_Type_Code") +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Primary_Account_Holder_Flag
                    if (maskingRecords.get(index).get("Primary_Account_Holder_Flag").equals(
                            dqRecords.get(index).get("Primary_Account_Holder_Flag"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Primary_Account_Holder_Flag:- " + maskingRecords.get(index).get("Primary_Account_Holder_Flag") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Primary_Account_Holder_Flag:- " + maskingRecords.get(index).get("Primary_Account_Holder_Flag") +
                                " is not equal with Non-Masking Primary_Account_Holder_Flag:- " + dqRecords.get(index).get("Primary_Account_Holder_Flag") +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Primary_Account_Holder_Flag:- " + maskingRecords.get(index).get("Primary_Account_Holder_Flag") +
                                " is not equal with Non-Masking Primary_Account_Holder_Flag:- " + dqRecords.get(index).get("Primary_Account_Holder_Flag") +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Payee_Flag
                    if (maskingRecords.get(index).get("Payee_Flag").equals(
                            dqRecords.get(index).get("Payee_Flag"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Payee_Flag:- " + maskingRecords.get(index).get("Payee_Flag") + "is equal " +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Payee_Flag:- " + maskingRecords.get(index).get("Payee_Flag") +
                                " is not equal with Non-Masking Payee_Flag:- " + dqRecords.get(index).get("Payee_Flag") +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Payee_Flag:- " + maskingRecords.get(index).get("Payee_Flag") +
                                " is not equal with Non-Masking Payee_Flag:- " + dqRecords.get(index).get("Payee_Flag") +
                                " for Depositor_Unique_ID:- " + maskingRecords.get(index).get("Depositor_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    if (failedMessages.size() != 0) {
                        String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                        if (!failedMessage.isEmpty()) {
                            maskingRecords.get(index).put("Result", failedMessage);
                            maskingRecords.get(index).put("FileName", fileName);
                            failedMessages = null;
                            failedMessages = new LinkedList<>();
                        }
                    } else {
                        maskingRecords.get(index).put("Result", "**PASS**");
                        maskingRecords.get(index).put("FileName", fileName);
                    }


                }

            } else {
                test.fail("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
                System.err.println("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
            }

            if (headers == null && maskingRecords.size() > 0) {
                headers = maskingRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : maskingRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + ".txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    @Then("^validate (\\d+)-Ledger and Sub-Ledger Balances masking content$")
    public void validateLedgerAndSubLedgerBalancesMaskingContent(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_masking").mkdir();
        }
        List<String> fileNames = new DQRules().getMaskingFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        List<String> results = new LinkedList<>();
        String mi = null;

        List<String> fileValues = new DQRules().getMaskingFileNames(tableId);
        String headers = null;

        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);

            test.log("INFO", "-----Masking File Name--> " + fileName + " --------");
            System.out.println("-----Masking File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/maskingFiles/" + fileName);
            List<Map<String, String>> maskingRecords = ReadTextFile.getDqSamples();
            List<String> accountUniqueIdsTable10 = null;
            DQRules rules = new DQRules();
            if (maskingRecords.size() == dqRecords.size()) {
                test.pass("**********Pass Statement********* Masking file and DQ file:- " + fileName +
                        " records size is equal and it's size is " + maskingRecords.size());
                for (int index = 0; index < maskingRecords.size(); index++) {

                    // Ledger_Account
                    if (maskingRecords.get(index).get("Ledger_Account").equals(
                            dqRecords.get(index).get("Ledger_Account"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Ledger_Account:- " + maskingRecords.get(index).get("Ledger_Account") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Ledger_Account:- " + maskingRecords.get(index).get("Ledger_Account") +
                                " is not equal with Non-Masking Ledger_Account:- " + dqRecords.get(index).get("Ledger_Account") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Ledger_Account:- " + maskingRecords.get(index).get("Ledger_Account") +
                                " is not equal with Non-Masking Ledger_Account:- " + dqRecords.get(index).get("Ledger_Account") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Ledger_Description
                    if (maskingRecords.get(index).get("Ledger_Description").equals(
                            dqRecords.get(index).get("Ledger_Description"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Ledger_Description:- " + maskingRecords.get(index).get("Ledger_Description") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Ledger_Description:- " + maskingRecords.get(index).get("Ledger_Description") +
                                " is not equal with Non-Masking Ledger_Description:- " + dqRecords.get(index).get("Ledger_Description") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Ledger_Description:- " + maskingRecords.get(index).get("Ledger_Description") +
                                " is not equal with Non-Masking Ledger_Description:- " + dqRecords.get(index).get("Ledger_Description") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Ledger_Flag
                    if (maskingRecords.get(index).get("Ledger_Flag").equals(
                            dqRecords.get(index).get("Ledger_Flag"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Ledger_Flag:- " + maskingRecords.get(index).get("Ledger_Flag") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Ledger_Flag:- " + maskingRecords.get(index).get("Ledger_Flag") +
                                " is not equal with Non-Masking Ledger_Flag:- " + dqRecords.get(index).get("Ledger_Flag") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Ledger_Flag:- " + maskingRecords.get(index).get("Ledger_Flag") +
                                " is not equal with Non-Masking Ledger_Flag:- " + dqRecords.get(index).get("Ledger_Flag") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // GL_Account
                    if (maskingRecords.get(index).get("GL_Account").equals(
                            dqRecords.get(index).get("GL_Account"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule GL_Account:- " + maskingRecords.get(index).get("GL_Account") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " GL_Account:- " + maskingRecords.get(index).get("GL_Account") +
                                " is not equal with Non-Masking GL_Account:- " + dqRecords.get(index).get("GL_Account") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " GL_Account:- " + maskingRecords.get(index).get("GL_Account") +
                                " is not equal with Non-Masking GL_Account:- " + dqRecords.get(index).get("GL_Account") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Debit
                    if (maskingRecords.get(index).get("Debit").equals(
                            dqRecords.get(index).get("Debit"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Debit:- " + maskingRecords.get(index).get("Debit") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Debit:- " + maskingRecords.get(index).get("Debit") +
                                " is not equal with Non-Masking Debit:- " + dqRecords.get(index).get("Debit") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Debit:- " + maskingRecords.get(index).get("Debit") +
                                " is not equal with Non-Masking Debit:- " + dqRecords.get(index).get("Debit") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Credit
                    if (maskingRecords.get(index).get("Credit").equals(
                            dqRecords.get(index).get("Credit"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Credit:- " + maskingRecords.get(index).get("Credit") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Credit:- " + maskingRecords.get(index).get("Credit") +
                                " is not equal with Non-Masking Credit:- " + dqRecords.get(index).get("Credit") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Credit:- " + maskingRecords.get(index).get("Credit") +
                                " is not equal with Non-Masking Credit:- " + dqRecords.get(index).get("Credit") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }


                    //   Account_Unique_ID
                    if (!(maskingRecords.get(index).get("Account_Unique_ID").equals(
                            dqRecords.get(index).get("Account_Unique_ID"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Account_Unique_ID")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Account_Unique_ID is masked, Masking Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " and DQ Account_Unique_ID:- " + dqRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + " is same as Non-Masking which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + " is same as Non-Masking which is not as per Rule-Masking");
                    }


                    //Account_Number
                    if (!(maskingRecords.get(index).get("Account_Number").equals(
                            dqRecords.get(index).get("Account_Number"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Account_Number")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Account_Number is masked, Masking Account_Number:- " + maskingRecords.get(index).get("Account_Number") +
                                " and DQ Account_Number:- " + dqRecords.get(index).get("Account_Number" +
                                "for Ledger_Account:- " + maskingRecords.get(index).get("Ledger_Account")));
                    } else {
                        test.fail("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") + " is same as Non-Masking " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Account_Number:- " + maskingRecords.get(index).get("Account_Number") + " is same as Non-Masking " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }


                    // Account_Balance
                    if (maskingRecords.get(index).get("Account_Balance").equals(
                            dqRecords.get(index).get("Account_Balance"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Account_Balance:- " + maskingRecords.get(index).get("Account_Balance") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Account_Balance:- " + maskingRecords.get(index).get("Account_Balance") +
                                " is not equal with Non-Masking Account_Balance:- " + dqRecords.get(index).get("Account_Balance") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Account_Balance:- " + maskingRecords.get(index).get("Account_Balance") +
                                " is not equal with Non-Masking Account_Balance:- " + dqRecords.get(index).get("Account_Balance") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    if (failedMessages.size() != 0) {
                        String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                        if (!failedMessage.isEmpty()) {
                            maskingRecords.get(index).put("Result", failedMessage);
                            maskingRecords.get(index).put("FileName", fileName);
                            failedMessages = null;
                            failedMessages = new LinkedList<>();
                        }
                    } else {
                        maskingRecords.get(index).put("Result", "**PASS**");
                        maskingRecords.get(index).put("FileName", fileName);
                    }


                }

            } else {
                test.fail("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
                System.err.println("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
            }

            if (headers == null && maskingRecords.size() > 0) {
                headers = maskingRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : maskingRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }

        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + ".txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }


    @Then("^validate (\\d+)-Hold Balance File masking content$")
    public void validateHoldBalanceFileMaskingContent(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_masking").mkdir();
        }
        List<String> fileNames = new DQRules().getMaskingFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        List<String> results = new LinkedList<>();
        String mi = null;
        String headers = null;
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----Masking File Name--> " + fileName + " --------");
            System.out.println("-----Masking File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/maskingFiles/" + fileName);
            List<Map<String, String>> maskingRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();
            if (maskingRecords.size() == dqRecords.size()) {
                test.pass("**********Pass Statement********* Masking file and DQ file:- " + fileName +
                        " records size is equal and it's size is " + maskingRecords.size());
                for (int index = 0; index < maskingRecords.size(); index++) {
//                    Account_Unique_ID
                    if (!(maskingRecords.get(index).get("Account_Unique_ID").equals(
                            dqRecords.get(index).get("Account_Unique_ID"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Account_Unique_ID")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Account_Unique_ID is masked, Masking Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " and DQ Account_Unique_ID:- " + dqRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + " is same as Non-Masking which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + " is same as Non-Masking which is not as per Rule-Masking");
                    }

                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Account_Unique_ID"))) {
                        test.pass("**********Pass Statement********* Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + "is as per Rule-18");
                    } else {
                        test.fail(
                                "--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                        " is not as per Rule-18");
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " is not as per Rule-18");
                    }

                    // Subsystem_ID
                    if (maskingRecords.get(index).get("Subsystem_ID").equals(
                            dqRecords.get(index).get("Subsystem_ID"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Subsystem_ID:- " + maskingRecords.get(index).get("Subsystem_ID") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Subsystem_ID:- " + maskingRecords.get(index).get("Currency_Code") +
                                " is not equal with Non-Masking Subsystem_ID:- " + dqRecords.get(index).get("Currency_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Masking Subsystem_ID:- " + maskingRecords.get(index).get("Subsystem_ID") +
                                " is not equal with DQ Subsystem_ID:- " + dqRecords.get(index).get("Subsystem_ID") + " for Account_Unique_ID:- "
                                + maskingRecords.get(index).get("Account_Unique_ID"));
                    }

                    // CDIC_Hold_Status_Code
                    if (maskingRecords.get(index).get("CDIC_Hold_Status_Code").equals(
                            dqRecords.get(index).get("CDIC_Hold_Status_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule CDIC_Hold_Status_Code:- " + maskingRecords.get(index).get("CDIC_Hold_Status_Code") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " CDIC_Hold_Status_Code:- " + maskingRecords.get(index).get("CDIC_Hold_Status_Code") +
                                " is not equal with Non-Masking CDIC_Hold_Status_Code:- " + dqRecords.get(index).get("CDIC_Hold_Status_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " CDIC_Hold_Status_Code:- " + maskingRecords.get(index).get("CDIC_Hold_Status_Code") +
                                " is not equal with Non-Masking CDIC_Hold_Status_Code:- " + dqRecords.get(index).get("CDIC_Hold_Status_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Account_Balance
                    if (maskingRecords.get(index).get("Account_Balance").equals(
                            dqRecords.get(index).get("Account_Balance"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Account_Balance:- " + maskingRecords.get(index).get("Account_Balance") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Account_Balance:- " + maskingRecords.get(index).get("Account_Balance") +
                                " is not equal with Non-Masking Account_Balance:- " + dqRecords.get(index).get("Account_Balance") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Account_Balance:- " + maskingRecords.get(index).get("Account_Balance") +
                                " is not equal with Non-Masking Account_Balance:- " + dqRecords.get(index).get("Account_Balance") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Accessible_Balance
                    if (maskingRecords.get(index).get("Accessible_Balance").equals(
                            dqRecords.get(index).get("Accessible_Balance"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Accessible_Balance:- " + maskingRecords.get(index).get("Accessible_Balance") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Accessible_Balance:- " + maskingRecords.get(index).get("Accessible_Balance") +
                                " is not equal with Non-Masking Accessible_Balance:- " + dqRecords.get(index).get("Accessible_Balance") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Accessible_Balance:- " + maskingRecords.get(index).get("Accessible_Balance") +
                                " is not equal with Non-Masking Accessible_Balance:- " + dqRecords.get(index).get("Accessible_Balance") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // CDIC_Hold_Amount
                    if (maskingRecords.get(index).get("CDIC_Hold_Amount").equals(
                            dqRecords.get(index).get("CDIC_Hold_Amount"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule CDIC_Hold_Amount:- " + maskingRecords.get(index).get("CDIC_Hold_Amount") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " CDIC_Hold_Amount:- " + maskingRecords.get(index).get("CDIC_Hold_Amount") +
                                " is not equal with Non-Masking CDIC_Hold_Amount:- " + dqRecords.get(index).get("CDIC_Hold_Amount") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " CDIC_Hold_Amount:- " + maskingRecords.get(index).get("CDIC_Hold_Amount") +
                                " is not equal with Non-Masking CDIC_Hold_Amount:- " + dqRecords.get(index).get("CDIC_Hold_Amount") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Currency_Code
                    if (maskingRecords.get(index).get("Currency_Code").equals(
                            dqRecords.get(index).get("Currency_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Currency_Code:- " + maskingRecords.get(index).get("Currency_Code") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Currency_Code:- " + maskingRecords.get(index).get("Currency_Code") +
                                " is not equal with Non-Masking Currency_Code:- " + dqRecords.get(index).get("Currency_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Currency_Code:- " + maskingRecords.get(index).get("Currency_Code") +
                                " is not equal with Non-Masking Currency_Code:- " + dqRecords.get(index).get("Currency_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    if (failedMessages.size() != 0) {
                        String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                        if (!failedMessage.isEmpty()) {
                            maskingRecords.get(index).put("Result", failedMessage);
                            maskingRecords.get(index).put("FileName", fileName);
                            failedMessages = null;
                            failedMessages = new LinkedList<>();
                        }
                    } else {
                        maskingRecords.get(index).put("Result", "**PASS**");
                        maskingRecords.get(index).put("FileName", fileName);
                    }


                }

            } else {
                test.fail("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
                System.err.println("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
            }

            if (headers == null && maskingRecords.size() > 0) {
                headers = maskingRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : maskingRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + ".txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));

    }


    @Then("^validate (\\d+)-Account Accrued Interest Data masking content$")
    public void validateAccountAccruedInterestDataMaskingContent(String tableId) throws IOException {
        if (!(new File(System.getProperty("user.dir") + "/" + tableId).exists())) {
            new File(System.getProperty("user.dir") + "/" + tableId + "_masking").mkdir();
        }
        List<String> fileNames = new DQRules().getMaskingFileNames(tableId);
        ReadTextFile readTextFile = new ReadTextFile();
        List<String> results = new LinkedList<>();
        String mi = null;
        String headers = null;
        for (String fileName : fileNames) {
            mi = fileName.substring(0, 4);
            test.log("INFO", "-----Masking File Name--> " + fileName + " --------");
            System.out.println("-----Masking File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/dqFiles/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/maskingFiles/" + fileName);
            List<Map<String, String>> maskingRecords = ReadTextFile.getDqSamples();
            DQRules rules = new DQRules();

            if (maskingRecords.size() == dqRecords.size()) {
                test.pass("**********Pass Statement********* Masking file and DQ file:- " + fileName +
                        " records size is equal and it's size is " + maskingRecords.size());
                for (int index = 0; index < maskingRecords.size(); index++) {
                    if (!(maskingRecords.get(index).get("Account_Unique_ID").equals(
                            dqRecords.get(index).get("Account_Unique_ID"))) &&
                            !(rules.checkMaskedOrNotMasked(maskingRecords.get(index).get("Account_Unique_ID")))) {
                        test.pass("**********Pass Statement********* DQ file and Masking file Account_Unique_ID is masked, Masking Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " and DQ Account_Unique_ID:- " + dqRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + " is same as Non-Masking which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + " is same as Non-Masking which is not as per Rule-Masking");
                    }

                    //   Rule-18
                    if (rules.noBlankRule18(maskingRecords.get(index).get("Account_Unique_ID"))) {
                        test.pass("**********Pass Statement********* Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") + "is as per Rule-18");

                    } else {
                        test.fail("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " is not as per Rule-18");
                        System.err.println("--- FAIL ---> Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " is not as per Rule-18");
                    }

// Subsystem_ID
                    if (maskingRecords.get(index).get("Subsystem_ID").equals(
                            dqRecords.get(index).get("Subsystem_ID"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Subsystem_ID:- " + maskingRecords.get(index).get("Subsystem_ID") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Subsystem_ID:- " + maskingRecords.get(index).get("Subsystem_ID") +
                                " is not equal with Non-Masking Subsystem_ID:- " + dqRecords.get(index).get("Subsystem_ID") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Subsystem_ID:- " + maskingRecords.get(index).get("Subsystem_ID") +
                                " is not equal with Non-Masking Subsystem_ID:- " + dqRecords.get(index).get("Subsystem_ID") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Last_Interest_Payment_Date
                    if (maskingRecords.get(index).get("Last_Interest_Payment_Date").equals(
                            dqRecords.get(index).get("Last_Interest_Payment_Date"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Last_Interest_Payment_Date:- " + maskingRecords.get(index).get("Last_Interest_Payment_Date") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Last_Interest_Payment_Date:- " + maskingRecords.get(index).get("Last_Interest_Payment_Date") +
                                " is not equal with Non-Masking Last_Interest_Payment_Date:- " + dqRecords.get(index).get("Last_Interest_Payment_Date") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Last_Interest_Payment_Date:- " + maskingRecords.get(index).get("Last_Interest_Payment_Date") +
                                " is not equal with Non-Masking Last_Interest_Payment_Date:- " + dqRecords.get(index).get("Last_Interest_Payment_Date") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Interest_Accrued_Amount
                    if (maskingRecords.get(index).get("Interest_Accrued_Amount").equals(
                            dqRecords.get(index).get("Interest_Accrued_Amount"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Interest_Accrued_Amount:- " + maskingRecords.get(index).get("Interest_Accrued_Amount") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Interest_Accrued_Amount:- " + maskingRecords.get(index).get("Interest_Accrued_Amount") +
                                " is not equal with Non-Masking Interest_Accrued_Amount:- " + dqRecords.get(index).get("Interest_Accrued_Amount") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Interest_Accrued_Amount:- " + maskingRecords.get(index).get("Interest_Accrued_Amount") +
                                " is not equal with Non-Masking Interest_Accrued_Amount:- " + dqRecords.get(index).get("Interest_Accrued_Amount") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    // Currency_Code
                    if (maskingRecords.get(index).get("Currency_Code").equals(
                            dqRecords.get(index).get("Currency_Code"))) {
                        test.pass("**********Pass Statement********* Index- " + index + " Masking an DQ rule Currency_Code:- " + maskingRecords.get(index).get("Currency_Code") + "is equal " +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID"));
                    } else {
                        test.fail("--- FAIL ---> Index- " + index + " Currency_Code:- " + maskingRecords.get(index).get("Currency_Code") +
                                " is not equal with Non-Masking Currency_Code:- " + dqRecords.get(index).get("Currency_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                        System.err.println("--- FAIL ---> Index- " + index + " Currency_Code:- " + maskingRecords.get(index).get("Currency_Code") +
                                " is not equal with Non-Masking Currency_Code:- " + dqRecords.get(index).get("Currency_Code") +
                                " for Account_Unique_ID:- " + maskingRecords.get(index).get("Account_Unique_ID") +
                                " which is not as per Rule-Masking");
                    }

                    if (failedMessages.size() != 0) {
                        String failedMessage = failedMessages.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(";"));
                        if (!failedMessage.isEmpty()) {
                            maskingRecords.get(index).put("Result", failedMessage);
                            maskingRecords.get(index).put("FileName", fileName);
                            failedMessages = null;
                            failedMessages = new LinkedList<>();
                        }
                    } else {
                        maskingRecords.get(index).put("Result", "**PASS**");
                        maskingRecords.get(index).put("FileName", fileName);
                    }


                }

            } else {
                test.fail("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
                System.err.println("--- FAIL ---> Masking file and DQ file:- " + fileName +
                        " records size is not equal, Masking records size is " + maskingRecords.size() +
                        " and DQ records size is " + dqRecords.size());
            }

            if (headers == null && maskingRecords.size() > 0) {
                headers = maskingRecords.get(0).entrySet().stream().map(x -> x.getKey()).collect(Collectors.joining(","));
            }


            for (Map<String, String> mapObj : maskingRecords) {
                results.add(mapObj.values().stream().map(x -> x.valueOf(x).replaceAll(",", "")).collect(Collectors.joining(",")));
            }


        }
        results.add(0, headers);
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + ".txt"), results);
        HashMap<String, Map<String, Integer>> summaryReport = new DQRules().generateReport(results);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(Paths.get(System.getProperty("user.dir") + "/" + tableId + "_masking/" + tableId + "_summaryReport.json"), Collections.singleton(gson.toJson(summaryReport)));
    }

    @Given("^verify tables \"([^\"]*)\" reference table \"([^\"]*)\"$")
    public void verifyTablesReferenceTable(String table, String referenceTables) throws Throwable {
        List<String> listOfFiles = new DQRules().getDQFileNames(table);
        for (String file : listOfFiles) {
            String mi = file.substring(0, 4);
            String subSystemValue = file.substring(26, 29);
            String[] referenceValues = referenceTables.split(",");
            for (String referenceTable : referenceValues) {
                List<String> referenceTablesList = new DQRules().getDQFileNames(referenceTable);
                List<String> referenceTablesMiList = referenceTablesList.stream().filter(x -> x.substring(0, 4).equals(mi)).collect(Collectors.toList());
                List<String> referenceTableMISubsystemFiles = referenceTablesMiList.stream().filter(x -> x.substring(26, 29).equals(subSystemValue)).collect(Collectors.toList());
                int referenceFilesCount = referenceTableMISubsystemFiles.size();

                if ((table.equals("0100") || table.equals("0110") || table.equals("0120") || table.equals("0121")
                        || table.equals("0201") || table.equals("0202") || table.equals("0211")
                        || table.equals("0212") || table.equals("0221"))) {
                    if ((referenceTable.equals("0130") || referenceTable.equals("0140") || referenceTable.equals("0152") || referenceTable.equals("0153")
                            || referenceTable.equals("0160") || referenceTable.equals("0231") || referenceTable.equals("0232")
                            || referenceTable.equals("0233") || referenceTable.equals("0234") || referenceTable.equals("0235") || referenceTable.equals("0236")
                            || referenceTable.equals("0237") || referenceTable.equals("0238") || referenceTable.equals("0239") || referenceTable.equals("0240")
                            || referenceTable.equals("0241") || referenceTable.equals("0242") || referenceTable.equals("0400") || referenceTable.equals("0401")
                            || referenceTable.equals("0500") || referenceTable.equals("0501") || referenceTable.equals("0600") || referenceTable.equals("0800")
                            || referenceTable.equals("0900") || referenceTable.equals("0999"))) {
                        if (referenceFilesCount == 0) {
                         /*   System.out.println("**********Pass Statement******** File:- " + file + " Table:- " + table +
                                    " Reference Table:- " + referenceTable + " reference table count:-" + referenceFilesCount);*/
                        } else {
                            System.err.println("--- FAIL ---> File:- " + file + " Table:- " + table +
                                    " Reference Table:- " + referenceTable + " Actual reference table count:-" + referenceFilesCount +
                                    " and Expected reference table count:- '0'");
                            test.fail("--- FAIL ---> File:- " + file + " Table:- " + table +
                                    " Reference Table:- " + referenceTable + " Actual reference table count:-" + referenceFilesCount +
                                    " and Expected reference table count:- '0'");
                        }
                    } else if ((referenceTable.equals("0100") || referenceTable.equals("0110") || referenceTable.equals("0120") || referenceTable.equals("0121")
                            || referenceTable.equals("0201") || referenceTable.equals("0202") || referenceTable.equals("0211")
                            || referenceTable.equals("0212") || referenceTable.equals("0221"))) {
                        if (referenceFilesCount == 1) {
                           /* System.out.println("**********Pass Statement******** File:- " + file + " Table:- " + table +
                                    " Reference Table:- " + referenceTable + " reference table count:-" + referenceFilesCount);*/
                        } else {
                            System.err.println("--- FAIL ---> File:- " + file + " Table:- " + table +
                                    " Reference Table:- " + referenceTable + " Actual reference table count:-" + referenceFilesCount +
                                    " and Expected reference table count:- '0'");
                            test.fail("--- FAIL ---> File:- " + file + " Table:- " + table +
                                    " Reference Table:- " + referenceTable + " Actual reference table count:-" + referenceFilesCount +
                                    " and Expected reference table count:- '0'");
                        }
                    } else {
                        System.err.println("--- FAIL ---> File:- " + file + " Unkonwn TablId:- " + table +
                                " it is not as per DSR-3.1");
                        test.fail("--- FAIL ---> File:- " + file + " Unkonwn TablId:- " + table +
                                " it is not as per DSR-3.1");
                    }

                } else if ((table.equals("0130") || table.equals("0140") || table.equals("0152") || table.equals("0153")
                        || table.equals("0160") || table.equals("0231") || table.equals("0232")
                        || table.equals("0233") || table.equals("0234") || table.equals("0235") || table.equals("0236")
                        || table.equals("0237") || table.equals("0238") || table.equals("0239") || table.equals("0240")
                        || table.equals("0241") || table.equals("0242") || table.equals("0400") || table.equals("0401")
                        || table.equals("0500") || table.equals("0501") || table.equals("0600") || table.equals("0800")
                        || table.equals("0900") || table.equals("0999"))) {
                    if ((referenceTable.equals("0130") || referenceTable.equals("0140") || referenceTable.equals("0152") || referenceTable.equals("0153")
                            || referenceTable.equals("0160") || referenceTable.equals("0231") || referenceTable.equals("0232")
                            || referenceTable.equals("0233") || referenceTable.equals("0234") || referenceTable.equals("0235") || referenceTable.equals("0236")
                            || referenceTable.equals("0237") || referenceTable.equals("0238") || referenceTable.equals("0239") || referenceTable.equals("0240")
                            || referenceTable.equals("0241") || referenceTable.equals("0242") || referenceTable.equals("0400") || referenceTable.equals("0401")
                            || referenceTable.equals("0500") || referenceTable.equals("0501") || referenceTable.equals("0600") || referenceTable.equals("0800")
                            || referenceTable.equals("0900") || referenceTable.equals("0999"))) {
                        if (referenceFilesCount == 1) {
                            /*System.out.println("**********Pass Statement******** File:- " + file + " Table:- " + table +
                                    " Reference Table:- " + referenceTable + " reference table count:-" + referenceFilesCount);*/
                        } else {
                            System.err.println("--- FAIL ---> File:- " + file + " Table:- " + table +
                                    " Reference Table:- " + referenceTable + " Actual reference table count:-" + referenceFilesCount +
                                    " and Expected reference table count:- '1'");
                            test.fail("--- FAIL ---> File:- " + file + " Table:- " + table +
                                    " Reference Table:- " + referenceTable + " Actual reference table count:-" + referenceFilesCount +
                                    " and Expected reference table count:- '1'");
                        }
                    } else if ((referenceTable.equals("0100") || referenceTable.equals("0110") || referenceTable.equals("0120") || referenceTable.equals("0121")
                            || referenceTable.equals("0201") || referenceTable.equals("0202") || referenceTable.equals("0211")
                            || referenceTable.equals("0212") || referenceTable.equals("0221"))) {
                        if (referenceFilesCount == 0) {
                           /* System.out.println("**********Pass Statement******** File:- " + file + " Table:- " + table +
                                    " Reference Table:- " + referenceTable + " reference table count:-" + referenceFilesCount);*/
                        } else {
                            System.err.println("--- FAIL ---> File:- " + file + " Table:- " + table +
                                    " Reference Table:- " + referenceTable + " Actual reference table count:-" + referenceFilesCount +
                                    " and Expected reference table count:- '1'");
                            test.fail("--- FAIL ---> File:- " + file + " Table:- " + table +
                                    " Reference Table:- " + referenceTable + " Actual reference table count:-" + referenceFilesCount +
                                    " and Expected reference table count:- '1'");
                        }
                    } else {
                        System.err.println("--- FAIL ---> File:- " + file + " Unkonwn TablId:- " + table +
                                " it is not as per DSR-3.1");
                        test.fail("--- FAIL ---> File:- " + file + " Unkonwn TablId:- " + table +
                                " it is not as per DSR-3.1");
                    }

                }
            }

        }

        Files.write(Paths.get(System.getProperty("user.dir") + "/missingFiles.txt"), failedMessages);
    }

    private List<Map<String, String>> getMdmDbCredentials() {
        List<Map<String, String>> mdmObject = new LinkedList<>();
        Map<String, String> dbProperties = new HashMap<>();
        dbProperties.put("dbHost", ConfigurationManager.getBundle().getPropertyValue("mdm.dbHost"));
        dbProperties.put("dbPort", ConfigurationManager.getBundle().getPropertyValue("mdm.dbPort"));
        dbProperties.put("databaseName", ConfigurationManager.getBundle().getPropertyValue("mdm.databaseName"));
        dbProperties.put("dbUserName", ConfigurationManager.getBundle().getPropertyValue("mdm.dbUserName"));
        dbProperties.put("dbPassword", ConfigurationManager.getBundle().getPropertyValue("mdm.dbPassword"));
        dbProperties.put("dbSchema", ConfigurationManager.getBundle().getPropertyValue("mdm.dbSchema"));
        mdmObject.add(dbProperties);
        return mdmObject;
    }

    private List<Map<String, String>> get7KDbCredentials() {
        List<Map<String, String>> sevenKObject = new LinkedList<>();
        Map<String, String> dbProperties = new HashMap<>();
        dbProperties.put("dbHost", ConfigurationManager.getBundle().getPropertyValue("7k.dbHost"));
        dbProperties.put("dbPort", ConfigurationManager.getBundle().getPropertyValue("7k.dbPort"));
        dbProperties.put("databaseName", ConfigurationManager.getBundle().getPropertyValue("7k.databaseName"));
        dbProperties.put("dbUserName", ConfigurationManager.getBundle().getPropertyValue("7k.dbUserName"));
        dbProperties.put("dbPassword", ConfigurationManager.getBundle().getPropertyValue("7k.dbPassword"));
        dbProperties.put("dbSchema", ConfigurationManager.getBundle().getPropertyValue("7k.dbSchema"));
        sevenKObject.add(dbProperties);
        return sevenKObject;
    }


    public void validate0152BBRecordsAgainst7KAndMDMDB(String table, String accountType) throws IOException {
        ReadTextFile readTextFile = new ReadTextFile();
        List<Map<String, String>> mdmCredentials = getMdmDbCredentials();
        List<Map<String, String>> sevenKCredentials = get7KDbCredentials();
        DataBaseData mdmDbObect = new DataBaseData(mdmCredentials);
        DataBaseData sevenKDbObect = new DataBaseData(sevenKCredentials);
        List<Map<String, String>> fileRecords = null;
        List<String> fileNames = new DQRules().getDQ0152FileNames(table)
                .stream().filter(x -> x.substring(26, 29).equals(accountType.split("-")[1])).collect(Collectors.toList());
        List<Map<String, Object>> countriesList = sevenKDbObect.getResultsListsFromDB("select \"DESC\" from CCBS.T7K_COUNTRY_CODE");
        for (String fileName : fileNames) {


            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/0152FilesDataComparision/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            Set<String> accountNumbers = dqRecords.stream().map(x -> x.get("Account_Number")).collect(Collectors.toSet());
            List<Map<String, Object>> allAccountsRecords = sevenKDbObect.getResultsFromDB("select * from CCBS.T7W_CUST_ACCT_BENE_ASSOC where ACCT_ID in(" +
                    accountNumbers.stream().map(x -> "'" + x + "'").collect(Collectors.joining(",")) + ")");
            //        List<Map<String,String>> dbRecords = sevenKDbObect.getResultsFromDB(query_7k)
            for (Map<String, String> dqRecord : dqRecords) {
//                Validating below field's
//                 Account_Unique_ID
//                 Account_Number
//                 Name
//                 First_Name
//                 Middle_Name
//                 Last_Name
//                 Address_1
//                 Address_2
//                 City
//                  Province
//                  Postal_Code
//                  Country
//                  SIA_Individual_Flag
//                  Interest_In_Deposit_Flag
//                  Interest_In_Deposit
                String schema_7K = ConfigurationManager.getBundle().getPropertyValue("7k.dbSchema");
                String schema_MDM = ConfigurationManager.getBundle().getPropertyValue("7k.dbSchema");
                String accountNumber = dqRecord.get("Account_Number");
                String name = dqRecord.get("Name").trim();
                String address1 = dqRecord.get("Address_1").trim();
                String address2 = dqRecord.get("Address_2").trim();
                String interestInDeposit = dqRecord.get("Interest_In_Deposit");
                String city = dqRecord.get("City").trim();
                String province = dqRecord.get("Province").trim();
                String postalCode = dqRecord.get("Postal_Code").trim();


                List<Map<String, Object>> dbRecords = allAccountsRecords.stream().filter(x -> Objects.equals(x.get("ACCT_ID") == null ? "" : x.get("ACCT_ID").toString().trim(), accountNumber)
                        && Objects.equals(x.get("FULL_NAME") == null ? "" : x.get("FULL_NAME").toString().trim(), name)
                        && Objects.equals(x.get("ADDR_LINE_1_TXT") == null ? "" : x.get("ADDR_LINE_1_TXT").toString().trim(), address1)
                        && Objects.equals(x.get("ADDR_LINE_2_TXT") == null ? "" : x.get("ADDR_LINE_2_TXT").toString().trim(), address2)
                        && Objects.equals(x.get("OWNERSHIP_PCT") == null ? "" : x.get("OWNERSHIP_PCT").toString().trim(), interestInDeposit)
                        && Objects.equals(x.get("CITY_NM") == null ? "" : x.get("CITY_NM").toString().trim(), city)
                        && Objects.equals(x.get("PROV_CD") == null ? "" : x.get("PROV_CD").toString().trim(), province)
                        && Objects.equals(x.get("POSTAL_CD") == null ? "" : x.get("POSTAL_CD").toString().trim(), postalCode)).collect(Collectors.toList());

//                String query_7k = "select * from " + schema_7K + ".T7W_CUST_ACCT_BENE_ASSOC where ACCT_ID='" + accountNumber + "' and FULL_NAME='" + name + "'";
                String query_7k = "select * from " + schema_7K + ".T7W_CUST_ACCT_BENE_ASSOC where ACCT_ID='" + accountNumber + "' and (ADDR_LINE_1_TXT" + address1 + " and FULL_NAME" + name + " and ADDR_LINE_2_TXT" + address2 +
                        "  and OWNERSHIP_PCT" + interestInDeposit + "  and CITY_NM" + city + ")";
                String query_mdm = "select * from " + schema_MDM + ".T7W_CUST_ACCT_BENE_ASSOC where ACCT_ID='" + accountNumber + "' and FULL_NAME='" + name + "'";

//                Map<String, Object> sevenKRecord = sevenKDbObect.getResultsFromDB(query_7k).get(0);
                Map<String, Object> sevenKRecord = dbRecords.size() > 0 ? dbRecords.get(0) : null;
//                Map<String, Object> mdmRecord = mdmDbObect.getResultsFromDB(query_mdm);
                if (sevenKRecord != null) {
                    //                 Account_Unique_ID - 7K
                    if (dqRecord.get("Account_Unique_ID").trim().equals(((String) sevenKRecord.get("PARTY_SUBSYS_MNEMONIC_CD")).trim() + ((String) sevenKRecord.get("ACCT_ID")).trim())) {
//                    System.out.println("7K-> Account_Unique_ID is as expected Account_Unique_ID:-" + dqRecord.get("Account_Unique_ID").trim());
                    } else {
                        System.err.println("7K-> Account_Unique_ID is not as expected Extract Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID").trim()
                                + " and 7K Account_Unique_ID:- " + ((String) sevenKRecord.get("PARTY_SUBSYS_MNEMONIC_CD")).trim() + ((String) sevenKRecord.get("ACCT_ID")).trim());
                    }
/*
//                 Account_Unique_ID - MDM
                    if (dqRecord.get("Account_Unique_ID").trim().equals(((String) mdmRecord.get("PARTY_SUBSYS_MNEMONIC_CD")).trim() + ((String) mdmRecord.get("ACCT_ID")).trim())) {
//                    System.out.println("7K-> Account_Unique_ID is as expected Account_Unique_ID:-" + dqRecord.get("Account_Unique_ID").trim());
                    } else {
                        System.err.println("MDM-> Account_Unique_ID is not as expected Extract Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID").trim()
                                + " and MDM Account_Unique_ID:- " + ((String) mdmRecord.get("PARTY_SUBSYS_MNEMONIC_CD")).trim() + ((String) mdmRecord.get("ACCT_ID")).trim());
                    }
*/

                    //                 Account_Number - 7K
                    if (dqRecord.get("Account_Number").trim().equals(((String) sevenKRecord.get("ACCT_ID")).trim())) {
//                    System.out.println("7K-> Account_Number is as expected Account_Number:-" + dqRecord.get("Account_Number").trim());
                    } else {
                        System.err.println("7K-> Account_Number is not as expected Extract Account_Number:- " + dqRecord.get("Account_Number").trim()
                                + " and 7K Account_Number:- " + ((String) sevenKRecord.get("ACCT_ID")).trim());
                    }

   /*                 //                 Account_Number - MDM
                    if (dqRecord.get("Account_Number").trim().equals(((String) mdmRecord.get("ACCT_ID")).trim())) {
//                    System.out.println("7K-> Account_Number is as expected Account_Number:-" + dqRecord.get("Account_Number").trim());
                    } else {
                        System.err.println("MDM-> Account_Number is not as expected Extract Account_Number:- " + dqRecord.get("Account_Number").trim()
                                + " and MDM Account_Number:- " + ((String) mdmRecord.get("ACCT_ID")).trim());
                    }
*/
                    //                 Name - 7K
                    String Name_7K = sevenKRecord.get("FULL_NAME") == null ? "" : ((String) sevenKRecord.get("FULL_NAME")).trim();
                    if (dqRecord.get("Name").trim().equals(Name_7K)) {
//                    System.out.println("7K-> Name is as expected Name:-" + dqRecord.get("Name").trim());
                    } else {
                        System.err.println("7K-> Name is not as expected Extract Name:- " + dqRecord.get("Name").trim()
                                + " and 7K Name:- " + Name_7K + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }

               /*     //                 Name - MDM
                    String name_MDM = mdmRecord.get("FULL_NAME") == null ? "" : ((String) mdmRecord.get("FULL_NAME")).trim();
                    if (dqRecord.get("Name").trim().equals(name_MDM)) {
//                    System.out.println("7K-> Name is as expected Name:-" + dqRecord.get("Name").trim());
                    } else {
                        System.err.println("MDM-> Name is not as expected Extract Name:- " + dqRecord.get("Name").trim()
                                + " and MDM Name:- " + name_MDM + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/
                    //                 First_Name - 7K
                    String firstName_7K = sevenKRecord.get("FIRST_NAME") == null ? "" : ((String) sevenKRecord.get("FIRST_NAME")).trim();
                    if (dqRecord.get("First_Name").trim().equals(firstName_7K)) {
//                    System.out.println("7K-> First_Name is as expected FIRST_NAME:-" + dqRecord.get("First_Name").trim());
                    } else {
                        System.err.println("7K-> First_Name is not as expected Extract First_Name:- " + dqRecord.get("First_Name").trim()
                                + " and 7K First_Name:- " + firstName_7K + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }

              /*      //                 First_Name - MDM
                    String firstName_mdm = mdmRecord.get("FIRST_NAME") == null ? "" : ((String) mdmRecord.get("FIRST_NAME")).trim();
                    if (dqRecord.get("First_Name").trim().equals(firstName_mdm)) {
//                    System.out.println("7K-> First_Name is as expected FIRST_NAME:-" + dqRecord.get("First_Name").trim());
                    } else {
                        System.err.println("MDM-> First_Name is not as expected Extract First_Name:- " + dqRecord.get("First_Name").trim()
                                + " and MDM First_Name:- " + firstName_mdm + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/
//                 Middle_Name - 7K
                    String middleName_7K = sevenKRecord.get("MIDDLE_NAME") == null ? "" : ((String) sevenKRecord.get("MIDDLE_NAME")).trim();
                    if (dqRecord.get("Middle_Name").trim().equals(middleName_7K)) {
//                    System.out.println("7K-> Middle_Name is as expected Name:-" + dqRecord.get("Middle_Name").trim());
                    } else {
                        System.err.println("7K-> Middle_Name is not as expected Extract Middle_Name:- " + dqRecord.get("Middle_Name").trim()
                                + " and 7K Middle_Name:- " + ((String) sevenKRecord.get("MIDDLE_NAME")).trim() + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }

               /*     //                 Middle_Name - MDM
                    String middleName_mdm = mdmRecord.get("MIDDLE_NAME") == null ? "" : ((String) mdmRecord.get("MIDDLE_NAME")).trim();
                    if (dqRecord.get("Middle_Name").trim().equals(middleName_mdm)) {
//                    System.out.println("7K-> Middle_Name is as expected Name:-" + dqRecord.get("Middle_Name").trim());
                    } else {
                        System.err.println("MDM-> Middle_Name is not as expected Extract Middle_Name:- " + dqRecord.get("Middle_Name").trim()
                                + " and MDM Middle_Name:- " + middleName_mdm + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/
//                 Last_Name - 7K
                    String lastName_7K = sevenKRecord.get("LAST_NAME") == null ? "" : ((String) sevenKRecord.get("LAST_NAME")).trim();
                    if (dqRecord.get("Last_Name").trim().equals(lastName_7K)) {
//                    System.out.println("7K-> Last_Name is as expected LAST_NAME:-" + dqRecord.get("Last_Name").trim());
                    } else {
                        System.err.println("7K-> Last_Name is not as expected Extract Last_Name:- " + dqRecord.get("Last_Name").trim()
                                + " and 7K Last_Name:- " + lastName_7K + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }

              /*      //                 Last_Name - MDM
                    String lastName_mdm = mdmRecord.get("LAST_NAME") == null ? "" : ((String) mdmRecord.get("LAST_NAME")).trim();
                    if (dqRecord.get("Last_Name").trim().equals(lastName_mdm)) {
//                    System.out.println("7K-> Last_Name is as expected LAST_NAME:-" + dqRecord.get("Last_Name").trim());
                    } else {
                        System.err.println("MDM-> Last_Name is not as expected Extract Last_Name:- " + dqRecord.get("Last_Name").trim()
                                + " and MDM Last_Name:- " + lastName_mdm + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/
//                 Address_1 - 7K
                    String address1_7K = sevenKRecord.get("ADDR_LINE_1_TXT") == null ? "" : ((String) sevenKRecord.get("ADDR_LINE_1_TXT")).trim();
                    if (dqRecord.get("Address_1").trim().equals(address1_7K)) {
//                    System.out.println("7K-> Address_1 is as expected Address_1:-" + dqRecord.get("Address_1").trim());
                    } else {
                        System.err.println("7K-> Address_1 is not as expected Extract Address_1:- " + dqRecord.get("Address_1").trim()
                                + " and 7K Address_1:- " + address1_7K + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }

              /*      //                 Address_1 - MDM
                    String address1_mdm = mdmRecord.get("ADDR_LINE_1_TXT") == null ? "" : ((String) mdmRecord.get("ADDR_LINE_1_TXT")).trim();
                    if (dqRecord.get("Address_1").trim().equals(address1_mdm)) {
//                    System.out.println("7K-> Address_1 is as expected Address_1:-" + dqRecord.get("Address_1").trim());
                    } else {
                        System.err.println("MDM-> Address_1 is not as expected Extract Address_1:- " + dqRecord.get("Address_1").trim()
                                + " and MDM Address_1:- " + address1_mdm + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/
//                 Address_2 - 7K
                    String address2_7K = sevenKRecord.get("ADDR_LINE_2_TXT") == null ? "" : ((String) sevenKRecord.get("ADDR_LINE_2_TXT")).trim();
                    if (dqRecord.get("Address_2").trim().equals(address2_7K)) {
//                    System.out.println("7K-> Address_2 is as expected Address_2:-" + dqRecord.get("Address_2").trim());
                    } else {
                        System.err.println("7K-> Address_2 is not as expected Extract Address_2:- " + dqRecord.get("Address_2").trim()
                                + " and 7K Address_2:- " + ((String) sevenKRecord.get("ADDR_LINE_2_TXT")).trim() + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }

               /*     //                 Address_2 - MDM
                    String address2_mdm = mdmRecord.get("ADDR_LINE_2_TXT") == null ? "" : ((String) mdmRecord.get("ADDR_LINE_2_TXT")).trim();
                    if (dqRecord.get("Address_2").trim().equals(address2_mdm)) {
//                    System.out.println("7K-> Address_2 is as expected Address_2:-" + dqRecord.get("Address_2").trim());
                    } else {
                        System.err.println("MDM-> Address_2 is not as expected Extract Address_2:- " + dqRecord.get("Address_2").trim()
                                + " and MDM Address_2:- " + address2_mdm + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/
//                 City - 7K
                    String city_7K = sevenKRecord.get("CITY_NM") == null ? "" : ((String) sevenKRecord.get("CITY_NM")).trim();
                    if (dqRecord.get("City").trim().equals(city_7K)) {
//                    System.out.println("7K-> City is as expected City:-" + dqRecord.get("City").trim());
                    } else {
                        System.err.println("7K-> City is not as expected Extract City:- " + dqRecord.get("City").trim()
                                + " and 7K City:- " + city_7K + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }

             /*       //                 City - MDM
                    String city_mdm = mdmRecord.get("CITY_NM") == null ? "" : ((String) mdmRecord.get("CITY_NM")).trim();
                    if (dqRecord.get("City").trim().equals(city_mdm)) {
//                    System.out.println("7K-> City is as expected City:-" + dqRecord.get("City").trim());
                    } else {
                        System.err.println("MDM-> City is not as expected Extract City:- " + dqRecord.get("City").trim()
                                + " and MDM City:- " + city_mdm + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/

                    //                  Province - 7K
                    String province_7K = sevenKRecord.get("PROV_CD") == null ? "" : ((String) sevenKRecord.get("PROV_CD")).trim();
                    if (dqRecord.get("Province").trim().equals(province_7K)) {
//                    System.out.println("7K-> Province is as expected Province:-" + dqRecord.get("Province").trim());
                    } else {
                        System.err.println("7K-> Province is not as expected Extract Province:- " + dqRecord.get("Province").trim()
                                + " and 7K Province:- " + ((String) sevenKRecord.get("PROV_CD")).trim() + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }

            /*        //                  Province - MDM
                    String province_mdm = mdmRecord.get("PROV_CD") == null ? "" : ((String) mdmRecord.get("PROV_CD")).trim();
                    if (dqRecord.get("Province").trim().equals(province_mdm)) {
//                    System.out.println("7K-> Province is as expected Province:-" + dqRecord.get("Province").trim());
                    } else {
                        System.err.println("MDM-> Province is not as expected Extract Province:- " + dqRecord.get("Province").trim()
                                + " and MDM Province:- " + province_mdm + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/
//                  Postal_Code - 7K
                    String postalCode_7K = sevenKRecord.get("POSTAL_CD") == null ? "" : ((String) sevenKRecord.get("POSTAL_CD")).trim();
                    if (dqRecord.get("Postal_Code").trim().equals(postalCode_7K)) {
//                    System.out.println("7K-> Postal_Code is as expected Postal_Code:-" + dqRecord.get("Postal_Code").trim());
                    } else {
                        System.err.println("7K-> Postal_Code is not as expected Extract Postal_Code:- " + dqRecord.get("Postal_Code").trim()
                                + " and 7K Postal_Code:- " + ((String) sevenKRecord.get("POSTAL_CD")).trim() + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }

            /*        //                  Postal_Code - MDM
                    String postalCode_mdm = mdmRecord.get("POSTAL_CD") == null ? "" : ((String) mdmRecord.get("POSTAL_CD")).trim();
                    if (dqRecord.get("Postal_Code").trim().equals(postalCode_mdm)) {
//                    System.out.println("7K-> Postal_Code is as expected Postal_Code:-" + dqRecord.get("Postal_Code").trim());
                    } else {
                        System.err.println("MDM-> Postal_Code is not as expected Extract Postal_Code:- " + dqRecord.get("Postal_Code").trim()
                                + " and MDM:- " + postalCode_mdm + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/
//                  Country - 7K
                    List<String> countries = countriesList.stream().map(x -> ((String) x.get("DESC")).trim()).collect(Collectors.toList());
                    if (dqRecord.get("Country").trim().equals(((String) sevenKRecord.get("CNTRY_CD")).trim())) {
//                    System.out.println("7K-> Country is as expected Country:-" + dqRecord.get("Country").trim());
                    } else {
                        System.err.println("7K-> Country is not as expected Extract Country:- " + dqRecord.get("Country").trim()
                                + " and 7K Country:- " + ((String) sevenKRecord.get("CNTRY_CD")).trim() + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
                    if (countries.contains(dqRecord.get("Country").trim().toUpperCase())) {
                        //                    System.out.println("7K-> Country is exists in 7K Country table, Country:-" + dqRecord.get("Country").trim());
                    } else {
                        System.err.println("7K-> Country is not exists in 7K Country table Extract Country:- " + dqRecord.get("Country").trim()
                                + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
           /*         //                  Country - MDM
                    if (dqRecord.get("Country").trim().equals(((String) mdmRecord.get("CNTRY_CD")).trim())) {
//                    System.out.println("7K-> Country is as expected Country:-" + dqRecord.get("Country").trim());
                    } else {
                        System.err.println("MDM-> Country is not as expected Extract Country:- " + dqRecord.get("Country").trim()
                                + " and MDM Country:- " + ((String) mdmRecord.get("CNTRY_CD")).trim() + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/
//                  SIA_Individual_Flag - 7K
                    if (dqRecord.get("SIA_Individual_Flag").equals("")) {
//                    System.out.println("7K-> SIA_Individual_Flag is as expected SIA_Individual_Flag:-" + dqRecord.get("SIA_Individual_Flag").trim());
                    } else {
                        System.err.println("7K-> SIA_Individual_Flag is not as expected Extract SIA_Individual_Flag:- " + dqRecord.get("SIA_Individual_Flag").trim()
                                + " and Expected SIA_Individual_Flag:- ''" + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }


//                  Interest_In_Deposit_Flag - 7K
                    if (dqRecord.get("Interest_In_Deposit_Flag").equals("P")) {
//                    System.out.println("7K-> Interest_In_Deposit_Flag is as expected Interest_In_Deposit_Flag:-" + dqRecord.get("Interest_In_Deposit_Flag").trim());
                    } else {
                        System.err.println("7K-> Interest_In_Deposit_Flag is not as expected Extract Interest_In_Deposit_Flag:- " + dqRecord.get("Interest_In_Deposit_Flag").trim()
                                + " and Expected Interest_In_Deposit_Flag:- 'P'" + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
//                  Interest_In_Deposit - 7K
                    String interestInDeposit_7K = sevenKRecord.get("OWNERSHIP_PCT") == null ? "" : (sevenKRecord.get("OWNERSHIP_PCT").toString()).trim();
                    if (dqRecord.get("Interest_In_Deposit").trim().equals(interestInDeposit_7K)) {
//                    System.out.println("7K-> Interest_In_Deposit is as expected Interest_In_Deposit:-" + dqRecord.get("Interest_In_Deposit").trim());
                    } else {
                        System.err.println("7K-> Interest_In_Deposit is not as expected Extract Interest_In_Deposit:- " + dqRecord.get("Interest_In_Deposit").trim()
                                + " and 7K Interest_In_Deposit:- " + interestInDeposit_7K + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }

       /*             //                  Interest_In_Deposit - MDM
                    String interestInDeposit_mdm = mdmRecord.get("OWNERSHIP_PCT") == null ? "" : (mdmRecord.get("OWNERSHIP_PCT").toString()).trim();
                    if (dqRecord.get("Interest_In_Deposit").trim().equals(interestInDeposit_mdm)) {
//                    System.out.println("7K-> Interest_In_Deposit is as expected Interest_In_Deposit:-" + dqRecord.get("Interest_In_Deposit").trim());
                    } else {
                        System.err.println("MDM-> Interest_In_Deposit is not as expected Extract Country:- " + dqRecord.get("Interest_In_Deposit").trim()
                                + " and MDM Interest_In_Deposit:- " + interestInDeposit_mdm + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/
                } else {
                    System.err.println("7K-> Record is not found in the 7K DB for the values " +
                            dqRecord.entrySet().stream().map(x -> x.getKey() + "=" + x.getValue()).collect(Collectors.joining(",")));


                }

            }
        }
    }

    public void validate0152UFRecordsAgainst7KAndMDMDB(String table, String accountType) throws IOException {
        ReadTextFile readTextFile = new ReadTextFile();
        List<Map<String, String>> mdmCredentials = getMdmDbCredentials();
        List<Map<String, String>> sevenKCredentials = get7KDbCredentials();
        DataBaseData mdmDbObect = new DataBaseData(mdmCredentials);
        DataBaseData sevenKDbObect = new DataBaseData(sevenKCredentials);
        List<Map<String, String>> fileRecords = null;
        List<String> fileNames = new DQRules().getDQ0152FileNames(table)
                .stream().filter(x -> x.substring(26, 29).equals(accountType.split("-")[1])).collect(Collectors.toList());
        List<Map<String, Object>> countriesList = sevenKDbObect.getResultsListsFromDB("select \"DESC\" from CCBS.T7K_COUNTRY_CODE");
        for (String fileName : fileNames) {
            test.log("INFO", "-----File Name--> " + fileName + " --------");
            System.out.println("-----File Name--> " + fileName + " --------");
            readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "/resources/0152FilesDataComparision/" + fileName);
            List<Map<String, String>> dqRecords = ReadTextFile.getDqSamples();
            Set<String> accountNumbers = dqRecords.stream().map(x -> x.get("Account_Number")).collect(Collectors.toSet());
            List<Map<String, Object>> allAccountsRecords = sevenKDbObect.getResultsFromDB("select * from CCBS.T7W_CUST_ACCT_BENE_ASSOC_UF where ACCT_ID in(" +
                    accountNumbers.stream().map(x -> "'" + x + "'").collect(Collectors.joining(",")) + ")");
            //        List<Map<String,String>> dbRecords = sevenKDbObect.getResultsFromDB(query_7k)
            for (Map<String, String> dqRecord : dqRecords) {
//                Validating below field's
//                 Account_Unique_ID
//                 Account_Number
//                 Name
//                 First_Name
//                 Middle_Name
//                 Last_Name
//                 Address_1
//                 Address_2
//                 City
//                  Province
//                  Postal_Code
//                  Country
//                  SIA_Individual_Flag
//                  Interest_In_Deposit_Flag
//                  Interest_In_Deposit
                String schema_7K = ConfigurationManager.getBundle().getPropertyValue("7k.dbSchema");
                String schema_MDM = ConfigurationManager.getBundle().getPropertyValue("7k.dbSchema");
                String accountNumber = dqRecord.get("Account_Number");
                String name = dqRecord.get("Name").trim();
                String address1 = dqRecord.get("Address_1").trim();
                String address2 = dqRecord.get("Address_2").trim();
                String interestInDeposit = dqRecord.get("Interest_In_Deposit");
                String city = dqRecord.get("City").trim();
                String province = dqRecord.get("Province").trim();
                String postalCode = dqRecord.get("Postal_Code").trim();


                List<Map<String, Object>> dbRecords = allAccountsRecords.stream().filter(x -> Objects.equals(x.get("ACCT_ID") == null ? "" : x.get("ACCT_ID").toString().trim(), accountNumber)
                        && Objects.equals(x.get("FULL_NAME") == null ? "" : x.get("FULL_NAME").toString().trim(), name)
                        && Objects.equals(x.get("ADDR_LINE_1_TXT") == null ? "" : x.get("ADDR_LINE_1_TXT").toString().trim(), address1)
                        && Objects.equals(x.get("ADDR_LINE_2_TXT") == null ? "" : x.get("ADDR_LINE_2_TXT").toString().trim(), address2)
                        && Objects.equals(x.get("OWNERSHIP_PCT") == null ? "" : x.get("OWNERSHIP_PCT").toString().trim(), interestInDeposit)
                        && Objects.equals(x.get("CITY_NM") == null ? "" : x.get("CITY_NM").toString().trim(), city)
                        && Objects.equals(x.get("PROV_CD") == null ? "" : x.get("PROV_CD").toString().trim(), province)
                        && Objects.equals(x.get("POSTAL_CD") == null ? "" : x.get("POSTAL_CD").toString().trim(), postalCode)).collect(Collectors.toList());

//                String query_7k = "select * from " + schema_7K + ".T7W_CUST_ACCT_BENE_ASSOC where ACCT_ID='" + accountNumber + "' and FULL_NAME='" + name + "'";
                String query_7k = "select * from " + schema_7K + ".T7W_CUST_ACCT_BENE_ASSOC where ACCT_ID='" + accountNumber + "' and (ADDR_LINE_1_TXT" + address1 + " and FULL_NAME" + name + " and ADDR_LINE_2_TXT" + address2 +
                        "  and OWNERSHIP_PCT" + interestInDeposit + "  and CITY_NM" + city + ")";
                String query_mdm = "select * from " + schema_MDM + ".T7W_CUST_ACCT_BENE_ASSOC where ACCT_ID='" + accountNumber + "' and FULL_NAME='" + name + "'";

//                Map<String, Object> sevenKRecord = sevenKDbObect.getResultsFromDB(query_7k).get(0);
                Map<String, Object> sevenKRecord = dbRecords.size() > 0 ? dbRecords.get(0) : null;
//                Map<String, Object> mdmRecord = mdmDbObect.getResultsFromDB(query_mdm);
                if (sevenKRecord != null) {
                    //                 Account_Unique_ID - 7K
                    if (dqRecord.get("Account_Unique_ID").trim().equals(((String) sevenKRecord.get("ACCT_SUBSYS_ID")).trim() + ((String) sevenKRecord.get("ACCT_ID")).trim())) {
//                    System.out.println("7K-> Account_Unique_ID is as expected Account_Unique_ID:-" + dqRecord.get("Account_Unique_ID").trim());
                    } else {
                        System.err.println("7K-> Account_Unique_ID is not as expected Extract Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID").trim()
                                + " and 7K Account_Unique_ID:- " + ((String) sevenKRecord.get("ACCT_SUBSYS_ID")).trim() + ((String) sevenKRecord.get("ACCT_ID")).trim());
                    }
/*
//                 Account_Unique_ID - MDM
                    if (dqRecord.get("Account_Unique_ID").trim().equals(((String) mdmRecord.get("PARTY_SUBSYS_MNEMONIC_CD")).trim() + ((String) mdmRecord.get("ACCT_ID")).trim())) {
//                    System.out.println("7K-> Account_Unique_ID is as expected Account_Unique_ID:-" + dqRecord.get("Account_Unique_ID").trim());
                    } else {
                        System.err.println("MDM-> Account_Unique_ID is not as expected Extract Account_Unique_ID:- " + dqRecord.get("Account_Unique_ID").trim()
                                + " and MDM Account_Unique_ID:- " + ((String) mdmRecord.get("PARTY_SUBSYS_MNEMONIC_CD")).trim() + ((String) mdmRecord.get("ACCT_ID")).trim());
                    }
*/

                    //                 Account_Number - 7K
                    if (dqRecord.get("Account_Number").trim().equals(((String) sevenKRecord.get("PLAN_ACCOUNT_NO")).trim())) {
//                    System.out.println("7K-> Account_Number is as expected Account_Number:-" + dqRecord.get("Account_Number").trim());
                    } else {
                        System.err.println("7K-> Account_Number is not as expected Extract Account_Number:- " + dqRecord.get("Account_Number").trim()
                                + " and 7K Account_Number:- " + ((String) sevenKRecord.get("PLAN_ACCOUNT_NO")).trim());
                    }

   /*                 //                 Account_Number - MDM
                    if (dqRecord.get("Account_Number").trim().equals(((String) mdmRecord.get("ACCT_ID")).trim())) {
//                    System.out.println("7K-> Account_Number is as expected Account_Number:-" + dqRecord.get("Account_Number").trim());
                    } else {
                        System.err.println("MDM-> Account_Number is not as expected Extract Account_Number:- " + dqRecord.get("Account_Number").trim()
                                + " and MDM Account_Number:- " + ((String) mdmRecord.get("ACCT_ID")).trim());
                    }
*/
                    //                 Name - 7K
                    String Name_7K = sevenKRecord.get("LAST_NAME") == null ? "" : ((String) sevenKRecord.get("LAST_NAME")).trim() +
                            sevenKRecord.get("FIRST_NAME") == null ? "" : ((String) sevenKRecord.get("FIRST_NAME")).trim();
                    if (dqRecord.get("Name").trim().equals(Name_7K)) {
//                    System.out.println("7K-> Name is as expected Name:-" + dqRecord.get("Name").trim());
                    } else {
                        System.err.println("7K-> Name is not as expected Extract Name:- " + dqRecord.get("Name").trim()
                                + " and 7K Name:- " + Name_7K + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }

               /*     //                 Name - MDM
                    String name_MDM = mdmRecord.get("FULL_NAME") == null ? "" : ((String) mdmRecord.get("FULL_NAME")).trim();
                    if (dqRecord.get("Name").trim().equals(name_MDM)) {
//                    System.out.println("7K-> Name is as expected Name:-" + dqRecord.get("Name").trim());
                    } else {
                        System.err.println("MDM-> Name is not as expected Extract Name:- " + dqRecord.get("Name").trim()
                                + " and MDM Name:- " + name_MDM + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/
                    //                 First_Name - 7K
                    String firstName_7K = sevenKRecord.get("FIRST_NAME") == null ? "" : ((String) sevenKRecord.get("FIRST_NAME")).trim();
                    if (dqRecord.get("First_Name").trim().equals(firstName_7K)) {
//                    System.out.println("7K-> First_Name is as expected FIRST_NAME:-" + dqRecord.get("First_Name").trim());
                    } else {
                        System.err.println("7K-> First_Name is not as expected Extract First_Name:- " + dqRecord.get("First_Name").trim()
                                + " and 7K First_Name:- " + firstName_7K + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }

              /*      //                 First_Name - MDM
                    String firstName_mdm = mdmRecord.get("FIRST_NAME") == null ? "" : ((String) mdmRecord.get("FIRST_NAME")).trim();
                    if (dqRecord.get("First_Name").trim().equals(firstName_mdm)) {
//                    System.out.println("7K-> First_Name is as expected FIRST_NAME:-" + dqRecord.get("First_Name").trim());
                    } else {
                        System.err.println("MDM-> First_Name is not as expected Extract First_Name:- " + dqRecord.get("First_Name").trim()
                                + " and MDM First_Name:- " + firstName_mdm + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/
//                 Middle_Name - 7K
                    String middleName_7K = sevenKRecord.get("MIDDLE_NAME") == null ? "" : ((String) sevenKRecord.get("MIDDLE_NAME")).trim();
                    if (dqRecord.get("Middle_Name").trim().equals(middleName_7K)) {
//                    System.out.println("7K-> Middle_Name is as expected Name:-" + dqRecord.get("Middle_Name").trim());
                    } else {
                        System.err.println("7K-> Middle_Name is not as expected Extract Middle_Name:- " + dqRecord.get("Middle_Name").trim()
                                + " and 7K Middle_Name:- " + ((String) sevenKRecord.get("MIDDLE_NAME")).trim() + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }

               /*     //                 Middle_Name - MDM
                    String middleName_mdm = mdmRecord.get("MIDDLE_NAME") == null ? "" : ((String) mdmRecord.get("MIDDLE_NAME")).trim();
                    if (dqRecord.get("Middle_Name").trim().equals(middleName_mdm)) {
//                    System.out.println("7K-> Middle_Name is as expected Name:-" + dqRecord.get("Middle_Name").trim());
                    } else {
                        System.err.println("MDM-> Middle_Name is not as expected Extract Middle_Name:- " + dqRecord.get("Middle_Name").trim()
                                + " and MDM Middle_Name:- " + middleName_mdm + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/
//                 Last_Name - 7K
                    String lastName_7K = sevenKRecord.get("LAST_NAME") == null ? "" : ((String) sevenKRecord.get("LAST_NAME")).trim();
                    if (dqRecord.get("Last_Name").trim().equals(lastName_7K)) {
//                    System.out.println("7K-> Last_Name is as expected LAST_NAME:-" + dqRecord.get("Last_Name").trim());
                    } else {
                        System.err.println("7K-> Last_Name is not as expected Extract Last_Name:- " + dqRecord.get("Last_Name").trim()
                                + " and 7K Last_Name:- " + lastName_7K + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }

              /*      //                 Last_Name - MDM
                    String lastName_mdm = mdmRecord.get("LAST_NAME") == null ? "" : ((String) mdmRecord.get("LAST_NAME")).trim();
                    if (dqRecord.get("Last_Name").trim().equals(lastName_mdm)) {
//                    System.out.println("7K-> Last_Name is as expected LAST_NAME:-" + dqRecord.get("Last_Name").trim());
                    } else {
                        System.err.println("MDM-> Last_Name is not as expected Extract Last_Name:- " + dqRecord.get("Last_Name").trim()
                                + " and MDM Last_Name:- " + lastName_mdm + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/
//                 Address_1 - 7K
                    String address1_7K = sevenKRecord.get("ADDR_LINE_1_TXT") == null ? "" : ((String) sevenKRecord.get("ADDR_LINE_1_TXT")).trim();
                    if (dqRecord.get("Address_1").trim().equals(address1_7K)) {
//                    System.out.println("7K-> Address_1 is as expected Address_1:-" + dqRecord.get("Address_1").trim());
                    } else {
                        System.err.println("7K-> Address_1 is not as expected Extract Address_1:- " + dqRecord.get("Address_1").trim()
                                + " and 7K Address_1:- " + address1_7K + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }

              /*      //                 Address_1 - MDM
                    String address1_mdm = mdmRecord.get("ADDR_LINE_1_TXT") == null ? "" : ((String) mdmRecord.get("ADDR_LINE_1_TXT")).trim();
                    if (dqRecord.get("Address_1").trim().equals(address1_mdm)) {
//                    System.out.println("7K-> Address_1 is as expected Address_1:-" + dqRecord.get("Address_1").trim());
                    } else {
                        System.err.println("MDM-> Address_1 is not as expected Extract Address_1:- " + dqRecord.get("Address_1").trim()
                                + " and MDM Address_1:- " + address1_mdm + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/
//                 Address_2 - 7K
                    String address2_7K = sevenKRecord.get("ADDR_LINE_2_TXT") == null ? "" : ((String) sevenKRecord.get("ADDR_LINE_2_TXT")).trim();
                    if (dqRecord.get("Address_2").trim().equals(address2_7K)) {
//                    System.out.println("7K-> Address_2 is as expected Address_2:-" + dqRecord.get("Address_2").trim());
                    } else {
                        System.err.println("7K-> Address_2 is not as expected Extract Address_2:- " + dqRecord.get("Address_2").trim()
                                + " and 7K Address_2:- " + ((String) sevenKRecord.get("ADDR_LINE_2_TXT")).trim() + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }

               /*     //                 Address_2 - MDM
                    String address2_mdm = mdmRecord.get("ADDR_LINE_2_TXT") == null ? "" : ((String) mdmRecord.get("ADDR_LINE_2_TXT")).trim();
                    if (dqRecord.get("Address_2").trim().equals(address2_mdm)) {
//                    System.out.println("7K-> Address_2 is as expected Address_2:-" + dqRecord.get("Address_2").trim());
                    } else {
                        System.err.println("MDM-> Address_2 is not as expected Extract Address_2:- " + dqRecord.get("Address_2").trim()
                                + " and MDM Address_2:- " + address2_mdm + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/
//                 City - 7K
                    String city_7K = sevenKRecord.get("CITY_NM") == null ? "" : ((String) sevenKRecord.get("CITY_NM")).trim();
                    if (dqRecord.get("City").trim().equals(city_7K)) {
//                    System.out.println("7K-> City is as expected City:-" + dqRecord.get("City").trim());
                    } else {
                        System.err.println("7K-> City is not as expected Extract City:- " + dqRecord.get("City").trim()
                                + " and 7K City:- " + city_7K + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }

             /*       //                 City - MDM
                    String city_mdm = mdmRecord.get("CITY_NM") == null ? "" : ((String) mdmRecord.get("CITY_NM")).trim();
                    if (dqRecord.get("City").trim().equals(city_mdm)) {
//                    System.out.println("7K-> City is as expected City:-" + dqRecord.get("City").trim());
                    } else {
                        System.err.println("MDM-> City is not as expected Extract City:- " + dqRecord.get("City").trim()
                                + " and MDM City:- " + city_mdm + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/

                    //                  Province - 7K
                    String province_7K = sevenKRecord.get("PROV_CD") == null ? "" : ((String) sevenKRecord.get("PROV_CD")).trim();
                    if (dqRecord.get("Province").trim().equals(province_7K)) {
//                    System.out.println("7K-> Province is as expected Province:-" + dqRecord.get("Province").trim());
                    } else {
                        System.err.println("7K-> Province is not as expected Extract Province:- " + dqRecord.get("Province").trim()
                                + " and 7K Province:- " + ((String) sevenKRecord.get("PROV_CD")).trim() + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }

            /*        //                  Province - MDM
                    String province_mdm = mdmRecord.get("PROV_CD") == null ? "" : ((String) mdmRecord.get("PROV_CD")).trim();
                    if (dqRecord.get("Province").trim().equals(province_mdm)) {
//                    System.out.println("7K-> Province is as expected Province:-" + dqRecord.get("Province").trim());
                    } else {
                        System.err.println("MDM-> Province is not as expected Extract Province:- " + dqRecord.get("Province").trim()
                                + " and MDM Province:- " + province_mdm + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/
//                  Postal_Code - 7K
                    String postalCode_7K = sevenKRecord.get("POSTAL_CD") == null ? "" : ((String) sevenKRecord.get("POSTAL_CD")).trim();
                    if (dqRecord.get("Postal_Code").trim().equals(postalCode_7K)) {
//                    System.out.println("7K-> Postal_Code is as expected Postal_Code:-" + dqRecord.get("Postal_Code").trim());
                    } else {
                        System.err.println("7K-> Postal_Code is not as expected Extract Postal_Code:- " + dqRecord.get("Postal_Code").trim()
                                + " and 7K Postal_Code:- " + ((String) sevenKRecord.get("POSTAL_CD")).trim() + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }

            /*        //                  Postal_Code - MDM
                    String postalCode_mdm = mdmRecord.get("POSTAL_CD") == null ? "" : ((String) mdmRecord.get("POSTAL_CD")).trim();
                    if (dqRecord.get("Postal_Code").trim().equals(postalCode_mdm)) {
//                    System.out.println("7K-> Postal_Code is as expected Postal_Code:-" + dqRecord.get("Postal_Code").trim());
                    } else {
                        System.err.println("MDM-> Postal_Code is not as expected Extract Postal_Code:- " + dqRecord.get("Postal_Code").trim()
                                + " and MDM:- " + postalCode_mdm + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/
//                  Country - 7K
                    List<String> countries = countriesList.stream().map(x -> ((String) x.get("DESC")).trim()).collect(Collectors.toList());
                    if (dqRecord.get("Country").trim().equals(((String) sevenKRecord.get("CNTRY_CD")).trim())) {
//                    System.out.println("7K-> Country is as expected Country:-" + dqRecord.get("Country").trim());
                    } else {
                        System.err.println("7K-> Country is not as expected Extract Country:- " + dqRecord.get("Country").trim()
                                + " and 7K Country:- " + ((String) sevenKRecord.get("CNTRY_CD")).trim() + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
                    if (countries.contains(dqRecord.get("Country").trim().toUpperCase())) {
                        //                    System.out.println("7K-> Country is exists in 7K Country table, Country:-" + dqRecord.get("Country").trim());
                    } else {
                        System.err.println("7K-> Country is not exists in 7K Country table Extract Country:- " + dqRecord.get("Country").trim()
                                + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
           /*         //                  Country - MDM
                    if (dqRecord.get("Country").trim().equals(((String) mdmRecord.get("CNTRY_CD")).trim())) {
//                    System.out.println("7K-> Country is as expected Country:-" + dqRecord.get("Country").trim());
                    } else {
                        System.err.println("MDM-> Country is not as expected Extract Country:- " + dqRecord.get("Country").trim()
                                + " and MDM Country:- " + ((String) mdmRecord.get("CNTRY_CD")).trim() + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/
//                  SIA_Individual_Flag - 7K
                    if (dqRecord.get("SIA_Individual_Flag").equals("")) {
//                    System.out.println("7K-> SIA_Individual_Flag is as expected SIA_Individual_Flag:-" + dqRecord.get("SIA_Individual_Flag").trim());
                    } else {
                        System.err.println("7K-> SIA_Individual_Flag is not as expected Extract SIA_Individual_Flag:- " + dqRecord.get("SIA_Individual_Flag").trim()
                                + " and Expected SIA_Individual_Flag:- ''" + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }


//                  Interest_In_Deposit_Flag - 7K
                    if (dqRecord.get("Interest_In_Deposit_Flag").equals("P")) {
//                    System.out.println("7K-> Interest_In_Deposit_Flag is as expected Interest_In_Deposit_Flag:-" + dqRecord.get("Interest_In_Deposit_Flag").trim());
                    } else {
                        System.err.println("7K-> Interest_In_Deposit_Flag is not as expected Extract Interest_In_Deposit_Flag:- " + dqRecord.get("Interest_In_Deposit_Flag").trim()
                                + " and Expected Interest_In_Deposit_Flag:- 'P'" + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
//                  Interest_In_Deposit - 7K
                    String interestInDeposit_7K = sevenKRecord.get("OWNERSHIP_PCT") == null ? "" : (sevenKRecord.get("OWNERSHIP_PCT").toString()).trim();
                    if (dqRecord.get("Interest_In_Deposit").trim().equals(interestInDeposit_7K)) {
//                    System.out.println("7K-> Interest_In_Deposit is as expected Interest_In_Deposit:-" + dqRecord.get("Interest_In_Deposit").trim());
                    } else {
                        System.err.println("7K-> Interest_In_Deposit is not as expected Extract Interest_In_Deposit:- " + dqRecord.get("Interest_In_Deposit").trim()
                                + " and 7K Interest_In_Deposit:- " + interestInDeposit_7K + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }

       /*             //                  Interest_In_Deposit - MDM
                    String interestInDeposit_mdm = mdmRecord.get("OWNERSHIP_PCT") == null ? "" : (mdmRecord.get("OWNERSHIP_PCT").toString()).trim();
                    if (dqRecord.get("Interest_In_Deposit").trim().equals(interestInDeposit_mdm)) {
//                    System.out.println("7K-> Interest_In_Deposit is as expected Interest_In_Deposit:-" + dqRecord.get("Interest_In_Deposit").trim());
                    } else {
                        System.err.println("MDM-> Interest_In_Deposit is not as expected Extract Country:- " + dqRecord.get("Interest_In_Deposit").trim()
                                + " and MDM Interest_In_Deposit:- " + interestInDeposit_mdm + " for Account_Number:- " + dqRecord.get("Account_Number"));
                    }
*/
                } else {
                    System.err.println("7K-> Record is not found in the 7K DB for the values " +
                            dqRecord.entrySet().stream().map(x -> x.getKey() + "=" + x.getValue()).collect(Collectors.joining(",")));


                }

            }
        }
    }


    @Given("^validate \"([^\"]*)\" and \"([^\"]*)\" accounts against SevenK and MDM Database$")
    public void validateAndAccountsAgainstSevenKAndMDMDatabase(String table, String accountType) throws Throwable {
        if (accountType.contains("BB")) {
            validate0152BBRecordsAgainst7KAndMDMDB(table, accountType);
        } else if (accountType.contains("UF")) {
            validate0152UFRecordsAgainst7KAndMDMDB(table, accountType);
        }
    }
}


