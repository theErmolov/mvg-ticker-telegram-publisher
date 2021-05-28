package com.ermolov.persistence;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.ermolov.rss.TickerItem;

import java.time.Clock;
import java.time.Duration;

public class DynamoDbRepository implements TickerItemRepository {

    private static final long TTL = Duration.ofDays(30).toSeconds();

    private final Table tableAccessor;

    public DynamoDbRepository(String tableName) {
        var dynamoDbClient = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.EU_CENTRAL_1)
                .build();

        this.tableAccessor = new DynamoDB(dynamoDbClient).getTable(tableName);
    }

    @Override
    public boolean isNotProcessed(TickerItem tickerItem) {
        return tableAccessor.getItem(new PrimaryKey("guid", tickerItem.getGuid())) == null;
    }

    @Override
    public void persist(TickerItem tickerItem, boolean success, String errorMessage) {
        var itemToSave = new Item().withPrimaryKey("guid", tickerItem.getGuid())
                .withBoolean("success", success);
        var now = Clock.systemUTC().instant().getEpochSecond();
        if (success) {
            itemToSave.withNumber("expiryDate", now + TTL);
        } else {
            itemToSave = itemToSave
                    .withNumber("timestamp", now)
                    .withString("error", errorMessage)
                    .withString("sourceMessage", tickerItem.toString());
        }
        tableAccessor.putItem(itemToSave);
    }
}
