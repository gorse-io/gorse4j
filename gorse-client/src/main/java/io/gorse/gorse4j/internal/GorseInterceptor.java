package io.gorse.gorse4j.internal;

import io.avaje.http.client.HttpClientRequest;
import io.avaje.http.client.RequestIntercept;

/**
 * Implementation of {@link RequestIntercept} that adds the required headers to Gorse requests.
 */
public final class GorseInterceptor implements RequestIntercept {
	
	private final String apiKey;
	
	public GorseInterceptor(String apiKey) {
		this.apiKey = apiKey;
	}
	
	@Override
	public void beforeRequest(HttpClientRequest request) {
		request.header("X-API-KEY", this.apiKey);
	}
}