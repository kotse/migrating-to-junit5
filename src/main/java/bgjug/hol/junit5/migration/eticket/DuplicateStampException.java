package bgjug.hol.junit5.migration.eticket;

public class DuplicateStampException extends RuntimeException {

    public DuplicateStampException(String message) {
        super(message);
    }
}
