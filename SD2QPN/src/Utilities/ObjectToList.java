package Utilities;

import noNamespace.ProjectDocument.Project.Models.Frame.ModelChildren.*;
import noNamespace.ProjectDocument.Project.Models.ModelRelationshipContainer.ModelChildren.ModelRelationshipContainer2.ModelChildren2.Message;
import models.SD.*;

import java.util.ArrayList;

public class ObjectToList {

    public static ArrayList<SDMessage> MessagesToList (Message[] amessage)
    {
        ArrayList<SDMessage> messages = new ArrayList<>();
        for(Message m:amessage)
        {
            messages.add(new SDMessage(m));
        }
        return messages;
    }

    public static ArrayList<SDLifeLine> LifeLineToList(InteractionLifeLine[] alifeLine)
    {
        ArrayList<SDLifeLine> lifeLines = new ArrayList<>();

        for(InteractionLifeLine l: alifeLine)
        {
            lifeLines.add(new SDLifeLine(l));
        }
        return lifeLines;
    }

    public static ArrayList<SDCF> CFToList(CombinedFragment[] acf)
    {
        ArrayList<SDCF> cfs = new ArrayList<>();
        for(CombinedFragment cf: acf)
        {
            cfs.add(new SDCF(cf));
        }
        return cfs;
    }
}
