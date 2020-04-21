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
  @Bean
  public CacheConfig cacheConfigFactory() {
    CacheConfig result = CacheConfig
        .custom()
        .setMaxCacheEntries(DEFAULT_MAX_CACHE_ENTRIES)
        .setSharedCache(false)
        .build();
    return result;
  }

  @Bean
  public CloseableHttpClient httpClientFactory(CacheConfig cacheConfig) {
    return CachingHttpClients.custom()
        .setCacheConfig(cacheConfig)
        .build();
  }

  @Bean
  public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactoryFactory(
      CloseableHttpClient httpClient) {
    HttpComponentsClientHttpRequestFactory requestFactory =
        new HttpComponentsClientHttpRequestFactory();
    requestFactory.setHttpClient(httpClient);
    return requestFactory;
  }
}
