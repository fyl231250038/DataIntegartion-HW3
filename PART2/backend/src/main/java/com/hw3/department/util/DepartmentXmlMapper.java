package com.hw3.department.util;

import com.hw3.department.config.DepartmentProperties;
import com.hw3.department.model.Choice;
import com.hw3.department.model.Course;
import com.hw3.department.model.Student;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.springframework.stereotype.Component;

@Component
public class DepartmentXmlMapper {

    private final DepartmentProperties.Xml xml;

    public DepartmentXmlMapper(DepartmentProperties properties) {
        this.xml = properties.getXml();
    }

    public DepartmentProperties.Xml getXml() {
        return xml;
    }

    public Student parseStudent(String xmlPayload) {
        Document document = XmlSupport.parse(xmlPayload);
        Element root = document.getDocumentElement();
        NodeList items = root.getElementsByTagName(xml.getStudent().getItemName());
        if (items.getLength() == 0) {
            throw new IllegalArgumentException("student xml missing item node");
        }
        Element node = (Element) items.item(0);
        Student student = new Student();
        student.setId(text(node, xml.getStudent().getIdTag()));
        student.setName(text(node, xml.getStudent().getNameTag()));
        student.setSex(optionalText(node, xml.getStudent().getSexTag()));
        student.setMajor(optionalText(node, xml.getStudent().getMajorTag()));
        return student;
    }

    public Choice parseChoice(String xmlPayload) {
        Document document = XmlSupport.parse(xmlPayload);
        Element root = document.getDocumentElement();
        NodeList items = root.getElementsByTagName(xml.getChoice().getItemName());
        if (items.getLength() == 0) {
            throw new IllegalArgumentException("choice xml missing item node");
        }
        Element node = (Element) items.item(0);
        Choice choice = new Choice();
        choice.setStudentId(text(node, xml.getChoice().getStudentIdTag()));
        choice.setCourseId(text(node, xml.getChoice().getCourseIdTag()));
        choice.setScore(optionalText(node, xml.getChoice().getScoreTag()));
        return choice;
    }

    public List<Course> parseCourses(String xmlPayload) {
        Document document = XmlSupport.parse(xmlPayload);
        Element root = document.getDocumentElement();
        NodeList items = root.getElementsByTagName(xml.getCourse().getItemName());
        List<Course> courses = new ArrayList<>();
        for (int index = 0; index < items.getLength(); index++) {
            Element node = (Element) items.item(index);
            Course course = new Course();
            course.setId(text(node, xml.getCourse().getIdTag()));
            course.setName(text(node, xml.getCourse().getNameTag()));
            course.setTime(optionalText(node, xml.getCourse().getTimeTag()));
            course.setScore(text(node, xml.getCourse().getScoreTag()));
            course.setTeacher(text(node, xml.getCourse().getTeacherTag()));
            course.setLocation(text(node, xml.getCourse().getLocationTag()));
            course.setShare(optionalText(node, xml.getCourse().getShareTag()));
            courses.add(course);
        }
        return courses;
    }

    public String buildStudentXml(Student student) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<").append(xml.getStudent().getRootName()).append(">");
        xmlBuilder.append("<").append(xml.getStudent().getItemName()).append(">");
        append(xmlBuilder, xml.getStudent().getIdTag(), student.getId());
        append(xmlBuilder, xml.getStudent().getNameTag(), student.getName());
        append(xmlBuilder, xml.getStudent().getSexTag(), student.getSex());
        append(xmlBuilder, xml.getStudent().getMajorTag(), student.getMajor());
        xmlBuilder.append("</").append(xml.getStudent().getItemName()).append(">");
        xmlBuilder.append("</").append(xml.getStudent().getRootName()).append(">");
        return xmlBuilder.toString();
    }

    public String buildChoiceXml(Choice choice) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<").append(xml.getChoice().getRootName()).append(">");
        xmlBuilder.append("<").append(xml.getChoice().getItemName()).append(">");
        append(xmlBuilder, xml.getChoice().getCourseIdTag(), choice.getCourseId());
        append(xmlBuilder, xml.getChoice().getStudentIdTag(), choice.getStudentId());
        append(xmlBuilder, xml.getChoice().getScoreTag(), choice.getScore());
        xmlBuilder.append("</").append(xml.getChoice().getItemName()).append(">");
        xmlBuilder.append("</").append(xml.getChoice().getRootName()).append(">");
        return xmlBuilder.toString();
    }

    public String buildCoursesXml(List<Course> courses) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<").append(xml.getCourse().getRootName()).append(">");
        for (Course course : courses) {
            xmlBuilder.append("<").append(xml.getCourse().getItemName()).append(">");
            append(xmlBuilder, xml.getCourse().getIdTag(), course.getId());
            append(xmlBuilder, xml.getCourse().getNameTag(), course.getName());
            append(xmlBuilder, xml.getCourse().getTimeTag(), course.getTime());
            append(xmlBuilder, xml.getCourse().getScoreTag(), course.getScore());
            append(xmlBuilder, xml.getCourse().getTeacherTag(), course.getTeacher());
            append(xmlBuilder, xml.getCourse().getLocationTag(), course.getLocation());
            append(xmlBuilder, xml.getCourse().getShareTag(), course.getShare());
            xmlBuilder.append("</").append(xml.getCourse().getItemName()).append(">");
        }
        xmlBuilder.append("</").append(xml.getCourse().getRootName()).append(">");
        return xmlBuilder.toString();
    }

    private String text(Element element, String tag) {
        String value = optionalText(element, tag);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("missing tag: " + tag);
        }
        return value;
    }

    private String optionalText(Element element, String tag) {
        NodeList nodes = element.getElementsByTagName(tag);
        if (nodes.getLength() == 0) {
            return null;
        }
        Node node = nodes.item(0);
        return node == null ? null : node.getTextContent();
    }

    private void append(StringBuilder xmlBuilder, String tag, String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        xmlBuilder.append("<").append(tag).append(">");
        xmlBuilder.append(escape(value));
        xmlBuilder.append("</").append(tag).append(">");
    }

    private String escape(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
