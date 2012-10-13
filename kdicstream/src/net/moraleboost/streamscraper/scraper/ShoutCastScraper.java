/*
 **
 **  Jul. 20, 2009
 **
 **  The author disclaims copyright to this source code.
 **  In place of a legal notice, here is a blessing:
 **
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 **
 **                                         Stolen from SQLite :-)
 **  Any feedback is welcome.
 **  Kohei TAKETA <k-tak@void.in>
 **
 */
package net.moraleboost.streamscraper.scraper;

import java.net.URI;
import java.util.List;

import net.moraleboost.streamscraper.ScrapeException;
import net.moraleboost.streamscraper.Scraper;
import net.moraleboost.streamscraper.Stream;
import net.moraleboost.streamscraper.fetcher.HttpFetcher;
import net.moraleboost.streamscraper.parser.ShoutCastFeedParser;
import net.moraleboost.streamscraper.parser.ShoutCastStatusPageParser;

public class ShoutCastScraper implements Scraper
{
    private HttpFetcher fetcher;
    private ShoutCastStatusPageParser statusPageParser;
    private ShoutCastFeedParser feedParser;
    
    public ShoutCastScraper()
    {
        fetcher = new HttpFetcher();
        statusPageParser = new ShoutCastStatusPageParser();
        feedParser = new ShoutCastFeedParser();
    }

    public HttpFetcher getFetcher()
    {
        return fetcher;
    }

    public void setFetcher(HttpFetcher fetcher)
    {
        this.fetcher = fetcher;
    }

    public ShoutCastStatusPageParser getStatusPageParser()
    {
        return statusPageParser;
    }

    public void setStatusPageParser(ShoutCastStatusPageParser statusPageParser)
    {
        this.statusPageParser = statusPageParser;
    }

    public ShoutCastFeedParser getFeedParser()
    {
        return feedParser;
    }

    public void setFeedParser(ShoutCastFeedParser feedParser)
    {
        this.feedParser = feedParser;
    }

    public List<Stream> scrape(URI uri) throws ScrapeException
    {
        try {
            byte[] status = fetcher.fetch(uri.resolve("/"));
            byte[] feed = fetcher.fetch(uri.resolve("/7.html"));
            
            List<Stream> statusStreams = statusPageParser.parse(uri, status);
            List<Stream> feedStreams = feedParser.parse(uri, feed);
            
            if (statusStreams.size() > 0 && feedStreams.size() > 0) {
                statusStreams.get(0).merge(feedStreams.get(0));
            }
            
            return statusStreams;
        } catch (Exception e) {
            throw new ScrapeException(e);
        }
    }
}
