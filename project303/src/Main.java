import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
   // static Map<String, Integer> studentsPerCourse=new HashMap<>();
   static Map<String, Integer> studentCountPerCourse = new HashMap<>();
   static Map<String, Integer> examDurationPerCourse = new HashMap<>();
    static Map<String, Integer> examDurations = new HashMap<>();

    public static void main(String[] args) {

        List<Course> courses = readCourseCsvFile("C:\\Users\\lenovo\\Desktop\\projeY\\project303\\classList.csv");
        //printCourses(courses);
        List<Classroom> classrooms = readClassroomCsvFile("C:\\Users\\lenovo\\Desktop\\projeY\\project303\\classroomCapacity.csv");
        //printClassrooms(classrooms);

        // User input for blocked hours
        HashMap<String, HashMap<String, String>> blockedHours = getUserInputBlockedHours();
        System.out.println("Blocked Hours:");

        for (String day : blockedHours.keySet()) {
            for (String hour : blockedHours.get(day).keySet()) {
                String courseId = blockedHours.get(day).get(hour);
                System.out.println(day + " " + hour + " " + courseId);
            }
        }

        ExamScheduler scheduler = new ExamScheduler(classrooms, courses, examDurations);

        blockedHours.forEach((day, hours) -> hours.forEach((hour, courseId) -> scheduler.addBlockedHour(day, hour, courseId)));
        scheduler.createSchedule();

      /*  for (Course course : courses) {
            studentCountPerCourse.put(course.getCourseID(), studentCountPerCourse.getOrDefault(course.getCourseID(), 0) + 1);
            examDurationPerCourse.put(course.getCourseID(), course.getExamDuration());
        }
        System.out.println("Öğrenci Sayıları ve Sınav Süreleri:");
        for (String courseId : studentCountPerCourse.keySet()) {
            int studentCount = studentCountPerCourse.get(courseId);
            int examDuration = examDurationPerCourse.get(courseId);

            System.out.println("Ders Kodu: " + courseId + ", Öğrenci Sayısı: " + studentCount + ", Sınav Süresi: " + examDuration + " dakika");
        }
*/

    }


    // Modify the method to return HashMap<String, Set<String>>
    private static HashMap<String, HashMap<String, String>> getUserInputBlockedHours() {
        HashMap<String, HashMap<String, String>> blockedHours = new HashMap<>();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter blocked hours for common courses (format: Day Hour CourseID):");
        System.out.println("Example: Monday 2 PM TİT101");

        while (true) {
            System.out.print("Enter blocked hour (or type 'done' to finish): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("done")) {
                break;
            }

            // Ayrıştırma işlemi
            String[] parts = input.split("\\s+");
            if (parts.length == 4) {
                String day = parts[0];
                String hour = convertTo24HourFormat(parts[1], parts[2]);
                String courseID = parts[3];

                // blockedHours'a ekleme işlemi
                if (!blockedHours.containsKey(day)) {
                    blockedHours.put(day, new HashMap<>());
                }

                blockedHours.get(day).put(hour, courseID);
            } else {
                System.out.println("Invalid input format. Please use the format: Day Hour CourseID");
            }
        }

        return blockedHours;
    }
    public static String convertTo24HourFormat(String hour, String ampm) {
        int hourValue = Integer.parseInt(hour);

        if (ampm.equalsIgnoreCase("PM")) {
            hourValue = (hourValue % 12) + 12;
        }

        return String.format("%02d", hourValue) + ":00";
    }


    private static void printCourses(List<Course> courses) {
        if (courses != null && !courses.isEmpty()) {
            System.out.println("Printing Courses:");
            for (Course course : courses) {
                System.out.println(course);
            }
        } else {
            System.out.println("No courses to print.");
        }
    }

    private static void printClassrooms(List<Classroom> classrooms) {
        if (classrooms != null && !classrooms.isEmpty()) {
            System.out.println("Printing Classrooms:");
            for (Classroom classroom : classrooms) {
                System.out.println(classroom);
            }
        } else {
            System.out.println("No classrooms to print.");
        }
    }


    //read classList.csv
    private static List<Course> readCourseCsvFile(String csvFile) {
        String line;
        List<Course> courses = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length == 4) {
                    String studentId = columns[0].trim();
                    String professorName = columns[1].trim();
                    String courseId = columns[2].trim();
                    int examDuration = Integer.parseInt(columns[3].trim());
                    examDurations.put(courseId, examDuration);
                   // studentsPerCourse.put(courseId, studentsPerCourse.getOrDefault(courseId, 0) + 1);
                    Course course = new Course(studentId, professorName, courseId, examDuration);
                    courses.add(course);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return courses;
    }

    private static void addCourseToList(List<Course> courses, Course course) {
        courses.add(course);
    }


    //read classroomCapacities.csv
    private static List<Classroom> readClassroomCsvFile(String csvFile) {
        String line;
        List<Classroom> classrooms = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length == 2) {
                    String roomId = columns[0].trim();
                    int capacity = Integer.parseInt(columns[1].trim());

                    Classroom classroom = new Classroom(roomId, capacity);
                    classrooms.add(classroom);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classrooms;
    }

    private static Classroom[] addClassroomToArray(Classroom[] classrooms, Classroom classroom) {
        if (classrooms == null) {
            classrooms = new Classroom[1];
            classrooms[0] = classroom;
        } else {
            Classroom[] newArray = new Classroom[classrooms.length + 1];
            System.arraycopy(classrooms, 0, newArray, 0, classrooms.length);
            newArray[classrooms.length] = classroom;
            classrooms = newArray;
        }
        return classrooms;
    }
}