import org.junit.Test;
import org.junit.Assert;
import time.Interval;


public class TestInterval {

    @Test
    public void testGetters() {
        for (int i = 0; i < TestMain.RANDOM_TEST_AMOUNT; i++) {
            int a = TestMain.RANDOM_DIS.nextInt();
            int b = TestMain.RANDOM_DIS.nextInt();
            int min = a < b ? a : b;
            int max = a < b ? b : a;

            Interval interval = new Interval(min, max);
            Assert.assertEquals(interval.getMin(), min);
            Assert.assertEquals(interval.getMax(), max);
            Assert.assertThrows(IllegalArgumentException.class, () -> new Interval(max, min));
        }
    }


    @Test
    public void testIsIn() {
        for (int i = 0; i < TestMain.RANDOM_TEST_AMOUNT; i++) {
            int a = TestMain.RANDOM_DIS.nextInt();
            int b = TestMain.RANDOM_DIS.nextInt();
            int min = a < b ? a : b;
            int max = a < b ? b : a;

            Interval interval = new Interval(min, max);
            Interval parent = new Interval(min - 1, max + 1);
            Assert.assertTrue(interval.isIn(parent));
            Assert.assertTrue(interval.isIn(interval));
            Assert.assertTrue(parent.isIn(parent));
            Assert.assertFalse(parent.isIn(interval));
        }
    }

}
