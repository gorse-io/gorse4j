package io.gorse.gorse4j;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class GorseTest {

    private static final GorseServer gorse = GorseFactory.mock();
    
    @AfterClass
    public static void after() throws Exception {
        gorse.close();
    }
    
    @Test
    public void testUsers() {
        SynchronousGorseClient client = gorse.synchronous();
        if (client == null) fail();

        // Insert a user.
        User user = new User("100", List.of("a", "b", "c"));
        RowAffected rowAffected = client.insertUser(user);
        assertEquals(1, rowAffected.getRowAffected());

        // Get this user.
        User returnUser = client.getUser("100");
        assertEquals(user, returnUser);

        // Delete this user.
        rowAffected = client.deleteUser("100");
        assertEquals(1, rowAffected.getRowAffected());
        assertThrows(FileNotFoundException.class, () -> client.getUser("100"));
    }

    @Test
    public void testItems() {
        SynchronousGorseClient client = gorse.synchronous();
        if (client == null) fail();

        // Insert an item.
        Item item = new Item("100", true, List.of("a", "b", "c"), List.of("d", "e"), "2022-11-20T13:55:27Z", "comment");
        RowAffected rowAffected = client.insertItem(item);
        assertEquals(1, rowAffected.getRowAffected());

        // Get this item.
        Item returnItem = client.getItem("100");
        assertEquals(item, returnItem);

        // Delete this item.
        rowAffected = client.deleteItem("100");
        assertEquals(1, rowAffected.getRowAffected());
        assertThrows(FileNotFoundException.class, () -> client.getItem("100"));
    }

    @Test
    public void testFeedback() {
        SynchronousGorseClient client = gorse.synchronous();
        if (client == null) fail();

        List<Feedback> feedbacks = List.of(
                new Feedback("read", "100", "300", Instant.parse("2022-11-20T13:55:27Z")),
                new Feedback("read", "100", "400", Instant.parse("2022-11-20T13:55:27Z"))
        );

        // Insert feedback.
        RowAffected rowAffected = client.insertFeedback(feedbacks);
        assertEquals(2, rowAffected.getRowAffected());

        // List feedback
        List<Feedback> returnFeedback = client.listFeedback("100", "read");
        assertEquals(feedbacks, returnFeedback);
    }

    @Test
    public void testRecommend() {
        Map<String, Double> scoreMembers = Map.of(
                "1", 1.0,
                "2", 2.0,
                "3", 3.0
        );
        gorse.jedis().zadd("offline_recommend/100", scoreMembers);
        SynchronousGorseClient client = gorse.synchronous();

        List<String> items = client.getRecommend("100");
        assertEquals(List.of("3", "2", "1"), items);
    }
}