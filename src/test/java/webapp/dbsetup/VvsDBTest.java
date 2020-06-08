package webapp.dbsetup;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.Destination;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import org.junit.Before;
import org.junit.BeforeClass;

import static webapp.utils.DBSetupUtils.*;

public abstract class VvsDBTest {

    private static Destination dataSource;

    // the tracker is static because JUnit uses a separate Test instance for every test method.
    private static final DbSetupTracker dbSetupTracker = new DbSetupTracker();

    @BeforeClass
    public static void setupClass() {
        startApplicationDatabaseForTesting();
        dataSource = DriverManagerDestination.with(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    @Before
    public void setup() {
        DbSetup dbSetup = new DbSetup(dataSource, initDBOperations());
        // Use the tracker to launch the DbSetup. This will speed-up tests
        // that do not not change the BD. Otherwise, just use dbSetup.launch();
        dbSetupTracker.launchIfNecessary(dbSetup);
    }

    public abstract Operation initDBOperations();
}
