import java.util.*;

public class ExamSchedule {
    Map<String, List<String>> schedule; // Time Slot -> List of Course IDs
    Map<String, String> professorCourseMap; // Course ID -> Professor Name
    Map<String, List<String>> studentCourseMap; // Student ID -> List of Course IDs
    Map<String, String> courseClassroomMap; // Course ID -> Classroom ID

    private HashMap<String, HashMap<String, String>> blockedHours; // Day -> (Hour -> CourseID)

    List<Classroom> classrooms;
    List<Course> courses;

    public ExamSchedule(List<Classroom> classrooms, List<Course> courses) {
        this.classrooms = new ArrayList<>(classrooms);
        this.courses = new ArrayList<>(courses);
        this.courseClassroomMap = new HashMap<>();
        this.schedule = new HashMap<>();
        this.blockedHours = new HashMap<>();
        initializeTimeSlots();

        this.professorCourseMap = new HashMap<>();
        this.studentCourseMap = new HashMap<>();
        for (Course course : courses) {
            // Map each course to its professor
            professorCourseMap.put(course.getCourseId(), course.getProfessorName());

            // Add course to the list of courses for each student
            if (!studentCourseMap.containsKey(course.getStudentId())) {
                studentCourseMap.put(course.getStudentId(), new ArrayList<>());
            }
            studentCourseMap.get(course.getStudentId()).add(course.getCourseId());
        }
    }

    public void addBlockedHour(String day, String hour, String courseId) {
        blockedHours.computeIfAbsent(day, k -> new HashMap<>()).put(hour, courseId);
    }
    public void createSchedule() {
        initializeTimeSlots();
        boolean needExtraDay = false;

        for (Course course : courses) {
            boolean scheduled = scheduleCourse(course);
            if (!scheduled) {
                needExtraDay = true;
                break; // If unable to schedule a course, break out of the loop
            }
        }

        if (needExtraDay) {
            addExtraDay(); // Method to add an extra day to the schedule
            resetSchedule(); // Clear the current schedule and try again
            for (Course course : courses) {
                boolean scheduled = scheduleCourse(course);
                if (!scheduled) {
                    // Handling in case even with an extra day, scheduling is not possible
                    System.out.println("Failed to schedule all exams even with an extra day.");
                    return;
                }
            }
        }

        printSchedule(); // Method to print the final schedule
    }
    private void addExtraDay() {
        String extraDay = "Sunday";
        for (int hour = 9; hour <= 17; hour++) { // 9:00 AM to 5:00 PM, assuming 1-hour slots
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

    void printSchedule() {
        System.out.println("Final Exam Schedule:");
        for (Map.Entry<String, List<String>> entry : schedule.entrySet()) {
            String timeSlot = entry.getKey();
            List<String> coursesInSlot = entry.getValue();

            System.out.println(timeSlot + ":");
            for (String courseId : coursesInSlot) {
                System.out.println(" - Course ID: " + courseId + ", Classroom: " + findClassroomIdForCourse(courseId));
            }
        }
    }

    private void initializeTimeSlots() {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        for (String day : days) {
            for (int hour = 9; hour <= 17; hour++) { // 9:00 AM to 5:00 PM, assuming 1-hour slots
                String timeSlot = day + " " + hour + ":00";
                schedule.put(timeSlot, new ArrayList<>());
            }
        }
    }



    private boolean scheduleCourse(Course course) {
        if (courseIsAlreadyScheduled(course)) {
            return true; // Skip already scheduled courses
        }

        for (String timeSlot : schedule.keySet()) {
            if (isSlotAvailable(timeSlot, course)) {
                schedule.get(timeSlot).add(course.getCourseId());
                assignClassroomToCourse(timeSlot, course);
                return true;
            }
        }
        return false;
    }

    private boolean courseIsAlreadyScheduled(Course course) {
        return schedule.values().stream()
                .anyMatch(courses -> courses.contains(course.getCourseId()));
    }

    private void assignClassroomToCourse(String timeSlot, Course course) {
        // Calculate the number of students in the course from the studentCourseMap
        List<String> studentsInCourse = studentCourseMap.get(course.getStudentId());
        int classSize = (studentsInCourse != null) ? studentsInCourse.size() : 0;

        for (Classroom classroom : classrooms) {
            if (classroom.getCapacity() >= classSize * 2) {
                // Check if this classroom is already used in this time slot
                if (!isClassroomUsed(timeSlot, classroom.getRoomId())) {
                    // Assign this classroom to the course
                    courseClassroomMap.put(course.getCourseId(), classroom.getRoomId());
                    return;
                }
            }
        }
    }


    private boolean isSlotAvailable(String timeSlot, Course course) {
        // Check if any student in the course has an exam at this time slot
        List<String> studentsInCourse = studentCourseMap.get(course.getStudentId());
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

    private boolean isProfessorBusy(String timeSlot, String professor) {
        for (Map.Entry<String, String> entry : professorCourseMap.entrySet()) {
            if (entry.getValue().equals(professor) && schedule.get(timeSlot).contains(entry.getKey())) {
                return true; // Professor has another exam at this time slot
            }
        }
        return false;
    }

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

    private boolean isClassroomUsed(String timeSlot, String roomId) {
        // Check if the classroom is already scheduled for an exam in the given time slot
        for (String courseId : schedule.get(timeSlot)) {
            // Assuming there is a method to find the classroom assigned to a course
            String assignedClassroomId = findClassroomIdForCourse(courseId);
            if (assignedClassroomId != null && assignedClassroomId.equals(roomId)) {
                return true; // Classroom is already used in this time slot
            }
        }
        return false;
    }

    private String findClassroomIdForCourse(String courseId) {
        return courseClassroomMap.getOrDefault(courseId, null);
    }


}
