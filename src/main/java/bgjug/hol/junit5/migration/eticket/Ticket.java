package bgjug.hol.junit5.migration.eticket;

import java.time.LocalDateTime;

public class Ticket {

    private final String ticketCode;

    private final LocalDateTime issuedAt;

    public Ticket(String ticketCode, LocalDateTime issuedAt) {
        this.ticketCode = ticketCode;
        this.issuedAt = issuedAt;

        checkTicketCode(ticketCode);
    }

    private void checkTicketCode(String ticketCode) {
        if (ticketCode == null || ticketCode.isEmpty()) {
            throw new InvalidTickeException("ticket code should not be null or empty");
        }

        if (ticketCode.length() < 8) {
            throw new InvalidTickeException("ticket code should be at least 8 characters long");
        }
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public boolean isNotValid(LocalDateTime stampedAt) {
        return issuedAt.isBefore(stampedAt.minusYears(2))
                || issuedAt.isAfter(stampedAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ticket ticket = (Ticket) o;

        if (ticketCode != null ? !ticketCode.equals(ticket.ticketCode) : ticket.ticketCode != null) return false;
        return issuedAt != null ? issuedAt.equals(ticket.issuedAt) : ticket.issuedAt == null;
    }

    @Override
    public int hashCode() {
        int result = ticketCode != null ? ticketCode.hashCode() : 0;
        result = 31 * result + (issuedAt != null ? issuedAt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketCode='" + ticketCode + '\'' +
                ", issuedAt=" + issuedAt +
                '}';
    }
}
