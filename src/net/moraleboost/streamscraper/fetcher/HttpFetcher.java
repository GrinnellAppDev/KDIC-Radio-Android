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
package net.moraleboost.streamscraper.fetcher;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import net.moraleboost.streamscraper.FetchException;
import net.moraleboost.streamscraper.Fetcher;

public class HttpFetcher implements Fetcher
{
    private static final String DEFAULT_USER_AGENT =
        "Mozilla/5.0 (compatible; StreamScraper/1.0; +http://code.google.com/p/streamscraper/)";
    private static final HttpParams DEFAULT_PARAMS;
    
    static {
        DEFAULT_PARAMS = new BasicHttpParams();
        HttpProtocolParams.setVersion(DEFAULT_PARAMS, HttpVersion.HTTP_1_0);
        HttpProtocolParams.setUserAgent(DEFAULT_PARAMS, DEFAULT_USER_AGENT);
        HttpConnectionParams.setConnectionTimeout(DEFAULT_PARAMS, 10000);
        HttpConnectionParams.setSoTimeout(DEFAULT_PARAMS, 10000);
    }
    
    public HttpFetcher()
    {
    }

    public byte[] fetch(URI uri) throws FetchException
    {
        DefaultHttpClient client = new DefaultHttpClient(DEFAULT_PARAMS);
        HttpGet req = new HttpGet(uri);
        
        HttpResponse res;
        try {
            res = client.execute(req);
            if (res.getStatusLine().getStatusCode() != 200) {
                throw new FetchException("Status code != 200");
            }
            HttpEntity entity = res.getEntity();
            return EntityUtils.toByteArray(entity);
        } catch (IOException e) {
            throw new FetchException(e);
        }
    }
    
    public static void main(String[] args) throws Exception
    {
        HttpFetcher fetcher = new HttpFetcher();
        byte[] data = fetcher.fetch(new URI("http://code.google.com/p/streamscraper/"));
        System.out.println(new String(data, "UTF-8"));
    }
}
