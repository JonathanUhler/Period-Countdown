import java.util.Random;
import time.UTCTime;


public class TestMain {

    static final int RANDOM_TEST_AMOUNT = 100000;
    static final Random RANDOM_DIS = new Random();


    public static String randomString(int length) {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < length; i++) {
            string.append((char) TestMain.RANDOM_DIS.nextInt(255));
        }
        return string.toString();
    }


    public static UTCTime randomTime() {
        int year = TestMain.RANDOM_DIS.nextInt(9000) + 1000;
        int month = TestMain.RANDOM_DIS.nextInt(12) + 1;
        int day = TestMain.RANDOM_DIS.nextInt(28) + 1;
        int hour = TestMain.RANDOM_DIS.nextInt(23) + 1;
        int minute = TestMain.RANDOM_DIS.nextInt(59) + 1;
        int second = TestMain.RANDOM_DIS.nextInt(59) + 1;
        int millis = TestMain.RANDOM_DIS.nextInt(999) + 1;

        UTCTime time = UTCTime.of(String.format("%04d", year) + "-" +
                                  String.format("%02d", month) + "-" +
                                  String.format("%02d", day) + "T" +
                                  String.format("%02d", hour) + ":" +
                                  String.format("%02d", minute) + ":" +
                                  String.format("%02d", second) + "." +
                                  String.format("%03d", millis),
                                  "Z");
        return time;
    }

}
