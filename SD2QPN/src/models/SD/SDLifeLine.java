package models.SD;

import noNamespace.ProjectDocument.Project.Models.Frame.ModelChildren.InteractionLifeLine;
import java.util.ArrayList;

public class SDLifeLine {
    private InteractionLifeLine l;
    private String Id;
    private String Name = "";
    private ArrayList<SDActivation> activations = new ArrayList<>();

    public SDLifeLine(InteractionLifeLine l)
    {
        this.l = l;
        this.setAttributes(l);
    }

    public SDLifeLine(){

    }

    private void setAttributes(InteractionLifeLine l)
    {
        if(l.isSetId())
            this.Id = l.getId();
        if(l.isSetName())
            this.Name = l.getName();
        if(l.isSetActivations())
            for(InteractionLifeLine.Activations.Activation a: l.getActivations().getActivationArray())
            {
                activations.add(new SDActivation(a));
            }
    }

    public InteractionLifeLine getL() {
        return l;
    }

    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public ArrayList<SDActivation> getActivations() {
        return activations;
    }

    public SDActivation getActivation(int index)
    {
        if(index < activations.size())
            return activations.get(index);
        return null;
    }

    public SDActivation getActivationById(String id)
    {
        for(SDActivation activation: activations)
        {
            if(activation.getId().equals(id))
                return activation;
        }
        return null;
    }

    public void setActivation(int index, SDActivation activation)
    {
        if(index < activations.size())
        {
            activations.get(index).set(activation);
        }
        else
            activations.add(activation);
    }

    public void addActivation(SDActivation activation)
    {
        if(activation.getId() != null && !this.activations.contains(activation))
            this.activations.add(activation);
    }

    public void setId(String id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public String toString() {
        return "SDLifeLine{" +
                ", Id='" + Id + '\'' +
                ", Name='" + Name + '\'' +
                ", activations=" + activations +
                '}';
    }
}
