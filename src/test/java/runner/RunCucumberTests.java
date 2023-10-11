package runner;


import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.AfterClass;
import org.junit.runner.RunWith;

import static com.quantum.utility.ExtentHelper.*;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/main/resources/scenarios",
        glue = "com.quantum.steps",
        tags = "@us0999dq",
        plugin = {"json:target/cucumber-report.json",
                "html:target/cucumber-report"})
public class RunCucumberTests {

}
