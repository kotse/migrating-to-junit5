package bgjug.hol.junit5.migration.eticket;

import org.junit.*;

import java.time.LocalDateTime;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class StamperRepoTest {

    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final LocalDateTime HOUR_AGO = NOW.minusHours(1);
    private static final LocalDateTime MORE_THAN_AN_HOUR_AGO = HOUR_AGO.minusSeconds(1);

    private static final Ticket TICKET = new Ticket("codeForTicket", NOW.minusHours(1));

    private StamperRepo stamperRepo;

    @BeforeClass
    public static void setUpClass() throws Exception {
        // not used
    }

    @Before
    public void setUp() throws Exception {
        stamperRepo = new StamperRepo();
    }

    @After
    public void tearDown() throws Exception {
        // not used
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        // not used
    }

    @Ignore
    @Test(expected = DuplicateStampException.class)
    public void store_whenDuplicateTicket_shouldNotStore() {
        Stamp firstStamp = new Stamp(TICKET.getTicketCode(), HOUR_AGO);
        Stamp secondStamp = new Stamp(TICKET.getTicketCode(), NOW);

        stamperRepo.store(firstStamp);
        stamperRepo.store(secondStamp);
    }

    @Test
    public void stampExistsFor_whenNoTicket_shouldReturnFalse() {
        boolean stampExists = stamperRepo.stampExistsFor(TICKET, NOW);

        assertFalse(stampExists);
    }

    @Test
    public void stampExistsFor_whenRecentlyStampedTicket_shouldReturnTrue() {
        Stamp validStamp = new Stamp(TICKET.getTicketCode(), NOW);

        stamperRepo.store(validStamp);

        boolean stampExists = stamperRepo.stampExistsFor(TICKET, NOW);

        assertTrue(stampExists);
    }

    @Test
    public void stampExistsFor_whenOneHourSinceStampedTicket_shouldReturnTrue() {
        Stamp almostExpiredStamp = new Stamp(TICKET.getTicketCode(), HOUR_AGO);

        stamperRepo.store(almostExpiredStamp);

        boolean stampExists = stamperRepo.stampExistsFor(TICKET, NOW);

        assertTrue(stampExists);
    }

    @Test
    public void stampExistsFor_whenMoreThanAnHourAgoSinceStampedTicket_shouldReturnFalse() {
        Stamp expiredStamp = new Stamp(TICKET.getTicketCode(), MORE_THAN_AN_HOUR_AGO);

        stamperRepo.store(expiredStamp);

        boolean stampExists = stamperRepo.stampExistsFor(TICKET, NOW);

        assertFalse(stampExists);
    }
}
