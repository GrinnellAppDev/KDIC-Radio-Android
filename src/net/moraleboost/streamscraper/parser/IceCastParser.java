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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import net.moraleboost.streamscraper.ParseException;
import net.moraleboost.streamscraper.Parser;
import net.moraleboost.streamscraper.Stream;
import net.moraleboost.streamscraper.util.CharsetUtils;
import net.moraleboost.streamscraper.util.JerichoHtmlUtils;

public class IceCastParser implements Parser
{
    private static final Pattern MOUNTPOINT_PATTERN_231 = Pattern.compile("^(.*)\\.m3u$");
    private static final Pattern MOUNTPOINT_PATTERN_232 = Pattern.compile("^Mount Point (.*)$");
    public static final String DEFAULT_CHARSET = "Shift_JIS";
    
    private String nonUnicodeCharset;

    public IceCastParser()
    {
        this.nonUnicodeCharset = DEFAULT_CHARSET;
    }
    
    public void setNonUnicodeCharset(String charset)
    {
        this.nonUnicodeCharset = charset;
    }

    public String getNonUnicodeCharset()
    {
        return nonUnicodeCharset;
    }
    
    public List<Stream> parse(URI uri, byte[] src) throws ParseException
    {
        try {
            CharsetDecoder utf8dec = CharsetUtils.createDecoder(
                    "UTF-8", CodingErrorAction.IGNORE, CodingErrorAction.IGNORE);
            CharsetDecoder nudec = CharsetUtils.createDecoder(
                    nonUnicodeCharset, CodingErrorAction.IGNORE, CodingErrorAction.IGNORE);
            
            Source utf8src = new Source(CharsetUtils.decode(utf8dec, src));
            Source nusrc = new Source(CharsetUtils.decode(nudec, src));
            
            utf8src.fullSequentialParse();
            nusrc.fullSequentialParse();
            
            List<Stream> utf8streams = new LinkedList<Stream>();
            List<Stream> sjisstreams = new LinkedList<Stream>();
            
            parseSouce(uri, utf8src, utf8streams);
            parseSouce(uri, nusrc, sjisstreams);
            
            return mergeStreams(utf8streams, sjisstreams);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }
    
    private void parseSouce(URI uri, Source src, List<Stream> streams) throws ParseException
    {
        List<Element> containers =
            JerichoHtmlUtils.findAllElements(
                    src, HTMLElementName.DIV, "class", "newscontent");
        for (Element container: containers) {
            parseContainer(uri, container, streams);
        }
    }
    
    private void parseContainer(URI uri, Element container, List<Stream> streams)
    {
        Stream stream = new Stream();
        
        parseMountPoint(uri, container, stream);
        parseAttributes(uri, container, stream);
        
        streams.add(stream);
    }
    
    private void parseMountPoint(URI uri, Element container, Stream stream)
    {
        String mountPoint = parseMountPoint231(container);
        if (mountPoint == null) {
            mountPoint = parseMountPoint232(container);
        }
        
        if (mountPoint == null) return;
        
        stream.setUri(uri.resolve(mountPoint));
    }
    
    private String parseMountPoint231(Element container)
    {
        Element h3 = JerichoHtmlUtils.findFirstChildElement(container, HTMLElementName.H3);
        if (h3 == null) return null;
        
        Element a = JerichoHtmlUtils.findFirstChildElement(h3, HTMLElementName.A);
        if (a == null) return null;
        
        String value = a.getAttributeValue("href");
        if (value == null) return null;
        
        Matcher m = MOUNTPOINT_PATTERN_231.matcher(value);
        if (!m.matches()) return null;
        
        return m.group(1);
    }
    
    private String parseMountPoint232(Element container)
    {
        Element header =
            JerichoHtmlUtils.findFirstElement(container, HTMLElementName.DIV, "class", "streamheader");
        if (header == null) return null;
        
        Element table = JerichoHtmlUtils.findFirstChildElement(header, HTMLElementName.TABLE);
        if (table == null) return null;
        
        Element tr = JerichoHtmlUtils.findFirstChildElement(table, HTMLElementName.TR);
        if (tr == null) return null;
        
        Element td = JerichoHtmlUtils.findFirstChildElement(tr, HTMLElementName.TD);
        if (td == null) return null;
        
        Element h3 = JerichoHtmlUtils.findFirstChildElement(td, HTMLElementName.H3);
        if (h3 == null) return null;
        
        String value = h3.getTextExtractor().toString();
        if (value == null) return null;
        
        Matcher m = MOUNTPOINT_PATTERN_232.matcher(value);
        if (!m.matches()) return null;
        
        return m.group(1);
    }
    
    private void parseAttributes(URI uri, Element container, Stream stream)
    {
        Element table =
            JerichoHtmlUtils.findFirstChildElement(container, HTMLElementName.TABLE);
        if (table == null) return;
        
        List<Element> rows =
            JerichoHtmlUtils.findAllChildElement(table, HTMLElementName.TR);
        for (Element row: rows) {
            List<Element> cols =
                JerichoHtmlUtils.findAllChildElement(row, HTMLElementName.TD);
            if (cols.size() != 2) continue;
            
            String name = cols.get(0).getTextExtractor().toString();
            String value = cols.get(1).getTextExtractor().toString();
            
            if (name.equalsIgnoreCase("Stream Title:")) {
                stream.setTitle(value);
            } else if (name.equalsIgnoreCase("Stream Description:")) {
                stream.setDescription(value);
            } else if (name.equalsIgnoreCase("Content Type:")) {
                stream.setContentType(value);
            } else if (name.equalsIgnoreCase("Bitrate:")) {
                stream.setBitRate(value);
            } else if (name.equalsIgnoreCase("Current Listeners:")) {
                try {
                    stream.setCurrentListenerCount(Integer.parseInt(value));
                } catch (NumberFormatException e) {}
            } else if (name.equalsIgnoreCase("Peak Listeners:")) {
                try {
                    stream.setPeakListenerCount(Integer.parseInt(value));
                } catch (NumberFormatException e) {}
            } else if (name.equalsIgnoreCase("Stream Genre:")) {
                stream.setGenre(value);
            } else if (name.equalsIgnoreCase("Current Song:")) {
                stream.setCurrentSong(value);
            }
        }
    }
    
    private List<Stream> mergeStreams(List<Stream> utf8streams, List<Stream> nustreams)
    {
        List<Stream> result = new LinkedList<Stream>();
        
        for (Stream nu: nustreams) {
            if (isNonUnicode(nu.getContentType())) {
                result.add(nu);
            }
        }
        for (Stream utf8: utf8streams) {
            if (!isNonUnicode(utf8.getContentType())) {
                result.add(utf8);
            }
        }
        
        return result;
    }
    
    private boolean isNonUnicode(String contentType)
    {
        return (contentType != null && (contentType
                .equalsIgnoreCase("audio/mpeg") || contentType
                .equalsIgnoreCase("audio/aacp"))) ? true : false;
    }
}
