package Application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {
    List<CourseOffering> courselist = new ArrayList<CourseOffering>();

    public List<CourseOffering> parse(String csv) throws RuntimeException {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(csv));
            br.readLine();
            while((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if(fields[6].startsWith("\"")) {
                    fields[6] = fields[6].substring(1);
                    fields[fields.length - 2] = fields[fields.length - 2].substring(0, fields[fields.length - 2].length() - 1);
                }
                List<String> instructors = new ArrayList<>();
                for (int i = 6; i < fields.length - 1; i++) {
                    instructors.add(fields[i].trim());
                }
                CourseOffering course = new CourseOffering(Integer.parseInt(fields[0]), fields[1].trim(), fields[2].trim(), fields[3].trim(), Integer.parseInt(fields[4]), Integer.parseInt(fields[5]), instructors, fields[fields.length - 1].trim());
                courselist.add(course);
            }
            return courselist;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void printList() {
        for (CourseOffering course : courselist) {
            System.out.println(course.semester + " " + course.subject + " " + course.catalogNumber + " " + course.location + " " + course.enrollmentCapacity + " " + course.enrollmentTotal + " " + course.instructors + " " + course.componentCode);
        }
    }
}
