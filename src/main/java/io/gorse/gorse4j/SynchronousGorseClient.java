package io.gorse.gorse4j;

import io.avaje.http.api.Client;
import io.avaje.http.api.Delete;
import io.avaje.http.api.Get;
import io.avaje.http.api.Post;

import java.util.List;

/**
 * This interface is the synchronous entry-point for the gorse Java client.
 * An instance of this is particularly heavy to obtain and should be shared across your application.
 * It should not be stored in a static field, in order to avoid deadlocking.
 * You can obtain an instance with {@link GorseFactory}.
 * @see AsynchronousGorseClient
 */
@Client
public interface SynchronousGorseClient {
	
	//#region User
	/**
	 * Insert a user.
	 */
	@Post("/api/user")
	RowAffected insertUser(User user);
	
	/**
	 * Get a user by their identifier.
	 */
	@Get("/api/user/{userId}")
	User getUser(String userId);
	
	/**
	 * Delete a user.
	 */
	@Delete("/api/user/{userId}")
	RowAffected deleteUser(String userId);
	//#endregion
	//#region Item
	/**
	 * Insert an item.
	 */
	@Post("/api/item")
	RowAffected insertItem(Item item);
	
	/**
	 * Get a user by their identifier.
	 */
	@Get("/api/item/{itemId}")
	Item getItem(String itemId);
	
	/**
	 * Delete a user.
	 */
	@Delete("/api/item/{itemId}")
	RowAffected deleteItem(String itemId);
	//#endregion
	//#region Feedback
	/**
	 * Insert feedback.
	 */
	@Post("/api/feedback")
	RowAffected insertFeedback(List<Feedback> feedback);
	
	/**
	 * List feedback.
	 */
	@Get("/api/user/{userId}/feedback/{feedbackType}")
	List<Feedback> listFeedback(String userId, String feedbackType);
	//#endregion
	//#region Recommend
	/**
	 * Get recommendations for the provided user.
	 */
	@Get("/api/recommend/{userId}")
	List<String> getRecommend(String userId);
	//#endregion
}