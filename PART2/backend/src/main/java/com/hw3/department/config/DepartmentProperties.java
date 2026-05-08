package com.hw3.department.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "department")
public class DepartmentProperties {

    private String code;
    private Db db = new Db();
    private Xml xml = new Xml();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Db getDb() {
        return db;
    }

    public void setDb(Db db) {
        this.db = db;
    }

    public Xml getXml() {
        return xml;
    }

    public void setXml(Xml xml) {
        this.xml = xml;
    }

    public static class Db {

        private String quotePrefix = "";
        private String quoteSuffix = "";
        private String shareYesValue = "Y";
        private Tables tables = new Tables();
        private Columns columns = new Columns();

        public String getQuotePrefix() {
            return quotePrefix;
        }

        public void setQuotePrefix(String quotePrefix) {
            this.quotePrefix = quotePrefix;
        }

        public String getQuoteSuffix() {
            return quoteSuffix;
        }

        public void setQuoteSuffix(String quoteSuffix) {
            this.quoteSuffix = quoteSuffix;
        }

        public String getShareYesValue() {
            return shareYesValue;
        }

        public void setShareYesValue(String shareYesValue) {
            this.shareYesValue = shareYesValue;
        }

        public Tables getTables() {
            return tables;
        }

        public void setTables(Tables tables) {
            this.tables = tables;
        }

        public Columns getColumns() {
            return columns;
        }

        public void setColumns(Columns columns) {
            this.columns = columns;
        }
    }

    public static class Tables {

        private String students = "students";
        private String courses = "courses";
        private String choices = "choices";
        private String users = "users";

        public String getStudents() {
            return students;
        }

        public void setStudents(String students) {
            this.students = students;
        }

        public String getCourses() {
            return courses;
        }

        public void setCourses(String courses) {
            this.courses = courses;
        }

        public String getChoices() {
            return choices;
        }

        public void setChoices(String choices) {
            this.choices = choices;
        }

        public String getUsers() {
            return users;
        }

        public void setUsers(String users) {
            this.users = users;
        }
    }

    public static class Columns {

        private String studentId = "student_id";
        private String studentName = "student_name";
        private String studentSex = "student_sex";
        private String studentMajor = "student_major";

        private String courseId = "course_id";
        private String courseName = "course_name";
        private String courseTime = "course_time";
        private String courseScore = "course_score";
        private String courseTeacher = "course_teacher";
        private String courseLocation = "course_location";
        private String courseShare = "course_share";

        private String choiceStudentId = "student_id";
        private String choiceCourseId = "course_id";
        private String choiceScore = "score";

        private String userName = "username";
        private String userPassword = "password";

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public String getStudentSex() {
            return studentSex;
        }

        public void setStudentSex(String studentSex) {
            this.studentSex = studentSex;
        }

        public String getStudentMajor() {
            return studentMajor;
        }

        public void setStudentMajor(String studentMajor) {
            this.studentMajor = studentMajor;
        }

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getCourseTime() {
            return courseTime;
        }

        public void setCourseTime(String courseTime) {
            this.courseTime = courseTime;
        }

        public String getCourseScore() {
            return courseScore;
        }

        public void setCourseScore(String courseScore) {
            this.courseScore = courseScore;
        }

        public String getCourseTeacher() {
            return courseTeacher;
        }

        public void setCourseTeacher(String courseTeacher) {
            this.courseTeacher = courseTeacher;
        }

        public String getCourseLocation() {
            return courseLocation;
        }

        public void setCourseLocation(String courseLocation) {
            this.courseLocation = courseLocation;
        }

        public String getCourseShare() {
            return courseShare;
        }

        public void setCourseShare(String courseShare) {
            this.courseShare = courseShare;
        }

        public String getChoiceStudentId() {
            return choiceStudentId;
        }

        public void setChoiceStudentId(String choiceStudentId) {
            this.choiceStudentId = choiceStudentId;
        }

        public String getChoiceCourseId() {
            return choiceCourseId;
        }

        public void setChoiceCourseId(String choiceCourseId) {
            this.choiceCourseId = choiceCourseId;
        }

