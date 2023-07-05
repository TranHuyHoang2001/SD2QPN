package models.SD;

import noNamespace.ProjectDocument.Project.Models.Frame.ModelChildren.CombinedFragment;

import java.util.ArrayList;

public class SDCF {
    private CombinedFragment cf;
    private String Id;
    private String Name;
    private String kind;
    private ArrayList<String> lifeLines = new ArrayList<>();
    private ArrayList<Operand> operands = new ArrayList<>();

    public SDCF(CombinedFragment cf)
    {
        this.cf = cf;
        this.setAttributes(cf);
    }

    private void setAttributes(CombinedFragment cf)
    {
        this.Id = cf.getId();
        if(cf.isSetName())
            this.Name = cf.getName();
        if(cf.isSetOperatorKind())
            this.kind = cf.getOperatorKind();
        if(cf.isSetCoveredLifeLines()) {
            for (CombinedFragment.CoveredLifeLines.InteractionLifeLine lf : cf.getCoveredLifeLines().getInteractionLifeLineArray())
            {
                this.lifeLines.add(lf.getIdref());
            }
        }

        for(CombinedFragment.ModelChildren2.InteractionOperand operand : cf.getModelChildren().getInteractionOperandArray())
        {
            operands.add(new Operand(operand));
        }
    }

    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getKind() {
        return kind;
    }

    public ArrayList<String> getLifeLines() {
        return lifeLines;
    }

    public ArrayList<Operand> getOperands() {
        return operands;
    }

    @Override
    public String toString() {
        return "SDCF{" +
                "Id='" + Id + '\'' +
                ", Name='" + Name + '\'' +
                ", kind='" + kind + '\'' +
                ", lifelines=" + lifeLines +
                ", operands=" + operands +
                '}';
    }
}
