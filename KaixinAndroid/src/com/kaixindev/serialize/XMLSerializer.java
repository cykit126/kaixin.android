package com.kaixindev.serialize;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kaixindev.android.Log;
import com.kaixindev.core.XMLUtil;

/**
 * NOTICE:
 * Array of primitive type can't be used, use its boxed type instead.
 * e.g. Use Integer[] instead of int[]
 * @author Wilbur Luo
 */
public class XMLSerializer implements Serializer {
    private static final String ROOT_NAME = "Table";
    private static final String ARRAY_ITEM = "item";
    private static final String PROPERTY_TYPE = "type";
    
    /**
     * Unserialize an object from byte array. 
     * @param xmlContent
     * @return null for failure.
     */
    public Object unserialize(byte[] xmlContent) {
        if (xmlContent == null || xmlContent.length <= 0) {
            return null;
        }
        
        DocumentBuilder builder = getXMLDocumentBuilder();
        try {
            Document doc = builder.parse(new ByteArrayInputStream(xmlContent));
            return unserialize(doc.getDocumentElement());
        }
        catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Unserialize an object from a Element. 
     * @param el
     * @return null for failure.
     */
    public Object unserialize(Element el) {
        if (el == null || !el.hasChildNodes()) {
            return null;
        }
        String type = el.getAttribute(PROPERTY_TYPE);
        return elementToObject(el,type);        
    }
    
    /**
     * Serialize an object to byte array.
     * @param object
     * @return null for failure.
     */
    public byte[] serialize(Object object) {
        DocumentBuilder builder = getXMLDocumentBuilder();
        
        // Create XML document.
        Document doc = builder.newDocument();
        Element root = doc.createElement(ROOT_NAME);
        objectToElement(doc,root,object,true);
        doc.appendChild(root);
        
        // Convert XML document to string.
        return XMLUtil.XMLToByteArray(doc);
    }
    
    /**
     * Serialize an object to a Element.
     * @param object
     * @param doc
     * @param el
     */
    public void serialize(Object object, Document doc, Element el) {
        objectToElement(doc,el,object,true);
    }
    
    /**
     * Parse XML Element to Object.
     * @param el
     * @return null for failure.
     */
    private static Object elementToObject(Element el, String type) {
        if (el == null || type == null) {
            return null;
        }
        
        if (Type.isArray(type)) {
            if (el.hasChildNodes()) {
                String arrayType = Type.getArrayType(type);
                if (Type.isArray(arrayType)) {
                    Log.w("Multidimensional is not supported.");
                    return null;
                }
                NodeList nodes = el.getChildNodes();
                
                int count = 0;
                for (int i=0; i<nodes.getLength(); ++i) {
                    Node node = nodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        ++count;
                    }
                }
                
                Class<?> klass = null;
                try {
                    klass = Class.forName(Type.toJavaName(arrayType));
                }
                catch (ClassNotFoundException e1) {
                    return null;
                } 
                Object[] objs = (Object[])Array.newInstance(klass, count);
                int j = 0;
                for (int i=0; i<nodes.getLength(); ++i) {
                    Node node = nodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        try {
                            objs[j] = elementToObject((Element)node,arrayType);
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        } finally {
                            ++j;
                        }
                    }
                }
                return objs;
            } else {
                return null;
            }
        } else if (Type.isPrimitive(type)) {
            return parsePrimitive(type, el.getTextContent());
        } else if (Type.isSerializable(type)) {
            try {
                Class<?> klass = Class.forName(type);
                Serializable o = (Serializable)klass.newInstance();
                if (el.hasChildNodes()) {
                    NodeList nodes = el.getChildNodes();
                    for (int i=0; i<nodes.getLength(); ++i) {
                        Node node = nodes.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE ) {
                            Element elField = (Element)node;
                            if (elField.hasAttribute(PROPERTY_TYPE)) {
                                o.setField(
                                        elField.getTagName(),
                                        elementToObject(elField,elField.getAttribute(PROPERTY_TYPE))
                                        );
                            }
                        }
                    }
                }
                return o;
            }
            catch (Exception e) {
                return null;
            }
        }
        else {
            try {
                Class<?> klass = Class.forName(Type.toJavaName(type));
                Object obj = null;
                try {
                    obj = klass.newInstance();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                if (el.hasChildNodes()) {
                    NodeList nodes = el.getChildNodes();
                    for (int i=0; i<nodes.getLength(); ++i) {
                        Node node = nodes.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element elField = (Element)node;
                            if (elField.hasAttribute(PROPERTY_TYPE)) {
                                elementToField(obj,(Element)node,elField.getAttribute(PROPERTY_TYPE));
                            }
                        }
                    }
                }
                return obj;
            }
            catch (ClassNotFoundException e) {
                return null;
            }
        }
    }
    
    private static void elementToField(Object object, Element el, String type) {
        if (object == null || el == null || type == null) {
            return;
        }
        
        Class<?> klass = object.getClass();
        try {
            Field field = klass.getField(el.getTagName());
            field.set(object, elementToObject(el,type));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static Object parsePrimitive(String type, String content) {
        if (type == null || content == null) {
            return null;
        }
        try {
            if (type.equals(Type.BOOLEAN)) {
                return Boolean.valueOf(content);
            } else if (type.equals(Type.BYTE)) {
                return Byte.valueOf(content);
            } else if (type.equals(Type.SHORT)) {
                return Short.valueOf(content);
            } else if (type.equals(Type.INTEGER)) {
                return Integer.valueOf(content);
            } else if (type.equals(Type.LONG)) {
                return Long.valueOf(content);
            } else if (type.equals(Type.FLOAT)) {
                return Float.valueOf(content);
            } else if (type.equals(Type.DOUBLE)) {
                return Double.valueOf(content);
            } else if (type.equals(Type.STRING)) {
                return content;
            }              
        } catch (Exception e) {}
        return null;
    }
    
    
    /**
     * Serialize Object to XML Element
     * @param doc
     * @param object
     * @return Return null if it's impossible to represent Object in XML Element.
     */
    private static void objectToElement(Document doc, Element item, Object object, boolean addType) {
        if (object == null || doc == null || item == null) {
            return;
        }
        
        Class<?> klass = object.getClass();
        if (addType) {
            item.setAttribute(PROPERTY_TYPE,Type.javaTypeToName(klass.getName()));
        }
        
        if (klass.isPrimitive() 
                || Boolean.class.isInstance(object)
                || Byte.class.isInstance(object)
                || Character.class.isInstance(object)
                || Short.class.isInstance(object)
                || Integer.class.isInstance(object)
                || Long.class.isInstance(object)
                || Float.class.isInstance(object)
                || Double.class.isInstance(object)
                || String.class.isInstance(object)) 
        {
            String content = object.toString();
            if (content != null && content.length() > 0) {
                item.setTextContent(content);
            }
        } else if (Serializable.class.isInstance(object)) {
            Serializable s = (Serializable)object;
            for (String key : s.keySet()) {
                if (key != null && key.length() > 0) {
                    Element subItem = doc.createElement(key);
                    objectToElement(doc,subItem,s.getField(key),true);
                    item.appendChild(subItem);
                }
            }
        } else if (klass.isArray()) {
            for (int i=0; i<Array.getLength(object); ++i) {
                Element subItem = doc.createElement(ARRAY_ITEM);
                objectToElement(doc,subItem,Array.get(object,i),false);
                item.appendChild(subItem);
            }
        } else {
            Field[] fields = klass.getFields();
            if (fields != null) {
                for (Field field : fields) {
                    if (field.getAnnotation(Attribute.class) != null) 
                    {
                        try {
                            Element subItem = doc.createElement(field.getName());
                            objectToElement(doc,subItem,field.get(object),true);
                            item.appendChild(subItem);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
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













