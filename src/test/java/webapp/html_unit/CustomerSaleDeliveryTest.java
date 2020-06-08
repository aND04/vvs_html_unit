package webapp.html_unit;

import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.junit.Test;
import webapp.services.CustomerDTO;
import webapp.utils.AddressTestHelper;
import webapp.utils.VvsTest;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CustomerSaleDeliveryTest extends VvsTest {

    private static final String VAT = "221172025";

    @Test
    public void test() throws IOException {
        CustomerDTO customer = new CustomerDTO(888, Integer.parseInt(VAT), "fc52475", 919717597);
        insertNewCustomer(customer);
        HtmlPage customerPage = submitForm(get("GetCustomerPageController",
                Arrays.asList(new NameValuePair("vat", VAT), new NameValuePair("submit", "Get Customer"))));
        assertTrue(customerPage.asText().contains(customer.designation));
        assertTrue(customerPage.asText().contains(String.valueOf(customer.phoneNumber)));

        AddressTestHelper address = new AddressTestHelper("Easy St. 3", "3", "3333", "Lisbon");
        addAddress(VAT, address);
        insertNewSale(VAT, false);
        HtmlPage insertSaleDelivery = submitForm(get("AddSaleDeliveryPageController",
                Arrays.asList(new NameValuePair("vat", VAT), new NameValuePair("submit", "Get Customer"))));

        DomNodeList<DomElement> tables = insertSaleDelivery.getElementsByTagName("table");
        assertEquals(2, tables.size());

        HtmlTable addresses = (HtmlTable) tables.get(0);
        HtmlTableRow lastAddress = addresses.getRow(addresses.getRowCount() - 1);
        assertEquals(lastAddress.getCell(1).asText(), address.getAddress());
        assertEquals(lastAddress.getCell(2).asText(), address.getDoor());
        assertEquals(lastAddress.getCell(3).asText(), address.getPostalCode());
        assertEquals(lastAddress.getCell(4).asText(), address.getLocality());
        String newAddressInsertedId = lastAddress.getCell(0).asText();

        HtmlTable sales = (HtmlTable) tables.get(1);
        HtmlTableRow lastSale = sales.getRow(sales.getRowCount() - 1);
        assertEquals("O", lastSale.getCell(3).asText());
        assertEquals(VAT, lastSale.getCell(4).asText());
        String newInsertedSaleId = lastSale.getCell(0).asText();

        HtmlForm insertSaleDeliveryForm = getForm(insertSaleDelivery, "AddSaleDeliveryPageController");
        textInput(insertSaleDeliveryForm, "addr_id", newAddressInsertedId);
        textInput(insertSaleDeliveryForm, "sale_id", newInsertedSaleId);
        clickButtonByValue(insertSaleDeliveryForm, "Insert");
        HtmlPage insertedSalePage = submitForm(get("AddSaleDeliveryPageController",
                Arrays.asList(new NameValuePair("addr_id", newAddressInsertedId),
                        new NameValuePair("sale_id", newInsertedSaleId),
                        new NameValuePair("submit", "insert"))));
        HtmlTable insertedSaleTable = (HtmlTable) insertedSalePage.getElementsByTagName("table").get(0);
        HtmlTableRow lastRow = insertedSaleTable.getRow(insertedSaleTable.getRowCount() - 1);
        assertEquals(newInsertedSaleId, lastRow.getCell(1).asText());
        assertEquals(newAddressInsertedId, lastRow.getCell(2).asText());
        removeCustomer(VAT);
    }
}
