import com.cloudbees.hudson.plugins.modeling.impl.jobTemplate.JobTemplate
import com.cloudbees.hudson.plugins.modeling.controls.NestedAuxModelControl
import com.cloudbees.hudson.plugins.modeling.impl.auxiliary.AuxModel

Jenkins jenkins = Jenkins.get()
JobTemplate jobTemplate = jenkins.allItems(JobTemplate).find { it.name == "Job_Template_Name" }

def getNestedModules(def attr, def initSpaces) {
    println "-" * initSpaces + "[A]${attr.name}"
    if (attr.control instanceof NestedAuxModelControl) {
        def auxModels = Jenkins.get().allItems(AuxModel).findAll { it.superTypeId == attr.control.getItemType() }
        auxModels.each { sub ->
            println  " " * (initSpaces + 2) + "[M]${sub.name}"
            sub.attributes.each { subAttr ->
            getNestedModules(subAttr, initSpaces * 3)
            }
        }
        
        def aux = Jenkins.get().allItems(AuxModel).find { it.name == attr.control.getItemType() }
        aux.attributes.each { attr2 -> 
            println "+" * (initSpaces + 4) + "[A]$attr2.name"
        }
    }
}

println "Attributes:"
jobTemplate.attributes.each { attr ->
  getNestedModules(attr, 2)
}

return null
