package html_unit;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.junit.Test;
import utils.AddressTestHelper;
import utils.VvsTest;
import webapp.services.CustomerDTO;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class AddressInsertTest extends VvsTest {

    private static final String VAT = "259428272";

    @Test
    public void addressInsertionTest() throws IOException {
        insertNewCustomer(new CustomerDTO(999, Integer.parseInt(VAT), "fc52475", 919717597));
        int initialNumberOfAddresses = getCustomerAddresses().getRowCount();

        HtmlPage addressPage = navigate("addAddressToCustomer.html", "Enter Address");
        HtmlForm newAddressForm = getForm(addressPage, "GetCustomerPageController");
        AddressTestHelper firstAddress = new AddressTestHelper("Easy St.", "1", "1173", "Lisbon");
        addAddress(newAddressForm, firstAddress);
        AddressTestHelper secondAddress = new AddressTestHelper("Easy St. 2", "2", "1174", "Lisbon2");
        addAddress(newAddressForm, secondAddress);

        HtmlTable table = getCustomerAddresses();
        assertEquals(2L, table.getRowCount() - initialNumberOfAddresses);
        List<HtmlTableRow> rows = table.getRows().stream().skip(table.getRowCount() - 2).collect(Collectors.toList());
        verifySavedAddressInfo(firstAddress, rows.get(0));
        verifySavedAddressInfo(secondAddress, rows.get(1));
        removeCustomer(VAT);
    }

    private void verifySavedAddressInfo(AddressTestHelper address, HtmlTableRow row) {
        assertEquals(4, row.getCells().size());
        assertEquals(row.getCell(0).asText(), address.getAddress());
        assertEquals(row.getCell(1).asText(), address.getDoor());
        assertEquals(row.getCell(2).asText(), address.getPostalCode());
        assertEquals(row.getCell(3).asText(), address.getLocality());
    }

    private HtmlTable getCustomerAddresses() throws IOException {
        HtmlPage customerInfoPage = submitForm("GetCustomerPageController",
                HttpMethod.GET,
                Arrays.asList(new NameValuePair("vat", VAT), new NameValuePair("submit", "Get Customer")));

        return (HtmlTable) customerInfoPage.getElementsByTagName("table").get(0);
    }

    private void addAddress(HtmlForm form, AddressTestHelper address) throws IOException {
        textInput(form, "vat", VAT);
        textInput(form, "address", address.getAddress());
        textInput(form, "door", address.getDoor());
        textInput(form, "postalCode", address.getPostalCode());
        textInput(form, "locality", address.getLocality());
        clickButtonByValue(form, "Insert");
    }
}
