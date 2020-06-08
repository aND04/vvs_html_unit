package webapp.utils;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.junit.BeforeClass;
import webapp.services.CustomerDTO;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public abstract class VvsTest {

    protected static HtmlPage page;

    private static final String APPLICATION_URL = "http://localhost:8080/VVS_webappdemo/";
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

    public HtmlPage submitForm(WebRequest webRequest) throws IOException {
        HtmlPage resultPage = webClient.getPage(webRequest);
        assertEquals(200, resultPage.getWebResponse().getStatusCode());

        return resultPage;
    }

    public WebRequest get(String controller, List<NameValuePair> values) throws MalformedURLException {
        WebRequest webRequest = new WebRequest(new URL(APPLICATION_URL + controller), HttpMethod.GET);
        webRequest.setRequestParameters(values);
        return webRequest;
    }

    public WebRequest post(String controller, List<NameValuePair> body) throws MalformedURLException {
        WebRequest webRequest = new WebRequest(new URL(APPLICATION_URL + controller), HttpMethod.POST);
        /* TODO parse NameValuePair list if more than one element is present */
        webRequest.setRequestBody(String.format("%s=%s", body.get(0).getName(), body.get(0).getValue()));
        return webRequest;
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

    public void addAddress(String vat, AddressTestHelper address) throws IOException {
        HtmlPage addressPage = navigate("addAddressToCustomer.html", "Enter Address");
        HtmlForm newAddressForm = getForm(addressPage, "GetCustomerPageController");
        textInput(newAddressForm, "vat", vat);
        textInput(newAddressForm, "address", address.getAddress());
        textInput(newAddressForm, "door", address.getDoor());
        textInput(newAddressForm, "postalCode", address.getPostalCode());
        textInput(newAddressForm, "locality", address.getLocality());
        clickButtonByValue(newAddressForm, "Insert");
    }

    public HtmlTable getCustomerAddresses(String vat) throws IOException {
        HtmlPage customerInfoPage = submitForm(get("GetCustomerPageController",
                Arrays.asList(new NameValuePair("vat", vat), new NameValuePair("submit", "Get Customer"))));
        assertNotNull(customerInfoPage.getElementsByTagName("table"));
        return (HtmlTable) customerInfoPage.getElementsByTagName("table").get(0);
    }

    public HtmlPage insertNewSale(String vat, boolean newCustomer) throws IOException {
        if (newCustomer) {
            insertNewCustomer(new CustomerDTO(995, Integer.parseInt(vat), "fc52475 - 3", 919717597));
        }
        HtmlPage newSalePage = navigate("addSale.html", "New Sale");
        HtmlForm newSaleForm = getForm(newSalePage, "AddSalePageController");
        textInput(newSaleForm, "customerVat", vat);
        return submitForm(post("AddSalePageController",
                Collections.singletonList(new NameValuePair("customerVat", vat))));
    }
}
