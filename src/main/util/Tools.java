package util;


import school.SchoolAPI;
import school.SchoolPeriod;
import user.UserAPI;
import user.UserPeriod;
import user.UserJson;
import java.util.List;
import java.util.ArrayList;


/**
 * Miscellaneous tools that do not fit into any other util class.
 *
 * @author Jonathan Uhler
 */
public class Tools {

    /**
     * Pads a string to a given width with a given character. If the string is longer than the 
     * specified width, no change is made. If the given string is {@code null}, an empty string
     * is returned.
     *
     * @param str        the string to pad to the left.
     * @param width      the minimum final width of the string.
     * @param character  the character to pad with.
     *
     * @return a new string that is at least {@code width} characters long and padded to the left
     *         with the specified character.
     */
    public static String pad(String str, int width, char character) {
        if (str == null)
            str = "";
        return String.format("%" + width + "s", str).replace(' ', character);
    }


    /**
     * Returns the list of next class information.
     *
     * @param schoolAPI  school information to use.
     * @param userAPI    user information to use.
     * @param timezone   the unix TZ identifier for the school.
     *
     * @return the list of next class information.
     */
    public static List<String> getNextUpList(SchoolAPI schoolAPI,
                                             UserAPI userAPI,
                                             String timezone)
    {
        return Tools.getNextUpList(schoolAPI, userAPI, timezone, UTCTime.now());
    }
	

    /**
     * Returns the list of next class information.
     *
     * @param schoolAPI  school information to use.
     * @param userAPI    user information to use.
     * @param timezone   the unix TZ identifier for the school.
     * @param now        the time to get the next up information for.
     *
     * @return the list of next class information.
     */
    public static List<String> getNextUpList(SchoolAPI schoolAPI,
                                             UserAPI userAPI,
                                             String timezone,
                                             UTCTime now)
    {
        if (schoolAPI == null || userAPI == null)
            return new ArrayList<>();
		
        String nextUp = userAPI.getNextUp();
        ArrayList<String> nextUpList = new ArrayList<>();
		
        SchoolPeriod nextPeriod = schoolAPI.getNextPeriodToday(now);
        while (nextPeriod != null) {
            if (nextUp.equals(UserJson.NEXT_UP_DISABLED))
                break;

            // Get the class (with user data like teacher and room) based on the generic period,
            // if that generic period can have a class
            UserPeriod nextClass = null;
            if (nextPeriod.isCounted())
                nextClass = userAPI.getPeriod(nextPeriod);

            // Format the string
            // Default periodString is "<period/class name> | <start>-<end>"
            UTCTime periodStart = nextPeriod.getStart();
            UTCTime periodEnd = nextPeriod.getEnd();

            // Add 1 to the end time ms to make the end time the same as the start time of
            // the next period
            periodEnd = periodEnd.plus(1, UTCTime.MILLISECONDS);

            try {
                periodStart = periodStart.to(timezone);
                periodEnd = periodEnd.to(timezone);
            }
            catch (IllegalArgumentException e) {
                // Ignore, just continue with UTC time
            }

            String periodString =
                ((nextClass == null) ? nextPeriod.getName() : nextClass.getName()) + " | " +
                Tools.pad(Integer.toString(periodStart.get(UTCTime.HOUR)), 2, '0') + ":" +
                Tools.pad(Integer.toString(periodStart.get(UTCTime.MINUTE)), 2, '0') + "-" +
                Tools.pad(Integer.toString(periodEnd.get(UTCTime.HOUR)), 2, '0') + ":" +
                Tools.pad(Integer.toString(periodEnd.get(UTCTime.MINUTE)), 2, '0');
			
            // If the school period has a class during it add " | <teacher>, <room>"
            if (!nextPeriod.isFree() && nextClass != null && !nextClass.isFree()) {
                String teacher = nextClass.getTeacher();
                String room = nextClass.getRoom();

                // Format based on what data is available (either one, the other, both, or neither)
                if (!teacher.equals("") && room.equals(""))
                    periodString += " | " + teacher;
                else if (!room.equals("") && teacher.equals(""))
                    periodString += " | " + room;
                else if (!room.equals("") && !teacher.equals(""))
                    periodString += " | " + teacher + ", " + room;
            }
            // If the period has something during it (a period, lunch, brunch, etc.) add it to
            // the list
            if (nextPeriod.isCounted())
                nextUpList.add(periodString);

            // Get next period
            nextPeriod = schoolAPI.getNextPeriodToday(periodStart);

            // If only the next period should be shown and that period has been found, skip
            // the rest of the search
            if (nextUp.equals(UserJson.NEXT_UP_ONE) && nextUpList.size() == 1)
                break;
        }

        return nextUpList;
    }

}
