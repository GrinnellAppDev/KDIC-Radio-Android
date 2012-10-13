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
package net.moraleboost.streamscraper.parser;

import java.net.URI;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.LinkedList;
import java.util.List;

import net.htmlparser.jericho.Source;
import net.moraleboost.streamscraper.ParseException;
import net.moraleboost.streamscraper.Parser;
import net.moraleboost.streamscraper.Stream;
import net.moraleboost.streamscraper.util.CharsetUtils;

public class ShoutCastFeedParser implements Parser
{
    public static final String DEFAULT_CHARSET = "Shift_JIS";
    
    private String charset;

    public ShoutCastFeedParser()
    {
        this.charset = DEFAULT_CHARSET;
    }
    
    public void setCharset(String charset)
    {
        this.charset = charset;
    }

    public String getCharset()
    {
        return charset;
    }

    public List<Stream> parse(URI uri, byte[] src) throws ParseException
    {
        try {
            CharsetDecoder dec = CharsetUtils.createDecoder(
                    charset, CodingErrorAction.IGNORE, CodingErrorAction.IGNORE);
            Source usrc = new Source(CharsetUtils.decode(dec, src));
            
            usrc.fullSequentialParse();
            
            return parseSource(uri, usrc);
        } catch (ParseException e) {
            throw e;
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }
    
    private List<Stream> parseSource(URI uri, Source src) throws ParseException
    {
        String line = src.getTextExtractor().toString();
        String[] attrs = line.split(",", 8);
        if (attrs.length != 7) {
            return new LinkedList<Stream>();
        }
        
        // attrs[0] : curListeners
        // attrs[1] : server status (0=down, 1=up)
        // attrs[2] : listener peak
        // attrs[3] : max listeners
        // attrs[4] : unique listeners
        // attrs[5] : bit rate
        // attrs[6] : song title

        Stream stream = new Stream();
        stream.setUri(uri.resolve("/"));
        try {
            stream.setCurrentListenerCount(Integer.parseInt(attrs[0]));
        } catch (NumberFormatException e) {}
        try {
            int status = Integer.parseInt(attrs[1]);
            if (status == 0) {
                throw new ParseException("Server is down.");
            }
        } catch (NumberFormatException e) {}
        try {
            stream.setPeakListenerCount(Integer.parseInt(attrs[2]));
        } catch (NumberFormatException e) {}
        try {
            stream.setMaxListenerCount(Integer.parseInt(attrs[3]));
        } catch (NumberFormatException e) {}
        stream.setBitRate(attrs[5]);
        stream.setCurrentSong(attrs[6]);
        
        List<Stream> ret = new LinkedList<Stream>();
        ret.add(stream);
        return ret;
    }
}
