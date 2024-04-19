package Application;

import java.util.ArrayList;
import java.util.List;

public class CourseOffering {
    int semester;
    String subject;
    String catalogNumber;
    String location;
    int enrollmentCapacity;
    int enrollmentTotal;
    List<String> instructors;
    String componentCode;

    public int getSemester() {
        return semester;
    }

    public String getSubject() {
        return subject;
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public String getLocation() {
        return location;
    }

    public int getEnrollmentCapacity() {
        return enrollmentCapacity;
    }

    public int getEnrollmentTotal() {
        return enrollmentTotal;
    }

    public List<String> getInstructors() {
        return instructors;
    }

    public String getComponentCode() {
        return componentCode;
    }

    public CourseOffering(int semester, String subject, String catalogNumber, String location, int enrollmentCapacity, int enrollmentTotal, List<String> instructors, String componentCode) {
        this.semester = semester;
        this.subject = subject;
        this.catalogNumber = catalogNumber;
        this.location = location;
        this.enrollmentCapacity = enrollmentCapacity;
        this.enrollmentTotal = enrollmentTotal;
        this.instructors = new ArrayList<>();
        for (String instructor : instructors) {
            if (instructor.equals("<null>") || instructor.equals("(null)")) {
                this.instructors.add("");
            } else {
                this.instructors.add(instructor);
            }
        }
        this.componentCode = componentCode;
    }


}
