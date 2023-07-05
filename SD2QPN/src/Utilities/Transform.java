package Utilities;

import models.SD.*;
import models.qpn.*;

import java.io.File;
import java.util.ArrayList;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Transform {

    public static void transformLifeLines(SDDocument sdDocument, QPNDocument qpnDocument) {
        if (qpnDocument == null)
            qpnDocument = new QPNDocument();

        ArrayList<SDLifeLine> lifeLines = sdDocument.getLifeLines();

        int x = 100,y = 100;

        for (int i = 0; i < lifeLines.size(); i++) {
            //maps
            ArrayList<String> tmpPlaces = new ArrayList<>();
            ArrayList<String> tmpTransitions = new ArrayList<>();
            ArrayList<String> tmpConnections = new ArrayList<>();
            ArrayList[] arrayLists = new ArrayList[3];
            arrayLists[0] = tmpPlaces;
            arrayLists[1] = tmpTransitions;
            arrayLists[2] = tmpConnections;

            //Tạo Queue, Color, Place và Transition của LifeLine
            SDLifeLine lifeLine = lifeLines.get(i);
            Queue queue = new Queue(lifeLine.getId() + "_Q", lifeLine.getName() + "_Q");
            Color color = new Color(lifeLine.getId() + "_C", lifeLine.getName() + "_C", RandomColor.getRandomColor());

            //Place mở
            Place openPlace = new Place(lifeLine.getId() + "_OP", lifeLine.getName(),x,y,queue);
            x+=75;
            ColorRef colorRef = new ColorRef(openPlace.getId() + "_CR",color.getId());
            colorRef.setInit(1);
            openPlace.addColorRef(colorRef);
            Transition openTransition = new Transition(lifeLine.getId() + "_OT",lifeLine.getName(), x,y);
            x+=75;
            Connection openConnection = new Connection(lifeLine.getId() + "__OPT",openPlace.getId(),openTransition.getId());

            //Connection Open Workload
            Connection connectionOWL = new Connection(lifeLine.getId() + "__OTP",openTransition.getId(),openPlace.getId());

            //Tạo các modes
            Mode mode = new Mode(openTransition.getId() + "_M",openTransition.getName(), RandomColor.getRandomColor());
            Connection modeConnection1 = new Connection(mode.getId() + "_C",colorRef.getId(),mode.getId(),1);
            Connection modeConnection2 = new Connection(mode.getId() + "_OWL",mode.getId(),colorRef.getId(),1);
            openTransition.addMode(mode);
            openTransition.addConnection(modeConnection1);
            openTransition.addConnection(modeConnection2);

            //add to QPN Document
            qpnDocument.addQueue(queue);
            qpnDocument.addColor(color);
            qpnDocument.addPlace(openPlace);
            qpnDocument.addTransition(openTransition);
            qpnDocument.addConnection(openConnection);
            qpnDocument.addConnection(connectionOWL);

            //add to maps
            tmpPlaces.add(openPlace.getId());
            tmpTransitions.add(openTransition.getId());
            tmpConnections.add(openConnection.getId());
            tmpConnections.add(connectionOWL.getId());

            int num = 1;

            Transition prev = openTransition; //Transition ở phía trước, dùng để tạo Connection

            //Các Place của Executions và Occurrences
            ArrayList<SDActivation> executions = lifeLine.getActivations();
            for(int j = 0; j < executions.size(); j++)
            {
                SDActivation execution = executions.get(j);
                ArrayList<String> occurrences = execution.getOcrs();
                for(String occ : occurrences)
                {
                    //Place
                    Place occPlace = new Place(occ + "_P",lifeLine.getName() + "_" + num,x,y,queue);
                    ColorRef occColorRef = new ColorRef(occPlace.getId() + "_CR",color.getId());
                    occPlace.addColorRef(occColorRef);
                    x+=75;

                    //Transition
                    Transition occTran = new Transition(occ + "_T", lifeLine.getName() + "_" + num, x, y);
                    x+=75;

                    Mode occMode = new Mode(occTran.getId() + "_M",occTran.getName(),RandomColor.getRandomColor());
                    occTran.addMode(occMode);

                    //Connection
                    if(prev != null)
                    {
                        Connection prevConnection = new Connection(prev.getId() + "-P",prev.getId(),occPlace.getId());
                        Connection prevModeConnection = new Connection(prev.getId() + "_M_N",prev.getId() + "_M",occColorRef.getId(),1);
                        qpnDocument.addConnection(prevConnection);
                        qpnDocument.getTransitionById(prev.getId()).addConnection(prevModeConnection);
                        tmpConnections.add(prevConnection.getId());
                    }

                    Connection occConnection = new Connection(occ + "_PT", occPlace.getId(),occTran.getId());
                    Connection occModeConnection = new Connection(occMode.getId() + "_C", occColorRef.getId(), occMode.getId(),1);
                    occTran.addConnection(occModeConnection);

                    qpnDocument.addPlace(occPlace);
                    qpnDocument.addTransition(occTran);
                    qpnDocument.addConnection(occConnection);

                    tmpPlaces.add(occPlace.getId());
                    tmpTransitions.add(occTran.getId());
                    tmpConnections.add(occConnection.getId());

                    prev = occTran;
                    num++;
                }

                //Place đóng của execution
                Place closePlace = new Place(execution.getId() + "_CP", lifeLine.getName() + "_" + num,x,y,queue);
                x+=75;
                ColorRef eColorRef = new ColorRef(closePlace.getId() + "_CR",color.getId());
                closePlace.addColorRef(eColorRef);

                Transition closeTran = new Transition(execution.getId() + "_CT", lifeLine.getName() + "_" + num,x,y);
                x+=75;

                Connection eConnection = new Connection(execution.getId() + "_PT", closePlace.getId(), closeTran.getId());
                Connection prevConnection = new Connection(prev.getId() + "-P",prev.getId(),closePlace.getId());
                qpnDocument.addConnection(prevConnection);
                qpnDocument.addConnection(eConnection);
                Connection prevModeConnection = new Connection(prev.getId() + "_M_N", prev.getId() + "_M", eColorRef.getId(),1);
                qpnDocument.getTransitionById(prev.getId()).addConnection(prevModeConnection);

                tmpConnections.add(prevConnection.getId());
                tmpConnections.add(eConnection.getId());

                Mode eMode = new Mode(closeTran.getId() + "_M", closeTran.getName(), RandomColor.getRandomColor());
                Connection eModeConnection = new Connection(eMode.getId() + "_C",eColorRef.getId(), eMode.getId(),1);

                closeTran.addMode(eMode);
                closeTran.addConnection(eModeConnection);

                //Close workload
                if(j == executions.size() - 1)
                {
                    Connection connectionCWL = new Connection(lifeLine.getId() + "__CTP",closeTran.getId(),closePlace.getId());
                    qpnDocument.addConnection(connectionCWL);
                    Connection modeConnectionCWL = new Connection(eMode.getId() + "_CWL", eMode.getId(), eColorRef.getId(),1);
                    closeTran.addConnection(modeConnectionCWL);
                    tmpConnections.add(connectionCWL.getId());
                }

                qpnDocument.addPlace(closePlace);
                qpnDocument.addTransition(closeTran);
                tmpPlaces.add(closePlace.getId());
                tmpTransitions.add(closeTran.getId());

                num++;
                prev = closeTran;
            }

            x = 100;
            y += 150;

            Maps.lifelineMaps.put(lifeLine.getId(),arrayLists);
        }
    }

    public static void transformMessages(SDDocument sdDocument, QPNDocument qpnDocument)
    {
        ArrayList<SDMessage> messages = sdDocument.getMessages();
        for(SDMessage m : messages)
        {
            if(m.isInCF())
                continue;
            if(m.getType().equals("Send Message"))
            {
                SDMessage reply = sdDocument.getMessageById(m.getReturnMessage());
                transformSendMessage(qpnDocument,m,reply);
            }
            else if(m.getType().equals("Return Message"))
            {
                transformReturnMessage(qpnDocument,m);
            }
            else if(m.getType().equals("Found Message"))
            {
                transformFoundMessage(qpnDocument,m);
            }
            else if(m.getType().equals("Lost Message"))
            {
                transformLostMessage(qpnDocument,m);
            }
            else
            {
                transformAsynchronousMessage(qpnDocument,m);
            }
        }
    }

    private static void transformSendMessage(QPNDocument qpnDocument, SDMessage m, SDMessage reply)
    {
        //maps
        ArrayList<String> tmpPlaces = new ArrayList<>();
        ArrayList<String> tmpTransitions = new ArrayList<>();
        ArrayList<String> tmpConnections = new ArrayList<>();
        ArrayList[] arrayLists = new ArrayList[3];
        arrayLists[0] = tmpPlaces;
        arrayLists[1] = tmpTransitions;
        arrayLists[2] = tmpConnections;

        //message
        Transition src;
        Place tar;
        Color messageColor;
        ColorRef messageColorRef;
        Connection messageConnection;
        Connection modeConnection;

        src = qpnDocument.getTransitionById(m.getFromEnd() + "_T");
        Place prev = qpnDocument.getPlaceById(m.getFromEnd() + "_P");
        int preIndex  = qpnDocument.getPlaces().indexOf(prev);
        Place next = qpnDocument.getPlaces().get(preIndex + 1);
        tar = qpnDocument.getPlaceById(m.getToEnd() + "_P");

        Transition replyT = qpnDocument.getTransitionById(reply.getFromEnd() + "_T");
//                Place replyP = qpnDocument.getPlaceById(reply.getToEnd() + "_P");
//                if(!replyP.equals(next))
//                {
//                    Logging.Error("Error in message " + reply.getName() + " id " + reply.getId());
//                }
        //remove next connection
        Connection remove = null;
        for(Connection c: qpnDocument.getConnections())
        {
            if(c.getSrcId().equals(src.getId()) && c.getTarId().equals(next.getId()))
                remove = c;
        }

        if(remove != null) {
            qpnDocument.removeConnection(remove);
            remove = null;
        }

        for(Connection c : src.getConnections())
        {
            if(c.getTarId().equals(next.getColorRefs().get(0).getId()))
                remove = c;
        }
        if(remove != null)
        {
            src.removeConnection(remove);
        }
        //Sync place
        Place syncPlace = new Place(m.getId() + "_sync", "Sync_" + prev.getName(),src.getx(),(src.gety() + replyT.gety())/2, PlaceType.OPLACE);
        ColorRef syncColorRef = new ColorRef(m.getId() + "_syncC",prev.getColorRefs().get(0).getColorId());
        syncPlace.addColorRef(syncColorRef);
        qpnDocument.addPlace(syncPlace);
        Connection syncConnection1 = new Connection(src.getId() + "_sync",src.getId(),syncPlace.getId());
        Connection syncConnection2 = new Connection(replyT.getId() + "_sync",syncPlace.getId(),replyT.getId());
        qpnDocument.addConnection(syncConnection1);
        qpnDocument.addConnection(syncConnection2);
        Connection modeConnection1 = new Connection(src.getId() + "_M_sync",src.getModes().get(0).getId(),syncColorRef.getId(),1);
        src.addConnection(modeConnection1);
        Connection modeConnection2 = new Connection(replyT.getId() + "_M_sync",syncColorRef.getId(),replyT.getModes().get(0).getId(),1);
        replyT.addConnection(modeConnection2);


        messageColor = new Color(m.getId(), m.getName(), RandomColor.getRandomColor());
        qpnDocument.addColor(messageColor);

        messageColorRef = new ColorRef(m.getId() + "_CR", messageColor.getId());
        tar.addColorRef(messageColorRef);

        messageConnection = new Connection(m.getId() + "_MC", src.getId(), tar.getId());
        qpnDocument.addConnection(messageConnection);

        modeConnection = new Connection(m.getId() + "_M_C", src.getModes().get(0).getId(), messageColorRef.getId(), 1);
        src.addConnection(modeConnection);

        tmpPlaces.add(syncPlace.getId());
        tmpConnections.add(syncConnection1.getId());
        tmpConnections.add(syncConnection2.getId());
        tmpConnections.add(messageConnection.getId());
        Maps.messageMaps.put(m.getId(),arrayLists);
    }

    private static void transformReturnMessage(QPNDocument qpnDocument, SDMessage m)
    {
        //maps
        ArrayList<String> tmpPlaces = new ArrayList<>();
        ArrayList<String> tmpTransitions = new ArrayList<>();
        ArrayList<String> tmpConnections = new ArrayList<>();
        ArrayList[] arrayLists = new ArrayList[3];
        arrayLists[0] = tmpPlaces;
        arrayLists[1] = tmpTransitions;
        arrayLists[2] = tmpConnections;

        if(m.getName().length() < 1)
            m.setName("Reply");
        Transition src = qpnDocument.getTransitionById(m.getFromEnd() + "_T");
        Place tarP = qpnDocument.getPlaceById(m.getToEnd() + "_P");
        Transition tarT = qpnDocument.getTransitionById(m.getToEnd() + "_T");
        Color messageColor = new Color(m.getId(), m.getName(), RandomColor.getRandomColor());
        ColorRef messageColorRef = new ColorRef(m.getId() + "_CR", messageColor.getId());
        Connection messageConnection = new Connection(m.getId() + "_C", src.getId(), tarP.getId());
        Connection modeConnection = new Connection(m.getId() + "_M_C", src.getModes().get(0).getId(), messageColorRef.getId(), 1);
        Connection modePlaceConnection = new Connection(m.getId() + "_M_P_C", messageColorRef.getId(), tarT.getModes().get(0).getId(), 1);

        qpnDocument.addColor(messageColor);
        tarP.addColorRef(messageColorRef);
        qpnDocument.addConnection(messageConnection);
        src.addConnection(modeConnection);
        tarT.addConnection(modePlaceConnection);

        tmpConnections.add(messageConnection.getId());
        Maps.messageMaps.put(m.getId(),arrayLists);
    }

    private static void transformFoundMessage(QPNDocument qpnDocument, SDMessage m)
    {
        //maps
        ArrayList<String> tmpPlaces = new ArrayList<>();
        ArrayList<String> tmpTransitions = new ArrayList<>();
        ArrayList<String> tmpConnections = new ArrayList<>();
        ArrayList[] arrayLists = new ArrayList[3];
        arrayLists[0] = tmpPlaces;
        arrayLists[1] = tmpTransitions;
        arrayLists[2] = tmpConnections;

        Place tarP = qpnDocument.getPlaceById(m.getToEnd() + "_P");
        Transition tarT = qpnDocument.getTransitionById(m.getToEnd() + "_T");
        Queue foundQueue = new Queue(m.getId(),m.getName().length() > 0?m.getName():"Found");
        Color foundColor = new Color(m.getId(),m.getName().length() > 0?m.getName():"Found",RandomColor.getRandomColor());
        ColorRef foundColorRef = new ColorRef(m.getId() + "_FCR", foundColor.getId());
        foundColorRef.setInit(1);
        Place foundPlace = new Place(m.getId() + "_P", m.getName().length() > 0?m.getName():"pFound",tarP.getx() - 75, tarP.gety() - 75,foundQueue);

        foundPlace.addColorRef(foundColorRef);
        Transition foundTransition = new Transition(m.getId() + "_T", m.getName().length() > 0 ? m.getName():"tFound",tarP.getx(), tarP.gety() - 75);
        Mode mode = new Mode(foundTransition.getId() + "_M",foundTransition.getName(),RandomColor.getRandomColor());
        foundTransition.addMode(mode);
        Connection foundConnection = new Connection(m.getId() + "_F",foundPlace.getId(),foundTransition.getId());
        Connection foundConnectionWL = new Connection(m.getId() + "_FWL",foundTransition.getId(),foundPlace.getId());
        Connection messageConnection = new Connection(m.getId() + "_C",foundTransition.getId(),tarP.getId());
        Connection modeConnectionFound = new Connection(m.getId() + "_M_F",foundColorRef.getId(),mode.getId(),1);
        Connection modeConnectionFoundWL = new Connection(m.getId() + "_M_FWL",mode.getId(),foundColorRef.getId(),1);
        Connection modeMessageConnection = new Connection(m.getId() + "_M_C",mode.getId(),tarP.getColorRefs().get(0).getId(),1);
        foundTransition.addConnection(modeConnectionFound);
        foundTransition.addConnection(modeConnectionFoundWL);
        foundTransition.addConnection(modeMessageConnection);

        //add color to target Place & Transition
        ColorRef messageColorRef = new ColorRef(m.getId() + "_CR",foundColor.getId());
        tarP.addColorRef(messageColorRef);
        Connection modeConnection = new Connection(tarT.getId() + "_M_M", messageColorRef.getId(),tarT.getModes().get(0).getId(),1);
        tarT.addConnection(modeConnection);

        qpnDocument.addQueue(foundQueue);
        qpnDocument.addColor(foundColor);
        qpnDocument.addPlace(foundPlace);
        qpnDocument.addTransition(foundTransition);
        qpnDocument.addConnection(foundConnection);
        qpnDocument.addConnection(foundConnectionWL);
        qpnDocument.addConnection(messageConnection);

        tmpPlaces.add(foundPlace.getId());
        tmpTransitions.add(foundTransition.getId());
        tmpConnections.add(foundConnection.getId());
        tmpConnections.add(foundConnectionWL.getId());
        tmpConnections.add(messageConnection.getId());
        Maps.messageMaps.put(m.getId(),arrayLists);
    }

    private static void transformLostMessage(QPNDocument qpnDocument, SDMessage m)
    {
        //maps
        ArrayList<String> tmpPlaces = new ArrayList<>();
        ArrayList<String> tmpTransitions = new ArrayList<>();
        ArrayList<String> tmpConnections = new ArrayList<>();
        ArrayList[] arrayLists = new ArrayList[3];
        arrayLists[0] = tmpPlaces;
        arrayLists[1] = tmpTransitions;
        arrayLists[2] = tmpConnections;

        Transition src = qpnDocument.getTransitionById(m.getFromEnd() + "_T");
        Queue messageQueue = new Queue(m.getId(),m.getName().length() > 0?m.getName():"Lost");
        Color messageColor = new Color(m.getId(),m.getName().length() > 0?m.getName():"Lost",RandomColor.getRandomColor());
        ColorRef messageColorRef = new ColorRef(m.getId() + "_CR", messageColor.getId());
        Place lostPlace = new Place(m.getId() + "_P", m.getName().length() > 0?m.getName():"pLost",src.getx(), src.gety() + 75,messageQueue);
        lostPlace.addColorRef(messageColorRef);
        Transition lostTransition = new Transition(m.getId() + "_T", m.getName().length() > 0 ? m.getName():"tLost",src.getx() + 75, src.gety() + 75);
        Mode mode = new Mode(lostTransition.getId() + "_M",lostTransition.getName(),RandomColor.getRandomColor());
        lostTransition.addMode(mode);
        Connection lostConnection = new Connection(m.getId() + "_L",lostTransition.getId(),lostPlace.getId());
        Connection lostConnectionWL = new Connection(m.getId() + "_LWL",lostPlace.getId(),lostTransition.getId());
        Connection messageConnection = new Connection(m.getId() + "_C",src.getId(),lostPlace.getId());
        Connection modeConnectionLost = new Connection(m.getId() + "_M_L",mode.getId(),messageColorRef.getId(),1);
        Connection modeConnectionLostWL = new Connection(m.getId() + "_M_LWL",messageColorRef.getId(),mode.getId(),1);
        Connection modeConnection = new Connection(m.getId() + "_M_C",src.getModes().get(0).getId(),messageColorRef.getId(),1);
        lostTransition.addConnection(modeConnectionLost);
        lostTransition.addConnection(modeConnectionLostWL);
        lostTransition.addConnection(modeConnection);

        qpnDocument.addQueue(messageQueue);
        qpnDocument.addColor(messageColor);
        qpnDocument.addPlace(lostPlace);
        qpnDocument.addTransition(lostTransition);
        qpnDocument.addConnection(lostConnection);
        qpnDocument.addConnection(lostConnectionWL);
        qpnDocument.addConnection(messageConnection);

        tmpPlaces.add(lostPlace.getId());
        tmpTransitions.add(lostTransition.getId());
        tmpConnections.add(lostConnection.getId());
        tmpConnections.add(lostConnectionWL.getId());
        tmpConnections.add(messageConnection.getId());
        Maps.messageMaps.put(m.getId(),arrayLists);
    }

    private static void transformAsynchronousMessage(QPNDocument qpnDocument, SDMessage m)
    {
        //maps
        ArrayList<String> tmpPlaces = new ArrayList<>();
        ArrayList<String> tmpTransitions = new ArrayList<>();
        ArrayList<String> tmpConnections = new ArrayList<>();
        ArrayList[] arrayLists = new ArrayList[3];
        arrayLists[0] = tmpPlaces;
        arrayLists[1] = tmpTransitions;
        arrayLists[2] = tmpConnections;

        String colorName;
        Transition src = qpnDocument.getTransitionById(m.getFromEnd() + "_T");
        Place tarP = qpnDocument.getPlaceById(m.getToEnd() + "_P");
        Transition tarT = qpnDocument.getTransitionById(m.getToEnd() + "_T");

        if(m.getType().equals("Create Message")) {
            colorName = m.getName() + " Create";
            tarP = qpnDocument.getPlaceById(m.getToElement() + "_OP");
            tarT = qpnDocument.getTransitionById(m.getToElement() + "_OT");
        }
        else if(m.getType().equals("Destroy Message"))
            colorName = m.getName() + " Destroy";
        else
            colorName = m.getName();

        Color messageColor = new Color(m.getId(), colorName , RandomColor.getRandomColor());
        ColorRef messageColorRef = new ColorRef(m.getId() + "_CR", messageColor.getId());
        Connection messageConnection = new Connection(m.getId() + "_C", src.getId(), tarP.getId());
        Connection modeConnection1 = new Connection(m.getId() + "_M_C1", src.getModes().get(0).getId(), messageColorRef.getId(), 1);
        Connection modeConnection2 = new Connection(m.getId() + "_M_C2", messageColorRef.getId(),tarT.getModes().get(0).getId(), 1);

        qpnDocument.addColor(messageColor);
        tarP.addColorRef(messageColorRef);
        qpnDocument.addConnection(messageConnection);
        src.addConnection(modeConnection1);
        tarT.addConnection(modeConnection2);

        tmpConnections.add(messageConnection.getId());
        Maps.messageMaps.put(m.getId(),arrayLists);
    }

    public static void transformCFs(SDDocument sdDocument, QPNDocument qpnDocument)
    {
        ArrayList<SDCF> cfs = sdDocument.getCfs();
        for(SDCF cf : cfs) {
            if (cf.getKind().equals("opt"))
                transformOpt(sdDocument, qpnDocument, cf);
        }
    }

    private static void transformOpt(SDDocument sdDocument, QPNDocument qpnDocument, SDCF cf) {
        /*ArrayList<Operand> operands = cf.getOperands();
        for(int i = 0; i < operands.size(); i++) {
            Operand operand = operands.get(i);
            Subnet subnet = new Subnet();
            QPNDocument tmpQPN = new QPNDocument();
            SDDocument tmpSD = new SDDocument();

            ArrayList<SDLifeLine> lifeLines = new ArrayList<>();
            ArrayList<SDMessage> messages = new ArrayList<>();

            for (String l : cf.getLifeLines()) {
                SDLifeLine lifeLine = new SDLifeLine();
                lifeLine.setId(l + "_CF");
                lifeLine.setName(sdDocument.getLifeLineById(l).getName() + "_CF");
                for(String m : operand.getMessages())
                {
                    SDMessage message = sdDocument.getMessageById(m).clone();
                    message.setCf(null);
                    if(!messages.contains(message))
                        messages.add(message);
                    SDActivation activation = new SDActivation();
                    if(message.getFromElement().equals(l))
                    {
                        activation.setId(message.getFromActivation());
                        activation.addOcrs(message.getFromEnd());
                    }

                    if(message.getToElement().equals(l))
                    {
                        activation.setId(message.getToActivation());
                        activation.addOcrs(message.getToEnd());
                    }

                    lifeLine.addActivation(activation);
                }
                lifeLines.add(lifeLine);
            }
            tmpSD.setLifeLines(lifeLines);
            tmpSD.setMessages(messages);

            transformLifeLines(tmpSD,tmpQPN);
            transformMessages(tmpSD,tmpQPN);

            for(SDLifeLine l : lifeLines)
            {
                ArrayList<String> places = Maps.lifelineMaps.get(l.getId())[0];
                ArrayList<String> transitions = Maps.lifelineMaps.get(l.getId())[1];
                ArrayList<String> connections = Maps.lifelineMaps.get(l.getId())[2];
                tmpQPN.removeColor(l.getId() + "_C");
                tmpQPN.removePlace(places.get(0));
                tmpQPN.removePlace(places.get(places.size() - 1));
                tmpQPN.removeTransition(transitions.get(0));
                tmpQPN.removeTransition(transitions.get(transitions.size() - 1));
                tmpQPN.removeConnection(connections.get(0));
                tmpQPN.removeConnection(connections.get(1));
                tmpQPN.removeConnection(connections.get(2));
                tmpQPN.removeConnection(connections.get(connections.size() - 1));
                tmpQPN.removeConnection(connections.get(connections.size() - 2));
                tmpQPN.removeConnection(connections.get(connections.size() - 3));
            }

            int[][] location = tmpQPN.getLocation();
            int ix, iy, ox, oy;
            ix = location[0][0] < 200? 0 : location[0][0] - 150;
            iy = location[1][1] + 75;
            ox = location[0][1] + 75;
            oy = iy;
            Place inputPlace = new Place(operand.getId() + "_IP","input-place",ix,iy,PlaceType.OPLACE);
            Place outputPlace = new Place(operand.getId() + "_OP","output-place",ox,oy,PlaceType.OPLACE);
            Transition tDo = new Transition(operand.getId() + "_DO", "tOptDo",ix + 75,iy);
            //Transition outputTran = new Transition(operand.getId() + "_OT", "output-transition",ox - 75,oy);
            subnet.setColors(tmpQPN.getColors());
            subnet.setPlaces(tmpQPN.getPlaces());
            subnet.setTransitions(tmpQPN.getTransitions());
            subnet.setConnections(tmpQPN.getConnections());
        }*/
        SDDocument tmpSD = CFToSD(sdDocument, cf);
        QPNDocument tmpQPN = new QPNDocument();
        Subnet subnet;
        transformLifeLines(tmpSD,tmpQPN);
        transformMessages(tmpSD,tmpQPN);

        int[][] location = tmpQPN.getLocation();
        int ix, iy, ox, oy;
        ix = location[0][0] < 200? 0 : location[0][0] - 150;
        iy = location[1][1] + 75;
        ox = location[0][1] + 75;
        oy = iy;

        Place inputPlace = new Place(cf.getId() + "_IP","input-place",ix,iy,PlaceType.OPLACE);
        Place outputPlace = new Place(cf.getId() + "_OP","output-place",ox,oy,PlaceType.OPLACE);
        Transition tOptDo = new Transition(cf.getId() + "_DO", "tOptDo",ix + 75,iy);
        Mode doOpt = new Mode(cf.getId() + "_MDO","doOpt",RandomColor.getRandomColor());
        Mode exitOpt = new Mode(cf.getId() + "_ME","exitOpt",RandomColor.getRandomColor());

        Place subnetPlace = new Place(cf.getId() + "_SP", "CF", 200, 200, PlaceType.SPLACE);
        Transition tOptExit = new Transition(cf.getId() + "_ST", "tOptExit", 300,200);
        Mode exitMode = new Mode(tOptExit.getId() + "_M","exitOpt",RandomColor.getRandomColor());
        tOptExit.addMode(exitMode);
        for (String l: cf.getLifeLines()) {
            ColorRef subnetCR = new ColorRef(l + "_SCR", l + "_C");
            subnetPlace.addColorRef(subnetCR);
            inputPlace.addColorRef(subnetCR);
            outputPlace.addColorRef(subnetCR);
            tOptExit.addConnection(new Connection(subnetCR.getId() + "_MC",subnetCR.getId(),exitMode.getId(),1));
        }
        float doWeight;
        try {
            doWeight = Float.valueOf(cf.getOperands().get(0).getConstraint());
        }catch (NumberFormatException e)
        {
            doWeight = 1.0f;
        }

        doOpt.setWeight(doWeight);
        exitOpt.setWeight(1-doWeight);
        tOptDo.addMode(doOpt);
        tOptDo.addMode(exitOpt);

        ArrayList<Place> removePlaces = new ArrayList<>();                 //Các place cần xóa và thay bằng subnet place
        ArrayList<Transition> removeTransitions = new ArrayList<>();      //Các transition cần xóa và thay bằng subnet place
        ArrayList<Connection> removeConnections = new ArrayList<>();      //Các connection cần xóa và thay bằng subnet place
        ArrayList<Connection> newConnections = new ArrayList<>();         //Các connection cần thêm vào

        int sx=0,sy=0,sn = 0;

        for(SDLifeLine l : tmpSD.getLifeLines()) {
            ArrayList<String> places = Maps.lifelineMaps.get(l.getId())[0];
            ArrayList<String> transitions = Maps.lifelineMaps.get(l.getId())[1];
            ArrayList<String> connections = Maps.lifelineMaps.get(l.getId())[2];
            Place lastPlace = tmpQPN.getPlaceById(places.get(places.size() - 1));
            String lifelineColor = tmpQPN.getPlaceById(places.get(0)).getColorRefs().get(0).getColorId();
            tmpQPN.removeColor(lifelineColor);
            tmpQPN.removePlace(places.get(0));
            tmpQPN.removePlace(places.get(places.size() - 1));
            tmpQPN.removeTransition(transitions.get(0));
            tmpQPN.removeTransition(transitions.get(transitions.size() - 1));
            tmpQPN.removeConnection(connections.get(0));
            tmpQPN.removeConnection(connections.get(1));
            tmpQPN.removeConnection(connections.get(2));
            tmpQPN.removeConnection(connections.get(connections.size() - 1));
            tmpQPN.removeConnection(connections.get(connections.size() - 2));
            tmpQPN.removeConnection(connections.get(connections.size() - 3));

             sn += places.size() - 2;
            for(int i = 1; i < places.size() - 1    ; i++)
            {
                String id = places.get(i);
                Place sp = tmpQPN.getPlaceById(id);
                sp.setQueue(qpnDocument.getQueueById(l.getId().replace(cf.getId(),"") + "_Q"));
                Place p = qpnDocument.getPlaceById(id.replace("_S",""));
                sx += p.getx();
                sy += p.gety();
                removePlaces.add(p);
            }

            for(int i = 1; i < transitions.size() - 1; i++)
            {
                String id = transitions.get(i);
                Transition t = qpnDocument.getTransitionById(id.replace("_S",""));
                removeTransitions.add(t);
            }
            for (int i = 3; i < connections.size()- 3; i++)
            {
                String id = connections.get(i);
                Connection c = qpnDocument.getConnectionById(id.replaceAll("_S",""));
                removeConnections.add(c);
            }
            for(Connection c : qpnDocument.getConnections())
            {
                //////Place ở đầu
                String tar1 = places.get(1).replace("_S","");
                int index1 = Maps.lifelineMaps.get(l.getId().replace(cf.getId(),""))[0].indexOf(tar1);
                String src1 = (String)Maps.lifelineMaps.get(l.getId().replace(cf.getId(),""))[1].get(index1 - 1);
                if(c.getSrcId().equals(src1) && c.getTarId().equals(tar1))
                {
                    c.setTarId(subnetPlace.getId());
                }
                Place tmpP = qpnDocument.getPlaceById(tar1);        //Place xóa ở đầu
                Transition tmpT = qpnDocument.getTransitionById(src1);  //Transition xóa ở đầu
                for(Connection mc : tmpT.getConnections())      //Duyệt các connection trong tmpT
                {
                    ColorRef tmpCR = tmpP.getColorRefs().get(0);    //ColorRef ứng với color của lifeline
                    if(mc.getTarId().equals(tmpCR.getId()))         //đổi connection đến subnet
                    {
                        for(ColorRef cr : subnetPlace.getColorRefs())
                            if(cr.getColorId().equals(tmpCR.getColorId()))
                                mc.setTarId(cr.getId());
                    }

                }

                //Place ở cuối
                String tar2 = places.get(places.size() - 1).replace("_S","");
                int index2 = Maps.lifelineMaps.get(l.getId().replace(cf.getId(),""))[0].indexOf(tar2);
                String src2 = (String)Maps.lifelineMaps.get(l.getId().replace(cf.getId(),""))[1].get(index2 - 1);
                if(c.getSrcId().equals(src2) && c.getTarId().equals(tar2))
                {
                    c.setSrcId(tOptExit.getId());
                }
                tmpP = qpnDocument.getPlaceById(tar2);        //Place xóa ở cuói
                ColorRef tmpCR = tmpP.getColorRefs().get(0);    //ColorRef ứng với color của lifeline
                Connection tmpC = new Connection(tOptExit.getId() + "_MCE",exitMode.getId(),tmpCR.getId(),1);
                tOptExit.addConnection(tmpC);

            }

            for(Place p : tmpQPN.getPlaces())
            {
                p.getColorRefs().get(0).setColorId(p.getColorRefs().get(0).getColorId().replace(cf.getId(),""));
            }

            Connection connection = new Connection(places.get(1) + "_DP",tOptDo.getId(),places.get(1));
            tmpQPN.addConnection(connection);
            ColorRef tmpColorRef = tmpQPN.getPlaceById(places.get(1)).getColorRefs().get(0);
            Connection modeConnection = new Connection(places.get(1) + "_DMP",doOpt.getId(),tmpColorRef.getId(),1);
            tOptDo.addConnection(modeConnection);

            Transition eT = tmpQPN.getTransitionById(transitions.get(transitions.size() - 2));

            ArrayList<Connection> removeModeConnections = new ArrayList<>(); //Các connection cần xóa trong mode của transition cuối cùng
            for(Connection c : eT.getConnections())
            {
                if(c.getTarId().equals(lastPlace.getColorRefs().get(0).getId()))
                    removeModeConnections.add(c);
            }
            for(Connection c : removeModeConnections)
            {
                eT.removeConnection(c);
            }


            Connection eConnection = new Connection(eT.getId() + "_ET",eT.getId(),outputPlace.getId());
            tmpQPN.addConnection(eConnection);
            for(ColorRef cr: outputPlace.getColorRefs())
            {
                if(cr.getColorId().equals(lifelineColor.replace(cf.getId(),""))) {
                    Connection modeEConnection = new Connection(cr.getId() + "_EMO", eT.getModes().get(0).getId(), cr.getId(), 1);
                    eT.addConnection(modeEConnection);
                    break;
                }
            }
        }

        for(ColorRef colorRef:inputPlace.getColorRefs())
        {
            Connection modeConnection1 = new Connection(inputPlace.getId() + "_IDM",colorRef.getId(),doOpt.getId(),1);
            Connection modeConnection2 = new Connection(inputPlace.getId() + "_IEM",colorRef.getId(),exitOpt.getId(),1);
            tOptDo.addConnection(modeConnection1);
            tOptDo.addConnection(modeConnection2);
        }
        for (ColorRef colorRef : outputPlace.getColorRefs())
        {
            Connection exitConnection = new Connection(colorRef.getId() + "_EMO", exitOpt.getId(), colorRef.getId(),1);
            tOptDo.addConnection(exitConnection);
        }
        Connection beginConnection = new Connection(tOptDo + "_B", inputPlace.getId(),tOptDo.getId());
        Connection exitConnection = new Connection(tOptDo + "_E", tOptDo.getId(),outputPlace.getId());
        tmpQPN.addConnection(beginConnection);
        tmpQPN.addConnection(exitConnection);

        tmpQPN.addPlace(inputPlace);
        tmpQPN.addPlace(outputPlace);
        tmpQPN.addTransition(tOptDo);
        subnet = QpnToSubnet(tmpQPN);

        subnetPlace.setX(sx/sn - 50);
        subnetPlace.setY(sy/sn);
        tOptExit.setX(sx/sn + 50);
        tOptExit.setY(sy/sn);

        subnetPlace.setSubnet(subnet);
        qpnDocument.addPlace(subnetPlace);
        qpnDocument.addTransition(tOptExit);
        qpnDocument.addConnection(new Connection(subnetPlace.getId() + "_PT",subnetPlace.getId(),tOptExit.getId()));

        //////Xóa các places, transitions, connections, thay bằng subnet

        for(Place p : removePlaces)
            qpnDocument.removePlace(p.getId());
        for(Transition t : removeTransitions)
            qpnDocument.removeTransition(t.getId());
        for(Connection c : removeConnections)
            qpnDocument.removeConnection(c.getId());
        //////
        toXml("D:\\SD\\Cfs\\Opt\\subnet.qpe",qpnDocument);
    }

    private static Subnet QpnToSubnet(QPNDocument tmpQPN) {
        if(tmpQPN == null)
            Logging.Error("Error in Function QpnToSubnet: tmpQPN is Null");
        Subnet subnet = new Subnet();
        subnet.setColors(tmpQPN.getColors());
        subnet.setPlaces(tmpQPN.getPlaces());
        subnet.setTransitions(tmpQPN.getTransitions());
        subnet.setConnections(tmpQPN.getConnections());
        return subnet;
    }

    private static SDDocument CFToSD(SDDocument sdDocument, SDCF cf)
    {
        SDDocument result = new SDDocument();
        ArrayList<SDMessage> messages = new ArrayList<>();

        for (Operand op: cf.getOperands())
        {
            for (String s : op.getMessages()) {     //Duyệt các message trong operand, bỏ thuộc tính CF
                SDMessage m = sdDocument.getMessageById(s);
                if(m == null)
                    continue;
                if (!messages.contains(m)) {
                    m.setCf(null);
                    m.setId(s+ "_S");
                    if(m.getReturnMessage() != "")
                        m.setReturnMessage(m.getReturnMessage() + "_S");
                    if(m.getSendMessage() != "")
                        m.setSendMessage(m.getSendMessage() + "_S");
                    messages.add(m);
                }
            }
        }

        for (SDMessage m : messages)    //Đánh dấu các message này là trong CF
        {
            if(m.getFromEnd() != "")
                m.setFromEnd(m.getFromEnd() + "_S");
            if(m.getToEnd() != "")
                m.setToEnd(m.getToEnd() + "_S");
        }

        for(String s : cf.getLifeLines())       //Duyệt các lifeline trong cf
        {
            SDLifeLine lifeLine = sdDocument.getLifeLineById(s);        //Lifeline gốc
            if(lifeLine == null)
                continue;
            SDLifeLine newLifeLine = new SDLifeLine();          //Lifeline mới
            newLifeLine.setId(lifeLine.getId() + cf.getId());
            newLifeLine.setName(lifeLine.getName());
            for(SDMessage m : messages)         //Duyệt các message để lấy occurrences
            {
                if(m.getType().equals("Found Message") || m.getType().equals("Lost Message"))   //Bỏ qua found và lost message
                    continue;
                if(m.getFromElement().equals(lifeLine.getId()))         //Kiểm tra bắt đầu từ lifeline
                {
                    SDActivation activation = newLifeLine.getActivationById(m.getFromActivation());         //kiểm tra activation đã có trong lifeline chưa
                    if(activation == null)                  //nếu activation chưa có trong lifeline thì tạo mới
                    {
                        SDActivation newActivation = new SDActivation();
                        newActivation.setId(m.getFromActivation());
                        SDActivation baseActivation = lifeLine.getActivationById(m.getFromActivation());
                        if(baseActivation != null)
                        {
                            newActivation.setX(baseActivation.getX());
                            newActivation.setY(baseActivation.getY());
                        }
                        newActivation.addOcrs(m.getFromEnd());
                        newLifeLine.addActivation(newActivation);
                        m.setFromElement(newLifeLine.getId());
                    }
                    else
                        activation.addOcrs(m.getFromEnd());
                }

                if(m.getToElement().equals(lifeLine.getId()))
                {
                    SDActivation activation = newLifeLine.getActivationById(m.getToActivation());
                    if(activation == null)
                    {
                        SDActivation newActivation = new SDActivation();
                        newActivation.setId(m.getToActivation());
                        SDActivation baseActivation = lifeLine.getActivationById(m.getToActivation());
                        if(baseActivation != null)
                        {
                            newActivation.setX(baseActivation.getX());
                            newActivation.setY(baseActivation.getY());
                        }
                        newActivation.addOcrs(m.getToEnd());
                        newLifeLine.addActivation(newActivation);
                        m.setToElement(newLifeLine.getId());
                    }
                    else
                        activation.addOcrs(m.getToEnd());
                }
            }
            result.addLifeLine(newLifeLine);
        }

        result.setMessages(messages);
        return result;
    }

    private  static Place operandToSubnet(SDDocument sdDocument, SDCF cf, Operand operand)
    {
        Place result = new Place(cf.getId() + operand.getId(),"Operand",100,100,PlaceType.SPLACE);
        Subnet subnet = new Subnet();

        ArrayList<SDMessage> messages = new ArrayList<>();
        ArrayList<SDLifeLine> lifeLines = new ArrayList<>();
        for(String s : operand.getMessages())
        {
            SDMessage message = sdDocument.getMessageById(s);
            if(message != null)
                messages.add(message);
        }

        SDDocument tmpSD = new SDDocument();
        tmpSD.setMessages(messages);
        for(SDMessage m : messages)
        {

        }

        result.setSubnet(subnet);
        return result;
    }
    private static Element creatColors(ArrayList<Color> colors, Document document)
    {
        Element colorsElement = document.createElement("colors");
        for(Color c : colors)
        {
            Element colorElement = document.createElement("color");
            colorElement.setAttribute("id", c.getId());
            colorElement.setAttribute("name", c.getName());
            colorElement.setAttribute("real-color", c.getRealColor());
            colorsElement.appendChild(colorElement);
        }
        return colorsElement;
    }

    private static Element creatQueues(ArrayList<Queue> queues, Document document)
    {
        Element queuesElement = document.createElement("queues");
        for(Queue q: queues)
        {
            Element queueElement = document.createElement("queue");
            queueElement.setAttribute("id",q.getId());
            queueElement.setAttribute("name",q.getName());
            queueElement.setAttribute("number-of-servers","1");
            queueElement.setAttribute("strategy","FCFS");
            queuesElement.appendChild(queueElement);
        }
        return queuesElement;
    }

    private static Element creatPlaces(ArrayList<Place> places, Document document)
    {
        Element placesElement = document.createElement("places");
        for(Place p : places)
        {
            Element placeElement = document.createElement("place");
            placeElement.setAttribute("id",p.getId());
            placeElement.setAttribute("name", p.getName());
            placeElement.setAttribute("departure-discipline","NORMAL");
            placeElement.setAttribute("xsi:type", p.getType());
            placesElement.appendChild(placeElement);

            if(p.getTypeId() == 1 ) {
                Element colorRefsElement = document.createElement("color-refs");
                placeElement.appendChild(colorRefsElement);
                for(ColorRef cr : p.getColorRefs())
                {
                    Element colorRefElement = document.createElement("color-ref");
                    colorRefElement.setAttribute("id",cr.getId());
                    colorRefElement.setAttribute("color-id",cr.getColorId());
                    colorRefElement.setAttribute("initial-population",cr.getInit());
                    colorRefElement.setAttribute("xsi:type","queueing-color-reference");
                    colorRefsElement.appendChild(colorRefElement);
                }
            }
            else if(p.getTypeId() == 2 ) {
                placeElement.setAttribute("queue-ref", p.getQueue().getId());
                Element colorRefsElement = document.createElement("color-refs");
                placeElement.appendChild(colorRefsElement);
                for(ColorRef cr : p.getColorRefs())
                {
                    Element colorRefElement = document.createElement("color-ref");
                    colorRefElement.setAttribute("id",cr.getId());
                    colorRefElement.setAttribute("color-id",cr.getColorId());
                    colorRefElement.setAttribute("initial-population",cr.getInit());
                    colorRefElement.setAttribute("distribution-function","Exponential");
                    colorRefElement.setAttribute("lambda",cr.getLambda());
                    colorRefElement.setAttribute("xsi:type","queueing-color-reference");
                    colorRefsElement.appendChild(colorRefElement);
                }
            }
            else if(p.getTypeId() == 3)
            {
                Element subnetElement = creatSubnet(p.getSubnet(),document);
                placeElement.appendChild(subnetElement);

                Element colorRefsElement = document.createElement("color-refs");
                placeElement.appendChild(colorRefsElement);
                for(ColorRef cr : p.getColorRefs())
                {
                    Element colorRefElement = document.createElement("color-ref");
                    colorRefElement.setAttribute("id",cr.getId());
                    colorRefElement.setAttribute("color-id",cr.getColorId());
                    colorRefElement.setAttribute("initial-population",cr.getInit());
                    colorRefElement.setAttribute("maximum-capacity","0");
                    colorRefElement.setAttribute("direction","both");
                    colorRefElement.setAttribute("xsi:type","subnet-color-reference");
                    colorRefsElement.appendChild(colorRefElement);
                }
            }

            Element metasElement = document.createElement("meta-attributes");
            placeElement.appendChild(metasElement);
            Element metaElement = document.createElement("meta-attribute");
            metaElement.setAttribute("location-x",p.getX());
            metaElement.setAttribute("location-y",p.getY());
            metaElement.setAttribute("xsi:type","location-attribute");
            metasElement.appendChild(metaElement);
        }

        return placesElement;
    }

    private static Element creatPlaces(ArrayList<Place> places, Document document, boolean isSubnet)
    {
        if(!isSubnet)
            return creatPlaces(places,document);
        else
        {
            Element elementPlaces = creatPlaces(places,document);
            NodeList nodeListPlaces = elementPlaces.getElementsByTagName("place");
            for(int i = 0; i < nodeListPlaces.getLength(); i++)
            {
                Node nodePlace = nodeListPlaces.item(i);
                if(nodePlace != null)
                {
                    String placeName = nodePlace.getAttributes().getNamedItem("name").getNodeValue();
                    if(placeName.equals("input-place"))
                    {
                        Element elementPlace = (Element)nodePlace;
                        elementPlace.setAttribute("locked","true");
                    }
                    else if(placeName.equals("output-place"))
                    {
                        Element elementPlace = (Element)nodePlace;
                        elementPlace.setAttribute("locked","true");
                    }
                }
            }
            return elementPlaces;
        }
    }

    private static Element creatTransitions(ArrayList<Transition> transitions, Document document)
    {
        Element transitionsElement = document.createElement("transitions");
        for(Transition t : transitions)
        {
            Element transitionElement = document.createElement("transition");
            transitionElement.setAttribute("id",t.getId());
            transitionElement.setAttribute("name",t.getName());
            transitionElement.setAttribute("priority",t.getPriority());
            transitionElement.setAttribute("weight",t.getWeight());
            transitionElement.setAttribute("xsi:type","immediate-transition");
            transitionsElement.appendChild(transitionElement);

            Element modesElement = document.createElement("modes");
            transitionElement.appendChild(modesElement);
            for (Mode m: t.getModes())
            {
                Element modeElement = document.createElement("mode");
                modeElement.setAttribute("id",m.getId());
                modeElement.setAttribute("name",m.getName());
                modeElement.setAttribute("firing-weight",m.getWeight());
                modeElement.setAttribute("real-color",m.getColor());
                modesElement.appendChild(modeElement);
            }


            Element connectionsElement = creatConnections(t.getConnections(),document);
            transitionElement.appendChild(connectionsElement);

            Element metasElement = document.createElement("meta-attributes");
            transitionElement.appendChild(metasElement);
            Element metaElement = document.createElement("meta-attribute");
            metaElement.setAttribute("location-x",t.getX());
            metaElement.setAttribute("location-y",t.getY());
            metaElement.setAttribute("xsi:type","location-attribute");
            metasElement.appendChild(metaElement);
        }
        return transitionsElement;
    }

    private static Element creatConnections(ArrayList<Connection> connections, Document document)
    {
        Element connectionsElement = document.createElement("connections");
        for(Connection c: connections)
        {
            Element connectionElement = document.createElement("connection");
            connectionElement.setAttribute("id",c.getId());
            connectionElement.setAttribute("source-id",c.getSrcId());
            connectionElement.setAttribute("target-id",c.getTarId());
            if(c.getCounts() >= 0)
                connectionElement.setAttribute("count", c.getCount());
            connectionsElement.appendChild(connectionElement);
        }
        return connectionsElement;
    }

    private static Element creatSubnet(Subnet subnet, Document document)
    {
        Element subnetElement = document.createElement("subnet");   //Creat subnet node
        Element colorsElement = creatColors(subnet.getColors(),document);   //Creat colors node
        Element placesElement = creatPlaces(subnet.getPlaces(),document,true);   //Creat places node
        Element transitionsElement = creatTransitions(subnet.getTransitions(),document);    //Creat transitions node
        Element connectionsElement = creatConnections(subnet.getConnections(),document);    //Creat connections node
        subnetElement.appendChild(colorsElement);       //append colors under subnet
        subnetElement.appendChild(placesElement);       //append places under subnet
        subnetElement.appendChild(transitionsElement);      //append transitions under subnet
        subnetElement.appendChild(connectionsElement);      //append connections under subnet
        return subnetElement;
    }

    public static void toXml(String path, QPNDocument qpnDocument)
    {
            File output = new File(path);
        try {

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.newDocument();

            //root node
            Element rootElement = document.createElement("net");
            rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            rootElement.setAttribute("qpme-version", "2.1.0");
            document.appendChild(rootElement);

            //Element nodes
            Element colorsElement = creatColors(qpnDocument.getColors(),document);
            Element queuesElement = creatQueues(qpnDocument.getQueues(),document);
            Element placesElement = creatPlaces(qpnDocument.getPlaces(),document);
            Element transitionsElement = creatTransitions(qpnDocument.getTransitions(),document);
            Element connectionsElement = creatConnections(qpnDocument.getConnections(),document);

            rootElement.appendChild(colorsElement);
            rootElement.appendChild(queuesElement);
            rootElement.appendChild(placesElement);
            rootElement.appendChild(transitionsElement);
            rootElement.appendChild(connectionsElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(output);
            transformer.transform(source,result);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
