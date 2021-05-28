package com.ermolov.rss;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MvgTickerClient {

    private final HttpClient httpClient;

    public MvgTickerClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String sendRequestToMvgTicker() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://ticker.mvg.de/"))
                .build();
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't request data from MVG", e);
        }
    }
}
