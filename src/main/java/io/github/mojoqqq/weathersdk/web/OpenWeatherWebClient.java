package io.github.mojoqqq.weathersdk.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mojoqqq.weathersdk.exceptions.OpenApiHttpException;
import io.github.mojoqqq.weathersdk.model.CoordinatesRs;
import io.github.mojoqqq.weathersdk.model.OpenWeatherRs;
import io.github.mojoqqq.weathersdk.model.WeatherData;
import io.github.mojoqqq.weathersdk.util.WeatherTransformer;
import jakarta.ws.rs.core.UriBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

public class OpenWeatherWebClient {
    private static final ObjectMapper om = new ObjectMapper();
    private static final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    private static void configure() {
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static WeatherData fetchWeatherFromAPI(String city, String apiKey) {
        configure();
        CoordinatesRs coordinatesByCityName = null;
        try {
            coordinatesByCityName = getCoordinatesByCityName(city, apiKey);
            OpenWeatherRs weatherByCoordinates = getWeatherByCoordinates(coordinatesByCityName, apiKey);
            return WeatherTransformer.transform(weatherByCoordinates);
        } catch (Exception e ) {
            e.printStackTrace();
        }
        return null;
    }


    private static CoordinatesRs getCoordinatesByCityName(String city, String apiKey) throws Exception {
        URI uri = UriBuilder.fromUri("http://api.openweathermap.org/geo/1.0/direct")
                .queryParam("q", city).queryParam("limit", 1).queryParam("appid", apiKey).build();
        HttpRequest rq = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(rq, HttpResponse.BodyHandlers.ofString());
        Optional<Integer> possibleError = handleErrorCodes(response.statusCode());
        if (possibleError.isEmpty()) {
            CoordinatesRs[] coordinatesRs = om.readValue(response.body(), CoordinatesRs[].class);
            if (coordinatesRs.length > 0) return coordinatesRs[0];
            else throw new OpenApiHttpException("OpenWeather cannot find coordinates for:" + city);
        } else {
            if (String.valueOf(possibleError.get()).startsWith("4")) {
                throw new OpenApiHttpException("OpenWeather API throws an client-side 4xx exception : \n" + response.body());
            } else
                throw new OpenApiHttpException("Unknown exception. OpenWeather api returns http code : " + response.statusCode());
        }
    }

    private static OpenWeatherRs getWeatherByCoordinates(CoordinatesRs coordinates, String apiKey) throws Exception {
        URI uri = UriBuilder.fromUri("https://api.openweathermap.org/data/2.5/weather")
                .queryParam("lat", coordinates.getLat())
                .queryParam("lon", coordinates.getLon())
                .queryParam("appid", apiKey)
                .build();
        HttpRequest rq = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(rq, HttpResponse.BodyHandlers.ofString());
        Optional<Integer> possibleError = handleErrorCodes(response.statusCode());

        if (possibleError.isEmpty()) {
            return om.readValue(response.body(), OpenWeatherRs.class);
        } else {
            if (String.valueOf(possibleError.get()).startsWith("4")) {
                throw new OpenApiHttpException("OpenWeather API throws an client-side 4xx exception : \n" + response.body());
            } else
                throw new OpenApiHttpException("Unknown exception. OpenWeather api returns http code : " + response.statusCode());
        }
    }

    private static Optional<Integer> handleErrorCodes(Integer statusCode) {
        if (statusCode == 200) return Optional.empty();
        else return Optional.of(statusCode);
    }
}
