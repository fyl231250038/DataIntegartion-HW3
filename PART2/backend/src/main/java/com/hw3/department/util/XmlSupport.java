package com.hw3.department.util;

import java.io.StringReader;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class XmlSupport {

    public static Document parse(String xml) {
        try {
            DocumentBuilderFactory factory = newDocumentBuilderFactory();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xml)));
        } catch (Exception exception) {
            throw new IllegalArgumentException("XML parse failed: " + exception.getMessage(), exception);
        }
    }

    public static DocumentBuilderFactory newDocumentBuilderFactory() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            return factory;
        } catch (Exception exception) {
            throw new IllegalStateException("XML parser init failed: " + exception.getMessage(), exception);
        }
    }
}
