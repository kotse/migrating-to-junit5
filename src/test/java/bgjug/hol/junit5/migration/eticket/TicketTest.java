package bgjug.hol.junit5.migration.eticket;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class TicketTest {

    private static final String VALID_TICKET_CODE = "valid_ticket_code";
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final LocalDateTime AN_HOUR_AGO = NOW.minusHours(1);
    private static final LocalDateTime FUTURE = NOW.plusDays(1);
    private static final LocalDateTime TWO_YEAR_OLD = NOW.minusYears(2);
    private static final LocalDateTime MORE_THEN_TWO_YEARS = NOW.minusYears(2).minusSeconds(1);

    //TODO: write tests for valid and invalid ticket codes
    // a valid ticket code is one that is not an empty string and is at least 8 characters long

    @Test
    public void recentlyBoughtTicket_isValid() {
        Ticket ticket = new Ticket(VALID_TICKET_CODE, AN_HOUR_AGO);

        assertEquals(false, ticket.isNotValid(NOW));
    }

    @Test
    public void futureTicket_isNotValid() {
        Ticket ticket = new Ticket(VALID_TICKET_CODE, FUTURE);

        assertEquals("ticket in the future cannot be valid", true, ticket.isNotValid(NOW));
    }

    @Test
    public void moreThanTwoYears_isNotValid() {
        Ticket ticket = new Ticket(VALID_TICKET_CODE, MORE_THEN_TWO_YEARS);

        assertEquals("too old ticket cannot be valid", true, ticket.isNotValid(NOW));
    }

    @Test
    public void twoYearOldOldTicket_isValid() {
        Ticket ticket = new Ticket(VALID_TICKET_CODE, TWO_YEAR_OLD);

        assertEquals( false, ticket.isNotValid(NOW));
    }
}
