import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileNotFoundException;
import org.junit.Test;
import org.junit.Assert;
import time.UTCTime;
import school.SchoolAPI;
import school.SchoolPeriod;


public class TestSchoolAPI {

    public static final Path TEST_SCHOOL_FILE = Paths.get("tests/json/TestSchool.json");

    @Test
    public void testWalkYear() throws FileNotFoundException {
        SchoolAPI api = new SchoolAPI(TEST_SCHOOL_FILE);

        UTCTime time = UTCTime.of("1970-01-01", "Z");
        UTCTime end = UTCTime.of("1971-01-01", "Z");
        while (time.isBefore(end)) {
            SchoolPeriod period = api.getCurrentPeriod(time);
            Assert.assertNotNull(period);
            time = period.getEnd().plus(1, UTCTime.MILLISECONDS);
        }
    }

    @Test
    public void testGetNextPeriod() throws FileNotFoundException {
        SchoolAPI api = new SchoolAPI(TEST_SCHOOL_FILE);

        UTCTime time = UTCTime.of("1970-01-01", "Z");
        UTCTime end = UTCTime.of("1971-01-01", "Z").plus(-1, UTCTime.DAYS);
        while (time.isBefore(end)) {
            SchoolPeriod period = api.getCurrentPeriod(time);
            SchoolPeriod next = api.getNextPeriod(time);

            Assert.assertNotNull(period);
            Assert.assertNotNull(next);
            Assert.assertEquals(period.getEnd().plus(1, UTCTime.MILLISECONDS), next.getStart());

            time = period.getEnd().plus(1, UTCTime.MILLISECONDS);
        }
    }

    @Test
    public void testGetNextPeriodToday() throws FileNotFoundException {
        SchoolAPI api = new SchoolAPI(TEST_SCHOOL_FILE);

        UTCTime time = UTCTime.of("1970-01-01", "Z");
        UTCTime end = UTCTime.of("1971-01-01", "Z");
        while (time.isBefore(end)) {
            SchoolPeriod period = api.getCurrentPeriod(time);
            SchoolPeriod next = api.getNextPeriodToday(time);

            Assert.assertNotNull(period);
            if (!period.isLast()) {
                Assert.assertNotNull(next);
                Assert.assertEquals(period.getEnd().plus(1, UTCTime.MILLISECONDS), next.getStart());
                Assert.assertEquals(period.getEnd().toMidnight(), next.getStart().toMidnight());
            }
            else {
                Assert.assertNull(next);
            }

            time = period.getEnd().plus(1, UTCTime.MILLISECONDS);
        }
    }

    @Test
    public void testGetters() throws FileNotFoundException {
        SchoolAPI api = new SchoolAPI(TEST_SCHOOL_FILE);

        Assert.assertEquals(api.getTimezone(), "Z");
        Assert.assertEquals(api.getFirstPeriod(), 1);
        Assert.assertEquals(api.getLastPeriod(), 2);
    }

}
