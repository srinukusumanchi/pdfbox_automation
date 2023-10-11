package com.quantum.utility;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import com.quantum.utils.LogHelper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static com.quantum.constants.CommonConstants.SLASH_DELIMITER_TEXT;


public class ReadFromExcel {
    private HashMap<String, String> details = null;
    private List<String> columnNames = null;
    private List<String> columnValues = null;
    private static Workbook workbook = null;
    private Row row = null;

    private static Sheet createExcelInstance(String filePath, String fileName, String sheetName) throws IOException {
        String fileExtensionName = fileName.substring(fileName.indexOf('.'));
        //Create an object of File class to open xlsx file
        File file = new File(filePath + SLASH_DELIMITER_TEXT + fileName);
        LogHelper.logger.info("File name is" + filePath + SLASH_DELIMITER_TEXT + fileName);
        Sheet sheet = null;
        //Create an object of FileInputStream class to read excel file
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            if (fileExtensionName.equals(".xlsx")) {
                //If it is xlsx file then create object of XSSFWorkbook class
                workbook = new XSSFWorkbook(fileInputStream);
            }
            //Check condition if the file is xls file
            else if (fileExtensionName.equals(".xls")) {
                //If it is xls file then create object of XSSFWorkbook class
                workbook = new HSSFWorkbook(fileInputStream);
            }
        } catch (FileNotFoundException e) {
            LogHelper.logger.error(e.getMessage());
            //Check condition if the file is xlsx file

        }
        sheet = workbook.getSheet(sheetName);
        return sheet;
    }

    //To clear data in HashMap and ArrayList
    private void clearData(Map<String, String> details, List<String> columnNames) {
        details.clear();
        columnNames.clear();
    }

    public void saveColumnValueToSpecificScenarioSheetName(String filePath, String fileName, String sheetName, String scenarioId, String columnName, String columnValue) throws FilloException {
        Fillo fillo = new Fillo();
        Connection connection = fillo.getConnection(filePath + SLASH_DELIMITER_TEXT + fileName);
        String query = "UPDATE " + sheetName + " SET " + columnName + "='" + columnValue.replace("'","''") + "' WHERE ScenarioID = '" + scenarioId + "'";
        connection.executeUpdate(query);
        connection.close();
    }

    public Map<String, String> readLoginDetailsFromExcel(String filePath, String fileName, String sheetName, String scenarioId) throws IOException {
        Sheet sheet = createExcelInstance(filePath, fileName, sheetName);
        columnNames = readColumnNames(sheet);
        //Getting column data based on login scenario id
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            row = rowIterator.next();
            columnValues = readColumnValues(row);
            details = (HashMap<String, String>) dataCombiner(columnNames, columnValues);
            if (details.get("LoginEnvironment").equals(scenarioId)) {
                break;
            } else {
                clearData(details, columnValues);
            }
        }
        return details;
    }

    public Map<String, String> readLoginEnvDetailsFromExcel(String filePath, String fileName, String sheetName, String env) throws IOException {
        Sheet sheet = createExcelInstance(filePath, fileName, sheetName);
        columnNames = readColumnNames(sheet);

        //Getting column data based on login scenario id
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            row = rowIterator.next();
            columnValues = readColumnValues(row);
            details = (HashMap<String, String>) dataCombiner(columnNames, columnValues);
            if (details.get("LoginEnv").equals(env)) {
                break;
            } else {
                clearData(details, columnValues);
            }

        }
        return details;
    }

    //To Read column names from Excel and storing in ArrayList
    private List<String> readColumnNames(Sheet sheet) {
        ArrayList<String> columnNms = new ArrayList<>();
        row = sheet.getRow(0);
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            columnNms.add(cellIterator.next().toString());
        }
        return columnNms;
    }

    //To Read column values from Excel and storing in ArrayList
    private List<String> readColumnValues(Row row) {
        ArrayList<String> columnVal = new ArrayList<>();
        for (Cell aRow : row) {
            String tempcolumndata = aRow.toString();
            columnVal.add(tempcolumndata);
        }
        return columnVal;
    }

    //Storing column names and column data in a HashMap
    private Map<String, String> dataCombiner(List<String> columnNames, List<String> columnValues) {
        HashMap<String, String> loginDetails = new HashMap<>();
        Iterator<String> columnNameIterator = columnNames.iterator();
        Iterator<String> columnValueIterator = columnValues.iterator();
        while (columnNameIterator.hasNext() && columnValueIterator.hasNext()) {
            loginDetails.put(columnNameIterator.next(), columnValueIterator.next());
        }

        return loginDetails;
    }

    //To read login details from Excel
    public Map<String, String> readLoginExcel(String filePath, String fileName, String sheetName, String scenarioId) throws IOException {
        Sheet sheet = createExcelInstance(filePath, fileName, sheetName);
        columnNames = readColumnNames(sheet);

        //Getting column data based on login scenario id
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            row = rowIterator.next();
            columnValues = readColumnValues(row);
            details = (HashMap<String, String>) dataCombiner(columnNames, columnValues);
            if (details.get("TestID").equals(scenarioId)) {
                break;
            } else {
                clearData(details, columnValues);
            }
        }
        return details;
    }


    //To read Scenario details from Excel
    public Map<String, String> readFromExcel(String scenarioFilePath, String scenarioFileName, String scenarioSheetName, String scenarioId) throws IOException {
        Sheet sheet = createExcelInstance(scenarioFilePath, scenarioFileName, scenarioSheetName);
        //Getting column Names
        columnNames = readColumnNames(sheet);
        //Getting column Data
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            row = rowIterator.next();
            columnValues = readColumnValues(row);
            details = (HashMap<String, String>) dataCombiner(columnNames, columnValues);
            if (details.get("ExecutionFlag").equals("Yes") && details.get("ScenarioID").equals(scenarioId)) {
                break;
            } else {
                clearData(details, columnValues);
            }
        }
        return details;
    }

    public static void setSaveValue(String filePath, String fileName, String sheetName, String variableName, String variableValue) throws FilloException {

        Fillo fillo = new Fillo();
        Connection connection = fillo.getConnection(filePath + SLASH_DELIMITER_TEXT + fileName);
        Recordset variableNameRecordSet = null;
        String query = "SELECT VariableName FROM " + sheetName + " WHERE VariableName='" + variableName + "'";
        try {
            variableNameRecordSet = connection.executeQuery(query);
            while (variableNameRecordSet.next()) {
                if (variableNameRecordSet.getCount() > 0) {
                    query = "UPDATE " + sheetName + " SET VariableValue='" + variableValue + "' WHERE VariableName='" + variableName + "'";
                    connection.executeUpdate(query);

                }
            }
        } catch (FilloException fe) {
            query = "INSERT INTO " + sheetName + " (VariableName,VariableValue) VALUES ('" + variableName + "','" + variableValue + "')";
            connection.executeUpdate(query);
        } finally {
            connection.close();
        }

    }

    public Recordset getSaveValue(String filePath, String fileName, String sheetName, String variableName) throws FilloException {
        Recordset recordset = null;
        try {
            Fillo fillo = new Fillo();
            Connection connection = fillo.getConnection(filePath + "\\" + fileName);
            String query = "SELECT * FROM " + sheetName + " WHERE Description=" + "'" + variableName + "'";
            recordset = connection.executeQuery(query);
            recordset.next();


        } catch (NullPointerException ne) {
            LogHelper.logger.error("NullPointerException");
        }

        return recordset;
    }

    public Map<String, String> readSecurityQuestions(String filePath, String fileName, String sheetName, String scenarioId) throws IOException {
        Sheet sheet = createExcelInstance(filePath, fileName, sheetName);
        columnNames = readColumnNames(sheet);
        //Getting column data
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            row = rowIterator.next();
            columnValues = readColumnValues(row);
            details = (HashMap<String, String>) dataCombiner(columnNames, columnValues);
            if (details.get("Scenarioid").equals(scenarioId)) {
                break;
            } else {
                clearData(details, columnValues);
            }
        }
        return details;
    }
}