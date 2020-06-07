package vvs_webapp;

import static org.junit.Assert.*;
import org.junit.*;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import utils.VvsTest;

import java.net.MalformedURLException;

import java.io.*;
import java.util.*;

public class TestIndex extends VvsTest {
	
	@Test
	public void indexTest() throws Exception {
        assertEquals("WebAppDemo Menu", page.getTitleText());

        final String pageAsXml = page.asXml();
        assertTrue(pageAsXml.contains("<div class=\"w3-container w3-blue-grey w3-center w3-allerta\" id=\"body\">"));

        final String pageAsText = page.asText();
        assertTrue(pageAsText.contains("WebAppDemo Menu"));
	}
	
	@Test
	public void numberOfOptionsTest() throws Exception { 
         List<DomElement> inputs = page.getElementsById("botao2");  // get list of case uses
         assertTrue(inputs.size()==APPLICATION_NUMBER_USE_CASES);
	}
	
	/**
	 * Here we test two operations (insert & remove) in order to leave the database
	 * in the original state
	 * 
	 * @throws IOException
	 */
	@Test
	public void insertAndRemoveClientTest() throws IOException {
        final String NPC = "503183504";
        final String DESIGNATION = "FCUL";
        final String PHONE = "217500000";
		
		// get a specific link
		HtmlAnchor addCustomerLink = page.getAnchorByHref("addCustomer.html");
		// click on it
		HtmlPage nextPage = (HtmlPage) addCustomerLink.openLinkInNewWindow();
		// check if title is the one expected
		assertEquals("Enter Name", nextPage.getTitleText());
		
		// get the page first form:
		HtmlForm addCustomerForm = nextPage.getForms().get(0);
		
		// place data at form
		HtmlInput vatInput = addCustomerForm.getInputByName("vat");
		vatInput.setValueAttribute(NPC);
		HtmlInput designationInput = addCustomerForm.getInputByName("designation");
		designationInput.setValueAttribute(DESIGNATION);
		HtmlInput phoneInput = addCustomerForm.getInputByName("phone");
		phoneInput.setValueAttribute(PHONE);
		// submit form
		HtmlInput submit = addCustomerForm.getInputByName("submit");

		// check if report page includes the proper values
		HtmlPage reportPage = submit.click();
		String textReportPage = reportPage.asText();
		assertTrue(textReportPage.contains(NPC));
		assertTrue(textReportPage.contains(DESIGNATION));
		assertTrue(textReportPage.contains(PHONE));
		
		// at index, goto Remove case use and remove the previous client
		HtmlAnchor removeCustomerLink = page.getAnchorByHref("RemoveCustomerPageController");
		nextPage = (HtmlPage) removeCustomerLink.openLinkInNewWindow();
		assertTrue(nextPage.asText().contains(NPC));
		
		HtmlForm removeCustomerForm = nextPage.getForms().get(0);
		vatInput = removeCustomerForm.getInputByName("vat");
		vatInput.setValueAttribute(NPC);
		submit = removeCustomerForm.getInputByName("submit");
		submit.click();
		
		// now check that the new client was erased
		HtmlAnchor getCustomersLink = page.getAnchorByHref("GetAllCustomersPageController");
		nextPage = (HtmlPage) getCustomersLink.openLinkInNewWindow();
		assertFalse(nextPage.asText().contains(NPC));
	}

	// not testing, just to show how to access tables inside the HTML
	@Test
	public void tablesTest() throws MalformedURLException {
		HtmlAnchor getCustomersLink = page.getAnchorByHref("GetAllCustomersPageController");
		HtmlPage nextPage = (HtmlPage) getCustomersLink.openLinkInNewWindow();
		
		final HtmlTable table = nextPage.getHtmlElementById("clients");
		System.out.println("---------------------------------");
		for (final HtmlTableRow row : table.getRows()) {
		    System.out.println("Found row");
		    for (final HtmlTableCell cell : row.getCells()) {
		       System.out.println("   Found cell: " + cell.asText());
		    }
		}
		System.out.println("---------------------------------");
	}
	
	// Eg of testing a GET request.
	// For a POST request cf. stackoverflow.com/questions/30687614
	@Test
	public void parametersGetTest() throws IOException {
		
		HtmlPage reportPage;
		
		// Build a GET request
		try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) { 
			java.net.URL url = new java.net.URL(APPLICATION_URL+"GetCustomerPageController");
			WebRequest requestSettings = new WebRequest(url, HttpMethod.GET);

			// Set the request parameters
			requestSettings.setRequestParameters(new ArrayList<NameValuePair>());
			requestSettings.getRequestParameters().add(new NameValuePair("vat", "197672337"));
			requestSettings.getRequestParameters().add(new NameValuePair("submit", "Get+Customer"));

			reportPage = webClient.getPage(requestSettings);
			assertEquals(HttpMethod.GET, reportPage.getWebResponse().getWebRequest().getHttpMethod());		
		}
		
		assertTrue(reportPage.asXml().contains("JOSE FARIA"));
		
		// to check GET parameter's
//		List<NameValuePair> parameters = reportPage.getWebResponse().getWebRequest().getRequestParameters();
//		for (NameValuePair parameter : parameters) {
//			System.out.println(parameter.getName() + " = " + parameter.getValue());
//		}
	}

}





