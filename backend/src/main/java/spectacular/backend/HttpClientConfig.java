package spectacular.backend;

import static org.apache.http.impl.client.cache.CacheConfig.DEFAULT_MAX_CACHE_ENTRIES;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Configuration
public class HttpClientConfig {
  /**
   * A bean factory function for the producing the required cache configuration.
   *
   * @return a CacheConfig bean with the required cache config values
   */
  @Bean
  public CacheConfig cacheConfigFactory() {
    CacheConfig result = CacheConfig
        .custom()
        .setMaxCacheEntries(DEFAULT_MAX_CACHE_ENTRIES)
        .setSharedCache(false)
        .build();
    return result;
  }

  /**
   * A bean factory function for creating a CloseableHttpClient configured to cache any cache-able http request responses.
   *
   * @param cacheConfig the cache configuration
   * @return a CloseableHttpClient that will cache http response
   */
  @Bean
  public CloseableHttpClient httpClientFactory(CacheConfig cacheConfig) {
    return CachingHttpClients.custom()
        .setCacheConfig(cacheConfig)
        .build();
  }

  /**
   * A bean factory function for creating a HttpComponentsClientHttpRequestFactory configured to use our caching CloseableHttpClient.
   *
   * @param httpClient the CloseableHttpClient to be used when creating new http requests
   * @return a HttpComponentsClientHttpRequestFactory configured to created http requests who's responses that will be cached
   */
  @Bean
  public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactoryFactory(CloseableHttpClient httpClient) {
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    requestFactory.setHttpClient(httpClient);
    return requestFactory;
  }
}
