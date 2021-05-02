def choiceOption(d, v, i) {
    def o = {
        displayName(d)
        value(v)
        inlineHelp("")
    }
    return o
}

job('Aux_Ansible_2') {
    configure { project ->
        project.name = 'com.cloudbees.hudson.plugins.modeling.impl.auxiliary.AuxModel'
        project.remove(project / scm)
        project << superType("Aux_Group_Deploy")
        project << instantiable(true)
        project / attributes << 'template-attribute' {
            name("taskId")
            displayName("Ansible Tower Task ID")
            control(class: 'com.cloudbees.hudson.plugins.modeling.controls.TextFieldControl')
        }
        project / attributes << 'template-attribute' {
            name("stage")
            displayName("Stage")
            control(class: 'com.cloudbees.hudson.plugins.modeling.controls.ChoiceControl') {
                mode("DROPDOWN_LIST")
                options {
                    'com.cloudbees.hudson.plugins.modeling.controls.ChoiceControl_-Option' choiceOption("Dev", "dev", "")
                    'com.cloudbees.hudson.plugins.modeling.controls.ChoiceControl_-Option' choiceOption("Staging", "staging", "")
                    'com.cloudbees.hudson.plugins.modeling.controls.ChoiceControl_-Option' choiceOption("Prod", "prod", "")
                } 
            }
        }
    }
}
