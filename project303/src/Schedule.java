import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Schedule{

    private Map<Integer, Map<Integer, List<Exam>>> schedule;

    public Schedule() {
        this.schedule = new HashMap<>();
    }

    public boolean scheduleExam(int day, int hour, Course course, Classroom classroom) {
        if (!hasStudentExam(day, hour, course) && !hasProfessorExam(day, hour, course)) {
            // if (classroom.getCapacity() >= 2 * studentsPerCourse.get(course.getCourseId())) {
            schedule.computeIfAbsent(day, k -> new HashMap<>());//ensure there is a mapping, if is not compose new hashmap and associated with day
            schedule.get(day).computeIfAbsent(hour, k -> new ArrayList<>());
            schedule.get(day).get(hour).add(new Exam(course, classroom));
            return true;
        }
        //}
        return false;
    }

    private boolean hasStudentExam(int day, int hour, Course course) {
        Map<Integer, List<Exam>> hourSchedule = schedule.get(day);
        if (hourSchedule != null) {
            List<Exam> exams = hourSchedule.get(hour);
            if (exams != null) {
                for (Exam exam : exams) {
                    if (exam.getCourse().getStudentId().equals(course.getStudentId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasProfessorExam(int day, int hour, Course course) {
        Map<Integer, List<Exam>> hourSchedule = schedule.get(day);
        if (hourSchedule != null) {
            List<Exam> exams = hourSchedule.get(hour);
            if (exams != null) {
                for (Exam exam : exams) {
                    if (exam.getCourse().getProfessorName().equals(course.getProfessorName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public void unscheduleExam(int day, int hour, Course course, Classroom classroom) {
        if (schedule.containsKey(day) && schedule.get(day).containsKey(hour)) {
            schedule.get(day).get(hour).removeIf(exam -> exam.getCourse().equals(course)
                    && exam.getClassroom().equals(classroom));
        }
    }

    public boolean isExamScheduled(Course course) {
        for (Map<Integer, List<Exam>> daySchedule : schedule.values()) {
            for (List<Exam> exams : daySchedule.values()) {
                for (Exam exam : exams) {
                    if (exam.getCourse().equals(course)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isComplete(int totalExams) {
        int scheduledExams = 0;
        for (Map<Integer, List<Exam>> daySchedule : schedule.values()) {
            for (List<Exam> exams : daySchedule.values()) {
                scheduledExams += exams.size();
            }
        }
        return scheduledExams == totalExams;
    }
    public Map<Integer, List<Exam>> getSchedule(int day) {
        return this.schedule.get(day);
    }

}

class Exam {
    private Course course;
    private Classroom classroom;
    private Map<Integer, Map<Integer, List<Exam>>> schedule;

    public Exam(Course course, Classroom classroom) {
        this.course = course;
        this.classroom = classroom;
    }

    public Course getCourse() {
        return course;
    }

    public Classroom getClassroom() {
        return classroom;
    }


}