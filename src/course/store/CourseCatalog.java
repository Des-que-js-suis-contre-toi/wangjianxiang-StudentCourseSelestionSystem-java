package course.store;


import course.model.Course;

import java.util.ArrayList;
import java.util.List;

public final class CourseCatalog {



    private CourseCatalog() {
    }

    public static List<Course> presetCourses() {
        List<Course> courses = new ArrayList<>();
        courses.add(new Course("CS101", "程序设计基础", 4.0, "周一第1-2节", 60, 0));
        courses.add(new Course("CS102", "面向对象程序设计", 3.5, "周二第3-4节", 55, 0));
        courses.add(new Course("CS201", "数据结构", 4.0, "周一第3-4节", 50, 0));
        courses.add(new Course("CS202", "算法设计与分析", 3.0, "周三第1-2节", 45, 0));
        courses.add(new Course("CS301", "操作系统", 3.5, "周二第5-6节", 40, 0));
        courses.add(new Course("CS302", "计算机网络", 3.0, "周四第3-4节", 45, 0));
        courses.add(new Course("CS303", "数据库原理", 3.0, "周三第5-6节", 50, 0));
        courses.add(new Course("CS304", "编译原理", 3.5, "周五第1-2节", 30, 0));
        courses.add(new Course("CS305", "软件工程", 2.5, "周四第5-6节", 55, 0));
        courses.add(new Course("MA101", "高等数学", 4.0, "周一第5-6节", 60, 0));
        courses.add(new Course("MA102", "线性代数", 2.5, "周二第1-2节", 55, 0));
        courses.add(new Course("MA201", "概率论与数理统计", 3.0, "周五第3-4节", 50, 0));
        courses.add(new Course("EN101", "大学英语", 2.0, "周三第1-2节", 60, 0));
        courses.add(new Course("EN201", "学术英语写作", 1.5, "周四第7-8节", 35, 0));
        courses.add(new Course("PE101", "体育", 1.0, "周五第5-6节", 60, 0));
        courses.add(new Course("HU101", "中国近现代史纲要", 2.0, "周一第7-8节", 60, 0));
        courses.add(new Course("HU102", "思想道德与法治", 2.0, "周二第7-8节", 60, 0));
        courses.add(new Course("SC101", "大学物理", 3.5, "周三第7-8节", 40, 0));
        courses.add(new Course("SC102", "物理实验", 1.5, "周六第1-2节", 30, 0));
        courses.add(new Course("AR101", "艺术鉴赏", 1.0, "周日第3-4节", 50, 0));
        return courses;
    }
}
