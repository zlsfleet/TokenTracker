import java.sql.Timestamp;

class Test {
    public static void main(String args[]) {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        System.out.println(time.toString());
    }
}
