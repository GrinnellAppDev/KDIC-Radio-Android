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

import java.util.LinkedList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Segment;

public class JerichoHtmlUtils
{
    public static Element findFirstElement(
            Segment segment, String name, String attrname, String attrvalue)
    {
        List<Element> elements = segment.getAllElements(name);
        if (elements != null) {
            for (Element element: elements) {
                String value = element.getAttributeValue(attrname);
                if (value != null && value.equals(attrvalue)) {
                    return element;
                }
            }
        }
        
        return null;
    }
    
    public static List<Element> findAllElements(
            Segment segment, String name, String attrname, String attrvalue)
    {
        List<Element> ret = new LinkedList<Element>();
        
        List<Element> elements = segment.getAllElements(name);
        if (elements != null) {
            for (Element element: elements) {
                String value = element.getAttributeValue(attrname);
                if (value != null && value.equals(attrvalue)) {
                    ret.add(element);
                }
            }
        }
        
        return ret;
    }
    
    public static Element findFirstChildElement(Element element, String name)
    {
        List<Element> children = element.getChildElements();
        if (children != null) {
            for (Element child: children) {
                if (child.getName().equalsIgnoreCase(name)) {
                    return child;
                }
            }
        }
        
        return null;
    }
    
    public static List<Element> findAllChildElement(Element element, String name)
    {
        List<Element> ret = new LinkedList<Element>();

        List<Element> children = element.getChildElements();
        if (children != null) {
            for (Element child: children) {
                if (child.getName().equalsIgnoreCase(name)) {
                    ret.add(child);
                }
            }
        }
        
        return ret;
    }
}
