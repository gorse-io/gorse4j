package io.gorse.gorse4j;

import redis.clients.jedis.JedisPooled;

/**
 * This interface is the entry-point for writing tests for gorse.
 * An instance of this is very heavy to obtain and should be shared across your tests.
 * You can obtain an instance with {@link GorseFactory#mock()}.
 */
public interface GorseServer extends AutoCloseable {
	
	/**
	 * Returns a {@link JedisPooled} client that this server is connected to for cache.
	 */
	JedisPooled jedis();
	
	/**
	 * Returns a connected instance of {@link SynchronousGorseClient}.
	 */
	SynchronousGorseClient synchronous();
	
	/**
	 * Returns a connected instance of {@link AsynchronousGorseClient}.
	 */
	AsynchronousGorseClient asynchronous();
}