package models.SD;

import noNamespace.ProjectDocument.Project.Models.Frame.ModelChildren.InteractionLifeLine.Activations.Activation;

import java.util.ArrayList;

public class SDActivation implements Comparable{

    private Activation a;
    private String Id;
    private ArrayList<String> ocrs = new ArrayList<>();
    int x, y;

    public SDActivation(Activation a)
    {
        this.a = a;
        if(a.isSetId())
            this.Id = a.getId();
    }

    public SDActivation() {

    }

    public Activation getA() {
        return a;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public ArrayList<String> getOcrs() {
        return ocrs;
    }

    public void setOcrs(ArrayList<String> ocrs) {
        this.ocrs = ocrs;
    }

    public void addOcrs(String ocr)
    {
        if(!ocrs.contains(ocr))
            this.ocrs.add(ocr);
    }

    public void set(SDActivation sa) {
        this.a = sa.getA();
        this.setId(sa.getId());
        this.setOcrs(sa.getOcrs());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int compareTo(Object o)
    {
        SDActivation a = (SDActivation) o;
        return (this.x == a.getX() ? this.y - a.getY() : this.x - a.getX());
    }

    @Override
    public String toString() {
        return "SDActivation{" +
                ", Id='" + Id + '\'' +
                ", ocrs=" + ocrs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SDActivation that = (SDActivation) o;

        return Id.equals(that.Id);
    }

}
