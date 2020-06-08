package webapp.dbsetup;

import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.operation.Operation;
import org.junit.Test;
import webapp.services.ApplicationException;
import webapp.services.CustomerService;
import webapp.services.SaleService;

import static org.junit.Assert.assertEquals;
import static webapp.utils.DBSetupUtils.*;

public class SalesDeliveryTest extends VvsDBTest {

    @Override
    public Operation initDBOperations() {
        return Operations.sequenceOf(DELETE_ALL, INSERT_CUSTOMER_SALE_ADDRESS_DELIVERYSALE_DATA);
    }

    @Test
    public void getSalesDeliveryByVatTest() throws ApplicationException {
        assertEquals(NUM_INIT_SALESDELIVERY, SaleService.INSTANCE.getSaleByCustomerVat(VAT).sales.size());
    }

    @Test
    public void addNewSalesDeliveryIncreasesSalesSizeByOneTest() throws ApplicationException {
        int saleDeliverySize = SaleService.INSTANCE.getSalesDeliveryByVat(VAT).sales_delivery.size();
        int saleId = SaleService.INSTANCE.getSaleByCustomerVat(VAT).sales.get(0).id;
        int addrId = CustomerService.INSTANCE.getAllAddresses(VAT).addrs.get(0).id;
        SaleService.INSTANCE.addSaleDelivery(saleId, addrId);
        assertEquals(saleDeliverySize + 1, SaleService.INSTANCE.getSalesDeliveryByVat(VAT).sales_delivery.size());
    }
}
