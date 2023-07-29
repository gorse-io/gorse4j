package io.gorse.gorse4j;

import io.avaje.http.client.HttpClient;
import io.avaje.http.client.HttpClientRequest;
import io.avaje.http.client.JsonbBodyAdapter;
import io.avaje.http.client.RequestIntercept;
import io.avaje.jsonb.Jsonb;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPooled;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.Logger.Level.*;

/**
 * Factory class to obtain instances of {@link SynchronousGorseClient}/{@link AsynchronousGorseClient}.
 */
public final class GorseFactory {

	private static final System.Logger logger = System.getLogger("io.gorse.gorse4j");
	private static final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
	private static final Jsonb jsonb = Jsonb.builder().build();
	private static final ExecutorService executor = Executors.newCachedThreadPool();
	
	/**
	 * Returns an instance of {@link SynchronousGorseClient}.
	 * @see GorseFactory#synchronous(URL, String)
	 */
	public static SynchronousGorseClient synchronous(String endpoint, String apiKey) {
		try {
			return synchronous(new URL(endpoint), apiKey);
		} catch (MalformedURLException exception) {
			logger.log(TRACE, "Failed to open synchronous client instance", exception);
			return null;
		}
	}
	
	/**
	 * Returns an instance of {@link SynchronousGorseClient}.
	 * @see GorseFactory#synchronous(String, String)
	 */
	public static SynchronousGorseClient synchronous(URL endpoint, String apiKey) {
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
	 * @see GorseFactory#asynchronous(URL, String)
	 */
	public static AsynchronousGorseClient asynchronous(String endpoint, String apiKey) {
		try {
			return asynchronous(new URL(endpoint), apiKey);
		} catch (MalformedURLException exception) {
			logger.log(TRACE, "Failed to open synchronous client instance", exception);
			return null;
		}
	}
	
	/**
	 * Returns an instance of {@link AsynchronousGorseClient}.
	 * @see GorseFactory#asynchronous(URL, String, Executor)
	 */
	public static AsynchronousGorseClient asynchronous(URL endpoint, String apiKey) {
		return asynchronous(endpoint, apiKey, executor);
	}
	
	/**
	 * Returns an instance of {@link AsynchronousGorseClient}, using the provided {@link java.util.concurrent.Executor}.
	 * @see GorseFactory#asynchronous(URL, String)
	 */
	public static AsynchronousGorseClient asynchronous(URL endpoint, String apiKey, Executor executor) {
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
	
	/**
	 * Runs a mock server on the provided port, accepting the provided key.
	 * Running this method is an extremely heavy operation and should only take place in tests.
	 * @return An instance of {@link GorseServer} that can be used to obtain client instances
	 * that connect to the mock server for testing. When writing tests, you should call the
	 * {@link GorseServer#close()} method when running cleanup.
	 */
	public static GorseServer mock() {
		DockerImageName name = DockerImageName.parse("zhenghaoz/gorse-in-one:0.4.14");
		
		JedisPooled jedis = new JedisPooled("127.0.0.1", 6379);
		PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres"));
		GenericContainer<?> container = new GenericContainer<>(name)
				.withExposedPorts(8086, 8088)
				.withEnv("GORSE_CACHE_STORE", "redis://127.0.0.1:6379")
				.withEnv("GORSE_DATA_STORE", "postgresql://" + postgres.getUsername() + ":" + postgres.getPassword() + "@" + postgres.getHost() + ":" + postgres.getFirstMappedPort() + "/" + postgres.getDatabaseName());
		return new GorseMock(container, jedis, postgres);
	}
}

/**
 * Implementation of {@link GorseServer}.
 */
final class GorseMock implements GorseServer {
	
	private final AutoCloseable gorse;
	private final JedisPooled jedis;
	private final AutoCloseable[] others;
	
	public GorseMock(AutoCloseable gorse, JedisPooled jedis, AutoCloseable... others) {
		this.gorse = gorse;
		this.jedis = jedis;
		this.others = others;
	}
	
	@Override
	public JedisPooled jedis() {
		return jedis;
	}
	
	@Override
	public SynchronousGorseClient synchronous() {
		return GorseFactory.synchronous("http://127.0.0.1:8088", "mock");
	}
	
	@Override
	public AsynchronousGorseClient asynchronous() {
		return GorseFactory.asynchronous("http://127.0.0.1:8088", "mock");
	}
	
	@Override
	public void close() throws Exception {
		this.gorse.close();
		for (AutoCloseable other : this.others) other.close();
	}
}

/**
 * Implementation of {@link RequestIntercept} that adds the required headers to Gorse requests.
 */
final class GorseInterceptor implements RequestIntercept {
	
	private final String apiKey;
	
	public GorseInterceptor(String apiKey) {
		this.apiKey = apiKey;
	}
	
	@Override
	public void beforeRequest(HttpClientRequest request) {
		request.header("X-API-KEY", this.apiKey);
	}
}