# Generated by Selenium IDE
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By


class TestDefaultSuite():
  def setup_method(self, method):
    chrome_options = Options()
    chrome_options.add_argument('--no-sandbox')
    chrome_options.add_argument('--headless')
    chrome_options.add_argument('--remote-debugging-port=9222')
    chrome_options.add_argument('--disable-dev-shm-usage')
    chrome_options.binary_location = "/usr/bin/google-chrome"
    self.driver = webdriver.Chrome(options=chrome_options, executable_path="/usr/local/bin/chromedriver")
    self.vars = {}

  def teardown_method(self, method):
    self.driver.quit()

  def test_login(self):
    self.driver.get("http://localhost:8080/dev/chose.xhtml")
    self.driver.find_element(By.NAME, "j_idt89:j_idt97").click()
    elements = self.driver.find_elements(By.CSS_SELECTOR, ".success > td:nth-child(1)")
    assert len(elements) > 0

  def test_createquery(self):
    self.driver.get("http://localhost:8080/researcher/newQuery.xhtml?jsonQueryId=3")
    self.driver.find_element(By.NAME, "j_idt89:j_idt95").click()
    self.driver.find_element(By.ID, "uploadform:title").click()
    self.driver.find_element(By.ID, "uploadform:title").send_keys("test")
    self.driver.find_element(By.ID, "uploadform:description").click()
    self.driver.find_element(By.ID, "uploadform:description").send_keys("test")
    self.driver.find_element(By.ID, "uploadform:requestdescription").click()
    self.driver.find_element(By.ID, "uploadform:requestdescription").send_keys("test")
    self.driver.find_element(By.NAME, "uploadform:j_idt185").click()
    elements = self.driver.find_elements(By.LINK_TEXT, "[5] test")
    assert len(elements) > 0

  def test_approvequery(self):
    self.driver.get("http://localhost:8080/negotiator_v2_war/dev/chose.xhtml")
    self.driver.find_element(By.NAME, "j_idt89:j_idt97").click()
    self.driver.find_element(By.CSS_SELECTOR, ".bbmriModule").click()
    self.driver.find_element(By.CSS_SELECTOR, ".nav:nth-child(1) .dropdown-toggle").click()
    self.driver.find_element(By.LINK_TEXT, "List of requests to review").click()
    self.driver.find_element(By.NAME, "j_idt101:0:approveRequest:j_idt103").click()
    self.driver.find_element(By.CSS_SELECTOR, ".panel-success > .panel-body:nth-child(2)").click()
    elements = self.driver.find_elements(By.CSS_SELECTOR, ".panel-success > .panel-body:nth-child(2)")
    assert len(elements) > 0

  def test_commentquery(self):
    self.driver.get("http://localhost:8080/dev/chose.xhtml")
    self.driver.find_element(By.NAME, "j_idt89:j_idt93").click()
    self.driver.find_element(By.ID, "j_idt134:0:queryItemActions").click()
    self.driver.find_element(By.CSS_SELECTOR, "#second > a").click()
    self.driver.find_element(By.ID, "j_idt324:uploadform:commentText").click()
    self.driver.find_element(By.ID, "j_idt324:uploadform:commentText").send_keys("test comment")
    self.driver.find_element(By.NAME, "j_idt324:uploadform:j_idt372").click()
    elements = self.driver.find_elements(By.CSS_SELECTOR, ".panel-heading:nth-child(2)")
    assert len(elements) > 0
