package io.github.mojoqqq.weathersdk.exceptions;

public class OpenApiHttpException extends RuntimeException {
    public OpenApiHttpException(String message) {
        super(message);
    }

    public OpenApiHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
