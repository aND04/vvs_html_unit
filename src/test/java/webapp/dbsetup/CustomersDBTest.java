package webapp.dbsetup;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.Destination;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import webapp.services.ApplicationException;
import webapp.services.CustomerDTO;
import webapp.services.CustomerService;
import webapp.services.CustomersDTO;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;
import static webapp.dbsetup.DBSetupUtils.*;

public class CustomersDBTest {

    private static Destination dataSource;

    // the tracker is static because JUnit uses a separate Test instance for every test method.
    private static DbSetupTracker dbSetupTracker = new DbSetupTracker();

    @BeforeClass
    public static void setupClass() {
        startApplicationDatabaseForTesting();
        dataSource = DriverManagerDestination.with(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    @Before
    public void setup() throws SQLException {

        Operation initDBOperations = Operations.sequenceOf(
                DELETE_ALL
                , INSERT_CUSTOMER_ADDRESS_DATA
        );

        DbSetup dbSetup = new DbSetup(dataSource, initDBOperations);

        // Use the tracker to launch the DbSetup. This will speed-up tests 
        // that do not not change the BD. Otherwise, just use dbSetup.launch();
        dbSetupTracker.launchIfNecessary(dbSetup);

    }

    @Test
    public void queryCustomerNumberTest() throws ApplicationException {
        // read-only test: unnecessary to re-launch setup after test has been run
        dbSetupTracker.skipNextLaunch();

        int expected = NUM_INIT_CUSTOMERS;
        int actual = CustomerService.INSTANCE.getAllCustomers().customers.size();

        assertEquals(expected, actual);
    }

    @Test
    public void addCustomerSizeTest() throws ApplicationException {

        CustomerService.INSTANCE.addCustomer(503183504, "FCUL", 217500000);
        int size = CustomerService.INSTANCE.getAllCustomers().customers.size();

        assertEquals(NUM_INIT_CUSTOMERS + 1, size);
    }

    private boolean hasClient(int vat) throws ApplicationException {
        CustomersDTO customersDTO = CustomerService.INSTANCE.getAllCustomers();

        for (CustomerDTO customer : customersDTO.customers)
            if (customer.vat == vat)
                return true;
        return false;
    }

    @Test
    public void addCustomerTest() throws ApplicationException {

        assumeFalse(hasClient(503183504));
        CustomerService.INSTANCE.addCustomer(503183504, "FCUL", 217500000);
        assertTrue(hasClient(503183504));
    }

}
