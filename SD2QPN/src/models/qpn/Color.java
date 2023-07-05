package models.qpn;

public class Color {
    String id;
    String name;
    String realColor;

    public Color(String id, String name, String realColor) {
        this.id = id;
        this.name = name;
        this.realColor = realColor;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealColor() {
        return realColor;
    }

    public void setRealColor(String realColor) {
        this.realColor = realColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Color color = (Color) o;

        if (id != null ? !id.equals(color.id) : color.id != null) return false;
        if (name != null ? !name.equals(color.name) : color.name != null) return false;
        return realColor != null ? realColor.equals(color.realColor) : color.realColor == null;
    }
}
