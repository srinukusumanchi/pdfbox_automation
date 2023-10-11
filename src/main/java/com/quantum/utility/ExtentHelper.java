package com.quantum.utility;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class ExtentHelper {

    public static ExtentHelper test = new ExtentHelper();
    public static ExtentReports extent = null;
    public static JSONObject sampleObject = null;
    public static JSONArray messages = null;
    public static List<String> failedMessages = new LinkedList<>();

    public ExtentHelper() {
        // start reporters
        if (extent == null) {
       /*     ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("extent.html");

            // create ExtentReports and attach reporter(s)
            extent = new ExtentReports();
            extent.attachReporter(htmlReporter);
            // creates a toggle for the given test, adds all log events under it
            test = extent.createTest("Verify table 0800-Hold Balance File - Masking Content", "Masking files Validations");
       */
            messages = new JSONArray();
            sampleObject = new JSONObject();
        }


    }

    public void pass(String message) {


    }

    public void log(String messageHeader, String message) {

    }

    public void fail(String message) {
        failedMessages.add(message);
    }

    public void writeJsonSimpleDemo(String filename) throws Exception {

        sampleObject.put("Test Case Name", "Verify table 0800-Hold Balance File - Masking Content");


        Files.write(Paths.get(filename), sampleObject.toJSONString().getBytes());
    }

}
