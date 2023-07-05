package models.SD;

import noNamespace.ProjectDocument.Project.Models.ModelRelationshipContainer.ModelChildren.ModelRelationshipContainer2.ModelChildren2.Message;

public class SDMessage implements Comparable {
    private Message m;
    private String Id;
    private String Name="";
    private String Type = "";
    private String FromEnd = "";
    private String ToEnd = "";
    private String FromElement = "";
    private String ToElement = "";
    private String FromActivation = "";
    private String ToActivation = "";
    private String SendMessage = "";
    private String ReturnMessage = "";
    private String SequenceNumber = "";
    private String Occurence = "";
    private int x,y;
    private String cf = null;

    public SDMessage(Message m)
    {
        this.m = m;
        this.setAttributes(m);
    }

    private void setAttributes(Message m)
    {
        if(m.isSetId())
            this.Id = m.getId();
        if(m.isSetName())
            this.Name = m.getName();
        this.Type = getType(m);
        if(m.getFromEnd().getMessageEnd().isSetEndModelElement())
            this.FromEnd = m.getFromEnd().getMessageEnd().getId();
        if(m.getToEnd().getMessageEnd().isSetEndModelElement())
            this.ToEnd = m.getToEnd().getMessageEnd().getId();
        if(m.isSetEndRelationshipFromMetaModelElement())
            this.FromElement = m.getEndRelationshipFromMetaModelElement();
        if(m.isSetEndRelationshipToMetaModelElement())
            this.ToElement = m.getEndRelationshipToMetaModelElement();
        if(m.isSetFromActivation())
            this.FromActivation = m.getFromActivation();
        if(m.isSetToActivation())
            this.ToActivation = m.getToActivation();
        if(m.isSetReturnMessage())
            this.ReturnMessage = m .getReturnMessage();
        if(m.isSetSequenceNumber())
            this.SequenceNumber = m.getSequenceNumber();
        this.Occurence = m.getMasterView().getMessage().getIdref();
    }

    private String getType(Message m) {
        if(!m.isSetEndRelationshipFromMetaModelElement() && m.isSetEndRelationshipToMetaModelElement())
            return "Found Message";
        if(!m.isSetEndRelationshipToMetaModelElement() && m.isSetEndRelationshipFromMetaModelElement())
            return "Lost Message";
        if(!m.isSetType() || (m.getType().equals("Message") && !m.isSetActionType()))
            return "Asynchronous Message";
        if(m.isSetType() && m.getType().equals("Create Message"))
            return "Create Message";
        if(m.isSetType() && m.getType().equals("Message")) {
            if (m.isSetReturnMessage() && m.isSetActionType() && m.getActionType().isSetActionTypeSend() && m.getActionType().getActionTypeSend().getName().equals("Send"))
                return "Send Message";
            if (m.isSetActionType() && m.getActionType().isSetActionTypeReturn() && m.getActionType().getActionTypeReturn().getName().equals("Return"))
                return "Return Message";
            if (m.isSetActionType() && m.getActionType().isSetActionTypeDestroy() && m.getActionType().getActionTypeDestroy().getName().equals("Destroy"))
                return "Destroy Message";
            if (m.isSetActionType() && m.getActionType().isSetActionTypeCall() && m.getActionType().getActionTypeCall().getName().equals("Call"))
                return "Asynchronous Message";
        }
        if(m.isSetType())
            return m.getType();
        return "";
    }

