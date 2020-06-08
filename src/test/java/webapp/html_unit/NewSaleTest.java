package webapp.html_unit;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.junit.Test;
import webapp.utils.VvsTest;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class NewSaleTest extends VvsTest {

    private static final String VAT = "222560436";

    @Test
    public void insertNewSaleTest() throws IOException {
        HtmlPage submittedSalesPage = insertNewSale(VAT);
        HtmlTable table = (HtmlTable) submittedSalesPage.getElementsByTagName("table").get(0);
        assertEquals("O", table.getRow(table.getRowCount() - 1).getCell(3).asText());
        removeCustomer(VAT);
    }

    @Test
    public void closeExitingSaleTest() throws IOException {
        insertNewSale(VAT);
        HtmlPage closeSalePage = navigate("UpdateSaleStatusPageControler", "Enter Sale Id");
        HtmlTable table = (HtmlTable) closeSalePage.getElementsByTagName("table").get(0);
        assertEquals("O", table.getRow(table.getRowCount() - 1).getCell(3).asText());
        String lastSaleId = table.getRow(table.getRowCount() - 1).getCell(0).asText();
        HtmlPage updatedCloseSalesPage = submitForm(get("UpdateSaleStatusPageControler",
                Arrays.asList(new NameValuePair("id", lastSaleId), new NameValuePair("submit", "Close Sale"))));
        HtmlTable updatedTable = (HtmlTable) updatedCloseSalesPage.getElementsByTagName("table").get(0);
        assertEquals("C", updatedTable.getRow(updatedTable.getRowCount() - 1).getCell(3).asText());
        removeCustomer(VAT);
    }
}