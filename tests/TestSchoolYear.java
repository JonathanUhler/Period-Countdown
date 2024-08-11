import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileNotFoundException;
import org.junit.Test;
import org.junit.Assert;
import time.UTCTime;
import school.SchoolYear;
import school.SchoolPeriod;


public class TestSchoolYear {

    public static final Path TEST_SCHOOL_FILE = Paths.get("tests/json/TestSchool.json");

    @Test
    public void testWalkYear() throws FileNotFoundException {
        SchoolYear year = new SchoolYear(TEST_SCHOOL_FILE);

        UTCTime time = UTCTime.of("1970-01-01", "Z");
        UTCTime end = UTCTime.of("1971-01-01", "Z");
        while (time.isBefore(end)) {
            SchoolPeriod period = year.getPeriod(time);
            Assert.assertNotNull(period);
            time = period.getEnd().plus(1, UTCTime.MILLISECONDS);
        }
    }

    @Test
    public void testGetPeriod() throws FileNotFoundException {
        SchoolYear year = new SchoolYear(TEST_SCHOOL_FILE);

        UTCTime time;

        time = UTCTime.of("1970-01-05", "Z");
        Assert.assertEquals(year.getPeriod(time).getName(), "HolidayDay");

        time = UTCTime.of("1970-01-12", "Z");
        Assert.assertEquals(year.getPeriod(time).getName(), "HolidayDay");

        time = UTCTime.of("1970-01-19", "Z");
        Assert.assertEquals(year.getPeriod(time).getName(), "BeforeClass");
    }

    @Test
    public void testGetters() throws FileNotFoundException {
        SchoolYear year = new SchoolYear(TEST_SCHOOL_FILE);

        Assert.assertEquals(year.getTimezone(), "Z");
        Assert.assertEquals(year.getFirstPeriod(), 1);
        Assert.assertEquals(year.getLastPeriod(), 2);
    }

    @Test
    public void testDiscontinuity() {
        Assert.assertThrows(IllegalArgumentException.class,
                            () -> new SchoolYear(Paths.get("tests/json/TestDiscontinuity.json")));
    }

    @Test
    public void testMissingParameters() {
        for (String p : new String[] {"tests/json/TestMissingFirstPeriod.json",
                                      "tests/json/TestMissingLastPeriod.json",
                                      "tests/json/TestMissingFirstDayTag.json",
                                      "tests/json/TestMissingLastDayTag.json",
                                      "tests/json/TestMissingTimezone.json",
                                      "tests/json/TestMissingDays.json",
                                      "tests/json/TestMissingInfo.json",
                                      "tests/json/TestMissingWeeks.json",
                                      "tests/json/TestMissingExceptions.json",
                                      "tests/json/TestMissingDefaultWeek.json"})
        {
            Assert.assertThrows(IllegalArgumentException.class, () -> new SchoolYear(Paths.get(p)));
        }
    }

    @Test
    public void testIllegalParameters() {
        for (String p : new String[] {"tests/json/TestInvalidFirstPeriod.json",
                                      "tests/json/TestInvalidLastPeriod.json",
                                      "tests/json/TestInvalidPeriodRange.json",
                                      "tests/json/TestInvalidFirstDayTag.json",
                                      "tests/json/TestInvalidLastDayTag.json",
                                      "tests/json/TestInvalidTimezone.json"})
        {
            Assert.assertThrows(IllegalArgumentException.class, () -> new SchoolYear(Paths.get(p)));
        }
    }

    @Test
    public void testInvalidPathOnDisk() {
        Assert.assertThrows(FileNotFoundException.class,
                            () -> new SchoolYear(Paths.get("/Invalid/Path")));
    }

    @Test
    public void testInvalidPathInJar() {
        Assert.assertThrows(FileNotFoundException.class,
                            () -> new SchoolYear(Paths.get("assets/json/user/invalid json")));
    }

    @Test
    public void testEmptyJsonFile() {
        Assert.assertThrows(IllegalArgumentException.class,
                            () -> new SchoolYear(Paths.get("tests/json/TestEmptySchool.json")));
    }

    @Test
    public void testInvalidJsonFile() {
        Assert.assertThrows(IllegalArgumentException.class,
                            () -> new SchoolYear(Paths.get("tests/json/TestInvalidSchool.json")));
    }

}
