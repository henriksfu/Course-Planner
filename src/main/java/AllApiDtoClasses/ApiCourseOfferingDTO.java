package AllApiDtoClasses;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ApiCourseOfferingDTO {
    public long courseOfferingId;
    public String location;
    public String instructors;
    public String term;
    public long semesterCode;
    public int year;

    public ApiCourseOfferingDTO(long courseOfferingId, String location, String instructors, String term, long semesterCode, int year) {
        this.courseOfferingId = courseOfferingId;
        this.location = location;
        this.instructors = instructors;
        this.term = term;
        this.semesterCode = semesterCode;
        this.year = year;
    }

    public void setInstructors(String instructors) {
        Set<String> existingInstructorsSet = new HashSet<>(Arrays.asList(this.instructors.split(", ")));
        Set<String> newInstructorsSet = new HashSet<>(Arrays.asList(instructors.split(", ")));

        existingInstructorsSet.addAll(newInstructorsSet);

        this.instructors = String.join(", ", existingInstructorsSet);
    }
}
