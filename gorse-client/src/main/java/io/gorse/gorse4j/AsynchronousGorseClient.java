package io.gorse.gorse4j;

import io.avaje.http.api.Client;
import io.avaje.http.api.Delete;
import io.avaje.http.api.Get;
import io.avaje.http.api.Post;
import io.gorse.gorse4j.model.Feedback;
import io.gorse.gorse4j.model.Item;
import io.gorse.gorse4j.model.RowAffected;
import io.gorse.gorse4j.model.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This interface is the asynchronous entry-point for the gorse Java client.
 * An instance of this is particularly heavy to obtain and should be shared across your application.
 * It should not be stored in a static field, in order to avoid deadlocking.
 * You can obtain an instance with {@link GorseFactory}.
 * @see SynchronousGorseClient
 */
@Client
public interface AsynchronousGorseClient {
	
	//#region User
	/**
	 * Insert a user.
	 */
	@Post("/api/user")
	CompletableFuture<RowAffected> insertUser(User user);
	
	/**
	 * Get a user by their identifier.
	 */
	@Get("/api/user/{userId}")
	CompletableFuture<User> getUser(String userId);
	
	/**
	 * Delete a user.
	 */
	@Delete("/api/user/{userId}")
	CompletableFuture<RowAffected> deleteUser(String userId);
	//#endregion
	//#region Item
	/**
	 * Insert an item.
	 */
	@Post("/api/item")
	CompletableFuture<RowAffected> insertItem(Item item);
	
	/**
	 * Get a user by their identifier.
	 */
	@Get("/api/item/{itemId}")
	CompletableFuture<Item> getItem(String itemId);
	
	/**
	 * Delete a user.
	 */
	@Delete("/api/item/{itemId}")
	CompletableFuture<RowAffected> deleteItem(String itemId);
	//#endregion
	//#region Feedback
	/**
	 * Insert feedback.
	 */
	@Post("/api/feedback")
	CompletableFuture<RowAffected> insertFeedback(List<Feedback> feedback);
	
	/**
	 * List feedback.
	 */
	@Get("/api/user/{userId}/feedback/{feedbackType}")
	CompletableFuture<List<Feedback>> listFeedback(String userId, String feedbackType);
	//#endregion
	//#region Recommend
	/**
	 * Get recommendations for the provided user.
	 */
	@Get("/api/recommend/{userId}")
	CompletableFuture<List<String>> getRecommend(String userId);
	//#endregion
}