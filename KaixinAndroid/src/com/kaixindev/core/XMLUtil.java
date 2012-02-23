package com.kaixindev.core;

import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.kaixindev.android.Log;

public class XMLUtil {
    
    /**
     * Transform XML document object to string.
     * @param doc
     * @return Returns null if fails.
     */
    public static String XMLtoString(Document doc) {
        if (doc == null) {
            return null;
        }
        
        // Convert XML document to string.
        //set up a transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans;
        try {
            trans = transfac.newTransformer();
        }
        catch (TransformerConfigurationException e) {
            return null;
        }
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.setOutputProperty(OutputKeys.ENCODING, "utf-8");

        //create string from XML tree
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(doc);
        try {
            trans.transform(source, result);
        }
        catch (TransformerException e) {
            Log.w(e.getMessage());
            e.printStackTrace();
            return null;
        }
        
        return sw.toString();
    }
    
    /**
     * Transform XML document object to byte array.
     * @param doc
     * @return Returns null if fails.
     */
    public static byte[] XMLToByteArray(Document doc) {
        String data = XMLtoString(doc);
        if (data != null) {
            return data.getBytes();
        } else {
            return null;
        }
    }
    
    /**
     * Transform XML document object to byte array.
     * @param doc
     * @return Returns false if fails.
     */
    public static boolean writeXML(Document doc, OutputStream dest) {
        if (doc == null || dest == null) {
            return false;
        }
        
        // Convert XML document to string.
        //set up a transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = null;
        try {
            trans = transfac.newTransformer();
        }
        catch (Exception e) {
            Log.w(e.getMessage());
            e.printStackTrace();
            return false;
        }
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.setOutputProperty(OutputKeys.ENCODING, "utf-8");

        //create string from XML tree
        StreamResult result = new StreamResult(dest);
        DOMSource source = new DOMSource(doc);
        try {
            trans.transform(source, result);
        }
        catch (Exception e) {
            Log.w(e.getMessage());
            e.printStackTrace();
            return false;
        }        
        
        return true;
    }
}
