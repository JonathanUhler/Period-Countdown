package web.transport;


import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.nio.file.Path;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseApp;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import school.SchoolJson;
import user.UserJson;


/**
 * An generic database system to manage user and school configuration information.
 *
 * Currently, this class uses Google Firebase to store data. The data storage solution can be
 * changed by only modifying this class. Only the following public methods are required, and their
 * data management implementation can be anything:
 *
 * - {@code UserJson getUserJson(String)}
 * - {@code SchoolJson getSchoolJson(String, Path)}
 * - {@code void setUserJson(String, UserJson)}
 * - {@code void setSchoolJson(String, SchoolJson, Path)}
 *
 * @author Jonathan Uhler
 */
public class TransportDatabase {

    private FirebaseDatabase database;


    /**
     * Constructs a new {@code TransportDatabase}.
     *
     * @param properties  server properties from which database configuration can be read.
     */
    public TransportDatabase(Properties properties) {
        String databaseCredentialsPath = properties.getProperty("transport.databaseCredentials");
        String databaseUri = properties.getProperty("transport.databaseUri");
        if (databaseCredentialsPath == null) {
            PCTransport.LOGGER.severe("transport.databaseCredentials is not defined");
            System.exit(1);
            return;
        }
        if (databaseUri == null) {
            PCTransport.LOGGER.severe("transport.databaseUri is not defined");
            System.exit(1);
            return;
        }

        FileInputStream databaseCredentials;
        try {
            databaseCredentials = new FileInputStream(databaseCredentialsPath);
        }
        catch (IOException e) {
            PCTransport.LOGGER.severe("cannot load transport.databaseCredentials: " + e);
            System.exit(1);
            return;
        }

        FirebaseOptions databaseOptions;
        try {
            databaseOptions = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(databaseCredentials))
                .setDatabaseUrl(databaseUri)
                .build();
        }
        catch (IOException e) {
            PCTransport.LOGGER.severe("cannot load database: " + e);
            System.exit(1);
            return;
        }

