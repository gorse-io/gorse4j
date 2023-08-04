package io.gorse.gorse4j;

import io.avaje.http.client.HttpClient;
import io.avaje.http.client.JsonbBodyAdapter;
import io.avaje.jsonb.Jsonb;
import io.gorse.gorse4j.internal.GorseInterceptor;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.Logger.Level.*;

/**
 * Factory class to obtain instances of {@link SynchronousGorseClient}/{@link AsynchronousGorseClient}.
 */
public final class GorseFactory {

	static final System.Logger logger = System.getLogger("io.gorse.gorse4j");
	static final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
	
	static final Jsonb jsonb = Jsonb.builder().build();
	static final ExecutorService executor = Executors.newCachedThreadPool();
	
	/**
	 * Returns an instance of {@link SynchronousGorseClient}.
	 * @see GorseFactory#synchronous(URI, String)
	 */
	public static SynchronousGorseClient synchronous(String endpoint, String apiKey) {
		try {
			return synchronous(new URI(endpoint), apiKey);
		} catch (URISyntaxException exception) {
			logger.log(TRACE, "Failed to open synchronous client instance", exception);
			return null;
		}
	}
	
	/**
	 * Returns an instance of {@link SynchronousGorseClient}.
	 * @see GorseFactory#synchronous(String, String)
	 */
	public static SynchronousGorseClient synchronous(URI endpoint, String apiKey) {
		long uid = Instant.now().toEpochMilli();
		logger.log(INFO, "Opening synchronous client instance {0} @ {1}.", uid, endpoint);
		long start = threadBean.getCurrentThreadCpuTime();
		SynchronousGorseClient client = HttpClient.builder()
				.baseUrl(endpoint.toString())
				.bodyAdapter(new JsonbBodyAdapter(jsonb))
				.requestIntercept(new GorseInterceptor(apiKey))
				.build()
				.create(SynchronousGorseClient.class);
		long end = threadBean.getCurrentThreadCpuTime();
		logger.log(INFO, "Opened synchronous client instance {0} in {1}ns.", uid, (end - start));
		return client;
	}
	
	/**
	 * Returns an instance of {@link AsynchronousGorseClient}.
	 * @see GorseFactory#asynchronous(URI, String)
	 */
	public static AsynchronousGorseClient asynchronous(String endpoint, String apiKey) {
		try {
			return asynchronous(new URI(endpoint), apiKey);
		} catch (URISyntaxException exception) {
			logger.log(TRACE, "Failed to open synchronous client instance", exception);
			return null;
		}
	}
	
	/**
	 * Returns an instance of {@link AsynchronousGorseClient}.
	 * @see GorseFactory#asynchronous(URI, String, Executor)
	 */
	public static AsynchronousGorseClient asynchronous(URI endpoint, String apiKey) {
		return asynchronous(endpoint, apiKey, executor);
	}
	
	/**
	 * Returns an instance of {@link AsynchronousGorseClient}, using the provided {@link java.util.concurrent.Executor}.
	 * @see GorseFactory#asynchronous(URI, String)
	 */
	public static AsynchronousGorseClient asynchronous(URI endpoint, String apiKey, Executor executor) {
		long uid = Instant.now().toEpochMilli();
		logger.log(INFO, "Opening asynchronous client instance {0} @ {1}.", uid, endpoint);
		long start = threadBean.getCurrentThreadCpuTime();
		AsynchronousGorseClient client = HttpClient.builder()
				.baseUrl(endpoint.toString())
				.bodyAdapter(new JsonbBodyAdapter(jsonb))
				.requestIntercept(new GorseInterceptor(apiKey))
				.executor(executor)
				.build()
				.create(AsynchronousGorseClient.class);
		long end = threadBean.getCurrentThreadCpuTime();
		logger.log(INFO, "Opened asynchronous client instance {0} in {1}ns.", uid, (end - start));
		return client;
	}
}