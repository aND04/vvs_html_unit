package webapp.dbsetup;

import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.operation.Operation;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import webapp.services.ApplicationException;
import webapp.services.CustomerDTO;
import webapp.services.CustomerService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static webapp.utils.DBSetupUtils.DELETE_ALL;
import static webapp.utils.DBSetupUtils.INSERT_MULTIPLE_CUSTOMERS;

public class MultipleCustomerDeleteTest extends VvsDBTest {

    @Override
    public Operation initDBOperations() {
        return Operations.sequenceOf(DELETE_ALL, INSERT_MULTIPLE_CUSTOMERS);
    }

    @Test
    @DisplayName("after deleting all costumers, the list of all customers should be empty")
    public void deletingAllCustomersShouldEmptyCustomerTableTest() throws ApplicationException {
        List<CustomerDTO> customers = CustomerService.INSTANCE.getAllCustomers().customers;
        assertEquals(5, customers.size());
        for (CustomerDTO customer : customers) {
            CustomerService.INSTANCE.removeCustomer(customer.vat);
        }
        assertTrue(CustomerService.INSTANCE.getAllCustomers().customers.isEmpty());
    }
}
