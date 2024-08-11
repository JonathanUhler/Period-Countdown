import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import org.junit.Assert;
import os.OSPath;


public class TestOSPath {

    @Test
    public void testConstants() {
        Assert.assertNotNull(OSPath.HOME);
        Assert.assertNotNull(OSPath.OS);
        Assert.assertNotEquals(OSPath.OS.length(), 0);
        Assert.assertTrue(OSPath.IS_WIN ^ OSPath.IS_LIN ^ OSPath.IS_MAC);
        if (OSPath.IS_LIN ^ OSPath.IS_MAC) {
            Assert.assertTrue(OSPath.IS_UNIX);
        }
        else {
            Assert.assertFalse(OSPath.IS_UNIX);
        }
    }

    @Test
    public void testGetAppSupportPath() {
        Assert.assertNotNull(OSPath.getAppSupportPath());
    }

    @Test
    public void testGetSchoolJsonPaths() {
        Assert.assertNotNull(OSPath.getSchoolJsonJarPath());
        Assert.assertEquals(OSPath.getSchoolJsonJarPath(), Paths.get("assets/json/schools"));
        Assert.assertNotNull(OSPath.getSchoolJsonDiskPath());
        Assert.assertTrue(OSPath.getSchoolJsonDiskPath().endsWith("schools"));
    }

    @Test
    public void testGetUserJsonPaths() {
        Assert.assertNotNull(OSPath.getUserJsonJarPath());
        Assert.assertEquals(OSPath.getUserJsonJarPath(), Paths.get("assets/json/user"));
        Assert.assertNotNull(OSPath.getUserJsonDiskPath());
        Assert.assertTrue(OSPath.getUserJsonDiskPath().endsWith("user"));
        Assert.assertNotNull(OSPath.getUserJsonFile());
        Assert.assertEquals(OSPath.getUserJsonFile(), Paths.get("User.json"));
    }

    @Test
    public void testJoinStrings() {
        Assert.assertEquals(OSPath.join("a/", "b"), "a/b");
        Assert.assertEquals(OSPath.join("a", "b"), "a/b");
        Assert.assertEquals(OSPath.join("/a", "b"), "/a/b");
        Assert.assertEquals(OSPath.join("/a/", "b"), "/a/b");
        Assert.assertEquals(OSPath.join("a", "b/"), "a/b");
        Assert.assertEquals(OSPath.join("a/", "b/"), "a/b");
        Assert.assertEquals(OSPath.join("a", "/b"), "a/b");
    }

    @Test
    public void testJoinPaths() {
        Assert.assertEquals(OSPath.join(Paths.get("a/"), Paths.get("b")), Paths.get("a/b"));
        Assert.assertEquals(OSPath.join(Paths.get("a"), Paths.get("b")), Paths.get("a/b"));
        Assert.assertEquals(OSPath.join(Paths.get("/a"), Paths.get("b")), Paths.get("/a/b"));
        Assert.assertEquals(OSPath.join(Paths.get("/a/"), Paths.get("b")), Paths.get("/a/b"));
        Assert.assertEquals(OSPath.join(Paths.get("a"), Paths.get("b/")), Paths.get("a/b"));
        Assert.assertEquals(OSPath.join(Paths.get("a/"), Paths.get("b/")), Paths.get("a/b"));
        Assert.assertEquals(OSPath.join(Paths.get("a"), Paths.get("/b")), Paths.get("a/b"));
    }

    @Test
    public void testJoinNull() {
        Assert.assertThrows(NullPointerException.class, () -> OSPath.join("a", null));
        Assert.assertThrows(NullPointerException.class, () -> OSPath.join(null, "b"));
        Assert.assertThrows(NullPointerException.class, () -> OSPath.join((String) null, null));

        Path p = Paths.get("a");
        Assert.assertThrows(NullPointerException.class, () -> OSPath.join(p, null));
        Assert.assertThrows(NullPointerException.class, () -> OSPath.join(null, p));
        Assert.assertThrows(NullPointerException.class, () -> OSPath.join((Path) null, null));
    }

    @Test
    public void testIsCertainPath() {
        Assert.assertFalse(OSPath.isSchoolInJar(Paths.get("schools")));
        Assert.assertFalse(OSPath.isSchoolInJar(Paths.get("assets")));
        Assert.assertFalse(OSPath.isSchoolInJar(Paths.get("assets/json")));
        Assert.assertFalse(OSPath.isSchoolInJar(Paths.get("assets/json/schools")));
        Assert.assertFalse(OSPath.isSchoolInJar(Paths.get("assets/json/schools/")));
        Assert.assertTrue(OSPath.isSchoolInJar(Paths.get("assets/json/schools/School.json")));

        Assert.assertFalse(OSPath.isUserInJar(Paths.get("user")));
        Assert.assertFalse(OSPath.isUserInJar(Paths.get("assets")));
        Assert.assertFalse(OSPath.isUserInJar(Paths.get("assets/json")));
        Assert.assertFalse(OSPath.isUserInJar(Paths.get("assets/json/user")));
        Assert.assertFalse(OSPath.isUserInJar(Paths.get("assets/json/user/")));
        Assert.assertTrue(OSPath.isUserInJar(Paths.get("assets/json/user/User.json")));

        Assert.assertFalse(OSPath.isInJar(Paths.get("assets")));
        Assert.assertFalse(OSPath.isInJar(Paths.get("assets/json")));
        Assert.assertFalse(OSPath.isInJar(Paths.get("schools")));
        Assert.assertFalse(OSPath.isInJar(Paths.get("users")));
        Assert.assertFalse(OSPath.isInJar(Paths.get("assets/json/schools")));
        Assert.assertFalse(OSPath.isInJar(Paths.get("assets/json/user")));
        Assert.assertTrue(OSPath.isInJar(Paths.get("assets/json/schools/School.json")));
        Assert.assertTrue(OSPath.isInJar(Paths.get("assets/json/user/User.json")));

        Assert.assertFalse(OSPath.isJsonFile(Paths.get("not a json file")));
        Assert.assertFalse(OSPath.isJsonFile(Paths.get("$.json")));
        Assert.assertTrue(OSPath.isJsonFile(Paths.get("is a json file.json")));
        Assert.assertTrue(OSPath.isJsonFile(Paths.get("is-a-json-file.json")));
        Assert.assertTrue(OSPath.isJsonFile(Paths.get("is_a_json_file.json")));
        Assert.assertTrue(OSPath.isJsonFile(Paths.get("Is-A-Json-File.json")));
    }

    @Test
    public void testIsCertainPathOnNull() {
        Assert.assertThrows(NullPointerException.class, () -> OSPath.isSchoolInJar(null));
        Assert.assertThrows(NullPointerException.class, () -> OSPath.isUserInJar(null));
        Assert.assertThrows(NullPointerException.class, () -> OSPath.isInJar(null));
        Assert.assertThrows(NullPointerException.class, () -> OSPath.isJsonFile(null));
    }

}
