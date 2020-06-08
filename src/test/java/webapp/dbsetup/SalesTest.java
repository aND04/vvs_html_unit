package webapp.dbsetup;

import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.operation.Operation;
import org.junit.Test;
import webapp.services.ApplicationException;
import webapp.services.SaleDTO;
import webapp.services.SaleService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static webapp.utils.DBSetupUtils.*;

public class SalesTest extends VvsDBTest {

    @Override
    public Operation initDBOperations() {
        return Operations.sequenceOf(DELETE_ALL, INSERT_CUSTOMER_SALE_DATA);
    }

    @Test
    public void addingSaleIncreasesSalesSizeByOneTest() throws ApplicationException {
        List<SaleDTO> sales = SaleService.INSTANCE.getSaleByCustomerVat(VAT).sales;
        SaleService.INSTANCE.addSale(VAT);
        assertEquals(sales.size() + 1, SaleService.INSTANCE.getSaleByCustomerVat(VAT).sales.size());
    }

    @Test
    public void newSaleShouldHaveOpenStatusTest() throws ApplicationException {
        SaleService.INSTANCE.addSale(VAT);
        List<SaleDTO> sales = SaleService.INSTANCE.getSaleByCustomerVat(VAT).sales;
        SaleDTO insertedSale = sales.get(sales.size() - 1);
        assertEquals("O", insertedSale.statusId);
    }

    @Test
    public void updatingSaleShouldChangeTheStatusToClosedTest() throws ApplicationException {
        List<SaleDTO> sales = SaleService.INSTANCE.getSaleByCustomerVat(VAT).sales;
        SaleDTO sale = sales.get(sales.size() - 1);
        assertEquals("O", sale.statusId);
        SaleService.INSTANCE.updateSale(sale.id);
        sales = SaleService.INSTANCE.getSaleByCustomerVat(VAT).sales;
        sale = sales.get(sales.size() - 1);
        assertEquals("C", sale.statusId);
    }
}
