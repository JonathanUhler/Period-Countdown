import org.junit.Test;
import org.junit.Assert;
import time.UTCTime;
import school.SchoolPeriod;
import school.SchoolJson;


public class TestSchoolPeriod {

    @Test
    public void testGetters() {
        for (String type : new String[] {SchoolJson.NOTHING, SchoolJson.SPECIAL, "0", "1", "2"}) {
            UTCTime time1 = TestMain.randomTime();
            UTCTime time2 = TestMain.randomTime();
            UTCTime start = time1.isBefore(time2) ? time1 : time2;
            UTCTime end = time1.isBefore(time2) ? time2 : time1;
            SchoolPeriod period = new SchoolPeriod(type, "name", start, end, false);

            Assert.assertEquals(period.getType(), type);
            Assert.assertEquals(period.getName(), "name");
            Assert.assertEquals(period.getStart(), start);
            Assert.assertEquals(period.getEnd(), end);
            Assert.assertFalse(period.isLast());
        }
    }

    @Test
    public void testSpecialTypes() {
        UTCTime start = TestMain.randomTime();
        UTCTime end = start.plus(1, UTCTime.HOURS);

        SchoolPeriod nothing = new SchoolPeriod(SchoolJson.NOTHING, "name", start, end, false);
        Assert.assertFalse(nothing.isCounted());
        Assert.assertTrue(nothing.isFree());

        SchoolPeriod special = new SchoolPeriod(SchoolJson.SPECIAL, "name", start, end, false);
        Assert.assertTrue(special.isCounted());
        Assert.assertTrue(special.isFree());

        SchoolPeriod period = new SchoolPeriod("1", "name", start, end, false);
        Assert.assertTrue(period.isCounted());
        Assert.assertFalse(period.isFree());
    }

    @Test
    public void testInvalidTimes() {
        UTCTime start = TestMain.randomTime();
        UTCTime end = start.plus(1, UTCTime.HOURS);
        Assert.assertThrows(IllegalArgumentException.class,
                            () -> new SchoolPeriod("1", "name", end, start, false));
    }

    @Test
    public void testInvalidType() {
        UTCTime start = TestMain.randomTime();
        UTCTime end = start.plus(1, UTCTime.HOURS);
        Assert.assertThrows(IllegalArgumentException.class,
                            () -> new SchoolPeriod("INVALID", "name", start, end, false));
    }

}
