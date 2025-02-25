package io.github.mojoqqq.weathersdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class CoordinatesRs {
    private String name;
    private String lat;
    private String lon;
    private String country;
    private String state;
}
