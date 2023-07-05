package models.SD;

import noNamespace.ProjectDocument.Project.Models.Frame.ModelChildren.CombinedFragment.ModelChildren2.InteractionOperand;

import java.util.ArrayList;

public class Operand {
    private InteractionOperand op;
    private String Id;
    private ArrayList<String> messages = new ArrayList<>();
    private String Constraint = "1";

    public Operand(InteractionOperand operand)
    {
        this.op = operand;
        this.setAttributes(operand);
    }

    private void setAttributes(InteractionOperand operand)
    {
        this.Id = operand.getId();
        if(operand.isSetMessages())
            for( InteractionOperand.Messages.Message m : operand.getMessages().getMessageArray())
            {
                messages.add(m.getIdref());
            }
        InteractionOperand.Guard.InteractionConstraint constraint = operand.getGuard().getInteractionConstraint();
            if(constraint.isSetConstraint())
            {
                this.Constraint = constraint.getConstraint();
            }
    }

    public String getId() {
        return Id;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public String getConstraint() {
        return Constraint;
    }

    public void setConstraint(String constraint) {
        Constraint = constraint;
    }

    @Override
    public String toString() {
        return "Operand{" +
                "Id='" + Id + '\'' +
                ", messages=" + messages +
                '}';
    }
}
