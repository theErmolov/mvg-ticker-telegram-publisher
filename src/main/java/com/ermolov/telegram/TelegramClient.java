package com.ermolov.telegram;

import com.ermolov.rss.TickerItem;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TelegramClient {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final String apiToken;
    private final String chatId;

    private final HttpClient httpClient;

    public TelegramClient(String apiToken, String chatId, HttpClient httpClient) {
        this.apiToken = apiToken;
        this.chatId = chatId;
        this.httpClient = httpClient;
    }

    public SendMessageResponse publish(TickerItem tickerItem) {
        var messageText = String.format("<b><a href=\"%s\">%s</a></b>\n\n%s",
                tickerItem.getLink(),
                tickerItem.getTitle(),
                tickerItem.getDescription());
        var telegramRequest = new SendMessageRequest(chatId, "HTML", messageText, "true");

        try {
            var httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.telegram.org/bot" + apiToken + "/sendMessage"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(telegramRequest)))
                    .build();
            String body = httpClient
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString())
                    .body();

            return OBJECT_MAPPER.readValue(body, SendMessageResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("Couldn't send message to Telegram", e);
        }
    }
}
