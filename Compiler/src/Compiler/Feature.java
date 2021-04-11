package Compiler;

import java.util.Objects;
import java.util.Optional;

public class Feature implements Comparable<Feature> {
    private String symbol;
    private ScopeType type;
    private String properties;
    private int lineNumber;
    private String fieldType;
    private Feature classParent;


    public Feature(String symbol, ScopeType type, String properties, int lineNumber) {
        this.symbol = symbol;
        this.type = type;
        this.properties = properties;
        this.lineNumber = lineNumber;
    }

    public Feature getClassParent() {
        return classParent;
    }

    public void setClassParent(Feature classParent) {
        this.classParent = classParent;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(Optional<String> fieldType) {
        this.fieldType = fieldType.orElse("");
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public ScopeType getType() {
        return type;
    }

    public void setType(ScopeType type) {
        this.type = type;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feature feature = (Feature) o;
        return Objects.equals(symbol, feature.symbol) && type == feature.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, type, properties);
    }

    @Override
    public String toString() {
        return "Feature{"
                + "symbol='"
                + symbol
                + '\''
                + ", type="
                + type
                + ", properties='"
                + properties
                + '\''
                + '}';
    }

    @Override
    public int compareTo(Feature o) {
        return this.symbol.compareTo(o.symbol);
    }
}