        public String getChoiceScore() {
            return choiceScore;
        }

        public void setChoiceScore(String choiceScore) {
            this.choiceScore = choiceScore;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserPassword() {
            return userPassword;
        }

        public void setUserPassword(String userPassword) {
            this.userPassword = userPassword;
        }
    }

    public static class Xml {

        private StudentXml student = new StudentXml();
        private CourseXml course = new CourseXml();
        private ChoiceXml choice = new ChoiceXml();

        public StudentXml getStudent() {
            return student;
        }

        public void setStudent(StudentXml student) {
            this.student = student;
        }

        public CourseXml getCourse() {
            return course;
        }

        public void setCourse(CourseXml course) {
            this.course = course;
        }

        public ChoiceXml getChoice() {
            return choice;
        }

        public void setChoice(ChoiceXml choice) {
            this.choice = choice;
        }
    }

    public static class StudentXml {

        private String rootName = "Students";
        private String itemName = "student";
        private String idTag = "Sno";
        private String nameTag = "Snm";
        private String sexTag = "Sex";
        private String majorTag = "Sde";

        public String getRootName() {
            return rootName;
        }

        public void setRootName(String rootName) {
            this.rootName = rootName;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getIdTag() {
            return idTag;
        }

        public void setIdTag(String idTag) {
            this.idTag = idTag;
        }

        public String getNameTag() {
            return nameTag;
        }

        public void setNameTag(String nameTag) {
            this.nameTag = nameTag;
        }

        public String getSexTag() {
            return sexTag;
        }

        public void setSexTag(String sexTag) {
            this.sexTag = sexTag;
        }

        public String getMajorTag() {
            return majorTag;
        }

        public void setMajorTag(String majorTag) {
            this.majorTag = majorTag;
        }
    }

    public static class CourseXml {

        private String rootName = "Classes";
        private String itemName = "class";
        private String idTag = "Cno";
        private String nameTag = "Cnm";
        private String timeTag = "Ctm";
        private String scoreTag = "Cpt";
        private String teacherTag = "Tec";
        private String locationTag = "Pla";
        private String shareTag = "Share";

        public String getRootName() {
            return rootName;
        }

        public void setRootName(String rootName) {
            this.rootName = rootName;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getIdTag() {
            return idTag;
        }

        public void setIdTag(String idTag) {
            this.idTag = idTag;
        }

        public String getNameTag() {
            return nameTag;
        }

        public void setNameTag(String nameTag) {
            this.nameTag = nameTag;
        }

        public String getTimeTag() {
            return timeTag;
        }

        public void setTimeTag(String timeTag) {
            this.timeTag = timeTag;
        }

        public String getScoreTag() {
            return scoreTag;
        }

        public void setScoreTag(String scoreTag) {
            this.scoreTag = scoreTag;
        }

        public String getTeacherTag() {
            return teacherTag;
        }

        public void setTeacherTag(String teacherTag) {
            this.teacherTag = teacherTag;
        }

        public String getLocationTag() {
            return locationTag;
        }

        public void setLocationTag(String locationTag) {
            this.locationTag = locationTag;
        }

        public String getShareTag() {
            return shareTag;
        }

        public void setShareTag(String shareTag) {
            this.shareTag = shareTag;
        }
    }

    public static class ChoiceXml {

        private String rootName = "Choices";
        private String itemName = "choice";
        private String studentIdTag = "Sno";
        private String courseIdTag = "Cno";
        private String scoreTag = "Grd";

        public String getRootName() {
            return rootName;
        }

        public void setRootName(String rootName) {
            this.rootName = rootName;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getStudentIdTag() {
            return studentIdTag;
        }

        public void setStudentIdTag(String studentIdTag) {
            this.studentIdTag = studentIdTag;
        }

        public String getCourseIdTag() {
            return courseIdTag;
        }

        public void setCourseIdTag(String courseIdTag) {
            this.courseIdTag = courseIdTag;
        }

        public String getScoreTag() {
            return scoreTag;
        }

        public void setScoreTag(String scoreTag) {
            this.scoreTag = scoreTag;
        }
    }
}
