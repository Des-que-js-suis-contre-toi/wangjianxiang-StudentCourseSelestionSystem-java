package course.service;

import course.model.Course;
import course.model.Student;
import course.model.TimeSlot;

import java.util.*;


public class CourseService {

    private final List<Course> courses;
    private final Map<String, Student> students;

    public CourseService(List<Course> courses, Map<String, Student> students) {
        this.courses = courses;
        this.students = students;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public Collection<Student> getStudents() {
        return students.values();
    }

    public boolean containsStudent(String studentId) {
        return students.containsKey(studentId);
    }

    public Student student(String studentId) {
        return students.get(studentId);
    }

    /** 大小写不敏感地查课程，找不到返回 null。 */
    public Course findCourse(String rawCourseId) {
        String id = Course.normalizeId(rawCourseId);
        for (Course course : courses) {
            if (course.getId().equals(id)) {
                return course;
            }
        }
        return null;
    }

        //先按学分降序，再按课程编号升序
    public List<Course> sortedCourses() {
        return courses.stream()
                .sorted(Comparator.comparingDouble(Course::getCredit).reversed()
                        .thenComparing(Course::getId))
                .toList();
    }


    public void enroll(Student student, String rawCourseId) throws EnrollmentException {
        Course course = findCourse(rawCourseId);

        if (course == null) {
            throw new EnrollmentException("课程编号不存在: " + rawCourseId);
        }
        if(course.isFull()){
            throw new EnrollmentException("课程已满: " + course.getName());
        }
        if(student.hasCourse(course.getId())) {
            throw new EnrollmentException("本人已选过该课: " + course.getName());
        }
        for(String c : student.getCourseIds()){
            Course selected = findCourse(c);
            if(selected == null){
                continue;
            }
            if (course.getTimeSlot().conflictsWith(selected.getTimeSlot())) {
                throw new EnrollmentException("时间冲突：与 " + selected.getId() + " "
                        + selected.getName() + "（" + selected.getTimeSlot().getText() + "）冲突");
            }
        }
        // 通过了所有校验，开始修改数据
        course.enrollOne();
        student.addCourse(course.getId());
    }


    public void drop(Student student, String rawCourseId) throws EnrollmentException {
        Course course = findCourse(rawCourseId);
        if (course == null) {
            throw new EnrollmentException("课程编号不存在: " + rawCourseId);
        }
        if (!student.hasCourse(course.getId())) {
            throw new EnrollmentException("你未选该课程: " + rawCourseId);
        }
        course.dropOne();
        student.removeCourse(course.getId());

    }


    public List<Course> timetable(Student student) {
//        List<Course> selected = student.getCourseIds().stream()
//                .map(this::findCourse)
//                .filter(course -> course != null)
//                .toList();
//        return selected.stream()
//                .sorted(Comparator.comparingInt((Course c) -> c.getTimeSlot().getDay())
//                        .thenComparingInt(c -> c.getTimeSlot().getStartPeriod()))
//                .toList();

        List<Course> select = new ArrayList<>();
        for (String courseId : student.getCourseIds()){
            Course course = findCourse(courseId);
            if (course != null) {
                select.add(course);
            }
        }select.sort(Comparator.comparingInt((Course c) -> c.getTimeSlot().getDay())
                .thenComparingInt(c -> c.getTimeSlot().getStartPeriod()));
        return select;
    }


    public void rebuildEnrollment() {
        for (Course course : courses) {
            course.resetEnrolled();
        }
        for (Student student : students.values()){
            List<String> invalid = new ArrayList<>();
            for(String courseId : student.getCourseIds()){
                Course course = findCourse(courseId);
                if (course == null) {
                    System.out.println("[警告]学生 " + student.getId() + " 选了不存在的课程 " + courseId);
                    invalid.add(courseId);
                    continue;
                }
                if(course.isFull()){
                    System.out.println("[警告]学生 " + student.getId() + " 选了已满的课程 " + courseId);
                    invalid.add(courseId);
                    continue;
                }
                course.enrollOne();
            } invalid.forEach(student::removeCourse);
        }

    }
}

