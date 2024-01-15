import java.util.*;

public class ExamScheduler {
     Map<String, Integer> examDurations; // Course ID -> Exam Duration

    Map<String, List<String>> schedule; // Time Slot -> List of Course IDs
    Map<String, String> professorCourseMap; // Course ID -> Professor Name
    Map<String, List<String>> studentCourseMap; // Student ID -> List of Course IDs
    Map<String, String> courseClassroomMap; // Course ID -> Classroom ID
    Map<String, Integer> courseStudentCountMap = new HashMap<>();
    Map<String, List<String>> courseStudentsMap;
    HashMap<String, HashMap<String, String>> blockedHours; // Day -> (Hour -> CourseID)

    List<Classroom> classrooms;
    List<Course> courses;

      /**
     * Constructor for ExamScheduler.
     *
     * @param classrooms     List of classrooms available for exams.
     * @param allCourses      List of all courses, including student and professor information.
     * @param examDurations   Map containing the duration of each exam (Course ID -> Exam Duration).
     */

    public ExamScheduler(List<Classroom> classrooms, List<Course> allCourses, Map<String, Integer> examDurations) {
        this.classrooms = new ArrayList<>(classrooms);
        this.courseClassroomMap = new HashMap<>();
        this.schedule = new HashMap<>();
        this.blockedHours = new HashMap<>();
        this.examDurations = examDurations;
        courseStudentsMap = new HashMap<>();

        initializeTimeSlots();

        this.professorCourseMap = new HashMap<>();
        this.studentCourseMap = new HashMap<>();

        //for determining every lesson only one time 
        Set<String> uniqueCourseIds = new HashSet<>();


        List<Course> uniqueCourses = new ArrayList<>();
        //represents all students in allCourses csv
        for (Course course : allCourses) {
            if (uniqueCourseIds.add(course.getCourseId())) {
                uniqueCourses.add(course);

                // profesörler ve verdikleri dersler
                professorCourseMap.put(course.getCourseId(), course.getProfessorName());
            }


            if (!studentCourseMap.containsKey(course.getStudentId())) {
                studentCourseMap.put(course.getStudentId(), new ArrayList<>());
            }
            studentCourseMap.get(course.getStudentId()).add(course.getCourseId());
            courseStudentCountMap.merge(course.getCourseId(), 1, Integer::sum);
            courseStudentsMap.computeIfAbsent(course.getCourseId(), k -> new ArrayList<>()).add(course.getStudentId());

        }


        this.courses = uniqueCourses;
        //System.out.println("BAKALIM\n"+ courseStudentsMap);
    }
     /**
     * Adds a blocked hour for a specific course on a given day and hour.
     *
     * @param day       The day on which the blockage occurs.
     * @param hour      The hour during which the blockage occurs.
     * @param courseId  The ID of the course for which the hour is blocked.
     */

    public void addBlockedHour(String day, String hour, String courseId) {
        //System.out.println("Blocked " + day + " " +  hour + " " + courseId);
        blockedHours.computeIfAbsent(day, k -> new HashMap<>()).put(hour, courseId);
    }

    public void createSchedule() {
        //compose time slot
        //initializeTimeSlots(); //not necessary

        //request extra day
        boolean needExtraDay = false;

       // System.out.println("COURSES\n"+ courses.size());
        for (Course course : courses) {

            boolean scheduled = scheduleCourse(course);
            if (!scheduled) {
                needExtraDay = true;
                break;
            }
        }

        if (needExtraDay) {
            addExtraDay(); // Method to add an extra day to the schedule
            resetSchedule(); // Clear the current schedule and try again
            for (Course course : courses) {
                boolean scheduled = scheduleCourse(course);
                if (!scheduled) {
                    //control message
                    System.out.println("Failed to schedule all exams even with an extra day.");
                    return;
                }
            }
        }

        printSchedule();
    }
    private void addExtraDay() {
        String extraDay = "Sunday";
        for (int hour = 9; hour <= 17; hour++) {
            String timeSlot = extraDay + " " + hour + ":00";
            schedule.put(timeSlot, new ArrayList<>());
        }
    }

    private void resetSchedule() {
        for (List<String> courses : schedule.values()) {
            courses.clear(); // Clear the list of courses in each time slot
        }

        // Optionally, reset the courseClassroomMap if you're tracking classroom assignments
        courseClassroomMap.clear();
    }

