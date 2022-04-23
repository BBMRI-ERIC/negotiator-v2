package SeleniumTests.CreateRequestTest;

import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.support.FindBy;
import java.io.IOException;

public class CreateRequestTest {

    public void runTestCreateNewRequest() throws IOException, ParseException {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        loginToNegotiator(driver);
        String url = createQueryAPICall();
        createNewRequest(driver, url);

        driver.quit();
    }

    private void loginToNegotiator(WebDriver driver) {
        driver.get("http://localhost:8080/bbmri_negotiator_war/dev/chose.xhtml");
        WebElement jidt81jidt93Input = driver.findElement(By.name("j_idt81:j_idt93"));
        jidt81jidt93Input.click();
    }

    private String createQueryAPICall() throws IOException, ParseException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create("{\n    \"collections\":[\n        {\n            \"biobankId\":\"bbmri-eric:ID:UK_GBR-1-19\",\n            \"collectionId\":\"bbmri-eric:ID:UK_GBR-1-19:collection:542\"\n        }\n    ],\n    \"humanReadable\":\"test\",\n    \"URL\":\"http://d1.ref.development.bibbox.org\"\n}", mediaType);
        Request request = new Request.Builder()
                .url("http://localhost:8080/bbmri_negotiator_war/api/directory/create_query")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Basic bW9sZ2VuaXN1c2VybmFtZTptb2xnZW5pc3Bhc3N3b3Jk")
                .build();
        Response response = client.newCall(request).execute();

        String responseString  = response.body().string();
        JSONParser parser = new JSONParser();
        JSONObject jsonObjectOriginalRequest = (JSONObject) parser.parse(responseString);
        String url = jsonObjectOriginalRequest.get("redirect_uri").toString();
        return url;
    }

    private void createNewRequest(WebDriver driver, String url) {
        driver.get(url);
        WebElement title = driver.findElement(By.id("uploadform:title"));
        WebElement description = driver.findElement(By.id("uploadform:description"));
        WebElement requestdescription = driver.findElement(By.id("uploadform:requestdescription"));
        WebElement submit = driver.findElement(By.name("uploadform:j_idt154"));

        title.sendKeys("Automated Test");
        description.sendKeys("Automated Test");
        requestdescription.sendKeys("Automated Test");
        submit.click();

        String url2 = driver.getCurrentUrl();
    }
}
