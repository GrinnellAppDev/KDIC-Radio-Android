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
package net.moraleboost.streamscraper.util;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.UnsupportedCharsetException;

public class CharsetUtils
{
    /**
     * Creates an charset encoder for the specified charset.
     * 
     * @param charset
     * @param malformedInputAction
     * @param unmappableCharacterAction
     * @return The encoder created.
     * @throws IllegalCharsetNameException
     *             An invalid charset name was specified.
     * @throws UnsupportedCharsetException
     *             The JVM does not support the charset.
     * @throws IllegalArgumentException
     *             An invalid CondingErrorAction value was specified.
     */
    public static CharsetEncoder createEncoder(String charset,
            CodingErrorAction malformedInputAction,
            CodingErrorAction unmappableCharacterAction)
    {
        Charset cset = Charset.forName(charset);
        if (!cset.canEncode()) {
            throw new UnsupportedCharsetException(charset);
        }
        CharsetEncoder encoder = cset.newEncoder();
        encoder.onMalformedInput(malformedInputAction);
        encoder.onUnmappableCharacter(unmappableCharacterAction);

        return encoder;
    }

    /**
     * Creates an charset decoder for the specified charset.
     * 
     * @param charset
     * @param malformedInputAction
     * @param unmappableCharacterAction
     * @return The decoder created.
     * @throws IllegalCharsetNameException
     *             An invalid charset name was specified.
     * @throws UnsupportedCharsetException
     *             The JVM does not support the charset.
     * @throws IllegalArgumentException
     *             An invalid CodingErrorAction value was specified.
     */
    public static CharsetDecoder createDecoder(String charset,
            CodingErrorAction malformedInputAction,
            CodingErrorAction unmappableCharacterAction)
    {
        Charset cset = Charset.forName(charset);
        CharsetDecoder decoder = cset.newDecoder();
        decoder.onMalformedInput(malformedInputAction);
        decoder.onUnmappableCharacter(unmappableCharacterAction);
        return decoder;
    }

    /**
     * Encodes the string using the specified encoder.
     * 
     * @param encoder
     * @param text
     * @param terminateWithNull
     *            If true, a NULL byte is added to the end of the encoded byte array.
     * @return The encoded byte array.
     * @throws CharacterCodingException
     */
    public static byte[] encode(CharsetEncoder encoder, CharSequence text,
            boolean terminateWithNull) throws CharacterCodingException
    {
        ByteBuffer buf = encoder.encode(CharBuffer.wrap(text));
        int size = buf.limit();

        byte[] ret = null;
        if (terminateWithNull) {
            // append \0
            ret = new byte[size + 1];
            buf.get(ret, 0, size);
            ret[size] = 0;
        } else {
            ret = new byte[size];
            buf.get(ret, 0, size);
        }

        return ret;
    }

    /**
     * Decodes the byte array using the specified decoder.
     * 
     * @param decoder
     * @param rawText
     * @return The decoded Unicode string.
     * @throws CharacterCodingException
     */
    public static String decode(CharsetDecoder decoder, byte[] rawText)
    throws CharacterCodingException
    {
        CharBuffer buf = decoder.decode(ByteBuffer.wrap(rawText));
        return buf.toString();
    }
}
