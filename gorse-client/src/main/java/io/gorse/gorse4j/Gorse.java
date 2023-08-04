package io.gorse.gorse4j;

import io.gorse.gorse4j.model.Feedback;
import io.gorse.gorse4j.model.Item;
import io.gorse.gorse4j.model.RowAffected;
import io.gorse.gorse4j.model.User;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @deprecated
 * @see GorseFactory
 */
@Deprecated
public final class Gorse implements SynchronousGorseClient {
    
    private final SynchronousGorseClient delegate;
    
    public Gorse(String endpoint, String apiKey) {
        try {
            this.delegate = GorseFactory.synchronous(new URI(endpoint), apiKey);
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException("Endpoint provided to Gorse#Gorse(String, String) is not a valid URL", exception);
        }
    }
    
    @Override
    public RowAffected insertUser(User user) {
        return delegate.insertUser(user);
    }
    
    @Override
    public User getUser(String userId) {
        return delegate.getUser(userId);
    }
    
    @Override
    public RowAffected deleteUser(String userId) {
        return delegate.deleteUser(userId);
    }
    
    @Override
    public RowAffected insertItem(Item item) {
        return delegate.insertItem(item);
    }
    
    @Override
    public Item getItem(String itemId) {
        return delegate.getItem(itemId);
    }
    
    @Override
    public RowAffected deleteItem(String itemId) {
        return delegate.deleteItem(itemId);
    }
    
    @Override
    public RowAffected insertFeedback(List<Feedback> feedback) {
        return delegate.insertFeedback(feedback);
    }
    
    @Override
    public List<Feedback> listFeedback(String userId, String feedbackType) {
        return delegate.listFeedback(userId, feedbackType);
    }
    
    @Override
    public List<String> getRecommend(String userId) {
        return delegate.getRecommend(userId);
    }
}