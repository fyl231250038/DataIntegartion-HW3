package com.hw3.integration.service;

import com.hw3.integration.config.IntegrationProperties;
import com.hw3.integration.exception.IntegrationException;
import com.hw3.integration.model.DepartmentCode;
import com.hw3.integration.model.PayloadType;
import com.hw3.integration.util.XmlSupport;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Service
public class XmlIntegrationEngine {

    private static final String REQUEST_ENVELOPE_SCHEMA = "classpath:schemas/integration-request.xsd";
    private static final String RESPONSE_ENVELOPE_SCHEMA = "classpath:schemas/integration-response.xsd";

    private final ResourceLoader resourceLoader;
    private final IntegrationProperties integrationProperties;
    private final Map<PayloadType, String> unifiedSchemas = new HashMap<>();

    public XmlIntegrationEngine(ResourceLoader resourceLoader, IntegrationProperties integrationProperties) {
        this.resourceLoader = resourceLoader;
        this.integrationProperties = integrationProperties;
        unifiedSchemas.put(PayloadType.STUDENT, "classpath:schemas/unified/formatStudent.xsd");
        unifiedSchemas.put(PayloadType.CLASS, "classpath:schemas/unified/formatClass.xsd");
        unifiedSchemas.put(PayloadType.CHOICE, "classpath:schemas/unified/formatClassChoice.xsd");
    }

    public void validateRequestEnvelope(String xml) {
        validate(xml, REQUEST_ENVELOPE_SCHEMA);
    }

    public void validateResponseEnvelope(String xml) {
        validate(xml, RESPONSE_ENVELOPE_SCHEMA);
    }

    public void validateUnified(String xml, PayloadType payloadType) {
        validate(xml, unifiedSchemas.get(payloadType));
    }

    public void validateLocal(String xml, PayloadType payloadType, DepartmentCode departmentCode) {
        IntegrationProperties.DepartmentConfig config = integrationProperties.getDepartment(departmentCode);
        String schema = switch (payloadType) {
            case STUDENT -> config.getStudentSchema();
            case CLASS -> config.getClassSchema();
            case CHOICE -> config.getChoiceSchema();
        };
        validate(xml, schema);
    }

    public String toUnified(String xml, PayloadType payloadType, DepartmentCode sourceDepartment) {
        validateLocal(xml, payloadType, sourceDepartment);
        String transformed = transform(xml, inboundXsl(payloadType));
        validateUnified(transformed, payloadType);
        return transformed;
    }

    public String fromUnified(String xml, PayloadType payloadType, DepartmentCode targetDepartment) {
        validateUnified(xml, payloadType);
        String transformed = transform(xml, outboundXsl(payloadType, targetDepartment));
        validateLocal(transformed, payloadType, targetDepartment);
        return transformed;
    }

    public String mergeUnifiedDocuments(PayloadType payloadType, List<String> xmlList) {
        if (xmlList.isEmpty()) {
            throw new IntegrationException("没有可合并的 " + payloadType.rootName() + " XML");
        }
        try {
            DocumentBuilderFactory factory = XmlSupport.newDocumentBuilderFactory();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document mergedDocument = builder.newDocument();
            Element root = mergedDocument.createElement(payloadType.rootName());
            mergedDocument.appendChild(root);

            for (String xml : xmlList) {
                Document document = builder.parse(new InputSource(new StringReader(xml)));
                NodeList nodes = document.getDocumentElement().getElementsByTagName(payloadType.itemName());
                for (int index = 0; index < nodes.getLength(); index++) {
                    Node importedNode = mergedDocument.importNode(nodes.item(index), true);
                    root.appendChild(importedNode);
                }
            }

            return XmlSupport.documentToString(mergedDocument);
        } catch (Exception exception) {
            throw new IntegrationException("合并统一 XML 失败: " + exception.getMessage(), exception);
        }
    }

    private void validate(String xml, String schemaLocation) {
        try {
            Resource schemaResource = resourceLoader.getResource(schemaLocation);
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(schemaResource.getURL());
            schema.newValidator().validate(new StreamSource(new StringReader(xml)));
        } catch (Exception exception) {
            throw new IntegrationException("XML 校验失败(" + schemaLocation + "): " + exception.getMessage(), exception);
        }
    }

    private String transform(String xml, String xslLocation) {
        try {
            Resource xslResource = resourceLoader.getResource(xslLocation);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(xslResource.getInputStream()));
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            StringWriter writer = new StringWriter();
            transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(writer));
            return writer.toString();
        } catch (Exception exception) {
            throw new IntegrationException("XML 转换失败(" + xslLocation + "): " + exception.getMessage(), exception);
        }
    }

    private String inboundXsl(PayloadType payloadType) {
        return switch (payloadType) {
            case STUDENT -> "classpath:xsl/formatStudent.xsl";
            case CLASS -> "classpath:xsl/formatClass.xsl";
            case CHOICE -> "classpath:xsl/formatClassChoice.xsl";
        };
    }

    private String outboundXsl(PayloadType payloadType, DepartmentCode targetDepartment) {
        return switch (payloadType) {
            case STUDENT -> "classpath:xsl/studentTo" + targetDepartment.name() + ".xsl";
            case CLASS -> "classpath:xsl/classTo" + targetDepartment.name() + ".xsl";
            case CHOICE -> "classpath:xsl/choiceTo" + targetDepartment.name() + ".xsl";
        };
    }
}
