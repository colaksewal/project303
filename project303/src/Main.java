import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    private static final int DAYS_IN_WEEK = 6;
    private static final int START_HOUR = 9;
    private static final int END_HOUR = 18;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: CsvReader <classListCsvFile> <classroomCapacityCsvFile>");
            System.exit(1);
        }

        String classListFile = args[0];
        String classroomCapacityFile = args[1];

        //READ CSV FILES
        Course[] courses = readCourseCsvFile(classListFile);


        Classroom[] classrooms = readClassroomCsvFile(classroomCapacityFile);


        //printCourses(courses);
        //printClassrooms(classrooms);

        //Backtracing
        /*Schedule schedule = backtrackingScheduler(courses, classrooms);
        printSchedule(schedule);*/

        List<Schedule> schedules = generateGreedySchedules(courses, classrooms);

        if (schedules.isEmpty()) {
            System.out.println("No valid schedules found.");
        } else {
            for (int i = 0; i < schedules.size(); i++) {
                System.out.println("Schedule " + (i + 1) + ":");
                printSchedule(schedules.get(i));
                System.out.println();
            }
        }


    }




    private static Course[] readCourseCsvFile(String csvFile) {
        String line;
        Course[] courses = null;

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
                    String examDurationString = columns[3].trim();

                    // "Exam Duration" sütunu sayısal bir değerse devam edelim
                    try {
                        int examDuration = Integer.parseInt(examDurationString);

                        // Create Course object and add to the array
                        Course course = new Course(studentId, professorName, courseId, examDuration);
                        courses = addCourseToArray(courses, course);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Exam Duration format: " + examDurationString);
                    }
                } else {
                    break; // Sütun sayısı 4 değilse döngüden çık
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return courses;
    }

    private static Classroom[] readClassroomCsvFile(String csvFile) {
        String line;
        Classroom[] classrooms = null;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Başlık satırını atlayalım
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");

                // Assuming columns are: RoomID, Capacity
                if (columns.length == 2) {
                    String roomId = columns[0].trim();
                    String capacityString = columns[1].trim();

                    try {
                        int capacity = Integer.parseInt(capacityString);
                        Classroom classroom = new Classroom(roomId, capacity);
                        classrooms = addClassroomToArray(classrooms, classroom);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Capacity format: " + capacityString);
                    }
                } else {
                    System.out.println("Invalid column order. The CSV should have exactly 2 columns.");
                    break; // Sütun sayısı 2 değilse döngüden çık
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

    private static void printCourses(Course[] courses) {
        if (courses != null) {
            System.out.println("Printing Courses:");
            for (Course course : courses) {
                System.out.println(course);
            }
        } else {
            System.out.println("No courses to print.");
        }
    }

    private static void printClassrooms(Classroom[] classrooms) {
        if (classrooms != null) {
            System.out.println("Printing Classrooms:");
            for (Classroom classroom : classrooms) {
                System.out.println(classroom);
            }
        } else {
            System.out.println("No classrooms to print.");
        }
    }


    /*

    public static Schedule backtrackingScheduler(Course[] courses, Classroom[] classrooms) {
        Schedule schedule = new Schedule();

        // Başlangıç saatini belirle
        int currentHour = START_HOUR;
        int currentDay = 0;

        // Backtracking algoritması ile deneme
        backtrackingHelper(courses, classrooms, schedule, currentDay, currentHour);

        return schedule;
    }

    public static boolean backtrackingHelper(Course[] courses, Classroom[] classrooms, Schedule schedule,
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
    }*/

    private static List<Schedule> generateGreedySchedules(Course[] courses, Classroom[] classrooms) {
        List<Schedule> schedules = new ArrayList<>();

        List<Course> sortedCourses = new ArrayList<>(List.of(courses));
        Collections.shuffle(sortedCourses); // Kursları karıştır

        for (Course course : sortedCourses) {
            for (int day = 0; day < DAYS_IN_WEEK; day++) {
                for (int hour = START_HOUR; hour <= END_HOUR; hour++) {
                    for (Classroom classroom : classrooms) {
                        if (classroom.getCapacity() >= classroom.getCapacity() / 2) {
                            Schedule schedule = new Schedule();
                            schedule.scheduleExam(day, hour, course, classroom);

                            // Diğer sınavları da random sırayla ekleyebilirsiniz
                            List<Course> remainingCourses = new ArrayList<>(List.of(courses));
                            remainingCourses.remove(course); // Şu anki kursu çıkar

                            Collections.shuffle(remainingCourses);

                            for (Course remainingCourse : remainingCourses) {
                                for (int remainingDay = 0; remainingDay < DAYS_IN_WEEK; remainingDay++) {
                                    for (int remainingHour = START_HOUR; remainingHour <= END_HOUR; remainingHour++) {
                                        for (Classroom remainingClassroom : classrooms) {
                                            if (remainingClassroom.getCapacity() >= remainingClassroom.getCapacity() / 2) {
                                                schedule.scheduleExam(remainingDay, remainingHour, remainingCourse, remainingClassroom);
                                            }
                                        }
                                    }
                                }
                            }

                            if (schedule.isComplete(courses.length)) {
                                schedules.add(schedule);
                                return schedules; // Bir tane bile çözüm bulduysa yeterli
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










