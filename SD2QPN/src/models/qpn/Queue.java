package models.qpn;
public class Queue {
    String id;
    String name;
    String numberOfServe = "1";
    String strategy = "FCFS";

    public Queue(String id, String name) {
        this.id = id;
        this.name = name;
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

    public String getNumberOfServe() {
        return numberOfServe;
    }

    public String getStrategy() {
        return strategy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Queue queue = (Queue) o;

        if (!id.equals(queue.id)) return false;
        if (!name.equals(queue.name)) return false;
        if (!numberOfServe.equals(queue.numberOfServe)) return false;
        return strategy.equals(queue.strategy);
    }
}

