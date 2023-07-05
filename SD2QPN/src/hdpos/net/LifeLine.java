/*
 * Class LifeLine
 */
package hdpos.net;

public class LifeLine {
    private String id;
    private String name;
    private String color;
    private String[] coveredBys;
    private String[] names;

    public LifeLine(String id, String name, String color, String[] coveredBys, String[] names) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.coveredBys = coveredBys;
        this.names = names;
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

    public String[] getCoveredBys() {
        return coveredBys;
    }

    public void setCoveredBys(String[] coveredBys) {
        this.coveredBys = coveredBys;
    }

    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }
}
