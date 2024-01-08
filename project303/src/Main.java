import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    static Map<String, Integer> studentCountPerCourse = new HashMap<>();
    static Map<String, Integer> examDurationPerCourse = new HashMap<>();

    private static final int DAYS_IN_WEEK = 6;
    private static final int START_HOUR = 9;
    private static final int END_HOUR = 18;

    public static void main(String[] args) {

      /*  // READ CSV FILES
        Course[] courses = readCourseCsvFile("classList.csv");

        Classroom[] classrooms = readClassroomCsvFile("classroomCapacity.csv");

        printCourses(courses);
        printClassrooms(classrooms);

        // Extract professors from courses
        Set<String> uniqueProfessors = extractProfessors(courses);
        String[] professorsArray = uniqueProfessors.toArray(new String[0]);
        System.out.println("Professors:");
        for (String professor : professorsArray) {
            System.out.println(professor);
        }
        System.out.println(professorsArray.length);


        // Extract courseIds from courses
        Set<String> uniqueCourseIds = extractCourseIds(courses);

        String[] courseIdsArray = uniqueCourseIds.toArray(new String[0]);
        System.out.println("Course IDs:");

        for (String courseId : courseIdsArray) {
            System.out.println(courseId);
        }
        System.out.println(courseIdsArray.length);
        //Print course id(keys) and number of students(values).HashSet
        printStudentsPerCourse();



        // Backtracing
        /*
         Schedule schedule = backtrackingScheduler(courses, classrooms);
          printSchedule(schedule);
         */

        //infinite loop olduğu için kapattım
      /*  //Get input from user, and put to list these inputs
         Scanner scanner = new Scanner(System.in);
        String input;
        List<String> inputList = new ArrayList<>();

        while(true){
            input = scanner.nextLine();
            inputList.add(input);
        }


        List<String> blockedHours = getUserInputBlockedHours();
        System.out.println("Blocked Hours:");
        for (String blockedHour : blockedHours) {
            System.out.println(blockedHour);
        }
        List<Schedule> schedules = generateGreedySchedules(courses, classrooms,blockedHours);

        if (schedules.isEmpty()) {
            System.out.println("No valid schedules found.");
        } else {
            for (int i = 0; i < schedules.size(); i++) {
                System.out.println("Schedule " + (i + 1) + ":");
                printSchedule(schedules.get(i));
                System.out.println();
            }
        }
*/

        //deneme
        List<Course> courses = readCourseCsvFile("classList.csv");
        printCourses(courses);
        List<Classroom> classrooms = readClassroomCsvFile("classroomCapacity.csv");
        printClassrooms(classrooms);

        // User input for blocked hours
        HashMap<String, HashMap<String, String>> blockedHours = getUserInputBlockedHours();
        System.out.println("Blocked Hours:");
       /* for (String blockedHour : blockedHours) {
            System.out.println(blockedHour);
        }*/
        for (Course course : courses) {
            studentCountPerCourse.put(course.getCourseId(), studentCountPerCourse.getOrDefault(course.getCourseId(), 0) + 1);
            examDurationPerCourse.put(course.getCourseId(), course.getExamDuration());
        }


        System.out.println("Student No and Exam Duration:");
        for (String courseId : studentCountPerCourse.keySet()) {
            int studentCount = studentCountPerCourse.get(courseId);
            int examDuration = examDurationPerCourse.get(courseId);

            System.out.println("Course Code: " + courseId + ", Student Number: " + studentCount + ", Exam Duration: " + examDuration + "minute");

        }

        ExamSchedule scheduler = new ExamSchedule(classrooms, courses);

        blockedHours.forEach((day, hours) -> hours.forEach((hour, courseId) ->
                scheduler.addBlockedHour(day, hour, courseId)));

        scheduler.createSchedule();}


    //try common course blocked hourse
    private static HashMap<String, HashMap<String, String>> getUserInputBlockedHours() {
        HashMap<String, HashMap<String, String>> blockedHours = new HashMap<>();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter blocked hours for common courses (format: Day Hour CourseID):");
        System.out.println("Example: Monday 2 PM TIT102");

        while (true) {
            System.out.print("Enter blocked hour (or type 'done' to finish): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("done")) {
                break;
            }

            String[] parts = input.split(" ");
            if (parts.length == 3) {
                String day = parts[0];
                String hour = parts[1];
                String courseId = parts[2];
                blockedHours.computeIfAbsent(day, k -> new HashMap<>()).put(hour, courseId);
            }
        }

        return blockedHours;
    }




    private static Set<String> extractProfessors(Course[] courses) {
        Set<String> uniqueProfessors = new HashSet<>();

        if (courses != null) {
            for (Course course : courses) {
                uniqueProfessors.add(course.getProfessorName());
            }
        }

        return uniqueProfessors;
    }

    private static Set<String> extractCourseIds(Course[] courses) {
        Set<String> uniqueCourseIds = new HashSet<>();

        if (courses != null) {
            for (Course course : courses) {
                uniqueCourseIds.add(course.getCourseId());
            }
        }
        return uniqueCourseIds;
    }

    private static List<Course> readCourseCsvFile(String csvFile) {
        String line;
                List<Course> courses = null;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Başlık satırını atlayalım
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");

                // Assuming columns are: StudentID, ProfessorName, CourseID, ExamDuration
                if (columns.length == 4) {
                    String studentId = columns[0].trim();
                    String professorName = columns[1].trim();
                    String courseId = columns[2].trim();
                   // studentsPerCourse.put(courseId, studentsPerCourse.getOrDefault(courseId, 0) + 1);
                    int examDuration = Integer.parseInt(columns[3].trim());

                    // studentsPerCourse.put(courseId, studentsPerCourse.getOrDefault(courseId, 0) + 1);
                    Course course = new Course(studentId, professorName, courseId, examDuration);
                    courses.add(course);

                   }}} catch (IOException e) {
            e.printStackTrace();
        }
            return courses;
        }


    private static void printStudentsPerCourse() {
        System.out.println("Number of students per course:");
        for (Map.Entry<String, Integer> entry : studentCountPerCourse.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " students");
        }
    }

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

    private static Course[] addCourseToArray(Course[] courses, Course course) {
        if (courses == null) {
            courses = new Course[1];
            courses[0] = course;
        } else {
            Course[] newArray = new Course[courses.length + 1];
            System.arraycopy(courses, 0, newArray, 0, courses.length);
            newArray[courses.length] = course;
            courses = newArray;
        }
        return courses;
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

    /*

      public static Schedule backtrackingScheduler(Course[] courses, Classroom[]
      classrooms) {
      Schedule schedule = new Schedule();

      // Başlangıç saatini belirle
      int currentHour = START_HOUR;
      int currentDay = 0;

      // Backtracking algoritması ile deneme
      backtrackingHelper(courses, classrooms, schedule, currentDay, currentHour);

      return schedule;
      }

      public static boolean backtrackingHelper(Course[] courses, Classroom[]
      classrooms, Schedule schedule,
      int currentDay, int currentHour) {
      // Tüm sınavlar tamamlandıysa başarıyla tamamlandı
      if (schedule.isComplete(courses.length)) {
      return true;
      }

      // Her sınav için
      for (Course course : courses) {
      // Eğer sınav daha önce planlanmışsa atla
      if (schedule.isExamScheduled(course)) {
      continue;
      }

      // Her sınıf için
      for (Classroom classroom : classrooms) {
      // Eğer sınıf kapasitesi uygunsa devam et
      if (classroom.getCapacity() >= classroom.getCapacity() / 2) {
      // Sınavı planla
      schedule.scheduleExam(currentDay, currentHour, course, classroom);

      // Rekürsif olarak bir sonraki sınavı planla
      int nextDay = (currentHour == END_HOUR) ? currentDay + 1 : currentDay;
      int nextHour = (currentHour + 1) % (END_HOUR + 1);

      if (nextDay < DAYS_IN_WEEK &&
      backtrackingHelper(courses, classrooms, schedule, nextDay, nextHour)) {
      return true; // Başarıyla tamamlandı
      }

      // Planı geri al
      schedule.unscheduleExam(currentDay, currentHour, course, classroom);
      }
      }
      }
      return false; // Başarısız oldu
      }


      private static void printSchedule(Schedule schedule) {
      if (schedule == null) {
      System.out.println("Schedule is not available.");
      return;
      }

      System.out.println("Midterm Exam Schedule:");

      for (int day = 0; day < DAYS_IN_WEEK; day++) {
      System.out.println("Day " + (day + 1) + ":");

      Map<Integer, List<Exam>> daySchedule = schedule.getSchedule(day);
      if (daySchedule != null) {
      for (int hour = START_HOUR; hour <= END_HOUR; hour++) {
      List<Exam> exams = daySchedule.get(hour);

      if (exams != null) {
      for (Exam exam : exams) {
      System.out.println("Time: Day " + (day + 1) + ", Hour " + hour +
      " - Course: " + exam.getCourse().getCourseId() +
      ", Professor: " + exam.getCourse().getProfessorName() +
      ", Classroom: " + exam.getClassroom().getRoomId());
      }
      }
      }
      }
      }
      }
     */

    private static List<Schedule> generateGreedySchedules(Course[] courses, Classroom[] classrooms,List<String> inputList) {
        List<Schedule> schedules = new ArrayList<>();

        List<Course> sortedCourses = new ArrayList<>(List.of(courses));
        Collections.shuffle(sortedCourses); // Shuffle Courses

        //Compose new ArrayList for used courses. I will use this ArrayList to find remaining courses
        List<Course> usedCourse = new ArrayList<>();

        System.out.println(sortedCourses.size());
        for (Course course : sortedCourses) {

            for (int day = 0; day < DAYS_IN_WEEK; day++) {
                for (int hour = START_HOUR; hour <= END_HOUR; hour++) {//saat 6 dahil mi?
                    for (Classroom classroom : classrooms) {

                        for (Map.Entry<String, Integer> entries : studentCountPerCourse.entrySet()) {
                            //I write this if statement for reaching matching present course and course of studentsPerCourse
                            //Because i want to find student number for present course
                            if (course.getCourseId() == entries.getKey()) {
                                //ClassRoom capacity / 2 should be grater then student number
                                // entries.getValue() is student number which take present course
                                if (classroom.getCapacity() / 2 >= entries.getValue()) {
                                    Schedule schedule = new Schedule(); //compose nested map structure as schedule
                                    schedule.scheduleExam(day, hour, course, classroom);
                                    //Add used courses to Arraylist
                                    usedCourse.add(course);
                                    //Add remaining courses to continue control
                                    List<Course> remainingCourses = new ArrayList<>(List.of(courses));
                                    //Bunu kullanırsak önceki döngüde çıkardığımız kurs da eklenmez mi?Bu yüzden modify etcem
                               /*List<Course> remainingCourses = new ArrayList<>(List.of(courses));
                            remainingCourses.remove(course); // Şu anki kursu çıkar*/
                                    Collections.shuffle(remainingCourses);
                                    for (Course remainingCourse : remainingCourses) {
                                        //If it is not in usedCourse ArrayList, find classroom for remaining course
                                        if (!usedCourse.contains(remainingCourse)) {
                                            for (int remainingDay = 0; remainingDay < DAYS_IN_WEEK; remainingDay++) {
                                                for (int remainingHour = START_HOUR; remainingHour <= END_HOUR; remainingHour++) {
                                                    for (Classroom remainingClassroom : classrooms) {
                                                        for (Map.Entry<String, Integer> remainingEntries : studentCountPerCourse.entrySet()) {
                                                            if (remainingCourse.getCourseId() == entries.getKey()) {
                                                                if (remainingClassroom.getCapacity() / 2 >= remainingEntries.getValue()) {
                                                                    schedule.scheduleExam(remainingDay, remainingHour, remainingCourse,
                                                                            remainingClassroom);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        }
                                    //Bunun yeri yanlış olabilir
                                    if (schedule.isComplete(courses.length)) {
                                        schedules.add(schedule);
                                        return schedules; // Bir tane bile çözüm bulduysa yeterli
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return schedules;
    }

    private static void printSchedule(Schedule schedule) {
        if (schedule == null) {
            System.out.println("Schedule is not available.");
            return;
        }
        for (int day = 0; day < DAYS_IN_WEEK; day++) {
            System.out.println("Day " + (day + 1) + ":");
            Map<Integer, List<Exam>> daySchedule = schedule.getSchedule(day);

            if (daySchedule != null) {
                for (int hour = START_HOUR; hour <= END_HOUR; hour++) {
                    List<Exam> exams = daySchedule.get(hour);

                    if (exams != null) {
                        for (Exam exam : exams) {
                            System.out.println("Time: Day " + (day + 1) + ", Hour " + hour +
                                    " - Course: " + exam.getCourse().getCourseId() +
                                    ", Professor: " + exam.getCourse().getProfessorName() +
                                    ", Classroom: " + exam.getClassroom().getRoomId());
                        }
                    }
                }
            }
        }
    }

}
