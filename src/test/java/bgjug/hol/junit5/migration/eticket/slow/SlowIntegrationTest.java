package bgjug.hol.junit5.migration.eticket.slow;

import org.junit.Test;

public class SlowIntegrationTest {

    @Test
    public void slowTestOne() throws InterruptedException {
        Thread.sleep(2000);
    }
}
