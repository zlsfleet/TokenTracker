import java.sql.Timestamp;

public class Test {
    public static void main(String args[]) {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        System.out.println(time.toString());
    }
}
