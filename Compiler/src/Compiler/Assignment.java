package Compiler;

public class Assignment {
    private String symbol;
    private String value;
    private String operations;
    private int lineNumber;

    public Assignment(String symbol, String value, String operations, int lineNumber) {
        this.symbol = symbol;
        this.value = value;
        this.operations = operations;
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getValue() {
        return value;
    }

    public String getOperations() {
        return operations;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "symbol='" + symbol + '\'' +
                ", value='" + value + '\'' +
                ", fieldType='" + operations + '\'' +
                '}';
    }
}