    public  void printSchedule() {
        System.out.println("Exam Schedule:");

        // Define a custom order for days
        List<String> customDayOrder = Arrays.asList("Monday", "Tuesday" , "Wednesday", "Thursday",  "Friday", "Saturday", "Sunday");

        // Use a custom comparator to sort days according to the custom order
        TreeMap<String, List<String>> sortedSchedule = new TreeMap<>(Comparator.comparingInt(customDayOrder::indexOf));

        // Populate sortedSchedule
        for (Map.Entry<String, List<String>> entry : schedule.entrySet()) {
            String timeSlot = entry.getKey();
            List<String> courseIds = entry.getValue();
            String day = timeSlot.split(" ")[0];
            sortedSchedule.computeIfAbsent(day, k -> new ArrayList<>()).addAll(courseIds);
        }

        // Print the schedule
        for (String day : sortedSchedule.keySet()) {
            System.out.println(day);
            List<String> coursesInDay = sortedSchedule.get(day);
            for (String courseId : coursesInDay) {
                Course course = findCourseById(courseId);
                if (course != null) {
                    String timeSlot = findTimeSlotForCourse(courseId);
                    String classroom = courseClassroomMap.get(courseId);
                    int duration = examDurations.getOrDefault(course.getCourseId(), 30);

                    //Saatleri sıralayarak yazdır

                    System.out.println("  " + formatTimeSlot(timeSlot, duration) + ": " + courseId + " - Room " + classroom);
                }
            }
            System.out.println();
        }
    }

    //time slot of the lesson 
    private String findTimeSlotForCourse(String courseId) {
        for (Map.Entry<String, List<String>> entry : schedule.entrySet()) {
            if (entry.getValue().contains(courseId)) {
                return entry.getKey();
            }
        }
        return "";
    }
    Course findCourseById(String courseId) {
        for (Course course : courses) {
            if (course.getCourseId().equals(courseId)) {
                return course;
            }
        }
        return null;
    }

 /**
     * Formats a given time slot and duration into a human-readable string.
     * @param timeSlot The time slot to format.
     * @param duration The duration of the exam.
     * @return A formatted string representing the time slot and duration.
     */
    String formatTimeSlot(String timeSlot, int duration) {
        // Example entry: "Monday 16:00"
        String[] parts = timeSlot.split(" ");
        String day = parts[0];
        String[] hourMinute = parts[1].split(":");

        int startHour = Integer.parseInt(hourMinute[0]);
        int startMinute = Integer.parseInt(hourMinute[1]);

        String startAmPm = startHour >= 12 ? "PM" : "AM";
        startHour = startHour > 12 ? startHour - 12 : (startHour == 0 ? 12 : startHour);

        // formatting hour and seconds 
        if (startAmPm.equalsIgnoreCase("PM")) {
            startHour = (startHour % 12) + 12;
        }
        String startTime = String.format("%02d:%02d %s", startHour, startMinute, startAmPm);


        // Calculate end time
        int totalMinutes = startMinute + duration;
        int endHour = startHour + totalMinutes / 60;
        int endMinute = totalMinutes % 60;

        if (endHour >= 12) {
            endHour -= 12;
            startAmPm = "PM";
        }

        if (startAmPm.equalsIgnoreCase("PM")) {
            endHour = (endHour % 12) + 12;
        }

        String endTime = String.format("%02d:%02d %s", endHour, endMinute, startAmPm);

        return day + " " + startTime + " - " + endTime;
    }


    private void initializeTimeSlots() {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        for (String day : days) {
            for (int hour = 9; hour <= 17; hour++) {
                String timeSlot = day + " " + hour + ":00";
                schedule.put(timeSlot, new ArrayList<>());
            }
        }
    }
/**
     * Attempts to schedule a course into an available time slot.
     * @param course The course to be scheduled.
     * @return true if the course was successfully scheduled, false otherwise.
     */
    private boolean scheduleCourse(Course course) {

        int examDuration = examDurations.getOrDefault(course.getCourseId(), 30); // Varsayılan süre 30 dakika olsun

        for (String timeSlot : schedule.keySet()) {
            if (isSlotAvailableForDuration(timeSlot, examDuration, course) ) {

                if (assignClassroomToCourse(timeSlot, course, examDuration)) {
                    schedule.get(timeSlot).add(course.getCourseId());
                    markOccupiedTimeSlots(timeSlot, examDuration);
                    return true;
                }
            }
        }
        return false;
    }

