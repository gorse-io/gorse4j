package io.gorse.gorse4j.test;

import io.avaje.http.client.HttpClient;
import io.avaje.http.client.JsonbBodyAdapter;
import io.avaje.jsonb.Jsonb;
import io.gorse.gorse4j.AsynchronousGorseClient;
import io.gorse.gorse4j.SynchronousGorseClient;
import io.gorse.gorse4j.internal.GorseInterceptor;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Factory class to obtain instances of {@link SynchronousGorseClient}/{@link AsynchronousGorseClient}
 * inside of tests, spinning up containers using TestContainers.
 * <p>
 * Instances of this should be obtained with {@link GorseTestFactory#create()} ()}, and they should be
 * shared across all tests before eventually calling {@link GorseTestFactory#close()} to terminate the
 * mock server setup - they should be shared in order to save memory as this is an extremely heavy class
 * to instantiate.
 * <p>
 * Obtained client instances through this factory should be stored as local variables and obtained for
 * each test individually, as this helps keep potential issues to the scope of a single test. For example,
 * if a provided executor is invalid for some reason after passing it through.
 */
public final class GorseTestFactory implements AutoCloseable {
	
	private final Jsonb jsonb;
	private final ExecutorService executor;
	
    private final GenericContainer<?> redis;
	private final GenericContainer<?> sql;
	private final GenericContainer<?> gorse;
	
	private GorseTestFactory() {
		this.jsonb = Jsonb.builder().build();
		this.executor = Executors.newCachedThreadPool();
		
		this.redis = new GenericContainer<>("redis:latest")
            .withExposedPorts(6379);
		String redisHost = this.redis.getHost();
		int redisPort = this.redis.getFirstMappedPort();
		
		this.sql = new GenericContainer<>("mysql/mysql-server:latest")
            .withExposedPorts(3306)
            .withEnv("MYSQL_ROOT_PASSWORD", "root_pass")
            .withEnv("MYSQL_DATABASE", "gorse")
            .withEnv("MYSQL_USER", "gorse")
            .withEnv("MYSQL_PASSWORD", "gorse_pass");
		String sqlHost = this.sql.getHost();
		int sqlPort = this.sql.getFirstMappedPort();
		
		this.gorse = new GenericContainer<>("zhenghaoz/gorse-in-one:latest")
            .withExposedPorts(8086, 8088)
            .withEnv("GORSE_CACHE_STORE", String.format("redis://%s:%d", redisHost, redisPort))
            .withEnv("GORSE_DATA_STORE", String.format("mysql://gorse:gorse_pass@tcp(%s:%d)/gorse?parseTime=true", sqlHost, sqlPort))
            .withCommand(
					"-c", "/etc/gorse/config.toml",
					"--log-path", "/var/log/gorse/master.log",
					"--cache-path", "/var/lib/gorse/master_cache.data"
			)
            .withClasspathResourceMapping(
					"io/gorse/gorse4j/resources/config.toml",
					"/etc/gorse/config.toml",
					BindMode.READ_ONLY
			);
		
		this.redis.start();
		this.sql.start();
		this.gorse.start();
	}
	
	/**
	 * Create an instance of the test factory.
	 * @throws IllegalStateException Thrown if TestContainers throws while attempting to spin up containers.
	 */
	public static GorseTestFactory create() {
		try {
			return new GorseTestFactory();
		} catch (Throwable throwable) {
			throw new IllegalStateException("GorseTestFactory#create() failed - could not create containers (is Docker installed correctly?)", throwable);
		}
	}
	
	/**
	 * Returns a mock instance of {@link SynchronousGorseClient}.
	 */
	public SynchronousGorseClient synchronous() {
		return HttpClient.builder()
				.baseUrl(String.format("http://%s:%d", this.gorse.getHost(), this.gorse.getFirstMappedPort()))
				.bodyAdapter(new JsonbBodyAdapter(jsonb))
				.requestIntercept(new GorseInterceptor("zhenghaoz"))
				.build()
				.create(SynchronousGorseClient.class);
	}
	
	/**
	 * Returns a mock instance of {@link AsynchronousGorseClient}.
	 */
	public AsynchronousGorseClient asynchronous() {
		return asynchronous(executor);
	}
	
	/**
	 * Returns a mock instance of {@link AsynchronousGorseClient}, using the provided {@link java.util.concurrent.Executor}.
	 */
	public AsynchronousGorseClient asynchronous(Executor executor) {
		return HttpClient.builder()
				.baseUrl(String.format("http://%s:%d", this.gorse.getHost(), this.gorse.getFirstMappedPort()))
				.bodyAdapter(new JsonbBodyAdapter(jsonb))
				.requestIntercept(new GorseInterceptor("zhenghaoz"))
				.executor(executor)
				.build()
				.create(AsynchronousGorseClient.class);
	}
	
	@Override
	public void close() {
		this.gorse.close();
		this.sql.close();
		this.redis.close();
	}
}