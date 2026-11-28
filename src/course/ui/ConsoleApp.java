package course.ui;

import course.model.Course;
import course.model.Student;
import course.service.CourseService;
import course.service.EnrollmentException;

import java.util.List;
import java.util.Scanner;


public class ConsoleApp {

    private final CourseService service;
    private final Scanner scanner;

    public ConsoleApp(CourseService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    public void run() {
        System.out.println("================ 学生选课系统 ================");
        Student current = login();
        if (current == null) {
            System.out.println("已退出登录。");
            return;
        }
        mainLoop(current);
        System.out.println("感谢使用，再见。");
    }

    /** 登录：学号必须是 2024001—2024050 之一；回车退出。 */
    private Student login() {
        while (true) {
            String input = readLine("请输入学号（直接回车退出）：");
            if (input == null || input.isEmpty()) {
                return null;
            }
            if (!input.matches("\\d{7}")) {
                System.out.println("学号应为 7 位数字，请重试。");
                continue;
            }
            if (!service.containsStudent(input)) {
                System.out.println("学号不存在（有效范围 2024001—2024050），请重试。");
                continue;
            }
            System.out.println("登录成功，欢迎同学 " + input + "！");
            return service.student(input);
        }
    }

    private void mainLoop(Student student) {
        while (true) {
            printMenu();
            String choice = readLine("请选择：");
            if (choice == null) {
                System.out.println();
                System.out.println("输入已结束，自动退出。");
                return;
            }

                switch (choice) {
                    case "1" -> showCourses();
                    case "2" -> enroll(student);
                    case "3" -> drop(student);
                    case "4" -> showTimetable(student);
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("无效选项，请输入 0—4。");
                }

        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("---------------- 功能菜单 ----------------");
        System.out.println("1. 浏览课程   2. 选课   3. 退课   4. 我的课表   0. 退出");
    }

    private void showCourses() {
        List<Course> courses = service.sortedCourses();
        if (courses.isEmpty()) {
            System.out.println("当前没有可浏览的课程。");
            return;
        }
        System.out.println();
        System.out.println(pad("编号", 8) + "  " + pad("名称", 22) + "  " + padLeft("学分", 5)
                + "  " + pad("时间", 16) + "  " + padLeft("已选/容量", 9));
        System.out.println("-----------------------------------------------------------------------");
        for (Course course : courses) {
            System.out.println(pad(course.getId(), 8) + "  " + pad(course.getName(), 22) + "  "
                    + padLeft(String.format("%.1f", course.getCredit()), 5) + "  "
                    + pad(course.getTimeSlot().getText(), 16) + "  "
                    + padLeft(course.getEnrolled() + "/" + course.getCapacity(), 9));
        }
    }

    private void enroll(Student student) {
        String input = readLine("请输入课程编号（直接回车取消）：");
        if (input == null || input.isEmpty()) {
            System.out.println("已取消选课。");
            return;
        }
        try {
            service.enroll(student, input);
            System.out.println("选课成功。");
        } catch (EnrollmentException e) {
            System.out.println("选课失败：" + e.getMessage());
        }
    }

    private void drop(Student student) {
        String input = readLine("请输入要退的课程编号（直接回车取消）：");
        if (input == null || input.isEmpty()) {
            System.out.println("已取消退课。");
            return;
        }
        try {
            service.drop(student, input);
            System.out.println("退课成功。");
        } catch (EnrollmentException e) {
            System.out.println("退课失败：" + e.getMessage());
        }
    }

    private void showTimetable(Student student) {
        List<Course> courses = service.timetable(student);
        if (courses.isEmpty()) {
            System.out.println("本学期你还没有选课。");
            return;
        }
        System.out.println();
        System.out.println("---------------- 我的课表 ----------------");
        String currentDay = null;
        double totalCredit = 0;
        for (Course course : courses) {
            String day = course.getTimeSlot().getDayName();
            if (!day.equals(currentDay)) {
                System.out.println("【" + day + "】");
                currentDay = day;
            }
            System.out.println("  " + pad(course.getId(), 8) + "  " + pad(course.getName(), 22) + "  "
                    + pad(course.getTimeSlot().getText(), 16) + "  "
                    + padLeft(String.format("%.1f", course.getCredit()), 5) + " 学分");
            totalCredit += course.getCredit();
        }
        System.out.println("------------------------------------------");
        System.out.printf("共 %d 门课程，合计 %.1f 学分%n", courses.size(), totalCredit);
    }

    /** 返回 null 表示输入流已经结束。 */
    private String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine().trim();
    }

    /*
     * 控制台对齐：中文占 2 个字符宽度，直接用 printf("%-20s") 会错位，
     * 所以按“显示宽度”补空格。这段只是排版，不影响业务。
     */
    private static String pad(String text, int width) {
        StringBuilder sb = new StringBuilder(text);
        for (int i = displayWidth(text); i < width; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    private static String padLeft(String text, int width) {
        StringBuilder sb = new StringBuilder();
        for (int i = displayWidth(text); i < width; i++) {
            sb.append(' ');
        }
        return sb.append(text).toString();
    }


    /*
    这个方法是用来计算字符串的显示宽度的，只影响排版
    之前的方法是打印的表格并不美观，选择的，其中涉及Unicode 区块判断，并且用 codePointAt 按码点遍历
    我没有了解，所以用 ascii 判断
     */
    private static int displayWidth(String text) {
        int width = 0;
        for (int i = 0; i < text.length();i++ ) {
            boolean ascii = text.charAt(i) <= 0x7F;
            width += ascii ? 1 : 2;
        }
        return width;
    }


}
