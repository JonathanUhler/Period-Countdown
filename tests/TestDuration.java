import org.junit.Test;
import org.junit.Assert;
import time.Duration;
import time.UTCTime;


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

    @Test
    public void testConstructorWithValues() {
        for (int i = 0; i < TestMain.RANDOM_TEST_AMOUNT; i++) {
            int expectedHours = Math.abs(TestMain.RANDOM_DIS.nextInt());
            int expectedMinutes = TestMain.RANDOM_DIS.nextInt(Duration.MINUTES_PER_HOUR);
            int expectedSeconds = TestMain.RANDOM_DIS.nextInt(Duration.SECONDS_PER_MINUTE);
            int expectedMillis = TestMain.RANDOM_DIS.nextInt(Duration.MS_PER_SECOND);

            Duration duration = new Duration(expectedHours,
                                             expectedMinutes,
                                             expectedSeconds,
                                             expectedMillis);

            Assert.assertNull(duration.getStart());
            Assert.assertNull(duration.getEnd());
            Assert.assertEquals(duration.hr(), expectedHours);
            Assert.assertEquals(duration.min(), expectedMinutes);
            Assert.assertEquals(duration.sec(), expectedSeconds);
            Assert.assertEquals(duration.ms(), expectedMillis);
        }
    }

    @Test
    public void testConstructorWithInvalidValues() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new Duration(-1, 0, 0, 0));
        Assert.assertThrows(IllegalArgumentException.class, () -> new Duration(0, -1, 0, 0));
        Assert.assertThrows(IllegalArgumentException.class, () -> new Duration(0, 0, -1, 0));
        Assert.assertThrows(IllegalArgumentException.class, () -> new Duration(0, 0, 0, -1));
        Assert.assertThrows(IllegalArgumentException.class, () -> new Duration(0, 60, 0, 0));
        Assert.assertThrows(IllegalArgumentException.class, () -> new Duration(0, 0, 60, 0));
        Assert.assertThrows(IllegalArgumentException.class, () -> new Duration(0, 0, 0, 1000));
    }

    @Test
    public void testConstructorWithTimeRange() {
        for (int i = 0; i < TestMain.RANDOM_TEST_AMOUNT; i++) {
            int expectedHours = Math.abs(TestMain.RANDOM_DIS.nextInt());
            int expectedMinutes = TestMain.RANDOM_DIS.nextInt(Duration.MINUTES_PER_HOUR);
            int expectedSeconds = TestMain.RANDOM_DIS.nextInt(Duration.SECONDS_PER_MINUTE);
            int expectedMillis = TestMain.RANDOM_DIS.nextInt(Duration.MS_PER_SECOND);

            UTCTime start = UTCTime.now();
            UTCTime end = start
                .plus(expectedHours, UTCTime.HOURS)
                .plus(expectedMinutes, UTCTime.MINUTES)
                .plus(expectedSeconds, UTCTime.SECONDS)
                .plus(expectedMillis, UTCTime.MILLISECONDS);

            Duration duration = new Duration(start, end);

            Assert.assertEquals(duration.getStart(), start);
            Assert.assertEquals(duration.getEnd(), end);
            Assert.assertEquals(duration.hr(), expectedHours);
            Assert.assertEquals(duration.min(), expectedMinutes);
            Assert.assertEquals(duration.sec(), expectedSeconds);
            Assert.assertEquals(duration.ms(), expectedMillis);
        }
    }

    @Test
    public void testConstructorWithNullRange() {
        Assert.assertThrows(NullPointerException.class, () -> new Duration(null, UTCTime.now()));
        Assert.assertThrows(NullPointerException.class, () -> new Duration(UTCTime.now(), null));
        Assert.assertThrows(NullPointerException.class, () -> new Duration(null, null));
    }

    @Test
    public void testConstructorWithInvalidRange() {
        UTCTime time = UTCTime.now();
        Assert.assertThrows(IllegalArgumentException.class,
                            () -> new Duration(time, time.plus(-1, UTCTime.DAYS)));
    }

}
