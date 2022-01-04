package SeleniumTests;

import SeleniumTests.CreateRequestTest.CreateRequestTest;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class CreateRequestTestRunner {

    public static void main(String[] args)  {
        CreateRequestTest createRequestTest = new CreateRequestTest();
        try {
            createRequestTest.runTestCreateNewRequest();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
