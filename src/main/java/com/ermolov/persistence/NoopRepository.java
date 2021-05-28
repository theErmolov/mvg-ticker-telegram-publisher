package com.ermolov.persistence;

import com.ermolov.rss.TickerItem;

public class NoopRepository implements TickerItemRepository {
    @Override
    public boolean isNotProcessed(TickerItem tickerItem) {
        return true;
    }

    @Override
    public void persist(TickerItem tickerItem, boolean success, String errorMessage) {
    }
}
