package AllApiDtoClasses;

public class ApiOfferingSectionDTO {
    public String type;
    public int enrollmentCap;
    public int enrollmentTotal;

    public ApiOfferingSectionDTO(String type, int enrollmentCap, int enrollmentTotal) {
        this.type = type;
        this.enrollmentCap = enrollmentCap;
        this.enrollmentTotal = enrollmentTotal;
    }

    public void setEnrollmentCapacity(int enrollmentCapacity) {
        this.enrollmentCap += enrollmentCapacity;
    }
    public void setEnrollmentTotal(int enrollmentTotal) {
        this.enrollmentTotal += enrollmentTotal;
    }
}
