package bgjug.hol.junit5.migration.eticket;

import java.time.LocalDateTime;
import java.util.Optional;

public class Stamper {

    private StamperRepo stamperRepo;

    public Stamper(StamperRepo stamperRepo) {

        this.stamperRepo = stamperRepo;
    }

    public Optional<Stamp> stamp(Ticket ticket, LocalDateTime stampedAt) {
        if (ticket.isNotValid(stampedAt)) {
            return Optional.empty();
        }

        if (stamperRepo.stampExistsFor(ticket, stampedAt)) {
            return Optional.empty();
        }

        Stamp stamp = new Stamp(ticket.getTicketCode(), stampedAt);

        stamperRepo.store(stamp);

        return Optional.of(stamp);
    }
}
