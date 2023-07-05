package models.qpn;

public class Connection {
    private String id;
    private String srcId;
    private String tarId;
    int count = -1;

    public Connection(String id, String srcId, String tarId) {
        this.id = id;
        this.srcId = srcId;
        this.tarId = tarId;
    }

    public Connection(String id, String srcId, String tarId, int count) {
        this.id = id;
        this.srcId = srcId;
        this.tarId = tarId;
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSrcId() {
        return srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }

    public String getTarId() {
        return tarId;
    }

    public void setTarId(String tarId) {
        this.tarId = tarId;
    }

    public int getCounts()
    {
        return this.count;
    }
    public String getCount() {
        return String.valueOf(count);
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Connection that = (Connection) o;

        if (count != that.count) return false;
        if (!id.equals(that.id)) return false;
        if (!srcId.equals(that.srcId)) return false;
        return tarId.equals(that.tarId);
    }
}
