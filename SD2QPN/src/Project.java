import Utilities.Logging;
import Utilities.ReadObjects;
import Utilities.Transform;
import models.SD.SDCF;
import models.SD.SDDocument;
import models.SD.SDLifeLine;
import models.SD.SDMessage;
import models.qpn.QPNDocument;
import noNamespace.ProjectDocument;

import java.io.File;
import java.util.ArrayList;

public class Project {
    public static void main(String[] args) {
        transform("E:\\project.xml", "\"E:\"");
       // transform("D:\\SD\\Par\\project.xml", "D:\\SD\\Output\\Par");

    }

    public static void transform(String file, String folder)
    {
        try {
            Logging.log("Transforming file: " + file);
            File inputXml = new File(file);
            if (inputXml != null) {
                ProjectDocument projectDocument = ProjectDocument.Factory.parse(inputXml);
                ProjectDocument.Project project = projectDocument.getProject();
                ProjectDocument.Project.Models.ModelRelationshipContainer.ModelChildren.ModelRelationshipContainer2.ModelChildren2.Message m;
                ProjectDocument.Project.Models models = project.getModels();
                ArrayList<SDLifeLine> lifeLines = ReadObjects.ReadLifeLines(project);
                ArrayList<SDMessage> messages = ReadObjects.ReadMessages(project);
                ReadObjects.ReadOccurences(lifeLines, messages);
                ArrayList<SDCF> cfs = ReadObjects.ReadCF(project);
                SDDocument sdDocument = new SDDocument(lifeLines, messages, cfs);
                QPNDocument qpnDocument = new QPNDocument();

                Transform.transformLifeLines(sdDocument, qpnDocument);
                Transform.transformMessages(sdDocument, qpnDocument);
                Transform.transformCFs(sdDocument,qpnDocument);
                Transform.toXml(folder + "\\output.qpe", qpnDocument);
            }
        } catch (Exception e) {
            System.out.println("Error in file: " + file);
            e.printStackTrace();
            System.exit(0);
        }
        Logging.log("Transformed file: " + file + " successful!");
    }
    public static void dump(ArrayList<SDMessage> messages, ArrayList<SDLifeLine> lifeLines, ArrayList<SDCF> cfs) {
        System.out.println("Messages:");
        for (SDMessage ms : messages)
            System.out.println(ms);
        System.out.println("LifeLines:");
        for (SDLifeLine sl : lifeLines)
            System.out.println(sl);

        System.out.println("CFs:");
        for (SDCF cf : cfs)
            System.out.println(cf);
    }
}
