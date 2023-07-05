package Utilities;

import noNamespace.ProjectDocument.Project;
import noNamespace.ProjectDocument.Project.Diagrams.InteractionDiagram.Connectors.Message;
import noNamespace.ProjectDocument.Project.Diagrams.InteractionDiagram.Shapes.Activation;
import models.SD.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

public class ReadObjects {

    public static ArrayList<SDMessage> ReadMessages(Project project) {
        try {
            ArrayList<SDMessage> messages = ObjectToList.MessagesToList(project.getModels().getModelRelationshipContainer().getModelChildren().getModelRelationshipContainer().getModelChildren().getMessageArray());
            Message[] arrMessages = project.getDiagrams().getInteractionDiagram().getConnectors().getMessageArray();
            for (Message m : arrMessages) {
                String idM = m.getMetaModelElement();
                int y = m.getY();
                for (SDMessage sm : messages) {
                    if (sm.getId().equals(idM)) {
                        sm.setY(y);
                        break;
                    }
                }
            }
            for (SDMessage m : messages) {
                if (m.getType().equals("Send Message")) {
                    String returnId = m.getReturnMessage();
                    String sendId = m.getId();
                    for (SDMessage tmp : messages) {
                        if (tmp.getId().equals(returnId)) {
                            tmp.setSendMessage(sendId);
                        }
                    }
                }
            }

            Collections.sort(messages);
            return messages;
        } catch (NullPointerException ne) {
            ArrayList<SDMessage> messages = new ArrayList<>();
            return messages;
        }
    }

    public static ArrayList<SDLifeLine> ReadLifeLines(Project project)
    {
        ArrayList<SDLifeLine> lifeLines = ObjectToList.LifeLineToList(project.getModels().getFrame().getModelChildren().getInteractionLifeLineArray());
        Activation[] activations = project.getDiagrams().getInteractionDiagram().getShapes().getActivationArray();
        Hashtable<String,Integer[]> hashtable = new Hashtable<String, Integer[]>();
        for(Activation a : activations)
        {
            hashtable.put(a.getMetaModelElement(), new Integer[]{new Integer(a.getX()),new Integer(a.getY())});
        }
        for(SDLifeLine l: lifeLines)
        {
            for(SDActivation a : l.getActivations())
            {
                a.setX(hashtable.get(a.getId())[0].intValue());
                a.setY(hashtable.get(a.getId())[1].intValue());
            }
            Collections.sort(l.getActivations());
        }

        return lifeLines;
    }

    public static ArrayList<SDCF> ReadCF(Project project)
    {
        return ObjectToList.CFToList(project.getModels().getFrame().getModelChildren().getCombinedFragmentArray());
    }

    public static void ReadOccurences(ArrayList<SDLifeLine> lifeLines, ArrayList<SDMessage> messages)
    {
        for(SDLifeLine l : lifeLines)
        {
            String lifelineId = l.getId();
            for(SDMessage m : messages)
            {
                if(!m.getType().equals("Found Message") && m.getFromElement().equals(lifelineId))
                {
                    for(SDActivation a : l.getActivations())
                    {
                        if(m.getFromActivation().equals(a.getId()))
                            a.addOcrs(m.getFromEnd());
                    }
                }

                if(!m.getType().equals("Lost Message") && m.getToElement().equals(lifelineId))
                {
                    for(SDActivation a : l.getActivations())
                    {
                        if(m.getToActivation().equals(a.getId()))
                            a.addOcrs(m.getToEnd());
                    }
                }
            }
        }
    }

}
