// Course class
public class Course {
    private String studentId;
    private String professorName;
    private String courseId;
    private int examDuration;
    //Constructor of class
    public Course(String studentId, String professorName, String courseId, int examDuration) {
        this.studentId = studentId;
        this.professorName = professorName;
        this.courseId = courseId;
        this.examDuration = examDuration;
    }
    //Getters
    public String getStudentId() {
        return studentId;
    }

    public String getProfessorName() {
        return professorName;
    }

    public String getCourseId() {
        return courseId;
    }

    public int getExamDuration() {
        return examDuration;
    }

    @Override
    public String toString() {
        return "StudentID: " + studentId +
                ", ProfessorName: " + professorName +
                ", CourseID: " + courseId +
                ", ExamDuration: " + examDuration + " mins";
    }


}
