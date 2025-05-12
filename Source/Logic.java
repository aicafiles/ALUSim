package Source;

public class Logic {
    private int result;
    private String binaryResult;
    private java.beans.PropertyChangeSupport changes;
    private static final int MAX_HISTORY_ENTRIES = 10;
    private String[] recentCalculations = new String[MAX_HISTORY_ENTRIES];
    private int historyIndex = 0;

    public Logic() {
        changes = new java.beans.PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }

    private void updateResult(int newResult) {
        int oldResult = this.result;
        this.result = newResult;
        this.binaryResult = String.format("%32s", Integer.toBinaryString(newResult))
                               .replace(' ', '0')
                               .replaceAll("(.{8})", "$1 ")
                               .trim();
        changes.firePropertyChange("result", oldResult, newResult);
        changes.firePropertyChange("binaryResult", null, binaryResult);
    }

    public int add(int a, int b) {
        long result = (long) a + b;
        if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
            throw new ArithmeticException("Addition overflow");
        }
        updateResult((int) result);
        return (int) result;
    }

    public int subtract(int a, int b) {
        long result = (long) a - b;
        if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
            throw new ArithmeticException("Subtraction overflow");
        }
        updateResult((int) result);
        return (int) result;
    }

    public int multiply(int a, int b) {
        long result = (long) a * b;
        if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
            throw new ArithmeticException("Multiplication overflow");
        }
        updateResult((int) result);
        return (int) result;
    }

    public int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero");
        }
        if (a == Integer.MIN_VALUE && b == -1) {
            throw new ArithmeticException("Division overflow");
        }
        int result = a / b;
        updateResult(result);
        return result;
    }

    public int modulo(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Modulo by zero");
        }
        int result = a % b;
        updateResult(result);
        return result;
    }

    public int and(int a, int b) {
        int result = a & b;
        updateResult(result);
        return result;
    }

    public int or(int a, int b) {
        int result = a | b;
        updateResult(result);
        return result;
    }

    public int not(int a) {
        int result = ~a;
        updateResult(result);
        return result;
    }

    public int leftShift(int a, int b) {
        if (b < 0 || b >= 32) {
            throw new IllegalArgumentException("Shift amount must be between 0 and 31");
        }
        int result = a << b;
        updateResult(result);
        return result;
    }

    public int rightShift(int a, int b) {
        if (b < 0 || b >= 32) {
            throw new IllegalArgumentException("Shift amount must be between 0 and 31");
        }
        int result = a >> b;
        updateResult(result);
        return result;
    }

    public String getBinaryResult() {
        return binaryResult;
    }

    public boolean isValidInput(String input, String base) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            if ("BINARY".equals(base.toUpperCase())) {
                for (char c : input.toCharArray()) {
                    if (c != '0' && c != '1') {
                        return false;
                    }
                }
                if (input.length() <= 32) {
                    Integer.parseInt(input, 2);
                } else {
                    return false;
                }
            } else {
                Integer.parseInt(input);
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void addToHistory(String operation, int a, int b, int result, String base) {
        String entry;
        if (operation.equals("NOT")) {
            entry = formatHistoryEntry(operation, a, 0, result, base, true);
        } else {
            entry = formatHistoryEntry(operation, a, b, result, base, false);
        }
        recentCalculations[historyIndex] = entry;
        historyIndex = (historyIndex + 1) % MAX_HISTORY_ENTRIES;
        changes.firePropertyChange("historyUpdate", null, getHistory());
    }

    private String formatNumber(int number, String base) {
        if ("BINARY".equals(base.toUpperCase())) {
            return Integer.toBinaryString(number);
        }
        return Integer.toString(number);
    }

    private String formatHistoryEntry(String operation, int a, int b, int result, String base, boolean isUnary) {
        String numAStr = formatNumber(a, base);
        String resultStr = formatNumber(result, base);

        if (isUnary) {
            return String.format("%s %s = %s",
                numAStr,
                operation,
                resultStr);
        } else {
            String numBStr = formatNumber(b, base);
            return String.format("%s %s %s = %s",
                numAStr,
                operation,
                numBStr,
                resultStr);
        }
    }

    public String[] getHistory() {
        return recentCalculations;
    }
}