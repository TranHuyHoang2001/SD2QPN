package models.qpn;

import java.util.ArrayList;

public class Subnet {
    private ArrayList<Color> colors;
    private ArrayList<Place> places;
    private ArrayList<Transition> transitions;
    private ArrayList<Connection> connections;
    private Place input;
    private Place output;

    public Subnet(ArrayList<Color> colors, ArrayList<Place> places, ArrayList<Transition> transitions, ArrayList<Connection> connections) {
        this.colors = colors;
        this.places = places;
        this.transitions = transitions;
        this.connections = connections;
    }

    public Subnet() {
        this.colors = new ArrayList<>();
        this.places = new ArrayList<>();
    }

    public ArrayList<Color> getColors() {
        return colors;
    }

    public void addColor(Color color)
    {
        this.colors.add(color);
    }

    public void addColors(ArrayList<Color> colors)
    {
        for(Color c: colors)
            this.addColor(c);
    }

    public void setColors(ArrayList<Color> colors) {
        this.colors = colors;
    }

    public ArrayList<Place> getPlaces() {
        return places;
    }

    public void addPlace(Place place)
    {
        this.places.add(place);
    }

    public void addPlaces(ArrayList<Place> places)
    {
        for (Place p : places)
            this.addPlace(p);
    }

    public void setPlaces(ArrayList<Place> places) {
        this.places = places;
    }

    public ArrayList<Transition> getTransitions() {
        return transitions;
    }

    public void addTransition(Transition transition)
    {
        this.transitions.add(transition);
    }

    public void addTransitions(ArrayList<Transition> transitions)
    {
        for (Transition t : transitions)
            this.addTransition(t);
    }

    public void setTransitions(ArrayList<Transition> transitions) {
        this.transitions = transitions;
    }

    public ArrayList<Connection> getConnections() {
        return connections;
    }

    public void addConnection(Connection connection)
    {
        this.connections.add(connection);
    }

    public void addConnections(ArrayList<Connection> connections)
    {
        for (Connection c : connections)
            this.addConnection(c);
    }

    public void setConnections(ArrayList<Connection> connections) {
        this.connections = connections;
    }

    public Place getInput() {
        return input;
    }

    public void setInput(Place input) {
        this.input = input;
    }

    public Place getOutput() {
        return output;
    }

    public void setOutput(Place output) {
        this.output = output;
    }

    public boolean contains(Object o)
    {
        if(o instanceof Color)
            return colors.contains((Color)o);
        else if(o instanceof Place)
            return places.contains((Place)o);
        else if(o instanceof Transition)
            return transitions.contains((Transition)o);
        else if(o instanceof Connection)
            return connections.contains((Connection)o);
        else
            return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subnet subnet = (Subnet) o;

        if (!colors.equals(subnet.colors)) return false;
        if (!places.equals(subnet.places)) return false;
        if (!transitions.equals(subnet.transitions)) return false;
        return connections.equals(subnet.connections);
    }
}
