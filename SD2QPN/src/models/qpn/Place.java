package models.qpn;

import java.util.ArrayList;

public class Place {
    String id;
    String name;
    int x,y;                    //Tọa độ của place
    PlaceType type;
    Queue queue;
    ArrayList<ColorRef> colorRefs = new ArrayList<>();
    Subnet subnet;

    public Place(String id, String name, int x, int y, Queue queue) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.queue = queue;
        this.type = PlaceType.QPLACE;
    }

    public Place(String id, String name, int x, int y, PlaceType type) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.type = type;
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

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public int getx()
    {
        return this.x;
    }

    public String getX() {
        return String.valueOf(this.x);
    }

    public void setX(int x) {
        this.x = x;
    }

    public int gety() {
        return this.y;
    }
    public String getY() {
        return String.valueOf(this.y);
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getType() {
        return type.getValue();
    }

    public int getTypeId()
    {
        return this.type.getId();
    }

    public void setType(PlaceType type)
    {
        this.type = type;
    }

    public ArrayList<ColorRef> getColorRefs() {
        return this.colorRefs;
    }

    public void setColorRefs(ArrayList<ColorRef> colorRefs) {
        this.colorRefs = colorRefs;
    }

    public void addColorRef(ColorRef colorRef) {
        this.colorRefs.add(colorRef);
    }

    public void addColorRef(ArrayList<ColorRef> colorRefs) {
        for(ColorRef colorRef : colorRefs)
            this.addColorRef(colorRef);
    }

    public void removeColorRef(ColorRef color)
    {
        this.colorRefs.remove(color);
    }

    public Subnet getSubnet() {
        return subnet;
    }

    public void setSubnet(Subnet subnet) {
        this.subnet = subnet;
    }

    public boolean contains(Object o)
    {
        if(o instanceof ColorRef)
            return colorRefs.contains((ColorRef)o);
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        if (x != place.x) return false;
        if (y != place.y) return false;
        if (!id.equals(place.id)) return false;
        if (!name.equals(place.name)) return false;
        if (type != place.type) return false;
        if (!queue.equals(place.queue)) return false;
        if (!colorRefs.equals(place.colorRefs)) return false;
        return subnet.equals(place.subnet);
    }
}
