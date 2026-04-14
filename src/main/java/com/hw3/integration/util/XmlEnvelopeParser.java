package com.hw3.integration.util;

import com.hw3.integration.exception.BadRequestException;
import com.hw3.integration.model.ActionType;
import com.hw3.integration.model.DepartmentCode;
import com.hw3.integration.model.IntegrationRequest;
import com.hw3.integration.model.IntegrationResponse;
import com.hw3.integration.model.PayloadType;
import com.hw3.integration.model.ResponseStatus;
import java.io.StringReader;
import java.time.Instant;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Component
public class XmlEnvelopeParser {

    public IntegrationRequest parseRequest(String xml) {
        try {
            Document document = parse(xml);
            Element root = document.getDocumentElement();
            Element header = child(root, "header");
            IntegrationRequest request = new IntegrationRequest(
                    text(header, "requestId"),
                    DepartmentCode.from(text(header, "sourceSystem")),
                    optionalDepartment(header, "targetSystem"),
                    optionalDepartment(header, "originSystem"),
                    ActionType.valueOf(text(header, "action")),
                    parseInstant(text(header, "timestamp")));
            readBody(root, request);
            return request;
        } catch (BadRequestException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BadRequestException("请求 XML 解析失败: " + exception.getMessage());
        }
    }

    public IntegrationResponse parseResponse(String xml) {
        try {
            Document document = parse(xml);
            Element root = document.getDocumentElement();
            Element header = child(root, "header");
            IntegrationResponse response = new IntegrationResponse(
                    optionalText(header, "requestId"),
                    optionalDepartment(header, "sourceSystem"),
                    optionalDepartment(header, "targetSystem"),
                    optionalAction(header, "action"),
                    parseInstant(optionalText(header, "timestamp")),
                    ResponseStatus.valueOf(text(root, "status")),
                    optionalText(root, "message"));
            readBody(root, response);
            return response;
        } catch (BadRequestException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BadRequestException("响应 XML 解析失败: " + exception.getMessage());
        }
    }

    public String requireBodyFragment(String xml, PayloadType payloadType) {
        Document document = parse(xml);
        Element body = child(document.getDocumentElement(), "body");
        NodeList nodes = body.getChildNodes();
        for (int index = 0; index < nodes.getLength(); index++) {
            Node node = nodes.item(index);
            if (node.getNodeType() == Node.ELEMENT_NODE
                    && payloadType.rootName().equalsIgnoreCase(node.getNodeName())) {
                return XmlSupport.nodeToString(node);
            }
        }
        throw new BadRequestException("响应体缺少 " + payloadType.rootName() + " XML 数据");
    }

    private void readBody(Element root, IntegrationRequest request) {
        Element body = child(root, "body");
        NodeList nodes = body.getChildNodes();
        for (int index = 0; index < nodes.getLength(); index++) {
            Node node = nodes.item(index);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                request.addBodyFragment(node.getNodeName(), XmlSupport.nodeToString(node));
            }
        }
    }

    private void readBody(Element root, IntegrationResponse response) {
        Element body = child(root, "body");
        NodeList nodes = body.getChildNodes();
        for (int index = 0; index < nodes.getLength(); index++) {
            Node node = nodes.item(index);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String rootName = node.getNodeName().trim().toLowerCase();
                if (PayloadType.CLASS.rootName().equals(rootName)) {
                    response.addBodyFragment(PayloadType.CLASS, XmlSupport.nodeToString(node));
                } else if (PayloadType.STUDENT.rootName().equals(rootName)) {
                    response.addBodyFragment(PayloadType.STUDENT, XmlSupport.nodeToString(node));
                } else if (PayloadType.CHOICE.rootName().equals(rootName)) {
                    response.addBodyFragment(PayloadType.CHOICE, XmlSupport.nodeToString(node));
                }
            }
        }
    }

    private Document parse(String xml) {
        try {
            DocumentBuilderFactory factory = XmlSupport.newDocumentBuilderFactory();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xml)));
        } catch (Exception exception) {
            throw new BadRequestException("XML 不是合法文档: " + exception.getMessage());
        }
    }

    private Element child(Element parent, String name) {
        NodeList nodes = parent.getElementsByTagName(name);
        if (nodes.getLength() == 0) {
            throw new BadRequestException("缺少节点: " + name);
        }
        return (Element) nodes.item(0);
    }

    private String text(Element element, String name) {
        String value = optionalText(element, name);
        if (value == null || value.isBlank()) {
            throw new BadRequestException("缺少字段: " + name);
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

    private DepartmentCode optionalDepartment(Element element, String name) {
        String value = optionalText(element, name);
        return value == null || value.isBlank() ? null : DepartmentCode.from(value);
    }

    private ActionType optionalAction(Element element, String name) {
        String value = optionalText(element, name);
        return value == null || value.isBlank() ? null : ActionType.valueOf(value);
    }

    private Instant parseInstant(String value) {
        return value == null || value.isBlank() ? Instant.now() : Instant.parse(value);
    }
}
