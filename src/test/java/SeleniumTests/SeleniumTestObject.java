package SeleniumTests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

// http://localhost:8080/bbmri_negotiator_war/dev/chose.xhtml
public class SeleniumTestObject {

    @FindBy(name = "j_idt81:j_idt93")
    public WebElement jidt81jidt93Input;

    @FindBy(id = "uploadform:title")
    public WebElement uploadformtitleInput;

    @FindBy(id = "uploadform:description")
    public WebElement uploadformdescriptionTextarea;

    @FindBy(id = "uploadform:requestdescription")
    public WebElement uploadformrequestdescriptionTextarea;

    @FindBy(name = "uploadform:j_idt154")
    public WebElement uploadformjidt154Input;

    public SeleniumTestObject(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }
}