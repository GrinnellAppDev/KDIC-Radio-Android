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
package net.moraleboost.streamscraper;

import java.io.Serializable;
import java.net.URI;

@SuppressWarnings("serial")
public class Stream implements Serializable
{
    private String title;
    private String description;
    private URI uri;
    private int currentListenerCount;
    private int maxListenerCount;
    private int peakListenerCount;
    private String bitRate;
    private String currentSong;
    private String contentType;
    private String genre;
    
    public Stream()
    {
        clear();
    }
    
    public String toString()
    {
        return ("Stream(title=" + title +
                ", desc=" + description +
                ", uri=" + uri +
                ", lc=" + currentListenerCount +
                ", mlc=" + maxListenerCount +
                ", plc=" + peakListenerCount +
                ", br=" + bitRate +
                ", song=" + currentSong +
                ", mime=" + contentType +
                ", genre=" + genre + ")");
    }
    
    public void clear()
    {
        title = null;
        description = null;
        uri = null;
        currentListenerCount = -1;
        maxListenerCount = -1;
        peakListenerCount = -1;
        bitRate = null;
        currentSong = null;
        contentType = null;
        genre = null;
    }
    
    public void merge(Stream another)
    {
        if (title == null) {
            title = another.getTitle();
        }
        if (description == null) {
            description = another.getDescription();
        }
        if (uri == null) {
            uri = another.getUri();
        }
        if (currentListenerCount < 0) {
            currentListenerCount = another.getCurrentListenerCount();
        }
        if (maxListenerCount < 0) {
            maxListenerCount = another.getMaxListenerCount();
        }
        if (peakListenerCount < 0) {
            peakListenerCount = another.getPeakListenerCount();
        }
        if (bitRate == null) {
            bitRate = another.getBitRate();
        }
        if (currentSong == null) {
            currentSong = another.getCurrentSong();
        }
        if (contentType == null) {
            contentType = another.getContentType();
        }
        if (genre == null) {
            genre = another.getGenre();
        }
    }
    
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public URI getUri()
    {
        return uri;
    }
    
    public void setUri(URI uri)
    {
        this.uri = uri;
    }
    
    public int getCurrentListenerCount()
    {
        return currentListenerCount;
    }
    
    public void setCurrentListenerCount(int currentListenerCount)
    {
        this.currentListenerCount = currentListenerCount;
    }
    
    public int getMaxListenerCount()
    {
        return maxListenerCount;
    }
    
    public void setMaxListenerCount(int maxListenerCount)
    {
        this.maxListenerCount = maxListenerCount;
    }
    
    public int getPeakListenerCount()
    {
        return peakListenerCount;
    }
    
    public void setPeakListenerCount(int peakListenerCount)
    {
        this.peakListenerCount = peakListenerCount;
    }
    
    public String getBitRate()
    {
        return bitRate;
    }
    
    public void setBitRate(String bitRate)
    {
        this.bitRate = bitRate;
    }
    
    public String getCurrentSong()
    {
        return currentSong;
    }
    
    public void setCurrentSong(String currentSong)
    {
        this.currentSong = currentSong;
    }
    
    public String getContentType()
    {
        return contentType;
    }
    
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }
    
    public String getGenre()
    {
        return genre;
    }
    
    public void setGenre(String genre)
    {
        this.genre = genre;
    }
}