    public Message getM() {
        return m;
    }

    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return Type;
    }

    public String getFromEnd() {
        return FromEnd;
    }

    public String getToEnd() {
        return ToEnd;
    }

    public String getFromElement() {
        return FromElement;
    }

    public String getToElement() {
        return ToElement;
    }

    public String getFromActivation() {
        return FromActivation;
    }

    public String getToActivation() {
        return ToActivation;
    }

    public String getSendMessage() {
        return SendMessage;
    }

    public void setSendMessage(String sendMessage) {
        SendMessage = sendMessage;
    }

    public void setReturnMessage(String returnMessage) {
        ReturnMessage = returnMessage;
    }

    public String getReturnMessage() {
        return ReturnMessage;
    }

    public String getSequenceNumber() {
        return SequenceNumber;
    }

    public String getOccurence() {
        return Occurence;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getCf() {
        return cf;
    }

    public void setCf(String cf) {
        this.cf = cf;
    }

    public boolean isInCF()
    {
        return this.cf != null;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setType(String type) {
        Type = type;
    }

    public void setFromEnd(String fromEnd) {
        FromEnd = fromEnd;
    }

    public void setToEnd(String toEnd) {
        ToEnd = toEnd;
    }

    public void setFromElement(String fromElement) {
        FromElement = fromElement;
    }

    public void setToElement(String toElement) {
        ToElement = toElement;
    }

    public void setFromActivation(String fromActivation) {
        FromActivation = fromActivation;
    }

    public void setToActivation(String toActivation) {
        ToActivation = toActivation;
    }

    public void setSequenceNumber(String sequenceNumber) {
        SequenceNumber = sequenceNumber;
    }

    public void setOccurence(String occurence) {
        Occurence = occurence;
    }

    public SDMessage clone()
    {
        try {
            return (SDMessage) super.clone();
        }catch (Exception e)
        {
            SDMessage message = new SDMessage(this.m);
            message.setName(this.Name);
            message.setCf(this.cf);
            message.setSendMessage(this.SendMessage);
            message.setReturnMessage(this.ReturnMessage);
            message.setX(this.x);
            message.setY(this.y);
            return message;

        }
    }
    @Override
    public int compareTo(Object o) {
        SDMessage m = (SDMessage) o;
        return this.y == m.getY() ? this.x - m.getX() : this.y - m.getY();
    }

    @Override
    public String toString() {
        return "SDMessage{" +
                "Id='" + Id + '\'' +
                ", Name='" + Name + '\'' +
                ", Type='" + Type + '\'' +
                ", FromEnd='" + FromEnd + '\'' +
                ", ToEnd='" + ToEnd + '\'' +
                ", FromElement='" + FromElement + '\'' +
                ", ToElement='" + ToElement + '\'' +
                ", FromActivation='" + FromActivation + '\'' +
                ", ToActivation='" + ToActivation + '\'' +
                ", ReturnMessage='" + ReturnMessage + '\'' +
                ", SequenceNumber='" + SequenceNumber + '\'' +
                ", Occurence='" + Occurence + '\'' +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SDMessage message = (SDMessage) o;

        if (x != message.x) return false;
        if (y != message.y) return false;
        if (!Id.equals(message.Id)) return false;
        if (Name != null ? !Name.equals(message.Name) : message.Name != null) return false;
        if (!Type.equals(message.Type)) return false;
        if (FromEnd != null ? !FromEnd.equals(message.FromEnd) : message.FromEnd != null) return false;
        if (ToEnd != null ? !ToEnd.equals(message.ToEnd) : message.ToEnd != null) return false;
        if (FromElement != null ? !FromElement.equals(message.FromElement) : message.FromElement != null) return false;
        if (ToElement != null ? !ToElement.equals(message.ToElement) : message.ToElement != null) return false;
        if (FromActivation != null ? !FromActivation.equals(message.FromActivation) : message.FromActivation != null)
            return false;
        if (ToActivation != null ? !ToActivation.equals(message.ToActivation) : message.ToActivation != null)
            return false;
        if (SendMessage != null ? !SendMessage.equals(message.SendMessage) : message.SendMessage != null) return false;
        if (ReturnMessage != null ? !ReturnMessage.equals(message.ReturnMessage) : message.ReturnMessage != null)
            return false;
        if (SequenceNumber != null ? !SequenceNumber.equals(message.SequenceNumber) : message.SequenceNumber != null)
            return false;
        if (Occurence != null ? !Occurence.equals(message.Occurence) : message.Occurence != null) return false;
        return cf != null ? cf.equals(message.cf) : message.cf == null;
    }

    @Override
    public int hashCode() {
        int result = Id.hashCode();
        result = 31 * result + (Name != null ? Name.hashCode() : 0);
        result = 31 * result + Type.hashCode();
        result = 31 * result + (FromEnd != null ? FromEnd.hashCode() : 0);
        result = 31 * result + (ToEnd != null ? ToEnd.hashCode() : 0);
        result = 31 * result + (FromElement != null ? FromElement.hashCode() : 0);
        result = 31 * result + (ToElement != null ? ToElement.hashCode() : 0);
        result = 31 * result + (FromActivation != null ? FromActivation.hashCode() : 0);
        result = 31 * result + (ToActivation != null ? ToActivation.hashCode() : 0);
        result = 31 * result + (SendMessage != null ? SendMessage.hashCode() : 0);
        result = 31 * result + (ReturnMessage != null ? ReturnMessage.hashCode() : 0);
        result = 31 * result + (SequenceNumber != null ? SequenceNumber.hashCode() : 0);
        result = 31 * result + (Occurence != null ? Occurence.hashCode() : 0);
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + (cf != null ? cf.hashCode() : 0);
        return result;
    }
}
