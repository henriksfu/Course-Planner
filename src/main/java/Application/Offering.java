package Application;

import AllApiDtoClasses.ApiCourseOfferingDTO;
import AllApiDtoClasses.ApiOfferingSectionDTO;

import java.util.List;

public class Offering {
    ApiCourseOfferingDTO courseOfferingDTO;
    List<ApiOfferingSectionDTO> offeringSectionDTOList;

    public Offering(ApiCourseOfferingDTO courseOfferingDTO, List<ApiOfferingSectionDTO> offeringSectionDTOList) {
        this.courseOfferingDTO = courseOfferingDTO;
        this.offeringSectionDTOList = offeringSectionDTOList;
    }

    public ApiCourseOfferingDTO getCourseOfferingDTO() {
        return courseOfferingDTO;
    }

    public List<ApiOfferingSectionDTO> getOfferingSectionDTOList() {
        return offeringSectionDTOList;
    }
}
