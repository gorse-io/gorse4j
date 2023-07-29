import io.avaje.http.client.HttpClient;
import io.avaje.jsonb.Jsonb;

module io.gorse.gorse4j {
	requires java.management;
	
	requires io.avaje.jsonb;
	requires io.avaje.http.client;
	requires io.avaje.http.api;
	
	requires redis.clients.jedis;
	
	// TODO (start) - replace these with module references when https://github.com/testcontainers/testcontainers-java/issues/7337 is merged
	requires testcontainers;
	requires postgresql;
	// (end)
	
	provides Jsonb.GeneratedComponent with io.gorse.gorse4j.jsonb.GeneratedJsonComponent;
	provides HttpClient.GeneratedComponent with io.gorse.gorse4j.httpclient.GeneratedHttpComponent;
	
	exports io.gorse.gorse4j;
}