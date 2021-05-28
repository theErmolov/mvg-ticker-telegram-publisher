package com.ermolov.telegram;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SendMessageRequest {
    private String chatId;
    private String parseMode;
    private String text;
    private String disableWebPagePreview;

    public SendMessageRequest(String chatId, String parseMode, String text, String disableWebPagePreview) {
        this.chatId = chatId;
        this.parseMode = parseMode;
        this.text = text;
        this.disableWebPagePreview = disableWebPagePreview;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getParseMode() {
        return parseMode;
    }

    public void setParseMode(String parseMode) {
        this.parseMode = parseMode;
    }

    public String getDisableWebPagePreview() {
        return disableWebPagePreview;
    }

    public void setDisableWebPagePreview(String disableWebPagePreview) {
        this.disableWebPagePreview = disableWebPagePreview;
    }
}