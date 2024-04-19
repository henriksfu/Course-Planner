package Application;

import AllApiDtoClasses.ApiCourseDTO;
import AllApiDtoClasses.ApiCourseOfferingDTO;

import java.util.List;

public class Course {
    ApiCourseDTO courseDTO;
    List<Offering> courseOfferingDTOList;

    public Course(ApiCourseDTO courseDTO, List<Offering> courseOfferingDTOList) {
        this.courseDTO = courseDTO;
        this.courseOfferingDTOList = courseOfferingDTOList;
    }

    public ApiCourseDTO getCourseDTO() {
        return courseDTO;
    }

    public List<Offering> getCourseOfferingDTOList() {
        return courseOfferingDTOList;
    }
}
