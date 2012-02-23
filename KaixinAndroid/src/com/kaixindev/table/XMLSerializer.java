package com.kaixindev.table;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kaixindev.core.XMLUtil;

public class XMLSerializer implements Serializer {
    
    private static final String ROOT_NAME = "Table";

    @Override
    public Map<String,Value> unserialize(byte[] content) {
        if (content == null || content.length <= 0) {
            return null;
        }
        
        DocumentBuilder builder = getXMLDocumentBuilder();
        try {
            Map<String,Value> data = new HashMap<String,Value>();
            
            Document doc = builder.parse(new ByteArrayInputStream(content));
            Element root = doc.getDocumentElement();
            if (root != null && root.hasChildNodes()) {
                com.kaixindev.serialize.XMLSerializer serializer = new com.kaixindev.serialize.XMLSerializer();
                
                NodeList nodes = root.getChildNodes();
                for (int i=0; i<nodes.getLength(); ++i) {
                    Node node = nodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element el = (Element)node;
                        String key = el.getTagName();
                        Value value = null;
                        try {
                             value = (Value) serializer.unserialize(el);
                        } catch (Exception e) {}
                        data.put(key,value);
                    }
                }
            }
            
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public byte[] serialize(Map<String,Value> data) {
        if (data == null) {
            return null;
        }

        DocumentBuilder builder = getXMLDocumentBuilder();
        Document doc = builder.newDocument();
        
        com.kaixindev.serialize.XMLSerializer serializer = new com.kaixindev.serialize.XMLSerializer();
        Element root = doc.createElement(ROOT_NAME);
        doc.appendChild(root);
        
        for (Entry<String,Value> entry : data.entrySet()) {
            String key = entry.getKey();
            if (key != null && key.length() > 0) {
                Element el = doc.createElement(key);
                serializer.serialize(entry.getValue(), doc, el);
                root.appendChild(el);
            }
        }
        
        return XMLUtil.XMLToByteArray(doc);
    }

    
    private static DocumentBuilder getXMLDocumentBuilder() {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(false);
        try {
            return docBuilderFactory.newDocumentBuilder();
        } catch (Exception e) {
            return null;
        }        
    }
}
