package com.ermolov;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.ermolov.persistence.DynamoDbRepository;
import com.ermolov.persistence.NoopRepository;
import com.ermolov.persistence.TickerItemRepository;
import com.ermolov.rss.MvgTickerClient;
import com.ermolov.rss.RssParser;
import com.ermolov.telegram.SendMessageResponse;
import com.ermolov.telegram.TelegramClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;

public class MvgTickerTelegramPublisher implements RequestHandler<Map<String, String>, String> {
    private static final String TABLE_NAME_KEY = "tableName";
    private static final String API_TOKEN_KEY = "telegramApiToken";
    private static final String CHAT_ID_KEY = "telegramChatId";

    @Override
    public String handleRequest(Map<String, String> parameters, Context context) {
        if (!parameters.containsKey(API_TOKEN_KEY) || !parameters.containsKey(CHAT_ID_KEY)) {
            throw new RuntimeException("API Token or chatId were not provided in input request");
        }

        new Handler(parameters).run();

        return null;
    }

    private static class Handler {
        private final MvgTickerClient mvgTickerClient;
        private final RssParser rssParser;
        private final TelegramClient telegramClient;
        private final TickerItemRepository tickerItemRepository;

        public Handler(Map<String, String> parameters) {
            var tableName = parameters.get(TABLE_NAME_KEY);
            var httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(3))
                    .build();

            this.mvgTickerClient = new MvgTickerClient(httpClient);
            this.rssParser = new RssParser();
            this.telegramClient = new TelegramClient(parameters.get(API_TOKEN_KEY), parameters.get(CHAT_ID_KEY), httpClient);
            this.tickerItemRepository = tableName == null ? new NoopRepository() : new DynamoDbRepository(tableName);
        }

        private void run() {
            rssParser.parseRss(mvgTickerClient.sendRequestToMvgTicker())
                    .stream()
                    .filter(tickerItemRepository::isNotProcessed)
                    .forEach(this::processItem);
        }

        private void processItem(com.ermolov.rss.TickerItem tickerItem) {
            SendMessageResponse result = telegramClient.publish(tickerItem);
            if (result != null) {
                var success = "true".equals(result.getOk());
                if (success) {
                    System.out.printf("Message with guid %s has been processed successfully%n", tickerItem.getGuid());
                } else {
                    System.out.printf("Message with guid %s has been rejected by Telegram with error message '%s'%n",
                            tickerItem.getGuid(),
                            result.getDescription());
                }

                tickerItemRepository.persist(tickerItem, success, result.getDescription());
            }
        }
    }
}
