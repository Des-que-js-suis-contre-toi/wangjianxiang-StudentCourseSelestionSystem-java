package course.store;

import course.model.Course;
import course.model.Student;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DataStore {
    private final Path coursesFile;
    private final Path studentsFile;

    public DataStore(Path dataDir) {
        this.coursesFile = dataDir.resolve("courses.txt");
        this.studentsFile = dataDir.resolve("students.txt");
    }

    public Path getCoursesFile() {
        return coursesFile;
    }

    public Path getStudentsFile() {
        return studentsFile;
    }
    public List<Course> loadCourses() throws IOException {
        if (!Files.exists(coursesFile)) {
            System.out.println("信息未找到" + coursesFile.toAbsolutePath() + "，将使用默认数据（20门课程）");
            return CourseCatalog.presetCourses();
        }
        List<Course> courses = new ArrayList<>();
        List<String> lines = Files.readAllLines(coursesFile, StandardCharsets.UTF_8);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).replace("\uFEFF", "").trim();
            if (line.isEmpty() || line.startsWith("#"))
                continue;
            //原本是先将字段切好，再进行判断，但如果一行只有分隔符切出来空串length为0，然后取parts[0]就会报错,程序会崩溃
            String parts[] = line.split("[，,、]");
            if (parts.length != 6) {
                System.out.println("第" + (i + 1) + "行字段数量错误，请检查");
                continue;
            }
            String id = parts[0].trim();

            try {
                courses.add(new Course(id, parts[1].trim(),
                        Double.parseDouble(parts[2].trim()), parts[3].trim(),
                        Integer.parseInt(parts[4].trim()),
                        Integer.parseInt(parts[5].trim())));
            } catch (IllegalArgumentException e) {
                System.out.println("第" + (i + 1) + "行数据格式错误，请检查");
            }
        }
        if (courses.isEmpty()) {
            System.out.println("没有找到有效的课程数据, 将使用默认数据（20门课程）");
            return CourseCatalog.presetCourses();
        }
        return courses;
    }

    public void saveCourses(List<Course> courses) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# ID，名称，学分，时间，容量，已选人数");
        for (Course course : courses) {
            lines.add(course.toDataLine());
        }
        Files.write(coursesFile, lines, StandardCharsets.UTF_8);
    }
    public Map<String,List<String>> loadStudentSelections() throws IOException {
        Map<String,List<String>> selections = new LinkedHashMap<>();
        if(!Files.exists(studentsFile)){
            System.out.println("信息未找到" + studentsFile.toAbsolutePath() + "，将使用默认数据（50名学生）");
            return selections;}

            List<String> lines = Files.readAllLines(studentsFile, StandardCharsets.UTF_8);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).replace("\uFEFF", "").trim();
            if(line.isEmpty() || line.startsWith("#"))
                continue;
            String parts[] = line.split("[，,、]");
            //parts.length == 0是为了一行只有分隔符的情况，
            // parts.length == 1是为了一行只有一个学生ID的情况不算坏行
            if(parts.length == 0){
                System.out.println("第" + (i + 1) + "行字段数量错误，请检查");
                continue;
            }
            String studentsId = parts[0].trim();
            if(!studentsId.matches("\\d{7}")){
                System.out.println("第" + (i + 1) + "行学生ID格式错误，请检查");
                continue;
            }
            List<String> courseIds = new ArrayList<>();
            for (int j = 1; j < parts.length; j++) {
                String courseId = parts[j].trim();
                if(!courseId.isEmpty())
                    courseIds.add(Course.normalizeId(courseId));
            }
            selections.put(studentsId, courseIds);
        }
        return selections;
    }
    public void saveStudentSelections(Collection<Student> students) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# 学号，课程编号1，课程编号2，...");
        //这是使用流式操作的代码
//        students.stream().
//                sorted((a,b)->a.getId().compareTo(b.getId())).
//                forEach(student->lines.add(student.getId()+
//                        (student.getCourseIds().isEmpty()?""
//                                :","+String.join(",", student.getCourseIds()))));
        //这是使用集合排序的代码，都是先排序，再遍历添加
        List<Student> ordered = new ArrayList<>(students);
        //按学生的ID升序排序
        ordered.sort(Comparator.comparing(Student::getId));
        for (Student student : ordered) {
            StringBuilder line = new StringBuilder(student.getId());
            for (String courseId : student.getCourseIds()) {
//                student.getCourseIds()在方法中用了Collections.unmodifiableSet但这里没有通过它对集合进行修改
//                ，只是将它加工成字符串，不会抛出UnsupportedOperationException
                line.append(',').append(courseId);
            }
            lines.add(line.toString());
        }

        Files.write(studentsFile, lines, StandardCharsets.UTF_8);


    }









}
