package bgjug.hol.junit5.migration.eticket;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

class TicketTest {

    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final LocalDateTime AN_HOUR_AGO = NOW.minusHours(1);
    private static final LocalDateTime FUTURE = NOW.plusDays(1);
    private static final LocalDateTime TWO_YEAR_OLD = NOW.minusYears(2);
    private static final LocalDateTime MORE_THEN_TWO_YEARS = NOW.minusYears(2).minusSeconds(1);

    private static final boolean IS_VALID = Boolean.TRUE;

    static Stream<Arguments> ticketsTestDataProvider() {
        return Stream.of(
                Arguments.of("Ticket bought an hour ago is valid", AN_HOUR_AGO, IS_VALID),
                Arguments.of("Ticket bought in the future cannot be valid", FUTURE, !IS_VALID),
                Arguments.of("Ticket bought in more than two years ago is not valid anymore", MORE_THEN_TWO_YEARS, !IS_VALID),
                Arguments.of("Ticket exactly two years ago is still (barely) valid", TWO_YEAR_OLD, IS_VALID)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(value = "ticketsTestDataProvider")
    void ticketAreNotValid(String label, LocalDateTime time, boolean isValid) {
        Ticket ticket = new Ticket("code", time);

        assertEquals(isValid, !ticket.isNotValid(NOW));
    }
}
