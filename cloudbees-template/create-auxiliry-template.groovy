import com.cloudbees.hudson.plugins.modeling.impl.auxiliary.AuxModel
import com.cloudbees.hudson.plugins.modeling.Attribute
import com.cloudbees.hudson.plugins.modeling.controls.*

def jenkins = Jenkins.get()

def taskIdAttr = new Attribute("taskId", "Ansible Tower Task ID", (new TextFieldControl()))
def stageChoiceAttr = new Attribute("stage", "Stage", new ChoiceControl(
  [
    new ChoiceControl.Option("Dev", "dev"), 
    new ChoiceControl.Option("Staging", "staging"),
    new ChoiceControl.Option("Prod", "prod"),
  ],
  ChoiceControl.Mode.RADIO_BUTTON
))

//def newNestedAuxAttr = new Attribute("scan", "Scan Tool", new NestedAuxModelControl(NestedAuxModelControl.Mode.SINGLE, aAnsible))

def newAuxModel = new AuxModel(jenkins, "Aux_Ansible")
newAuxModel.setAttributes([taskIdAttr, stageChoiceAttr])
newAuxModel.setSuperTypeId("Aux_Group_Deploy")
newAuxModel.save()
jenkins.save()
jenkins.reload()

null