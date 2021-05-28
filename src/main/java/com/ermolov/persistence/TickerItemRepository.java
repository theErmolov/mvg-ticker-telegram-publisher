package com.ermolov.persistence;

import com.ermolov.rss.TickerItem;

public interface TickerItemRepository {
    boolean isNotProcessed(TickerItem tickerItem);

    void persist(TickerItem tickerItem, boolean success, String errorMessage);
}
