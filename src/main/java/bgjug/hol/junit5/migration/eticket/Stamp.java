package bgjug.hol.junit5.migration.eticket;

import java.time.LocalDateTime;

public class Stamp {

    private final String ticketCode;
    private final LocalDateTime stampedAt;

    public Stamp(String ticketCode, LocalDateTime stampedAt) {
        this.ticketCode = ticketCode;
        this.stampedAt = stampedAt;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public LocalDateTime getStampedAt() {
        return stampedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stamp stamp = (Stamp) o;

        if (ticketCode != null ? !ticketCode.equals(stamp.ticketCode) : stamp.ticketCode != null) return false;
        return stampedAt != null ? stampedAt.equals(stamp.stampedAt) : stamp.stampedAt == null;
    }

    @Override
    public int hashCode() {
        int result = ticketCode != null ? ticketCode.hashCode() : 0;
        result = 31 * result + (stampedAt != null ? stampedAt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Stamp{" +
                "ticketCode='" + ticketCode + '\'' +
                ", stampedAt=" + stampedAt +
                '}';
    }
}
