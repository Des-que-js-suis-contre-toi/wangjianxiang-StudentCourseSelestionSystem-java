package course.model;

import java.util.Locale;
import java.util.Objects;

public class Course {
    private final String id;
    private final String name;
    private final double credit;
    private final TimeSlot timeSlot;
    private final int capacity;
    private int enrolled;
    public Course(String id, String name, double credit, String timeText, int capacity, int enrolled) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("课程编号不能为空");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("课程名称不能为空");
        }
        if (credit < 1.0 || credit > 4.0) {
            throw new IllegalArgumentException("学分必须在 1.0—4.0 之间：" + credit);
        }
        if (credit * 2 != Math.round(credit * 2)) { 
            throw new IllegalArgumentException("学分必须以 0.5 为步进：" + credit);
}
        if (capacity < 30 || capacity > 60) {
            throw new IllegalArgumentException("容量必须在 30—60之间：" + capacity);
        }
        if (enrolled < 0 || enrolled > capacity) {
            throw new IllegalArgumentException("已选人数必须在 0—容量之间：" + enrolled);
        }
        this.id = normalizeId(id);
        this.name = name.trim();
        this.credit = credit;
        this.timeSlot = TimeSlot.parse(timeText);
        this.capacity = capacity;
        this.enrolled = enrolled;
    }
    public static String normalizeId(String raw) {
        return raw==null?"":raw.trim().toUpperCase(Locale.ROOT);
    }



    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getCredit() {
        return credit;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getEnrolled() {
        return enrolled;
    }

    public boolean isFull() {
        return enrolled >= capacity;
    }
    public int getRemaining() {
        return capacity - enrolled;
    }
    //选课成功后
    public void enrollOne(){
        if(isFull()) throw new IllegalStateException(id+"课程已满");
        enrolled++;
    }
    public void dropOne() {
        if (enrolled <= 0) {
            throw new IllegalStateException("课程已选人数为 0，无法再减：" + id);
        }
        enrolled--;
    }

    //重算
    public void resetEnrolled(){
        enrolled=0;
    }

    //方便输入
    public String toDataLine(){
        return String.join(",", id, name, String.valueOf(credit), timeSlot.getText(), String.valueOf(capacity), String.valueOf(enrolled));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof Course other && id.equals(other.id);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + " " + name + " " + credit + " 学分 " + timeSlot
                + " 容量 " + enrolled + "/" + capacity;
    }

}
