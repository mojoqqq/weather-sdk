package io.github.mojoqqq.weathersdk.service;

import io.github.mojoqqq.weathersdk.model.Mode;
import io.github.mojoqqq.weathersdk.model.WeatherData;
import io.github.mojoqqq.weathersdk.web.OpenWeatherWebClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.github.mojoqqq.weathersdk.web.OpenWeatherWebClient.fetchWeatherFromAPI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class WeatherSDKTest {

    private static final String API_KEY = "test-api-key";
    private WeatherSDK weatherSDK;

    @BeforeEach
    void setUp() {
        WeatherSDK.instances.clear();
    }

    @Test
    void testOnDemand_CacheHit() {
        weatherSDK = WeatherSDK.create(API_KEY, Mode.ON_DEMAND);
        String city = "Moscow";
        WeatherData cachedWeather = createTestWeatherData(city);
        weatherSDK.getCache().put(city, cachedWeather);
        WeatherData result = weatherSDK.getWeather(city);
        assertNotNull(result);
        assertEquals(cachedWeather, result);
    }

    @Test
    void testOnDemand_CacheMiss() {
        weatherSDK = WeatherSDK.create(API_KEY, Mode.ON_DEMAND);
        String city = "Moscow";
        WeatherData apiWeather = createTestWeatherData(city);
        try (MockedStatic<OpenWeatherWebClient> mocked = mockStatic(OpenWeatherWebClient.class)) {
            mocked.when(() -> fetchWeatherFromAPI(any(), any())).thenReturn(apiWeather);
            WeatherData result = weatherSDK.getWeather(city);
            assertNotNull(result);
            assertEquals(apiWeather, result);
            WeatherData cachedResult = weatherSDK.getCache().getIfPresent(city);
            assertNotNull(cachedResult);
            assertEquals(apiWeather, cachedResult);
            mocked.verify(() -> fetchWeatherFromAPI(city, API_KEY), times(1));
        }
    }
    @Test
    void testPollingModeFlow(){
        weatherSDK = WeatherSDK.create(API_KEY,Mode.POLLING);

        WeatherData novgorodWeather = createTestWeatherData("Novgorod");
        WeatherData omskWeather = createTestWeatherData("Omsk");

        try ( MockedStatic<OpenWeatherWebClient> mockedStatic = mockStatic(OpenWeatherWebClient.class)) {

            mockedStatic.when(() -> fetchWeatherFromAPI(eq("Novgorod"), eq(API_KEY))).thenReturn(novgorodWeather);
            mockedStatic.when(() -> fetchWeatherFromAPI(eq("Omsk"), eq(API_KEY))).thenReturn(omskWeather);
            // Вызываем метод getWeather, чтобы данные добавились в кэш
            WeatherData resultNovgorod = weatherSDK.getWeather("Novgorod");
            WeatherData resultOmsk = weatherSDK.getWeather("Omsk");

            // Проверяем, что данные корректны
            assertNotNull(resultNovgorod);
            assertEquals(novgorodWeather, resultNovgorod);

            assertNotNull(resultOmsk);
            assertEquals(omskWeather, resultOmsk);

            // Проверяем, что метод fetchWeatherFromAPI был вызван для каждого города
            mockedStatic.verify(() -> fetchWeatherFromAPI(eq("Novgorod"), eq(API_KEY)), times(1));
            mockedStatic.verify(() -> fetchWeatherFromAPI(eq("Omsk"), eq(API_KEY)), times(1));
        }

    }

    private WeatherData createTestWeatherData(String city) {
        WeatherData weatherData = new WeatherData();
        weatherData.setName(city);

        WeatherData.Weather weather = new WeatherData.Weather();
        weather.setMain("Clouds");
        weather.setDescription("overcast clouds");
        weatherData.setWeather(weather);

        WeatherData.Temperature temperature = new WeatherData.Temperature();
        temperature.setTemp(15.0);
        temperature.setFeelsLike(13.5);
        weatherData.setTemperature(temperature);

        weatherData.setVisibility(10000);

        WeatherData.Wind wind = new WeatherData.Wind();
        wind.setSpeed(3.2);
        weatherData.setWind(wind);

        weatherData.setDatetime(System.currentTimeMillis() / 1000);

        WeatherData.Sys sys = new WeatherData.Sys();
        sys.setSunrise(1675751262L);
        sys.setSunset(1675787560L);
        weatherData.setSys(sys);

        weatherData.setTimezone(10800);

        return weatherData;
    }
}