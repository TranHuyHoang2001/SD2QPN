package models.qpn;

public class ColorRef {
    private String id;
    private String ColorId;
    int init = 0;
    float lambda = 1f;

    public ColorRef(String id, String colorId) {
        this.id = id;
        ColorId = colorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColorId() {
        return ColorId;
    }

    public void setColorId(String colorId) {
        ColorId = colorId;
    }

    public String getInit() {
        return String.valueOf(this.init);
    }

    public void setInit(int init) {
        this.init = init;
    }

    public String getLambda() {
        return String.valueOf(this.lambda);
    }

    public void setLambda(float lambda) {
        this.lambda = lambda;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColorRef colorRef = (ColorRef) o;

        if (init != colorRef.init) return false;
        if (Float.compare(colorRef.lambda, lambda) != 0) return false;
        if (!id.equals(colorRef.id)) return false;
        return ColorId.equals(colorRef.ColorId);
    }
}
