import io.avaje.http.client.HttpClient;
import io.avaje.jsonb.Jsonb;

module io.gorse.gorse4j {
	requires java.management;
	
	requires io.avaje.jsonb;
	requires io.avaje.http.client;
	requires io.avaje.http.api;
	
	provides Jsonb.GeneratedComponent with io.gorse.gorse4j.model.jsonb.GeneratedJsonComponent;
	provides HttpClient.GeneratedComponent with io.gorse.gorse4j.httpclient.GeneratedHttpComponent;
	
	exports io.gorse.gorse4j;
	exports io.gorse.gorse4j.model;
}