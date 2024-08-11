import org.junit.Test;
import org.junit.Assert;
import time.UTCTime;


public class TestUTCTime {

    @Test
    public void testNow() {
        Assert.assertNotNull(UTCTime.now());
    }

    @Test
    public void testOfWithPartialDatetime() {
        UTCTime time = UTCTime.of("1970-01-01", "America/New_York");
        Assert.assertNotNull(time);
        Assert.assertEquals(time.get(UTCTime.YEAR), 1970);
        Assert.assertEquals(time.get(UTCTime.MONTH), 1);
        Assert.assertEquals(time.get(UTCTime.DAY), 1);
        Assert.assertEquals(time.get(UTCTime.DAY_OF_WEEK), 4);
        Assert.assertEquals(time.get(UTCTime.HOUR), 5);
        Assert.assertEquals(time.get(UTCTime.MINUTE), 0);
        Assert.assertEquals(time.get(UTCTime.SECOND), 0);
        Assert.assertEquals(time.get(UTCTime.MILLISECOND), 0);
    }

    @Test
    public void testOfWithFullDatetime() {
        UTCTime time = UTCTime.of("1970-01-01T05:37:23.947", "America/New_York");
        Assert.assertNotNull(time);
        Assert.assertEquals(time.get(UTCTime.YEAR), 1970);
        Assert.assertEquals(time.get(UTCTime.MONTH), 1);
        Assert.assertEquals(time.get(UTCTime.DAY), 1);
        Assert.assertEquals(time.get(UTCTime.DAY_OF_WEEK), 4);
        Assert.assertEquals(time.get(UTCTime.HOUR), 10);
        Assert.assertEquals(time.get(UTCTime.MINUTE), 37);
        Assert.assertEquals(time.get(UTCTime.SECOND), 23);
        Assert.assertEquals(time.get(UTCTime.MILLISECOND), 947);
    }

    @Test
    public void testOfWithNullArguments() {
        Assert.assertThrows(NullPointerException.class, () -> UTCTime.of(null, "America/New_York"));
        Assert.assertThrows(NullPointerException.class, () -> UTCTime.of("1970-01-01", null));
        Assert.assertThrows(NullPointerException.class, () -> UTCTime.of(null, null));
    }

    @Test
    public void testOfWithInvalidDatetime() {
        Assert.assertThrows(IllegalArgumentException.class,
                            () -> UTCTime.of("invalid datetime string", "America/New_York"));
        Assert.assertThrows(IllegalArgumentException.class,
                            () -> UTCTime.of("1970-01-01Tinvalid time", "America/New_York"));
        Assert.assertThrows(IllegalArgumentException.class,
                            () -> UTCTime.of("1/1/1970", "America/New_York"));
    }

    @Test
    public void testOfWithInvalidTimezone() {
        Assert.assertThrows(IllegalArgumentException.class,
                            () -> UTCTime.of("1970-01-01", "invalid"));
        Assert.assertThrows(IllegalArgumentException.class,
                            () -> UTCTime.of("1970-01-01", "america/new_york"));
        Assert.assertThrows(IllegalArgumentException.class,
                            () -> UTCTime.of("1970-01-01", "America/New-York"));
    }

    @Test
    public void testTo() {
        UTCTime time1 = UTCTime.now();
        UTCTime time2 = time1.to("America/New_York");
        Assert.assertTrue(time1.compareTo(time2) == 0);
        Assert.assertNotEquals(time1, time2);
    }

}
