/**
 * The {@code Course} class represents a course with information about the student, professor,
 * course ID, and exam duration.
 */
public class Course {

    // Fields

    /**
     * The unique identifier for the student enrolled in the course.
     */
    private String studentId;

    /**
     * The name of the professor teaching the course.
     */
    private String professorName;

    /**
     * The unique identifier for the course.
     */
    private String courseId;

    /**
     * The duration of the exam for the course, measured in minutes.
     */
    private int examDuration;

    // Constructor

    /**
     * Constructs a new {@code Course} with the specified student ID, professor name,
     * course ID, and exam duration.
     *
     * @param studentId      The unique identifier for the student enrolled in the course.
     * @param professorName  The name of the professor teaching the course.
     * @param courseId       The unique identifier for the course.
     * @param examDuration   The duration of the exam for the course, measured in minutes.
     */
    public Course(String studentId, String professorName, String courseId, int examDuration) {
        this.studentId = studentId;
        this.professorName = professorName;
        this.courseId = courseId;
        this.examDuration = examDuration;
    }

    // Getters

    /**
     * Retrieves the student ID enrolled in the course.
     *
     * @return The unique identifier for the student enrolled in the course.
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * Retrieves the name of the professor teaching the course.
     *
     * @return The name of the professor teaching the course.
     */
    public String getProfessorName() {
        return professorName;
    }

    /**
     * Retrieves the course ID.
     *
     * @return The unique identifier for the course.
     */
    public String getCourseId() {
        return courseId;
    }

    /**
     * Retrieves the exam duration for the course.
     *
     * @return The duration of the exam for the course, measured in minutes.
     */
    public int getExamDuration() {
        return examDuration;
    }

    // Override Methods

    /**
     * Returns a string representation of the {@code Course} object.
     *
     * @return A string containing the student ID, professor name, course ID, and exam duration.
     */
    @Override
    public String toString() {
        return "StudentID: " + studentId +
                ", ProfessorName: " + professorName +
                ", CourseID: " + courseId +
                ", ExamDuration: " + examDuration + " mins";
    }

    /**
     * Retrieves the course ID. This is an additional method for consistency.
     *
     * @return The unique identifier for the course.
     */
    public String getCourseID() {
        return courseId;
    }
}
