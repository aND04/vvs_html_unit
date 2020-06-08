package webapp.dbsetup;

import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.operation.Operation;
import org.junit.Test;
import webapp.services.ApplicationException;
import webapp.services.CustomerService;
import webapp.services.SaleDTO;
import webapp.services.SaleService;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static webapp.utils.DBSetupUtils.*;

public class CustomersTest extends VvsDBTest {

    @Override
    public Operation initDBOperations() {
        return Operations.sequenceOf(DELETE_ALL, INSERT_CUSTOMER_SALE_DATA);
    }

    @Test
    public void insertCustomerWithSameVatTest() throws ApplicationException {
        int sizeBeforeInsert = CustomerService.INSTANCE.getAllCustomers().customers.size();
        assertThrows(ApplicationException.class, () -> CustomerService.INSTANCE.addCustomer(VAT, "ALREADY EXISTENT CUSTOMER", 217500000));
        int sizeAfterInsert = CustomerService.INSTANCE.getAllCustomers().customers.size();
        assertEquals(sizeBeforeInsert, sizeAfterInsert);
    }

    @Test
    public void updateCustomerPhoneNumber() throws ApplicationException {
        assertEquals(ORIGINAL_PHONE_NUMBER, CustomerService.INSTANCE.getCustomerByVat(VAT).phoneNumber);
        CustomerService.INSTANCE.updateCustomerPhone(VAT, 808717597);
        assertEquals(808717597, CustomerService.INSTANCE.getCustomerByVat(VAT).phoneNumber);
    }

    @Test
    public void reAddDeletedCustomer() throws ApplicationException {
        assertNotNull(CustomerService.INSTANCE.getCustomerByVat(VAT));
        assertThrows(ApplicationException.class, () -> CustomerService.INSTANCE.addCustomer(VAT, "ALREADY EXISTENT CUSTOMER", 217500000));
        CustomerService.INSTANCE.removeCustomer(VAT);
        assertThrows(ApplicationException.class, () -> CustomerService.INSTANCE.getCustomerByVat(VAT));
        CustomerService.INSTANCE.addCustomer(VAT, "ALREADY EXISTENT CUSTOMER", 217500000);
        assertNotNull(CustomerService.INSTANCE.getCustomerByVat(VAT));
    }

    @Test
    public void salesRemovalOnCustomerRemovalTest() throws ApplicationException {
        List<SaleDTO> sales = SaleService.INSTANCE.getSaleByCustomerVat(VAT).sales;
        assertEquals(2, sales.size());
        CustomerService.INSTANCE.removeCustomer(VAT);
        assertTrue(SaleService.INSTANCE.getSaleByCustomerVat(VAT).sales.isEmpty());
    }
}