        FirebaseApp.initializeApp(databaseOptions);
        this.database = FirebaseDatabase.getInstance();
    }


    /**
     * Returns a specified database resource for a specific user.
     *
     * This method is synchronous.
     *
     * @param userId  the unique identifier of the database user.
     * @param key     the key to get.
     *
     * @return the database resource, or {@code null} if the resource cannot be retrieved.
     */
    private String getDatabaseResource(String userId, String key) {
        String refPath = "users/" + userId + "/" + key;
        DatabaseReference ref = this.database.getReference(refPath);

        CountDownLatch latch = new CountDownLatch(1);
        String[] data = new String[1];
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Object value = snapshot.getValue(true);
                    if (value != null) {
                        data[0] = (String) value;
                    }
                    latch.countDown();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    PCTransport.LOGGER.warning("database get '" + refPath + "' failed: " + error);
                    data[0] = null;
                    latch.countDown();
                }
            });

        try {
            latch.await(10, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            return null;
        }
        return data[0];
    }


    /**
     * Returns a list of all children of a specified database resource for a specific user.
     *
     * This method is synchronous.
     *
     * @param userId     the unique identifier of the database user.
     * @param parentKey  the parent to get all the child keys of.
     *
     * @return the list of child resource keys. If the parent cannot be retrieved or no children
     *         exist, an empty list will be returned.
     */
    private List<String> getAvailableResources(String userId, String parentKey) {
        String refPath = "users/" + userId + "/" + parentKey;
        DatabaseReference ref = this.database.getReference(refPath);

        CountDownLatch latch = new CountDownLatch(1);
        List<String> data = new ArrayList<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        data.add(child.getKey());
                    }
                    latch.countDown();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    PCTransport.LOGGER.warning("database get '" + refPath + "' failed: " + error);
                    latch.countDown();
                }
            });

        try {
            latch.await(10, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            return null;
        }
        return data;
    }


    /**
     * Sets a specified database resource for a specific user.
     *
     * This method is synchronous.
     *
     * @param userId    the unique identifier of the database user.
     * @param key       the key to set.
     * @param resource  the resource to set at {@code userId/key}.
     */
    private void setDatabaseResource(String userId, String key, String resource) {
        String refPath = "users/" + userId + "/" + key;
        DatabaseReference ref = this.database.getReference(refPath);
        ref.setValue(resource, null);
    }


    /**
     * Returns the {@code UserJson} object for a specified database user.
     *
     * If the user does not exist, a new user JSON record will be created with the provided ID.
     * If the user does exist, but an error occurs retrieving the record, {@code null} will be
     * returned.
     *
     * @param userId  the unique identifier of the database user.
     *
     * @return the {@code UserJson} object for the specified user.
     */
    public UserJson getUserJson(String userId) {
        String resource = this.getDatabaseResource(userId, "user");
        if (resource == null) {
            UserJson newUserJson = new UserJson();
            this.setUserJson(userId, newUserJson);
            return newUserJson;
        }

        Gson gson = new Gson();
        UserJson json;
        try {
            json = gson.fromJson(resource, UserJson.class);
        }
        catch (JsonSyntaxException e) {
            PCTransport.LOGGER.warning("cannot parse database response as UserJson: " + e);
            return null;
        }
        return json;
    }


    /**
     * Returns the {@code SchoolJson} object for a specified database user and school.
     *
     * If the user or school does not exist, or an error occurs in retrieving the school record,
     * {@code null} will be returned.
     *
     * @param userId      the unique identifier of the database user.
     * @param schoolFile  an identifier of the school file name in the database.
     *
     * @return the {@code SchoolJson} object for the specified user and school.
     */
    public SchoolJson getSchoolJson(String userId, Path schoolFile) {
        String schoolName = schoolFile.toFile().getName();
        String resource = this.getDatabaseResource(userId, "schools/" + schoolName);
        if (resource == null) {
            return null;
        }

        Gson gson = new Gson();
        SchoolJson json;
        try {
            json = gson.fromJson(resource, SchoolJson.class);
        }
        catch (JsonSyntaxException e) {
            PCTransport.LOGGER.warning("cannot parse database response as SchoolJson: " + e);
            return null;
        }
        return json;
    }


    /**
     * Returns a list of school file names that, when converted with {@code Paths.get}, can be
     * passed to {@code getSchoolJson}.
     *
     * @param userId  the unique identifier of the database user.
     *
     * @return a list of school file names.
     */
    public List<String> getAvailableSchools(String userId) {
        return this.getAvailableResources(userId, "schools");
    }


    /**
     * Updates or creates the {@code UserJson} object for the specified database user.
     *
     * @param userId  the unique identifier of the database user.
     * @param json    the updated record to set for the specified user.
     */
    public void setUserJson(String userId, UserJson json) {
        Gson gson = new Gson();
        String resource;
        try {
            resource = gson.toJson(json);
        }
        catch (JsonSyntaxException e) {
            PCTransport.LOGGER.warning("cannot convert UserJson to Map: " + e);
            return;
        }

        this.setDatabaseResource(userId, "user", resource);
    }


    /**
     * Updates or creates the {@code SchoolJson} object for the specified database user and school.
     *
     * @param userId      the unique identifier of the database user.
     * @param json        the updated record to set for the specified school file.
     * @param schoolFile  the file name of the school record to update or create.
     */
    public void setSchoolJson(String userId, SchoolJson json, Path schoolFile) {
        Gson gson = new Gson();
        String resource;
        try {
            resource = gson.toJson(json);
        }
        catch (JsonSyntaxException e) {
            PCTransport.LOGGER.warning("cannot convert UserJson to Map: " + e);
            return;
        }

        String schoolName = schoolFile.toFile().getName();
        this.setDatabaseResource(userId, "schools/" + schoolName, resource);
    }

}
