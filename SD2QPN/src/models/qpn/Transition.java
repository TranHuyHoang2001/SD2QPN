package models.qpn;

import java.util.ArrayList;

public class Transition {
    private String id;
    private String name;
    private int x;
    private int y;
    private int priority = 0;
    private float weight = 1.0f;
    private ArrayList<Mode> modes;
    private ArrayList<Connection> connections;

    public Transition(String id, String name, int x, int y) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.modes = new ArrayList<>();
        this.connections = new ArrayList<>();
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

    public String getX() {
        return String.valueOf(x);
    }

    public int getx()
    {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public String getY() {
        return String.valueOf(y);
    }

    public int gety()
    {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getPriority() {
        return String.valueOf(priority);
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getWeight() {
        return String.valueOf(weight);
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public ArrayList<Mode> getModes() {
        return modes;
    }

    public void addMode(Mode mode)
    {
        this.modes.add(mode);
    }

    public void removeMode(Mode mode)
    {
        this.modes.remove(mode);
    }

    public ArrayList<Connection> getConnections() {
        return connections;
    }

    public void addConnection(Connection connection)
    {
        this.connections.add(connection);
    }

    public void removeConnection(Connection connection)
    {
        this.connections.remove(connection);
    }

    public boolean contains(Object o)
    {
        if(o instanceof Mode)
            return modes.contains((Mode)o);
        else if(o instanceof Connection)
            return connections.contains((Connection)o);
        else
            return false;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transition that = (Transition) o;

        if (x != that.x) return false;
        if (y != that.y) return false;
        if (priority != that.priority) return false;
        if (Float.compare(that.weight, weight) != 0) return false;
        if (!id.equals(that.id)) return false;
        if (!name.equals(that.name)) return false;
        if (!modes.equals(that.modes)) return false;
        return connections.equals(that.connections);
    }
}