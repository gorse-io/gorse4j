import io.gorse.gorse4j.Gorse;
import io.gorse.gorse4j.RowAffected;
import io.gorse.gorse4j.User;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class GorseTest {

    static final String GORSE_ENDPOINT = "http://127.0.0.1:8088";
    static final String GORSE_API_KEY = "zhenghaoz";

    @Test
    public void testUsers() throws IOException {
        Gorse client = new Gorse(GORSE_ENDPOINT, GORSE_API_KEY);
        // Insert a user.
        RowAffected rowAffected = client.insertUser(new User("100", List.of("a","b","c")));
        Assert.assertEquals(1, rowAffected.getRowAffected());
        // Get this user.
        User user = client.getUser("100");
        Assert.assertEquals(new User("100", List.of("a","b","c")), user);
        // Delete this user.
        rowAffected = client.deleteUser("100");
        Assert.assertEquals(1, rowAffected.getRowAffected());
        try {
            client.getUser("100");
            Assert.fail();
        } catch (FileNotFoundException ignored) {

        }
    }

    @Test
    public void testItems() {

    }

    @Test
    public void testFeedback() {

    }

    @Test
    public void testRecommend() {

    }
}
