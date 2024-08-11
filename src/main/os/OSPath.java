package os;


import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Utilities for manipulating pathnames in an operating system agnostic manner.
 *
 * @author Jonathan Uhler
 */
public class OSPath {
    
    /** The user's home on the disk. This is {@code ~} or {@code $HOME} on unix systems. */
    public static final Path HOME = Paths.get(System.getProperty("user.home"));
    /** The name of the operating system the application is running on. */
    public static final String OS = System.getProperty("os.name");
    /** Whether the user's operating system is Windows. */
    public static final boolean IS_WIN = OSPath.OS.startsWith("Windows");
    /** Whether the user's operating system is a Linux distro. */
    public static final boolean IS_LIN = OSPath.OS.startsWith("Linux");
    /** Whether the user's operating system is Mac OS X. */
    public static final boolean IS_MAC = OSPath.OS.startsWith("Mac");
    /** Whether the user's operating system is unix based (e.g. Mac OS X or a Linux distro). */
    public static final boolean IS_UNIX = OSPath.IS_LIN || OSPath.IS_MAC;
    
    
    /**
     * Returns a {@code Path} object that points to the directory used by the application for
     * local data storage.
     *
     * This path is operating system dependent. If the operating system is not known, then
     * an exception is thrown.
     * 
     * - Windows: {@code $HOME\AppData\PeriodCountdown}
     * - Mac OS X: {@code $HOME/Library/Application Support/PeriodCountdown}
     * - Linux: {@code $HOME/.PeriodCountdown}
     *
     * @return a {@code Path} object that points to the local data directory.
     *
     * @throws IllegalStateException  if the operating system is not Mac, Windows, or Linux.
     */
    public static Path getAppSupportPath() {
        if (OSPath.IS_WIN) {
            return OSPath.join(OSPath.HOME, Paths.get("AppData\\PeriodCountdown"));
        }
        else if (OSPath.IS_MAC) {
            return OSPath.join(OSPath.HOME,
                               Paths.get("Library/Application Support/PeriodCountdown"));
        }
        else if (OSPath.IS_LIN) {
            return OSPath.join(OSPath.HOME, Paths.get(".PeriodCountdown"));
        }
        else {
            throw new IllegalStateException("unknown operating system type: " + OSPath.OS);
        }
    }
    
    
    /**
     * Returns a {@code Path} object that points to {@code assets/json/schools}.
     *
     * @return a {@code Path} object that points to {@code assets/json/schools}.
     */
    public static Path getSchoolJsonJarPath() {
        return Paths.get("assets/json/schools");
    }
    
    
    /**
     * Returns a {@code Path} object that points to the {@code schools} folder concatenated
     * to the value of {@code OSPath.getAppSupportPath()}.
     *
     * @return a {@code Path} object that points to the {@code schools} folder concatenated
     *         to the value of {@code OSPath.getAppSupportPath()}.
     */
    public static Path getSchoolJsonDiskPath() {
        return OSPath.join(OSPath.getAppSupportPath(), Paths.get("schools"));
    }
    
    
    /**
     * Returns a {@code Path} object that points to {@code assets/jons/user}.
     *
     * @return a {@code Path} object that points to {@code assets/jons/user}.
     */
    public static Path getUserJsonJarPath() {
        return Paths.get("assets/json/user");
    }
    
    
    /**
     * Returns a {@code Path} object that points to the {@code user} folder concatenated to the
     * value of {@code OSPath.getAppSupportPath()}.
     *
     * @return a {@code Path} object that points to the {@code user} folder concatenated to the
     *         value of {@code OSPath.getAppSupportPath()}.
     */
    public static Path getUserJsonDiskPath() {
        return OSPath.join(OSPath.getAppSupportPath(), Paths.get("user"));
    }
    
    
    /**
     * Returns a {@code Path} object that points to {@code User.json}.
     *
     * @return a {@code Path} object that points to {@code User.json}.
     */
    public static Path getUserJsonFile() {
        return Paths.get("User.json");
    }
    
    
    /**
     * Joins two paths represented by string literals. The returned result is a string.
     *
     * @param a  the first path component.
     * @param b  the second path component.
     *
     * @return the merged path {@code a + b}.
     *
     * @throws NullPointerException  if either string is null.
     */
    public static String join(String a, String b) {
        if (a == null) {
            throw new NullPointerException("a cannot be null");
        }
        if (b == null) {
            throw new NullPointerException("b cannot be null");
        }

        Path pa = Paths.get(a);
        Path pb = Paths.get(b);
        return OSPath.join(pa, pb).toString();
    }
    
    
    /**
     * Joins two {@code Path}s.
     *
     * @param a  the first path component.
     * @param b  the second path component.
     *
     * @return the merged path {@code a + b}.
     *
     * @throws NullPointerException  if either path is null.
     */
    public static Path join(Path a, Path b) {
        if (a == null) {
            throw new NullPointerException("a cannot be null");
        }
        if (b == null) {
            throw new NullPointerException("b cannot be null");
        }

        return Paths.get(a.toString(), b.toString());
    }
    
    
    /**
     * Determines whether the specified path is a file in {@code OSPath.getSchoolJsonJarPath()}.
     *
     * @param path  the path to check.
     *
     * @return whether the specified path is a file in {@code OSPath.getSchoolJsonJarPath()}.
     *
     * @throws NullPointerException  if {@code path} is null.
     */
    public static boolean isSchoolInJar(Path path) {
        if (path == null) {
            throw new NullPointerException("path cannot be null");
        }

        Path schoolJsonJarPath = OSPath.getSchoolJsonJarPath();
        return path.startsWith(schoolJsonJarPath) && !path.equals(schoolJsonJarPath);
    }
    
    
    /**
     * Determines whether the specified path is a file in {@code OSPath.getUserJsonJarPath()}.
     *
     * @param path  the path to check.
     *
     * @return whether the specified path is a file in {@code OSPath.getUserJsonJarPath()}.
     *
     * @throws NullPointerException  if {@code path} is null.
     */
    public static boolean isUserInJar(Path path) {
        if (path == null) {
            throw new NullPointerException("path cannot be null");
        }

        Path userJsonJarPath = OSPath.getUserJsonJarPath();
        return path.startsWith(userJsonJarPath) && !path.equals(userJsonJarPath);
    }
    
    
    /**
     * Determines whether the specified path is in the jar file.
     *
     * @param path  the path to check.
     *
     * @return whether the specified path is in the jar file.
     *
     * @throws NullPointerException  if {@code path} is null.
     */
    public static boolean isInJar(Path path) {
        if (path == null) {
            throw new NullPointerException("path cannot be null");
        }

        return OSPath.isSchoolInJar(path) || OSPath.isUserInJar(path);
    }
    
    
    /**
     * Determines whether the specified path is has the name of a valid json file.
     *
     * If the check passes, the path may or may not be a json file that exists, but is
     * syntactically correct in its name.
     *
     * @param path  the path to check.
     *
     * @return whether the specified path is has the name of a valid json file.
     *
     * @throws NullPointerException  if {@code path} is null.
     */
    public static boolean isJsonFile(Path path) {
        if (path == null) {
            throw new NullPointerException("path cannot be null");
        }

        return path.getFileName().toString().matches("^[\\w\\-. ]+\\.json$");
    }
    
}
