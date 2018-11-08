package bgjug.hol.junit5.migration.eticket.slow;

import org.junit.Test;

public class AnotherSlowIntegrationTest {

    @Test
    public void slowTestTwo() throws InterruptedException {
        Thread.sleep(2000);
    }
}
