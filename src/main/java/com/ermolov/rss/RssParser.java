package com.ermolov.rss;

import org.apache.commons.text.StringEscapeUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class RssParser {
    private static final String ITEM = "item";
    private static final String TITLE = "title";
    private static final String LINK = "link";
    private static final String DESCRIPTION = "description";
    private static final String GUID = "guid";

    private final XMLInputFactory xmlInputFactory;

    public RssParser() {
        this.xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setProperty("javax.xml.stream.isCoalescing", true);
    }

    public List<TickerItem> parseRss(String xml) {
        var resultFeed = new ArrayList<TickerItem>();
        try {
            var title = "";
            var link = "";
            var description = "";
            var guid = "";

            var xmlEventReader = xmlInputFactory.createXMLEventReader(new StringReader(xml));
            while (xmlEventReader.hasNext()) {
                var xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    var localPart = xmlEvent.asStartElement().getName().getLocalPart();
                    switch (localPart) {
                        case TITLE:
                            title = getText(xmlEventReader);
                            break;
                        case DESCRIPTION:
                            description = processDescription(getText(xmlEventReader));
                            break;
                        case LINK:
                            link = getText(xmlEventReader);
                            break;
                        case GUID:
                            guid = extractGuid(getText(xmlEventReader));
                            break;
                    }
                } else if (xmlEvent.isEndElement() && ITEM.equals(xmlEvent.asEndElement().getName().getLocalPart())) {
                    resultFeed.add(new TickerItem(title, link, description, guid));
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException("Couldn't parse RSS feed", e);
        }

        return resultFeed;
    }

    private String extractGuid(String guid) {
        int lastSlash = guid.lastIndexOf("/");
        guid = guid.substring(lastSlash + 1);
        return guid;
    }

    private String processDescription(String textRaw) {
        return StringEscapeUtils.unescapeHtml4(textRaw
                .replaceAll("</div> ", "\n")
                .replaceAll("<br/>", "\n\n")
                .replaceAll("</a>", "SLASH-A")
                .replaceAll("<[^a][^>]*>", "")
                .replaceAll("SLASH-A", "</a>")
        );
    }

    private String getText(XMLEventReader xmlEventReader) throws XMLStreamException {
        var event = xmlEventReader.nextEvent();
        if (event instanceof Characters) {
            return event.asCharacters().getData();
        }

        return "";
    }
}
