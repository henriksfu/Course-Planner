package Application;

import AllApiDtoClasses.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
public class Service {
    Controller controller = new Controller();

    @GetMapping("/api/about")
    public ApiAboutDTO about() {
        return new ApiAboutDTO("Assignment 5", "Henrik");
    }

    @GetMapping("/api/dump-model")
    public String dumpModel() {
        return controller.modelDump();
    }

    @GetMapping("/api/departments")
    public List<ApiDepartmentDTO> departments() {
        return controller.getDepartmentDTOList();
    }

    @GetMapping("/api/departments/{id}/courses")
    public List<ApiCourseDTO> courses(@PathVariable long id) {
        List<ApiCourseDTO> courses = controller.getCourseDTOList(id);
        if (courses == null) {
            throw new CustomException("Courses not found for department id: " + id);
        }
        return courses;
    }

    @GetMapping("/api/departments/{id}/courses/{courseId}/offerings")
    public List<ApiCourseOfferingDTO> offerings(@PathVariable long id, @PathVariable long courseId) {
        List<ApiCourseOfferingDTO> offerings = controller.getOfferingDTOList(id, courseId);
        if (offerings == null) {
            throw new CustomException("Offerings not found for department id: " + id + ", and course id: " + courseId);
        }
        return offerings;
    }

    @GetMapping("/api/departments/{id}/courses/{courseId}/offerings/{offeringId}")
    public List<ApiOfferingSectionDTO> sections(@PathVariable long id, @PathVariable long courseId, @PathVariable long offeringId) {
        List<ApiOfferingSectionDTO> sections = controller.getOfferingDTO(id, courseId, offeringId);
        if (sections == null) {
            throw new CustomException("Sections not found for department id: " + id + ", course id: " + courseId + ", and offering id: " + offeringId);
        }
        return sections;
    }

    @PostMapping("/api/addoffering")
    public ApiOfferingSectionDTO addOffering(@RequestBody ApiOfferingDataDTO offeringDataDTO) {
        int semester = Integer.parseInt(offeringDataDTO.getSemester());
        String subject = offeringDataDTO.getSubjectName();
        String catalogNumber = offeringDataDTO.getCatalogNumber();
        String location = offeringDataDTO.getLocation();
        String componentCode = offeringDataDTO.getComponent();
        int enrollmentTotal = offeringDataDTO.getEnrollmentTotal();
        int enrollmentCapacity = offeringDataDTO.getEnrollmentCap();
        List<String> instructors = Collections.singletonList(offeringDataDTO.getInstructor());
        CourseOffering courseOffering = new CourseOffering(semester, subject, catalogNumber, location, enrollmentCapacity, enrollmentTotal, instructors, componentCode);
        Controller.addOffering(courseOffering);
        controller.addEvent(courseOffering);
        return new ApiOfferingSectionDTO(componentCode, enrollmentCapacity, enrollmentTotal);
    }

    @GetMapping("/api/watchers")
    public List<ApiWatcherDTO> watchers() {
        return controller.getWatcherDTOList();
    }

    @PostMapping("/api/watchers")
    public ApiWatcherDTO addWatcher(@RequestBody ApiWatcherCreateDTO watcherDTO) {
        ApiWatcherDTO watcher = controller.addWatcher(watcherDTO);
        if (watcher == null) {
            throw new CustomException("Department or course not found for watcher");
        }
        return watcher;
    }

    @GetMapping("/api/watchers/{id}")
    public ApiWatcherDTO watcher(@PathVariable long id) {
        ApiWatcherDTO watcher = controller.getWatcherDTO(id);
        if (watcher == null) {
            throw new CustomException("Watcher not found for id: " + id);
        }
        return watcher;
    }

    @DeleteMapping("/api/watchers/{id}")
    public void deleteWatcher(@PathVariable long id) {
        if (!controller.deleteWatcher(id)) {
            throw new CustomException("Watcher not found for id: " + id);
        }
    }

    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCustomException(CustomException ex) {
        return ex.getMessage();
    }

    public static class CustomException extends RuntimeException {
        public CustomException(String message) {
            super(message);
        }
    }

}
