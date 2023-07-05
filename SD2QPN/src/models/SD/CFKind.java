package models.SD;

public enum CFKind {
    ALTERNATIVE(1,"alt"),
    OPTION(2,"opt"),
    LOOP(3,"loop"),
    BREAK(4,"break"),
    PARALLEL(5, "par"),
    STRICT(6,"strict"),
    ASSERTION(7,"assert"),
    CONSIDER(8,"consider"),
    CRITICAL(9,"critical"),
    IGNORE(10,"ignore"),
    NEGATIVE(11,"neg"),
    SEQUENCE(12,"seq");


    private int id;
    private String value;
    private CFKind(int id, String value)
    {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
