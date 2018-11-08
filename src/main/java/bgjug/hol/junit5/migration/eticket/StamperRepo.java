package bgjug.hol.junit5.migration.eticket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StamperRepo {

    private List<Stamp> stamps = new ArrayList<>();

    public void store(Stamp stamp) {

        if (duplicate(stamp)){
            throw new DuplicateStampException("duplicate stamp with code " + stamp.getTicketCode());
        }


        stamps.add(stamp);
    }

    private boolean duplicate(Stamp stamp) {
        return stamps.stream()
                .anyMatch(s -> s.getTicketCode().equals(stamp.getTicketCode()));
    }

    public boolean stampExistsFor(Ticket ticket, LocalDateTime time) {
        return stamps.stream()
                .filter(s -> s.getTicketCode().equals(ticket.getTicketCode()))
                .anyMatch(s -> !s.getStampedAt().isBefore(time.minusHours(1)));
    }
}
