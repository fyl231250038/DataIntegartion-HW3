package com.hw3.department.integration;

import com.hw3.department.util.XmlSupport;
import java.io.StringWriter;
import java.time.Instant;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class IntegrationEnvelopeParser {

    public IntegrationRequest parseRequest(String xml) {
        try {
            Document document = XmlSupport.parse(xml);
            Element root = document.getDocumentElement();
            Element header = child(root, "header");
            IntegrationRequest request = new IntegrationRequest(
                    text(header, "requestId"),
                    DepartmentCode.from(text(header, "sourceSystem")),
                    DepartmentCode.from(optionalText(header, "targetSystem")),
                    DepartmentCode.from(optionalText(header, "originSystem")),
                    ActionType.valueOf(text(header, "action")),
                    parseInstant(text(header, "timestamp")));
            readBody(root, request);
            return request;
        } catch (Exception exception) {
            throw new IllegalArgumentException("integration request parse failed: " + exception.getMessage(), exception);
        }
    }

    public IntegrationResponseEnvelope parseResponse(String xml) {
        try {
            Document document = XmlSupport.parse(xml);
            Element root = document.getDocumentElement();
            Element header = child(root, "header");
            IntegrationResponseEnvelope response = new IntegrationResponseEnvelope(
                    optionalText(header, "requestId"),
                    DepartmentCode.from(optionalText(header, "sourceSystem")),
                    DepartmentCode.from(optionalText(header, "targetSystem")),
                    optionalAction(header, "action"),
                    parseInstant(optionalText(header, "timestamp")),
                    ResponseStatus.valueOf(text(root, "status")),
                    optionalText(root, "message"));
            readBody(root, response);
            return response;
        } catch (Exception exception) {
            throw new IllegalArgumentException("integration response parse failed: " + exception.getMessage(), exception);
        }
    }

    private void readBody(Element root, IntegrationRequest request) {
        Element body = child(root, "body");
        NodeList nodes = body.getChildNodes();
        for (int index = 0; index < nodes.getLength(); index++) {
            Node node = nodes.item(index);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                request.addBodyFragment(node.getNodeName(), nodeToString(node));
            }
        }
    }

    private void readBody(Element root, IntegrationResponseEnvelope response) {
        Element body = child(root, "body");
        NodeList nodes = body.getChildNodes();
        for (int index = 0; index < nodes.getLength(); index++) {
            Node node = nodes.item(index);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                response.addBodyFragment(node.getNodeName(), nodeToString(node));
            }
        }
    }

    private String nodeToString(Node node) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(node), new StreamResult(writer));
            return writer.toString();
        } catch (Exception exception) {
            throw new IllegalArgumentException("node serialization failed: " + exception.getMessage(), exception);
        }
    }

    private Element child(Element parent, String name) {
        NodeList nodes = parent.getElementsByTagName(name);
        if (nodes.getLength() == 0) {
            throw new IllegalArgumentException("missing node: " + name);
        }
        return (Element) nodes.item(0);
    }

    private String text(Element element, String name) {
        String value = optionalText(element, name);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("missing field: " + name);
        }
        return value;
    }

    private String optionalText(Element element, String name) {
        NodeList nodes = element.getElementsByTagName(name);
        if (nodes.getLength() == 0) {
            return null;
        }
        return nodes.item(0).getTextContent();
    }

    private ActionType optionalAction(Element element, String name) {
        String value = optionalText(element, name);
        return value == null || value.trim().isEmpty() ? null : ActionType.valueOf(value);
    }

    private Instant parseInstant(String value) {
        return value == null || value.trim().isEmpty() ? Instant.now() : Instant.parse(value);
    }
}
