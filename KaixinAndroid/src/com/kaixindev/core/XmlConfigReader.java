package com.kaixindev.core;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

public class XmlConfigReader
{
    private XPath xpath = null;
    private Document doc = null;

    private XmlConfigReader(final Document doc) {
        xpath = XPathFactory.newInstance().newXPath();
        this.doc = doc;
    }

    public static XmlConfigReader open(final InputStream source) {
        try {
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setNamespaceAware(false);
            final DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
            final Document doc = builder.parse(source);
            return new XmlConfigReader(doc);
        }
        catch (final Exception e) {
            return null;
        }
    }

    private String get(final String xpath) {
        try {
            final XPathExpression expr = this.xpath.compile(xpath);
            return expr.evaluate(doc);
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean getBoolean(final String xpath, final boolean defValue) {
        final String ret = get(xpath);
        if (ret != null) {
            return Boolean.valueOf(ret);
        }
        else {
            return defValue;
        }
    }

    public short getShort(final String xpath, final short defValue) {
        final String ret = get(xpath);
        if (ret != null) {
            return Short.valueOf(ret);
        }
        else {
            return defValue;
        }
    }

    public int getInt(final String xpath, final int defValue) {
        final String ret = get(xpath);
        if (ret != null) {
            return Integer.valueOf(ret);
        }
        else {
            return defValue;
        }
    }

    public long getLong(final String xpath, final long defValue) {
        final String ret = get(xpath);
        if (ret != null) {
            return Long.valueOf(ret);
        }
        else {
            return defValue;
        }
    }

    public float getFloat(final String xpath, final float defValue) {
        final String ret = get(xpath);
        if (ret != null) {
            return Float.valueOf(ret);
        }
        else {
            return defValue;
        }
    }

    public double getDouble(final String xpath, final double defValue) {
        final String ret = get(xpath);
        if (ret != null) {
            return Double.valueOf(ret);
        }
        else {
            return defValue;
        }
    }

    public String getString(final String xpath, final String defValue) {
        final String ret = get(xpath);
        if (ret != null) {
            return ret;
        }
        else {
            return defValue;
        }
    }
}
