import com.cloudbees.pipeline.rules.RulesInterceptionManagement
def policiesConfig = new RulesInterceptionManagement.Config([])
RulesInterceptionManagement.get().setConfig(policiesConfig)
RulesInterceptionManagement.get().save()
Jenkins.get().save()
