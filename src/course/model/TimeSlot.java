package course.model;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeSlot {
    private static final Pattern PATTERN = Pattern.compile("^\\s*周\\s*([一二三四五六日天1-7])\\s*第\\s*(\\d{1,2})\\s*[-—–~至]\\s*(\\d{1,2})\\s*节\\s*$");
    private static final String[] DAY_NAMES = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private final int day;
    private final int startPeriod;
    private final int endPeriod;
    private final String text;

    private TimeSlot(int day, int startPeriod, int endPeriod, String text) {
        this.day = day;
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
        this.text = text;
    }

    public static TimeSlot parse(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("上课时间不能为空 ");
        }
        Matcher matcher = PATTERN.matcher(raw);
        if (!matcher.matches())
            throw new IllegalArgumentException("时间的格式应为：周X第Y-Y节");
        int day = parseDay(matcher.group(1), raw);
        int start = Integer.parseInt(matcher.group(2));
        int end = Integer.parseInt(matcher.group(3));
        if (start < 1 || start > 8 || end < 1 || end > 8) {
            throw new IllegalArgumentException("节次必须在1到8之间");
        }
        if (start > end)
            throw new IllegalArgumentException("起始节次不能大于结束节次");
        return new TimeSlot(day, start, end, DAY_NAMES[day] + "第" + start + "-" + end + "节");
    }


    private static int parseDay(String token, String raw) {
        return switch (token) {
            case "一", "1" -> 1;
            case "二", "2" -> 2;
            case "三", "3" -> 3;
            case "四", "4" -> 4;
            case "五", "5" -> 5;
            case "六", "6" -> 6;
            case "日", "天", "7" -> 7;
            default -> throw new IllegalArgumentException("时间的格式应为：周X第Y-Y节");
        };
    }
    public boolean conflictsWith(TimeSlot other) {
        if(other==null){
            return false;
        }
        // 这段的代码，可以类比数轴，判断两个线段是否有交集
        return day == other.day && startPeriod <= other.endPeriod && endPeriod >= other.startPeriod;
    }


    public int getDay() {
        return day;
    }

    public int getStartPeriod() {
        return startPeriod;
    }

    public int getEndPeriod() {
        return endPeriod;
    }

    /** 规范化文本，用于展示与持久化。 */
    public String getText() {
        return text;
    }

    /** 「周一」这样的中文星期名，课表分组时使用。 */
    public String getDayName() {
        return DAY_NAMES[day];
    }

    @Override
    public String toString() {
        return text;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSlot other))
                return false;
            return day == other.day
                    && startPeriod == other.startPeriod
                    && endPeriod == other.endPeriod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, startPeriod, endPeriod);
    }
}
