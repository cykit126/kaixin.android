package com.kaixindev.serialize;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface AttributeSerializer {
    public Element serialize(Document doc, Object object);
}
