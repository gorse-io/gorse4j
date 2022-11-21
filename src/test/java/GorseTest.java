import io.gorse.gorse4j.*;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.JedisPooled;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GorseTest {

    static final String GORSE_ENDPOINT = "http://127.0.0.1:8088";
    static final String GORSE_API_KEY = "zhenghaoz";

    @Test
    public void testUsers() throws IOException {
        Gorse client = new Gorse(GORSE_ENDPOINT, GORSE_API_KEY);
        // Insert a user.
        User user = new User("100", List.of("a", "b", "c"));
        RowAffected rowAffected = client.insertUser(user);
        Assert.assertEquals(1, rowAffected.getRowAffected());
        // Get this user.
        User returnUser = client.getUser("100");
        Assert.assertEquals(user, returnUser);
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
    public void testItems() throws IOException {
        Gorse client = new Gorse(GORSE_ENDPOINT, GORSE_API_KEY);
        // Insert an item.
        Item item = new Item("100", true, List.of("a", "b", "c"), List.of("d", "e"), "2022-11-20T13:55:27Z", "comment");
        RowAffected rowAffected = client.insertItem(item);
        Assert.assertEquals(1, rowAffected.getRowAffected());
        // Get this item.
        Item returnItem = client.getItem("100");
        Assert.assertEquals(item, returnItem);
        // Delete this item.
        rowAffected = client.deleteItem("100");
        Assert.assertEquals(1, rowAffected.getRowAffected());
        try {
            client.getItem("100");
            Assert.fail();
        } catch (FileNotFoundException ignored) {

        }
    }

    @Test
    public void testFeedback() throws IOException {
        Gorse client = new Gorse(GORSE_ENDPOINT, GORSE_API_KEY);
        List<Feedback> feedbacks = List.of(
                new Feedback("read", "100", "300", "2022-11-20T13:55:27Z"),
                new Feedback("read", "100", "400", "2022-11-20T13:55:27Z")
        );
        // Insert feedback.
        RowAffected rowAffected = client.insertFeedback(feedbacks);
        Assert.assertEquals(2, rowAffected.getRowAffected());
        // List feedback
        List<Feedback> returnFeedback = client.listFeedback("100", "read");
        Assert.assertEquals(feedbacks, returnFeedback);
    }

    @Test
    public void testRecommend() {
        try (JedisPooled jedis = new JedisPooled("localhost", 6379)) {
            Map<String, Double> scoreMembers = new HashMap<String, Double>();
            scoreMembers.put("1", 1.0);
            scoreMembers.put("2", 2.0);
            scoreMembers.put("3", 3.0);
            jedis.zadd("offline_recommend/100", scoreMembers);
            Gorse client = new Gorse(GORSE_ENDPOINT, GORSE_API_KEY);
            List<String> items = client.getRecommend("100");
            Assert.assertEquals(List.of("3", "2", "1"), items);
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
