package models.qpn;

public class Mode {
    private String id;
    private String name;
    private String color;
    private float weight = 1f;

    public Mode(String id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getWeight() {
        return String.valueOf(weight);
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mode mode = (Mode) o;

        if (Float.compare(mode.weight, weight) != 0) return false;
        if (!id.equals(mode.id)) return false;
        if (!name.equals(mode.name)) return false;
        return color.equals(mode.color);
    }

}
