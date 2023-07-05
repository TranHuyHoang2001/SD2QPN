package models.qpn;

import Utilities.RandomColor;

import java.util.ArrayList;

public class QPNDocument {
    private ArrayList<Color> colors = new ArrayList<>();
    private ArrayList<Queue> queues = new ArrayList<>();
    private ArrayList<Place> places = new ArrayList<>();
    private ArrayList<Transition> transitions = new ArrayList<>();
    private ArrayList<Connection> connections = new ArrayList<>();

    public QPNDocument(ArrayList<Color> colors, ArrayList<Queue> queues, ArrayList<Place> places, ArrayList<Transition> transitions, ArrayList<Connection> connections) {
        this.colors = colors;
        this.queues = queues;
        this.places = places;
        this.transitions = transitions;
        this.connections = connections;
    }

    public QPNDocument() {

    }

    public ArrayList<Color> getColors() {
        return colors;
    }

    public void setColors(ArrayList<Color> colors) {
        this.colors = colors;
    }

    public ArrayList<Queue> getQueues() {
        return queues;
    }

    public Queue getQueueById(String id)
    {
        for(Queue q : queues)
            if(q.getId().equals(id))
                return q;
        return null;
    }

    public void setQueues(ArrayList<Queue> queues) {
        this.queues = queues;
    }

    public ArrayList<Place> getPlaces() {
        return places;
    }

    public void setPlaces(ArrayList<Place> places) {
        this.places = places;
    }

    public Place getPlaceById(String id) {
        for (Place p : places) {
            if (p.getId().equals(id))
                return p;
        }
        return null;
    }

    public ArrayList<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(ArrayList<Transition> transitions) {
        this.transitions = transitions;
    }

    public Transition getTransitionById(String id) {
        for (Transition t : transitions) {
            if (t.getId().equals(id))
                return t;
        }
        return null;
    }

    public ArrayList<Connection> getConnections() {
        return connections;
    }

    public Connection getConnectionById(String id)
    {
        for(Connection c : this.connections)
            if(c.getId().equals(id))
                return c;
        return null;
    }

    public void setConnections(ArrayList<Connection> connections) {
        this.connections = connections;
    }

    public void addColor(Color color) {
        if(!colors.contains(color))
        {
            boolean check = false;
            int num = 1;
            String name = color.getName();
            do {
                check = false;
                for (Color c : colors) {
                    if(c.getRealColor().equals(color.getRealColor())) {
                        color.setRealColor(RandomColor.getRandomColor());
                        check = true;
                    }

                    if (c.getName().equals(color.getName())) {
                        color.setName(name + "_" + num++);
                        check = true;
                    }

                    if(check)
                        break;
                }
            }while (check);

            colors.add(color);
        }
    }

    public void addColors(ArrayList<Color> colors)
    {
        for(Color c : colors)
            this.addColor(c);
    }

    public void removeColor(Color color) {
        this.colors.remove(color);
    }

    public void removeColor(String id)
    {
        Color color = null;
        for(Color c : this.colors)
        {
            if(c.getId().equals(id))
                color = c;
        }
        if(color != null)
            this.colors.remove(color);
    }

    public void addQueue(Queue queue) {
        int num = 1;
        boolean check;
        do{
            check = false;
            for(Queue q: queues)
            {
                if(q.getName().equals(queue.getName())) {
                    check = true;
                    break;
                }
            }
            if(check)
                queue.setName(queue.getName() + num++);
        }while (check);
        this.queues.add(queue);
    }

    public void addQueues(ArrayList<Queue> queues)
    {
        for(Queue q : queues)
            this.addQueue(q);
    }

    public void removeQueue(Queue queue) {
        this.queues.remove(queue);
    }

    public void addPlace(Place place) {
        int num = 1;
        boolean check;
        do {
            check = false;
            for(Place p : places)
            {
                if(p.getName().equals(place.getName())) {
                    check = true;
                    break;
                }
            }

            if(check)
                place.setName(place.getName() + " " + num++);
        }while (check);

        this.places.add(place);
    }

    public void addPlaces(ArrayList<Place> places)
    {
        for (Place p : places)
            this.addPlace(p);
    }

    public void removePlace(Place place) {
        this.places.remove(place);
    }

    public void removePlace(String id)
    {
        Place place = this.getPlaceById(id);
        this.places.remove(place);
    }

    public void addTransition(Transition transition) {
        int num = 1;
        boolean check;
        do {
            check = false;
            for(Transition t : transitions)
            {
                if(t.getName().equals(transition.getName())) {
                    check = true;
                    break;
                }
            }

            if(check)
                transition.setName(transition.getName() + " " + num++);
        }while (check);
        this.transitions.add(transition);
    }

    public void addTransitions(ArrayList<Transition> transitions)
    {
        for (Transition t : transitions)
            this.addTransition(t);
    }

    public void removeTransition(Transition transition) {
        this.transitions.remove(transition);
    }

    public void removeTransition(String id)
    {
        Transition transition = this.getTransitionById(id);
        this.transitions.remove(transition);
    }

    public void addConnection(Connection connection) {
        this.connections.add(connection);
    }

    public void addConnections(ArrayList<Connection> connections)
    {
        for (Connection c: connections)
            this.addConnection(c);
    }

    public void removeConnection(Connection connection) {
        this.connections.remove(connection);
    }

    public void removeConnection(String id)
    {
        Connection connection = null;
        for (Connection c : connections)
        {
            if(c.getId().equals(id))
                connection = c;
        }
        connections.remove(connection);
    }
    public boolean contains(Object o)
    {
        if(o instanceof Color)
            return colors.contains((Color)o);
        else if(o instanceof  Queue)
            return queues.contains((Queue)o);
        else if(o instanceof Place)
            return places.contains((Place)o);
        else if(o instanceof Transition)
            return transitions.contains((Transition)o);
        else if(o instanceof Connection)
            return connections.contains((Connection)o);
        else
            return false;
    }

    public int[][] getLocation()
    {
        /*  location:
            minX, maxX
            minY, maxY
        */
        int[][] location = new int[2][2];
        int minX = places.get(0).getx(), minY = places.get(0).gety() , maxX = transitions.get(0).getx(), maxY = transitions.get(0).gety();
        for(Place p : places)
        {
            int x = p.getx();
            int y = p.gety();

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
        }

        for (Transition t : transitions)
        {
            int x = t.getx();
            int y = t.gety();

            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }

        location[0][0] = minX;
        location[0][1] = maxX;
        location[1][0] = minY;
        location[1][1] = maxY;
        return location;
    }
}
