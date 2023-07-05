package models.SD;

import java.util.ArrayList;

public class SDDocument {
    private ArrayList<SDLifeLine> lifeLines;
    private ArrayList<SDMessage> messages;
    private ArrayList<SDCF> cfs;

    public SDDocument() {
        lifeLines = new ArrayList<>();
        messages = new ArrayList<>();
        cfs = new ArrayList<>();
    }

    public SDDocument(ArrayList<SDLifeLine> lifeLines, ArrayList<SDMessage> messages, ArrayList<SDCF> cfs) {
        this.setLifeLines(lifeLines);
        this.setMessages(messages);
        this.setCfs(cfs);
    }

    public ArrayList<SDLifeLine> getLifeLines() {
        return lifeLines;
    }

    public SDLifeLine getLifeLineById(String id)
    {
        for(SDLifeLine lifeLine : lifeLines)
        {
            if(lifeLine.getId().equals(id))
                return lifeLine;
        }
        return null;
    }

    public void setLifeLines(ArrayList<SDLifeLine> lifeLines) {
        this.lifeLines = lifeLines;
    }

    public ArrayList<SDMessage> getMessages() {
        return messages;
    }
    public SDMessage getMessageById(String id)
    {
        for(SDMessage message : messages)
        {
            if(message.getId().equals(id))
                return message;
        }
        return null;
    }

    public void setMessages(ArrayList<SDMessage> messages) {
        this.messages = messages;
    }

    public ArrayList<SDCF> getCfs() {
        return cfs;
    }

    public void setCfs(ArrayList<SDCF> cfs) {
        this.cfs = cfs;
        for(SDCF cf : cfs)
        {
            for(Operand operand : cf.getOperands())
            {
                for(String messageId : operand.getMessages())
                {
                    this.getMessageById(messageId).setCf(cf.getId());
                }
            }
        }
    }

    public void addLifeLine(SDLifeLine lifeLine)
    {
        this.lifeLines.add(lifeLine);
    }

    public void removeLifeLine(SDLifeLine lifeLine)
    {
        this.lifeLines.remove(lifeLine);
    }

    public void addMessage(SDMessage message)
    {
        this.messages.add(message);
    }



    public void removeMessage(SDMessage message)
    {
        this.messages.remove(message);
    }

    public void addCF(SDCF cf)
    {
        this.cfs.add(cf);
    }

    public void removeCF(SDCF cf)
    {
        this.cfs.remove(cf);
    }
}
