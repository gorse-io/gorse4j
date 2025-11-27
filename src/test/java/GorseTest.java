import io.gorse.gorse4j.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

public class GorseTest {

    static final String GORSE_ENDPOINT = "http://127.0.0.1:8088";
    static final String GORSE_API_KEY = "zhenghaoz";

    Gorse client;

    @Before
    public void setUp() {
        client = new Gorse(GORSE_ENDPOINT, GORSE_API_KEY);
    }

    @Test
    public void testUsers() throws IOException {
        User user = new User("1000", new LinkedHashMap<String, String>() {
            {
                put("gender", "M");
                put("occupation", "engineer");
            }
        });
        client.insertUser(user);
        User resp = client.getUser("1000");
        Assert.assertEquals(user, resp);

        client.deleteUser("1000");
        Assert.assertThrows(IOException.class, () -> {
            client.getUser("1000");
        });
    }

    @Test
    public void testItems() throws IOException {
        Item item = new Item("2000", true, new LinkedHashMap<String, Object>() {
            {
                put("embedding", List.of(0.1, 0.2, 0.3));
            }
        }, List.of("Comedy", "Animation"), "2024-06-01T00:00:00Z", "Minions (2015)");
        client.insertItem(item);
        Item resp = client.getItem("2000");
        Assert.assertEquals(item, resp);

        client.deleteItem("2000");
        Assert.assertThrows(IOException.class, () -> {
            client.getItem("2000");
        });
    }

    @Test
    public void testFeedback() throws IOException {
        client.insertUser(new User("2000", null));

        Feedback fb1 = new Feedback("watch", "2000", "1", 1, "2024-06-01T00:00:00Z");
        Feedback fb2 = new Feedback("watch", "2000", "1060", 2, "2024-06-01T00:00:00Z");
        Feedback fb3 = new Feedback("watch", "2000", "11", 3, "2024-06-01T00:00:00Z");

        client.insertFeedback(List.of(fb1, fb2, fb3));

        List<Feedback> userFeedback = client.listFeedback("2000", "watch");
        Assert.assertEquals(3, userFeedback.size());

        client.deleteFeedback("watch", "2000", "1");
        userFeedback = client.listFeedback("2000", "watch");
        Assert.assertEquals(2, userFeedback.size());
    }

    @Test
    public void testRecommend() throws IOException {
        client.insertUser(new User("3000", null));
        List<String> recommendations = client.getRecommend("3000");
        Assert.assertEquals("315", recommendations.get(0));
        Assert.assertEquals("1432", recommendations.get(1));
        Assert.assertEquals("918", recommendations.get(2));
    }
}
