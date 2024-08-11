import org.junit.Test;
import org.junit.Assert;
import time.Duration;


public class TestDuration {

    @Test
    public void testConstants() {
        Assert.assertEquals(Duration.DAYS_PER_YEAR, 365);
        Assert.assertEquals(Duration.DAYS_PER_WEEK, 7);
        Assert.assertEquals(Duration.HOURS_PER_DAY, 24);
        Assert.assertEquals(Duration.MINUTES_PER_HOUR, 60);
        Assert.assertEquals(Duration.SECONDS_PER_MINUTE, 60);
        Assert.assertEquals(Duration.MS_PER_SECOND, 1000);
        Assert.assertEquals(Duration.MS_PER_MINUTE, 60000);
        Assert.assertEquals(Duration.MS_PER_HOUR, 3600000);
    }

}
