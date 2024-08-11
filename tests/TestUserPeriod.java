import org.junit.Test;
import org.junit.Assert;
import user.UserPeriod;


public class TestUserPeriod {

    @Test
    public void testGetters() {
        UserPeriod period = new UserPeriod("name", "status", "teacher", "room");

        Assert.assertEquals(period.getName(), "name");
        Assert.assertEquals(period.getStatus(), "status");
        Assert.assertEquals(period.getTeacher(), "teacher");
        Assert.assertEquals(period.getRoom(), "room");
        Assert.assertFalse(period.isFree());
    }

    @Test
    public void testFreePeriod() {
        for (String status : new String[] {"Free", "free", "None", "none", "N/A", "n/a"}) {
            UserPeriod period = new UserPeriod("name", status, "teacher", "room");
            Assert.assertTrue(period.isFree());
        }
    }

}