      /**
     * Marks time slots as occupied for a certain duration starting from a given time slot.
     * @param startTimeSlot The starting time slot.
     * @param duration The duration for which to mark time slots as occupied.
     */
    private void markOccupiedTimeSlots(String startTimeSlot, int duration) {
        String[] parts = startTimeSlot.split(" ");
        String startDay = parts[0];
        int startHour = Integer.parseInt(parts[1].split(":")[0]);

        for (int i = 0; i < duration; i += 30) { // 30 dakikalık zaman dilimleri için döngü
            int currentHour = startHour + (i / 60);
            String currentHourString = String.format("%02d:00", currentHour);
            String currentTimeSlot = startDay + " " + currentHourString;

            if (schedule.containsKey(currentTimeSlot)) {
                // Zaman dilimi schedule'da varsa, bu zaman dilimini meşgul olarak işaretle
                List<String> occupiedCourses = schedule.get(currentTimeSlot);
                if (!occupiedCourses.contains("OCCUPIED")) {
                    occupiedCourses.add("OCCUPIED");
                }
            } else {
                //If time slot is not in schedule, add "OCCUPIED" label to this time slot 
                schedule.put(currentTimeSlot, new ArrayList<>(Arrays.asList("OCCUPIED")));
            }
        }
    }

     /**
     * Checks if a time slot is available for a course for a given duration.
     * @param startTimeSlot The starting time slot.
     * @param duration The duration for which to check availability.
     * @param course The course for which to check the availability.
     * @return true if the time slot is available, false otherwise.
     */
    private boolean isSlotAvailableForDuration(String startTimeSlot, int duration, Course course) {
        String[] parts = startTimeSlot.split(" ");
        String startDay = parts[0];
        int startHour = Integer.parseInt(parts[1].split(":")[0]);

        for (int i = 0; i < duration; i += 30) { // 30 dakikalık zaman dilimleri için döngü
            int currentHour = startHour + (i / 60);
            String currentHourString = String.format("%02d:00", currentHour);
            String currentTimeSlot = startDay + " " + currentHourString;

            if (!schedule.containsKey(currentTimeSlot) || !isSlotAvailable(currentTimeSlot, course)) {
                return false; // If time slot not available or is not in schedule 
            }
        }
        return true; //All time slots are available 
    }
 /**
     * Assigns a classroom to a course for a specific time slot.
     * @param timeSlot The time slot for which to assign a classroom.
     * @param course The course to which the classroom is to be assigned.
     * @param duration The duration of the exam.
     * @return true if a classroom was successfully assigned, false otherwise.
     */
    private boolean assignClassroomToCourse(String timeSlot, Course course, int duration) {
        // Calculate the number of students in the course from the studentCourseMap
        //  List<String> studentsInCourse = studentCourseMap.get(course.getCourseID());
        //  System.out.println("Course id: "+course.getCourseId()+" \nSTUDENT LIST "+studentsInCourse);
        int courseStudentCount = courseStudentCountMap.getOrDefault(course.getCourseId(), 0);
        int requiredCapacity = courseStudentCount * 2;


        int accumulatedCapacity = 0;
        //int classSize = (studentsInCourse != null) ? studentsInCourse.size() : 0;
        List<String> assignedClassrooms = new ArrayList<>();
        String day = timeSlot.split(" ")[0];
        String hour = timeSlot.split(" ")[1];
        HashMap<String, String> dayBlockedHours = blockedHours.getOrDefault(day, new HashMap<>());

        for (Classroom classroom : classrooms) {
            if (!isClassroomUsed(timeSlot, classroom.getRoomId())) {
                accumulatedCapacity += classroom.getCapacity();
                assignedClassrooms.add(classroom.getRoomId());
                if (accumulatedCapacity >= requiredCapacity) {
                    // Yeterli kapasiteye ulaşıldı, sınıfları kursa ata
                    courseClassroomMap.put(course.getCourseId(), String.join(", ", assignedClassrooms));
                    markOccupiedTimeSlots(timeSlot, duration);
                    return true;
                }
            }
        }
        // If no classroom is assigned, print a message
        System.out.println("Failed to assign a classroom to Course ID: " + course.getCourseId());

    return false;}

