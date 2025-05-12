package Source;

public class Logic {
    public Logic() {
        // Empty constructor for now
    }

    public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }

    public int and(int a, int b) {
        return a & b;
    }

    public int or(int a, int b) {
        return a | b;
    }

    public int not(int a) {
        return ~a;
    }
}