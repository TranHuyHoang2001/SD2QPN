package models.qpn;

public enum PlaceType {

    OPLACE(1,"ordinary-place"),
    QPLACE(2,"queueing-place"),
    SPLACE(3,"subnet-place");
    private int id;
    private String value;

    private PlaceType(int id, String value) {
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