     /**
     * Checks if a time slot is available for scheduling a course.
     * @param timeSlot The time slot to check for availability.
     *
 * @param course The course for which the availability is to be checked.
 * @return true if the time slot is available, false otherwise.
 */

    private boolean isSlotAvailable(String timeSlot, Course course) {
        // Check if any student in the course has an exam at this time slot
        List<String> studentsInCourse = courseStudentsMap.get(course.getCourseId());
        if (studentsInCourse != null) {
            for (String studentId : studentsInCourse) {
                if (isStudentBusy(timeSlot, studentId)) {
                    return false;
                }
            }
        }
        // Check if the professor has an exam at this time slot
        String professor = course.getProfessorName();
        if (isProfessorBusy(timeSlot, professor)) {
            return false;
        }

        String day = timeSlot.split(" ")[0];
        String hour = timeSlot.split(" ")[1];


        HashMap<String, String> dayBlockedHours = blockedHours.getOrDefault(day, new HashMap<>());

        if (dayBlockedHours.containsKey(hour) && !dayBlockedHours.get(hour).equals(course.getCourseId())) {
            return false; // The time slot is blocked by another course
        }

        // Check for classroom availability and capacity
        return isClassroomAvailable(timeSlot, course);
    }
/**
 * Checks if a student is already scheduled for an exam in a given time slot.
 * @param timeSlot The time slot to check.
 * @param studentId The ID of the student.
 * @return true if the student is busy in the given time slot, false otherwise.
 */
    private boolean isStudentBusy(String timeSlot, String studentId) {
        List<String> courses = studentCourseMap.get(studentId);
        if (courses != null) {
            for (String courseId : courses) {
                if (schedule.get(timeSlot).contains(courseId)) {
                    return true; // Student has another exam at this time slot
                }
            }
        }
        return false;
    }

     /**
 * Checks if a professor is already scheduled for an exam in a given time slot.
 * @param timeSlot The time slot to check.
 * @param professor The name of the professor.
 * @return true if the professor is busy in the given time slot, false otherwise.
 */
    private boolean isProfessorBusy(String timeSlot, String professor) {
        for (Map.Entry<String, String> entry : professorCourseMap.entrySet()) {
            if (entry.getValue().equals(professor) && schedule.get(timeSlot).contains(entry.getKey())) {
                return true; // Professor has another exam at this time slot
            }
        }
        return false;
    }

     /**
 * Checks if a classroom is available for a course at a given time slot.
 * @param timeSlot The time slot to check.
 * @param course The course for which to check classroom availability.
 * @return true if an appropriate classroom is available, false otherwise.
 */
    private boolean isClassroomAvailable(String timeSlot, Course course) {
        // Calculate the number of students in the course from the studentCourseMap
        List<String> studentsInCourse = studentCourseMap.get(course.getStudentId());
        int classSize = (studentsInCourse != null) ? studentsInCourse.size() : 0;

        for (Classroom classroom : classrooms) {
            if (classroom.getCapacity() >= classSize * 2) {
                // Check if this classroom is already used in this time slot
                boolean isUsed = isClassroomUsed(timeSlot, classroom.getRoomId());
                if (!isUsed) {
                    return true; // Found an available classroom with sufficient capacity
                }
            }
        }
        return false;
    }
/**
 * Checks if a classroom is already used in a given time slot.
 * @param timeSlot The time slot to check.
 * @param roomId The ID of the classroom.
 * @return true if the classroom is used in the time slot, false otherwise.
 */
    private boolean isClassroomUsed(String timeSlot, String roomId) {
        //Controll assigning class of all courses which plaining in time slot  
        if (!schedule.containsKey(timeSlot)) {
            return false; //If time slot is not in schedule map, this class is not used. 
        }

        for (String courseId : schedule.get(timeSlot)) {
            String assignedRooms = courseClassroomMap.get(courseId);
            if (assignedRooms != null && Arrays.asList(assignedRooms.split(", ")).contains(roomId)) {
                return true; // Specified class is used by another course in same time slot 
            }
        }

        return false; //  Specified class is not used in this time slot 
    }




}
