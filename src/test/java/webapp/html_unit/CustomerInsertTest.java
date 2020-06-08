package webapp.html_unit;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import org.junit.Test;
import webapp.services.CustomerDTO;
import webapp.utils.VvsTest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CustomerInsertTest extends VvsTest {

    private static final String VAT1 = "290998204";
    private static final String VAT2 = "229841783";

    @Test
    public void customerInsertionTest() throws IOException {
        CustomerDTO firstCustomer = new CustomerDTO(998, Integer.parseInt(VAT1), "fc52475 - 1", 929717596);
        insertNewCustomer(firstCustomer);
        CustomerDTO secondCustomer = new CustomerDTO(997, Integer.parseInt(VAT2), "fc52475 - 2", 969717595);
        insertNewCustomer(secondCustomer);

        HtmlAnchor getCustomersLink = page.getAnchorByHref("GetAllCustomersPageController");
        HtmlPage nextPage = (HtmlPage) getCustomersLink.openLinkInNewWindow();
        final HtmlTable table = nextPage.getHtmlElementById("clients");
        List<HtmlTableRow> rows = table.getRows().stream().skip(table.getRowCount() - 2).collect(Collectors.toList());
        verifySavedCustomerInfo(firstCustomer, rows.get(0));
        verifySavedCustomerInfo(secondCustomer, rows.get(1));

        removeCustomer(VAT1);
        removeCustomer(VAT2);
        nextPage = (HtmlPage) getCustomersLink.openLinkInNewWindow();
        assertFalse(nextPage.asText().contains(VAT1));
        assertFalse(nextPage.asText().contains(VAT2));
    }

    private void verifySavedCustomerInfo(CustomerDTO customer, HtmlTableRow row) {
        assertEquals(row.getCell(0).asText(), customer.designation);
        assertEquals(row.getCell(1).asText(), String.valueOf(customer.phoneNumber));
        assertEquals(row.getCell(2).asText(), String.valueOf(customer.vat));
    }
}
