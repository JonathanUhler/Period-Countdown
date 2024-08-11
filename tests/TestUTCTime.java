import org.junit.Test;
import org.junit.Assert;
import time.UTCTime;


public class TestUTCTime {

    @Test
    public void testNow() {
        Assert.assertNotNull(UTCTime.now());
    }

}
