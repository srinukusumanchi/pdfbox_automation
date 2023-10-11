package com.quantum.utility;

import com.quantum.baseclass.BasePage;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.quantum.constants.CommonConstants.SCREENSHOT;

public class DQRules {

    /*private static ExtentReportHelper extentReportHelper;

    public static ExtentReportHelper getExtentReportHelper() {
        return extentReportHelper;
    }

    public static void setExtentReportHelper(ExtentReportHelper extentReportHelper) {
        DQRules.extentReportHelper = extentReportHelper;
    }
*/
//    public DQRules() {
//        DQRules.setExtentReportHelper(new ExtentReportHelper());
//    }

    // It accepts only 0-9 multiple characters prefix can have - or + it accepts even prefix not there also
    public Boolean onlyNumericCharactersRule1(String value) {
        if (value.isEmpty()) {
            return true;
        }
        Pattern pattern = Pattern.compile("(-|\\+?)\\d+");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    // No leading trailing spaces does not accepts empty string and size should be atleast 1 character and it can be any character
    public Boolean acceptsAllChactersRule2(String value) {
        Pattern pattern = Pattern.compile(".*");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }


    public Boolean acceptsAllChactersWithSpecificLengthRule3(String value, int length) {
        if (value.isEmpty()) {
            return true;
        }
        Pattern pattern = Pattern.compile("[^[^\\n ]*$]{" + length + "}");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public Boolean birthDateValidationRule4(String value) {
        if (value.isEmpty()) {
            return true;
        }
        Pattern pattern = Pattern.compile("^\\d{4}\\d{2}\\d{2}$");
        Matcher matcher = pattern.matcher(value);
        if (matcher.matches()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            dateFormat.setLenient(false);
            try {
                dateFormat.parse(value);
            } catch (ParseException pe) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public Boolean addressChangeValidationRule5(String value) {
        if (value.isEmpty()) {
            return true;
        }
        Pattern pattern = Pattern.compile("^\\d{4}\\d{2}\\d{2}:\\d{2}\\d{2}\\d{2}$");
        Matcher matcher = pattern.matcher(value);
        if (matcher.matches()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd:HHmmss");
            dateFormat.setLenient(false);
            try {
                dateFormat.parse(value);
            } catch (ParseException pe) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

    public Boolean amountValidationRule6(String value, String integer, String decimal) {
        if (value.isEmpty()) {
            return true;
        }
        Pattern pattern = Pattern.compile("^(-?)\\d{1," + integer + "}(\\.\\d{1," + decimal + "})?$");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public Boolean noDuplicatesPrimaryKeyRule9(List<Map<String, String>> dqRecords, String key, String value) {
        boolean flag = false;
        int counter = 0;
        for (Map<String, String> dqRecord : dqRecords) {
            if (dqRecord.get(key).equals(value)) {
                counter = counter + 1;
            }
        }
        if (counter == 1) {
            flag = true;
        }
        return flag;
    }

    public List<String> getDQFileNames(String tableId) {
        List<String> fileNames = null;
        File filesFolder = new File(System.getProperty("user.dir") + "\\resources\\dqFiles");
        File[] listOfFiles = filesFolder.listFiles();
        fileNames = new LinkedList<>();
        for (File file : listOfFiles) {
            if (file.getName().substring(18, 22).equals(tableId)) {
                fileNames.add(file.getName());
            }
        }

        return fileNames;
    }

    public List<String> getDQ0152FileNames(String tableId) {
        List<String> fileNames = null;
        File filesFolder = new File(System.getProperty("user.dir") + "\\resources\\0152FilesDataComparision");
        File[] listOfFiles = filesFolder.listFiles();
        fileNames = new LinkedList<>();
        for (File file : listOfFiles) {
            if (file.getName().substring(18, 22).equals(tableId)) {
                fileNames.add(file.getName());
            }
        }

        return fileNames;
    }


    public List<String> getMasking0100BeforeNameLogicFiles(String tableId) {
        List<String> fileNames = null;
        File filesFolder = new File(System.getProperty("user.dir") + "\\resources\\masking0100BeforeNameLogicFiles");
        File[] listOfFiles = filesFolder.listFiles();
        fileNames = new LinkedList<>();
        for (File file : listOfFiles) {
            if (file.getName().substring(18, 22).equals(tableId)) {
                fileNames.add(file.getName());

            }
        }

        return fileNames;
    }

    public List<String> getMaskingFileNames(String tableId) {
        List<String> fileNames = null;
        File filesFolder = new File(System.getProperty("user.dir") + "\\resources\\maskingFiles");
        File[] listOfFiles = filesFolder.listFiles();
        fileNames = new LinkedList<>();
        for (File file : listOfFiles) {
            if (file.getName().substring(18, 22).equals(tableId)) {
                fileNames.add(file.getName());

            }
        }

        return fileNames;
    }

    public List<String> getListOfString(String mi, String tableId, String targetFileColumnName, String subSystemId, String maskingOrDq) throws IOException {
        ReadTextFile readTextFile = new ReadTextFile();
        List<String> fileNames = null;
        List<String> targetColumnValues = null;
        List<List<String>> multipleFileTargetColumnValue = new LinkedList<>();
        if (maskingOrDq.equalsIgnoreCase("DQ")) {
            fileNames = getDQFileNames(tableId);
        } else if (maskingOrDq.equalsIgnoreCase("MASKING")) {
            fileNames = getMaskingFileNames(tableId);
        }

        fileNames = fileNames.stream().filter(x -> x.substring(0, 4).equals(mi)).collect(Collectors.toList());
        if (fileNames.size() == 0) {
            System.err.println("--- FAIL ---> ****File Not Found---Unable to find File:- MI:- " + mi + "and table Id:- " + tableId);
        }

        if ((tableId.equals("0100") || tableId.equals("0110") || tableId.equals("0120") || tableId.equals("0121")
                || tableId.equals("0201") || tableId.equals("0202") || tableId.equals("0211")
                || tableId.equals("0212") || tableId.equals("0221")) && fileNames.size() == 1) {
            List<Map<String, String>> fileRecords = null;
            for (String file : fileNames) {
                if (maskingOrDq.equalsIgnoreCase("DQ")) {
                    fileRecords = readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "\\resources\\dqFiles\\" + file);
                } else if (maskingOrDq.equalsIgnoreCase("MASKING")) {
                    fileRecords = readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "\\resources\\maskingFiles\\" + file);
                }
                targetColumnValues = fileRecords.stream().map(x -> x.get(targetFileColumnName)).collect(Collectors.toList());

            }
        } else if ((tableId.equals("0130") || tableId.equals("0140") || tableId.equals("0152") || tableId.equals("0153")
                || tableId.equals("0160") || tableId.equals("0231") || tableId.equals("0232")
                || tableId.equals("0233") || tableId.equals("0234") || tableId.equals("0235") || tableId.equals("0236")
                || tableId.equals("0237") || tableId.equals("0238") || tableId.equals("0239") || tableId.equals("0240")
                || tableId.equals("0241") || tableId.equals("0242") || tableId.equals("0400") || tableId.equals("0401")
                || tableId.equals("0500") || tableId.equals("0501") || tableId.equals("0600") || tableId.equals("0800")
                || tableId.equals("0900") || tableId.equals("0999")) && fileNames.size() >= 1) {
            List<Map<String, String>> fileRecords = null;
            List<String> files = null;
            if (!subSystemId.equals("")) {
                files = fileNames.stream().filter(x -> x.substring(26, 29).equals(subSystemId)).collect(Collectors.toList());
            } else {
                files = fileNames;
            }

            if (files.size() == 0) {
                if (subSystemId.equals("000")) {
                    files = Arrays.asList(fileNames.get(0));
                }
            }
            for (String file : files) {
                if (maskingOrDq.equalsIgnoreCase("DQ")) {
                    fileRecords = readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "\\resources\\dqFiles\\" + file);
                } else if (maskingOrDq.equalsIgnoreCase("MASKING")) {
                    fileRecords = readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "\\resources\\maskingFiles\\" + file);
                }
                multipleFileTargetColumnValue.add(fileRecords.stream().map(x -> x.get(targetFileColumnName)).collect(Collectors.toList()));
            }
            targetColumnValues = multipleFileTargetColumnValue.stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }
        return targetColumnValues;
    }

    public Boolean verifyForeignKeyInPrimaryKeyFileRule10(String mi, String tableId, String targetFileColumnName, String currentFileValue, String maskingOrDq) throws IOException {
        boolean flag = false;
        boolean primaryForeignKeyFlag = false;
        ReadTextFile readTextFile = new ReadTextFile();
        List<String> fileNames = null;
        if (maskingOrDq.equalsIgnoreCase("DQ")) {
            fileNames = getDQFileNames(tableId);
        } else if (maskingOrDq.equalsIgnoreCase("MASKING")) {
            fileNames = getMaskingFileNames(tableId);
        }

        fileNames = fileNames.stream().filter(x -> x.substring(0, 4).equals(mi)).collect(Collectors.toList());
        if (fileNames.size() == 0) {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))), "--- FAIL ---> ****File Not Found---Unable to find File:- MI:- " + mi + "and table Id:- " + tableId + " for " + currentFileValue);
        }

