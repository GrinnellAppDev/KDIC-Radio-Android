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

@SuppressWarnings("serial")
public class StreamScraperException extends Exception
{
    public StreamScraperException()
    {
        super();
    }
    
    public StreamScraperException(String message)
    {
        super(message);
    }
    
    public StreamScraperException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public StreamScraperException(Throwable cause)
    {
        super(cause);
    }
}
