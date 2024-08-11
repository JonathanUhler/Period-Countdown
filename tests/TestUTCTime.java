import java.time.DayOfWeek;
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
        for (int i = 0; i < TestMain.RANDOM_TEST_AMOUNT; i++) {
            int expectedYear = TestMain.RANDOM_DIS.nextInt(9000) + 1000;
            int expectedMonth = TestMain.RANDOM_DIS.nextInt(12) + 1;
            int expectedDay = TestMain.RANDOM_DIS.nextInt(28) + 1;

            UTCTime time = UTCTime.of(String.format("%04d", expectedYear) + "-" +
                                      String.format("%02d", expectedMonth) + "-" +
                                      String.format("%02d", expectedDay),
                                      "Z");

            Assert.assertNotNull(time);
            Assert.assertEquals(time.get(UTCTime.YEAR), expectedYear);
            Assert.assertEquals(time.get(UTCTime.MONTH), expectedMonth);
            Assert.assertEquals(time.get(UTCTime.DAY), expectedDay);
            Assert.assertEquals(time.get(UTCTime.HOUR), 0);
            Assert.assertEquals(time.get(UTCTime.MINUTE), 0);
            Assert.assertEquals(time.get(UTCTime.SECOND), 0);
            Assert.assertEquals(time.get(UTCTime.MILLISECOND), 0);
        }
    }

    @Test
    public void testOfWithFullDatetime() {
        for (int i = 0; i < TestMain.RANDOM_TEST_AMOUNT; i++) {
            int expectedYear = TestMain.RANDOM_DIS.nextInt(9000) + 1000;
            int expectedMonth = TestMain.RANDOM_DIS.nextInt(12) + 1;
            int expectedDay = TestMain.RANDOM_DIS.nextInt(28) + 1;
            int expectedHour = TestMain.RANDOM_DIS.nextInt(23) + 1;
            int expectedMinute = TestMain.RANDOM_DIS.nextInt(59) + 1;
            int expectedSecond = TestMain.RANDOM_DIS.nextInt(59) + 1;
            int expectedMillis = TestMain.RANDOM_DIS.nextInt(999) + 1;

            UTCTime time = UTCTime.of(String.format("%04d", expectedYear) + "-" +
                                      String.format("%02d", expectedMonth) + "-" +
                                      String.format("%02d", expectedDay) + "T" +
                                      String.format("%02d", expectedHour) + ":" +
                                      String.format("%02d", expectedMinute) + ":" +
                                      String.format("%02d", expectedSecond) + "." +
                                      String.format("%03d", expectedMillis),
                                      "Z");

            Assert.assertNotNull(time);
            Assert.assertEquals(time.get(UTCTime.YEAR), expectedYear);
            Assert.assertEquals(time.get(UTCTime.MONTH), expectedMonth);
            Assert.assertEquals(time.get(UTCTime.DAY), expectedDay);
            Assert.assertEquals(time.get(UTCTime.HOUR), expectedHour);
            Assert.assertEquals(time.get(UTCTime.MINUTE), expectedMinute);
            Assert.assertEquals(time.get(UTCTime.SECOND), expectedSecond);
            Assert.assertEquals(time.get(UTCTime.MILLISECOND), expectedMillis);
        }
    }

    @Test
    public void testOfWithOutOfRangeYear() {
        for (int year = -1000; year < 1000; year++) {
            String datetime = year + "-01-01";
            Assert.assertThrows(IllegalArgumentException.class, () -> UTCTime.of(datetime, "Z"));
        }
        for (int year = 10000; year < 11000; year++) {
            String datetime = year + "-01-01";
            Assert.assertThrows(IllegalArgumentException.class, () -> UTCTime.of(datetime, "Z"));
        }
    }

    @Test
    public void testOfWithOutOfRangeMonth() {
        for (int month : new int[] {-1, 0, 13}) {
            String datetime = "1970-" + month + "-01";
            Assert.assertThrows(IllegalArgumentException.class, () -> UTCTime.of(datetime, "Z"));
        }
    }

    @Test
    public void testOfWithOutOfRangeDay() {
        for (int day : new int[] {-1, 0, 32}) {
            String datetime = "1970-01-" + day;
            Assert.assertThrows(IllegalArgumentException.class, () -> UTCTime.of(datetime, "Z"));
        }
    }

    @Test
    public void testOfWithLeapDate() {
        UTCTime.of("2020-02-29", "Z");  // No error should be thrown
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
        Assert.assertEquals(time1, time2);
    }

    @Test
    public void testChronology() {
        for (int i = 0; i < TestMain.RANDOM_TEST_AMOUNT; i++) {
            UTCTime time1 = TestMain.randomTime();
            UTCTime time2 = TestMain.randomTime();

            if (time1.isBefore(time2)) {
                Assert.assertTrue(time1.compareTo(time2) < 0);
                Assert.assertTrue(time2.compareTo(time1) >= 0);
                Assert.assertTrue(time1.get(UTCTime.YEAR) < time2.get(UTCTime.YEAR) ||
                                  time1.get(UTCTime.MONTH) < time2.get(UTCTime.MONTH) ||
                                  time1.get(UTCTime.DAY) < time2.get(UTCTime.DAY) ||
                                  time1.get(UTCTime.HOUR) < time2.get(UTCTime.HOUR) ||
                                  time1.get(UTCTime.MINUTE) < time2.get(UTCTime.MINUTE) ||
                                  time1.get(UTCTime.SECOND) < time2.get(UTCTime.SECOND) ||
                                  time1.get(UTCTime.MILLISECOND) < time2.get(UTCTime.MILLISECOND));
            }
            else if (time1.equals(time2)) {
                Assert.assertTrue(time1.compareTo(time2) == 0);
                Assert.assertTrue(time2.compareTo(time1) == 0);
                Assert.assertTrue(time1.get(UTCTime.YEAR) == time2.get(UTCTime.YEAR) &&
                                  time1.get(UTCTime.MONTH) == time2.get(UTCTime.MONTH) &&
                                  time1.get(UTCTime.DAY) == time2.get(UTCTime.DAY) &&
                                  time1.get(UTCTime.HOUR) == time2.get(UTCTime.HOUR) &&
                                  time1.get(UTCTime.MINUTE) == time2.get(UTCTime.MINUTE) &&
                                  time1.get(UTCTime.SECOND) == time2.get(UTCTime.SECOND) &&
                                  time1.get(UTCTime.MILLISECOND) == time2.get(UTCTime.MILLISECOND));
            }
            else {
                Assert.assertTrue(time1.compareTo(time2) > 0);
                Assert.assertTrue(time2.compareTo(time1) < 0);
                Assert.assertTrue(time1.get(UTCTime.YEAR) > time2.get(UTCTime.YEAR) ||
                                  time1.get(UTCTime.MONTH) > time2.get(UTCTime.MONTH) ||
                                  time1.get(UTCTime.DAY) > time2.get(UTCTime.DAY) ||
                                  time1.get(UTCTime.HOUR) > time2.get(UTCTime.HOUR) ||
                                  time1.get(UTCTime.MINUTE) > time2.get(UTCTime.MINUTE) ||
                                  time1.get(UTCTime.SECOND) > time2.get(UTCTime.SECOND) ||
                                  time1.get(UTCTime.MILLISECOND) > time2.get(UTCTime.MILLISECOND));
            }
        }
    }

    @Test
    public void testAddTime() {
        for (int i = 0; i < TestMain.RANDOM_TEST_AMOUNT; i++) {
            UTCTime time = TestMain.randomTime();
            int n = TestMain.RANDOM_DIS.nextInt(1000);
            Assert.assertEquals(time.plus(n, UTCTime.YEARS).plus(-n, UTCTime.YEARS), time);
            Assert.assertEquals(time.plus(n, UTCTime.MONTHS).plus(-n, UTCTime.MONTHS), time);
            Assert.assertEquals(time.plus(n, UTCTime.DAYS).plus(-n, UTCTime.DAYS), time);
            Assert.assertEquals(time.plus(n, UTCTime.HOURS).plus(-n, UTCTime.HOURS), time);
            Assert.assertEquals(time.plus(n, UTCTime.SECONDS).plus(-n, UTCTime.SECONDS), time);
            Assert.assertEquals(time.plus(n, UTCTime.MILLISECONDS).plus(-n, UTCTime.MILLISECONDS),
                                time);
        }
    }

    @Test
    public void testShiftedTo() {
        DayOfWeek[] daysOfWeek = new DayOfWeek[] {UTCTime.SUNDAY,
                                                  UTCTime.MONDAY,
                                                  UTCTime.TUESDAY,
                                                  UTCTime.WEDNESDAY,
                                                  UTCTime.THURSDAY,
                                                  UTCTime.FRIDAY,
                                                  UTCTime.SATURDAY};
        for (int i = 0; i < TestMain.RANDOM_TEST_AMOUNT; i++) {
            UTCTime time = TestMain.randomTime();
            for (DayOfWeek dayOfWeek : daysOfWeek) {
                UTCTime prev = time.shiftedToPrevious(dayOfWeek);
                UTCTime next = time.shiftedToNext(dayOfWeek);
                Assert.assertEquals(prev.get(UTCTime.DAY_OF_WEEK), dayOfWeek.getValue());
                Assert.assertEquals(next.get(UTCTime.DAY_OF_WEEK), dayOfWeek.getValue());
                Assert.assertTrue(prev.compareTo(time) <= 0);
                Assert.assertTrue(next.compareTo(time) >= 0);
            }
        }
    }

    @Test
    public void testToMidnight() {
        for (int i = 0; i < TestMain.RANDOM_TEST_AMOUNT; i++) {
            UTCTime time = TestMain.randomTime();
            UTCTime midnight = time.toMidnight();
            Assert.assertEquals(midnight.get(UTCTime.YEAR), time.get(UTCTime.YEAR));
            Assert.assertEquals(midnight.get(UTCTime.MONTH), time.get(UTCTime.MONTH));
            Assert.assertEquals(midnight.get(UTCTime.DAY), time.get(UTCTime.DAY));
            Assert.assertEquals(midnight.get(UTCTime.HOUR), 0);
            Assert.assertEquals(midnight.get(UTCTime.MINUTE), 0);
            Assert.assertEquals(midnight.get(UTCTime.SECOND), 0);
            Assert.assertEquals(midnight.get(UTCTime.MILLISECOND), 0);
        }
    }

    @Test
    public void testGettingTags() {
        for (int i = 0; i < TestMain.RANDOM_TEST_AMOUNT; i++) {
            int expectedYear = TestMain.RANDOM_DIS.nextInt(9000) + 1000;
            int expectedMonth = TestMain.RANDOM_DIS.nextInt(12) + 1;
            int expectedDay = TestMain.RANDOM_DIS.nextInt(28) + 1;

            String expectedDayTag = String.format("%04d", expectedYear) + "-" +
                String.format("%02d", expectedMonth) + "-" +
                String.format("%02d", expectedDay);

            UTCTime time = UTCTime.of(expectedDayTag, "Z");
            Assert.assertEquals(time.getDayTag(), expectedDayTag);
        }
    }

}
