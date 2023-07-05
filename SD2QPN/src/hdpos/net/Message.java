/*
 * Class Message
 */
package hdpos.net;

public class Message {
    private String id = "";
    private String name = "";
    private String color = "";
    private String send = "";
    private String receive = "";
    private String sequenceNumber; //thứ tự của Message
    private String sendFromLifeLine = null; //id của Lifeline nơi Message gửi đi
    private String sendFromExecution = null; //id của Execution nơi Message gửi đi
    private String receiveOnLifeLine = null; //id của Lifeline nơi Message gửi đến
    private String receiveOnExecution = null; //id của Execution nơi Message gửi đến
    private String type; //Loại Message

    public Message(String id, String name, String color, String send, String receive, String sequenceNumber, String type) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.send = send;
        this.receive = receive;
        this.sequenceNumber = sequenceNumber;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getReceive() {
        return receive;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getSendFromLifeLine() {
        return sendFromLifeLine;
    }

    public void setSendFromLifeLine(String sendFromLifeLine) {
        this.sendFromLifeLine = sendFromLifeLine;
    }

    public String getSendFromExecution() {
        return sendFromExecution;
    }

    public void setSendFromExecution(String sendFromExecution) {
        this.sendFromExecution = sendFromExecution;
    }

    public String getReceiveOnLifeLine() {
        return receiveOnLifeLine;
    }

    public void setReceiveOnLifeLine(String receiveOnLifeLine) {
        this.receiveOnLifeLine = receiveOnLifeLine;
    }

    public String getReceiveOnExecution() {
        return receiveOnExecution;
    }

    public void setReceiveOnExecution(String receiveOnExecution) {
        this.receiveOnExecution = receiveOnExecution;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setReceive(String receive) {
        this.receive = receive;
    }
}