        if ((tableId.equals("0100") || tableId.equals("0110") || tableId.equals("0120") || tableId.equals("0121")
                || tableId.equals("0201") || tableId.equals("0202") || tableId.equals("0211")
                || tableId.equals("0212") || tableId.equals("0221")) && fileNames.size() == 1) {
            List<Map<String, String>> fileRecords = null;
            for (String file : fileNames) {
                if (maskingOrDq.equalsIgnoreCase("DQ")) {
                    fileRecords = readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "\\resources\\dqFiles\\" + file);
                } else if (maskingOrDq.equalsIgnoreCase("MASKING")) {
                    fileRecords = readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "\\resources\\maskingFiles\\" + file);
                }
                List<String> targetColumnValues = fileRecords.stream().map(x -> x.get("Depositor_Unique_ID")).collect(Collectors.toList());
                primaryForeignKeyFlag = targetColumnValues.stream()
                        .anyMatch(t -> t.equals(currentFileValue));

             /*   for (Map<String, String> fileRecord : fileRecords) {
                    if (fileRecord.get(targetFileColumnName).equals(currentFileValue)) {
                        primaryForeignKeyFlag = true;
                        break;
                    }
                }*/
                /*if (primaryForeignKeyFlag) {
                    break;
                }*/

            }
        } else if ((tableId.equals("0130") || tableId.equals("0140") || tableId.equals("0152") || tableId.equals("0153")
                || tableId.equals("0160") || tableId.equals("0231") || tableId.equals("0232")
                || tableId.equals("0233") || tableId.equals("0234") || tableId.equals("0235") || tableId.equals("0236")
                || tableId.equals("0237") || tableId.equals("0238") || tableId.equals("0239") || tableId.equals("0240")
                || tableId.equals("0241") || tableId.equals("0242") || tableId.equals("0400") || tableId.equals("0401")
                || tableId.equals("0500") || tableId.equals("0501") || tableId.equals("0600") || tableId.equals("0800")
                || tableId.equals("0900") || tableId.equals("0999")) && fileNames.size() > 1) {
            List<Map<String, String>> fileRecords = null;
            for (String file : fileNames) {
                if (maskingOrDq.equalsIgnoreCase("DQ")) {
                    fileRecords = readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "\\resources\\dqFiles\\" + file);
                } else if (maskingOrDq.equalsIgnoreCase("MASKING")) {
                    fileRecords = readTextFile.readDQMaskingTextFiles(System.getProperty("user.dir") + "\\resources\\maskingFiles\\" + file);
                }

                for (Map<String, String> fileRecord : fileRecords) {
                    if (fileRecord.get(targetFileColumnName).equals(currentFileValue)) {
                        primaryForeignKeyFlag = true;
                        break;
                    }
                }
                if (!primaryForeignKeyFlag) {
                    System.err.println("Value:- " + currentFileValue + " for MI " +
                            mi + " is not present for tableId:- " + tableId);
                    primaryForeignKeyFlag = false;
                    break;
                }

            }
        } else {
            LoggingUtils.logFailure((new File((String) Objects.requireNonNull(BasePage.getExtentReportHelper().getScreenshot(SCREENSHOT)))),
                    "--- FAIL ---> Rule-->10 contains Multiple sub-system files for MI " + mi + " and table id " + tableId);
        }

        return primaryForeignKeyFlag;
    }

    //            Specific those characters in pipe delimited
    public Boolean acceptsOnlySpecificCharactersRule13(String value, String characters) {
        Pattern pattern = Pattern.compile("[" + characters + "]");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    //            Specific those characters in pipe delimited
    public Boolean acceptsOnlySpecificCharactersRule15(String value, String characters) {
        Pattern pattern = Pattern.compile("[" + characters + "]");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    //            Specific those characters in pipe delimited
    public Boolean acceptsOnlySpecificCharactersRule16(String value, String characters) {
        Pattern pattern = Pattern.compile("[" + characters + "]");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }


    public Boolean noBlankRule18(String value) {
        boolean blank = false;
        if (!StringUtils.isBlank(value)) {
            blank = true;
        }
        return blank;
    }

    public Boolean checkMaskedOrNotMasked(String value) {
        Pattern pattern = Pattern.compile("^[X]{1,}$");
        Matcher matcher = pattern.matcher(value.replaceAll("\\s", ""));
        return matcher.matches();
    }

    //            Specific those characters in pipe delimited
    public Boolean acceptsOnlySpecificCharactersRule21(String value, String characters) {
        Pattern pattern = Pattern.compile("(" + characters + ")");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    //            Specific those characters in pipe delimited
    public Boolean acceptsOnlySpecificCharactersRule22(String value, String characters) {
        Pattern pattern = Pattern.compile("[" + characters + "]");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }


    //            Specific those characters in pipe delimited
    public Boolean acceptsOnlySpecificCharactersRule24(String value, String characters) {
        Pattern pattern = Pattern.compile("(" + characters + ")");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    //  Specific those characters in pipe delimited
    public Boolean craCountryCodesRule25(String countryValue) {
        boolean countryFlag = false;
        String[] countries = {"AND", "ARE", "AFG", "ATG", "AIA", "ALB", "ARM", "ANT", "AGO", "ATA", "ARG", "ASM", "AUT", "AUS", "ABW", "AZE", "BIH", "BRB", "BGD", "BEL", "BFA", "BGR", "BHR", "BDI", "BEN", "BMU", "BRN", "BOL", "BRA", "BHS", "BTN", "BVT", "BWA", "BLR", "BLZ", "CAN", "CCK", "COD", "CAF", "COG", "CHE", "CIV", "COK", "CHL", "CMR", "CHN", "COL", "CRI", "CUB", "CPV", "CXR", "CYP", "CZE", "DEU", "DJI", "DNK", "DMA", "DOM", "DZA", "ECU", "EST", "EGY", "ESH", "ERI", "ESP", "ETH", "FIN", "FJI", "FLK", "FSM", "FRO", "FRA", "GAB", "GBR", "GRD", "GEO", "GUF", "GGY", "GHA", "GIB", "GRL", "GMB", "GIN", "GLP", "GNQ", "GRC", "SGS", "GTM", "GUM", "GNB", "GUY", "HKG", "HMD", "HND", "HRV", "HTI", "HUN", "IDN", "IRL", "ISR", "IND", "IOT", "IRQ", "IRN", "ISL", "ITA", "JEY", "JAM", "JOR", "JPN", "KEN", "KGZ", "KHM", "KIR", "COM", "KNA", "PRK", "KOR", "KWT", "CYM", "KAZ", "LAO", "LBN", "LCA", "LIE", "LKA", "LBR", "LSO", "LTU", "LUX", "LVA", "LBY", "MAR", "MCO", "MDA", "MNE", "MDG", "MHL", "MKD", "MLI", "MMR", "MNG", "MAC", "MNP", "MTQ", "MRT", "MSR", "MLT", "MUS", "MDV", "MWI", "MEX", "MYS", "MOZ", "NAM", "NCL", "NER", "NFK", "NGA", "NIC", "NLD", "NOR", "NPL", "NRU", "NIU", "NZL", "OMN", "PAN", "PER", "PYF", "PNG", "PHL", "PAK", "POL", "SPM", "PCN", "PRI", "PSE", "PRT", "PLW", "PRY", "QAT", "REU", "ROM", "SRB", "RUS", "RWA", "SAU", "SLB", "SYC", "SDN", "SWE", "SGP", "SHN", "SVN", "SJM", "SVK", "SLE", "SMR", "SEN", "SOM", "SUR", "STP", "SLV", "SYR", "SWZ", "TCA", "TCD", "ATF", "TGO", "THA", "TJK", "TKL", "TLS", "TKM", "TUN", "TON", "TUR", "TTO", "TUV", "TWN", "TZA", "UKR", "UGA", "UMI", "USA", "URY", "UZB", "VAT", "VCT", "VEN", "VGB", "VIR", "VNM", "VUT", "WLF", "WSM", "YEM", "MYT", "ZAF", "ZMB", "ZWE"};
        for (String country : countries) {
            if (countryValue.equals(country)) {
                countryFlag = true;
            }
        }
        return countryFlag;
    }

    //  Specific those characters in pipe delimited
    public Boolean isoCountryRule26(String countryValue) {
        boolean countryFlag = false;
        String[] countries = {"ADP", "AED", "AFA", "ALL", "AMD", "ANG", "AON", "AOR", "ARS", "ATS", "AUD", "AWG", "AZM", "BAM", "BBD", "BDT", "BEF", "BGL", "BGN", "BHD", "BIF", "BMD", "BND", "BRL", "BSD", "BTN", "BWP", "BYR", "BZD", "CAD", "CDF", "CHF", "CLF", "CLP", "CNY", "COP", "CRC", "CUP", "CVE", "CYP", "CZK", "DEM", "DJF", "DKK", "DOP", "DZD", "ECS", "ECV", "EEK", "EGP", "ERN", "ESP", "ETB", "EUR", "EGT", "FIM", "FJD", "FKP", "FRF", "GBP", "GEL", "GGP", "GHC", "GIP", "GMD", "GNF", "GRD", "GTQ", "GWP", "GYD", "HKD", "HNL", "HRK", "HTG", "HUF", "IDR", "IEP", "ILS", "INR", "IQD", "IRR", "ISK", "ITL", "JMD", "JOD", "JPY", "KES", "KGS", "KHR", "KMF", "KPW", "KRW", "KWD", "KYD", "KZT", "LAK", "LBP", "LKR", "LRD", "LSL", "LTL", "LUF", "LVL", "LYD", "MAD", "MDL", "MGF", "MKD", "MMK", "MNT", "MOP", "MRO", "MTL", "MUR", "MVR", "MWK", "MXN", "MXP", "MXV", "MYR", "MZM", "NAD", "NGN", "NIO", "NLG", "NOK", "NPR", "NZD", "OMR", "PAB", "PEN", "PGK", "PHP", "PKR", "PLN", "PTE", "PYG", "QAR", "ROL", "RUB", "RUR", "RWF", "SAR", "SBD", "SCR", "SDD", "SEK", "SGD", "SHP", "SIT", "SKK", "SLL", "SOS", "SRG", "STD", "SVC", "SYP", "SZL", "THB", "TJR", "TJS", "TMM", "TND", "TOP", "TPE", "TRL", "TRY", "TTD", "TWD", "TZS", "UAH", "UGX", "USD", "USN", "USS", "UYU", "UZS", "VEB", "VND", "VUV", "WST", "XAF", "XAG", "XAU", "XBA", "XBB", "XBC", "XBD", "XCD", "XDR", "XEU", "XFO", "XFU", "XOF", "XPD", "XPF", "XPT", "XTS", "XXX", "YER", "YUM", "ZAL", "ZAR", "ZMK", "ZRN", "ZWD"};
        for (String country : countries) {
            if (countryValue.equals(country)) {
                countryFlag = true;
            }
        }
        return countryFlag;
    }

    //  Specific those characters in pipe delimited
    public Boolean countryNamesRule27(String countryValue) {
        boolean countryFlag = false;
//        String[] countries = {"ANDORRA", "UNITED ARAB EMIRATES", "AFGHANISTAN", "ANTIGUA AND BARBUDA", "ANGUILLA", "ALBANIA", "ARMENIA", "NETHERLANDS ANTILLES", "ANGOLA", "ANTARCTICA", "ARGENTINA", "AMERICAN SAMOA", "AUSTRIA", "AUSTRALIA", "ARUBA", "AZERBAIJAN", "BOSNIA-HERZEGOVINA", "BARBADOS", "BANGLADESH", "BELGIUM", "BURKINA FASO", "BULGARIA", "BAHRAIN", "BURUNDI", "BENIN", "BERMUDA", "BRUNEI DARUSSALAM", "BOLIVIA", "BRAZIL", "BAHAMAS", "BHUTAN", "BOUVET ISLAND", "BOTSWANA", "BELARUS", "BELIZE", "CANADA", "COCOS (KEELING) ISLAND", "CONGO, DEMOCRATIC REPUBLIC OF", "CENTRAL AFRICAN REPUBLIC", "CONGO (REPUBLIC)", "SWITZERLAND", "IVORY COAST", "COOK ISLANDS", "CHILE", "CAMEROON", "CHINA", "COLOMBIA", "COSTA RICA", "CUBA", "CAPE VERDE", "CHRISTMAS ISLAND", "CYPRUS", "CZECH REPUBLIC", "GERMANY", "DJIBOUTI", "DENMARK", "DOMINICA", "DOMINICAN REPUBLIC", "ALGERIA", "ECUADOR", "ESTONIA", "EGYPT", "WESTERN SAHARA", "ERITREA", "SPAIN", "ETHIOPIA", "FINLAND", "FIJI", "FALKLAND ISLANDS", "MICRONESIA, FEDERAL STATES OF", "FAROE ISLANDS", "FRANCE", "GABON", "United Kingdom", "UNITED KINGDOM (GREAT BRITAIN)", "GRENADA", "GEORGIA", "FRENCH GUIANA", "GUERNSEY", "GHANA", "GIBRALTAR", "GREENLAND", "GAMBIA", "GUINEA", "GUADELOUPE", "EQUATORIAL GUINEA", "GREECE", "SOUTH GEORGIA & SOUTH SANDWICH ISLANDS", "GUATEMALA", "GUAM", "GUINEA-BISSAU", "GUYANA", "HONG KONG", "HEARD & MCDONALD ISLANDS", "HONDURAS", "CROATIA", "HAITI", "HUNGARY", "INDONESIA", "IRELAND", "ISRAEL", "INDIA", "BRITISH INDIAN OCEAN TERRITORY", "IRAQ", "IRAN", "ICELAND", "ITALY", "JERSEY", "JAMAICA", "JORDAN", "JAPAN", "KENYA", "KYRGYZSTAN", "CAMBODIA", "KIRIBATI", "COMOROS", "SAINT KITTS AND NEVIS", "KOREA (DEMOCRATIC PEOPLE'S REPUBLIC)", "KOREA (SOUTH)", "KUWAIT", "CAYMAN ISLANDS", "KAZAKHSTAN", "LAO (PEOPLE'S DEMOCRATIC REPUBLIC)", "LEBANON", "SAINT LUCIA", "LIECHTENSTEIN", "SRI LANKA", "LIBERIA", "LESOTHO", "LITHUANIA", "LUXEMBOURG", "LATVIA", "LIBYA", "MOROCCO", "MONACO", "MOLDOVA", "MONTENEGRO", "MADAGASCAR", "MARSHALL ISLANDS", "MACEDONIA", "MALI", "MYANMAR", "MONGOLIA", "MACAU", "NORTHERN MARIANA ISLANDS", "MARTINIQUE", "MAURITANIA", "MONTSERRAT", "MALTA", "MAURITIUS", "MALDIVES", "MALAWI", "MEXICO", "MALAYSIA", "MOZAMBIQUE", "NAMIBIA", "NEW CALEDONIA", "NIGER", "NORFOLK ISLANDS", "NIGERIA", "NICARAGUA", "NETHERLANDS", "NORWAY", "NEPAL", "NAURU", "NIUE", "NEW ZEALAND", "OMAN", "PANAMA", "PERU", "FRENCH POLYNESIA", "PAPUA NEW GUINEA", "PHILIPPINES", "PAKISTAN", "POLAND", "SAINT PIERRE AND MIQUELON", "PITCAIRN", "PUERTO RICO", "PALESTINE", "PORTUGAL", "PALAU", "PARAGUAY", "QATAR", "REUNION", "ROMANIA", "SERBIA", "RUSSIAN FEDERATION", "RWANDA", "SAUDI ARABIA", "SOLOMON ISLANDS", "SEYCHELLES", "SUDAN", "SWEDEN", "SINGAPORE", "SAINT HELENA", "SLOVENIA", "SVALBARD & JAN MAYEN", "SLOVAKIA", "SIERRA LEONE", "SAN MARINO", "SENEGAL", "SOMALIA", "SURINAME", "SAO TOME AND PRINCIPE", "EL SALVADOR", "SYRIA", "SWAZILAND", "TURKS AND CAICOS ISLANDS", "CHAD", "FRENCH SOUTHERN TERRITORIES", "TOGO", "THAILAND", "TAJIKISTAN", "TOKELAU", "TIMOR-LESTE", "TURKMENISTAN", "TUNISIA", "TONGA", "TURKEY", "TRINIDAD AND TOBAGO", "TUVALU", "TAIWAN", "TANZANIA", "UKRAINE", "UGANDA", "US MINOR OUTLYING ISLANDS", "USA", "URUGUAY", "UZBEKISTAN", "VATICAN", "ST VINCENT AND THE GRENADINES", "VENEZUELA", "BRITISH VIRGIN ISLANDS", "U.S. VIRGIN ISLANDS", "VIET NAM", "VANUATU", "WALLIS AND FUTUNA", "SAMOA", "YEMEN", "MAYOTTE", "SOUTH AFRICA", "ZAMBIA", "ZIMBABWE"};
        String[] countries = {"Admiralty Islands", "Aegean Islands", "Afghanistan", "Aland Islands", "Albania", "Algeria", "Alofi Islands", "American Samoa", "Andaman Islands", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Ascension", "Aunu'u and Manua Islands", "Australia", "Austria", "Azerbaijan", "Azores", "Bahamas", "Bahrain", "Baker Island", "Balearic Islands", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bikini Island", "Billiton Island", "Bolivia", "Bonaire", "Bosnia-Herzegovina", "Botswana", "Bouvet Island", "Brazil", "British Antarctic Territory", "British Indian Ocean Territory", "British Virgin Islands", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burma", "Burundi", "Cambodia", "Cameroon", "Campbell Island", "Canada", "Canary Islands", "Cape Verde", "Caroline Islands", "Cayman Islands", "Central African Republic", "Chad", "Channel Islands", "Chatham Island", "Chile", "China", "Christmas Island", "Cocos (Keeling) Island", "Colombia", "Comoros", "Congo (Republic)", "Congo, Democratic Republic Of", "Cook Islands", "Corfu", "Corsica", "Costa Rica", "Crete", "Croatia", "Cuba", "Curacao", "Cyprus", "Cyrenaica", "Czech Republic", "Denmark ", "Desroches", "Djibouti", "Dodecanese Island", "Dominica", "Dominican Republic", "Ducie Island", "Dutch Caribbean", "East Timor", "Easter Island", "Ecuador", "Egypt", "El Salvador", "Ellice Island", "England", "Enwetok and Kwajelein Islands", "Equatorial Guinea", "Eritrea", "Estonia", "Eswatini", "Ethiopia", "Falkland Islands", "Fanning Island", "Faroe Islands", "Farquhar Island", "Fiji", "Finland", "France", "French Guinea", "French Polynesia", "French Southern Territories", "Friendly Islands", "Futuna Island", "Gabon", "Gambia", "Gambier Islands", "Georgia", "Germany", "Ghana", "Gibraltar", "Gilbert Islands", "Great Britain", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guernsey", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Hawaii", "Heard & McDonald Islands", "Henderson Island", "Hervey Islands", "Holland", "Holy See", "Honduras", "Hong Kong", "Howland Island", "Hungary", "Huon Islands", "Iceland", "Ifni Territory", "India", "Indonesia", "Inner Mongolia", "Iran", "Iraq", "Ireland", "Isle of Man", "Isle of Pines", "Israel", "Italy", "Ivory Coast", "Jamaica", "Japan", "Jarvis Island", "Jersey", "Johnston Island", "Jordan", "Kamaran Islands", "Kazakhstan", "Kenya", "Kermadec Islands", "Kiribati", "Korea (Democratic People's Republic)", "Korea (South)", "Kosova", "Kuwait", "Kyrgyzstan", "Lao", "Latvia", "Lebanon", "Leeward Islands", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Line Island", "Lithuania", "Lord Howe Island", "Loyalty Islands", "Luxembourg", "Macau", "Macedonia", "Madagascar", "Madeira", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Malvinas", "Manchuria", "Manus Island", "Mariana Islands", "Marquesas Islands", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia, Federal States Of", "Midway Island", "Moldova", "Moluccas Islands", "Monaco", "Mongolia", "Montenegro", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Navassa Island", "Nepal", "Netherlands", "Netherlands Antilles", "Nevis", "New Britain", "New Caledonia", "New Ireland", "New Zealand", "Nicaragua", "Nicobar Island", "Niger", "Nigeria", "Niue", "Norfolk Islands", "Northern Ireland", "Northern Mariana Islands", "Norway", "Ocean Island", "Oeno Island", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Phoenix Island", "Philippines", "Pitcairn", "Poland", "Portugal", "Portuguese Timor", "Principe", "Puerto Rico", "Qatar", "Rarotonga", "Réunion", "Rodriguez Island", "Romania", "Ross Dependency", "Russian Federation", "Rwanda", "Saba", "Saint Barthélémy", "Saint Helena", "Saint Kitts and Nevis", "Saint Lucia", "Saint Martin", "Saint Pierre and Miquelon", "Samoa", "San Marino", "Sand Island", "Santas Cruz Island", "Sao Tome and Principe", "Saudi Arabia", "Savage Island", "Scotland", "Senegal", "Serbia", "Seychelles", "Shortland Island", "Sicily", "Sierra Leone", "Singapore", "Sint Eustatius", "Sint Maarten", "Slovakia", "Slovenia", "Society Islands", "Solomon Islands", "Somalia", "South Africa", "South Georgia & South Sandwich Islands", "South Orkney Islands", "South Shetland Islands", "South Sudan", "Spain", "Spanish Territories of North Africa", "Sri Lanka", "St Croix", "St John Island", "St Thomas Island", "St Vincent and the Grenadines", "Sudan", "Suriname", "Suwarrow Island", "Svalbard & Jan Mayen", "Swains Island", "Sweden", "Switzerland", "Syria", "Tahiti", "Taiwan", "Tajikistan", "Tanzania", "Tarawa Island", "Tasmania", "Thailand", "Tibet", "Timor-Leste", "Togo", "Tokelau", "Tonga", "Torres Island", "Tortola Island", "Touamotu Islands", "Trinidad and Tobago", "Tripolitania", "Tristan da Cunha", "Truk Island", "Trust Territory of Pacific", "Tubuai Islands", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tutuila Island", "Tuvalu", "U.S. Virgin Islands", "Uganda", "Ukraine", "Union Group", "United Arab Emirates", "United Kingdom", "United Kingdom (Great Britain)", "USA", "Uruguay", "U.S. Minor Outlying Islands", "Uzbekistan", "Vanuatu", "Vatican", "Venezuela", "Vietnam", "Vojvodina", "Wake Island", "Wales", "Wallis and Futuna", "Washington Island", "West Bank and Gaza Strip", "Western Sahara", "Yap Island", "Yemen", "Yugoslavia", "Zaire", "Zambia", "Zimbabwe"};
        for (String country : countries) {
            if (countryValue.equalsIgnoreCase(country)) {
                countryFlag = true;
            }
        }
        return countryFlag;
    }

    //    Rule is not present in PDF document just for identifying i added a new rule
    //  Specific those characters in pipe delimited
    public Boolean provinceRule28(String provinceValue) {
        boolean provinceFlag = false;
        String[] provinces = {"AB", "BC", "MB", "NB", "NL", "NT", "NS", "NU", "ON", "PE", "QC", "SK", "YT", "AL", "AK", "AS", "AZ", "AR", "AE", "AA", "AE", "AE", "AE", "AP", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "GU", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MH", "MD", "MA", "MI", "FM", "MN", "UM", "MS", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "MP", "OH", "OK", "OR", "PW", "PA", "PR", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "VI", "WA", "WV", "WI", "WY"};
        for (String province : provinces) {
            if (provinceValue.equalsIgnoreCase(province)) {
                provinceFlag = true;
            }
        }
        return provinceFlag;
    }


    public HashMap<String, Map<String, Integer>> generateReport(List<String> report) {
        HashMap<String, String> summaryReport = new LinkedHashMap<>();
        HashMap<String, Map<String, Integer>> finalReport = new LinkedHashMap<>();
        Set<String> fileNames = new HashSet<>();
        List<String> failedResults = null;
        for (String result : report) {
            if (result.contains(".txt")) {
                String fileName = result.split(",")[report.get(1).split(",").length - 1];
                fileNames.add(fileName);
            }
        }
        for (String fileName : fileNames) {
            Set<String> rule9Values = new HashSet<>();
            Set<String> fileKeys = new HashSet<>();
            Map<String, Integer> dataCount = new HashMap<>();
            failedResults = report.stream().filter(x -> x.contains(fileName)).collect(Collectors.toList()).
                    stream().filter(x -> !(x.contains("**PASS**"))).collect(Collectors.toList());
            for (String failedResult : failedResults) {
                String results = failedResult.split(",")[failedResult.split(",").length - 2];
                if (!results.equalsIgnoreCase("**PASS**")) {
                    List<String> listOfKeys = getKeysFromResult(results);
                    if (results.contains("Rule-9")) {
                        rule9Values.add(Arrays.asList(results.split("Rule-9")[0].split(":-")[1].split("\\s")).stream().filter(x -> !x.equals("")).collect(Collectors.toList()).get(0));
                    }
                    for (String key : listOfKeys) {
                        if (fileKeys.contains(key)) {
                            dataCount.put(key, (dataCount.get(key) + 1));
                        } else if (key.contains("Rule-9")) {
                            dataCount.put(key, (rule9Values.size()));
                        } else {
                            fileKeys.add(key);
                            dataCount.put(key, 1);
                        }
                    }
                }

            }

            finalReport.put(fileName, dataCount);

        }
        return finalReport;
    }

    public List<String> getKeysFromResult(String result) {
        List<String> keys = new LinkedList<>();
        List<String> results = Arrays.asList(result.split(";"));
        for (int i = 0; i < results.size(); i++) {
            String key = results.get(i).split("->")[1].split(":-")[0].trim();
            keys.add(getRuleValue(results.get(i)) + "_" + key);
        }
        return keys;
    }

    public String getRuleValue(String result) {
        String ruleValue = null;
        for (String word : result.split(" ")) {
            if (word.contains("Rule")) {
                ruleValue = word;
                break;
            }
        }
        return ruleValue;
    }


}


