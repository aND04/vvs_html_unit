package webapp.utils;

import com.ninja_squad.dbsetup.operation.Insert;
import com.ninja_squad.dbsetup.operation.Operation;
import webapp.persistence.PersistenceException;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.ninja_squad.dbsetup.Operations.*;

/**
 * References:
 * <p>
 * HOME: http://dbsetup.ninja-squad.com/index.html
 * API: http://dbsetup.ninja-squad.com/apidoc/2.1.0/index.html
 * Best practices: http://dbunit.sourceforge.net/bestpractices.html
 *
 * @author jpn
 */
public class DBSetupUtils {

    public static final int VAT = 282917250;
    public static final int ORIGINAL_PHONE_NUMBER = 919717597;

    public static final String DB_URL = "jdbc:hsqldb:file:src/main/resources/data/hsqldb/cssdb";
    public static final String DB_USERNAME = "SA";
    public static final String DB_PASSWORD = "";

    private static boolean appDatabaseAlreadyStarted = false;

    public static void startApplicationDatabaseForTesting() {
        if (appDatabaseAlreadyStarted)  // just do it once for the entire test suite;
            return;
        try {
            webapp.persistence.DataSource.INSTANCE.connect(DB_URL, DB_USERNAME, DB_PASSWORD);
            appDatabaseAlreadyStarted = true;
        } catch (PersistenceException e) {
            throw new Error("Application DataSource could not be started");
        }
    }

    //////////////////////////////////////////
    // Operations for populating test database
    public static final Operation DELETE_ALL =
            deleteAllFrom("CUSTOMER", "SALE", "ADDRESS", "SALEDELIVERY");

    public static final int NUM_INIT_SALESDELIVERY;

    public static final Operation INSERT_CUSTOMER_SALE_DATA;
    public static final Operation INSERT_MULTIPLE_CUSTOMERS;
    public static final Operation INSERT_CUSTOMER_SALE_ADDRESS_DELIVERYSALE_DATA;

    static {
        Insert insertCustomer =
                insertInto("CUSTOMER")
                        .columns("ID", "DESIGNATION", "PHONENUMBER", "VATNUMBER")
                        .values(1, "FC52475", ORIGINAL_PHONE_NUMBER, VAT)
                        .build();

        Insert insertSales =
                insertInto("SALE")
                        .columns("ID", "DATE", "TOTAL", "STATUS", "CUSTOMER_VAT")
                        .values(1, new GregorianCalendar(2018, Calendar.JANUARY, 2), 0.0, 'O', VAT)
                        .values(2, new GregorianCalendar(2017, Calendar.MARCH, 25), 0.0, 'O', VAT)
                        .build();

        INSERT_CUSTOMER_SALE_DATA = sequenceOf(insertCustomer, insertSales);

        INSERT_MULTIPLE_CUSTOMERS =
                insertInto("CUSTOMER")
                        .columns("DESIGNATION", "PHONENUMBER", "VATNUMBER")
                        .values("name 1", 808789451, 290187915)
                        .values("name 2", 808789452, 209590467)
                        .values("name 3", 808789453, 249036584)
                        .values("name 4", 808789454, 227735587)
                        .values("name 5", 808789455, 215540972)
                        .build();

        Insert insertAddresses =
                insertInto("ADDRESS")
                        .columns("ID", "ADDRESS", "CUSTOMER_VAT")
                        .values(1, "FCUL, Campo Grande, Lisboa", VAT)
                        .values(2, "R. 25 de Abril, 101A, Porto", VAT)
                        .values(3, "Av Neil Armstrong, Cratera Azul, Lua", VAT)
                        .build();

        Insert insertSalesDelivery =
                insertInto("SALEDELIVERY")
                        .columns("SALE_ID", "CUSTOMER_VAT", "ADDRESS_ID")
                        .values(1, VAT, 1)
                        .values(2, VAT, 2)
                        .build();

        NUM_INIT_SALESDELIVERY = insertSalesDelivery.getRowCount();

        INSERT_CUSTOMER_SALE_ADDRESS_DELIVERYSALE_DATA = sequenceOf(insertCustomer, insertSales, insertAddresses, insertSalesDelivery);
    }

}

