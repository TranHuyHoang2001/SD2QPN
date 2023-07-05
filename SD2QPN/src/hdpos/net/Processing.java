/*
 * Class Processing
 */
package hdpos.net;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Processing {

    public static void convert(String fileInput, String pathOutput) {
        try {
            File file = new File(fileInput);
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            builder = builderFactory.newDocumentBuilder();
            Document doc = builder.parse(file);
            System.out.println("Bắt đầu chuyển đổi...");
            ArrayList<Message> messages = readMessage(doc);
            ArrayList<LifeLine> lifeLines = readLifeLine(doc, messages);
            if (writeLifeLine(doc, pathOutput, lifeLines, messages) == 1)
                System.out.println("Chuyển đổi thành công!");
            else
                System.out.println("Chuyển đổi thất bại!");
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    private static ArrayList<Message> readMessage(Document doc) {
        ArrayList<Message> messages = new ArrayList<>();
        NodeList nodeListOfMessage = doc.getElementsByTagName("Message");
        for (int i = 0; i < nodeListOfMessage.getLength(); i++) {
            Node node = nodeListOfMessage.item(i);
            if (!node.getParentNode().getNodeName().equals("ModelChildren") || node == null) continue;
            String id = node.getAttributes().getNamedItem("Id").getNodeValue();
            String type = node.getAttributes().getNamedItem("Type").getNodeValue();
            String name = node.getAttributes().getNamedItem("Name").getNodeValue();
            String sequenceNumber = node.getAttributes().getNamedItem("SequenceNumber").getNodeValue();
            String send = null;
            String receive = null;
            NodeList tmpNodeList = node.getChildNodes();
            for (int j = 0; j < tmpNodeList.getLength(); j++) {
                Node tmpNode = tmpNodeList.item(j);
                if (tmpNode.getNodeName().equals("FromEnd")) {
                    send = tmpNode.getChildNodes().item(1).getAttributes().getNamedItem("Id").getNodeValue();
                    continue;
                }
                if (tmpNode.getNodeName().equals("ToEnd")) {
                    receive = tmpNode.getChildNodes().item(1).getAttributes().getNamedItem("Id").getNodeValue();
                }
            }
            Message localMess = new Message(id, name, RandomColor.getRandomColor(), send, receive, sequenceNumber, type);
            NamedNodeMap attributes = node.getAttributes();
            if (attributes.getNamedItem("EndRelationshipFromMetaModelElement") != null)
                localMess.setSendFromLifeLine(attributes.getNamedItem("EndRelationshipFromMetaModelElement").getNodeValue());
            if (attributes.getNamedItem("EndRelationshipToMetaModelElement") != null)
                localMess.setReceiveOnLifeLine(attributes.getNamedItem("EndRelationshipToMetaModelElement").getNodeValue());
            if (attributes.getNamedItem("FromActivation") != null)
                localMess.setSendFromExecution(attributes.getNamedItem("FromActivation").getNodeValue());
            if (attributes.getNamedItem("ToActivation") != null)
                localMess.setReceiveOnExecution(attributes.getNamedItem("ToActivation").getNodeValue());
            if (localMess.getType().equals("Self Message"))
                localMess.setType("Message");
            if (localMess.getSendFromLifeLine() != null && localMess.getReceiveOnLifeLine() == null)
                localMess.setType("Lost Message");
            if (localMess.getReceiveOnLifeLine() != null && localMess.getSendFromLifeLine() == null)
                localMess.setType("Found Message");
            if (localMess.getSendFromLifeLine() != null && localMess.getReceiveOnLifeLine() != null && localMess.getSendFromExecution() != null && localMess.getReceiveOnExecution() == null) {
                localMess.setType("Create Message");
                localMess.setReceive(localMess.getReceiveOnLifeLine());
            }
            messages.add(localMess);
        }
        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                String sN1 = o1.getSequenceNumber();
                String sN2 = o2.getSequenceNumber();
                int result = 0;
                if (sN1.length() < sN2.length()) {
                    if (sN2.indexOf(sN1) == 0)
                        return -1;
                    else {
                        String[] aSN1 = sN1.replace(".", " ").split(" ");
                        String[] aSN2 = sN2.replace(".", " ").split(" ");
                        for (int i = 0; i < aSN1.length; i++) {
                            int localInt1 = Integer.parseInt(aSN1[i]);
                            int localInt2 = Integer.parseInt(aSN2[i]);
                            result = Integer.compare(localInt1, localInt2);
                            if (result == 0)
                                continue;
                            else
                                break;
                        }
                    }
                } else if (sN1.length() > sN2.length()) {
                    if (sN1.indexOf(sN2) == 0)
                        return 1;
                    else {
                        String[] aSN1 = sN1.replace(".", " ").split(" ");
                        String[] aSN2 = sN2.replace(".", " ").split(" ");
                        for (int i = 0; i < aSN2.length; i++) {
                            int localInt1 = Integer.parseInt(aSN1[i]);
                            int localInt2 = Integer.parseInt(aSN2[i]);
                            result = Integer.compare(localInt1, localInt2);
                            if (result == 0)
                                continue;
                            else
                                break;
                        }
                    }
                } else {
                    String[] aSN1 = sN1.replace(".", " ").split(" ");
                    String[] aSN2 = sN2.replace(".", " ").split(" ");
                    for (int i = 0; i < aSN1.length; i++) {
                        int localInt1 = Integer.parseInt(aSN1[i]);
                        int localInt2 = Integer.parseInt(aSN2[i]);
                        result = Integer.compare(localInt1, localInt2);
                        if (result == 0)
                            continue;
                        else
                            break;
                    }
                }
                return result;
            }
        });
        return messages;
    }

    private static ArrayList<LifeLine> readLifeLine(Document doc, ArrayList<Message> messages) {
        ArrayList<LifeLine> lifeLines = new ArrayList<>();
        NodeList nodeListOfLifeLine = doc.getElementsByTagName("InteractionLifeLine");
        String[] names;
        String[] coveredBy;
        String[] tempcoveredBy = new String[100];
        for (int i = 0; i < nodeListOfLifeLine.getLength(); ++i) {
            Node node = nodeListOfLifeLine.item(i);
            if (!node.getParentNode().getNodeName().equals("ModelChildren")) continue;
            String IdLifeline = node.getAttributes().getNamedItem("Id").getNodeValue();

            // Lấy các điểm 'coveredby' của lifeline này
            int countnames = 1;
            tempcoveredBy[0] = IdLifeline;
            NodeList nodeListMess = doc.getElementsByTagName("Message");
            String tempIdExecution = "null";

            for (Message localMess : messages) {

                // Ktra message này có bắt đầu từ lifeline này không
                if (!localMess.getType().equals("Found Message"))
                    if (localMess.getSendFromLifeLine().equals(IdLifeline)) {
                        if (!localMess.getSendFromExecution().equals(tempIdExecution)) {
                            if (!tempIdExecution.equals("null")) tempcoveredBy[countnames++] = tempIdExecution + 'e';
                            tempIdExecution = localMess.getSendFromExecution();
                            tempcoveredBy[countnames++] = tempIdExecution + 'b';
                        }
                        tempcoveredBy[countnames++] = localMess.getSend();
                    }

                // Ktra message này có kết thúc trên lifeline này không
                if (!localMess.getType().equals("Lost Message"))
                    if (localMess.getReceiveOnLifeLine().equals(IdLifeline)) {
                        if (localMess.getType().equals("Create Message")) continue;
                        if (!localMess.getReceiveOnExecution().equals(tempIdExecution)) {
                            if (!tempIdExecution.equals("null")) tempcoveredBy[countnames++] = tempIdExecution + 'e';
                            tempIdExecution = localMess.getReceiveOnExecution();
                            tempcoveredBy[countnames++] = tempIdExecution + 'b';
                        }
                        tempcoveredBy[countnames++] = localMess.getReceive();
                    }
            }
            if (!tempIdExecution.equals(IdLifeline))
                tempcoveredBy[countnames++] = tempIdExecution + 'e';

            names = new String[countnames];
            coveredBy = new String[countnames];
            for (int j = 0; j < countnames; j++) {
                names[j] = node.getAttributes().getNamedItem("Name").getNodeValue() + j;
                String tmp = tempcoveredBy[j];
                coveredBy[j] = tempcoveredBy[j];
            }
            ;

            lifeLines.add(new LifeLine(
                    IdLifeline,
                    node.getAttributes().getNamedItem("Name").getNodeValue(),
                    RandomColor.getRandomColor(),
                    coveredBy,
                    names
            ));
        }
        return lifeLines;
    }

    private static int writeLifeLine(Document doc, String pathOutput, ArrayList<LifeLine> lifelines, ArrayList<Message> messages) {
        try {

            //Creat document
            DocumentBuilderFactory builderFactory_o = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder_o = null;
            builder_o = builderFactory_o.newDocumentBuilder();
            Document doc_o = builder_o.newDocument();

            //write root
            Element elementRoot = doc_o.createElement("net");
            doc_o.appendChild(elementRoot);
            elementRoot.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            elementRoot.setAttribute("qpme-version", "2.1.0");

            //viết ra các lifeline bằng color
            Element element_color = doc_o.createElement("colors");
            elementRoot.appendChild(element_color);
            for (int i = 0; i < lifelines.size(); i++) {
                Element elementLifeLineColor = doc_o.createElement("color");
                elementLifeLineColor.setAttribute("id", lifelines.get(i).getId());
                elementLifeLineColor.setAttribute("name", lifelines.get(i).getName());
                elementLifeLineColor.setAttribute("real-color", lifelines.get(i).getColor());
                element_color.appendChild(elementLifeLineColor);
            }

            //viết ra các message bằng color
            for (int i = 0; i < messages.size(); i++) {
                Element elementMessageColor = doc_o.createElement("color");
                elementMessageColor.setAttribute("id", messages.get(i).getId());
                elementMessageColor.setAttribute("name", messages.get(i).getName());
                elementMessageColor.setAttribute("real-color", messages.get(i).getColor());
                element_color.appendChild(elementMessageColor);
            }

            //viết ra các lifeline bằng queues
            Element element_queues = doc_o.createElement("queues");
            elementRoot.appendChild(element_queues);
            for (int i = 0; i < lifelines.size(); i++) {
                Element elementLifeLineQueue = doc_o.createElement("queue");
                elementLifeLineQueue.setAttribute("id", "q_" + lifelines.get(i).getId());
                elementLifeLineQueue.setAttribute("name", lifelines.get(i).getName());
                elementLifeLineQueue.setAttribute("number-of-servers", "1");
                elementLifeLineQueue.setAttribute("strategy", "FCFS");
                element_queues.appendChild(elementLifeLineQueue);
            }

            //Tạo ra các place
            Element element_places = doc_o.createElement("places");
            elementRoot.appendChild(element_places);
            int x = 10;
            int y = 10;
            int minX = Integer.MAX_VALUE;
            String firstLifeLine = "";
            NodeList nodeListLifeLine = doc.getElementsByTagName("InteractionLifeLine");
            for (int i = 0; i < nodeListLifeLine.getLength(); ++i) {
                Node nodeLifeLine = nodeListLifeLine.item(i);
                if (!nodeLifeLine.getParentNode().getNodeName().equals("Shapes")) continue;
                int X = Integer.parseInt(nodeLifeLine.getAttributes().getNamedItem("X").getNodeValue());
                if (X < minX) {
                    minX = X;
                    firstLifeLine = nodeLifeLine.getAttributes().getNamedItem("Name").getNodeValue();
                }
            }
            for (int i = 0; i < lifelines.size(); i++) {
                y += 100;
                x = 60;
                String[] places = lifelines.get(i).getCoveredBys();
                String[] names_x = lifelines.get(i).getNames();
                for (int j = 0; j < places.length; j++) {
                    x += 200;
                    Element elementPlace = doc_o.createElement("place");
                    elementPlace.setAttribute("id", "P_" + places[j]);
                    elementPlace.setAttribute("departure-discipline", "NORMAL");
                    elementPlace.setAttribute("queue-ref", "q_" + lifelines.get(i).getId());
                    elementPlace.setAttribute("xsi:type", "queueing-place");

                    elementPlace.setAttribute("name", names_x[j]);
                    element_places.appendChild(elementPlace);

                    //sub
                    Element elementPlaceAttribute = doc_o.createElement("meta-attributes");
                    elementPlace.appendChild(elementPlaceAttribute);
                    Element element3 = doc_o.createElement("meta-attribute");
                    element3.setAttribute("xsi:type", "location-attribute");
                    element3.setAttribute("location-x", String.valueOf(x));
                    element3.setAttribute("location-y", String.valueOf(y));
                    elementPlaceAttribute.appendChild(element3);

                    //colorref
                    Element element_place_color_refs = doc_o.createElement("color-refs");
                    elementPlace.appendChild(element_place_color_refs);
                    Element elementPlaceColorRef = doc_o.createElement("color-ref");
                    elementPlaceColorRef.setAttribute("xsi:type", "queueing-color-reference");
                    elementPlaceColorRef.setAttribute("color-id", lifelines.get(i).getId());
                    elementPlaceColorRef.setAttribute("id", names_x[j] + "_" + lifelines.get(i).getId());
                    elementPlaceColorRef.setAttribute("distribution-function", "Exponential");
                    elementPlaceColorRef.setAttribute("initial-population", "0");
                    if (names_x[j].equals(lifelines.get(i).getName() + '0'))
                        elementPlaceColorRef.setAttribute("initial-population", "1");
                    if (names_x[j].equals(firstLifeLine + "0")) {
                        elementPlaceColorRef.setAttribute("lambda", "0.1");
                    } else {
                        elementPlaceColorRef.setAttribute("lambda", "1");
                    }
                    element_place_color_refs.appendChild(elementPlaceColorRef);

                    //color message truyền
                    for (int k = 0; k < messages.size(); k++) {
                        if (places[j].equals(messages.get(k).getReceive())) {
                            Element elementMessageColorRef = doc_o.createElement("color-ref");
                            elementMessageColorRef.setAttribute("xsi:type", "queueing-color-reference");
                            elementMessageColorRef.setAttribute("color-id", messages.get(k).getId());
                            elementMessageColorRef.setAttribute("id", "CL_" + messages.get(k).getId());
                            elementMessageColorRef.setAttribute("distribution-function", "Exponential");
                            elementMessageColorRef.setAttribute("lambda", "1");
                            elementMessageColorRef.setAttribute("initial-population", "0");
                            element_place_color_refs.appendChild(elementMessageColorRef);
                        }
                    }
                }
            }

            //tạo ra transitions
            Element element_transitions = doc_o.createElement("transitions");
            elementRoot.appendChild(element_transitions);
            x = 10;
            y = 10;
            for (int i = 0; i < lifelines.size(); i++) {
                y += 100;
                x = 170;
                String[] places = lifelines.get(i).getCoveredBys();
                String[] names_x = lifelines.get(i).getNames();
                for (int j = 0; j < places.length; j++) {
                    x += 200;
                    Element elementTransition = doc_o.createElement("transition");
                    elementTransition.setAttribute("id", "T_" + places[j]);
                    elementTransition.setAttribute("weight", "1.0");
                    elementTransition.setAttribute("priority", "0");
                    elementTransition.setAttribute("xsi:type", "immediate-transition");
                    elementTransition.setAttribute("name", "t" + names_x[j]);
                    element_transitions.appendChild(elementTransition);

                    //mode
                    Element element_modes = doc_o.createElement("modes");
                    elementTransition.appendChild(element_modes);
                    Element elementMode = doc_o.createElement("mode");
                    elementMode.setAttribute("id", "M_" + places[j]);
                    elementMode.setAttribute("firing-weight", "1");
                    elementMode.setAttribute("real-color", RandomColor.getRandomColor());
                    elementMode.setAttribute("name", "t" + names_x[j]);
                    element_modes.appendChild(elementMode);

                    //connection
                    Element element_connections = doc_o.createElement("connections");
                    elementTransition.appendChild(element_connections);
                    Element elementConnection = doc_o.createElement("connection");
                    elementConnection.setAttribute("id", "CM_" + places[j]);
                    elementConnection.setAttribute("source-id", names_x[j] + "_" + lifelines.get(i).getId());
                    elementConnection.setAttribute("target-id", "M_" + places[j]);
                    elementConnection.setAttribute("count", "1");
                    element_connections.appendChild(elementConnection);
                    if (j != places.length - 1) {
                        Element localElementConnection = doc_o.createElement("connection");
                        localElementConnection.setAttribute("id", "CM_" + places[j]);
                        localElementConnection.setAttribute("source-id", "M_" + places[j]);
                        localElementConnection.setAttribute("target-id", names_x[j + 1] + "_" + lifelines.get(i).getId());
                        localElementConnection.setAttribute("count", "1");
                        element_connections.appendChild(localElementConnection);
                    }
                    if (j == 0) {
                        if (j != places.length - 1) {
                            Element localElementConnection = doc_o.createElement("connection");
                            localElementConnection.setAttribute("id", "CM_" + places[j]);
                            localElementConnection.setAttribute("source-id", "M_" + places[j]);
                            localElementConnection.setAttribute("target-id", names_x[j] + "_" + lifelines.get(i).getId());
                            localElementConnection.setAttribute("count", "1");
                            element_connections.appendChild(localElementConnection);
                        }
                    }
                    for (int k = 0; k < messages.size(); k++) {
                        if (places[j].equals(messages.get(k).getReceive())) {
                            Element localElementConnection = doc_o.createElement("connection");
                            localElementConnection.setAttribute("id", "CM_" + places[j]);
                            localElementConnection.setAttribute("source-id", "CL_" + messages.get(k).getId());
                            localElementConnection.setAttribute("target-id", "M_" + places[j]);
                            localElementConnection.setAttribute("count", "1");
                            element_connections.appendChild(localElementConnection);
                        }
                    }
                    for (int k = 0; k < messages.size(); k++) {
                        if (places[j].equals(messages.get(k).getSend())) {
                            Element localElementConnection = doc_o.createElement("connection");
                            localElementConnection.setAttribute("id", "CM_" + places[j]);
                            localElementConnection.setAttribute("source-id", "M_" + places[j]);
                            localElementConnection.setAttribute("target-id", "CL_" + messages.get(k).getId());
                            localElementConnection.setAttribute("count", "1");
                            element_connections.appendChild(localElementConnection);
                        }
                    }

                    //sub
                    Element element_attribute = doc_o.createElement("meta-attributes");
                    elementTransition.appendChild(element_attribute);
                    Element elementAttribute = doc_o.createElement("meta-attribute");
                    elementAttribute.setAttribute("xsi:type", "location-attribute");
                    elementAttribute.setAttribute("location-x", String.valueOf(x));
                    elementAttribute.setAttribute("location-y", String.valueOf(y));
                    element_attribute.appendChild(elementAttribute);
                }
            }

            //tạo ra connections
            Element element_connections = doc_o.createElement("connections");
            elementRoot.appendChild(element_connections);
            String old_conn = "";
            for (int i = 0; i < lifelines.size(); i++) {
                String[] places = lifelines.get(i).getCoveredBys();
                String[] names_x = lifelines.get(i).getNames();
                old_conn = "";
                for (int j = 0; j < places.length; j++) {
                    Element elementConnection = doc_o.createElement("connection");
                    elementConnection.setAttribute("source-id", "P_" + places[j]);
                    elementConnection.setAttribute("target-id", "T_" + places[j]);
                    elementConnection.setAttribute("id", "C_" + places[j]);
                    element_connections.appendChild(elementConnection);
                    if (old_conn != "") {
                        elementConnection = doc_o.createElement("connection");
                        elementConnection.setAttribute("source-id", old_conn);
                        elementConnection.setAttribute("target-id", "P_" + places[j]);
                        elementConnection.setAttribute("id", "CP_" + places[j]);
                        element_connections.appendChild(elementConnection);
                    }
                    old_conn = "T_" + places[j];
                }
            }

            //tạo ra connections messenge
            for (int i = 0; i < messages.size(); i++) {
                Element elementConnectionFromMessage = doc_o.createElement("connection");
                elementConnectionFromMessage.setAttribute("source-id", "T_" + messages.get(i).getSend());
                elementConnectionFromMessage.setAttribute("target-id", "P_" + messages.get(i).getReceive());
                elementConnectionFromMessage.setAttribute("id", "CXX_" + messages.get(i).getId());
                element_connections.appendChild(elementConnectionFromMessage);
            }

            //tạo ra connections workload
            for (int i = 0; i < lifelines.size(); i++) {
                String[] places = lifelines.get(i).getCoveredBys();
                String[] names_x = lifelines.get(i).getNames();
                Element elementConnectFromWorkload1 = doc_o.createElement("connection");
                elementConnectFromWorkload1.setAttribute("source-id", "T_" + places[0]);
                elementConnectFromWorkload1.setAttribute("target-id", "P_" + places[0]);
                elementConnectFromWorkload1.setAttribute("id", "C_" + places[0]);
                element_connections.appendChild(elementConnectFromWorkload1);

                Element elementConnectFromWorkload2 = doc_o.createElement("connection");
                elementConnectFromWorkload2.setAttribute("source-id", "T_" + places[places.length - 1]);
                elementConnectFromWorkload2.setAttribute("target-id", "P_" + places[places.length - 1]);
                elementConnectFromWorkload2.setAttribute("id", "C_" + places[places.length - 1]);
                element_connections.appendChild(elementConnectFromWorkload2);

                //save file
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = null;
                transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc_o);
                StreamResult result = new StreamResult(
                        new File(pathOutput + "\\outputx.qpe"));
                StreamResult result2 = new StreamResult(
                        new File(pathOutput + "\\outputx.xml"));
                transformer.transform(source, result);
                transformer.transform(source, result2);
            }
        } catch (Exception localException) {
            localException.printStackTrace();
            return -1;
        }
        return 1;
    }
}
