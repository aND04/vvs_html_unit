package utils;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.junit.BeforeClass;
import webapp.services.CustomerDTO;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public abstract class VvsTest {

    protected static final String APPLICATION_URL = "http://localhost:8080/VVS_webappdemo/";
    protected static final int APPLICATION_NUMBER_USE_CASES = 11;

    protected static HtmlPage page;

    private static WebClient webClient;

    @BeforeClass
    public static void setUpClass() throws IOException {
        webClient = new WebClient(BrowserVersion.getDefault());
        // possible configurations needed to prevent JUnit tests to fail for complex HTML pages
        webClient.setJavaScriptTimeout(15000);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        page = webClient.getPage(APPLICATION_URL);
        assertEquals(200, page.getWebResponse().getStatusCode()); // OK status
    }

    public void textInput(HtmlForm form, String name, String value) {
        final HtmlTextInput textInput = form.getInputByName(name);
        textInput.setValueAttribute(value);
    }

    public void clickButtonByValue(HtmlForm form, String value) throws IOException {
        final HtmlSubmitInput button = form.getInputByValue(value);
        button.click();
    }

    // https://stackoverflow.com/questions/9589802/htmlunit-getformbyname-with-no-form-name-specified-in-the-website
    public HtmlForm getForm(HtmlPage currentPage, String action) {
        return currentPage.getFirstByXPath("//form[@action='" + action + "']");
    }

    public HtmlPage submitForm(String controller, HttpMethod httpMethod, List<NameValuePair> values) throws IOException {
        URL url = new URL(APPLICATION_URL + controller);
        WebRequest webRequest = new WebRequest(url, httpMethod);
        webRequest.setRequestParameters(new ArrayList<>());
        webRequest.getRequestParameters().addAll(values);

        HtmlPage resultPage = webClient.getPage(webRequest);
        assertEquals(httpMethod, resultPage.getWebResponse().getWebRequest().getHttpMethod());

        return resultPage;
    }

    public HtmlPage navigate(String href, String pageTitle) throws MalformedURLException {
        HtmlPage htmlPage = (HtmlPage) page.getAnchorByHref(href).openLinkInNewWindow();
        assertEquals(pageTitle, htmlPage.getTitleText());
        return htmlPage;
    }

    public void insertNewCustomer(CustomerDTO customer) throws IOException {
        HtmlPage addCustomerPage = navigate("addCustomer.html", "Enter Name");
        HtmlForm form = getForm(addCustomerPage, "AddCustomerPageController");
        textInput(form, "vat", String.valueOf(customer.vat));
        textInput(form, "designation", customer.designation);
        textInput(form, "phone", String.valueOf(customer.phoneNumber));
        clickButtonByValue(form, "Get Customer");
    }

    public void removeCustomer(String vat) throws IOException {
        HtmlPage removeCustomerPage = navigate("RemoveCustomerPageController", "Enter VatNumber");
        HtmlForm form = getForm(removeCustomerPage, "RemoveCustomerPageController");
        textInput(form, "vat", vat);
        clickButtonByValue(form, "Remove");
    }
}
