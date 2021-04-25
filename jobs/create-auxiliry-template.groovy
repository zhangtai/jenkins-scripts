import com.cloudbees.hudson.plugins.modeling.impl.auxiliary.AuxModel
import com.cloudbees.hudson.plugins.modeling.Attribute
import com.cloudbees.hudson.plugins.modeling.controls.*

def jenkins = Jenkins.get()
def aus = jenkins.allItems(AuxModel)
def aAnsible = aus.find { it.id == "Aux Ansible" }

def newTextAttr = new Attribute("who", "Who?", (new TextFieldControl()))
newTextAttr.control.setDefaultValue("tiger")

def newChoiceAttr = new Attribute("what", "What?", new ChoiceControl(
  [
    new ChoiceControl.Option("A", "a"), 
    new ChoiceControl.Option("B", "b")
  ],
  ChoiceControl.Mode.RADIO_BUTTON
))

def newNestedAuxAttr = new Attribute("scan", "Scan Tool", new NestedAuxModelControl(NestedAuxModelControl.Mode.SINGLE, aAnsible))

def newAuxModel = new AuxModel(jenkins, "Aux1")
newAuxModel.setAttributes([newTextAttr, newChoiceAttr, newNestedAuxAttr])
newAuxModel.save()
jenkins.save()
jenkins.reload()

null