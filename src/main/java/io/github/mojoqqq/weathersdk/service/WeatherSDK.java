package io.github.mojoqqq.weathersdk.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.mojoqqq.weathersdk.exceptions.OpenApiHttpException;
import io.github.mojoqqq.weathersdk.model.Mode;
import io.github.mojoqqq.weathersdk.model.WeatherData;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.github.mojoqqq.weathersdk.web.OpenWeatherWebClient.fetchWeatherFromAPI;
@Data
@Slf4j
public class WeatherSDK {

    protected static final Map<String, WeatherSDK> instances = new ConcurrentHashMap<>();
    private final String apiKey;
    protected final Cache<String, WeatherData> cache;
    protected final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    WeatherSDK(String apiKey, Mode mode) {
        this.apiKey = apiKey;
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(10)
                .removalListener((key, value, cause) -> {
                    log.info("[apikey:{}] Element {} was deleted from cache cause : {}", apiKey, key, cause);
                })
                .build();

        if (mode == Mode.POLLING) {
            startPolling();
        }
    }

    /**
     * @param apiKey API-ключ OpenWeather
     * @param mode   Режим работы SDK (ON_DEMAND или POLLING)
     * @return Экземпляр SDK
     */
    public static WeatherSDK create(String apiKey, Mode mode) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("API Key cannot be null or empty");
        }
        if (instances.containsKey(apiKey)) {
            throw new IllegalStateException("An instance with the same API key already exists");
        }
        WeatherSDK instance = new WeatherSDK(apiKey, mode);
        instances.put(apiKey, instance);
        return instance;
    }

    /**
     * @param city city name
     * @return weather info
     */
    public WeatherData getWeather(String city) {
        WeatherData cachedData = cache.getIfPresent(city);
        if (cachedData != null) {
            log.info("[apikey:{}] cache contain weather for city {}", apiKey, city);
            return cachedData;
        } else {
            log.info("[apikey:{}] updating weather for city {}", apiKey, city);
            WeatherData weatherData = fetchWeatherFromAPI(city, apiKey);
            if(weatherData != null) {
                cache.put(city, weatherData);
                return weatherData;
            } else return null;
        }
    }

    /**
     * polling mode scheduler
     */
    private void startPolling() {
        scheduler.scheduleAtFixedRate(() -> {
            log.info("[apikey:{}] Scheduler starting update weather for cities size {}", apiKey, cache.asMap().keySet().size());
            for (String city : cache.asMap().keySet()) {
                WeatherData weatherData = fetchWeatherFromAPI(city, apiKey);
                log.info("[apikey:{}] Updated weather for city {}", apiKey, city);
                cache.put(city, weatherData);
            }
        }, 0, 10, TimeUnit.MINUTES);
    }


    /**
     * POLLING STOPS
     */
    public void shutdown() {
        scheduler.shutdown();
    }

    /*Remove sdk by api key*/
    public static void removeInstance(String apiKey) {
        WeatherSDK instance = instances.remove(apiKey);
        if (instance != null) {
            instance.shutdown();
        }
    }


}