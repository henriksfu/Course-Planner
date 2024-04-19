package Application;

import AllApiDtoClasses.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Date;

public class Controller {
    static List<CourseOffering> courses = new ArrayList<>();
    public static void startApplication(String csv) {
        CsvParser csvParser = new CsvParser();
        courses = csvParser.parse(csv);
        mapCourses();
    }

    private static Map<ApiDepartmentDTO, List<Course>> departmentDTOMap = new HashMap<>();
    private static long deptId;
    private static long courseId;
    private static long offeringId;
    private static final List<ApiWatcherDTO> watcherDTOList = new ArrayList<>();
    private static long watcherId;

    public static void addOffering(CourseOffering course) {
        addDepartment(course.subject);
        addCourse(getDepartmentId(course.subject), course.catalogNumber);
        addOffering(getDepartmentId(course.subject), getCourseId(getDepartmentId(course.subject), course.catalogNumber), course.location, course.instructors, getTerm(course.semester), course.semester, getYear(course.semester));
        addSection(getDepartmentId(course.subject), getCourseId(getDepartmentId(course.subject), course.catalogNumber), getOfferingId(getDepartmentId(course.subject), getCourseId(getDepartmentId(course.subject), course.catalogNumber), course.location, course.semester), course.componentCode, course.enrollmentCapacity, course.enrollmentTotal);
        departmentDTOMap = departmentDTOMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(ApiDepartmentDTO::getName)))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().sorted(Comparator.comparing(c -> c.courseDTO.catalogNumber)).collect(Collectors.toList()),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }
    public static void mapCourses() {
        for(CourseOffering course : courses) {
            addOffering(course);
        }

    }

    private static int getYear(int semester) {
        return (1900 + semester / 10);
    }
    private static String getTerm(int semester) {
        return semester % 10 == 1 ? "Spring" : semester % 10 == 4 ? "Summer" : "Fall";
    }
    private static long getDepartmentId(String subject) {
        return departmentDTOMap.keySet().stream().filter(departmentDTO -> departmentDTO.name.equals(subject)).findFirst().get().deptId;
    }

    private static long getCourseId(long deptId, String catalogNumber) {
        for(ApiDepartmentDTO departmentDTO : departmentDTOMap.keySet()) {
            if(departmentDTO.deptId == deptId) {
                return departmentDTOMap.get(departmentDTO).stream()
                        .filter(course -> course.courseDTO.catalogNumber.equals(catalogNumber)).findFirst().get().courseDTO.courseId;
            }
        }
        return -1;
    }
    private static long getOfferingId(long deptId, long courseId, String location, long semester) {
        for(ApiDepartmentDTO departmentDTO : departmentDTOMap.keySet()) {
            if(departmentDTO.deptId == deptId) {
                for(Course course : departmentDTOMap.get(departmentDTO)) {
                    if(course.courseDTO.courseId == courseId) {
                        return course.courseOfferingDTOList.stream()
                                .filter(offering -> offering.courseOfferingDTO.location.equals(location) && offering.courseOfferingDTO.semesterCode == semester)
                                .findFirst().get().courseOfferingDTO.courseOfferingId;
                    }
                }
            }
        }
        return -1;
    }
    private static void addDepartment(String subject) {
        boolean departmentExists = departmentDTOMap.keySet().stream()
                .anyMatch(departmentDTO -> departmentDTO.name.equals(subject));
        if(!departmentExists) {
            deptId++;
            ApiDepartmentDTO departmentDTO = new ApiDepartmentDTO(deptId, subject);
            departmentDTOMap.put(departmentDTO, new ArrayList<>());
        }
    }
    private static void addCourse(long deptId, String catalogNumber) {
        for(ApiDepartmentDTO departmentDTO : departmentDTOMap.keySet()) {
            if(departmentDTO.deptId == deptId) {
                boolean courseExists = departmentDTOMap.get(departmentDTO).stream()
                        .anyMatch(course -> course.courseDTO.catalogNumber.equals(catalogNumber));
                if(!courseExists) {
                    courseId++;
                    ApiCourseDTO courseDTO = new ApiCourseDTO(courseId, catalogNumber);
                    departmentDTOMap.get(departmentDTO).add(new Course(courseDTO, new ArrayList<>()));
                }
            }
        }
    }
    private static void addOffering(long deptId, long courseId, String location, List<String> instructors, String term, long semester, int year) {
        for(ApiDepartmentDTO departmentDTO : departmentDTOMap.keySet()) {
            if(departmentDTO.deptId == deptId) {
                for(Course course : departmentDTOMap.get(departmentDTO)) {
                    if(course.courseDTO.courseId == courseId) {
                        if(course.courseOfferingDTOList.stream().noneMatch(o -> o.courseOfferingDTO.location.equals(location) && o.courseOfferingDTO.semesterCode == semester)) {
                            offeringId++;
                            ApiCourseOfferingDTO offeringDTO = new ApiCourseOfferingDTO(offeringId, location, String.join(", ", instructors), term, semester, year);
                            course.courseOfferingDTOList.add(new Offering(offeringDTO, new ArrayList<>()));
                        }
                        else {
                            course.courseOfferingDTOList.stream().filter(o -> o.courseOfferingDTO.location.equals(location) && o.courseOfferingDTO.semesterCode == semester).findFirst().get().courseOfferingDTO.setInstructors(String.join(", ", instructors));
                        }
                    }
                }
            }
        }
    }

    private static void addSection(long deptId, long courseId, long offeringId, String componentCode, int enrollmentCapacity, int enrollmentTotal) {
        for(ApiDepartmentDTO departmentDTO : departmentDTOMap.keySet()) {
            if(departmentDTO.deptId == deptId) {
                for(Course course : departmentDTOMap.get(departmentDTO)) {
                    if(course.courseDTO.courseId == courseId) {
                        for(Offering offering : course.courseOfferingDTOList) {
                            if(offering.courseOfferingDTO.courseOfferingId == offeringId) {
                                if(offering.offeringSectionDTOList.stream().noneMatch(s -> s.type.equals(componentCode))) {
                                    offering.offeringSectionDTOList.add(new ApiOfferingSectionDTO(componentCode, enrollmentCapacity, enrollmentTotal));
                                } else {
                                    offering.offeringSectionDTOList.stream().filter(s -> s.type.equals(componentCode)).findFirst().get().setEnrollmentCapacity(enrollmentCapacity);
                                    offering.offeringSectionDTOList.stream().filter(s -> s.type.equals(componentCode)).findFirst().get().setEnrollmentTotal(enrollmentTotal);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    public String modelDump() {
        for (ApiDepartmentDTO departmentDTO : departmentDTOMap.keySet()) {
            for (Course course : departmentDTOMap.get(departmentDTO)) {
                System.out.println(departmentDTO.name + " " + course.courseDTO.catalogNumber);
                for (Offering offering : course.courseOfferingDTOList) {
                    System.out.println("\t" + offering.courseOfferingDTO.semesterCode + " in " + offering.courseOfferingDTO.location + " by " + offering.courseOfferingDTO.instructors);
                    for (ApiOfferingSectionDTO section : offering.offeringSectionDTOList) {
                        System.out.println("\t\tType: " + section.type + ", Enrollment: " + section.enrollmentTotal + "/" + section.enrollmentCap);
                    }
                }
            }
        }
        return "SUCCESS!";
    }

    public List<ApiDepartmentDTO> getDepartmentDTOList() {
        List<ApiDepartmentDTO> departmentDTOList = new ArrayList<>(departmentDTOMap.keySet().stream().toList());
        departmentDTOList.sort(Comparator.comparing(departmentDTO -> departmentDTO.name));
        return departmentDTOList;
    }

    public List<ApiCourseDTO> getCourseDTOList(long deptId) {
        for(ApiDepartmentDTO departmentDTO : departmentDTOMap.keySet()) {
            if(departmentDTO.deptId == deptId) {
                return departmentDTOMap.get(departmentDTO).stream()
                        .map(course -> course.courseDTO).sorted(Comparator.comparing(courseDTO -> courseDTO.catalogNumber)).collect(Collectors.toList());
            }
        }
        return null;
    }
    public List<ApiCourseOfferingDTO> getOfferingDTOList(long deptId, long courseId) {
        for(ApiDepartmentDTO departmentDTO : departmentDTOMap.keySet()) {
            if(departmentDTO.deptId == deptId) {
                for(Course course : departmentDTOMap.get(departmentDTO)) {
                    if(course.courseDTO.courseId == courseId) {
                        List<ApiCourseOfferingDTO> offeringDTOList = new ArrayList<>(course.courseOfferingDTOList.stream()
                                .map(offering -> offering.courseOfferingDTO).toList());
                        offeringDTOList.sort(Comparator.comparing(offeringDTO -> offeringDTO.semesterCode));
                        return offeringDTOList;
                    }
                }
            }
        }
        return null;
    }
    public List<ApiOfferingSectionDTO> getOfferingDTO(long deptId, long courseId, long offeringId) {
        for(ApiDepartmentDTO departmentDTO : departmentDTOMap.keySet()) {
            if(departmentDTO.deptId == deptId) {
                for(Course course : departmentDTOMap.get(departmentDTO)) {
                    if(course.courseDTO.courseId == courseId) {
                        for(Offering offering : course.courseOfferingDTOList) {
                            if(offering.courseOfferingDTO.courseOfferingId == offeringId) {
                                return offering.offeringSectionDTOList;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public void addEvent(CourseOffering courseOffering) {
        Date date = new Date();
        SimpleDateFormat simpleFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        String formattedDate = simpleFormat.format(date);

        for(ApiWatcherDTO watcherDTO : watcherDTOList) {
            if(watcherDTO.department.name.equals(courseOffering.subject) && watcherDTO.course.catalogNumber.equals(courseOffering.catalogNumber)) {
                String event = formattedDate + ": Added section " + courseOffering.componentCode + " with enrollment: " + courseOffering.enrollmentTotal + "/" + courseOffering.enrollmentCapacity  + " to offering " + getTerm(courseOffering.semester) + " " + getYear(courseOffering.semester);
                watcherDTO.events.add(event);
            }
        }

    }
    public List<ApiWatcherDTO> getWatcherDTOList() {
        return watcherDTOList;
    }
    public ApiWatcherDTO addWatcher(ApiWatcherCreateDTO watcherDTO) {
        ApiDepartmentDTO departmentDTO = getDepartmentDTO(watcherDTO.deptId);
        ApiCourseDTO courseDTO = getCourseDTO(watcherDTO.deptId, watcherDTO.courseId);
        if(departmentDTO == null || courseDTO == null) {
           return null;
        }
        ApiWatcherDTO apiWatcherDTO = new ApiWatcherDTO(watcherId++, departmentDTO, courseDTO, new ArrayList<>());
        watcherDTOList.add(apiWatcherDTO);
        return apiWatcherDTO;
    }

    private ApiDepartmentDTO getDepartmentDTO(long deptId) {
        for(ApiDepartmentDTO departmentDTO : departmentDTOMap.keySet()) {
            if(departmentDTO.deptId == deptId) {
                return departmentDTO;
            }
        }
        return null;
    }
    private ApiCourseDTO getCourseDTO(long deptId, long courseId) {
        for(ApiDepartmentDTO departmentDTO : departmentDTOMap.keySet()) {
            if(departmentDTO.deptId == deptId) {
                return departmentDTOMap.get(departmentDTO).stream()
                        .filter(course -> course.courseDTO.courseId == courseId).findFirst().get().courseDTO;
            }
        }
        return null;
    }

    public ApiWatcherDTO getWatcherDTO(long id) {
        return watcherDTOList.stream().filter(watcherDTO -> watcherDTO.id == id).findFirst().orElse(null);
    }

    public Boolean deleteWatcher(long id) {
        return watcherDTOList.removeIf(watcherDTO -> watcherDTO.id == id);
    }
}
