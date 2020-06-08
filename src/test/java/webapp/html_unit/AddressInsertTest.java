package webapp.html_unit;

import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import webapp.services.CustomerDTO;
import webapp.utils.AddressTestHelper;
import webapp.utils.VvsTest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class AddressInsertTest extends VvsTest {

    private static final String VAT = "259428272";

    @Test
    @DisplayName("insert two new addresses for an existing customer, then the table of " +
            "addresses of that client includes those addresses and its total row size " +
            "increases by two")
    public void addressInsertionTest() throws IOException {
        insertNewCustomer(new CustomerDTO(999, Integer.parseInt(VAT), "fc52475", 919717597));
        // table row count returns the header as one of the rows, which is not an address
        int initialNumberOfAddresses = 1;
        try {
            initialNumberOfAddresses = getCustomerAddresses(VAT).getRowCount();
        } catch (IndexOutOfBoundsException e) {
        }

        AddressTestHelper firstAddress = new AddressTestHelper("Easy St.", "1", "1173", "Lisbon");
        addAddress(VAT, firstAddress);
        AddressTestHelper secondAddress = new AddressTestHelper("Easy St. 2", "2", "1174", "Lisbon2");
        addAddress(VAT, secondAddress);

        HtmlTable table = getCustomerAddresses(VAT);
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
}
