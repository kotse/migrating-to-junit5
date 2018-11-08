package bgjug.hol.junit5.migration.eticket;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
public class StamperTest {

    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final LocalDateTime AN_HOUR_AGO = NOW.minusHours(1);
    private static final LocalDateTime TEN_YEARS_AGO = NOW.minusYears(10);

    private static final Ticket VALID_TICKET = new Ticket("ticketCode", AN_HOUR_AGO);
    private static final Ticket NOT_VALID_TICKET = new Ticket("ticketCode", TEN_YEARS_AGO);

    private Stamper stamper;

    @Mock
    private StamperRepo stamperRepo;

    @Before
    public void setUp() throws Exception {
        stamper = new Stamper(stamperRepo);
    }

    @Test
    public void whenValidTicket_shouldStampIt() {
        BDDMockito.given(stamperRepo.stampExistsFor(VALID_TICKET, NOW)).willReturn(false);

        Optional<Stamp> actual = stamper.stamp(VALID_TICKET, NOW);
        Stamp expected = new Stamp(VALID_TICKET.getTicketCode(), NOW);

        assertEquals(expected, actual.get());
    }

    @Test
    public void whenValidTicket_shouldStoreIt() {
        BDDMockito.given(stamperRepo.stampExistsFor(VALID_TICKET, NOW)).willReturn(false);

        Optional<Stamp> stamp = stamper.stamp(VALID_TICKET, NOW);

        BDDMockito.then(stamperRepo).should().store(stamp.get());
    }

    @Test
    public void whenNotValidTicket_shouldNotStampAndStore() {
        Optional<Stamp> stamp = stamper.stamp(NOT_VALID_TICKET, NOW);

        assertEquals(Optional.empty(), stamp);
        BDDMockito.then(stamperRepo).should(never()).store(any(Stamp.class));
    }

    @Test
    public void whenAlreadyStamped_shouldNotStampAgain() {
        BDDMockito.given(stamperRepo.stampExistsFor(VALID_TICKET, NOW)).willReturn(true);

        Optional<Stamp> stamp = stamper.stamp(VALID_TICKET, NOW);

        assertEquals(Optional.empty(), stamp);
        BDDMockito.then(stamperRepo).should(never()).store(any(Stamp.class));
    }
}
