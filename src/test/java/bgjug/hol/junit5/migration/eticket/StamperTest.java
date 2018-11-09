package bgjug.hol.junit5.migration.eticket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class StamperTest {

    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final LocalDateTime AN_HOUR_AGO = NOW.minusHours(1);
    private static final LocalDateTime TEN_YEARS_AGO = NOW.minusYears(10);

    private static final Ticket VALID_TICKET = new Ticket("ticketCode", AN_HOUR_AGO);
    private static final Ticket NOT_VALID_TICKET = new Ticket("ticketCode", TEN_YEARS_AGO);

    private Stamper stamper;

    @Mock
    private StamperRepo stamperRepo;

    @BeforeEach
    void setUp() throws Exception {
        stamper = new Stamper(stamperRepo);
    }

    @Nested
    class whenValidTicket {

        @BeforeEach
        void setUp() {
            BDDMockito.given(stamperRepo.stampExistsFor(VALID_TICKET, NOW)).willReturn(false);
        }

        @Test
        void shouldStampIt() {
            Optional<Stamp> actual = stamper.stamp(VALID_TICKET, NOW);
            Stamp expected = new Stamp(VALID_TICKET.getTicketCode(), NOW);

            assertEquals(expected, actual.get());
        }

        @Test
        void shouldStoreIt() {
            Optional<Stamp> stamp = stamper.stamp(VALID_TICKET, NOW);

            BDDMockito.then(stamperRepo).should().store(stamp.get());
        }
    }

    @Test
    void whenNotValidTicket_shouldNotStampAndStore() {
        Optional<Stamp> stamp = stamper.stamp(NOT_VALID_TICKET, NOW);

        assertEquals(Optional.empty(), stamp);
        BDDMockito.then(stamperRepo).should(never()).store(any(Stamp.class));
    }

    @Test
    void whenAlreadyStamped_shouldNotStampAgain() {
        BDDMockito.given(stamperRepo.stampExistsFor(VALID_TICKET, NOW)).willReturn(true);

        Optional<Stamp> stamp = stamper.stamp(VALID_TICKET, NOW);

        assertEquals(Optional.empty(), stamp);
        BDDMockito.then(stamperRepo).should(never()).store(any(Stamp.class));
    }
}
